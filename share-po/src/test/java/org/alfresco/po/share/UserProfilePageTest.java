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
package org.alfresco.po.share;

import org.alfresco.po.AbstractTest;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify User profile page elements are in place.
 * 
 * @author Chiran
 * @since 1.7.1
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class UserProfilePageTest extends AbstractTest
{
    String userinfo = "puser" + System.currentTimeMillis() + "@test.com";
    private DashBoardPage dashBoard;
    private UserSearchPage page;
    UserSearchPage results;

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        dashBoard = loginAs(username, password);
        UserSearchPage page = dashBoard.getNav().getUsersPage().render();
        NewUserPage newPage = page.selectNewUser().render();
        newPage.inputFirstName(userinfo);
        newPage.inputLastName(userinfo);
        newPage.inputEmail(userinfo);
        newPage.inputUsername(userinfo);
        newPage.inputPassword(userinfo);
        newPage.inputVerifyPassword(userinfo);
        UserSearchPage userCreated = newPage.selectCreateUser().render();
        page = userCreated.searchFor(userinfo).render();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void deleteNullUserMethod() throws Exception
    {
        page = dashBoard.getNav().getUsersPage().render();
        results = page.searchFor(userinfo).render();
        Assert.assertTrue(results.hasResults());
        results.clickOnUser(null);
        Assert.fail("IllegalArgumentException Expected");
    }

    @Test(dependsOnMethods = "deleteNullUserMethod")
    public void deleteUser() throws Exception
    {
        page = dashBoard.getNav().getUsersPage().render();
        Assert.assertTrue(page.isLogoPresent());
        Assert.assertTrue(page.isTitlePresent());
        results = page.searchFor(userinfo).render();

        Assert.assertTrue(results.hasResults());

        UserProfilePage userProfile = results.clickOnUser(userinfo).render();
        results = userProfile.deleteUser().render();
        results = page.searchFor(userinfo).render();

        Assert.assertFalse(results.hasResults());
    }
}
