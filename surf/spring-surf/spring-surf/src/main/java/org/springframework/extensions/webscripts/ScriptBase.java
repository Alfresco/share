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

package org.springframework.extensions.webscripts;

import java.io.Serializable;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;

/**
 * Base class for all Web Framework Root-Scope and Script Model objects
 * 
 * This class can accept a binding to a RenderContext (for convenience).
 * The RenderContext object is the primary interface to the request
 * for the Java API.
 * 
 * @author muzquiano
 */
public abstract class ScriptBase implements Serializable
{
    final protected RequestContext context;
    protected ScriptableMap<String, Serializable> properties;

    /**
     * Instantiates a new web framework script base
     * 
     * @param context the context
     */
    public ScriptBase(RequestContext context)
    {
        // store a reference to the request context
        this.context = context;
    }
    
    public ScriptBase()
    {
        super();
        this.context = null;
    }

    /**
     * Gets the request context.
     * 
     * @return the request context
     */
    final public RequestContext getRequestContext()
    {
        return context;
    }
    
    /**
     * Gets the model object service
     * 
     * @return model object service
     */
    protected ModelObjectService getObjectService()
    {
        return context.getObjectService();
    }
    
    /**
     * Retrieves a model object from the underlying store and hands it back
     * wrapped as a ScriptModelObject.  If the model object cannot be found,
     * null will be returned.
     * 
     * @param id the id
     * 
     * @return the script model object
     */
    final public ScriptModelObject getObject(String objectTypeId, String objectId)
    {
        return ScriptHelper.getObject(getRequestContext(), objectTypeId, objectId);
    }        
    
    public ScriptableMap<String, Serializable> getProperties()
    {
        if (this.properties == null)
        {
            this.properties = buildProperties();
        }
        
        return this.properties;
    }
    
    final public WebFrameworkConfigElement getConfig()
    {
        return FrameworkBean.getConfig();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (getProperties() != null)
        {
            return getProperties().toString();
        }
        else
        {
            return this.context.toString();
        }
    }
    
    protected abstract ScriptableMap<String, Serializable> buildProperties();
}
