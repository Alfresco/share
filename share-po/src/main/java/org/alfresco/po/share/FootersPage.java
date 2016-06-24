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

import java.util.HashMap;
import java.util.Map;

import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This is a Footers page carrying info about Copyright, license and etc.
 * 
 * @author nshah
 */
public class FootersPage extends ShareDialogue
{
    Log logger = LogFactory.getLog(this.getClass());
    @RenderWebElement
    private static By FOOTER = By.cssSelector(".about");
    @RenderWebElement
    private static By COPYRIGHT_INFO = By.cssSelector(".copy");
  
    private static By HEADER_DETAILS = By.cssSelector(".about>div.header");
  

    @SuppressWarnings("unchecked")
    @Override
    public FootersPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FootersPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Is License info present.
     * 
     * @return boolean
     */
    public boolean isLicenseInfoPresent()
    {
        try
        {
            return driver.findElement(LICENSE_TO).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.error("LicenseInfo is not present");
            }
            return false;
        }

    }

    /**
     * @param driver
     * @return
     */
    private Map<String, String> getHeaderInfo(WebDriver driver)
    {
        String searchString = "Alfresco Enterprise v";
        Map<String, String> headerMap = new HashMap<String, String>();
        try
        {
            for (WebElement element : driver.findElements(HEADER_DETAILS))
            {
                if (element.getText().contains(searchString))
                {
                    headerMap.put("productInfo", element.getText());
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.error("FootersPage is not present");
            }
        }

        return headerMap;
    }

    /**
     * Get Alfresco product info which contains the version and name of the product.
     * @return String
     */
    public String getAlfrescoVersion()
    {
        Map<String, String> versionMap = getHeaderInfo(driver);
        if(versionMap.containsKey("productInfo"))
        {
            return versionMap.get("productInfo");
        }
        else
        {
            throw new PageOperationException("Prodcut info is not present");
        }


    }

//    /**
//     * @return
//     */
//    public String getLicenseInfo()
//    {
//        
//        try
//        {
//            return driver.findElement(LICENSE_HOLDER).getText();
//        }
//        catch (NoSuchElementException nse)
//        {
//            if (logger.isTraceEnabled())
//            {
//                logger.trace("License holder element not present" + nse.getMessage());
//            }
//            throw new PageOperationException("Not a license page");
//        }
//
//    }

//    /**
//     * @return
//     */
//    public List<String> getContributors()
//    {
//        List<String> contributors = new ArrayList<String>();
//
//        List<WebElement> elements = driver.findElements(CONTRIBUTORS);
//
//        for (WebElement element : elements)
//        {
//            contributors.add(element.getText());
//        }
//        return contributors;
//    }

//    /**
//     * @return
//     */
//    public String getCopyRightsDate()
//    {
//        if(alfrescoVersion.isCloud())
//        {
//            COPYRIGHTS_DATE = By.cssSelector(".copy") ;//All rights reserved.
//        }
//        try{
//        for(WebElement element: driver.findElements(COPYRIGHTS_DATE))
//        {
//            if(element.getText().contains("All rights reserved."))
//            {
//                return element.getText();
//            }
//        }
//        }
//        catch(NoSuchElementException nse)
//        {
//            if (logger.isTraceEnabled())
//            {
//                logger.trace("Copy right details not present" + nse.getMessage());
//            }           
//        }
//        throw new PageOperationException("Footer page does not have copyright details");
//        
//    }

}
