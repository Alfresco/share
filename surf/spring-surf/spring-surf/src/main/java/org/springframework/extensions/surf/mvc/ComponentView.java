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

import static org.springframework.extensions.surf.WebFrameworkConstants.FOCUS;
import static org.springframework.extensions.surf.WebFrameworkConstants.MODE;
import static org.springframework.extensions.surf.WebFrameworkConstants.REGION_ID;
import static org.springframework.extensions.surf.WebFrameworkConstants.REGION_SCOPE_GLOBAL;
import static org.springframework.extensions.surf.WebFrameworkConstants.SCOPE_ID;
import static org.springframework.extensions.surf.WebFrameworkConstants.SOURCE_ID;

import java.util.Map;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.RenderMode;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.render.RenderUtil;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.types.Component;

/**
 * <p>Renders a component along with its chrome. A Surf component is identified by the following tokens:</p>
 * <ul>
 * <li>scopeId: The scope (either "page", "template" or "global")</li>
 * <li>regionId: The id of the region</li>
 * <li>sourceId: The id of the page, template or "global"</li>
 * <li>mode: Either "view", "edit" or "help"</li>
 * <li>focus: Either "header", "body" or "all"</li>
 * </ul>
 *
 * @author muzquiano
 * @author Dave Draper
 */
public class ComponentView extends AbstractWebFrameworkView
{
    /**
     * <p>This is the preferred constructor to use for instantiating a new <code>ComponentView</code> because it allows
     * complete flexibility when rendering the view. An <code>AbstractWebFrameworkView</code> is typically instantiated from
     * within a <code>AbstractWebFrameworkViewResolver</code> and all the arguments in the constructor signature should be
     * supplied to the <code>AbstractWebFrameworkViewResolver</code> as beans via the Spring configuration.</p> 
     * 
     * @param webFrameworkServiceRegistry
     * @param webFrameworkConfiguration
     * @param modelObjectService
     * @param resourceService
     * @param presentationService
     * @param renderService
     * @param templatesContainer
     */
    public ComponentView(WebFrameworkConfigElement webFrameworkConfiguration,
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
    public ComponentView(WebFrameworkServiceRegistry serviceRegistry)
    {
        super(serviceRegistry);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.mvc.AbstractWebFrameworkView#renderView(org.springframework.extensions.surf.render.RequestContext)
     */
    protected void renderView(RequestContext context)
        throws Exception
    {
        // this method assumes that the uri tokens have already been processed through the
        // uri template mappings.
        //
        // the tokens are represented by:
        //
        //    <mode>/<focus>/<scopeId>/<regionId>/<sourceId>

        // tokens
        Map<String, String> uriTokens = getUriTokens();
        String mode = uriTokens.get(MODE);
        String focus = uriTokens.get(FOCUS);
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

        // determine the render attributes
        RenderMode renderMode = RenderMode.VIEW;
        if (mode != null)
        {
            renderMode = RenderMode.valueOf(mode.toUpperCase());
        }

        RenderFocus renderFocus = RenderFocus.BODY;
        if (focus != null)
        {
            renderFocus = RenderFocus.valueOf(focus.toUpperCase());
        }

        // create the component id
        String componentId = RenderUtil.generateComponentId(scopeId, regionId, sourceId);

        // determine the valid component id
        Component component = getObjectService().getComponent(componentId);
        if (component == null)
        {
            if (componentId.startsWith("/"))
            {
                // try taking the "/" off the front
                component = getObjectService().getComponent(componentId.substring(1));
            }
            else
            {
                // try adding a "/" to the front
                component = getObjectService().getComponent("/" + componentId);
            }
        }

        // set the render mode
        context.setRenderMode(renderMode);

        // do the render of the component
        getRenderService().renderComponent(context, renderFocus, component, null, false);
    }
}