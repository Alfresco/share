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
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Functional test to test View Properties Page
 * 
 * @author Maryia Zaichanka
 */
@Listeners(FailedTestListener.class)
public class ViewPropertiesPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(ViewPropertiesPageTest.class);

    private DashBoardPage dashBoard;
    private DocumentLibraryPage documentLibPage;
    private DocumentDetailsPage docDetailsPage;
    private ViewPropertiesPage viewPropPage;

    private String siteName;
    private static String fileName;
    private String v = "1.0";

    @BeforeClass(groups = { "Enterprise4.2"})
    public void setup() throws Exception
    {
        siteName = "site-" + System.currentTimeMillis();
        fileName = "File";

        dashBoard = loginAs(username, password);
        dashBoard = dashBoard.getNav().selectMyDashBoard().render();

        siteUtil.createSite(driver, username, password, siteName, "description", "Public");

        // Select DocLib
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        // Upload File
        File file = siteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();

        // Open Details Page of the uploaded file
        docDetailsPage = documentLibPage.selectFile(fileName).render();

        // Upload new version
        UpdateFilePage updateFilePage = docDetailsPage.selectUploadNewVersion().render();

        updateFilePage.selectMajorVersionChange();
        updateFilePage.uploadFile(file.getCanonicalPath());
        docDetailsPage = updateFilePage.submitUpload().render();

    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test
    public void resolveViewPropDialogue() throws Exception
    {
        docDetailsPage.selectViewProperties(v).render();
        viewPropPage = factoryPage.getPage(driver).render();
        Assert.assertTrue(viewPropPage.isVersionButtonDisplayed());

        String title = viewPropPage.getVersionButtonTitle();
        viewPropPage = factoryPage.getPage(driver).render();
        Assert.assertNotNull(viewPropPage);

        logger.info("Version button title: " + title);

    }

    @Test(dependsOnMethods = "resolveViewPropDialogue")
    public void selectOtherVersionWindow() throws Exception
    {
        String versionTitle = viewPropPage.getVersionButtonTitle();
        viewPropPage.selectOtherVersion(false);
        String otherVersionTitle = viewPropPage.getVersionButtonTitle();
        viewPropPage = factoryPage.getPage(driver).render();
        Assert.assertNotNull(viewPropPage);
        Assert.assertNotSame(versionTitle, otherVersionTitle);

        logger.info("Version button title: " + viewPropPage.getVersionButtonTitle());
    }

    @Test(dependsOnMethods = "selectOtherVersionWindow")
    public void closeCreateSiteDialogue() throws Exception
    {
        docDetailsPage = closeDialogue().render();
    }

    public HtmlPage closeDialogue() throws Exception
    {
        ViewPropertiesPage dialogue = factoryPage.getPage(driver).render();
        HtmlPage sharePage = dialogue.closeDialogue().render();

        Assert.assertNotNull(sharePage);
        return sharePage;
    }
}
