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
import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.sitecontent.DetailedViewInformation;
import org.alfresco.po.share.dashlet.sitecontent.SimpleViewInformation;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.docsummary")
/**
 * Site content dashlet object, holds all element of the HTML relating to site
 * content dashlet.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteContentDashlet extends AbstractDashlet implements Dashlet
{

    private final Log logger = LogFactory.getLog(SiteContentDashlet.class);
    private static final String DATA_LIST_CSS_LOCATION = "h3.filename>a";
    private static final String SITE_LIST_CSS_LOCATION = "a[class='site-link theme-color-1']";
    private static final String DASHLET_CONTAINER_PLACEHOLDER = "div.dashlet.docsummary";
    private static final String DASHLET_DETAILED_VIEW_BUTTON = "button[title='Detailed View']";
    private static final String DASHLET_SIMPLE_VIEW_BUTTON = "button[title='Simple View']";
    private static final String DASHLET_HELP_BUTTON = "div[class='dashlet docsummary resizable yui-resize']>div.titleBarActions>div.titleBarActionIcon.help";
    private static final String CONTENT_DASHLET_LIST_OF_FILTER_BUTTONS = "span[class*=' yui-menu-button-active']+div+div+div>div.bd>ul.first-of-type>li";
    private static final String DASHLET_HELP_BALLOON_HEADER = "div[style*='visible']>div>div.balloon>div.text>p";
    private static final String DASHLET_HELP_BALLOON_MSG = "div[style*='visible']>div>div.balloon>div.text>ul>li";
    private static final String DASHLET_HELP_BALLOON_CLOSE_BUTTON = "div[style*='visible']>div>div.balloon>div.closeButton";
    private static final String DASHLET_HELP_BALLOON = "div[style*='visible']>div>div.balloon";
    private static final String DEFAULT_FILTER_BUTTON = "button[id$='default-filters-button']";
    private static final String CONTENT_DETAILS = "div.dashlet.docsummary>div>div>table>tbody>tr>td>div>div>span";
    private static final String EMPTY_CONTENT_HEADING = "div.dashlet.docsummary>div>div>table>tbody>tr>td>div>div>h3";
    private static final String NUMBER_OF_DOCS_TABLE = "//tbody[contains(@class,'dt-data')]/tr";
    // Simple View Details Related CSS
    private static final By SIMPLE_THUMBNAIL_VIEW = By.xpath("//span[@class='icon32']/a");
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


    public SiteContentDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * The content of the site that is displayed on site content dashlet.
     * 
     * @return List<ShareLink> site links
     */
    public  List<ShareLink> getSiteContents()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Retrieves the link that match the content name.
     * 
     * @param name
     *            identifier
     * @return {@link ShareLink} that matches members name
     */
    public  ShareLink select(final String name)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        List<ShareLink> shareLinks = getList(DATA_LIST_CSS_LOCATION);
        if (shareLinks != null)
        {
            for (ShareLink link : shareLinks)
            {
                if (name.equalsIgnoreCase(link.getDescription()))
                {
                    return link;
                }
            }
            throw new PageException(String.format("Link %s can not be found on the page, dashlet exists: %s link size: %d", name, dashlet, shareLinks.size()));
        }
        throw new PageException("Link can not be found on the dashlet");
    }
    
    
    /**
     * Retrieves the site link that matches the site name.
     * 
     * @param name
     *            identifier
     * @return {@link ShareLink} that matches members name
     */
    public  ShareLink selectSite(final String name)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        List<ShareLink> shareLinks = getList(SITE_LIST_CSS_LOCATION);
        if (shareLinks != null)
        {
            for (ShareLink link : shareLinks)
            {
                if (name.equalsIgnoreCase(link.getDescription()))
                {
                    return link;
                }
            }
            throw new PageException(String.format("Link %s can not be found on the page, dashlet exists: %s link size: %d", name, dashlet, shareLinks.size()));
        }
        throw new PageException("Link can not be found on the dashlet");
    }
    
    
    @SuppressWarnings("unchecked")
    public  SiteContentDashlet render(RenderTime timer)
    {
        setResizeHandle(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
        dashlet = driver.findElement(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER));
        elementRender(timer, getVisibleRenderElement(By.cssSelector(DASHLET_CONTAINER_PLACEHOLDER)),
                getVisibleRenderElement(By.cssSelector(DEFAULT_FILTER_BUTTON)));
        return this;
    }

    public  SiteContentDashlet renderSimpleViewWithContent()
    {
        return renderSimpleViewWithContent(new RenderTime(maxPageLoadingTime));
    }

    public  SiteContentDashlet renderSimpleViewWithContent(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(SIMPLE_FILENAME), getVisibleRenderElement(SIMPLE_ITEM), getVisibleRenderElement(SIMPLE_THUMBNAIL_VIEW));
        return this;
    }

    public  SiteContentDashlet renderDetailViewWithContent()
    {
        return renderDetailViewWithContent(new RenderTime(maxPageLoadingTime));
    }

    public  SiteContentDashlet renderDetailViewWithContent(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DETAIL_THUMBNAIL_LINK), getVisibleRenderElement(DETAIL_CONTENT_STATUTS),
                getVisibleRenderElement(COMMENT_LINK), getVisibleRenderElement(FAVOURITE_LINK), getVisibleRenderElement(LIKE_LINK),
                getVisibleRenderElement(LIKE_COUNT), getVisibleRenderElement(USER_LINK), getVisibleRenderElement(FILE_SIZE));
        return this;
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
            return driver.findElement(By.cssSelector(DASHLET_DETAILED_VIEW_BUTTON)).isDisplayed();
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
            return driver.findElement(By.cssSelector(DASHLET_SIMPLE_VIEW_BUTTON)).isDisplayed();

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

    /**
     * Retrieves the link based on the given cssSelector.
     * 
     */
    public void selectHelpButton()
    {
        try
        {
            driver.findElement(By.cssSelector(DASHLET_HELP_BUTTON)).click();
        }
        catch (NoSuchElementException exception)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the Help Button.");
            }
            throw new NoSuchElementException("Not able to find the Help Button.", exception);
        }
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
            return driver.findElement(By.cssSelector(DASHLET_HELP_BUTTON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Not able to find the Help Button.", e);
            }
        }
        return false;
    }

    public void clickHelpButton()
    {

        driver.findElement(By.cssSelector(DASHLET_HELP_BUTTON)).click();

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
     * This method gets the list of messages based on given cssselector and
     * appends it to a string.
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
     * Retrieves the closeButton for Help balloon window based on the given
     * cssSelector and clicks on it.
     */
    public SiteContentDashlet closeHelpBallon()
    {
        driver.findElement(By.cssSelector(DASHLET_HELP_BALLOON_CLOSE_BUTTON)).click();
        waitUntilElementDisappears(By.cssSelector(DASHLET_HELP_BALLOON_CLOSE_BUTTON), 1);
        return this;
    }

    /**
     * Retrieves the Site content FilterButton based on the given cssSelector
     * and clicks on it.
     */
    public void clickFilterButtton()
    {
        try
        {
            findAndWait(By.cssSelector(DEFAULT_FILTER_BUTTON)).click();
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
     * Select the given {@link SiteContentFilter} on Site Content Dashlet.
     * 
     * @param filter
     *            - The {@link SiteContentFilter} to be selected
     * @return {@link SiteDashboardPage}
     */
    public HtmlPage selectFilter(SiteContentFilter filter)
    {
        clickFilterButtton();
        List<WebElement> filterElements = driver.findElements(By.cssSelector(CONTENT_DASHLET_LIST_OF_FILTER_BUTTONS));
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
     * Get the Filter applied on site content dashlet.
     * 
     * @return {@link SiteContentFilter}
     */
    public SiteContentFilter getCurrentFilter()
    {
        try
        {
            return SiteContentFilter.getFilter(driver.findElement(By.cssSelector(DEFAULT_FILTER_BUTTON)).getText());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to locate filter elements from the dropdown", e);
        }
    }

    /**
     * Heading text from site content dashlet.
     * 
     * @return Heading Text.
     */
    public String getEmptyContentHeading()
    {
        try
        {
            return driver.findElement(By.cssSelector(EMPTY_CONTENT_HEADING)).getText();
        }
        catch (NoSuchElementException elementException)
        {
            logger.error("Not able to find the empty heading on Site Content Dashlet", elementException);
        }
        return "";
    }

    /**
     * To get the Empty Site Content Dashlet Details.
     * 
     * @return {@link List} String - Content Details.
     */
    public List<String> getContentsDetails()
    {
        List<String> details = null;
        List<WebElement> contentsDetails = findAndWaitForElements(By.cssSelector(CONTENT_DETAILS));
        if (contentsDetails != null && !contentsDetails.isEmpty())
        {
            details = new ArrayList<String>();
            for (WebElement webElement : contentsDetails)
            {
                details.add(webElement.getText());
            }
        }
        return details;
    }

    /**
     * Mimics action of Clicking the Simple View Button.
     */
    public void clickSimpleView()
    {
        findAndWait(By.cssSelector(DASHLET_SIMPLE_VIEW_BUTTON)).click();
    }

    /**
     * Get the List of Simple View Information. Select Simple View Button on
     * Site Content Dashlet. Read Content information and return the list.
     * 
     * @return {@link List} of {@link SimpleViewInformation}.
     */
    public List<SimpleViewInformation> getSimpleViewInformation()
    {
        List<SimpleViewInformation> informations = null;
        try
        {
            findAndWait(By.cssSelector(DASHLET_SIMPLE_VIEW_BUTTON)).click();
            List<WebElement> links = findAndWaitForElements(By.xpath(NUMBER_OF_DOCS_TABLE));
            informations = new ArrayList<SimpleViewInformation>(links.size());
            for (WebElement tr : links)
            {
                WebElement thumbnailLink = tr.findElement(SIMPLE_THUMBNAIL_VIEW);
                ShareLink thumbnail = new ShareLink(thumbnailLink, driver, factoryPage);

                WebElement contentLink = tr.findElement(SIMPLE_FILENAME);
                ShareLink content = new ShareLink(contentLink, driver, factoryPage);

                WebElement userLink = tr.findElement(By.cssSelector(".item-simple" + ">a"));
                ShareLink user = new ShareLink(userLink, driver, factoryPage);

                String contentStatus = tr.findElement(SIMPLE_ITEM).getText();

                mouseOver(thumbnailLink);
                WebElement docPreview = findAndWait(SIMPLE_PREVIEW_IMAGE);

                informations.add(new SimpleViewInformation(driver, thumbnail, content, user, contentStatus, docPreview.isDisplayed(), factoryPage));
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error(nse);
            throw new PageException("Unable to display simple view informationin site content dashlet data");
        }

        return informations;

    }

    /**
     * Mimics action of Clicking the Details View Button.
     */
    public void clickDetailView()
    {
        findAndWait(By.cssSelector(DASHLET_DETAILED_VIEW_BUTTON)).click();
    }

    /**
     * Get the List of Detailed View Information. Select Detailed View Button on
     * Site Content Dashlet. Read Content information and return the list.
     * 
     * @return {@link List} of {@link DetailedViewInformation}.
     */
    public List<DetailedViewInformation> getDetailedViewInformation()
    {
        List<DetailedViewInformation> informations = null;
        try
        {
            findAndWait(By.cssSelector(DASHLET_DETAILED_VIEW_BUTTON)).click();
            // findAndWait(By.cssSelector(NUMBER_OF_DOCS_TABLE + ">" + DETAIL_DESC));
            List<WebElement> links = findAndWaitForElements(By.xpath(NUMBER_OF_DOCS_TABLE));
            if (links == null || links.isEmpty())
            {
                return Collections.emptyList();
            }
            informations = new ArrayList<DetailedViewInformation>();
            for (WebElement tr : links)
            {
                WebElement thumbnailLink = tr.findElement(DETAIL_THUMBNAIL_LINK);
                ShareLink thumbnail = new ShareLink(thumbnailLink, driver, factoryPage);

                WebElement contentLink = tr.findElement(By.cssSelector(".filename>a"));
                ShareLink contentDetail = new ShareLink(contentLink, driver, factoryPage);

                WebElement userLink = tr.findElement(USER_LINK);
                ShareLink user = new ShareLink(userLink, driver, factoryPage);

                String contentStatus = tr.findElement(DETAIL_CONTENT_STATUTS).getText();
                WebElement commentLink = tr.findElement(COMMENT_LINK);
                ShareLink comment = new ShareLink(commentLink, driver, factoryPage);

                WebElement likeLink = tr.findElement(LIKE_LINK);
                ShareLink like = new ShareLink(likeLink, driver, factoryPage);
                boolean likeEnabled = false;

                String likeClass = likeLink.getAttribute("class");
                if (likeClass != null)
                {
                    likeEnabled = likeClass.endsWith("enabled");
                }

                WebElement favouriteLink = tr.findElement(FAVOURITE_LINK);
                ShareLink favourite = new ShareLink(favouriteLink, driver, factoryPage);
                boolean favouriteEnabled = false;

                String favouriteClass = favouriteLink.getAttribute("class");
                if (favouriteClass != null)
                {
                    favouriteEnabled = favouriteClass.endsWith("enabled");
                }

                int likeCount = Integer.parseInt(tr.findElement(LIKE_COUNT).getText());
                String fileSize = tr.findElement(FILE_SIZE).getText();
                String desc = tr.findElement(By.cssSelector(DETAIL_DESC)).getText();

                mouseOver(thumbnailLink);
                WebElement docVersionElement = tr.findElement(DOCUMENT_VERSION);

                double docVersion = 0;
                if (docVersionElement != null && !docVersionElement.getText().isEmpty())
                {
                    docVersion = Double.parseDouble(docVersionElement.getText());
                }
                DetailedViewInformation detailedView = new DetailedViewInformation(driver, thumbnail, contentDetail, user, contentStatus, comment, like,
                        favourite, likeCount, fileSize, desc, docVersion, favouriteEnabled, likeEnabled, factoryPage);
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
     * The filters of the Site content text those are displayed in filters dropdown.
     * 
     * @return <List<String>> site links
     */
    public List<SiteContentFilter> getFilters()
    {
        List<SiteContentFilter> list = new ArrayList<SiteContentFilter>();
        try
        {
            for (WebElement element : driver.findElements(By.cssSelector(CONTENT_DASHLET_LIST_OF_FILTER_BUTTONS)))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(SiteContentFilter.getFilter(text.trim()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to access site content dashlet filters data", nse);
        }

        return list;
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteContentDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
