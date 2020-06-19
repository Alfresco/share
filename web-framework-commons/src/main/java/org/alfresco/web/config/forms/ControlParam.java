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
package org.alfresco.web.config.forms;
 
/**
 * This class represents a single control-param configuration item.
 * 
 * @author Neil McErlean.
 */
public class ControlParam
{
    private final String name;
    private String value;

    /**
     * Constructs a ControlParam object with the specified name and value.
     * 
     * @param name the name of the param.
     * @param value the value associated with that name.
     */
    public ControlParam(String name, String value)
    {
    	if (value == null)
    	{
    		value = "";
    	}
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of this ControlParam.
     * @return the param name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the value of this ControlParam.
     * @return the value.
     */
    public String getValue()
    {
        return value;
    }
    
    /* default */ void setValue(String newValue)
    {
    	this.value = newValue;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(name).append(":").append(value);
        return result.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + 7 * value.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == this)
        {
            return true;
        }
        else if (otherObj == null
                || !otherObj.getClass().equals(this.getClass()))
        {
            return false;
        }
        ControlParam otherCP = (ControlParam) otherObj;
        return otherCP.name.equals(this.name)
                && otherCP.value.equals(this.value);
    }
}