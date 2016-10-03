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
 * Test Class to test Applying Dynamic Model types and aspects, editing properties using Share
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.alfresco.test.enums.CMISBinding;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class PropertyWithConstraintShareTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(PropertyWithConstraintShareTest.class);
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
    
    @AlfrescoTest(testlink="tobeaddeddel1")
    // SHA-961: Removal of Regex Match Required option
    @Test(groups = "EnterpriseOnly", priority=1, enabled = false)
    public void testRegexForTypeMatchRequiredNotSet() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeRegexLowerCase" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "LowerCase" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);

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
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);        
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Lower Case: Valid: Lower Case + Nos, Special Chars
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName, "fred1@alfresco.com");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
    
        // Lower Case: Valid: Mixed Case Characters
        properties.put(propertyName, "FREd");
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
    
        // Lower Case: Valid: Lower Case with trailing space
        properties.put(propertyName, "spaceattheend ");
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
        
        // Lower Case: Invalid: Only Lower Case Chars
        properties.put(propertyName, "alf");
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");   
    }
    
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testRegexForTypeMatchRequiredSet() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeRegexLowerCaseMR" + testName;

        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "LowerCaseMR" + testName;
        
        String detailsPagePropName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName); 
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Lower Case: Valid: Lower Case Characters Only
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName, "fred");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Edit Property succeeds
        properties = new HashMap<String, Object>();
        properties.put(modelName + propertyName, "fred");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
        
        // Get Edit Properties Page
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Lower Case: Valid: Lower Case Characters + Space
        properties = new HashMap<String, Object>();
        properties.put(propertyName, "fred ");
        siteActions.editNodeProperties(driver, true, properties).render();
        
        // MNT-16165, MNT-14235: Fix means expect a failure from repo
        // Remove Space at the end now: This should succeed
        properties = new HashMap<String, Object>();
        properties.put(propertyName, "fred");        
        siteActions.editNodePropertiesExpectError(driver, properties).render();
        
        // Expected Error: Edit Property fails
        properties = new HashMap<String, Object>();
        properties.put(detailsPagePropName, "fred");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint: Space at the end");  
        
        // Get Edit Properties Page
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Lower Case: Invalid
        properties = new HashMap<String, Object>();
        properties.put(propertyName, "Fred1@alfresco.com");
        siteActions.editNodePropertiesExpectError(driver, properties).render();
        
        // Expected Error: Edit Property fails
        siteActions.editNodeProperties(driver, false, properties).render();
        
        properties = new HashMap<String, Object>();
        properties.put(detailsPagePropName, "Fred1@alfresco.com");
        Assert.assertFalse(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint"); 
    }
    
    @AlfrescoTest(testlink="tobeaddeddel3")
    // SHA-961: Removal of Regex Match Required option
    @Test(groups = "EnterpriseOnly", priority=3, enabled = false)
    public void testRegexForAspectMatchRequiredNotSet() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspectNoMatch" + System.currentTimeMillis();
        String aspectNameOnShare = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "nomatch" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
                
        // Apply Default Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName); 
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectNameOnShare);
        siteActions.addAspects(driver, aspects);
        
        // Edit Aspect Properties: valid value: Mixed Characters
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, "anyText");
                
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName); 
        
        // Add Aspect: valid value: Pattern mismatch
        properties = new HashMap<>();
        properties.put(propertyName, "fred@alfresco1.com");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName); 
        
        // Add Aspect: valid value: Pattern alone      
        properties.put(propertyName, " @ alfresco.com");
    
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);   
        
        // Add Aspect: valid value: Pattern at the end
        properties.put(propertyName, "test@alfresco.com ");

        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName); 
        
        // Add Aspect: valid value: Pattern in the middle
        properties.put(propertyName, "test@alfresco.com1");

        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName); 
        
        // Add Aspect: valid value: Pattern at the end
        properties.put(propertyName, "test@alfresco.com");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();

        // TODO: Expected Error: Edit Property Fails
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");
    }
    
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testRegexForAspectMatchRequiredSet() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspectMatchReq" + System.currentTimeMillis();
        String shareAspectName = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "matchReq" + testName;
        
        String detailsPagePropName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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

        // Apply Default Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);
        
        // TODO: Edit Props: Aspect: valid value: Pattern Match
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, "test@alfresco.com");

        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        properties = new HashMap<>();
        properties.put(detailsPagePropName, "test@alfresco.com");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
        
        // Add Aspect: valid value: Pattern with spaces
        properties = new HashMap<>();
        properties.put(propertyName, " @alfresco.com");
        
        // Edit Props
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // TODO: Edit Property succeeds
        properties = new HashMap<>();
        properties.put(detailsPagePropName, "@alfresco.com");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint. Ref Node: " + docName);

        // Add Aspect: valid value: Pattern in the middle  
        properties = new HashMap<>();
        properties.put(propertyName, "test@alfresco.com1");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties).render();

        // Edit Property fails
        siteActions.editNodeProperties(driver, false, properties);

        properties = new HashMap<>();
        properties.put(detailsPagePropName, "test@alfresco.com1");
        Assert.assertFalse(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint"); 

        // Valid value: Pattern ends with spaces: Expect Failures like cmis
        properties = new HashMap<>();
        properties.put(propertyName, "endswithspace@alfresco.com ");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties).render();
        
        // MNT-16165, MNT-14235: Fix means expect a failure from repo - same result as cmis
        // Remove Space at the end now: This should succeed
        properties = new HashMap<String, Object>();
        properties.put(propertyName, "endswithspace@alfresco.com");        
        siteActions.editNodeProperties(driver, true, properties).render();
        
        properties = new HashMap<>();
        properties.put(detailsPagePropName, "endswithspace@alfresco.com");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint");        
        
        // Valid value: Pattern Mismatch: Expect Failure like cmis
        properties = new HashMap<>();
        properties.put(propertyName, "fred@alfresco1.com"); 
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodePropertiesExpectError(driver, properties).render();
        // Edit Property fails        
        siteActions.editNodeProperties(driver, false, properties);
        
        properties = new HashMap<>();
        properties.put(detailsPagePropName, "fred@alfresco1.com"); 
        Assert.assertFalse(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint");          
        
        // Add Aspect: valid value: Pattern Mismatch - Not available
        properties = new HashMap<>();
        properties.put(propertyName, "anyText");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodePropertiesExpectError(driver, properties).render();
        
        // Edit Property fails        
        siteActions.editNodeProperties(driver, false, properties);
        
        properties = new HashMap<>();
        properties.put(detailsPagePropName, "anyText");
        Assert.assertFalse(cmmActions.compareCMProperties(driver, properties), "Error: Node with Property values that do not adhere to the Constraint");          
    }
    
    @AlfrescoTest(testlink="tobeaddeddel5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testMinMaxValueForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeValue" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
                
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
  
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Get Edit Properties Page
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Text Prop for Int DataType
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName, docName);

        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint"); 
        
        // Value out of Range
        properties.put(propertyName, 20);
        
        editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");         
        
        // Correct Prop for Property
        properties.put(propertyName, 10);

        detailsPage = siteActions.editNodeProperties(driver, true, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(detailsPage, "Error: Editing properties for a Node with values that adhere to the Constraint");
    }
    
    @AlfrescoTest(testlink="tobeaddeddel6")
    @Test(groups = "EnterpriseOnly", priority=6)
    public void testMinMaxValueForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String shareAspectName = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);
        
        // Add Aspect: where Value adheres the the constraint
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, 10);
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Edit Property succeeds        
        properties = new HashMap<>();
        properties.put(modelName + propertyName, 10);
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT saved when value adheres to the Constraint Ref Node: " + docName);
        
        // Get Edit Properties Page
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Value does not match the datatype
        properties = new HashMap<>();
        properties.put(propertyName, "18.8");        

        // Edit Property fails        
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");
        
        // Add Aspect: where Value does not adhere the the constraint
        properties = new HashMap<>();
        properties.put(propertyName, 19);
        
        // Edit Property fails        
        editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");   
    }
    
    @AlfrescoTest(testlink="tobeaddeddel7")
    @Test(groups = "EnterpriseOnly", priority=7)
    public void testMinMaxLengthForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeLength" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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

        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
  
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage.changeType(shareTypeName).render();
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Edit Props: Value out of Range: Too long
        Map<String, Object> properties = new HashMap<String, Object>();
        // Value out of Range: Too short
        properties = new HashMap<String, Object>();
        properties.put(propertyName, "this");

        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");
        
        // Correct Prop for Property
        properties.put(propertyName, "Right");

        detailsPage = siteActions.editNodeProperties(driver, true, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(detailsPage, "Error: Editing Node with Property values that adhere to the Constraint");
        
        // Edit Props: Value out of Range: Too long
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        properties = new HashMap<String, Object>();
        properties.put(propertyName, docFile.getName());

        // TODO: Edit Property fails
        siteActions.editNodeProperties(driver, true, properties);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, docFile.getName());
        Assert.assertFalse(cmmActions.compareCMProperties(driver, properties), "Error: Node Saved with Property values that do not adhere to the Constraint");
    
    }
    
    @AlfrescoTest(testlink="tobeaddeddel8")
    @Test(groups = "EnterpriseOnly", priority=8)
    public void testMinMaxLengthForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String shareAspectName = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);
        
        // Add Aspect: where value adheres to the constraint
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, "12characters");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Edit Property succeeds
        properties = new HashMap<>();
        properties.put(modelName + ":" + propertyName, "12characters");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
        
        // Get Edit Properties Page
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Add Aspect: where value does not adhere to the constraint: Too short
        properties = new HashMap<>();
        properties.put(propertyName, "6chars");
        
        // Edit Property fails        
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");
        
        // Add Aspect: where value does not adhere to the constraint: Too long
        properties.put(propertyName, docFile.getName());
        
        // Edit Property fails
        siteActions.editNodeProperties(driver, true, properties);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, docFile.getName());
        
        properties = new HashMap<>();
        properties.put(modelName + ":" + propertyName, docFile.getName());
        Assert.assertFalse(cmmActions.compareCMProperties(driver, properties), "Error: Node Saved with Property values that do not adhere to the Constraint");
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=9)
    public void testListForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeList" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Edit Props: Value out of Range: Different
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName, "Spring");

        detailsPage = siteActions.editNodeProperties(driver, true, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(detailsPage, "Error: Editing properties for a Node with values that adhere to the Constraint"); 
                
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Correct Prop for Property
        properties.put(propertyName, "Mansoon");

        try
        {
            siteActions.editNodeProperties(driver, true, properties).render();
        }
        catch (NoSuchElementException nse)
        {
            // Expected Exception if List Value not found
        }
        
        // Edit Property fails
        EditDocumentPropertiesPage editPropPage = cmmActions.getSharePage(driver).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");         

        // Value out of Range: Different Case
        properties.put(propertyName, "spring");

        try
        {
            siteActions.editNodeProperties(driver, true, properties).render();
        }
        catch (NoSuchElementException nse)
        {
            // Expected Exception if List Value not found
        }

        // TODO: Edit Property fails
        editPropPage = cmmActions.getSharePage(driver).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint"); 
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=10)
    public void testListForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String shareAspectName = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);
        
        // Add Aspect: where value adheres to the constraint
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, "Winter");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Edit Property succeeds
        properties = new HashMap<>();
        properties.put(modelName + ":" + propertyName, "Winter");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);
        
        // Get Edit Properties Page
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        
        // Add Aspect: where value does not adhere to the constraint: Different Value
        properties.put(propertyName, "N/A");
        
        // Edit Property fails
        try
        {
            siteActions.editNodeProperties(driver, true, properties).render();
        }
        catch (PageException | NoSuchElementException e)
        {
            // Expected Exception if List Value not found
        }
        EditDocumentPropertiesPage editPropPage = cmmActions.getSharePage(driver).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");
        
        // Add Aspect: where value does not adhere to the constraint: Different Case
        properties.put(propertyName, "winter");

        // Edit Property fails
        try
        {
            siteActions.editNodeProperties(driver, true, properties).render();
        }
        catch (PageException | NoSuchElementException e)
        {
            // Expected Exception if List Value not found
        }
        
        editPropPage = cmmActions.getSharePage(driver).render();        
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint"); 
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=11, enabled=false)
    public void testJavaClassForType() throws Exception
    {
        String testName = getUniqueTestName();
        
        String typeName = "typeJClass" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "PropT" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        constraintDetails.setValue("org.alfresco.extension.classconstraint.example.InvoiceConstraint");

        cmmActions.createPropertyWithConstraint(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails).render();

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Edit Props: Correct Prop for Property
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName, "alfrescoAdmin");

        cmisApiClient.createDocument(bindingType, authDetails, authDetails[0], testDomain, testSiteName, docName, properties);              
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, testDomain, testSiteName, "", docName);
        Assert.assertNotNull(docNodeRef, "Node NOT created when value adheres to the Constraint. Ref Node: " + docName);
          
        // Invalid Value: Empty or values not allowed
        properties.put(propertyName, "_");

        EditDocumentPropertiesPage editPropPage = siteActions.editNodeProperties(driver, true, properties).render();

        // TODO: Edit Property fails
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint"); 
           
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=12, enabled=false)
    public void testJavaClassForAspect() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspect" + testName;
        String shareAspectName = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "PropA" + testName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
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
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);
        
        // Add Aspect: where value adheres to the constraint
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, "newuser");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Edit Property succeeds
        Assert.assertTrue(cmmActions.compareCMProperties(driver, properties), "Node NOT created when value adheres to the Constraint Ref Node: " + docName);  
        
        // Value does not adhere to the constraint:
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        properties.put(propertyName, "#");
        
        // Edit Property fails
        EditDocumentPropertiesPage editPropPage = siteActions.editNodeProperties(driver, true, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Node with Property values that do not adhere to the Constraint");        
    }
}
