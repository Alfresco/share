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
//package org.alfresco.webdrone.share.site.document;
//
//import org.alfresco.webdrone.WebDroneUtil;
//import org.alfresco.webdrone.share.AbstractTest;
//import org.alfresco.webdrone.share.site.NewFolderPage;
//import org.alfresco.webdrone.util.SiteUtil;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify document library page is operating correctly.
// * 
// * @author Michael Suzuki
// * @since 1.0
// */
//public class DocumentLibraryPaginationTest extends AbstractTest
//{
//    private static String siteName;
//
//    /**
//     * Pre test setup of a dummy file to upload.
//     * 
//     * @throws Exception
//     */
//    @BeforeTest
//    public void startWebDrone() throws Exception
//    {
//        setup();
//    }
//
//    @AfterTest(alwaysRun = true)
//    public void quit()
//    {
//        tearDown();
//    }
//    
//    @BeforeClass
//    public void prepare()throws Exception
//    {
//        siteName = "site" + System.currentTimeMillis();
//        SiteUtil.createSite(siteName, "description", drone.getAlfrescoVersion().isCloud(), shareUrl);
//        int MAX_COUNT = 51;
//
//        DocumentLibraryPage documentLibPage = DocumentLibraryUtil.getDocumentLibraryPage(drone, shareUrl, siteName);
//        for(int i = 0; i <= MAX_COUNT; i ++)
//        {
//            NewFolderPage newFolderPage = documentLibPage.getNavigation().createNewFolder();
//            documentLibPage = newFolderPage.createNewFolder(String.format("test-%d",i)).render();
//        }
//        WebDroneUtil.logout(drone);
//    }
//
//    @AfterClass
//    public void deleteSite()
//    {
//        SiteUtil.deleteSite(siteName, drone.getAlfrescoVersion().isCloud(), shareUrl);
//    }
//
//    @Test
//    public void pagination()
//    {
//        DocumentLibraryPage documentLibraryPage = DocumentLibraryUtil.getDocumentLibraryPage(drone, shareUrl, siteName);
//        Assert.assertTrue(documentLibraryPage.hasNextPage());
//        Assert.assertFalse(documentLibraryPage.hasPrevioudPage());
//
//        documentLibraryPage.selectNextPage();
//        Assert.assertFalse(documentLibraryPage.hasNextPage());
//        Assert.assertTrue(documentLibraryPage.hasPrevioudPage());
//
//        documentLibraryPage.selectPreviousPage();
//        Assert.assertTrue(documentLibraryPage.hasNextPage());
//        Assert.assertFalse(documentLibraryPage.hasPrevioudPage());
//    }
//    
//
//}
