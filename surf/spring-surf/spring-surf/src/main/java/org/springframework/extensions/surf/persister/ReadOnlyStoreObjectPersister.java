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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.extensions.surf.ModelHelper;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.cache.ContentCache;
import org.springframework.extensions.surf.cache.ModelObjectCache;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.types.AdvancedComponent;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.PageAssociation;
import org.springframework.extensions.surf.util.ReflectionHelper;
import org.springframework.extensions.surf.util.XMLUtil;
import org.springframework.extensions.webscripts.ClassPathStore;
import org.springframework.extensions.webscripts.DeclarativeRegistry;
import org.springframework.extensions.webscripts.PathUtil;
import org.springframework.extensions.webscripts.RemoteStore;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebApplicationStore;

/**
 * A persister that provides read-only inter-operability with Web Script
 * Framework stores.
 * <p>
 * The caching layer is extended and tiered intentionally to allow for composite
 * keys formed from a combination of store ids and object-type ids.
 * <p>
 * The objective is to allow persisters to interrogate the cache and ask for
 * objects of a specified type in the current store context.  The store context
 * is allowed to shift in the Web Framework runtime and the cache must be
 * sensitive to that.
 * <p>
 * This persister follows the pattern of store usage in the web script framework
 * by treating the cache as a "master copy" of the persistence state.  The cache
 * is populated by the init() method and is then considered to be the master copy.
 * If contents change inside of the underlying store, they will not be detected
 * until the reset() method is employed.
 * <p>
 * This persister implementation therefore lets the web framework accommodate
 * a pattern of store usage that is analogous to the web script framework.
 * 
 * @see ClassPathStore
 * @see RemoteStore
 * @see WebApplicationStore
 * 
 * @author muzquiano
 * @author kevinr
 */
public class ReadOnlyStoreObjectPersister extends AbstractCachedObjectPersister
{
    private static Log logger = LogFactory.getLog(ReadOnlyStoreObjectPersister.class);
    
    protected Store store;
    protected String pathPrefix = null;
    final private ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    
    
    /**
     * Sets the store.
     * 
     * @param store the new store
     */
    public void setStore(Store store)
    {
        this.store = store;
    }   
    
    /**
     * Indicates whether the store used by the persister is read only or not.
     * 
     * @return <code>true</code> is the store is read only and <code>false</code> otherwise.
     */
    public boolean hasReadOnlyStore()
    {
        return this.store.isReadOnly();
    }
    
    /**
     * Sets a path prefix to be applied to generated paths
     * 
     * @param pathPrefix the path prefix
     */
    public void setPathPrefix(String pathPrefix)
    {
        this.pathPrefix = pathPrefix;
    }
    
    /**
     * Gets the path prefix.
     * 
     * @return the path prefix or null if not provided
     */
    public String getPathPrefix()
    {
        return this.pathPrefix;
    }
    
    /**
     * Generates a persistence path for a given object id
     * and object type id.
     * 
     * Default way to convert an object id into a storage path
     * is to add the .xml extension to the object id.
     * <p>
     * This method should never return a null value.
     * 
     * @param objectTypeId the object type id
     * @param objectId the object id
     * 
     * @return the storage path for this id
     */
    protected String generatePath(String objectTypeId, String objectId)
    {
        String path = null;
        
        String prefix = getPathPrefix();
        if (prefix == null)
        {
            path = new StringBuilder(objectId.length() + 4).append(objectId).append(".xml").toString();
        }
        else
        {
            path = new StringBuilder(prefix.length() + objectId.length() + 5).append(prefix).append('/').append(objectId).append(".xml").toString();
        }
        
        return path;        
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#saveObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean saveObject(ModelPersistenceContext context, ModelObject modelObject)
        throws ModelObjectPersisterException    
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#saveObjects(org.springframework.extensions.surf.ModelPersistenceContext, java.util.List)
     */
    public boolean saveObjects(ModelPersistenceContext context, List<ModelObject> objects)
            throws ModelObjectPersisterException
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#newObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public ModelObject newObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException
    {
        if (objectId == null)
        {
            throw new ModelObjectPersisterException("Cannot create object with null object id");
        }
        
        // create the minimum XML - nodes will be added using DOM methods to it
        String xml = "<" + objectTypeId + "></" + objectTypeId + ">";
        
        // build the object
        ModelObject obj = null;
        try
        {
            Document document = XMLUtil.parse(xml);
            
            // calculate the path where the object will eventually be persisted
            String path = generatePath(objectTypeId, objectId);
            
            // build the object
            ModelPersisterInfo info = new ModelPersisterInfo(getId(), path, false);                
            String implClassName = getWebFrameworkConfiguration().getTypeDescriptor(objectTypeId).getImplementationClass();
            obj = (ModelObject) ReflectionHelper.newObject(
                    implClassName, MODELOBJECT_CLASSES,
                    new Object[] { objectId, info, document });
            
            // if constructed ok, place the object into the cache if required
            if (obj != null)
            {
                obj.touch();
                
                cachePut(context, obj);
            }
            else
            {
                throw new ModelObjectPersisterException("Unable to construct object of class: " + implClassName + " for path: " + path);
            }
        }
        catch (DocumentException de)
        {
            // something failed while trying to load the xml object
            throw new ModelObjectPersisterException("Failed to load objectId: " + objectId, de);
        }
        
        return obj;
    }
        
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public boolean removeObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException    
    {
        return false;
    }    
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public ModelObject getObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException    
    {
        ModelObject mo = null;
        
        if (isEnabled())
        {
            // get the object from the cache
            mo = cacheGet(context, objectTypeId, objectId);
        }
        
        return mo;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public boolean hasObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException  
    {
        // do not process if persister is disabled
        if (!isEnabled())
        {
            return false;
        }
        
        // check the cache here
        return (cacheGet(context, objectTypeId, objectId) != null);
    }  
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getTimestamp(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public long getTimestamp(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException
    {
        long timestamp = -1;
        
        ModelObject obj = cacheGet(context, objectTypeId, objectId);
        if (obj != null)
        {
            timestamp = obj.getModificationTime();
        }
        
        return timestamp;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context, String objectTypeId)
        throws ModelObjectPersisterException
    {
        return getAllObjectsByFilter(context, objectTypeId, null);
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#getAllObjectsByFilter(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> getAllObjectsByFilter(ModelPersistenceContext context, String objectTypeId, String objectIdPattern)
        throws ModelObjectPersisterException
    {
        // do not process if persister is disabled
        if (!isEnabled())
        {
            // empty map
            return new HashMap<String, ModelObject>();
        }
        
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(256);
        
        // walk through all of our caches and hand back all objects of this type
        Iterator<String> it = this.caches.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            if (key.startsWith(objectTypeId + ":"))
            {
                ModelObjectCache cache = (ModelObjectCache) this.caches.get(key);
                
                Iterator<String> it2 = cache.keys().iterator();
                while (it2.hasNext())
                {
                    boolean proceed = false;
                    
                    String key2 = (String) it2.next();
                    
                    if (objectIdPattern != null)
                    {
                        proceed = Pattern.matches(objectIdPattern, key2);
                    }
                    else
                    {
                        // no pattern provided, so accept
                        proceed = true;
                    }
                    
                    if (proceed)
                    {
                        objects.put(key2, cache.get(key2));
                    }
                }
            }
        }
        
        return objects;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#init(org.alfresco.web.framework.ModelPersistenceContext)
     */
    public void init(ModelPersistenceContext context)
    {
        // we require a store to use this persister
        // if a store doesn't exist, then disable persister
        if (!this.store.exists())
        {
            if (logger.isInfoEnabled())
                logger.info("Store missing for persister: " + this.getId());

            this.disable();
            
            return;
        }
        
        this.reset();
        
        if (context == null)
        {
            // dummy context
            context = new ModelPersistenceContext(null);
        }
        
        // walk through all documents
        int count = 0;
        
        List<String> failedPaths = new ArrayList<String>();
        String[] paths = this.store.getAllDocumentPaths();
        for (int i = 0; i < paths.length; i++)
        {
            boolean candidate = true;
            
            String path = paths[i];
            
            String lowerCasePath = path.toLowerCase();
            // if this is a web script descriptor, don't deal with it
            if (lowerCasePath.endsWith(DeclarativeRegistry.WEBSCRIPT_DESC_XML))
            {
                candidate = false;
            }
            else if (!lowerCasePath.endsWith(".xml"))
            {
                candidate = false;
            }
            
            // if this document has the potential to be a Surf model object
            // descriptor file, then try to load it into the cache
            if (candidate)
            {
                try
                {
                    ModelObject obj = this.getObjectByPath(context, path);
                    if (obj != null)
                    {
                        count++;
                    }
                }
                catch (ModelObjectPersisterException mope)
                {
                    // this is not a valid SURF object
                    if (logger.isDebugEnabled())
                    {
                        failedPaths.add(path);
                    }
                }
            }
        }
        
        // output debugging information
        if (logger.isDebugEnabled())
        {
            if (count > 0)
            {
                logger.debug("Store Persister '" + getId() + "' preload count: " + count);
            }
            if (failedPaths.size() != 0)
            {
                logger.debug("Store Persister '" + getId() + "' failed to load the following paths:");
                for (String p : failedPaths)
                {
                    logger.debug("   " + p);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.ModelObjectPersister#reset()
     */
    public void reset()
    {
        this.invalidateCache();
    }
    
    /**
     * Gets the cache for a particular model persistence context
     * 
     * @param context       ModelPersistenceContext
     * @param bucket        Cache bucket to pick
     * 
     * @return the cache
     */
    @Override
    protected ContentCache<ModelObject> getCache(ModelPersistenceContext context, String bucket)
    {
        String storeId = (String)context.getValue(ModelPersistenceContext.REPO_STOREID);
        if (storeId == null)
        {
            String userId = context.getUserId();
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
        
        String key;
        if (storeId != null)
        {
            key = new StringBuilder(100).append(bucket).append(':').append(storeId).toString();            
        }
        else
        {
            key = new StringBuilder(64).append(bucket).append(GLOBAL_STORE_ID_SUFFIX).toString();
        }
        
        ContentCache<ModelObject> cache = null;
        this.cacheLock.readLock().lock();
        try
        {
            cache = this.caches.get(key);
            if (cache == null)
            {
                this.cacheLock.readLock().unlock();
                this.cacheLock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock
                    cache = this.caches.get(key);
                    if (cache == null)
                    {
                        cache = createCache();
                        this.caches.put(key, cache);
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
        
        return cache;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.persister.AbstractCachedObjectPersister#createCache()
     */
    @Override
    protected ContentCache<ModelObject> createCache()
    {
        return new ModelObjectCache(this.store, -1, -1L);
    }
    
    /**
     * Returns an object from the cache
     * 
     * @param context the context
     * @param objectTypeId
     * @param objectId
     * 
     * @return the model object
     */
    protected ModelObject cacheGet(ModelPersistenceContext context, String objectTypeId, String objectId)
    {
        ContentCache<ModelObject> cc = getCache(context, objectTypeId);
        ModelObject mo = cc.get(objectId);
        return mo;
    }
    
    /**
     * Removes an object from the cache
     * 
     * @param context the context
     * @param objectTypeId
     * @param objectId
     */
    protected void cacheRemove(ModelPersistenceContext context, String objectTypeId, String objectId)
    {
        if (this.useCache)
        {
            if (logger.isDebugEnabled())
                logger.debug("Remove from cache: " + objectId);
            
            getCache(context, objectTypeId).remove(objectId);
        }
    }
    
    /**
     * Retrieves an object from the underlying store by path
     * 
     * This performs an interrogation of the underlying document
     * to determine its object type and object id.
     * 
     * @param context
     * @param path
     * @return
     * @throws ModelObjectPersisterException
     */
    protected ModelObject getObjectByPath(ModelPersistenceContext context, String path)
        throws ModelObjectPersisterException    
    {
        // do not process if the persister is disabled
        if (!isEnabled())
        {
            return null;
        }
        
        // additional filtering for support in hot-deploy IDE environments
        if (PathUtil.isDevelopmentEnvironmentPath(path))
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
                // Now make our best effort to parse this into a model object
                // We have to allow for the possibility that the XML is not an Surf Model Object
                
                // parse XML to a Document DOM
                Document document = XMLUtil.parse(this.store.getDocument(path));
                
                // get the object type id described by this document (if possible)
                String objectTypeId = this.getObjectTypeId(document, path);
                if (objectTypeId != null)
                {                
                    // get the object id described by this document (if possible)
                    String objectId = this.getObjectId(document, path);
                    if (objectId != null)
                    {
                        Map<String, ModelObject> map = loadObjectAndDependants(context, document, objectTypeId, objectId, path);
                        
                        // Place the objects into the cache
                        for (final ModelObject o: map.values())
                        {
                            o.touch();
                            getCache(context, o.getTypeId()).put(o.getId(), o);
                        }
                        
                        obj = map.get(objectId);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw new ModelObjectPersisterException("Failure to load model object for path: " + path, ex);
        }
        
        return obj;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<String, ModelObject> loadObjectAndDependants(ModelPersistenceContext context, Document document, String objectTypeId, String objectId, String path)
        throws ModelObjectPersisterException
    {
        Map<String, ModelObject> map = new LinkedHashMap<String, ModelObject>();
        
        // instantiate object
        ModelObject obj = createObject(document, objectTypeId, objectId, path);
        if (obj != null)
        {
            map.put(obj.getId(), obj);
            
            // see if there are any components bound into this object
            Element componentsElement = document.getRootElement().element("components");
            if (componentsElement != null)
            {
                List componentElements = componentsElement.elements("component");
                for (int i = 0; i < componentElements.size(); i++)
                {
                    Element componentElement = (Element) componentElements.get(i);
                    
                    String regionId = (String) componentElement.elementText(Component.PROP_REGION_ID);
                    String url = (String) componentElement.elementText(Component.PROP_URL);
                    String uri = (String) componentElement.elementText(Component.PROP_URI); // Also get uri to ensure consistency between component types.
                    String componentTypeId = (String) componentElement.elementText(Component.PROP_COMPONENT_TYPE_ID);
                    
                    String chromeId = (String) componentElement.elementText(Component.PROP_CHROME);
                    String title = (String) componentElement.elementText(Component.PROP_TITLE);
                    String description = (String) componentElement.elementText(Component.PROP_DESCRIPTION);
                    
                    // determine the scope
                    String scope = null;                
                    if ("page".equals(objectTypeId))
                    {
                        scope = "page";
                    }
                    if ("template-instance".equals(objectTypeId))
                    {
                        scope = "template";
                    }
                    
                    // flag component for update
                    if (scope != null)
                    {                
                        // create a component
                        String componentObjectId = ModelHelper.newGUID();
                        Component component = (Component) newObject(context, "component", componentObjectId);
                        
                        // If a Component was defined inside another object then we need to set its
                        // sourcePath to indicate the location where it was originally defined. This
                        // is done for debugging purposes.
                        component.getKey().setSourcePath(this.store.getBasePath() + "/" + path);

                        component.setRegionId(regionId);
                        component.setSourceId(obj.getId());
                        component.setScope(scope);
                        component.setURL(url);
                        component.setURI(uri); // Also set the UI to ensure consistency between component types
                        component.setComponentTypeId(componentTypeId);
                        
                        component.setChrome(chromeId);
                        component.setTitle(title);
                        component.setDescription(description);

                        // add the custom properties
                        Element properties = componentElement.element("properties");
                        if (properties != null)
                        {
                            Iterator<Element> itr = properties.elementIterator();
                            while (itr.hasNext())
                            {
                                Element prop = itr.next();
                                component.setCustomProperty(prop.getName(), prop.getText());
                            }
                        }
                        
                        // Check to see if this is an AdvancedComponent (which we do by checking for a renderable-elements
                        // element. If this advanced configuration is found then it is applied into the Component.
                        if (component instanceof AdvancedComponent)
                        {
                            Element renderableElementsEl = componentElement.element(AdvancedComponent.SUB_COMPONENTS);
                            if (renderableElementsEl != null)
                            {
                                ((AdvancedComponent) component).applyConfig(componentElement);
                            }
                        }
                        
                        map.put(component.getId(), component);
                    }
                }
            }
            
            // see if there are any associations bound into this object
            Element associationsElement = document.getRootElement().element("associations");
            if (associationsElement != null)
            {
                if ("page".equals(objectTypeId))
                {
                    List pageAssociationElements = associationsElement.elements("page-association");
                    for (int i = 0; i < pageAssociationElements.size(); i++)
                    {
                        Element pageAssociationElement = (Element) pageAssociationElements.get(i);
                        
                        String destId = (String) pageAssociationElement.elementText(PageAssociation.PROP_DEST_ID);
                        String assocType = (String) pageAssociationElement.elementText(PageAssociation.PROP_ASSOC_TYPE);
                        
                        String orderId = (String) pageAssociationElement.elementText(PageAssociation.PROP_ORDER_ID);

                        // create a page association
                        String pageAssociationObjectId = ModelHelper.newGUID();
                        PageAssociation pageAssociation = (PageAssociation) newObject(context, "page-association", pageAssociationObjectId);
                        
                        pageAssociation.setDestId(destId);
                        pageAssociation.setAssociationType(assocType);
                        pageAssociation.setSourceId(objectId);
                        
                        pageAssociation.setOrderId(orderId);
                        
                        map.put(pageAssociation.getId(), pageAssociation);
                    }
                }
            }            
        }
        return map;
    }
    
    protected ModelObject createObject(Document document, String objectTypeId, String objectId, String path)
    {
        // To create a ModelObject we will associate the supplied Document with some generated
        // ModelPersisterInfo. However, we need to make sure that the Document supplied really
        // does contain the type of data that we're expecting. The simplest way to check this
        // is to check that the root element of the document does indeed match the expected
        // object type. If it does not, we will not create the object as this would be invalid.
        ModelObject obj = null;
        Element e = document.getRootElement();
        String name = e.getName();
        if (name.equals(objectTypeId))
        {
            ModelPersisterInfo info = new ModelPersisterInfo(getId(), path, true);
            String implClassName = getWebFrameworkConfiguration().getTypeDescriptor(objectTypeId).getImplementationClass();
            obj = (ModelObject) ReflectionHelper.newObject(
                    implClassName,
                    MODELOBJECT_CLASSES,
                    new Object[] { objectId, info, (Document)document }
            );       
        }
        else
        {
            // The document does not contain the correct data. 
        }        
        return obj;
    }
    
    /**
     * Determines the object type id of a serialized model object
     * contained in a document.  This will assert that the discovered
     * object type is a valid, registered object type.  If the object
     * type is invalid, null will be returned.
     * 
     * @param doc document
     * @param path the path
     * 
     * @return object type id (or null if it is not a model object)
     */
    protected String getObjectTypeId(Document doc, String path)
    {
        String objectTypeId = null;

        if (doc != null && doc.getRootElement() != null)
        {
            objectTypeId = doc.getRootElement().getName();
        }
        
        // ensure that the object type is valid
        if (objectTypeId != null)
        {
            if (getWebFrameworkConfiguration().getTypeDescriptor(objectTypeId) == null)
            {
                objectTypeId = null;
            }
        }
        
        return objectTypeId;
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
    protected String getObjectId(Document doc, String path)
    {
        String id = doc.getRootElement().elementText("id");
        if (id == null && path != null)
        {
            path = path.replace('\\', '/');

            int i = path.lastIndexOf('/');
            if (i > -1)
            {
                id = path.substring(i+1);
            }
            else
            {
                id = path;
            }

            if (id.endsWith(".xml"))
            {
                id = id.substring(0, id.length() - 4);
            }
        }
        
        return id;
    }
    
    /**
     * Determines whether the xml contained in the given document
     * describes a valid model object type
     * 
     * @param doc document
     * @param path the path
     * 
     * @return whether this describes a valid model object type
     */
    protected boolean isModelObject(Document doc, String path)
    {
        return (getObjectTypeId(doc, path) != null);
    }
    
    @Override
    public String toString()
    {
        return getClass().getName() + " ID: " + getId() + " PathPrefix: " + pathPrefix + " Store: " + store.toString();
    }
}
