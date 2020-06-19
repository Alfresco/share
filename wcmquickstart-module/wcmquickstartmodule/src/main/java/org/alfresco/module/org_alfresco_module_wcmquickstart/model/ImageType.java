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
/**
 * 
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.publish.PublishService;
import org.alfresco.module.org_alfresco_module_wcmquickstart.rendition.RenditionHelper;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.copy.CopyServicePolicies.OnCopyNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * Behaviour for ws:image type
 * 
 * @author Roy Wetherall
 */
public class ImageType implements WebSiteModel,
                                  NodeServicePolicies.BeforeDeleteNodePolicy
{
    /** Policy component */
    private PolicyComponent policyComponent;
    
    /** Node service */
    private NodeService nodeService;
    
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
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.BeforeDeleteNodePolicy.QNAME, 
                TYPE_IMAGE,
                new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.FIRST_EVENT));
    }

    /**
     * @see org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy#beforeDeleteNode(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        // Remove all referencing and referenced associations
        if (!nodeService.hasAspect(nodeRef, ContentModel.ASPECT_PENDING_DELETE))
        {
            removeAll(nodeService.getSourceAssocs(nodeRef, RegexQNamePattern.MATCH_ALL));
            removeAll(nodeService.getTargetAssocs(nodeRef, RegexQNamePattern.MATCH_ALL));        
        }
    }
    
    /**
     * Remove all the associations in the list
     * @param assocs    list of associations
     */
    private void removeAll(List<AssociationRef> assocs)
    {
        for (AssociationRef assoc : assocs)
        {
            nodeService.removeAssociation(assoc.getSourceRef(), assoc.getTargetRef(), assoc.getTypeQName());
        }
    }
}
