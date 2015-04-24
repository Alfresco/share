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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.cache.ContentCache;
import org.springframework.extensions.surf.cache.ModelObjectCache;
import org.springframework.extensions.surf.cache.ModelObjectCache.ModelObjectSentinel;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.Pair;
import org.springframework.extensions.surf.util.ReflectionHelper;
import org.springframework.extensions.surf.util.XMLUtil;


/**
 * Provides an implementation of a persister on top of a Web Script Remote
 * style path based store.
 * <p>
 * Unlike the read-only implementation, this implementation does not assume
 * the cache as the "master copy" of the data.  Rather, the "master copy" is
 * considered to be the store itself.  Any put or remote based interactions
 * with persisted content are delegated to the store itself. All get operations
 * utilize the cache for performance benefits, with a configured timeout to
 * reintegrogate last modified dates of actual store content.
 * <p>
 * Since remote stores are completely based on file-path semantics, this persister
 * must make assumptions on how to map ids to to file-paths. The ID of an object
 * is intrinsically linked to it's path within the store - the object type is
 * not relevant - therefore all caching is based on storage path.
 * <p>
 * The id to file-path and file-path to id conversion methods are split out so
 * that they can be adjusted in inheriting implementations.
 * <p>
 * In addition, path prefixes support token replacement for object type ids
 * and object ids.
 * 
 * @author Kevin Roast
 */
public class PathStoreObjectPersister extends AbstractStoreObjectPersister
{
    private static Log logger = LogFactory.getLog(PathStoreObjectPersister.class);
    
    /** Default object cache */
    protected ModelObjectCache objectCache;
    
    /** Set of types that should not be cached by the persister.
        Types that are loaded remotely from a central repository should not be 
        cached if the web-tier is going to be clustered or in a web-farm */
    protected Set<String> noncachableObjectTypes = null;
    
    /** Lock object for cache construction */
    final protected ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    
    /** Settings to use a tenant partitioned object cache - based on \@domain in username field
        Default is true. Set to false if using email style usernames but not using tenant features */
    protected boolean tenantObjectCache = true;
    
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#init(org.alfresco.web.framework.ModelPersistenceContext)
     */
    @Override
    public void init(ModelPersistenceContext context)
    {
        // we require a store to use this persister
        // if a store doesn't exist, then disable persister
        if (!this.store.exists())
        {
            if (logger.isInfoEnabled())
                logger.info("Store missing for persister: " + this.getId());
            
            this.disable();
        }
        
        // create the cache once Spring property init has setup appropriate cache config options
        this.objectCache = (ModelObjectCache)createCache();
    }
    
    /**
     * The Set of model object types that should never be cached by the persister
     */
    public void setNoncachableObjectTypes(Set<String> types)
    {
        this.noncachableObjectTypes = types;
    }
    
    /**
     * @param tenantObjectCache true to use a tenant partitioned object cache based on \@domain username
     */
    public void setTenantObjectCache(boolean tenantObjectCache)
    {
        this.tenantObjectCache = tenantObjectCache;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#saveObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
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
                cachePut(context, path, modelObject);
            }
            else
            {
                // object was already saved
                // what we do in this case depends on whether the path changed
                if (!oldPath.equals(path))
                {
                    // path has changed, so first create the new object
                    this.store.createDocument(path, content);
                    
                    // adjust the persister information to reflect new storage state
                    modelObject.getKey().setStoragePath(path);
                    modelObject.getKey().setSaved(true);
                    
                    // put object into new cache location
                    cachePut(context, path, modelObject);
                    
                    // remove old object from old cache location
                    cacheRemove(context, modelObject.getTypeId(), oldPath);
                    
                    // remove the old object from the store
                    this.store.removeDocument(oldPath);
                }
                else
                {
                    // file not moved, so just do an update
                    this.store.updateDocument(oldPath, content);
                    
                    // make sure it is marked as saved
                    modelObject.getKey().setSaved(true);
                    
                    // refresh the object in cache
                    cachePut(context, modelObject);
                }
            }
        }
        catch (IOException ex)
        {
            throw new ModelObjectPersisterException("Unable to save object: " + oldPath + " due to error: "
                    + ex.getMessage(), ex);
        }
        
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.persister.ReadOnlyStoreObjectPersister#saveObjects(org.springframework.extensions.surf.ModelPersistenceContext, java.util.List)
     */
    @Override
    public boolean saveObjects(ModelPersistenceContext context, List<ModelObject> modelObjects)
            throws ModelObjectPersisterException
    {
        // do not process if the persister is disabled
        if (!isEnabled())
        {
            return false;
        }
        
        try
        {
            List<Pair<String, Document>> docsToCreate = new LinkedList<Pair<String,Document>>();
            List<ModelObject> objectsToCache = new LinkedList<ModelObject>();
            for (ModelObject modelObject : modelObjects)
            {
                // the path to which we expect to save, this is essentially the path against
                // which we were instantiated or from which we were loaded - this may change
                // if the ID of the object has changed since creation - as objects are named
                // by convention based on the ID
                String oldPath = modelObject.getStoragePath();
                
                // calculate what path we want to save to
                String path = generatePath(modelObject.getTypeId(), modelObject.getId());
                if (!modelObject.isSaved())
                {
                    // create the document
                    docsToCreate.add(new Pair<String, Document>(path, modelObject.getDocument()));
                    
                    // adjust the persister information to reflect new storage state
                    ModelPersisterInfo info = modelObject.getKey();
                    info.setStoragePath(path);
                    info.setSaved(true);
                    
                    // put object into cache
                    objectsToCache.add(modelObject);
                }
                else
                {
                    // object was already saved
                    // what we do in this case depends on whether the path changed
                    if (!oldPath.equals(path))
                    {
                        // path has changed, so first create the new object
                        docsToCreate.add(new Pair<String, Document>(path, modelObject.getDocument()));
                        
                        // adjust the persister information to reflect new storage state
                        ModelPersisterInfo info = modelObject.getKey();
                        info.setStoragePath(path);
                        info.setSaved(true);
                        
                        // put object into new cache location
                        objectsToCache.add(modelObject);
                        
                        // remove old object from old cache location
                        cacheRemove(context, modelObject.getTypeId(), oldPath);
                        
                        // remove the old object from the store
                        this.store.removeDocument(oldPath);
                    }
                    else
                    {
                        // file not moved, so just do an update
                        this.store.updateDocument(oldPath, modelObject.toXML());
                        
                        // make sure it is marked as saved
                        modelObject.getKey().setSaved(true);
                        
                        // refresh the object in cache
                        cachePut(context, modelObject);
                    }
                }
            }
            
            // Bulk create and cache the documents
            if (!docsToCreate.isEmpty())
            {
                this.store.createDocuments(docsToCreate);
            }
            
            for (ModelObject modelObject : objectsToCache)
            {
                cachePut(context, modelObject);
            }
        }
        catch (IOException ex)
        {
            throw new ModelObjectPersisterException("Unable to save objects due to error: " + ex.getMessage(), ex);
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
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
        final String path = generatePath(objectTypeId, objectId);
        ModelObject obj = cacheGet(context, path);
        if (obj == null)
        {
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
                        
                        // Document does not exist - add sentinel object, this will timeout like other cached values,
                        // this is to avoid multiple store.hasDocument() calls on missing objects.
                        if (useCacheForType(objectTypeId))
                        {
                            getCache(context, null).put(path, ModelObjectSentinel.getInstance());
                        }
                    }
                    
                    if (document != null)
                    {
                        // instantiate object
                        ModelPersisterInfo info = new ModelPersisterInfo(getId(), path, true);
                        String implClassName = getWebFrameworkConfiguration().getTypeDescriptor(objectTypeId).getImplementationClass();
                        obj = (ModelObject) ReflectionHelper.newObject(
                                implClassName,
                                MODELOBJECT_CLASSES,
                                new Object[] { objectId, info, (Document)document }
                        );
                        
                        // if found, place the object into the cache
                        if (obj != null)
                        {
                            obj.touch();
                            
                            cachePut(context, path, obj);
                        }
                        else
                        {
                            throw new ModelObjectPersisterException("Unable to construct object of class: " + implClassName);
                        }
                    }
                }
                else    
                {
                    // Document does not exist - add sentinel object, this will timeout like other cached values,
                    // this is to avoid multiple store.hasDocument() calls on missing objects.
                    if (useCacheForType(objectTypeId))
                    {
                        getCache(context, null).put(path, ModelObjectSentinel.getInstance());
                    }
                }
            }
            catch (IOException err)
            {
                // if an IO error occurs (remote server down etc.) then return null for the object but
                // do not cache the result - this allows retries later and exceptions not to bubble up
                if (logger.isDebugEnabled())
                    logger.debug("IO Error: during getObject() " + err.getMessage(), err);
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
    
    /**
     * Return true if the cache should be used for the given object type ID.
     * Takes the class wide useCache value into account, and then checks the
     * noncachableObjectTypes member to see if caching has been specifically disabled.
     * 
     * @param objectTypeId to check
     * 
     * @return true if the cache should be used for the type, false otherwise
     */
    protected boolean useCacheForType(String objectTypeId)
    {
        boolean useCache = this.useCache;
        
        // if cache is enabled, check the type list also
        if (useCache && this.noncachableObjectTypes != null)
        {
            useCache = !this.noncachableObjectTypes.contains(objectTypeId);
        }
        
        return useCache;
    }
    
    /**
     * Places an object into this persister's cache.
     * 
     * @param context the context
     * @param path the path
     * @param obj the obj
     */
    protected void cachePut(ModelPersistenceContext context, String path, ModelObject obj)
    {
        if (useCacheForType(obj.getTypeId()))
        {
            if (logger.isDebugEnabled())
                logger.debug("Put into cache: " + path);
            
            getCache(context, null).put(path, obj);
        }
    }
    
    /**
     * Places an object into this persister's cache.
     * 
     * @param context the context
     * @param id ID of the object in the cache
     * @param obj the obj
     */
    @Override
    protected void cachePut(ModelPersistenceContext context, ModelObject obj)
    {
        if (useCacheForType(obj.getTypeId()))
        {
            final String path = generatePath(obj.getTypeId(), obj.getId());
            
            if (logger.isDebugEnabled())
                logger.debug("Put into cache: " + path);
            
            getCache(context, null).put(path, obj);
        }
    }
    
    /**
     * Removes an object from the cache
     * 
     * @param context the context
     * @param objectTypeId
     * @param objectId
     */
    @Override
    protected void cacheRemove(ModelPersistenceContext context, ModelObject obj)
    {
        if (useCacheForType(obj.getTypeId()))
        {
            final String path = generatePath(obj.getTypeId(), obj.getId());
            
            if (logger.isDebugEnabled())
                logger.debug("Remove from cache: " + path);
            
            getCache(context, null).remove(path);
        }
    }

    /**
     * Removes an object from the cache
     * 
     * @param context the context
     * @param objectTypeId
     * @param objectId
     */
    @Override
    protected void cacheRemove(ModelPersistenceContext context, String objectTypeId, String objectId)
    {
        if (useCacheForType(objectTypeId))
        {
            final String path = generatePath(objectTypeId, objectId);
            
            if (logger.isDebugEnabled())
                logger.debug("Remove from cache: " + path);
            
            getCache(context, null).remove(path);
        }
    }
    
    /**
     * Returns an object from the cache
     * 
     * @param context the context
     * @param path the path
     * 
     * @return the model object
     */
    protected ModelObject cacheGet(ModelPersistenceContext context, String path)
    {
        return getCache(context, null).get(path);
    }
    
    /**
     * Returns an object from the cache. Always check the cache - even if not explicity enabled - as
     * nested component definitions are always cached otherwise they would not be found on lookup.
     * 
     * @param context the context
     * @param objectTypeId
     * @param objectId
     * 
     * @return the model object
     */
    @Override
    protected ModelObject cacheGet(ModelPersistenceContext context, String objectTypeId, String objectId)
    {
        final String path = generatePath(objectTypeId, objectId);
        return getCache(context, null).get(path);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    @Override
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context, String objectTypeId)
        throws ModelObjectPersisterException
    {
        // not supported by this store - as could return 10,000's of objects
        return new HashMap<String, ModelObject>(0);
    }
    
    /**
     * Retrieves an object from the underlying store by path
     * 
     * This performs an interrogation of the underlying document
     * to determine its object type and object id.
     * 
     * @param context
     * @param path
     * 
     * @return ModelObject
     * 
     * @throws ModelObjectPersisterException
     */
    @Override
    protected ModelObject getObjectByPath(ModelPersistenceContext context, String path)
        throws ModelObjectPersisterException    
    {
        // TODO: if this is called, the incorrect ID will be generated for Share objects
        //       *if* the ID field is not present in the XML doc...
        
        // do not process if the persister is disabled
        if (!isEnabled())
        {
            return null;
        }        
        
        if (logger.isDebugEnabled())
            logger.debug("Getting object for path: " + path);
        
        ModelObject obj = null;
        try
        {
            // check to see if the requested object is present in the store
            if (this.store.hasDocument(path))
            {
                // parse XML to a Document DOM
                Document document = XMLUtil.parse(this.store.getDocument(path));
                
                // get the object type id described by this document (if possible)
                final String objectTypeId = this.getObjectTypeId(document, path);
                if (objectTypeId != null)
                {                
                    // get the object id described by this document (if possible)
                    final String objectId = getObjectId(document, path);
                    
                    obj = createObject(document, objectTypeId, objectId, path);
                    
                    // if found, place the object into the cache
                    if (obj != null)
                    {
                        obj.touch();
                        
                        cachePut(context, path, obj);
                    }
                    else
                    {
                        throw new ModelObjectPersisterException("Unable to construct object for path: " + path);
                    }
                }
                else
                {
                    logger.warn("Failed to calculate objectTypeId for path: " + path);
                }
            }
        }
        catch (Exception ex)
        {
            throw new ModelObjectPersisterException("Failure to load model object for path: " + path, ex);
        }
        
        return obj;
    }
    
    /**
     * Determines the object id of a serialized model object
     * contained in a document.
     * 
     * If the <id/> property is available in the serialized document, it will be
     * used.  Otherwise, the id will be assumed to be the file name.
     * 
     * @param doc document
     * @param path the path
     * 
     * @return object id (or null if it is not a model object)
     */
    @Override
    protected String getObjectId(Document doc, String path)
    {
        String id = doc.getRootElement().elementText("id");
        if (id == null && path != null)
        {
            // convert file separators if needed
            path = path.replace('\\', '/');
            
            // strip .xml file extension
            if (path.endsWith(XML_EXT))
            {
                path = path.substring(0, path.length() - 4);
            }
            
            // use path as ID - from the final element if more than one present
            id = path;
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash != -1)
            {
                id = path.substring(lastSlash + 1);
            }
        }
        
        return id;
    }
    
    /**
     * Gets the cache for a particular model persistence context
     * 
     * @param context       ModelPersistenceContext
     * @param bucket        Cache bucket to pick (not used by PathStore)
     * 
     * @return the cache
     */
    @Override
    protected ContentCache<ModelObject> getCache(ModelPersistenceContext context, String bucket)
    {
        ContentCache<ModelObject> cache = this.objectCache;
        
        if (this.tenantObjectCache)
        {
            String storeId = (String)context.getValue(ModelPersistenceContext.REPO_STOREID);
            if (storeId == null)
            {
                String userId = ThreadLocalRequestContext.getRequestContext().getUserId();
                if (userId != null)
                {
                    int idx = userId.indexOf('@');
                    if (idx != -1)
                    {
                        // assume MT so partition by user domain
                        storeId = userId.substring(idx);
                    }
                }
            }
            
            if (storeId != null)
            {
                this.cacheLock.readLock().lock();
                try
                {
                    cache = this.caches.get(storeId);
                    if (cache == null)
                    {
                        this.cacheLock.readLock().unlock();
                        this.cacheLock.writeLock().lock();
                        try
                        {
                            // check again, as more than one thread could have been waiting on the Write lock
                            cache = this.caches.get(storeId);
                            if (cache == null)
                            {
                                cache = createCache();
                                this.caches.put(storeId, cache);
                            }
                        }
                        finally
                        {
                            this.cacheLock.readLock().lock();
                            this.cacheLock.writeLock().unlock();
                        }
                    }
                }
                finally
                {
                    this.cacheLock.readLock().unlock();
                }
            }
        }
        
        return cache;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.persister.CachingPersister#invalidateCache()
     */
    @Override
    public synchronized void invalidateCache()
    {
        super.invalidateCache();
        this.objectCache.invalidate();
    }
}
