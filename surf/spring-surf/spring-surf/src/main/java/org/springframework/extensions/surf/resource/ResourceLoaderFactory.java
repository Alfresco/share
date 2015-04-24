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

package org.springframework.extensions.surf.resource;

/**
 * Interface for web framework resource loader factories.
 * 
 * @author muzquiano
 */
public interface ResourceLoaderFactory
{
    /**
     * Returns a resource loader that is bound to the given
     * endpoint.
     * 
     * @param protocolId the protocol id
     * @param endpointId the endpoint id
     * 
     * @return the resource loader
     */
    public ResourceLoader getResourceLoader(String protocolId, String endpointId);
    
    /**
     * Returns the order of preference for this resource loader
     * 
     * @return order
     */
    public int getOrder();
    
    /**
     * Identifies whether this factory can produce resource
     * loaders that can handle the given protocol
     * 
     * @param protocolId
     * 
     * @return whether this factory can produce for the given protocol 
     */
    public boolean canHandle(String protocolId);    
}
