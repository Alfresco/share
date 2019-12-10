/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.ServerRequest;
import org.keycloak.adapters.servlet.OIDCFilterSessionStore.SerializableKeycloakAccount;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;

public class AIMSLogoutController extends SlingshotLogoutController {

    private static Log logger = LogFactory.getLog(AIMSLogoutController.class);

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String username = null;

        // Check whether there is already an user logged in
        HttpSession session = request.getSession(false);
        if (session != null)
        {
            username = (String) session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
            if (username != null && !username.isEmpty())
            {
                SerializableKeycloakAccount account = (SerializableKeycloakAccount) session.getAttribute(KeycloakAccount.class.getName());
                if (account != null)
                {
                    InputStream is = this.getServletContext().getResourceAsStream("/WEB-INF/keycloak.json");
                    KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(is);

                    account.getKeycloakSecurityContext().logout(deployment);
                    session.removeAttribute(KeycloakAccount.class.getName());
                    session.removeAttribute(KeycloakSecurityContext.class.getName());
                }
            }
        }

        return super.handleRequestInternal(request, response);
    }
}
