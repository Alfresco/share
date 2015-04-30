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

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:visitorFeedbackSummary type behaviours.
 * 
 * @author Brian Remmington
 */
public class VisitorFeedbackSummaryType implements WebSiteModel
{
    private final static Log log = LogFactory.getLog(VisitorFeedbackSummaryType.class);
    
	/** Policy component */
	private PolicyComponent policyComponent;
	
	private BehaviourFilter behaviourFilter;
	
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
	
	public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    /**
	 * Init method.  Binds model behaviours to policies.
	 */
	public void init()
	{
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, WebSiteModel.TYPE_VISITOR_FEEDBACK_SUMMARY,
                new JavaBehaviour(this, "onUpdatePropertiesOnCommit", NotificationFrequency.TRANSACTION_COMMIT));
	}

    public void onUpdatePropertiesOnCommit(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        NodeRef summarisedAsset = (NodeRef)after.get(PROP_SUMMARISED_ASSET);
        if (summarisedAsset != null && nodeService.exists(summarisedAsset))
        {
            //We are going to copy the comment count and average rating onto the asset itself 
            behaviourFilter.disableBehaviour(summarisedAsset, WebSiteModel.ASPECT_WEBASSET);
            try
            {
                Map<QName, Serializable> props = nodeService.getProperties(summarisedAsset);
                Float averageRating = (Float)after.get(PROP_AVERAGE_RATING);
                Serializable commentCount = after.get(PROP_COMMENT_COUNT);
                if (log.isDebugEnabled())
                {
                    log.debug("Updating web asset " + summarisedAsset + " with feedback summary. Average rating = " + 
                            averageRating + "; Comment count = " + commentCount);
                }
                
                int roundedRating = 0;
                if (averageRating != null)
                {
                    float actualRating = averageRating;
                    roundedRating = Math.round(actualRating * 10.0F);
                }
                
                props.put(PROP_DERIVED_AVERAGE_RATING, roundedRating);
                props.put(PROP_DERIVED_COMMENT_COUNT, commentCount);
                nodeService.setProperties(summarisedAsset, props);
            }
            finally
            {
                behaviourFilter.enableBehaviour(summarisedAsset, WebSiteModel.ASPECT_WEBASSET);
            }
        }
    }
}
