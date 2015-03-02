/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import java.io.File;
import java.io.IOException;

import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
@Test(groups="alfresco-one")
@Listeners(FailedTestListener.class)
public class DocumentLibraryPageWithLotsOfFilesTest extends AbstractDocumentTest
{
    private Log logger = LogFactory.getLog(DocumentLibraryPageWithLotsOfFilesTest.class);
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private String fileName;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SiteDashboardPage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        file = SiteUtil.prepareFile();
        uploadForm.uploadFile(file.getCanonicalPath());
        countCheck(1);
        fileName = file.getName();

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder("michaels-test").render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolder("mikes-test").render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolder("last-test").render();
    }
    
    /**
     * Pauses the test upload process to make sure all files are uploaded.
     * @param count int number of expected file in collection
     * @return true count match
     */
    private boolean countCheck(int count)
    {
        RenderTime timer = new RenderTime(drone.getDefaultWaitTime());
        while(true)
        {
        	timer.start();
            synchronized (this)
            {
                try{ this.wait(100L); } catch (InterruptedException e) {}
            }
            try
            {
            	documentLibPage.render();
                logger.info(String.format("count check %d and actual %d", count, documentLibPage.getFiles().size()));
                if(count == documentLibPage.getFiles().size())
                {
                    return true;
                }
            }
            catch (StaleElementReferenceException ste){ }
            finally
            {
            	timer.end();
            }
        }
    }
        
    @AfterClass(groups="alfresco-one")
    public void teardown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }


    @Test
    public void selectFile() throws IOException
    {
    	DocumentDetailsPage detailsPage = documentLibPage.selectFile(fileName).render();
    	Assert.assertNotNull(detailsPage);
    	Assert.assertEquals(fileName, detailsPage.getDocumentTitle());
    }
    @Test(dependsOnMethods="selectFile")
    public void removeAllFilesAndFolder()
    {
        DocumentDetailsPage detailsPage = drone.getCurrentPage().render();
        DocumentLibraryPage lib = detailsPage.getSiteNav().selectSiteDocumentLibrary().render();
        lib = lib.getNavigation().selectDetailedView().render();
        Assert.assertTrue(lib.hasFiles());
        lib = lib.deleteItem(1).render();
        lib = lib.deleteItem(2).render();
        lib = lib.getFileDirectoryInfo(1).delete().render();
        lib = lib.deleteItem(fileName).render();
        Assert.assertNotNull(lib);
        Assert.assertFalse(lib.hasFiles());
    }
}
