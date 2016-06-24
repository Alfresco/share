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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchHeaderSearchForm.
 */
public class FacetedSearchHeaderSearchForm extends PageElement
{
    /** Constants */
    private static final By HEADER_SEARCH_BOX = By.cssSelector("div#HEADER_SEARCH_BOX");
    private static final By HEADER_SEARCH_BOX_FORM_FIELD = By.cssSelector("INPUT#HEADER_SEARCHBOX_FORM_FIELD");

    private WebDriver driver;
    private WebElement input;

    /**
     * Instantiates a new faceted search form.
     */
    public FacetedSearchHeaderSearchForm(WebDriver driver)
    {
        this.driver = driver;
        WebElement facetedSearchHeaderSearchForm = driver.findElement(HEADER_SEARCH_BOX);
        this.input = facetedSearchHeaderSearchForm.findElement(HEADER_SEARCH_BOX_FORM_FIELD);
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
        this.input.sendKeys(Keys.RETURN);

        return factoryPage.getPage(this.driver);
    }

}
