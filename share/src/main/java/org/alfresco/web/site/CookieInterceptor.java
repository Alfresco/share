/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.web.site;

import javax.servlet.http.Cookie;

import org.alfresco.web.config.cookie.CookieConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.mvc.AbstractWebFrameworkInterceptor;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

/**
 * Cookie Interceptor that removes configured cookies from response.
 * 
 * @author Alex Bykov
 */
public class CookieInterceptor extends AbstractWebFrameworkInterceptor
{
    private ConfigService configService;
    private CookieConfigElement cookieConfig;

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    public void init()
    {
        cookieConfig = (CookieConfigElement) configService.getConfig("Cookie").getConfigElement("cookie");
        if (cookieConfig == null)
        {
            cookieConfig = new CookieConfigElement();
        }
    }

    public void preHandle(WebRequest request) throws Exception
    {
    }

    public void postHandle(WebRequest webRequest, ModelMap model) throws Exception
    {
        if (webRequest instanceof DispatcherServletWebRequest && !cookieConfig.isCookieEnabled())
        {
            DispatcherServletWebRequest request = (DispatcherServletWebRequest) webRequest;
            if (request != null)
            {
                // remove cookies
                for (String cookie : cookieConfig.getCookiesToRemove())
                {
                    Cookie userCookie = new Cookie(cookie, "");
                    userCookie.setPath(request.getContextPath());
                    userCookie.setMaxAge(0);
                    request.getResponse().addCookie(userCookie);
                }
            }
        }
    }

    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
    }
}
