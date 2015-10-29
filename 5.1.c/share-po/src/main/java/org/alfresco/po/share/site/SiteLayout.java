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
package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * Different types Site Layouts.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public enum SiteLayout
{

    THREE_COLUMN_WIDE_CENTRE(By.cssSelector("button[id*='select-button-dashboard-3-column']"));

    private By by;

    private SiteLayout(By by)
    {
        this.by = by;
    }

    /**
     * Get Locator for the given {@link SiteLayout}.
     * 
     * @return {@link By}
     */
    public By getLocator()
    {
        return this.by;
    }

}
