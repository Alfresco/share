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
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
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
 * The Class EditPropertyPopUpTest
 * 
 * @author Charu
 */
@SuppressWarnings("unused")
@Listeners(FailedTestListener.class)
public class EditPropertyPopUpTest extends AbstractTestCMM
{
    /** The logger */
    private static Log logger = LogFactory.getLog(CreateNewPropertyPopUpTest.class);
    @Autowired CmmActions cmmActions;
    ModelManagerPage mmp;
    CreateNewModelPopUp cnmp;
    ManageTypesAndAspectsPage mtaap;
    CreateNewCustomTypePopUp cnctp;
    CreateNewPropertyGroupPopUp cnpgp;
    CreateNewPropertyPopUp cnpp;
    ManagePropertiesPage mpp;
    EditPropertyPopUp epp;

    private String name = "model1" + System.currentTimeMillis();
    private String name2 = "model2" + System.currentTimeMillis();
    private String typename = "modeltype1" + System.currentTimeMillis();
    private String propGroupName = "modelpropgroup1" + System.currentTimeMillis();
    private String tPropName = "tpn" + System.currentTimeMillis();
    private String apropName = "apn" + System.currentTimeMillis();
    private String compoundTypeName = name + ":" + typename;
    private String compoundPGName = name + ":" + propGroupName;
    private String compoundPropertyName = name + ":" + tPropName;
    private String compoundPropertyName2 = name + ":" + apropName;

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
        Assert.assertTrue(mtaap.isCustomTypeRowDisplayed(compoundTypeName));

        // Create new property Group
        cnpgp = mtaap.clickCreateNewPropertyGroupButton().render();
        cnpgp.setNameField(propGroupName).render();
        cnpgp.setDescriptionField(propGroupName).render();
        cnpgp.setTitleField(propGroupName).render();
        mtaap = cnpgp.selectCreateButton().render();
        Assert.assertTrue(mtaap.isPropertyGroupRowDisplayed(compoundPGName));

    }

    /**
     * Logout at the end
     */
    @AfterClass
    public void cleanupSession()
    {
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        if (mmp.getCustomModelRowByName(name).getCmActions().hasActionByName(deleteAction))
        {
            mmp.getCustomModelRowByName(name).getCmActions().clickActionByNameAndDialogByButtonName(deleteAction, deleteAction);
        }
        else
        {
            mmp.getCustomModelRowByName(name).getCmActions().clickActionByName("Deactivate");
            mmp = cmmActions.navigateToModelManagerPage(driver).render();
            mmp.getCustomModelRowByName(name).getCmActions().clickActionByNameAndDialogByButtonName(deleteAction, deleteAction);
        }

        cleanSession(driver);
    }

    /**
     * Verify Edit property pop-up form works
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void editpropertiesPopupFormTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "1", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // navigate to edit pop up
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "1");

        // Verify form initial state
        Assert.assertFalse(epp.isNameEnabled(), "Name field should be disabled");
        Assert.assertFalse(epp.isTitleValidationMessageDisplayed(), "Title validation message should not be shown");
        Assert.assertFalse(epp.isDescriptionValidationMessageDisplayed(), "Description validation message should not be shown");
        Assert.assertFalse(epp.isDataTypeValidationMessageDisplayed(), "DataType validation message should not be shown");
        Assert.assertFalse(epp.isMandatoryValidationMessageDisplayed(), "Mandatory validation message should not be shown");
        Assert.assertTrue(epp.isMultipleFieldSelected(), "Multiple checkbox should not be selected");
        Assert.assertFalse(epp.isDefaultTextValueValidationMessageDisplayed(), "Default value validation message should not be shown");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "The Save button should not be enabled");
        epp.selectCancelButton();

        // Edit Property
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        // SHA: 787, 1260: Removal of cm:content from the Property data types
        mpp = cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropertyName + "1",
                "content",
                "content",
                DataType.MlTextContent,
                MandatoryClassifier.Mandatory,
                true,
                "content").render();

        // navigate to edit pop up
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp1 = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "1").render();

        // Verify the state of the form after completion
        Assert.assertEquals(epp1.getTitleField(), "content", "Title field did not set correctly");
        Assert.assertEquals(epp1.getDescriptionField(), "content", "Description field did not set correctly");
        Assert.assertEquals(epp1.getDataTypeField(), mlText, "Data type field did not set correctly");
        Assert.assertEquals(epp1.getMandatoryField(), mandatoryProperty, "Mandatory field did not set correctly");
        Assert.assertFalse(epp1.isMultipleFieldSelected(), "Multiple checkbox should be selected");
        Assert.assertEquals(epp1.getDefaultTextValueField(), "content", "Default value field did not set correctly");
        Assert.assertFalse(epp1.isTitleValidationMessageDisplayed(), "Title validation message should not be shown");
        Assert.assertFalse(epp1.isDescriptionValidationMessageDisplayed(), "Description validation message should not be shown");
        Assert.assertFalse(epp1.isDataTypeValidationMessageDisplayed(), "DataType validation message should not be shown");
        Assert.assertFalse(epp1.isMandatoryValidationMessageDisplayed(), "Mandatory validation message should not be shown");
        Assert.assertFalse(epp1.isDefaultTextValueValidationMessageDisplayed(), "Default value validation message should not be shown");
        Assert.assertTrue(epp1.isSaveButtonEnabled(), "The create button should be enabled");
        mpp = epp1.selectCloseButton().render();

        // Check if the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "1"), "New property was not found");

    }

    /**
     * Verify property can be Edited
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void propertiesPopupEditTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "2", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // Edit Property
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editProperty(driver, compoundTypeName, compoundPropertyName + "2", "1", "1", DataType.Int, MandatoryClassifier.Mandatory, false, "1")
                .render();

        // Find the edited row in the list of properties
        PropertyRow row = mpp.getPropertyRowByName(compoundPropertyName + "2");
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertEquals(row.getDisplayLabel(), "1", "Display label is not updated correctly");
        Assert.assertEquals(row.getDatatype(), propertyDatatypeInt, "data type not updated correctly");

        // Visit properties via group
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundPGName).render();
        mpp = cmmActions.createProperty(driver, apropName + "1", apropName, apropName, DataType.Text, MandatoryClassifier.Optional, true, apropName).render();

        // Visit properties via property group
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editProperty(driver, compoundPGName, compoundPropertyName2 + "1", "1", "1", DataType.Int, MandatoryClassifier.Mandatory, false, "1")
                .render();

        // Find the new row in the list of properties
        row = mpp.getPropertyRowByName(compoundPropertyName2 + "1");
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertEquals(row.getDatatype(), propertyDatatypeInt, "New property type is not correct");

    }

    /**
     * Verify property can be edited leaving optional fields blank
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void propertiesPopupEditTestBlankOptionals() throws Exception
    {

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "3", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // Visit properties via property group
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropertyName + "3",
                "float",
                "float",
                DataType.Float,
                MandatoryClassifier.Mandatory,
                false,
                "1.6789").render();

        // Find the new row in the list of properties
        PropertyRow row = mpp.getPropertyRowByName(compoundPropertyName + "3");
        Assert.assertNotNull(row, "New property not found by name");
        Assert.assertFalse(row.getDisplayLabel().isEmpty(), "Display label not empty");
        Assert.assertNotNull(row.getDatatype(), "New property type is not correct");

    }

    /**
     * Verify property can be edited and cancelled
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void propertiesPopupEditCancelTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "4", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // Visit properties via property group
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropertyName + "4",
                "date",
                "date",
                DataType.Date,
                MandatoryClassifier.Mandatory,
                false,
                "1/12/2040").render();

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "4").render();

        // Populate the form and submit
        epp.setTitleField("newdate");
        epp.setDescriptionField("newdate");
        epp.setDataTypeField(propertyDatatypeDate);
        epp.setMandatoryField(mandatoryProperty);
        epp.clickMultipleField();
        epp.setDefaultDateValueField("01/01/2100");
        mpp = epp.selectCancelButton().render();

        // Find the new row in the list of properties
        PropertyRow row = mpp.getPropertyRowByName(compoundPropertyName + "4");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "4"), "Property is displayed");
        Assert.assertEquals(row.getDisplayLabel(), "date", "Display label modified");
        Assert.assertEquals(row.getDatatype(), propertyDatatypeDate, "property data type modified");

    }

    /**
     * Create with regex
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void propertiesPopupEditWithRegexConstraint() throws Exception
    {

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "5", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, "regextrue").render();

        // SHA-961: Removal of Regex Match Required option
        /*
         * // Visit properties via type
         * mtaap = mpp.selectBackToTypesPropertyGroupsButton();
         * EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(drone, compoundTypeName, compoundPropertyName + "5").render();
         * ConstraintDetails constraintDetails = new ConstraintDetails();
         * constraintDetails.setValue("regexfalse");
         * constraintDetails.setMatchRequired(false);
         * epp.editRegexConstraint(constraintDetails);
         * mpp = epp.selectSaveButton().render();
         * // Find the new row in the list of properties
         * Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "5"), "Property is not created");
         */

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp1 = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "5").render();

        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setValue("regextrue");
        constraintDetails1.setMatchRequired(true);

        epp1.editRegexConstraint(constraintDetails1);

        mpp = epp1.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "5"), "Property is not created");

    }

    /**
     * Create with length
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void propertiesPopupEditWithLengthConstraint() throws Exception
    {

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "6", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, "length").render();

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "6").render();

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setMinValue(2);
        constraintDetails.setMaxValue(20023);

        epp.editLengthConstraint(constraintDetails);

        mpp = epp.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "6"), "Property is not created");

    }

    /**
     * Create with minmax
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void propertiesPopupEditWithMinMaxConstraint() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "7", tPropName, tPropName, DataType.Int, MandatoryClassifier.Optional, true, "20022").render();

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "7").render();

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(20023);

        epp.editMinMaxValueConstraint(constraintDetails);

        mpp = epp.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "7"), "Property is not created");

    }

    /**
     * Create with list
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 8, enabled = false)
    public void propertiesPopupEditWithListConstraint() throws Exception
    {

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "8", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, " ").render();

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "8").render();

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setValue("One\nTwo\nThree\nFour\n");
        constraintDetails.setSorted(true);
        epp.editListConstraint(constraintDetails);

        mpp = epp.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "8"), "Property is not displayed");

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp1 = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "8").render();

        epp1.setDataTypeField(propertyDatatypeInt);
        epp1.setDefaultTextValueField("4");
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setValue("1\n2\n3\n4\n5\n");
        constraintDetails1.setSorted(false);
        epp1.editListConstraint(constraintDetails1);

        mpp = epp1.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "8"), "Property is not displayed");

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp2 = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "8").render();

        epp2.setDataTypeField(propertyDatatypeFloat);
        epp2.setDefaultNumberValueField("456.05678");
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setValue("1\n25.67578\n-356.54678\n-45678\n55433.77655\n");
        constraintDetails2.setSorted(false);
        epp2.editListConstraint(constraintDetails2);

        mpp = epp2.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "8"), "Property is not displayed");
    }

    /**
     * Test constraint validations
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 9, enabled = false)
    public void propertiesPopupEditWithConstraintValidations() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "9", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "9").render();
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");

        // Exercise regex constraint validation
        epp.setConstraintField(propertyConstraintRegex);
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintExpressionField("regex");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");

        // Exercise length constraint validation
        epp.setConstraintField(propertyConstraintLength);
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");
        epp.setConstraintMinLengthField("abc");
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintMinLengthField("10");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");
        epp.setConstraintMaxLengthField("abc");
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintMaxLengthField("10");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");

        // Exercise minmax constraint validation
        epp.setConstraintField(propertyConstraintMinmax);
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");
        epp.setConstraintMinValueField("abc");
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintMinValueField("10");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");
        epp.setConstraintMaxValueField("abc");
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintMaxValueField("10");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");

        // Exercise list constraint validation
        epp.setConstraintField(propertyConstraintList);
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintAllowedValuesField("One\nTwo\nThree\n\nFour\n");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");

        // Exercise class constraint validation
        epp.setConstraintField(propertyConstraintClass);
        Assert.assertFalse(epp.isSaveButtonEnabled(), "Save button should not be enabled");
        epp.setConstraintClassField("class");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "Save button should be enabled");
        mpp = epp.selectCancelButton().render();

    }

    /**
     * Test Regex constraint
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 10, enabled = false)
    public void testPropertyEditWithConstraintRegex() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "10", tPropName, tPropName, DataType.MlText, MandatoryClassifier.Optional, true, tPropName).render();

        // Add Property With Constraint: Regex
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue("regex");

        // Edit property with constraints
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "10",
                " ",
                " ",
                DataType.Text,
                MandatoryClassifier.Mandatory,
                true,
                "",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "10"), "Property is displayed");

        // Add Property With Constraint: Regex
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.REGEX);
        constraintDetails1.setValue("[1-10]*");

        // Edit property with constraints
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "10",
                " ",
                " ",
                DataType.Int,
                MandatoryClassifier.Mandatory,
                true,
                "3",
                constraintDetails1).render();

    }

    /**
     * Test Length constraint
     * 
     * @throws Exception the exception
     */

    @Test(groups = { "Enterprise-only" }, priority = 11, enabled = false)
    public void testEditPropertyWithConstraintLength()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mtaap.selectCustomTypeRowByName(compoundTypeName);

        // Add Property With Constraint: MINMAXLENGTH
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails.setMinValue(10);
        constraintDetails.setMaxValue(200);

        // Edit property with Constarint1: MINIMAXLENGTH
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails1.setMinValue(0);
        constraintDetails1.setMaxValue(1800);

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                tPropName + "11",
                "",
                "",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                constraintDetails).render();

        // Edit property with constraints
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "11",
                "",
                "",
                DataType.Int,
                MandatoryClassifier.Mandatory,
                true,
                "0",
                constraintDetails1).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "11"), "Property is displayed");
    }

    /**
     * Test value constraint
     * 
     * @throws Exception the exception
     */

    @Test(groups = { "Enterprise-only" }, priority = 12, enabled = false)
    public void testEditPropertyWithConstraintValue()
    {

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mtaap.selectCustomTypeRowByName(compoundTypeName);

        // Add Property With Constraint: MINMAXVALUE
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(1800);

        // Edit Property With Constraint: MINMAXVALUE
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails1.setMinValue(1);
        constraintDetails1.setMaxValue(2000);

        cmmActions.createPropertyWithConstraint(driver, tPropName + "121", "", "", DataType.Int, MandatoryClassifier.Optional, false, "18", constraintDetails)
                .render();

        cmmActions
                .createPropertyWithConstraint(driver, tPropName + "122", "", "", DataType.Long, MandatoryClassifier.Optional, false, "1245", constraintDetails)
                .render();

        cmmActions.createPropertyWithConstraint(
                driver,
                tPropName + "123",
                "",
                "",
                DataType.Float,
                MandatoryClassifier.Optional,
                false,
                "1234.678",
                constraintDetails).render();

        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                tPropName + "124",
                "",
                "",
                DataType.Double,
                MandatoryClassifier.Optional,
                false,
                "1345.7654",
                constraintDetails).render();

        // Edit property with constraints
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "121",
                "",
                "",
                DataType.Long,
                MandatoryClassifier.Mandatory,
                true,
                "1234",
                constraintDetails1).render();

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "122",
                "",
                "",
                DataType.Float,
                MandatoryClassifier.Mandatory,
                true,
                "1345.6789",
                constraintDetails1).render();

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "123",
                "",
                "",
                DataType.Double,
                MandatoryClassifier.Mandatory,
                true,
                "1456.34",
                constraintDetails1).render();

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "124",
                "",
                "",
                DataType.Int,
                MandatoryClassifier.Mandatory,
                true,
                "20",
                constraintDetails1).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "121"), "Property is displayed");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "122"), "Property is displayed");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "123"), "Property is displayed");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "124"), "Property is displayed");

    }

    /**
     * Test List constraint
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 13, enabled = false)
    public void testEditPropertyWithConstraintList()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mtaap.selectCustomTypeRowByName(compoundTypeName);

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.LIST);
        constraintDetails.setValue("a\nb\nc\nd\ne");

        cmmActions.createPropertyWithConstraint(driver, tPropName + "131", "", "", DataType.Text, MandatoryClassifier.Optional, false, "a", constraintDetails)
                .render();

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("1\n11\n10\n2\n3\n4\n5\n6\n7\n8\n9");
        constraintDetails1.setSorted(false);

        // Amended the dataType from long to Int for SHA-1077
        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                tPropName + "132",
                "",
                "",
                DataType.Int,
                MandatoryClassifier.Optional,
                false,
                "",
                constraintDetails1).render();

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.LIST);
        constraintDetails2.setValue("1\n11\n10\n2\n3\n4\n5\n6\n7\n8\n9");

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "131",
                "",
                "",
                DataType.Int,
                MandatoryClassifier.Mandatory,
                true,
                "3",
                constraintDetails2).render();

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.LIST);
        constraintDetails3.setValue("1\n11\n10\n2\n3\n4\n5\n-6\n7\n8\n9\n12\n13");
        constraintDetails3.setSorted(true);

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "132",
                "",
                "",
                DataType.Float,
                MandatoryClassifier.Mandatory,
                false,
                "-6",
                constraintDetails3).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "131"), "Property is displayed");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "132"), "Property is displayed");

    }

    /**
     * Create with class
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 14, enabled = false)
    public void propertiesPopupEditWithClassConstraint() throws Exception
    {

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "14", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // Visit properties via type
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "14").render();

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setValue("org.alfresco.repo.dictionary.constraint.UserNameConstraint");

        epp.editJavaClassConstraint(constraintDetails);

        mpp = epp.selectSaveButton().render();

        // Find the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "14"), "Property is not displayed");

    }

    @Test(groups = { "Enterprise-only" }, priority = 15, enabled = false)
    public void testCreatePropertyWithConstraintJavaClass()
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mtaap.selectCustomTypeRowByName(compoundTypeName);

        // Add Property With Constraint: JAVACLASS
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.JAVACLASS);
        constraintDetails.setValue("org.alfresco.repo.dictionary.constraint.UserNameConstraint");

        mpp = cmmActions.createProperty(driver, tPropName + "class", "", "", DataType.Text, MandatoryClassifier.Optional, false, " ").render();

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraint(
                driver,
                compoundTypeName,
                compoundPropertyName + "class",
                "",
                "",
                DataType.Text,
                MandatoryClassifier.Mandatory,
                true,
                " ",
                constraintDetails).render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "class"), "Property is not created");

    }

    /**
     * Verify Edit property pop-up for active model property without constraint
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 16)
    public void editpropPopupForActiveModelTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mmp = mmp.getCustomModelRowByName(name).getCmActions().clickActionByName("Activate").render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();
        mpp = cmmActions.createProperty(driver, tPropName + "16", tPropName, tPropName, DataType.Text, MandatoryClassifier.Optional, true, tPropName).render();

        // navigate to edit pop up
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "1").render();

        // Verify form initial state
        Assert.assertFalse(epp.isNameEnabled(), "Name field should be disabled");
        Assert.assertTrue(epp.isDataTypeDisabled(), "Data Type field should be disabled");
        Assert.assertTrue(epp.isMandatoryDisabled(), "Mandatory field should be disabled");
        Assert.assertTrue(epp.isMultipleCBDisabled(), "Multiple CB field should be disabled");
        Assert.assertFalse(epp.isTitleValidationMessageDisplayed(), "Title validation message should not be shown");
        Assert.assertFalse(epp.isDescriptionValidationMessageDisplayed(), "Description validation message should not be shown");
        Assert.assertFalse(epp.isDataTypeValidationMessageDisplayed(), "DataType validation message should not be shown");
        Assert.assertFalse(epp.isMandatoryValidationMessageDisplayed(), "Mandatory validation message should not be shown");
        Assert.assertFalse(epp.isDefaultTextValueValidationMessageDisplayed(), "Default value validation message should not be shown");
        Assert.assertTrue(epp.isSaveButtonEnabled(), "The Save button should not be enabled");
        epp.selectCancelButton();

        // Edit Property
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyForAM(driver, compoundTypeName, compoundPropertyName + "16", "content", "content", "content").render();

        // navigate to edit pop up
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        EditPropertyPopUp epp1 = cmmActions.getEditPropertyPopUp(driver, compoundTypeName, compoundPropertyName + "1");

        // Verify the state of the form after completion
        Assert.assertEquals(epp1.getTitleField(), "content", "Title field did not set correctly");
        Assert.assertEquals(epp1.getDescriptionField(), "content", "Description field did not set correctly");
        Assert.assertTrue(epp1.isDataTypeDisabled(), "Data type field disabled");
        Assert.assertTrue(epp1.isMultipleCBDisabled(), "Mandatory field disabled");
        Assert.assertEquals(epp1.getDefaultTextValueField(), "content", "Default value field did not set correctly");
        Assert.assertFalse(epp1.isTitleValidationMessageDisplayed(), "Title validation message should not be shown");
        Assert.assertFalse(epp1.isDescriptionValidationMessageDisplayed(), "Description validation message should not be shown");
        Assert.assertFalse(epp1.isDataTypeValidationMessageDisplayed(), "DataType validation message should not be shown");
        Assert.assertFalse(epp1.isMandatoryValidationMessageDisplayed(), "Mandatory validation message should not be shown");
        Assert.assertFalse(epp1.isDefaultTextValueValidationMessageDisplayed(), "Default value validation message should not be shown");
        Assert.assertTrue(epp1.isSaveButtonEnabled(), "The create button should be enabled");
        mpp = epp1.selectCloseButton().render();

        // Check if the new row in the list of properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "16"), "New property was not found");

    }

    /**
     * Verify Edit property pop-up for active model property with constraint
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 17, enabled = false)
    public void editpropPopupForAMWCTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mmp = mmp.getCustomModelRowByName(name).getCmActions().clickActionByName("Activate").render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = cmmActions.viewProperties(driver, compoundTypeName).render();

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.LIST);

        // TODO: Amended the LOVs suitable for Int for SHA-1077
        constraintDetails.setValue("-1.5\n23\n3444.6789\n-87\n6.0");

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("1\n11\n10\n2\n3\n4\n5\n6\n7\n8\n9");
        constraintDetails1.setSorted(false);

        // TODO: Amended the dataType from long to Int for SHA-1077
        mpp = cmmActions.createPropertyWithConstraint(
                driver,
                tPropName + "17",
                "",
                "",
                DataType.Double,
                MandatoryClassifier.Optional,
                false,
                "-1.5",
                constraintDetails).render();

        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compoundTypeName, compoundPropertyName + "17", "content", "content", "3", constraintDetails1)
                .render();

        // Check the properties
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName + "17"), "Property is displayed");

    }

}
