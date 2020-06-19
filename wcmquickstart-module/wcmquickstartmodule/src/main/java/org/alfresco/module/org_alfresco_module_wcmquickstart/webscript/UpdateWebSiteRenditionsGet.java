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
     * @param nodeRef NodeRef
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
     * @param docLib NodeRef
     * @return NodeRef
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