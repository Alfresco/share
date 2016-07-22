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

import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteLayout;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

public class CustomiseUserDashboardPage extends SharePage
{

    private Log logger = LogFactory.getLog(this.getClass());
    private static final By CHANGE_LAYOUT_BUTTON = By.cssSelector("button[id*='change-button']");
    private static final By ADD_DASHLET_BUTTON = By.cssSelector("button[id*='addDashlets-button']");
    private static final By TRASHCAN = By.cssSelector(".trashcan");
    private static final By SAVE_BUTTON = By.cssSelector("button[id$=save-button-button]");
    private static final By AVAILABLE_DASHLETS = By.cssSelector(".availableList>li>div.dnd-draggable");
    private static final By AVAILABLE_DASHLETS_NAMES = By.cssSelector("ul.availableList>li.availableDashlet>span");
    private static final String DRAGABLE_COLUMN_FORMAT = "ul[id$='column-ul-%d']>li>div.dnd-draggable";
    private static final String COLUMN_FORMAT = "ul[id$='column-ul-%d']";
    private static final int NUMBER_OF_COLUMNS = 4;
    private static final int MAX_DASHLETS_IN_COLUMN = 5;
    private static final By SELECT_ONE_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-1-column-button']");
    private static final By SELECT_TWO_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-2-columns-wide-left-button']");
    private static final By SELECT_THREE_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-3-columns-button']");
    private static final By SELECT_FOUR_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-4-columns-button']");

    // Restore Get Started Panel
    private static final By SHOW_ON_DASHBOARD_RADIO_BUTTON = By.cssSelector("input[id*='welcomePanelEnabled']");
    private static final By HIDE_ON_DASHBOARD_RADIO_BUTTON = By.cssSelector("input[id*='welcomePanelDisabled']");

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseUserDashboardPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if (driver.findElement(CHANGE_LAYOUT_BUTTON).isEnabled())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse)
            {
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
    public CustomiseUserDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Mimics the action of selection change layout button.
     * 
     * @return {@link CustomiseUserDashboardPage}
     */
    public CustomiseUserDashboardPage selectChangeLayou()
    {
        driver.findElement(CHANGE_LAYOUT_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Change Layout button has been found and selected");
        }

        return getCurrentPage().render();
    }

    /**
     * Mimics the action of the Add Dashlets button click.
     */
    public void selectAddDashlets()
    {
        driver.findElement(ADD_DASHLET_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Add Dashlet button has been found and selected");
        }
    }

    /**
     * Mimics the action of removing the all dashlets from Columns.
     * 
     * @return {@link SiteDashboardPage}
     */
    public DashBoardPage removeAllDashlets()
    {
        for (int column = 1; column <= NUMBER_OF_COLUMNS; column++)
        {
            List<WebElement> elements = driver.findElements(By.cssSelector(String.format(DRAGABLE_COLUMN_FORMAT, column)));
            if (elements != null)
            {
                for (WebElement source : elements)
                {
                    dragAndDrop(source, driver.findElement(TRASHCAN));
                }
            }
        }

        return selectOk();
    }

    /**
     * Select Layout from given {@link SiteLayout}.
     * 
     * @return {@link SiteDashboardPage}
     */
    public DashBoardPage selectDashboard(SiteLayout layout)
    {
        driver.findElement(layout.getLocator()).click();

        return selectOk();
    }

    /**
     * Add all the dashlets into different columns available.
     * 
     * @return {@link SiteDashboardPage}
     */
    public DashBoardPage addAllDashlets()
    {
        this.selectAddDashlets();
        List<WebElement> dashlets = driver.findElements(AVAILABLE_DASHLETS);
        if (logger.isTraceEnabled())
        {
            logger.trace("There are " + dashlets.size() + " dashlets found.");
        }
        int currentColumn = 1;
        int dashletCounter = 1;
        WebElement target = null;

        for (WebElement source : dashlets)
        {
            target = driver.findElement(By.cssSelector(String.format(COLUMN_FORMAT, currentColumn)));
            dragAndDrop(source, target);
            if (dashletCounter % MAX_DASHLETS_IN_COLUMN == 0)
            {
                currentColumn++;
            }
            dashletCounter++;
        }

        return selectOk();
    }

    /**
     * Add given dashlet into given column.
     * 
     * @param dashletName Dashlets
     * @param columnNumber int
     * @return {@link SiteDashboardPage}
     */
    public DashBoardPage addDashlet(Dashlets dashletName, int columnNumber)
    {
        if (dashletName == null)
        {
            throw new IllegalArgumentException("Dashlet Name is required");
        }

        if (columnNumber < 1 || columnNumber > NUMBER_OF_COLUMNS)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }

        WebElement newDashlet = null;
        int noOfColumns = 0;

        this.selectAddDashlets();

        try
        {
//            String dashletXpath = String.format("//li[@class='availableDashlet dnd-draggable']/span[text()=\"%s\"]", dashletName.getDashletName());
//            WebElement element = findAndWait(By.xpath(dashletXpath));
//            executeJavaScript(String.format("window.scrollTo('%s', '%s')", element.getLocation().getX(), element.getLocation().getY()));
            
        	String dashletSelector = String.format("li.availableDashlet div.dnd-draggable[title*=\"%s\"]", dashletName.getDashletName().replace("'", "\'"));
            By dashlet = By.cssSelector("li.availableDashlet div.dnd-draggable[title*=\"" + dashletName.getDashletName().replace("'", "\'") + "\"]");
            WebElement element = findAndWait(dashlet);
            
            // Move element into View if not already
            Actions actions = new Actions(driver);
            actions.moveToElement(element).perform();
            
            Coordinates coord = ((Locatable)element).getCoordinates();
            coord.inViewPort();

            element.click();
            List<WebElement> dashlets = findAndWaitForElements(AVAILABLE_DASHLETS_NAMES, getDefaultWaitTime());
            for (WebElement source : dashlets)
            {
                if (source.getText().equals(dashletName.getDashletName()))
                {
                    newDashlet = source;
                    break;
                }
            }
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded time to find the Available dashlet names ", te);
        }

        if (newDashlet != null)
        {
            try
            {
                String columns = driver.findElement(By.cssSelector("div[id$='default-wrapper-div']")).getAttribute("class");
                if (!StringUtils.isEmpty(columns))
                {
                    String columnSize = columns.substring(columns.length() - 1);
                    noOfColumns = Integer.valueOf(columnSize);
                }
            }
            catch (NoSuchElementException te)
            {
                logger.error("Unable to find the Columns css " + te);
            }

            if (columnNumber <= noOfColumns)
            {
                try
                {
                    List<WebElement> existingDashletsInColumn = Collections.emptyList();
                    try
                    {
                        existingDashletsInColumn = findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li", columnNumber)), getDefaultWaitTime());
                    }
                    catch (TimeoutException e)
                    {
                        logger.error("Selected column is empty", e);
                    }
                    if (existingDashletsInColumn.size() < MAX_DASHLETS_IN_COLUMN)
                    {
                        WebElement target = findAndWait(By.xpath(String.format("//ul[@class='usedList' and contains(@id,'-column-ul-%d')]", columnNumber)));
                        executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                        dragAndDrop(newDashlet, target);
                        return selectOk();
                    }
                    else
                    {
                        throw new PageOperationException("Exceeded the no. of dashlets in given column.");
                    }
                }
                catch (TimeoutException te)
                {
                    logger.error("Exceeded time to find the Available dashlet names ", te);
                }
            }
            else
            {
                throw new PageOperationException("Expected column does not exist in available columns list.");
            }
        }

        throw new PageOperationException("Error in adding dashlet using drag and drop");
    }

    /**
     * Add given dashlet into given column.
     * 
     * @param dashletName String
     * @param columnNumber int
     * @return {@link SiteDashboardPage}
     */
    public DashBoardPage addDashlet(String dashletName, int columnNumber)
    {
        if (dashletName == null)
        {
            throw new IllegalArgumentException("Dashlet Name is required");
        }

        if (columnNumber < 1 || columnNumber > NUMBER_OF_COLUMNS)
        {
            throw new IllegalArgumentException("Column number should be between 1 and 4");
        }

        WebElement newDashlet = null;
        int noOfColumns = 0;

        this.selectAddDashlets();

        try
        {
            String dashletXpath = String.format("//*[@class='availableDashlet dnd-draggable']/span[text()=\"%s\"]", dashletName);
            WebElement element = findAndWait(By.xpath(dashletXpath));
            element.click();
            List<WebElement> dashlets = findAndWaitForElements(AVAILABLE_DASHLETS_NAMES, getDefaultWaitTime());
            for (WebElement source : dashlets)
            {
                if (source.getText().equals(dashletName))
                {
                    newDashlet = source;
                    break;
                }
            }
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded time to find the Available dashlet names ", te);
        }

        if (newDashlet != null)
        {
            try
            {
                String columns = driver.findElement(By.cssSelector("div[id$='default-wrapper-div']")).getAttribute("class");
                if (!StringUtils.isEmpty(columns))
                {
                    String columnSize = columns.substring(columns.length() - 1);
                    noOfColumns = Integer.valueOf(columnSize);
                }
            }
            catch (NoSuchElementException te)
            {
                logger.error("Unable to find the Columns css " + te);
            }

            if (columnNumber <= noOfColumns)
            {
                try
                {
                    List<WebElement> existingDashletsInColumn = Collections.emptyList();
                    try
                    {
                        existingDashletsInColumn = findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li", columnNumber)), getDefaultWaitTime());
                    }
                    catch (TimeoutException e)
                    {
                        logger.error("Selected column is empty", e);
                    }
                    if (existingDashletsInColumn.size() < MAX_DASHLETS_IN_COLUMN)
                    {
                        WebElement target = findAndWait(By.xpath(String.format("//ul[@class='usedList' and contains(@id,'-column-ul-%d')]", columnNumber)));
                        executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                        dragAndDrop(newDashlet, target);
                        return selectOk();
                    }
                    else
                    {
                        throw new PageOperationException("Exceeded the no. of dashlets in given column.");
                    }
                }
                catch (TimeoutException te)
                {
                    logger.error("Exceeded time to find the Available dashlet names ", te);
                }
            }
            else
            {
                throw new PageOperationException("Expected column does not exist in available columns list.");
            }
        }

        throw new PageOperationException("Error in adding dashlet using drag and drop");
    }

    /**
     * This method used to select the ok button present on Customize site
     * dashboard page.
     * 
     * @return SiteDashboardPage
     */
    public DashBoardPage selectOk()
    {
        try
        {
            driver.findElement(SAVE_BUTTON).click();
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the Save button css ", te);
        }
        return factoryPage.instantiatePage(driver, DashBoardPage.class).render();
    }

    /**
     * Remove dashlet by name.
     * 
     * @param dashlet Dashlets
     */
    public DashBoardPage removeDashlet(Dashlets dashlet)
    {
        String dashletXpath = String.format("//div[@class='column']//span[text()=\"%s\"]/../div", dashlet.getDashletName());
        WebElement element = findAndWait(By.xpath(dashletXpath));
        dragAndDrop(element, driver.findElement(TRASHCAN));
        waitUntilAlert();
        return selectOk();
    }

    /**
     * Method to change layout on Customize Site Dashboard page
     * 
     * @param numOfColumns int
     */

    public void selectNewLayout(int numOfColumns)
    {
        if (numOfColumns > NUMBER_OF_COLUMNS || numOfColumns < 1)
        {
            throw new IllegalArgumentException("Select correct number of columns");
        }
        else
        {
            try
            {
                switch (numOfColumns)
                {
                    case 1:
                        findAndWait(SELECT_ONE_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 2:
                        findAndWait(SELECT_TWO_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 3:
                        findAndWait(SELECT_THREE_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 4:
                        findAndWait(SELECT_FOUR_COLUMN_LAYOUT_BTN).click();
                        break;
                }
            }
            catch (NoSuchElementException nse)
            {
                logger.info("Unable to find the Select button css " + nse);
            }
        }
    }

    /**
     * Clicks on Show on dashboard radio button (to show Get Started Panel on user dashboard)
     * 
     * @return
     */
    public HtmlPage clickOnShowOnDashboardRadioButton()
    {
        try
        {
            findAndWait(SHOW_ON_DASHBOARD_RADIO_BUTTON).click();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Show Get Started Panel radio button on Customise User dashboard page.", toe);
            }
        }
        return getCurrentPage();
    }

    /**
     * Clicks on Hide on dashboard radio button (to hide Get Started Panel on user dashboard)
     * 
     * @return
     */
    public HtmlPage clickOnHideOnDashboardRadioButton()
    {
        try
        {
            findAndWait(HIDE_ON_DASHBOARD_RADIO_BUTTON).click();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Hide Get Started Panel radio button on Customise User dashboard page.", toe);
            }
        }
        return getCurrentPage();
    }
}
