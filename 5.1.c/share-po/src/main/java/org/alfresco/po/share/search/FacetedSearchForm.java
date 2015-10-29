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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.share.FactoryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchForm.
 */
public class FacetedSearchForm extends PageElement
{
    /** Constants */
    private static final By FACETED_SEARCH_FORM = By.cssSelector("div#FCTSRCH_SEARCH_FORM");
    public final By SEARCH_FIELD = By.cssSelector("INPUT[name=searchTerm]");
    public final By SEARCH_BUTTON = By.cssSelector("span.dijitButtonNode");

    private WebElement input;
    private WebElement button;

    /**
     * Instantiates a new faceted search form.
     */
    public FacetedSearchForm(WebDriver driver, FactoryPage factoryPage)
    {
        this.driver = driver;
        this.factoryPage = factoryPage;
        WebElement facetedSearchForm = driver.findElement(FACETED_SEARCH_FORM);
        this.input = facetedSearchForm.findElement(SEARCH_FIELD);
        this.button = facetedSearchForm.findElement(SEARCH_BUTTON);
    }

    /**
     * Gets the search term.
     *
     * @return the search term
     */
    public String getSearchTerm()
    {
        return this.input.getAttribute("value");
    }

    /**
     * Sets the search term.
     *
     * @param searchTerm the new search term
     */
    public void setSearchTerm(String searchTerm)
    {
        this.input.sendKeys(searchTerm);
    }

    /**
     * Clear search term.
     */
    public void clearSearchTerm()
    {
        this.input.clear();
    }

    /**
     * Performs a search for the provided searchTerm.
     *
     * @param searchTerm the term upon which to search
     * @return the html page
     */
    public HtmlPage search(String searchTerm)
    {
        clearSearchTerm();
        setSearchTerm(searchTerm);
        this.button.click();
        
        return factoryPage.getPage(this.driver);
    }

}
