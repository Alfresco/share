/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.enums.ViewType;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.WebElement;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the document library view type.
 * 
 * @author Chiran
 * @since 4.3
 */
public final class FactoryShareFileDirectoryInfo
{

    /**
     * Constructor.
     */
    private FactoryShareFileDirectoryInfo()
    {
    }

    /**
     * Gets the FileDirectoryInfo based on the given view type.
     * 
     * @param drone
     *            {@link org.alfresco.webdrone.WebDrone}
     * @param viewType
     * @return FileDirectoryInfo
     */
    public static FileDirectoryInfo getPage(final String nodeRef, final WebElement webElement, final WebDrone drone, final ViewType viewType)
    {
        try
        {
            switch (viewType)
            {
                case SIMPLE_VIEW:
                    return new SimpleViewFileDirectoryInfo(nodeRef, webElement, drone);
                case DETAILED_VIEW:
                    return new DetailedViewFileDirectoryInfo(nodeRef, webElement, drone);
                case GALLERY_VIEW:
                    return new GalleryViewFileDirectoryInfo(nodeRef, webElement, drone);
                case FILMSTRIP_VIEW:
                    return new FilmStripViewFileDirectoryInfo(nodeRef, webElement, drone);
                case TABLE_VIEW:
                    return new TableViewFileDirectoryInfo(nodeRef, webElement, drone);
                default:
                    throw new PageException(String.format("%s does not match any known file directory view name", viewType.name()));
            }
        }
        catch (Exception ex)
        {
            throw new PageException("FileDirecotyInfo View object can not be matched: " + viewType.name(), ex);
        }
    }
}
