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

import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.springframework.extensions.webscripts.Runtime;

public class CMISConnectionManagerImpl extends CMISHelper implements CMISConnectionManager
{
    private CMISScriptParameterFactory factory;
    private Runtime runtime;

    public CMISConnectionManagerImpl(CMISScriptParameterFactory factory, Runtime runtime)
    {
        this.factory = factory;
        this.runtime = runtime;
    }

    public Runtime getRuntime()
    {
        return runtime;
    }

    public CMISConnection createDefaultConnection(CMISServer server)
    {
        return factory.createDefaultConnection(this, server);
    }

    public CMISConnection createUserConnection(CMISServer server, String connectionId)
    {
        return factory.createUserConnection(this, server, connectionId);
    }

    public CMISConnection createSharedConnection(CMISServer server, String connectionId)
    {
        return factory.createSharedConnection(this, server, connectionId);
    }

    public CMISConnection getConnection()
    {
        return factory.getConnection(this);
    }

    public CMISConnection getConnection(String connectionId)
    {
        return factory.getConnection(this, connectionId);
    }

    public List<CMISConnection> getUserConnections()
    {
        return factory.getUserConnections(this);
    }

    public List<CMISConnection> getSharedConnections()
    {
        return factory.getSharedConnections();
    }

    public List<CMISServer> getServerDefinitions()
    {
        return factory.getServerDefinitions();
    }

    public CMISServer getServerDefinition(String serverName)
    {
        return factory.getServerDefinition(serverName);
    }

    public CMISServer createServerDefinition(String serverName, Map<String, String> parameters)
    {
        return factory.createServerDefinition(serverName, parameters);
    }

    public CMISServer createServerDefinition(CMISServer server, String username, String password)
    {
        return factory.createServerDefinition(server, username, password);
    }

    public CMISServer createServerDefinition(CMISServer server, String username, String password, String repositoryId)
    {
        return factory.createServerDefinition(server, username, password, repositoryId);
    }

    public List<Repository> getRepositories(CMISServer server)
    {
        return factory.getRepositories(server);
    }

    public void removeConnection(CMISConnection connection)
    {
        factory.removeConnection(this, connection);
    }
}
