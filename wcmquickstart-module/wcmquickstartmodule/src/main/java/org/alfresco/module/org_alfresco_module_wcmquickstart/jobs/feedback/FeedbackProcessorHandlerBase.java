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

import org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.FeedbackProcessor;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * Feedback processor handler base class.
 * 
 * @author Roy Wetherall
 */
public abstract class FeedbackProcessorHandlerBase implements FeedbackProcessorHandler, WebSiteModel
{
    /** Node service */
    protected NodeService nodeService;
    
    /** Site helper */
    protected SiteHelper siteHelper;
    
    /** Feedback processor */
    private FeedbackProcessor feedbackProcessor;
    
    /** Feedback type */
    private String feedbackType;
    
    /**
     * Init method.  Registers this bean with the feedback processor so it it delegated to 
     * when a feedback node of the specified type is found.
     */
    public void init()
    {
        // Register this class with the feedback processor
        feedbackProcessor.registerHandler(this);
    }
    
    /**
     * Sets the node service
     * @param nodeService   node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Sets the site helper
     * @param siteHelper    site helper
     */
    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }
    
    /**
     * Sets the feedback processor
     * @param feedbackProcessor feedback processor
     */
    public void setFeedbackProcessor(FeedbackProcessor feedbackProcessor)
    {
        this.feedbackProcessor = feedbackProcessor;
    }
    
    /**
     * Sets the feedback type this handler deals with.
     * @param feedbackType  feedback type
     */
    public void setFeedbackType(String feedbackType)
    {
        this.feedbackType = feedbackType;
    }
    
    /**
     * @see org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback.FeedbackProcessorHandler#getFeedbackType()
     */
    public String getFeedbackType()
    {
        return feedbackType;
    }    
    
    /**
     * Default implementation does nothing.
     * @see org.alfresco.module.org_alfresco_module_wcmquickstart.jobs.feedback.FeedbackProcessorHandler#processorCallback()
     */
    @Override
    public void processorCallback()
    {
    }
}
