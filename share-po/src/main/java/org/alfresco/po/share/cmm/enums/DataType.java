
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