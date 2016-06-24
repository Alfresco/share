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
package org.alfresco.po.share.site.discussions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Topic View page object
 * relating to Share topic view page
 *
 * @author Marina Nenadovets
 */
public class TopicViewPage extends DiscussionsPage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By REPLY_LINK = By.cssSelector(".onAddReply>a");
    private static final By BACK_LINK = By.cssSelector(".backLink>a");
    private static final By REPLY_CONTAINER = By.cssSelector(".reply");
    private static final By TAG = By.cssSelector(".tag-link");
    private static final By TAG_NONE = By.xpath("//span[@class='nodeAttrValue' and text()='(None)']");

    private static final By TOPIC_TITLE = By.xpath("//div[@class='nodeTitle']/a");
    private static final By TOPIC_TEXT = By.xpath("//div[contains(@class,'topicview')]//p");
    private static final By TOPIC_TAGS = By.xpath("//span[@class='tag']/a");
    private static final By TOPIC_REPLIES = By.xpath("//div/div[@class='reply']//p");

    @SuppressWarnings("unchecked")
    @Override
    public TopicViewPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(BACK_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    public TopicViewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method for clicking Back button from Topic View page
     *
     * @return Discussions Page object
     */
    public DiscussionsPage clickBack()
    {
        try
        {
            findAndWait(BACK_LINK).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find Back Link");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return factoryPage.instantiatePage(driver, DiscussionsPage.class);
    }

    /**
     * Method for clicking Reply button on topic view page
     *
     * @return AddReplyForm page object
     */
    private AddReplyForm clickReply()
    {
        try
        {
            findAndWait(REPLY_LINK).click();
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        AddReplyForm addReplyForm = new AddReplyForm();
        addReplyForm.setWebDriver(driver);
        return addReplyForm;
    }

    /**
     * Method for creating a reply
     *
     * @param replyText String
     * @return TopicViewPage
     */
    public TopicViewPage createReply(String replyText)
    {
        try
        {
            clickReply();
            waitUntilAlert();
            AddReplyForm addReplyForm = new AddReplyForm();
            addReplyForm.setWebDriver(driver);
            addReplyForm.insertText(replyText);
            addReplyForm.clickSubmit().render();
            waitUntilAlert();
            logger.info("Created a reply " + "'" + replyText + "'");
            return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * Method to verify whether reply link is available
     *
     * @return true if displayed
     */
    public boolean isReplyLinkDisplayed()
    {
        try
        {
            return findAndWait(REPLY_LINK, 2000).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    /**
     * Return information about replay with text.
     *
     * @param title String
     * @return ReplyDirectoryInfo
     */
    public ReplyDirectoryInfo getReplyDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = findAndWait(By.xpath(String.format("//div[@class='nodeContent']/div[2]/p[text()='%s']/../../..", title)), getDefaultWaitTime());
        }
        catch (TimeoutException te)
        {
            throw new ShareException(String.format("File directory info with title %s was not found", title), te);
        }
        return new ReplyDirectoryInfo(driver, row);
    }

    /**
     * Method to edit a reply
     *
     * @param title String
     * @param replyText String
     * @return Topic View Page
     */
    public TopicViewPage editReply(String title, String replyText)
    {
        getReplyDirectoryInfo(title).clickEdit();
        AddReplyForm addReplyForm = new AddReplyForm();
        addReplyForm.setWebDriver(driver);
        addReplyForm.insertText(replyText);
        addReplyForm.clickSubmit().render();
        logger.info("Reply was edited");
        return factoryPage.instantiatePage(driver, TopicViewPage.class).render().render();
    }

    public TopicViewPage deleteReply(String title)
    {
        getReplyDirectoryInfo(title).clickDelete();
        logger.info("Reply was deleted");
        return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
    }

    /**
     * Method to get the number of replies
     *
     * @return number of replies
     */
    public int getReplyCount()
    {
        try
        {
            if (!isElementDisplayed(REPLY_CONTAINER))
            {
                return 0;
            }
            return driver.findElements(REPLY_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + REPLY_CONTAINER);
        }
    }

    public boolean isEditReplyDisplayed(String reply)
    {
        boolean isDisplayed = getReplyDirectoryInfo(reply).isEditDisplayed();
        return isDisplayed;
    }

    public boolean isDeleteReplyDisplayed(String reply)
    {
        boolean isDisplayed = getReplyDirectoryInfo(reply).isDeleteDisplayed();
        return isDisplayed;
    }

    /**
     * Method to retrieve tag added to Discussion Topic
     *
     * @return String
     */
    public String getTagName()
    {
        try
        {
            if (!isElementDisplayed(TAG_NONE))
            {
                String tagName = findAndWait(TAG).getText();
                if (!tagName.isEmpty())
                    return tagName;
                else
                    throw new IllegalArgumentException("Cannot find tag");

            }
            else
                return driver.findElement(TAG_NONE).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
    }

    /**
     * Method return topic title text
     *
     * @return String
     */
    public String getTopicTitle()
    {
        return findAndWait(TOPIC_TITLE).getText();
    }

    /**
     * Method return topic body text
     *
     * @return String
     */
    public String getTopicText()
    {
        return findAndWait(TOPIC_TEXT).getText();
    }

    /**
     * click on topic tag
     *
     * @param tagName String
     * @return discussionsPage
     */
    public HtmlPage clickOnTag(String tagName)
    {
        getElementWithText(TOPIC_TAGS, tagName).click();
        return getCurrentPage();
    }

    /**
     * Return true if this topic has reply with text
     *
     * @param replyText String
     * @return boolean
     */
    public boolean isReplyDisplay(String replyText)
    {
        try
        {
            return getElementWithText(TOPIC_REPLIES, replyText).isDisplayed();
        }
        catch (PageException e)
        {
            return false;
        }
    }

    private WebElement getElementWithText(By selector, String text)
    {
        checkNotNull(text);
        checkNotNull(selector);
        try
        {
            List<WebElement> elements = findAndWaitForElements(selector);
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
