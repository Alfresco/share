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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.IndexingOptions;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class PropertyPopUpIndexingTest
 * 
 * @author Meenal Bhave
 */
@SuppressWarnings("unused")
@Listeners(FailedTestListener.class)
public class PropertyPopUpIndexingTest extends AbstractTestCMM
{
    /** The logger */
    private static Log logger = LogFactory.getLog(PropertyPopUpIndexingTest.class);

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

        // Select Type
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

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

    private HtmlPage getManagePropertiesPage(String typeAspectName) throws Exception
    {
        mpp = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        return cmmActions.viewProperties(driver, typeAspectName);
    }

    /**
     * Verify text property indexing options
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void textPropertyValidIndexingOptionsTest() throws Exception
    {
        String propname = "text";

        // Verify text Property Indexing Options
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "none",
                propname + "none",
                propname + "none",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.None);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "free",
                propname + "free",
                propname + "free",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.FreeText);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "lovWhole",
                propname + "lovWhole",
                propname + "lovWhole",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.LOVPartial);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "lovPartial",
                propname + "lovPartial",
                propname + "lovPartial",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.LOVWhole);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "PatternMany",
                propname + "PatternMany",
                propname + "PatternMany",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.PatternMany);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "PatternUnique",
                propname + "PatternUnique",
                propname + "PatternUnique",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.PatternUnique);
    }

    /**
     * Verify invalid options for indexing for text property results in exception
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2, expectedExceptions = PageOperationException.class)
    public void textPropertyInvalidIndexingOptionsTest() throws Exception
    {
        String propname = "text";

        // Error on Invalid Type
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "basic",
                propname + "basic",
                propname + "basic",
                DataType.Text,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Basic);
    }

    /**
     * Verify mltext property indexing options
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void mltextPropertyValidIndexingOptionsTest() throws Exception
    {
        String propname = "mltext";

        getManagePropertiesPage(compoundTypeName);

        // Verify text Property Indexing Options
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "none",
                propname + "none",
                propname + "none",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.None);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "free",
                propname + "free",
                propname + "free",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.FreeText);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "lovWhole",
                propname + "lovWhole",
                propname + "lovWhole",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.LOVPartial);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "lovPartial",
                propname + "lovPartial",
                propname + "lovPartial",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.LOVWhole);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "PatternMany",
                propname + "PatternMany",
                propname + "PatternMany",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.PatternMany);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "PatternUnique",
                propname + "PatternUnique",
                propname + "PatternUnique",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.PatternUnique);
    }

    /**
     * Verify invalid options for indexing for mltext property results in exception
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4, expectedExceptions = PageOperationException.class)
    public void mltextPropertyInvalidIndexingOptionsTest() throws Exception
    {
        String propname = "mltext";

        getManagePropertiesPage(compoundTypeName);

        // Error on Invalid Type
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "enhanced",
                propname + "enhanced",
                propname + "enhanced",
                DataType.MlText,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Basic);
    }

    /**
     * Verify nonText property indexing options
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void nonTextPropertyValidIndexingOptionsTest() throws Exception
    {
        String propname = "nonTextNum";

        getManagePropertiesPage(compoundPGName);

        // Verify text Property Indexing Options
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "none",
                propname + "none",
                propname + "none",
                DataType.Int,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.None);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "basic",
                propname + "basic",
                propname + "basic",
                DataType.Long,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Basic);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "FloatEn",
                propname + "FloatEn",
                propname + "FloatEn",
                DataType.Float,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Enhanced);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "doubleEn",
                propname + "doubleEn",
                propname + "doubleEn",
                DataType.Double,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Enhanced);

        propname = "nonTextDate";

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "dateEn",
                propname + "dateEn",
                propname + "dateEn",
                DataType.Date,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Enhanced);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "dttmBasic",
                propname + "dttmBasic",
                propname + "dttmBasic",
                DataType.DateTime,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Basic);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "dttmNone",
                propname + "dttmNone",
                propname + "dttmNone",
                DataType.DateTime,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.None);
    }

    /**
     * Verify invalid options for indexing for nontext property results in exception
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 6, expectedExceptions = PageOperationException.class)
    public void nonTextPropertyInvalidIndexingOptionsTest() throws Exception
    {
        String propname = "nonTextNum";

        getManagePropertiesPage(compoundPGName);

        // Error on Invalid Type
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "FreeText",
                propname + "FreeText",
                propname + "FreeText",
                DataType.Int,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.FreeText);
    }

    /**
     * Verify boolean property indexing options
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void booleanPropertyValidIndexingOptionsTest() throws Exception
    {
        String propname = "boolean";

        getManagePropertiesPage(compoundPGName);

        // Verify text Property Indexing Options
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "none",
                propname + "none",
                propname + "none",
                DataType.Boolean,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.None);

        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "basic",
                propname + "basic",
                propname + "basic",
                DataType.Boolean,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Basic);
    }

    /**
     * Verify invalid options for indexing for boolean property results in exception
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 8, expectedExceptions = PageOperationException.class)
    public void booleanPropertyInvalidEnhIndexingOptionsTest() throws Exception
    {
        String propname = "boolean";

        getManagePropertiesPage(compoundTypeName);

        // Error on Invalid Type
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "Enhanced",
                propname + "Enhanced",
                propname + "Enhanced",
                DataType.Boolean,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.Enhanced);
    }

    /**
     * Verify invalid options for indexing for boolean property results in exception
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 9, expectedExceptions = PageOperationException.class)
    public void booleanPropertyInvalidIndexingOptionsTest() throws Exception
    {
        String propname = "boolean";

        getManagePropertiesPage(compoundTypeName);

        // Error on Invalid Type
        cmmActions.createPropertyWithIndexingOption(
                driver,
                propname + "LOVWhole",
                propname + "LOVWhole",
                propname + "FreeTLOVWholeext",
                DataType.Boolean,
                MandatoryClassifier.Optional,
                false,
                "",
                IndexingOptions.LOVWhole);
    }
}
