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
package org.alfresco.web.extensibility;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public class AIMSAuthenticationUtil /*extends AuthenticationUtil*/ {

    // private ServletContext servletContext;
    private static RefreshableKeycloakSecurityContext keycloakContext;

    public AIMSAuthenticationUtil() {}

    public static boolean isAuthenticated(HttpServletRequest request) {
        if ( ! AuthenticationUtil.isAuthenticated(request))
        {
            return false;
        }



        return true;
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationUtil.logout(request, response);
    }

    public static void login(HttpServletRequest request, String userId) {
        AuthenticationUtil.login(request, userId);
    }
}
