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
package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

/**
 * Abstract of an Alfresco Share site pages.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public abstract class SitePage extends SharePage
{
    /**
     * Checks that the current site is the same as
     * requested site name.
     * 
     * @return true if site pages match siteName
     */
    public boolean isSite(final String siteName)
    {
        String url = driver.getCurrentUrl();
        if (url != null && !url.isEmpty())
        {
            if (url.toLowerCase().contains(String.format("/site/%s/", siteName.toLowerCase())))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks the high lighted link in the site sub navigation
     * to verify which page we are on.
     * 
     * @return true if site document library is visible
     */
    public boolean isSitePage(final String pageTitle)
    {
        boolean displayed = false;
        try
        {
            String selector = "div.dijitSelected span";
            String title = findAndWait(By.cssSelector(selector)).getText();
            displayed = pageTitle.equalsIgnoreCase(title) ? true : false;
        }
        catch (NoSuchElementException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Get main navigation.
     * 
     * @return Navigation page object
     */
    public SiteNavigation getSiteNav()
    {
        return factoryPage.instantiatePage(driver, SiteNavigation.class).render();
    }

}
