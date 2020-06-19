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
package org.alfresco.wcm.client.directive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;

/**
 * Freemarker directive to stream a repository asset's content to the HTTP
 * response. Usage: <@streamasset asset=xxx/> where xxx is a variable which
 * references an asset object
 * 
 * @author Chris Lack
 */
public class AssetDirective implements TemplateDirectiveModel
{
    protected static final int DEFAULT_BUFFERSIZE = 4096;
    protected static final Log logger = LogFactory.getLog(AssetDirective.class);

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException,
            IOException
    {
        if (params.size() != 1)
        {
            throw new TemplateModelException("asset directive expects one parameter of type Asset");
        }

        StringModel assetParam = (StringModel) params.get("asset");
        if (assetParam == null || !(assetParam.getWrappedObject() instanceof Asset))
        {
            throw new TemplateModelException("asset directive expects asset parameter with a value of class Asset");
        }
        Asset asset = (Asset) assetParam.getWrappedObject();

        // Get the assets content stream
        ContentStream contentStream = asset.getContentAsInputStream();
        Writer out = env.getOut();

        String mimeType = contentStream.getMimeType();

        String encoding = Charset.forName("UTF-8").name();
        String content = null;

        int bufferSize = DEFAULT_BUFFERSIZE;
        int length = (int) contentStream.getLength();
        if (length > 0 && length < bufferSize)
        {
            bufferSize = (int) length;
        }

        //MNT-16014: Parse and encode content in order to prevent XSS attacks
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferSize);
        final InputStream input = contentStream.getStream();
        if (input != null)
        {
            // get data into our byte buffer for processing
            try
            {
                final byte[] buffer = new byte[bufferSize];
                int read = input.read(buffer);
                while (read != -1)
                {
                    bos.write(buffer, 0, read);
                    read = input.read(buffer);
                }
            }
            finally
            {
                input.close();
            }

            // convert to appropriate string format
            content = encoding != null ? new String(bos.toByteArray(), encoding) : new String(bos.toByteArray());

            // if found HTML content we need to process in-memory and perform stripping on
            if (mimeType.contains("text/html") || mimeType.contains("application/xhtml+xml"))
            {
                // process with HTML stripper
                content = StringUtils.stripUnsafeHTMLDocument(content, false);
            }
            else
            {
                // we cannot be sure what we are processing here - it could be
                // html embedded in XML or anything else
                content = StringUtil.XHTMLEnc(content);
            }
        }

        // push the modified response to the real outputstream
        try
        {
            // output the bytes
            String processedContent = new String(content.getBytes(), encoding);
            out.write(processedContent);
        }
        catch (IOException ex)
        {
            logger.error(ex);
        }
    }

}
