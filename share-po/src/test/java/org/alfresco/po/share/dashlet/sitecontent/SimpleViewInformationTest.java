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
package org.alfresco.po.share.dashlet.sitecontent;

import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.ShareLink;
import org.openqa.selenium.WebDriver;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Shan Nagarajan
 * @since  1.6.1
 */
@Test(groups="unit")
public class SimpleViewInformationTest
{

    WebElement element;
    WebDriver driver;
    
    @BeforeClass
    public void setup()
    {
        element = Mockito.mock(WebElement.class);
        Mockito.when(element.getText()).thenReturn("");
        Mockito.when(element.getAttribute("href")).thenReturn("");
        driver = Mockito.mock(WebDriver.class);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void withNullThumnail()
    {
       new SimpleViewInformation(driver, null, new ShareLink(element, null, null), new ShareLink(element, null, null), "", false, null);
    }

    @Test(dependsOnMethods="withNullThumnail", expectedExceptions=UnsupportedOperationException.class)
    public void withNullContentDetaill()
    {
        new SimpleViewInformation(driver, new ShareLink(element, null, null), null, new ShareLink(element, null, null), "", false, null);
    }

    @Test(dependsOnMethods="withNullContentDetaill", expectedExceptions=UnsupportedOperationException.class)
    public void withNullUser()
    {
        new SimpleViewInformation(driver, new ShareLink(element, null, null), new ShareLink(element, null, null), null,"", false, null);
    }

    @Test(dependsOnMethods="withNullUser")
    public void toStringTest()
    {
        SimpleViewInformation information = new SimpleViewInformation(driver, new ShareLink(element, null, null), new ShareLink(element, null, null), new ShareLink(element, null, null),"", false, null);
        assertEquals("SimpleViewInformation [thumbnail=ShareLink [description=, href=], contentDetail=ShareLink [description=, href=], contentStatus=, user=ShareLink [description=, href=], previewDisplayed=false]", information.toString());
    }
}
