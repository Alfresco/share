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
package org.alfresco.web.site;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.site.servlet.MTAuthenticationFilter;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.mvc.AbstractWebFrameworkInterceptor;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

/**
 * Framework interceptor responsible for constructing user dashboards if one has not already
 * been initialised and persisted for the current user.
 * 
 * @author Kevin Roast
 */
public class UserDashboardInterceptor extends AbstractWebFrameworkInterceptor
{
    private static final Pattern PATTERN_DASHBOARD_PATH = Pattern.compile(".*/user/([^/]*)/dashboard");
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#preHandle(org.springframework.web.context.request.WebRequest)
     */
    @Override
    public void preHandle(WebRequest request) throws Exception
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final String pathInfo = rc.getUri();
        Matcher matcher;
        if (pathInfo != null && (matcher = PATTERN_DASHBOARD_PATH.matcher(pathInfo)).matches())
        {
            HttpServletRequest req = MTAuthenticationFilter.getCurrentServletRequest();
            if (req != null)
            {
                try
                {
                    // init the user object so we can test the current user ID against the page uri
                    ServletUtil.setRequest(req);
                    RequestContextUtil.populateRequestContext(rc, req);
                    final String userid = rc.getUserId();

                    // test user dashboard page exists?
                    String usernameFromURL = URLDecoder.decode(matcher.group(1));
                    if (isUserIDMatchingUsernameFromURL(userid, usernameFromURL))
                    {
                        WebFrameworkServiceRegistry serviceRegistry = rc.getServiceRegistry();
                        if (serviceRegistry.getModelObjectService().getPage("user/" + userid + "/dashboard") == null)
                        {
                            // no dashboard found! create initial dashboard for this user...
                            Map<String, String> tokens = new HashMap<String, String>(2);
                            tokens.put("userid", userid);
                            serviceRegistry.getPresetsManager().constructPreset("user-dashboard", tokens);
                        }
                    }
                    else
                    {
                       // reset the user context to ensure Guest or similar is not applied - this will avoid
                       // issues with SSO filters or similar that expect empty user after interceptor execution
                       rc.setUser(null);
                       rc.setPage(ThreadLocalRequestContext.getRequestContext().getRootPage());
                    }
                }
                catch (UserFactoryException uerr)
                {
                    // unable to generate the user dashboard - the user can still do this by visiting the index page
                }
            }
        }
    }

    private boolean isUserIDMatchingUsernameFromURL(final String userid, String usernameFromURL)
    {
        if (userid == null || usernameFromURL == null)
        {
            return false;
        }
        return shouldUseCaseSensitiveUsernameCompare() ? userid.equals(usernameFromURL) : userid.equalsIgnoreCase(usernameFromURL);
    }

    private boolean shouldUseCaseSensitiveUsernameCompare()
    {
        // TODO we should probably make a request to the backend and
        // ask for the value of "user.name.caseSensitive" property
        // all though FileFolderServiceImpl.resolveNamePath and the simpleSearch method it uses
        // does not seem to take into consideration the case of the user name
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#postHandle(org.springframework.web.context.request.WebRequest, org.springframework.ui.ModelMap)
     */
    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception
    {
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#afterCompletion(org.springframework.web.context.request.WebRequest, java.lang.Exception)
     */
    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
    }
}
