//package org.alfresco.po.share.site.document;
//
//import java.io.File;
//import java.util.StringTokenizer;
//
//import org.alfresco.po.share.DashBoardPage;
//import org.alfresco.po.share.SharePage;
//import org.alfresco.po.share.site.CreateSitePage;
//import org.alfresco.po.share.site.SiteDashboardPage;
//import org.alfresco.po.share.site.UploadFilePage;
//
//import org.alfresco.po.util.FailedTestListener;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Listeners;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify Tag page operations.
// * 
// * @author Charu
// * @since 1.0
// */
//@Listeners(FailedTestListener.class)
//public class TagPageTest extends AbstractDocumentTest
//{
//    private String siteName;
//    private String fileName;
//    private File file;
//    private String tagName1;
//    private String tagName2;
//    DashBoardPage dashBoard;
//    SiteDashboardPage site;
//    TagPage tagPage =  null;
//    EditDocumentPropertiesPage editPage = null;
//
//    @AfterClass(alwaysRun = true)	
//    public void quit()
//    {
//        if (site != null)
//        {
//            siteUtil.deleteSite(username, password, siteName);
//        }
//        closeWebDriver();
//    }
//    
//    /**
//     * Pre test setup of a dummy file to upload.
//     * 
//     * @throws Exception
//     */
//    @BeforeClass(groups="Enterprise-only")
//    public void prepare()throws Exception
//    {
//        siteName = "TagPageTest" + System.currentTimeMillis();
//        file = siteUtil.prepareFile();
//        StringTokenizer st = new StringTokenizer(file.getName(), ".");
//        fileName = st.nextToken();
//        tagName1 = siteName;
//
//        File file = siteUtil.prepareFile();
//        fileName = file.getName();
//        loginAs(username, password);
//        SharePage page = resolvePage(driver).render();
//        dashBoard = page.getNav().selectMyDashBoard().render();
//        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
//        site = createSite.createNewSite(siteName).render();
//        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
//        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
//        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
//        DocumentDetailsPage detailsPage = docPage.selectFile(fileName);
//        editPage = detailsPage.selectEditProperties().render();
//        tagPage = editPage.getTag().render();
//    }
//    
//   @Test(groups="Enterprise-only")
//    public void addTagTest() throws Exception
//    {
//       tagPage = tagPage.enterTagValue(tagName1).render();
//       editPage = tagPage.clickOkButton().render();
//       Assert.assertTrue(editPage.hasTags());
//    }
//   
//   @Test(dependsOnMethods = "addTagTest", groups="Enterprise-only")
//   public void clickRemoveTagTest() throws Exception
//   {
//      tagName2 = siteName + "2";
//      tagPage = editPage.getTag().render();
//      tagPage = tagPage.enterTagValue(tagName2).render();
//      editPage = tagPage.clickCancelButton().render();
//      Assert.assertTrue(editPage.hasTags());
//      tagPage = editPage.getTag().render();
//      editPage = tagPage.RemoveTagValue(tagName1).render();
//      tagPage = editPage.getTag().render();
//      editPage = tagPage.RemoveTagValue(tagName2).render();
//      Assert.assertFalse(editPage.hasTags());
//   }
//   
//   @Test(dependsOnMethods = "clickRemoveTagTest", groups="Enterprise-only", expectedExceptions = IllegalArgumentException.class)
//   public void testRemoveTagWithNull()
//   {
//       tagPage = editPage.getTag().render();
//       tagPage.RemoveTagValue(null);
//   }
//   
//   @Test(dependsOnMethods = "testRemoveTagWithNull", groups="Enterprise-only", expectedExceptions = IllegalArgumentException.class)
//   public void testRemoveTagWithEmpty()
//   {
//       tagPage = editPage.getTag().render();
//       tagPage.RemoveTagValue("");
//   }
//}
