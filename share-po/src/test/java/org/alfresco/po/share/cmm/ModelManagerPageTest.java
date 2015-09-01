/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.po.share.cmm;

/**
 * Test class to test ModelManager Page
 * 
 * @author mbhave
 */

import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.cmm.admin.ConfirmDeletePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.ModelRow;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ModelManagerPageTest extends AbstractTestCMM
{
    private String name = "model1" + System.currentTimeMillis();
    @Value("cmm.model.status.draft") String modelStatusDraft;
    @Value("cmm.model.action.edit") String modelStatusActive;
    @Value("cmm.model.action.edit") String editAction;
    @Value("cmm.model.action.activate") String activateAction;
    @Value("cmm.model.action.delete") String deleteAction;
    @Value("cmm.model.action.deactivate") String deactivateAction;
    @Value("cmm.model.action.export") String exportAction;

    @BeforeClass(groups = { "alfresco-one" }, alwaysRun = true)
    public void setup() throws Exception
    {

        modelStatusDraft = factoryPage.getValue("cmm.model.status.draft");

        modelStatusActive = factoryPage.getValue("cmm.model.status.active");

        editAction = factoryPage.getValue("cmm.model.action.edit");

        deleteAction = factoryPage.getValue("cmm.model.action.delete");

        activateAction = factoryPage.getValue("cmm.model.action.activate");

        deactivateAction = factoryPage.getValue("cmm.model.action.deactivate");

        exportAction = factoryPage.getValue("cmm.model.action.export");

        loginAs(username, password);
    }

    @AfterClass
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void selectCreateModelButtonTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
        createNewModelPopUpPage.setName(name);
        createNewModelPopUpPage.setNameSpace(name).render();
        createNewModelPopUpPage.setPrefix(name);
        createNewModelPopUpPage.setDescription(name).render();

        ModelManagerPage cmmpage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        Assert.assertTrue(cmmpage.isCustomModelRowDisplayed(name), "Custom Model Row is not displayed");

    }

    /**
     * Click on CreateModelModel button Verify CreateNewModelPopUp page rendered correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void clickCreateModelCloseTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        int modelsCount = cmmPage.getCMRows().size();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        Assert.assertFalse(createNewModelPopUpPage.getDialogueTitle().isEmpty(), "Tiltle displayed");

        cmmPage = createNewModelPopUpPage.selectCloseButton().render();

        Assert.assertEquals(modelsCount, cmmPage.getCMRows().size(), "Expecting size: " + modelsCount);
    }

    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void getNameInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        Assert.assertTrue(cmmPage.getCustomModelRowByName(name).getCMName().contains(name));
    }

    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void getNameSpaceInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        Assert.assertTrue(cmmPage.getCustomModelRowByName(name).getCmNamespace().contains(name));
    }

    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void getStatusInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);

        Assert.assertTrue(row.getCmStatus().contains(modelStatusDraft));
        Assert.assertTrue(row.getCmActions().hasActionByName(editAction), "Action edit displayed");
    }

    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void clickActivateInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();

        Assert.assertTrue(row.getCmStatus().contains(modelStatusDraft));
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));
        Assert.assertTrue(actions.hasActionByName(activateAction));
        Assert.assertFalse(actions.hasActionByName(deactivateAction));
        Assert.assertTrue(actions.hasActionByName(exportAction));

        cmmPage = actions.clickActionByName(activateAction).render();
        row = cmmPage.getCustomModelRowByName(name);
        actions = row.getCmActions();

        Assert.assertTrue(row.getCmStatus().contains(modelStatusActive));
        Assert.assertFalse(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deactivateAction));
        Assert.assertFalse(actions.hasActionByName(deleteAction));
        Assert.assertFalse(actions.hasActionByName(activateAction));
        Assert.assertTrue(actions.hasActionByName(exportAction));
    }

    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void clickDeactivateInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();

        Assert.assertTrue(row.getCmStatus().contains(modelStatusActive));
        Assert.assertFalse(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deactivateAction));
        Assert.assertFalse(actions.hasActionByName(deleteAction));
        Assert.assertFalse(actions.hasActionByName(activateAction));
        Assert.assertTrue(actions.hasActionByName(exportAction));

        cmmPage = actions.clickActionByName(deactivateAction).render();
        row = cmmPage.getCustomModelRowByName(name);
        actions = row.getCmActions();

        Assert.assertTrue(row.getCmStatus().contains(modelStatusDraft));
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));
        Assert.assertTrue(actions.hasActionByName(activateAction));
        Assert.assertFalse(actions.hasActionByName(deactivateAction));
        Assert.assertTrue(actions.hasActionByName(exportAction));
    }

    @Test(groups = { "Enterprise-only" }, priority = 8)
    public void clickModelNameInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ManageTypesAndAspectsPage typesAndAspectsPage = cmmPage.selectCustomModelRowByName(name).render();

        Assert.assertNotNull(typesAndAspectsPage);
        Assert.assertEquals(name, typesAndAspectsPage.getModelName());

        Assert.assertEquals(0, typesAndAspectsPage.getCustomModelPropertyGroupRows().size());
        Assert.assertEquals(0, typesAndAspectsPage.getCustomModelTypeRows().size());
    }

    @Test(groups = { "Enterprise-only" }, priority = 9)
    public void clickDeleteInModelListTest() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();

        Assert.assertTrue(row.getCmStatus().contains(modelStatusDraft));
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));
        Assert.assertTrue(actions.hasActionByName(activateAction));
        Assert.assertFalse(actions.hasActionByName(deactivateAction));
        Assert.assertTrue(actions.hasActionByName(exportAction));

        ConfirmDeletePopUp cdPop = actions.clickActionByNameAndDialogByButtonName(deleteAction, deleteAction).render();
        cmmPage = cdPop.clickActionByName(deleteAction).render();
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(name));
    }
}
