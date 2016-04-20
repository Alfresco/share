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
package org.alfresco.po.alfresco;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.PageElement;
import org.alfresco.po.share.Navigation;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Michael Suzuki on 30/11/2015.
 */
public class PageElementTest extends AbstractTest
{

    PageElement element;
    @Test
    public void findValidElement()
    {
        driver.get(shareUrl);
        element = factoryPage.instantiatePageElement(driver, Navigation.class);
        Assert.assertNotNull(element);
        boolean visible = element.findAndWait(By.cssSelector("button[id$='_default-submit-button']")).isDisplayed();
        Assert.assertTrue(visible);
    }
    @Test(expectedExceptions = NoSuchElementException.class)
    public void findElementThatsNotThere()
    {
        element = factoryPage.instantiatePageElement(driver, Navigation.class);
        element.findAndWait(By.cssSelector("input.null"));
    }
}
