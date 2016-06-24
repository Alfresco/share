/*
 * #%L
 * Alfresco WCMQS AMP
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
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
