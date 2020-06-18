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

import java.io.Serializable;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.WebassetCollectionHelper;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * ws:webassetCollection behaviours
 * 
 * @author Roy Wetherall
 */
public class WebassetCollectionType implements WebSiteModel, NodeServicePolicies.OnUpdatePropertiesPolicy,
        NodeServicePolicies.OnCreateChildAssociationPolicy, NodeServicePolicies.OnCreateAssociationPolicy
{
    /** Policy component */
    private PolicyComponent policyComponent;

    /** Node service */
    private NodeService nodeService;

    /** On create association behaviour */
    private JavaBehaviour onCreateAssociation;

    /** Collection helper */
    private WebassetCollectionHelper collectionHelper;

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
     * Set the web asset collection helper
     * 
     * @param collectionHelper
     *            web asset collection helper
     */
    public void setCollectionHelper(WebassetCollectionHelper collectionHelper)
    {
        this.collectionHelper = collectionHelper;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
                TYPE_WEBASSET_COLLECTION, new JavaBehaviour(this, "onUpdateProperties",
                        NotificationFrequency.TRANSACTION_COMMIT));
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                TYPE_WEBASSET_COLLECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onCreateChildAssociation", NotificationFrequency.FIRST_EVENT));
        onCreateAssociation = new JavaBehaviour(this, "onCreateAssociation", NotificationFrequency.FIRST_EVENT);
        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateAssociationPolicy.QNAME,
                TYPE_WEBASSET_COLLECTION, ASSOC_WEBASSETS, onCreateAssociation);
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy#onUpdateProperties(org.alfresco.service.cmr.repository.NodeRef,
     *      java.util.Map, java.util.Map)
     */
    @Override
    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        onCreateAssociation.disable();
        try
        {
            String queryBefore = makeNull((String) before.get(PROP_QUERY));
            String queryAfter = makeNull((String) after.get(PROP_QUERY));

            if ((queryBefore == null && queryAfter != null)
                    || (queryBefore != null && queryAfter != null && queryBefore.equals(queryAfter) == false))
            {
                // Refresh the collection
                collectionHelper.refreshCollection(nodeRef);

                // Set the dynamic flag
                nodeService.setProperty(nodeRef, PROP_IS_DYNAMIC, true);
            }
            else if (queryBefore != null && queryAfter == null)
            {
                // Clear the contents of the collection as we are resetting the
                // query
                collectionHelper.clearCollection(nodeRef);

                // Set the dynamic flag
                nodeService.setProperty(nodeRef, PROP_IS_DYNAMIC, false);
            }
        }
        finally
        {
            onCreateAssociation.enable();
        }
    }

    /**
     * Helper method to convert empty strings into null values
     * 
     * @param value
     *            value
     * @return String passed value, or null if value empty
     */
    private String makeNull(String value)
    {
        String result = value;
        if (value != null && value.trim().length() == 0)
        {
            result = null;
        }
        return result;
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(org.alfresco.service.cmr.repository.ChildAssociationRef,
     *      boolean)
     */
    @Override
    public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode)
    {
        // No contains associations can be created within a collection
        throw new AlfrescoRuntimeException(
                "Content or folders can not be created or added within a resource collection.");
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.OnCreateAssociationPolicy#onCreateAssociation(org.alfresco.service.cmr.repository.AssociationRef)
     */
    @Override
    public void onCreateAssociation(AssociationRef nodeAssocRef)
    {
        // Check that all items being added to the collection are assets
        if (nodeService.hasAspect(nodeAssocRef.getTargetRef(), ASPECT_WEBASSET) == false)
        {
            throw new AlfrescoRuntimeException("Can not add resource to a collection unless it is an asset.");
        }
    }
}
