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

import org.springframework.extensions.surf.exception.ResourceLoaderException;

/**
 * Interface for resource loader.
 * 
 * A resource loader is responsible for loading and creating a Resource
 * object for a given object id.
 * 
 * Example: The object id may be a nodeRef to a document in the
 * Alfresco content management system.  Or, it might be a URL to an
 * asset somewhere else in the world.
 * 
 * A resource loader knows how to retrieve the metadata and content for
 * the resource and manufacture a Resource object for use by the Web
 * Framework.
 * 
 * @author muzquiano
 */
public interface ResourceLoader
{
    /**
     * Returns the endpoint id for this resource loader
     * 
     * @return the endpoint id
     */
    public String getEndpointId();
    
    /**
     * Gets the protocol id.
     * 
     * @return the protocol id
     */
    public String getProtocolId();
    
    /**
     * Loads the resource with the given object id
     * 
     * @param objectId
     * @return
     * @throws ResourceLoaderException
     */
    public Resource load(String objectId)
            throws ResourceLoaderException;
}
