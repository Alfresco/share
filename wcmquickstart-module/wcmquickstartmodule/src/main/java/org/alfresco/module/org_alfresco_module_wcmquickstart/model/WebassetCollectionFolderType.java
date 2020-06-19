/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * ws:sectionWebassetCollections behaviours
 * 
 * @author Roy Wetherall
 */
public class WebassetCollectionFolderType implements WebSiteModel
{
    /** Policy component */
    private PolicyComponent policyComponent;

    /** Node service */
    private NodeService nodeService;

    /** Dictionary service */
    private DictionaryService dictionaryService;

    /** Permission service */
    private PermissionService permissionService;

    /**
     * Set the policy component
     * 
     * @param policyComponent
     *            policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * Set the node service
     * 
     * @param nodeService
     *            node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Set the permission service
     * 
     * @param permissionService
     *            permission service
     */
    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindAssociationBehaviour(QName.createQName(NamespaceService.ALFRESCO_URI,
                "onCreateChildAssociation"), WebSiteModel.TYPE_WEBASSET_COLLECTION_FOLDER, ContentModel.ASSOC_CONTAINS,
                new JavaBehaviour(this, "onCreateChildAssociationEveryEvent", NotificationFrequency.EVERY_EVENT));
    }

    /**
     * Down-casts any folders created in a the collection folder to be webasset
     * collections.
     * 
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef,
     *      boolean)
     */
    public void onCreateChildAssociationEveryEvent(ChildAssociationRef childAssoc, boolean isNewNode)
    {
        NodeRef nodeRef = childAssoc.getChildRef();
        if (nodeService.exists(nodeRef) == true)
        {
            QName createdNodeType = nodeService.getType(nodeRef);
            if (dictionaryService.isSubClass(createdNodeType, ContentModel.TYPE_FOLDER))
            {
                if (!dictionaryService.isSubClass(createdNodeType, TYPE_WEBASSET_COLLECTION))
                {
                    // Down-cast created node to ws:webassetCollection
                    nodeService.setType(nodeRef, TYPE_WEBASSET_COLLECTION);
                }
                // Remove create child permission on createdNode
                permissionService.setPermission(nodeRef, PermissionService.ALL_AUTHORITIES,
                        PermissionService.CREATE_CHILDREN, false);
            }
        }
    }
}
