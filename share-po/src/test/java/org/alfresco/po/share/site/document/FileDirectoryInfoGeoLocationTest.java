/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
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
 * @since 5.0
 */
@Listeners(FailedTestListener.class)
public class FileDirectoryInfoGeoLocationTest extends AbstractDocumentTest
{

    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private static DocumentDetailsPage detailsPage;
    private File testFile;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="Enterprise4.2")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        loginAs(username, password);

        SiteUtil.createSite(drone, siteName, "description", "Public");
        testFile = SiteUtil.prepareFile("testFile");
    }



    @AfterClass(groups="Enterprise4.2")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="Enterprise4.2")
    public void createData() throws Exception
    {
        documentLibPage = openSiteDocumentLibraryFromSearch(drone, siteName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(testFile.getCanonicalPath()).render();
    }



    @Test(dependsOnMethods = "createData", groups = { "Enterprise4.2" })
    public void isIconDisplayedFalse()
    {
        documentLibPage = documentLibPage.getSiteNav().selectSiteDocumentLibrary().render();
        
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectSimpleView().render();
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectGalleryView().render();
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectTableView().render();
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertFalse(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();

    }

    @Test(dependsOnMethods = "isIconDisplayedFalse", groups = { "Enterprise4.2" })
    public void isIconDisplayedTrue()
    {
        detailsPage = documentLibPage.selectFile(testFile.getName()).render();

        SelectAspectsPage selectAspectsPage = detailsPage.selectManageAspects().render();

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.GEOGRAPHIC);
        aspectsToAdd.add(DocumentAspect.EXIF);

        selectAspectsPage = selectAspectsPage.add(aspectsToAdd).render();
        detailsPage = selectAspectsPage.clickApplyChanges().render();

        documentLibPage = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();

        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectSimpleView().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectGalleryView().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectFilmstripView().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectTableView().render();
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isGeoLocationIconDisplayed());
        Assert.assertTrue(documentLibPage.getFileDirectoryInfo(testFile.getName()).isEXIFIconDisplayed());

        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
    }
}
