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

package org.springframework.extensions.surf.persister;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelObjectPersister;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;

/**
 * <p><code>MultiModelObjectPersister</code> class maintains the ModelObjectPersister contract
 * but redirects the retrieval of objects to a number of delegates. The delegate persisters can 
 * be of any persister implementation and are called in order of the supplied map of persisters 
 * during construction.</p>  
 * 
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 * 
 */
public class MultiObjectPersister extends AbstractObjectPersister implements CachedPersister
{
    private final static Log logger = LogFactory.getLog(MultiObjectPersister.class);

    /**
     * <p>This attribute should be configured through the Spring application context to set a
     * list of <code>ModelObjectPersister</code> instances to use for retrieving Spring objects.
     * The persisters will be searched as ordered in the list. This list should also include the
     * default persister (but if it does not it will get added to the list when the <code>init</code>
     * method is called.</p>
     */
    private List<ModelObjectPersister> persisters;
    
    /**
     * <p>This is the <code>ModelObjectPersister</code> that will be used for creating and saving
     * <code>ModelObjects</code>. This will get added to the end of  <code>persisters</code> list if it is
     * not already contained within it.</p>
     */
    private ModelObjectPersister defaultPersister;
    
    /**
     * Sets the persisters.
     * 
     * @param persisters the new persisters
     */
    public void setPersisters(List<ModelObjectPersister> persisters)
    {
        this.persisters = persisters;
    }
    
    /**
     * Gets the persisters.
     * 
     * @return the list of persisters
     */
    public List<ModelObjectPersister> getPersisters()
    {
    	return this.persisters;
    }
    
    /**
     * Sets the default persister.
     * 
     * @param defaultPersister the new default persister
     */
    public void setDefaultPersister(ModelObjectPersister defaultPersister)
    {
        this.defaultPersister = defaultPersister;
    }
            
    /**
     * <p>Iterates over the <code>persisters</code> list attempting to find a <code>ModelObject</code> of
     * the supplied type with the supplied id. The first match will be returned.</p>
     * 
     * @param The current persistence context
     * @param The type of object to retrieve
     * @param The id of the object to retrieve
     * @return A <code>ModelObject</code> matching the supplied type and id (or <code>null</code> if a match
     * could not be found.
     */
    public ModelObject getObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException
    {
        final boolean debug = logger.isDebugEnabled();
        ModelObject modelObject = null;
        
        // for each persister, see if we can load the object from its underlying storage
        for (ModelObjectPersister persister: this.persisters)
        {
            String persisterId = persister.getId();
            
            // try to load the object
            try
            {
                if (debug)
                    logger.debug("getObject attempting to load '" + objectId + "' of type '" + objectTypeId + "' from persister: " + persisterId); 
                
                modelObject = persister.getObject(context, objectTypeId, objectId);
            }
            catch (ModelObjectPersisterException mope)
            {
                throw new ModelObjectPersisterException("Error loading object id: " + objectId + " from persister id: " + persisterId, mope);
            }
            
            if (modelObject != null)
            {
                if (debug)
                    logger.debug("getObject loaded '" + objectId + "' from persister: " + persisterId); 
                
                // if we have the object, jump out
                break;
            }
        }
        
        if (modelObject == null && debug)
        {
            logger.debug("getObject() unable to get object from any persisters");
        }
        
        return modelObject;
    }
    
    /**
     * <p>Saves the supplied <code>ModelObject</code> using the default <code>ModelObjectPersister</code> (<b>Please 
     * note:</b> saving does <b>not</b> iterate over the <code>persisters</code> list - it only used the <code>default</code>
     * persister.</p>
     * 
     * @param context The current persistence context
     * @param object The <code>ModelObject</code> to save.
     * @return <code>true</code> if the object was successfully saved and <code>false</code> otherwise.
     * @throws ModelObjectPersisterException If an error occurred saving the object.
     */
    public boolean saveObject(ModelPersistenceContext context, ModelObject object) throws ModelObjectPersisterException    
    {
        boolean saved = false;
        
        if (object != null)
        {
            ModelObjectPersister targetPersister = resolveTargetPersister(object);
            
            // If we've found a persister, save the object...
            if (targetPersister != null)
            {
                saved = targetPersister.saveObject(context, object);
            }
        }
        else
        {
            // No object provided, nothing to save. 
        }        
        return saved;
    }

    private ModelObjectPersister resolveTargetPersister(ModelObject object) throws ModelObjectPersisterException
    {
        ModelObjectPersister targetPersister = this.defaultPersister;
        String targetPersisterId = object.getPersisterId();
        if (targetPersisterId == null)
        {
            // No persister has been configured. This should not be possible, however should this
            // manage to occur then we will just use the default persister.
        }
        else if (targetPersisterId.equals(this.getId()))
        {
            // It should NOT be possible for the target persister to be this object. This is because
            // even if Spring Surf has been configured to use a MultiObjectPersister to persist a 
            // particular object type, the actual persistence will be deferred to another object.
            // HOWEVER... should something manage to circumvent this restriction then we will use
            // the default persister (if configured) to avoid entering an infinite loop.
            // 
            // The target persister is already initialised to the default so no action is required.
        }
        else
        {
            // Get the persister from the PersisterService. The target persister will be in the map
            // providing that the object has been retrieved from the persister configured for the
            // object type. However, it is possible that we are saving an object retrieved from 
            // an alternative persister. If this is the case then the persister will not be present
            // so we will need to look it up from the application context. This service is also
            // provided by the PersisterService.
            targetPersister = getPersisterService().getPersisterIdToPersisterMap().get(targetPersisterId);
            
            if (targetPersister.hasReadOnlyStore())
            {
                // If the persister that was used when creating the object has a read only store then it 
                // will not be possible to save the object, therefore we should use the default persister.
                targetPersister = this.defaultPersister;
            }
            
            // If we STILL haven't found the persister, then we are in an error state because the 
            // object has been retrieved from a persister that does not exist. It is NOT acceptable to 
            // just save to the default persister because we will end up with 2 copies of the object.
            if (targetPersister == null)
            {
                throw new ModelObjectPersisterException("The target persister: \"" + targetPersisterId + "\" configured for the object: \"" + object + "\" does not exist");
            }
        }
        return targetPersister;
    }
    
    /**
     * <p>Saves the supplied <code>ModelObject</code> list using the default <code>ModelObjectPersister</code> (<b>Please 
     * note:</b> saving does <b>not</b> iterate over the <code>persisters</code> list - it only used the <code>default</code>
     * persister.</p>
     * 
     * @param context The current persistence context
     * @param objects The List of <code>ModelObject</code>s to save.
     * @return <code>true</code> if the objects were successfully saved and <code>false</code> otherwise.
     * @throws ModelObjectPersisterException If an error occurred saving the objects.
     */
    public boolean saveObjects(ModelPersistenceContext context, List<ModelObject> objects)
        throws ModelObjectPersisterException
    {
        boolean saved = true;
        
        Map<ModelObjectPersister, List<ModelObject>> saveMap = new LinkedHashMap<ModelObjectPersister, List<ModelObject>>();
        for (ModelObject object : objects)
        {
            ModelObjectPersister persister = resolveTargetPersister(object);
            List<ModelObject> toAppend = saveMap.get(persister);
            if (toAppend == null)
            {
                toAppend = new LinkedList<ModelObject>();
                saveMap.put(persister, toAppend);
            }
            toAppend.add(object);
        }
        
        for (ModelObjectPersister persister : saveMap.keySet())
        {
            saved = persister.saveObjects(context, saveMap.get(persister)) && saved;
            if (logger.isDebugEnabled())
                logger.debug("saveObjects save to persister '" + persister.getId() + "' returned: " + saved);
        }
        
        return saved;
    }
    
    /**
     * <p>Removes the supplied object the first <code>ModelObjectPersister</code> containing a match.</p>
     * 
     * @param context The current persistence context
     * @param object The object to remove
     * @return <code>true</code> if the object was successfully removed and <code>false</code> otherwise.
     * @throws ModelObjectPersisterException If an error occurs while removing the object.
     */
    public boolean removeObject(ModelPersistenceContext context, ModelObject object) throws ModelObjectPersisterException    
    {
        return removeObject(context, object.getTypeId(), object.getId());
    }
    
    /**
     * <p>Removes an object with the supplied type and id from the first <code>ModelObjectPersister</code>
     * containing a match.</p>
     * 
     * @param context The current persistence context
     * @param objectTypeId The type of object to remove
     * @param objectId The id of the object to remove
     * @return <code>true</code> if the object was successfully removed and <code>false</code> otherwise.
     * @throws ModelObjectPersisterException If an error occurs while removing the object.
     */
    public boolean removeObject(ModelPersistenceContext context, String objectTypeId, String objectId) throws ModelObjectPersisterException    
    {
        boolean removed = false;
        
        // for each persister, see if we can find the object from its underlying storage
        for (ModelObjectPersister persister: this.persisters)
        {
            String persisterId = persister.getId();
            
            if (persister.hasObject(context, objectTypeId, objectId))
            {
                if (logger.isDebugEnabled())
                    logger.debug("removeObject attempting to remove '" + objectId + "' of type '" + objectTypeId + "' from persister: " + persisterId); 
                
                removed = persister.removeObject(context, objectTypeId, objectId);
                
                if (logger.isDebugEnabled())
                    logger.debug("removeObject removed from persister '" + persisterId + "' returned: " + removed);
            }
            else
            {
                // clean up caches for all persisters
                if (logger.isDebugEnabled())
                    logger.debug("removeObject cleaning up cache for persister '" + persisterId);
                persister.removeObject(context, objectTypeId, objectId);
            }
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException  
    {
        return hasObject(context, object.getTypeId(), object.getId());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public boolean hasObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException  
    {
        boolean hasObject = false;
        
        // for each persister, see if we can load the object from its underlying storage
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister.hasObject(context, objectTypeId, objectId))
            {
                hasObject = true;
                break;
            }
        }
        
        return hasObject;
    }
    
    /**
     * <p>Creates a new object of the supplied type with the supplied id using the 
     */
    public ModelObject newObject(ModelPersistenceContext context, String objectTypeId, String objectId) throws ModelObjectPersisterException    
    {
        ModelObject obj = null;
        
        ModelObjectPersister targetPersister = this.getPersisterService().getTypeToPersisterMap().get(objectTypeId);
        if (targetPersister == null)
        {
            // If no persister could be found in the map then this means that the object type
            // has been configured to use a persister that could not be located in the application
            // context. An exception SHOULD have been thrown long before we reach this code
            // block (ideally in the PersisterService when setting up the persister maps) but
            // if we have managed to get this far then we need to throw an exception.
            throw new ModelObjectPersisterException("The persister configured for object type: \"" + objectTypeId + "\" does not exist");
        }
        else if (targetPersister == this)
        {
            // If Spring Surf has been configured to use this MultiObjectPersister as the
            // persister to use to create the supplied object type then the implication is the
            // default persister for this MultiObjectPersister should be used. However, if 
            // no default has been provided then there is no alternative other than to throw
            // an exception.
            if (this.defaultPersister == null)
            {
                throw new ModelObjectPersisterException("Unable to create new object - no default persister configured");    
            }
            else
            {
                targetPersister = this.defaultPersister;    
            }            
        }
        
        // Create the object...
        obj = defaultPersister.newObject(context, objectTypeId, objectId);
        return obj;
    }  
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context, String objectTypeId)
        throws ModelObjectPersisterException
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(512, 1.0f);
        
        // for each persister, see if we can load all objects from its underlying storage
        for (ModelObjectPersister persister: this.persisters)
        {
            Map<String, ModelObject> map = persister.getAllObjects(context, objectTypeId);
            objects.putAll(map);
        }
        
        if (logger.isDebugEnabled())
            logger.debug("getAllObjects returned set of size: " + objects.size());
        
        return objects;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getAllObjectsByFilter(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> getAllObjectsByFilter(ModelPersistenceContext context, String objectTypeId, String filter) throws ModelObjectPersisterException
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(128, 1.0f);
        
        // for each persister, see if we can load all objects from its underlying storage
        for (ModelObjectPersister persister: this.persisters)
        {
            Map<String, ModelObject> map = persister.getAllObjectsByFilter(context, objectTypeId, filter);
            objects.putAll(map);
        }
        
        if (logger.isDebugEnabled())
            logger.debug("getAllObjects by filter: " + filter + " returned set of size: " + objects.size());
        
        return objects;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getTimestamp(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public long getTimestamp(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException
    {
        long timestamp = -1;
        
        // find the persister that has the object
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister.hasObject(context, objectTypeId, objectId))
            {
                timestamp = persister.getTimestamp(context, objectTypeId, objectId);
                break;
            }
        }
        
        return timestamp;
    }

    /**
     * <p>If this <code>MultiObjectPerister</code> has been added to the list of <code>ModelObjectPersisters</code>
     * managed by a <code>PeristerService</code> then this method will be invoked when that <code>PeristerService</code>
     * is initialised. It initialises all the delegate <code>ModelObjectPersisters</code> used for retrieving objects
     * as well as the default <code>ModelObjectPersister</code> used for creating/saving objects (which will also be
     * added to the list of delegates if it has not been configured as such).</p>
     */
    public void init(ModelPersistenceContext context)
    {
        // Initialise each persister
        boolean initialisedDefaultPersister = false;
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister.equals(this.defaultPersister))
            {
                initialisedDefaultPersister = true;
            }
            persister.init(context);
        }
        
        // If the default persister was not included in the list of delegated
        // persisters then it will not have been initialised, this also means
        // that it will not be searched when attempting to retrieve an object.
        // We should therefore initialise it AND add it to the list (as there is
        // little point in creating/saving objects if they cannot be retrieved!!)
        if (!initialisedDefaultPersister)
        {
            this.defaultPersister.init(context);
            this.persisters.add(this.defaultPersister);
        }
    }
    
    /**
     * @see org.springframework.extensions.surf.persister.CachingPersister#invalidateCache()
     */
    public void invalidateCache()
    {
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister instanceof CachedPersister)
            {
                ((CachedPersister)persister).invalidateCache();
            }
        }
    }
    
    /**
     * @see org.springframework.extensions.surf.persister.CachedPersister#setCache(boolean)
     */
    public void setCache(boolean cache)
    {
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister instanceof CachedPersister)
            {
                ((CachedPersister)persister).setCache(cache);
            }
        }
    }
    
    /**
     * @see org.springframework.extensions.surf.persister.CachedPersister#setCacheCheckDelay(int)
     */
    public void setCacheCheckDelay(int cacheCheckDelay)
    {
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister instanceof CachedPersister)
            {
                ((CachedPersister)persister).setCacheCheckDelay(cacheCheckDelay);
            }
        }
    }
    
    /**
     * @see org.springframework.extensions.surf.persister.CachedPersister#setCacheMaxSize(int)
     */
    public void setCacheMaxSize(int cacheMaxSize)
    {
        for (ModelObjectPersister persister: this.persisters)
        {
            if (persister instanceof CachedPersister)
            {
                ((CachedPersister)persister).setCacheMaxSize(cacheMaxSize);
            }
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder out = new StringBuilder();
        int i = 0;
        for (ModelObjectPersister p: this.persisters)
        {
            if (i == 0) out.append("[");
            out.append(p.toString());
            if (i < this.persisters.size() - 1) out.append(", ");
            if (i == this.persisters.size() - 1) out.append("]");
            i++;
        }
        return out.toString();
    }

    public boolean hasReadOnlyStore()
    {
        return this.defaultPersister.hasReadOnlyStore();
    }
}
