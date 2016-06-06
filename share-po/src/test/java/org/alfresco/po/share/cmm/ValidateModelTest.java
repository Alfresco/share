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

import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class ValidateModelTest
 * 
 * @author Meenal Bhave
 */
@SuppressWarnings("unused")
@Listeners(FailedTestListener.class)
public class ValidateModelTest extends AbstractTestCMM
{
    /** The logger */
    private static Log logger = LogFactory.getLog(ValidateModelTest.class);

    private static final String CREATE_PROPERTY_DIALOGUE_HEADER = "cmm.dialogue.label.create.property";

    private static final String CREATE_PROPERTY_DATATYPE = "cmm.property.datatype";

    private static final String CREATE_PROPERTY_DATATYPE_INT = "cmm.property.datatype.int";

    private static final String CREATE_PROPERTY_MANDATORY = "cmm.property.mandatory";

    private static final String CREATE_PROPERTY_OPTIONAL = "cmm.property.optional";

    private static final String CREATE_PROPERTY_CONSTRAINT_NONE = "cmm.property.constraint.none";

    private static final String CREATE_PROPERTY_CONSTRAINT_REGEX = "cmm.property.constraint.regex";

    private static final String CREATE_PROPERTY_CONSTRAINT_LENGTH = "cmm.property.constraint.length";

    private static final String CREATE_PROPERTY_CONSTRAINT_MINMAX = "cmm.property.constraint.minmax";

    private static final String CREATE_PROPERTY_CONSTRAINT_LIST = "cmm.property.constraint.list";

    private static final String CREATE_PROPERTY_CONSTRAINT_CLASS = "cmm.property.constraint.class";

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
        mmp.getCustomModelRowByName(name).getCmActions().clickActionByNameAndDialogByButtonName(deleteAction,deleteAction);

        cleanSession(driver);
    }

    /**
     * Verify Validate Model while creating Property for Type
     * Java Class Invalid
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testValidateCreatePropertyWithJavaClass() throws Exception
    {

        String uniqueString = "org.alfresco." + System.currentTimeMillis();

        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Verify the Model is not saved / Property isn't created when invalid
        ConstraintDetails constraintDetails = new ConstraintDetails();

        // Java Class: Class: Invalid
        constraintDetails.setType(ConstraintTypes.JAVACLASS);
        constraintDetails.setValue(uniqueString);

        cmmActions.createPropertyWithConstraint(driver, propertyName + "jc", "", "", DataType.Text, MandatoryClassifier.Optional, false, "", constraintDetails)
                .render();

        mpp = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();

        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropName + "jc"), "Property created when Model is not valid");

    }

    /**
     * Verify Validate Model while Creating Property for Type:
     * Regex Constraint: Match Required, Default value does not match
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testValidateCreatePropertyWithRegex() throws Exception
    {
        // Regex Match Required: Default value valid
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setMatchRequired(true);
        constraintDetails.setValue("[a-z]");

        // SHA: 787, 1260: Removal of cm:content from the Property data types
        cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "regex",
                "",
                "",
                DataType.MlTextContent,
                MandatoryClassifier.Optional,
                false,
                "A",
                constraintDetails).render();

        mpp = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();

        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropName + "regex"), "Property created when Model is not valid");
    }

    /**
     * Verify Validate Model while Creating Property for Property Group:
     * MinMax Length Constraint: Default value shorter than allowed range
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testValidateCreatePropertyWithLength() throws Exception
    {
        // Go back and visit properties via property group
        cmmActions.navigateToModelManagerPage(driver);
        mtaap = cmmActions.viewTypesAspectsForModel(driver, name).render();

        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();

        ConstraintDetails constraintDetails = new ConstraintDetails();
        // MinMax Constraint: Default value shorter
        constraintDetails.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails.setMinValue(6);
        constraintDetails.setMaxValue(8);

        cmmActions.createPropertyWithConstraint(
                driver,
                propertyName + "length",
                "",
                "",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "small",
                constraintDetails).render();

        mpp = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();

        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropName + "length"), "Property created when Model is not valid");
    }

    /**
     * Verify Validate Model while Creating Property for Property Group:
     * DataType Date / DateTime: Default Value not compliant with data-type
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4, enabled = false)
    public void testValidateCreatePropertyDateValue() throws Exception
    {
        String validDate = "12/12/2012";
        String validDateTime = validDate + "00:00:05";

        // Go back and visit properties via property group
        cmmActions.navigateToModelManagerPage(driver);
        mtaap = cmmActions.viewTypesAspectsForModel(driver, name).render();

        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();

        cmmActions.createProperty(driver, propertyName + "date", "", "", DataType.Date, MandatoryClassifier.Optional, false, "date").render();

        mpp = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();

        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropName + "date"), "Property created when Model is not valid");

        cmmActions.createProperty(driver, propertyName + "dateTime", "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "0").render();

        mpp = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();

        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropName + "dateTime"), "Property created when Model is not valid");

        mpp = cmmActions.createProperty(driver, propertyName + "dateV", "", "", DataType.Date, MandatoryClassifier.Optional, false, validDate).render();

        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropName + "dateV"), "Property created when Model is not valid");
    }
}
