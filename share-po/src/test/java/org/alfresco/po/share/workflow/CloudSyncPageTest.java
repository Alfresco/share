/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.workflow;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.HtmlPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


/**
 * Integration test to verify CloudSync connect library page is operating correctly.
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */

@Test(groups = "Hybrid")
@Listeners(FailedTestListener.class)
public class CloudSyncPageTest extends AbstractTest
{
    /**
     * This test is to assert the navigation to the cloud sync page.
     *
     * @throws Exception
     */
    @Test
    public void navigateToCloudSync() throws Exception
    {
        DashBoardPage dashBoard = loginAs(username, password);
        MyProfilePage myProfilePage = dashBoard.getNav().selectMyProfile().render();
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        assertTrue(cloudSyncPage.getPageTitle().contains("User Cloud Auth"), "Title must  contain User Cloud Auth");
    }

    /**
     * This test is to assert the loading of cloud sync page.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "navigateToCloudSync")
    public void loadCloudSyncSignIn() throws Exception
    {
        CloudSyncPage cloudSyncPage = drone.getCurrentPage().render();
        if (cloudSyncPage.isDisconnectButtonDisplayed())
        {
            Assert.assertTrue(cloudSyncPage.isDisconnectButtonDisplayed());
            drone.find(CloudSyncPage.DISCONNECT_BUTTON).click();
            cloudSyncPage.confirmDelete();
        }
        signInToCloud(cloudUserName, cloudUserPassword).render();
    }


    /**
     * Method to sign in Cloud page and return Cloud Sync page
     *
     * @return boolean
     */
    public HtmlPage signInToCloud(final String username, final String password)
    {

        final By SIGN_IN_BUTTON = By.cssSelector("button#template_x002e_user-cloud-auth_x002e_user-cloud-auth_x0023_default-button-signIn-button");
        drone.findAndWait(SIGN_IN_BUTTON).click();
        CloudSignInPage cloudSignInPage = new CloudSignInPage(drone);
        cloudSignInPage.loginAs(username, password);
        return drone.getCurrentPage();
    }

}



