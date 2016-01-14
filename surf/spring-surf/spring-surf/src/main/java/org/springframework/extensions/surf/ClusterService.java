/*
 * Copyright (C) 2005-2016 Alfresco Software Limited.
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
package org.springframework.extensions.surf;

import java.io.Serializable;
import java.util.Map;

/**
 * Generic Cluster Service interface that will be implemented by a concrete ClusterService
 * class such as Hazelcast or similar.
 * <p>
 * Surf itself is insulated from the specific cluster implementation and is only aware of how
 * to receive and post simple Map based message payloads of Serializable objects.
 * 
 * @author Kevin Roast
 */
public interface ClusterService
{
    /**
     * Publish a cluster message from this bean to be sent to other nodes.
     * 
     * @param messageType   The unique cluster Type ID to indicate the handler bean for this mesage.
     * @param payload       Message payload
     */
    public void publishClusterMessage(String messageType, Map<String, Serializable> payload);
}
