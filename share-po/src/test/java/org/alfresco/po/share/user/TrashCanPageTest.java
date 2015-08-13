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

package org.alfresco.po.share.user;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TrashCanPage Test
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
@Listeners(FailedTestListener.class)
public class TrashCanPageTest extends AbstractTest
{
    private String siteName;
    private String siteName1;
    private String fileName1;
    private String fileName2;
    private String fileName3;
    private String fileName4;
    private String fileName5;
    private String folderName;
    DocumentLibraryPage docPage;
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    MyProfilePage myprofile;
    TrashCanPage trashCan;
    private String userName;
    private String userFullName;

    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "Enterprise4.2" })
    public void prepare() throws Exception
    {
        siteName = "TrashCanTest" + System.currentTimeMillis();
        siteName1 = "DeleteTrashCanSite" + System.currentTimeMillis();
        folderName = "folder1" + System.currentTimeMillis();;
        File file0 = siteUtil.prepareFile("file1.txt");
        fileName1 = file0.getName();
        File file1 = siteUtil.prepareFile("file2.txt");
        fileName2 = file1.getName();
        File file2 = siteUtil.prepareFile("file3.txt");
        fileName3 = file2.getName();
        File file3 = siteUtil.prepareFile("file4.txt");
        fileName4 = file3.getName();
        File file4 = siteUtil.prepareFile("file5.txt");
        fileName5 = file4.getName();
        userName = "user" + System.currentTimeMillis();
        userFullName = userName + " " + userName;
        createEnterpriseUser(userName);
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "", "Public");
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName1, "", "Public");
        siteUtil.deleteSite(userName, UNAME_PASSWORD, siteName1);
        loginAs(userName, UNAME_PASSWORD).getNav().selectSearchForSites();
        SiteFinderPage siteFinderPage = resolvePage(driver).render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, siteName);
        SiteDashboardPage site = siteFinderPage.selectSite(siteName).render();
        docPage = site.getSiteNav().selectDocumentLibrary().render();
        NewFolderPage folder = docPage.getNavigation().selectCreateNewFolder().render();
        docPage = folder.createNewFolder(folderName).render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        upLoadPage.uploadFile(file0.getCanonicalPath()).render();
        upLoadPage = docPage.getNavigation().selectFileUpload().render();
        upLoadPage.uploadFile(file1.getCanonicalPath()).render();
        upLoadPage = docPage.getNavigation().selectFileUpload().render();
        upLoadPage.uploadFile(file2.getCanonicalPath()).render();
        upLoadPage = docPage.getNavigation().selectFileUpload().render();
        upLoadPage.uploadFile(file3.getCanonicalPath()).render();
        upLoadPage = docPage.getNavigation().selectFileUpload().render();
        upLoadPage.uploadFile(file4.getCanonicalPath()).render();
        docPage = resolvePage(driver).render();

        docPage = docPage.deleteItem(fileName1).render();
        docPage = docPage.deleteItem(fileName2).render();
        docPage = docPage.deleteItem(fileName3).render();
        docPage = docPage.deleteItem(fileName4).render();
        docPage = docPage.deleteItem(fileName5).render();
        docPage = docPage.deleteItem(folderName).render();
    }
    
    @AfterClass(groups = { "Enterprise4.2" })
    public void deleteSite()
    {
        trashCan.selectEmpty().render();
        siteUtil.deleteSite(username, password, siteName);
    }
    
    public TrashCanPage getTrashCan()
    {
        dashBoard = docPage.getNav().selectMyDashBoard().render();
        myprofile = dashBoard.getNav().selectMyProfile().render();
        trashCan = myprofile.getProfileNav().selectTrashCan().render();
        return trashCan;
    }

    /**
     * Test to Check from My profile Page trashCan Page can be accessed
     */

    @Test(groups = { "Enterprise4.2" }, priority=1)
    public void test101TrashCanDisplayed()
    {
        trashCan = getTrashCan();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
        trashCan = (TrashCanPage) trashCan.itemSearch("ZZZZZ").render();
        Assert.assertTrue(trashCan.checkNoItemsMessage());
        trashCan = trashCan.clearSearch().render();
    }
    /*
     * Test to check deleted files and folder are present in trashCan
     */
    @Test(groups = { "Enterprise4.2" }, priority=2)
    public void test102TrashCanInfoOfDeleteItems()
    {
        trashCan = getTrashCan();
        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE, fileName1, "documentLibrary");
        if(item1.size() == 1)
        {
            Assert.assertTrue(item1.get(0).getFileName().equalsIgnoreCase(fileName1));
            Assert.assertTrue(item1.get(0).getUserFullName().equalsIgnoreCase(userFullName));
            Assert.assertTrue(item1.get(0).getDate().contains(Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
        List<TrashCanItem> item6 = trashCan.getTrashCanItemForContent(TrashCanValues.FOLDER, folderName, "documentLibrary");
        if(item6.size() <= 1)
        {
            Assert.assertTrue(item6.get(0).getFileName().equalsIgnoreCase(folderName));
        }
        else
        {
            Assert.fail("Cannot find unique file");
        } 
        List<TrashCanItem> item7 = trashCan.getTrashCanItemForContent(TrashCanValues.SITE, siteName1, "sites");
        if(item7.size() <= 1)
        {
            Assert.assertTrue(item7.get(0).getFileName().equalsIgnoreCase(siteName1));
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }     
    }
    /**
     * Test to check the differnt select options ie All
     */
    @Test(groups = { "Enterprise4.2" }, priority=3)
    public void test103SelectActionsAll()
    {
        trashCan = getTrashCan();
        trashCan = (TrashCanPage) trashCan.selectAction(SelectActions.ALL).render();
        List<TrashCanItem>  item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName1, "documentLibrary");
        List<TrashCanItem>  item2 =  trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName2, "documentLibrary");
        List<TrashCanItem>  item3 =  trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName3, "documentLibrary");
        List<TrashCanItem>  item4 =  trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName4, "documentLibrary");
        List<TrashCanItem>  item5 =  trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName5, "documentLibrary");
        if(item1.size()<= 1)
        {
            Assert.assertTrue(item1.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
        if(item2.size()<= 1)
        {
            Assert.assertTrue(item2.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
        if(item3.size()<= 1)
        {
            Assert.assertTrue(item3.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
        if(item4.size()<= 1)
        {
            Assert.assertTrue(item4.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
        if(item5.size()<= 1)
        {
            Assert.assertTrue(item5.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
    }

    /**
     * Test to check the differnt select options ie invert
     */
    @Test(groups = { "Enterprise4.2" }, priority=4)
    public void test104SelectActionsInvert()
    {
        trashCan = (TrashCanPage) trashCan.selectAction(SelectActions.INVERT).render();
        List<TrashCanItem>  item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName1, "documentLibrary");
        if(item1.size()<= 1)
        {
            Assert.assertFalse(item1.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
     }

    /**
     * Test to check the differnt select options ie none
     */
    @Test(dependsOnMethods = "test104SelectActionsInvert", groups = { "Enterprise4.2" } , priority=5)
    public void test105SelectActionsnone()
    {
        trashCan = (TrashCanPage) trashCan.selectAction(SelectActions.NONE).render();
        List<TrashCanItem>  item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName2, "documentLibrary");
        if(item1.size()<= 1)
        {
            Assert.assertFalse(item1.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
    }

    /**
     * Test to Check searching in trashcan
     */

    @Test(dependsOnMethods = "test105SelectActionsnone", groups = { "Enterprise4.2" }, priority=6)
    public void test106TrashCanSearch()
    {
        boolean results = false;
        trashCan = getTrashCan();
        trashCan = (TrashCanPage) trashCan.itemSearch("ZZZZZ").render();
        Assert.assertTrue(trashCan.checkNoItemsMessage());
        trashCan = (TrashCanPage) trashCan.itemSearch("file").render();
        List<TrashCanItem> trashCanItem = trashCan.getTrashCanItems();
        Assert.assertFalse(trashCanItem.isEmpty());
        for (TrashCanItem searchTerm : trashCanItem)
        {
            if (searchTerm.getFileName().contains("file")) results = true;
        }
        Assert.assertTrue(results);
    }

    /**
     * Test to Check searching in trashcan
     */

    @Test(groups = { "Enterprise4.2" }, priority=7)
    public void test107TrashCanSelectCheckBox()
    {
        trashCan = getTrashCan();
        List<TrashCanItem>  item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName4, "documentLibrary");
        if(item1.size() <= 1)
        {
          trashCan= item1.get(0).selectTrashCanItemCheckBox();
           Assert.assertTrue(item1.get(0).isCheckBoxSelected());
        }
        else
        {
            Assert.fail("Cannot find unique file");
        }
    }   
    
    /**
     * Test to Validated Selected items can be recovered
     */
    @Test(groups = { "Enterprise4.2" }, priority=8)
    public void test108TrashCanSelectedRecover()
    {
        boolean results = false;
        trashCan = getTrashCan();
        List<TrashCanItem>  item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE,fileName4, "documentLibrary");
        trashCan= item1.get(0).selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCan.selectedRecover().render();
        assertEquals (trashCanRecoverConfirmation.getNotificationMessage(),"Successfully recovered 1 item(s), 0 failed.");
        trashCan = trashCanRecoverConfirmation.clickRecoverOK().render();
        List<TrashCanItem> trashCanItem = trashCan.getTrashCanItems();
        for (TrashCanItem itemTerm : trashCanItem)
        {
            if (itemTerm.getFileName().contains("fileName4")) results = true;
        }
        Assert.assertFalse(results);
    }

    /**
     * Test to Validated Selected items can be recovered
     */
    @Test(dependsOnMethods = "test108TrashCanSelectedRecover", groups = { "Enterprise4.2" }, priority=9)
    public void test109TrashCanSelectedDelete()
    {
        boolean results = false;
        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE, fileName5, "documentLibrary");
        if (item1.size() <= 1)
        {
            trashCan = item1.get(0).selectTrashCanItemCheckBox();
        }
        TrashCanDeleteConfirmationPage trashCanDeleteConfirmation = trashCan.selectedDelete().render();
        assertEquals (trashCanDeleteConfirmation.getNotificationMessage(),"This will permanently delete the item(s). Are you sure?");
        TrashCanDeleteConfirmDialog trashCanConfrimDialog = trashCanDeleteConfirmation.clickOkButton().render();
        trashCan = trashCanConfrimDialog.clickDeleteOK().render();
        List<TrashCanItem> trashCanItemResults = trashCan.getTrashCanItems();
        for (TrashCanItem itemTerm : trashCanItemResults)
        {
            if (itemTerm.getFileName().contains("fileName5")) results = true;
        }
        Assert.assertFalse(results);
    }

    /**
     * Test to Clear the search entry trashcan
     */
    @Test(dependsOnMethods = "test109TrashCanSelectedDelete", groups = { "Enterprise4.2" }, priority=10)
    public void test110TrashClear()
    {
        trashCan = (TrashCanPage) trashCan.clearSearch().render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
    }

    /**
     * Test to Recover an item trashcan
     */

    @Test(dependsOnMethods = "test110TrashClear", groups = { "Enterprise4.2" }, priority=11)
    public void test111TrashCanRecover()
    {
        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE, fileName1, "documentLibrary");
        if(item1.size() <= 1)
        {
           trashCan = (TrashCanPage) item1.get(0).selectTrashCanAction(TrashCanValues.RECOVER).render();
        }
        List<TrashCanItem> item2 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE, fileName1, "documentLibrary");
        Assert.assertTrue(item2.isEmpty());

    }

    /**
     * Test to Delete an item trashcan
     */
    @Test(dependsOnMethods = "test111TrashCanRecover", groups = { "Enterprise4.2" }, priority=12)
    public void test112TrashCanDelete()
    {
        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE, fileName2, "documentLibrary");
        if(item1.size() <= 1)
        {
            TrashCanDeleteConfirmationPage trashCanConfirmationDeleteDialog = (TrashCanDeleteConfirmationPage) item1.get(0).selectTrashCanAction(TrashCanValues.DELETE).render();
            Assert.assertTrue(trashCanConfirmationDeleteDialog.isConfirmationDialogDisplayed());
            trashCan = trashCanConfirmationDeleteDialog.clickOkButton().render();
        }
       List<TrashCanItem> item2 = trashCan.getTrashCanItemForContent(TrashCanValues.FILE, fileName2, "documentLibrary");
        Assert.assertTrue(item2.isEmpty());
    }

     
    /**
     * Test to Empty trashcan
     */
    @Test(dependsOnMethods = "test112TrashCanDelete", groups = { "Enterprise4.2" }, priority=13)
    public void test113TrashCanEmpty()
    {
        TrashCanEmptyConfirmationPage trashCanEmptyDialogPage = (TrashCanEmptyConfirmationPage) trashCan.selectEmpty().render();
        Assert.assertTrue(trashCanEmptyDialogPage.isConfirmationDialogDisplayed());
        trashCan = trashCanEmptyDialogPage.clickOkButton().render();
        Assert.assertTrue(trashCan.getPageTitle().equalsIgnoreCase("User Trashcan"));
        Assert.assertFalse(trashCan.hasTrashCanItems());
    }

}
