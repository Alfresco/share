/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.extensions.cmis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.ScriptParameterFactory;
import org.springframework.extensions.webscripts.ScriptParameterFactoryRegistry;

/**
 * CMISConnectionManager factory.
 */
public class CMISScriptParameterFactory implements ScriptParameterFactory
{
    public static final String SERVER_NAME = "name";
    public static final String SERVER_DESCRIPTION = "description";
    public static final String DEFAULT_CONNECTION_ID = "default";
    public static final String SESSION_ATTRIBUTE = " org.springframework.extensions.cmis.usersessions";

    private ConfigService configService;
    private ScriptParameterFactoryRegistry scriptParameterFactoryRegistry;
    private final SessionFactory sessionFactory = SessionFactoryImpl.newInstance();

    private int sharedConnectionsCapacity = 100;
    private LinkedHashMap<String, CMISConnection> sharedConnections;
    private Map<String, CMISServer> servers;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // --- set up ---

    public void setScriptParameterFactoryRegistry(ScriptParameterFactoryRegistry scriptParameterFactoryRegistry)
    {
        this.scriptParameterFactoryRegistry = scriptParameterFactoryRegistry;
    }

    public void setSharedConnectionsCapacity(int sharedConnectionsCapacity)
    {
        this.sharedConnectionsCapacity = sharedConnectionsCapacity;
    }

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    public void init()
    {
        // create shared connection LRU cache
        sharedConnections = new LinkedHashMap<String, CMISConnection>(sharedConnectionsCapacity,
                (int) Math.ceil(sharedConnectionsCapacity / 0.75) + 1, true)
        {
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CMISConnection> eldest)
            {
                return size() > sharedConnectionsCapacity;
            }
        };

        // register
        scriptParameterFactoryRegistry.registerScriptParameterFactory(this);
    }

    // --- interface ---

    public Map<String, Object> getParameters(Runtime runtime)
    {
        CMISConnectionManagerImpl connectionManager = new CMISConnectionManagerImpl(this, runtime);
        return Collections.singletonMap("cmis", (Object) connectionManager);
    }

    // --- connection methods ----

    public CMISConnection createDefaultConnection(CMISConnectionManagerImpl connectionManager, CMISServer server)
    {
        CMISConnection connection;

        lock.writeLock().lock();
        try
        {
            Map<String, CMISConnection> userConnections = getUserConnectionsFromSession(connectionManager.getRuntime(),
                    true);
            if (userConnections.containsKey(DEFAULT_CONNECTION_ID))
            {
                throw new IllegalStateException("Connection id is already in use!");
            }

            if (server == null)
            {
                throw new IllegalStateException("Server definition must be set!");
            }

            if (!server.getParameters().containsKey(SessionParameter.USER))
            {
                if (ThreadLocalRequestContext.getRequestContext() != null
                        && ThreadLocalRequestContext.getRequestContext().getUserId() != null)
                {

                    Map<String, String> parameters = new HashMap<String, String>(server.getParameters());
                    parameters.put(SessionParameter.USER, ThreadLocalRequestContext.getRequestContext().getUserId());
                    server = createServerDefinition(server.getName(), parameters);
                }
            }

            connection = createConnection(connectionManager, server, DEFAULT_CONNECTION_ID, true, false);

            userConnections.put(DEFAULT_CONNECTION_ID, connection);
        } finally
        {
            lock.writeLock().unlock();
        }

        return connection;
    }

    public CMISConnection createUserConnection(CMISConnectionManagerImpl connectionManager, CMISServer server,
            String connectionId)
    {
        if (connectionId == null || connectionId.length() == 0)
        {
            throw new IllegalArgumentException("Invalid connection id!");
        }

        CMISConnection connection;

        lock.writeLock().lock();
        try
        {
            Map<String, CMISConnection> userConnections = getUserConnectionsFromSession(connectionManager.getRuntime(),
                    true);
            if (userConnections.containsKey(connectionId))
            {
                throw new IllegalStateException("Connection id is already in use!");
            }

            connection = createConnection(connectionManager, server, connectionId,
                    connectionId.equals(DEFAULT_CONNECTION_ID), false);

            userConnections.put(connectionId, connection);
        } finally
        {
            lock.writeLock().unlock();
        }

        return connection;
    }

    public CMISConnection createSharedConnection(CMISConnectionManagerImpl connectionManager, CMISServer server,
            String connectionId)
    {
        if (connectionId == null || connectionId.length() == 0 || DEFAULT_CONNECTION_ID.equals(connectionId))
        {
            throw new IllegalArgumentException("Invalid connection id!");
        }

        CMISConnection connection;

        lock.writeLock().lock();
        try
        {
            if (sharedConnections.containsKey(connectionId))
            {
                throw new IllegalStateException("Connection id is already in use!");
            }

            connection = createConnection(connectionManager, server, connectionId, false, true);

            sharedConnections.put(connection.getInternalId(), connection);
        } finally
        {
            lock.writeLock().unlock();
        }

        return connection;
    }

    private CMISConnection createConnection(CMISConnectionManagerImpl connectionManager, CMISServer server,
            String connectionId, boolean isDefault, boolean isShared)
    {
        Session session = createSession(server.getParameters());
        String username = server.getParameters().get(SessionParameter.USER);

        return new CMISConnectionImpl(connectionManager, connectionId, session, server, username, isDefault, isShared);
    }

    /**
     * Creates a remote connection.
     */
    public CMISConnection getConnection(CMISConnectionManagerImpl connectionManager)
    {
        return getConnection(connectionManager, DEFAULT_CONNECTION_ID);
    }

    public CMISConnection getConnection(CMISConnectionManagerImpl connectionManager, String connectionId)
    {
        lock.writeLock().lock();
        try
        {
            CMISConnection connection = sharedConnections.get(connectionId);
            if (connection != null)
            {
                return connection;
            }

            Map<String, CMISConnection> userConnections = getUserConnectionsFromSession(connectionManager.getRuntime(),
                    false);
            return userConnections == null ? null : userConnections.get(connectionId);
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    public List<CMISConnection> getUserConnections(CMISConnectionManagerImpl connectionManager)
    {
        lock.writeLock().lock();
        try
        {
            Map<String, CMISConnection> userConnections = getUserConnectionsFromSession(connectionManager.getRuntime(),
                    false);
            if (userConnections == null)
            {
                return Collections.emptyList();
            }

            List<CMISConnection> result = new ArrayList<CMISConnection>(userConnections.values());

            Collections.sort(result);
            return Collections.unmodifiableList(result);
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    public List<CMISConnection> getSharedConnections()
    {
        lock.writeLock().lock();
        try
        {
            List<CMISConnection> result = new ArrayList<CMISConnection>(sharedConnections.values());

            Collections.sort(result);
            return Collections.unmodifiableList(result);
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    public void removeConnection(CMISConnectionManagerImpl connectionManager, CMISConnection connection)
    {
        if (connection == null || connection.getInternalId() == null)
        {
            return;
        }

        lock.writeLock().lock();
        try
        {
            if (connection.isShared())
            {
                sharedConnections.remove(connection.getInternalId());
            } else
            {
                Map<String, CMISConnection> userConnections = getUserConnectionsFromSession(
                        connectionManager.getRuntime(), false);
                if (userConnections != null)
                {
                    userConnections.remove(connection.getInternalId());
                }
            }
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    protected Session createSession(Map<String, String> parameters)
    {
        if (parameters.containsKey(SessionParameter.REPOSITORY_ID))
        {
            return sessionFactory.createSession(new HashMap<String, String>(parameters));
        } else
        {
            return sessionFactory.getRepositories(new HashMap<String, String>(parameters)).get(0).createSession();
        }
    }

    // --- servers ---

    protected void loadServerDefintions()
    {
        lock.writeLock().lock();
        try
        {
            if (servers == null)
            {
                CMISServersConfigElement cmisServersConfig = (CMISServersConfigElement) configService.getConfig("CMIS")
                        .getConfigElement("cmis-servers");
                if (cmisServersConfig != null && cmisServersConfig.getServerDefinitions() != null)
                {
                    servers = cmisServersConfig.getServerDefinitions();
                } else
                {
                    servers = new HashMap<String, CMISServer>();
                }
            }
        } finally
        {
            lock.writeLock().unlock();
        }
    }

    public List<CMISServer> getServerDefinitions()
    {
        if (servers == null)
        {
            loadServerDefintions();
        }

        return Collections.unmodifiableList(new ArrayList<CMISServer>(servers.values()));
    }

    public CMISServer getServerDefinition(String serverName)
    {
        if (servers == null)
        {
            loadServerDefintions();
        }

        return servers.get(serverName);
    }

    public CMISServer createServerDefinition(String serverName, Map<String, String> parameters)
    {
        return new CMISServerImpl(serverName, null, parameters);
    }

    public CMISServer createServerDefinition(CMISServer server, String username, String password)
    {
        if (server == null)
        {
            throw new IllegalArgumentException("Server must be set!");
        }

        Map<String, String> parameters = new HashMap<String, String>(server.getParameters());
        parameters.put(SessionParameter.USER, username);
        parameters.put(SessionParameter.PASSWORD, password);

        return new CMISServerImpl(server.getName(), server.getDescription(), parameters);
    }

    public CMISServer createServerDefinition(CMISServer server, String username, String password, String repositoryId)
    {
        if (server == null)
        {
            throw new IllegalArgumentException("Server must be set!");
        }

        Map<String, String> parameters = new HashMap<String, String>(server.getParameters());
        parameters.put(SessionParameter.USER, username);
        parameters.put(SessionParameter.PASSWORD, password);
        parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);

        return new CMISServerImpl(server.getName(), server.getDescription(), parameters);
    }

    public static CMISServer createServerDefinition(Map<String, String> parameters)
    {
        if (parameters == null)
        {
            throw new IllegalArgumentException("Parameters must be set!");
        }

        String name = parameters.get(SERVER_NAME);
        parameters.remove(SERVER_NAME);

        String description = parameters.get(SERVER_DESCRIPTION);
        parameters.remove(SERVER_DESCRIPTION);

        if (name != null)
        {
            return new CMISServerImpl(name, description, parameters);
        }

        return null;
    }

    public List<Repository> getRepositories(CMISServer server)
    {
        if (server == null)
        {
            throw new IllegalArgumentException("Server must be set!");
        }

        return sessionFactory.getRepositories(new HashMap<String, String>(server.getParameters()));
    }

    public Map<String, CMISConnection> getUserConnectionsFromSession(Runtime runtime, boolean create)
    {
        lock.writeLock().lock();
        try
        {
            @SuppressWarnings("unchecked")
            Map<String, CMISConnection> userConnections = (Map<String, CMISConnection>) runtime.getSession().getValue(
                    SESSION_ATTRIBUTE);

            if (userConnections == null && create)
            {
                userConnections = new HashMap<String, CMISConnection>();
                runtime.getSession().setValue(SESSION_ATTRIBUTE, userConnections);
            }

            return userConnections;
        } finally
        {
            lock.writeLock().unlock();
        }
    }
}
