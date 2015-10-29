/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.model.RenditionModel;
import org.alfresco.repo.transfer.AbstractNodeFinder;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Given a node, this finder finds any renditions that it has
 * @author Brian
 *
 */
public class RenditionsFinder extends AbstractNodeFinder
{
    private NodeService nodeService;

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService();
    }

    @Override
    public Set<NodeRef> findFrom(NodeRef thisNode)
    {
        Set<NodeRef> result = Collections.emptySet();
        if (nodeService.exists(thisNode) && 
                nodeService.hasAspect(thisNode, RenditionModel.ASPECT_RENDITIONED))
        {
            result = new HashSet<NodeRef>();
            
            List<ChildAssociationRef> renditions = nodeService.getChildAssocs(thisNode, RenditionModel.ASSOC_RENDITION, RegexQNamePattern.MATCH_ALL);
            for (ChildAssociationRef rendition : renditions)
            {
                result.add(rendition.getChildRef());
            }
        }
        return result;
    }

}
