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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:visitorFeedback type behaviours.
 * 
 * @author Brian Remmington
 */
public class VisitorFeedbackType implements WebSiteModel
{
    /** Logger */
    private final static Log log = LogFactory.getLog(VisitorFeedbackType.class);
    
    private final static String AFFECTED_VISITOR_FEEDBACK = "AffectedVisitorFeedback";
    
    /** Feedback types */
    public final static String COMMENT_TYPE = "Comment";
    public final static String CONTACT_REQUEST_TYPE = "Contact Request";
    
	/** Policy component */
	private PolicyComponent policyComponent;
	
	/** Behaviour filter */
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
	
	/**
	 * Set the behaviour filter
	 * @param behaviourFilter	behaviour filter
	 */
	public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
	
    /**
	 * Init method.  Binds model behaviours to policies.
	 */
	public void init()
	{
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, 
                WebSiteModel.TYPE_VISITOR_FEEDBACK,
                new JavaBehaviour(this, "onUpdatePropertiesEveryEvent", NotificationFrequency.EVERY_EVENT));
        
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                WebSiteModel.TYPE_VISITOR_FEEDBACK, 
                new JavaBehaviour(this, "onCreateNodeEveryEvent", NotificationFrequency.EVERY_EVENT));

        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, 
                WebSiteModel.TYPE_VISITOR_FEEDBACK,
                new JavaBehaviour(this, "onUpdatePropertiesOnCommit", NotificationFrequency.TRANSACTION_COMMIT));
        
        policyComponent.bindClassBehaviour(
                NodeServicePolicies.OnCreateNodePolicy.QNAME,
                WebSiteModel.TYPE_VISITOR_FEEDBACK, 
                new JavaBehaviour(this, "onCreateNodeOnCommit", NotificationFrequency.TRANSACTION_COMMIT));
	}

	/**
	 * On create node, every event.
	 * @param childAssocRef		child association reference
	 */
    public void onCreateNodeEveryEvent(ChildAssociationRef childAssocRef)
    {
        if (log.isDebugEnabled() == true)
        {
            log.debug("onCreateNode (every event) - recording node " + childAssocRef.getChildRef().toString());
        }
        recordNode(childAssocRef.getChildRef());
    }
    
    /**
     * On update properties behaviour, every event
     * @param nodeRef	node reference
     * @param before	before property values
     * @param after		after property values
     */
    public void onUpdatePropertiesEveryEvent(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        if (log.isDebugEnabled() == true)
        {
            log.debug("onUpdateProperties (every event) - recording node " + nodeRef.toString());
        }
        recordNode(nodeRef);
    }
    
    /**
     * On create node behaviour, on commit.
     * @param childAssocRef		child association reference
     */
    public void onCreateNodeOnCommit(ChildAssociationRef childAssocRef)
    {
        if (log.isDebugEnabled() == true)
        {
            log.debug("onCreateNode (on commit) - process commit " + childAssocRef.getChildRef().toString());
        }
        processCommit(childAssocRef.getChildRef());        
    }
        
    /**
     * onUpdateProperties behaviour (on commit)
     * @param nodeRef   node reference
     * @param before    before property values
     * @param after     after property valuesS
     */
    public void onUpdatePropertiesOnCommit(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        if (log.isDebugEnabled() == true)
        {
            log.debug("onUpdateProperties (on commit) - process commit " + nodeRef.toString());
        }       
        processCommit(nodeRef);
    }

    /**
     * Record the node to be processed later
     * @param nodeRef   node reference
     */
    private void recordNode(NodeRef nodeRef)
    {
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport
                .getResource(AFFECTED_VISITOR_FEEDBACK);
        if (affectedNodeRefs == null)
        {
            affectedNodeRefs = new HashSet<NodeRef>(5);
            AlfrescoTransactionSupport.bindResource(AFFECTED_VISITOR_FEEDBACK, affectedNodeRefs);
        }
        affectedNodeRefs.add(nodeRef);
    }

    /**
     * Process node reference commit
     * @param nodeRef   node reference
     */
    private void processCommit(NodeRef nodeRef)
    {
        @SuppressWarnings("unchecked")
        Set<NodeRef> affectedNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport
                .getResource(AFFECTED_VISITOR_FEEDBACK);
        if (affectedNodeRefs != null && affectedNodeRefs.remove(nodeRef))
        {
            try
            {
                behaviourFilter.disableBehaviour(nodeRef, TYPE_VISITOR_FEEDBACK);
                Map<QName,Serializable> props = nodeService.getProperties(nodeRef);
                NodeRef relevantArticle = (NodeRef)props.get(PROP_RELEVANT_ASSET);
                List<AssociationRef> assocs = nodeService.getTargetAssocs(nodeRef, ASSOC_RELEVANT_ASSET);
                boolean existingAssocRemoved = false;
                if (!assocs.isEmpty() && !assocs.get(0).getTargetRef().equals(relevantArticle))
                {
                    nodeService.removeAssociation(nodeRef, relevantArticle, ASSOC_RELEVANT_ASSET);
                    existingAssocRemoved = true;
                }
                if (assocs.isEmpty() || existingAssocRemoved)
                {
                    nodeService.createAssociation(nodeRef, relevantArticle, ASSOC_RELEVANT_ASSET);
                }
                //Check that the two properties "commentFlagged" and "ratingProcessed" have a value, and 
                //force to the default of "false" if not
                if (!props.containsKey(PROP_COMMENT_FLAGGED))
                {
                    nodeService.setProperty(nodeRef, PROP_COMMENT_FLAGGED, Boolean.FALSE);
                }
                if (!props.containsKey(PROP_RATING_PROCESSED))
                {
                    nodeService.setProperty(nodeRef, PROP_RATING_PROCESSED, Boolean.FALSE);
                }
            }
            finally
            {
                behaviourFilter.enableBehaviour(nodeRef, TYPE_VISITOR_FEEDBACK);
            }
        }
    }
}
