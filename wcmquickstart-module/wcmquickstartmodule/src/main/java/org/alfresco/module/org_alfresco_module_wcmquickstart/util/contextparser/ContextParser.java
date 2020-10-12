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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Context parser abstract base implementation.
 * 
 * @author Roy Wetherall
 */
public abstract class ContextParser implements WebSiteModel
{
	/** Query parser name */
	protected String name;
	
	/** Context parser service */
	private ContextParserService registry;
	
	/** Node service */
	protected NodeService nodeService;
	
	/** Site helper */
	protected SiteHelper siteHelper;

	/**
	 * Sets the node service
	 * @param nodeService	node service
	 */
	public void setNodeService(NodeService nodeService)
    {
	    this.nodeService = nodeService;
    }
	
	/** 
	 * Sets the site helper.
	 * @param siteHelper	site helper
	 */
	public void setSiteHelper(SiteHelper siteHelper)
    {
	    this.siteHelper = siteHelper;
    }
	
	/**
	 * Get name of the context parser.
	 * 
	 * @return String	context parser name
	 */
	public String getName()
    {
	    return name;
    }
	
	/**
	 * Set the name of the context parser
	 * 
	 * @param name	context parser name
	 */
	public void setName(String name)
    {
	    this.name = name;
    }
	
	/**
	 * Set context parser registry
	 * 
	 * @param registry	context parser registry
	 */
	public void setRegistry(ContextParserService registry)
    {
	    this.registry = registry;
    }
	
	/**
	 * Initialisation method
	 */
	public void init()
	{
		registry.register(this);
	}
	
	/**
	 * Executes the context parser, returning the parsed string value.
	 * @param context   node reference providing context
	 * @return String	parsed string value
	 */
	public abstract String execute(NodeRef context);
	
	public boolean canHandle(String invocation)
	{
	    return (invocation != null && invocation.equals(name));
	}
	
	public String execute(NodeRef context, String invocation)
	{
	    return execute(context);
	}
}
