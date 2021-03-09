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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify My Meeting Workspaces dash let page elements are in place.
 * 
 * @author Bogdan.Bocancea
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one", "bug" })
public class MyMeetingWorkspacesTest extends AbstractTest
{
    private DashBoardPage dashBoard;
    private CustomiseUserDashboardPage customizeUserDash;
    
    private String userName;
    MyMeetingWorkSpaceDashlet dashlet = null;
    
    @BeforeClass(groups = { "alfresco-one" })
    public void setup() throws Exception
    {
        userName = "UserMeeting" + System.currentTimeMillis();
        createEnterpriseUser(userName);
        shareUtil.loginAs(driver, shareUrl, userName, UNAME_PASSWORD).render();
    }

    @Test
    public void instantiateMyMeetingWorkspacesDashlet()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();

        customizeUserDash = dashBoard.getNav().selectCustomizeUserDashboard();
        customizeUserDash.render();

        dashBoard = customizeUserDash.addDashlet(Dashlets.MY_MEETING_WORKSPACES, 1).render();

        dashlet =dashletFactory.getDashlet(driver, MyMeetingWorkSpaceDashlet.class).render();
        Assert.assertNotNull(dashlet);
    }

    @Test(dependsOnMethods = "instantiateMyMeetingWorkspacesDashlet")
    public void getSites() throws Exception
    {
        dashlet = dashletFactory.getDashlet(driver, MyMeetingWorkSpaceDashlet.class).render();
        boolean isMessage = dashlet.isNoMeetingWorkspaceDisplayed();
        Assert.assertTrue(isMessage);
    }
    
    @Test(dependsOnMethods="getSites")
    public void selectMySiteDashlet() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        dashlet = dashletFactory.getDashlet(driver, MyMeetingWorkSpaceDashlet.class).render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("My Meeting Workspaces", title);
    }

}
