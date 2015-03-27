/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.workflow;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

import java.util.NoSuchElementException;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public final class FactoryShareWorkFlow
{

    /**
     * Constructor.
     */
    private FactoryShareWorkFlow()
    {
    }

    /**
     * Gets the sub WorkFlow HTML element from the StartWorkFlow page.
     *
     * @param drone     {@link org.alfresco.webdrone.WebDrone}
     * @param workFlowType WorkFlowType
     * @return workFlowType
     */
    @SuppressWarnings("unchecked")
    public static <T extends WorkFlowPage> T getPage(final WebDrone drone, final WorkFlowType workFlowType)
    {
        if (drone == null)
        {
            throw new IllegalArgumentException("WebDrone can't be null.");
        }
        if (workFlowType == null)
        {
            throw new IllegalArgumentException("Workflow Type can't be null.");
        }
        try
        {
            switch (workFlowType)
            {
                case NEW_WORKFLOW:
                    return (T) new NewWorkflowPage(drone);
                case REVIEW_AND_APPROVE:
                    return (T) new NewWorkflowPage(drone);
                case SEND_DOCS_FOR_REVIEW:
                    return (T) new NewWorkflowPage(drone);
                case POOLED_REVIEW_AND_APPROVE:
                    return (T) new NewWorkflowPage(drone);
                case CLOUD_TASK_OR_REVIEW:
                    return (T) new CloudTaskOrReviewPage(drone);
                case GROUP_REVIEW_AND_APPROVE:
                    return (T) new NewWorkflowPage(drone);
                default:
                    throw new PageException(String.format("%s does not match any known workflow name", workFlowType.name()));
            }
        }
        catch (NoSuchElementException ex)
        {
            throw new PageException("Workflow object can not be matched: " + workFlowType.name(), ex);
        }

    }

}