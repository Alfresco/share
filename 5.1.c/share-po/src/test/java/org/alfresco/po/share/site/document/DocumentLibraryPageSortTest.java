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

import java.io.File;
import java.util.List;

import org.alfresco.po.AbstractTest;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test to verify document library page sort operations are operating
 * correctly.
 * 
 * @author Jallison
 * @since 2.1.1
 */
@Listeners(FailedTestListener.class)
@Test
public class DocumentLibraryPageSortTest extends AbstractTest
{
    private static String siteName;
    private static String fileName1;
    private static String fileName2;
    private static DocumentLibraryPage documentLibPage;
    private File file1;
    private File file2;
    SitePage page = null;

    /**
     * Pre-test setup of a dummy files to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();

        fileName1 = "aaaaaa";
        fileName2 = "bbbbbb";

        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        
        file1 = siteUtil.prepareFile(fileName1);
        fileName1 = file1.getName();
        file2 = siteUtil.prepareFile(fileName2);
        fileName2 = file2.getName();

        page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();

        // uploading new files.
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();
   }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(groups="alfresco-one")
    public void selectSortFieldFromDropDown()
    {
        documentLibPage = documentLibPage.getNavigation().selectSortFieldFromDropDown(SortField.POPULARITY).render();
        SortField sortField = documentLibPage.getNavigation().getCurrentSortField();
        Assert.assertEquals(sortField, SortField.POPULARITY); 

        documentLibPage = documentLibPage.getNavigation().selectSortFieldFromDropDown(SortField.CREATED).render();
        sortField = documentLibPage.getNavigation().getCurrentSortField();
        Assert.assertEquals(sortField, SortField.CREATED); 

        documentLibPage = documentLibPage.getNavigation().selectSortFieldFromDropDown(SortField.MIMETYPE).render();
        sortField = documentLibPage.getNavigation().getCurrentSortField();
        Assert.assertEquals(sortField, SortField.MIMETYPE); 

        documentLibPage = documentLibPage.getNavigation().selectSortFieldFromDropDown(SortField.NAME).render();
        sortField = documentLibPage.getNavigation().getCurrentSortField();
        Assert.assertEquals(sortField, SortField.NAME);

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.get(0).getName(), fileName1);
        Assert.assertEquals(files.get(1).getName(), fileName2);
    }
    
    @Test(dependsOnMethods = "selectSortFieldFromDropDown", groups="alfresco-one")
    public void sortDescending()
    {
        documentLibPage = documentLibPage.getNavigation().selectSortFieldFromDropDown(SortField.NAME).render();

        documentLibPage = documentLibPage.getNavigation().sortAscending(false).render();
        Assert.assertFalse(documentLibPage.getNavigation().isSortAscending());

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.get(0).getName(), fileName2);
        Assert.assertEquals(files.get(1).getName(), fileName1);
    }
    
    @Test(dependsOnMethods = "sortDescending", groups="alfresco-one")
    public void sortAscending()
    {
        documentLibPage = documentLibPage.getNavigation().selectSortFieldFromDropDown(SortField.NAME).render();

        documentLibPage = documentLibPage.getNavigation().sortAscending(true).render();
        Assert.assertTrue(documentLibPage.getNavigation().isSortAscending());

        List<FileDirectoryInfo> files = documentLibPage.getFiles();
        Assert.assertEquals(files.get(0).getName(), fileName1);
        Assert.assertEquals(files.get(1).getName(), fileName2);
    }
}
