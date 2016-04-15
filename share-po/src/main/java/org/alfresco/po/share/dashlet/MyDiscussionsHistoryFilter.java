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

package org.alfresco.po.share.dashlet;

/**
 * Contains topics history filters
 * 
 * @author jcule
 */
public enum MyDiscussionsHistoryFilter
{

    LAST_DAY_TOPICS("Topics updated in the last day"), 
    SEVEN_DAYS_TOPICS("Topics updated in the last 7 days"), 
    FOURTEEN_DAYS_TOPICS("Topics updated in the last 14 days"), 
    TWENTY_EIGHT_DAYS_TOPICS("Topics updated in the last 28 days");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private MyDiscussionsHistoryFilter(String description)
    {
        this.description = description;
    }

    /**
     * Gets description.
     * 
     * @return String description
     */
    public String getDescription()
    {
        return this.description;
    }

    public static MyDiscussionsHistoryFilter getFilter(String description)
    {
        for (MyDiscussionsHistoryFilter filter : MyDiscussionsHistoryFilter.values())
        {
            if (description.contains(filter.getDescription()))
                return filter;
        }
        return null;
    }

}
