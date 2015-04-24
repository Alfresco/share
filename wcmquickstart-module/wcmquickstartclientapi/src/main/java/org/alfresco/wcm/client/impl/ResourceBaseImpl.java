/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.Resource;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * Resource interface implementation
 * 
 * @author Roy Wetherall
 * @author Brian
 */
public abstract class ResourceBaseImpl implements Resource 
{	
    private static final long serialVersionUID = 2137271248424335766L;
    
    private Map<String, Serializable> properties = new TreeMap<String,Serializable>();
    private SectionFactory sectionFactory;
    private AssetFactory assetFactory;
    private CollectionFactory collectionFactory;

    private String primarySectionId;

    private String id;
    private String typeId;
    private String name;

	public ResourceBaseImpl() 
	{
	}

	/**
	 * Set resources properties
	 * @param props		property map
	 */
	public void setProperties(Map<String,Serializable> props)
	{
	    properties = new TreeMap<String, Serializable>(props);
	    mimicCmisProperties();
	    id = (String)properties.get(PropertyIds.OBJECT_ID);
	    typeId = (String)properties.get(PropertyIds.OBJECT_TYPE_ID);
	    name = (String)properties.get(PropertyIds.NAME);
	    properties = Collections.unmodifiableMap(properties);
	}
	
    /**
	 *  @see org.alfresco.wcm.client.Resource#getId()
	 */
	@Override
	public String getId() 
	{
		return id;
	}
	
	/**
	 *  @see org.alfresco.wcm.client.Resource#getName()
	 */	
	@Override
	public String getName() 
	{
		return name;
	}

	/**
	 *  @see org.alfresco.wcm.client.Resource#getTitle()
	 */	
	@Override
	public String getTitle() 
	{
		return (String)properties.get(PROPERTY_TITLE);
	}

    /**
     * @see org.alfresco.wcm.client.Resource#getType()
     */
    @Override
    public String getType()
    {
        return (String)properties.get(PropertyIds.OBJECT_TYPE_ID);
    }
    
	/**
	 *  @see org.alfresco.wcm.client.Resource#getDescription()
	 */	
	@Override
	public String getDescription() 
	{
		return (String)properties.get(PROPERTY_DESCRIPTION);
	}

	/**
	 *  @see org.alfresco.wcm.client.Resource#getContainingSection()
	 */	
	@Override
	public Section getContainingSection() 
	{
        Section section = (primarySectionId == null) ? null : getSectionFactory().getSection(primarySectionId);
        return section;
	}

	/**
	 *  @see org.alfresco.wcm.client.Resource#getProperty()
	 */
	@Override
	public Serializable getProperty(String propertyName) 
	{
		return properties.get(propertyName);
	}
	
	/**
	 *  @see org.alfresco.wcm.client.Resource#getProperties()
	 */
	@Override
	public Map<String, Serializable> getProperties() 
	{
		return properties;
	}

    public SectionFactory getSectionFactory()
    {
        return sectionFactory;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }

    public AssetFactory getAssetFactory()
    {
        return assetFactory;
    }

    public void setAssetFactory(AssetFactory resourceFactory)
    {
        this.assetFactory = resourceFactory;
    }

    public CollectionFactory getCollectionFactory()
    {
        return collectionFactory;
    }

    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }
	
    public void setPrimarySectionId(String sectionId)
    {
        this.primarySectionId = sectionId;
    }

    public String getPrimarySectionId()
    {
        return primarySectionId;
    }

    private void mimicCmisProperties()
    {
        //Quickly check that there doesn't appear to be any CMIS properties defined already
        if (properties.get(PropertyIds.OBJECT_ID) == null)
        {
            //And, if not, derive the CMIS equivalents from what we have
            properties.put(PropertyIds.OBJECT_ID, properties.get("id"));
            
            //Translate the root types to their CMIS equivalent...
            String typeName = (String)properties.get("type");
            if ("cm:content".equals(typeName))
            {
                typeName = DictionaryService.TYPE_CMIS_DOCUMENT;
            }
            else if ("cm:folder".equals(typeName))
            {
                typeName = DictionaryService.TYPE_CMIS_FOLDER;
            }
            properties.put(PropertyIds.OBJECT_TYPE_ID, typeName);
    
            properties.put(PropertyIds.NAME, properties.get("cm:name"));
            properties.put(PropertyIds.LAST_MODIFICATION_DATE, properties.get("cm:modified"));
            ContentInfo contentInfo = (ContentInfo) properties.get("cm:content");
            if (contentInfo != null)
            {
                properties.put(PropertyIds.CONTENT_STREAM_LENGTH, BigInteger.valueOf(contentInfo.getSize()));
                properties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, contentInfo.getMimeType());
            }
        }
    }
}

