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

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.util.Content;

 /**
  * LocalWebScriptRequest represents a locally processed WebScript request object
  * based on a URL.
  * 
  * @author muzquiano
  * @author kevinr
  */
public class LocalWebScriptRequest extends WebScriptRequestURLImpl
{
    final private Map<String, Serializable> parameters;
    final private ServerProperties serverProperties;
    final private LocalWebScriptContext context;
    final private String[] parameterNames;
    
    
    /**
     * Instantiates a new local web script request.
     * 
     * @param runtime the runtime
     * @param scriptUrl the script url
     * @param match the match
     * @param parameters the parameters
     * @param context the web script context
     */
    public LocalWebScriptRequest(Runtime runtime, String scriptUrl,
            Match match, Map<String, Serializable> parameters, ServerProperties serverProps, LocalWebScriptContext context)
    {
        super(runtime, splitURL(context.getRequestContext().getContextPath(), scriptUrl),  match);
        parameters.putAll(queryArgs);
        this.parameters = parameters;
        this.serverProperties = serverProps;
        this.context = context;
        
        // cache parameter names as they are inspected multiple times
        this.parameterNames = this.parameters.keySet().toArray(new String[this.parameters.size()]);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterNames()
     */
    public String[] getParameterNames()
    {
        return this.parameterNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        return (String) this.parameters.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.WebScriptRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        final String[] values = new String[1];
        values[0] = (String) this.parameters.get(name);
        return values;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getAgent()
     */
    public String getAgent()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getServerPath()
     */
    public String getServerPath()
    {
        return getServerScheme() + "://" + getServerName() + ":" + getServerPort();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderNames()
     */
    public String[] getHeaderNames()
    {
        return new String[] { };
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeader(java.lang.String)
     */
    public String getHeader(String name)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getHeaderValues(java.lang.String)
     */
    public String[] getHeaderValues(String name)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebScriptRequest#getContent()
     */
    public Content getContent()
    {
        return null;
    }
    
    /**
     * Get Server Scheme
     * 
     * @return  server scheme
     */
    private String getServerScheme()
    {
        HttpServletRequest request = ServletUtil.getRequest();
        
        String scheme = null;
        if (serverProperties != null)
        {
            scheme = serverProperties.getScheme();
        }
        if (scheme == null)
        {
            scheme = request.getScheme();
        }
        return scheme;
    }

    /**
     * Get Server Name
     * 
     * @return  server name
     */
    private String getServerName()
    {
        HttpServletRequest request = ServletUtil.getRequest();
        
        String name = null;
        if (serverProperties != null)
        {
            name = serverProperties.getHostName();
        }
        if (name == null)
        {
            name = request.getServerName();
        }
        return name;
    }

    /**
     * Get Server Port
     * 
     * @return  server name
     */
    private int getServerPort()
    {
        HttpServletRequest request = ServletUtil.getRequest();
        
        Integer port = null;
        if (serverProperties != null)
        {
            port = serverProperties.getPort();
        }
        if (port == null)
        {
            port = request.getServerPort();
        }
        return port;
    }
}
