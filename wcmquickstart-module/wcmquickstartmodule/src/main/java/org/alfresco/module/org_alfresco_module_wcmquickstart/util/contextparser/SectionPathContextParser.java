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
