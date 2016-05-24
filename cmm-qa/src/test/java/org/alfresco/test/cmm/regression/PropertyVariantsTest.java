/*
 * #%L
 * Alfresco CMM Automation QA
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

package org.alfresco.test.cmm.regression;
/**
 * Test Class to test Property Constraints
 * 
 * @author mbhave
 */

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class PropertyVariantsTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(PropertyVariantsTest.class);
    
    private String testName;

    public DashBoardPage dashBoardpage;
    
    private String testUser;
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);   
        
      
        testUser = getUserNameFreeDomain(testName+ System.currentTimeMillis());
        
        //Login as Admin to create a new user
        loginAs(driver, new String[] {username});
        
        //Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, testUser, testUser, testUser, testUser, DEFAULT_PASSWORD, modelAdmin );        
        
        //Logout as admin
        logout(driver);
    }

    /**
     * User logs out after test is executed
     * 
     * @throws Exception
     */
    @AfterMethod
    public void quit() throws Exception
    {
        logout(driver);
    }

    @AlfrescoTest(testlink="tobeaddedview1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testPropNameSameAsTypeAspectModel() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model1" + testName;
        
        String typeName = "type";
        String aspectName = "aspect";
        
        String typePropertyName = typeName;

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositePropNameType = compositeTypeName;
        String compositePropNameAspect = compositeAspectName;
        String compositePropNameModel = modelName + ":" + modelName;

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        cmmActions.createType(driver, typeName).render();

        // Add Aspects: Model1
        cmmActions.createAspect(driver, aspectName).render();
        
        // Create Property for Type: Name same as Type Name
        cmmActions.viewProperties(driver, compositeTypeName);
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, typePropertyName).render();
        
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNameType), "Unable to create Property with name same as type name");
        
        // Create Property for Type: Name same as Aspect Name
        propListPage = cmmActions.createProperty(driver, aspectName).render();
        
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNameAspect), "Unable to create Property with name same as aspect name");
    
        // Create Property for Type: Name same as Model / Prefix Name
        propListPage = cmmActions.createProperty(driver, modelName).render();
        
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNameModel), "Unable to create Property with name same as model / prefix name");   
    }
    
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testDuplicatePropWithinSameTypeAspect() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model2" + testName;
        
        String typeName = "type";
        String aspectName = "aspect";
        
        String propertyName = "property";

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositePropName = modelName + ":" + propertyName;
        String compositePropNameAspect = compositeAspectName;

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();

        // Add Aspects: Model
        cmmActions.createAspect(driver, aspectName).render();
        
        // Create Property for Type
        cmmActions.viewProperties(driver, compositeTypeName);
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyName).render();
        
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropName), "Unable to create Property with name same as type name");
        
        int propCount = propListPage.getPropertyRows().size();
        
        // Create Property for Type: Name same as Property Name above
        cmmActions.createProperty(driver, propertyName);  
        
        // Expect Error: CreateNewPropertyPopUp displayed        
        propListPage = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();
        Assert.assertEquals(propListPage.getPropertyRows().size(), propCount, "Duplicate Property created");
    
        // Create Property for Type: Name same as Model / Prefix Name
        propListPage.selectBackToTypesPropertyGroupsButton().render();
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, propertyName);
        
        // Expect Error: CreateNewPropertyPopUp displayed        
        propListPage = cmmActions.closeShareDialogue(driver, CreateNewPropertyPopUp.class).render();
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositePropNameAspect), "Duplicate Property created");
        Assert.assertEquals(propListPage.getPropertyRows().size(), 0, "Duplicate Property created");
    }
    
    @AlfrescoTest(testlink="tobeaddeddel3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testStringPropertyDefault() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        
        String propertyName = "property";
        String propertyNameWithDefault = "propertyDefault";

        String compositeTypeName = modelName + ":" + typeName;
        
        String compositePropName = modelName + ":" + propertyName;
        String compositePropNameWithDefault = modelName + ":" + propertyNameWithDefault;

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: No Default Set
        cmmActions.createProperty(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
        
        // Create String Property2 for Type: Default Set
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNameWithDefault, "", "", DataType.Text, MandatoryClassifier.Optional, false, "alfresco").render();
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropName), "Unable to create String Property without default value");
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNameWithDefault), "Unable to create String Property with default value");
        
        PropertyRow propRow = propListPage.getPropertyRowByName(compositePropName);
        Assert.assertNotNull(propRow);
        Assert.assertEquals(propRow.getDefaultValue(),"", "Default value incorrect for property: " + compositePropName);
        
        propRow = propListPage.getPropertyRowByName(compositePropNameWithDefault);
        Assert.assertNotNull(propRow);
        Assert.assertEquals(propRow.getDefaultValue(),"alfresco", "Default value incorrect for property: " + compositePropNameWithDefault);
    }
    
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testStringPropertyMandatoryOptional() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        
        String propertyName = "property";
        String propertyEnforced = "propertyEnforced";
        String propertyNotEnforced = "propertyNotEnforced";

        String compositeTypeName = modelName + ":" + typeName;
        
        String compositePropName = modelName + ":" + propertyName;
        String compositePropEnforced = modelName + ":" + propertyEnforced;
        String compositePropNotEnforced = modelName + ":" + propertyNotEnforced;

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: Optional
        cmmActions.createProperty(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
  
        // Create String Property2 for Type: Mandatory Not Enforced
        cmmActions.createProperty(driver, propertyNotEnforced, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "").render();                
         
        // Create String Property3 for Type: Mandatory Enforced
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyEnforced, "", "", DataType.Text, MandatoryClassifier.MANDATORYENF, false, "").render();
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropName), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropEnforced), "Unable to view Property: Mandatory enforced");
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforced), "Unable to view Property: Mandatory Not enforced");
        
        // Check the Mandatory values in the list
        PropertyRow propRow = propListPage.getPropertyRowByName(compositePropName);
        Assert.assertNotNull(propRow);
        Assert.assertEquals(propRow.getMandatory(),optional, "Mandatory value incorrect for property: " + compositePropName);
             
        propRow = propListPage.getPropertyRowByName(compositePropEnforced);
        Assert.assertNotNull(propRow);
        Assert.assertEquals(propRow.getMandatory(),mandatory, "Mandatory value incorrect for property: " + compositePropEnforced);
             
        propRow = propListPage.getPropertyRowByName(compositePropNotEnforced);
        Assert.assertNotNull(propRow);
        Assert.assertEquals(propRow.getMandatory(),mandatory, "Mandatory value incorrect for property: " + compositePropNotEnforced);     
    }
    
    @AlfrescoTest(testlink="tobeaddeddel5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testPropertyDataTypeNoDefaults() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        
        String propertyText = "text";
        String propertyMlText = "mltext";
        
        String propertyContent = "content";
        
        String propertyInt = "int";
        String propertyLong = "long";
        String propertyFloat = "float";
        String propertyDouble = "double";
        
        String propertyDate = "date";
        String propertyDatetime = "dateTime";
        
        String propertyBoolean = "boolean";

        String compositeTypeName = modelName + ":" + typeName;
        
        String compositeText = modelName + ":" + propertyText;
        String compositeMlText = modelName + ":" + propertyMlText;
        String compositeContent = modelName + ":" + propertyContent;
        String compositeInt = modelName + ":" + propertyInt;
        String compositeLong = modelName + ":" + propertyLong;
        String compositeFloat = modelName + ":" + propertyFloat;
        String compositeDouble = modelName + ":" + propertyDouble;
        String compositeDate = modelName + ":" + propertyDate;
        String compositeDatetime = modelName + ":" + propertyDatetime;
        String compositeBoolean = modelName + ":" + propertyBoolean;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create Properties for Type: 
        cmmActions.createProperty(driver, propertyText, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
        cmmActions.createProperty(driver, propertyMlText, "", "", DataType.MlText, MandatoryClassifier.Optional, false, "").render();
        // SHA: 787, 1260: Removal of cm:content from the Property data types        
        cmmActions.createProperty(driver, propertyContent, "", "", DataType.MlTextContent, MandatoryClassifier.Optional, false, "").render();
        cmmActions.createProperty(driver, propertyInt, "", "", DataType.Int, MandatoryClassifier.Optional, false, "").render();
        cmmActions.createProperty(driver, propertyLong, "", "", DataType.Long, MandatoryClassifier.Optional, false, "").render();
        cmmActions.createProperty(driver, propertyFloat, "", "", DataType.Float, MandatoryClassifier.Optional, false, "").render();
        cmmActions.createProperty(driver, propertyDouble, "", "", DataType.Double, MandatoryClassifier.Optional, false, "").render();
        cmmActions.createProperty(driver, propertyDate, "", "", DataType.Date, MandatoryClassifier.Optional, false, "").render();
        cmmActions.createProperty(driver, propertyDatetime, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "").render();
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyBoolean, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "").render();
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeText), "Unable to view Property: " + compositeText);
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeMlText), "Unable to view Property: " + compositeMlText); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeContent), "Unable to view Property: " + compositeContent); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeInt), "Unable to view Property: " + compositeInt); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeLong), "Unable to view Property: " + compositeLong); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeFloat), "Unable to view Property: " + compositeFloat); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDouble), "Unable to view Property: " + compositeDouble); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDate), "Unable to view Property: " + compositeDate);
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDatetime), "Unable to view Property: " + compositeDatetime);
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeBoolean), "Unable to view Property: " + compositeBoolean);        
        
        // Check the Data Type and Default values in the list
        PropertyRow propRow = propListPage.getPropertyRowByName(compositeText);
        Assert.assertEquals(propRow.getDatatype(),datatypet, "DataType incorrect for property: " + compositeText);
        Assert.assertEquals(propRow.getDefaultValue(),"", "Default Value incorrect for property: " + compositeText);

        propRow = propListPage.getPropertyRowByName(compositeMlText);
        Assert.assertEquals(propRow.getDatatype(), mlText, "DataType incorrect for property: " + compositeMlText);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeMlText);

        propRow = propListPage.getPropertyRowByName(compositeContent);
        // SHA: 787, 1260: Removal of cm:content from the Property data types
        Assert.assertEquals(propRow.getDatatype(), mlText, "DataType incorrect for property: " + compositeContent);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeContent);

        propRow = propListPage.getPropertyRowByName(compositeInt);
        Assert.assertEquals(propRow.getDatatype(), datatypei, "DataType incorrect for property: " + compositeInt);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeInt);

        propRow = propListPage.getPropertyRowByName(compositeLong);
        Assert.assertEquals(propRow.getDatatype(),datatypel, "DataType incorrect for property: " + compositeLong);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeLong);

        propRow = propListPage.getPropertyRowByName(compositeFloat);
        Assert.assertEquals(propRow.getDatatype(), datatypef, "DataType incorrect for property: " + compositeFloat);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeFloat);

        propRow = propListPage.getPropertyRowByName(compositeDouble);
        Assert.assertEquals(propRow.getDatatype(), datatyped, "DataType incorrect for property: " + compositeDouble);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeDouble);
        propRow = propListPage.getPropertyRowByName(compositeDate);
        Assert.assertEquals(propRow.getDatatype(), datatypedate, "DataType incorrect for property: " + compositeDate);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeDate);

        propRow = propListPage.getPropertyRowByName(compositeDatetime);
        Assert.assertEquals(propRow.getDatatype(), datatypeDateTime, "DataType incorrect for property: " + compositeDatetime);
        Assert.assertEquals(propRow.getDefaultValue(), "", "Default Value incorrect for property: " + compositeDatetime);

        propRow = propListPage.getPropertyRowByName(compositeBoolean);
        Assert.assertEquals(propRow.getDatatype(), datatypeb, "DataType incorrect for property: " + compositeBoolean);
        Assert.assertEquals(propRow.getDefaultValue(), "false", "Default Value incorrect for property: " + compositeBoolean);
    }
    
    @AlfrescoTest(testlink="tobeaddeddel6")
    @Test(groups = "EnterpriseOnly", priority=6)
    public void testPropertyDataAspectValidDefaults() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String aspectName = "aspect";
        
        String propertyText = "text";
        String propertyMlText = "mltext";
        
        String propertyContent = "content";
        
        String propertyInt = "int";
        String propertyLong = "long";
        String propertyFloat = "float";
        String propertyDouble = "double";
        
        String propertyDate = "date";
        String propertyDatetime = "dateTime";
        
        // SHA:1253: Disallows date formats other than the ones set by Browser Locale
        // String propertyDatetime2 = "dateTime2";
        // String propertyDatetime3 = "dateTime3";
        
        String propertyBoolean = "boolean";
        String propertyBooleanF = "booleanF";

        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositeText = modelName + ":" + propertyText;
        String compositeMlText = modelName + ":" + propertyMlText;
        String compositeContent = modelName + ":" + propertyContent;
        String compositeInt = modelName + ":" + propertyInt;
        String compositeLong = modelName + ":" + propertyLong;
        String compositeFloat = modelName + ":" + propertyFloat;
        String compositeDouble = modelName + ":" + propertyDouble;
        String compositeDate = modelName + ":" + propertyDate;
        String compositeDatetime = modelName + ":" + propertyDatetime;
        
        // SHA:1253: Disallows date formats other than the ones set by Browser Locale
        // String compositeDatetime2 = modelName + ":" + propertyDatetime2;
        // String compositeDatetime3 = modelName + ":" + propertyDatetime3;
        
        String compositeBoolean = modelName + ":" + propertyBoolean;
        String compositeBooleanF = modelName + ":" + propertyBooleanF;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);
        
        // Create Properties for Type: 
        cmmActions.createProperty(driver, propertyText, "", "", DataType.Text, MandatoryClassifier.Optional, false, "Abc123!").render();                
        cmmActions.createProperty(driver, propertyMlText, "", "", DataType.MlText, MandatoryClassifier.Optional, false, "ö").render();
        // SHA: 787, 1260: Removal of cm:content from the Property data types
        cmmActions.createProperty(driver, propertyContent, "", "", DataType.MlTextContent, MandatoryClassifier.Optional, false, "anytext*999").render();
        cmmActions.createProperty(driver, propertyInt, "", "", DataType.Int, MandatoryClassifier.Optional, false, "99999").render();
        cmmActions.createProperty(driver, propertyLong, "", "", DataType.Long, MandatoryClassifier.Optional, false, "1000099999").render();
        cmmActions.createProperty(driver, propertyFloat, "", "", DataType.Float, MandatoryClassifier.Optional, false, "5.5").render();
        cmmActions.createProperty(driver, propertyDouble, "", "", DataType.Double, MandatoryClassifier.Optional, false, "-3.142").render();
        cmmActions.createProperty(driver, propertyDate, "", "", DataType.Date, MandatoryClassifier.Optional, false, dateEntry).render();
        cmmActions.createProperty(driver, propertyDatetime, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, dateEntry).render();
        // SHA:1253: Disallows date formats other than the ones set by Browser Locale
        // cmmActions.createProperty(driver, propertyDatetime2, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "2015-09-15T10:26:36").render();
        // cmmActions.createProperty(driver, propertyDatetime3, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "2015-09-15T10:26:36.00+01:00").render();
        cmmActions.createProperty(driver, propertyBoolean, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "True").render();
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyBooleanF, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "false").render();
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeText), "Unable to view Property: " + compositeText);
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeMlText), "Unable to view Property: " + compositeMlText); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeContent), "Unable to view Property: " + compositeContent); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeInt), "Unable to view Property: " + compositeInt); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeLong), "Unable to view Property: " + compositeLong); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeFloat), "Unable to view Property: " + compositeFloat); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDouble), "Unable to view Property: " + compositeDouble); 
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDate), "Unable to view Property: " + compositeDate);
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDatetime), "Unable to view Property: " + compositeDatetime);
        // SHA:1253: Disallows date formats other than the ones set by Browser Locale
        // Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDatetime2), "Unable to view Property: " + compositeDatetime2);
        // Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeDatetime3), "Unable to view Property: " + compositeDatetime3);
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeBoolean), "Unable to view Property: " + compositeBoolean);        
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositeBooleanF), "Unable to view Property: " + compositeBooleanF);
        
        // Check the Data Type and Default values in the list
        PropertyRow propRow = propListPage.getPropertyRowByName(compositeText);
        Assert.assertEquals(propRow.getDatatype(),datatypet, "DataType incorrect for property: " + compositeText);
        Assert.assertEquals(propRow.getDefaultValue(),"Abc123!", "Default Value incorrect for property: " + compositeText);

        propRow = propListPage.getPropertyRowByName(compositeMlText);
        Assert.assertEquals(propRow.getDatatype(), mlText, "DataType incorrect for property: " + compositeMlText);
        Assert.assertEquals(propRow.getDefaultValue(), "ö", "Default Value incorrect for property: " + compositeMlText);

        propRow = propListPage.getPropertyRowByName(compositeContent);
		// SHA: 787, 1260: Removal of cm:content from the Property data types
        Assert.assertEquals(propRow.getDatatype(), mlText, "DataType incorrect for property: " + compositeContent);
        Assert.assertEquals(propRow.getDefaultValue(), "anytext*999", "Default Value incorrect for property: " + compositeContent);

        propRow = propListPage.getPropertyRowByName(compositeInt);
        Assert.assertEquals(propRow.getDatatype(), datatypei, "DataType incorrect for property: " + compositeInt);
        Assert.assertEquals(propRow.getDefaultValue(), "99999", "Default Value incorrect for property: " + compositeInt);

        propRow = propListPage.getPropertyRowByName(compositeLong);
        Assert.assertEquals(propRow.getDatatype(), datatypel, "DataType incorrect for property: " + compositeLong);
        Assert.assertEquals(propRow.getDefaultValue(), "1000099999", "Default Value incorrect for property: " + compositeLong);

        propRow = propListPage.getPropertyRowByName(compositeFloat);
        Assert.assertEquals(propRow.getDatatype(), datatypef, "DataType incorrect for property: " + compositeFloat);
        Assert.assertEquals(propRow.getDefaultValue(), "5.5", "Default Value incorrect for property: " + compositeFloat);

        propRow = propListPage.getPropertyRowByName(compositeDouble);
        Assert.assertEquals(propRow.getDatatype(), datatyped, "DataType incorrect for property: " + compositeDouble);
        Assert.assertEquals(propRow.getDefaultValue(), "-3.142", "Default Value incorrect for property: " + compositeDouble);

        // SHA:1253: Disallows date formats other than the ones set by Browser Locale
        propRow = propListPage.getPropertyRowByName(compositeDate);
        Assert.assertEquals(propRow.getDatatype(), datatypedate, "DataType incorrect for property: " + compositeDate);
        Assert.assertEquals(propRow.getDefaultValue(), "2100-01-01", "Default Value incorrect for property: " + compositeDate);

        propRow = propListPage.getPropertyRowByName(compositeDatetime);
        Assert.assertEquals(propRow.getDatatype(), datatypeDateTime, "DataType incorrect for property: " + compositeDatetime);
        Assert.assertEquals(propRow.getDefaultValue(), "2100-01-01", "Default Value incorrect for property: " + compositeDatetime);
        // SHA:1253: Disallows date formats other than the ones set by Browser Locale
//        propRow = propListPage.getPropertyRowByName(compositeDatetime2);
//        Assert.assertEquals(propRow.getDatatype(), driver.getElement(DataType.DateTime.getListValue()), "DataType incorrect for property: " + compositeDatetime2);
//        Assert.assertEquals(propRow.getDefaultValue(), "2015-09-15T10:26:36", "Default Value incorrect for property: " + compositeDatetime2);
//
//        propRow = propListPage.getPropertyRowByName(compositeDatetime3);
//        Assert.assertEquals(propRow.getDatatype(), driver.getElement(DataType.DateTime.getListValue()), "DataType incorrect for property: " + compositeDatetime3);
//        Assert.assertEquals(propRow.getDefaultValue(), "2015-09-15T10:26:36.00+01:00", "Default Value incorrect for property: " + compositeDatetime3);
//        
        propRow = propListPage.getPropertyRowByName(compositeBoolean);
        Assert.assertEquals(propRow.getDatatype(), datatypeb, "DataType incorrect for property: " + compositeBoolean);
        Assert.assertEquals(propRow.getDefaultValue(), "true", "Default Value incorrect for property: " + compositeBoolean);
        
        propRow = propListPage.getPropertyRowByName(compositeBooleanF);
        Assert.assertEquals(propRow.getDatatype(), datatypeb, "DataType incorrect for property: " + compositeBooleanF);
        Assert.assertEquals(propRow.getDefaultValue(), "false", "Default Value incorrect for property: " + compositeBooleanF);
    }
    
    @AlfrescoTest(testlink="tobeaddeddel7")
    @Test(groups = "EnterpriseOnly", priority=7)
    public void testPropertyDataInvalidDefaults() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String aspectName = "aspect";
        
        String propertyIntText = "Inttext";
        String propertyIntMax = "IntMax";
        
        String propertyIntNegetive = "IntNegetive";
        
        String propertyIntFloat = "intFloat";
        String propertyLongText = "long";
        String propertyFloatText = "float";
        String propertyDoubleText = "double";
        
        String propertyDateText = "date";
        String propertyDateWrong = "dateWrong";
        String propertyDateNotISO = "dateNotISO";
        
        String propertyDatetimeText = "dateTime";
        String propertyDatetimeWrong = "dateTimeWrong";
        String propertyDatetimeNotISO = "dateTimeNotISO";
        
//        String propertyBooleanNum = "boolean0";
//        String propertyBooleanT = "booleanT";
//        String propertyBooleanYes = "booleanYes";
//        String propertyBooleanNo = "booleanNo";
        
        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositeIntText = modelName + ":" + propertyIntText;
        String compositeIntMax = modelName + ":" + propertyIntMax;
        String compositeIntNegetive = modelName + ":" + propertyIntNegetive;
        String compositeIntFloat = modelName + ":" + propertyIntFloat;
        String compositeLongText = modelName + ":" + propertyLongText;
        String compositeFloatText = modelName + ":" + propertyFloatText;
        String compositeDoubleText = modelName + ":" + propertyDoubleText;
        String compositeDateText = modelName + ":" + propertyDateText;
        String compositeDateWrong = modelName + ":" + propertyDateWrong;
        String compositeDateNotISO = modelName + ":" + propertyDateNotISO;
        String compositeDatetimeText = modelName + ":" + propertyDatetimeText;
        String compositeDatetimeWrong = modelName + ":" + propertyDatetimeWrong;
        String compositeDatetimeNotISO = modelName + ":" + propertyDatetimeNotISO;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);
        
        // Create Properties for Type: Int
        CreateNewPropertyPopUp createPropertyPopup = cmmActions.createProperty(driver, propertyIntText, "", "", DataType.Int, MandatoryClassifier.Optional, false, "Abc123!").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyIntText);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyIntMax, "", "", DataType.Int, MandatoryClassifier.Optional, false, "2147483648").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyIntMax);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyIntNegetive, "", "", DataType.Int, MandatoryClassifier.Optional, false, "-2147483649").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyIntNegetive);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyIntFloat, "", "", DataType.Int, MandatoryClassifier.Optional, false, "99.09").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyIntFloat);
        createPropertyPopup.clickClose().render();
        
        // Create Properties for Type: long
        createPropertyPopup = cmmActions.createProperty(driver, propertyLongText, "", "", DataType.Long, MandatoryClassifier.Optional, false, "abc!").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyLongText);
        createPropertyPopup.clickClose().render();
        
        // Create Properties for Type: float
        createPropertyPopup = cmmActions.createProperty(driver, propertyFloatText, "", "", DataType.Float, MandatoryClassifier.Optional, false, "5.5C").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyFloatText);
        createPropertyPopup.clickClose().render();
        
        // Create Properties for Type: double
        createPropertyPopup = cmmActions.createProperty(driver, propertyDoubleText, "", "", DataType.Double, MandatoryClassifier.Optional, false, "-3..142").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyDoubleText);
        createPropertyPopup.clickClose().render();
        
        // Create Properties for Type: date        
        createPropertyPopup = cmmActions.createProperty(driver, propertyDateText, "", "", DataType.Date, MandatoryClassifier.Optional, false, "2015-Sept-15").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyIntText);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyDateWrong, "", "", DataType.Date, MandatoryClassifier.Optional, false, "2015-15-15").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyDateWrong);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyDateNotISO, "", "", DataType.Date, MandatoryClassifier.Optional, false, "15-09/2015").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyDateNotISO);
        createPropertyPopup.clickClose().render();
        
        // Create Properties for Type: dateTime        
        createPropertyPopup = cmmActions.createProperty(driver, propertyDatetimeText, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "September").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyDatetimeText);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyDatetimeWrong, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "2015-15-09T10:26:36").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyDatetimeWrong);
        createPropertyPopup.clickClose().render();
        
        createPropertyPopup = cmmActions.createProperty(driver, propertyDatetimeNotISO, "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "2015-09-15 10:26:36.00").render();
        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyDatetimeNotISO);
        ManagePropertiesPage propListPage = createPropertyPopup.clickClose().render();

//      SHA-1141: Default Value control changed: Boolean can not be set but selected. Tests not needed anymore        
        // Create Properties for Type: boolean        
//        createPropertyPopup = cmmActions.createProperty(driver, propertyBooleanNum, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "0").render();
//        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyBooleanNum);
//        createPropertyPopup.clickClose().render();
//        
//        createPropertyPopup = cmmActions.createProperty(driver, propertyBooleanYes, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "Yes").render();
//        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyBooleanYes);
//        createPropertyPopup.clickClose().render();
//        
//        createPropertyPopup = cmmActions.createProperty(driver, propertyBooleanNo, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "No").render();
//        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyBooleanNo);
//        createPropertyPopup.clickClose().render();
//        
//        createPropertyPopup = cmmActions.createProperty(driver, propertyBooleanT, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "T").render();
//        Assert.assertNotNull(createPropertyPopup, "Expected Error creating property: " + propertyBooleanT);
//        createPropertyPopup.clickClose().render();
//        
//        ManagePropertiesPage propListPage = cmmActions.viewProperties(driver, compositeAspectName).render();
        
        // Check the properties are created
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeIntText), "Invalid Property Created: " + compositeIntText);
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeIntMax), "Invalid Property Created: " + compositeIntMax); 
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeIntNegetive), "Invalid Property Created: " + compositeIntNegetive); 
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeIntFloat), "Invalid Property Created: " + compositeIntFloat); 
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeLongText), "Invalid Property Created: " + compositeLongText); 
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeFloatText), "Invalid Property Created: " + compositeFloatText); 
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDoubleText), "Invalid Property Created: " + compositeDoubleText); 
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDateText), "Invalid Property Created: " + compositeDateText);
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDateWrong), "Invalid Property Created: " + compositeDateWrong);
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDateNotISO), "Invalid Property Created: " + compositeDateNotISO);
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDatetimeText), "Invalid Property Created: " + compositeDatetimeText);
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDatetimeWrong), "Invalid Property Created: " + compositeDatetimeWrong);
        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeDatetimeNotISO), "Invalid Property Created: " + compositeDatetimeNotISO);
        
        // SHA-1141: Default Value control changed: Boolean can not be set but selected. Tests not needed anymore
//        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeBooleanNum), "Invalid Property Created: " + compositeBooleanNum);        
//        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeBooleanT), "Invalid Property Created: " + compositeBooleanT);
//        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeBooleanYes), "Invalid Property Created: " + compositeBooleanYes);
//        Assert.assertFalse(propListPage.isPropertyRowDisplayed(compositeBooleanNo), "Invalid Property Created: " + compositeBooleanNo);
    }
    
}
