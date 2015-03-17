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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page in Table View is operating correctly.
 *
 * @author Jamie Allison
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class TableViewFileDirectoryInfoTest extends AbstractDocumentTest
{
    private static final String FILE_TITLE = "File";
    private static final String FILE_DESCRIPTION = "This is file";
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File file1;
    private File file2;
    private String userName;
    private String userFullName;

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "TableView" + System.currentTimeMillis();
        userName = siteName;
        userFullName = userName + "@test.com" + " " + userName + "@test.com";

        AlfrescoVersion version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
            userFullName = anotherUser.getfName() + " " + anotherUser.getlName();
        }
        else
        {
            createEnterpriseUser(userName);

            ShareUtil.loginAs(drone, shareUrl, userName, "password").render();
        }

        SiteUtil.createSite(drone, siteName, "description", "Public");
        file1 = SiteUtil.prepareFile();
        file2 = SiteUtil.prepareFile();

        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
    }

    @AfterClass(groups = "alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "alfresco-one")
    public void selectTableView() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        Assert.assertEquals(documentLibPage.getViewType(), ViewType.TABLE_VIEW);
    }

    @Test(dependsOnMethods = "selectTableView", groups = "alfresco-one")
    public void selectEditProperties() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);
        EditDocumentPropertiesPage editPage = fileInfo.selectEditProperties().render();
        editPage.setDescription(FILE_DESCRIPTION);
        editPage.setDocumentTitle(FILE_TITLE);
        documentLibPage = editPage.selectSave().render();

        Assert.assertEquals(fileInfo.getTitle(), FILE_TITLE);
    }

    @Test(dependsOnMethods = "selectEditProperties", groups = "alfresco-one")
    public void getCreator() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);

        Assert.assertEquals(fileInfo.getCreator(), userFullName);
    }

    @Test(dependsOnMethods = "getCreator", groups = "alfresco-one")
    public void selectCreator() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);
        MyProfilePage profile = fileInfo.selectCreator().render();

        navigateDocumentLib(profile);
    }

    private void navigateDocumentLib(MyProfilePage profile)
    {
        SiteFinderPage finderPage = profile.getNav().selectSearchForSites().render();
        finderPage = finderPage.searchForSite(siteName).render();
        SiteDashboardPage dashboardPage = finderPage.selectSite(siteName).render();
        documentLibPage = dashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
    }

    @Test(dependsOnMethods = "selectCreator", groups = "alfresco-one")
    public void getCreated() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);
        Assert.assertNotNull(fileInfo.getCreated());
    }

    @Test(dependsOnMethods = "getCreated", groups = "alfresco-one")
    public void getModifier() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);

        Assert.assertEquals(fileInfo.getModifier(), userFullName);
    }

    @Test(dependsOnMethods = "getModifier", groups = "alfresco-one")
    public void selectModifier() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);
        MyProfilePage profile = fileInfo.selectModifier().render();

        navigateDocumentLib(profile);
    }

    @Test(enabled = true, groups = "alfresco-one", dependsOnMethods = "selectModifier")
    public void testClickTitle()
    {
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage.getFileDirectoryInfo(file2.getName()).clickOnTitle().render();
    }

    @Test(dependsOnMethods = "testClickTitle", groups = "alfresco-one")
    public void getModified() throws Exception
    {
        SitePage page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo fileInfo = documentLibPage.getFiles().get(0);
        Assert.assertNotNull(fileInfo.getModified());
    }

    @Test(dependsOnMethods = "getModified", enabled = true, groups = "alfresco-one")
    public void renameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName());
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        thisRow.renameContent(file1.getName() + " updated");
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(file1.getName() + " updated").getName(), file1.getName() + " updated");
    }

    @Test(enabled = true, groups = "alfresco-one", dependsOnMethods = "renameContentTest")
    public void cancelRenameContentTest()
    {
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();

        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file2.getName());
        assertFalse(thisRow.isSaveLinkVisible());
        assertFalse(thisRow.isCancelLinkVisible());
        thisRow.contentNameEnableEdit();
        assertTrue(thisRow.isSaveLinkVisible());
        assertTrue(thisRow.isCancelLinkVisible());
        thisRow.contentNameEnter(file2.getName() + " not updated");
        thisRow.contentNameClickCancel();
        documentLibPage = drone.getCurrentPage().render();
        Assert.assertEquals(documentLibPage.getFileDirectoryInfo(file2.getName()).getName(), file2.getName());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "alfresco-one" }, dependsOnMethods = "cancelRenameContentTest")
    public void testGetContentNameFromInfoMenu() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName() + " updated");

        thisRow.getContentNameFromInfoMenu();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class, groups = { "alfresco-one" }, dependsOnMethods = "testGetContentNameFromInfoMenu")
    public void testGetVersionInfo() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName() + " updated");

        thisRow.getVersionInfo();
    }

    @Test(groups = { "alfresco-one" }, dependsOnMethods = "testGetVersionInfo")
    public void testCheckboxAndVersionMenu() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectTableView().render();
        // Get File
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file1.getName() + " updated");

        Assert.assertFalse(thisRow.isVersionVisible());
        Assert.assertTrue(thisRow.isCheckBoxVisible());
    }

    @Test(groups = { "alfresco-one" }, expectedExceptions = UnsupportedOperationException.class, dependsOnMethods = "testCheckboxAndVersionMenu")
    public void testIsCommentOptionPresent() throws Exception
    {
        documentLibPage = drone.getCurrentPage().render();
        documentLibPage.getFileDirectoryInfo(file2.getName()).isCommentLinkPresent();
    }

    @Test(enabled = true, groups = "alfresco-one", dependsOnMethods = "testIsCommentOptionPresent")
    public void testSelectViewInBrowser() throws Exception
    {
        documentLibPage = drone.getCurrentPage().render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(file2.getName());
        String mainWinHandle = drone.getWindowHandle();
        thisRow.selectViewInBrowser();
        Thread.sleep(2000);
        assertTrue(drone.getCurrentUrl().toLowerCase().contains(file2.getName().toLowerCase()));
        drone.closeWindow();
        drone.switchToWindow(mainWinHandle);
    }

}