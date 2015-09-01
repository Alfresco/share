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

package org.alfresco.po.share.cmm.enums;

import org.alfresco.po.share.FactoryPage;

/**
 * The Data type enum.
 * 
 * @author Meenal Bhave
 * @since 1.0
 */


public enum DataType
{
    // Removed in line with JIRA SHA-785
    // Any("cmm.property.datatype.any"),
    // Encrypted("cmm.property.datatype.encrypted"),
    Text("cmm.property.datatype.text"),
    MlText("cmm.property.datatype.mltext"),
    // SHA: 787, 1260: Removal of cm:content from the Property data types. Re-factor MlTextContent to Content when Content is re-introduced
    Content("cmm.property.datatype.content"),
    MlTextContent("cmm.property.datatype.mltext"),
    Int("cmm.property.datatype.int"),
    Long("cmm.property.datatype.long"),
    Float("cmm.property.datatype.float"),
    Double("cmm.property.datatype.double"),
    Date("cmm.property.datatype.date"),
    DateTime("cmm.property.datatype.datetime"),
    Boolean("cmm.property.datatype.boolean");

    private String listValue;

    DataType(String listValue)
    {
        this.listValue = listValue;
    }

    /**
     * Gets the list value.
     * 
     * @return the classifier for list value
     */
    public String getListValue()
    {
        return this.listValue;
    }

    /**
     * Find by list value.
     * 
     * @param listValue the list value
     * @param drone the drone
     * @return the data type
     */
    public static DataType findByListValue(String listValue, FactoryPage factoryPage)
    {
        if (listValue != null)
        {
            for (DataType dataType : DataType.values())
            {
                if (listValue.equalsIgnoreCase(factoryPage.getValue(dataType.getListValue())))
                {
                    return dataType;
                }
            }
        }

        return null;
    }
}