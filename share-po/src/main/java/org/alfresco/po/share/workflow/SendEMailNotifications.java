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
 * This enum hold the Type field in Task Details (My Tasks page)
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum SendEMailNotifications
{

    YES("Yes"),
    NO("No");

    private String value;

    SendEMailNotifications(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Returns {@link org.alfresco.webdrone.share.workflow.SendEMailNotifications} based on given value.
     * 
     * @param value
     * @return {@link org.alfresco.webdrone.share.workflow.SendEMailNotifications}
     */
    public static SendEMailNotifications getValue(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (SendEMailNotifications notification : SendEMailNotifications.values())
        {
            if (value.equals(notification.value))
            {
                return notification;
            }
        }
        throw new IllegalArgumentException("Invalid SendEMailNotifications Value : " + value);
    }
}
