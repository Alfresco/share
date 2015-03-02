/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test to verify document library page tag operations are operating
 * correctly.
 * 
 * @author Cbairaajoni
 * @version 1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups={"Enterprise-only","TestBug"})
public class DocumentLibraryPageTagTest extends AbstractTest
{
    private static String siteName;
    private static String folderName1, folderName2;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibPage;
    private File file;
    List<String> tags = new LinkedList<String>();
    SitePage page = null;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();

        tags.add("1234567890");

        folderName1 = "The first folder";
        folderName2 = folderName1 + "-1";
        folderDescription = String.format("Description of %s", folderName1);
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        file = SiteUtil.prepareFile();

        page = drone.getCurrentPage().render();
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName1, folderDescription).render();
        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName2, folderDescription).render();

        // uploading new file.
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
    }

    @AfterClass
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    //Until WD-71 is fixed nonCloud tag will remain as it fails.
    @Test
    public void testEditingTagAndCancelChanges() throws Exception
    {
        List<FileDirectoryInfo> results = documentLibPage.render().getFiles();

        int i = 0;
        for (FileDirectoryInfo content : results)
        {
            if (!content.isTypeFolder())
            {
                Assert.assertTrue(content.getContentEditInfo().contains("Created just now by "));

                // Get Node reference
                Assert.assertNotNull(content.getContentNodeRef(), "Node Reference is null");

                // Tag
                Assert.assertFalse(content.hasTags());

        content.addTag(tags.get(0));
        
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        content = documentLibPage.getFiles().get(i);

                List<String> contentTags = content.getTags();
                Assert.assertEquals(contentTags.size(), tags.size());

                for (String tagName : contentTags)
                {
                    content.clickOnAddTag();
                    Assert.assertTrue(content.removeTagButtonIsDisplayed(tagName));
                    content.clickOnTagCancelButton();
                }
            }
            i++;
        }
    }
    //Until WD-71 is fixed nonCloud tag will remain as it fails.
    @Test( dependsOnMethods = "testEditingTagAndCancelChanges")
    public void testGetAllTags() throws Exception
    {
        List<String> listOfTagNames = documentLibPage.getAllTagNames();

        Assert.assertNotNull(listOfTagNames);
        Assert.assertEquals(listOfTagNames.size(), tags.size());

        for (String tagName : tags)
        {
            Assert.assertTrue(listOfTagNames.contains(tagName));
        }
    }

   // TODO ALF-19161 Once Defect fixed enable below test
/*    @Test(dependsOnMethods = "testGetAllTags")
    public void testClickOnTagNameUnderFolder() throws Exception
    {
        try
        {
            tags.add("testTag");

            FileDirectoryInfo content = documentLibPage.getFiles().get(0);
            documentLibPage = content.addTag(content.getName(), tags.get(2));
            content = documentLibPage.getFiles().get(1);
            documentLibPage = content.addTag(content.getName(), tags.get(2));

            documentLibPage = documentLibPage.getShareContentRow(folderName1).clickOnTagNameLink(tags.get(2)).render();
            List<FileDirectoryInfo> results = documentLibPage.getFiles();
            Assert.assertEquals(results.size(), 2);

            String name = null;

            for (FileDirectoryInfo folder : results)
            {
                name = folder.getName();
                Assert.assertTrue(name.equalsIgnoreCase(folderName1) || name.equalsIgnoreCase(folderName2));
            }
        }
        catch (Throwable e)
        {
            saveScreenShot("DocumentLibraryPageTagTest.testClickOnTagNameUnderFolder");
            throw new Exception("Failed DocumentLibraryPageTagTest", e);
        }
    }

    @Test(dependsOnMethods = "testGetAllTags")
    public void testClickOnTagNameUnderTagsTreeMenuOnDocumentLibrary() throws Exception
    {
        try
        {
            documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
            documentLibPage = documentLibPage.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(tags.get(2)).render();

            List<FileDirectoryInfo> folders = documentLibPage.getFiles();
            Assert.assertEquals(folders.size(), 2);

            String name = null;

            for (FileDirectoryInfo folder : folders)
            {
                name = folder.getName();
                Assert.assertTrue(name.equalsIgnoreCase(folderName1) || name.equalsIgnoreCase(folderName2));
            }
        }
        catch (Throwable e)
        {
            saveScreenShot("DocumentLibraryPageTagTest.testClickOnTagNameUnderTagsTreeMenuOnDocumentLibrary");
            throw new Exception("Failed DocumentLibraryPageTagTest", e);
        }
    }
*/
  //Until WD-71 is fixed nonCloud tag will remain as it fails.
    @Test( dependsOnMethods = "testGetAllTags")
    public void testEditingTagAndSaveChanges() throws Exception
    {
        documentLibPage = page.getSiteNav().selectSiteDocumentLibrary().render();
        List<FileDirectoryInfo> results = documentLibPage.render().getFiles();

        for (FileDirectoryInfo content : results)
        {
            if (!content.isTypeFolder())
            {
                List<String> contentTags = content.getTags();
                Assert.assertEquals(contentTags.size(), tags.size());

                for (String tagName : contentTags)
                {
                    content.clickOnAddTag();
                    content.clickOnTagRemoveButton(tagName);
                    content.clickOnTagSaveButton();
                }
            }
        }
        
        documentLibPage = (DocumentLibraryPage) documentLibPage.getSiteNav().selectSiteDocumentLibrary();
        documentLibPage.render();
        
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(file.getName()).hasTags());
    }
    
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testClickOnNullTagName()
    {
        DocumentLibraryPage docLib = new DocumentLibraryPage(drone);
        Assert.assertNotNull(docLib.clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(null));

    }
    
}
