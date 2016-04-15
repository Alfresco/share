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

/**
 * Given a section, this finder finds all sections below it
 * @author Brian
 *
 */
public class DownwardsSectionFinder extends AbstractNodeFinder
{
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private Set<QName> typesOfInterest = Collections.emptySet();

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService();
        dictionaryService = serviceRegistry.getDictionaryService();
        typesOfInterest = new HashSet<QName>();
        typesOfInterest.add(WebSiteModel.TYPE_SECTION);
    }

    @Override
    public Set<NodeRef> findFrom(NodeRef thisNode)
    {
        Set<NodeRef> result = Collections.emptySet();
        if (dictionaryService.isSubClass(nodeService.getType(thisNode), WebSiteModel.TYPE_SECTION))
        {
            result = new HashSet<NodeRef>(23);
            
            List<ChildAssociationRef> childSections = nodeService.getChildAssocs(thisNode, typesOfInterest);
            for (ChildAssociationRef childSection : childSections)
            {
                result.add(childSection.getChildRef());
            }
        }
        return result;
    }

}
