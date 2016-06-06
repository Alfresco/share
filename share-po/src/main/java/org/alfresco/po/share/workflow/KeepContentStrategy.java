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
public enum KeepContentStrategy
{

    KEEPCONTENT("Keep content synced on cloud", "documentsSynced"),
    KEEPCONTENTREMOVESYNC("Keep content on cloud and remove sync", "documentsUnSynced"),
    DELETECONTENT("Delete content on cloud and remove sync", "documentsDelete");

    private String strategy;
    private String value;

    KeepContentStrategy(String strategy, String value)
    {
        this.strategy = strategy;
        this.value = value;
    }

    public String getStrategy()
    {
        return strategy;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Returns {@link KeepContentStrategy} based on given value.
     * 
     * @param stringValue String
     * @return {@link KeepContentStrategy}
     */
    public static KeepContentStrategy getKeepContentStrategy(String stringValue)
    {
        if (StringUtils.isEmpty(stringValue))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (KeepContentStrategy strategy : KeepContentStrategy.values())
        {
            if (stringValue.equalsIgnoreCase(strategy.strategy))
            {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Invalid Keep Content Strategy Value : " + stringValue);
    }

}
