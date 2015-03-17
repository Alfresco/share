/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Repository page object, holds all element of the HTML page relating to
 * share's repository page.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
public class RepositoryPage extends DocumentLibraryPage
{
    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public RepositoryPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     * @param subfolderName a subfolder name when drilling down
     */
    public RepositoryPage(WebDrone drone, final String subfolderName)
    {
        super(drone, subfolderName);
    }

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     * @param shouldHaveFiles a subfolder name when drilling down
     */
    public RepositoryPage(WebDrone drone, final boolean shouldHaveFiles)
    {
        super(drone);
    }

    @Override
    public RepositoryPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @Override
    public RepositoryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public RepositoryPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        return isBrowserTitle("Repository Browser");
    }
}
