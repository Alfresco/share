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
import java.util.Iterator;

import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.ModelHelper;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.types.AdvancedComponent;
import org.springframework.extensions.surf.util.ParameterCheck;

/**
 * Provides a write-able model object wrapper to the script engine.
 *
 * The properties of this object are writeable which means that the
 * developer has the option to use either the properties array or
 * explicit methods.
 * 
 * The following commands are equivalent:
 * 
 * myObject.properties.title = "abc";
 * myObject.properties["title"] = "abc";
 * myObject.setProperty("title", "abc");
 * 
 * Note: The index on the properties array is not supported.  Thus, a command
 * such as this:
 * 
 * myObject.properties[0] = "abc";
 * 
 * will no-op and do nothing.
 * 
 * The following is available for working with resources:
 * 
 * var resources = myObject.resources;
 * 
 * @author muzquiano
 */
public final class ScriptModelObject extends ScriptBase
{
    // unmodifiable "system" properties
    private static final long serialVersionUID = -3378946227712939601L;
    private final ModelObject modelObject;
    private final ScriptResources resources;
    
    /**
     * Instantiates a new script model object.
     * 
     * @param context the request context
     * @param modelObject the model object
     */
    public ScriptModelObject(RequestContext context, ModelObject modelObject)
    {
        super(context);
        
        // store a reference to the model object
        this.modelObject = modelObject;
        
        // initialize the resources container
        this.resources = new ScriptResources(context, this.modelObject);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractScriptableObject#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            // construct and add in all of our model object properties
            this.properties = new ScriptableLinkedHashMap<String, Serializable>(modelObject.getProperties())
            {
                // trap this method so that we can adjust the model object
                @Override
                public void put(String name, Scriptable start, Object value)
                {
                    Serializable unwrapped = (Serializable)ScriptValueConverter.unwrapValue(value);
                    put(name, unwrapped);
                    
                    // update the model object
                    modelObject.setProperty(name, unwrapped != null ? unwrapped.toString() : null);
                }

                // do not allow
                @Override
                public void put(int index, Scriptable start, Object value)
                {
                }

                // trap this method so that we can adjust the model object
                @Override
                public void delete(String name)
                {
                    remove(name);
                    
                    // update the model object
                    modelObject.removeProperty(name);
                }

                // do not allow
                @Override
                public void delete(int index)
                {
                }
            };
        }
        
        return this.properties;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return modelObject.getId();
    }
    
    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    public void setId(String id)
    {
        ModelHelper.resetId(modelObject, id);        
    }
    
    /**
     * Gets the type id for the underlying model object
     * 
     * @return the type id
     */
    public String getTypeId()
    {
        return this.modelObject.getTypeId();
    }    
    
    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle()
    {
        return modelObject.getTitle();
    }
    
    /**
     * Sets the title.
     * 
     * @param value the new title
     */
    public void setTitle(Serializable value)
    {
        if (value != null)
        {
            getProperties().put("title", value);
        }
        else
        {
            getProperties().delete("title");
        }
    }
    
    /**
     * Gets the title id.
     * 
     * @return the title id
     */
    public String getTitleId()
    {
        return modelObject.getTitleId();
    }
    
    /**
     * Sets the title id.
     * 
     * @param value the new title id
     */
    public void setTitleId(Serializable value)
    {
        if (value != null)
        {
            getProperties().put("titleId", value);
        }
        else
        {
            getProperties().delete("titleId");
        }
    }
    
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return modelObject.getDescription();
    }
    
    /**
     * Gets the description id.
     * 
     * @return the description id
     */
    public String getDescriptionId()
    {
        return modelObject.getDescriptionId();
    }
    
    /**
     * Sets the description id.
     * 
     * @param value the new description id
     */
    public void setDescriptionId(Serializable value)
    {
        if (value != null)
        {
            getProperties().put("descriptionId", value);
        }
        else
        {
            getProperties().delete("descriptionId");
        }
    }
    
    /**
     * Sets the description.
     * 
     * @param value the new description
     */
    public void setDescription(Serializable value)
    {
        if (value != null)
        {
            getProperties().put("description", value);
        }
        else
        {
            getProperties().delete("description");
        }
    }
    
    /**
     * Gets the timestamp.
     * 
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return modelObject.getModificationTime();
    }
    
    /**
     * Gets the persister id.
     * 
     * @return the persister id
     */
    public String getPersisterId()
    {
        return modelObject.getPersisterId();
    }
    
    /**
     * Gets the storage path.
     * 
     * @return the storage path
     */
    public String getStoragePath()
    {
        return modelObject.getStoragePath();
    }
    
    public ScriptResources getResources()
    {
        return this.resources;        
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Functions
    
    /**
     * Persist the object and all modified properties.
     * @throws ModelObjectPersisterException 
     */
    public void save(boolean persist) throws ModelObjectPersisterException
    {
        // retrieve values from our properties array
        Iterator it = getProperties().keySet().iterator();
        while (it.hasNext())
        {
            String propertyName = (String) it.next();
            String propertyValue = (String) getProperties().get(propertyName);
            modelObject.setProperty(propertyName, propertyValue);
        }
        
        if (persist)
        {
            getObjectService().saveObject(modelObject);
        }
        
        // This is a massive hack, but under the circumstances there are no alternatives. This
        // is required because when "old school" Component configuration is converted into an
        // AdvancedComponent a default SubComponent is generated with the URI of the Component, 
        // however... if the Component URI is changed the default SubComponent URI will not.
        // By resetting the SubComponent cache after each save we can guarantee that the SubComponent
        // will always have the correct URI.
        if (this.modelObject instanceof AdvancedComponent)
        {
            ((AdvancedComponent) this.modelObject).setSubComponents(null);
        }
    }
    
    /**
     * Persist the object and all modified properties.
     * @throws ModelObjectPersisterException 
     */
    public void save() throws ModelObjectPersisterException
    {
        save(true);
    }

    /**
     * Removes the object
     */
    public void remove()
    {
        getObjectService().removeObject(modelObject);
    }
    
    /**
     * Deletes the object
     */
    public void delete()
    {
        remove();
    }

    /**
     * To xml.
     * 
     * @return the string
     */
    public String toXML()
    {
        return modelObject.toXML();
    }
    
    /**
     * Touches the object.
     * This set its timestamp to the current time.
     */
    public void touch()
    {
        modelObject.touch();
        
        // this forces all of the properties to reload
        this.properties = null;
    }
    
    /**
     * Gets the boolean property.
     * 
     * @param propertyName the property name
     * 
     * @return the boolean property
     */
    public boolean getBooleanProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        return modelObject.getBooleanProperty(propertyName);
    }

    /**
     * Gets the property.
     * 
     * @param propertyName the property name
     * 
     * @return the property
     */
    public String getProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        return (String)getProperties().get(propertyName);
    }

    /**
     * Sets the property.
     * 
     * @param propertyName the property name
     * @param propertyValue the property value
     */
    public void setProperty(String propertyName, String propertyValue)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        ParameterCheck.mandatory("propertyValue", propertyValue);
        getProperties().put(propertyName, propertyValue);
    }

    /**
     * Removes the property.
     * 
     * @param propertyName the property name
     */
    public void removeProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        getProperties().remove(propertyName);
    }    
    
    /**
     * Returns the model object
     * 
     * @return
     */
    public ModelObject getModelObject()
    {
        return this.modelObject;
    }
    
    /**
     * Creates a clone of this model object
     */
    public ScriptModelObject clone()
    {
        String objectTypeId = this.getModelObject().getTypeId();
        String objectId = this.getModelObject().getId();
        
        ModelObject obj = getObjectService().clone(objectTypeId, objectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }

    /**
     * Creates a clone of this model object
     * The provided is to set as the new id of the object
     */
    public ScriptModelObject clone(String newObjectId)
    {
        ParameterCheck.mandatory("newObjectId", newObjectId);

        String objectTypeId = this.getModelObject().getTypeId();
        String objectId = this.getModelObject().getId();
        
        ModelObject obj = getObjectService().clone(objectTypeId, objectId, newObjectId);
        return ScriptHelper.toScriptModelObject(context, obj);
    }
}
