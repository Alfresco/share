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
 * Inteface to be implemented by Surf beans that need to be aware of and or post cluster messages.
 * This is generally the case for cluster aware caching beans e.g. PathStoreObjectPersister or
 * beans that need to perform logic when data changes in the web cluster.
 * 
 * @author Kevin Roast
 */
public interface ClusterMessageAware
{
    /**
     * Called by a ClusterService implementation to set a ClusterService for this bean to use.
     * If this is never called (i.e. is null) then there is no ClusterService present and no
     * messages should be posted by the implementor of this interface.
     * 
     * @param service   ClusterService to use to post cluster message payloads
     */
    public void setClusterService(ClusterService service);
    
    /**
     * The unique message cluster Type ID that this bean deals with and posts using. This is part
     * of the contract of the ClusterMessageAware interface and must be implemented by the bean.
     * 
     * @return unique cluster message Type ID for this bean.
     */
    public String getClusterMessageType();
    
    /**
     * Called by a ClusterService implementation when a message for this bean has been sent to
     * this cluster node and is ready to be processed by the handler based on the bean message
     * type as supplied in getClusterMessageType().
     * 
     * @param payload   Payload from the message
     */
    public void onClusterMessage(Map<String, Serializable> payload);
}
