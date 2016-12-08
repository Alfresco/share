/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * The Class FacetedSearchView
 * This is still not completed since the development is in progress
 * 
 * @author Charu
 */
public class FacetedSearchView extends SharePage
{
    /** Constants. */
    private static final By VIEW_TYPES = By.cssSelector("#DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP > div.alf-menu-group-items td:nth-of-type(3)");
    private static final By DETAILED_VIEW_RESULTS = By.cssSelector("tbody[id=FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS] td.thumbnailCell");
    private static final By GALLERY_VIEW_RESULTS = By.cssSelector("#FCTSRCH_GALLERY_VIEW");
    private static final By GALLERY_VIEW_RESULT = By.cssSelector(".alfresco-lists-views-layouts-Grid__cell");
    private static final String DISPLAY_NAMES = ".displayName";
    private static final By GALLERY_VIEW_ICON = By.cssSelector(".alfresco-renderers-MoreInfo");
    private static Log logger = LogFactory.getLog(FacetedSearchView.class);

    private String results;
    private WebElement sortOrderButton;
    private WebElement detailedViewResults;
    private WebElement galleryViewResults;

    /**
     * Gets the results.
     *
     * @return the results
     */
    public String getResults()
    {
        return results;
    }

    /**
     * Gets the sort order button.
     *
     * @return the sort order button
     */
    public WebElement getSortOrderButton()
    {
        return sortOrderButton;
    }

    /**
     * select view by index by the indexed item in the view menu
     *
     * @param i the index number of the item upon which to select
     * @return the html page
     */
    public HtmlPage selectViewByIndex(int i)
    {
        openMenu();
        boolean found = false;
        List<WebElement> menuElements = driver.findElements(VIEW_TYPES);
        if (menuElements != null && !menuElements.isEmpty())
        {
            menuElements.get(i).click();
            found = true;
        }

        if (!found)
        {
            cancelMenu();
        }
        return factoryPage.getPage(this.driver);
    }

    /**
     * Select by label.
     *
     * @param label the label to be sorted on
     * @return the html page
     */
    public HtmlPage selectViewByLabel(String label)
    {
        openMenu();
        boolean found = false;
        List<WebElement> menuElements = driver.findElements(VIEW_TYPES);
        for (WebElement option : menuElements)
        {
            String value = StringUtils.trim(option.getText());
            if (label.equalsIgnoreCase(value))
            {
                StringUtils.trim(option.getText());
                option.click();
                found = true;
                break;
            }
        }
        if (!found)
        {
            cancelMenu();
        }
        return factoryPage.getPage(this.driver);
    }

    /**
     * Verify is results displayed in detailed view
     */
    public boolean isDetailedViewResultsDisplayed()
    {
        try
        {
            detailedViewResults = driver.findElement(DETAILED_VIEW_RESULTS);
            if (detailedViewResults.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
        return false;
    }

    /**
     * Verify is results displayed in Gallery view
     */
    public boolean isGalleryViewResultsDisplayed()
    {
        try
        {
            galleryViewResults = driver.findElement(GALLERY_VIEW_RESULTS);
            if (galleryViewResults.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
        return false;
    }

    /**
     * Open the sort menu.
     */
    private void openMenu()
    {
        driver.findElement(By.cssSelector("#FCTSRCH_RESULTS_MENU")).click(); // Regain page control from dojo
        WebElement element = driver.findElement(By.cssSelector("img.alf-configure-icon"));
        Actions action = new Actions(driver);
        action.moveToElement(element).click().perform();
    }

    /**
     * Cancel an open menu.
     */
    private void cancelMenu()
    {
        driver.findElement(By.cssSelector("#FCTSRCH_RESULTS_MENU")).click();
    }

    /**
     * Click on the GalleryIcon by name
     *
     * @param name String
     * @return GalleryViewPopupPage
     */
    public HtmlPage clickGalleryIconByName(String name)
    {
        PageUtils.checkMandatoryParam("Name", name);

        try
        {
            List<WebElement> displayNames = driver.findElements(By.cssSelector(DISPLAY_NAMES));
            {
                for (WebElement results : displayNames)
                {
                    if (results.getText().equalsIgnoreCase(name))
                    {
                        mouseOver(results);
                        WebElement element = findFirstDisplayedElement(GALLERY_VIEW_ICON);
                        mouseOver(element);
                        element.click();
                        return factoryPage.instantiatePage(driver, GalleryViewPopupPage.class);
                    }
                }
            }
        }
        catch (TimeoutException e)
        {

            logger.error("Unable to get the name : ", e);
        }
        throw new PageOperationException("Unable to get the name  : ");
    }

    @Override
    public <T extends HtmlPage> T render(RenderTime timer)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Verify if any search result is highlighted in Gallery View.
     */
    public boolean isAnyItemHighlightedGalleryView()
    {
        List<WebElement> elements = driver.findElements(GALLERY_VIEW_RESULT);
        for (WebElement element : elements)
        {
            if (element.findElements(By.cssSelector("mark")).size() > 0)
            {
                return true;
            }

        }
        return false;
    }
}
