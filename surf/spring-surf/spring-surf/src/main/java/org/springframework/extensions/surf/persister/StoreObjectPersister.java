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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.cache.ModelObjectCache.ModelObjectSentinel;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.util.XMLUtil;

/**
 * Provides an implementation of a persister on top of a Web Script style store.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class StoreObjectPersister extends AbstractStoreObjectPersister
{    
    private static Log logger = LogFactory.getLog(StoreObjectPersister.class);
    
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#saveObject(org.springframework.extensions.surf.ModelPersistenceContext, org.springframework.extensions.surf.ModelObject)
     */
    @Override
    public boolean saveObject(ModelPersistenceContext context, ModelObject modelObject)
        throws ModelObjectPersisterException    
    {
        // do not process if the persister is disabled
        if (!isEnabled())
        {
            return false;
        }
        
        boolean saved = false;
        
        String content = modelObject.toXML();
        
        // the path to which we expect to save, this is essentially the path against
        // which we were instantiated or from which we were loaded - this may change
        // if the ID of the object has changed since creation - as objects are named
        // by convention based on the ID
        String oldPath = modelObject.getStoragePath();
        
        // calculate what path we want to save to
        String path = generatePath(modelObject.getTypeId(), modelObject.getId());
        try
        {
            // if the object hasn't been saved yet
            if (!modelObject.isSaved())
            {
                // create the document
                this.store.createDocument(path, content);
                
                // adjust the persister information to reflect new storage state
                ModelPersisterInfo info = modelObject.getKey();
                info.setStoragePath(path);
                info.setSaved(true);
                
                // put object into cache
                cachePut(context, modelObject);
                
                // flag that the save was successful
                saved = true;
            }
            else
            {
                // object was already saved
                // what we do in this case depends on whether the path changed
                if (!oldPath.equals(path))
                {
                    // path has changed, so first create the new object
                    this.store.createDocument(path, content);
                    
                    // remove the old object from the store
                    this.store.removeDocument(oldPath);
                    
                    // adjust the persister information to reflect new storage state
                    modelObject.getKey().setStoragePath(path);
                    modelObject.getKey().setSaved(true);
                    
                    // refresh the object in cache
                    cachePut(context, modelObject);
                    
                    // flag that the save was successful
                    saved = true;
                }
                else
                {
                    // file not moved, so just do an update
                    this.store.updateDocument(oldPath, content);
                    
                    // make sure it is marked as saved
                    modelObject.getKey().setSaved(true);
                    
                    // refresh the object in cache
                    cachePut(context, modelObject);
                    
                    // flag that the save was successful
                    saved = true;
                }
            }
        }
        catch (IOException ex)
        {
            throw new ModelObjectPersisterException("Unable to save object: " + oldPath + " due to error: "
                    + ex.getMessage(), ex);
        }
        
        return saved;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#saveObjects(org.springframework.extensions.surf.ModelPersistenceContext, java.util.List)
     */
    @Override
    public boolean saveObjects(ModelPersistenceContext context, List<ModelObject> objects)
            throws ModelObjectPersisterException
    {
        for (ModelObject obj : objects)
        {
            saveObject(context, obj);
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getObject(org.springframework.extensions.surf.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    @Override
    public ModelObject getObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException    
    {
        // do not process if the persister is disabled
        if (!isEnabled())
        {
            return null;
        }
        
        // get the object from the cache if possible
        ModelObject obj = cacheGet(context, objectTypeId, objectId);
        if (obj == null)
        {
            // calculate the path where the object is located
            String path = generatePath(objectTypeId, objectId);
            
            if (logger.isDebugEnabled())
                logger.debug("Loading object for path: " + path);
            
            try
            {
                // check to see if the requested object is present in the store
                if (this.store.hasDocument(path))
                {
                    // parse XML to a Document DOM
                    Document document = null;
                    try
                    {
                        document = XMLUtil.parse(this.store.getDocument(path));
                    }
                    catch (Exception err)
                    {
                        // if this occurs, it means the XML couldn't parse - log this
                        logger.warn("Failure to load model object for path: " + path, err);
                    }
                    
                    if (document != null)
                    {
                        Map<String, ModelObject> map = loadObjectAndDependants(context, document, objectTypeId, objectId, path);
                        
                        // Place the objects into the cache
                        
                        // We may not place the root object in the cache if the cache is disabled,
                        // however nested dependant items are always cached, so we set the flag true
                        // for all further items after the first. This is required because nested items
                        // cannot be loaded from a store without knowledge of the parent - so they will not
                        // be found at all unless they are present in the cache. It should be noted that the
                        // cache is always checked by the various get() methods - it is the put() behaviour
                        // that changes when caching is disabled by config.
                        boolean cacheItem = this.useCache;
                        for (final ModelObject o: map.values())
                        {
                            o.touch();
                            if (cacheItem)
                            {
                                getCache(context, o.getTypeId()).put(o.getId(), o);
                            }
                            cacheItem = true;
                        }
                        
                        obj = map.get(objectId);
                    }
                }
                else    
                {
                    // Document does not exist - add sentinel object, this will timeout like other cached values,
                    // this is to avoid multiple expensive store.hasDocument() calls on missing objects.
                    if (this.useCache)
                    {
                        getCache(context, objectTypeId).put(objectId, ModelObjectSentinel.getInstance());
                    }
                }
            }
            catch (Exception ex)
            {
                throw new ModelObjectPersisterException("Failure to load model object for path: " + path, ex);
            }
        }
        
        // handle cached sentinel case - we return null but the cache keeps the sentinel object reference
        if (obj == ModelObjectSentinel.getInstance())
        {
            obj = null;
        }
        
        return obj;
    }
}
