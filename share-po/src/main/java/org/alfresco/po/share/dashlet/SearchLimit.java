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
 * Enum to handle Search Limit drop down.
 * 
 * @author Ranjith Manyam
 * @since 1.9
 */
public enum SearchLimit
{
    TEN(10),
    TWENTY_FIVE(25),
    FIFTY(50),
    HUNDRED(100);

    private int value;

    SearchLimit(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    /**
     * Returns {@link SearchLimit} based on given value.
     * 
     * @param intValue
     * @return {@link SearchLimit}
     */
    public static SearchLimit getSearchLimit(int intValue)
    {
        for (SearchLimit limit : SearchLimit.values())
        {
            if (intValue == limit.value)
            {
                return limit;
            }
        }
        throw new IllegalArgumentException("Invalid SearchLimit Value : " + intValue);
    }
}
