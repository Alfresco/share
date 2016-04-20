package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Site Id context Parser
 * 
 * @author Roy Wetherall
 */
public class SiteIdContextParser extends ContextParser implements WebSiteModel
{	
	/**
	 * @see org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParser#execute(org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	public String execute(NodeRef context)
	{	
		String result = null;
		NodeRef nodeRef = siteHelper.getRelevantWebRoot(context);
		if (nodeRef != null)
		{
			result = nodeRef.toString();
		}
		return result;
	}

}
