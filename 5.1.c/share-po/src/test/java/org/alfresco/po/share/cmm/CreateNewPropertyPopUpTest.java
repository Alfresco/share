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

import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class CreateNewPropertyPopUpTest
 * 
 * @author Richard Smith
 * @author Meenal Bhave
 */
@SuppressWarnings("unused")
@Listeners(FailedTestListener.class)
public class CreateNewPropertyPopUpTest extends AbstractTestCMM
{
    /** The logger */
    private static Log logger = LogFactory.getLog(CreateNewPropertyPopUpTest.class);


    ModelManagerPage mmp;

    CreateNewModelPopUp cnmp;

    ManageTypesAndAspectsPage mtaap;

    CreateNewCustomTypePopUp cnctp;

    CreateNewPropertyGroupPopUp cnpgp;

    ManagePropertiesPage mpp;

    CreateNewPropertyPopUp cnpp;

    private String name = "model1" + System.currentTimeMillis();

    private String name2 = "model2" + System.currentTimeMillis();

    private String typename = "modeltype1" + System.currentTimeMillis();

    private String propGroupName = "modelpropgroup1" + System.currentTimeMillis();

    private String compoundTypeName = name + ":" + typename;

    private String compoundPGName = name + ":" + propGroupName;

    private String compoundPropertyName = name + ":" + name;

    private String compoundPropertyName2 = name + ":" + name2;

    String propertyName = "Prop" + System.currentTimeMillis();

    String compoundPropName = name + ":" + propertyName;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {

        loginAs(username, password);

        // Navigate to the CMM page
        mmp = cmmActions.navigateToModelManagerPage(driver).render();

        // Create a model
        CreateNewModelPopUp cnmp = mmp.clickCreateNewModelButton().render();
        cnmp.setName(name);
        cnmp.setNameSpace(name).render();
        cnmp.setPrefix(name);
        cnmp.setDescription(name).render();
        mmp = cnmp.selectCreateModelButton("Create").render();

        // Navigate to the tpg page
        mtaap = mmp.selectCustomModelRowByName(name).render();

        // Create a custom type
        cnctp = mtaap.clickCreateNewCustomTypeButton().render();
        cnctp.setNameField(typename);
        cnctp.setDescriptionField(typename);
        cnctp.setTitleField(typename);
        mtaap = cnctp.selectCreateButton().render();

        // Create a property group
        cnpgp = mtaap.clickCreateNewPropertyGroupButton().render();
        cnpgp.setNameField(propGroupName).render();
        cnpgp.setDescriptionField(propGroupName).render();
        cnpgp.setTitleField(propGroupName).render();
        mtaap = cnpgp.selectCreateButton().render();

    }

    /**
     * Logout at the end
     */
    @AfterClass
    public void cleanupSession()
    {
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mmp.getCustomModelRowByName(name).getCmActions().clickActionByNameAndDialogByButtonName(deleteAction, deleteAction);
        cleanSession(driver);
    }

    /**
     * Verify property pop-up loads
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void propertiesPopupLoadsTest() throws Exception
    {

        String dialogueHeader = createPropertyDialogueHeader;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Verify the pop-up dialogue is not null and has the correct title
        Assert.assertNotNull(cnpp);
        Assert.assertEquals(cnpp.getDialogueTitle(), dialogueHeader, "The create property dialogue header is incorrect");

        // Close the dialogue
        mpp = cnpp.selectCloseButton().render();

        // Go back and visit properties via property group
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Verify the pop-up dialogue is not null and has the correct title
        Assert.assertNotNull(cnpp);
        Assert.assertEquals(cnpp.getDialogueTitle(), dialogueHeader, "The create property dialogue header is incorrect");

        // Close the dialogue
        mpp = cnpp.selectCloseButton().render();
        Assert.assertNotNull(mpp);

    }

    /**
     * Verify property pop-up form works
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void propertiesPopupFormTest() throws Exception
    {


        String propName = name + "close";
        String compoundPropName = name + ":" + propName;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Verify form initial state
        // Commented out as error behaviour is changed. Messages will only be displayed on invalid input.
        // Discussed within CMM that tests for validations etc are in scope for Aikau team

        // Assert.assertTrue(cnpp.isNameValidationMessageDisplayed(), "Name validation message should be shown");
        // Assert.assertFalse(cnpp.isTitleValidationMessageDisplayed(), "Title validation message should not be shown");
        // Assert.assertFalse(cnpp.isDescriptionValidationMessageDisplayed(), "Description validation message should not be shown");
        // Assert.assertFalse(cnpp.isDataTypeValidationMessageDisplayed(), "DataType validation message should not be shown");
        // Assert.assertFalse(cnpp.isMandatoryValidationMessageDisplayed(), "Mandatory validation message should not be shown");
        // Assert.assertFalse(cnpp.isMultipleFieldSelected(), "Multiple checkbox should not be selected");
        // Assert.assertFalse(cnpp.isDefaultTextValueValidationMessageDisplayed(), "Default value validation message should not be shown");

        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "The create button should not be enabled");

        // Populate the form
        cnpp.setNameField(propName);
        cnpp.setTitleField(propName);
        cnpp.setDescriptionField(propName);
        cnpp.setDataTypeField(propertyDatatype);
        cnpp.setMandatoryField(mandatoryProperty);
        cnpp.clickMultipleField();
        cnpp.setDefaultTextValueField(propName);
        cnpp = cnpp.render();

        // Verify the state of the form after completion
        Assert.assertEquals(cnpp.getNameField(), propName, "Name field did not set correctly");
        Assert.assertEquals(cnpp.getTitleField(), propName, "Title field did not set correctly");
        Assert.assertEquals(cnpp.getDescriptionField(), propName, "Description field did not set correctly");
        Assert.assertEquals(cnpp.getDataTypeField(), propertyDatatype, "Data type field did not set correctly");
        Assert.assertEquals(cnpp.getMandatoryField(), mandatoryProperty, "Mandatory field did not set correctly");
        Assert.assertTrue(cnpp.isMultipleFieldSelected(), "Multiple checkbox should be selected");
        Assert.assertEquals(cnpp.getDefaultTextValueField(), propName, "Default value field did not set correctly");
        Assert.assertFalse(cnpp.isNameValidationMessageDisplayed(), "Name validation message should be shown");
        Assert.assertFalse(cnpp.isTitleValidationMessageDisplayed(), "Title validation message should not be shown");
        Assert.assertFalse(cnpp.isDescriptionValidationMessageDisplayed(), "Description validation message should not be shown");
        Assert.assertFalse(cnpp.isDataTypeValidationMessageDisplayed(), "DataType validation message should not be shown");
        Assert.assertFalse(cnpp.isMandatoryValidationMessageDisplayed(), "Mandatory validation message should not be shown");
        Assert.assertFalse(cnpp.isDefaultTextValueValidationMessageDisplayed(), "Default value validation message should not be shown");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "The create button should be enabled");

        // Close the popup dialogue
        mpp = cnpp.selectCloseButton().render();

        // Check if the new row in the list of properties
        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropName), "New property was found");

    }

    /**
     * Verify property can be created
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void propertiesPopupCreateTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(name);
        cnpp.setTitleField(name);
        cnpp.setDescriptionField(name);
        cnpp.setDataTypeField(propertyDatatype);
        cnpp.setMandatoryField(mandatoryProperty);
        cnpp.clickMultipleField();
        cnpp.setDefaultTextValueField(name);
        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        PropertyRow row = mpp.getPropertyRowByName(compoundPropertyName);
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertEquals(row.getDatatype(), propertyDatatype, "New property type is not correct");

        // Visit properties via property group
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();

        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(name2);
        cnpp.setTitleField(name2);
        cnpp.setDescriptionField(name2);
        cnpp.setDataTypeField(propertyDatatype);
        cnpp.setMandatoryField(mandatoryProperty);
        cnpp.clickMultipleField();
        cnpp.setDefaultTextValueField(name2);
        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        row = mpp.getPropertyRowByName(compoundPropertyName2);
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertEquals(row.getDatatype(), propertyDatatype, "New property type is not correct");

    }

    /**
     * Verify property can be created leaving optional fields blank
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void propertiesPopupCreateTestBlankOptionals() throws Exception
    {

        String propName = name + "opt";
        String compoundPropertyName = name + ":" + propName;


        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the mandatory fields on the form and submit
        cnpp.setNameField(propName);
        // cnpp.setTitleField(propName);
        // cnpp.setDescriptionField(propName);
        // cnpp.setDataTypeField(datatype);
        // cnpp.setMandatoryField(optional);
        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        PropertyRow row = mpp.getPropertyRowByName(compoundPropertyName);
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertEquals(row.getDatatype(), propertyDatatype, "New property type is not correct");

        // Find the new row in the list of properties
        row = mpp.getPropertyRowByName(compoundPropertyName);
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertTrue(row.getDisplayLabel().isEmpty(), "Display label not empty");
        Assert.assertNotNull(row.getDatatype(), "New property type is not correct");
        // Assert.assertNotNull(row.getTitle(), "Title is not correct");
        // Assert.assertNotNull(row.getInfoMandatory(), "Mandatory Info is not correct");
    }

    /**
     * Verify property can be created
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void propertiesPopupCreateCancelTest() throws Exception
    {
        String propName = "NewProp" + System.currentTimeMillis();
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(propName);
        cnpp.setTitleField(propName);
        cnpp.setDescriptionField(propName);
        cnpp.setDataTypeField(propertyDatatype);
        cnpp.setMandatoryField(mandatoryProperty);
        cnpp.clickMultipleField();
        cnpp.setDefaultTextValueField(propName);
        mpp = cnpp.selectCancelButton().render();

        // Find the new row in the list of properties
        Assert.assertFalse(mpp.isPropertyRowDisplayed(propName), "Property is created");

    }

    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testCreatePropertyWithConstraintNone()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Add Property With Constraint: None
        ConstraintDetails constraintDetails = new ConstraintDetails();

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "none",
                "",
                "",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "none"), "Property is not created");
    }

    /**
     * Create with regex
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testCreatePropertyWithConstraintRegexMatchReq() throws Exception
    {

        String propName = "NewProp" + System.currentTimeMillis();
        String compoundPropertyName = name + ":" + propName;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(propName);
        cnpp.setTitleField("");
        cnpp.setDefaultTextValueField("regex");

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setValue("regex");
        constraintDetails.setMatchRequired(true);

        cnpp.addRegexConstraint(constraintDetails);

        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property is not created");

    }

    @Test(groups = { "Enterprise-only" }, priority = 8)
    public void testCreatePropertyWithConstraintRegex()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Add Property With Constraint: Regex
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue("regex");
        // SHA-961: Removal of Regex Match Required option
        // constraintDetails.setMatchRequired(false);

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "regex",
                "",
                "",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "regex"), "Property is not created");
    }

    /**
     * Create with length
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 9)
    public void testCreatePropertyWithConstraintLength() throws Exception
    {

        String propName = "NewProp" + System.currentTimeMillis();
        String compoundPropertyName = name + ":" + propName;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(propName);

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setMinValue(200);
        constraintDetails.setMaxValue(20023);

        cnpp.addLengthConstraint(constraintDetails);

        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property is not created");

    }

    @Test(groups = { "Enterprise-only" }, priority = 10)
    public void testCreatePropertyWithConstraintLengthWithDefault()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Add Property With Constraint: MINMAXLENGTH
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(18);

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "length",
                "",
                "",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "Not Set",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "length"), "Property is not created");
    }

    /**
     * Create with minmax
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 11)
    public void testCreatePropertyWithConstraintValueText() throws Exception
    {

        String propName = "NewProp" + System.currentTimeMillis();
        String compoundPropertyName = name + ":" + propName;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render().render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(propName);
        cnpp.setDataTypeField(propertyDatatypeInt);
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(20023);

        cnpp.addMinMaxValueConstraint(constraintDetails);

        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property is not created");

    }

    @Test(groups = { "Enterprise-only" }, priority = 12)
    public void testCreatePropertyWithConstraintValueNumeric()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Add Property With Constraint: MINMAXVALUE
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(18);

        cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "value",
                "",
                "",
                DataType.Int,
                MandatoryClassifier.Optional,
                false,
                "18",
                constraintDetails).render();

        cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "valueL",
                "",
                "",
                DataType.Long,
                MandatoryClassifier.Optional,
                false,
                "1",
                constraintDetails).render();

        cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "valueF",
                "",
                "",
                DataType.Float,
                MandatoryClassifier.Optional,
                false,
                "10",
                constraintDetails).render();

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "valueD",
                "",
                "",
                DataType.Double,
                MandatoryClassifier.Optional,
                false,
                "0",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "value"), "Property is not created");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "valueL"), "Property is not created");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "valueF"), "Property is not created");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "valueD"), "Property is not created");
    }

    /**
     * Create with class
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 13)
    public void testCreatePropertyWithConstraintJavaClass() throws Exception
    {

        String propName = "NewProp" + System.currentTimeMillis();
        String compoundPropertyName = name + ":" + propName;
        String invalidClassName = "org.alfresco.notavalidclass";

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(propName);

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setValue("org.alfresco.repo.dictionary.constraint.UserNameConstraint");

        cnpp.addJavaClassConstraint(constraintDetails);

        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property is not created");

        // Create Another Property: Invalid
        cnpp = mpp.clickCreateNewPropertyButton().render();

        cnpp.setNameField(propName + "InvalidClass");

        // Invalid Value fails to create Constraint
        constraintDetails.setValue(invalidClassName);

        cnpp.addJavaClassConstraint(constraintDetails);

        Assert.assertEquals(cnpp.getConstraintClassField(), invalidClassName, "ClassName set incorrectly");

        cnpp = cnpp.selectCreateButton().render();
        mpp = cnpp.selectCloseButton().render();

        // Find the new row in the list of properties
        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropertyName + "InvalidClass"), "Property is created with invalid JavaClassName");
    }

    @Test(groups = { "Enterprise-only" }, priority = 14)
    public void testCreatePropertyWithConstraintJavaClassWithDefault()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Add Property With Constraint: JAVACLASS
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.JAVACLASS);
        constraintDetails.setValue("org.alfresco.repo.dictionary.constraint.UserNameConstraint");

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "class",
                "",
                "",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "admin",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "class"), "Property is not created");

    }

    /**
     * Test constraint validations
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 15)
    public void propertiesPopupCreateWithConstraintValidations() throws Exception
    {
        String propName = "NewProp" + System.currentTimeMillis();
        String compoundPropertyName = name + ":" + propName;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form
        cnpp.setNameField(propName);
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");

        // Exercise regex constraint validation
        cnpp.setConstraintField(propertyConstraintRegex);
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintExpressionField("regex");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");

        // Exercise length constraint validation
        cnpp.setConstraintField(propertyConstraintLength);
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");
        cnpp.setConstraintMinLengthField("abc");
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintMinLengthField("10");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");
        cnpp.setConstraintMaxLengthField("abc");
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintMaxLengthField("10");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");

        // Exercise minmax constraint validation
        cnpp.setConstraintField(propertyConstraintMinmax);
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");
        cnpp.setConstraintMinValueField("abc");
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintMinValueField("10");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");
        cnpp.setConstraintMaxValueField("abc");
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintMaxValueField("10");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");

        // Exercise class constraint validation
        cnpp.setConstraintField(propertyConstraintClass);
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintClassField("class");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");

        // Exercise list constraint validation
        cnpp.setConstraintField(propertyConstraintList);
        Assert.assertFalse(cnpp.isCreateButtonEnabled(), "Create button should not be enabled");
        cnpp.setConstraintAllowedValuesField("One\nTwo\nThree\n\nFour\n");
        Assert.assertTrue(cnpp.isCreateButtonEnabled(), "Create button should be enabled");

        mpp = cnpp.selectCancelButton().render();

    }

    @Test(groups = { "Enterprise-only" }, priority = 16)
    public void testCreatePropertyWithConstraintListWithDefault()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.LIST);
        constraintDetails.setValue("a\nb\nc\nd\ne");
        constraintDetails.setSorted(false);

        cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "list",
                "",
                "",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "a",
                constraintDetails);

        constraintDetails.setValue("1\n11\n10\n2\n3\n4\n5\n6\n7\n8\n9");
        constraintDetails.setSorted(true);

        // Amended the dataType from long to Int for SHA-1077
        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "listint",
                "",
                "",
                DataType.Int,
                MandatoryClassifier.Optional,
                false,
                "6",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "list"), "Property is not created");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "listint"), "Property is not created");
    }

    /**
     * Create with list
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 17)
    public void testCreatePropertyWithConstraintList() throws Exception
    {
        String propName = "NewProp" + System.currentTimeMillis();
        String compoundPropertyName = name + ":" + propName;

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render().render();
        cnpp = mpp.clickCreateNewPropertyButton().render();

        // Populate the form and submit
        cnpp.setNameField(propName);

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setValue("One\nTwo\nThree\n\nFour\n");
        constraintDetails.setSorted(true);

        cnpp.addListConstraint(constraintDetails);

        mpp = cnpp.selectCreateButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property is not created");

    }

}
