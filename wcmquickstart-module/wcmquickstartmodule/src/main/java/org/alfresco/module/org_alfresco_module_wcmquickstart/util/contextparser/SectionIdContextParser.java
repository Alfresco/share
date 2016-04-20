package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Section Id Context Parser
 * 
 * @author Roy Wetherall
 */
public class SectionIdContextParser extends ContextParser
{	
	/**
	 * @see org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParser#execute(org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	public String execute(NodeRef context)
	{
		String result = null;
		NodeRef nodeRef = siteHelper.getRelevantSection(context);
		if (nodeRef != null)
		{
			result = nodeRef.toString();
		}
		return result;
	}

}
