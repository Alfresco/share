/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

package org.alfresco.po.share;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteLayout;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

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

    /**
     * Constructor.
     */
    public CustomiseUserDashboardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseUserDashboardPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if (drone.find(CHANGE_LAYOUT_BUTTON).isEnabled())
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

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseUserDashboardPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Mimics the action of selection change layout button.
     *
     * @return {@link CustomiseUserDashboardPage}
     */
    public CustomiseUserDashboardPage selectChangeLayou()
    {
        drone.find(CHANGE_LAYOUT_BUTTON).click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Change Layout button has been found and selected");
        }

        return new CustomiseUserDashboardPage(drone);
    }

    /**
     * Mimics the action of the Add Dashlets button click.
     */
    public void selectAddDashlets()
    {
        drone.find(ADD_DASHLET_BUTTON).click();
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
            List<WebElement> elements = drone.findAll(By.cssSelector(String.format(DRAGABLE_COLUMN_FORMAT, column)));
            if (elements != null)
            {
                for (WebElement source : elements)
                {
                    drone.dragAndDrop(source, drone.find(TRASHCAN));
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
        drone.find(layout.getLocator()).click();

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
        List<WebElement> dashlets = drone.findAll(AVAILABLE_DASHLETS);
        if (logger.isTraceEnabled())
        {
            logger.trace("There are " + dashlets.size() + " dashlets found.");
        }
        int currentColumn = 1;
        int dashletCounter = 1;
        WebElement target = null;

        for (WebElement source : dashlets)
        {
            target = drone.find(By.cssSelector(String.format(COLUMN_FORMAT, currentColumn)));
            drone.dragAndDrop(source, target);
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
            String dashletXpath = String.format("//*[@class='availableDashlet dnd-draggable']/span[text()=\"%s\"]", dashletName.getDashletName());
            WebElement element = drone.findAndWait(By.xpath(dashletXpath));
            element.click();
            List<WebElement> dashlets = drone.findAndWaitForElements(AVAILABLE_DASHLETS_NAMES);
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
                String columns = drone.find(By.cssSelector("div[id$='default-wrapper-div']")).getAttribute("class");
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
                        existingDashletsInColumn = drone.findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li",
                                columnNumber)));
                    }
                    catch (TimeoutException e)
                    {
                        logger.error("Selected column is empty", e);
                    }
                    if (existingDashletsInColumn.size() < MAX_DASHLETS_IN_COLUMN)
                    {
                        WebElement target = drone.findAndWait(By.xpath(String.format("//ul[@class='usedList' and contains(@id,'-column-ul-%d')]", columnNumber)));
                        drone.executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                        drone.dragAndDrop(newDashlet, target);
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
            WebElement element = drone.findAndWait(By.xpath(dashletXpath));
            element.click();
            List<WebElement> dashlets = drone.findAndWaitForElements(AVAILABLE_DASHLETS_NAMES);
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
                String columns = drone.find(By.cssSelector("div[id$='default-wrapper-div']")).getAttribute("class");
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
                        existingDashletsInColumn = drone.findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li",
                                columnNumber)));
                    }
                    catch (TimeoutException e)
                    {
                        logger.error("Selected column is empty", e);
                    }
                    if (existingDashletsInColumn.size() < MAX_DASHLETS_IN_COLUMN)
                    {
                        WebElement target = drone.findAndWait(By.xpath(String.format("//ul[@class='usedList' and contains(@id,'-column-ul-%d')]", columnNumber)));
                        drone.executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                        drone.dragAndDrop(newDashlet, target);
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
            drone.find(SAVE_BUTTON).click();
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the Save button css ", te);
        }

        return new DashBoardPage(drone);
    }

    /**
     * Remove dashlet by name.
     *
     * @param dashlet Dashlets
     */
    public DashBoardPage removeDashlet(Dashlets dashlet)
    {
        String dashletXpath = String.format("//div[@class='column']//span[text()='%s']/../div", dashlet.getDashletName());
        WebElement element = drone.findAndWait(By.xpath(dashletXpath));
        drone.dragAndDrop(element, drone.find(TRASHCAN));
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
                        drone.findAndWait(SELECT_ONE_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 2:
                        drone.findAndWait(SELECT_TWO_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 3:
                        drone.findAndWait(SELECT_THREE_COLUMN_LAYOUT_BTN).click();
                        break;
                    case 4:
                        drone.findAndWait(SELECT_FOUR_COLUMN_LAYOUT_BTN).click();
                        break;
                }
            }
            catch (NoSuchElementException nse)
            {
                logger.info("Unable to find the Select button css " + nse);
            }
        }
    }
}
