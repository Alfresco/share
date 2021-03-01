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
package org.alfresco.po.share.task;

import static org.alfresco.po.share.task.EditTaskPage.Button.ADD;
import static org.alfresco.po.share.task.EditTaskPage.Button.CANCEL;
import static org.alfresco.po.share.task.EditTaskPage.Button.REASSIGN;
import static org.alfresco.po.share.task.EditTaskPage.Button.SAVE_AND_CLOSE;
import static org.alfresco.po.share.task.EditTaskPage.Button.TASK_DONE;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests the page object - <code>EditTaskPage</code>
 *
 * @author Abhijeet Bharade
 * @since 1.6.2
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "bug" })
public class EditTaskPageTest extends AbstractTaskTest
{
    private EditTaskPage pageUnderTest;
    private String otherUser = "";
    protected String modSiteName;
    protected String itemSiteName;
    private SharePage returnedPage;
    private SiteFinderPage siteFinder;
    private String testUserName;
    private String fileName;


    @BeforeClass(groups = "Enterprise4.2")
    public void setUp() throws Throwable
    {
        otherUser = "otherUser" + System.currentTimeMillis();
        siteName = "AdhocReassign" + System.currentTimeMillis();
        modSiteName = "modSiteName" + System.currentTimeMillis();
        itemSiteName = "itemSiteName" + System.currentTimeMillis();
        taskName = siteName;
        testUserName = "reviewer" + System.currentTimeMillis();
        createEnterpriseUser(testUserName);
        createEnterpriseUser(otherUser);

        // add site with item for selectItem test
        loginAs(testUserName, UNAME_PASSWORD);
        File file = siteUtil.prepareFile();
        fileName = file.getName();
        siteUtil.createSite(driver, testUserName, UNAME_PASSWORD, itemSiteName, "", "Moderated");
        SitePage site = resolvePage(driver).render();
        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        upLoadPage.uploadFile(file.getCanonicalPath()).render();
        logout(driver);

        createTask(testUserName, UNAME_PASSWORD);
        pageUnderTest = myTasksPage.navigateToEditTaskPage(taskName, testUserName).render();
    }

    @AfterClass(groups = "Enterprise4.2")
    public void deleteSite()
    {
        siteUtil.deleteSite(testUserName, UNAME_PASSWORD, itemSiteName);
    }
    
    @Test(groups = "Enterprise4.2")
    public void checkIsLabels()
    {
        List<String> formLabels = pageUnderTest.getAllLabels();       
        assertTrue(formLabels.contains("Message:"));
        assertTrue(formLabels.contains("Owner:"));
        assertTrue(formLabels.contains("Priority:"));
        assertTrue(formLabels.contains("Due:"));
        assertTrue(formLabels.contains("Identifier:"));
    }
       
    @Test(groups = "Enterprise4.2", dependsOnMethods = "checkIsLabels")
    public void checkIsButtonDisplayed()
    {
        assertTrue(pageUnderTest.isButtonsDisplayed(SAVE_AND_CLOSE));
        assertTrue(pageUnderTest.isButtonsDisplayed(CANCEL));
        assertTrue(pageUnderTest.isButtonsDisplayed(REASSIGN));
        assertTrue(pageUnderTest.isButtonsDisplayed(TASK_DONE));
        assertTrue(pageUnderTest.isButtonsDisplayed(ADD));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "checkIsButtonDisplayed")
    public void selectStatusDropDownTest()
    {
        assertTrue(TaskStatus.NOTYETSTARTED.equals(pageUnderTest.getSelectedStatusFromDropDown()),
                "The selected status should be not yet started");
        pageUnderTest.selectStatusDropDown(TaskStatus.COMPLETED).render();
        assertTrue(TaskStatus.COMPLETED.equals(pageUnderTest.getSelectedStatusFromDropDown()), "The selected status should be completed");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectStatusDropDownTest")
    public void selectItem()
    {
        pageUnderTest.selectItem(fileName, itemSiteName);
        pageUnderTest = resolvePage(driver).render();
        assertTrue(pageUnderTest.getTaskItems().get(0).getItemName().equals(fileName), "The expected item isn't added");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectItem")
    public void selectReassign() throws Exception
    {
        myTasksPage = pageUnderTest.selectReassign(otherUser).render();
        assertFalse(myTasksPage.isTaskPresent(taskName), "Task isn't reassigned on the user '" + otherUser + "'");
        shareUtil.logout(driver);
        DashBoardPage dash = loginAs(otherUser, UNAME_PASSWORD);
        myTasksPage = dash.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task isn't reassigned on the user '" + otherUser + "'");
        pageUnderTest = myTasksPage.navigateToEditTaskPage(taskName).render();
        myTasksPage = pageUnderTest.selectReassign(testUserName).render();
        shareUtil.logout(driver);
        dash = loginAs(testUserName, UNAME_PASSWORD);
        myTasksPage = dash.getNav().selectMyTasks().render();
        assertTrue(myTasksPage.isTaskPresent(taskName), "Task isn't reassigned on the user '" + testUserName + "'");
        pageUnderTest = myTasksPage.navigateToEditTaskPage(taskName).render();
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectReassign")
    public void selectTaskDoneButtonTest()
    {
        pageUnderTest.enterComment("Task Completed");
        returnedPage = pageUnderTest.selectTaskDoneButton().render();
        assertTrue(returnedPage instanceof MyTasksPage, "The return page should be an instance of MyTasksPage page");
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectTaskDoneButtonTest")
    public void selectRejectButtonTest() throws Exception
    {
        siteUtil.createSite(driver,testUserName, UNAME_PASSWORD, modSiteName, "", "Moderated");
        SiteDashboardPage sitePage = resolvePage(driver).render();
        assertTrue(sitePage.isSiteTitle(modSiteName), "Site Dashboad page should be opened for - " + modSiteName);
        shareUtil.logout(driver);

        DashBoardPage dash = loginAs(otherUser, UNAME_PASSWORD);
        siteFinder = dash.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(modSiteName).render();
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, modSiteName);
        siteFinder.requestToJoinSite(modSiteName).render();
        shareUtil.logout(driver);
        // Rejecting the request to join
        dash = loginAs(testUserName, UNAME_PASSWORD);
        myTasksPage = dash.getNav().selectMyTasks().render();
        pageUnderTest = myTasksPage.navigateToEditTaskPage("Request to join "+modSiteName+" site").render();
        returnedPage = pageUnderTest.selectRejectButton().render();
        assertTrue(returnedPage instanceof MyTasksPage, "The return page should be an instance of MyTasksPage page");
        shareUtil.logout(driver);
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectRejectButtonTest")
    public void selectApproveButtonTest() throws Exception
    {

        DashBoardPage dash = loginAs(otherUser, UNAME_PASSWORD);
        siteFinder = dash.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(modSiteName).render();
        siteFinder.requestToJoinSite(modSiteName).render();
        shareUtil.logout(driver);

        // Approving request to join.
        dash = loginAs(testUserName, UNAME_PASSWORD);
        myTasksPage = dash.getNav().selectMyTasks().render();
        pageUnderTest = myTasksPage.navigateToEditTaskPage("Request to join "+modSiteName+" site").render();
        returnedPage = pageUnderTest.selectApproveButton().render();
        assertTrue(returnedPage instanceof MyTasksPage, "The return page should be an instance of MyTasksPage page");
    }
    
    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectReassign")
    public void isCommentTextAreaDisplayedTest() throws Exception
    {
    	assertTrue(pageUnderTest.isCommentTextAreaDisplayed()); 
    }
    
    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectReassign")
    public void readCommentFromCommentTextAreaTest() throws Exception
    {
    	pageUnderTest.enterComment("test");
    	assertTrue(pageUnderTest.readCommentFromCommentTextArea().contains("test")); 
    }
}
