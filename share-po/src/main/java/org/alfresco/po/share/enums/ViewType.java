package org.alfresco.po.share.enums;

import org.alfresco.po.share.util.PageUtils;

/**
 * Enum to contain all the view Type
 * 
 * @author Chiran
 * @author Shan Nagarajan
 */
public enum ViewType
{
    SIMPLE_VIEW("Simple"),
    DETAILED_VIEW("Detailed"),
    TABLE_VIEW("Table"),
    AUDIO_VIEW("Audio"),
    MEDIA_VIEW("media_table");

    private String name;

    private ViewType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Finds the view type based on the name passed.
     * 
     * @param name String
     * @return {@link ViewType}
     */
    public static ViewType getViewType(String name)
    {
        PageUtils.checkMandotaryParam("name", name);

        if (name.equalsIgnoreCase((SIMPLE_VIEW.getName())))
        {
            return SIMPLE_VIEW;
        }
        else if (name.equalsIgnoreCase((DETAILED_VIEW.getName())))
        {
            return DETAILED_VIEW;
        }
        else if (name.equalsIgnoreCase((TABLE_VIEW.getName())))
        {
            return TABLE_VIEW;
        }
        else if (name.equalsIgnoreCase((AUDIO_VIEW.getName())))
        {
            return AUDIO_VIEW;
        }
        else if (name.equalsIgnoreCase((MEDIA_VIEW.getName())))
        {
            return MEDIA_VIEW;
        }

        throw new IllegalArgumentException("Not able to find the view type for give name: " + name);
    }

}
