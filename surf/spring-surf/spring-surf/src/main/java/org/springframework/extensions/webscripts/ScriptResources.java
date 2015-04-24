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

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.resource.ResourceProvider;

/**
 * Provides an interface to resources as stored on a model object.
 * 
 * var objectId = resources.get("abc").objectId;
 * var endpointId = resources.get("abc").endpointId;
 * 
 * @author muzquiano
 */
public final class ScriptResources extends ScriptBase
{
    private static final long serialVersionUID = -3378946227712931201L;
    
    final private ModelObject modelObject;
        
    /**
     * Instantiates a new resources object
     * 
     * @param context the request context
     * @param modelObject the model object
     */
    public ScriptResources(RequestContext context, ModelObject modelObject)
    {
        super(context);

        this.modelObject = modelObject;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractScriptableObject#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            // construct and add in all of our model object properties
            this.properties = new ScriptableLinkedHashMap<String, Serializable>()
            {
                // For now, leave this as a read-only map
            };
            
            // populate the scriptable map
            String[] resourceNames = this.getNames();
            for (int i = 0; i < resourceNames.length; i++)
            {
                ScriptResource scriptResource = this.get(resourceNames[i]);
                this.properties.put(resourceNames[i], scriptResource);
            }
        }
        
        return this.properties;

    }
    
    
    // --------------------------------------------------------------
    // JavaScript Functions
        
    /**
     * Returns the model object
     * 
     * @return
     */
    public ModelObject getModelObject()
    {
        return this.modelObject;
    }
    
    public ScriptResource get(String name)
    {
        ScriptResource scriptResource = null;
        
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;
            
            // now add
            Resource resource = provider.getResource(name);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
            }
        }
        
        return scriptResource;        
    }
    
    public void remove(String name)
    {
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;                        
            provider.removeResource(name);
            
            // update properties
            this.properties.remove(name);
        }
    }
    
    public ScriptResource add(String name, String resourceId)
    {
        ScriptResource scriptResource = null;
        
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;                        
            
            // now add
            Resource resource = provider.addResource(name, resourceId);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
                this.properties.put(name, scriptResource);
            }
        }
        
        return scriptResource;
    }

    public ScriptResource add(String name, String protocolId, String endpointId, String objectId)
    {
        ScriptResource scriptResource = null;
        
        if (modelObject instanceof ResourceProvider)
        {
            ResourceProvider provider = (ResourceProvider) modelObject;                        
            
            // now add
            Resource resource = provider.addResource(name, protocolId, endpointId, objectId);
            if (resource != null)
            {
                scriptResource = new ScriptResource(context, resource);
                this.properties.put(name, scriptResource);
            }
        }
        
        return scriptResource;
    }
    
    public String[] getNames()
    {
        String[] names = new String[0];
        
        if (modelObject instanceof ResourceProvider)
        {        
            ResourceProvider provider = (ResourceProvider) modelObject;        
            Resource[] array = provider.getResources();
            if (array.length > 0)
            {
                names = new String[array.length];
                
                for (int i = 0; i < array.length; i++)
                {
                    names[i] = array[i].getName();
                }
            }
        }
        
        return names;
    }
}
