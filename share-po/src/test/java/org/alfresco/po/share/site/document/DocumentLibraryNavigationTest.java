/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.enums.ViewType;
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
public class DocumentLibraryNavigationTest extends AbstractTest
{
    private static String siteName;
    private static DocumentLibraryPage documentLibPage;
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

        loginAs(username, password);

        SiteUtil.createSite(drone, siteName, "Public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        // uploading new files.
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
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
    public void testNavigateToAudioView() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectAudioView().render();

        assertTrue(documentLibPage.getViewType().equals(ViewType.AUDIO_VIEW), documentLibPage.getViewType() + " was selected");
    }

    @Test(enabled = true, priority = 2)
    public void testNavigateToMediaView() throws Exception
    {
        documentLibPage = documentLibPage.getNavigation().selectMediaView().render();

        assertTrue(documentLibPage.getViewType().equals(ViewType.MEDIA_VIEW), documentLibPage.getViewType() + " was selected");
    }

}