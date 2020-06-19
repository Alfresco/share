/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.web.scripts.forms;

import java.util.Comparator;

public class OptionsComparator implements Comparator<String>
{
    /**
     * <p>The delimiter that marks the beginning of the label.</p>
     */
    public static final String delimiter = "|";
    
    /**
     * <p>Uses the default <code>compareTo</code> method defined by the {@link Comparable} interface
     * of {@link String} to compare labels returned from calls to <code>getLabel</code>.
     */
    @Override
    public int compare(String o1, String o2)
    {
        return getLabel(o1).compareTo(getLabel(o2));
    }

    /**
     * <p>Get the label from the supplied String. The label begins after the delimiter. The 
     * label is used as the basis for a natural sort. It is converted into lower case as a 
     * natural sort will place ALL upper-case characters before lower-case ones. If the 
     * delimiter is not found then the supplied string is returned.</p>
     * 
     * @param s The String to find the label within.
     * @return The label within the string converted to lower case or the supplied string if
     * the delimiter cannot be found.
     */
    protected String getLabel(String s)
    {
        String label = null;
        int delimiterIndex = s.indexOf(delimiter);
        if (delimiterIndex != -1)
        {
            label = s.substring(delimiterIndex);
        }
        else
        {
            label = s;
        }
        return label.toLowerCase();
    }
    
}
