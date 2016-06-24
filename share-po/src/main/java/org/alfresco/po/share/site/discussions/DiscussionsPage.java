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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.dashlet.mydiscussions.TopicsListPage;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site Discussions Page object
 * relating to Share site Discussions page
 * 
 * @author Marina Nenadovets
 */
public class DiscussionsPage extends TopicsListPage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By NEW_TOPIC_BTN = By.xpath("//button[contains(@id,'create-button-button')]");
    private static final By TOPIC_TITLE = By.cssSelector(".nodeTitle>a");
    private static final By TOPIC_CONTAINER = By.cssSelector("tbody[class='yui-dt-data']>tr");
    private static final String DISCUSSION_TOPIC_TITLE = "//span[@class='nodeTitle']/a[text()='%s']";
    private static final String TAG_NONE = "//a[contains(text(),'%s')]/ancestor::div[@class='node topic']"
            + "/following-sibling::div[@class='nodeFooter']/span[@class='nodeAttrLabel tagLabel']/following-sibling::span";
    private static final String TAG_NAME = "//a[contains(text(),'%s')]/ancestor::div[@class='node topic']"
            + "/following-sibling::div[@class='nodeFooter']/span[@class='tag']/a[text()='%s']";
    private static final By NO_TOPICS = By.cssSelector("td[class='yui-dt-empty']>div");

    @SuppressWarnings("unchecked")
    @Override
    public DiscussionsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NEW_TOPIC_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DiscussionsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to click New Topic button
     * 
     * @return NewTopicForm page
     */
    private NewTopicForm clickNewTopic()
    {
        try
        {
            findAndWait(NEW_TOPIC_BTN).click();
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate New Topic button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return factoryPage.instantiatePage(driver, NewTopicForm.class).render();
    }

    /**
     * Method to create new topic
     * 
     * @param titleField String
     * @param textLines String
     * @return TopicViewPage
     */

    public TopicViewPage createTopic(String titleField, String textLines)
    {
        try
        {
            DiscussionsPage discussionsPage = 
                    factoryPage.instantiatePage(driver,DiscussionsPage.class).render();
            NewTopicForm newTopicForm = discussionsPage.clickNewTopic();

            newTopicForm.setTitleField(titleField);
            try
            {
                checkNotNull(textLines);
            }
            catch (NullPointerException e)
            {
                throw new ShareException("The lines are null!");
            }
            newTopicForm.insertText(textLines);
            newTopicForm.clickSave();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
    }

    /**
     * Method to create new topic without text field
     * 
     * @param titleField String
     * @return TopicViewPage
     */

    public TopicViewPage createTopic(String titleField)
    {
        try
        {
            DiscussionsPage discussionsPage = factoryPage.instantiatePage(driver,DiscussionsPage.class).render();
            NewTopicForm newTopicForm = discussionsPage.clickNewTopic();

            newTopicForm.setTitleField(titleField);
            newTopicForm.clickSave();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
    }

    /**
     * Method to create new topic with tag
     * 
     * @param titleField String
     * @param textLines String
     * @param tag String
     * @return TopicViewPage
     */

    public TopicViewPage createTopic(String titleField, String textLines, String tag)
    {
        try
        {
            DiscussionsPage discussionsPage = factoryPage.instantiatePage(driver,DiscussionsPage.class).render();
            NewTopicForm newTopicForm = discussionsPage.clickNewTopic();

            newTopicForm.setTitleField(titleField);
            try
            {
                checkNotNull(textLines);
            }
            catch (NullPointerException e)
            {
                throw new ShareException("The lines are null!");
            }
            newTopicForm.insertText(textLines);
            newTopicForm.addTag(tag);

            newTopicForm.clickSave();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }
        return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
    }

    public TopicDirectoryInfo getTopicDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = findAndWait(By.xpath(String.format("//a[text()='%s']/../../../..", title)), getDefaultWaitTime());
            mouseOver(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return new TopicDirectoryInfoImpl(driver, row);
    }

    /**
     * Method to verify whether New Topic Link is available
     * 
     * @return true if enabled
     */
    public boolean isNewTopicEnabled()
    {
        try
        {
            return findAndWait(NEW_TOPIC_BTN).isEnabled();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    /**
     * Method to view topic
     * 
     * @return TopicViewPage
     */
    public TopicViewPage viewTopic(String title)
    {
        try
        {
            getTopicDirectoryInfo(title).viewTopic();
            return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to view the topic");
        }
    }

    /**
     * Method to edit topic
     * 
     * @param oldTitle String
     * @param newTitle String
     * @param txtLines String
     * @return TopicViewPage
     */
    public HtmlPage editTopic(String oldTitle, String newTitle, String txtLines)
    {
        try
        {
            NewTopicForm newTopicForm = getTopicDirectoryInfo(oldTitle).editTopic().render();
            newTopicForm.setTitleField(newTitle);
            newTopicForm.insertText(txtLines);
            newTopicForm.clickSave();
            waitUntilAlert();
            logger.info("Edited topic " + oldTitle);
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding buttons");
        }
    }

    /**
     * Method to edit topic
     * 
     * @param oldTitle String
     * @param newTitle String
     * @param txtLines String
     * @param tagName String
     * @return TopicViewPage
     */
    public TopicViewPage editTopic(String oldTitle, String newTitle, String txtLines, String tagName)
    {
        return editTopic(oldTitle, newTitle, txtLines, tagName, false);
    }

    /**
     * Method to edit topic
     *
     * @param oldTitle String
     * @param newTitle String
     * @param txtLines String
     * @param tagName String
     * @param removeTag boolean
     * @return TopicViewPage
     */
    public TopicViewPage editTopic(String oldTitle, String newTitle, String txtLines, String tagName,  boolean removeTag)
    {
        try
        {
            NewTopicForm newTopicForm = getTopicDirectoryInfo(oldTitle).editTopic().render();
            newTopicForm.setTitleField(newTitle);
            newTopicForm.insertText(txtLines);
            if (!removeTag) {
                newTopicForm.addTag(tagName);
            } else {
                newTopicForm.removeTag(tagName);
            }
            newTopicForm.clickSave();
            waitUntilAlert();
            logger.info("Edited topic " + oldTitle);
            return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding buttons");
        }
    }

    /**
     * Method to delete topic with confirmation
     * 
     * @param title String
     * @return Discussions Page
     */
    public DiscussionsPage deleteTopicWithConfirm(String title)
    {
        try
        {
            getTopicDirectoryInfo(title).deleteTopic();
            logger.info("Topic " + title + "was deleted");
            return factoryPage.instantiatePage(driver,DiscussionsPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete a topic");
        }
    }

    /**
     * Method to get topic count
     * 
     * @return number of topics
     */
    public int getTopicsCount()
    {
        try
        {
            if (!isElementDisplayed(TOPIC_CONTAINER))
            {
                return 0;
            }
            return driver.findElements(TOPIC_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + TOPIC_CONTAINER);
        }
    }

    /**
     * Method to verify whether edit topic is displayed
     * 
     * @param title String
     * @return true if displayed
     */
    public boolean isEditTopicDisplayed(String title)
    {
        return getTopicDirectoryInfo(title).isEditTopicDisplayed();
    }

    /**
     * Method to verify whether delete topic is displayed
     * 
     * @param title String
     * @return true if displayed
     */
    public boolean isDeleteTopicDisplayed(String title)
    {
        return getTopicDirectoryInfo(title).isDeleteTopicDisplayed();
    }

    /**
     * Return Object for interacting with left filter panel.
     * 
     * @return TopicsListFilter
     */
    public TopicsListFilter getTopicsListFilter()
    {
        return new TopicsListFilter(driver);
    }

    /**
     * Return list of titles displayed on page.
     * 
     * @return List<String>
     */
    public List<String> getTopicTitles()
    {
        List<String> topicTitles = new ArrayList<String>();
        List<WebElement> elements = findAndWaitForElements(TOPIC_TITLE);
        for (WebElement element : elements)
        {
            topicTitles.add(element.getText());
        }
        return topicTitles;
    }

    /**
     * Method to verify is discussion presented
     * 
     * @param title String
     * @return Return true if discussion displayed, and return false if discussion is absent
     */
    public boolean isTopicPresented(String title)
    {
        boolean isDisplayed;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            WebElement theItem = driver.findElement(By.xpath(String.format(DISCUSSION_TOPIC_TITLE, title)));
            isDisplayed = theItem.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return isDisplayed;
    }

    /**
     * Method to check tags for topic page
     * if param tag is null will be return true if 'Tags: (None)'
     * 
     * @param title String
     * @param tag String
     * @return return true if expected tag information presented
     */
    public boolean checkTags(String title, String tag)
    {
        boolean isDisplayed;
        WebElement element;
        String tagXpath;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        if (tag == null)
        {

            tagXpath = String.format(TAG_NONE, title);
            try
            {
                element = findAndWait(By.xpath(tagXpath));
                isDisplayed = element.getText().contains("None");
            }
            catch (NoSuchElementException ex)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate topic or 'Tags: (None)'", ex);
                }
                throw new PageOperationException("Unable to locate topic or 'Tags: (None)'");
            }

        }
        else
        {

            tagXpath = String.format(TAG_NAME, title, tag);
            try
            {
                element = findAndWait(By.xpath(tagXpath));
                isDisplayed = element.isDisplayed();
            }
            catch (NoSuchElementException te)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate expected tag or topic", te);
                }
                throw new PageOperationException("Unable to locate expected tag or topic");
            }
        }
        return isDisplayed;
    }

    /**
     * Method check that no topic displayed
     * 
     * @return true if no topic displayed
     */
    public boolean isNoTopicsDisplayed()
    {
        boolean isDisplayed;

        try
        {
            WebElement theItem = findAndWait(NO_TOPICS);
            isDisplayed = theItem.isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to locate expected element.", te);
            }
            throw new PageOperationException("Unable to locate expected element.");
        }
        return isDisplayed;
    }
}
