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

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * Abstract web framework interceptor
 * 
 * @author muzquiano
 */
public abstract class AbstractWebFrameworkInterceptor implements ApplicationContextAware, ServletContextAware, WebRequestInterceptor 
{
    private ApplicationContext applicationContext = null;
    private ServletContext servletContext = null;
    private WebFrameworkServiceRegistry webFrameworkServiceRegistry = null;
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Retrieves the application context
     * 
     * @return app context
     */
    public ApplicationContext getApplicationContext()
    {
        return this.applicationContext;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }
    
    /**
     * Sets the service registry.
     * 
     * @param webFrameworkServiceRegistry the new service registry
     */
    public void setServiceRegistry(WebFrameworkServiceRegistry webFrameworkServiceRegistry)
    {
        this.webFrameworkServiceRegistry = webFrameworkServiceRegistry;
    }
    
    /**
     * Gets the service registry.
     * 
     * @return the service registry
     */
    public WebFrameworkServiceRegistry getServiceRegistry()
    {
        return this.webFrameworkServiceRegistry;
    }
    
    /**
     * Gets the web framework configuration.
     * 
     * @return the web framework configuration
     */
    public WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        return this.webFrameworkServiceRegistry.getWebFrameworkConfiguration();
    }    
    
    /**
     * Gets the model object service
     * 
     * @return model object service
     */
    public ModelObjectService getObjectService()
    {
        return this.webFrameworkServiceRegistry.getModelObjectService();
    }
    
    /**
     * Gets the render service.
     * 
     * @return the render service
     */
    public RenderService getRenderService()
    {
        return this.webFrameworkServiceRegistry.getRenderService();
    }
}