/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Path;
import org.alfresco.wcm.client.PathResolutionDetails;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.UgcService;
import org.alfresco.wcm.client.WebSite;

/**
 * Web Site Implementation
 * 
 * @author Roy Wetherall
 */
public class WebSiteImpl implements WebSite
{
	private static final long serialVersionUID = 1L;

	private String id;
	
	/** Host name */
	private String hostName;
	
	/** Context **/
	private String context;
	
	/** Host port */
	private int hostPort;
	
	/** Site title */
	private String title;
	
	/** Site description */
	private String description;

	/** Logo */
	private Asset logo;
	
	private transient SectionFactory sectionFactory;
	private transient UgcService ugcService;

    private String rootSectionId;

    private Map<String, String> configMap;

    private Boolean editorial;

	/**
	 * Constructor 
	 * 
	 * @param id		id 
	 * @param hostName  host name
	 * @param hostPort  host port
	 */
	public WebSiteImpl(String id, String hostName, int hostPort, int sectonsRefreshAfter)
	{
		this.id = id;
		this.rootSectionId = id;
		this.hostName = hostName;
		this.hostPort = hostPort;		
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getHostName()
	 */
	@Override
	public String getHostName() 
	{
		return hostName;
	}

	/**
	 * @see org.alfresco.wcm.client.WebSite#getHostPort()
	 */
	@Override
	public int getHostPort()
	{
		return hostPort;
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getContext()
	 */
	@Override	
	public String getContext()
	{
		return context;
	}
	
	public void setContext(String context) 
	{
		this.context = context;
	}
	
	public String getId()
    {
        return id;
    }

    /**
	 * @see org.alfresco.wcm.client.WebSite#getRootSection()
	 */
	@Override
	public Section getRootSection() 
	{
		return getSectionByPath("");
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getSections()
	 */
	@Override
	public List<Section> getSections() 
	{ 
		return getRootSection().getSections();	
	}	
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getAssetByPath(java.lang.String)
	 */
	@Override
	public Asset getAssetByPath(String path)
	{
	    PathResolutionDetails resolution = getRootSection().resolvePath(path);
	    return resolution.getAsset();
	}
	
	/**
	 * @see org.alfresco.wcm.client.WebSite#getSectionByPath(java.lang.String)
	 */
	@Override
	public Section getSectionByPath(String path)
	{
		Path segmentedPath = new PathImpl(path); 
		String[] sectionPath = segmentedPath.getPathSegments();
		
		return sectionFactory.getSectionFromPathSegments(rootSectionId, sectionPath);
	}	
	
	public void setSectionFactory(SectionFactory sectionFactory) 
	{
		this.sectionFactory = sectionFactory; 
	}

    @Override
    public UgcService getUgcService()
    {
        return ugcService;
    }

    public void setUgcService(UgcService ugcService)
    {
        this.ugcService = ugcService;
    }
    
	@Override
    public String getDescription()
    {
	    return description;
    }
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@Override
    public String getTitle()
    {
	    return title;
    }

	public void setTitle(String title) 
	{
		this.title = title;
	}

	@Override
	public Asset getLogo() {
		return logo;
	}
	
	public void setLogo(Asset logo)
    {
		this.logo = logo;
    }
	
    public void setRootSectionId(String rootSectionId)
    {
        this.rootSectionId = rootSectionId;
    }

    public void setConfig(Map<String, String> configProperties)
    {
        configMap = configProperties;
    }
    
    @Override
    public Map<String,String> getConfig()
    {
        return configMap;
    }

    @Override
    public boolean isEditorialSite()
    {
        if (editorial == null)
        {
            //Attempt to read from the config. Defaults to false.
            editorial = Boolean.parseBoolean(configMap.get(CONFIG_IS_EDITORIAL));
        }
        return editorial;
    }

    @Override
    public PathResolutionDetails resolvePath(String path)
    {
        return getRootSection().resolvePath(path);
    }
}
