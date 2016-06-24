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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.StringTokenizer;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;

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
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        testFile = siteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(testFile.getName(), ".");
        fileName = st.nextToken();
        File file = siteUtil.prepareFile();
        fileName = file.getName();
        SitePage site = resolvePage(driver).render();
        documentLibPage = site.getSiteNav().selectDocumentLibrary().render();

        UploadFilePage upLoadPage = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        documentLibPage = documentLibPage.getNavigation().selectDetailedView().render();
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
        FileDirectoryInfo row = documentLibPage.getFileDirectoryInfo(fileName);
        assertTrue(row.isEdited());
        DocumentDetailsPage detailsPage = row.clickOnTitle().render();
        assertTrue(detailsPage.isEditOfflineDisplayed());
        detailsPage.getSiteNav().selectDocumentLibrary().render();
        
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
        FileDirectoryInfo row = documentLibPage.getFileDirectoryInfo(fileName);
        DocumentDetailsPage detailsPage = row.clickOnTitle().render();
        assertFalse(detailsPage.isEditOfflineDisplayed());
        detailsPage.getSiteNav().selectDocumentLibrary().render();
    }
}
