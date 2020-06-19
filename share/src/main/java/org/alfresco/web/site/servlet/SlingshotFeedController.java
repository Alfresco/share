/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.mvc.FeedController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Slingshot override of the SpringSurf FeedController class. This implementation allows for
 * a single configuration switch to allow SSO configuration for authentication to be used in
 * preference to the "Basic Auth" pattern provided by the base feed controller if external-auth
 * is set to true. If external-auth is false then the default basic auth from FeedController 
 * will be used
 * <p>
 * Users of SSO may wish feed client apps to use whatever auth stack is already for the rest
 * of Share. If that is the case they should copy the settings from the "alfresco" connector
 * to the "alfresco-feed" instance and specify <external-auth>true</external-auth>
 * 
 * @author Kevin Roast
 */
public class SlingshotFeedController extends FeedController
{
    public static final String ENDPOINT_ALFRESCO_FEED = "alfresco-feed";

    private RemoteConfigElement config;
    
    private ConfigService configService;
    
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * @see org.springframework.extensions.surf.mvc.FeedController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res)
    {
        // retrieve Alfresco endpoint descriptor and query for Basic Auth configuration
        EndpointDescriptor descriptor = getRemoteConfig().getEndpointDescriptor(ENDPOINT_ALFRESCO_FEED);
        if (!descriptor.getExternalAuth())
        {
            return super.handleRequestInternal(req, res);
        }
        else
        {
            // get the URI (after the controller)
            String uri = (String) req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            uri = uri + (req.getQueryString() != null ? ("?" + req.getQueryString()) : "");
            
            try
            {
                // forward to the authenticated webscript controller for the service
                // this ensures the usual "alfresco" auth pattern will be used rather than basic auth
                req.getRequestDispatcher("/page/" + uri).forward(req, res);
            }
            catch (Throwable e)
            {
                throw new AlfrescoRuntimeException(e.getMessage(), e);
            }
            
            return null;
        }
    }
    
    /**
     * Gets the remote config.
     * 
     * @return the remote config
     */
    private RemoteConfigElement getRemoteConfig()
    {
        if (this.config == null)
        {
            // retrieve the remote configuration
            this.config = (RemoteConfigElement)this.configService.getConfig("Remote").getConfigElement("remote");
        }
        
        return this.config;
    }
}