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

package org.springframework.extensions.webeditor;

import java.util.List;

/**
 * Interface definition of a Web Editor Framework resource
 *
 * @author Gavin Cornwell
 */
public interface WEFResource
{
    /**
     * Returns the resource name.
     * 
     * @return The name
     */
    public String getName();
    
    /**
     * Returns the resource description.
     * 
     * @return The description
     */
    public String getDescription();
    
    /**
     * Returns the resource type.
     * 
     * @return The type
     */
    public String getType();
    
    /**
     * Returns the resource path.
     * 
     * @return The path
     */
    public String getPath();
    
    /**
     * Returns the resource varaible name
     * 
     * @return The variable name
     */
    public String getVariableName();
    
    /**
     * Returns the user agent
     * 
     * @return The user agent
     */
    public String getUserAgent();
    
    /**
     * Returns the resource container.
     * 
     * @return The container
     */
    public String getContainer();
    
    /**
     * Returns a list of dependencies this resouce has.
     * 
     * @return The resource's dependencies
     */
    public List<WEFResource> getDependencies();
}
