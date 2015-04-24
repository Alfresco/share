package org.alfresco.module.org_alfresco_module_wcmquickstart.feedback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.WCMQuickStartTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

public class DeleteArticleTest extends WCMQuickStartTest {
    public void testDeleteRalatedFeedbacks() throws Exception
    {
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        // create article node
        NodeRef article = 
           nodeService.createNode(editorialSite, ContentModel.ASSOC_CONTAINS, QName.createQName("article"), WebSiteModel.TYPE_ARTICLE).getChildRef();
        // add relevant asset property to feedback node
        Map<QName,Serializable> props = new HashMap<QName,Serializable>();
        props.put(WebSiteModel.PROP_RELEVANT_ASSET, article);

        List<NodeRef> feedbacks = new ArrayList<NodeRef>();
        // Create couple feedbacks
        for (int index = 0; index < 3; index++)
        {
            NodeRef feedback = 
               nodeService.createNode(editorialSite, ContentModel.ASSOC_CONTAINS, QName.createQName("feedback"), WebSiteModel.TYPE_VISITOR_FEEDBACK, props).getChildRef();
            feedbacks.add(feedback);
        }

        userTransaction.commit();

        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        // delete article node
        nodeService.deleteNode(article);

        userTransaction.commit();

        // all feedbacks should be deleted
        for (NodeRef feedback : feedbacks)
        {
            assertFalse(nodeService.exists(feedback));
        }

        assertFalse(nodeService.exists(article));
    }
}
