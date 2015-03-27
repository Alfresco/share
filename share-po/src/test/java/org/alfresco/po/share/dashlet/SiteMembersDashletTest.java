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
package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SiteMember;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Site members dashlet page elements are in place.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "check", "alfresco-one", "Enterprise-only" })
public class SiteMembersDashletTest extends AbstractSiteDashletTest
{
    String loginName;

    @BeforeClass(groups = { "check", "alfresco-one" })
    public void setup() throws Exception
    {
        siteName = "SiteMemberTests" + System.currentTimeMillis();
        loginAs(username, password);
        String fname = anotherUser.getfName();
        String lname = anotherUser.getlName();
        loginName = fname + " " + lname;
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass(groups = { "check", "alfresco-one" })
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void instantiateMySiteDashlet()
    {
        MySitesDashlet dashlet = new MySitesDashlet(drone);
        assertNotNull(dashlet);
    }

    @Test(dependsOnMethods = "instantiateMySiteDashlet")
    public void getMembers() throws IOException
    {
        SiteMembersDashlet dashlet = new SiteMembersDashlet(drone).render();
        if (dashlet.getMembers().isEmpty())
            saveScreenShot("SiteMembersDashletTest.getMembers.empty");
        List<ShareLink> members = dashlet.getMembers();
        assertNotNull(members);
        Assert.assertFalse(members.isEmpty());
    }

    /**
     * Test process of accessing my site
     * dash let from the dash board view.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "getMembers")
    public void selectMySiteDashlet() throws Exception
    {
        navigateToSiteDashboard();
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("Site Members", title);
    }

    @Test(dependsOnMethods = "selectMySiteDashlet")
    public void navigateSiteDashboard()
    {
        drone.navigateTo(shareUrl);
        SharePage page = drone.getCurrentPage().render();
        DashBoardPage dashboard = page.getNav().selectMyDashBoard().render();
        MySitesDashlet siteDashlet = dashboard.getDashlet("my-sites").render();
        siteDashBoard = siteDashlet.selectSite(siteName).click().render();
        assertNotNull(siteDashBoard);
        Assert.assertEquals(siteDashBoard.getPageTitle(), siteName);
    }

    @Test(dependsOnMethods = "navigateSiteDashboard")
    public void selectMember() throws Exception
    {
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        AlfrescoVersion version = drone.getProperties().getVersion();
        String name = version.isCloud() ? loginName : "Administrator";
        SiteMember siteMember = dashlet.selectMember(name);
        Assert.assertEquals(siteMember.getRole(), UserRole.MANAGER);
        SharePage page = siteMember.getShareLink().click().render();

        assertNotNull(page);
        Assert.assertTrue(page.getPageTitle().contains("Profile"));
    }

    @Test(expectedExceptions = PageOperationException.class, dependsOnMethods = "selectMember")
    public void selectFakeMember() throws Exception
    {
        navigateToSiteDashboard();
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        dashlet.selectMember("bla");
    }

    @Test(dependsOnMethods = "selectFakeMember")
    public void selectAllMembers()
    {
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        SiteMembersPage siteMembersPage = dashlet.clickAllMembers();
        assertNotNull(siteMembersPage);
        assertTrue(drone.getCurrentPage().render() instanceof SiteMembersPage);
    }

    @Test(dependsOnMethods = "selectAllMembers")
    public void verifyClickOnUser()
    {
        navigateToSiteDashboard();
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        AlfrescoVersion version = drone.getProperties().getVersion();
        SharePage sharePage = null;
        if (version.isCloud())
        {
            sharePage = dashlet.clickOnUser(anotherUser.getfName() + " " + anotherUser.getlName());
        }
        else 
        {
            sharePage = dashlet.clickOnUser("Administrator");
        }
        assertNotNull(sharePage);
        assertTrue(sharePage instanceof MyProfilePage);
    }

    @Test(dependsOnMethods = "verifyClickOnUser", expectedExceptions = PageOperationException.class)
    public void verifyClickOnUserNegative()
    {
        navigateSiteDashboard();
        SiteMembersDashlet dashlet = siteDashBoard.getDashlet("site-members").render();
        dashlet.clickOnUser("gogno12345678");
    }

}
