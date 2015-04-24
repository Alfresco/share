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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Listen for call from a client to log the user out from the current session.
 * 
 * @author kevinr
 * @author muzquiano
 */
public class LogoutController extends AbstractController
{
    public static final String REDIRECT_URL_PARAMETER = "redirectURL";
    public static final String REDIRECT_URL_PARAMETER_QUERY_KEY = "redirectURLQueryKey";
    public static final String REDIRECT_URL_PARAMETER_QUERY_VALUE = "redirectURLQueryValue";
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        AuthenticationUtil.logout(request, response);
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Check for a redirect URL - this should only be used when login is not required...
        String redirectURL = request.getParameter(REDIRECT_URL_PARAMETER);
        if (redirectURL != null)
        {
            String[] keys = request.getParameterValues(REDIRECT_URL_PARAMETER_QUERY_KEY);
            String[] values = request.getParameterValues(REDIRECT_URL_PARAMETER_QUERY_VALUE);
            
            if (keys != null && 
                values != null && 
                keys.length > 0 && 
                keys.length == values.length)
            {
                for (int i=0; i<keys.length; i++)
                {
                    String delim = (i == 0) ? "?" : "&";
                    redirectURL = redirectURL + delim + keys[i] + "=" + values[i];
                }
            }
            response.setHeader("Location", redirectURL);
        }
        else
        {
            // redirect to the root of the website
            response.setHeader("Location", request.getContextPath());
        }
        
        return null;
    }
}