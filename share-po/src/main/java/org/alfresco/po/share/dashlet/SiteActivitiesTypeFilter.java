/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * Contains the Type filters on Site Activities Dashlet.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public enum SiteActivitiesTypeFilter
{
    ALL_ITEMS("all items"),
    STATUS_UPDATES("status updates"),
    COMMENTS("comments"),
    CONTENT("content"),
    MEMBERSHIPS("memberships");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private SiteActivitiesTypeFilter(String description)
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

    public static SiteActivitiesTypeFilter getFilter(String description)
    {
        for (SiteActivitiesTypeFilter filter : SiteActivitiesTypeFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

}