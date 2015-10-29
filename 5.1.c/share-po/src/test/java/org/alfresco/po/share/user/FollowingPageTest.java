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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.PeopleFinderPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify I'm Following page elements are in place.
 * Created by Olga Lokhach
 */

@Listeners(FailedTestListener.class)
public class FollowingPageTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private PeopleFinderPage peopleFinderPage;
    private MyProfilePage myProfilePage;
    private FollowingPage followingPage;
    
    private String userName = "Mike";
    private String userName1;

    @BeforeClass (groups = { "alfresco-one" }, alwaysRun = true)
    public void setup() throws Exception
    {
        userName1 = "User_" + System.currentTimeMillis();
        createEnterpriseUser(userName1);
        shareUtil.loginAs(driver, shareUrl, userName1, UNAME_PASSWORD).render();
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        peopleFinderPage = dashBoard.getNav().selectPeople().render();
        peopleFinderPage = peopleFinderPage.searchFor(userName).render();
        List<ShareLink> searchLinks = peopleFinderPage.getResults();
        if (!searchLinks.isEmpty())
        {
            for (ShareLink result : searchLinks)
            {
                if (result.getDescription().contains(userName))
                {
                    peopleFinderPage.selectFollowForUser(userName);
                }
            }
        }
        else
        {
            fail(userName + " is not found");
        }
        assertEquals(peopleFinderPage.getTextForFollowButton(userName), "Unfollow");
    }

    @Test(groups = { "alfresco-one" })
    public void openFollowingPage()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        assertNotNull(followingPage);
    }

    @Test(groups="alfresco-one", dependsOnMethods = "openFollowingPage")
    public void isHeaderTitlePresent() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        assertTrue(followingPage.isTitlePresent("Following"), "Title is incorrect");
    }

    @Test(groups="alfresco-one", dependsOnMethods = "isHeaderTitlePresent")
    public void isUserLinkPresent() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        assertTrue(followingPage.isUserLinkPresent(userName), "Can't find " + userName1);
        assertEquals(followingPage.getFollowingCount(), "1");
    }

    @Test(groups="alfresco-one", dependsOnMethods = "isUserLinkPresent")
    public void togglePrivate() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        followingPage.togglePrivate(true);
        assertTrue(followingPage.isPrivateChecked(), "Private is not checked");
    }

    @Test(groups="alfresco-one", dependsOnMethods = "togglePrivate")
    public void selectUnfollowForUser() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        myProfilePage = dashBoard.getNav().selectMyProfile().render();
        followingPage = myProfilePage.getProfileNav().selectFollowing().render();
        followingPage.selectUnfollowForUser(userName);
        assertTrue(followingPage.isNotFollowingMessagePresent(), "Not Following message isn't displayed");
        assertEquals(followingPage.getFollowingCount(), "0");
    }

}
