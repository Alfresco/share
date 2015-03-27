/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.user;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * LoginPage process integration test
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
public class CloudSignInPageTest extends AbstractTest
{
    private DashBoardPage dashBoardPage;
    private MyProfilePage myProfilePage;
    private CloudSyncPage cloudSyncPage;
    private CloudSignInPage cloudSignInPage;
    private String mainWindow;
    private static String siteName;
    private File loginTestFile;

    @BeforeClass(groups = { "Hybrid" })
    public void prepare() throws Exception
    {
        dashBoardPage = loginAs(username, password);

        siteName = "site" + System.currentTimeMillis();
        loginTestFile = SiteUtil.prepareFile("SyncFailFile");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibraryPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        UploadFilePage uploadForm = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = uploadForm.uploadFile(loginTestFile.getCanonicalPath()).render();
        dashBoardPage = documentLibraryPage.getNav().selectMyDashBoard().render();

        myProfilePage = dashBoardPage.getNav().selectMyProfile().render();
        cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        if (cloudSyncPage.isDisconnectButtonDisplayed())
        {
            cloudSyncPage = cloudSyncPage.disconnectCloudAccount().render();
        }
        cloudSignInPage = cloudSyncPage.selectCloudSign().render();
    }

    /**
     * Checks the forgot password functionality
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Hybrid" })
    public void testSelectFogotPassordLink() throws Throwable
    {
        cloudSignInPage.selectFogotPassordLink();
        switchWindow();
        assertTrue(drone.getTitle().contains("Forgot Password"), "Title not matched. Was found to be - " + drone.getTitle());
        closeWindowAndSwitchBack();
    }

    /**
     * Checks the Register functionality
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Hybrid" }, dependsOnMethods = "testSelectFogotPassordLink")
    public void testSelectSignUpLink() throws Exception
    {
        cloudSignInPage.selectSignUpLink();
        switchWindow();
        assertTrue(drone.getTitle().contains("Cloud Document Management"), "Title not matched. Was found to be - " + drone.getTitle());
        closeWindowAndSwitchBack();
    }

    /**
     * Log a user into Alfresco with valid credentials and then logout
     * 
     * @throws Exception
     *             if error
     */
    @Test(groups = { "Hybrid" }, dependsOnMethods = "testSelectSignUpLink")
    public void loginAndLogout() throws Exception
    {
        Assert.assertEquals(cloudSignInPage.getPageTitle(), "Sign in to Alfresco in the cloud");
        Assert.assertTrue(cloudSignInPage.isForgotPasswordLinkDisplayed());
        Assert.assertEquals(cloudSignInPage.getForgotPasswordURL(), "https://my.alfresco.com/share/page/forgot-password");
    }

    /**
     * Closes the newly created win and swithes back to main
     */
    private void closeWindowAndSwitchBack()
    {
        drone.closeWindow();
        drone.switchToWindow(mainWindow);
    }

    /**
     * Switches to the newly created window.
     */
    private void switchWindow()
    {
        mainWindow = drone.getWindowHandle();
        Set<String> windows = drone.getWindowHandles();
        windows.remove(mainWindow);
        drone.switchToWindow(windows.iterator().next());
    }

    /**
     * Log a user into Alfresco with valid credentials and verify
     * the return page.
     * 
     * @throws Exception
     *             if error
     */
    @Test(groups = { "Hybrid" }, dependsOnMethods = "loginAndLogout")
    public void loginAs() throws Exception
    {
        cloudSignInPage.loginAs(cloudUserName, cloudUserPassword).render();
        Assert.assertTrue(cloudSyncPage.isDisconnectButtonDisplayed());
        cloudSyncPage.disconnectCloudAccount().render();

        DocumentLibraryPage documentLibraryPage = openSiteDocumentLibraryFromSearch(drone, siteName);
        cloudSignInPage = documentLibraryPage.getFileDirectoryInfo(loginTestFile.getName()).selectSyncToCloud().render();
        DestinationAndAssigneePage destinationAndAssigneePage = cloudSignInPage.loginAs(cloudUserName, cloudUserPassword).render();
        Assert.assertTrue(destinationAndAssigneePage.isNetworkDisplayed(cloudUserName.split("@")[1]));
        documentLibraryPage = (DocumentLibraryPage) destinationAndAssigneePage.selectCancelButton();
        documentLibraryPage.render();
    }

    /**
     * Log a user into Alfresco with invalid credentials and verify
     * the message displayed
     * 
     * @throws Exception
     *             if error
     */
    @Test(groups = { "Hybrid" }, dependsOnMethods = "loginAs")
    public void loginWithInvalidCredentials() throws Exception
    {
        String fakePassword = "fakePass";

        myProfilePage = dashBoardPage.getNav().selectMyProfile().render();
        cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        if (cloudSyncPage.isDisconnectButtonDisplayed())
        {
            cloudSyncPage = cloudSyncPage.disconnectCloudAccount().render();
        }
        cloudSignInPage = cloudSyncPage.selectCloudSign().render();

        cloudSignInPage.loginToCloud(cloudUserName, fakePassword);
        Assert.assertEquals(cloudSignInPage.getAccountNotRecognisedError(), "Email or password not recognised");
        Assert.assertTrue(cloudSignInPage.isAccountNotRecognised());

    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
        disconnectCloudSync(drone);
    }
}
