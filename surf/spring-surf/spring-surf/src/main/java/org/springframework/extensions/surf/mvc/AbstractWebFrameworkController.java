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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Abstract Spring MVC Controller that produces Surf Views.
 * 
 * Developers who wish to implement custom Spring controllers for use
 * with Alfresco Surf will benefit by extending from this class.  This
 * class provides member functions for accessing the application context
 * as well as important Surf services.
 * 
 * @author muzquiano
 */
public abstract class AbstractWebFrameworkController extends AbstractController implements ServletContextAware
{
    private static final String MIMETYPE_HTML = "text/html;charset=utf-8";
    
    /** The web framework service registry. */
    private WebFrameworkServiceRegistry webFrameworkServiceRegistry;
        
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
        return this.getServiceRegistry().getWebFrameworkConfiguration();
    }    
    
    /**
     * Gets the model object service
     * 
     * @return model object service
     */
    public ModelObjectService getObjectService()
    {
        return this.getServiceRegistry().getModelObjectService();
    }
    
    /**
     * Gets the render service.
     * 
     * @return the render service
     */
    public RenderService getRenderService()
    {
        return this.getServiceRegistry().getRenderService();
    }    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // debug logging
        if (getLogger().isDebugEnabled())
        {
            String qs = request.getQueryString();
            getLogger().debug("Processing URL: ("  + request.getMethod() + ") " + request.getRequestURI() + 
                  ((qs != null && qs.length() != 0) ? ("?" + qs) : ""));
        }
        
        // set no cache headers
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        
        // set response content type and charset
        // TODO: is this the right place to do this?
        response.setContentType(MIMETYPE_HTML);        
        
        // create model and view
        return createModelAndView(request, response);
    }
    
    /**
     * Internal method to be implemented by inheriting class to create
     * model and view for the controller.
     * 
     * @param request
     * @param response
     * @return ModelAndView
     */
    public abstract ModelAndView createModelAndView(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    /**
     * Returns a logger for the controller
     * @return logger
     */
    public abstract Log getLogger();
}