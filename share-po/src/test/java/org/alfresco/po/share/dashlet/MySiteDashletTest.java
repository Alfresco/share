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
package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.dashlet.MySitesDashlet.FavouriteType;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify My Site dash let page elements are in place.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one"})
public class MySiteDashletTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(MySiteDashletTest.class);
    private DashBoardPage dashBoard;
    private String siteName;
    private String newSiteName;
    private String sampleSiteFullName = "Sample: Web Site Design Project";

    @BeforeClass(groups={"alfresco-one"})
    public void setup()throws Exception
    {
        siteName = "MySiteTests" + System.currentTimeMillis();
        newSiteName = "NewSiteTests" + System.currentTimeMillis();
        dashBoard = loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
    }

    @AfterClass(groups={"alfresco-one"})
    public void deleteSite()
    {
        try
        {
            siteUtil.deleteSite(username, password, siteName);
        }
        catch(Exception e)
        {
            logger.error("tear down was unable to delete site", e);
        }
        
    }
    
    @Test
    public void instantiateMySiteDashlet()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet dashlet = dashletFactory.getDashlet(driver, MySitesDashlet.class).render();
        Assert.assertNotNull(dashlet);
    }
    
    @Test(dependsOnMethods="instantiateMySiteDashlet")
    public void getSites() throws Exception
    {
        MySitesDashlet dashlet = dashletFactory.getDashlet(driver, MySitesDashlet.class).render();
        if (dashlet.getSites().isEmpty()) saveScreenShot("MySiteDashletTest.getSites.empty");
        List<ShareLink> sites = dashlet.getSites();
        Assert.assertNotNull(sites);
        Assert.assertEquals(false, sites.isEmpty());
    }
    
    /**
     * Test process of accessing my site
     * dash let from the dash board view.
     * @throws Exception 
     */
    @Test(dependsOnMethods="getSites")
    public void selectMySiteDashlet() throws Exception
    {
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("My Sites", title);
    }
    
    @Test(dependsOnMethods="selectFakeSite")
    public void selectSite() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        ShareLink link = dashlet.selectSite(siteName);
        SitePage sitePage = link.click().render();

        Assert.assertNotNull(sitePage);
        Assert.assertEquals(true, sitePage.isSite(siteName));
    }
    
    @Test(dependsOnMethods="selectMySiteDashlet" ,expectedExceptions = PageException.class)
    public void selectFakeSite() throws Exception
    {
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        dashlet.selectSite("bla");
    }

    /**
     * Checks the site is favourite.
     */
    @Test(dependsOnMethods = "selectSite")
    public void isSiteFavouriteTest()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        //Site created by api is not a favorite by default.
        Assert.assertFalse(dashlet.isSiteFavourite(siteName));
        dashlet.selectFavorite(siteName);
        Assert.assertTrue(dashlet.isSiteFavourite(siteName));
        Assert.assertFalse(dashlet.isSiteFavourite(sampleSiteFullName));
    }
    
    @Test(dependsOnMethods = "isSiteFavouriteTest")
    public void selectMyFavouriteSite()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        dashBoard = dashlet.selectMyFavourites(FavouriteType.ALL).render();
        dashlet = dashBoard.getDashlet("my-sites").render();
        Assert.assertTrue(dashlet.selectSite(siteName).click() instanceof SiteDashboardPage);
    }

    @Test(dependsOnMethods = "selectMyFavouriteSite")
    public void createSiteFromSiteDashlet() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        Assert.assertTrue(dashlet.isCreateSiteButtonDisplayed(), "Create Site button isn't displayed");
        CreateSitePage createSitePage = dashlet.clickCreateSiteButton().render();
        SiteDashboardPage siteDashboardPage = createSitePage.createNewSite(newSiteName, "description").render();
        assertTrue(siteDashboardPage.isSiteTitle(newSiteName), "Site Dashboard page for created site " + newSiteName + " isn't opened");
    }

    @Test(dependsOnMethods = "createSiteFromSiteDashlet")
    public void deleteSiteFromSiteDashlet() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        dashlet.deleteSite(newSiteName).render();
        dashlet = dashBoard.getDashlet("my-sites").render();
        dashlet.selectMyFavourites(MySitesDashlet.FavouriteType.ALL).render();
        assertFalse(dashlet.isSitePresent(newSiteName), newSiteName + " is found ");
    }

}
