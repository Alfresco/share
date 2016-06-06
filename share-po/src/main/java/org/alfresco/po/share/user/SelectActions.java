package org.alfresco.po.share.user;

/**
 * All the properites of select String Enum
 * 
 * @author sprasanna
 * @since v1.7
 */
public enum SelectActions
{
    ALL("Select All"),
    INVERT("Invert Selection"), 
    NONE("None");

    private String selection;

    private SelectActions(String type)
    {
        selection = type;
    }

    public String getSelectAction()
    {
        return selection;
    }

}
