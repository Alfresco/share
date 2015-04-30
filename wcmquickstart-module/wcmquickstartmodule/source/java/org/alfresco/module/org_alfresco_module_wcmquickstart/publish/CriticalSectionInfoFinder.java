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

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transfer.AbstractNodeFinder;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

public class CriticalSectionInfoFinder extends AbstractNodeFinder
{
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private Set<QName> relevantNodeTypes = Collections.emptySet();

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService();
        dictionaryService = serviceRegistry.getDictionaryService();
        relevantNodeTypes = new HashSet<QName>();
        relevantNodeTypes.add(WebSiteModel.TYPE_INDEX_PAGE);
        relevantNodeTypes.add(WebSiteModel.TYPE_WEBASSET_COLLECTION_FOLDER);
    }

    @Override
    public Set<NodeRef> findFrom(NodeRef thisNode)
    {
        Set<NodeRef> result = Collections.emptySet();
        if (nodeService.exists(thisNode) && 
                dictionaryService.isSubClass(nodeService.getType(thisNode), WebSiteModel.TYPE_SECTION))
        {
            result = new HashSet<NodeRef>();
            //From a section we want to include its index page and collections folder
            List<ChildAssociationRef> necessaryChildren = nodeService.getChildAssocs(thisNode, relevantNodeTypes);
            for (ChildAssociationRef child : necessaryChildren)
            {
                result.add(child.getChildRef());
            }
        }
        return result;
    }

}
