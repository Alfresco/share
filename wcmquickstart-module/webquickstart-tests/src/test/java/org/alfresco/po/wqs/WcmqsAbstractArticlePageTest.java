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

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.dashlet.SiteWebQuickStartDashlet;
import org.alfresco.po.share.dashlet.WebQuickStartOptions;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.wqs.AbstractWQS;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.springframework.social.alfresco.api.entities.Site;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by P3700360 on 06.02.2015.
 */

@Listeners(FailedTestListener.class)
public class WcmqsAbstractArticlePageTest extends AbstractWQS
{
    private static final Logger logger = Logger.getLogger(WcmqsAbstractArticlePageTest.class);
    DashBoardPage dashBoard;
    private String siteName;
    private String ipAddress;
    private String[] loginInfo;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        String testName = this.getClass().getSimpleName();
        siteName = testName + System.currentTimeMillis();
        loginInfo = new String[] { ADMIN_USERNAME, ADMIN_PASSWORD };
        ipAddress = getIpAddress();
        logger.info(" wcmqs url : " + wqsURL);
        logger.info("Start Tests from: " + testName);

        int columnNumber = 2;
        String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
        loginActions.loginToShare(drone, loginInfo, shareUrl);
        siteService.create(ADMIN_USERNAME, ADMIN_PASSWORD, DOMAIN_FREE, siteName, "", Site.Visibility.PUBLIC);

        SiteDashboardPage siteDashboardPage = (SiteDashboardPage) siteActions.openSiteDashboard(drone, siteName);
        CustomiseSiteDashboardPage customiseSiteDashboardPage = siteDashboardPage.getSiteNav().selectCustomizeDashboard().render();
        siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, 1).render();

        SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
        wqsDashlet.clickImportButtton();
        wqsDashlet.waitForImportMessage();

        // Change property for quick start to sitename
        DocumentLibraryPage documentLibPage = siteActions.openSiteDashboard(drone, siteName).getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage.selectFolder("Alfresco Quick Start");
        org.alfresco.po.share.site.document.EditDocumentPropertiesPage documentPropertiesPage = documentLibPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname(ipAddress);
        documentPropertiesPage.clickSave();

        // Change property for quick start live to ip address
        documentLibPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
        documentPropertiesPage.setSiteHostname("localhost");
        documentPropertiesPage.clickSave();
        ShareUtil.logout(drone);
        waitForWcmqsToLoad();
        loginToWqsFromHomePage();

    }

    @Test
    public void testClickEditButton()
    {
        String titleEdit = "Edit Ethical funds";
        WcmqsAbstractArticlePage wqsPage = new WcmqsBlogPostPage(drone);
        WcmqsBlogPage wqsBlogs = wqsPage.selectMenu("blog").render();
        wqsBlogs.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsEditPage wcmqsEditPage = wqsPage.clickEditButton().render();
        Assert.assertEquals(titleEdit, wcmqsEditPage.getEditPageTitle());
    }

    @Test
    public void testClickCreateButton()
    {
        String titleEdit = "Create Article";
        WcmqsAbstractArticlePage wqsPage = new WcmqsBlogPostPage(drone);
        WcmqsBlogPage wqsBlogs = wqsPage.selectMenu("blog").render();
        wqsBlogs.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        WcmqsEditPage wcmqsEditPage = wqsPage.clickCreateButton().render();
        Assert.assertEquals(titleEdit, wcmqsEditPage.getEditPageTitle());
    }

    @Test
    public void testClickDeleteButton()
    {
        String cssEdit = "a[class='alfresco-content-delete']";
        String titleEdit = "Confirm Delete";
        WcmqsAbstractArticlePage wqsPage = new WcmqsBlogPostPage(drone);
        WcmqsBlogPage wqsBlogs = wqsPage.selectMenu("blog").render();
        WcmqsBlogPostPage wcmqsBlogPostPage = wqsBlogs.clickLinkByTitle(WcmqsBlogPage.ETHICAL_FUNDS).render();
        wqsPage.clickDeleteButton();
        Assert.assertEquals(titleEdit, wcmqsBlogPostPage.getDeleteConfirmationTitle());
    }

    @Test
    public void testIsEditButtonDisplayed()
    {
        WcmqsBlogPostPage wqsArticle = new WcmqsBlogPostPage(drone);
        Assert.assertTrue(wqsArticle.isEditButtonDisplayed());
    }

    @Test
    public void testIsDeleteButtonDisplayed()
    {
        WcmqsBlogPostPage wqsArticle = new WcmqsBlogPostPage(drone);
        Assert.assertTrue(wqsArticle.isDeleteButtonDisplayed());
    }

    @Test
    public void testIsCreateButtonDisplayed()
    {
        WcmqsBlogPostPage wqsArticle = new WcmqsBlogPostPage(drone);
        Assert.assertTrue(wqsArticle.isCreateButtonDisplayed());
    }
}

