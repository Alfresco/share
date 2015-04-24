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

package org.springframework.extensions.surf.support;

import java.util.Map;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * <p>A link builder which supports linking to dispatch state via
 * request parameters.
 * </p><p>
 * This provides a very simple, request-parameter driven way of creating
 * URLs.  URLs are created according to the following design:
 * </p>
 * For references to a page:
 * <ul>
 * <li>?p=<{@code}pageId></li>
 * <li>?p=<{@code}pageId>&f=<{@code}formatId></li>
 * </ul>    
 * For references to a page type:
 * <ul>
 * <li>?pt=<{@code}pageTypeId></li>
 * <li>?pt=<{@code}pageTypeId>&f=<{@code}formatId></li>
 * </ul>    
 * For references to an object:
 * <ul>
 * <li>?o=<{@code}objectId></li>
 * <li>?o=<{@code}objectId>&f=<{@code}formatId></li>
 * </ul>
 * 
 * @author muzquiano
 * @author David Draper
 */
public class RequestParameterLinkBuilder extends AbstractLinkBuilder
{
    /**
     * 
     * @param serviceRegistry
     * @deprecated Because it relies on the supplied <code>WebFrameworkServiceRegistry</code> to obtain the required services.
     */
    protected RequestParameterLinkBuilder(WebFrameworkServiceRegistry serviceRegistry)
    {
        super(serviceRegistry);
    }

    /**
     * <p>This is the preferred constructor to use when instantiating a <code>RequestParameterLinkBuilder</code> because
     * it allows the services to be set directly.</p>
     * 
     * @param webFrameworkConfigElement
     * @param modelObjectService
     * @param resourceService
     */
    public RequestParameterLinkBuilder(WebFrameworkConfigElement webFrameworkConfigElement, 
                                       ModelObjectService modelObjectService,
                                       ResourceService resourceService)
    {
        super(webFrameworkConfigElement, modelObjectService, resourceService);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.support.AbstractLinkBuilder#page(org.alfresco.web.framework.RequestContext, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params)
    {
        if (pageId == null)
        {
            throw new IllegalArgumentException("PageId is mandatory.");
        }
        
        StringBuilder buffer = new StringBuilder(64);
        buffer.append(context.getContextPath());        
        if (formatId != null)
        {
            buffer.append("?f=" + formatId);
        }
        buffer.append("&p=" + pageId);
        if (objectId != null && objectId.length() != 0)
        {
              buffer.append("&o=" + objectId);
        }
        if (params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();                
                buffer.append("&" + key + "=" + URLEncoder.encode(value));
            }
        }
        
        return buffer.toString();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.support.AbstractLinkBuilder#pageType(org.alfresco.web.framework.RequestContext, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId, Map<String, String> params)
    {
        if (pageTypeId == null)
        {
            throw new IllegalArgumentException("PageTypeId is mandatory.");
        }
        
        StringBuilder buffer = new StringBuilder(64);
        buffer.append(context.getContextPath());
        if (formatId != null)
        {
            buffer.append("?f=" + formatId);
        }
        buffer.append("&pt=" + pageTypeId);
        if (objectId != null && objectId.length() != 0)
        {
              buffer.append("&o=" + objectId);
        }
        if (params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();                
                buffer.append("&" + key + "=" + URLEncoder.encode(value));
            }
        }
        
        return buffer.toString();
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.support.AbstractLinkBuilder#object(org.alfresco.web.framework.RequestContext, java.lang.String, java.lang.String, java.util.Map)
     */
    public String object(RequestContext context, String objectId,
            String formatId, Map<String, String> params)
    {
        if (objectId == null)
        {
            throw new IllegalArgumentException("ObjectId is mandatory.");
        }
        
        StringBuilder buffer = new StringBuilder(64);
        buffer.append(context.getContextPath());
        if (formatId != null)
        {
            buffer.append("?f=" + formatId);
        }
        buffer.append("&o=" + objectId);
        
        if (params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                String key = entry.getKey();
                String value = entry.getValue();                
                buffer.append("&" + key + "=" + URLEncoder.encode(value));
            }
        }
        
        return buffer.toString();
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.support.AbstractLinkBuilder#resource(org.springframework.extensions.surf.RequestContext, java.lang.String)
     */
    public String resource(RequestContext context, String uri)
    {
        StringBuilder buffer = new StringBuilder(64);
        buffer.append(context.getContextPath());
        buffer.append(uri);
                
        return buffer.toString();        
    }    
}
