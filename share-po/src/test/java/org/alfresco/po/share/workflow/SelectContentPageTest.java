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
package org.alfresco.po.share.workflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 *
 * @author Shan Nagarajan
 * @since  1.7.1
 */
@Listeners(FailedTestListener.class)
public class SelectContentPageTest extends AbstractTest
{
    NewWorkflowPage newWorkflowPage;
    DashBoardPage dashBoardPage; 
    private String siteName; 
    /**
     * Prepare test.
     * @throws Exception if error
     */
    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
    {
        dashBoardPage = loginAs(username, password);
        siteName = String.format("test-%d-selectContent", System.currentTimeMillis());
        CreateSitePage createSite = dashBoardPage.getNav().selectCreateSite().render();
        SiteDashboardPage site = createSite.createNewSite(siteName).render();

        DocumentLibraryPage libraryPage = site.getSiteNav().selectDocumentLibrary().render();

        CreatePlainTextContentPage contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Doc3");
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();

        libraryPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
        contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        contentDetails = new ContentDetails();
        contentDetails.setName("Doc1");
        detailsPage = contentPage.create(contentDetails).render();

        libraryPage = detailsPage.getSiteNav().selectDocumentLibrary().render();

        contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();

        contentDetails = new ContentDetails();
        contentDetails.setName("Doc2");
        detailsPage = contentPage.create(contentDetails).render();

        libraryPage = detailsPage.getSiteNav().selectDocumentLibrary().render();

        NewFolderPage folderPage = libraryPage.getNavigation().selectCreateNewFolder();

        libraryPage = folderPage.createNewFolder("Folder1").render();

        libraryPage = libraryPage.selectFolder("Folder1").render();

        contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();

        contentDetails = new ContentDetails();
        contentDetails.setName("F1Doc1");
        detailsPage = contentPage.create(contentDetails).render();

        libraryPage = detailsPage.getSiteNav().selectDocumentLibrary().render();

        folderPage = libraryPage.getNavigation().selectCreateNewFolder().render();
        libraryPage = folderPage.createNewFolder("Folder2").render();
        libraryPage = libraryPage.selectFolder("Folder2").render();

        folderPage = libraryPage.getNavigation().selectCreateNewFolder();
        libraryPage = folderPage.createNewFolder("Folder11").render();
        libraryPage = libraryPage.selectFolder("Folder11").render();

        folderPage = libraryPage.getNavigation().selectCreateNewFolder();
        libraryPage = folderPage.createNewFolder("Folder21").render();
        libraryPage = libraryPage.selectFolder("Folder21").render();
 
        contentPage = libraryPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        contentDetails = new ContentDetails();
        contentDetails.setName("Doc211");
        detailsPage = contentPage.create(contentDetails).render();

        MyTasksPage tasksPage = dashBoardPage.getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = tasksPage.selectStartWorkflowButton().render();
        newWorkflowPage = startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW).render();
    }
    
    
    @AfterClass(groups = "Enterprise4.2")
    public void teardown() throws Exception
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    @Test(groups="Enterprise4.2")
    public void getAddedItems() throws Exception
    {
        SelectContentPage contentPage = newWorkflowPage.clickAddItems().render();
        
        Assert.assertTrue(contentPage.isOkButtonPresent());
        Assert.assertTrue(contentPage.isCancelButtonPresent());
        Assert.assertTrue(contentPage.isCompanyHomeButtonPresent());
        List<String> elements = contentPage.getDirectoriesLeftPanel();
        Assert.assertTrue(elements.contains("Data Dictionary") && elements.contains("Guest Home") && elements.contains("Imap Attachments")
                && elements.contains("IMAP Home") && elements.contains("Shared") && elements.contains("Sites") && elements.contains("User Homes"));
        
        Content content1 = new Content();
        content1.setName("Doc3");
        content1.setFolder(false);
        Content content2 = new Content();
        content2.setName("Doc1");
        content2.setFolder(false);
        Content content3 = new Content();
        content3.setName("Doc2");
        content3.setFolder(false);
        Content content4 = new Content();
        content4.setFolder(true);
        content4.setName("Folder1");
        Set<Content> contents4 = new HashSet<Content>();
        Content content41 = new Content();
        content41.setFolder(false);
        content41.setName("F1Doc1");
        contents4.add(content41);
        content4.setContents(contents4);
        Content content5= new Content();
        content5.setFolder(true);
        content5.setName("Folder2");
        Content content51 = new Content();
        content51.setFolder(true);
        content51.setName("Folder11");
        Content content52 = new Content();
        content52.setFolder(true);
        content52.setName("Folder21");
        Site site2 = new Site();
        site2.setName(siteName);
        Content content53 = new Content();
        content53.setFolder(false);
        content53.setName("Doc211");
        Set<Content> contents5 = new HashSet<Content>();
        contents5.add(content53);
        content52.setContents(contents5);
        Set<Content> contents51 = new HashSet<Content>();
        contents51.add(content52);
        content51.setContents(contents51);
        Set<Content> contents52 = new HashSet<Content>();
        contents52.add(content51);
        content5.setContents(contents52);
        Set<Content> site2Contents = new HashSet<Content>();
        site2Contents.add(content5);
        site2Contents.add(content4);
        site2Contents.add(content1);
        site2Contents.add(content2);
        site2Contents.add(content3);
        site2.setContents(site2Contents);
        CompanyHome companyHome = new CompanyHome();
        Set<Site> sites = new HashSet<Site>();
        sites.add(site2);
        companyHome.setSites(sites);
        contentPage.addItems(companyHome);
        
        Assert.assertTrue(contentPage.isRemoveIconPresent(content1.getName()));
        Assert.assertTrue(contentPage.isRemoveIconPresent(content2.getName()));
        
        List<String> expectedItems = new ArrayList<String>();
        expectedItems.add(content1.getName());
        expectedItems.add(content2.getName());
        expectedItems.add(content3.getName());
        expectedItems.add(content41.getName());
        expectedItems.add(content53.getName());
        Assert.assertTrue(CollectionUtils.subtract(contentPage.getAddedItems(), expectedItems).isEmpty());
        contentPage.selectOKButton();
    }
    
}
