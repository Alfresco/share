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
package org.alfresco.po.share;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Object associated with PopUp message about alfresco creators, version and revision.
 *
 * @author Aliaksei Boole
 */
public class AboutPopUp extends PageElement
{
    private final static By FORM_XPATH = By.xpath("//div[@id='alfresco-AboutShare-instance-logo']");
    private final static By VERSIONS_DETAILS = By.cssSelector(".about>div:nth-child(2)");

    public AboutPopUp(WebDriver driver)
    {
        WebElement webElement = findAndWait(FORM_XPATH);
        setWrappedElement(webElement);
    }

    /**
     * Return About Logo Url
     *
     * @return String
     */
    public String getLogoUrl()
    {
        return getWrappedElement().getCssValue("background-image").replace("url(\"", "").replace("\")", "");
    }
    
    /**
     * Verify if the version details are displayed
     *
     * @return true if found
     */
    public boolean isVersionsDetailDisplayed()
    {
        return isElementDisplayed(VERSIONS_DETAILS);
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
            return findAndWait(VERSIONS_DETAILS).getText();
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
