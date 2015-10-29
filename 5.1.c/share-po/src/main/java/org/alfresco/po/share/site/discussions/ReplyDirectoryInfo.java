/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site.discussions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Holds html elements related to Reply Directory info
 *
 * @author Marina.Nenadovets
 */
public class ReplyDirectoryInfo extends PageElement
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By EDIT_LINK = By.cssSelector(".onEditReply>a");
    private static final By DELETE_LINK = By.cssSelector(".onDeleteReply>a");
    private static final By REPLY_LINK = By.xpath(".//a[@class='reply-action-link']");
    private static final By SUB_REPLIES = By.xpath("./following-sibling::div[@class='indented']//div[@class='reply']//p");

    /**
     * Constructor
     */
    protected ReplyDirectoryInfo(WebDriver driver, WebElement webElement)
    {
        setWrappedElement(webElement);
    }

    /**
     * Method to click Edit
     */
    public void clickEdit()
    {
        try
        {
            findAndWait(EDIT_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + EDIT_LINK);
        }
    }

    /**
     * Method to click Delete
     */
    public void clickDelete()
    {
        try
        {
            findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }

    /**
     * Method to verify whether edit is displayed
     *
     * @return true if displayed
     */
    public boolean isEditDisplayed()
    {
        try
        {
            WebElement editLink = findElement(EDIT_LINK);
            return editLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("'Edit' link for reply didn't find on page.");
        }
        return false;
    }

    /**
     * Method to verify whether delete is displayed
     *
     * @return true if displayed
     */
    public boolean isDeleteDisplayed()
    {
        try
        {
            WebElement editLink = findElement(EDIT_LINK);
            return editLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("'Delete' link for reply didn't find on page.");
        }
        return false;
    }

    /**
     * Create sub reply to this reply
     *
     * @param text String
     * @return TopicViewPage
     */
    public TopicViewPage createSubReply(String text)
    {
        findElement(REPLY_LINK).click();
        AddReplyForm addReplyForm = new AddReplyForm();
        addReplyForm.insertText(text);
        logger.info("SubReply was created.");
        return addReplyForm.clickSubmit();
    }

    /**
     * Return true if reply has sub reply with text
     *
     * @param text String
     * @return boolean
     */
    public boolean isSubReply(String text)
    {
        try
        {
            return getElementWithText(SUB_REPLIES, text).isDisplayed();
        }
        catch (PageException e)
        {
            return false;
        }
    }

    /**
     * Return reply count
     *
     * @return int
     */
    public int getSubRepliesCount()
    {
        return findElements(SUB_REPLIES).size();
    }

    private WebElement getElementWithText(By selector, String text)
    {
        checkNotNull(text);
        checkNotNull(selector);
        try
        {
            List<WebElement> elements = findElements(selector);
            for (WebElement element : elements)
            {
                if (element.getText().contains(text))
                {
                    return element;
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            getElementWithText(selector, text);
        }
        throw new PageException(String.format("Element with selector[%s] and text[%s] not found on page.", selector, text));
    }
}
