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
package org.alfresco.po.share.user;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.FactoryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserContentItems extends PageElement 
{

	private static final By CONTENT_NAME = By.cssSelector("p a");
	
	private static Log logger = LogFactory.getLog(UserContentItems.class);
	
	/**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param driver
     */
    public UserContentItems(WebElement element, WebDriver driver, FactoryPage factoryPage)
    {
        setWrappedElement(element);
        this.factoryPage = factoryPage;
    }
    
    /**
     * Get the content name as displayed on screen.
     * 
     * @return String
     */
    public String getContentName()
    {
        try
        {
            return findAndWait(CONTENT_NAME).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find content name: " + CONTENT_NAME, e);
        }

        throw new PageOperationException("Unable to find the content name: " + CONTENT_NAME);
    }
    
    /**
     * Click on the Content name.
     * 
     * @return Page
     */
    public HtmlPage clickOnContentName()
    {
        try
        {
            findAndWait(CONTENT_NAME).click();
            domEventCompleted();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find content name: " + CONTENT_NAME, e);
        }

        throw new PageOperationException("Unable to find the site name: " + CONTENT_NAME);
    }
    
}
