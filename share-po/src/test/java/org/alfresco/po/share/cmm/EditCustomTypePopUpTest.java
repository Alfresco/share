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

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.EditCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class EditCustomTypePopUpTest
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class EditCustomTypePopUpTest extends AbstractTestCMM
{

    /** The logger */
    // private static final Log logger = LogFactory.getLog(EditCustomTypePopUpTest.class);
    @SuppressWarnings("unused")
    private SharePage page;
    private String name = "model1" + System.currentTimeMillis();
    @Autowired CmmActions cmmActions;
    private String typeName = "Type" + System.currentTimeMillis();
    private String parenttype = "cm:thumbnail (Thumbnail)";
    private String compoundTypeName = name + ":" + typeName;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {

        page = loginAs(username, password);

        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
        createNewModelPopUpPage.setName(name);
        createNewModelPopUpPage.setNameSpace(name).render();
        createNewModelPopUpPage.setPrefix(name);
        createNewModelPopUpPage.setDescription(name).render();

        cmmPage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        cmmPage.selectCustomModelRowByName(name).render();
        ManageTypesAndAspectsPage typesListPage = cmmActions.createType(driver, typeName).render();
        Assert.assertTrue(typesListPage.isCustomTypeRowDisplayed(compoundTypeName));

        // Model is not Active yet
    }

    /**
     * Logout between tests.
     */
    @AfterClass
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    /**
     * Test the Edit Action works
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testClickEditCustomTypeAction() throws Exception
    {
        EditCustomTypePopUp editTypePopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);
        Assert.assertNotNull(editTypePopUp);
        Assert.assertNotNull(editTypePopUp.getDialogueTitle());
        ManageTypesAndAspectsPage manageAspectsPage = editTypePopUp.selectCloseButton().render();
        Assert.assertNotNull(manageAspectsPage);
    }

    /**
     * Test to select cancel button
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testCancelButtonTest() throws Exception
    {
        EditCustomTypePopUp editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);
        Assert.assertTrue(editCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = editCustomTypeTPopUp.selectCancelButton().render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compoundTypeName), "Custom Type Row disaplayed");
    }

    /**
     * Test setting the name on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testName() throws Exception
    {
        EditCustomTypePopUp editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);

        Assert.assertFalse(editCustomTypeTPopUp.isNameEnabled(), "Name should not be enabled");

        Assert.assertEquals(editCustomTypeTPopUp.getNameField(), typeName, "Name field text displayed correctly");

        editCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the title on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testSetTitle() throws Exception
    {
        EditCustomTypePopUp editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);

        editCustomTypeTPopUp.setTitleField("Title").render();

        Assert.assertEquals(editCustomTypeTPopUp.getTitleField(), "Title", "Title field text displayed correctly");

        editCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the description on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testSetDescription() throws Exception
    {
        EditCustomTypePopUp editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);

        editCustomTypeTPopUp.setDescriptionField("Desc").render();

        Assert.assertEquals(editCustomTypeTPopUp.getDescriptionField(), "Desc", "Description field text displayed correctly");

        editCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the parent property group on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testSetCustomType() throws Exception
    {
        EditCustomTypePopUp editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);

        editCustomTypeTPopUp.selectParentTypeField(parenttype).render();

        Assert.assertEquals(editCustomTypeTPopUp.getParentTypeField(), parenttype, "Parent Type field text displayed correctly");

        Assert.assertTrue(editCustomTypeTPopUp.isTypeDisplayedInParentList(parenttype), "Type displayed");

        editCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Edit Type Fields checks on Activating Model
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testEditTypeForActiveModel() throws Exception
    {
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, name, true).render();

        cmmActions.viewTypesAspectsForModel(driver, name);

        EditCustomTypePopUp editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);

        Assert.assertTrue(editCustomTypeTPopUp.isTitleEnabled(), "Title Field should be enabled");
        Assert.assertTrue(editCustomTypeTPopUp.isDescriptionEnabled(), "Description Field should be enabled");

        Assert.assertFalse(editCustomTypeTPopUp.isNameEnabled(), "Name Field should be disabled");
        Assert.assertFalse(editCustomTypeTPopUp.isParentTypeEnabled(), "Parent Type Field should be disabled");

        editCustomTypeTPopUp.setTitleField("New");
        editCustomTypeTPopUp.setDescriptionField("New");
        editCustomTypeTPopUp.selectSaveButton().render();

        editCustomTypeTPopUp = cmmActions.getEditTypePopUp(driver, compoundTypeName);
        Assert.assertEquals(editCustomTypeTPopUp.getTitleField(), "New", "Incorrect Title Set");
        Assert.assertEquals(editCustomTypeTPopUp.getDescriptionField(), "New", "Incorrect Description Set");
    }
}
