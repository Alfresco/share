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
package org.alfresco.po.share.search;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.WebElement;

import java.util.NoSuchElementException;

/**
 * Site search results page object, holds all element of the html page relating to
 * search results. This is the same as the other search result page but has then specific
 * render logic.
 * 
 * @author Michael Suzuki
 * @since 1.4
 */
public class SiteResultsPage extends SearchResultsPage
{

    /**
     * Constructor.
     */
    public SiteResultsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteResultsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteResultsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteResultsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return {@link SiteResultsPage} page response
     */
    public SiteResultsPage search(final String term)
    {
        searchFor(term);
        return new SiteResultsPage(drone);
    }

    /**
     * Method to verify if 'Back to..Site' link is displayed
     *
     * @param siteName String
     * @return true if displayed
     */
    public boolean isBackToSiteDisplayed(String siteName)
    {
        boolean isDisplayed = false;
        try
        {
            WebElement backLink = drone.find(BACK_TO_SITE_LINK);
            if(backLink.getText().contains(siteName))
                isDisplayed = true;
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        return isDisplayed;
    }
}
