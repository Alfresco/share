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
 * Contains all the possible filters on Site Content Dashlet.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public enum SiteContentFilter
{
    I_AM_EDITING("I'm Editing"),
    I_HAVE_RECENTLY_MODIFIED("I've Recently Modified"),
    MY_FAVOURITES("My Favorites"),
    SYNCED_CONTENT("Synced content"),
    SYNCED_WITH_ERRORS("Synced with Errors");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private SiteContentFilter(String description)
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

    public static SiteContentFilter getFilter(String description)
    {
        for (SiteContentFilter filter : SiteContentFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

}