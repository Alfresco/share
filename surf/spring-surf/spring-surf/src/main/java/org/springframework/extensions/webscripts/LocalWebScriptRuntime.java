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
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.uri.UriUtils;

/**
 * WebScript Runtime for rendering as Web Framework components.
 * 
 * @author kevinr
 * @author muzquiano
 */
public class LocalWebScriptRuntime extends AbstractRuntime
{
    public static final String DEFAULT_METHOD_GET = "GET";
    
    private ServerProperties serverProperties;
    private LocalWebScriptContext context;
    private Writer out;
    private String method;

    
    /**
     * Constructor
     * 
     * @param out
     * @param container
     * @param serverProps
     * @param context
     */
    public LocalWebScriptRuntime(
            Writer out, RuntimeContainer container, ServerProperties serverProps, LocalWebScriptContext context) 
    {
        super(container);
        this.out = out;
        this.serverProperties = serverProps;
        this.context = context;
        this.method = DEFAULT_METHOD_GET;
    }
    
    /**
     * @return context object for this webscript runtime
     */
    public LocalWebScriptContext getLocalContext()
    {
        return context;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Runtime#getName()
     */
    public String getName()
    {
        return "SURF Web Framework Runtime";
    }

    @Override
    protected String getScriptUrl()
    {
        return context.getScriptUrl();
    }

    @Override
    protected WebScriptRequest createRequest(Match match)
    {
        // this includes all elements of the xml
        Map<String, Serializable> properties = context.getModelObject().getProperties();
        String scriptUrl = context.getExecuteUrl();
        
        // component ID is always available to the component
        ModelObject modelObject = context.getModelObject();
        properties.put("id", modelObject.getId());
        
        // Merge in the custom and evaluated properties...
        addProperties(properties, modelObject.getCustomProperties());
        addProperties(properties, context.getRequestContext().getEvaluatedProperties());
        
        // add the html binding id
        String htmlBindingId = (String) context.getRequestContext().getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
        if (htmlBindingId != null)
        {
            properties.put(ProcessorModelHelper.PROP_HTMLID, htmlBindingId);
        }
        
        return new LocalWebScriptRequest(this, scriptUrl, match, properties, serverProperties, context);
    }

    /**
     * <p>Merges one set of supplied properties into another, but performs replacement on the uri tokens.</p>
     * @param base The properties to add to
     * @param toMerge The properties to add
     */
    private void addProperties(Map<String, Serializable> base, Map<String, Serializable> toMerge)
    {
        for (Entry<String, Serializable> prop: toMerge.entrySet())
        {
            base.put(prop.getKey(), UriUtils.replaceTokens((String)prop.getValue(), context.getRequestContext(), null, null, ""));
        }
    }
    
    @Override
    protected LocalWebScriptResponse createResponse()
    {
        return new LocalWebScriptResponse(this, context, out);
    }

    @Override
    protected String getScriptMethod()
    {
        return method;
    }

    @Override
    protected Authenticator createAuthenticator()
    {
        return null;
    }

    @Override
    public WebScriptSessionFactory createSessionFactory()
    {
        return null;
    }

    public void setScriptMethod(String method)
    {
        this.method = method;
    }

    /**
     * @see org.springframework.extensions.webscripts.AbstractRuntime#beforeProcessError(org.springframework.extensions.webscripts.Match, java.lang.Throwable)
     * 
     * Override this hook to add special handling for "missing" WebScript components.
     * The page renderer can safely ignore components that no longer map to a URL. It
     * is recommended that that the debug flag is used to view missing webscript URLs.
     */
    @Override
    protected boolean beforeProcessError(Match match, Throwable e)
    {
        if (e instanceof WebScriptException && ((WebScriptException)e).getStatus() == HttpServletResponse.SC_NOT_FOUND)
        {
            // log info on server if we are debugging
            if (logger.isDebugEnabled())
            {
                logger.debug(e.getMessage());
            }
            return false;
        }
        else
        {
            return super.beforeProcessError(match, e);
        }
    }
}
