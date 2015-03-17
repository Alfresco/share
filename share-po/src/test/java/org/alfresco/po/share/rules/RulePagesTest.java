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
package org.alfresco.po.share.rules;

import static org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector.StringCompareOption.CONTAINS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.NewUserPage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.UserSearchPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.EmailMessageForm;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.IfSelectorCloudImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.IfSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class RulePagesTest extends AbstractTest
{

    protected String siteName;
    protected String folderName;
    protected String folderDescription;
    protected String userName = "user" + System.currentTimeMillis() + "@test.com";
    protected String firstName = userName;
    protected String lastName = userName;

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "alfresco-one")
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        folderName = "The first folder";
        folderDescription = String.format("Description of %s", folderName);
        createUserAndLogin();
        SiteUtil.createSite(drone, siteName, "description", "Public");
    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUserAndLogin() throws Exception
    {
        if (!alfrescoVersion.isCloud())
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            newPage.inputFirstName(firstName);
            newPage.inputLastName(lastName);
            newPage.inputEmail(userName);
            newPage.inputUsername(userName);
            newPage.inputPassword(userName);
            newPage.inputVerifyPassword(userName);
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userName).render();
            assertTrue(userCreated.hasResults());
            logout(drone);
            loginAs(userName, userName);
        }
        else
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
    }

    @AfterClass(groups = "alfresco-one")
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test(groups = "alfresco-one")
    public void getPageFromFactoryTest()
    {
        SiteDashboardPage siteDashboardPage = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibraryPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
        documentLibraryPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        documentLibraryPage = (documentLibraryPage.getNavigation().selectDetailedView()).render();
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName);
        fileDirectoryInfo.selectManageRules();
        FolderRulesPage folderRulesPage = drone.getCurrentPage().render();
        assertNotNull(folderRulesPage);
    }

    @Test(dependsOnMethods = "getPageFromFactoryTest", groups = "alfresco-one")
    public void folderRulePageTest()
    {
        FolderRulesPage folderRulesPage = drone.getCurrentPage().render();
        assertTrue(folderRulesPage.isPageCorrect(folderName));
        assertTrue(folderRulesPage.isInheritRuleToggleAvailable());
        assertFalse(folderRulesPage.isInheritRulesMessageDisplayed());
        assertEquals(folderRulesPage.getInheritRulesText(), "Inherit Rules");
    }

    @Test(dependsOnMethods = "folderRulePageTest", groups = {"alfresco-one", "TestBug"})
    public void createRulePageTest()
    {
        FolderRulesPage folderRulesPage = drone.getCurrentPage().render();
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage();
        assertTrue(createRulePage.isAllButtonEnableAndDisplay());
        assertTrue(createRulePage.isButtonsCorrectByDefault());
        assertTrue(createRulePage.isCheckBoxesCorrectByDefault());
        assertTrue(createRulePage.isDefaultSelectsChoiceCorrect());
        assertTrue(createRulePage.isNameFieldAndDescriptionEmpty());
        assertTrue(createRulePage.isPageCorrect());
    }

    @Test(dependsOnMethods = "createRulePageTest", groups = {"alfresco-one", "TestBug"})
    public void createRuleAndThenAnotherTest()
    {
        CreateRulePage createRulePage = drone.getCurrentPage().render();
        createRulePage.fillNameField("testRuleName1");
        createRulePage.fillDescriptionField("testRuleDescription1");

        WhenSelectorImpl whenSelector = createRulePage.getWhenOptionObj();
        whenSelector.selectInbound();

        AlfrescoVersion version = drone.getProperties().getVersion();

        if (!version.isCloud())
        {
            ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
            actionSelectorEnterp.selectIncrementCounter();

            IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
            ifSelectorEnterp.selectAllItems();
        }
        else
        {
            IfSelectorCloudImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
            ifSelectorEnterp.selectAllItems();
        }

        createRulePage.selectApplyToSubfolderCheckbox();

        createRulePage = createRulePage.clickAnotherCreate();
        assertTrue(createRulePage.isPageCorrect());
    }

    @Test(dependsOnMethods = "createRuleAndThenAnotherTest", groups = {"alfresco-one", "TestBug"})
    public void createNextRuleTest()
    {
        CreateRulePage createRulePage = drone.getCurrentPage().render();
        createRulePage.fillNameField("testRuleName2");
        createRulePage.fillDescriptionField("testRuleDescription2");

        WhenSelectorImpl whenSelector = createRulePage.getWhenOptionObj();
        whenSelector.selectUpdate();

        AlfrescoVersion version = drone.getProperties().getVersion();

        if (!version.isCloud())
        {
            ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
            actionSelectorEnterp.selectExtractMetadata();

            IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
            ifSelectorEnterp.selectAuthor(CONTAINS, "a");
        }
        else
        {
            IfSelectorCloudImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
            ifSelectorEnterp.selectAllItems();
        }

        createRulePage.selectRunRuleInBackgroundCheckbox();

        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate();
        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
    }

    @Test(dependsOnMethods = "createNextRuleTest", groups = {"alfresco-one", "TestBug"})
    public void foldersRuleWithRulesPageTest()
    {
        FolderRulesPageWithRules folderRulesPageWithRules = drone.getCurrentPage().render();
        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
        assertTrue(folderRulesPageWithRules.isInheritRuleToggleAvailable());
    }

    @Test(dependsOnMethods = "foldersRuleWithRulesPageTest", groups = {"alfresco-one", "TestBug"})
    public void deleteRuleTest()
    {
        FolderRulesPageWithRules folderRulesPageWithRules = drone.getCurrentPage().render();
        folderRulesPageWithRules.deleteRule("testRuleName2");
    }

    @Test(dependsOnMethods = "deleteRuleTest", groups = {"alfresco-one", "TestBug"})
    public void clickNewRuleThenCancelTest()
    {
        FolderRulesPageWithRules folderRulesPageWithRules = drone.getCurrentPage().render();
        CreateRulePage createRulePage = folderRulesPageWithRules.clickNewRuleButton();
        folderRulesPageWithRules = (FolderRulesPageWithRules) createRulePage.clickCancelButton().render();
        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
    }

    @Test(dependsOnMethods = "clickNewRuleThenCancelTest", groups = {"alfresco-one", "TestBug"})
    public void getInheritedRulesFolderNameTest()
    {
        FolderRulesPageWithRules folderRulesPageWithRules = drone.getCurrentPage().render();
        DocumentLibraryPage documentLibraryPage = folderRulesPageWithRules.getSiteNav().selectSiteDocumentLibrary().render();
        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
        documentLibraryPage = newFolderPage.createNewFolder(folderName + 1, folderDescription + 1).render();
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName + 1);
        FolderRulesPage folderRulesPage = fileDirectoryInfo.selectManageRules().render();
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage();

        createRulePage.fillNameField("testRuleName3");
        createRulePage.fillDescriptionField("testRuleDescription3");

        WhenSelectorImpl whenSelector = createRulePage.getWhenOptionObj();
        whenSelector.selectUpdate();

        AlfrescoVersion version = drone.getProperties().getVersion();

        if (!version.isCloud())
        {
            ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
            actionSelectorEnterp.selectExtractMetadata();

            IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
            ifSelectorEnterp.selectAuthor(CONTAINS, "a");
        }
        else
        {
            IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
            ifSelectorEnterp.selectAuthor(CONTAINS, "a");
        }

        createRulePage.selectRunRuleInBackgroundCheckbox();

        folderRulesPageWithRules = createRulePage.clickCreate();
        assertEquals(folderRulesPageWithRules.getInheritedRulesFolderName("testRuleName1"), folderName);
    }

    @Test(dependsOnMethods = "getInheritedRulesFolderNameTest", groups = {"alfresco-one", "TestBug"})
    public void isRuleNameDisplayedTest()
    {
        FolderRulesPageWithRules folderRulesPageWithRules = drone.getCurrentPage().render();
        assertTrue(folderRulesPageWithRules.isRuleNameDisplayed("testRuleName3"));
    }

    @Test(dependsOnMethods = "isRuleNameDisplayedTest", groups = {"EnterpriseOnly", "TestBug"})
    public void checkEmailForm()
    {
        FolderRulesPageWithRules folderRulesPageWithRules = drone.getCurrentPage().render();
        CreateRulePage createRulePage = folderRulesPageWithRules.clickNewRuleButton();
        ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
        EmailMessageForm emailMessageForm = actionSelectorEnterp.selectSendEmail();
        assertTrue(emailMessageForm.isDisplay());
        emailMessageForm.addUserToRecipients(userName);
        emailMessageForm.removeUserFromRecipients(userName);
        emailMessageForm.fillSubjectField("test subj");
        emailMessageForm.fillMessageArea("test body. Arrrrrrrrr!!!!");
        emailMessageForm.clickClose();
        assertFalse(emailMessageForm.isDisplay());
    }

}
