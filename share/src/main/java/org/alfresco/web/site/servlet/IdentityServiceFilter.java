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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.IdentityServiceFilterConfigUtils;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class IdentityServiceFilter extends KeycloakOIDCFilter  {

    private ServletContext servletContext;
    private boolean enabled;

    private ConnectorService connectorService;

    private SlingshotLoginController loginController;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        this.servletContext = filterConfig.getServletContext();

        ApplicationContext context = this.getApplicationContext();
        IdentityServiceFilterConfigUtils identityServiceFilterConfigUtils = (IdentityServiceFilterConfigUtils) context.getBean("identityServiceFilterConfigUtils");

        this.enabled = identityServiceFilterConfigUtils.isIdentityServiceEnabled();
        this.connectorService = (ConnectorService) context.getBean("connector.service");
        this.loginController = (SlingshotLoginController) context.getBean("loginController");
    }

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException
    {
        if (this.enabled) {
            super.doFilter(sreq, sres, chain);

            HttpServletRequest req = (HttpServletRequest) sreq;
            HttpServletResponse res = (HttpServletResponse) sres;
            HttpSession session = req.getSession();

            KeycloakSecurityContext context = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
            if (context != null && !AuthenticationUtil.isAuthenticated(req))
            {
                String username = context.getToken().getPreferredUsername();
                String accessToken = context.getTokenString();
                this.onSuccess(req, res, username, accessToken, session);
            }
        }
        chain.doFilter(sreq, sres);
    }

/*    protected ServletRequest wrapHeaderAuthenticatedRequest(ServletRequest sreq)
    {
        if (sreq instanceof HttpServletRequest)
        {
            final HttpServletRequest req = (HttpServletRequest) sreq;
            sreq = new HttpServletRequestWrapper(req)
            {
                @Override
                public String getRemoteUser()
                {
                    KeycloakSecurityContext context = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
                    if (context != null)
                    {
                        return context.getToken().getPreferredUsername();
                    }
                    else
                    {
                        return null;
                    }
                }
            };
        }
        return sreq;
    }*/

    private void onSuccess(HttpServletRequest req, HttpServletResponse res, String username, String accessToken, HttpSession session)
    {
        AuthenticationUtil.login(req, res, username);

        session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);
        session.setAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH, Boolean.TRUE);

        try
        {
            RequestContextUtil.initRequestContext(this.getApplicationContext(), req, true);
            this.loginController.beforeSuccess(req, res);

            CredentialVault vault = FrameworkUtil.getCredentialVault(session, username);
            Credentials credentials = vault.newCredentials(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_ACCESS_TOKEN, accessToken);
            vault.store(credentials);
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Error during loginController.onSuccess()", e);
        }
    }

    /*private void onSuccess(HttpServletRequest req, HttpServletResponse res, HttpSession session, String username)
    {
        // Ensure User ID is in session so the web-framework knows we have logged in
        session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);

        try
        {
            // Inform the Slingshot login controller of a successful login attempt as further processing may be required
            this.loginController.beforeSuccess(req, res);
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Error during loginController.onSuccess()", e);
        }
    }*/

    private ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(this.servletContext);
    }
}
