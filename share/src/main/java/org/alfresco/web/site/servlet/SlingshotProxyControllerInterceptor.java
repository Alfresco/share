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

import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.webscripts.servlet.mvc.ProxyControllerInterceptor;

/**
 * ProxyControllerInterceptor to allow Http Basic Authentication passthrough for
 * content node URLs.
 * 
 * @author Kevin Roast
 */
public class SlingshotProxyControllerInterceptor implements ProxyControllerInterceptor
{
    /**
     * @see org.springframework.extensions.webscripts.servlet.mvc.ProxyControllerInterceptor#allowHttpBasicAuthentication(org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor, java.lang.String)
     */
    @Override
    public boolean allowHttpBasicAuthentication(EndpointDescriptor endpoint, String uri)
    {
        return uri.contains("/api/node/content") || uri.contains("/cmis/") && uri.contains("/content");
    }

    /**
     * @see org.springframework.extensions.webscripts.servlet.mvc.ProxyControllerInterceptor#exceptionOnError(org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor, String)
     */
    @Override
    public boolean exceptionOnError(EndpointDescriptor endpoint, String uri)
    {
        return allowHttpBasicAuthentication(endpoint, uri);
    }
}
