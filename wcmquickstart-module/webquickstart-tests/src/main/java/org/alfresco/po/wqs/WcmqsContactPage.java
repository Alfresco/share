/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.po.wqs;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Created by Cristina Axinte on 1/7/2015.
 */
public class WcmqsContactPage extends WcmqsAbstractPage
{
    @RenderWebElement
    private final By PAGE_LOGO = By.cssSelector("#logo>a");
    private final By TITLE = By.xpath("//div[@class='interior-header']/*[text()='Contact']");

    private final By VISITOR_NAME = By.cssSelector("input[name=visitorName]");
    private final By VISITOR_EMAIL = By.cssSelector("input[name=visitorEmail]");
    private final By VISITOR_WEBSITE = By.cssSelector("input[name=visitorWebsite]");
    private final By FEEDBACK_COMMENT = By.cssSelector("textarea.bc-textarea");
    private final By POST_BUTTON = By.cssSelector("input.bc-submit");
    private final By ADD_SUCCESS_MESSAGE = By.cssSelector("div.contact-success");

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public WcmqsContactPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsContactPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsContactPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsContactPage render(final long time)
    {
        return render(new RenderTime(time));
    }


    /**
     * Method that types the visitor name
     *
     * @return String
     */
    public void setVisitorName(String visitorName)
    {
        try
        {
            WebElement element = drone.findAndWait(VISITOR_NAME);
            element.clear();
            element.sendKeys(visitorName);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find name field. " + e.toString());
        }
    }

    /**
     * Method that types the visitor email
     *
     * @return String
     */
    public void setVisitorEmail(String visitorEmail)
    {
        try
        {
            WebElement element = drone.findAndWait(VISITOR_EMAIL);
            element.clear();
            element.sendKeys(visitorEmail);

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find email field. " + e.toString());
        }
    }

    /**
     * Method that types the visitor website
     *
     * @return String
     */
    public void setVisitorWebsite(String visitorWebsite)
    {
        try
        {
            drone.findAndWait(VISITOR_WEBSITE).sendKeys(visitorWebsite);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find email field. " + e.toString());
        }
    }

    /**
     * Method that types the visitor comment
     *
     * @return String
     */
    public void setVisitorComment(String comment)
    {
        try
        {
            drone.findAndWait(FEEDBACK_COMMENT).sendKeys(comment);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find email field. " + e.toString());
        }
    }

    /**
     * Method to click on Create article
     *
     * @return WcmqsEditPage
     */
    public WcmqsContactPage clickPostButton()
    {
        try
        {
            drone.findAndWait(POST_BUTTON).click();
            return this;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to create article. " + e.toString());
        }
    }

    /**
     * Method to verify if add comment successful message is displayed
     *
     * @return true if displayed
     */
    public boolean isAddCommentMessageDisplay()
    {
        try
        {
            WebElement message = drone.findAndWait(ADD_SUCCESS_MESSAGE);
            return message.isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to get the add comment successful message
     *
     * @return WcmqsEditPage
     */
    public String getAddCommentSuccessfulMessage()
    {
        try
        {
            WebElement message = drone.findAndWait(ADD_SUCCESS_MESSAGE);
            return message.getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Successful message for adding comment. " + e.toString());
        }
    }

}
