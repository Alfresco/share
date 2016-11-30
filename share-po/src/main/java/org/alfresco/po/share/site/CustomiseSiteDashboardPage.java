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
package org.alfresco.po.share.site;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.Dashlets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Customize site dashboard page object, holds all element of the html page
 * relating to customize site dashboard page.
 *
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class CustomiseSiteDashboardPage extends SharePage
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
    private static final String DASHLET_IN_COLUMN_NAME_FORMAT = "//ul[contains(@id,'column-ul-%d')]/li/span";
    private static final int NUMBER_OF_COLUMNS = 4;
    private static final int MAX_DASHLETS_IN_COLUMN = 5;
    private static final By SELECT_ONE_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-1-column-button']");
    private static final By SELECT_TWO_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-2-columns-wide-left-button']");
    private static final By SELECT_THREE_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-3-columns-button']");
    private static final By SELECT_FOUR_COLUMN_LAYOUT_BTN = By.cssSelector("button[id*='dashboard-4-columns-button']");
    private static final By TITLE_ON_PAGE = By.cssSelector(".sub-title");
    private static final By USED_DASHLETS = By.cssSelector(".usedList>li>span");
    
    // Restore Get Started Panel
    private static final By GET_STARTED_PANEL = By.cssSelector("div[id$='_default-welcome-preference']");
    private static final By SHOW_ON_DASHBOARD_RADIO_BUTTON = By.cssSelector("input[id*='welcomePanelEnabled']");
    private static final By HIDE_FROM_DASHBOARD_RADIO_BUTTON = By.cssSelector("input[id*='welcomePanelDisabled']");


    @SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(TITLE_ON_PAGE),
                getVisibleRenderElement(LICENSE_TO),
                getVisibleRenderElement(USER_LOGGED_IN_LABEL)
        );
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomiseSiteDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    
    /**
     * Returns true if Get Started Panel title is displayed
     * 
     * @return
     */
    public boolean isGetStartedPanelDisplayed()
    {
        try
        {
            return driver.findElement(GET_STARTED_PANEL).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("Cannot find Get Started Panel Title.", nse);
        }
        return false;
    }
    
    /**
     * Returns true if Show on Dashboard radio button is displayed
     * 
     * @return
     */
    public boolean isShowOnDashboardDisplayed()
    {
        try
        {
            return driver.findElement(SHOW_ON_DASHBOARD_RADIO_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("Cannot find Show On Dashboard radio button.", nse);
        }
        return false;
    }
    
    /**
     * Returns true if Hide from Dashboard radio button is displayed
     * 
     * @return
     */
    public boolean isHideFromDashboardDisplayed()
    {
        try
        {
            return driver.findElement(HIDE_FROM_DASHBOARD_RADIO_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("Cannot find Hide From Dashboard radio button.", nse);
        }
        return false;
    }   
       

    /**
     * Mimics the action of selection change layout button.
     *
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseSiteDashboardPage selectChangeLayout()
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
    public SiteDashboardPage removeAllDashlets()
    {
        removeAllDashletsWithOutConfirm();
        return selectOk();
    }

    /**
     * Remove dashlet by name.
     *
     * @param dashlet Dashlets
     */
    public SiteDashboardPage remove(Dashlets dashlet)
    {
        String dashletXpath = String.format("//div[@class='column']//span[text()='%s']/../div", dashlet.getDashletName());
        WebElement element = findAndWait(By.xpath(dashletXpath));
        dragAndDrop(element, driver.findElement(TRASHCAN));
        waitUntilAlert();
        return selectOk();
    }

    /**
     * Remove all Dashlets.
     */
    public void removeAllDashletsWithOutConfirm()
    {
        List<WebElement> elements = getDragDashletElem();
        if (elements.size() != 0)
        {
            for (WebElement source : elements)
            {
                dragAndDrop(source, findAndWait(TRASHCAN));
                waitUntilAlert();
            }
        }
        else
        {
            logger.info("All Dashlets already removed");
        }
    }

    private List<WebElement> getDragDashletElem()
    {
        List<WebElement> allDashlets = new ArrayList<WebElement>();
        for (int column = 1; column <= NUMBER_OF_COLUMNS; column++)
        {
            allDashlets.addAll(driver.findElements(By.cssSelector(String.format(DRAGABLE_COLUMN_FORMAT, column))));
        }
        return allDashlets;
    }

    /**
     * Select Layout from given {@link SiteLayout}.
     *
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage selectDashboard(SiteLayout layout)
    {
        findAndWait(layout.getLocator()).click();
        return selectOk();
    }

    /**
     * Add all the dashlets into different columns available.
     *
     * @return {@link SiteDashboardPage}
     */
    public SiteDashboardPage addAllDashlets()
    {
        String scrollJs = "window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));";
        this.selectAddDashlets();
        executeJavaScript(scrollJs, "");
        removeAllDashletsWithOutConfirm();
        int i = 0;
        while (driver.findElements(AVAILABLE_DASHLETS).size() > 0)
        {
            removeAllDashletsWithOutConfirm();
            i++;
            if (i == 2)
            {
                break;
            }
        }
        waitUntilAlert();
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
            executeJavaScript(scrollJs, "");

            if (dashletCounter % MAX_DASHLETS_IN_COLUMN == 0)
            {
                currentColumn++;
            }
            dashletCounter++;
        }
        List<WebElement> usedDashlets = driver.findElements(USED_DASHLETS);
        if (!(usedDashlets.size() == dashlets.size()))
        {
            throw new PageException("Not all the dashlets were added");
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
    public SiteDashboardPage addDashlet(Dashlets dashletName, int columnNumber)
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
            String dashletSelector = String.format("li.availableDashlet div[title*='%s']", dashletName.getDashletName());
            WebElement element = findAndWait(By.cssSelector(dashletSelector));
            element.click();
            List<WebElement> dashlets = findAndWaitForElements(AVAILABLE_DASHLETS_NAMES);
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
            throw new PageOperationException("Error in adding dashlet using drag and drop", te);
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
                        existingDashletsInColumn = findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li",
                                columnNumber)));
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
    @Deprecated
    public SiteDashboardPage addDashlet(String dashletName, int columnNumber)
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
            String dashletXpath = String.format("//*[@class='availableDashlet dnd-draggable']/span[text()='%s']", dashletName);
            WebElement element = findAndWait(By.xpath(dashletXpath));
            element.click();
            List<WebElement> dashlets = findAndWaitForElements(AVAILABLE_DASHLETS_NAMES);
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
            logger.error("Exceeded time to find the Available dashlet names " + te);
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
                        existingDashletsInColumn = findAndWaitForElements(By.cssSelector(String.format("ul[id$='column-ul-%d'] li",
                                columnNumber)));
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
    public SiteDashboardPage selectOk()
    {
        try
        {
            findAndWait(SAVE_BUTTON, maxPageLoadingTime).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the Save button css " + te);
        }

        return getCurrentPage().render();
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
     * Method return list of dashlet names from column with selected number.
     *
     * @param columnNumber int
     * @return List<String>
     */
    public List<String> getDashletNamesFrom(int columnNumber)
    {
        List<String> dashletNames = new ArrayList<String>();
        List<WebElement> dashletsElem = getDashletsElemFrom(columnNumber);
        for (WebElement dashlet : dashletsElem)
        {
            dashletNames.add(dashlet.getText());
        }
        return dashletNames;
    }

    /**
     * Return dashlets count from column with selected number.
     *
     * @param columnNumber int
     * @return int
     */
    public int getDashletsCountIn(int columnNumber)
    {
        return getDashletsElemFrom(columnNumber).size();
    }

    /**
     * Check  is Dashlet with selected name in columnt with number columnNumber.
     *
     * @param dashlet Dashlets
     * @param columnNumber int
     * @return boolean
     */
    public boolean isDashletInColumn(Dashlets dashlet, int columnNumber)
    {
        List<WebElement> dashletsElem = getDashletsElemFrom(columnNumber);
        for (WebElement dashletElem : dashletsElem)
        {
            if (dashletElem.getText().equals(dashlet.getDashletName()))
            {
                return true;
            }
        }
        return false;
    }


    private List<WebElement> getDashletsElemFrom(int columnNumber)
    {
        checkArgument(columnNumber > 0 && columnNumber <= NUMBER_OF_COLUMNS);
        By DashletsInColumn = By.xpath(String.format(DASHLET_IN_COLUMN_NAME_FORMAT, columnNumber));
        try
        {
            return findAndWaitForElements(DashletsInColumn);
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
        catch (StaleElementReferenceException e)
        {
            return getDashletsElemFrom(columnNumber);
        }
    }
}
