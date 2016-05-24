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
 * Test Class to test Applying Dynamic Model types and aspects using Cmis
 * 
 * @author mbhave
 */

import java.util.HashMap;
import java.util.Map;

import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.alfresco.test.enums.CMISBinding;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class PropertyWithConstraintCMISTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(PropertyWithConstraintCMISTest.class);
    
    private String[] authDetails = new String[] {"admin", "admin"};
    
    private String testUser;
    
    private String testDomain = "";

    protected String testSiteName = "swsdp";

    protected CMISBinding bindingType = CMISBinding.BROWSER11;
    
    protected String testName = getTestName();    

    protected String modelName;
    
    @BeforeClass(alwaysRun = true)
    public void setupTest() throws Exception
    {
        super.setupCmis();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);   
       
        setupData();
    }

    private void setupData() throws Exception
    {
        String testName = getUniqueTestName(); 
        
        modelName = "model" + testName;

        testUser = username;  
        
        //Login as RepoAdmin
        loginAs(driver, new String[] {username});
        
        // testSiteName = getSiteName(testName);        
        //siteActions.createSite(driver, testSiteName, testSiteName, "public");
       
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

    }
    
    /**
     * User logs out after test is executed
     * 
     * @throws Exception
     */
    @AfterClass
    public void quit() throws Exception
    {
        logout(driver);
    }
    
    @AlfrescoTest(testlink="tobeaddeddel1")
    // SHA-961: Removal of Regex Match Required option    
    @Test(groups = "EnterpriseOnly", priority=1, enabled = false)
    public void testRegexForTypeMatchRequiredFalse() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeRegexLowerCase" + testName;
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "LowerCase" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        String docNameMixedCase = docName + "mixedcase";
        String docNameInvalidValues = docName + "invalid";
        String docNameLowerCaseWithSpace = docName + "endspace";
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: Regex: Match Required
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue("[a-z]+");
        constraintDetails.setMatchRequired(false);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();       
        
        // Add Node of Type: Value ADHERES to the constraint: Match Required = false
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        
        // Lower Case: Valid: Lower Case + Nos, Special Chars
        properties.put(PropertyIds.NAME, docName);
        properties.put(compositePropertyName, "fred1@alfresco.com");

        // Document is created
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
    
        // Lower Case: Valid: Mixed Case Characters
        properties.put(PropertyIds.NAME, docNameMixedCase);
        properties.put(compositePropertyName, "FREd");

        // Document is created
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docNameMixedCase, properties);
        
        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docNameMixedCase);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint Ref Node: " + docNameMixedCase);
    
        // Lower Case: Valid: Lower Case with trailing space
        properties.put(PropertyIds.NAME, docNameLowerCaseWithSpace);
        properties.put(compositePropertyName, "spaceattheend ");

        // Document is created
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docNameLowerCaseWithSpace, properties);
        
        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docNameLowerCaseWithSpace);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint Ref Node: " + docNameLowerCaseWithSpace);
        
        // Lower Case: Invalid: Only Lower Case Chars
        properties.put(PropertyIds.NAME, docNameInvalidValues);
        properties.put(compositePropertyName, "alf");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docNameInvalidValues, properties);
            Assert.fail("Node created when value adheres to the Constraint Ref Node: " + docNameInvalidValues);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName+ "ALF", e);
        }        
    }
    
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testRegexForTypeMatchRequiredTrue() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeRegexLowerCaseMR" + testName;
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "LowerCaseMR" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        String docNameInvalidValues = docName + "invalid";
        String docNameWithSpace = docName + "endspace";
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: Regex: Match Required
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue("[a-z]+");
        constraintDetails.setMatchRequired(true);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Add Node of Type: Value ADHERES to the constraint: Match Required = true
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        
        // Lower Case: Valid: Lower Case Characters Only
        properties.put(PropertyIds.NAME, docName);
        properties.put(compositePropertyName, "fred");

        // Document is created
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
    
        // Lower Case: Valid: Lower Case Characters + Space
        properties.put(PropertyIds.NAME, docNameWithSpace);
        properties.put(compositePropertyName, "fred ");

        // Expected Error
        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docNameWithSpace, properties);
            Assert.fail("Node created when value adheres to the Constraint Ref Node: " + docNameWithSpace);
        }
        catch (CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docNameWithSpace, e);
        }
        
        // Lower Case: Invalid
        properties.put(PropertyIds.NAME, docNameInvalidValues);
        properties.put(compositePropertyName, "Fred1@alfresco.com");

        // Expected Error
        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docNameInvalidValues, properties);
            Assert.fail("Node created when value adheres to the Constraint Ref Node: " + docNameInvalidValues);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName+ "ALF", e);
        }
    }
    
    @AlfrescoTest(testlink="tobeaddeddel3")
    // SHA-961: Removal of Regex Match Required option
    @Test(groups = "EnterpriseOnly", priority=3, enabled = false)
    public void testRegexForAspectMatchRequiredFalse() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspectNoMatch" + System.currentTimeMillis();
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "nomatch" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: Regex: Match Required
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue(".*@alfresco.com");
        constraintDetails.setMatchRequired(false);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Create Node
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, docName);
        
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Error creating Node cmis:document. Ref Node: " + docName);
        
        // Add Aspect: valid value: Mixed Characters
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "anyText");

        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  
        
        // Add Aspect: valid value: Pattern mismatch
        aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "fred@alfresco1.com");
        
        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
        
        // Add Aspect: valid value: Pattern alone      
        aspectProps.put(compositePropertyName, " @ alfresco.com");
    
        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  
        
        // Add Aspect: valid value: Pattern at the end
        aspectProps.put(compositePropertyName, "test@alfresco.com ");

        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
        
        // Add Aspect: valid value: Pattern in the middle
        aspectProps.put(compositePropertyName, "test@alfresco.com1");

        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
        
        // Add Aspect: valid value: Pattern at the end
        aspectProps.put(compositePropertyName, "test@alfresco.com");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node", e);
        }
    }
    
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testRegexForAspectMatchRequiredTrue() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspectMatchReq" + System.currentTimeMillis();
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "matchReq" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: Regex: Match Required
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue(".*@alfresco.com");
        constraintDetails.setMatchRequired(true);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Create Node
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, docName);
        
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Error creating Node cmis:document. Ref Node: " + docName);
        
        // Add Aspect: valid value: Pattern Match
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "test@alfresco.com");

        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  
        
        // Add Aspect: valid value: Pattern with spaces
        aspectProps.put(compositePropertyName, " @alfresco.com");

        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
        
        // Add Aspect: valid value: Pattern in the middle     
        aspectProps.put(compositePropertyName, "test@alfresco.com1");

        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch (CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node", e);
        }

        // Add Aspect: valid value: Pattern ends with spaces
        aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "endswithspace@alfresco.com ");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch (CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node", e);
        }
        
        // Add Aspect: valid value: Pattern Mismatch
        try
        {
            aspectProps.put(compositePropertyName, "fred@alfresco1.com");

            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch (CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node", e);
        }
        
        // Add Aspect: valid value: Pattern Mismatch - Not available
        aspectProps.put(compositePropertyName, "anyText");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node", e);
        }        
    }
    
    @AlfrescoTest(testlink="tobeaddeddel5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testMinMaxValueForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeValue" + testName;
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: MINMAXVALUE
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(18);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Int, MandatoryClassifier.Optional, false, "18",
                constraintDetails).render();
  
        // Text Prop for Int DataType
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName);
        properties.put(compositePropertyName, docName);

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(IllegalArgumentException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName, e);
        }
        
        // Value out of Range
        properties.put(compositePropertyName, 20);

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName, e);
        }
        
        // Correct Prop for Property
        properties.put(compositePropertyName, 10);

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);            
        }
        catch(Exception e)
        {
            Assert.fail("Node NOT created when value adheres to the Constraint. Ref Node: " + docName, e);
        }
            
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint. Ref Node: " + docName);
    }
    
    @AlfrescoTest(testlink="tobeaddeddel6")
    @Test(groups = "EnterpriseOnly", priority=6)
    public void testMinMaxValueForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: MINMAXVALUE
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails.setMinValue(0);
        constraintDetails.setMaxValue(18);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Int, MandatoryClassifier.Optional, false, "18",
                constraintDetails).render();
        
        // Create a node
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, docName);
        
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        
        // Add Aspect: where Value adheres the the constraint
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, 10);
        
        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  

        // Add Aspect: where Value does not match the datatype
        aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "18.8");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(Exception e) //CmisInvalidArgumentException
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
        
        // Add Aspect: where Value does not adhere the the constraint
        aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, 19);
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
       
    }
    
    @AlfrescoTest(testlink="tobeaddeddel7")
    @Test(groups = "EnterpriseOnly", priority=7)
    public void testMinMaxLengthForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeLength" + testName;
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: MINMAXLENGTH
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails.setMinValue(5);
        constraintDetails.setMaxValue(8);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
  
        // Value out of Range: Too long
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName+"long");
        properties.put(compositePropertyName, docName);

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName, e);
        }
        
        // Value out of Range: Too short
        properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName+"short");
        properties.put(compositePropertyName, "this");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName, e);
        }
        
        // Correct Prop for Property
        properties.put(PropertyIds.NAME, docName);
        properties.put(compositePropertyName, "Right");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);            
        }
        catch(Exception e)
        {
            Assert.fail("Node NOT created when value adheres to the Constraint. Ref Node: " + docName, e);
        }
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint. Ref Node: " + docName);
        
    }
    
    @AlfrescoTest(testlink="tobeaddeddel8")
    @Test(groups = "EnterpriseOnly", priority=8)
    public void testMinMaxLengthForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: MINMAXLENGTH
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails.setMinValue(8);
        constraintDetails.setMaxValue(12);

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Create a Node
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, docName);
        
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        
        // Add Aspect: where value adheres to the constraint
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "12characters");
        
        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  
        
        // Add Aspect: where value does not adhere to the constraint: Too short
        aspectProps.put(compositePropertyName, "6chars");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
        
        // Add Aspect: where value does not adhere to the constraint: Too long
        aspectProps.put(compositePropertyName, docName);
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=9)
    public void testListForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeList" + testName;
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: List
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.LIST);
        constraintDetails.setValue("Spring\nSummer\nAutumn\nWinter");

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
  
        // Value out of Range: Different
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName);
        properties.put(compositePropertyName, "Mansoon");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName, e);
        }
        
        // Correct Prop for Property
        properties.put(compositePropertyName, "Spring");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);           
        }
        catch(Exception e)
        {
            Assert.fail("Node NOT created when value adheres to the Constraint. Ref Node: " + docName, e);
        }    
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint. Ref Node: " + docName);        
        
        // Value out of Range: Different Case
        properties.put(PropertyIds.NAME, docName+"diffcasecase");
        properties.put(compositePropertyName, "spring");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName + "diffcasecase", properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName+"diffcasecase");
        }
        catch (CmisConstraintException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName+"diffcasecase", e);
        }
        
//        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName+"diffcasecase");
//        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint. Ref Node: " + docName+"diffcasecase");        
        
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=10)
    public void testListForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: LIST
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.LIST);
        constraintDetails.setValue("Spring\nSummer\nAutumn\nWinter");

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Create a Node
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, docName);
        
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        
        // Add Aspect: where value adheres to the constraint
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "Winter");
        
        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  
        
        // Add Aspect: where value does not adhere to the constraint: Different Value
        aspectProps.put(compositePropertyName, "N/A");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
        
        // Add Aspect: where value does not adhere to the constraint: Different Case
        aspectProps.put(compositePropertyName, "winter");

        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch (CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=11, enabled = false)
    public void testJavaClassForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeJClass" + testName;
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: JavaClass
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.JAVACLASS);
        // constraintDetails.setValue("org.alfresco.repo.dictionary.constraint.UserNameConstraint");
        constraintDetails.setValue("org.alfresco.extension.classconstraint.example.InvoiceConstraint");

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Correct Prop for Property
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName);
        properties.put(compositePropertyName, "alfrescoAdmin");

        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);              
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint. Ref Node: " + docName);
          
        // Invalid Value: Empty
        properties.put(PropertyIds.NAME, docName+"invalid");
        properties.put(compositePropertyName, "#");

        try
        {
            cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName+"invalid", properties);
            Assert.fail("Node created when value does not adhere to the Constraint. Ref Node: " + docName+"invalid");
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while creating node. Ref Node: " + docName+"invalid", e);
        }        
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=12, enabled=false)
    public void testJavaClassForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        String compositePropertyName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: JAVACLASS
        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.JAVACLASS);
        constraintDetails.setValue("org.alfresco.extension.classconstraint.example.InvoiceConstraint");

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();
        
        // Create a Node
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, docName);
        
        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        
        // Add Aspect: where value adheres to the constraint
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(compositePropertyName, "newuser");
        
        cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);  
        
        // Add Aspect: where value does not adhere to the constraint:
        aspectProps.put(compositePropertyName, "_");
        
        try
        {
            cmisApiClient.addAspect(bindingType, authDetails, testDomain, docNodeRef, cmisAspectName, aspectProps);
            Assert.fail("Aspect Added when value does not adhere to the Constraint. Ref Node: " + docName);
        }
        catch(CmisConstraintException e)
        {
            logger.info("Expected Exception while adding aspect to a node. Ref Node: " + docName, e);
        }
    }
}
