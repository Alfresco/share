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

package org.alfresco.po.share.search;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.MimeType;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to validate the advance Search Content Page.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
@Listeners(FailedTestListener.class)
public class AdvanceSearchContentTest extends AbstractTest
{
    private String siteName;
    private File file;
    private String fileName;
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    AdvanceSearchPage contentSearchPage;
    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date todayDate = new Date();

    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @BeforeClass(groups={"Enterprise-only"})
    public void prepare() throws Exception
    {
        siteName = "AdvanceSearchContent" + System.currentTimeMillis();
        file = siteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        fileName = st.nextToken();
        File file = siteUtil.prepareFile();
        fileName = file.getName();
        loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName,"", "Public");
        SitePage site = resolvePage(driver).render();
        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        DocumentDetailsPage docDetailsPage = docPage.selectFile(fileName).render();
        EditDocumentPropertiesPage editPage = docDetailsPage.selectEditProperties().render();
        editPage.selectMimeType(MimeType.XHTML);
        editPage.setAuthor("me");
        editPage.setDescription("my description");
        editPage.setDocumentTitle("my title");
        editPage.setName("my.txt");
        docDetailsPage = editPage.selectSave().render();
        dashBoard = docDetailsPage.getNav().selectMyDashBoard().render();
        Thread.sleep(20000);
    }

    @AfterClass(groups={"Enterprise-only"})
    public void deleteSite()
    {
        dashBoard.getNav().selectMyDashBoard().render();
        siteUtil.deleteSite(username, password, siteName);
    }

    /**
     * This Test case is to Test content search with all the fields entered.
     * Currently this feature is only present in Alfresco Enterprise.
     * @throws Exception
     */
    @Test(groups={"Enterprise-only","bug"})
    public void contentSearchTest() throws Exception
    {
        Date todayDate = new Date();
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        contentSearchPage.inputTitle("my title");
        contentSearchPage.inputDescription("my description");
        contentSearchPage.inputFromDate(dateFormat.format(todayDate));
        contentSearchPage.inputToDate(dateFormat.format(todayDate));
        contentSearchPage.inputModifier(username);        
        SearchResultPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
        //goBackToAdvanceSearch does not exist now and will be included later
        //searchResults.goBackToAdvanceSearch().render();
        // Validated the entered search data is all correct
        //advanceSearchFormValuesRetained();
    }

    /**
     * This method is keep searching the search until we get results.
     * 
     * @return FacetedSearchPage the search page object
     * @throws Exception if error
     */
    public SearchResultsPage searchRetry() throws Exception
    {
        int counter = 0;
        int waitInMilliSeconds = 2000;
        while(counter < 3)
        {
        	SearchResultsPage searchResults = contentSearchPage.clickSearch().render();
            if (searchResults.hasResults())
            {
                return searchResults;
            }
            else
            {
                counter++;
                searchResults.goBackToAdvanceSearch().render();
            }
            //double wait time to not over do solr search
            waitInMilliSeconds = (waitInMilliSeconds*2);
            synchronized (this)
            {
                try{ this.wait(waitInMilliSeconds); } catch (InterruptedException e) {}
            }
        }
        throw new Exception("search failed");
    }
    /**
     * This method is Check the entered value in content search is all correct.
     * 
     * @throws Exception if error
     */
    public void advanceSearchFormValuesRetained() throws Exception
    {
        Assert.assertEquals("my.txt", contentSearchPage.getName());
        Assert.assertEquals("my title", contentSearchPage.getTitle());
        Assert.assertEquals("my description", contentSearchPage.getDescription());
        Assert.assertEquals(dateFormat.format(todayDate), contentSearchPage.getFromDate());
        Assert.assertEquals(dateFormat.format(todayDate), contentSearchPage.getToDate());
        Assert.assertEquals(username, contentSearchPage.getModifier());
    }

    /**
     * Test to validate mime type
     */
    @Test(groups={"Enterprise-only","bug"})
    public void mimeTypeSearchTest() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        contentSearchPage.selectMimeType("XHTML");        
        SearchResultPage searchResults = contentSearchPage.clickSearch().render();        
        Assert.assertTrue(searchResults.hasResults());  
        //searchResults.goBackToAdvanceSearch().render();
        // Assert.assertEquals("XHTML",contentSearchPage.getMimeType());
    }

    
    /**
     * Test to validate modified from date.
     */
    @Test(groups={"Enterprise-only"})
    public void validateFromDateTest() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputFromDate(dateFormat.format(todayDate));
        Assert.assertTrue(contentSearchPage.isValidFromDate());
    }
    
    /**
     * Test to validate invalid modified from date.
     */
    @Test(groups={"Enterprise-only"})
    public void validateInvalidFromDateTest() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputFromDate("0/06/2013");
        Assert.assertFalse(contentSearchPage.isValidFromDate());
    }

    /**
     * This test is to test whether the first result item is Document or not.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "validateInvalidFromDateTest",groups = {"Enterprise-only","bug"})
    public void testIsFolder() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        SearchResultPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
        Assert.assertFalse(searchResults.getResults().get(0).isFolder());
        
        
    }

    /**
     * This test is to click on download icon and view in browser icon of the selected search result item.
     * Note: This test will be enabled only with chrome browser execution.
     * @throws Exception
     */
    @Test(dependsOnMethods = "testIsFolder", groups = { "Enterprise-only", "chromeOnly" ,"bug"})
    public void testClickOnDownloadAndViewInBrowserLink() throws Exception
    {
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        SearchResultPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
        FacetedSearchResult searchResultItem = (FacetedSearchResult) searchResults.getResults().get(0);
        searchResultItem.clickOnDownloadIcon();
        
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName("my.txt");
        searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
    }
    
    /**
     * This test is to click on download icon and view in browser icon of the selected search result item.
     * Note: This test will be enabled only with chrome browser execution.
     * @throws Exception
     */
    @Test(dependsOnMethods="testIsFolder", groups={"Enterprise-only","bug"})
    public void testGetFolderNamesFromPath() throws Exception
    {
        File newFile = siteUtil.prepareFile("folderPath");
        String fileName =  newFile.getName();

        driver.navigate().to(shareUrl + String.format("/page/site/%s/documentlibrary", siteName));
        DocumentLibraryPage docPage = resolvePage(driver).render();
        NewFolderPage folderPage = docPage.getNavigation().selectCreateNewFolder().render();
        folderPage.createNewFolder("Attachments", "testFolder Description").render();
        docPage.selectFolder("Attachments").render();
        
        folderPage = docPage.getNavigation().selectCreateNewFolder().render();
        docPage = folderPage.createNewFolder("TestFolder", "testFolder Description").render();
        docPage.selectFolder("TestFolder").render();
        
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = (DocumentLibraryPage) upLoadPage.uploadFile(newFile.getCanonicalPath());
        docPage.render();

        Thread.sleep(20000); //solr wait.
        contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        contentSearchPage.inputName(fileName);
        SearchResultPage searchResults = contentSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.hasResults());
        SearchResult searchResultItem = searchResults.getResults().get(0);
        
        List<String> list = ((FacetedSearchResult) searchResultItem).getFolderNamesFromContentPath();
        Assert.assertTrue(list.size() > 0);
        Assert.assertTrue(list.size() == 2);
        Assert.assertTrue(list.get(0).equalsIgnoreCase("Attachments"));
        Assert.assertTrue(list.get(1).equalsIgnoreCase("TestFolder"));
    }
}
