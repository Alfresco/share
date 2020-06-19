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

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.alfresco.model.ContentModel;;

/**
 * ws:article type behaviours.
 * 
 * @author Brian Remmington
 */
public class ArticleType implements WebSiteModel
{
    private final static Log log = LogFactory.getLog(ArticleType.class);
    
	/** Policy component */
	private PolicyComponent policyComponent;
	
	/** Node service */
	private NodeService nodeService;
	
	/**
	 * Set the policy component
	 * 
	 * @param policyComponent	policy component
	 */
	public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
	
	/**
	 * Set the node service
	 * 
	 * @param nodeService	node service
	 */
	public void setNodeService(NodeService nodeService)
	{
		this.nodeService = nodeService;
	}
	
	/**
	 * Init method.  Binds model behaviours to policies.
	 */
	public void init()
	{
        policyComponent.bindAssociationBehaviour(
                NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
                WebSiteModel.TYPE_VISITOR_FEEDBACK,
                WebSiteModel.ASSOC_RELEVANT_ASSET,
                new JavaBehaviour(this, "onDeleteAssociation", NotificationFrequency.EVERY_EVENT));
	}
    
    public void onDeleteAssociation(AssociationRef nodeAssocRef)
    {
        NodeRef sourceRef = nodeAssocRef.getSourceRef();
        
        if (nodeService.exists(sourceRef) && !nodeService.hasAspect(sourceRef, ContentModel.ASPECT_PENDING_DELETE))
        {
            // Delete the source
            nodeService.deleteNode(sourceRef);
        }
    }
    
    public void onDeleteAssociationEveryEvent(AssociationRef nodeAssocRef) 
    {
        NodeRef sourceNode = nodeAssocRef.getSourceRef();
        if (nodeService.exists(sourceNode))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Clearing relevant article property on node " + sourceNode);
            }
            nodeService.removeProperty(sourceNode, WebSiteModel.PROP_RELEVANT_ASSET);
        }
    }
    
}
