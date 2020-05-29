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
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.keycloak.adapters.servlet.OIDCFilterSessionStore;
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
import java.util.Collections;

public class AIMSFilter extends KeycloakOIDCFilter
{
    private static final Log logger = LogFactory.getLog(AIMSFilter.class);

    private ApplicationContext context;
    private ConnectorService connectorService;
    private SlingshotLoginController loginController;

    private boolean enabled = false;

    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";
    public static final String ALFRESCO_API_ENDPOINT_ID = "alfresco-api";

    /**
     * Initialize the filter
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("Initializing the AIMS filter.");
        }

        super.init(filterConfig);

        this.context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        AIMSConfig config = (AIMSConfig) this.context.getBean("aimsConfig");
        this.enabled = config.isEnabled();
        this.connectorService = (ConnectorService) context.getBean("connector.service");
        this.loginController = (SlingshotLoginController) context.getBean("loginController");

        // Check if there are valid values within keycloak.json config file
        if (this.enabled)
        {
            KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(config.getAdapterConfig());
            if (!deployment.isConfigured() || deployment.getRealm().isEmpty() ||
                deployment.getResourceName().isEmpty() || deployment.getAuthServerBaseUrl().isEmpty())
            {
                throw new AlfrescoRuntimeException("AIMS is not configured properly; realm, resource and auth-server-url should not be empty.");
            }

            // Update filter's deployment
            this.deploymentContext.updateDeployment(config.getAdapterConfig());
        }

        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("AIMS filter initialized.");
        }
    }

    /**
     * @param sreq Servlet Request
     * @param sres Servlet Response
     * @param chain Filter Chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) sreq;
        HttpSession session = request.getSession();

        if (this.enabled && (!AuthenticationUtil.isAuthenticated(request) || this.isLoggedOutFromKeycloak(session)))
        {
            final FilterChain downstreamFilter = chain;

            FilterChain next = new FilterChain()
            {
                public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
                {
                    RefreshableKeycloakSecurityContext context =
                        (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

                    if ( (context == null) || (context.getIdToken() == null) )
                    {
                        // this should not happen
                        throw new RuntimeException("Missing SecurityContext or token on authenticated Request");
                    }

                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    HttpServletResponse httpResponse = (HttpServletResponse) response;

                    onSuccess(httpRequest, httpResponse, session, context);

                    downstreamFilter.doFilter(httpRequest, httpResponse);
                }
            };

            super.doFilter(sreq, sres, next);
        }
        else
        {
            chain.doFilter(sreq, sres);
        }
    }

    /**
     * Checks if the user is logged out from Keycloak
     * Helps us when someone logs out from another application, but is still logged in on Share
     *
     * @param session
     * @return
     */
    private boolean isLoggedOutFromKeycloak(HttpSession session)
    {
        OIDCFilterSessionStore.SerializableKeycloakAccount account =
            (OIDCFilterSessionStore.SerializableKeycloakAccount) session.getAttribute(KeycloakAccount.class.getName());

        if (account != null)
        {
            RefreshableKeycloakSecurityContext context = account.getKeycloakSecurityContext();

            if (context != null)
            {
                return !context.refreshExpiredToken(false);
            }

            return true;
        }

        return true;
    }

    /**
     *
     * @param request HTTP Servlet Request
     * @param response HTTP Servlet Response
     * @param session HTTP Session
     * @param context Refreshable Keycloak Security Context
     */
    private void onSuccess(HttpServletRequest request, HttpServletResponse response, HttpSession session, RefreshableKeycloakSecurityContext context)
    {
        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("Completing the AIMS authentication.");
        }

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
            if (alfTicket != null)
            {
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
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Failed to complete AIMS authentication process", e);
        }
    }

    /**
     * Get an alfTicket using the JWT token from Keycloak
     *
     * @param session HTTP Session
     * @param username username
     * @param accessToken access token
     * @return The alfTicket
     * @throws ConnectorServiceException
     */
    private String getAlfTicket(HttpSession session, String username, String accessToken) throws ConnectorServiceException
    {
        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("retrieving the Alfresco Ticket from Repository.");
        }

        String alfTicket = null;
        Connector connector = this.connectorService.getConnector(ALFRESCO_API_ENDPOINT_ID, username, session);
        ConnectorContext c = new ConnectorContext(HttpMethod.GET, null, Collections.singletonMap("Authorization", "Bearer " + accessToken));
        c.setContentType("application/json");
        Response r = connector.call("/-default-/public/authentication/versions/1/tickets/-me-", c);

        if (Status.STATUS_OK != r.getStatus().getCode())
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Failed to retrieve Alfresco Ticket from Repository.");
            }
        }
        else
        {
            // Parse the alfTicket
            JSONObject json = new JSONObject(r.getText());
            try
            {
                alfTicket = json.getJSONObject("entry").getString("id");
            }
            catch (JSONException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Failed to parse Alfresco Ticket from Repository response.");
                }
            }
        }

        return alfTicket;
    }
}
