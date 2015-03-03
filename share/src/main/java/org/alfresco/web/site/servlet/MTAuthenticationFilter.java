/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.SlingshotPageViewResolver;

/**
 * Filter providing access to the servlet request for the {@link SlingshotPageViewResolver}
 * downstream. This enables the user context to be bound to the RequestContext earlier in the
 * SpringSurf lifecycle than would normally be available. This is essential for MT authentication
 * as resolving the view name from the page url requires a remote call, which for MT must be
 * authenticated (even for authentication=none webscripts) to resolve the correct Tenant.
 * 
 * @author Kevin Roast
 */
public class MTAuthenticationFilter implements Filter
{
    /** Thread local holder of the HttpServletRequest */
    private static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();
    
    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException
    {
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException
    {
        if (req instanceof HttpServletRequest)
        {
            requestHolder.set((HttpServletRequest)req);

            if (((HttpServletRequest) req).getHeader(ACCEPT_LANGUAGE_HEADER) == null)
            {
                req = new SlingshotServletRequestWrapper((HttpServletRequest) req);
                ((SlingshotServletRequestWrapper) req).addHeader(ACCEPT_LANGUAGE_HEADER, "en_US");
            }
        }
        try
        {
            chain.doFilter(req, res);
        }
        finally
        {
            requestHolder.remove();
        }
    }
    
    /**
     * @return HttpServletRequest for the current thread
     */
    public static HttpServletRequest getCurrentServletRequest()
    {
        return requestHolder.get();
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
    }
}