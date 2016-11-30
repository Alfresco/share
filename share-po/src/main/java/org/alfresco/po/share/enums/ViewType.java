/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
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
    MEDIA_VIEW("media_table"),
    GALLERY_VIEW("Gallery");

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
        PageUtils.checkMandatoryParam("name", name);

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
        else if (name.equalsIgnoreCase((GALLERY_VIEW.getName())))
        {
            return GALLERY_VIEW;
        }

        throw new IllegalArgumentException("Not able to find the view type for give name: " + name);
    }

}
