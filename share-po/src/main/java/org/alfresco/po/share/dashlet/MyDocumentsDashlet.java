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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.sitecontent.DetailedViewInformation;
import org.alfresco.po.share.dashlet.sitecontent.SimpleViewInformation;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * My documents dashlet object, holds all element of the HTML page relating to
 * share's my documents dashlet on dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyDocumentsDashlet extends AbstractDashlet implements Dashlet
{
    private final Log logger = LogFactory.getLog(MyDocumentsDashlet.class);
    private static final String DATA_LIST_CSS_LOCATION = "h3.filename > a";
    private static final String DASHLET_DIV_CONTAINER_PLACEHOLDER = "div.dashlet.my-documents";
    private static final String CONTENT_DASHLET_LIST_OF_FILTER_BUTTONS = "span[class*=' yui-menu-button-active']+div+div+div>div.bd>ul.first-of-type>li";
    private static final String DEFAULT_FILTER_BUTTON = "div.dashlet.my-documents button[id$='default-filters-button']";
    private static final String DASHLET_DETAILED_VIEW_BUTTON = "button[title='Detailed View']";
    private static final String DASHLET_SIMPLE_VIEW_BUTTON = "button[title='Simple View']";
    private static final String NUMBER_OF_DOCS_TABLE = "tbody[class$='dt-data']>tr";
    // Simple View Details Related CSS
    private static final By SIMPLE_THUMBNAIL_VIEW = By.cssSelector("span[class='icon32']>a");
    private static final By SIMPLE_FILENAME = By.cssSelector(".filename.simple-view>a");
    private static final By SIMPLE_ITEM = By.cssSelector(".item-simple");
    private static final By SIMPLE_PREVIEW_IMAGE = By.cssSelector("div[id$='default-previewTooltip']>div.bd>img");
    // Detailed View Details Related CSS
    private static final By DOCUMENT_VERSION = By.cssSelector(".document-version");
    private static final String DETAIL_DESC = "td>div>div:nth-of-type(2)>span[class='item']:nth-of-type(1)";
    private static final By LIKE_COUNT = By.cssSelector(".likes-count");
    private static final By FILE_SIZE = By.cssSelector(".detail>span[class='item']:nth-of-type(2)");
    private static final By USER_LINK = By.cssSelector(".detail>span[class='item']:nth-of-type(1)>a");
    private static final By LIKE_LINK = By.cssSelector("a[class^='like-action']");
    private static final By FAVOURITE_LINK = By.cssSelector("a[class^='favourite-action']");
    private static final By COMMENT_LINK = By.cssSelector(".comment");
    private static final By DETAIL_CONTENT_STATUTS = By.cssSelector("td>div>div:nth-of-type(1)>span[class='item']:nth-of-type(1)");
    private static final By DETAIL_THUMBNAIL_LINK = By.cssSelector(".thumbnail>a");



    /**
     * Constructor.
     */
    protected MyDocumentsDashlet(WebDrone drone)
    {
        super(drone, By.cssSelector(DASHLET_DIV_CONTAINER_PLACEHOLDER));
        setResizeHandle(By.xpath(".//div[contains (@class, 'yui-resize-handle')]"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized MyDocumentsDashlet render(RenderTime timer)
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
                    this.dashlet = drone.findAndWait(By.cssSelector(DASHLET_DIV_CONTAINER_PLACEHOLDER), 100L, 10L);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change
                    // is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find Content I'm Editing dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyDocumentsDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyDocumentsDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * The collection of documents displayed on my documents dashlet.
     * 
     * @return List<ShareLink> links
     */
    public synchronized List<ShareLink> getDocuments()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Selects a document that appears on my documents dashlet by the matching name and
     * clicks on the link.
     */
    public synchronized ShareLink selectDocument(final String title)
    {
        return getLink(DATA_LIST_CSS_LOCATION, title);
    }

    /**
     * Select the given {@link SiteContentFilter} on Site Content Dashlet.
     *
     * @param filter
     *            - The {@link SiteContentFilter} to be selected
     * @return {@link HtmlPage}
     */
    public HtmlPage selectFilter(SiteContentFilter filter)
    {
        clickFilterButton();
        List<WebElement> filterElements = drone.findAll(By.cssSelector(CONTENT_DASHLET_LIST_OF_FILTER_BUTTONS));
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
        waitUntilAlert(1);
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Retrieves the Site content FilterButton based on the given cssSelector
     * and clicks on it.
     */
    public void clickFilterButton()
    {
        try
        {
            drone.findAndWait(By.cssSelector(DEFAULT_FILTER_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Filter Button.", e);
            }
        }
    }

    /**
     * Mimics action of Clicking the Details View Button.
     */
    public void clickDetailView()
    {
        drone.findAndWait(By.cssSelector(DASHLET_DETAILED_VIEW_BUTTON)).click();
    }

    /**
     * Get the List of Detailed View Information. Select Detailed View Button on
     * Site Content Dashlet. Read Content information and return the list.
     *
     * @return {@link List} of {@link org.alfresco.po.share.dashlet.sitecontent.DetailedViewInformation}.
     */
    public List<DetailedViewInformation> getDetailedViewInformation()
    {
        List<DetailedViewInformation> informations = null;
        try
        {
            drone.findAndWait(By.cssSelector(DASHLET_DETAILED_VIEW_BUTTON)).click();
            List<WebElement> links = this.dashlet.findElements(By.cssSelector(NUMBER_OF_DOCS_TABLE));
            if (links == null || links.isEmpty())
            {
                return Collections.emptyList();
            }
            informations = new ArrayList<DetailedViewInformation>();
            for (WebElement tr : links)
            {
                WebElement thumbnailLink = tr.findElement(DETAIL_THUMBNAIL_LINK);
                ShareLink thumbnail = new ShareLink(thumbnailLink, drone);

                WebElement contentLink = tr.findElement(By.cssSelector(".filename>a"));
                ShareLink contentDetail = new ShareLink(contentLink, drone);

                WebElement userLink = tr.findElement(USER_LINK);
                ShareLink user = new ShareLink(userLink, drone);

                String contentStatus = tr.findElement(DETAIL_CONTENT_STATUTS).getText();
                WebElement commentLink = tr.findElement(COMMENT_LINK);
                ShareLink comment = new ShareLink(commentLink, drone);

                WebElement likeLink = tr.findElement(LIKE_LINK);
                ShareLink like = new ShareLink(likeLink, drone);
                boolean likeEnabled = false;

                String likeClass = likeLink.getAttribute("class");
                if (likeClass != null)
                {
                    likeEnabled = likeClass.endsWith("enabled");
                }

                WebElement favouriteLink = tr.findElement(FAVOURITE_LINK);
                ShareLink favourite = new ShareLink(favouriteLink, drone);
                boolean favouriteEnabled = false;

                String favouriteClass = favouriteLink.getAttribute("class");
                if (favouriteClass != null)
                {
                    favouriteEnabled = favouriteClass.endsWith("enabled");
                }

                int likeCount = Integer.parseInt(tr.findElement(LIKE_COUNT).getText());
                String fileSize = tr.findElement(FILE_SIZE).getText();
                String desc = tr.findElement(By.cssSelector(DETAIL_DESC)).getText();

                drone.mouseOverOnElement(thumbnailLink);
                WebElement docVersionElement = tr.findElement(DOCUMENT_VERSION);

                double docVersion = 0;
                if (docVersionElement != null && !docVersionElement.getText().isEmpty())
                {
                    docVersion = Double.parseDouble(docVersionElement.getText());
                }
                DetailedViewInformation detailedView = new DetailedViewInformation(drone, thumbnail, contentDetail, user, contentStatus, comment, like,
                    favourite, likeCount, fileSize, desc, docVersion, favouriteEnabled, likeEnabled);
                informations.add(detailedView);
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access site content dashlet data", nse);
        }
        return informations;
    }

    /**
     * Mimics action of Clicking the Simple View Button.
     */
    public void clickSimpleView()
    {
        drone.findAndWait(By.cssSelector(DASHLET_SIMPLE_VIEW_BUTTON)).click();
    }

    /**
     * Get the List of Simple View Information. Select Simple View Button on
     * Site Content Dashlet. Read Content information and return the list.
     *
     * @return {@link List} of {@link org.alfresco.po.share.dashlet.sitecontent.SimpleViewInformation}.
     */
    public List<SimpleViewInformation> getSimpleViewInformation()
    {
        List<SimpleViewInformation> informations = null;
        try
        {
            drone.findAndWait(By.cssSelector(DASHLET_SIMPLE_VIEW_BUTTON)).click();
            List<WebElement> links = this.dashlet.findElements(By.cssSelector(NUMBER_OF_DOCS_TABLE));
            informations = new ArrayList<SimpleViewInformation>(links.size());
            for (WebElement tr : links)
            {
                WebElement thumbnailLink = tr.findElement(SIMPLE_THUMBNAIL_VIEW);
                ShareLink thumbnail = new ShareLink(thumbnailLink, drone);

                WebElement contentLink = tr.findElement(SIMPLE_FILENAME);
                ShareLink content = new ShareLink(contentLink, drone);

                WebElement siteLink = tr.findElement(By.cssSelector(".item-simple>a"));
                ShareLink site = new ShareLink(siteLink, drone);

                String contentStatus = tr.findElement(SIMPLE_ITEM).getText();

                drone.mouseOver(thumbnailLink);
                WebElement docPreview = drone.findAndWait(SIMPLE_PREVIEW_IMAGE);

                informations.add(new SimpleViewInformation(drone, thumbnail, content, site, contentStatus, docPreview.isDisplayed()));
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error(nse);
            throw new PageException("Unable to display simple view informationin my documents dashlet data");
        }

        return informations;

    }

    /**
     * Retrieves the link based on the given cssSelector.
     *
     * @return boolean
     */
    public boolean isDetailButtonDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector(DASHLET_DETAILED_VIEW_BUTTON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the Detail View Button.", e);
            }
        }
        return false;
    }

    /**
     * Retrieves the link based on the given cssSelector.
     *
     * @return boolean
     */
    public boolean isSimpleButtonDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector(DASHLET_SIMPLE_VIEW_BUTTON)).isDisplayed();

        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the Simple View Button.", e);
            }
        }
        return false;
    }
}
