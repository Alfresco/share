/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */

package org.alfresco.po.share.search;


import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.HtmlPage;
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
 * Holds details of the document search result in the live search dropdown 
 * @author jcule
 *
 */
public class LiveSearchDocumentResult
{
    private static Log logger = LogFactory.getLog(LiveSearchDocumentResult.class);
    
    private static final String DOCUMENT_RESULT_TITLE = "a[href*='document-details']";
    private static final String DOCUMENT_RESULT_SITE_NAME = "span a[href*='documentlibrary']";
    private static final String DOCUMENT_RESULT_USER_NAME = "span a[href*='profile']";
    
    private WebDrone drone ;
    private WebElement webElement;
    private ShareLink title;
    private ShareLink siteName;
    private ShareLink userName;
    
    
    /**
     * Constructor
     * @param element {@link WebElement} 
     * @param drone 
     */
    public LiveSearchDocumentResult(WebElement element, WebDrone drone)
    {
        webElement = element;
        this.drone = drone;
    }

    /**
     * Title of search result document item.
     * @return String title
     */
    public ShareLink getTitle()
    {
        if(title == null)
        {
            try
            {
                WebElement titleElement = webElement.findElement(By.cssSelector(DOCUMENT_RESULT_TITLE));
                title = new ShareLink(titleElement, drone);
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search document result title", e);
            }
        }
        return title;
    }
    
    /**
     * Site name of document search result item.
     * @return String siteName
     */
    public ShareLink getSiteName()
    {
        if(siteName == null)
        {
            try
            {
                WebElement siteElement = webElement.findElement(By.cssSelector(DOCUMENT_RESULT_SITE_NAME));
                siteName = new ShareLink(siteElement, drone);
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search document result site name", e);
            }
        }
        return siteName;
    }
    
    /**
     * User name of document search result item.
     * @return String userName
     */
    public ShareLink getUserName()
    {
        if(userName == null)
        {
            try
            {
                WebElement userElement = webElement.findElement(By.cssSelector(DOCUMENT_RESULT_USER_NAME));
                userName = new ShareLink(userElement, drone);
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Unable to find live search document result user name", e);
            }
        }
        return userName;
    }
    
    /**
     * Clicks on document title on document search result
     */
    public HtmlPage clickOnDocumentTitle()
    {
        try
        {
            webElement.findElement(By.cssSelector(DOCUMENT_RESULT_TITLE)).click();
            return FactorySharePage.resolvePage(drone);
         
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Document title element not visible. " + nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find document title element. " + te);
        }
        throw new PageException("Unable to find document title element.");
    }
    
    /**
     * Clicks on document site title on document search result
     */
    public HtmlPage clickOnDocumentSiteTitle()
    {
        try
        {
            webElement.findElement(By.cssSelector(DOCUMENT_RESULT_SITE_NAME)).click();
            return FactorySharePage.resolvePage(drone);
         
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Document site title element not visible. " + nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find document site title element. " + te);
        }
        throw new PageException("Unable to find document site title element.");
    }
    
    /**
     * Clicks on document user name on document search result
     */
    public HtmlPage clickOnDocumentUserName()
    {
        try
        {
            webElement.findElement(By.cssSelector(DOCUMENT_RESULT_USER_NAME)).click();
            return FactorySharePage.resolvePage(drone);
         
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Document user name element not visible. " + nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find document user name element. " + te);
        }
        throw new PageException("Unable to find document user name element.");

    }    
}
