/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.po.share.cmm.admin;

import org.alfresco.po.share.cmm.enums.ConstraintTypes;

/**
 * Representation of Content Details that can be used while create/edit the Property.
 * 
 * @author Meenal Bhave
 * @since 1.0
 */
public class ConstraintDetails
{
    private ConstraintTypes constraintType;

    private String value;

    private int minValue;

    private int maxValue;

    private boolean matchRequired;

    private boolean sorted;

    public ConstraintDetails(ConstraintTypes type, String value, boolean matchRequired, boolean sorted, int minValue, int maxValue)
    {
        this.constraintType = type;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setMatchRequired(matchRequired);
        this.setSorted(sorted);
    }

    public ConstraintDetails(ConstraintTypes type)
    {
        this.constraintType = type;
        this.setMatchRequired(false);
        this.setSorted(false);
    }

    public ConstraintDetails()
    {
        this.setType(ConstraintTypes.None);
        this.setMatchRequired(false);
        this.setSorted(false);
    }

    public ConstraintTypes getType()
    {
        return constraintType;
    }

    public String getListValue()
    {
        return constraintType.getListValue();
    }

    public void setType(ConstraintTypes type)
    {
        this.constraintType = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getMinValue()
    {
        return Integer.toString(minValue);
    }

    public void setMinValue(int minValue)
    {
        this.minValue = minValue;
    }

    public String getMaxValue()
    {
        return Integer.toString(maxValue);
    }

    public void setMaxValue(int maxValue)
    {
        this.maxValue = maxValue;
    }

    public boolean isMatchRequired()
    {
        // SHA: 961: Removal of Regex Match Required option = False
        // return matchRequired;
        return true;
    }

    public void setMatchRequired(boolean matchRequired)
    {
        // SHA: 961: Removal of Regex Match Required option = False
        // this.matchRequired = matchRequired;
        this.matchRequired = true;
    }

    public boolean isSorted()
    {
        return sorted;
    }

    public void setSorted(boolean sorted)
    {
        this.sorted = sorted;
    }
}
