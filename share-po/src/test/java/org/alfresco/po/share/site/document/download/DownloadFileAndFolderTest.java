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
package org.alfresco.po.share.site.document.download;


import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Meenal Bhave
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups="download")
public class DownloadFileAndFolderTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String folderName;
    private static String folderDescription;
    private static DocumentLibraryPage documentLibraryPage;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        SitePage page = resolvePage(driver).render();
        documentLibraryPage = page.getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
        documentLibraryPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
    }

    @AfterClass
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    @Test
    public void downloadTextFile() throws Exception
    {
        CreatePlainTextContentPage contentPage = documentLibraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails details = new ContentDetails();
        details.setName("TextFile");
        DocumentDetailsPage detailsPage = contentPage.create(details).render();
        documentLibraryPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
        FileDirectoryInfo row = documentLibraryPage.getFileDirectoryInfo("TextFile");
        row.selectDownload();
        documentLibraryPage.waitForFile(downloadDirectory + "TextFile");
        documentLibraryPage.render();
    }

    @Test(dependsOnMethods="downloadTextFile")
    public void uploadFile() throws Exception
    {
        documentLibraryPage.getFileDirectoryInfo(folderName).selectDownloadFolderAsZip();
        documentLibraryPage.waitForFile(downloadDirectory + folderName + ".zip");
        documentLibraryPage.render();
    }
    
}
