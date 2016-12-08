/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.ACTION;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.DESTINATION;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Tests to verify the Interactions with Links created on files / folders.
 *
 * @author adinap
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class LinkToFileFolderActionsTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(CreateLinkToFileFolderTest.class);

    private String siteName1;
    private String siteName2;
    private File file1;
    private File file2;
    private File file3;
    private String folderName1;
    private String folderName2;
    private String file1LinkName;
    private String file2LinkName;
    private String file3LinkName;
    private String folder1LinkName;

    private DocumentLibraryPage docLib;

    @Autowired
    SiteActions siteActions;

    @BeforeClass(groups = "alfresco-one", alwaysRun=true)
    public void prepare() throws Exception
    {
        try
        {
            loginAs(username, password);

            siteName1 = "site1-" + System.currentTimeMillis();
            siteName2 = "site2-" + System.currentTimeMillis();

            file1 = siteUtil.prepareFile("file1-" + System.currentTimeMillis());
            file2 = siteUtil.prepareFile("file2-" + System.currentTimeMillis());
            file3 = siteUtil.prepareFile("file3-" + System.currentTimeMillis());

            folderName1 = "folder1-" + System.currentTimeMillis();
            folderName2 = "folder2-" + System.currentTimeMillis();

            siteUtil.createSite(driver, username, password, siteName1, "description", "Public");
            siteUtil.createSite(driver, username, password, siteName2, "description", "Public");

            siteActions.navigateToDocumentLibrary(driver, siteName1);
            siteActions.uploadFile(driver, file1);
            siteActions.uploadFile(driver, file2);
            siteActions.uploadFile(driver, file3);

            siteActions.createFolder(driver, folderName1, "folder 1 title", "folder 1 description");
            siteActions.createFolder(driver, folderName2, "folder 2 title", "folder 2 description");

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file1.getName(), ACTION.CREATE_LINK);
            file1LinkName = "Link to " + file1.getName();

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file2.getName(), ACTION.CREATE_LINK);
            file2LinkName = "Link to " + file2.getName();

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", file3.getName(), ACTION.CREATE_LINK);
            file3LinkName = "Link to " + file3.getName();

            siteActions.copyOrMoveArtifact(driver, DESTINATION.ALL_SITES, siteName1, "", folderName1, ACTION.CREATE_LINK);
            folder1LinkName = "Link to " + folderName1;
        }
        catch (Exception pe)
        {
            saveScreenShot("CreateLinkFileUpload");
            logger.error("Cannot upload file to site ", pe);
            
        }
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName1);
        siteUtil.deleteSite(username, password, siteName2);
    }

    /**
     * Check that actions available on a link to a file are correct
     */
    @Test(priority = 1)
    public void testLinkActionsDocLib()
    {
//        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
//
//        siteActions.copyOrMoveArtifact(driver, factoryPage, CopyOrMoveContentPage.DESTINATION.ALL_SITES,
//                siteName1, "", file1.getName(), CopyOrMoveContentPage.ACTION.CREATE_LINK);
//
//        String linkName = "Link to " + file1.getName();
//
//        Assert.assertTrue(docLib.isFileVisible(linkName));
//        
//        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(linkName);
//
    }

    /**
     * Check that Locate Linked Item for a link to a file redirects to 
     * document library page where the original file is located
     */
    @Test(priority = 2)
    public void testLocateLinkedItemFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        docLib = linkRow.selectLocateLinkedItem().render();
        Assert.assertTrue(docLib.isFileVisible(file1.getName()));

        FileDirectoryInfo docRow = docLib.getFileDirectoryInfo(file1.getName());
        Assert.assertTrue(docRow.isCheckboxSelected(), "Element found, but not checked");
    }

    /**
     * Check that Locate Linked Item for a link to a folder redirects to
     * document library page where the original folder is located
     */
    @Test(priority = 3)
    public void testLocateLinkedItemFolder()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(folder1LinkName);

        docLib = linkRow.selectLocateLinkedItem().render();
        Assert.assertTrue(docLib.isFileVisible(folderName1));

        FileDirectoryInfo docRow = docLib.getFileDirectoryInfo(folderName1);
        Assert.assertTrue(docRow.isCheckboxSelected(), "Element found, but not checked");
    }

    /**
     * Check that Delete Link action for a link deletes the link
     */
    @Test(priority = 4)
    public void testDeleteLink()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        Assert.assertTrue(docLib.isFileVisible(file2LinkName));

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file2LinkName);
        docLib = linkRow.deleteLink().render();

        Assert.assertFalse(docLib.isFileVisible(file2LinkName));
    }

    /**
     * Check that clicking on a link to a file redirects to document Details page
     */
    @Test(priority = 5)
    public void testClickLinkToFile()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        DocumentDetailsPage docDetailsPage = docLib.getFileDirectoryInfo(file1LinkName).clickOnTitle().render();

        Assert.assertTrue(docDetailsPage.getDocumentTitle().equalsIgnoreCase(file1.getName()));
    }

    /**
     * Check that clicking on a link to a folder redirects to folder contents page
     */
    // enable this after SHA-1864 is fixed
    @Test(priority = 6, enabled = false)
    public void testClickLinkToFolder()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        docLib = siteActions.getFileDirectoryInfo(driver, folder1LinkName).clickOnTitle().render();

        String path = docLib.getNavigation().getCrumbsElementDetailsLinkName();

        Assert.assertTrue(path.equalsIgnoreCase(folderName1));
    }

    /**
     * Check that a link to a file cannot be liked
     */
    @Test(priority = 7)
    public void testLinkLike()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isLikeVisible(), "Like is visible for link!");
    }

    /**
     * Check that a link to a file cannot be favoured
     */
    @Test(priority = 8)
    public void testLinkFavorite()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isFavoriteVisible(), "Favorite is visible for link!");
    }

    /**
     * Check that a link to a file cannot be commented on
     */
    @Test(priority = 9)
    public void testLinkComment()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isCommentLinkPresent(), "Comment is visible for link!");
    }

    /**
     * Check that a link to a file cannot be shared
     */
    @Test(priority = 10)
    public void testLinkShare()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();

        FileDirectoryInfo linkRow = docLib.getFileDirectoryInfo(file1LinkName);

        Assert.assertFalse(linkRow.isShareLinkVisible(), "Share is visible for link!");
    }

    /**
     * Check that link is deleted when the original file is deleted
     */
    @Test(priority = 11)
    public void testLinkDeletedWhenOriginalDeleted()
    {
        docLib = siteActions.navigateToDocumentLibrary(driver, siteName1).render();
        Assert.assertTrue(docLib.isItemVisble(file3LinkName));

        FileDirectoryInfo fileRow = docLib.getFileDirectoryInfo(file3.getName());
        docLib = fileRow.delete().render();

        Assert.assertFalse(docLib.isItemVisble(file3LinkName));
    }
}
