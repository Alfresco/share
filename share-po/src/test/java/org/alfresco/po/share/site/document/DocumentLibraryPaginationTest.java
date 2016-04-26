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
