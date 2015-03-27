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
package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchConfigPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class FacetedSearchConfigPage extends SharePage
{
    private static final By PAGE_TITLE = By.cssSelector("h1.alfresco-header-Title");
    private static final By FILTER = By.cssSelector("tr.alfresco-lists-views-layouts-Row.alfresco-lists-views-layout-_MultiItemRendererMixin--item");
    private static final By ADD_NEW_FILTER = By.id("CREATE_FACET_BUTTON");
    private static final Log logger = LogFactory.getLog(FacetedSearchConfigPage.class);

    private String title;
    private List<FacetedSearchConfigFilter> filters;

    /**
     * Instantiates a new faceted search config page.
     * 
     * @param drone WebDriver browser client
     */
    public FacetedSearchConfigPage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public FacetedSearchConfigPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public FacetedSearchConfigPage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
     */
    @Override
    public FacetedSearchConfigPage render(RenderTime timer)
    {
        loadElements();
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Page#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets the filters.
     * 
     * @return the filters
     */
    public List<FacetedSearchConfigFilter> getFilters()
    {
        return filters;
    }

    /**
     * Gets the filter
     * 
     * @return the filter
     */
    public FacetedSearchConfigFilter getFilter(String filterName)
    {
        WebDroneUtil.checkMandotaryParam("Filter Name", filterName);
        for (FacetedSearchConfigFilter facetedSearchConfigFilter : filters)
        {
            if (filterName.equalsIgnoreCase(facetedSearchConfigFilter.getFilterId_text()))
            {
                return facetedSearchConfigFilter;
            }

        }
        throw new PageOperationException("Not able to find the filter named : " + filterName);
    }

    /**
     * Click add new filter.
     * 
     * @return the creates the new filter pop up page
     */
    public CreateNewFilterPopUpPage clickAddNewFilter()
    {
        try
        {
            drone.findAndWait(ADD_NEW_FILTER).click();
            return new CreateNewFilterPopUpPage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the button: " + e.getMessage());
        }
        throw new PageOperationException("Not visible Element: AddNewFilter");
    }

    /**
     * Load elements.
     */
    private void loadElements()
    {
        // Initialise the title
        this.title = drone.find(PAGE_TITLE).getText();

        // Initialise the filters
       List<WebElement> filters = drone.findAndWaitForElements(FILTER);
       this.filters = new ArrayList<FacetedSearchConfigFilter>();
       for (WebElement filter : filters)
       {
           this.filters.add(new FacetedSearchConfigFilter(drone, filter));
       }
    }
}