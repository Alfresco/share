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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * My activities dashlet object, holds all element of the HTML page relating to
 * share's my activities dashlet on dashboard page.
 *
 * @author Michael Suzuki
 * @since 1.3
 */
@SuppressWarnings("unused")
public class MyActivitiesDashlet extends AbstractDashlet implements Dashlet
{
    private final Log logger = LogFactory.getLog(this.getClass());
    private static final By DIV_CLASS_DASHLET_PLACEHOLDER = By.cssSelector("div[id$='default-activityList']");
    private static final String DASHLET_DIV_PLACEHOLDER = "div.dashlet.activities";
    private static final By MY_ACTIVITIES_BUTTON = By.cssSelector("button[id$='default-user-button']");
    private static final By MY_ACTIVITIES_ITEM = By.cssSelector("div.activities div.visible ul.first-of-type li a");
    private static final By RSS_FEED_BUTTON = By.cssSelector(".titleBarActionIcon.rss");
    private static final By HISTORY_BUTTON = By.cssSelector("button[id$='default-range-button']");
    private static final By DASHLET_LIST_OF_FILTER = By.cssSelector("div.activities div.visible ul.first-of-type li a");
    private static final By MY_ACTIVITIES_MORE_LINK = By
            .xpath("//div[@class='activity']/following::div[@class='hidden']/preceding-sibling::div[@class='more']/a");

    public enum LinkType
    {
        User, Document, Site;
    }

    private List<ActivityShareLink> activity;

    /**
     * Constructor.
     */
    protected MyActivitiesDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_DIV_PLACEHOLDER));
        setResizeHandle(By.xpath(".//div[contains (@class, 'yui-resize-handle')]"));
    }

    @SuppressWarnings("unchecked")
    public MyActivitiesDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyActivitiesDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Populates all the possible links that appear on the dashlet
     * data view, the links are of user, document or site.
     *
     * @param selector css placeholder for the dashlet data
     */
    private synchronized void populateData()
    {
        activity = new ArrayList<ActivityShareLink>();
        ArrayList<ShareLink> shareLinks = new ArrayList<>();

        try
        {
            List<WebElement> linksMore = drone.findAll(MY_ACTIVITIES_MORE_LINK);
            if (!linksMore.isEmpty())
            {
                for (WebElement link : linksMore)
                {
                    link.click();
                }
            }

            List<WebElement> links = drone.findAll(By.cssSelector("div[id$='default-activityList'] > div.activity div:last-child[class$='content']"));

            for (WebElement div : links)
            {
                WebElement userLink = div.findElement(By.cssSelector("div.content>span.detail>a[class^='theme-color']"));

                ShareLink user = new ShareLink(userLink, drone);

                String description = div.findElement(By.cssSelector("div.content>span.detail")).getText();

                if (div.findElements(By.cssSelector("div.content>span.detail>a")).size() < 2)
                {
                    activity.add(new ActivityShareLink(user, description));
                }

                else if (div.findElements(By.cssSelector("div.content>span.detail>a")).size() == 2)
                {

                    if (div.findElements(By.cssSelector("div.content>span.detail>a[class^='theme-color']")).size() > 1)
                    {
                        List<WebElement> userLinks = div.findElements(By.cssSelector("div.content>span.detail>a[class^='theme-color']"));
                        for (WebElement element : userLinks)
                        {
                            shareLinks.add(new ShareLink(element, drone));
                        }
                        activity.add(new ActivityShareLink(shareLinks.get(0), shareLinks.get(1), description));
                    }
                    else
                    {
                        WebElement siteLink = div.findElement(By.cssSelector("div.content>span.detail>a[class^='site-link']"));
                        ShareLink site = new ShareLink(siteLink, drone);
                        activity.add(new ActivityShareLink(user, site, description));
                    }
                }

                else if (div.findElements(By.cssSelector("div.content>span.detail>a")).size() > 2)
                {
                    WebElement siteLink = div.findElement(By.cssSelector("div.content>span.detail>a[class^='site-link']"));
                    ShareLink site = new ShareLink(siteLink, drone);

                    WebElement documentLink = div.findElement(By.cssSelector("div.content>span.detail>a[class*='item-link']"));
                    ShareLink document = new ShareLink(documentLink, drone);
                    activity.add(new ActivityShareLink(user, document, site, description));
                }

            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }
    }

    @SuppressWarnings("unchecked")
    public MyActivitiesDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                try
                {
                    synchronized (this)
                    {
                        try
                        {
                            this.wait(100L);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    if (isEmpty(DASHLET_DIV_PLACEHOLDER))
                    {
                        // There are no results
                        break;
                    }
                    else if (isVisibleResults())
                    {
                        // Results are visible
                        break;
                    }
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    /**
     * Select a link from activity list by a given name
     * with a default of document type, as there are additional
     * links such as user or site in the same web element.
     *
     * @param name identifier to match against link title
     */
    public ActivityShareLink selectLink(final String name)
    {
        return selectLink(name, LinkType.Document);
    }

    /**
     * Find the match and selects on the link.
     *
     * @param name identifier to match against link title
     * @param enum that determines document, site or user type link
     */
    private synchronized ActivityShareLink selectLink(final String name, LinkType type)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        if (activity == null)
        {
            populateData();
        }
        for (ActivityShareLink link : activity)
        {
            ShareLink theLink = null;
            switch (type)
            {
                case Document:
                    theLink = (link.getDocument() != null) ? link.getDocument() : null;
                    break;
                case Site:
                    theLink = link.getSite();
                    break;
                case User:
                    theLink = link.getUser();
                    break;
            }
            if (theLink != null && name.equalsIgnoreCase(theLink.getDescription()))
            {
                return link;
            }

        }
        throw new PageException("Link searched for can not be found on the page");
    }

    /**
     * Selects the document link on the activity that appears on my activities dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     */
    public HtmlPage selectActivityDocument(final String name)
    {
        selectLink(name, LinkType.Document);
        throw new PageException("no documents found matching the given title: " + name);
    }

    /**
     * Selects the user link on an activity that appears on my activities dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     */
    public HtmlPage selectActivityUser(final String name)
    {
        selectLink(name, LinkType.User);
        throw new PageException("no documents found matching the given title: " + name);
    }

    /**
     * Selects a the site link on an activity that appears on my activities dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     */
    public HtmlPage selectActivitySite(final String name)
    {
        selectLink(name, LinkType.User);
        throw new PageException("no documents found matching the given title: " + name);
    }

    /**
     * Get Activities based on the link type.
     *
     * @param linktype Document, User or Site
     * @return {@link ShareLink} collection
     */
    public synchronized List<ActivityShareLink> getActivities()
    {
        if (activity == null)
        {
            populateData();
        }
        return activity;
    }

    /**
     * Select option from "My Activities" drop down
     *
     * @param myActivitiesOption
     * @return {@link ShareLink} collection
     * @author Cristina.Axinte
     */
    public HtmlPage selectOptionFromUserActivities(String myActivitiesOption)
    {
        if (myActivitiesOption == null)
        {
            throw new UnsupportedOperationException("Activity type is required");
        }
        try
        {
            WebElement myActivities = drone.find(MY_ACTIVITIES_BUTTON);
            myActivities.click();

            List<WebElement> filterElements = drone.findAll(MY_ACTIVITIES_ITEM);
            if (filterElements != null)
            {
                for (WebElement webElement : filterElements)
                {
                    if (webElement.getText().equals(myActivitiesOption))
                    {
                        webElement.click();
                        return FactorySharePage.resolvePage(drone);
                    }
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("My Activities option not present" + nse.getMessage());
        }
        throw new PageOperationException(myActivitiesOption + " option not present.");

    }

    /**
     * Select option from history filter drop down
     *
     * @param SiteActivitiesHistoryFilter
     * @return {@link ShareLink} collection
     * @author Cristina.Axinte
     */
    public HtmlPage selectOptionFromHistoryFilter(SiteActivitiesHistoryFilter lastDays)
    {
        drone.findAndWait(HISTORY_BUTTON).click();
        List<WebElement> filterElements = drone.findDisplayedElements(DASHLET_LIST_OF_FILTER);
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(lastDays.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Method for navigate to RSS Feed Page from site activity dashlet.
     *
     * @param username
     * @param password
     * @return RssFeedPage
     * @author Cristina.Axinte
     */
    public RssFeedPage selectRssFeedPage(String username, String password)
    {
        try
        {
            String currentUrl = drone.getCurrentUrl();
            String rssUrlPart = (String) drone.executeJavaScript("return activities.link");
            String protocolVar = PageUtils.getProtocol(currentUrl);
            String address = PageUtils.getAddress(currentUrl);
            String rssUrl = String.format("%s%s:%s@%s%s", protocolVar, username, password, address, rssUrlPart);
            drone.navigateTo(rssUrl);
            return new RssFeedPage(drone).render();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to select RSS Feed option");
    }

    /**
     * Method returns if the specified option is selected in My Activities button
     *
     * @param myActivitiesOption
     * @return
     * @author Cristina.Axinte
     */
    public boolean isOptionSelected(String myActivitiesOption)
    {

        try
        {
            WebElement dropdown = drone.findAndWait(MY_ACTIVITIES_BUTTON);
            String actualOption = dropdown.getText();
            actualOption = actualOption.substring(0, actualOption.length() - 2);
            if (actualOption.equals(myActivitiesOption))
                return true;
            return false;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the 'My activities' button");
        }

    }

    /**
     * Method returns if the specified option is selected in history button
     *
     * @param historyOption
     * @return
     * @author Cristina.Axinte
     */
    public boolean isHistoryOptionSelected(SiteActivitiesHistoryFilter lastDays)
    {

        try
        {
            WebElement dropdown = drone.findAndWait(HISTORY_BUTTON);
            String actualOption = dropdown.getText();
            actualOption = actualOption.substring(0, actualOption.length() - 2);
            if (actualOption.equals(lastDays.getDescription()))
                return true;
            return false;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the 'My activities' button");
        }

    }

}