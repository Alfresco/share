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
package org.alfresco.po.share.cmm;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
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
 * The Class CreateNewCustomTypePopUpTest
 * 
 * @author Richard Smith
 */
@Listeners(FailedTestListener.class)
public class CreateNewCustomTypePopUpTest extends AbstractTestCMM
{
    @SuppressWarnings("unused")
    private SharePage page;
    private String name = "model1" + System.currentTimeMillis();
    @Autowired private CmmActions cmmActions;
    private String parenttype = "cm:thumbnail (Thumbnail)";
    private String compoundName = name + ":" + name;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {

        page = loginAs(username, password);

        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
        createNewModelPopUpPage.setNameSpace(name);
        createNewModelPopUpPage.setPrefix(name);
        createNewModelPopUpPage.setName(name);
        createNewModelPopUpPage.setDescription(name);

        cmmPage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(name), "Custom Model Row is not displayed");

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
     * Test the create property group button works
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testClickCreateNewCustomTypeButton() throws Exception
    {
        CreateNewCustomTypePopUp createNewCustomTypePopUp = navigateToCustomTypePopUp();
        Assert.assertNotNull(createNewCustomTypePopUp);
        Assert.assertNotNull(createNewCustomTypePopUp.getDialogueTitle());
        ManageTypesAndAspectsPage manageAspectsPage = createNewCustomTypePopUp.selectCloseButton().render();
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
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();

        createNewCustomTypeTPopUp.setNameField(name);

        Assert.assertEquals(createNewCustomTypeTPopUp.getNameField(), name, "Name field text displayed correctly");

        createNewCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the title on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testSetTitle() throws Exception
    {
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();

        createNewCustomTypeTPopUp.setTitleField(name);

        Assert.assertEquals(createNewCustomTypeTPopUp.getTitleField(), name, "Title field text displayed correctly");

        createNewCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the description on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testSetDescription() throws Exception
    {
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();

        createNewCustomTypeTPopUp.setDescriptionField(name);

        Assert.assertEquals(createNewCustomTypeTPopUp.getDescriptionField(), name, "Description field text displayed correctly");

        createNewCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the parent property group on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testSetCustomType() throws Exception
    {
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();

        createNewCustomTypeTPopUp.selectParentTypeField(parenttype).render();

        Assert.assertEquals(createNewCustomTypeTPopUp.getParentPropertyTypeField(), parenttype, "Parent property group field text displayed correctly");

        Assert.assertTrue(createNewCustomTypeTPopUp.isTypeDisplayedInParentList(parenttype), "Type displayed");

        createNewCustomTypeTPopUp.selectCloseButton().render();
    }

    /**
     * Validating error messages on CreateNewCustomTypePopUp with valid data select cancel button
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testCancelButtonTest() throws Exception
    {
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();
        // Commented out as error behaviour is changed. Messages will only be displayed on invalid input.
        // Discussed within CMM that tests for validations etc are in scope for Aikau team

        // Assert.assertFalse(createNewCustomTypeTPopUp.isNameValidationMessageDisplayed(), "Name Validation message displayed successfully");
        // Assert.assertFalse(createNewCustomTypeTPopUp.isDescriptionValidationMessageDisplayed(), "Desc Validation message is not displayed successfully");
        // Assert.assertFalse(createNewCustomTypeTPopUp.isTitleValidationMessageDisplayed(), "Title Validation message is not displayed successfully");
        Assert.assertFalse(createNewCustomTypeTPopUp.isCreateButtonEnabled(), "Create button enabled successfully");
        Assert.assertTrue(createNewCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");

        createNewCustomTypeTPopUp.setNameField(name);

        createNewCustomTypeTPopUp.setDescriptionField(name);
        Assert.assertFalse(createNewCustomTypeTPopUp.isDescriptionValidationMessageDisplayed(), "Desc Validation message is not displayed successfully");

        createNewCustomTypeTPopUp.setTitleField(name);
        Assert.assertFalse(createNewCustomTypeTPopUp.isTitleValidationMessageDisplayed(), "Title Validation message is not displayed successfully");

        Assert.assertTrue(createNewCustomTypeTPopUp.isCreateButtonEnabled(), "Create button disabled successfully");
        Assert.assertTrue(createNewCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = createNewCustomTypeTPopUp.selectCancelButton().render();
        Assert.assertFalse(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compoundName), "Custom Type Row disaplayed");
    }

    /**
     * Validating error messages on CreateNewCustomTypePopUp with valid data select create button
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testValidationMessTest() throws Exception
    {
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();

        // Commented out as error behaviour is changed. Messages will only be displayed on invalid input.
        // Discussed within CMM that tests for validations etc are in scope for Aikau team

        // Assert.assertTrue(createNewCustomTypeTPopUp.isNameValidationMessageDisplayed(), "Name Validation message displayed successfully");
        // Assert.assertFalse(createNewCustomTypeTPopUp.isDescriptionValidationMessageDisplayed(), "Desc Validation message is not displayed successfully");
        // Assert.assertFalse(createNewCustomTypeTPopUp.isTitleValidationMessageDisplayed(), "Title Validation message is not displayed successfully");

        Assert.assertFalse(createNewCustomTypeTPopUp.isCreateButtonEnabled(), "Create button enabled successfully");
        Assert.assertTrue(createNewCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");

        createNewCustomTypeTPopUp.setNameField(name);

        createNewCustomTypeTPopUp.setDescriptionField(name);
        Assert.assertFalse(createNewCustomTypeTPopUp.isDescriptionValidationMessageDisplayed(), "Desc Validation message is not displayed successfully");

        createNewCustomTypeTPopUp.setTitleField(name);
        Assert.assertFalse(createNewCustomTypeTPopUp.isTitleValidationMessageDisplayed(), "Title Validation message is not displayed successfully");

        Assert.assertTrue(createNewCustomTypeTPopUp.isCreateButtonEnabled(), "Create button disabled successfully");
        Assert.assertTrue(createNewCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = createNewCustomTypeTPopUp.selectCreateButton().render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compoundName), "Custom Type Row disaplayed");
    }

    /**
     * Validating error messages on CreateNewCustomTypePopUp with invalid data
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testValidationMessinvaliddataTest() throws Exception
    {
        String specialtext = "!!!";
        CreateNewCustomTypePopUp createNewCustomTypeTPopUp = navigateToCustomTypePopUp();

        createNewCustomTypeTPopUp.setNameField(specialtext);
        createNewCustomTypeTPopUp.setDescriptionField(specialtext);
        createNewCustomTypeTPopUp.setTitleField(specialtext);

        Assert.assertFalse(createNewCustomTypeTPopUp.isCreateButtonEnabled(), "Create button disabled successfully");
        Assert.assertTrue(createNewCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");

        Assert.assertFalse(createNewCustomTypeTPopUp.isDescriptionValidationMessageDisplayed(), "Desc Validation message is not displayed successfully");
        Assert.assertFalse(createNewCustomTypeTPopUp.isTitleValidationMessageDisplayed(), "Title Validation message is not displayed successfully");

        Assert.assertFalse(createNewCustomTypeTPopUp.isCreateButtonEnabled(), "Create button disabled successfully");
        Assert.assertTrue(createNewCustomTypeTPopUp.isCancelButtonEnabled(), "Create button disabled successfully");

        createNewCustomTypeTPopUp.selectCancelButton().render();
    }

    /**
     * Navigate to property group pop up.
     * 
     * @return the creates the new property group pop up
     * @throws Exception the exception
     */
    private CreateNewCustomTypePopUp navigateToCustomTypePopUp() throws Exception
    {
        ModelManagerPage modelManagerPage = cmmActions.navigateToModelManagerPage(driver).render();
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = modelManagerPage.selectCustomModelRowByName(name).render();
        CreateNewCustomTypePopUp createNewCustomTypePopUp = manageTypesAndAspectsPage.clickCreateNewCustomTypeButton().render(); 
        return createNewCustomTypePopUp;
    }

}
