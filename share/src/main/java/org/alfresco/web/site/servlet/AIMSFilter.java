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
import org.alfresco.web.site.servlet.config.AIMSConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.rotation.AdapterTokenVerifier;
import org.keycloak.adapters.servlet.FilterRequestAuthenticator;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.keycloak.adapters.servlet.OIDCFilterSessionStore;
import org.keycloak.adapters.servlet.OIDCServletHttpFacade;
import org.keycloak.adapters.spi.AuthOutcome;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
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

public class AIMSFilter extends KeycloakOIDCFilter {

    private static Log logger = LogFactory.getLog(AIMSFilter.class);

    private boolean enabled;

    private ApplicationContext context;
    private ConnectorService connectorService;
    private SlingshotLoginController loginController;

    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";
    public static final String ALFRESCO_API_ENDPOINT_ID = "alfresco-api";
    public static final String SESSION_ATTRIBUTE_REFRESH_TOKEN = "refreshToken";

    /**
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {

        super.init(filterConfig);

        this.context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        AIMSConfigUtils configUtils = (AIMSConfigUtils) this.context.getBean("aimsConfigUtils");
        this.enabled = configUtils.isAIMSEnabled();
        this.connectorService = (ConnectorService) context.getBean("connector.service");
        this.loginController = (SlingshotLoginController) context.getBean("loginController");
    }

    /**
     *
     * @param sreq
     * @param sres
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) sreq;
        HttpServletResponse response = (HttpServletResponse) sres;
        HttpSession session = request.getSession();

        if (this.enabled && (!AuthenticationUtil.isAuthenticated(request)/* || this.isLoggedOutFromKeycloak(request, response)*/))
        {
            super.doFilter(sreq, sres, chain);

            RefreshableKeycloakSecurityContext context = (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
            if (context != null)
            {
                this.onSuccess(request, response, session, context);
            }
        }

        chain.doFilter(sreq, sres);
    }

    /*private boolean isLoggedOutFromKeycloak(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        OIDCServletHttpFacade facade = new OIDCServletHttpFacade(request, response);
        KeycloakDeployment deployment = this.deploymentContext.resolveDeployment(facade);

        OIDCServletHttpFacade facade = new OIDCServletHttpFacade(request, response);
        KeycloakDeployment deployment = this.deploymentContext.resolveDeployment(facade);
        if (deployment != null && deployment.isConfigured())
        {
            OIDCFilterSessionStore tokenStore = new OIDCFilterSessionStore(request, facade, 100000, deployment, this.idMapper);
            tokenStore.checkCurrentToken();
            FilterRequestAuthenticator authenticator = new FilterRequestAuthenticator(deployment, tokenStore, facade, request, 8443);
            AuthOutcome outcome = authenticator.authenticate();

            return outcome != AuthOutcome.AUTHENTICATED;
        }
        else
        {
            logger.error("Keycloak deployment not configured");
            throw new ServletException("Keycloak deployment not configured");
        }
    }*/

    /**
     *
     * @param request
     * @param response
     * @param session
     * @param context
     * @throws ServletException
     */
    private void onSuccess(HttpServletRequest request, HttpServletResponse response, HttpSession session, RefreshableKeycloakSecurityContext context) throws ServletException {

        String username = context.getIdToken().getPreferredUsername();
        String accessToken = context.getTokenString();

        try
        {
            // Perform a "silent" init - i.e. no user creation or remote connections ?
            RequestContextUtil.initRequestContext(this.context, request, true);

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
            this.loginController.beforeSuccess(request, response);
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
     * @return
     * @throws ConnectorServiceException
     * @throws JSONException
     */
    private String getAlfTicket(HttpSession session, String username, String accessToken) throws ConnectorServiceException, JSONException {
        Connector connector = this.connectorService.getConnector(ALFRESCO_API_ENDPOINT_ID, username, session);

        ConnectorContext c = new ConnectorContext(HttpMethod.GET, null, new HashMap<String, String>() {{
            put("Authorization", "Bearer " + accessToken);
        }});
        c.setContentType("application/json");
        Response r = connector.call("/-default-/public/authentication/versions/1/tickets/-me-", c);
        if (Status.STATUS_OK != r.getStatus().getCode())
        {
            throw new AlfrescoRuntimeException("Failed to read the returned content");
        }

        JSONObject json = new JSONObject(r.getText());
        String alfTicket = json.getJSONObject("entry").getString("id");

        return alfTicket;
    }
}
