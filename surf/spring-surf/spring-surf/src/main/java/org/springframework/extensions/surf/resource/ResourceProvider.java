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

import java.util.Map;

/**
 * Describes a provider of named Resource objects for the 
 * web framework.
 * 
 * A provider of resources should be able to do things like produce a
 * Resource object for a given resource id.
 * 
 * Some model objects, for example, can be thought of as containers of
 * properties.  They are also containers of "resources".  Resource
 * definitions are stored directly into the model object.
 * 
 * These resource containers have a resource provider interface on top
 * of them.
 * 
 * @author muzquiano
 */
public interface ResourceProvider
{
    /**
     * Looks up a resource with the given name
     * 
     * @param id
     * @return
     */
    public Resource getResource(String name);

    /**
     * Returns the set of all resources
     * 
     * @return
     */
    public Resource[] getResources();

    /**
     * Returns the map of resources
     * 
     * @return
     */
    public Map<String, Resource> getResourcesMap();

    /**
     * Adds/Creates a resource with the given name and resource id
     * 
     * @param name
     * @param resourceId
     * 
     * @return resource
     */
    public Resource addResource(String name, String resourceId);

    /**
     * Adds/Creates a resource with the given name, object id and
     * endpoint id
     * 
     * @param name
     * @param protocolId
     * @param endpointId
     * @param objectId
     * 
     * @return resource
     */
    public Resource addResource(String name, String protocolId, String endpointId, String objectId);

    /**
     * Updates a resource for the given name
     * 
     * @param name
     * @param resource
     */
    public void updateResource(String name, Resource resource);

    /**
     * Removes a resource with the given name
     * 
     * @param name
     */
    public void removeResource(String name);

}
