/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit tests to verify selectSyncToCloud methods in FileDirectoryInfo and DocumentLibraryNavigation are working correctly.
 * @author Ranjith Manyam
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class SelectForceUnSyncTest extends AbstractDocumentTest
{
    private String siteNameOP;
    private String siteNameCL;
    private File file1;
    private File file2;
    private File file3;
    private File file4;
    private File file5;
    private DocumentLibraryPage documentLibPage;
    private DestinationAndAssigneePage destinationAndAssigneePage ;

    /**
     * Pre test setup to disconnect CloudSync
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteNameOP = "Site-OP" + System.currentTimeMillis();
        siteNameCL = "Site-CL" + System.currentTimeMillis();
        file1 = SiteUtil.prepareFile("F1-");
        file2 = SiteUtil.prepareFile("F2-");
        file3 = SiteUtil.prepareFile("F3-");
        file4 = SiteUtil.prepareFile("F4-");
        file5 = SiteUtil.prepareFile("F5-");
        loginAs(username, password);
        disconnectCloudSync(drone);
        signInToCloud(drone, cloudUserName, cloudUserPassword);
        SiteUtil.createSite(drone, siteNameOP, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        uploadContent(drone, file1.getCanonicalPath());
        uploadContent(drone, file2.getCanonicalPath());
        uploadContent(drone, file3.getCanonicalPath());
        uploadContent(drone, file4.getCanonicalPath());
        documentLibPage = uploadContent(drone, file5.getCanonicalPath());

        loginAs(hybridDrone, hybridShareUrl, cloudUserName, cloudUserPassword);
        SiteUtil.createSite(hybridDrone, siteNameCL, "Public");
    }

    @Test(groups={"alfresco-one"})
    public void selectSyncToCloud()
    {
        documentLibPage = documentLibPage.getNavigation().selectAll().render();
        destinationAndAssigneePage = documentLibPage.getNavigation().selectSyncToCloud().render();
        destinationAndAssigneePage.selectSite(siteNameCL);
        documentLibPage = destinationAndAssigneePage.selectSubmitButtonToSync().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file1.getName()).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file2.getName()).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file3.getName()).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file4.getName()).isCloudSynced());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(file5.getName()).isCloudSynced());
        Assert.assertTrue(checkIfContentIsSynced(drone, file1.getName()));
        Assert.assertTrue(checkIfContentIsSynced(drone, file2.getName()));
        Assert.assertTrue(checkIfContentIsSynced(drone, file3.getName()));
        Assert.assertTrue(checkIfContentIsSynced(drone, file4.getName()));
        Assert.assertTrue(checkIfContentIsSynced(drone, file5.getName()));
    }

    @Test (dependsOnMethods = "selectSyncToCloud", groups={"alfresco-one"})
    public void selectForceUnSyncInCloud()
    {
        DocumentLibraryPage documentLibraryPageCL = openSiteDocumentLibraryFromSearch(hybridDrone, siteNameCL);
        documentLibraryPageCL = documentLibraryPageCL.getFileDirectoryInfo(file1.getName()).selectForceUnSyncInCloud().render();

        documentLibraryPageCL = documentLibraryPageCL.getNavigation().selectTableView().render();
        hybridDrone.refresh();
        documentLibraryPageCL = documentLibraryPageCL.getFileDirectoryInfo(file5.getName()).selectForceUnSyncInCloud().render();

        documentLibraryPageCL = documentLibraryPageCL.getNavigation().selectDetailedView().render();
        hybridDrone.refresh();
        documentLibraryPageCL = documentLibraryPageCL.getFileDirectoryInfo(file2.getName()).selectForceUnSyncInCloud().render();

        documentLibraryPageCL = documentLibraryPageCL.getNavigation().selectFilmstripView().render();
        hybridDrone.refresh();
        documentLibraryPageCL = documentLibraryPageCL.getFileDirectoryInfo(file4.getName()).selectForceUnSyncInCloud().render();


        documentLibraryPageCL = documentLibraryPageCL.getNavigation().selectGalleryView().render();
        hybridDrone.refresh();
        documentLibraryPageCL = documentLibraryPageCL.getFileDirectoryInfo(file3.getName()).selectForceUnSyncInCloud().render();

        documentLibraryPageCL = documentLibraryPageCL.getNavigation().selectSimpleView().render();

        hybridDrone.refresh();
        documentLibraryPageCL = documentLibraryPageCL.renderItem(maxWaitTime_CloudSync, file1.getName());
        Assert.assertFalse(documentLibraryPageCL.getFileDirectoryInfo(file1.getName()).isCloudSynced());
        Assert.assertFalse(documentLibraryPageCL.getFileDirectoryInfo(file2.getName()).isCloudSynced());
        Assert.assertFalse(documentLibraryPageCL.getFileDirectoryInfo(file3.getName()).isCloudSynced());
        Assert.assertFalse(documentLibraryPageCL.getFileDirectoryInfo(file4.getName()).isCloudSynced());
        Assert.assertFalse(documentLibraryPageCL.getFileDirectoryInfo(file5.getName()).isCloudSynced());
    }

    @AfterClass(groups={"alfresco-one"})
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteNameOP);
        disconnectCloudSync(drone);
        SiteUtil.deleteSite(hybridDrone, siteNameCL);
    }
}
