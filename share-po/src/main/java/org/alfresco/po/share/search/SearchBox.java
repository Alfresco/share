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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the html page relating to the search
 * functionality.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.1
 */
public class SearchBox extends HtmlElement
{
    private final Log logger = LogFactory.getLog(SearchBox.class);
    private final By selector = By.id("HEADER_SEARCHBOX_FORM_FIELD");

    /**
     * Constructor.
     */
    public SearchBox(WebDrone drone, boolean isDojoSupport)
    {
        super(drone);
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search.
     * 
     * @param term String term to search
     * @return true when actioned
     */
    public HtmlPage search(final String term)
    {
        if (term == null || term.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        try
        {

            WebElement input = drone.findAndWait(selector);
            input.clear();
            input.sendKeys(term + "\n");
            if (logger.isTraceEnabled())
            {
                logger.trace("Apply search on the keyword: " + term);
            }

        }
        catch (NoSuchElementException nse)
        {
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Performs the live search by typing the term into search field
     * 
     * @param term String term to search
     * @return true when actioned
     */
    public LiveSearchDropdown liveSearch(final String term)
    {
        if (term == null || term.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        try
        {

            WebElement input = drone.findAndWait(selector);
            input.clear();
            input.sendKeys(term);
            input.click();
            if (logger.isTraceEnabled())
            {
                logger.trace("Apply live search on the keyword: " + term);
            }
            return new LiveSearchDropdown(drone);
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Live search not displayed.");
        }
    }

}
