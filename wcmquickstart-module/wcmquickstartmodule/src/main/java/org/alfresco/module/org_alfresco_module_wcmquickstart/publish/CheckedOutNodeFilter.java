package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.AbstractNodeFilter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * This filter accepts only nodes that exist
 * @author Brian
 *
 */
public class CheckedOutNodeFilter extends AbstractNodeFilter
{
    private NodeService nodeService;

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService(); 
    }

    @Override
    public boolean accept(NodeRef thisNode)
    {
        return (!nodeService.hasAspect(thisNode, ContentModel.ASPECT_WORKING_COPY));
    }

}
