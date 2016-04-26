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

/**
 * @author Abhijeet Bharade
 */
public enum Priority
{
    HIGH("High", "1"),
    MEDIUM("Medium", "2"),
    LOW("Low", "3");

    private String priority;
    private String value;

    Priority(String priority, String value)
    {
        this.priority = priority;
        this.value = value;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Returns {@link Priority} based on given value.
     * 
     * @param stringValue String
     * @return {@link Priority}
     */
    public static Priority getPriority(String stringValue)
    {
        if (StringUtils.isEmpty(stringValue))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (Priority p : Priority.values())
        {
            if (stringValue.contains(p.priority))
            {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid Priority Value : " + stringValue);
    }
}
