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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.cache.ContentCache;
import org.springframework.extensions.surf.cache.ModelObjectCache;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.util.ReflectionHelper;
import org.springframework.extensions.surf.util.XMLUtil;
import org.springframework.extensions.webscripts.DeclarativeRegistry;
import org.springframework.extensions.webscripts.ui.common.StringUtils;

/**
 * Provides a base implementation of a persister on-top of a Web Script store.
 * <p>
 * Unlike the read-only implementation, this implementation does not assume
 * the cache as the "master copy" of the data.  Rather, the "master copy" is
 * considered to be the store itself.  Any put or remote based interactions
 * with persisted content are delegated to the store itself. All get operations
 * utilize the cache for performance benefits, with a configured timeout to
 * reintegrogate last modified dates of actual store content.
 * <p>
 * Since stores are based on file-path semantics, this persister must make
 * assumptions on how to map ids to to file-paths in order to allow for lazy
 * referencing and lazy loading of objects.
 * <p>
 * The id to file-path and file-path to id conversion methods are split out so
 * that they can be adjusted in inheriting implementations.
 * <p>
 * In addition, path prefixes support token replacement for object type ids
 * and object ids.
 * 
 * @author muzquiano
 * @author kevinr
 */
public abstract class AbstractStoreObjectPersister extends ReadOnlyStoreObjectPersister
{    
    private static Log logger = LogFactory.getLog(AbstractStoreObjectPersister.class);
    
    protected static final String XML_EXT = ".xml";
    
    // tokens used to replace known object type IDs in the general persister config
    // for example /WEB-INF/surf/${objectTypeIds} -> /WEB-INF/surf/pages
    private static final String OBJECT_TYPE_IDS = "${objectTypeIds}";
    private static final String OBJECT_TYPE_ID = "${objectTypeId}";
    private static final String OBJECT_ID = "${objectId}";
    private static final String PLURAL_S = "s";
    private static final String CHROME = "chrome";
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.persister.ReadOnlyStoreObjectPersister#generatePath(java.lang.String, java.lang.String)
     */
    @Override
    protected String generatePath(String objectTypeId, String objectId)
    {
        String path = null;
        
        String prefix = getPathPrefix(objectTypeId, objectId);
        if (prefix == null)
        {
            path = new StringBuilder(objectId.length() + 4).append(objectId).append(XML_EXT).toString();
        }
        else
        {
            path = new StringBuilder(prefix.length() + objectId.length() + 5).append(prefix).append('/').append(objectId).append(XML_EXT).toString();
        }
        
        return path;
    }
    
    /**
     * Gets the path prefix for the given token replacements.
     * 
     * @param objectTypeId the object type id
     * @param objectId the object id
     * 
     * @return the path prefix or null if not provided
     */
    public String getPathPrefix(String objectTypeId, String objectId)
    {
        String prefix = null;
        
        if (this.pathPrefix != null)
        {
            prefix = this.pathPrefix;
            
            // token replacement
            if (prefix.indexOf(OBJECT_TYPE_ID) != -1)
            {
                prefix = StringUtils.replace(prefix, OBJECT_TYPE_ID, objectTypeId);
            }
            else if (prefix.indexOf(OBJECT_ID) != -1)
            {
                prefix = StringUtils.replace(prefix, OBJECT_ID, objectId);
            }
            // plurality i.e. "pages" folder contains the "page" object type
            else if (prefix.indexOf(OBJECT_TYPE_IDS) != -1)
            {
            	String p = objectTypeId + PLURAL_S;
            	// special case for the "chrome" type - is not plural
            	if (CHROME.equals(objectTypeId))
            	{
            		p = objectTypeId;
            	}
            	prefix = StringUtils.replace(prefix, OBJECT_TYPE_IDS, p);
            }
            
            if (prefix.endsWith("/"))
            {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        
        return prefix;
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public boolean removeObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException    
    {
        // do not process if the persister is disabled
        if (!isEnabled())
        {
            return false;
        }
        
        boolean removed = false;
        
        // determine the path of this object in the store
        String path = generatePath(objectTypeId, objectId);
        
        // check whether the store contains this document
        try
        {
            if (this.store.hasDocument(path))
            {
                // remove document from store
                removed = this.store.removeDocument(path);
            }
            
            // always remove document from cache to ensure in sync - even if missing from underlying store
            if (cacheGet(context, objectTypeId, objectId) != null)
            {
                cacheRemove(context, objectTypeId, objectId);
                removed = true;
            }
        }
        catch (IOException ex)
        {
            throw new ModelObjectPersisterException("Unable to remove object for path: " + path);
        }
        
        return removed;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public boolean hasObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException  
    {
        // do not process if persister is disabled
        if (!isEnabled())
        {
            return false;
        }
        
        // determine the path of this object in the store
        String path = generatePath(objectTypeId, objectId);        
        
        try
        {
            return this.store.hasDocument(path);
        }
        catch (IOException ioe)
        {
            throw new ModelObjectPersisterException("Unable to test object at path: " + path, ioe);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#newObject(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    @Override
    public ModelObject newObject(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException
    {
        return newObject(context, objectTypeId, objectId, true);
    }
    
    protected ModelObject newObject(ModelPersistenceContext context, String objectTypeId, String objectId, boolean addToCache)
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
                
                if (addToCache)
                {
                    cachePut(context, obj);
                }
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
     * @see org.alfresco.web.framework.ModelObjectPersister#getTimestamp(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public long getTimestamp(ModelPersistenceContext context, String objectTypeId, String objectId)
        throws ModelObjectPersisterException
    {
        // do not process if persister is disabled
        if (!isEnabled())
        {
            return -1;
        }
        
        // determine the path of this object in the store
        String path = generatePath(objectTypeId, objectId);        

        long timestamp = -1;
        
        try
        {
            timestamp = this.store.lastModified(path);
        }
        catch (IOException ioe)
        {
            throw new ModelObjectPersisterException("Unable to check timestamp for object path: " + path, ioe);
        }    
        
        return timestamp;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getAllObjects(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String)
     */
    public Map<String, ModelObject> getAllObjects(ModelPersistenceContext context, String objectTypeId)
        throws ModelObjectPersisterException
    {
        String[] docPaths = this.store.getAllDocumentPaths();
        
        return getObjectsFromPaths(context, objectTypeId, docPaths);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getAllObjectsByFilter(org.alfresco.web.framework.ModelPersistenceContext, java.lang.String, java.lang.String)
     */
    public Map<String, ModelObject> getAllObjectsByFilter(ModelPersistenceContext context, String objectTypeId, String objectIdFilter)
        throws ModelObjectPersisterException
    {
        // do not process if persister is disabled
        if (!isEnabled())
        {
            // empty map
            return new HashMap<String, ModelObject>();
        }
        
        // determine the path of this object in the store
        String path = generatePath(objectTypeId, objectIdFilter);        
        
        // document paths
        try
        {
            String[] docPaths = this.store.getDocumentPaths("", true, path);
            
            // trim down document paths to remove any stuff we know we don't want
            ArrayList<String> array = new ArrayList<String>(docPaths.length);
            for (String docPath: docPaths)
            {
                if (!docPath.endsWith(DeclarativeRegistry.WEBSCRIPT_DESC_XML))
                {
                    array.add(docPath);
                }
            }
            
            // convert to objects
            String[] newDocPaths = array.toArray(new String[array.size()]);
            return getObjectsFromPaths(context, objectTypeId, newDocPaths);
        }
        catch (IOException e)
        {
            throw new ModelObjectPersisterException("Failed to get objects by filter: " + objectIdFilter, e);
        }
    }

    /**
     * @param context       ModelPersistenceContext
     * @param objectTypeId  object type id
     * @param docPaths      Array of document paths
     * 
     * @return map of IDs to model objects
     * 
     * @throws ModelObjectPersisterException
     */
    protected Map<String, ModelObject> getObjectsFromPaths(ModelPersistenceContext context, String objectTypeId, String[] docPaths)
        throws ModelObjectPersisterException
    {
        Map<String, ModelObject> objects = new HashMap<String, ModelObject>(docPaths.length, 1.0f);
        for (int i = 0; i < docPaths.length; i++)
        {
            // load object from path
            // this will retrieve from cache, if possible
            try
            {
                ModelObject object = getObjectByPath(context, docPaths[i]);
                
                // place into collected map
                if (object != null && object.getTypeId().equals(objectTypeId))
                {
                    objects.put(object.getId(), object);
                }
            }
            catch (ModelObjectPersisterException err)
            {
                // if this occurs, it means the XML couldn't parse - log this
                logger.warn("Failure to load model object for path: " + docPaths[i], err);
            }
        }
        return objects;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.persister.AbstractCachedObjectPersister#createCache()
     */
    @Override
    protected ContentCache<ModelObject> createCache()
    {
        return new ModelObjectCache(this.store, this.cacheMaxSize, this.cacheDelay);
    }
}
