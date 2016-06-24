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
import java.util.List;

import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify File Directory info methods are operating correctly.
 * 
 * @author Ranjith Manyam
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
public class FileDirectoryInfoMoreTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String folderName;
    @SuppressWarnings("unused")
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private File testSyncFailedFile;
    private File googleTestFile;
    private String folder2Name;
    private String userName = "FileDirectoryInfoMoreTest" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folder2Name = "The Second Folder";
        folderDescription = String.format("Description of %s", folderName);
        DashBoardPage dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.createEnterpriseUserWithGroup(userName, firstName, lastName, userName, userName, "ALFRESCO_ADMINISTRATORS");
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        userPage.searchFor(userName).render();
        Assert.assertTrue(userPage.hasResults());
        logout(driver);
        loginAs(userName, userName);
        driver.navigate().to(shareUrl);
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        testSyncFailedFile = siteUtil.prepareFile("1-SyncFailFile");
        googleTestFile = siteUtil.prepareFile("googleTestFile");
    }



    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="alfresco-one")
    public void createData() throws Exception
    {
        documentLibPage = openSiteDocumentLibraryFromSearch(driver, siteName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testSyncFailedFile.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(googleTestFile.getCanonicalPath()).render();
        NewFolderPage folderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = folderPage.createNewFolder(folderName).render();
    }

    @Test(dependsOnMethods = "createData", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionHideFolders()
    {
        Assert.assertTrue(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectHideFolders().render();
        Assert.assertFalse(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectHideFolders().render();
    }

    @Test(dependsOnMethods = "selectOptionHideFolders", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionShowFolders()
    {
        Assert.assertFalse(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectShowFolders().render();
        Assert.assertTrue(documentLibPage.isFileVisible(folderName));
        documentLibPage = documentLibPage.getNavigation().selectShowFolders().render();
    }
    
    @Test(dependsOnMethods = "selectOptionShowFolders", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionHideBreadcrump()
    {
        Assert.assertTrue(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
        Assert.assertFalse(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
    }
    
    @Test(dependsOnMethods = "selectOptionHideBreadcrump", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void selectOptionShowBreadcrump()
    {
        Assert.assertFalse(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
        Assert.assertTrue(documentLibPage.getNavigation().isNavigationBarVisible());
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
    }
    
    @Test(dependsOnMethods = "selectOptionShowBreadcrump", groups = {"Enterprise4.2"}, expectedExceptions=PageOperationException.class)
    public void getFoldersInNavBarWithException()
    {
        documentLibPage = documentLibPage.getNavigation().selectHideBreadcrump().render();
        documentLibPage.getNavigation().getFoldersInNavBar();
    }
    
    @Test(dependsOnMethods = "getFoldersInNavBarWithException", groups = {"Enterprise4.2"})
    public void getFoldersInNavBar()
    {
        documentLibPage = documentLibPage.getNavigation().selectShowBreadcrump().render();
        List<ShareLink> links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 1);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        documentLibPage = documentLibPage.selectFolder(folderName).render();
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 2);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        Assert.assertTrue(links.get(1).getDescription().equals(folderName));
        NewFolderPage folderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = folderPage.createNewFolder(folder2Name).render(); 
        Assert.assertTrue(documentLibPage.isFileVisible(folder2Name));
        documentLibPage = documentLibPage.selectFolder(folder2Name).render();
        Assert.assertFalse(documentLibPage.isFileVisible(folder2Name));
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 3);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        Assert.assertTrue(links.get(1).getDescription().equals(folderName));
        Assert.assertTrue(links.get(2).getDescription().equals(folder2Name));
        documentLibPage = documentLibPage.getNavigation().selectFolderInNavBar(folderName).render();
        Assert.assertTrue(documentLibPage.isFileVisible(folder2Name));
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 2);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
        Assert.assertTrue(links.get(1).getDescription().equals(folderName));
        documentLibPage = documentLibPage.getNavigation().selectFolderInNavBar("Documents").render();
        links = documentLibPage.getNavigation().getFoldersInNavBar();
        Assert.assertTrue(links.size() == 1);
        Assert.assertTrue(links.get(0).getDescription().equals("Documents"));
    }
}
