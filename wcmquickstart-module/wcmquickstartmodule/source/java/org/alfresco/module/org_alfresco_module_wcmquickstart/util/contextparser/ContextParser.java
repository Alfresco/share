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
