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
package org.alfresco.po.share.user;

import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryNavigation;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TrashCanPagination Test
 * 
 * @author Subashni Prasanna
 * @since 1.9
 */
@Listeners(FailedTestListener.class)
public class TrashCanPaginationTest extends AbstractTest
{
    protected DashBoardPage dashBoard;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private String firstName = userName;
    private String lastName = userName;
    TrashCanPage trashCan;
    private String siteName = "TrashCanTest" + System.currentTimeMillis();
    private String folderName;
    private String fileName;
    DocumentLibraryPage docPage;
    MyProfilePage myprofile;
    /**
     * Before method to create 60 files and delete them 
     * @throws Exception
     */
    @BeforeClass(groups = { "Enterprise4.2" })
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.inputFirstName(firstName);
        newPage.inputLastName(lastName);
        newPage.inputEmail(userName);
        newPage.inputUsername(userName);
        newPage.inputPassword(userName);
        newPage.inputVerifyPassword(userName);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        userCreated.searchFor(userName).render();
        Assert.assertTrue(userCreated.hasResults());
        logout(driver);
        loginAs(userName, userName);
    }
    
    private void prepare() throws Exception
    {
        siteUtil.createSite(driver, username, password, siteName, "", "Public");
        SitePage site = resolvePage(driver).render();
        docPage = site.getSiteNav().selectDocumentLibrary().render();
        for (int i = 0; i < 4; i++)
        {
            folderName = "folder" + i + System.currentTimeMillis();
            fileName = "file" + i + System.currentTimeMillis();
            File file = siteUtil.prepareFile(fileName);
            docPage = site.getSiteNav().selectDocumentLibrary().render();
            NewFolderPage folder = docPage.getNavigation().selectCreateNewFolder().render();
            docPage = folder.createNewFolder(folderName).render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            upLoadPage.uploadFile(file.getCanonicalPath()).render();
        }
        docPage = resolvePage(driver).render();
        for (int i = 0; i < 3; i++)
        {
            docPage = docPage.getNavigation().selectAll().render();
            CopyOrMoveContentPage copyContent = docPage.getNavigation().selectCopyTo().render();
            docPage = copyContent.selectOkButton().render();
            docPage = resolvePage(driver).render();
        }
        docPage = resolvePage(driver).render();
        do
        {
            docPage = docPage.getNavigation().selectAll().render();
            DocumentLibraryNavigation docLibNavOption = docPage.getNavigation();
            ConfirmDeletePage deletePage = docLibNavOption.selectDelete().render();
            deletePage.selectAction(Action.Delete);
            docPage = resolvePage(driver).render();
        } while (docPage.hasFiles());
    }
        
    @AfterClass(groups = { "Enterprise4.2" })
    public void deleteSite()
    {
      trashCan.selectEmpty().render();
      siteUtil.deleteSite(username, password, siteName);
    }
    
    private TrashCanPage getTrashCan()
    {
        dashBoard = docPage.getNav().selectMyDashBoard().render();
        myprofile = dashBoard.getNav().selectMyProfile().render();
        trashCan = myprofile.getProfileNav().selectTrashCan().render();
        return trashCan;
    }
    
    @Test (groups = { "Enterprise4.2" })
    public void trashCanEmptyPagination()
    {
     dashBoard = resolvePage(driver).render();
     myprofile = dashBoard.getNav().selectMyProfile().render();
     trashCan = myprofile.getProfileNav().selectTrashCan().render();
     Assert.assertFalse(trashCan.hasNextPage());
     Assert.assertFalse(trashCan.hasPreviousPage());
    }
    
    @Test (dependsOnMethods = "trashCanEmptyPagination", groups = { "Enterprise4.2" })
    public void trashCanHasPagination() throws Exception
    {
       prepare();
       trashCan  = getTrashCan();
        Assert.assertTrue(trashCan.hasNextPage());    
        trashCan = trashCan.selectNextPage().render();
        Assert.assertTrue(trashCan.getTrashCanItems().size() > 0);
        Assert.assertTrue(trashCan.hasPreviousPage());
        trashCan = trashCan.selectPreviousPage().render();
        Assert.assertTrue(trashCan.getTrashCanItems().size() > 0);
    }
}
