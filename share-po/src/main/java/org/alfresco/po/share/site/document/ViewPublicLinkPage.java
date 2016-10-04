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
package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
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
    //@RenderWebElement
    private static final By documentPreviewLocator = By.cssSelector("div[id$='web-preview-previewer-div']");
    private static final By pageNotFound = By.cssSelector("div.quickshare-error>h1");
    private static final By bodyNotFound = By.cssSelector("div.quickshare-error>p");

    private static final String THIN_DARK_TITLE_ELEMENT = "h1.quickshare-node-header-info-title.thin.dark";
    private static final String DOCUMENT_BODY = "div[id$='quickshare_x0023_web-preview-viewer-pageContainer-1']";
    
    private static final String DOCUMENT_ZOOMIN = "#page_x002e_components_x002e_quickshare_x0023_web-preview-zoomIn-button";                                                   
    private static final String DOCUMENT_ZOOMOUT = "#page_x002e_components_x002e_quickshare_x0023_web-preview-zoomOut-button" ;
    private static final String DOCUMENT_ZOOMSCALE = "#page_x002e_components_x002e_quickshare_x0023_web-preview-scaleSelectBtn-button" ;

    //@RenderWebElement: Commented out in case the file viewer is not rendered
    private static final By ZOOM = By.cssSelector(DOCUMENT_ZOOMSCALE);

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


    /**
     * Verify that public view page is displayed with document view.
     * 
     * @return boolean
     */
    public boolean isDocumentViewDisplayed()
    {
        try
        {
            return driver.findElement(documentPreviewLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }

    /**
     * Click Document Details present on Share Link page.
     *
     * @return HtmlPage
     */
    public HtmlPage clickOnDocumentDetailsButton()
    {
        try
        {
            WebElement button = driver.findElement(documentDetailsLinkLocator);
            button.click();
            return getCurrentPage();
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
            WebElement element = driver.findElement(By.cssSelector(THIN_DARK_TITLE_ELEMENT));
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
     * <br/><br/>author bogdan.bocancea
     */
    public String getButtonName()
    {
        try
        {
            WebElement element = driver.findElement(documentDetailsLinkLocator);
            return element.getText();
        }
        catch (NoSuchElementException e)
        {
        }
        return "";
    }

    /**
     * Checks if Document Details button is displayed
     *
     * @return boolean
     */
    public boolean isButtonVisible()
    {
        try
        {
            WebElement button = driver.findElement(documentDetailsLinkLocator);
            boolean displayed = button.isDisplayed();

            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("** Document Details button: %s", displayed));
            }
            return displayed;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No live search dropdown ", nse);
        }
        return false;
    }

    /**
     * Gets the error title label
     * 
     * @return String Page not available
     * <br/><br/>author bogdan.bocancea
     */
    public String getPageNotAvailable()
    {

        try
        {
            WebElement element = driver.findElement(pageNotFound);
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
     * <br/><br/>author bogdan.bocancea
     */
    public String getBodyPageNotAvailable()
    {
        try
        {
            WebElement element = driver.findElement(bodyNotFound);
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
     * <br/><br/>author Cristina Axinte
     */
    public String getDocumentBody()
    {
        int counter = 0;
        int retryRefreshCount = 5;
        WebElement element = findAndWait(By.cssSelector(DOCUMENT_BODY));

        while (counter < retryRefreshCount)
        {
            if (!element.getText().isEmpty())
            {
                return element.getText();
            }
            counter++;
            // double wait time to not over do slow search
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
            WebElement link = findAndWait(By.cssSelector(DOCUMENT_ZOOMIN));
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
            WebElement link = findAndWait(By.cssSelector(DOCUMENT_ZOOMOUT));
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
            WebElement zoomScale = findAndWait(By.cssSelector(DOCUMENT_ZOOMSCALE));
            return zoomScale.getText();
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
    
    /**
     * Returns the Img Src from the Alfresco logo
     * 
     * @return String img src
     */
    public String getLogoImgSrc()
    {
        try
        {
            WebElement scr = driver.findElement(alfrescoImageLocator);
            return scr.getAttribute("src");
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find the Alfresco logo");
        }
    }
}
