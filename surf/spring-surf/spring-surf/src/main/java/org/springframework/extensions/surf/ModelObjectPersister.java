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

import java.util.List;
import java.util.Map;

import org.springframework.extensions.surf.exception.ModelObjectPersisterException;

/**
 * @author muzquiano
 * @author kevinr
 */
public interface ModelObjectPersister
{
    /**
     * Initializes the persister by preloading the object cache
     * 
     * @param context the persistence context
     */
    public void init(ModelPersistenceContext context);
    
    /**
     * Resets the persister, clearing cache and starting anew.
     */
    public void reset();
    
    /**
     * Returns a unique id for this persister
     * 
     * If this persister is wrapped around a ClassPath store,
     * a LocalFileSystem store or a Repository store, this will return
     * the value provided getBasePath()
     * 
     * If this is wrapped around a RemoteStore, this will return the
     * AVM Store ID to which this persister is bound
     * 
     * @return
     */
    public String getId();
    
    /**
     * Gets an object from persisted storage by id
     * 
     * @param context
     * @param objectTypeId
     * @param objectId 
     * 
     * @return object instance
     */
    public ModelObject getObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Saves an object to persisted storage
     * 
     * @param context
     * @param object
     * 
     * @return whether the object was saved
     */
    public boolean saveObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException;
    
    /**
     * Saves a collection of objects to persisted storage
     * 
     * @param object
     */
    public boolean saveObjects(ModelPersistenceContext context, List<ModelObject> objects) 
        throws ModelObjectPersisterException;
    
    /**
     * Removes an object from persisted storage
     * 
     * @param context
     * @param object
     * 
     * @return whether the object was removed
     */
    public boolean removeObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException;
    
    /**
     * Removes an object from persisted storage
     * 
     * @param context
     * @param objectTypeId
     * @param objectId
     * 
     * @return whether the object was removed
     */
    public boolean removeObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Checks whether the given object is persisted
     * 
     * @param context
     * @param object
     * 
     * @return whether the object is persisted
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException;
    
    /**
     * Checks whether an object with the given path is persisted
     * 
     * @param context
     * @param objectTypeId
     * @param objectId
     * 
     * @return whether the object is persisted
     */
    public boolean hasObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Creates a new object
     * 
     * @param context
     * @param objectTypeId
     * @param objectId
     * 
     * @return the object
     */
    public ModelObject newObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Returns a map of all of the objects referenced by this persister.
     * <p>
     * In general, this is a very expensive call and should be avoided. Each object
     * descriptor referenced by the persister is loaded into the model object cache. 
     * 
     * @param context
     * @param objectTypeId
     * 
     * @return Map of object IDs to ModelObject instances
     * 
     * @throws ModelObjectException
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context, String objectTypeId)
        throws ModelObjectPersisterException;
    
    /**
     * Returns a map of all of the objects referenced by this persister filtered by
     * the given ID filter.
     * <p>
     * In general, this is an expensive call but less expensive than getAllObjects().
     * Each object descriptor referenced by the persister found using the filter is
     * loaded into the model object cache. 
     * 
     * @param context
     * @param objectTypeId
     * @param objectIdPattern
     * 
     * @return Map of object IDs to ModelObject instances
     * 
     * @throws ModelObjectException
     */
    public Map<String, ModelObject> getAllObjectsByFilter(ModelPersistenceContext context, String objectTypeId, String objectIdPattern)
        throws ModelObjectPersisterException;

    /**
     * Returns the timestamp of the given object in the underlying store
     * 
     * @param context
     * @param objectId
     * @return
     * @throws ModelObjectPersisterException
     */
    public long getTimestamp(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException;
    
    /**
     * Indicates whether this persisted is currently enabled.
     * Disabled persisters continue to work but do not modify data.
     * 
     * @return flag
     */
    public boolean isEnabled();
    
    /**
     * Indicates whether or not the persister uses a store that is read only.
     * @return
     */
    public boolean hasReadOnlyStore();
}
