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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

public class IdentityServiceFilter extends KeycloakOIDCFilter /*implements DependencyInjectedFilter, ApplicationContextAware*/ {

    private static Log logger = LogFactory.getLog(SSOAuthenticationFilter.class);

    private boolean enabled;

    private ApplicationContext context;
    private ConnectorService connectorService;
    private SlingshotLoginController loginController;

    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";
    public static final String ALFRESCO_API_ENDPOINT_ID = "alfresco-api";

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());

        IdentityServiceFilterConfigUtils identityServiceFilterConfigUtils = (IdentityServiceFilterConfigUtils) this.context.getBean("identityServiceFilterConfigUtils");

        this.enabled = identityServiceFilterConfigUtils.isIdentityServiceEnabled();
        this.connectorService = (ConnectorService) context.getBean("connector.service");
        this.loginController = (SlingshotLoginController) context.getBean("loginController");
    }

    public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse res = (HttpServletResponse) sres;
        HttpSession session = req.getSession();

        if (this.enabled && !AuthenticationUtil.isAuthenticated(req)) {
            super.doFilter(sreq, sres, chain);

            KeycloakSecurityContext context = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
            if (context != null) {
                String username = context.getToken().getPreferredUsername();
                String accessToken = context.getTokenString();
                this.onSuccess(req, res, username, accessToken, session);
            }
        }
        chain.doFilter(sreq, sres);
    }

    private void onSuccess(HttpServletRequest req, HttpServletResponse res, String username, String accessToken, HttpSession session) throws ServletException {
        try {
            // Perform a "silent" init - i.e. no user creation or remote connections ?
            // @TODO: Find out about the magic happening here !
            RequestContextUtil.initRequestContext(this.context, req, true);

            // Set the external auth flag so the UI knows we are using SSO etc.
            session.setAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH, Boolean.TRUE);
            // Ensure User ID is in session so the web-framework knows we have logged in
            session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);

            // Get the slf_ticket from repo, using the JWT token from Keycloak
            String alfTicket = this.getAlfTicket(session, username, accessToken);

            // Set the alfTicket into connector's session for further use on repo calls (will be set on the RemoteClient)
            Connector connector = this.connectorService.getConnector(ALFRESCO_ENDPOINT_ID, username, session);
            connector.getConnectorSession().setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, alfTicket);

            // Set credential username for further use on repo
            // if there is no pass, as in our case, there will be a "X-Alfresco-Remote-User" header set using this value
            CredentialVault vault = FrameworkUtil.getCredentialVault(session, username);
            Credentials credentials = vault.newCredentials(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            vault.store(credentials);

            // Inform the Slingshot login controller of a successful login attempt as further processing may be required ?
            this.loginController.beforeSuccess(req, res);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new ServletException(e);
        }
    }

    /**
     * Get an alf_ticket using the JWT token from Keycloak
     *
     * @param session
     * @param username
     * @param accessToken
     * @throws ConnectorServiceException
     */
    private String getAlfTicket(HttpSession session, String username, String accessToken) throws ConnectorServiceException {
        Connector connector = this.connectorService.getConnector(ALFRESCO_API_ENDPOINT_ID, username, session);

        ConnectorContext c = new ConnectorContext(HttpMethod.GET, null, new HashMap<String, String>() {{
            put("Authorization", "Bearer " + accessToken);
        }});
        c.setContentType("application/json");
        Response r = connector.call("/-default-/public/authentication/versions/1/tickets/-me-", c);
        if (Status.STATUS_OK != r.getStatus().getCode()) {
            throw new AlfrescoRuntimeException("Failed to read the returned content");
        }

        JSONObject json = new JSONObject(r.getText());
        String alfTicket = json.getJSONObject("entry").getString("id");

        return alfTicket;
    }
}
