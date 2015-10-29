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

package org.alfresco.po.share.dashlet;

/**
 * Contains My Discussion topics filters
 * 
 * @author jcule
 */
public enum MyDiscussionsTopicsFilter
{

    MY_TOPICS("My Topics"), 
    ALL_TOPICS("All Topics");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private MyDiscussionsTopicsFilter(String description)
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

    public static MyDiscussionsTopicsFilter getFilter(String description)
    {
        for (MyDiscussionsTopicsFilter filter : MyDiscussionsTopicsFilter.values())
        {
            if (description.contains(filter.getDescription()))
                return filter;
        }
        return null;
    }

}
