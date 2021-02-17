/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.scripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.tags.DoctypeTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

/**
 * Override the Spring WebScripts impl of RemoteClient to provide additional security
 * processing of HTML responses retrieved via content APIs. Prevents the execution of
 * inline JavaScript proxy driven API calls via XHR requests and similar.
 * 
 * @author Kevin Roast
 */
public class SlingshotRemoteClient extends RemoteClient
{
    private static final Pattern CONTENT_PATTERN_TO_CHECK = Pattern.compile(".*/(api|slingshot)/(node|path)/(content.*/)?workspace/SpacesStore/.+");
    private static final Pattern TRASHCAN_PATTERN_TO_CHECK = Pattern.compile(".*/(api|slingshot)/(node|path)/(content.*/)?archive/SpacesStore/.+");
    private static final Pattern CONTENT_PATTERN_TO_WHITE_LIST = Pattern.compile(".*/api/node/workspace/SpacesStore/[a-z0-9-]+/content/thumbnails/webpreview");
    private static final Pattern SLINGSHOT_WIKI_PAGE_PATTERN = Pattern.compile(".*/slingshot/wiki/page/.*");
    private static final Pattern SLINGSHOT_WIKI_VERSION_PATTERN = Pattern.compile(".*/slingshot/wiki/version/.*");

    private boolean swfEnabled = false;
    
    public void setSwfEnabled(boolean swfEnabled)
    {
        this.swfEnabled = swfEnabled;
    }
    
    @Override
    protected void copyResponseStreamOutput(URL url, HttpServletResponse res, OutputStream out,
            HttpResponse response, String contentType, int bufferSize) throws IOException
    {
        boolean processed = false;
        if (res != null && getRequestMethod() == HttpMethod.GET &&
                response.getStatusLine().getStatusCode() >= 200 &&
                response.getStatusLine().getStatusCode() < 300)
        {
            // only match if content is not an attachment - don't interfere with downloading of file content 
            Header cd = response.getFirstHeader("Content-Disposition");
            if (cd == null || !cd.getValue().startsWith("attachment"))
            {
                // only match appropriate content REST URIs 
                if (contentType != null && (CONTENT_PATTERN_TO_CHECK.matcher(url.getPath()).matches()
                        && !CONTENT_PATTERN_TO_WHITE_LIST.matcher(url.getPath()).matches()
                        || SLINGSHOT_WIKI_PAGE_PATTERN.matcher(url.getPath()).matches()
                        || SLINGSHOT_WIKI_VERSION_PATTERN.matcher(url.getPath()).matches())
                        || TRASHCAN_PATTERN_TO_CHECK.matcher(url.getPath()).matches())
                {
                    // found a GET request that might be a security risk
                    String mimetype = contentType;
                    String encoding = null;
                    int csi = contentType.indexOf(CHARSETEQUALS);
                    if (csi != -1)
                    {
                        mimetype = contentType.substring(0, csi - 1).toLowerCase();
                        encoding = contentType.substring(csi + CHARSETEQUALS.length());
                    }
                    
                    // examine the mimetype to see if additional processing is required
                    // MNT-18730 - specifically omit UTF-16 XML content
                    if (mimetype.contains("text/html") || mimetype.contains("application/xhtml+xml") || (mimetype.contains("text/xml") && !encoding.contains("UTF-16")))
                    {
                        // found HTML content we need to process in-memory and perform stripping on
                        ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
                        final InputStream input;
                        if (response.getEntity() != null && (input = response.getEntity().getContent()) != null)
                        {
                            // get data into our byte buffer for processing
                            try
                            {
                                final byte[] buffer = new byte[bufferSize];
                                int read = input.read(buffer);
                                while (read != -1)
                                {
                                    // halt on binary file - we assume this is HTML - it might not be - effectively a DNS attack
                                    for (int i=0; i<read; i++)
                                    {
                                        if (buffer[i] == 0x00)
                                        {
                                            res.setContentLength(0);
                                            out.close();
                                            return;
                                        }
                                    }
                                    bos.write(buffer, 0, read);
                                    read = input.read(buffer);
                                }
                            }
                            finally
                            {
                                input.close();
                            }
                            
                            // convert to appropriate string format
                            String content = encoding != null ? new String(bos.toByteArray(), encoding) : new String(bos.toByteArray());

                            if (mimetype.contains("text/html") || mimetype.contains("application/xhtml+xml"))
                            {
                                // process with HTML stripper
                                content = StringUtils.stripUnsafeHTMLDocument(content, false);
                            }
                            else if (mimetype.contains("text/xml"))
                            {
                                // we cannot be sure what we are processing here - it could be html embedded in XML
                                // If docType is set to xml browsers (at least IE & Chrome) will treat it like it
                                // does for a svg+xml document
                                res.setContentType("text/plain");
                            }
                            else if (mimetype.contains("text/x-component"))
                            {
                                // IE supports "behaviour" which means that css can load a .htc file that could
                                // contain XSS code in the form of jscript, vbscript etc, to stop it form being
                                // evaluated we set the contient type to text/plain
                                res.setContentType("text/plain");
                            }

                            // push the modified response to the real outputstream
                            try
                            {
                                byte[] bytes = encoding != null ? content.getBytes(encoding) : content.getBytes();
                                // rewrite size header as it wil have changed
                                res.setContentLength(bytes.length);
                                // output the bytes
                                out.write(bytes);
                            }
                            finally
                            {
                                out.close();
                            }
                        }
                        processed = true;
                    }
                    else if ((mimetype.contains("application/x-shockwave-flash") || mimetype.contains("image/svg+xml")) && !swfEnabled)
                    {
                        String msg = I18NUtil.getMessage("security.insecuremimetype");
                        try
                        {
                            byte[] bytes = encoding != null ? msg.getBytes(encoding) : msg.getBytes();
                            
                            // rewrite headers
                            res.setContentType("text/plain");
                            res.setContentLength(bytes.length);
                            // output the bytes
                            out.write(bytes);
                        }
                        finally
                        {
                            out.close();
                        }
                        processed = true;
                    }
                }
            }
        }
        if (!processed)
        {
            super.copyResponseStreamOutput(url, res, out, response, contentType, bufferSize);
        }
    }

    protected boolean hasDocType(String content, String docType, boolean encode)
    {
        try
        {
            Parser parser = Parser.createParser(content, "UTF-8");
            PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
            parser.setNodeFactory(factory);
            NodeIterator itr = parser.elements();
            while (itr.hasMoreNodes())
            {
                Node node = itr.nextNode();
                if (node instanceof DoctypeTag)
                {
                    // Found the doctype tag, now lets see if can find the searched for doctype attribute.
                    DoctypeTag docTypeTag = (DoctypeTag)node;
                    Vector<Attribute> attrs = docTypeTag.getAttributesEx();
                    if (attrs != null && attrs.size() > 1)
                    {
                        for (Attribute attr : attrs)
                        {
                            String name = attr.getName();
                            if (name != null && name.equalsIgnoreCase(docType))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (ParserException e)
        {
            // Not a valid xml document, return false below
        }
        return false;
    }
}
