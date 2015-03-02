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
 * Holds details of the user in people live search results
 * @author jcule
 *
 */
public class LiveSearchPeopleResult
{
    
    private static Log logger = LogFactory.getLog(LiveSearchPeopleResult.class);
    
    private static final String USER_NAME = "a";
    
    private WebDrone drone ;
    private WebElement webElement;
    private ShareLink userName;
    
    
    /**
     * Constructor
     * @param element {@link WebElement} 
     * @param drone 
     */
    public LiveSearchPeopleResult(WebElement element, WebDrone drone)
    {
        webElement = element;
        this.drone = drone;
    }

    /**
     * Returns user search result
     * @return String title
     */
    public ShareLink getUserName()
    {
        if(userName == null)
        {
            try
            {
                WebElement siteTitleElement = webElement.findElement(By.cssSelector(USER_NAME));
                userName = new ShareLink(siteTitleElement, drone);
            }
            catch (NoSuchElementException nse)
            {
                throw new PageOperationException("Unable to find live search user name result ", nse);
            }
        }
        return userName;
    }
    
  
 
    /**
     * Clicks on user search result
     */
    public HtmlPage clickOnUserName()
    {
        try
        {
            webElement.findElement(By.cssSelector(USER_NAME)).click();
            return FactorySharePage.resolvePage(drone);
         
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Username element not visible. " + nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find document username element. " + te);
        }
        throw new PageException("Unable to find username element.");
    }

    
    

}
