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
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.LoginPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify Share Link page is operating correctly.
 * 
 * @author Chiran
 * @author adinap
 */
@Test(groups={"alfresco-one"})
@Listeners(FailedTestListener.class)
public class ShareLinkTest extends AbstractDocumentTest
{
    private static String siteName;
    private static String sitePrivateName;
    private String userName = "user" + System.currentTimeMillis() + "@test.com";
    private String userName2 = "user2" + System.currentTimeMillis() + "@test.com";
    private static DocumentLibraryPage documentLibPage;
    private File file;
    private static String folderName1;
    private static String folderDescription;
    ViewPublicLinkPage viewPage;
    private static String shareLink;
    private String loginTitle;
    private String docDetails;

    @BeforeClass
    public void prepare() throws Exception
    {
    	loginTitle = factoryPage.getValue("login.title");
    	docDetails = factoryPage.getValue("docDetails.title");
    	
    	siteName = "site" + System.currentTimeMillis();
        sitePrivateName = "site-private" + System.currentTimeMillis();

        createEnterpriseUser(userName);
        createEnterpriseUser(userName2);

        loginAs(userName, UNAME_PASSWORD);

        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "description", "Public");
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, sitePrivateName, "private site", "Private");

        file = siteUtil.prepareFile("alfresco123");

        folderName1 = "The first folder";
        folderDescription = String.format("Description of %s", folderName1);

        createData(siteName);
        createData(sitePrivateName);

    }

    @AfterClass
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
        siteUtil.deleteSite(username, password, sitePrivateName);

    }

    /**
     * create document and folder in site
     *
     * @param site String
     * @throws Exception
     */
    private void createData(String site) throws Exception
    {
        siteActions.navigateToDocumentLibrary(driver, site);
        siteActions.uploadFile(driver, file);
        siteActions.createFolder(driver, folderName1, "folder title", folderDescription);
    }

    @Test(priority = 1)
    public void testViewLink()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName);
        FileDirectoryInfo thisRow = siteActions.getFileDirectoryInfo(driver, file.getName());

        Assert.assertTrue(thisRow.isShareLinkVisible());
        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        
        Assert.assertNotNull(shareLinkPage);
        Assert.assertTrue(shareLinkPage.isViewLinkPresent());
        String shareLink = shareLinkPage.getShareURL();

        viewPage = shareLinkPage.clickViewButton().render();
        Assert.assertEquals(driver.getCurrentUrl(), shareLink);
        Assert.assertTrue(viewPage.isDocumentViewDisplayed());
        Assert.assertTrue(viewPage.getButtonName().equalsIgnoreCase(docDetails));
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());
        viewPage.clickOnDocumentDetailsButton();
    }
    
    @Test(priority = 2)
    public void testVerifyUnShareLink()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName);
    	FileDirectoryInfo thisRow = siteActions.getFileDirectoryInfo(driver, file.getName());

        ShareLinkPage shareLinkPage = thisRow.clickShareLink().render();
        Assert.assertTrue(shareLinkPage.isUnShareLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
    }

    @Test(priority = 3)
    public void testVerifyEmailLink()
    {
    	siteActions.navigateToDocumentLibrary(driver, siteName);

        ShareLinkPage shareLinkPage = siteActions.shareFile(driver, file.getName()).render();
        
        Assert.assertTrue(shareLinkPage.isEmailLinkPresent());
        documentLibPage = shareLinkPage.clickOnUnShareButton().render();
        documentLibPage = documentLibPage.getSiteNav().selectDocumentLibrary().render();
    }

    @Test(priority = 4, expectedExceptions = UnsupportedOperationException.class)
    public void clickShareLinkFolder()
    {
        siteActions.navigateToDocumentLibrary(driver, siteName);
        
        siteActions.shareFile(driver, folderName1);
    }

    /**
     * Checks that after logging in from the Public share page with user with permissions to Document
     * user is redirected to the same public share page and Document Details button is displayed
     */
    @Test(priority = 5)
    public void testRedirectLogin() throws Exception
    {
        siteActions.navigateToDocumentLibrary(driver, siteName);
        
        // Share a doc
        ShareLinkPage shareLinkPage = siteActions.shareFile(driver, file.getName()).render();
        
        shareLink = shareLinkPage.getShareURL();

        logout(driver);
        
        // View shared link
        viewPage = siteActions.viewSharedLink(driver, shareLink).render();

        Assert.assertTrue(viewPage.getButtonName().equalsIgnoreCase(loginTitle));
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());

        LoginPage loginPage = viewPage.clickOnDocumentDetailsButton().render();

        Assert.assertTrue(loginPage.isBrowserTitle(loginTitle));
        Assert.assertFalse(loginPage.hasErrorMessage());

        viewPage = loginPage.loginAs(userName, UNAME_PASSWORD).render();

        Assert.assertTrue(viewPage.getButtonName().equalsIgnoreCase(docDetails));
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());

    }
    
    /**
     * Checks that when trying to login from the public share page with invalid username, login error appears and
     * user remains on the Login page
     *
     */
    @Test(priority = 6)
    public void testRedirectInvalidLogin() throws Exception
    {
        driver.navigate().to(shareUrl);

        siteActions.navigateToDocumentLibrary(driver, siteName);

        ShareLinkPage shareLinkPage = siteActions.shareFile(driver, file.getName()).render();
        
        shareLink = shareLinkPage.getShareURL();

        logout(driver);

        viewPage = siteActions.viewSharedLink(driver, shareLink).render();
        
        Assert.assertTrue(viewPage.getButtonName().equalsIgnoreCase(loginTitle));
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());

        LoginPage loginPage = viewPage.clickOnDocumentDetailsButton().render();

        Assert.assertTrue(loginPage.isBrowserTitle(loginTitle));
        Assert.assertFalse(loginPage.hasErrorMessage());

        viewPage = loginPage.loginAs("invalid-user", "invalid-pass").render();

        Assert.assertNotNull(viewPage, "Expected ViewPage with Error");
    }

    /**
     * Checks that after logging in from the Public share page with user without permissions to Document
     * user is redirected to the same public share page and Document Details button is not displayed
     */
    @Test(priority = 7)
    public void testRedirectLoginUserWithoutPermissions() throws Exception
    {
    	loginAs(userName, UNAME_PASSWORD);

        siteActions.navigateToDocumentLibrary(driver, sitePrivateName);
        
        ShareLinkPage shareLinkPage = siteActions.shareFile(driver, file.getName()).render();
        
        shareLink = shareLinkPage.getShareURL();

        logout(driver);

        viewPage = siteActions.viewSharedLink(driver, shareLink).render();

        Assert.assertTrue(viewPage.getButtonName().equalsIgnoreCase(loginTitle));
        Assert.assertEquals(viewPage.getContentTitle(), file.getName());

        LoginPage loginPage = viewPage.clickOnDocumentDetailsButton().render();

        Assert.assertTrue(loginPage.isBrowserTitle(loginTitle));
        Assert.assertFalse(loginPage.hasErrorMessage());

        viewPage = loginPage.loginAs(userName2, UNAME_PASSWORD).render();

        Assert.assertEquals(viewPage.getContentTitle(), file.getName());
        Assert.assertFalse(viewPage.isButtonVisible(), "Document Details button visible");
    }

}
