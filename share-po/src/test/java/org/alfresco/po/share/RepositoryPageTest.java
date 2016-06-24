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
package org.alfresco.po.share;

import java.io.File;
import java.util.List;

import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify repository page is operating correctly.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */

@Test(groups={"Repository", "Enterprise4.2"})
@Listeners(FailedTestListener.class)
public class RepositoryPageTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(RepositoryPageTest.class);
    private static final String MY_FOLDER = "aa--" + System.currentTimeMillis();
    private File sampleFile;
    private String contentName = "Test" + System.currentTimeMillis();
    private String userName = "RepositoryPageTest" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    
    @BeforeClass(groups={"Repository", "Enterprise4.2"})
    public void createSite()throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.createEnterpriseUserWithGroup(userName, firstName, lastName, userName, userName, "ALFRESCO_ADMINISTRATORS");
        UserSearchPage userPage = dashBoard.getNav().getUsersPage().render();
        userPage.searchFor(userName).render();
        Assert.assertTrue(userPage.hasResults());
        logout(driver);
        loginAs(userName, userName);
        sampleFile = siteUtil.prepareFile("ab--" + System.currentTimeMillis());
        logger.info("===completed create site");
    }

    @AfterClass(groups={"Repository", "Enterprise4.2"})
    public void deleteSite()
    {
        closeWebDriver();
    }

    @Test
    public void navigateToRepository() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        repositoryPage = repositoryPage.getNavigation().selectDetailedView().render();
        List<FileDirectoryInfo> files = repositoryPage.getFiles();
        Assert.assertTrue(files.size() > 0);
        Assert.assertTrue(repositoryPage.getTitle().contains("Repository Browser"));
    }
    
    @Test(dependsOnMethods="navigateToRepository")
    public void createFolder()
    {
        RepositoryPage repositoryPage = resolvePage(driver).render();
        NewFolderPage form = repositoryPage.getNavigation().selectCreateNewFolder();
        repositoryPage = form.createNewFolder(MY_FOLDER, "my test folder").render();
        Assert.assertNotNull(repositoryPage);
        FileDirectoryInfo folder = repositoryPage.getFileDirectoryInfo(MY_FOLDER);
        Assert.assertNotNull(folder);
        Assert.assertEquals(folder.getName(),MY_FOLDER);
//        Assert.assertEquals(folder.getDescription(),"my test folder");
    }
    
    @Test(dependsOnMethods="createFolder")
    public void navigateToParentFolderTest()
    {
        RepositoryPage repositoryPage = resolvePage(driver).render();
        FolderDetailsPage detailsPage = repositoryPage.getFileDirectoryInfo(MY_FOLDER).selectViewFolderDetails().render();
        repositoryPage = detailsPage.navigateToParentFolder().render();
        Assert.assertTrue(repositoryPage.isFileVisible(MY_FOLDER));
    }
    
    @Test(dependsOnMethods="navigateToParentFolderTest")
    public void uploadFile() throws Exception
    {
        RepositoryPage repositoryPage = resolvePage(driver).render();
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload().render();
        repositoryPage = uploadForm.uploadFile(sampleFile.getCanonicalPath()).render();
        FileDirectoryInfo file = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), sampleFile.getName());
    }
    

    @Test(dependsOnMethods="uploadFile")
    public void selectFolderByName()
    {
        RepositoryPage repositoryPage = resolvePage(driver).render();
        DocumentLibraryPage libPage = repositoryPage.selectFolder(MY_FOLDER).render();
        Assert.assertFalse(libPage.hasFiles());
    }
    
  //TODO Disbaled due to defect in prodct JIRA issue: ALF-20814
    @Test(dependsOnMethods="selectFolderByName", enabled=false)
    public void copyFolderTest()
    {
        FileDirectoryInfo info = null;
        boolean Results = false;
        String copyFolder = "Copy Folder" + System.currentTimeMillis();
        String toFolderCopied = "Folder to be Copied" + System.currentTimeMillis();
        RepositoryPage repoPage = resolvePage(driver).render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(copyFolder).render();
        form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(toFolderCopied).render();
        info = repoPage.getFileDirectoryInfo(toFolderCopied);
        CopyOrMoveContentPage copyOrMoveContentPage = info.selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath(MY_FOLDER, copyFolder).render();
        repoPage = copyOrMoveContentPage.selectOkButton().render();
        repoPage.selectFolder(copyFolder);
        List<FileDirectoryInfo> files = repoPage.getFiles();
        for(FileDirectoryInfo fileList : files)
        {
            if(fileList.getName().equalsIgnoreCase(toFolderCopied))
            {
                 Results = true;
            }
        }
        Assert.assertTrue(Results);
    }

  //TODO Disbaled due to defect in prodct JIRA issue: ALF-20814
    @Test(dependsOnMethods="selectFolderByName", enabled=false)
    public void copyFolderTestNegativeCase()
    {
        CopyOrMoveContentPage copyOrMoveContentPage = null;
      try
      {
        FileDirectoryInfo info = null;
        String copyFolder = "Copy Folder" + System.currentTimeMillis();
        String toFolderCopied = "Folder to be Copied" + System.currentTimeMillis();
        RepositoryPage repoPage = resolvePage(driver).render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(copyFolder).render();
        form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder(toFolderCopied).render();
        info = repoPage.getFileDirectoryInfo(toFolderCopied);
        copyOrMoveContentPage = info.selectCopyTo().render();
        copyOrMoveContentPage = copyOrMoveContentPage.selectPath(copyFolder, copyFolder + "1").render();
      }
      catch (PageOperationException pe)
      {
          copyOrMoveContentPage.selectCancelButton().render();
      }
    }
    
    @Test(dependsOnMethods="selectFolderByName")
    public void createInSubFolder()
    {
        RepositoryPage repoPage = resolvePage(driver).render();
        NewFolderPage form = repoPage.getNavigation().selectCreateNewFolder();
        repoPage = form.createNewFolder("test").render();
        FileDirectoryInfo folder = repoPage.getFileDirectoryInfo("test");
        Assert.assertNotNull(folder);
        Assert.assertEquals(folder.getName(),"test");
    }
    
    @Test(dependsOnMethods="createInSubFolder")
    public void uploadInSubFolder() throws Exception
    {
        RepositoryPage repositoryPage = resolvePage(driver).render();
        UploadFilePage uploadForm = repositoryPage.getNavigation().selectFileUpload().render();
        repositoryPage = uploadForm.uploadFile(sampleFile.getCanonicalPath()).render();
        FileDirectoryInfo file = repositoryPage.getFileDirectoryInfo(sampleFile.getName());
        Assert.assertNotNull(file);
        Assert.assertEquals(file.getName(), sampleFile.getName());
    }
    
    @Test(dependsOnMethods="uploadInSubFolder",expectedExceptions = PageOperationException  .class)
    public void delete()
    {
        SharePage page = resolvePage(driver).render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        
        repositoryPage = repositoryPage.getFileDirectoryInfo(MY_FOLDER).delete().render();
        FileDirectoryInfo folder = repositoryPage.getFileDirectoryInfo(MY_FOLDER);
        Assert.assertNull(folder);
    }
    /**
     * create content in repository 
     * <br/><br/>author sprasanna
     */
    
    @Test(dependsOnMethods="delete")
    public void createContent()
    {
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(contentName);
        contentDetails.setContent("test");
        SharePage sharePage = resolvePage(driver).render();
        RepositoryPage page = sharePage.getNav().selectRepository().render();
        CreatePlainTextContentPage contentPage = page.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        RepositoryPage repositoryPage = detailsPage.navigateToFolderInRepositoryPage().render();
        FileDirectoryInfo file = repositoryPage.getFileDirectoryInfo(contentName);
        Assert.assertTrue(file.getName().equalsIgnoreCase(contentName));
    }
 
    /**
     * Select the action of manage Aspects
     * <br/><br/>author sprasanna
     */
    
    @Test(
    dependsOnMethods="createContent")
    public void selectMangeAspectTest()
    {
        SharePage page = resolvePage(driver).render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        FileDirectoryInfo file = repositoryPage.getFileDirectoryInfo("Data Dictionary");
        SelectAspectsPage selectAspectPage = file.selectManageAspects().render();
        Assert.assertNotNull(selectAspectPage);
    }
}
