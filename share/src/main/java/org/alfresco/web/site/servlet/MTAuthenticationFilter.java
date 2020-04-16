/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
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
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

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
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        if (req instanceof HttpServletRequest)
        {
            req = filterRequestHeader((HttpServletRequest) req);
            requestHolder.set((HttpServletRequest) req);
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

    private String getLanguageFromRequestHeader(HttpServletRequest req)
    {
        Locale locale = Locale.getDefault();
        String acceptLang = req.getHeader(ACCEPT_LANGUAGE_HEADER);
        if (acceptLang != null && acceptLang.length() > 0)
        {
            StringTokenizer tokenizer = new StringTokenizer(StringUtils.stripUnsafeHTMLTags(acceptLang), ",; ");
            // get language and convert to java locale format
            String language = tokenizer.nextToken().replace('-', '_');
            locale = I18NUtil.parseLocale(language);
        }
        return locale.getLanguage();
    }

    private SlingshotServletRequestWrapper filterRequestHeader(HttpServletRequest req)
    {
        SlingshotServletRequestWrapper request = new SlingshotServletRequestWrapper(req);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            request.addHeader(key, StringUtils.stripUnsafeHTMLTags(value, false));
        }
        request.addHeader(ACCEPT_LANGUAGE_HEADER, getLanguageFromRequestHeader(req));
        return request;
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