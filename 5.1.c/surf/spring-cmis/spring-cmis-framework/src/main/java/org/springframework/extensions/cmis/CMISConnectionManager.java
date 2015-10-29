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

/**
 * Manages all CMIS client connections.
 */
public interface CMISConnectionManager
{
    // --- connections ---

    /**
     * Creates a new default connection that is only visible to the current user.
     */
    CMISConnection createDefaultConnection(CMISServer server);
    
    /**
     * Creates a new connection that is only visible to the current user.
     */
    CMISConnection createUserConnection(CMISServer server, String connectionId);

    /**
     * Creates a new connection that is visible to all users.
     */
    CMISConnection createSharedConnection(CMISServer server, String connectionId);

    /**
     * Gets or creates a connection to the local server or a default server.
     */
    CMISConnection getConnection();

    /**
     * Returns a specific connection or <code>null</code> if the connection id
     * is unknown.
     */
    CMISConnection getConnection(String connectionId);

    /**
     * Returns all user connections.
     */
    List<CMISConnection> getUserConnections();

    /**
     * Returns all shared connections.
     */
    List<CMISConnection> getSharedConnections();

    // --- servers ---

    /**
     * Returns all configured server definitions.
     */
    List<CMISServer> getServerDefinitions();

    /**
     * Gets a server definitions by name.
     */
    CMISServer getServerDefinition(String serverName);

    /**
     * Creates a new server definition.
     */
    CMISServer createServerDefinition(String serverName, Map<String, String> parameters);

    /**
     * Creates a new server definition from a template.
     */
    CMISServer createServerDefinition(CMISServer server, String username, String password);

    /**
     * Creates a new server definition from a template.
     */
    CMISServer createServerDefinition(CMISServer server, String username, String password, String repositoryId);

    // --- repositories ---

    /**
     * Returns all repositories available at this server.
     */
    List<Repository> getRepositories(CMISServer server);
}
