package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transfer.AbstractNodeFinder;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

public class IndexPageSectionFinder extends AbstractNodeFinder
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
                (WebSiteModel.TYPE_INDEX_PAGE.equals(nodeService.getType(thisNode))))
        {
            result = new HashSet<NodeRef>();
            result.add(nodeService.getPrimaryParent(thisNode).getParentRef());
        }
        return result;
    }

}
