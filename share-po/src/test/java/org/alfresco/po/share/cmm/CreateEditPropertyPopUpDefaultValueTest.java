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

import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class CreateEditPropertyPopUpDefaultValueTest extends AbstractTestCMM
{
    /** The logger */
    private static Log logger = LogFactory.getLog(CreateEditPropertyPopUpDefaultValueTest.class);

    private static final String CREATE_PROPERTY_DIALOGUE_HEADER = "cmm.dialogue.label.create.property";

    private static final String CREATE_PROPERTY_DATATYPE = "cmm.property.datatype";

    private static final String CREATE_PROPERTY_DATATYPE_INT = "cmm.property.datatype.int";

    private static final String CREATE_PROPERTY_MANDATORY = "cmm.property.mandatory";

    private static final String CREATE_PROPERTY_OPTIONAL = "cmm.property.optional";

    private static final String ACTION_DELETE = "cmm.model.action.delete";

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

    String propertyNoDef = "prop" + System.currentTimeMillis();

    String propertyDef = "propDef" + System.currentTimeMillis();

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {

        loginAs(username, password);

        // Navigate to the CMM page
        cmmActions.navigateToModelManagerPage(driver);

        // Create a model
        mmp = cmmActions.createNewModel(driver, name).render();

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
        String delete = deleteAction;

        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mmp.getCustomModelRowByName(name).getCmActions().clickActionByNameAndDialogByButtonName(delete, delete);

        cleanSession(driver);
    }

    /**
     * Verify create property pop-up with no defaults specified
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testCreatePropertiesNoDefaultsTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Create Properties without Default
        cmmActions.createProperty(driver, propertyNoDef + "t", propertyNoDef + "t", "", DataType.Text, MandatoryClassifier.Mandatory, false, "");
        cmmActions.createProperty(driver, propertyNoDef + "ml", propertyNoDef + "ml", "", DataType.MlText, MandatoryClassifier.Optional, false, "");
        
        cmmActions.createProperty(driver, propertyNoDef + "bool", propertyNoDef + "bool", "", DataType.Boolean, MandatoryClassifier.Mandatory, false, "");

        cmmActions.createProperty(driver, propertyNoDef + "int", propertyNoDef + "int", "", DataType.Int, MandatoryClassifier.Mandatory, false, "");
        cmmActions.createProperty(driver, propertyNoDef + "dbl", propertyNoDef + "dbl", "", DataType.Double, MandatoryClassifier.Mandatory, false, "");
        cmmActions.createProperty(driver, propertyNoDef + "fl", propertyNoDef + "fl", "", DataType.Float, MandatoryClassifier.Mandatory, false, "");
        cmmActions.createProperty(driver, propertyNoDef + "long", propertyNoDef + "long", "", DataType.Long, MandatoryClassifier.Mandatory, false, "");

        cmmActions.createProperty(driver, propertyNoDef + "dt", propertyNoDef + "dt", "", DataType.Date, MandatoryClassifier.Mandatory, false, "");
        cmmActions.createProperty(driver, propertyNoDef + "dtm", propertyNoDef + "dttm", "", DataType.DateTime, MandatoryClassifier.Mandatory, false, "");
    }

    /**
     * Verify create property pop-up with default values specified
     * 
     * @throws Exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testCreatePropertiesWithDefaultsTest() throws Exception
    {
        String dateTime = "01-12-2016";

        // Visit properties via Aspect
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();

        // Create Properties without Default
        cmmActions.createProperty(driver, propertyDef + "t", propertyDef + "t", "", DataType.Text, MandatoryClassifier.Mandatory, false, "text");
        cmmActions.createProperty(driver, propertyDef + "ml", propertyDef + "ml", "", DataType.MlText, MandatoryClassifier.Optional, false, "mlText");
        
        cmmActions.createProperty(driver, propertyDef + "bool", propertyDef + "bool", "", DataType.Boolean, MandatoryClassifier.Mandatory, false, "False");

        cmmActions.createProperty(driver, propertyDef + "int", propertyDef + "int", "", DataType.Int, MandatoryClassifier.Mandatory, false, "0");
        cmmActions.createProperty(driver, propertyDef + "dbl", propertyDef + "dbl", "", DataType.Double, MandatoryClassifier.Mandatory, false, "-37.5");
        cmmActions.createProperty(driver, propertyDef + "fl", propertyDef + "fl", "", DataType.Float, MandatoryClassifier.Mandatory, false, "3.142");
        cmmActions.createProperty(driver, propertyDef + "long", propertyDef + "long", "", DataType.Long, MandatoryClassifier.Mandatory, false, "999999999");

        cmmActions.createProperty(driver, propertyDef + "dt", propertyDef + "dt", "", DataType.Date, MandatoryClassifier.Mandatory, false, getAikauDateEntryDMY());
        cmmActions.createProperty(driver, propertyDef + "dtm", propertyDef + "dttm", "", DataType.DateTime, MandatoryClassifier.Mandatory, false, getAikauDateEntryDMY());
    }

    /**
     * Verify create property pop-up with no defaults specified
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3, dependsOnMethods = "testCreatePropertiesNoDefaultsTest")
    public void testEditPropertiesNoDefaultsTest() throws Exception
    {
        String compoundPropName = name + ":" + propertyNoDef;
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Create Properties without Default
        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "t",
                compoundPropName + "t",
                "",
                DataType.Text,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "ml",
                compoundPropName + "ml",
                "",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "");
        
        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "bool",
                compoundPropName + "bool",
                "",
                DataType.Boolean,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "int",
                compoundPropName + "int",
                "",
                DataType.Int,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "dbl",
                compoundPropName + "dbl",
                "",
                DataType.Double,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "fl",
                compoundPropName + "fl",
                "",
                DataType.Float,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "long",
                compoundPropName + "long",
                "",
                DataType.Long,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "dt",
                compoundPropName + "dt",
                "",
                DataType.Date,
                MandatoryClassifier.Mandatory,
                false,
                "");

        cmmActions.editProperty(
                driver,
                compoundTypeName,
                compoundPropName + "dtm",
                compoundPropName + "dttm",
                "",
                DataType.DateTime,
                MandatoryClassifier.Mandatory,
                false,
                "");
    }

    /**
     * Verify create property pop-up with default values specified
     * 
     * @throws Exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4, dependsOnMethods = "testCreatePropertiesWithDefaultsTest")
    public void testEditPropertiesWithDefaultsTest() throws Exception
    {
        String compoundpropName = name + ":" + propertyDef;

        String dateTime = "01/02/2016";
        
        // Visit properties via Aspect
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();

        // Create Properties without Default
        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "t",
                compoundpropName + "t",
                "",
                DataType.Text,
                MandatoryClassifier.Mandatory,
                false,
                "text");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "ml",
                compoundpropName + "ml",
                "",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "mlText");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "bool",
                compoundpropName + "bool",
                "",
                DataType.Boolean,
                MandatoryClassifier.Mandatory,
                false,
                "True");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "int",
                compoundpropName + "int",
                "",
                DataType.Int,
                MandatoryClassifier.Mandatory,
                false,
                "0");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "dbl",
                compoundpropName + "dbl",
                "",
                DataType.Double,
                MandatoryClassifier.Mandatory,
                false,
                "-37.5");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "fl",
                compoundpropName + "fl",
                "",
                DataType.Float,
                MandatoryClassifier.Mandatory,
                false,
                "3.142");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "long",
                compoundpropName + "long",
                "",
                DataType.Long,
                MandatoryClassifier.Mandatory,
                false,
                "999999999");

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "dt",
                compoundpropName + "dt",
                "",
                DataType.Date,
                MandatoryClassifier.Mandatory,
                false,
                getAikauDateEntryDMY());

        cmmActions.editProperty(
                driver,
                compoundPGName,
                compoundpropName + "dt",
                compoundpropName + "dttm",
                "",
                DataType.DateTime,
                MandatoryClassifier.Mandatory,
                false,
                getAikauDateEntryDMY());
    }

}