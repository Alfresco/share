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
package org.alfresco.po.share.dashlet;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareLink;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.forumsummary")
/**
 * My Discussions dashlet object, holds all element of the HTML relating to My Discussions dashlet
 *
 * @author jcule
 */
public class MyDiscussionsDashlet extends AbstractDashlet implements Dashlet
{

    private static Log logger = LogFactory.getLog(MyDiscussionsDashlet.class);

    private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.forumsummary";

    // empty dashlet message
    private static final String EMPTY_DASHLET_MESSAGE = "div[id$='_default-filtered-topics']>table>tbody>tr.yui-dt-first.yui-dt-last>td>div";

    // filter drop downs
    private static final String DEFAULT_TOPICS_BUTTON = "button[id$='_default-topics-button']";
    private static final String DEFAULT_HISTORY_BUTTON = "button[id$='_default-history-button']";
    private static final String DASHLET_LIST_OF_FILTER_BUTTONS = "div[class*='yui-button-menu yui-menu-button-menu visible']>div.bd>ul.first-of-type>li>a";

    // help icon
    private static final String DASHLET_HELP_BUTTON = "div[class='dashlet forumsummary resizable yui-resize']>div.titleBarActions>div.titleBarActionIcon.help";
    private static final String DASHLET_HELP_BALLOON_HEADER = "div[style*='visible']>div>div.balloon>div.text>p";
    private static final String DASHLET_HELP_BALLOON_MSG = "div[style*='visible']>div>div.balloon>div.text>p>p";
    private static final String DASHLET_HELP_BALLOON_CLOSE_BUTTON = "div[style*='visible']>div>div.balloon>div.closeButton";
    private static final String DASHLET_HELP_BALLOON = "div[style*='visible']>div>div.balloon";

    // new topic icon
    private final static By NEW_TOPIC = By.xpath("//a[text()='New Topic']");

    // topic title
    private static final String TOPIC_TITLE = "//span[@class='nodeTitle']//a[text()='%s']";

    // Topics in the list
    private List<ShareLink> userLinks;
    private List<ShareLink> topicTitlesLinks;
    private List<TopicStatusDetails> topicStatusDetails;

    // Types of links in topics
    public enum LinkType
    {
        User, Topic;
    }

    @SuppressWarnings("unchecked")
    public MyDiscussionsDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    @SuppressWarnings("unchecked")
    public MyDiscussionsDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER)),
                getVisibleRenderElement(By.cssSelector(DEFAULT_TOPICS_BUTTON)), getVisibleRenderElement(By.cssSelector(DEFAULT_HISTORY_BUTTON)));
        return this;
    }

    /**
     * Gets empty dashlet message
     */
    public String getEmptyDashletMessage()
    {
        try
        {
            return driver.findElement(By.cssSelector(EMPTY_DASHLET_MESSAGE)).getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find empty dashlet message.", nse);
        }
        throw new PageOperationException("Error in finding the css for empty dashlet message.");
    }

    /**
     * Find whether Help Button is displayed on this dashlet.
     *
     * @return boolean True if displayed else false.
     */
    public boolean isHelpButtonDisplayed()
    {

        try
        {
            scrollDownToDashlet();
            getFocus();
            return findAndWait(By.cssSelector(DASHLET_HELP_BUTTON)).isDisplayed();
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

    /**
     * Find whether New Topic Link is displayed on this dashlet.
     *
     * @return boolean True if displayed else false.
     */
    public boolean isNewTopicLinkDisplayed()
    {

        try
        {
            getFocus();
            return findAndWait(NEW_TOPIC).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the new topic icon.", te);
            }
            return false;
        }
    }

    /**
     * Clicks on help button
     */
    public void clickHelpButton()
    {
        try
        {
            driver.findElement(By.cssSelector(DASHLET_HELP_BUTTON)).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find the help icon.", nse);
            throw new PageOperationException("Unable to click the Help icon");
        }
    }

    /**
     * Clicks on New Topic button
     */
    public HtmlPage clickNewTopicButton()
    {
        try
        {
            driver.findElement(NEW_TOPIC).click();
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find the New Topic icon.", nse);

        }
        throw new PageOperationException("Unable to click the New Topic icon");
    }

    /**
     * Finds whether help balloon is displayed on this page.
     *
     * @return True if the balloon displayed else false.
     */
    public boolean isBalloonDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector(DASHLET_HELP_BALLOON)).isDisplayed();
        }
        catch (NoSuchElementException elementException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the ballon", elementException);
            }
        }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into string.
     *
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        StringBuffer str = new StringBuffer();
        str.append(getHelpButtonMessage(DASHLET_HELP_BALLOON_HEADER));
        str.append(getHelpButtonMessage(DASHLET_HELP_BALLOON_MSG));
        return str.toString();
    }

    /**
     * This method gets the list of messages based on given cssselector and appends it to a string.
     *
     * @param cssLocator String
     */
    private String getHelpButtonMessage(String cssLocator)
    {
        if (cssLocator == null || cssLocator.isEmpty())
        {
            throw new UnsupportedOperationException("Input cssLocator identifier is required");
        }
        List<WebElement> links = driver.findElements(By.cssSelector(cssLocator));
        if (links == null)
        {
            throw new UnsupportedOperationException("Not able to find the css location");
        }
        StringBuffer sb = new StringBuffer();

        for (WebElement webElement : links)
        {
            sb.append(webElement.getText());
        }
        return sb.toString();
    }

    /**
     * Retrieves the closeButton for Help balloon window based on the given cssSelector and clicks on it.
     */
    public MyDiscussionsDashlet closeHelpBallon()
    {
        try
        {
            driver.findElement(By.cssSelector(DASHLET_HELP_BALLOON_CLOSE_BUTTON)).click();
            waitUntilElementDisappears(By.cssSelector(DASHLET_HELP_BALLOON_CLOSE_BUTTON), 1);
            return this;
        }
        catch (NoSuchElementException elementException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the ballon", elementException);
            }
            throw new UnsupportedOperationException("Exceeded time to find the help ballon close button.", elementException);
        }
    }

    /**
     * Retrieves the My Discussions My Topics button based on the given cssSelector and clicks on it.
     */
    public void clickTopicsButtton()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            findAndWait(By.cssSelector(DEFAULT_TOPICS_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Topic Filter Button.", e);
            }
        }
    }

    /**
     * Retrieves the My Discussions HistoryFilter button based on the given cssSelector and clicks on it.
     */
    public void clickHistoryButtton()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            findAndWait(By.cssSelector(DEFAULT_HISTORY_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Topic History Filter Button.", e);
            }
        }
    }

    /**
     * My Discussions topic filters displayed in the dropdown.
     *
     * @return <List<String>> topic filter links
     */
    public List<MyDiscussionsTopicsFilter> getTopicFilters()
    {
        List<MyDiscussionsTopicsFilter> list = new ArrayList<MyDiscussionsTopicsFilter>();
        try
        {
            for (WebElement element : driver.findElements(By.cssSelector(DASHLET_LIST_OF_FILTER_BUTTONS)))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(MyDiscussionsTopicsFilter.getFilter(text.trim()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to access My Discuss dashlet topic filters data", nse);
        }

        return list;
    }

    /**
     * My Discussions history filters displayed in the dropdown.
     *
     * @return <List<String>> topic filter links
     */
    public List<MyDiscussionsHistoryFilter> getHistoryFilters()
    {
        List<MyDiscussionsHistoryFilter> list = new ArrayList<MyDiscussionsHistoryFilter>();
        try
        {
            for (WebElement element : driver.findElements(By.cssSelector(DASHLET_LIST_OF_FILTER_BUTTONS)))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(MyDiscussionsHistoryFilter.getFilter(text.trim()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to access My Discussions history dashlet filters data", nse);
        }

        return list;
    }

    /**
     * Select the given {@link MyDiscussionsTopicsFilter} on My Discussion Dashlet.
     *
     * @param filter - The {@link MyDiscussionsTopicsFilter} to be selected
     * @return {@link org.alfresco.po.HtmlPage}
     */
    public HtmlPage selectTopicsFilter(MyDiscussionsTopicsFilter filter)
    {
        clickTopicsButtton();
        List<WebElement> filterElements = findAndWaitForElements(By.cssSelector(DASHLET_LIST_OF_FILTER_BUTTONS));
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(filter.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Get the default My Discussion topic Filter on My Discussion dashlet.
     *
     * @return {@link MyDiscussionsTopicsFilter}
     */
    public MyDiscussionsTopicsFilter getCurrentTopicFilter()
    {
        try
        {
            return MyDiscussionsTopicsFilter.getFilter(driver.findElement(By.cssSelector(DEFAULT_TOPICS_BUTTON)).getText());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate topic filter elements from the dropdown", e);
        }
    }

    /**
     * Get the default My Discussion history Filter on My Discussion dashlet.
     *
     * @return {@link MyDiscussionsHistoryFilter}
     */
    public MyDiscussionsHistoryFilter getCurrentHistoryFilter()
    {
        try
        {
            return MyDiscussionsHistoryFilter.getFilter(driver.findElement(By.cssSelector(DEFAULT_HISTORY_BUTTON)).getText());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate topic history filter from the dropdown", e);
        }
    }

    /**
     * Select the given {@link MyDiscussionsTopicsFilter} on My Discussion Dashlet.
     *
     * @param lastDayTopics - The {@link MyDiscussionsTopicsFilter} to be selected
     * @return {@link org.alfresco.po.HtmlPage}
     */
    public HtmlPage selectTopicsHistoryFilter(MyDiscussionsHistoryFilter lastDayTopics)
    {
        clickHistoryButtton();
        List<WebElement> filterElements = driver.findElements(By.cssSelector(DASHLET_LIST_OF_FILTER_BUTTONS));
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(lastDayTopics.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Select a link from topics list by a given name
     * with a default of user, as there is additional
     * link - topic title.
     *
     * @param name identifier to match against link title
     */
    public ShareLink selectLink(final String name)
    {
        return selectLink(name, LinkType.User);
    }

    /**
     * Find the match and selects on the link.
     *
     * @param name identifier to match against link title
     * @param type that determines topic title or user link
     */
    private synchronized ShareLink selectLink(final String name, LinkType type)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        if (userLinks == null || topicTitlesLinks == null)
        {
            populateData();
        }
        switch (type)
        {
            case Topic:
                return extractLink(name, topicTitlesLinks);
            case User:
                return extractLink(name, userLinks);
            default:
                throw new IllegalArgumentException("Invalid link type specified");
        }

    }

    /**
     * Extracts the link from the ShareLink List that matches
     * the title.
     *
     * @param name Title identifier
     * @param list Collection of ShareList
     * @return ShareLink link match
     */
    private ShareLink extractLink(final String name, List<ShareLink> list)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("title of item is required");
        }
        if (!list.isEmpty())
        {
            for (ShareLink link : list)
            {
                if (name.equalsIgnoreCase(link.getDescription()))
                {
                    return link;
                }
            }
        }
        throw new PageException(String.format("Link searched: %s can not be found on the page", name));
    }

    /**
     * Selects the topic title link on the topic that appears on my discussions dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     * @return {@link org.alfresco.po.share.ShareLink} target link
     */
    public ShareLink selectTopicTitle(final String name)
    {
        return selectLink(name, LinkType.Topic);
    }

    /**
     * Selects the user link on a topic that appears on my discussion dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     * @return {@link org.alfresco.po.share.ShareLink} target link
     */
    public ShareLink selectTopicUser(final String name)
    {
        return selectLink(name, LinkType.User);
    }

    /**
     * Populates all the possible links that appear in the dashlet topic list
     * like user and topic title
     */

    private synchronized void populateData()
    {
        try
        {
            userLinks = new ArrayList<ShareLink>();
            topicTitlesLinks = new ArrayList<ShareLink>();

            for (WebElement topicTitleLink : driver
                    .findElements(By.cssSelector("div[id$='_default-filtered-topics'] div.node.topic span.nodeTitle a:nth-of-type(1)")))
            {
                topicTitlesLinks.add(new ShareLink(topicTitleLink, driver, factoryPage));
            }
            for (WebElement topicUserLink : driver.findElements(By.cssSelector("div[id$='_default-filtered-topics'] div.node.topic div.published a:nth-of-type(1)")))
            {
                userLinks.add(new ShareLink(topicUserLink, driver, factoryPage));
            }

        }
        catch (NoSuchElementException nse)
        {
        }
    }

    /**
     * Populates topic status details that appear in the dashlet topic list
     * like creation time and update time
     */
    private synchronized void populateUpdatedTopicStatusDetails()
    {
        try
        {
            topicStatusDetails = new ArrayList<TopicStatusDetails>();

            List<WebElement> links = driver.findElements(By.cssSelector("div[id$='_default-filtered-topics'] div.node.topic"));
            for (WebElement link : links)
            {
                WebElement updated = link.findElement(By.cssSelector("span.nodeTitle span:nth-of-type(1)"));
                WebElement created = link.findElement(By.cssSelector("div.published span:nth-of-type(1)"));
                WebElement numberOfReplies = link.findElement(By.cssSelector("div.published span:nth-of-type(2)"));
                WebElement replyDetails = link.findElement(By.cssSelector("div.published span:nth-of-type(3)"));

                TopicStatusDetails topicDetails = new TopicStatusDetails(created.getText(), updated.getText());
                topicDetails.setNumberOfReplies(numberOfReplies.getText());
                topicDetails.setReplyDetails(replyDetails.getText());
                topicStatusDetails.add(topicDetails);
            }

        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }
    }

    /**
     * Get topics
     */
    public synchronized List<ShareLink> getTopics(LinkType linkType)
    {
        if (linkType == null)
        {
            throw new UnsupportedOperationException("LinkType is required");
        }
        scrollDownToDashlet();
        getFocus();
        populateData();
        switch (linkType)
        {
            case Topic:
                return topicTitlesLinks;
            case User:
                return userLinks;
            default:
                throw new IllegalArgumentException("Invalid link type specified");
        }
    }

    /**
     * Get topics - just for updated topics
     */
    public synchronized List<TopicStatusDetails> getUpdatedTopics()
    {

        populateUpdatedTopicStatusDetails();
        return topicStatusDetails;

    }

    /**
     * Checks if topic title is dispalyed in the dashlet
     *
     * @return boolean
     */

    public boolean isTopicTitleDisplayed(String topicTitle)
    {
        try
        {
            WebElement content = findAndWaitWithRefresh(By.xpath((String.format(TOPIC_TITLE, topicTitle))), getDefaultWaitTime());
            return content.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * This method gets the focus by placing mouse over on My Discussions Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER)));
    }

    public void resizeDashlet(int x, int y)
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            WebElement resizeHandleElement = dashlet.findElement(getResizeHandle());
            dragAndDrop(resizeHandleElement, x, y);
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and clickresize handle.", e);
            }
        }

    }

}
