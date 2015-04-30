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

package org.springframework.extensions.surf.uri;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * URI template remapping controller.
 * 
 * Using the facilities of the UriTemplateIndex and a configured list of URI Template mappings.
 * @see UriTemplateIndex
 * 
 * Each URI Template maps to one a page resource urls. The page resource URL is then forwarded
 * to the PageRendererServlet. 
 *
 * @author kevinr
 * @author muzquiano
 */
public class UriTemplateController extends AbstractController
{
    private static Log logger = LogFactory.getLog(UriTemplateController.class);
         
    public static final String CONFIG_ELEMENT = "UriTemplate";
    
    private ConfigService configService;
    
    /** URI Template index - Application url mappings */
    private UriTemplateMappingIndex uriTemplateIndex;
    
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * Spring init method
     */
    public void init()
    {
        initUriIndex(this.configService);
    }

    /**
     * Initialise the list of URL Mapper objects for the PageRenderer
     */
    private void initUriIndex(ConfigService configService)
    {
        Config config = configService.getConfig(CONFIG_ELEMENT);
        if (config == null)
        {
            throw new PlatformRuntimeException("Cannot find required config element 'UriTemplate'.");
        }
        ConfigElement uriConfig = config.getConfigElement("uri-mappings");
        if (uriConfig == null)
        {
            throw new PlatformRuntimeException("Missing required config element 'uri-mappings' under 'UriTemplate'.");
        }
        this.uriTemplateIndex = new UriTemplateMappingIndex(uriConfig);
    }

    /**
     * Match the specified URI against the URI template index
     * 
     * @param uri to match
     * 
     * @return the resource URL to use
     */
    private String matchUriTemplate(String uri)
    {
        String resource = this.uriTemplateIndex.findMatchAndReplace(uri);
        if (resource == null)
        {
            resource = uri;
        }
        return resource;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception
    {
        // get the URI (after the controller)
        String uri = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        // query string
        String qs = req.getQueryString();

        // build the uri ready for URI Template match
        uri = uri + (qs != null ? ("?" + qs) : "");

        if (logger.isDebugEnabled())
            logger.debug("Matching application URI template: " + uri);

        String resource = matchUriTemplate(uri);

        if (logger.isDebugEnabled())
            logger.debug("Resolved uri template to resource: " + resource);

        // rebuild page servlet URL to perform forward too
        req.getRequestDispatcher(resource).forward(req, res);
        
        return null;
    }
}