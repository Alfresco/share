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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Content I'm Editing dashlet web elements
 * Created by olga Lokhach
 */
@Listeners(FailedTestListener.class)
@Test(groups={"alfresco-one", "bug"})
public class EditingContentDashletTest extends AbstractSiteDashletTest
{
    private DashBoardPage dashBoardPage;
    private CustomiseUserDashboardPage customiseUserDashboardPage;
    private String userName;
    private DocumentLibraryPage documentLibPage;
    private EditingContentDashlet editingContentDashlet;
    private static final String EXP_HELP_BALLOON_MSG = "Check this dashlet to quickly see the items you are working on.";

    @BeforeClass
    public void setup()throws Exception
    {
        userName = "User_" + System.currentTimeMillis();
        siteName = "MySiteTests" + System.currentTimeMillis();
        createEnterpriseUser(userName);
        loginAs(driver, shareUrl, userName, UNAME_PASSWORD).render();
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "description", "Public");
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        File file = siteUtil.prepareFile(fileName);
        UploadFilePage uploadForm = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = uploadForm.uploadFile(file.getCanonicalPath()).render();
        fileName = file.getName();
        FileDirectoryInfo fileDirectoryInfo = documentLibPage.getFileDirectoryInfo(fileName);
        fileDirectoryInfo.selectEditOfflineAndCloseFileWindow();
    }

    @Test
    public void instantiateDashlet()
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();

        customiseUserDashboardPage = dashBoardPage.getNav().selectCustomizeUserDashboard();
        customiseUserDashboardPage.render();
        dashBoardPage = customiseUserDashboardPage.addDashlet(Dashlets.CONTENT_I_AM_EDITING, 1).render();
        editingContentDashlet = dashletFactory.getDashlet(driver, EditingContentDashlet.class).render();
        assertNotNull(editingContentDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        editingContentDashlet.clickOnHelpIcon();
        assertTrue(editingContentDashlet.isBalloonDisplayed(), "Baloon popup isn't displayed");
        String actualHelpBalloonMsg = editingContentDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        editingContentDashlet.closeHelpBallon();
        assertFalse(editingContentDashlet.isBalloonDisplayed(), "Baloon popup is displayed");
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void isItemDisplayed() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        assertTrue(editingContentDashlet.isItemWithDetailDisplayed(fileName, siteName), "Item is not found");
    }

    @Test(dependsOnMethods = "isItemDisplayed")
    public void clickItem() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        documentLibPage = editingContentDashlet.clickItem(fileName).render();
        assertNotNull(documentLibPage);
    }

    @Test(dependsOnMethods = "clickItem")
    public void clickSite() throws Exception
    {
        SharePage page = resolvePage(driver).render();
        dashBoardPage = page.getNav().selectMyDashBoard().render();
        editingContentDashlet = dashBoardPage.getDashlet("editing-content").render();
        siteDashBoard = editingContentDashlet.clickSite(siteName).render();
        assertNotNull(siteDashBoard);
    }
}
