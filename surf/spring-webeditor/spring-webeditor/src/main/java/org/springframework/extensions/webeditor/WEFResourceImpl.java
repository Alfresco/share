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

import java.util.Collections;
import java.util.List;

/**
 * Implementation of a WEFResource, representing a resource that can be 
 * referenced in a WEF based application. 
 *
 * @author Gavin Cornwell
 */
public class WEFResourceImpl implements WEFResource
{
    protected String name;
    protected String path;
    protected String type;
    protected String description;
    protected String variableName;
    protected String userAgent;
    protected String container;
    protected List<WEFResource> dependencies;

    /*
     * @see org.alfresco.wef.WEFResource#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the resource name.
     * 
     * @param name The name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /*
     * @see org.alfresco.wef.WEFResource#getPath()
     */
    public String getPath()
    {
        return this.path;
    }
    
    /**
     * Sets the resource path.
     * 
     * @param path The path
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /*
     * @see org.alfresco.wef.WEFResource#getType()
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Sets the resource type.
     * 
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    /*
     * @see org.alfresco.wef.WEFResource#getDescription()
     */
    public String getDescription()
    {
        return this.description;
    }
    
    /**
     * Sets the resource description.
     * 
     * @param description The description
     */
    public void setDescription(String description)
    {
        this.description = description;
    } 
    
    /*
     * @see org.alfresco.wef.WEFResource#getVariableName()
     */
    public String getVariableName()
    {
        return this.variableName;
    }

    /**
     * Sets the resource variable name.
     * 
     * @param variableName The variable name
     */
    public void setVariableName(String variableName)
    {
        this.variableName = variableName;
    }

    /*
     * @see org.springframework.extensions.webeditor.WEFResource#getUserAgent()
     */
    public String getUserAgent()
    {
        return this.userAgent;
    }

    /**
     * Sets the user agent.
     * 
     * @param userAgent The user agent
     */
    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    /*
     * @see org.alfresco.wef.WEFResource#getDependencies()
     */
    public List<WEFResource> getDependencies()
    {
        if (this.dependencies != null)
        {
            return this.dependencies;
        }
        else
        {
            return Collections.emptyList();
        }
    }
    
    /**
     * Sets the resource's dependencies
     * 
     * @param dependencies List of dependencies
     */
    public void setDependencies(List<WEFResource> dependencies)
    {
        this.dependencies = dependencies;
    }
    
    /*
     * @see org.alfresco.wef.WEFResource#getContainer()
     */
    public String getContainer()
    {
        return this.container;
    }
    
    /**
     * Sets the resource container.
     * 
     * @param container The container
     */
    public void setContainer(String container)
    {
        this.container = container;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(super.toString());
        
        builder.append(" (name: ");
        builder.append(this.name);
        builder.append(", path: ");
        builder.append(this.path);
        builder.append(", type: ");
        builder.append(this.type);
        builder.append(", description: ");
        builder.append(this.description);
        builder.append(", variableName: ");
        builder.append(this.variableName);
        builder.append(", userAgent: ");
        builder.append(this.userAgent);
        builder.append(", container: ");
        builder.append(this.container);
        builder.append(", dependencies: ");
        builder.append(getDependencies().size());
        builder.append(")");
        
        return builder.toString();
    }
}
