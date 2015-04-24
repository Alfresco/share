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

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;

public class CMISConnectionImpl implements CMISConnection
{
    private CMISConnectionManagerImpl connectionManager;

    private String id;
    private String internalId;
    private Session session;
    private CMISServer server;
    private String username;
    private boolean isDefault;
    private boolean isShared;

    public CMISConnectionImpl(CMISConnectionManagerImpl connectionManager, String id, Session session,
            CMISServer server, String username, boolean isDefault, boolean isShared)
    {
        if (connectionManager == null)
        {
            throw new IllegalArgumentException("Connection Manager must be set!");
        }

        if (id == null)
        {
            throw new IllegalArgumentException("Id must be set!");
        }

        if (session == null)
        {
            throw new IllegalArgumentException("Session must be set!");
        }

        this.connectionManager = connectionManager;
        this.internalId = id;
        this.id = id;
        this.session = session;
        this.server = server;
        this.username = username;
        this.isDefault = isDefault;
        this.isShared = isShared;
    }

    public String getId()
    {
        return id;
    }

    public String getInternalId()
    {
        return internalId;
    }

    public Session getSession()
    {
        return session;
    }

    public CMISServer getServer()
    {
        return server;
    }

    public String getUserName()
    {
        return username;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public boolean isShared()
    {
        return isShared;
    }

    public boolean supportsQuery()
    {
        if (session == null)
        {
            return false;
        }

        if (session.getRepositoryInfo().getCapabilities() == null)
        {
            return true;
        }

        return session.getRepositoryInfo().getCapabilities().getQueryCapability() != CapabilityQuery.NONE;
    }

    public void close()
    {
        connectionManager.removeConnection(this);
        session = null;
    }

    public int compareTo(CMISConnection conn)
    {
        return id.compareTo(conn.getId());
    }
}
