/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.dashlet;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(xpath="//div[count(./div[@class='toolbar'])=0 and @class='dashlet']")
/**
 * @author Aliaksei Boole
 */
public class MyProfileDashlet extends AbstractDashlet implements Dashlet
{
    private final Log logger = LogFactory.getLog(this.getClass());
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[count(./div[@class='toolbar'])=0 and @class='dashlet']");
    private static final By VIEW_FULL_PROFILE = By.xpath("//div[count(./div[@class='toolbar'])=0 and @class='dashlet']//div/span/span/a");
    private static final By AVATAR = By.cssSelector (".photo>img");
    static final By HELP_ICON = By.xpath("//div[contains(@class, 'help')]");
    private final By userName = By.cssSelector(".namelabel>a");
    @SuppressWarnings("unchecked")
    public MyProfileDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    scrollDownToDashlet();
                    getFocus(DASHLET_CONTAINER_PLACEHOLDER);
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for MyProfileDashlet dashlet was not found ", e);
                }
                catch (StaleElementReferenceException ste)
                {
                    logger.error("DOM has changed therefore page should render once change", ste);
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find my profile dashlet", te);
        }
        return this;
    }

    /**
     * Method to verify View Full Profile is displayed
     */
    public boolean isViewFullProfileDisplayed()
    {
        try
        {
            return findAndWait(VIEW_FULL_PROFILE).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }

    }

    /**
     * Click on View Full Profile button
     */
    public HtmlPage clickViewFullProfileButton()
    {
        try
        {
            driver.findElement(VIEW_FULL_PROFILE).click();
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find the View Full Profile button", nse);
        }
        throw new PageOperationException("Unable to click the View Full Profile button");
    }

    /**
     * Method to get element text for user name
     *
     * @return String
     */
    public String getUserName()
    {
        try
        {
            return findAndWait(userName).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find user name");
        }

        throw new PageOperationException("Unable to find the username: " + userName);
    }

    @FindBy(css=".fieldvalue>a") WebElement emailName;
    /**
     * Method to get element text for email.
     *
     * @return String
     */
    public String getEmailName()
    {
        return emailName.getText();
    }


    /**
     * Method to verify avatar is displayed
     *
     * @return true - if avatar displayed.
     */

    public boolean isAvatarDisplayed()
    {
        try
        {
            return findAndWait(AVATAR).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    /**
     * Click on the user name to go to the user profile.
     *
     * @return HtmlPage
     */
    public HtmlPage clickOnUserName()
    {
        try
        {
            findAndWait(userName).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find user name");
        }

        throw new PageOperationException("Unable to find the username: " + userName);
    }

    /**
     * Finds whether help icon is displayed or not.
     *
     * @return True if the help icon displayed else false.
     */
    public boolean isHelpIconPresent()
    {
        try
        {
            return this.dashlet.findElement(HELP_ICON).isEnabled();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the help icon.", te);
            }
        }

        return false;
    }
    @SuppressWarnings("unchecked")
    @Override
    public MyProfileDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
