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

import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.cmm.admin.EditModelPopUp;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.ModelRow;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test Class holds all tests to test EditModelPopUp methods
 * 
 * @author Charu
 */
@Listeners(FailedTestListener.class)
public class EditModelPopUpTest extends AbstractTestCMM
{
    @Value("${cmm.dialogue.label.edit.model}") String createCMDialogueHeader;
    private String name = "model1" + System.currentTimeMillis();
    @Autowired CmmActions cmmActions;
    private EditModelPopUp editModelPopUp;

    @BeforeClass(groups = { "alfresco-one" }, alwaysRun = true)
    public void setup() throws Exception
    {
        loginAs(username, password);
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.createNewModel(driver, name, name, name);
    }

    @AfterClass
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    /**
     * Verify dialogue title is displayed correctly
     * 
     * @throws Exception if error
     */

    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testTitle() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();

        Assert.assertNotNull(editModelPopUp);
        Assert.assertNotNull(editModelPopUp.getDialogueTitle());
        Assert.assertTrue(createCMDialogueHeader.equals(editModelPopUp.getDialogueTitle()));
        
        editModelPopUp.selectCloseButton().render();
    }

    /**
     * Send name to name text field Verify name displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testNameDisabled() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        Assert.assertTrue(editModelPopUp.isNameDisabled(), "Name field text dispalyed correctly");
        editModelPopUp.selectCloseButton().render();
    }

    /**
     * Send name space to name space text field Verify name space displayed correctly
     * 
     * @throws Exception if error
     */

    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testSetNameSpace() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        Assert.assertEquals(editModelPopUp.getNameSpace(), name, "Namespace field text disabled correctly");
        editModelPopUp.setNameSpace(name + "1").render();
        Assert.assertEquals(editModelPopUp.getNameSpace(), name + "1", "Namespace field text disabled correctly");
        editModelPopUp.selectCloseButton().render();
    }

    /**
     * Send prefix to prefix text field Verify prefix displayed correctly
     * 
     * @throws Exception if error
     */

    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testSetPrefix() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        Assert.assertEquals(editModelPopUp.getPrefix(), name, "Prefix field text dispalyed correctly");
        editModelPopUp.setPrefix(name + "1").render();
        Assert.assertEquals(editModelPopUp.getPrefix(), name + "1", "Prefix field text dispalyed correctly");
        editModelPopUp.selectCloseButton().render();
    }

    /**
     * Send description to description text field Verify description displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testSetDescription() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        Assert.assertEquals(editModelPopUp.getDescription(), name, "Namespace field text dispalyed correctly");
        editModelPopUp.setDescription(name + "1").render();
        Assert.assertEquals(editModelPopUp.getDescription(), name + "1", "Namespace field text dispalyed correctly");
        editModelPopUp.selectCloseButton().render();
    }

    /**
     * Send author to author text field Verify author displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testSetAuthor() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        Assert.assertEquals(editModelPopUp.getAuthor(), name, "Author field set to user name correctly");
        editModelPopUp.setAuthor("").render();
        editModelPopUp.selectEditModelButton("Save").render();
        ModelManagerPage cmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow mrow = cmPage.getCustomModelRowByName(name);
        ActionsSet mactions = mrow.getCmActions();
        editModelPopUp = mactions.clickActionByName("Edit").render();
        Assert.assertEquals(editModelPopUp.getAuthor(), "Administrator", "Autor field text updated correctly");
        editModelPopUp.setAuthor("newAuthor").render();
        editModelPopUp.selectEditModelButton("Save").render();
        ModelManagerPage cmpage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow morow = cmpage.getCustomModelRowByName(name);
        ActionsSet mctions = morow.getCmActions();
        editModelPopUp = mctions.clickActionByName("Edit").render();
        Assert.assertEquals(editModelPopUp.getAuthor(), "newAuthor", "Namespace field text dispalyed correctly");
        editModelPopUp.selectCloseButton().render();
    }

    /**
     * select Cancel button in EditModelPopUp Verify list is not modified
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testSelectCancelModelButton() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        editModelPopUp.setNameSpace(name + "1").render();
        editModelPopUp.setPrefix(name + "1").render();
        editModelPopUp.setAuthor(name + "1").render();
        editModelPopUp.setDescription(name + "1").render();
        Assert.assertTrue(editModelPopUp.isCancelButtonEnabled("Cancel"), "Cancel button enabled");
        ModelManagerPage cmmpage = editModelPopUp.selectCancelModelButton("Cancel").render();
        Assert.assertTrue(cmmpage.isCustomModelRowDisplayed(name), "Custom Model Row is displayed");
        Assert.assertEquals(cmmpage.getCustomModelRowByName(name).getCmNamespace(), name, "Namespace not edited");

    }

    /**
     * select 'save changes' button in EditModelPopUp Verify list is modified correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 8)
    public void testSelectSaveChangesButton() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        editModelPopUp.setNameSpace(name + "1").render();
        editModelPopUp.setPrefix(name + "1").render();
        editModelPopUp.setAuthor(name + "1").render();
        editModelPopUp.setDescription(name + "1").render();
        Assert.assertTrue(editModelPopUp.isEditButtonEnabled("Save"), "Cancel button enabled");
        ModelManagerPage cmmpage = editModelPopUp.selectEditModelButton("Save").render();
        Assert.assertTrue(cmmpage.isCustomModelRowDisplayed(name), "Custom Model Row is displayed");
        Assert.assertEquals(cmmpage.getCustomModelRowByName(name).getCmNamespace(), name + "1", "Namespace edited correctly");

    }

    /**
     * Verify validation message for Mandatory fields are displayed
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 9)
    public void testFieldValidationMessages() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ModelRow row = cmmPage.getCustomModelRowByName(name);
        ActionsSet actions = row.getCmActions();
        editModelPopUp = actions.clickActionByName("Edit").render();
        Assert.assertFalse(editModelPopUp.isNamespaceValationMessageDisplayed(), "Message displayed correctly");
        Assert.assertFalse(editModelPopUp.isNameValidationMessageDisplayed(), "Message displayed correctly");
        Assert.assertFalse(editModelPopUp.isPrefixValidationMessageDisplayed(), "Message displayed correctly");
        Assert.assertFalse(editModelPopUp.isAuthorValidationMessageDisplayed(), "Message not displayed correctly");
        Assert.assertFalse(editModelPopUp.isDescValidationMessageDisplayed(), "Message not displayed correctly");
        editModelPopUp.selectCloseButton().render();
    }

}
