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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */
public class TopicDirectoryInfoImpl extends PageElement implements TopicDirectoryInfo
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By VIEW_LINK = By.cssSelector(".onViewTopic>a");
    private static final By EDIT_TOPIC = By.cssSelector(".onEditTopic>a");
    private static final By DELETE_TOPIC = By.cssSelector(".onDeleteTopic>a");
    private static final By REPLIES_COUNT = By.xpath(".//div[@class='nodeFooter']//span[2]");
    private static final By READ_LINK = By.xpath(".//div[@class='nodeFooter']/span[4]/a");
    private static final By TAG_LINKS = By.xpath(".//div[@class='nodeFooter']//span[@class='tag']/a");

    /**
     * Constructor
     */
    protected TopicDirectoryInfoImpl(WebDriver driver, WebElement webElement)
    {
        setWrappedElement(webElement);
    }

    /**
     * Method to view the topic from Discussion list page
     *
     * @return TopicViewPage
     */
    public TopicViewPage viewTopic()
    {
        try
        {

            findAndWait(VIEW_LINK).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return factoryPage.instantiatePage(driver, TopicViewPage.class).render();
    }

    /**
     * Method to view the topic from Discussion list page
     *
     * @return TopicViewPage
     */
    public NewTopicForm editTopic()
    {
        try
        {
            findAndWait(EDIT_TOPIC).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return factoryPage.instantiatePage(driver, NewTopicForm.class).render();
    }

    /**
     * Method to delete a topic
     *
     * @return Discussions Page
     */
    public DiscussionsPage deleteTopic()
    {
        try
        {
            findAndWait(DELETE_TOPIC).click();
            findAndWait(By.xpath("//span[@class='button-group']/span[1]/span/button")).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        DiscussionsPage p = factoryPage.instantiatePage(driver, DiscussionsPage.class);
        return p.waitUntilAlert().render();
    }

    /**
     * Method to verify whether edit topic is displayed
     *
     * @return boolean
     */
    public boolean isEditTopicDisplayed()
    {
        try
        {
            WebElement editLink = findElement(EDIT_TOPIC);
            return editLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to verify whether edit topic is displayed
     *
     * @return boolean
     */
    public boolean isDeleteTopicDisplayed()
    {
        try
        {
            WebElement deleteLink = findElement(DELETE_TOPIC);
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * @see org.alfresco.po.share.site.discussions.TopicDirectoryInfo
     */
    @Override
    public int getRepliesCount()
    {
        String countAsString = findAndWait(REPLIES_COUNT).getText();
        int count = Integer.parseInt(countAsString.replace("(", "").replace(")", ""));
        return count;
    }

    /**
     * @see org.alfresco.po.share.site.discussions.TopicDirectoryInfo
     */
    @Override
    public HtmlPage clickRead()
    {
        findAndWait(READ_LINK).click();
        return getCurrentPage();
    }

    /**
     * @see org.alfresco.po.share.site.discussions.TopicDirectoryInfo
     */
    @Override
    public DiscussionsPage clickOnTag(String tagName)
    {
        List<WebElement> elements = driver.findElements(TAG_LINKS);
        for (WebElement element : elements)
        {
            if (tagName.equals(element.getText()))
            {
                element.click();
                return factoryPage.instantiatePage(driver, DiscussionsPage.class).waitUntilAlert().render();
            }
        }
        throw new PageException(String.format("Tag[%s] don't found.", tagName));
    }

}
