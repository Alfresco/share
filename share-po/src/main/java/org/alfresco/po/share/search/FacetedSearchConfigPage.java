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
package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 * The Class FacetedSearchConfigPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class FacetedSearchConfigPage extends SharePage
{
    private static final By PAGE_TITLE = By.cssSelector("h1.alfresco-header-Title");
    private static final By FILTER = By.cssSelector("tr.alfresco-lists-views-layouts-Row.alfresco-lists-views-layout-_MultiItemRendererMixin__item");
    private static final By ADD_NEW_FILTER = By.id("CREATE_FACET_BUTTON");
    private static final Log logger = LogFactory.getLog(FacetedSearchConfigPage.class);

    private String title;
    @FindAll({@FindBy(how = How.CSS, using = "tr.alfresco-lists-views-layouts-Row.alfresco-lists-views-layout-_MultiItemRendererMixin__item")}) 
    List<FacetedSearchConfigFilter> filters;

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render()
     */
    @Override
    public FacetedSearchConfigPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render(org.alfresco.po.RenderTime)
     */
    @Override
    public FacetedSearchConfigPage render(RenderTime timer)
    {
        loadElements();
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Page#getTitle()
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
        PageUtils.checkMandatoryParam("Filter Name", filterName);
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
    public HtmlPage clickAddNewFilter()
    {
        try
        {
            findAndWait(ADD_NEW_FILTER).click();
            return factoryPage.instantiatePage(driver, CreateNewFilterPopUpPage.class);
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
        this.title = driver.findElement(PAGE_TITLE).getText();

        // Initialise the filters
       List<WebElement> filters = findAndWaitForElements(FILTER);
       this.filters = new ArrayList<FacetedSearchConfigFilter>();
       for (WebElement filter : filters)
       {
           this.filters.add(new FacetedSearchConfigFilter(driver, filter));
       }
    }
}
