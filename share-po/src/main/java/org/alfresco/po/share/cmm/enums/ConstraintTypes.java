
package org.alfresco.po.share.cmm.enums;

/**
 * The Constraint Types enum.
 * 
 * @author Meenal Bhave
 * @since 1.0
 */

public enum ConstraintTypes
{
    None("cmm.property.constraint.none"),
    REGEX("cmm.property.constraint.regex"),
    MINMAXLENGTH("cmm.property.constraint.length"),
    MINMAXVALUE("cmm.property.constraint.minmax"),
    LIST("cmm.property.constraint.list"),
    JAVACLASS("cmm.property.constraint.class");

    private String listValue;

    ConstraintTypes(String listVal)
    {
        this.listValue = listVal;
    }

    /**
     * @return the classifier for list value
     */
    public String getListValue()
    {
        return listValue;
    }

}
