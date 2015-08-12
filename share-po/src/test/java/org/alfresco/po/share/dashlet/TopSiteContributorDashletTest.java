/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.dashlet;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.task.EditTaskPage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * TopSiteContributorDashlet test class for top site contributor report dashlet page object
 * 
 * @author jcule
 */
@Listeners(FailedTestListener.class)
@Test(groups = "QA-1661-bug")
public class TopSiteContributorDashletTest extends AbstractSiteDashletTest
{

    private static final String TOP_SITE_CONTRIBUTOR_REPORT = "top-site-contributor-report";
    private TopSiteContributorDashlet topSiteContributorDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private DashBoardPage dashBoard;
    private InviteMembersPage membersPage;
    private SiteDashboardPage siteDashBoard;
    
    private String random1 = "User_" + System.currentTimeMillis() + "_" + 1;
    private String random2 = "User_" + System.currentTimeMillis() + "_" + 2;
    private String random3 = "User_" + System.currentTimeMillis() + "_" + 3;
    private String random4 = "User_" + System.currentTimeMillis() + "_" + 4;
    private String random5 = "User_" + System.currentTimeMillis() + "_" + 5;
    
    private static int firstNumberOfFiles = 7;
    private static int secondNumberOfFiles = 4;
    private static int thirdNumberOfFiles = 1;
    private static int fourthNumberOfFiles = 6;
    private static int fifthNumberOfFiles = 10;



    @BeforeTest
    public void prepare()
    {
        siteName = "topsitecontributordashlettest" + System.currentTimeMillis();
    }

    @BeforeClass
    public void loadFiles() throws Exception
    {
        // uploadDocument();
        dashBoard = loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName, "description", "moderated");
        
        logout(driver);

        createUsersAndFiles(firstNumberOfFiles, random1);
        createUsersAndFiles(secondNumberOfFiles, random2);
        createUsersAndFiles(thirdNumberOfFiles, random3);
        createUsersAndFiles(fourthNumberOfFiles, random4);
        createUsersAndFiles(fifthNumberOfFiles, random5);

        
    }
    
    @AfterClass
    public void deleteSite()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    /**
     * Creates a user, invites it to the site as a collaborator and logs out
     * Collaborator logs in and uploads files to site's document library
     * 
     * @param numberOfFiles int
     * @param random1 String
     * @throws Exception
     */
    
    private void createUsersAndFiles(int numberOfFiles, String random1) throws Exception
    {
 
        createEnterpriseUser(random1);
        Thread.sleep(16000);
        loginAs(username, password).render();

        navigateToSiteDashboard();
        SitePage site = resolvePage(driver).render();
        membersPage = site.getSiteNav().selectInvite().render();
        List<String> users = membersPage.searchUser("User_");
        for (String user : users)
        {    
            if(user.equalsIgnoreCase("(" + random1 + ")"))
            {        
                membersPage.clickAddUser(random1);
            }
        }    
        membersPage.selectInviteeAndAssignRole("(" + random1 + ")", UserRole.COLLABORATOR);
        membersPage.clickInviteButton();
        logout(driver);
        loginAs(random1, UNAME_PASSWORD).render();
        MyTasksDashlet task = dashBoard.getDashlet("tasks").render();
        EditTaskPage editTaskPage = task.clickOnTask(siteName).render();
        dashBoard = editTaskPage.selectAcceptButton().render();

        navigateToSiteDashboard();
        site = resolvePage(driver).render();

        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();

        for (int i = 0; i < numberOfFiles; i++)
        {
            String random = UUID.randomUUID().toString();
            File file = siteUtil.prepareFile(random, random, ".txt");
            docPage = site.getSiteNav().selectDocumentLibrary().render();
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();

        }
        logout(driver);
    }

    /**
     * Drags and drops Top site contributor report dashlet to site's dashboard
     */
    @Test
    public void instantiateDashlet() throws Exception
    {
        DashBoardPage boardPage = loginAs(username, password).render();
        SiteFinderPage siteFinder = boardPage.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName).render();
        siteDashBoard = siteFinder.selectSite(siteName).render();
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();;
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.TOP_SITE_CONTRIBUTOR_REPORT, 2).render();
        topSiteContributorDashlet = siteDashBoard.getDashlet(TOP_SITE_CONTRIBUTOR_REPORT).render();
        Assert.assertNotNull(topSiteContributorDashlet);
    }
    
    
    @Test(dependsOnMethods = "instantiateDashlet")
    public void testTopSiteContributorData() throws Exception
    {
        verifyDashletData();
        
        //select Today option from calendar drop down
        topSiteContributorDashlet.clickOnCalendarDropdown();
        topSiteContributorDashlet.clickCalendarTodayOption();
        
        //Verify results
        verifyDashletData();
        
        //select Last 7 days option from calendar drop down
        topSiteContributorDashlet.clickOnCalendarDropdown();
        topSiteContributorDashlet.clickCalendarLastSevenDaysOption();
        
        //Verify results
        verifyDashletData();
        
        //select Past Year days option from calendar drop down
        topSiteContributorDashlet.clickOnCalendarDropdown();
        topSiteContributorDashlet.clickCalendarPastYearOption();
        
        //Verify results
        verifyDashletData();
        
        
        //select Date Range option from calendar drop down
        topSiteContributorDashlet.clickOnCalendarDropdown();
        topSiteContributorDashlet.clickCalendarDateRangeOption();
        
        //Verify results
        verifyDashletData();
         
    }
    
    /**
     * Verifies that dashlat displays correct data
     * 
     * @throws Exception
     */
    private void verifyDashletData() throws Exception
    {
        List<String> users = topSiteContributorDashlet.getTooltipUsers();
        List<String> usersData = topSiteContributorDashlet.getTooltipUserData();
        
        Assert.assertTrue(users.contains(random1));
        Assert.assertTrue(users.contains(random2));
        Assert.assertTrue(users.contains(random3));
        Assert.assertTrue(users.contains(random4));
        Assert.assertTrue(users.contains(random5)); 
        
        Assert.assertEquals(usersData.size(), 5);
        
        for(String userData : usersData)
        {
           String [] tokens = userData.split("-");
           String user = tokens[0];
           String fileCount = tokens[1];
           
           if (user.trim().equalsIgnoreCase(random1))
           {
               Assert.assertEquals(Integer.parseInt(fileCount), firstNumberOfFiles);
           }
           
           if (user.trim().equalsIgnoreCase(random2))
           {
               Assert.assertEquals(Integer.parseInt(fileCount), secondNumberOfFiles);
           }
           
           if (user.trim().equalsIgnoreCase(random3))
           {
                Assert.assertEquals(Integer.parseInt(fileCount), thirdNumberOfFiles);
           }
          
           if (user.trim().equalsIgnoreCase(random4))
           {
                Assert.assertEquals(Integer.parseInt(fileCount), fourthNumberOfFiles);
           }

           if (user.trim().equalsIgnoreCase(random5))
           {
                Assert.assertEquals(Integer.parseInt(fileCount), fifthNumberOfFiles);
           }
        }

    }
}
