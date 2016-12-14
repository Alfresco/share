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
package org.alfresco.po.share.site.document;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.enums.ViewType;
import org.openqa.selenium.WebDriver;
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
     * Gets the FileDirectoryInfo based on the given view type.
     * 
     * @param driver
     *            {@link org.alfresco.po.WebDriver}
     * @param viewType
     * @return FileDirectoryInfo
     */
    public static FileDirectoryInfo getPage(final String nodeRef, final WebElement webElement, final WebDriver driver, final ViewType viewType, FactoryPage factoryPage)
    {
        try
        {
            PageElement pe = null;
            switch (viewType)
            {
                case SIMPLE_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, SimpleViewFileDirectoryInfo.class);
                    break;
                case DETAILED_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, DetailedViewFileDirectoryInfo.class);
                    break;
                case TABLE_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, DetailedTableViewFileDirectoryInfo.class);
                    break;
                case GALLERY_VIEW:
                    pe = factoryPage.instantiatePageElement(driver, GalleryViewFileDirectoryInfo.class);
                    break;
                default:
                    throw new PageException(String.format("%s does not match any known file directory view name", viewType.name()));
            }
            FileDirectoryInfoImpl fdi = (FileDirectoryInfoImpl)pe;
            fdi.setNodeRef(nodeRef);
            fdi.setWrappedElement(webElement);
            return fdi;
        }
        catch (Exception ex)
        {
            throw new PageException("FileDirecotyInfo View object can not be matched: " + viewType.name(), ex);
        }
    }
}
