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
