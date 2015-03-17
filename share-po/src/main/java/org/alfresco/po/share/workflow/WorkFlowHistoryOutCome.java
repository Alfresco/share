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
 * This enum hold the WorkFlow History OutCome
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum WorkFlowHistoryOutCome
{

    TASK_DONE("Task Done"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private String outCome;

    WorkFlowHistoryOutCome(String outCome)
    {
        this.outCome = outCome;
    }

    public String getOutCome()
    {
        return outCome;
    }

    /**
     * Returns {@link org.alfresco.webdrone.share.workflow.WorkFlowHistoryOutCome} based on given value.
     * 
     * @param value
     * @return {@link org.alfresco.webdrone.share.workflow.WorkFlowHistoryOutCome}
     */
    public static WorkFlowHistoryOutCome getWorkFlowHistoryOutCome(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (WorkFlowHistoryOutCome historyOutCome : WorkFlowHistoryOutCome.values())
        {
            if (value.equals(historyOutCome.outCome))
            {
                return historyOutCome;
            }
        }
        throw new IllegalArgumentException("Invalid WorkFlowHistoryOutCome Value : " + value);
    }
}
