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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.importer.ACPImportPackageHandler;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.view.ImporterService;
import org.alfresco.service.cmr.view.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;


/**
 * Load web site data GET method
 * 
 * @author Roy Wetherall
 */
public class LoadWebSiteDataGet extends DeclarativeWebScript
{
	/** Log */
    private static Log logger = LogFactory.getLog(LoadWebSiteDataGet.class);
    
    /** DocLib component name */
    private static final String COMPONENT_DOCUMENT_LIBRARY = "documentLibrary";
    
    /** Arguments */
    private static final String ARG_SITE_NAME = "site";
    private static final String ARG_PREVIEW = "preview";
    private static final String ARG_IMPORT_ID = "importid";
    
    /** Default */
    private static final String DEFAULT_IMPORT_ID = "financial";
    
    /** Importer service */
    private ImporterService importerService;
    
    /** Nodes service */
    private NodeService nodeService;
    
    /** Site service */
    private SiteService siteService;
    
    /** File folder service */
    private FileFolderService fileFolderService;
    
    /** Map of import ACPs */
    private Map<String, String> importFileLocations;

    /**
     * Set the importer service
     * @param importerService	importer service
     */
    public void setImporterService(ImporterService importerService) 
    {
        this.importerService = importerService;
    }
    
    /**
     * Set the ndoe service
     * @param nodeService	node service
     */
    public void setNodeService(NodeService nodeService)
    {
	    this.nodeService = nodeService;
    }
    
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
     * Set the map of available import ACPs
     * @param importACPs    map of import ACPs
     */
    public void setImportFileLocations(Map<String, String> importFileLocations)
    {
        this.importFileLocations = importFileLocations;
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
        
        // Determine whether this is a preview or not
        boolean preview = false;
        if (req.getParameter(ARG_PREVIEW) != null)
        {
        	String value = req.getParameter(ARG_PREVIEW);
        	preview = Boolean.parseBoolean(value); 
        }
        
        // Determine the import id
        String importId = DEFAULT_IMPORT_ID;
        if (req.getParameter(ARG_IMPORT_ID) != null)
        {
            importId = req.getParameter(ARG_IMPORT_ID);
        }
        
        // Get the site from the site name
        SiteInfo site = siteService.getSite(siteName);
        if (site == null)
        {
        	throw new WebScriptException(Status.STATUS_BAD_REQUEST, "The site specified (" + siteName + ") does not exist.");
        }
            
        // Get the dod lib container
        NodeRef docLib = siteService.getContainer(siteName, COMPONENT_DOCUMENT_LIBRARY);
        
        // Check to see if the data has already been loaded
        boolean success = true;
        if (docLib != null && isDataLoaded(docLib) == true)
        {
        	success = false;
        }
        else
        {
	        if (preview == false)
	        {       
	            // Get the import location
	            String importFileLocation = importFileLocations.get(importId);
	            if (importFileLocation == null)
	            {
	                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "The import file location for import id " + importId + " could not be found.");
	            }
	            
	        	// If we don't have a doc lib create one
	        	if (docLib == null)
	            {
	                docLib = siteService.createContainer(siteName, COMPONENT_DOCUMENT_LIBRARY, WebSiteModel.TYPE_WEBSITE_CONTAINER, null);
	            }
	            else
	            {
	            	// Make sure we cast the created doc lib to the correct type
	            	if (WebSiteModel.TYPE_WEBSITE_CONTAINER.equals(nodeService.getType(docLib)) == false)
	            	{
	            		nodeService.setType(docLib, WebSiteModel.TYPE_WEBSITE_CONTAINER);
	            	}
	            }
	        	
		        // Log
		        if (logger.isDebugEnabled() == true)
		        {
		        	logger.debug("Importing " + importFileLocation + " into site " + siteName);
		        }
		            
		        // Import the web site data ACP into the the provided docLib node reference
		        InputStream is = LoadWebSiteDataGet.class.getClassLoader().getResourceAsStream(importFileLocation);
		        if (is == null)
		        {
		            throw new AlfrescoRuntimeException("The import file (" + importFileLocation + ") could not be found");
		        }
		
		        // Grab the ACP stream from the class path and pop it in a temp file as the ACP import handler
		        // expects this
		        File acpFile = null;
		        try
		        {
		        	acpFile = File.createTempFile("temp", ".acp");	        
		        	OutputStream out=new FileOutputStream(acpFile);
		        	try
		        	{
			        	byte buf[]=new byte[1024];
				        int len;
				        while ( (len=is.read(buf)) > 0)
				        {
				        	out.write(buf, 0, len);
				        }
		        	}
		        	finally
		        	{
		        		out.close();
		        		is.close();
		        	}
		        }
		        catch (IOException ioException)
		        {
		        	throw new AlfrescoRuntimeException("The import file (" + importFileLocation + ") could not be opened.");
		        }
		        
		        // Import the ACP into the doc lib of the site
		        ACPImportPackageHandler importHandler = new ACPImportPackageHandler(acpFile, "UTF-8");
		        Location location = new Location(docLib);        
		        importerService.importView(importHandler, location, null, null);
	        }
        }
        
        // Put the success string into the model    
        Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
    	model.put("success", success);
    	model.put("preview", preview);
    	if (success == true && preview == true)
    	{
    	    // Put a list of the available import id's into the model
    	    model.put("importids", importFileLocations.keySet());
    	}
    	
        return model;
    }
    
    /**
     * Indicates whether the data has already been loaded into the site or not.
     * @param docLib	document library node reference
     * @return boolean	true if the data has already been loaded, false otherwise
     */
    private boolean isDataLoaded(NodeRef docLib)
    {
    	boolean result = false;
    	if (fileFolderService.searchSimple(docLib, "Alfresco Quick Start") != null)
    	{
    		result = true;
    	}
    	return result;
    		
    }
}