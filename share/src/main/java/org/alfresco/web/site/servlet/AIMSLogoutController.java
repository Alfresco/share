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
package org.alfresco.web.site.servlet;

import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.servlet.OIDCFilterSessionStore;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.LogoutController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AIMSLogoutController extends AbstractController
{
    protected AIMSConfig config;
    protected LogoutController logoutController;

    /**
     *
     * @param config
     */
    public void setConfig(AIMSConfig config) { this.config = config; }

    /**
     *
     * @param logoutController
     */
    public void setLogoutController(LogoutController logoutController) { this.logoutController = logoutController; }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        if (config.isEnabled())
        {
            // Handle callback from Identity Service
            if (request.getParameter("success") != null)
            {
                // Do the Share logout
                logoutController.handleRequestInternal(request, response);

                doRedirect(response, request.getContextPath());
            }
            else
            {
                // Redirect the user to Identity Service logout endpoint
                HttpSession session = request.getSession(false);
                if (session != null)
                {
                    String userId = (String) session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
                    if (userId != null)
                    {
                        OIDCFilterSessionStore.SerializableKeycloakAccount account =
                                (OIDCFilterSessionStore.SerializableKeycloakAccount) session.getAttribute(KeycloakAccount.class.getName());

                        if (account != null)
                        {
                            // Build the url for Identity Service Front-Channel logout
                            KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(config.getAdapterConfig());
                            UrlBuilder urlBuilder = new UrlBuilder(deployment.getLogoutUrl().clone().build().toString());
                            urlBuilder.addParameter("id_token_hint", account.getKeycloakSecurityContext().getIdTokenString());
                            urlBuilder.addParameter("post_logout_redirect_uri", request.getRequestURL() + "?success");

                            doRedirect(response, urlBuilder.toString());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param response
     * @param location
     */
    protected void doRedirect(HttpServletResponse response, String location)
    {
        response.setStatus(301);
        response.setHeader("Location", location);
        response.setHeader("Cache-Control", "max-age=0");
    }
}
