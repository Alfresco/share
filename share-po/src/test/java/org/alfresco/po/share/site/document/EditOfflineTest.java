/*

 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.StringTokenizer;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify File Directory info methods are operating correctly.
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
@Listeners(FailedTestListener.class)
public class EditOfflineTest extends AbstractDocumentTest
{
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File testFile;
    private String fileName;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "Site" + System.currentTimeMillis();
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
        testFile = SiteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(testFile.getName(), ".");
        fileName = st.nextToken();
        File file = SiteUtil.prepareFile();
        fileName = file.getName();
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage upLoadPage = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
    }

    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="alfresco-one")
    public void selectEditOffline() throws Exception
    {
        assertFalse(documentLibPage.getFileDirectoryInfo(fileName).isEdited());
        documentLibPage = documentLibPage.getFileDirectoryInfo(fileName).selectEditOffline().render();
    }
    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="alfresco-one", dependsOnMethods = "selectEditOffline")
    public void isEdited() throws Exception
    {
        assertTrue(documentLibPage.getFileDirectoryInfo(fileName).isEdited());
    }

    /**
     * Test updating an existing file with a new uploaded file. The test covers major and minor version changes
     *
     * @throws Exception
     */
    @Test(groups="alfresco-one", dependsOnMethods = "isEdited")
    public void selectCancelEditing() throws Exception
    {
        documentLibPage = documentLibPage.getFileDirectoryInfo(fileName).selectCancelEditing().render();
        assertFalse(documentLibPage.getFileDirectoryInfo(fileName).isEdited());
    }
}