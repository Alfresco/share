/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.web.cmis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl;
import org.alfresco.web.site.SlingshotUserFactory;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.cmis.CMISConnection;
import org.springframework.extensions.cmis.CMISConnectionManagerImpl;
import org.springframework.extensions.cmis.CMISScriptParameterFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.AuthenticatingConnector;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.Credentials;

/**
 * CMIS Script Parameter Factory that sets the default connection to the
 * Alfresco back-end.
 * 
 * @author Florian Mueller
 * @since 4.0
 */
public class SlingshotCMISScriptParameterFactory extends CMISScriptParameterFactory
{
    private static final Log logger = LogFactory.getLog(SlingshotCMISScriptParameterFactory.class);
    private static final String CMIS_PATH = "/cmisatom";
    private static final String ALFRESCO_SERVICE_BASE_PATH = "/s";

    private ConnectorService connectorService;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }

    @Override
    public CMISConnection getConnection(CMISConnectionManagerImpl connectionManager)
    {
        lock.writeLock().lock();
        try
        {
            CMISConnection connection = super.getConnection(connectionManager);
            if (connection != null)
            {
                return connection;
            }

            if (ThreadLocalRequestContext.getRequestContext() == null)
            {
                return null;
            }

            RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            Credentials creds = rc.getCredentialVault().retrieve(SlingshotUserFactory.ALFRESCO_ENDPOINT_ID);

            Connector connector;
            try
            {
                connector = connectorService.getConnector(SlingshotUserFactory.ALFRESCO_ENDPOINT_ID);
                connector.setCredentials(creds);
            } catch (Exception e)
            {
                logger.info("Unable to get endpoint connector: " + e, e);
                return null;
            }

            String alfrescoEndpointUrl = connector.getEndpoint();
            if (alfrescoEndpointUrl.endsWith(ALFRESCO_SERVICE_BASE_PATH))
            {
                alfrescoEndpointUrl = alfrescoEndpointUrl.substring(0, alfrescoEndpointUrl.length()
                        - ALFRESCO_SERVICE_BASE_PATH.length());
            }

            String url = alfrescoEndpointUrl + CMIS_PATH;

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("name", "default-" + rc.getUserId());
            parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            parameters.put(SessionParameter.ATOMPUB_URL, url);
            parameters.put(SessionParameter.OBJECT_FACTORY_CLASS, AlfrescoObjectFactoryImpl.class.getName());

            String ticket = getTicket(connector);

            if (ticket != null)
            {
                parameters.put(SessionParameter.USER, "");
                parameters.put(SessionParameter.PASSWORD, ticket);
            } else if (creds != null)
            {
                parameters.put(SessionParameter.USER, (String) creds.getProperty(Credentials.CREDENTIAL_USERNAME));
                parameters.put(SessionParameter.PASSWORD, (String) creds.getProperty(Credentials.CREDENTIAL_PASSWORD));
            } else
            {
                return null;
            }

            return createDefaultConnection(connectionManager, createServerDefinition(parameters));
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    private String getTicket(Connector connector)
    {
        String ticket = (String) connector.getConnectorSession()
                .getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);

        if (ticket != null)
        {
            return ticket;
        }

        if (connector instanceof AuthenticatingConnector)
        {
            if (((AuthenticatingConnector) connector).handshake())
            {
                return (String) connector.getConnectorSession().getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
            }
        }

        return null;
    }
}
