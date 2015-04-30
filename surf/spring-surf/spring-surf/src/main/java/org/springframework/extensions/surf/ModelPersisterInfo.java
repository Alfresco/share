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

/**
 * The ModelPersisterInfo structure class holds the values that represent the binding
 * of a model object instance to a persistance store and it's path in that store.
 * 
 * @author muzquiano
 * @author David Draper
 */
public final class ModelPersisterInfo implements Serializable
{
    private static final long serialVersionUID = -2684766253715296643L;

    private String persisterId;
    private String storagePath;
    
    /**
     * <p>This contains the details of the file where the <code>ModelObject</code> is defined. This is different
     * from the <code>storagePath</code> because when a <code>ModelObject</code> is defined within another (e.g. 
     * when a <code>Component</code> is defined inside a <code>Page</code>) then a storage path is generated for 
     * internal use. The sourcePath is useful for debugging purposes when we want to determine not where the 
     * <code>ModelObject</code> is stored but where it is defined.</p>
     */
    private String sourcePath = null;
    
    /**
     * <p>This should be used to set the location where the <code>ModelObject</code> is originally defined.</p>
     * @param sourcePath The path to the definition of he <code>ModelObject</code>
     */
    public void setSourcePath(String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    /**
     * <p>Retrieves the path to the original definition of the <code>ModelObject</code>.
     * @return The path to the original definition of the <code>ModelObject</code>.
     */
    public String getSourcePath()
    {
        return sourcePath;
    }

    private boolean saved;

    /**
     * Instantiates a new model object key.
     * 
     * @param persisterId the persister id
     * @param storagePath the storage path
     */
    public ModelPersisterInfo(String persisterId, String storagePath, boolean saved)
    {
        this.persisterId = persisterId;
        this.storagePath = storagePath;
        this.saved = saved;
    }
    
    /**
     * Instantiates a new model object key.
     * 
     * @param persisterId the persister id
     * @param storagePath the storage path
     */
    public ModelPersisterInfo(String persisterId, String storagePath, String sourcePath, boolean saved)
    {
        this.persisterId = persisterId;
        this.storagePath = storagePath;
        this.saved = saved;
        this.sourcePath = sourcePath;
    }
    
    /**
     * Gets the persister id.
     * 
     * @return the persister id
     */
    public String getPersisterId()
    {
        return this.persisterId;
    }
    
    /**
     * Sets the storage path
     * 
     * @param storagePath
     */
    public void setStoragePath(String storagePath)
    {
        this.storagePath = storagePath;
    }
    
    /**
     * Gets the storage path.
     * 
     * @return the storage path
     */
    public String getStoragePath()
    {
        return this.storagePath;
    }
    
    /**
     * Returns whether the object is currently saved or not
     * 
     * @return whether saved
     */
    public boolean isSaved()
    {
        return this.saved;
    }
    
    /**
     * Marks the saved flag on the key
     * 
     * @param saved
     */
    public void setSaved(boolean saved)
    {
        this.saved = saved;
    }
}
