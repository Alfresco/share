/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.util.List;
import java.util.Map;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class FolderDetailsPageTest extends AbstractTest
{

    private final Log logger = LogFactory.getLog(this.getClass());

    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private FolderDetailsPage folderDetailsPage;
    private String userName = "FolderDetailsPageTest" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled())
        logger.trace("====prepare====");
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
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
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
    }

    @AfterClass(groups = { "alfresco-one" })
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers
     * major and minor version changes
     * 
     * @throws Exception
     */
    @Test(groups = { "alfresco-one" })
    public void createData() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====createData====");
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
    }

    @Test(dependsOnMethods = "createData", groups = { "alfresco-one" })
    public void testSelectViewFolderDetails() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====testSelectViewFolderDetails====");
        // Get folder
        FileDirectoryInfo content = getFolder();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(content.getName());
        folderDetailsPage = thisRow.selectViewFolderDetails().render();
        Assert.assertEquals(folderName, folderDetailsPage.getContentTitle());
    }

    @Test(dependsOnMethods = "testSelectViewFolderDetails", groups = { "alfresco-one" })
    public void testFolderDetailsPanels() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====testFolderDetailsPanels====");
        }
        boolean isCorrectPath = folderDetailsPage.isCorrectPath(folderName);
        Assert.assertTrue(isCorrectPath);
        Assert.assertTrue(folderDetailsPage.isCommentLinkPresent());
        Assert.assertTrue(folderDetailsPage.isSharePanePresent());
        Assert.assertEquals(true, folderDetailsPage.isTagPanelPresent());
        Assert.assertTrue(folderDetailsPage.isModifiedByDetailsPresent());
        Assert.assertTrue(folderDetailsPage.isFolderActionsPresent());

    }

    /**
     * Negative test case
     */
    @Test(dependsOnMethods = "testSelectViewFolderDetails", groups = { "alfresco-one" })
    public void testFolderDetailsPanelsNotCorrect() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====testFolderDetailsPanels====");
        boolean isCorrectPath = folderDetailsPage.isCorrectPath("Folder");
        Assert.assertFalse(isCorrectPath);
        Assert.assertFalse(!folderDetailsPage.isModifiedByDetailsPresent());
        Assert.assertFalse(!folderDetailsPage.isCommentLinkPresent());
        Assert.assertFalse(!folderDetailsPage.isSharePanePresent());
        Assert.assertEquals(false, !folderDetailsPage.isTagPanelPresent());
    }

    @Test (dependsOnMethods = "testFolderDetailsPanelsNotCorrect", groups = { "alfresco-one" })
    public void verifyPropertiesSection()
    {
        Assert.assertTrue(folderDetailsPage.isPropertiesLabelsPresent(), "Labels of Properties section display incorrectly");
    }

    // include negative scenario..

    @Test(dependsOnMethods = "verifyPropertiesSection", groups = { "alfresco-one" })
    public void testLikeMethodsForFolder() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====testLikeMethodsForFolder====");
        Assert.assertFalse(folderDetailsPage.isLiked());
        Assert.assertEquals(folderDetailsPage.getLikeCount(), "0");
        folderDetailsPage.selectLikeFolder().render();
        Assert.assertTrue(folderDetailsPage.isLiked());
    }

    @Test(dependsOnMethods = "testLikeMethodsForFolder", groups = { "alfresco-one" })
    public void testFavouriteMethodsForFolder() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====testFavouriteMethodsForFolder====");
        // Favourite
        Assert.assertFalse(folderDetailsPage.isFavourite());
        folderDetailsPage.selectFavourite().render();
        Assert.assertTrue(folderDetailsPage.isFavourite());
    }

    @Test(dependsOnMethods = "testFavouriteMethodsForFolder", groups = { "alfresco-one" })
    public void testFolderProperties()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testFolderProperties====");
        Map<String, Object> properties = folderDetailsPage.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertEquals(properties.get("Name"), folderName);
        Assert.assertEquals(properties.get("Title"), "(None)");
        Assert.assertEquals(properties.get("Description"), folderDescription);
    }

    @Test(dependsOnMethods = "testFolderProperties", groups = { "alfresco-one" })
    public void testFolderComments()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testFolderComments====");
        folderDetailsPage.isCommentAddedAndRemoved("COMMENT added");

    }

    @Test(dependsOnMethods = "testFolderComments", groups = { "alfresco-one" })
    public void testSelectEditProperties()
    {
        if (logger.isTraceEnabled())
            logger.trace("====testSelectEditProperties====");

        EditDocumentPropertiesPage propertiesPage = folderDetailsPage.selectEditProperties().render();
        folderDetailsPage = propertiesPage.selectCancel().render();

        propertiesPage = folderDetailsPage.selectEditProperties().render();
        folderDetailsPage = propertiesPage.selectSave().render();
    }

    @Test(dependsOnMethods = "testSelectEditProperties", groups = { "Enterprise4.2" })
    public void testDownloadAsZipFolder()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====testDownloadAsZipFolder====");
        }
        Assert.assertTrue(folderDetailsPage.isDownloadAsZipAtTopRight());
        folderDetailsPage.selectDownloadFolderAsZip("folder").render();
    }

    /*
     * @Test(dependsOnMethods = "testFolderComments", expectedExceptions = UnsupportedOperationException.class, groups = { "Enterprise4.1" })
     * public void testDownloadAsZipFolderWithException()
     * {
     * if (logger.isTraceEnabled()) logger.trace("====testDownloadAsZipFolderWithException====");
     * folderDetailsPage.selectDownloadFolderAsZip("folder").render();
     * }
     */

    /**
     * Method renders the documentlibrary page and returns the folder as
     * FileDirectoryInfo
     * 
     * @return FileDirectoryInfo element for folder / content at index 0
     * @throws Exception
     */
    private FileDirectoryInfo getFolder() throws Exception
    {
        if (logger.isTraceEnabled())
            logger.trace("====testFolderComments====");
        documentLibPage = resolvePage(driver).render();
        List<FileDirectoryInfo> results = documentLibPage.getFiles();
        if (results.isEmpty())
        {
            throw new Exception("Error getting folder");
        }
        else
        {
            // Get folder
            return results.get(0);
        }
    }

}
