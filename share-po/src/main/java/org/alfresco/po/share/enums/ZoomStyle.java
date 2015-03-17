/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share.enums;

/**
 * This ZoomStyle enums is used to select zoom in or out using the 4 states in
 * Gallery View FileDirectoryInfo.
 * 
 * @author cbairaajoni
 */
public enum ZoomStyle
{
    SMALLEST(0), 
    SMALLER(20), 
    BIGGER(40), 
    BIGGEST(60);

    private int size;

    private ZoomStyle(int size)
    {
        this.size = size;
    }

    /**
     * @return the size zoom Style.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Find {@link ZoomStyle} based on it is size.
     * 
     * @param size
     * @return {@link ZoomStyle}
     */
    public static ZoomStyle getZoomStyle(int size)
    {
        for (ZoomStyle style : ZoomStyle.values())
        {
            if (style.getSize() == size)
            {
                return style;
            }
        }
        throw new IllegalArgumentException("Invalid Size Value : " + size);
    }

}
