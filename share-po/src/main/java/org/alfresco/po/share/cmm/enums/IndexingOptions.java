
package org.alfresco.po.share.cmm.enums;

/**
 * The enum for Indexing Options for Properties
 * 
 * @author Meenal Bhave
 * @since 1.0
 */

public enum IndexingOptions
{
    None("cmm.property.index.none"),
    FreeText("cmm.property.index.freetext"),
    Basic("cmm.property.index.basic"),
    Enhanced("cmm.property.index.enhanced"),
    LOVWhole("cmm.property.index.lov.whole"),
    LOVPartial("cmm.property.index.lov.partial"),
    PatternUnique("cmm.property.index.pattern.unique"),
    PatternMany("cmm.property.index.pattern.many");

    private String listValue;

    IndexingOptions(String listVal)
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
