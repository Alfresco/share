/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.enums;

/**
 * Enum to contain all Site Visibility options.
 */
public enum SiteVisibility
{

    PUBLIC("Public"),
    MODERATED("Moderated"),
    PRIVATE("Private");

    private String displayValue;

    /**
     * Instantiates a new site visibility.
     * 
     * @param displayValue the display value
     */
    private SiteVisibility(String displayValue)
    {
        this.displayValue = displayValue;
    }

    /**
     * Gets the display value.
     * 
     * @return the display value
     */
    public String getDisplayValue()
    {
        return displayValue;
    }

    /**
     * Gets the enum.
     * 
     * @param displayValue the display value
     * @return the enum
     */
    public static SiteVisibility getEnum(String displayValue)
    {
        if (displayValue == null)
        {
            throw new IllegalArgumentException();
        }
        for (SiteVisibility val : values())
        {
            if (displayValue.equalsIgnoreCase(val.getDisplayValue()))
            {
                return val;
            }
        }
        throw new IllegalArgumentException();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return this.getDisplayValue();
    }
}