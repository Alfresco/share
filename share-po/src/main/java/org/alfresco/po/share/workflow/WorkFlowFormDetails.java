/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.workflow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is workflow object to hold info for workflow form details.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class WorkFlowFormDetails
{

    private String siteName;
    private List<String> reviewers;
    private String assignee;
    private int approvalPercentage;
    private String message;
    private String dueDate;
    private TaskType taskType = TaskType.SIMPLE_CLOUD_TASK;
    private  Priority taskPriority = Priority.MEDIUM;
    private KeepContentStrategy contentStrategy = KeepContentStrategy.KEEPCONTENTREMOVESYNC;
    private boolean lockOnPremise;

    public WorkFlowFormDetails(String siteName, String message, List<String> reviewers)
    {
        this.siteName = siteName;
        this.message = message;
        this.reviewers = reviewers;
    }

    /**
     * 
     */
    public WorkFlowFormDetails()
    {
        this.siteName = "";
        this.message = "";
        this.reviewers = new ArrayList<String> ();
    }

    /**
     * @return the dueDate
     */
    public String getDueDate()
    {
        return dueDate;
    }

    /**
     * @param dueDate
     *            the dueDate to set
     */
    public void setDueDate(String dueDate)
    {
        String datePattern = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(dueDate);
        if (StringUtils.isNotEmpty(dueDate))
        {
            if (matcher.matches())
            {
                this.dueDate = dueDate;
            }
            else
            {
                throw new UnsupportedOperationException("Date pattern should match dd/mm/yyyy");
            }
        }

    }

    /**
     * @return the taskPriority
     */
    public Priority getTaskPriority()
    {
        return taskPriority;
    }

    /**
     * @param taskPriority
     *            the taskPriority to set
     */
    public void setTaskPriority(Priority taskPriority)
    {
        this.taskPriority = taskPriority;
    }

    /**
     * @return the contentStrategy
     */
    public KeepContentStrategy getContentStrategy()
    {
        return contentStrategy;
    }

    /**
     * @param contentStrategy
     *            the contentStrategy to set
     */
    public void setContentStrategy(KeepContentStrategy contentStrategy)
    {
        this.contentStrategy = contentStrategy;
    }

    /**
     * @return the taskType
     */
    public TaskType getTaskType()
    {
        return taskType;
    }

    /**
     * @param taskType
     *            the taskType to set
     */
    public void setTaskType(TaskType taskType)
    {
        this.taskType = taskType;
    }

    /**
     * @return the lockOnPremise
     */
    public boolean isLockOnPremise()
    {
        return lockOnPremise;
    }

    /**
     * @param lockOnPremise
     *            the lockOnPremise to set
     */
    public void setLockOnPremise(boolean lockOnPremise)
    {
        this.lockOnPremise = lockOnPremise;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    public List<String> getReviewers()
    {
        return reviewers;
    }

    public void setReviewers(List<String> reviewers)
    {
        this.reviewers = reviewers;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public int getApprovalPercentage()
    {
        return approvalPercentage;
    }

    public void setApprovalPercentage(int approvalPercentage)
    {
        this.approvalPercentage = approvalPercentage;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkFlowFormDetails [siteName=");
        builder.append(siteName);
        builder.append(", reviewers=");
        builder.append(reviewers.toString());
        builder.append(", approvalPercentage=");
        builder.append(approvalPercentage);
        builder.append(", message=");
        builder.append(message);
        builder.append(", dueDate=");
        builder.append(dueDate);
        builder.append(", taskPriority=");
        builder.append(taskPriority);
        builder.append(", contentStrategy=");
        builder.append(contentStrategy);
        builder.append(", lockOnPremise=");
        builder.append(lockOnPremise);
        builder.append("]");
        return builder.toString();
    }

}
