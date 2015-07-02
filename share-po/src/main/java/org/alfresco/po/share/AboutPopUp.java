/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Object associated with PopUp message about alfresco creators, version and revision.
 *
 * @author Aliaksei Boole
 */
public class AboutPopUp extends HtmlElement
{
    private final static By FORM_XPATH = By.xpath("//div[@id='alfresco-AboutShare-instance-logo']");
    private final static By VERSIONS_DETAILS = By.cssSelector(".about>div:nth-child(2)");

    public AboutPopUp(WebDrone drone)
    {
        super(drone);
        WebElement webElement = drone.findAndWait(FORM_XPATH);
        setWebElement(webElement);
    }

    /**
     * Return About Logo Url
     *
     * @return String
     */
    public String getLogoUrl()
    {
        return getWebElement().getCssValue("background-image").replace("url(\"", "").replace("\")", "");
    }
    
    /**
     * Verify if the version details are displayed
     *
     * @return true if found
     */
    public boolean isVersionsDetailDisplayed()
    {
        return drone.isElementDisplayed(VERSIONS_DETAILS);
    }
    
    /**
     * Get the versions detail
     * 
     * @return String
     */
    public String getVersionsDetail()
    {
        try
        {
            return drone.findAndWait(VERSIONS_DETAILS).getText();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not able to find Version Details", e);
        }
        catch (TimeoutException te)
        {
            throw new PageException("Exceeded the time to find the Version Details", te);
        }
    }

}
