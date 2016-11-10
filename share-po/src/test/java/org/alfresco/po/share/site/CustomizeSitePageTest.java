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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Customize Site Page Test.
 * 
 * @author Shan Nagarajan
 * @since  1.7.0
 */
@Listeners(FailedTestListener.class)
public class CustomizeSitePageTest extends AbstractSiteDashletTest
{
    private String userName = "customizePageUser" + System.currentTimeMillis();
    private CustomizeSitePage customizeSitePage;
    private WikiPage wikiPage;

    @BeforeClass(groups={"Enterprise4.1", "Enterprise-only"})
    public void createSite() throws Exception
    {
        createEnterpriseUser(userName);

        loginAs(username, password);
        
        siteName = "customizePageSite" + System.currentTimeMillis();

        siteUtil.createSite(driver, username, password, siteName, "description", "public");
        siteDashBoard = siteActions.openSiteDashboard(driver, siteName);

        AddUsersToSitePage addUsersToSitePage = siteDashBoard
                .getSiteNav().selectAddUser().render();
        siteUtil.addUsersToSite(driver, addUsersToSitePage, userName, UserRole.COLLABORATOR);
        siteDashBoard = addUsersToSitePage.getSiteNav().selectSiteDashBoard().render();
    }
    
    @AfterClass
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    @Test(groups="Enterprise-only")
    public void selectCustomizeSite() throws Exception
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        assertNotNull(customizeSitePage);
    }
    
    @Test(dependsOnMethods="selectCustomizeSite", groups="Enterprise-only")
    public void getAvailablePages()
    {
        List<SitePageType> availablePageTypes = customizeSitePage.getAvailablePages();
        assertNotNull(availablePageTypes);
        List<SitePageType> expectedPageTypes = new ArrayList<SitePageType>();
        Collections.addAll(expectedPageTypes, SitePageType.values());
        expectedPageTypes.remove(SitePageType.SITE_DASHBOARD);
        expectedPageTypes.remove(SitePageType.DOCUMENT_LIBRARY);
        assertEquals(availablePageTypes.size(), expectedPageTypes.size());
        assertEquals(availablePageTypes, expectedPageTypes);
    }
    
    @Test(dependsOnMethods="getAvailablePages", groups="Enterprise-only")
    public void getCurrentPages()
    {
        List<SitePageType> currentPageTypes = customizeSitePage.getCurrentPages();
        assertNotNull(currentPageTypes);
        List<SitePageType> expectedPageTypes = new ArrayList<SitePageType>();
        expectedPageTypes.add(SitePageType.SITE_DASHBOARD);
        expectedPageTypes.add(SitePageType.DOCUMENT_LIBRARY);
        assertEquals(currentPageTypes.get(0).getDisplayText(), "Site Dashboard");
        assertEquals(currentPageTypes.size(), expectedPageTypes.size());
        assertEquals(currentPageTypes, expectedPageTypes);
    }
    
    @Test(dependsOnMethods = "getCurrentPages", groups = "Enterprise-only")
    public void addPages()
    {
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.WIKI);
        customizeSitePage = factoryPage.getPage(driver).render();
        siteDashBoard = customizeSitePage.addPages(addPageTypes).render();
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> currentPages = customizeSitePage.getCurrentPages();
        assertTrue(currentPages.contains(SitePageType.WIKI), "Current pages are:" + currentPages);
    }
    
    @Test(dependsOnMethods = "addPages", groups = "Enterprise-only")
    public void addSiteDashboardToAvailablePages()
    {
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.SITE_DASHBOARD);

        //Site Dashboard link is displayed in the site navigation header
        SiteNavigation siteNavigation = customizeSitePage.getSiteNav().render();
        assertTrue(siteNavigation.isDashboardDisplayed());
        
        customizeSitePage = siteNavigation.selectCustomizeSite().render();
        DocumentLibraryPage documentLibraryPage = customizeSitePage.addToAvailablePagesAndClickOK(addPageTypes).render();
        
        //Site Dashboard link is not displayed in the site navigation header
        siteNavigation = documentLibraryPage.getSiteNav().render();
        assertFalse(siteNavigation.isDashboardDisplayed());
        
        //Document library is configured as a default site page
        customizeSitePage = siteNavigation.selectCustomizeSite().render();
        
        List<SitePageType> currentPages = customizeSitePage.getCurrentPages();
        List<SitePageType> availablePages = customizeSitePage.getAvailablePages();
        
        //Site Dashboard is moved from Current Site Pages section to Available Site Pages section
        assertFalse(currentPages.contains(SitePageType.SITE_DASHBOARD), "Current pages are:" + currentPages);
        assertTrue(availablePages.contains(SitePageType.SITE_DASHBOARD), "Available pages are:" + availablePages);
    }
    
    /**
     * Move Site Dashboard to Current Site Pages  
     * and check it is set as a site default page
     * 
     */
    @Test(dependsOnMethods = "addSiteDashboardToAvailablePages", groups = "Enterprise-only")
    public void addSiteDashboardToCurrentPages()
    {
        customizeSitePage.addToAvailablePages(SitePageType.DOCUMENT_LIBRARY);
        customizeSitePage.addToAvailablePages(SitePageType.WIKI);
        
        //Set site dashboard again as a default page
        List<SitePageType> addCurrentPageTypes = new ArrayList<SitePageType>();
        addCurrentPageTypes.add(SitePageType.SITE_DASHBOARD);
        
        SiteDashboardPage siteDashboardPage = customizeSitePage.addPages(addCurrentPageTypes).render();

        Assert.assertEquals(siteDashboardPage.getPageTitle(), siteName);
        Assert.assertEquals(siteDashboardPage.getPageTitleLabel(), "Public");
 
        siteDashboardPage = siteDashboardPage.clickOnPageTitle().render();
        
        Assert.assertEquals(siteDashboardPage.getPageTitle(), siteName);
        Assert.assertEquals(siteDashboardPage.getPageTitleLabel(), "Public");
        
        //check the Site Dashboard link is displayed in the site navigation header
        SiteNavigation siteNavigation = customizeSitePage.getSiteNav().render();
        assertTrue(siteNavigation.isDashboardDisplayed());
        siteDashboardPage = siteNavigation.selectSiteDashBoard().render();
        Assert.assertEquals(siteDashboardPage.getPageTitle(), siteName);
        Assert.assertEquals(siteDashboardPage.getPageTitleLabel(), "Public");
        customizeSitePage = siteDashboardPage.getSiteNav().selectCustomizeSite().render();
    }
        
    /**
     * Current Site Pages - display order 
     * First page in Current Site Pages is default site page
     * 
     */
    @Test(dependsOnMethods = "addSiteDashboardToCurrentPages", groups = "Enterprise-only")
    public void addWikiPageAsDefaultSitePage()
    {
        customizeSitePage.addToAvailablePages(SitePageType.SITE_DASHBOARD);
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.WIKI);
        addPageTypes.add(SitePageType.DOCUMENT_LIBRARY);
        WikiPage wikiPage = customizeSitePage.addPages(addPageTypes).render();
        
        //check that the first page (wiki page) is set as a site dashboard
        wikiPage.clickOnPageTitle().render();
        Assert.assertTrue(wikiPage.getTitle().indexOf("Wiki") != -1);
        
        //check that Wiki link works
        SiteNavigation siteNavigation = customizeSitePage.getSiteNav().render();
        assertTrue(siteNavigation.isWikiDisplayed());
        wikiPage = siteNavigation.selectWikiPage().render();
        Assert.assertTrue(wikiPage.getTitle().indexOf("Wiki") != -1);
        
        customizeSitePage = wikiPage.getSiteNav().selectCustomizeSite().render();
    }
    
    /**
     * Site Dashboard in More drop down
     * 
     */
    @Test(dependsOnMethods = "addWikiPageAsDefaultSitePage", groups = "Enterprise-only")
    public void addSideDashboardToMorePages()
    {
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DATA_LISTS);
        addPageTypes.add(SitePageType.LINKS);
        addPageTypes.add(SitePageType.BLOG);
        addPageTypes.add(SitePageType.DISCUSSIONS);
        addPageTypes.add(SitePageType.CALENDER);
        addPageTypes.add(SitePageType.SITE_DASHBOARD);
        
        WikiPage wikiPage = customizeSitePage.addPages(addPageTypes).render();
        SiteNavigation siteNavigation = wikiPage.getSiteNav().render();
        
        Assert.assertTrue(siteNavigation.isMoreDisplayed());
        //click on Site Dashboard in More drop down
        SiteDashboardPage siteDashboardPage =  siteNavigation.selectSiteDashboardPage().render();

        //check the Site Dashboard link is displayed in the site navigation header
        siteNavigation = customizeSitePage.getSiteNav().render();
        assertTrue(siteNavigation.isDashboardDisplayed());
        siteDashboardPage = siteNavigation.selectSiteDashBoard().render();
        Assert.assertEquals(siteDashboardPage.getPageTitle(), siteName);
        Assert.assertEquals(siteDashboardPage.getPageTitleLabel(), "Public");
        
        Assert.assertEquals(siteDashboardPage.getPageTitle(), siteName);
        Assert.assertEquals(siteDashboardPage.getPageTitleLabel(), "Public");
 
        wikiPage = siteDashboardPage.clickOnPageTitle().render();
        
        Assert.assertTrue(wikiPage.getTitle().indexOf("Wiki") != -1);
        customizeSitePage = wikiPage.getSiteNav().selectCustomizeSite().render();
    }
    
    /**
     * Root site links display default site page, not error page
     */
    @Test(dependsOnMethods = "addSideDashboardToMorePages", groups = "Enterprise-only")
    public void rootSiteLinkToDefaultPage()
    {  
        String siteUrl = shareUrl + "/page/site/" + siteName;
        driver.navigate().to(siteUrl);  
        wikiPage = resolvePage(driver).render();
        Assert.assertTrue(wikiPage.getTitle().indexOf("Wiki") != -1);
        customizeSitePage = wikiPage.getSiteNav().selectCustomizeSite().render();
    }
    
    /**
     * Site Redirect page displayed with message for site manager that site has no 
     * default page configured 
     * 
     */
    @Test(dependsOnMethods = "rootSiteLinkToDefaultPage", groups = "Enterprise-only")
    public void noDefaultSitePageMessageForSiteManager()
    {
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.WIKI);
        addPageTypes.add(SitePageType.DOCUMENT_LIBRARY);
        addPageTypes.add(SitePageType.DATA_LISTS);
        addPageTypes.add(SitePageType.LINKS);
        addPageTypes.add(SitePageType.BLOG);
        addPageTypes.add(SitePageType.DISCUSSIONS);
        addPageTypes.add(SitePageType.SITE_DASHBOARD);
        addPageTypes.add(SitePageType.CALENDER);
             
        customizeSitePage.addToAvailablePagesAndClickOK(addPageTypes).render();
        
        Assert.assertTrue(resolvePage(driver).getTitle().indexOf("Site Redirect") != -1);
        logout(driver);
    }
   
    /**
     * Site Redirect page displayed with message for site manager that site has no 
     * default page configured 
     * 
     */
    @Test(dependsOnMethods = "noDefaultSitePageMessageForSiteManager", groups = "Enterprise-only")
    public void noDefaultSitePageMessageForSiteUser() throws Exception
    {    
        loginAs(userName, "password");
        String siteUrl = shareUrl + "/page/site/" + siteName;
        driver.navigate().to(siteUrl);
               
        //Site dassboard displayed instead of site redirect
        Assert.assertTrue(resolvePage(driver).getTitle().indexOf("Site Redirect") != -1);
        logout(driver);
    }
}
