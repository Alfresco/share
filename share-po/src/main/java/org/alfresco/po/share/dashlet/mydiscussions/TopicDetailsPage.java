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

package org.alfresco.po.share.dashlet.mydiscussions;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * TopicDetailsPage page object, holds all element of the HTML page relating to Topic Details
 * 
 * @author jcule
 */
public class TopicDetailsPage extends SitePage
{

    private static Log logger = LogFactory.getLog(TopicDetailsPage.class);

    private static final String TOPIC_TITLE = "div.nodeContent div.nodeTitle a";
    private static final String CREATED_BY = "div.published span:nth-child(1)";
    private static final String CREATION_DATE = "div.published span:nth-child(2)";
    private static final String TOPIC_AUTHOR = "div.published span:nth-child(5) a";
    private static final String TOPIC_TEXT = "div.nodeContent div.content.yuieditor";
    private static final String TOPIC_REPLIES = "div.published span:nth-child(7)";
    private static final String TOPIC_TAGS_LINK = "div[id$='_default-topicview.node'] div.nodeFooter span.tag a.tag-link";
    private static final String TOPIC_TAGS_LIST = "span.tag>a.tag-link";
    private static final String REPLY_LINK = "div.onAddReply a";
    private static final String EDIT_LINK = "div.onEditTopic a";
    private static final String DELETE_LINK = "div.onDeleteTopic a";
    private final static By NEW_TOPIC = By.xpath("//button[text()='New Topic']");
    private final static By CREATE_REPLY = By.xpath("//button[text()='Create']");

    /**
     * Constructor
     * 
     * @param drone
     */
    public TopicDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicDetailsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(TOPIC_TITLE)));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicDetailsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicDetailsPage render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Returns topic title
     * 
     * @return
     */
    public String getTopicTitle()
    {
        try
        {
            WebElement topicTitle = drone.find(By.cssSelector(TOPIC_TITLE));
            return topicTitle.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable find topic title on Topic Details Page.", nse);

        }
        throw new PageOperationException("Cannot find topic title on Topic Details Page.");
    }

    /**
     * Returns topic creation date
     * 
     * @return
     */
    public String getTopicCreationDate()
    {
        try
        {
            WebElement topicTitle = drone.find(By.cssSelector(CREATION_DATE));
            return topicTitle.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable find topic creation date on Topic Details Page.", nse);

        }
        throw new PageOperationException("Cannot find topic creation date on Topic Details Page.");
    }

    /**
     * Returns topic created by text
     * 
     * @return
     */
    public String getTopicCreatedBy()
    {
        try
        {
            WebElement topicCreatedBy = drone.find(By.cssSelector(CREATED_BY));
            return topicCreatedBy.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable find topic created by text on Topic Details Page.", nse);

        }
        throw new PageOperationException("Cannot find topic created by on Topic Details Page.");
    }

    /**
     * Returns topic author
     * 
     * @return
     */
    public String getTopicAuthor()
    {
        try
        {
            WebElement topicTitle = drone.find(By.cssSelector(TOPIC_AUTHOR));
            return topicTitle.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable find topic author on Topic Details Page.", nse);

        }
        throw new PageOperationException("Cannot find topic author on Topic Details Page.");
    }

    /**
     * Returns topic text
     * 
     * @return
     */
    public String getTopicText()
    {
        try
        {
            WebElement topicText = drone.find(By.cssSelector(TOPIC_TEXT));
            return topicText.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable find topic text on Topic Details Page.", nse);

        }
        throw new PageOperationException("Cannot find topic text on Topic Details Page.");
    }

    /**
     * Returns topic author
     * 
     * @return
     */
    public String getTopicReplies()
    {
        try
        {
            WebElement topicReplies = drone.find(By.cssSelector(TOPIC_REPLIES));
            return topicReplies.getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable find topic replies on Topic Details Page.", nse);

        }
        throw new PageOperationException("Cannot find topic replies on Topic Details Page.");
    }

    /**
     * Clicks on topic title
     */
    public HtmlPage clickOnTopicTitle()
    {
        try
        {
            WebElement topicTitle = drone.findAndWait(By.cssSelector(TOPIC_TITLE));
            topicTitle.click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css title on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic title on Topic Details Page.");
    }

    /**
     * Clicks on topic's author
     */
    public HtmlPage clickOnTopicAuthor()
    {
        try
        {
            WebElement topicTitle = drone.findAndWait(By.cssSelector(TOPIC_AUTHOR));
            topicTitle.click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css author on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic author on Topic Details Page.");
    }

    /**
     * Clicks on topic tags link
     */
    public HtmlPage clickOnTopicTagsLink()
    {
        try
        {
            WebElement topicTitle = drone.findAndWait(By.cssSelector(TOPIC_TAGS_LINK));
            topicTitle.click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css for topic tags link on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic tags link on Topic Details Page.");
    }

    /**
     * Gets the list of tags for topic
     * 
     * @return List<String> List of tags added to the topic
     */
    public List<String> getTags()
    {
        List<String> tagsList = new ArrayList<String>();
        try
        {
            List<WebElement> tagList = drone.findAll(By.cssSelector(TOPIC_TAGS_LIST));
            for (WebElement tag : tagList)
            {
                tagsList.add(tag.getText());
            }
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Cannot find topic tags", te);
            }
        }
        return tagsList;
    }

    /**
     * Clicks on topic reply link
     */
    public TopicDetailsPage clickOnReplyLink()
    {
        try
        {
            WebElement topicReply = drone.findAndWait(By.cssSelector(REPLY_LINK));
            topicReply.click();
            return new TopicDetailsPage(drone);
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css for topic reply link on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic reply link on Topic Details Page.");
    }

    /**
     * Clicks on topic create button
     */

    public TopicDetailsPage clickOnCreateReply()
    {
        try
        {
            WebElement replyCreateButton = drone.findAndWait(CREATE_REPLY);
            replyCreateButton.click();
            return new TopicDetailsPage(drone);
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css for topic reply link on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic reply link on Topic Details Page.");
    }

    /**
     * Clicks on topic edit link
     */
    public HtmlPage clickOnEditLink()
    {
        try
        {
            WebElement editTopic = drone.findAndWait(By.cssSelector(EDIT_LINK));
            editTopic.click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css for topic edit link on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic edit link on Topic Details Page.");
    }

    /**
     * Clicks on topic delete link
     */
    public DeleteTopicDialogPage clickOnDeleteLink()
    {
        try
        {
            WebElement deleteTopic = drone.findAndWait(By.cssSelector(DELETE_LINK));
            deleteTopic.click();
            return new DeleteTopicDialogPage(drone);
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css for topic delete link on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find topic delete link on Topic Details Page.");
    }

    /**
     * Clicks on new topic link
     */
    public HtmlPage clickOnNewTopicLink()
    {
        try
        {
            WebElement newTopic = drone.findAndWait(NEW_TOPIC);
            newTopic.click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find css for new topic link on Topic Details Page.", te);
            }
        }
        throw new PageOperationException("Cannot find new topic link on Topic Details Page.");
    }

}
