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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The class represents the Public view Link page and displays the view details of document.
 * 
 * @author cbairaajoni
 */
public class ViewPublicLinkPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ViewPublicLinkPage.class);
    @RenderWebElement
    private static final By alfrescoImageLocator = By.cssSelector(".quickshare-header-left>img");
    private static final By documentDetailsLinkLocator = By.cssSelector("div.quickshare-header-right>a.brand-button");
    // @RenderWebElement
    // private static final By documentNameLocator = By.cssSelector("div.quickshare-node-header h1");
    @RenderWebElement
    private static final By documentPreviewLocator = By.cssSelector("div[id$='web-preview-previewer-div']");
    private static final By pageNotFound = By.cssSelector("div.quickshare-error>h1");
    private static final By bodyNotFound = By.cssSelector("div.quickshare-error>p");

    private static final String THIN_DARK_TITLE_ELEMENT = "h1.quickshare-node-header-info-title.thin.dark";
    private static final String DOCUMENT_BODY = "div[id$='quickshare_x0023_web-preview-viewer-pageContainer-1']";
    
    private static final String DOCUMENT_ZOOMIN = "#page_x002e_components_x002e_quickshare_x0023_web-preview-zoomIn-button";                                                   
    private static final String DOCUMENT_ZOOMOUT = "#page_x002e_components_x002e_quickshare_x0023_web-preview-zoomOut-button" ;
    private static final String DOCUMENT_ZOOMSCALE = "#page_x002e_components_x002e_quickshare_x0023_web-preview-scaleSelectBtn-button" ;

    @RenderWebElement
    private static final By TEXT_LAYER = By.cssSelector("div[id$='web-preview-viewer-pageContainer-1']>.textLayer");
        
    /**
     * Constructor.
     */
    public ViewPublicLinkPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPublicLinkPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPublicLinkPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPublicLinkPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify that public view page is displayed with document view.
     * 
     * @return boolean
     */
    public boolean isDocumentViewDisplayed()
    {
        try
        {
            return drone.find(documentPreviewLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }

    /**
     * Click View Link present on Share Link page.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickOnDocumentDetailsButton()
    {
        try
        {
            drone.find(documentDetailsLinkLocator).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find documentDetailsLinkLocator css", ex);
        }

        throw new PageException("Unable to find documentDetailsLinkLocator css");
    }

    /**
     * Gets the page detail title.
     * 
     * @return String page detail page title
     */
    public String getContentTitle()
    {
        try
        {
            WebElement element = drone.find(By.cssSelector(THIN_DARK_TITLE_ELEMENT));
            return element.getText();
        }
        catch (NoSuchElementException e)
        {
        }
        return "";

    }

    /**
     * Gets the button label
     * 
     * @return String button label
     * @author bogdan.bocancea
     */
    public String getButtonName()
    {
        try
        {
            WebElement element = drone.find(documentDetailsLinkLocator);
            return element.getText();
        }
        catch (NoSuchElementException e)
        {
        }
        return "";
    }

    /**
     * Gets the error title label
     * 
     * @return String Page not available
     * @author bogdan.bocancea
     */
    public String getPageNotAvailable()
    {

        try
        {
            WebElement element = drone.find(pageNotFound);
            return element.getText();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find error title css", ex);
        }
        return "";
    }

    /**
     * Gets the error body label
     * 
     * @return String Page not available
     * @author bogdan.bocancea
     */
    public String getBodyPageNotAvailable()
    {
        try
        {
            WebElement element = drone.find(bodyNotFound);
            return element.getText();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find body error css", ex);
        }
        return "";
    }

    /**
     * Gets the content body.
     * 
     * @return String content body
     * @author Cristina Axinte
     */
    public String getDocumentBody()
    {
        int counter = 0;
        int waitInMilliSeconds = 2000;
        int retryRefreshCount = 5;
        WebElement element = drone.findAndWait(By.cssSelector(DOCUMENT_BODY));

        while (counter < retryRefreshCount)
        {
            if (!element.getText().isEmpty())
            {
                return element.getText();
            }
            counter++;
            // double wait time to not over do slow search
            waitInMilliSeconds = (waitInMilliSeconds * 2);
            synchronized (ShareUtil.class)
            {
                try
                {
                    ShareUtil.class.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        throw new PageException("Content search failed");
    }
    
    /**
     * Click the zoom in button
     *
     */
    public void clickZoomIn()
    {
        try
        {
            WebElement link = drone.findAndWait(By.cssSelector(DOCUMENT_ZOOMIN));
            link.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" , exception);
        }
    }
   
    /**
     * Click the zoom out button
     *
     */
    public void clickZoomOut()
    {
        try
        {
            WebElement link = drone.findAndWait(By.cssSelector(DOCUMENT_ZOOMOUT));
            link.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element" , exception);
        }
    }

    /**
     * Get the Zoom Scale
     *
     * @return String zoom scale
     */
    public String getZoomScale()
    {
        try
        {
            WebElement element = drone.findAndWait(By.cssSelector(DOCUMENT_ZOOMSCALE));
            return element.getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find element: " , e);
        }
        return "" ;
    }

    /**
     * Returns the Zoom Scale Percentage as Integer.
     * 
     * @return Zoom Scale in Integer.
     */
    public int getIntegerZoomScale()
    {
        String zoomScale = this.getZoomScale();
        if(zoomScale != null && !zoomScale.isEmpty())
        {
            return Integer.parseInt(zoomScale.trim().replaceAll("[^0-9]", ""));
        }
        throw new PageOperationException("Not able to find the zoom scale percentage.");
    }
    

}