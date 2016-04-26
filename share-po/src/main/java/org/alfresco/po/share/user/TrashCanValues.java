package org.alfresco.po.share.user;

/**
 * EnumValues of TrashCan Iem
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */
public enum TrashCanValues
{
    RECOVER("Recover"),
    DELETE("Delete"),
    FILE("File"),
    FOLDER("Folder"),
    SITE("Site");

    private String selection;

    private TrashCanValues(String type)
    {
        selection = type;
    }

    public String getTrashCanValues()
    {
        return selection;
    }
}
