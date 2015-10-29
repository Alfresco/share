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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.util.WebUtil;

/**
 * Class to represent the template model for a URL.
 * 
 * @author Kevin Roast
 */
public class DefaultURLHelper implements Serializable, URLHelper
{
    private static final long serialVersionUID = -966485798540601444L;

    private final String context;
    private final String pageContext;
    private final String uri;
    private final String queryString;
    private final Map<String, String> args;
    private final Map<String, String> templateArgs = new HashMap<String, String>(4, 1.0f);

    /**
     * Construction
     * 
     * @param context   Request Context to build URL model helper from
     */
    @SuppressWarnings("unchecked")
    public DefaultURLHelper(RequestContext context)
    {
        this.context = context.getContextPath();
        this.uri = context.getUri();
        
        String uriNoContext = context.getUri().substring(this.context.length());
        StringTokenizer t = new StringTokenizer(uriNoContext, "/");
        if (t.hasMoreTokens())
        {
            this.pageContext = this.context + "/" + t.nextToken();
        }
        else
        {
            this.pageContext = this.context;
        }
        
        this.queryString = WebUtil.getQueryStringForMap(context.getParameters());
        
        this.args = Collections.unmodifiableMap((HashMap<String, String>)((HashMap<String, String>)context.getParameters()).clone());
    }
    
    /**
     * Construction
     * 
     * @param context   Request Context to build URL model helper from
     */
    public DefaultURLHelper(RequestContext context, Map<String, String> templateArgs)
    {
        this(context);
        if (templateArgs != null)
        {
            this.templateArgs.putAll(templateArgs);
        }
    }
    
    public String getContext()
    {
        return context;
    }

    public String getServletContext()
    {
        return pageContext;
    }

    public String getUri()
    {
        return uri;
    }

    public String getUrl()
    {
        return uri + (this.queryString.length() != 0 ? ("?" + this.queryString) : "");
    }

    public String getQueryString()
    {
        return this.queryString;
    }
    
    public Map<String, String> getArgs()
    {
        return this.args;
    }
    
    public Map<String, String> getTemplateArgs()
    {
        return Collections.unmodifiableMap(this.templateArgs);
    }
}
