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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.persister.CachedPersister;
import org.springframework.extensions.surf.persister.PersisterService;
import org.springframework.extensions.webscripts.PreviewContext;

/**
 * Object Persistence Service
 * <p>
 * Generalized service for the loading, retrieving and persisting of model objects
 * to one or more back-end persisters.
 * <p>
 * This service works at the level of objects - it does not concern itself with path-based
 * representations of the objects (that is the role of the persisters).
 * <p>
 * Objects that are loaded from a persisters retain knowledge of their source location so
 * that future writes occur against the same persister.
 * <p>
 * New objects are created using a specified persister or they will default to using
 * the particular object type's registered default persister.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class ObjectPersistenceService
{
    private static final Log logger = LogFactory.getLog(ObjectPersistenceService.class);

    private PersisterService persisterService;

    /**
     * Sets the persister service
     *
     * @param persisterService persister service
     */
    public void setPersisterService(PersisterService persisterService)
    {
        this.persisterService = persisterService;
    }

    /**
     * Returns the persister service
     *
     * @return persister service
     */
    public PersisterService getPersisterService()
    {
        return this.persisterService;
    }

    /**
     * Provides a model persistence context
     *
     * If Surf is running in preview mode, the model persistence context will be populated
     * with information to inform the persisters of how to bind to the appropriate
     * Alfresco store.
     *
     * @return
     */
    public ModelPersistenceContext getPersistenceContext()
    {
        ModelPersistenceContext mpc = null;

        PreviewContext previewContext = ThreadLocalPreviewContext.getPreviewContext();
        if (previewContext != null)
        {
            mpc = new ModelPersistenceContext(previewContext.getUserId());
            mpc.setStoreId(previewContext.getStoreId());
            mpc.setWebappId(previewContext.getWebappId());
        }
        else
        {
            mpc = new ModelPersistenceContext(null);
        }

        return mpc;
    }

    /**
     * Retrieves an object from the persister that manages the given object type.
     * <p>
     * If the object is not available in cache, it is loaded from storage.
     *
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     *
     * @return the ModelObject or null if not found
     */
    public ModelObject getObject(String objectTypeId, String objectId)
    {
        ModelObject obj = null;
        
        ModelPersistenceContext context = getPersistenceContext();
        ModelObjectPersister preferredPersister = this.persisterService.getTypeToPersisterMap().get(objectTypeId);
        
        try
        {
            // Try the preferred persister first...
            obj = preferredPersister.getObject(context, objectTypeId, objectId);
        }
        catch (ModelObjectPersisterException e)
        {
            // Ignore this exception. We'll just try the remaining persisters.
        }
        
        if (obj == null)
        {
            Iterator<ModelObjectPersister> persisters = this.persisterService.getPersisters().iterator();
            while (obj == null && persisters.hasNext())
            {
                try
                {               
                    ModelObjectPersister currentPersister = persisters.next();
                    if (currentPersister == preferredPersister)
                    {
                        // Don't check the preferred persister a second time.
                    }
                    else
                    {
                        obj = currentPersister.getObject(context, objectTypeId, objectId);                        
                    }                    
                }
                catch (ModelObjectPersisterException mope)
                {
                    throw new PlatformRuntimeException("Unable to retrieve object: " + objectId + " of type: " + objectTypeId, mope);
                }                
            }
        }            
        return obj;
    }

    /**
     * Create a new object.
     *
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     *
     * @return the ModelObject or null if no persister for the given type can be found.
     */
    public ModelObject newObject(String objectTypeId, String objectId)
    {
        ModelObject obj = null;

        // get the default persister for this object type
        ModelObjectPersister persister = this.persisterService.getTypeToPersisterMap().get(objectTypeId);
        if (persister != null)
        {
            try
            {
                obj = persister.newObject(getPersistenceContext(), objectTypeId, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to create object: " + objectId + " of type: " + objectTypeId, mope);

                // allow null to be returned
            }
        }

        return obj;
    }

    /**
     * Create a new object.
     *
     * @param objectTypeId  the object type id
     *
     * @return the ModelObject or null if not found
     *
     * @return the model object
     */
    public ModelObject newObject(String objectTypeId)
    {
        ModelObject obj = null;

        // get the default persister for this object type
        ModelObjectPersister persister = this.persisterService.getTypeToPersisterMap().get(objectTypeId);
        if (persister != null)
        {
            String objectId = newGUID();
            try
            {
                obj = persister.newObject(getPersistenceContext(), objectTypeId, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to create object: " + objectId + " of type: " + objectTypeId, mope);
            }
        }

        return obj;
    }

    /**
     * Saves the object to its persister.
     *
     * @param object    the ModelObject to save
     * @throws ModelObjectPersisterException 
     */
    public boolean saveObject(ModelObject object) throws ModelObjectPersisterException
    {
        boolean saved = false;
        ModelObjectPersister persister = this.persisterService.getPersisterIdToPersisterMap().get(object.getPersisterId());
        if (persister != null)
        {
            try
            {                
                if (persister.hasReadOnlyStore())
                {
                    // If the persister that was used when creating the object has a read only store
                    // then it will not be possible to save the object, therefore we should try the 
                    // default persister assigned to the object type.
                    persister = this.persisterService.getTypeToPersisterMap().get(object.getTypeId());
                }
                
                if (logger.isDebugEnabled())
                    logger.debug("Attempting to save object '" + object.getId() + "' to persister: " + persister.getId());

                saved = persister.saveObject(getPersistenceContext(), object);                    
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to save object: " + object.getId() + " of type: " + object.getTypeId() +
                                " to persister: " + persister.getId() + " due to error", mope);
            }
        }
        else
        {
            // Could not find the persister that was used to create the object. This is an error condition.
            throw new ModelObjectPersisterException("Object \"" + object.getId() + "\" is configured to use the persister \"" + object.getPersisterId() + "\" that is not configured in the application context: " + this.persisterService.getPersisterIdToPersisterMap().size());
        }
        return saved;
    }
    
    /**
     * Saves a list of objects to their respective persisters. Objects with matching persisters are saved
     * in a single saveObjects() call to reduce individual API calls.
     * 
     * @param objects   the List of ModelObject's to save
     * @throws ModelObjectPersisterException
     */
    public boolean saveObjects(List<ModelObject> objects) throws ModelObjectPersisterException
    {
        boolean saved = false;

        Map<ModelObjectPersister, List<ModelObject>> saveMap = new LinkedHashMap<ModelObjectPersister, List<ModelObject>>();
        for (ModelObject object : objects)
        {
            ModelObjectPersister persister = this.persisterService.getPersisterIdToPersisterMap().get(object.getPersisterId());
            if (persister != null)
            {
                if (persister.hasReadOnlyStore())
                {
                    // If the persister that was used when creating the object has a read only store
                    // then it will not be possible to save the object, therefore we should try the 
                    // default persister assigned to the object type.
                    persister = this.persisterService.getTypeToPersisterMap().get(object.getTypeId());
                }
                
                List<ModelObject> toAppend = saveMap.get(persister);
                if (toAppend == null)
                {
                    toAppend = new LinkedList<ModelObject>();
                    saveMap.put(persister, toAppend);
                }
                toAppend.add(object);
            }
            else
            {
                // Could not find the persister that was used to create the object. This is an error condition.
                throw new ModelObjectPersisterException("Object \"" + object.getId() + "\" is configured to use the persister \"" + object.getPersisterId() + "\" that is not configured in the application context: " + this.persisterService.getPersisterIdToPersisterMap().size());
            }
        }
        
        for (ModelObjectPersister persister : saveMap.keySet())
        {
            try
            {
                saved = persister.saveObjects(getPersistenceContext(), saveMap.get(persister));
                if (logger.isDebugEnabled())
                    logger.debug("saveObjects save to persister '" + persister.getId() + "' returned: " + saved);
                if (saved) break;
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("Unable to save objects to persister: " + persister.getId() + " due to error", mope);
            }
        }
        
        return saved;
    }

    /**
     * Removes the object.
     *
     * @param object    the ModelObject to remove
     *
     * @return true if successful, false otherwise
     */
    public boolean removeObject(ModelObject object)
    {
        return removeObject(object.getTypeId(), object.getId());
    }

    /**
     * Removes the object.
     *
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     *
     * @return true if successful, false otherwise
     */
    public boolean removeObject(String objectTypeId, String objectId)
    {
        boolean removed = false;

        ModelPersistenceContext context = getPersistenceContext();
        ModelObjectPersister preferredPersister = this.persisterService.getTypeToPersisterMap().get(objectTypeId);
        try
        {
            // Try the preferred persister first...
            removed = preferredPersister.removeObject(context, objectTypeId, objectId);
        }
        catch (ModelObjectPersisterException e)
        {
            // Ignore this exception. We'll just try the remaining persisters.
        }
        
        if (!removed)
        {
            Iterator<ModelObjectPersister> persisters = this.persisterService.getPersisters().iterator();
            while (!removed && persisters.hasNext())
            {
                try
                {               
                    ModelObjectPersister currentPersister = persisters.next();
                    if (currentPersister == preferredPersister)
                    {
                        // Don't check the preferred persister a second time.
                    }
                    else
                    {
                        removed = persisters.next().removeObject(context, objectTypeId, objectId);                        
                    }                    
                }
                catch (ModelObjectPersisterException mope)
                {
                    throw new PlatformRuntimeException("Unable to remove object: " + objectId + " of type: " + objectTypeId, mope);
                }
            }
        }           
        
        return removed;
    }
    
    /**
     * Returns true if the object is present in a persister
     * 
     * @param object    the ModelObject to test for
     * 
     * @return true if found, false otherwise
     */
    public boolean hasObject(ModelObject object)
    {
        return hasObject(object.getTypeId(), object.getId());
    }
    
    /**
     * Returns true if the object is present in a persister
     * 
     * @param objectTypeId  the object type id
     * @param objectId      the object id
     * 
     * @return true if found, false otherwise
     */
    public boolean hasObject(String objectTypeId, String objectId)
    {
        boolean result = false;
        
        ModelPersistenceContext context = getPersistenceContext();
        ModelObjectPersister preferredPersister = this.persisterService.getTypeToPersisterMap().get(objectTypeId);
        
        try
        {
            // Try the preferred persister first...
            result = preferredPersister.hasObject(context, objectTypeId, objectId);
        }
        catch (ModelObjectPersisterException e)
        {
            // Ignore this exception. We'll just try the remaining persisters.
        }
        
        if (!result)
        {
            for (ModelObjectPersister currentPersister : this.persisterService.getPersisters())
            {
                try
                {
                    if (currentPersister != preferredPersister)
                    {
                        result = currentPersister.hasObject(context, objectTypeId, objectId);
                    }
                }
                catch (ModelObjectPersisterException mope)
                {
                    throw new PlatformRuntimeException("Unable to retrieve object: " + objectId + " of type: " + objectTypeId, mope);
                }

                if (result)
                {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * Retrieves all objects of a given type id.
     *
     * @param objectTypeId      Type ID
     *
     * @return a map of model objects (keyed by object id)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId)
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>();
        for(ModelObjectPersister persister: this.persisterService.getPersisters())
        {
            try
            {
                objects.putAll(persister.getAllObjects(getPersistenceContext(), objectTypeId));
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("ModelObjectManager unable to retrieve all objects", mope);
            }
        }
        return objects;
    }

    /**
     * Retrieves all objects of a given type id with the given object ID filter
     *
     * @param objectTypeId  the object type id
     *
     * @return a map of model objects (keyed by object id)
     */
    public Map<String, ModelObject> getAllObjects(String objectTypeId, String filter)
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(128, 1.0f);

        for (ModelObjectPersister persister: this.persisterService.getPersisters())
        {
            try
            {
                objects.putAll(persister.getAllObjectsByFilter(getPersistenceContext(), objectTypeId, filter));
            }
            catch (ModelObjectPersisterException mope)
            {
                if (logger.isInfoEnabled())
                    logger.info("ModelObjectManager unable to retrieve all objects by filter: " + filter, mope);
            }
        }

        return objects;
    }

    /**
     * New guid.
     *
     * @return the string
     */
    private static String newGUID()
    {
        return ModelHelper.newGUID();
    }

    /**
     * Invalidates the cache for all persisters in this persistence context
     */
    public void invalidateCache()
    {
        for (ModelObjectPersister persister: this.persisterService.getPersisters())
        {
            if (persister instanceof CachedPersister)
            {
                ((CachedPersister)persister).invalidateCache();
            }
        }
    }
}
