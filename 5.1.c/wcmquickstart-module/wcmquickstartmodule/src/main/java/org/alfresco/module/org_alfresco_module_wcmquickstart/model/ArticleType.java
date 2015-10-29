/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
