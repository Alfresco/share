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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.rendition.RenditionHelper;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Helper web script implementation to update all the renditions 
 * 
 * @author Roy Wetherall
 */
public class UpdateWebSiteRenditionsGet extends DeclarativeWebScript 
									   implements WebSiteModel
{
	/** Log */
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(UpdateWebSiteRenditionsGet.class);
    
    /** DocLib component name */
    private static final String COMPONENT_DOCUMENT_LIBRARY = "documentLibrary";
    
    /** Arguments */
    private static final String ARG_SITE_NAME = "site";
    
    /** Site service */
    private SiteService siteService;
    
    /** File folder service */
    private FileFolderService fileFolderService;
    
    /** Dictionary serivce */
    private DictionaryService dictionaryService;
    
    /** Node service */
    private NodeService nodeService;
    
    /** Rendition helper */
    private RenditionHelper renditionHelper;
    
    /**
     * Set the site service
     * @param siteService	site service
     */
    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }
    
    /**
     * Set the file folder service
     * @param fileFolderService	file folder service
     */
    public void setFileFolderService(FileFolderService fileFolderService)
    {
	    this.fileFolderService = fileFolderService;
    }
    
    /**
     * Set dictionary service
     * @param dictionaryService		dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
	    this.dictionaryService = dictionaryService;
    }
    
    /**
     * Set node service
     * @param nodeService	node service
     */
    public void setNodeService(NodeService nodeService)
    {
	    this.nodeService = nodeService;
    }
    
    /**
     * Set rendition helper
     * @param renditionHelper	rendition helper
     */
    public void setRenditionHelper(RenditionHelper renditionHelper)
    {
	    this.renditionHelper = renditionHelper;
    }
    
    /**
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status, org.springframework.extensions.webscripts.Cache)
     */
    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Get the site name
        String siteName = null;
        if (req.getParameter(ARG_SITE_NAME) != null)
        {
            siteName = req.getParameter(ARG_SITE_NAME);
        }
        else
        {
        	throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No site name specified.");
        }      
        
        // Get the site from the site name
        SiteInfo site = siteService.getSite(siteName);
        if (site == null)
        {
        	throw new WebScriptException(Status.STATUS_BAD_REQUEST, "The site specified (" + siteName + ") does not exist.");
        }
            
        // Resolve documentLibrary (filePlan) container
        NodeRef docLib = siteService.getContainer(siteName, COMPONENT_DOCUMENT_LIBRARY);
        if (docLib == null)
        {
            docLib = siteService.createContainer(siteName, COMPONENT_DOCUMENT_LIBRARY, null, null);
        }
     
        NodeRef website = getWebSite(docLib);
        if (website != null)
        {
        	renditionChildren(website);
        }
        
        // Put the success string into the model    
        Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
    	model.put("success", true);
    	
        return model;
    }
    
    /**
     * 
     * @param nodeRef
     */
    private void renditionChildren(NodeRef nodeRef)
    {
    	List<FileInfo> children = fileFolderService.list(nodeRef);
    	for (FileInfo child : children)
        {
	        NodeRef childNodeRef = child.getNodeRef();
	        QName childType = nodeService.getType(childNodeRef);
	        if (dictionaryService.isSubClass(childType, TYPE_SECTION) == true)
	        {
	        	// Recurse to children
	        	renditionChildren(childNodeRef);
	        }
	        else if (nodeService.hasAspect(childNodeRef, ASPECT_WEBASSET) == true &&
	        		 nodeService.hasAspect(childNodeRef, ContentModel.ASPECT_WORKING_COPY) == false)
	        {
	        	// Recalculate the renditions of the web asset
	        	renditionHelper.createRenditions(childNodeRef, true);
	        }
        }
    }
    
    /**
     * 
     * @param docLib
     * @return
     */
    private NodeRef getWebSite(NodeRef docLib)
    {
    	NodeRef result = fileFolderService.searchSimple(docLib, "Alfresco Quick Start"); 
    	if (result != null)
    	{
    		result = fileFolderService.searchSimple(result, "Quick Start Editorial");
    	}
    	return result;	
    }
}