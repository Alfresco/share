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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Superclass for login controller implementations, using the Template Method design pattern.
 * 
 * Authenticates the user by using userFactory, and then calls 
 * {@link #onSuccess(HttpServletRequest, HttpServletResponse)} if authentication is successful, or
 * {@link #onFailure(HttpServletRequest, HttpServletResponse)} if authentication fails.
 *    
 * @author muzquiano
 * @author kevinr
 */
public abstract class AbstractLoginController extends AbstractController
{
    /* password parameter */
    protected static final String PARAM_PASSWORD = "password";
    
    /* username parameter */
    protected static final String PARAM_USERNAME = "username";
    
    
    /**
     * <p>A <code>UserFactory</code> is required to authenticate requests. It will be supplied by the Spring Framework
     * providing that the controller is configured correctly - it requires that a "userFactory" is set with an instance
     * of a <code>UserFactory</code>. The <code>ConfigBeanFactory</code> can be used to generate <code>UserFactory</code>
     * Spring Beans</p>
     */
    private UserFactory userFactory;

    /**
     * <p>This method is provided to allow the Spring framework to set a <code>UserFactory</code> required for authenticating
     * requests</p>
     * 
     * @param userFactory
     */
    public void setUserFactory(UserFactory userFactory) 
    {
        this.userFactory = userFactory;
    }


    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        request.setCharacterEncoding("UTF-8");
        
        String username = (String) request.getParameter(AbstractLoginController.PARAM_USERNAME);
        String password = (String) request.getParameter(AbstractLoginController.PARAM_PASSWORD);
        
        boolean success = false;
        try
        {
            // check whether there is already a user logged in
            HttpSession session = request.getSession(false);
            // handle SSO which doesn't set a user until later
            if (session != null && request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID) != null)
            {
                // destroy old session and log out the current user
                AuthenticationUtil.logout(request, response);
            }
            
            // see if we can authenticate the user
            boolean authenticated = this.userFactory.authenticate(request, username, password);
            if (authenticated)
            {
                AuthenticationUtil.login(request, response, username, false);
                
                // mark the fact that we succeeded
                success = true;
            }
        }
        catch (Throwable err)
        {
            throw new ServletException(err);
        }
        
        // If they succeeded in logging in, redirect to the success page
        // Otherwise, redirect to the failure page
        if (success)
        {
            onSuccess(request, response);
        }
        else
        {
            onFailure(request, response);
        }
        
        return null;
    }

    /**
     * Template method. 
     * 
     * Called after failed authentication.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @throws Exception in case of errors
     */
    protected abstract void onFailure(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Template method. 
     * 
     * Called after successful authentication.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @throws Exception in case of errors
     */
    protected abstract void onSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception;
}