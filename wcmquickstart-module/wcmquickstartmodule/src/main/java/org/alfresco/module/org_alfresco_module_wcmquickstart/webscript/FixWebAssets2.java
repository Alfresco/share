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
