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
package org.alfresco.po.share.dashlet;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.webdrone.HtmlPage;
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
import org.openqa.selenium.support.ui.Select;

/**
 * This page object holds all element of the HTML relating to Configure Saved
 * Search Dialogue box.
 * 
 * @author Ranjith Manyam
 */
public class ConfigureSavedSearchDialogBoxPage extends SharePage
{
    private final Log logger = LogFactory.getLog(ConfigureSavedSearchDialogBoxPage.class);
    private static final By CONFIGURE_SEARCH_DIALOG_BOX = By
            .cssSelector("div[id$='default-configDialog-configDialog_c'][style*='visibility: visible']>div[id$='_default-configDialog-configDialog']");
    private static final By CONFIGURE_SEARCH_DIALOG_HEADER = By
            .cssSelector("div[id$='default-configDialog-configDialog_c'][style*='visibility: visible'] div[id$='_default-configDialog-configDialog_h']");
    private static final By SEARCH_TERM_BOX = By.cssSelector("input[name='searchTerm']");
    private static final By TITLE_BOX = By.cssSelector("input[name='title']");
    private static final By LIMIT_SELECT_BOX = By.cssSelector("select[name='limit']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='default-configDialog-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-configDialog-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector("a.container-close");
    private static final By HELP_BALLOON = By.cssSelector("div[style*='visible']>div.bd>div.balloon");
    private static final By HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.text");

    /**
     * Constructor.
     */
    protected ConfigureSavedSearchDialogBoxPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized ConfigureSavedSearchDialogBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(CONFIGURE_SEARCH_DIALOG_BOX), getVisibleRenderElement(CONFIGURE_SEARCH_DIALOG_HEADER),
                getVisibleRenderElement(SEARCH_TERM_BOX), getVisibleRenderElement(TITLE_BOX), getVisibleRenderElement(LIMIT_SELECT_BOX),
                getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON), getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureSavedSearchDialogBoxPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureSavedSearchDialogBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private boolean isConfigureSavedSearchDialogDisplayed()
    {
        try
        {
            drone.waitUntilElementDisappears(CONFIGURE_SEARCH_DIALOG_BOX, SECONDS.convert(WAIT_TIME_3000, MILLISECONDS));
        }
        catch (TimeoutException te)
        {
            return true;
        }
        return false;
    }

    /**
     * This method is used to Finds OK button and clicks on it.
     * 
     * @return {@link org.alfresco.po.share.site.SiteDashboardPage}
     */
    public HtmlPage clickOnOKButton()
    {
        try
        {
            drone.find(OK_BUTTON).submit();
            if (isConfigureSavedSearchDialogDisplayed())
            {
                return this;
            }
            else if (drone.getCurrentPage().render() instanceof SiteDashboardPage)
            {
                return new SiteDashboardPage(drone);
            }
            else if (drone.getCurrentPage().render() instanceof DashBoardPage)
            {
                return new DashBoardPage(drone);
            }
            else
            {
                throw new PageOperationException("Current page is not ConfigureSavedSearchDialog or SiteDashBoardPage or DashBoardPage");
            }
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the OK button.", te);
            throw new PageOperationException("Unable to click the OK Button.");
        }
    }

    /**
     * This method is used to Finds Cancel button and clicks on it.
     */
    public HtmlPage clickOnCancelButton()
    {
        return clickButton(CANCEL_BUTTON, "CANCEL");
    }

    /**
     * This method is used to Finds Close button and clicks on it.
     */
    public HtmlPage clickOnCloseButton()
    {
        return clickButton(CLOSE_BUTTON, "CLOSE");
    }

    private HtmlPage clickButton(By locator, String buttonName)
    {
        try
        {
            drone.find(locator).click();
            if (drone.getCurrentPage().render() instanceof DashBoardPage)
            {
                return new DashBoardPage(drone);
            }
            else if (drone.getCurrentPage().render() instanceof SiteDashboardPage)
            {
                return new SiteDashboardPage(drone);
            }
            else
            {
                throw new PageOperationException("Returned page is not either DashBoardPage or SiteDashBoardPage");
            }
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the " + buttonName + " button.", te);
            throw new PageOperationException("Unable to click the " + buttonName + " Button.");
        }
    }

    /**
     * This method sets the given Search Term into Search Content Configure
     * Search Term box.
     * 
     * @param searchTerm
     */
    public void setSearchTerm(String searchTerm)
    {
        if (searchTerm == null)
        {
            throw new IllegalArgumentException("Search Term is required");
        }

        try
        {
            WebElement searchTermBox = drone.find(SEARCH_TERM_BOX);
            searchTermBox.clear();
            searchTermBox.sendKeys(searchTerm);
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the Search Term box.", te);
            throw new PageOperationException("Unable to find the Search Term box.");
        }
    }

    /**
     * This method sets the given title into Site Content Configure title box.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        if (StringUtils.isEmpty(title))
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            WebElement titleBox = drone.find(TITLE_BOX);
            titleBox.clear();
            titleBox.sendKeys(title);
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the Title box.", te);
            throw new PageOperationException("Unable to find the Title box.");
        }
    }

    /**
     * Method to select Search searchLimit from search searchLimit drop down
     * 
     * @param searchLimit
     */
    public void setSearchLimit(SearchLimit searchLimit)
    {
        try
        {
            Select limitSelectBox = new Select(drone.find(LIMIT_SELECT_BOX));
            limitSelectBox.selectByValue(String.valueOf(searchLimit.getValue()));
        }
        catch (NoSuchElementException te)
        {
            logger.error("Unable to find the Search SearchLimit drop down.", te);
            throw new PageOperationException("Unable to find the Search SearchLimit drop down.");
        }
    }

    /**
     * Method to get available search limit values
     * 
     * @return {@link List} of {@link java.lang.Integer}
     */
    public List<Integer> getAvailableListOfSearchLimitValues()
    {
        List<Integer> searchLimitList = new ArrayList<Integer>();
        Select limitSelectBox = new Select(drone.find(LIMIT_SELECT_BOX));

        for (WebElement element : limitSelectBox.getOptions())
        {
            searchLimitList.add(Integer.parseInt(element.getText()));
        }
        return searchLimitList;
    }

    /**
     * Finds whether help balloon is displayed on this page.
     * 
     * @return True if the balloon displayed else false.
     */
    public boolean isHelpBalloonDisplayed()
    {
        try
        {
            return drone.find(HELP_BALLOON).isDisplayed();
        }
        catch (NoSuchElementException elementException)
        {
        }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into
     * string.
     * 
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        try
        {
            return drone.findAndWait(HELP_BALLOON_TEXT).getText();
        }
        catch (TimeoutException elementException)
        {
            logger.error("Exceeded time to find the help ballon text", elementException);
        }
        throw new UnsupportedOperationException("Not able to find the help text");
    }

}
