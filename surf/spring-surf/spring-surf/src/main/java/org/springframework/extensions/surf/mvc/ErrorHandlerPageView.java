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

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;

/**
 * <p>Default view implementation for Surf system pages.</p>
 * 
 * @author muzquiano
 * @author David Draper
 */
public class ErrorHandlerPageView extends AbstractWebFrameworkView 
{
    /**
     * <p>This is the preferred constructor to use for instantiating a new <code>ErrorHandlerPageView</code> because it allows
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
    public ErrorHandlerPageView(WebFrameworkConfigElement webFrameworkConfiguration,
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
    public ErrorHandlerPageView(WebFrameworkServiceRegistry serviceRegistry)
    {
        super(serviceRegistry);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.mvc.AbstractWebFrameworkView#renderView(org.springframework.extensions.surf.render.RequestContext)
     */
    protected void renderView(RequestContext context) throws Exception 
    {
        String errorHandlerPageId = this.getUrl();
        getRenderService().renderErrorHandlerPage(context, errorHandlerPageId);
    }        
}