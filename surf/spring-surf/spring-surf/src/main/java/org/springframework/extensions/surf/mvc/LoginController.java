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

import org.springframework.extensions.surf.uri.UriUtils;
 
/**
 * Responds to Login POSTs to allow the user to authenticate to the application.
 * 
 * @author kevinr
 */
public class LoginController extends AbstractLoginController
{
    protected static final String PARAM_FAILURE = "failure";
    protected static final String PARAM_SUCCESS = "success";

    /**
     * Sends an HTTP redirect response to the success page provided in the request parameters, if present, falling
     * back to root of the web application.
     * 
     * @see org.springframework.extensions.surf.mvc.AbstractLoginController#onSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	@Override
    protected void onSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String successPage = (String) request.getParameter(LoginController.PARAM_SUCCESS);
        if (successPage != null)
        {
            response.sendRedirect(UriUtils.relativeUri(successPage));
        }
        else
        {
            response.sendRedirect(request.getContextPath());
        }
    }

    /**
     * Sends an HTTP redirect response to the failure page provided in the request parameters, if present, falling
     * back to root of the web application.
     * 
     * @see org.springframework.extensions.surf.mvc.AbstractLoginController#onSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
	@Override
	protected void onFailure(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
        String failurePage = (String) request.getParameter(LoginController.PARAM_FAILURE);
        
        // Invalidate the session to ensure any session ID cookies are no longer valid
        // as the auth has failed - mitigates session fixation attacks by ensuring that no
        // valid session IDs are created until after a successful user auth attempt
        request.getSession().invalidate();
        if (failurePage != null)
        {
            response.sendRedirect(UriUtils.relativeUri(failurePage));
        }
        else
        {
            response.sendRedirect(request.getContextPath());
        }
	}
 }