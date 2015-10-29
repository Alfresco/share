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

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

/**
 * Web Framework Handler Interceptor to apply Theme to the currently executing context.
 *
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class ThemeInterceptor extends AbstractWebFrameworkInterceptor
{
    private static Log logger = LogFactory.getLog(ThemeInterceptor.class);

    /**
     * The constant "alfTheme" used as an HttpSession attribute key.
     * TODO: Consider supporting a different session attribute key as this one is coupled to Alfresco.
     */
    protected static final String SESSION_CURRENT_THEME    = "alfTheme";

    /**
     * This is the request parameter than can be used to specify which theme to use for building a view.
     * The purpose of a theme is to support different pages being rendered for a particular page type. The
     * configuration for a theme should define which page to use for each page type.
     */
    private static final String _THEME_REQUEST_PARAMETER = "theme";

    /**
     * <p>The WebFrameworkConfigElement.</p>
     * TODO: Need a description of what this is and what it is used for!
     */
    private WebFrameworkConfigElement webFrameworkConfig;
    
    public void setWebFrameworkConfig(WebFrameworkConfigElement webFrameworkConfig)
    {
        this.webFrameworkConfig = webFrameworkConfig;
    }
    
    /**
     * Determines the theme to use for the request. The order of precedence for determining which
     * theme to use is as follows:
     * <ol><li>Look on the request parameter for a specific requested theme</li>
     * <li>Look for a theme choice previously set as a session attribute</li>
     * <li>Look for the theme defined in the site configuration</li>
     * <li>Look for the theme defined in the web-framework configuration</li>
     * <li>Use "default"</li>
     * </ol>
     *
     * TODO: Currently the code does not support web-framework configuration.
     * TODO: Need to decide whether specifying a theme as a request parameter should update the session attribute?
     *
     * @param webRequest The request being processed.
     */
    public void preHandle(WebRequest webRequest) throws Exception
    {
        Theme theme = null;

        // Check to see whether or not a particular theme has been specifically requested...
        String themeId = webRequest.getParameter(_THEME_REQUEST_PARAMETER);
        if (themeId != null)
        {
            // Check to see whether or not a theme exists for the requested theme id...
            theme = getObjectService().getTheme(themeId);
            if (theme == null) logger.warn("Unable to locate theme specified as request parameter with ID: " + themeId);
        }

        // Check the HttpSession to see whether or not a theme selection has been saved as an attribute...
        if (theme == null)
        {
            HttpSession session = ServletUtil.getSession(false);
            if (session != null)
            {
                theme = (Theme) session.getAttribute(SESSION_CURRENT_THEME);
            }
        }

        // Check the configuration for theme settings...
        if (theme == null)
        {
            // look for a theme in the Site Configuration
            Configuration siteConfiguration = ThreadLocalRequestContext.getRequestContext().getSiteConfiguration();
            if (siteConfiguration != null)
            {
                themeId = siteConfiguration.getProperty("theme");
            }

            // look for a theme in the web-framework configuration
            if (themeId == null)
            {
                themeId = getDefaultThemeId();
            }

            if (themeId != null)
            {
                theme = getObjectService().getTheme(themeId);
                if (theme == null)
                {
                    // fallback - if theme object no longer exists i.e. a theme has been deleted - then
                    // we must have a sensible fallback path - attempt to resolve default theme if this occurs.
                    themeId = getDefaultThemeId();
                    
                    theme = getObjectService().getTheme(themeId);
                }
            }
        }

        if (theme != null)
        {
            ThreadLocalRequestContext.getRequestContext().setTheme(theme);
        }
    }
    
    protected String getDefaultThemeId()
    {
        String themeId = webFrameworkConfig.getDefaultThemeId();
        if (themeId == null)
        {
            // select a default if no theme set elsewhere
            themeId = WebFrameworkConstants.DEFAULT_THEME_ID;
        }
        return themeId;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#postHandle(org.springframework.web.context.request.WebRequest, org.springframework.ui.ModelMap)
     */
    public void postHandle(WebRequest request, ModelMap model) throws Exception
    {
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#afterCompletion(org.springframework.web.context.request.WebRequest, java.lang.Exception)
     */
    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
    }
}
