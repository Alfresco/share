
package org.alfresco.po.share.cmm.enums;

/**
 * The Data type enum.
 * 
 * @author Meenal Bhave
 * @since 1.0
 */

public enum MandatoryClassifier
{
    Optional("cmm.property.optional"), MANDATORYENF("cmm.property.mandatory"), Mandatory("cmm.property.mandatory");

    private String listValue;

    MandatoryClassifier(String listVal)
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
