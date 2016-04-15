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

import javax.servlet.http.HttpSession;

import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.webscripts.Authenticator;
import org.springframework.extensions.webscripts.BasicHttpAuthenticatorFactory;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;

/**
 * Slingshot override of the BasicHttpAuthenticatorFactory to provide user and guest WebScript auth
 * through the standard Share authentication stack i.e. a validated User object in the HttpSession.
 * <p>
 * For Admin auth level WebScripts, if the current user is not an admin, then authentication is still
 * routed through the standard WebScript Basic Http authentication pattern.
 * 
 * @author Kevin Roast
 */
public class SlingshotBasicHttpAuthenticatorFactory extends BasicHttpAuthenticatorFactory
{
    @Override
    public Authenticator create(WebScriptServletRequest req, WebScriptServletResponse res)
    {
        Authenticator auth = null;
        switch (req.getServiceMatch().getWebScript().getDescription().getRequiredAuthentication())
        {
            case admin:
            {
                HttpSession session = req.getHttpServletRequest().getSession(false);
                if (session != null)
                {
                    final User user = (User)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
                    if (user != null && user.isAdmin())
                    {
                        auth = new Authenticator()
                        {
                            @Override
                            public boolean emptyCredentials()
                            {
                                return false;
                            }
                            
                            @Override
                            public boolean authenticate(RequiredAuthentication required, boolean isGuest)
                            {
                                return true;
                            }
                        };
                    }
                    else
                    {
                        auth = super.create(req, res);
                    }
                }
                else
                {
                    auth = super.create(req, res);
                }
                break;
            }
            case user:
            {
                final HttpSession session = req.getHttpServletRequest().getSession(false);
                if (session != null)
                {
                    auth = new Authenticator()
                    {
                        @Override
                        public boolean emptyCredentials()
                        {
                            return false;
                        }
                        
                        @Override
                        public boolean authenticate(RequiredAuthentication required, boolean isGuest)
                        {
                            User user = (User)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
                            return (user != null && !AuthenticationUtil.isGuest(user.getId()));
                        }
                    };
                }
                break;
            }
            case guest:
            {
                final HttpSession session = req.getHttpServletRequest().getSession(false);
                if (session != null)
                {
                    auth = new Authenticator()
                    {
                        @Override
                        public boolean emptyCredentials()
                        {
                            return false;
                        }
                        
                        @Override
                        public boolean authenticate(RequiredAuthentication required, boolean isGuest)
                        {
                            User user = (User)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
                            return (user != null && AuthenticationUtil.isGuest(user.getId()));
                        }
                    };
                    break;
                }
            }
        }
        return auth;
    }
}
