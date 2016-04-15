package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transfer.AbstractNodeFinder;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Given an article, this finder finds its primary and secondary images
 * @author Brian
 *
 */
public class ArticleImageFinder extends AbstractNodeFinder
{
    private NodeService nodeService;
    private DictionaryService dictionaryService;

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService();
        dictionaryService = serviceRegistry.getDictionaryService();
    }

    @Override
    public Set<NodeRef> findFrom(NodeRef thisNode)
    {
        Set<NodeRef> result = Collections.emptySet();
        if (nodeService.exists(thisNode) && 
                dictionaryService.isSubClass(nodeService.getType(thisNode), WebSiteModel.TYPE_ARTICLE))
        {
            result = new HashSet<NodeRef>();
            
            List<AssociationRef> images = nodeService.getTargetAssocs(thisNode, WebSiteModel.ASSOC_PRIMARY_IMAGE);
            images.addAll(nodeService.getTargetAssocs(thisNode, WebSiteModel.ASSOC_SECONDARY_IMAGE));
            for (AssociationRef assoc : images)
            {
                result.add(assoc.getTargetRef());
            }
        }
        return result;
    }

}
