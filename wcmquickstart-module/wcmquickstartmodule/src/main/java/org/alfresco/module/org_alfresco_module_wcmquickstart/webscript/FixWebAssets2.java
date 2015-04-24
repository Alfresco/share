/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.util.Map;
import java.util.TreeMap;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Webscript that fixes up webasset properties following model changes
 * 
 * @author Brian Remmington
 */
public class FixWebAssets2 extends DeclarativeWebScript implements WebSiteModel
{
	/** Node Service */
	private NodeService nodeService;
	
	/** Search Service */
	private SearchService searchService;
	/**
	 * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status, org.springframework.extensions.webscripts.Cache)
	 */
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
	{
	    ResultSet rs = null;
	    
	    try
	    {
	        //Find all nodes with the webasset aspect and populate the publishDate and published properties
	        rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, "ASPECT:\"" + ASPECT_WEBASSET + "\"");
	        for (ResultSetRow row : rs)
	        {
                    nodeService.addAspect(row.getNodeRef(), ContentModel.ASPECT_AUTHOR, null);
	        }
	    }
	    finally
	    {
	        if (rs != null) {rs.close();}
	    }
	    
	    rs = null;
	    try
	    {
                rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_LUCENE, "TYPE:\"" + TYPE_SECTION + "\"");
                for (ResultSetRow row : rs)
                {
                    nodeService.setProperty(row.getNodeRef(), PROP_EXCLUDE_FROM_NAV, Boolean.FALSE);
                }
	    }
	    finally
	    {
	    	if (rs != null) {rs.close();}
	    }
	    return new TreeMap<String, Object>();
	}
	
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }
    
}
