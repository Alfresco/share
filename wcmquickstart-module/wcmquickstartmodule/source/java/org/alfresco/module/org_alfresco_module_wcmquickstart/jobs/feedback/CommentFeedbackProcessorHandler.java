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
package org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Comment feedback processor handler.
 * 
 * @author Roy Wetherall
 */
public class CommentFeedbackProcessorHandler extends FeedbackProcessorHandlerBase
{
    /** Logger */
    private static final Log log = LogFactory.getLog(CommentFeedbackProcessorHandler.class);
    
    /** Container name */
    private static final String FEEDBACK_SUMMARIES_CONTAINER_NAME = "feedbackSummaries";
    
    /** Summary map key */
    private static final String KEY_SUMMARY_MAP = "summaryMap";
    
    /**
     * @see org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback.FeedbackProcessorHandler#processFeedback(org.alfresco.service.cmr.repository.NodeRef)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processFeedback(NodeRef feedback)
    {
        // Get the node summary map
        HashMap<NodeRef, SummaryInfo> nodeSummaryMap = (HashMap<NodeRef, SummaryInfo>)AlfrescoTransactionSupport.getResource(KEY_SUMMARY_MAP);
        if (nodeSummaryMap == null)
        {
            nodeSummaryMap = new HashMap<NodeRef, SummaryInfo>(87);
            AlfrescoTransactionSupport.bindResource(KEY_SUMMARY_MAP, nodeSummaryMap);
        }
        
        //Get the asset to which this feedback relates
        NodeRef relatedAsset = (NodeRef)nodeService.getProperty(feedback, PROP_RELEVANT_ASSET);         
        if (relatedAsset != null)
        {
            //and check whether it has a feedback summary node associated with it
            SummaryInfo info = nodeSummaryMap.get(relatedAsset);
            if (info == null &&
                nodeService.exists(relatedAsset) == true)
            {
                //We haven't come across this asset previously in this run, so we need to look for a summary node for it
                List<AssociationRef> assocs = nodeService.getSourceAssocs(relatedAsset, WebSiteModel.ASSOC_SUMMARISED_ASSET);
                NodeRef summaryNode = null;
                if (assocs.isEmpty())
                {
                    //There is no summary node currently. Create one.
                    //If the asset is in a Share site (which it probably is) then place the summary node in a
                    //specific container named "feedbackSummaries"...
                    NodeRef summaryParent = siteHelper.getWebSiteContainer(relatedAsset, FEEDBACK_SUMMARIES_CONTAINER_NAME);
                    if (summaryParent != null)
                    {
                        String name = "FeedbackSummary_" + relatedAsset.getId();
                        HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
                        props.put(ContentModel.PROP_NAME, name);
                        props.put(WebSiteModel.PROP_AVERAGE_RATING, 0.0);
                        props.put(WebSiteModel.PROP_PROCESSED_RATINGS, 0);
                        props.put(WebSiteModel.PROP_COMMENT_COUNT, 0);
                        props.put(WebSiteModel.PROP_SUMMARISED_ASSET, relatedAsset);
                        summaryNode = nodeService.createNode(summaryParent, ContentModel.ASSOC_CONTAINS, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name), 
                                WebSiteModel.TYPE_VISITOR_FEEDBACK_SUMMARY, props).getChildRef();
                        nodeService.createAssociation(summaryNode, relatedAsset, WebSiteModel.ASSOC_SUMMARISED_ASSET);
                        if (log.isDebugEnabled())
                        {
                            log.debug("Created a new feedback summary node for asset " + relatedAsset);
                        }
                    }
                    else
                    {
                        if (log.isDebugEnabled() == true)
                        {
                            log.debug("Unable to create feedback summary node for asset " + relatedAsset + " because parent container could not be found.");
                        }
                    }
                }
                else
                {
                    //There is an existing summary node to use
                    summaryNode = assocs.get(0).getSourceRef();
                    if (log.isDebugEnabled())
                    {
                        log.debug("Found an existing feedback summary node for asset " + relatedAsset);
                    }
                }
                //Create and record a SummaryInfo object in which to gather data for this asset
                info = new SummaryInfo(summaryNode);
                nodeSummaryMap.put(relatedAsset, info);
            }
            if (nodeService.getProperty(feedback, WebSiteModel.PROP_COMMENT) != null)
            {
                info.commentCount++;
            }
            Integer rating = (Integer)nodeService.getProperty(feedback, WebSiteModel.PROP_RATING);
            if (rating != null)
            {
                info.totalRating += rating;
                info.ratingCount++;
            }
        }
        else
        {
            if (log.isInfoEnabled())
            {
                log.info("Skipping a piece of feedback that is related to no asset: " + feedback);
            }
        }        
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void processorCallback()
    {
        // Get the node summary map
        HashMap<NodeRef, SummaryInfo> nodeSummaryMap = (HashMap<NodeRef, SummaryInfo>)AlfrescoTransactionSupport.getResource(KEY_SUMMARY_MAP);
        if (nodeSummaryMap != null)
        {                 
            //Now we can work through the records that we've recorded in memory and update the necessary summary nodes
            for (Map.Entry<NodeRef, SummaryInfo> entry : nodeSummaryMap.entrySet())
            {
                SummaryInfo summaryInfo = entry.getValue();
                NodeRef summaryNode = summaryInfo.summaryNode;
                
                Map<QName,Serializable> props = nodeService.getProperties(summaryNode);
                
                //Get the current values from the summary node
                Integer commentCountObj = (Integer)nodeService.getProperty(summaryNode, WebSiteModel.PROP_COMMENT_COUNT);
                Integer processedRatingsObj = (Integer)nodeService.getProperty(summaryNode, WebSiteModel.PROP_PROCESSED_RATINGS);
                Float averageRatingObj = (Float)nodeService.getProperty(summaryNode, WebSiteModel.PROP_AVERAGE_RATING);
                
                int commentCount = commentCountObj == null ? 0 : commentCountObj.intValue();
                int processedRatings = processedRatingsObj == null ? 0 : processedRatingsObj.intValue();
                float averageRating = averageRatingObj == null ? 0 : averageRatingObj.floatValue();
                
                if (log.isDebugEnabled())
                {
                    log.debug("About to update feedback summary for asset " + entry.getKey() + ". Current values are: " +
                            "commentCount = " + commentCount + "; processedRatings = " + processedRatings + "; averageRating = " + averageRating);
                }
                
                //Update the values with the information gathered in the SummaryInfo object...
                commentCount += summaryInfo.commentCount;
                float totalRatingSoFar = averageRating * processedRatings;
                if (summaryInfo.ratingCount > 0)
                {
                    processedRatings += summaryInfo.ratingCount;
                    totalRatingSoFar += summaryInfo.totalRating;
                    averageRating = totalRatingSoFar / processedRatings;
                }
                if (log.isDebugEnabled())
                {
                    log.debug("About to update feedback summary for asset " + entry.getKey() + ". New values are: " +
                            "commentCount = " + commentCount + "; processedRatings = " + processedRatings + "; averageRating = " + averageRating);
                }
                props.put(WebSiteModel.PROP_COMMENT_COUNT, commentCount);
                props.put(WebSiteModel.PROP_PROCESSED_RATINGS, processedRatings);
                props.put(WebSiteModel.PROP_AVERAGE_RATING, averageRating);
                // ... and write the new values back to the repo.
                nodeService.setProperties(summaryNode, props);
            }
        }
    }
 
    /**
     * Summary information class
     */
    private static class SummaryInfo
    {
        public int commentCount = 0;
        public int totalRating = 0;
        public int ratingCount = 0;
        public final NodeRef summaryNode;
        
        public SummaryInfo(NodeRef summaryNode)
        {
            this.summaryNode = summaryNode;
        }
    }

}
