/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test to verify document library page tag operations are operating correctly.
 * 
 * @author Abhijeet Bharade
 * @version 2.1
 */
@Listeners(FailedTestListener.class)
@Test(groups="Enterprise-only")
public class DocumentLibraryPageFilmstripTest extends AbstractTest
{
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File file2;
    private File file1;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();

        file1 = SiteUtil.prepareFile(siteName + "1");
        file2 = SiteUtil.prepareFile(siteName + "2");

        loginAs(username, password);

        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        // uploading new files.
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file2.getCanonicalPath()).render();

        uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file1.getCanonicalPath()).render();
    }

    @AfterClass
    public void teardown()
    {
        SiteFinderPage siteFinder = SiteUtil.searchSite(drone, siteName).render();
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        DocumentLibraryPage docPage = siteDash.getSiteNav().selectSiteDocumentLibrary().render();
        docPage.getNavigation().selectDetailedView();

        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(enabled = true, priority=1)
    public void testNavigateToFilmstripView() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();

        assertTrue(documentLibPage.getFilmstripActions().isFilmStripViewDisplayed());
        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file1.getName());
    }

    @Test(enabled = true, priority=2)
    public void testSelectNextPreviousFilmstripItem() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();

        assertTrue(documentLibPage.getFilmstripActions().isNextFilmstripArrowPresent());
        assertFalse(documentLibPage.getFilmstripActions().isPreviousFilmstripArrowPresent());

        documentLibPage.getFilmstripActions().selectNextFilmstripItem().render();

        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file2.getName());
        assertFalse(documentLibPage.getFilmstripActions().isNextFilmstripArrowPresent());
        assertTrue(documentLibPage.getFilmstripActions().isPreviousFilmstripArrowPresent());

        documentLibPage.getFilmstripActions().selectPreviousFilmstripItem().render();

        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file1.getName());
        assertTrue(documentLibPage.getFilmstripActions().isNextFilmstripArrowPresent());
        assertFalse(documentLibPage.getFilmstripActions().isPreviousFilmstripArrowPresent());
    }

    @Test(priority = 10)
    public void testArrowsOnTape() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();

        for (int i = 3; i < 5; i++)
        {
            File file = SiteUtil.prepareFile(siteName + i);
            UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
            documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        }

        for (int i = 0; i < 2; i++)
        {
            documentLibPage = documentLibPage.getNavigation().selectAll().render();

            // Select Copy To
            CopyOrMoveContentPage copyContent = documentLibPage.getNavigation().render().selectCopyTo().render();

            // Keep the selected Destination: Current Site > DocumentLibrary Folder
            documentLibPage = copyContent.selectOkButton().render();
        }

        assertTrue(documentLibPage.getFilmstripActions().isNextFilmstripTapeArrowPresent());
        assertFalse(documentLibPage.getFilmstripActions().isPreviousFilmstripTapeArrowPresent());

        documentLibPage = documentLibPage.getFilmstripActions().selectNextFilmstripTape().render();
        documentLibPage = documentLibPage.getFilmstripActions().selectPreviousFilmstripTape().render();

        assertTrue(documentLibPage.getFilmstripActions().isNextFilmstripTapeArrowPresent());
        assertFalse(documentLibPage.getFilmstripActions().isPreviousFilmstripTapeArrowPresent());
    }

    @Test(priority = 3)
    public void testGetSelectedFIlesForFilmstrip() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();
        // documentLibPage = documentLibPage.getNavigation().selectDesellectAll().render();
        assertEquals(documentLibPage.getFilmstripActions().getSelectedFIlesForFilmstrip().size(), 0);

        FileDirectoryInfo fileInfo = documentLibPage.getFileDirectoryInfo(file1.getName());
        fileInfo.selectCheckbox();
        List<String> selectFiles = documentLibPage.getFilmstripActions().getSelectedFIlesForFilmstrip();
        assertTrue(selectFiles.contains(file1.getName()), selectFiles.toString());
        assertFalse(selectFiles.contains(file2.getName()), selectFiles.toString());
    }

    @Test(priority = 5)
    public void testToggleNavHandleForFilmstrip() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();
        boolean isTapeDiaplyed = documentLibPage.getFilmstripActions().isFilmstripTapeDisplpayed();
        documentLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();
        assertEquals(documentLibPage.getFilmstripActions().isFilmstripTapeDisplpayed(), !isTapeDiaplyed);
        documentLibPage.getFilmstripActions().toggleNavHandleForFilmstrip();
        assertEquals(documentLibPage.getFilmstripActions().isFilmstripTapeDisplpayed(), isTapeDiaplyed);
    }

    // Looks like a bug
    // https://issues.alfresco.com/jira/browse/MNT-10621
    @Test(enabled = true, priority = 4)
    public void testSendKeysForFilmstrip() throws Exception
    {

        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();

        documentLibPage.getFilmstripActions().sendKeyRightArrowForFilmstrip().render();

        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file2.getName());

        documentLibPage.getFilmstripActions().sendKeyLeftArrowForFilmstrip().render();

        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file1.getName());

        documentLibPage.getFilmstripActions().sendKeyDownArrowForFilmstrip().render();

        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file2.getName());

        documentLibPage.getFilmstripActions().sendKeyUpArrowForFilmstrip().render();

        assertEquals(documentLibPage.getFilmstripActions().getDisplyedFilmstripItem(), file1.getName());

    }
}