/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.test.FailedTestListener;
import org.springframework.social.alfresco.connect.exception.AlfrescoException;
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
        createEnterpriseUser(userName);
        shareUtil.loginAs(driver, shareUrl, userName, UNAME_PASSWORD).render();
        dashboardPage = factoryPage.getPage(driver).render();
        myprofile = dashboardPage.getNav().selectMyProfile().render();
        userSitesPage = myprofile.getProfileNav().selectSites().render();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void tearDown()
    {
    	try
    	{
    		siteUtil.deleteSite(userName, UNAME_PASSWORD, siteName1);
    		siteUtil.deleteSite(userName, UNAME_PASSWORD, siteName2);
    	}
    	catch(AlfrescoException e)
    	{
    		//Site not present therefore no need to delete.
    	}
    }

    @Test(groups = { "alfresco-one" })
    public void getSitesNoSite()
    {
        //This test must be run before any sites are created in getSites() test however it cannot
        //be run in cloud since a new user is not created so cannot guarantee no sites.
        assertTrue(userSitesPage.getSites().isEmpty());
        assertTrue(userSitesPage.isNoSiteMessagePresent());
    }

    @Test(dependsOnMethods = "getSitesNoSite", groups = { "alfresco-one" })
    public void getSites()
    {
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName1, "description", "Public");
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName2, "description", "Public");
        SiteDashboardPage siteDashboardPage = factoryPage.getPage(driver).render();
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
        assertEquals(userSitesPage.getSite(siteName1).getSiteName(), siteName1);
    }

    @Test(dependsOnMethods = "getSiteName", groups = { "alfresco-one" })
    public void toggleActivityFeed()
    {
        userSitesPage.getSite(siteName1).toggleActivityFeed(true);

        assertTrue(userSitesPage.getSite(siteName1).isActivityFeedEnabled());
        assertEquals(userSitesPage.getSite(siteName1).getActivityFeedButtonLabel(), factoryPage.getValue("user.profile.sites.disable.activity.feeds"));

        userSitesPage.getSite(siteName1).toggleActivityFeed(false);

        userSitesPage.render();
        assertFalse(userSitesPage.getSite(siteName1).isActivityFeedEnabled());
        assertEquals(userSitesPage.getSite(siteName1).getActivityFeedButtonLabel(), factoryPage.getValue("user.profile.sites.enable.activity.feeds"));
    }

    @Test(dependsOnMethods = "toggleActivityFeed", groups = { "alfresco-one" })
    public void clickOnSiteName()
    {
        SiteDashboardPage siteDashboardPage = userSitesPage.getSite(siteName1).clickOnSiteName().render();

        assertTrue(siteDashboardPage.isSiteTitle(siteName1));
    }
}
