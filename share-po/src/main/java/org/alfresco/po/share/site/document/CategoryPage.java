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
package org.alfresco.po.share.site.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author
 */
public class CategoryPage extends AbstractEditProperties
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By SELECT_HEADER = By.cssSelector("div[id$='categories-cntrl-picker-head']");
    private static final By AVAILABLE_CATEGORIES_TABLE = By.cssSelector("div[id$='categories-cntrl-picker-results']>table>tbody.yui-dt-data>tr");
    private static final By CURRENTLY_ADDED_CATEGORIES_TABLE = By.cssSelector("div[id$='categories-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr");
    private static final By HEADER_CATEGORIES_TABLE = By.cssSelector("td>div>h3");
    private static final By ADD_REMOVE_LINK = By.cssSelector("td>div>a");
    protected static final By OK_BUTTON = By.cssSelector("button[id$='categories-cntrl-ok-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='categories-cntrl-cancel-button']");

    public CategoryPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CategoryPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if (isCategoryPageVisible())
                {
                    break;
                }
            }
            catch (TimeoutException te)
            {
                throw new PageException("Category page not rendered in time", te);
            }
            catch (NoSuchElementException te)
            {
                // Expected if the page has not rendered
            }
            catch (StaleElementReferenceException e)
            {
                // This occurs occasionally as well
            }
            finally
            {
                timer.end();
            }
        }
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public CategoryPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CategoryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if CategoryPage is displayed.
     *
     * @return true if displayed
     */
    public boolean isCategoryPageVisible()
    {
        try
        {
            return (drone.find(SELECT_HEADER).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Remove the {@link Categories} if it is available to remove.
     *
     * @param aspects {@link List} of {@link Categories} to remove.
     * @return {@link DocumentDetailsPage}
     * @depricated Use {@link #addCategories(List<String>, String...)} instead.
     */
    @Deprecated
    public HtmlPage remove(List<Categories> categories)
    {
        List<String> newList = new ArrayList<>();
        for(Categories category : categories)
        {
            newList.add(category.getValue());
        }
        return addRemoveCategories(newList, CURRENTLY_ADDED_CATEGORIES_TABLE);
    }

    /**
     * Remove top-level categories or sub-categories.
     *
     * @param Categories {@link List} to be removed.
     * @param Optional - VarArg of parent category tree.
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage removeCategories(List<String> categories, String... parentCategories)
    {
        if(parentCategories == null)
        {
            throw new UnsupportedOperationException("parentCategories can't be null.");
        }
        
        for(String parent : parentCategories)
        {
            openSubCategories(parent);
        }
        return addRemoveCategories(categories, AVAILABLE_CATEGORIES_TABLE);
    }

    /**
     * Add the {@link Categories} if it is available to add.
     *
     * @param aspects {@link List} of {@link Categories} to added.
     * @return {@link DocumentDetailsPage}
     * @depricated Use {@link #addCategories(List<String>, String...)} instead.
     */
    @Deprecated
    public HtmlPage add(List<Categories> categories)
    {
        List<String> newList = new ArrayList<>();
        for(Categories category : categories)
        {
            newList.add(category.getValue());
        }
        return addRemoveCategories(newList, AVAILABLE_CATEGORIES_TABLE);
    }

    /**
     * Add top-level categories or sub-categories.
     *
     * @param Categories {@link List} to be added.
     * @param Optional - VarArg of parent category tree.
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage addCategories(List<String> categories, String... parentCategories)
    {
        if(parentCategories == null)
        {
            throw new UnsupportedOperationException("parentCategories can't be null.");
        }
        
        for(String parent : parentCategories)
        {
            openSubCategories(parent);
        }
        return addRemoveCategories(categories, AVAILABLE_CATEGORIES_TABLE);
    }

    /**
     * Add the {@link Categories} if it is available to add.
     *
     * @param categories {@link List} of {@link String} to added.
     * @return {@link DocumentDetailsPage}
     */
    private HtmlPage addRemoveCategories(List<String> categories, By by)
    {
        if (categories == null || categories.isEmpty())
        {
            throw new UnsupportedOperationException("Categories can't be empty or null.");
        }
        List<WebElement> availableElements = null;
        Map<String, ShareLink> availableCategoriesMap = null;
        try
        {
            availableElements = drone.findAndWaitForElements(by);
        }
        catch (TimeoutException exception)
        {
        }

        if (availableElements != null && !availableElements.isEmpty())
        {
            // Convert List into Map
            availableCategoriesMap = new HashMap<String, ShareLink>();
            for (WebElement webElement : availableElements)
            {
                try
                {
                    WebElement header = webElement.findElement(HEADER_CATEGORIES_TABLE);
                    WebElement addLink = webElement.findElement(ADD_REMOVE_LINK);
                    ShareLink addShareLink = new ShareLink(addLink, drone);
                    availableCategoriesMap.put(header.getText(), addShareLink);
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Not able to find the header or link element on this row.", e);
                }
                catch (Exception e)
                {
                    logger.error("Exception while finding & adding categories : ", e);
                }
            }
        }

        if (availableCategoriesMap != null && !availableCategoriesMap.isEmpty())
        {
            for (String category : categories)
            {
                ShareLink link = availableCategoriesMap.get(category);
                if (link != null)
                {
                    try
                    {
                        link.click();
                        if (logger.isTraceEnabled())
                        {
                            logger.trace(category + "Categories Added.");
                        }
                    }
                    catch (StaleElementReferenceException exception)
                    {
                        drone.find(CANCEL_BUTTON).click();
                        throw new PageException("Unexpected Refresh on Page lost reference to the Categories.", exception);
                    }
                }
                else
                {
                    logger.error("Not able to find in the available categories bucket " + category);
                }
            }
        }

        return this;
    }

    /**
     * Get {@link List} of {@link Categories} which can be add able.
     *
     * @return {@link List} of {@link Categories}
     * @depricated Use {@link #getAddAbleCatgoryList()} instead.
     */
    @Deprecated
    public List<Categories> getAddAbleCatgories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        List<WebElement> elements = drone.findAndWaitForElements(AVAILABLE_CATEGORIES_TABLE);
        for (WebElement webElement : elements)
        {
            if (webElement.findElement(By.cssSelector(".addIcon")).isDisplayed())
            {
                categories.add(Categories.getCategory(webElement.findElement(By.cssSelector(".item-name>a")).getText()));
            }
        }
        return categories;
    }

    /**
     * Get {@link List} of {@link Categories} which can be add able.
     *
     * @return {@link List} of categories
     */
    public List<String> getAddAbleCatgoryList()
    {
        List<String> categories = new ArrayList<>();
        List<WebElement> elements = drone.findAndWaitForElements(AVAILABLE_CATEGORIES_TABLE);
        for (WebElement webElement : elements)
        {
            if (webElement.findElement(By.cssSelector(".addIcon")).isDisplayed())
            {
                categories.add(webElement.findElement(By.cssSelector(".item-name>a")).getText());
            }
        }
        return categories;
    }

    /**
     * Get the {@link List} of {@link Categories} which is added.
     *
     * @return {@link List} of {@link Categories}
     * @depricated Use {@link #getAddedCatgoryList()} instead.
     */
    @Deprecated
    public List<Categories> getAddedCatgories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        List<WebElement> elements = drone.findAll(CURRENTLY_ADDED_CATEGORIES_TABLE);
        for (WebElement webElement : elements)
        {
            String categoryName = webElement.findElement(By.cssSelector("h3.name")).getText();
            if (categoryName != null && categoryName.trim().length() > 0)
            {
                categories.add(Categories.getCategory(categoryName));
            }
        }
        return categories;
    }

    /**
     * Get the {@link List} of categories which have been added.
     *
     * @return {@link List} of categories
     */
    public List<String> getAddedCatgoryList()
    {
        List<String> categories = new ArrayList<>();
        List<WebElement> elements = drone.findAll(CURRENTLY_ADDED_CATEGORIES_TABLE);
        for (WebElement webElement : elements)
        {
            String categoryName = webElement.findElement(By.cssSelector("h3.name")).getText();
            if (categoryName != null && categoryName.trim().length() > 0)
            {
                categories.add(categoryName);
            }
        }
        return categories;
    }

    /**
     * Click on {@link cancel} in {@link CategoryPage}
     *
     * @return {@link EditDocumentPropertiesPage}
     */
    public HtmlPage clickCancel()
    {
        try
        {
            drone.find(CANCEL_BUTTON).click();
            return drone.getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not able find the cancel button: ", nse);
        }
    }

    /**
     * Click on {@link ApplyChanges} in {@link selectAspectsPage}
     *
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage clickOk()
    {
        try
        {
            drone.find(OK_BUTTON).click();
            return drone.getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not able find the ok button: ", nse);
        }
    }

    /**
     * This method click on category to see subCategories.
     *
     * @param category
     * @depricated Use {@link #openSubCategories(String)} instead.
     */
    @Deprecated
    public void openSubCategories(Categories category)
    {
        openSubCategories(category.getValue());
    }

    /**
     * This method clicks on the category to see subCategories.
     *
     * @param category
     */
    public void openSubCategories(String category)
    {
        List<WebElement> elements = drone.findAndWaitForElements(AVAILABLE_CATEGORIES_TABLE);
        for (WebElement webElement : elements)
        {
            if (webElement.findElement(By.cssSelector(".item-name>a")).getText().equals(category))
            {
                By headerSelector = By.cssSelector("h3 > a");
                WebElement headerElement = webElement.findElement(headerSelector);
                String headerText = headerElement.getText();
                headerElement.click();
                drone.waitUntilNotVisibleWithParitalText(headerSelector, headerText, drone.getDefaultWaitTime()/1000);
                return;
            }
        }
    }
}
