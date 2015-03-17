/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */

package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document CRUD is operating correctly.
 * 
 * @author Subashni Prasanna
 * @author Chiran
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "NonGrid" })
public class EditInGoogleDocsPageTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String fileName;
    private static String documentVersion;
    SiteDashboardPage site;
    DocumentDetailsPage detailsPage;
    EditInGoogleDocsPage googleDocsPage;
    
    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "editInGoogleDocsTest" + System.currentTimeMillis();
        fileName = siteName;
        drone.deleteCookies();
        loginAs(username, password);
        SiteUtil.createSite(drone, siteName, "description", "Public");
    }

    @AfterClass
    public void deleteSite() throws Exception
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void googleDocsAuthorisationCancel() throws Exception
    {
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        GoogleDocsAuthorisation googleAuth = docPage.getNavigation().selectCreateContent(ContentType.GOOGLEDOCS).render();
        Assert.assertTrue(googleAuth.isAuthorisationDisplayed());
        docPage = (DocumentLibraryPage) googleAuth.cancel();
        Assert.assertTrue(docPage.isDocumentLibrary());
    }

    @Test(dependsOnMethods = "googleDocsAuthorisationCancel")
    public void createGoogleDocsContentTest() throws Exception
    {
        String newName = fileName + ".docx";
        SitePage site = drone.getCurrentPage().render();
        DocumentLibraryPage docPage = site.getSiteNav().selectSiteDocumentLibrary().render();
        GoogleDocsAuthorisation googleAuth = docPage.getNavigation().selectCreateContent(ContentType.GOOGLEDOCS).render();
        Assert.assertTrue(googleAuth.isAuthorisationDisplayed());
        GoogleSignUpPage signUpPage = googleAuth.submitAuth().render();
        Assert.assertTrue(signUpPage.isSignupWindowDisplayed());
        EditInGoogleDocsPage googleDocsPage = signUpPage.signUp(googleusername, googlepassword).render();
        GoogleDocsRenamePage renameDocs = googleDocsPage.renameDocumentTitle().render();
        googleDocsPage = renameDocs.updateDocumentName(fileName).render();
        docPage = googleDocsPage.selectSaveToAlfresco().render();
        List<FileDirectoryInfo> files = docPage.getFiles();
        FileDirectoryInfo file = files.get(0);
        Assert.assertEquals(files.size(), 1);
        Assert.assertEquals(file.getName(), newName);
        detailsPage = docPage.selectFile(newName).render();

    }

    @Test(dependsOnMethods = "editInGoogleDocsDiscardCancelTest")
    public void editInGoogleDocsDiscardOkTest() throws Exception
    {
        //EditInGoogleDocsPage googleDocsPage = drone.getCurrentPage().render();
        //Assert.assertTrue(googleDocsPage.isDiscardChangesVisible());
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render();
        DocumentDetailsPage backToDetailsPage = googleDocsDiscardChanges.clickOkButton().render();
        Assert.assertTrue(backToDetailsPage.isDocumentDetailsPage());
    }

    @Test(dependsOnMethods = "editInGoogleDocsAndBack")
    public void editInGoogleDocsDiscardCancelTest() throws Exception
    {
        googleDocsPage = detailsPage.resumeEditInGoogleDocs().render();
        googleDocsPage.render();
        Assert.assertTrue(googleDocsPage.isDiscardChangesVisible());
        GoogleDocsDiscardChanges googleDocsDiscardChanges = googleDocsPage.selectDiscard().render();
        googleDocsPage = googleDocsDiscardChanges.clickCancelButton().render();
        Assert.assertTrue(googleDocsPage.isDiscardChangesVisible());
    }

    @Test(dependsOnMethods = "editInGoogleDocsSaveMajorTest")
    public void editInGoogleDocsSaveMinorTest() throws Exception
    {
        EditInGoogleDocsPage googleDocsPage = null;
        try
        {
            googleDocsPage = detailsPage.editInGoogleDocs().render();
            googleDocsPage.render();
            Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
            // googleDocsPage.edit("hello there");
            GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
            googleUpdatefile.selectMinorVersionChange();
            DocumentDetailsPage backToDetailsPage = googleUpdatefile.submit().render();
            Assert.assertTrue(backToDetailsPage.isDocumentDetailsPage());
            Assert.assertTrue(backToDetailsPage.isEditInGoogleDocsLinkVisible());
            Assert.assertNotEquals(backToDetailsPage.getDocumentVersion(), documentVersion);
        }
        catch (Exception e)
        {
            saveScreenShot(testName);
            if (googleDocsPage != null)
            {
                googleDocsPage.selectDiscard();
            }
            throw new Exception("Failed Google Docs Save Test ", e);
        }
    }

    @Test(dependsOnMethods = "editIngoogleDocsRenameTest")
    public void editInGoogleDocsSaveMajorTest() throws Exception
    {
        EditInGoogleDocsPage googleDocsPage = null;
        try
        {
            detailsPage = drone.getCurrentPage().render();
            googleDocsPage = detailsPage.editInGoogleDocs().render();
            googleDocsPage.render();
            Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
            // googleDocsPage.edit("hello there");
            GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
            googleUpdatefile.selectMajorVersionChange();
            DocumentDetailsPage backToDetailsPage = googleUpdatefile.submit().render();
            Assert.assertTrue(backToDetailsPage.isDocumentDetailsPage());
            Assert.assertTrue(backToDetailsPage.isEditInGoogleDocsLinkVisible());
            Assert.assertNotEquals(backToDetailsPage.getDocumentVersion(), documentVersion);
        }
        catch (Exception e)
        {
            saveScreenShot(testName);
            if (googleDocsPage != null)
            {
                googleDocsPage.selectDiscard();
            }
            throw new Exception("Failed Google Docs Save Test ", e);
        }
    }

    @Test(dependsOnMethods = "editIngoogleDocsRenameCancelTest")
    public void editIngoogleDocsRenameTest() throws Exception
    {
        String newName = "Test2222";
        Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
        GoogleDocsRenamePage googleDocsRename = googleDocsPage.renameDocumentTitle().render();
        googleDocsRename.updateDocumentName(newName);
        googleDocsPage.setGoogleCreate(false);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        DocumentDetailsPage backToDetailsPage = googleUpdatefile.submit().render();
        Assert.assertTrue(backToDetailsPage.isDocumentDetailsPage());
        Assert.assertTrue(backToDetailsPage.isEditInGoogleDocsLinkVisible());
        Assert.assertTrue(backToDetailsPage.getDocumentTitle().equalsIgnoreCase(newName + ".docx"));
        Assert.assertNotEquals(backToDetailsPage.getDocumentVersion(), documentVersion);
    }

    @Test(dependsOnMethods = "editInGoogleDocsDiscardOkTest")
    public void editIngoogleDocsRenameCancelTest() throws Exception
    {
        detailsPage = drone.getCurrentPage().render();
        googleDocsPage = detailsPage.editInGoogleDocs().render();
        googleDocsPage.render();
        Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
        GoogleDocsRenamePage googleDocsRename = googleDocsPage.renameDocumentTitle().render();
        googleDocsRename.cancelDocumentRename();
        Assert.assertTrue(googleDocsPage.isDiscardChangesVisible());
    }

    @Test(dependsOnMethods = "createGoogleDocsContentTest")
    public void editInGoogleDocsAndBack() throws Exception
    {
        EditInGoogleDocsPage googleDocsPage = detailsPage.editInGoogleDocs().render();
        googleDocsPage.render();
        Assert.assertTrue(googleDocsPage.isBackToSharevisible());
        DocumentDetailsPage backToDetailsPage = (DocumentDetailsPage) googleDocsPage.selectBackToShare();
        backToDetailsPage.render();
        Assert.assertTrue(backToDetailsPage.isDocumentDetailsPage());
    }
    
    @Test(dependsOnMethods = "editInGoogleDocsSaveMinorTest")
    public void editIngoogleDocsGetDocTitleTest() throws Exception
    {
        detailsPage = drone.getCurrentPage().render();
        googleDocsPage = detailsPage.editInGoogleDocs().render();
        googleDocsPage.render();
        Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
        Assert.assertTrue(googleDocsPage.getDocumentTitle().contains("Test2222"));
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        DocumentDetailsPage backToDetailsPage = googleUpdatefile.submit().render();
        Assert.assertTrue(backToDetailsPage.isDocumentDetailsPage());
    }

    @Test(dependsOnMethods = "editIngoogleDocsGetDocTitleTest")
    public void editIngoogleDocsCancelUpdateTest() throws Exception
    {
        detailsPage = drone.getCurrentPage().render();
        googleDocsPage = detailsPage.editInGoogleDocs().render();
        googleDocsPage.render();
        Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
        googleDocsPage.setGoogleCreate(false);
        GoogleDocsUpdateFilePage googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectCancel();
        Assert.assertTrue(googleDocsPage.isSaveToAlfrescoVisible());
        googleUpdatefile = googleDocsPage.selectSaveToAlfresco().render();
        googleUpdatefile.render();
        googleUpdatefile.selectMinorVersionChange();
        detailsPage = googleUpdatefile.submit().render();
        Assert.assertNotNull(detailsPage);
    }
}
