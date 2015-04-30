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

import java.io.IOException;
import java.io.Serializable;

/**
 * Describes a resource whose binary content retrieval and
 * referencing is resolved by the Web Framework.
 * 
 * The following are all valid resources:
 * 
 *    - a URL describing an image on a remote server
 *    - an asset in the web application at a relative path
 *    - a document in a CMIS repository at a specific location 
 * 
 * @author muzquiano
 */
public interface Resource extends Serializable
{
    /**
     * Gets the resource id.
     * 
     * @return the resource id
     */
    public String getResourceId();
    
    /**
     * Gets the protocol id
     * 
     * @return the protocol id
     */
    public String getProtocolId();    
    
    /**
     * Returns the object id of the resource
     * 
     * @return the object id
     */
    public String getObjectId();
    
    /**
     * Sets the object id.
     * 
     * @param objectId the new object id
     */
    public void setObjectId(String objectId);

    /**
     * Returns the endpoint of the resource
     * 
     * @return the endpoint
     */
    public String getEndpointId();

    /**
     * Sets the endpoint of the resource
     * 
     * @param endpoint
     */
    public void setEndpointId(String endpointId);
    
    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName();
    
    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name);
    
    /**
     * Gets the metadata for this resource.  If there is no metadata
     * for this resource, null will be returned.
     * 
     * @return the metadata or null
     */
    public ResourceContent getMetadata() throws IOException;
    
    /**
     * Gets the content for this resource.  If there is no content
     * for this resource, null will be returned.
     * 
     * @return the content or null.
     */
    public ResourceContent getContent() throws IOException;
    
    /**
     * Gets the metadata url.  If there is no metadata url for this
     * resource, null will be returned.
     * 
     * @return the metadata url or null.
     */
    public String getMetadataURL();

    /**
     * Gets the content url.  If there is no content url for this
     * resource, null will be returned.
     * 
     * @return the content url
     */
    public String getContentURL();
    
    /**
     * Gets the object type id.
     * 
     * @return the object type id
     */
    public String getObjectTypeId();
    
    /**
     * Checks if the resource is a container.
     * 
     * @return true, if is container
     */
    public boolean isContainer();    
}
