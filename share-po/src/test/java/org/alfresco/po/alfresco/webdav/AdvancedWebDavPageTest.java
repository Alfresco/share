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
package org.alfresco.po.alfresco.webdav;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class AdvancedWebDavPageTest extends AbstractTest
{
    WebDavPage webDavPage;

    @BeforeClass
    public void setup() throws Exception
    {
        loginAs(username, password).render();
        webDavPage = shareUtil.navigateToWebDav(driver, username, password).render();

    }

    @Test(groups = "Enterprise-only", alwaysRun = true)
    public void getDirectoryTextTest() throws Exception
    {
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Webdav page isn't opened");
    }

    @Test(dependsOnMethods = "getDirectoryTextTest", groups = "Enterprise-only", alwaysRun = true)
    public void checkDirectoryDisplayedTest() throws Exception
    {
        Assert.assertTrue(webDavPage.checkDirectoryDisplayed("Sites"), "Directory 'Sites' isn't displayed");
    }

    @Test(dependsOnMethods = "checkDirectoryDisplayedTest", groups = "Enterprise-only", alwaysRun = true)
    public void clickDirectoryTest() throws Exception
    {
        webDavPage.clickDirectory("Sites");
        Assert.assertTrue(webDavPage.checkUpToLevelDisplayed(), "Link 'Up to level' isn't displayed");
    }

    @Test(dependsOnMethods = "clickDirectoryTest", groups = "Enterprise-only", alwaysRun = true)
    public void checkUpToLevelDisplayedTest() throws Exception
    {
        Assert.assertTrue(webDavPage.checkUpToLevelDisplayed(), "Link 'Up to level' isn't displayed");

    }

    @Test(dependsOnMethods = "checkUpToLevelDisplayedTest", groups = "Enterprise-only", alwaysRun = true)
    public void clickUpToLevelTest() throws Exception
    {
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /Sites"), "Current directory isn't 'Sites'");
        webDavPage.clickUpToLevel();
        Assert.assertTrue(webDavPage.getDirectoryText().equals("Directory listing for /"), "Directory 'one level up' isn't opened");
    }

}
