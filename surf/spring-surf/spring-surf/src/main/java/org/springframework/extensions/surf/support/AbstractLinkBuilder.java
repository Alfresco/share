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
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.resource.ResourceService;

/**
 * <p>Abstract base class for LinkBuilder implementations.  This
 * is provided as a convenience to developers who wish to build their
 * own custom LinkBuilder variations.
 * </p><p>
 * The Link Builder defines methods that are used generically to
 * construct links to other pages, page types or objects within the
 * system.
 * </p>
 * In general, links are either to specific "known" pages or to
 * page placeholders that must be resolved when the link is clicked.
 * </p><p>
 * Example - a link to a page:
 * </p><ul>
 * <li>String link = builder.page(context, "homepageInstance");</li>
 * </ul>
 * 
 * @author muzquiano
 * @author David Draper
 */
public abstract class AbstractLinkBuilder extends BaseFactoryBean implements LinkBuilder
{
    /**
     * <p>This constructor has been deprecated because it uses the deprecated <code>WebFrameworkServiceRegistry</code>
     * to obtain the actual Spring bean elements needed by the <code>AbstractLinkBuilder</code>.
     * 
     * @param serviceRegistry
     * @deprecated
     */
    public AbstractLinkBuilder(WebFrameworkServiceRegistry serviceRegistry)
    {
        this(serviceRegistry.getWebFrameworkConfiguration(), serviceRegistry.getModelObjectService(), serviceRegistry.getResourceService());
    } 
    
    /**
     * <p>This is the preferred constructor to use when instantiating an <code>AbstractLinkBuilder</code>. By supplying the
     * <code>WebFrameworkConfigElement</code>, <code>ModelObjectService</code> and <code>ResourceService</code> arguments
     * directly (rather than obtaining them from the <code>WebFrameworkServiceRegistry</code>) you can have link builders
     * using different services.</p>
     * 
     * @param webFrameworkConfigElement
     * @param modelObjectService
     * @param resourceService
     */
    public AbstractLinkBuilder(WebFrameworkConfigElement webFrameworkConfigElement, 
                               ModelObjectService modelObjectService,
                               ResourceService resourceService)
    {
        super(webFrameworkConfigElement, modelObjectService, resourceService);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#page(org.springframework.extensions.surf.RequestContext, java.lang.String)
     */
    public String page(RequestContext context, String pageId)
    {
        String formatId = getWebFrameworkConfiguration().getDefaultFormatId();
        return page(context, pageId, formatId);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#page(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String)
     */
    public String page(RequestContext context, String pageId, 
            String formatId)
    {
        return page(context, pageId, formatId, null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#page(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String, java.lang.String)
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId)
    {
        return page(context, pageId, formatId, objectId, null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#page(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public abstract String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params);
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#pageType(org.springframework.extensions.surf.RequestContext, java.lang.String)
     */
    public String pageType(RequestContext context, String pageTypeId)
    {
        String formatId = getWebFrameworkConfiguration().getDefaultFormatId();
        return pageType(context, pageTypeId, formatId);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#pageType(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String)
     */
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId)
    {
        return pageType(context, pageTypeId, formatId, null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#pageType(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String, java.lang.String)
     */
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId)
    {
        return pageType(context, pageTypeId, formatId, objectId, null);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#pageType(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public abstract String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId, Map<String, String> params);

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#object(org.springframework.extensions.surf.RequestContext, java.lang.String)
     */
    public String object(RequestContext context, String objectId)
    {
        String formatId = getWebFrameworkConfiguration().getDefaultFormatId();
        return object(context, objectId, formatId);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#object(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String)
     */
    public String object(RequestContext context, String objectId,
            String formatId)
    {
        return object(context, objectId, formatId, null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#object(org.springframework.extensions.surf.RequestContext, java.lang.String, java.lang.String, java.util.Map)
     */
    public abstract String object(RequestContext context, String objectId,
            String formatId, Map<String, String> params);
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.LinkBuilder#resource(org.springframework.extensions.surf.RequestContext, java.lang.String)
     */
    public abstract String resource(RequestContext context, String uri);    
}
