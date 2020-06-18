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
import org.springframework.extensions.surf.*;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.support.ServletRequestContextFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
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
        AIMSConfig config = (AIMSConfig) this.context.getBean("aims.config");
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
        HttpServletResponse response = (HttpServletResponse) sres;
        HttpSession session = request.getSession();

        if (this.enabled && (!AuthenticationUtil.isAuthenticated(request) || this.isLoggedOutFromKeycloak(session)))
        {
            super.doFilter(sreq, sres, chain);

            RefreshableKeycloakSecurityContext context =
                (RefreshableKeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

            if (context != null)
            {
                this.onSuccess(request, response, session, context);
            }
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

        String username = context.getToken().getPreferredUsername();
        String accessToken = context.getTokenString();

        try
        {
            // Init request context for further use on getting user
            this.initRequestContext(request, response);

            // Get the alfTicket from repo, using the JWT token from Keycloak
            String alfTicket = this.getAlfTicket(session, username, accessToken);
            if (alfTicket != null)
            {
                // Ensure User ID is in session so the web-framework knows we have logged in
                session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);

                // Set the alfTicket into connector's session for further use on repo calls (will be set on the RemoteClient)
                Connector connector = this.connectorService.getConnector(ALFRESCO_ENDPOINT_ID, username, session);
                connector.getConnectorSession().setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, alfTicket);

                // Set credential username for further use on repo
                // if there is no pass, as in our case, there will be a "X-Alfresco-Remote-User" header set using this value
                CredentialVault vault = FrameworkUtil.getCredentialVault(session, username);
                Credentials credentials = vault.newCredentials(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
                credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                vault.store(credentials);

                // Initialise the user metadata object used by some web scripts
                this.initUser(request);

                // Inform the Slingshot login controller of a successful login attempt as further processing may be required ?
                this.loginController.beforeSuccess(request, response);
            }
            else
            {
                logger.error("Could not get an alfTicket from Repository.");
            }
        }
        catch (Exception e)
        {
            throw new AlfrescoRuntimeException("Failed to complete AIMS authentication process.", e);
        }
    }

    /**
     * Initialise the request context and request attributes for further use by some web scripts
     * that require authentication
     *
     * @param request
     * @throws RequestContextException
     */
    private void initRequestContext(HttpServletRequest request, HttpServletResponse response) throws RequestContextException
    {
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        if (context == null)
        {
            ServletRequestContextFactory factory =
                (ServletRequestContextFactory) this.context.getBean("webframework.factory.requestcontext.servlet");
            context = factory.newInstance(new ServletWebRequest(request));
            request.setAttribute(RequestContext.ATTR_REQUEST_CONTEXT, context);
        }

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        ServletUtil.setRequest(request);
    }

    /**
     * Initialise the user meta data object and set it into the session and request context (_alf_USER_OBJECT)
     * The user meta data object is used by web scripts that require authentication
     *
     * This is present in the filter for avoiding Basic Authentication prompt for those web scripts,
     * when user access them and is logged out (see https://issues.alfresco.com/jira/browse/APPS-117)
     *
     * @param request
     * @throws UserFactoryException
     */
    private void initUser(HttpServletRequest request) throws UserFactoryException
    {
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        if (context != null && context.getUser() == null)
        {
            String userEndpointId = (String) context.getAttribute(RequestContext.USER_ENDPOINT);
            UserFactory userFactory = context.getServiceRegistry().getUserFactory();
            User user = userFactory.initialiseUser(context, request, userEndpointId);
            context.setUser(user);
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
            logger.info("Retrieving the Alfresco Ticket from Repository.");
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
