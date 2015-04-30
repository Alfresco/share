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

package org.springframework.extensions.surf.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.ModelHelper;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.resource.ModelObjectResourceProvider;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.resource.ResourceProvider;
import org.springframework.extensions.surf.util.XMLUtil;

/**
 * Abstract base class that can be extended to introduce custom model
 * objects into the framework.  Custom model objects must be registered
 * with the configuration file.  Once done, they can be loaded and
 * persisted along with other model objects.
 * <p>
 * All model classes extending from this class are expected to have
 * "id", "title" and "description" fields.
 * 
 * @author muzquiano
 * @author Kevin Roast
 */
public abstract class AbstractModelObject implements ModelObject, ResourceProvider
{
    private static final long serialVersionUID = 832501982131108977L;

    private static final String CONTAINER_PROPERTIES = "properties";
    private static final String CONTAINER_RESOURCES = "resources";
    
    /** internal serialised version of the ModelObject as XML */
    private String documentXML;
    
    protected final ModelPersisterInfo info;
    protected String id;
    
    protected long modificationTime;
    
    protected Map<String, Serializable> modelProperties;    
    protected Map<String, Serializable> customProperties;
    
    // cached values
    protected String title;
    protected String titleId;
    protected String description;
    protected String descriptionId;
    
    // resources
    protected ResourceProvider resourceContainer = null;
    
    /** Lock object for property map construction */
    private ReadWriteLock propertyLock = new ReentrantReadWriteLock();
    
    
    /**
     * Constructs a new model object
     * 
     * @param document the document
     */
    public AbstractModelObject(String id, ModelPersisterInfo info, Document document)
    {
        this.info = info;
        updateXML(document);
        this.id = id;
    }

    /**
     * Constructor used by sentinel object
     */
    protected AbstractModelObject()
    {
        this.info = null;
        this.documentXML = null;
        this.id = null;
    }
    
    /**
     * Method to be used by sub-classes to update the internal serialised representation
     * of the ModelObject from a given XML DOM. The ModelObject itself only ever maintains
     * a serialised XML representation to reduce memory usage as many 10000's of ModelObject
     * instances are stored in the various persister caches.
     * 
     * @param document      Document for XML serialisation
     */
    protected void updateXML(Document document)
    {
        this.documentXML = XMLUtil.toXML(document);
    }

    /**
     * @return the structure that represents the persistence information for this model object
     */
    public final ModelPersisterInfo getKey()
    {
        return this.info;
    }


    ///////////////////////////////////////////////////////////////
    // Common model properties
    ///////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getId()
     */
    public final String getId()
    {
        return this.id;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTitle()
     */
    public final String getTitle()
    {
        if (this.title == null)
        {
            this.title = getProperty(PROP_TITLE);
        }
        return this.title;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTitleId()
     */
    public final String getTitleId()
    {
        if (this.titleId == null)
        {
            this.titleId = getProperty(PROP_TITLE_ID);
        }
        return this.titleId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setTitle(java.lang.String)
     */
    public final void setTitle(String title)
    {
        setProperty(PROP_TITLE, title);
        this.title = title;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setTitleId(java.lang.String)
     */
    public final void setTitleId(String titleId)
    {
        setProperty(PROP_TITLE_ID, titleId);
        this.titleId = titleId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getDescription()
     */
    public final String getDescription()
    {
        if (this.description == null)
        {
            this.description = getProperty(PROP_DESCRIPTION);
        }
        return this.description;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getDescriptionId()
     */
    public final String getDescriptionId()
    {
        if (this.descriptionId == null)
        {
            this.descriptionId = getProperty(PROP_DESCRIPTION_ID);
        }
        return this.descriptionId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setDescription(java.lang.String)
     */
    public final void setDescription(String value)
    {
        setProperty(PROP_DESCRIPTION, value);
        this.description = value;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setDescriptionId(java.lang.String)
     */
    public final void setDescriptionId(String value)
    {
        setProperty(PROP_DESCRIPTION_ID, value);
        this.descriptionId = value;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#isSaved()
     */
    public final boolean isSaved()
    {
        return this.info.isSaved();
    }
    
    
    ///////////////////////////////////////////////////////////////
    // XML methods
    ///////////////////////////////////////////////////////////////    

    /**
     * Return the representation of this Model Object as an XML DOM.
     * <p>
     * Note that this Document is transient and created new each time this method is
     * called from the internal serialised representation. Therefore changes to the
     * ModelObject must be persisted back through the various setProperty() methods
     * or the serialised XML manually updated by an appropriate sub-class method.
     */
    public final Document getDocument()
    {
        try
        {
            return XMLUtil.parse(this.documentXML);
        }
        catch (DocumentException err)
        {
            throw new IllegalStateException("Unable to parse ModelObject XML content: " + this.documentXML);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#toXML()
     */
    public final String toXML()
    {
        return this.documentXML;
    }
    
    
    ///////////////////////////////////////////////////////////////
    // Generic property accessors
    ///////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getBooleanProperty(java.lang.String)
     */
    public final boolean getBooleanProperty(String propertyName)
    {
        String val = getProperty(propertyName);
        return Boolean.parseBoolean(val);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getProperty(java.lang.String)
     */
    public final String getProperty(String propertyName)
    {
        if (isModelProperty(propertyName))
        {
            return getModelProperty(propertyName);
        }
        else
        {
            return getCustomProperty(propertyName);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setProperty(java.lang.String, java.lang.String)
     */
    public final void setProperty(String propertyName, String propertyValue)
    {
        if (isModelProperty(propertyName))
        {
            setModelProperty(propertyName, propertyValue);
        }
        else
        {
            setCustomProperty(propertyName, propertyValue);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#removeProperty(java.lang.String)
     */
    public final void removeProperty(String propertyName)
    {
        if (isModelProperty(propertyName))
        {
            removeModelProperty(propertyName);
        }
        else
        {
            removeCustomProperty(propertyName);
        }
    }
    
    /**
     * Uses reflection to determine whether the given property name
     * is a custom property.  A custom property is a non-model-specific
     * property.  Custom properties are written under the <properties/>
     * container element in the XML.
     * 
     * @param propertyName the property name
     * 
     * @return true, if checks if is custom property
     */
    protected final boolean isCustomProperty(String propertyName)
    {
        return (!isModelProperty(propertyName));        
    }
    
    /**
     * Uses reflection to determine whether the given property name
     * is a model property.  Model properties are written directly
     * under the root element of the XML document.
     * 
     * @param propertyName the property name
     * 
     * @return true, if checks if is model property
     */
    protected final boolean isModelProperty(String propertyName)
    {
        return ModelHelper.isModelProperty(this, propertyName);
    }
    
    
    ////////////////////////////////////////////////////////////
    // Model Properties
    ////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelProperty(java.lang.String)
     */
    public final String getModelProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        Map<String, Serializable> properties = getModelProperties();
        this.propertyLock.readLock().lock();
        try
        {
            return (String)properties.get(propertyName);
        }
        finally
        {
            this.propertyLock.readLock().unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setModelProperty(java.lang.String, java.lang.String)
     */
    public final void setModelProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        // if the propertyValue is null, remove the property
        if (propertyValue == null)
        {
            removeModelProperty(propertyName);
            return;
        }
        
        // do the set
        Map<String, Serializable> properties = getModelProperties();
        this.propertyLock.writeLock().lock();
        try
        {
            Document document = getDocument();
            Element el = document.getRootElement().element(propertyName);
            if (el == null)
            {
                el = document.getRootElement().addElement(propertyName);
            }
            
            // put value
            el.setText(propertyValue);
            
            // update caches
            properties.put(propertyName, propertyValue);
            updateXML(document);
        }
        finally
        {
            this.propertyLock.writeLock().unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#removeModelProperty(java.lang.String)
     */
    public final void removeModelProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        // do the remove
        Map<String, Serializable> properties = getModelProperties();
        this.propertyLock.writeLock().lock();
        try
        {
            Document document = getDocument();
            Element el = document.getRootElement().element(propertyName);
            if (el != null)
            {
                document.getRootElement().remove(el);
                
                // update the caches
                properties.remove(propertyName);
                updateXML(document);
            }
        }
        finally
        {
            this.propertyLock.writeLock().unlock();
        }
    }

    
    ////////////////////////////////////////////////////////////
    // Custom Properties
    ////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getCustomProperty(java.lang.String)
     */
    public final String getCustomProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        Map<String, Serializable> properties = getCustomProperties();
        this.propertyLock.readLock().lock();
        try
        {
            return (String)properties.get(propertyName);
        }
        finally
        {
            this.propertyLock.readLock().unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setCustomProperty(java.lang.String, java.lang.String)
     */
    public final void setCustomProperty(String propertyName, String propertyValue)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        // if the propertyValue is null, remove the property
        if (propertyValue == null)
        {
            removeCustomProperty(propertyName);
            return;
        }
        
        // do the set
        Map<String, Serializable> props = getCustomProperties();
        this.propertyLock.writeLock().lock();
        try
        {
            Document document = getDocument();
            Element properties = document.getRootElement().element(CONTAINER_PROPERTIES);
            if (properties == null)
            {
                properties = document.getRootElement().addElement(CONTAINER_PROPERTIES);
            }
            
            Element el = properties.element(propertyName);
            if (el == null)
            {
                el = properties.addElement(propertyName);
            }
            
            // put value
            el.setText(propertyValue);
            
            // update the caches
            props.put(propertyName, propertyValue);
            updateXML(document);
        }
        finally
        {
            this.propertyLock.writeLock().unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#removeCustomProperty(java.lang.String)
     */
    public final void removeCustomProperty(String propertyName)
    {
        if (propertyName == null)
        {
            throw new IllegalArgumentException("Property Name is mandatory.");
        }
        
        Map<String, Serializable> props = getCustomProperties();
        this.propertyLock.writeLock().lock();
        try
        {
            // do the remove
            Document document = getDocument();
            Element properties = document.getRootElement().element(CONTAINER_PROPERTIES);
            if (properties != null)
            {
                Element el = properties.element(propertyName);
                if (el != null)
                {
                    properties.remove(el);
                    
                    // update the caches
                    props.remove(propertyName);
                    updateXML(document);
                }
            }
        }
        finally
        {
            this.propertyLock.writeLock().unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getProperties()
     */
    public Map<String, Serializable> getProperties()
    {
        Map<String, Serializable> properties = new HashMap<String, Serializable>(16, 1.0f);
        properties.putAll(getModelProperties());
        properties.putAll(getCustomProperties());
        return properties;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModelProperties()
     */
    @SuppressWarnings("rawtypes")
    public final Map<String, Serializable> getModelProperties()
    {
        this.propertyLock.readLock().lock();
        try
        {
            if (this.modelProperties == null)
            {
                // property structure has not been built yet - lock while we build it
                this.propertyLock.readLock().unlock();
                this.propertyLock.writeLock().lock();
                try
                {
                    // check again as multiple threads could have been waiting on the write lock
                    if (this.modelProperties == null)
                    {
                        Map<String, Serializable> modelProperties = new HashMap<String, Serializable>(8, 1.0f);
                        
                        final List elements = getDocument().getRootElement().elements();
                        for (int i = 0; i < elements.size(); i++)
                        {
                            final Element el = (Element) elements.get(i);
                            String elementName = el.getName();
                            if (elementName != null)
                            {
                                if (!CONTAINER_PROPERTIES.equals(elementName) && !CONTAINER_RESOURCES.equals(elementName))
                                {
                                    String elementValue = el.getStringValue();
                                    modelProperties.put(elementName, elementValue);
                                }
                            }
                        }
                        
                        this.modelProperties = modelProperties;
                    }
                }
                finally
                {
                    this.propertyLock.readLock().lock();
                    this.propertyLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.propertyLock.readLock().unlock();
        }
        return this.modelProperties;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getCustomProperties()
     */
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> getCustomProperties()
    {
        this.propertyLock.readLock().lock();
        try
        {
            if (this.customProperties == null)
            {
                // property structure has not been built yet - lock while we build it
                this.propertyLock.readLock().unlock();
                this.propertyLock.writeLock().lock();
                try
                {
                    // check again as multiple threads could have been waiting on the write lock
                    if (this.customProperties == null)
                    {
                        Map<String, Serializable> customProperties = new HashMap<String, Serializable>(4, 1.0f);
                        
                        Element properties = getDocument().getRootElement().element(CONTAINER_PROPERTIES);
                        if (properties != null)
                        {
                            List<Element> elements = properties.elements();
                            for (int i = 0; i < elements.size(); i++)
                            {
                                Element el = elements.get(i);
                                customProperties.put(el.getName(), el.getTextTrim());
                            }
                        }
                        
                        this.customProperties = customProperties;
                    }
                }
                finally
                {
                    this.propertyLock.readLock().lock();
                    this.propertyLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.propertyLock.readLock().unlock();
        }
        return this.customProperties;
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getModificationTime()
     */
    public final long getModificationTime()
    {
        return this.modificationTime;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#setModificationTime(long)
     */
    public final void setModificationTime(long modificationTime)
    {
        this.modificationTime = modificationTime;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#touch()
     */
    public final void touch()
    {
        setModificationTime(System.currentTimeMillis());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.ModelObject#getTypeId()
     */
    public abstract String getTypeId();
        
       
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getTypeId() + ": " + getId() + ", " + toXML();
    }
    
    /**
     * Returns the ModelObjectPersister id that this object is bound to
     */
    public final String getPersisterId()
    {
        return this.info.getPersisterId();
    }
    
    /**
     * Returns the persistence storage path of this object 
     */
    public final String getStoragePath()
    {
        return this.info.getStoragePath();
    }
    
    // resource provider methods
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContainer#getResource(java.lang.String)
     */
    public Resource getResource(String name)
    {
        return getResourceContainer().getResource(name);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContainer#getResources()
     */
    public Resource[] getResources()
    {
        return getResourceContainer().getResources();
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContainer#getResourcesMap()
     */
    public Map<String, Resource> getResourcesMap()
    {
        return getResourceContainer().getResourcesMap();
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("deprecation")
    public Resource addResource(String name, String resourceId)
    {
        String[] ids = FrameworkBean.getResourceService().getResourceDescriptorIds(resourceId);
        return getResourceContainer().addResource(name, ids[0], ids[1], ids[2]);        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#addResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Resource addResource(String name, String protocolId, String endpointId, String objectId)
    {
        return getResourceContainer().addResource(name, protocolId, endpointId, objectId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#updateResource(java.lang.String, org.alfresco.web.framework.resource.Resource)
     */
    public void updateResource(String name, Resource resource)
    {
        getResourceContainer().updateResource(name, resource);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceProvider#removeResource(java.lang.String)
     */
    public void removeResource(String name)
    {
        getResourceContainer().removeResource(name);        
    }
    
    protected synchronized ResourceProvider getResourceContainer()
    {
        if (this.resourceContainer == null)
        {
            this.resourceContainer = new ModelObjectResourceProvider(this);
        }
        return this.resourceContainer;
    }    
    
    /**
     * Allows for reassignment of the id of the object
     * 
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }
}
