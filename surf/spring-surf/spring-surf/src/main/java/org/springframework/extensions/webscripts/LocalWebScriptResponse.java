/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Implementation of a WebScript Response object for WebScript Component type.
 * Mostly based on the existing WebScriptResponseImpl - just adds support for
 * encoding URLs to manage user click requests to any component on the page.
 * 
 * @author kevinr
 */
public class LocalWebScriptResponse extends WebScriptResponseImpl
{
    private Writer out;
    private Runtime runtime;
    private LocalWebScriptContext context;
    private String scriptUrlPrefix = null;

    public LocalWebScriptResponse(Runtime runtime, LocalWebScriptContext context, Writer out)
    {
        super(runtime);
        this.context = context;
        this.runtime = runtime;
        this.out = out;
    }

    public String encodeScriptUrl(String url)
    {
        // encode to allow presentation tier webscripts to call back to themselves on a page
        // needs the servlet URL plus args to identify the webscript id and the new url
        if (scriptUrlPrefix == null)
        {
            // And build up the request URL - may be used by webscript responses to build further urls
            StringBuffer buf = new StringBuffer(128);
            buf.append(context.getRequestContext().getUri());
            boolean first = true;
            for (Map.Entry<String, String> entry : context.getTokens().entrySet())
            {
                String key = entry.getKey();
                if (!WebScriptProcessor.PARAM_WEBSCRIPT_URL.equals(key) &&
                    !WebScriptProcessor.PARAM_WEBSCRIPT_ID.equals(key))
                {
                    String value = entry.getValue();                
                    buf.append(first ? '?' : '&').append(key).append('=').append(URLEncoder.encode(value));
                    first = false;
                }
            }
            scriptUrlPrefix = buf.toString();
        }
        return scriptUrlPrefix + (context.getTokens().size() != 0 ? '&' : '?') +
               WebScriptProcessor.PARAM_WEBSCRIPT_URL + "=" +
               URLEncoder.encode(url) + "&" +
               WebScriptProcessor.PARAM_WEBSCRIPT_ID + "=" + context.getModelObject().getId();
    }

    public String getEncodeScriptUrlFunction(String name)
    {
        // TODO: may be required?
        return null;
    }

    public OutputStream getOutputStream() throws IOException
    {
        // NOTE: not support by local WebScript runtime 
        return null;
    }

    public Writer getWriter() throws IOException
    {
        return this.out;
    }

    public void reset()
    {
        // not supported
    }

    public void setCache(Cache cache)
    {
        // not supported
    }

    public void setHeader(String name, String value)
    {
        // not supported
    }

    public void addHeader(String name, String value)
    {
        // not supported
    }

    public void setContentType(String contentType)
    {
        // not supported
    }

    public void setContentEncoding(String contentEncoding)
    {
        // not supported
    }

    public void setStatus(int status)
    {
        // not supported
    }
}
