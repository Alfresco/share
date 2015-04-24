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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * cm:taggable aspect behaviours.
 * 
 * @author Brian
 */
public class TaggableAspect implements WebSiteModel
{
    private static final Log log = LogFactory.getLog(TaggableAspect.class);
    
    /** Policy component */
    private PolicyComponent policyComponent;
    private BehaviourFilter behaviourFilter;
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

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ContentModel.ASPECT_TAGGABLE,
                new JavaBehaviour(this, "onUpdatePropertiesOnCommit", NotificationFrequency.TRANSACTION_COMMIT));
    }

    @SuppressWarnings("unchecked")
    public void onUpdatePropertiesOnCommit(
            NodeRef nodeRef,
            Map<QName, Serializable> before,
            Map<QName, Serializable> after)
    {
        //When tags are updated on a webasset, copy their values into the ws:tags property for easy and fast access
        if (nodeService.exists(nodeRef) && nodeService.hasAspect(nodeRef, ASPECT_WEBASSET) && !EqualsHelper.nullSafeEquals(before.get(ContentModel.PROP_TAGS), after.get(ContentModel.PROP_TAGS)) )
        {
            List<NodeRef> tagIds = (List<NodeRef>) after.get(ContentModel.PROP_TAGS);
            ArrayList<String> tags = new ArrayList<String>();

            if (log.isDebugEnabled()) 
            {
                log.debug("Processing tags on a webasset node (" + nodeRef + "). Category nodes are: " + tagIds);
            }
            if (tagIds != null)
            {
                for (NodeRef tagId : tagIds)
                {
                    if (nodeService.exists(tagId))
                    {
                        tags.add((String)nodeService.getProperty(tagId, ContentModel.PROP_NAME));
                    }
                }
            }
            
            behaviourFilter.disableBehaviour(nodeRef, ASPECT_WEBASSET);
            try
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Setting webasset tags to be " + tags + " on node " + nodeRef);
                }
                nodeService.setProperty(nodeRef, PROP_TAGS, tags);
            }
            finally
            {
                behaviourFilter.enableBehaviour(nodeRef, ASPECT_WEBASSET);
            }
        }
    }
}
