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
