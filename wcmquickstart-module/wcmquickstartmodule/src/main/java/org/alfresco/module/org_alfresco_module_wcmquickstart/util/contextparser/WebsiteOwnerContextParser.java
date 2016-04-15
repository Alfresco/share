package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.OwnableService;

/**
 * Website owner parser
 * 
 * @author Roy Wetherall
 */
public class WebsiteOwnerContextParser extends ContextParser implements WebSiteModel
{	
	/** Ownable service */
	private OwnableService ownableService;
	
	/** 
	 * Sets the ownable service
	 * @param ownableService	ownable service
	 */
	public void setOwnableService(OwnableService ownableService)
    {
	    this.ownableService = ownableService;
    }
	
	/**
	 * @see org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParser#execute(org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	public String execute(NodeRef context)
	{	
		String result = null;
		NodeRef nodeRef = siteHelper.getRelevantWebSite(context);
		if (nodeRef != null)
		{
			result = ownableService.getOwner(nodeRef);
		}
		return result;
	}

}
