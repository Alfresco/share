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
package org.alfresco.po.share.search;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.util.*;

/**
 * Holds the information of a search result item.
 * When completing a search the resulting page yields results
 * which each individual row is represented by SearchResultItem class.
 * 
 * @author Michael Suzuki
 * @since 1.3
 */
public class SearchResultItem implements SearchResult
{
    private WebDrone drone;    
    private static final String ITEM_NAME_CSS_HOLDER = "div.nameAndTitleCell span.alfresco-renderers-Property:first-of-type span.inner a";
    private static final String TITLE = "span.value";
    private static final String DOWNLOAD_LINK = "div>a>img[title='Download']";
    private static final String THUMBNAIL_LINK = "div.thumbnail-cell";    
    private static final String THUMBNAIL_IMG = " span a img";
    private static final String VIEW_IN_BROWSER_LINK = "a>img[title='View In Browser']";
    private static final String FOLDER_CSS = "img[src$='folder.png']";
    private static final String FOLDER_PATH_CSS = "a";
    private static final String CONTENT_DETAILS_CSS = "div.details";
    private static final String DATE = "div.dateCell span.inner";
    private static final String SITE = "div.siteCell span.inner";
    private static final String IMAGE = "tbody[id=FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS] td.thumbnailCell img";

    private WebElement webElement;
    private String title;
    private String thumbnail;

    /**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param drone
     */
    public SearchResultItem(WebElement element, WebDrone drone)
    {
        webElement = element;
        this.drone = drone;
    }

    /**
     * Title of search result item.
     * 
     * @return String title
     */
    public String getTitle()
    {
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (title == null)
        {
            try
            {
                if (version.isFacetedSearch())
                {
                    title = webElement.findElement(By.cssSelector(ITEM_NAME_CSS_HOLDER + ">" + TITLE)).getText();
                }
                else
                {
                    title = webElement.findElement(By.cssSelector("h3.itemname a")).getText();
                }

            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find item title", e);
            }
        }
        return title;
    }

    /**
     * Method to Click on Download link of result item present on search results.
     * Note : This thumbnail css used in this method works only on chrome browser.
     */
    public void clickOnDownloadIcon()
    {
        WebElement download;
        RenderTime timer = new RenderTime(10000);
        try
        {
            WebElement thumbnail = webElement.findElement(By.cssSelector(THUMBNAIL_LINK));
            drone.mouseOverOnElement(thumbnail);

            while (true)
            {
                download = webElement.findElement(By.cssSelector(DOWNLOAD_LINK));

                try
                {
                    timer.start();

                    if (download.isDisplayed())
                    {
                        download.click();
                        break;
                    }
                }
                catch (ElementNotVisibleException e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (ElementNotVisibleException e)
        {
            throw new PageException("Download link is not visible on Search Results Item", e);
        }
    }

    /**
     * Method to Click on viewInBrowser link of result item present on search results.
     * Note : This thumbnail css used in this method works only on chrome browser.
     * 
     * @return String, url of the current window.
     */
    public String clickOnViewInBrowserIcon()
    {
        WebElement viewInBrowser;
        String mainWindow;
        Set<String> windows;
        String newTab;
        String url;
        RenderTime timer = new RenderTime(10000);

        try
        {
            WebElement thumbnail = webElement.findElement(By.cssSelector(THUMBNAIL_LINK));
            drone.mouseOverOnElement(thumbnail);

            while (true)
            {
                viewInBrowser = webElement.findElement(By.cssSelector(VIEW_IN_BROWSER_LINK));

                try
                {
                    timer.start();

                    if (viewInBrowser.isDisplayed())
                    {
                        mainWindow = drone.getWindowHandle();
                        viewInBrowser.click();
                        windows = drone.getWindowHandles();
                        windows.remove(mainWindow);
                        newTab = windows.iterator().next();
                        drone.switchToWindow(newTab);
                        url = drone.getCurrentUrl();
                        drone.closeWindow();
                        drone.switchToWindow(mainWindow);

                        return url;
                    }
                }
                catch (NoSuchWindowException ne)
                {
                }
                catch (ElementNotVisibleException e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (ElementNotVisibleException e)
        {
            throw new ElementNotVisibleException("View in browser link is not visible in Search Results Item", e);
        }

    }

    /**
     * This method finds whether the selected item is folder or not, if yes returns true otherwise false.
     * 
     * @return boolean
     */
    public boolean isFolder()
    {
        try
        {
            return webElement.findElement(By.cssSelector(FOLDER_CSS)).isDisplayed();
        }
        catch (Exception e)
        {
        }

        return false;
    }

    /**
     * This method finds the selected item's folderpath and returns the main
     * folder and sub folder names as list of string. value. Search Result Item
     * parent folder will be available as the first element in the returned
     * list.
     * 
     * @return List<String>
     */
    public List<String> getFolderNamesFromContentPath()
    {
        try
        {
            List<WebElement> details = webElement.findElements(By.cssSelector(CONTENT_DETAILS_CSS));
            for (WebElement detail : details)
            {
                if (detail.getText().contains("In folder"))
                {
                    String folderPath = detail.findElement(By.cssSelector(FOLDER_PATH_CSS)).getText();
                    StringTokenizer tokens = new StringTokenizer(folderPath, "/");
                    List<String> tokensList = new LinkedList<String>();
                    while (tokens.hasMoreElements())
                    {
                        tokensList.add(tokens.nextElement().toString());
                    }
                    return tokensList;
                }
            }
        }
        catch (NoSuchElementException ne)
        {
        }
        return Collections.emptyList();
    }

    @Override
    public HtmlPage clickLink()
    {
        WebElement link = webElement.findElement(By.cssSelector(ITEM_NAME_CSS_HOLDER));
        link.click();
        return FactorySharePage.resolvePage(drone);
    }

    @Override
    /**
     * The name and title are treated the same up till version 5.0
     * @return String title
     */
    public String getName()
    {
        return getTitle();
    }    

    @Override
    /**
     * The site are treated the same up till version 5.0
     * @return String Site
     */
    public String getSite()
    {
        return getSite();
    }
    
    @Override
    /**
     * The date are treated the same up till version 5.0
     * @return String Date
     */
    public String getDate()
    {
        return getDate();
    }
    
    @Override
    /**
     * The ActionsSet are treated the same up till version 5.0
     * @return ActionsSet
     */
    public ActionsSet getActions()
    {
        return getActions();
    }
    
    @Override
    public HtmlPage clickDateLink()
    {
        WebElement link = webElement.findElement(By.cssSelector(DATE));
        link.click();
        return FactorySharePage.resolvePage(drone);
    }
    
    @Override
    public HtmlPage clickSiteLink()
    {
        WebElement link = webElement.findElement(By.cssSelector(SITE));
        link.click();
        return FactorySharePage.resolvePage(drone);
    }
    
    @Override
    public PreViewPopUpPage clickImageLink()
    {
        WebElement link = webElement.findElement(By.cssSelector(IMAGE));
        link.click();
        return new PreViewPopUpPage(drone);
    }

    @Override
    public PreViewPopUpImagePage clickImageLinkToPicture()
    {
        WebElement link = webElement.findElement(By.cssSelector(IMAGE));
        link.click();
        return new PreViewPopUpImagePage(drone);
    }

    /**
     * Method to click on content path in the details section
     *
     * @return SharePage
     */
    public SharePage clickContentPath()
    {
        List<WebElement> details = webElement.findElements(By.cssSelector(CONTENT_DETAILS_CSS + " a"));
        for (WebElement detail : details)
        {
            if (detail.getAttribute("href").contains("path"))
            {
                detail.click();
            }
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to click Site name in the details section
     *
     * @return SitePage
     */
    public SitePage clickSiteName()
    {
        List<WebElement> details = webElement.findElements(By.cssSelector(CONTENT_DETAILS_CSS + " a"));
        for (WebElement detail : details)
        {
            if (detail.getAttribute("href").contains("site"))
            {
                detail.click();
                break;
            }
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to get thumbnail url
     *
     * @return String
     */
    public String getThumbnailUrl()
    {
        try
        {
            String urlDocLib = webElement.findElement(By.cssSelector(THUMBNAIL_LINK + THUMBNAIL_IMG)).getAttribute("src");
            String urlImgPreview = urlDocLib.replace("doclib", "imgpreview");
            return urlImgPreview;
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find the thumbnail");
        }
    }

    /**
     * Method to get preview url
     *
     * @return String
     */
    public String getPreViewUrl()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(THUMBNAIL_LINK + THUMBNAIL_IMG)).getAttribute("src");
        }
        catch (TimeoutException ex)
        {
            throw new ShareException("Exceeded time to find the preView img", ex);
        }
    }

    public String getThumbnail()
    {
        return thumbnail;
    }
	
}