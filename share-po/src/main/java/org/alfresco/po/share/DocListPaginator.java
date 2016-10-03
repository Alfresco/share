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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Pagination object for Document List.
 * 
 * @author Richard Smith
 */
public class DocListPaginator extends PageElement
{
    private static final String MENU_ELEMENT_SUFFIX = "_dropdown";
    private static final String MENU_ELEMENT_SELECTOR_TEMPLATE = "div#?";
    private static final By FIRST_MENU_ROW = By.cssSelector("tr.dijitMenuItem:first-of-type");

    private WebDriver driver;
    @FindBy(css="div[id=DOCLIB_PAGINATION_MENU_PAGE_SELECTOR]") private WebElement pageSelector;
    @FindBy(css="div[id=DOCLIB_PAGINATION_MENU_PAGE_BACK]") private WebElement prevPageButton;
    @FindBy(css="div[id=DOCLIB_PAGINATION_MENU_PAGE_FORWARD]") private WebElement nextPageButton;
    @FindBy(css="div[id=DOCLIB_PAGINATION_MENU_RESULTS_PER_PAGE_SELECTOR]") private WebElement resultsPerPageSelector;
	@FindBy(css="div[id=DOCLIB_PAGINATION_MENU_PAGE_MARKER] span") private WebElement pageNumber;

    /**
     * Go to the first page of results.
     * 
     * @return the html page
     */
    public HtmlPage gotoFirstResultsPage()
    {
        // If we're on page 1 return
        if (getPageNumber() == 1)
        {
            return getCurrentPage();
        }
        else
        {
            try
            {
                // Click the page selector to open the drop down menu
                pageSelector.click();

                // Compose the selector for the drop down menu
                String menuId = this.pageSelector.getAttribute("id") + MENU_ELEMENT_SUFFIX;
                String menuSelector = StringUtils.replace(MENU_ELEMENT_SELECTOR_TEMPLATE, "?", menuId);

                // Find the menu
                WebElement menu = this.driver.findElement(By.cssSelector(menuSelector));

                // If the menu is not null and is displayed and is enabled
                if (PageUtils.usableElement(menu))
                {
                    // Within the menu select the first page
                    WebElement firstPage = menu.findElement(FIRST_MENU_ROW);

                    // First page found - click it
                    if (PageUtils.usableElement(firstPage))
                    {
                        firstPage.click();
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
            }
            catch (TimeoutException te)
            {
            }

        }

        // Return the resolved page
        return getCurrentPage();
    }

    /**
     * Checks if there is a prev page available.
     * 
     * @return true, if successful
     */
    public boolean hasPrevPage()
    {
        return PageUtils.usableElement(prevPageButton);
    }

    /**
     * Gets the previous page button.
     * 
     * @return the previous page button
     */
    public WebElement getPrevPageButton()
    {
        return prevPageButton;
    }

    /**
     * Click the previous page button.
     * 
     * @return the html page
     */
    public HtmlPage clickPrevButton()
    {
        if (PageUtils.usableElement(prevPageButton))
        {
            try
            {
                prevPageButton.click();
            }
            catch (NoSuchElementException nse)
            {
            }
            catch (TimeoutException te)
            {
            }
            return getCurrentPage();
        }
        return null;
    }

    /**
     * Gets the current page number.
     * 
     * @return the page number
     */
    public Integer getPageNumber()
    {
        return Integer.parseInt(StringUtils.trim(pageNumber.getText()));
    }

    /**
     * Checks if there is a next page available.
     * 
     * @return true, if successful
     */
    public boolean hasNextPage()
    {
        return PageUtils.usableElement(nextPageButton);
    }

    /**
     * Gets the next page button.
     * 
     * @return the next page button
     */
    public WebElement getNextPageButton()
    {
        return nextPageButton;
    }

    /**
     * Click the next page button.
     * 
     * @return the html page
     */
    public HtmlPage clickNextButton()
    {
        if (PageUtils.usableElement(nextPageButton))
        {
            try
            {
                nextPageButton.click();
            }
            catch (NoSuchElementException nse)
            {
            }
            catch (TimeoutException te)
            {
            }
            return getCurrentPage();
        }
        return null;
    }
}
