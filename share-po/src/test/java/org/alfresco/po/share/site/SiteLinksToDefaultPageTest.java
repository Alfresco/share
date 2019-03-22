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
package org.alfresco.po.share.site;


import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.EVERYONES_ACTIVITIES;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.TODAY;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.MEMBERSHIPS;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.dashlet.ActivityShareLink;
import org.alfresco.po.share.dashlet.ConfigureSavedSearchDialogBoxPage;
import org.alfresco.po.share.dashlet.EditingContentDashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet;
import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SearchLimit;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.LiveSearchDropdown;
import org.alfresco.po.share.search.LiveSearchSiteResult;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.steps.AdminActions;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.StaleElementReferenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for links to default site page
 * 
 * @author jcule
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class SiteLinksToDefaultPageTest extends AbstractSiteDashletTest
{
    private static Log logger = LogFactory.getLog(SiteLinksToDefaultPageTest.class);

    private String siteName = "siteWithDefaultPage" + System.currentTimeMillis();
    private String userName = "userName" + System.currentTimeMillis();
    private DashBoardPage dashBoard;
    private CustomizeSitePage customizeSitePage;
    private DocumentLibraryPage documentLibraryPage;
    private UserSitesPage userSitesPage;
    private CustomiseUserDashboardPage customiseUserDashBoard;
    private ConfigureSavedSearchDialogBoxPage configureSavedSearchDialogBoxPage;
    
    @Autowired AdminActions adminActions;
    
    @BeforeClass(groups = { "alfresco-one" })
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
    	adminActions.createEnterpriseUser(driver, userName, userName, userName, userName + "@test.com", userName);
        
    	CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
        siteDashBoard = createSitePage.createNewSite(siteName).render();
       
        AddUsersToSitePage addUsersToSitePage = siteDashBoard.getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName, UserRole.COLLABORATOR);
        addUsersToSitePage = resolvePage(driver).render();
        customizeSitePage = addUsersToSitePage.getSiteNav().selectCustomizeSite().render();
        
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.SITE_DASHBOARD);

        // Site Dashboard link is displayed in the site navigation header
        SiteNavigation siteNavigation = customizeSitePage.getSiteNav().render();
        assertTrue(siteNavigation.isDashboardDisplayed());

        // Document library is configured as a default site page after moving Site Dashboard to Available Pages section
        customizeSitePage = siteNavigation.selectCustomizeSite().render();
        documentLibraryPage = customizeSitePage.addToAvailablePagesAndClickOK(addPageTypes).render();
        assertNotNull(documentLibraryPage);
        File file = siteUtil.prepareFile("fileInSiteWithDefaultPage", "file text");
        fileName = file.getName();
        UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
        
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectEditOfflineAndCloseFileWindow();
        documentLibraryPage = resolvePage(driver).render();
    }
    
    /**
     * Returns true if activity is displayed in the dashlet
     * 
     * @param dashlet
     * @return
     */
    private boolean isActivityDisplayed(MyActivitiesDashlet dashlet)
    {
        boolean isActivityDisplayed = false;
        try
        {
            List <ActivityShareLink> activities = dashlet.getActivities();
            for (ActivityShareLink link : activities)
            {
                if (link.getSite().getDescription().indexOf(siteName) != -1)
                {
                    isActivityDisplayed = true;
                }
            }
        } catch (StaleElementReferenceException se)
        {
            return isActivityDisplayed(dashlet);
        }    
        return isActivityDisplayed;
    }

  
    /**
     * Link to the most recent site in Sites drop down in the navigation header
     * 
     * @throws Exception
     */
    @Test(priority = 1)
    public void testSelectSiteFromRecentSites() throws Exception
    {
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        documentLibraryPage = dashBoard.getNav().selectMostRecentSite().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }

    /**
     * Link to my sites in Sites drop down in the navigation header
     * 
     * @throws Exception
     */
    @Test(priority = 2)
    public void testSelectSiteFromMySites() throws Exception
    {
        userSitesPage = documentLibraryPage.getNav().selectMySites().render();
        documentLibraryPage = userSitesPage.clickOnSiteName(siteName).render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }

    /**
     * Link to sites finder in Sites drop down in the navigation header
     * 
     * @throws Exception
     */
    @Test(priority = 3)
    public void testSelectSiteFromSiteFinder() throws Exception
    {
        SiteFinderPage siteFinderPage = documentLibraryPage.getNav().selectSearchForSites().render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, siteName);
        siteFinderPage.selectSite(siteName).render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }

    /**
     * Site name link in favourites in Sites drop down in the navigation header
     * 
     * @throws Exception
     */
    @Test(priority = 6)
    public void testSelectSiteFromFavourites() throws Exception
    {
        documentLibraryPage = documentLibraryPage.getNav().selectSiteFromFavourties(siteName).render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }

    
    /**
     * Link to site from live site search drop down
     * 
     * @throws Exception
     */
    @Test(priority = 7)
    public void testSelectSiteNameFromLiveSearch() throws Exception
    {
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        SearchBox search = dashBoard.getSearch();
        LiveSearchDropdown liveSearchResultPage = null;
        List<LiveSearchSiteResult> liveSearchSiteResults = new ArrayList<LiveSearchSiteResult>();
        int counter = 0;
        int waitInMilliSeconds = 8000;
        while (counter < 3)
        {
            liveSearchResultPage = search.liveSearch(siteName).render();
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                }
            }
            liveSearchSiteResults = liveSearchResultPage.getSearchSitesResults();
            if ((liveSearchSiteResults.size() == 1) && liveSearchSiteResults.get(0).getSiteName().getDescription().equalsIgnoreCase(siteName))
            {
                break;
            }
            else
            {
                counter++;
                dashBoard = dashBoard.getNav().selectMyDashBoard().render();
            }
            // double wait time to not overdo solr search
            waitInMilliSeconds = (waitInMilliSeconds * 2);

        }
        documentLibraryPage = liveSearchSiteResults.get(0).clickOnSiteTitle().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }
    
    /**
     * Back to site link on advance search page
     * 
     * @throws Exception
     */
    @Test(priority = 8, enabled = false)
    public void testSelectBackToSiteFromAdvanceSearch() throws Exception
    {
        AdvanceSearchPage advanceSearchPage = documentLibraryPage.getNav().selectAdvanceSearch().render();
        documentLibraryPage = advanceSearchPage.clickBackToSite().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }

    
    /**
     * Link to site from the search result on the faceted search page
     * 
     * @throws Exception
     */
    @Test(priority = 9)
    public void testSelectSiteNameFromFacetedSearch() throws Exception
    {
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        SearchBox search = dashBoard.getSearch();
        FacetedSearchPage resultPage = search.search(fileName).render();
        Assert.assertTrue(resultPage.hasResults(),"selectSiteNameFromFacetedSearch");
        String firstResult = resultPage.getResults().get(0).getSite();
        Assert.assertTrue(firstResult.indexOf("siteWithDefaultPage") != -1);
        documentLibraryPage = resultPage.selectItem("siteWithDefaultPage").render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }
        
    
    /**
     * Back to site link on search page
     * 
     * @throws Exception
     */
    
    @Test(priority = 10, enabled = false)
    public void testSelectBackToSiteFromSearch() throws Exception
    {
        AdvanceSearchPage advanceSearchPage = documentLibraryPage.getNav().selectAdvanceSearch().render();
        documentLibraryPage = advanceSearchPage.clickBackToSite().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }
    
    
    /**
     * Link to site from Site Content dashlet on the site dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 11)
    public void testSelectSiteNameFromSiteContentDashlet() throws Exception
    {
        customizeSitePage = documentLibraryPage.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.SITE_DASHBOARD);
        
        documentLibraryPage = customizeSitePage.addPages(addPageTypes).render();
        SiteNavigation siteNavigation = documentLibraryPage.getSiteNav().render();
        siteDashBoard =  siteNavigation.selectSiteDashboardPage().render();
 
        // This dashlet should not take over a minute to display content list.
        RenderTime timer = new RenderTime(60000);
        SiteContentDashlet dashlet;
        while (true)
        {
            timer.start();
            try
            {
                driver.navigate().refresh();
                dashlet = siteDashBoard.getDashlet("site-contents").render();
                if (!dashlet.getSiteContents().isEmpty())
                {
                    break;
                }    
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        List<ShareLink> list = dashlet.getSiteContents();
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());

        documentLibraryPage = dashlet.selectSite(siteName).click().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }
      
    
    /**
     * Link to site from Site Activities dashlet on the site dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 12)
    public void testSelectSiteNameFromSiteActivitiesDashlet() throws Exception
    {
        SiteNavigation siteNavigation = documentLibraryPage.getSiteNav().render();
        siteDashBoard =  siteNavigation.selectSiteDashboardPage().render();
 
        // This dashlet should not take over a minute to display content list.
        RenderTime timer = new RenderTime(60000);
        SiteActivitiesDashlet dashlet;
        while (true)
        {
            timer.start();
            try
            {
                driver.navigate().refresh();
                dashlet = siteDashBoard.getDashlet("site-activities").render();
                dashlet.selectUserFilter(EVERYONES_ACTIVITIES);
                dashlet.selectTypeFilter(MEMBERSHIPS);
                dashlet.selectHistoryFilter(TODAY);
                if (!dashlet.getSiteActivities(LinkType.Document).isEmpty())
                {
                    break;
                }    
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        
        List<ShareLink> siteLinks = dashlet.getSiteActivities(LinkType.Document);
        
        Assert.assertNotNull(siteLinks);
        if (siteLinks.isEmpty())
            saveScreenShot("getActivities.empty");
        Assert.assertFalse(siteLinks.isEmpty());


        documentLibraryPage = dashlet.selectLink(siteName, LinkType.Document).click().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
    }
    
       
    /**
     * Link to site from Site Search dashlet on the user dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 13)
    public void testSelectSiteNameFromSiteSearchDashlet() throws Exception
    {
 
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard().render();
        dashBoard = customiseUserDashBoard.addDashlet(Dashlets.SITE_SEARCH, 1).render();
 
        SiteSearchDashlet searchDashlet = dashBoard.getDashlet("site-search").render();
        Assert.assertNotNull(searchDashlet);
 
        searchDashlet.search(fileName).render();
        
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertNotNull(items);
        Assert.assertEquals(items.size(), 1);
        ShareLink siteName = items.get(0).getSiteName();
        Assert.assertNotNull(siteName);
        documentLibraryPage = siteName.click().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
        
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard().render();
        dashBoard = customiseUserDashBoard.removeDashlet(Dashlets.SITE_SEARCH);
    }   
  
    
    /**
     * Link to site from Saved Search dashlet on the user dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 14)
    public void testSelectSiteNameFromSavedSearchDashlet() throws Exception
    {
 
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard().render();
        dashBoard = customiseUserDashBoard.addDashlet(Dashlets.SAVED_SEARCH, 1).render();
 
        SavedSearchDashlet savedSearchDashlet = dashBoard.getDashlet("saved-search").render();
        Assert.assertNotNull(savedSearchDashlet);
 
        
        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        
        configureSavedSearchDialogBoxPage.setSearchTerm(fileName);
        configureSavedSearchDialogBoxPage.setTitle("Test Saved Search");
        configureSavedSearchDialogBoxPage.setSearchLimit(SearchLimit.HUNDRED);
        dashBoard = configureSavedSearchDialogBoxPage.clickOnOKButton().render();
        
        
        List<SiteSearchItem> items = savedSearchDashlet.getSearchItems();
        Assert.assertNotNull(items);
        Assert.assertEquals(items.size(), 1);
        ShareLink siteName = items.get(0).getSiteName();
        Assert.assertNotNull(siteName);
        documentLibraryPage = siteName.click().render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
        
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard().render();
        dashBoard = customiseUserDashBoard.removeDashlet(Dashlets.SAVED_SEARCH);
    }   
  
    
    
    
    /**
     * Link to site from Saved Search dashlet on the user dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 15)
    public void testSelectSiteNameFromEditingContentDashlet() throws Exception
    {
 
        //dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard().render();
        dashBoard = customiseUserDashBoard.addDashlet(Dashlets.CONTENT_I_AM_EDITING, 1).render();
 
        EditingContentDashlet editingContentDashlet = dashBoard.getDashlet("editing-content").render();
        Assert.assertNotNull(editingContentDashlet);
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(fileName, siteName), "Item is not found");
        documentLibraryPage = editingContentDashlet.clickSite(siteName).render();
        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
        
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
        customiseUserDashBoard = dashBoard.getNav().selectCustomizeUserDashboard().render();
        dashBoard = customiseUserDashBoard.removeDashlet(Dashlets.CONTENT_I_AM_EDITING);
    }   
    
 
    
    /**
     * Link to site from My Sites dashlet on the user dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 16)
    public void testSelectSiteNameFromMySitesDashlet() throws Exception
    {
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
  
//        long minimunRenderTime = 60000;
//        long renderTime = popupRendertime;
//        if (renderTime < minimunRenderTime)
//        {
//            renderTime = minimunRenderTime;
//        }
//        RenderTime timer = new RenderTime(renderTime);
//        while (true)
//        {
//            timer.start();
            try
            {
                driver.navigate().refresh();
                MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
                ShareLink siteLink = dashlet.selectSite(siteName);
                documentLibraryPage = siteLink.click().render();
                // break;
            }
            catch (PageException e)
            {
            }
//            finally
//            {
//                timer.end();
//            }
//        }

        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
        
    }
         
    
    /**
     * Link to site from My Activities dashlet on the user dashboard
     * 
     * @throws Exception
     */
    @Test(priority = 17)
    public void testSelectSiteNameFromMyActivitiesDashlet() throws Exception
    {
        dashBoard = documentLibraryPage.getNav().selectMyDashBoard().render();
  
//        long minimunRenderTime = 60000;
//        long renderTime = popupRendertime;
//        if (renderTime < minimunRenderTime)
//        {
//            renderTime = minimunRenderTime;
//        }
//        RenderTime timer = new RenderTime(renderTime);
//        while (true)
//        {
//            timer.start();
            try
            {
                driver.navigate().refresh();
                MyActivitiesDashlet dashlet = dashBoard.getDashlet("activities").render();
                dashlet.selectOptionFromUserActivities("My activities").render();
                dashlet.selectOptionFromActivitiesType("content");
                dashlet.selectOptionFromHistoryFilter(TODAY);
                if(isActivityDisplayed(dashlet))
                {
                    ActivityShareLink siteLink = dashlet.selectLink(siteName, LinkType.Site);
                    documentLibraryPage = siteLink.getSite().click().render();
//                    break;
                }
            }
            catch (PageException e)
            {
            }
//            finally
//            {
//                timer.end();
//            }
//        }

        assertTrue(documentLibraryPage.getTitle().indexOf("Document Library") != -1);
        
    }
  
}
