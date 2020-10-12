/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.po.wqs;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by P3700360 - Florean Ilinca on 21.01.2015.
 */
public class WcmqsContactPageTest extends AbstractTest {
    private static final Logger logger = Logger.getLogger(WcmqsContactPageTest.class);
    private String wqsURL;
    private String siteName;
    private String ipAddress;
    DashBoardPage dashBoard;

    @BeforeClass(alwaysRun = true)
    public void prepare() throws Exception {
        String testName = this.getClass().getSimpleName();
        siteName = testName;

        String hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
        try {
            ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
            logger.info("Ip address from Alfresco server was obtained");
        } catch (UnknownHostException | SecurityException e) {
            logger.error("Ip address from Alfresco server could not be obtained");
        }

        wqsURL = siteName + ":8080/wcmqs";
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        // WCM Quick Start is installed; - is not required to be executed automatically
        //int columnNumber = 2;
        //String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
        dashBoard = loginAs(username, password);
/*
        // Site is created in Alfresco Share;
        CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        SitePage site = createSitePage.createNewSite(siteName).render();

        // WCM Quick Start Site Data is imported;
        CustomiseSiteDashboardPage customiseSiteDashboardPage = site.getSiteNav().selectCustomizeDashboard().render();
        SiteDashboardPage siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, columnNumber);
        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();
        wqsDashlet.waitForImportMessage();

        // Change property for quick start to sitename
        DocumentLibraryPage documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage.selectFolder("Alfresco Quick Start");
        EditDocumentPropertiesPage documentPropertiesPage = documentLibraryPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties()
                .render();
        documentPropertiesPage.setSiteHostname(siteName);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibraryPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // setup new entry in hosts to be able to access the new wcmqs site
        String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
        Runtime.getRuntime().exec(setHostAddress);
        */

    }

    @AfterClass
    public void tearDown() {
        logout(drone);
    }

    @BeforeMethod
    public void setUp()
    {
        drone.navigateTo(wqsURL);
    }

    @Test
    public void testSetVisitorName()
    {
    //Positive test
        String visitorName = "Name";
        String cssVisitorName = "input[name=visitorName]";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorName(visitorName);
        String name = drone.find(By.cssSelector(cssVisitorName)).getAttribute("value");
        Assert.assertEquals(name,visitorName);
    }

    @Test
    public void testSetVisitorEmail()
    {
        //Positive test
        String visitorEmail = "test@test.com";
        String cssVisitorEmail = "input[name=visitorEmail]";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorEmail(visitorEmail);
        String email = drone.find(By.cssSelector(cssVisitorEmail)).getAttribute("value");
        Assert.assertEquals(email, visitorEmail);
    }

    @Test
    public void testSetVisitorWebsite()
    {
        //Positive test
        String visitorWebSite = "test.com";
        String cssVisitorWebSite = "input[name=visitorWebsite]";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorWebsite(visitorWebSite);
        String website = drone.find(By.cssSelector(cssVisitorWebSite)).getAttribute("value");
        Assert.assertEquals(website, visitorWebSite);
    }


    @Test
    public void testSetVisitorComment()
    {
        //Positive test
        String visitorComment = "Test Comment";
        String cssVisitorComment = "textarea.bc-textarea";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorComment(visitorComment);
        String comment = drone.find(By.cssSelector(cssVisitorComment)).getAttribute("value");
        Assert.assertEquals(comment, visitorComment);
    }

    @Test
    public void testClickPostButton()
    {
        //Positive test
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        String visitorComment = "Test comment";
        String postMessage = "Your message has been sent!";
        String cssMsg = "div.contact-success";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorName(visitorName);
        wqsPage.setVisitorEmail(visitorEmail);
        wqsPage.setVisitorComment(visitorComment);
        wqsPage.clickPostButton();
        String message = drone.find(By.cssSelector(cssMsg)).getText(); //getAttribute("value");
        Assert.assertTrue(message.contains(postMessage));
    }

    @Test
    public void testIsAddCommentMessageDisplayPositive()
    {
        //Positive test
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        String visitorComment = "Test comment";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorName(visitorName);
        wqsPage.setVisitorEmail(visitorEmail);
        wqsPage.setVisitorComment(visitorComment);
        wqsPage.clickPostButton();
        Assert.assertTrue(wqsPage.isAddCommentMessageDisplay());

    }

    @Test
    public void testIsAddCommentMessageDisplayNegative()
    {
        //Positive test
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorName(visitorName);
        wqsPage.setVisitorEmail(visitorEmail);
        wqsPage.clickPostButton();
        Assert.assertFalse(wqsPage.isAddCommentMessageDisplay());

    }

    @Test
    public void testGetAddCommentSuccessfulMessagePositive()
    {
        //Positive test
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        String visitorComment = "Test comment";
        String postMessage = "Your message has been sent!\n" + "Thank you for emailing us! We will get in touch with you soon.";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorName(visitorName);
        wqsPage.setVisitorEmail(visitorEmail);
        wqsPage.setVisitorComment(visitorComment);
        wqsPage.clickPostButton();
        Assert.assertEquals(postMessage, wqsPage.getAddCommentSuccessfulMessage());

    }

    @Test (expectedExceptions = PageOperationException.class)
    public void testGetAddCommentSuccessfulMessageNegative()
    {
        //Positive test
        String visitorName = "Name";
        String visitorEmail = "test@test.com";
        String postMessage = "Your message has been sent!\n" + "Thank you for emailing us! We will get in touch with you soon.";
        WcmqsContactPage wqsPage = new WcmqsContactPage(drone);

        wqsPage.clickContactLink();
        wqsPage.setVisitorName(visitorName);
        wqsPage.setVisitorEmail(visitorEmail);
        wqsPage.clickPostButton();
        Assert.assertNotEquals(postMessage, wqsPage.getAddCommentSuccessfulMessage());

    }

}

