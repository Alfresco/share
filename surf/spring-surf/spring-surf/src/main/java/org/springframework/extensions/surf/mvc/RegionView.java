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

package org.springframework.extensions.surf.mvc;

import static org.springframework.extensions.surf.WebFrameworkConstants.PAGE_ID;
import static org.springframework.extensions.surf.WebFrameworkConstants.REGION_ID;
import static org.springframework.extensions.surf.WebFrameworkConstants.REGION_SCOPE_GLOBAL;
import static org.springframework.extensions.surf.WebFrameworkConstants.REGION_SCOPE_PAGE;
import static org.springframework.extensions.surf.WebFrameworkConstants.REGION_SCOPE_TEMPLATE;
import static org.springframework.extensions.surf.WebFrameworkConstants.SCOPE_ID;
import static org.springframework.extensions.surf.WebFrameworkConstants.SOURCE_ID;
import static org.springframework.extensions.surf.WebFrameworkConstants.TEMPLATE_ID;

import java.util.Map;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;

/**
 * <p>View implementation for a Surf page region. URLs are expected to be invoked as shown:</p>
 * <ul>
 * <li>/regionId/{regionId} - displays a globally scoped region</li>
 * <li>/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId} - displays a page or template scoped region</li>
 * </ul>
 * <p>Most commonly, these are:</p>
 * <ul>
 * <li>scopeId: the scope of the region (i.e. 'page', 'template' or 'global')</li>
 * <li>regionId: the id of the region (i.e. 'footer')</li>
 * <li>sourceId: the id of the template or page instance or 'global' if in the global scope</li>
 * </ul>
 * <p>The region is rendered along with its chrome. If a component is contained in the region, it is also rendered.</p>
 *
 * @author muzquiano
 * @author David Draper
 */
public class RegionView extends AbstractWebFrameworkView
{
    /**
     * <p>This is the preferred constructor to use for instantiating a new <code>RegionView</code> because it allows
     * complete flexibility when rendering the view. An <code>AbstractWebFrameworkView</code> is typically instantiated from
     * within a <code>AbstractWebFrameworkViewResolver</code> and all the arguments in the constructor signature should be
     * supplied to the <code>AbstractWebFrameworkViewResolver</code> as beans via the Spring configuration.</p> 
     * 
     * @param webFrameworkServiceRegistry
     * @param webFrameworkConfiguration
     * @param modelObjectService
     * @param resourceService
     * @param renderService
     * @param templatesContainer
     */
    public RegionView(WebFrameworkConfigElement webFrameworkConfiguration,
                      ModelObjectService modelObjectService,
                      ResourceService resourceService,
                      RenderService renderService,
                      TemplatesContainer templatesContainer)
    {
        super(webFrameworkConfiguration, modelObjectService, resourceService, renderService, templatesContainer);
    }
    
    /**
     * <p>This constructor should be avoided if possible because it relies on the supplied <code>WebFrameworkServiceRegistry</code>
     * argument to provide all the other Spring beans required to render the view. This means that there is no flexibility via
     * configuration to adapt different views to use different beans.</p>
     * 
     * @param serviceRegistry
     * @deprecated
     */
    public RegionView(WebFrameworkServiceRegistry serviceRegistry)
    {
        super(serviceRegistry);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.mvc.AbstractWebFrameworkView#renderView(org.springframework.extensions.surf.render.RenderContext)
     */
    protected void renderView(RequestContext context)
        throws Exception
    {
        // this method assumes that the uri tokens have already been processed through the
        // uri template mappings.
        //
        // the tokens are represented by:
        //
        //    <scopeId>/<regionId>/<sourceId>

        // tokens
        Map<String, String> uriTokens = getUriTokens();
        String scopeId = uriTokens.get(SCOPE_ID);
        String regionId = uriTokens.get(REGION_ID);
        String sourceId = uriTokens.get(SOURCE_ID);

        // defaults
        if (scopeId == null)
        {
            scopeId = REGION_SCOPE_GLOBAL;
        }
        if (sourceId == null)
        {
            sourceId = REGION_SCOPE_GLOBAL;
        }

        // the region id always has to be provided
        if (regionId == null)
        {
            throw new RequestDispatchException("Region ID is missing");
        }

        // populate the model
        context.getModel().put(REGION_ID, regionId);
        context.getModel().put("regionScopeId", scopeId);
        context.getModel().put("regionSourceId", sourceId);

        // page scope
        if (REGION_SCOPE_PAGE.equals(scopeId))
        {
            Page page = (Page) getObjectService().getPage(sourceId);
            if (page != null)
            {
                context.setPage(page);
                context.setTemplate(page.getTemplate(context));
            }
            context.getModel().put(PAGE_ID, sourceId);
        }

        // template scope
        if (REGION_SCOPE_TEMPLATE.equals(scopeId))
        {
            TemplateInstance templateInstance = (TemplateInstance) getObjectService().getTemplate(sourceId);
            if (templateInstance != null)
            {
                context.setTemplate(templateInstance);
            }
            context.getModel().put(TEMPLATE_ID, sourceId);
        }

        // TODO: any setup for other scopes?

        // render the region
        getRenderService().renderRegion(context, RenderFocus.BODY, sourceId, regionId, scopeId, null, false);
    }
}