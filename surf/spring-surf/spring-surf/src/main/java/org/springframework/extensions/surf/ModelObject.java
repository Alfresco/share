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

package org.springframework.extensions.surf;

import java.io.Serializable;
import java.util.Map;

import org.dom4j.Document;

/**
 * @author muzquiano
 */
public interface ModelObject extends Serializable
{
    /** IMPORTANT - public fields starting with PROP_ are inspected by the ModelHelper */
    public static String PROP_ID = "id";
    public static String PROP_TITLE = "title";
    public static String PROP_TITLE_ID = "title-id";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_DESCRIPTION_ID = "description-id";
    
    /**
     * Returns the model object key instance
     * 
     * @return
     */
    public ModelPersisterInfo getKey();
    
    /**
     * Returns the id of the model object.
     * 
     * @return The id
     */
    public String getId();
    
    /**
     * Returns the type id of the model object.
     * 
     * @return The type id
     */
    public String getTypeId();
    
    /**
     * Returns the title property of the model object.
     * 
     * @return The title
     */
    public String getTitle();
    
    /**
     * Sets the title property of the model object
     * 
     * @param The new title
     */
    public void setTitle(String value);
    
    /**
     * Returns the title id property of the model object.
     * 
     * @return The title id
     */
    public String getTitleId();
    
    /**
     * Sets the title id property of the model object
     * 
     * @param The new title id
     */
    public void setTitleId(String value);
    
    /**
     * Returns the description property of the model object
     * 
     * @return The description
     */
    public String getDescription();
    
    /**
     * Sets the description property of the model object
     * 
     * @param The description
     */
    public void setDescription(String value);
    
    /**
     * Returns the description id property of the model object
     * 
     * @return The description id
     */
    public String getDescriptionId();
    
    /**
     * Sets the description id property of the model object
     * 
     * @param The description id
     */
    public void setDescriptionId(String value);
        
    /**
     * Indicates whether the object is currently persisted (saved)
     * or not.  A new object will have this flag set to false prior
     * to a save and true once the save operation has completed.
     * 
     * @return Whether the object is currently saved
     */
    public boolean isSaved();

    /**
     * Serializes the object to XML.  By default, this uses a 
     * pretty XML renderer so that the resulting XML is
     * human readable.
     * 
     * @return The XML string
     */
    public String toXML();

    // general property accessors
    public boolean getBooleanProperty(String propertyName);
    public String getProperty(String propertyName);
    public void setProperty(String propertyName, String propertyValue);
    public void removeProperty(String propertyName);
    public Map<String, Serializable> getProperties();

    // model properties
    public String getModelProperty(String propertyName);
    public void setModelProperty(String propertyName, String propertyValue);
    public void removeModelProperty(String propertyName);
    public Map<String, Serializable> getModelProperties();
    
    // custom properties
    public String getCustomProperty(String propertyName);
    public void setCustomProperty(String propertyName, String propertyValue);
    public void removeCustomProperty(String propertyName);
    public Map<String, Serializable> getCustomProperties();
    
    // persistence
    public String getStoragePath();
    public String getPersisterId();
    public long getModificationTime();
    public void touch();
    
    // allow xml retrieval via document
    public Document getDocument();    
}
