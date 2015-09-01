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
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.ModelPropertyGroupRow;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class CreateNewPropertyGroupPopUpTest
 * 
 * @author Richard Smith
 * @author mbhave
 */
@SuppressWarnings("unused")
@Listeners(FailedTestListener.class)
public class CreateNewPropertyGroupPopUpTest extends AbstractTestCMM
{

    /** The logger */
    private static Log logger = LogFactory.getLog(CreateNewPropertyGroupPopUpTest.class);


    private SharePage page;

    private String name = "model1" + System.currentTimeMillis();

    // SHA-1103: Amended Parent Aspect Name to
    private String parentModelName = "parent" + name;

    private String aspectName = "Aspect" + System.currentTimeMillis();

    // private String parentPropertyGroup = "cm:author (Author)";
    private String parentPropertyGroup = getParentTypeAspectName(parentModelName, aspectName);

    @Autowired CmmActions cmmActions;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {

        page = loginAs(username, password);

        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        // SHA-1103: Create another Model, Aspect to be used as Parent Type Group later
        cmmActions.createNewModel(driver, parentModelName);
        cmmActions.setModelActive(driver, parentModelName, true);

        // Create another model
        cmmPage = cmmActions.createNewModel(driver, name).render();
        cmmPage.selectCustomModelRowByName(parentModelName).render();

        ManageTypesAndAspectsPage aspectsListPage = cmmActions.createAspect(driver, aspectName).render();
    }

    /**
     * Logout at the end
     */
    @AfterClass
    public void cleanupSession()
    {
    	
        cleanSession(driver);
    }

    /**
     * Test the create property group button works
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testCreatePropertyGroupButton() throws Exception
    {
        String dialogueHeader = "Create Aspect";
        CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = createNewPropertyGroupForModel();
        Assert.assertNotNull(createNewPropertyGroupPopUp);
        Assert.assertNotNull(createNewPropertyGroupPopUp.getDialogueTitle());
        Assert.assertTrue(dialogueHeader.equals(createNewPropertyGroupPopUp.getDialogueTitle()));
        ManageTypesAndAspectsPage manageAspectsPage = createNewPropertyGroupPopUp.selectCloseButton().render();

        Assert.assertNotNull(manageAspectsPage);
    }

    /**
     * Test setting the name on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testSetName() throws Exception
    {
        CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = createNewPropertyGroupForModel();

        createNewPropertyGroupPopUp.setNameField(name).render();

        Assert.assertEquals(createNewPropertyGroupPopUp.getNameField(), name, "Name field text displayed correctly");

        createNewPropertyGroupPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the parent property group on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testSetParentPropertyGroup() throws Exception
    {
        CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = createNewPropertyGroupForModel();

        createNewPropertyGroupPopUp.setParentPropertyGroupField(parentPropertyGroup).render();

        Assert.assertEquals(
                createNewPropertyGroupPopUp.getParentPropertyGroupField(),
                parentPropertyGroup,
                "Parent property group field text displayed correctly");

        Assert.assertTrue(createNewPropertyGroupPopUp.isGroupDisplayedInParentList(parentPropertyGroup));

        createNewPropertyGroupPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the title on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testSetTitle() throws Exception
    {
        CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = createNewPropertyGroupForModel();

        createNewPropertyGroupPopUp.setTitleField(name).render();

        Assert.assertEquals(createNewPropertyGroupPopUp.getTitleField(), name, "Title field text displayed correctly");

        createNewPropertyGroupPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the description on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testSetDescription() throws Exception
    {
        CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = createNewPropertyGroupForModel();

        createNewPropertyGroupPopUp.setDescriptionField(name).render();

        Assert.assertEquals(createNewPropertyGroupPopUp.getDescriptionField(), name, "Description field text displayed correctly");

        createNewPropertyGroupPopUp.selectCloseButton().render();
    }

    /**
     * Test creating a property group works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testCreatePropertyGroup() throws Exception
    {
        CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = createNewPropertyGroupForModel();

        createNewPropertyGroupPopUp.setNameField(name);
        createNewPropertyGroupPopUp.setParentPropertyGroupField(parentPropertyGroup);
        createNewPropertyGroupPopUp.setTitleField(name);
        createNewPropertyGroupPopUp.setDescriptionField(name);

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = createNewPropertyGroupPopUp.selectCreateButton().render();
        String compoundName = name + ":" + name;
        ModelPropertyGroupRow modelPropertyGroupRow = manageTypesAndAspectsPage.getCustomModelPropertyGroupRowByName(compoundName);

        Assert.assertNotNull(modelPropertyGroupRow, "The new property group was not found");
    }

    /**
     * Test validations of a property group
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testCreatePropertyGroupValidations() throws Exception
    {
        CreateNewPropertyGroupPopUp newPGPopUp = createNewPropertyGroupForModel();

        // Commented out as error behaviour is changed. Messages will only be displayed on invalid input.
        // Discussed within CMM that tests for validations etc are in scope for Aikau team

        // Assert.assertFalse(createNewPropertyGroupPopUp.isNameValidationMessageDisplayed(), "There should be a name validation message shown");
        // Assert.assertFalse(newPGPopUp.isParentAspectValidationMessageDisplayed(), "There should not be a parent property group validation message shown");
        // Assert.assertFalse(createNewPropertyGroupPopUp.isTitleValidationMessageDisplayed(), "There should not be a title validation message shown");
        // Assert.assertFalse(createNewPropertyGroupPopUp.isDescriptionValidationMessageDisplayed(),
        // "There should not be a description validation message shown");

        Assert.assertTrue(newPGPopUp.isCancelButtonEnabled(), "The cancel button should be enabled");
        Assert.assertFalse(newPGPopUp.isCreateButtonEnabled(), "The create button should not be enabled");

        newPGPopUp.setNameField(name);
        newPGPopUp.setParentPropertyGroupField(parentPropertyGroup);
        newPGPopUp.setTitleField(name);
        newPGPopUp.setDescriptionField(name);

        Assert.assertFalse(newPGPopUp.isNameValidationMessageDisplayed(), "There should not be a name validation message shown");
        Assert.assertFalse(newPGPopUp.isParentAspectValidationMessageDisplayed(), "There should not be a parent property group validation message shown");
        Assert.assertFalse(newPGPopUp.isTitleValidationMessageDisplayed(), "There should not be a title validation message shown");
        Assert.assertFalse(newPGPopUp.isDescriptionValidationMessageDisplayed(), "There should not be a description validation message shown");

        Assert.assertTrue(newPGPopUp.isCancelButtonEnabled(), "The cancel button should be enabled");
        Assert.assertTrue(newPGPopUp.isCreateButtonEnabled(), "The create button should be enabled");

        newPGPopUp.setNameField("!£$%^&");

        Assert.assertTrue(newPGPopUp.isNameValidationMessageDisplayed(), "There should be a name validation message shown - illegal chars");

        Assert.assertTrue(newPGPopUp.isCancelButtonEnabled(), "The cancel button should be enabled");
        Assert.assertFalse(newPGPopUp.isCreateButtonEnabled(), "The create button should not be enabled");

        newPGPopUp.selectCloseButton().render();
    }

    /**
     * Navigate to property group pop up.
     * 
     * @return the creates the new property group pop up
     * @throws Exception the exception
     */
    private CreateNewPropertyGroupPopUp createNewPropertyGroupForModel()
    {
        ModelManagerPage modelPage = cmmActions.navigateToModelManagerPage(driver).render();

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = modelPage.selectCustomModelRowByName(name).render();

        return manageTypesAndAspectsPage.clickCreateNewPropertyGroupButton().render();
    }

}
