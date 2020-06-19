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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This context parser handles invocations of the form:
 *     section:/news/industrial
 * which it translates into the noderef of the section at the specified path.
 * It supports "." meaning current section and ".." meaning parent section.
 * @author Brian
 */
public class SectionPathContextParser extends ContextParser
{	
    private final static Log log = LogFactory.getLog(SectionPathContextParser.class);
    private final static String INVOCATION_PREFIX = "section:";
    
	/**
	 * @see org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParser#execute(org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	public String execute(NodeRef context, String invocation)
	{
	    if (log.isDebugEnabled())
	    {
	        log.debug("Executing invocation \"" + invocation + "\" on node context " + context);
	    }
	    String result = null;
		String path = invocation.substring(INVOCATION_PREFIX.length());
		String[] pathSegments = path.split("/");
		NodeRef rootOfPath;
		int pathSegmentIndex = 0;
		if ((pathSegments.length > 0) && (pathSegments[0].length() == 0))
		{
		    rootOfPath = siteHelper.getRelevantWebRoot(context);
		    pathSegmentIndex = 1;
		}
		else
		{
		    rootOfPath = siteHelper.getRelevantSection(context);
		}
		if (log.isDebugEnabled())
		{
		    log.debug("Root of path resolved to noderef " + rootOfPath);
		}
		
		NodeRef currentNode = rootOfPath;
		for (; (currentNode != null) && (pathSegmentIndex < pathSegments.length); ++pathSegmentIndex)
		{
		    String segment = pathSegments[pathSegmentIndex];
		    if (log.isDebugEnabled())
		    {
		        log.debug("Processing path segment " + segment);
		    }
		    if (".".equals(segment) || (segment.length() == 0))
		    {
                //Do nothing
		    } 
		    else if ("..".equals(segment))
		    {
		        currentNode = siteHelper.getRelevantSection(currentNode, false);
		    }
		    else
		    {
		        currentNode = nodeService.getChildByName(currentNode, ContentModel.ASSOC_CONTAINS, segment);
		    }
            if (log.isDebugEnabled())
            {
                log.debug("Path segment " + segment + " has resolved to node " + currentNode);
            }
		}

		if (currentNode != null)
		{
		    result = currentNode.toString();
		}
		return result;
	}

    @Override
    public boolean canHandle(String invocation)
    {
        return invocation.startsWith(INVOCATION_PREFIX);
    }

    @Override
    public String execute(NodeRef context)
    {
        return execute(context, INVOCATION_PREFIX);
    }
}
