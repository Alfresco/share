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
