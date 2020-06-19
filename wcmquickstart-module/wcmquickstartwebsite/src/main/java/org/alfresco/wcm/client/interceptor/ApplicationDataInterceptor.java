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
package org.alfresco.wcm.client.interceptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.PathResolutionDetails;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.exception.PageNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Load application-wide data into the Surf RequestContext and Spring model.
 * 
 * @author Chris Lack
 */
public class ApplicationDataInterceptor extends HandlerInterceptorAdapter
{
    private static final Log log = LogFactory.getLog(ApplicationDataInterceptor.class);

    private WebSiteService webSiteService;
    private ModelDecorator modelDecorator;
    private Set<String> countryCodes = new TreeSet<String>();
    private Set<String> languageCodes = new TreeSet<String>();

    public void init()
    {
        countryCodes.addAll(Arrays.asList(Locale.getISOCountries()));
        languageCodes.addAll(Arrays.asList(Locale.getISOLanguages()));
    }

    /**
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(HttpServletRequest,
     *      HttpServletResponse, Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();

        // Get the website object and store it in the surf request context
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        WebSite webSite = webSiteService.getWebSite(serverName, serverPort, contextPath);

        if (webSite == null)
        {
            log.warn("Received request for which no configured website can be found: " + serverName + ":" + serverPort);
            throw new PageNotFoundException(serverName + ":" + serverPort);
        }

        WebSiteService.setThreadWebSite(webSite);
        requestContext.setValue("webSite", webSite);
        requestContext.setValue("website", webSite);

        // Get the current asset and section and store them in the surf request
        // context
        String path = request.getPathInfo();
        PathResolutionDetails resolvedPath = webSite.resolvePath(path);
        
        if (resolvedPath.isRedirect())
        {
            String location = resolvedPath.getRedirectLocation();
            if (location.startsWith("/"))
            {
                location = contextPath + location;
            }
            response.sendRedirect(location);
            return false;
        }
        
        requestContext.setValue("asset", resolvedPath.getAsset());
        Section section = resolvedPath.getSection();
        if (section == null)
        {
            // If we haven't been able to resolve the section then use the root section 
            section = webSite.getRootSection();
        }
        requestContext.setValue("section", section);

        setLocaleFromPath(requestContext, path);

        return super.preHandle(request, response, handler);
    }

    protected void setLocaleFromPath(RequestContext requestContext, String path)
    {
        WebSite webSite = (WebSite) requestContext.getValue("webSite");
        Section rootSection = webSite.getRootSection();
        int pathlength = path.length();

        // Do we have a directory?
        if (pathlength > 1)
        {
            // Split
            String[] pathElements = path.split("/");
            if (log.isDebugEnabled())
            {
                log.debug("RootNavInterceptor: " + pathElements.length + " : " + path);
            }

            // What's the top level section?
            String topLevelPath = pathElements[1];

            if ((topLevelPath.length() == 2) && languageCodes.contains(topLevelPath))
            {
                //We'll shift the root section down to be the "locale root"
                rootSection = rootSection.getSection(topLevelPath);
                // Looks like a locale based path, treat as such
                String language = topLevelPath;
                Locale locale = null;

                // set locale onto Alfresco thread local
                locale = I18NUtil.parseLocale(language);
                I18NUtil.setLocale(locale);
                if (log.isDebugEnabled())
                {
                    log.debug("Picked " + I18NUtil.getLocale() + " from " + topLevelPath);
                }
                requestContext.setValue("locale", locale);
            }
        }
        requestContext.setValue("rootSection", rootSection);
    }

    /**
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(HttpServletRequest,
     *      HttpServletResponse, Object, ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception
    {
        super.postHandle(request, response, handler, modelAndView);

        modelDecorator.populate(request, modelAndView);
    }

    public void setWebSiteService(WebSiteService webSiteService)
    {
        this.webSiteService = webSiteService;
    }

    public void setModelDecorator(ModelDecorator modelDecorator)
    {
        this.modelDecorator = modelDecorator;
    }
}
