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

/**
 * Represents a CMIS connection.
 */
public interface CMISConnection extends Comparable<CMISConnection>
{
    /**
     * Gets the connection id.
     * 
     * @return connection id
     */
    String getId();

    /**
     * Gets the internal connection id.
     * 
     * @return connection id
     */
    String getInternalId();

    /**
     * Gets the OpenCMIS Session.
     * 
     * @return OpenCMIS session
     */
    Session getSession();

    /**
     * Gets the CMIS Server.
     * 
     * @return CMIS Server
     */
    CMISServer getServer();

    /**
     * Gets the user name.
     * 
     * @return user name
     */
    String getUserName();

    /**
     * Indicates if the connection is shared by multiple users.
     */
    boolean isShared();

    /**
     * Indicates if the connection is the default connection.
     */
    boolean isDefault();

    /**
     * Indicates is the repository supports queries.
     */
    boolean supportsQuery();

    /**
     * Releases the CMIS session and removes the connection from connection
     * manager.
     */
    void close();
}
