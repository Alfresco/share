package org.alfresco.po.share.workflow;

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

import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the WorkFlow History Type
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowHistoryType
{

    VERIFY_TASK_COMPLETED_ON_CLOUD("Verify task was completed on the cloud"),
    START_TASK_OR_REVIEW_ON_CLOUD("Start a task or review on Alfresco Cloud"),
    DOCUMENT_WAS_REJECTED_ON_CLOUD("Document was rejected on the cloud"),
    DOCUMENT_WAS_APPROVED_ON_CLOUD("Document was approved on the cloud"),
    TASK("Task"),
    REVIEW("Review"),
    START_REVIEW("Start Review");

    private String type;

    WorkFlowHistoryType(String type)
    {
        this.type = type;
    }

    public String getDescription()
    {
        return type;
    }

    /**
     * Returns {@link WorkFlowHistoryType} based on given value.
     * 
     * @param value String
     * @return {@link WorkFlowHistoryType}
     */
    public static WorkFlowHistoryType getWorkFlowHistoryType(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowHistoryType historyType : WorkFlowHistoryType.values())
        {
            if (value.equals(historyType.type))
            {
                return historyType;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowHistoryType Value : " + value);
    }
}
