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
import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.activities")
/**
 * Site activities dashlet object, holds all element of the HTML relating to dashlet site activity.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteActivitiesDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.activities");
    private static final By RSS_FEED_BUTTON = By.cssSelector(".titleBarActionIcon.rss");

    private static final String DEFAULT_USER_BUTTON = "button[id$='_default-user-button']";
    private static final String DEFAULT_TYPE_BUTTON = "button[id$='_default-activities-button']";
    private static final String DEFAULT_HISTORY_BUTTON = "button[id$='_default-range-button']";
    private static final By DASHLET_LIST_OF_FILTER = By.cssSelector("ul.first-of-type>li>a");
    private static final By MY_ACTIVITIES_MORE_LINK = By
            .xpath("//div[@class='activity']/following::div[@class='hidden']/preceding-sibling::div[@class='more']/a");

    private List<ShareLink> userLinks;
    private List<ShareLink> documentLinks;
    private List<String> groupNames;
    private List<String> activityDescriptions;

    private Log logger = LogFactory.getLog(SiteActivitiesDashlet.class);

//    /**
//     * Constructor.
//     */
//    protected SiteActivitiesDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector(".yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public SiteActivitiesDashlet render(RenderTime timer)
    {
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
        elementRender(timer,
                getVisibleRenderElement(DASHLET_CONTAINER_PLACEHOLDER),
                getVisibleRenderElement(DASHLET_TITLE));
        dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
        return this;
    }

    /**
     * Populates all the possible links that appear on the dashlet
     * data view, the links are of user, document or site.
     */
    private  void populateData()
    {
        userLinks = new ArrayList<ShareLink>();
        documentLinks = new ArrayList<ShareLink>();
        groupNames = new ArrayList<String>();
        activityDescriptions = new ArrayList<String>();
        try
        {
            List<WebElement> linksMore = driver.findElements(MY_ACTIVITIES_MORE_LINK);
            if (!linksMore.isEmpty())
            {
                for (WebElement link : linksMore)
                {
                    link.click();
                }
            }

            List<WebElement> links = driver.findElements(By.cssSelector("div[id$='default-activityList'] > div.activity div:last-child[class$='content']"));
            for (WebElement div : links)
            {
                try
                {
                	WebElement userLink = div.findElement(By.cssSelector("a:nth-of-type(1)"));
                	userLinks.add(new ShareLink(userLink, driver, factoryPage));
                }
                catch(NoSuchElementException nse)
                {
                	// No User Links
                }
                
                try
                {
                	WebElement documentLink = div.findElement(By.cssSelector("a:nth-of-type(2)"));
                	documentLinks.add(new ShareLink(documentLink, driver, factoryPage));
				} 
                catch (NoSuchElementException nse) 
                {
					// No Content Links
				}
                
                try
                { 
                	WebElement groupLink = div.findElement(By.cssSelector("span.detail.em"));
                	groupNames.add(groupLink.getText());
                }
                catch(NoSuchElementException nse)
                {
                	// No Group Activities
                }
                
                WebElement desc = div.findElement(By.cssSelector("span.detail"));
                activityDescriptions.add(desc.getText());
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access dashlet data", nse);
        }
        catch (StaleElementReferenceException e)
        {
            populateData();
        }
    }

    /**
     * Selects the document link on the activity that appears on my activities dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     * @return {@link ShareLink} target link
     */
    public ShareLink selectActivityDocument(final String name)
    {
        return selectLink(name, LinkType.Document);
    }

    /**
     * Selects the user link on an activity that appears on my activities dashlet
     * by matching the name to the link.
     *
     * @param name identifier
     * @return {@link ShareLink} target link
     */
    public ShareLink selectActivityUser(final String name)
    {
        return selectLink(name, LinkType.User);
    }

    /**
     * Find the match and selects on the link.
     *
     * @param name identifier to match against link title
     * @param type that determines document, site or user type link
     */
    public  ShareLink selectLink(final String name, LinkType type)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        if (userLinks == null || documentLinks == null)
        {
            populateData();
        }
        switch (type)
        {
            case Document:
                return extractLink(name, documentLinks);
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
     * Get Activities based on the link type.
     *
     * @param linktype Document, User or Site
     * @return {@link ShareLink} collection
     */
    public  List<ShareLink> getSiteActivities(LinkType linktype)
    {
        if (linktype == null)
        {
            throw new UnsupportedOperationException("LinkType is required");
        }
        populateData();
        switch (linktype)
        {
            case Document:
                return documentLinks;
            case User:
                return userLinks;
            default:
                throw new IllegalArgumentException("Invalid link type specified");
        }
    }

    /**
     * Get Activities descriptions.
     *
     * @return List<String> collection
     */
    public  List<String> getSiteActivityDescriptions()
    {

        if (activityDescriptions == null)
        {
            populateData();
        }

        return activityDescriptions;
    }

    /**
     * Method to verify whether RSS Feed is available
     *
     * @return boolean
     */
    public boolean isRssBtnDisplayed()
    {
        try
        {
            WebElement theTitleOfDashlet = findAndWait(By.xpath(".//div[contains(@class,'activities')]/div[@class='title']"));
            mouseOver(theTitleOfDashlet);
            return dashlet.findElement(RSS_FEED_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method for navigate to RSS from site activity dashlet.
     *
     * @param username String
     * @param password String
     */
    public RssFeedPage selectRssFeed(String username, String password)
    {
        try
        {
            String currentUrl = driver.getCurrentUrl();
            String rssUrlPart = (String) executeJavaScript("return activities.link");
            String protocolVar = PageUtils.getProtocol(currentUrl);
            String address = PageUtils.getAddress(currentUrl);
            String rssUrl = String.format("%s%s:%s@%s%s", protocolVar, username, password, address, rssUrlPart);
            driver.navigate().to(rssUrl);
            return factoryPage.instantiatePage(driver,RssFeedPage.class).render();
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
     * Retrieves the Site Activities User Filter button based on the given cssSelector and clicks on it.
     */
    public void clickUserButton()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            findAndWait(By.cssSelector(DEFAULT_USER_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Topic User Filter Button.", e);
            }
        }
    }

    /**
     * Retrieves the Site Activities Type Filter button based on the given cssSelector and clicks on it.
     */
    public void clickTypeButton()
    {
        try
        {
            scrollDownToDashlet();
            getFocus();
            findAndWait(By.cssSelector(DEFAULT_TYPE_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Topic Type Filter Button.", e);
            }
        }
    }

    /**
     * Retrieves the Site Activities HistoryFilter button based on the given cssSelector and clicks on it.
     */
    public void clickHistoryButton()
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
     * Site Activities user filters displayed in the dropdown.
     *
     * @return <List<String>> topic filter links
     */
    public List<SiteActivitiesUserFilter> getUserFilters()
    {
        List<SiteActivitiesUserFilter> list = new ArrayList<SiteActivitiesUserFilter>();
        try
        {
            for (WebElement element : findDisplayedElements(DASHLET_LIST_OF_FILTER))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(SiteActivitiesUserFilter.getFilter(text.trim()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to access My Discussions dashlet user filters data", nse);
        }

        return list;
    }

    /**
     * Site Activities type filters displayed in the dropdown.
     *
     * @return <List<String>> topic filter links
     */
    public List<SiteActivitiesTypeFilter> getTypeFilters()
    {
        List<SiteActivitiesTypeFilter> list = new ArrayList<SiteActivitiesTypeFilter>();
        try
        {
            for (WebElement element : findDisplayedElements(DASHLET_LIST_OF_FILTER))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(SiteActivitiesTypeFilter.getFilter(text.trim()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to access My Discussions dashlet type filters data", nse);
        }

        return list;
    }

    /**
     * Site Activities history filters displayed in the dropdown.
     *
     * @return <List<String>> topic filter links
     */
    public List<SiteActivitiesHistoryFilter> getHistoryFilters()
    {
        List<SiteActivitiesHistoryFilter> list = new ArrayList<SiteActivitiesHistoryFilter>();
        try
        {
            for (WebElement element : findDisplayedElements(DASHLET_LIST_OF_FILTER))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(SiteActivitiesHistoryFilter.getFilter(text.trim()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to access My Discussions dashlet history filters data", nse);
        }

        return list;
    }

    /**
     * Select the given {@link SiteActivitiesUserFilter} on Site Avtivities Dashlet.
     *
     * @param users - The {@link SiteActivitiesUserFilter} to be selected
     * @return {@link org.alfresco.po.HtmlPage}
     */
    public HtmlPage selectUserFilter(SiteActivitiesUserFilter users)
    {
        clickUserButton();
        List<WebElement> filterElements = findDisplayedElements(DASHLET_LIST_OF_FILTER);
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(users.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Select the given {@link SiteActivitiesTypeFilter} on Site Avtivities Dashlet.
     *
     * @param type - The {@link SiteActivitiesTypeFilter} to be selected
     * @return {@link org.alfresco.po.HtmlPage}
     */
    public HtmlPage selectTypeFilter(SiteActivitiesTypeFilter type)
    {
        clickTypeButton();
        List<WebElement> filterElements = findDisplayedElements(DASHLET_LIST_OF_FILTER);
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(type.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Select the given {@link SiteActivitiesHistoryFilter} on Site Avtivities Dashlet.
     *
     * @param lastXDays - The {@link SiteActivitiesHistoryFilter} to be selected
     * @return {@link org.alfresco.po.HtmlPage}
     */
    public HtmlPage selectHistoryFilter(SiteActivitiesHistoryFilter lastXDays)
    {
        clickHistoryButton();
        List<WebElement> filterElements = findDisplayedElements(DASHLET_LIST_OF_FILTER);
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(lastXDays.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Get the default Site Activities user Filter on Site Activities dashlet.
     *
     * @return {@link SiteActivitiesUserFilter}
     */
    public SiteActivitiesUserFilter getCurrentUserFilter()
    {
        try
        {
            return SiteActivitiesUserFilter.getFilter(driver.findElement(By.cssSelector(DEFAULT_USER_BUTTON)).getText());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate activity history filter from the dropdown", e);
        }
    }

    /**
     * Get the default Site Activities type Filter on Site Activities dashlet.
     *
     * @return {@link SiteActivitiesTypeFilter}
     */
    public SiteActivitiesTypeFilter getCurrentTypeFilter()
    {
        try
        {
            return SiteActivitiesTypeFilter.getFilter(driver.findElement(By.cssSelector(DEFAULT_TYPE_BUTTON)).getText());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate activity history filter from the dropdown", e);
        }
    }

    /**
     * Get the default Site Activities history Filter on Site Activities dashlet.
     *
     * @return {@link SiteActivitiesHistoryFilter}
     */
    public SiteActivitiesHistoryFilter getCurrentHistoryFilter()
    {
        try
        {
            return SiteActivitiesHistoryFilter.getFilter(driver.findElement(By.cssSelector(DEFAULT_HISTORY_BUTTON)).getText());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate activity history filter from the dropdown", e);
        }
    }

    /**
     * This method gets the focus by placing mouse over on Site Activities Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteActivitiesDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
