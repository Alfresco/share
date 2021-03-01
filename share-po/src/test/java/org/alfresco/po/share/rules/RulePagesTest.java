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
package org.alfresco.po.share.rules;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.IfSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
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
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName, "description", "Public");
    }

    /**
     * Create User
     *
     * @throws Exception
     */
    private void createUserAndLogin() throws Exception
    {
    	createEnterpriseUser(userName);
        loginAs(userName, UNAME_PASSWORD);
    }

    @AfterClass(groups = "alfresco-one")
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(groups = "alfresco-one")
    public void getPageFromFactoryTest()
    {
        SiteDashboardPage siteDashboardPage = resolvePage(driver).render();
        DocumentLibraryPage documentLibraryPage = siteDashboardPage.getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
        documentLibraryPage = newFolderPage.createNewFolder(folderName, folderDescription).render();
        documentLibraryPage = (documentLibraryPage.getNavigation().selectDetailedView()).render();
        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName);
        fileDirectoryInfo.selectManageRules();
        FolderRulesPage folderRulesPage = resolvePage(driver).render();
        assertNotNull(folderRulesPage);
    }

    @Test(dependsOnMethods = "getPageFromFactoryTest", groups = "alfresco-one")
    public void folderRulePageTest()
    {
        FolderRulesPage folderRulesPage = resolvePage(driver).render();
        assertTrue(folderRulesPage.isPageCorrect(folderName));
        assertTrue(folderRulesPage.isInheritRuleToggleAvailable());
        assertFalse(folderRulesPage.isInheritRulesMessageDisplayed());
        assertEquals(folderRulesPage.getInheritRulesText(), "Inherit Rules");
    }

    @Test(dependsOnMethods = "folderRulePageTest", groups = {"alfresco-one", "bug"})
    public void createRuleAndThenAnotherTest()
    {
        FolderRulesPage folderRulesPage = resolvePage(driver).render();
        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
        createRulePage.fillNameField("testRuleName1");
        createRulePage.fillDescriptionField("testRuleDescription1");

        WhenSelectorImpl whenSelector = createRulePage.getWhenOptionObj();
        whenSelector.selectInbound();
        ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
        actionSelectorEnterp.selectIncrementCounter();

        IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
        ifSelectorEnterp.selectAllItems();

        createRulePage.selectApplyToSubfolderCheckbox();
        createRulePage = createRulePage.clickAnotherCreate();
    }

//    @Test(dependsOnMethods = "createRuleAndThenAnotherTest", groups = {"alfresco-one", "bug"})
//    public void createNextRuleTest()
//    {
//        CreateRulePage createRulePage = resolvePage(driver).render();
//        createRulePage.fillNameField("testRuleName2");
//        createRulePage.fillDescriptionField("testRuleDescription2");
//
//        WhenSelectorImpl whenSelector = createRulePage.getWhenOptionObj();
//        whenSelector.selectUpdate();
//
//        
//        ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
//        actionSelectorEnterp.selectExtractMetadata();
//
//        IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
//        ifSelectorEnterp.selectAuthor(CONTAINS, "a");
//
//        createRulePage.selectRunRuleInBackgroundCheckbox();
//
//        FolderRulesPageWithRules folderRulesPageWithRules = createRulePage.clickCreate().render();
//        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
//    }
//
//    @Test(dependsOnMethods = "createNextRuleTest", groups = {"alfresco-one", "bug"})
//    public void foldersRuleWithRulesPageTest()
//    {
//        FolderRulesPageWithRules folderRulesPageWithRules = resolvePage(driver).render();
//        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
//        assertTrue(folderRulesPageWithRules.isInheritRuleToggleAvailable());
//    }
//
//    @Test(dependsOnMethods = "foldersRuleWithRulesPageTest", groups = {"alfresco-one", "bug"})
//    public void deleteRuleTest()
//    {
//        FolderRulesPageWithRules folderRulesPageWithRules = resolvePage(driver).render();
//        folderRulesPageWithRules.deleteRule("testRuleName2");
//    }
//
//    @Test(dependsOnMethods = "deleteRuleTest", groups = {"alfresco-one", "bug"})
//    public void clickNewRuleThenCancelTest()
//    {
//        FolderRulesPageWithRules folderRulesPageWithRules = resolvePage(driver).render();
//        CreateRulePage createRulePage = folderRulesPageWithRules.clickNewRuleButton().render();
//        folderRulesPageWithRules = (FolderRulesPageWithRules) createRulePage.clickCancelButton().render();
//        assertTrue(folderRulesPageWithRules.isPageCorrect(folderName));
//    }
//
//    @Test(dependsOnMethods = "clickNewRuleThenCancelTest", groups = {"alfresco-one", "bug"})
//    public void getInheritedRulesFolderNameTest()
//    {
//        FolderRulesPageWithRules folderRulesPageWithRules = resolvePage(driver).render();
//        DocumentLibraryPage documentLibraryPage = folderRulesPageWithRules.getSiteNav().selectDocumentLibrary().render();
//        documentLibraryPage = documentLibraryPage.selectFolder(folderName).render();
//        NewFolderPage newFolderPage = documentLibraryPage.getNavigation().selectCreateNewFolder();
//        documentLibraryPage = newFolderPage.createNewFolder(folderName + 1, folderDescription + 1).render();
//        FileDirectoryInfo fileDirectoryInfo = documentLibraryPage.getFileDirectoryInfo(folderName + 1);
//        FolderRulesPage folderRulesPage = fileDirectoryInfo.selectManageRules().render();
//        CreateRulePage createRulePage = folderRulesPage.openCreateRulePage().render();
//
//        createRulePage.fillNameField("testRuleName3");
//        createRulePage.fillDescriptionField("testRuleDescription3");
//
//        WhenSelectorImpl whenSelector = createRulePage.getWhenOptionObj();
//        whenSelector.selectUpdate();
//        ActionSelectorEnterpImpl actionSelectorEnterp = createRulePage.getActionOptionsObj();
//        actionSelectorEnterp.selectExtractMetadata();
//
//        IfSelectorEnterpImpl ifSelectorEnterp = createRulePage.getIfOptionObj();
//        ifSelectorEnterp.selectAuthor(CONTAINS, "a");
//
//        createRulePage.selectRunRuleInBackgroundCheckbox();
//
//        folderRulesPageWithRules = createRulePage.clickCreate().render();
//        assertEquals(folderRulesPageWithRules.getInheritedRulesFolderName("testRuleName1"), folderName);
//    }
//
//    @Test(dependsOnMethods = "getInheritedRulesFolderNameTest", groups = {"alfresco-one", "bug"})
//    public void isRuleNameDisplayedTest()
//    {
//        FolderRulesPageWithRules folderRulesPageWithRules = resolvePage(driver).render();
//        assertTrue(folderRulesPageWithRules.isRuleNameDisplayed("testRuleName3"));
//    }
}
