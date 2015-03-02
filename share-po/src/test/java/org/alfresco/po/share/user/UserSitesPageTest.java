/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * User Sites List Page Test
 * 
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class UserSitesPageTest extends AbstractTest
{
    private String siteName1;
    private String siteName2;
    private String userName;
    private AlfrescoVersion version;

    private DashBoardPage dashboardPage;
    private MyProfilePage myprofile;
    private UserSitesPage userSitesPage;
    private UserSiteItem userSiteItem;

    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        siteName1 = "UserSitesPage-1" + System.currentTimeMillis();
        siteName2 = "UserSitesPage-2" + System.currentTimeMillis();
        userName = "UserSitesPage" + System.currentTimeMillis();

        version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            createEnterpriseUser(userName);

            ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
        }

        dashboardPage = FactorySharePage.resolvePage(drone).render();
        myprofile = dashboardPage.getNav().selectMyProfile().render();
        userSitesPage = myprofile.getProfileNav().selectSites().render();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName1);
        SiteUtil.deleteSite(drone, siteName2);
    }

    @Test(groups = { "alfresco-one" })
    public void getSitesNoSite()
    {
        //This test must be run before any sites are created in getSites() test however it cannot
        //be run in cloud since a new user is not created so cannot guarantee no sites.
        if (!version.isCloud())
        {
            assertTrue(userSitesPage.getSites().isEmpty());
            assertTrue(userSitesPage.isNoSiteMessagePresent());
        }
    }

    @Test(dependsOnMethods = "getSitesNoSite", groups = { "alfresco-one" })
    public void getSites()
    {
        SiteUtil.createSite(drone, siteName1, "description", "Public");
        SiteUtil.createSite(drone, siteName2, "description", "Public");

        SiteDashboardPage siteDashboardPage = FactorySharePage.resolvePage(drone).render();
        myprofile = siteDashboardPage.getNav().selectMyProfile().render();
        userSitesPage = myprofile.getProfileNav().selectSites().render();

        List<UserSiteItem> userSiteItems = userSitesPage.getSites();
        List<String> siteNames = new ArrayList<String>(2);
        siteNames.add(siteName1);
        siteNames.add(siteName2);
        
        assertTrue(userSiteItems.size()>=2);
        for (UserSiteItem userSiteItem : userSiteItems)
        {
            siteNames.contains(userSiteItem.getSiteName());
        }
    }

    @Test(dependsOnMethods = "getSites", groups = { "alfresco-one" })
    public void getSiteName()
    {
        userSiteItem = userSitesPage.getSite(siteName1);

        assertEquals(userSiteItem.getSiteName(), siteName1);
    }

    @Test(dependsOnMethods = "getSiteName", groups = { "alfresco-one" })
    public void toggleActivityFeed()
    {
        userSitesPage = userSiteItem.toggleActivityFeed(true).render();
        userSiteItem = userSitesPage.getSite(siteName1);

        assertTrue(userSiteItem.isActivityFeedEnabled());
        assertEquals(userSiteItem.getActivityFeedButtonLabel(), drone.getValue("user.profile.sites.disable.activity.feeds"));

        userSitesPage = userSiteItem.toggleActivityFeed(false).render();
        userSiteItem = userSitesPage.getSite(siteName1);

        assertFalse(userSiteItem.isActivityFeedEnabled());
        assertEquals(userSiteItem.getActivityFeedButtonLabel(), drone.getValue("user.profile.sites.enable.activity.feeds"));
    }

    @Test(dependsOnMethods = "toggleActivityFeed", groups = { "alfresco-one" })
    public void clickOnSiteName()
    {
        SiteDashboardPage siteDashboardPage = userSiteItem.clickOnSiteName().render();

        assertTrue(siteDashboardPage.isSiteTitle(siteName1));
    }
}
