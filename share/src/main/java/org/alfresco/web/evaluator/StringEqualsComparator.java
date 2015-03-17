/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.web.evaluator;

/**
 * Compares a node value against an optionally case-insensitive value
 *
 * @author: mikeh
 */
public class StringEqualsComparator implements Comparator
{
    private Boolean caseInsensitive = true;
    private String value = null;

    /**
     * Setter for case insensitive comparison override
     *
     * @param caseInsensitive
     */
    public void setCaseInsensitive(Boolean caseInsensitive)
    {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Setter for static string value to compare to
     *
     * @param value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public boolean compare(Object nodeValue)
    {
        if (nodeValue == null)
        {
            return false;
        }
        
        if (caseInsensitive)
        {
            return nodeValue.toString().equalsIgnoreCase(this.value);
        }
        return nodeValue.toString().equals(this.value);
    }
}
