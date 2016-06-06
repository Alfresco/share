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
//package org.alfresco.po.share.site.document;
//
//import java.io.File;
//import java.util.StringTokenizer;
//
//import org.alfresco.po.share.site.SitePage;
//import org.alfresco.po.share.site.UpdateFilePage;
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
// * Integration test to verify that  webdriver is able to 
// * manage the edit off line document details page. 
// * 
// * @author Michael Suzuki
// * @since 1.0
// */
//@Listeners(FailedTestListener.class)
//@Test(groups="Enterprise-only")
//public class EditOffLineDocumentPageTest extends AbstractDocumentTest
//{
//    private String siteName;
//    private File file;
//    private String fileName;
//
//    /**
//     * Pre test setup of a dummy file to upload.
//     * 
//     * @throws Exception
//     */
//    @BeforeClass
//    public void setup() throws Exception
//    {
//        // getWebDriver();
//        siteName = "editDocumentSiteTest" + System.currentTimeMillis();
//        loginAs(username, password);
//        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
//        file = siteUtil.prepareFile();
//        StringTokenizer st = new StringTokenizer(file.getName(), ".");
//        fileName = st.nextToken();
//        File file = siteUtil.prepareFile();
//        fileName = file.getName();
//        SitePage site = resolvePage(driver).render();
//        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
//        // DocumentLibraryPage docPage =
//        // getDocumentLibraryPage(siteName).render();
//        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
//        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
//        docPage.selectFile(fileName);
//    }
//    
//    @AfterClass
//    public void teardown()
//    {
//         siteUtil.deleteSite(username, password,siteName);
//         closeWebDriver();
//    }
//    
//   
//    @Test
//    public void documentEditOffLineAndCancel() throws Exception  
//    {
//        try
//        {
//            //Check if edit off line has checked out the file
//            DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
//            Assert.assertFalse(docDetailsPage.isCheckedOut());
//            Assert.assertTrue(docDetailsPage.isEditOfflineLinkDisplayed());
//            
//            DocumentEditOfflinePage editOfflinePage = docDetailsPage.selectEditOffLine(null).render();
//            //TODO remove below hack to wait for file to download as webdriver is not able to determine os function
//            try{ Thread.sleep(8000L);} catch (InterruptedException ine) { }
//            Assert.assertTrue(editOfflinePage.isCheckedOut());
//            Assert.assertFalse(docDetailsPage.isEditOfflineLinkDisplayed());
//            
//            //Check if edit off line has checked out the file
//            //Test the cancel operation that should release the checked out hold
//            docDetailsPage = editOfflinePage.selectCancelEditing().render();
//            Assert.assertTrue(docDetailsPage.isDocumentDetailsPage());
//            Assert.assertFalse(docDetailsPage.isCheckedOut());
//            
//        }
//        catch (Exception e)
//        {
//            StringBuffer bf = new StringBuffer(e.getMessage());
//            saveScreenShot("DocumentDetailsPageTest.documentEditOffLineAndCancel");
//            try
//            {
//                DocumentEditOfflinePage editOfflinePage = resolvePage(driver).render();
//                editOfflinePage.selectCancelEditing().render();
//            }
//            catch (Exception ex) 
//            {
//                bf.append("Error reverting back to DocumentDetails mode from Edit offline");
//                bf.append(ex.getMessage());
//            }
//            throw new Exception(String.format("test failed: %s", bf.toString()));
//        }
//    }
//
//    
//    @Test(dependsOnMethods = "documentEditOffLineAndCancel")
//    public void editOffLine() throws Exception  
//    {
//        DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
//        DocumentEditOfflinePage editOfflinePage = docDetailsPage.selectEditOffLine(null).render();
//        // TODO remove below hack to wait for file to download as webdriver is
//        // not able to determine os function
//        try
//        {
//            Thread.sleep(8000L);
//        }
//        catch (InterruptedException ine)
//        {
//        }
//        Assert.assertTrue(editOfflinePage.isCheckedOut());
//        // Upload new file
//        UpdateFilePage updatePage = editOfflinePage.selectUploadNewVersion().render();
//        updatePage.selectMajorVersionChange();
//        updatePage.setComment("Edit off line loading the final image");
//        updatePage.uploadFile(file.getCanonicalPath());
//        docDetailsPage = updatePage.submit().render();
//
//        Assert.assertTrue(docDetailsPage.isDocumentDetailsPage());
//        Assert.assertFalse(docDetailsPage.isCheckedOut());
//        Assert.assertEquals(docDetailsPage.getDocumentVersion(), "2.0");
//    }
//    
//    
//    @Test(dependsOnMethods = "editOffLine")
//    public void testClickOnViewOriginalDocument() throws Exception
//    {
//    	DocumentDetailsPage docDetailsPage = resolvePage(driver).render();
//        DocumentEditOfflinePage editOfflinePage = docDetailsPage.selectEditOffLine(null).render();
//        Assert.assertTrue(editOfflinePage.isCheckedOut());
//        //Assert.assertTrue(docDetailsPage.isEditOfflineLinkDisplayed());
//        //TODO remove below hack to wait for file to download as webdriver is not able to determine os function
//        
//        try{ Thread.sleep(8000L);} catch (InterruptedException ine) { }
//        
//        editOfflinePage.selectViewOriginalDocument().render();    
//        
//        editOfflinePage.selectViewWorkingCopy().render();
//        editOfflinePage.selectCancelEditing().render();
//        Assert.assertNotNull(docDetailsPage);  
//        Assert.assertFalse(docDetailsPage.isCheckedOut());
//
//    }
//   
//}
