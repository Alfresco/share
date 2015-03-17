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
import org.openqa.selenium.By;

/**
 * This enum holda the task type details needed for workflow form P
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public enum TaskType
{

    SIMPLE_CLOUD_TASK(By.cssSelector("option[value='task']"), "Simple Cloud Task"),
    CLOUD_REVIEW_TASK(By.cssSelector("option[value='review']"), "Cloud Review Task");

    public By getSelector()
    {
        return selector;
    }

    private By selector;
    private String type;

    TaskType(By selector, String type)
    {
        this.selector = selector;
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    /**
     * Returns {@link TaskType} based on given value.
     * 
     * @param value
     * @return {@link TaskType}
     */
    public static TaskType getTaskType(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (TaskType type : TaskType.values())
        {
            if (value.equals(type.type))
            {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Tasktype Value: " + value);
    }
}
