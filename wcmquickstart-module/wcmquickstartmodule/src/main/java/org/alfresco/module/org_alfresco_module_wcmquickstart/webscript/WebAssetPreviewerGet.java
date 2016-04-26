package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Helper web script implementation to get web asset URL
 * 
 * @author Roy Wetherall
 */
public class WebAssetPreviewerGet extends DeclarativeWebScript 
									   implements WebSiteModel
{
    /** Arguments */
    private static final String ARG_SITE_ID = "id";
    
    /** Site helper */
    private SiteHelper siteHelper;
    
    /**
     * Sets the site helper
     * @param siteHelper	site helperS
     */
    public void setSiteHelper(SiteHelper siteHelper)
    {
	    this.siteHelper = siteHelper;
    }
    
    /**
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest, org.springframework.extensions.webscripts.Status, org.springframework.extensions.webscripts.Cache)
     */
    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        // Get the node id
    	Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
		String nodeId = templateVars.get(ARG_SITE_ID);
        if (nodeId == null)
        {
        	throw new WebScriptException(Status.STATUS_NOT_FOUND, "No node identifier provided to previewer.");
        }      
        
        // Get the web asset URL
        NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
        String url = siteHelper.getWebAssetURL(nodeRef);
        
        // Throw exception if no URL found
        if (url == null)
        {
        	throw new WebScriptException(Status.STATUS_NOT_FOUND, "No preview URL can be found for this node.");
        }
        
        // Put the URL in the model
        Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
    	model.put("url", url);
    	
        return model;
    }
}