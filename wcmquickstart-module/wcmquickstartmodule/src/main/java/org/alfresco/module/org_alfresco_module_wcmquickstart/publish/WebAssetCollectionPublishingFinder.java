package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transfer.AbstractNodeFinder;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Given a web asset collection folder, this finder finds all of the web asset collections 
 * that it contains.
 * 
 * @author Brian
 */
public class WebAssetCollectionPublishingFinder extends AbstractNodeFinder
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
                dictionaryService.isSubClass(nodeService.getType(thisNode), WebSiteModel.TYPE_WEBASSET_COLLECTION_FOLDER))
        {
            result = new HashSet<NodeRef>(23);
            for (ChildAssociationRef assoc : nodeService.getChildAssocs(thisNode))
            {
                result.add(assoc.getChildRef());
            }
        }
        return result;
    }

}
