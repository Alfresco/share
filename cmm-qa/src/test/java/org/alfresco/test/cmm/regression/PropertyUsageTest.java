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
 * Test Class to test Property Usage: Property with Various Data Types and use on Share 
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
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
public class PropertyUsageTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(PropertyUsageTest.class);
    
    private String testName;

    public DashBoardPage dashBoardpage;
    
    private String modelAdmin = "ALFRESCO_MODEL_ADMINISTRATORS";
    private String testUser;
    private String siteName;
    
    protected String modelName = "model";
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);  
        
        modelName = "model" + testName + System.currentTimeMillis();
        
        siteName = testName + System.currentTimeMillis();
        
       
        testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
        
        //Login as Admin to create a new user
        loginAs(driver, new String[] {username});
        
        //Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, testUser, testUser, testUser, testUser, DEFAULT_PASSWORD, modelAdmin );        
        
        //Logout as admin
        logout(driver);
        
        loginAs(driver, new String[] {testUser});
        
        siteActions.createSite(driver, siteName, siteName, "");
        logout(driver);
    }

    /**
     * User logs out after test is executed
     * 
     * @throws Exception
     */
    @AfterMethod(alwaysRun=true)
    public void quit() throws Exception
    {
        logout(driver);
    }
    
    /**
     * Create String Property and check the usage on Share
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testStringProperty() throws Exception
    {
        String testName = getUniqueTestName();
        
        String modelName = "model" + testName;
        String typeName = "type" + testName;
        String aspectName = "aspect" + testName;
        
        String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";
        String aspectNameOnShare = getShareAspectName(modelName, aspectName);
        
        String propertyOptT = "propT";
        String propertyNotEnforcedT = propertyOptT + "NE";
        
        String propertyOptA = "propA";
        String propertyNotEnforcedA = propertyOptA + "NE";

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositePropOptT = modelName + ":" + propertyOptT;
        String compositePropNotEnforcedT = modelName + ":" + propertyNotEnforcedT;
        
        String compositePropOptA = modelName + ":" + propertyOptA;
        String compositePropNotEnforcedA = modelName + ":" + propertyNotEnforcedA;
        
        String contentName = "content" + testName;
        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName);

        // Add Types:
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: Optional
        cmmActions.createProperty(driver, propertyOptT, "", "", DataType.Text, MandatoryClassifier.Optional, false, "N/A").render();                
  
        // Create String Property2 for Type: Mandatory Not Enforced
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNotEnforcedT, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "").render();                
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptT), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedT), "Unable to view Property: Mandatory Not enforced");
        
        // Apply Form Editor
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Add Aspect: 
        cmmActions.createAspect(driver, aspectName);
        
        cmmActions.viewProperties(driver, compositeAspectName);
        
        // Create String Property1 for Aspect: Optional
        cmmActions.createProperty(driver, propertyOptA, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
  
        // Create String Property2 for Aspect: Mandatory Not Enforced
        propListPage = cmmActions.createProperty(driver, propertyNotEnforcedA, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "A-0").render();                
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptA), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedA), "Unable to view Property: Mandatory Not enforced");
        
        // Apply Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // Create Node in Share
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName());
        
        // Change Type in Share
        siteActions.changeType(driver, shareTypeName);
        
        // Add Aspect in Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectNameOnShare);
        siteActions.addAspects(driver, aspects).render();
        
        // Check the property Values
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(modelName+":"+propertyOptT, "N/A");
        expectedProps.put(modelName+":"+propertyNotEnforcedT, "");
        expectedProps.put(modelName+":"+propertyOptA, "");
        expectedProps.put(modelName+":"+propertyNotEnforcedA, "A-0");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
        
        // Edit Properties: No Change: Save without specifying Mandatory Enforced Property
        siteActions.getEditPropertiesPage(driver, doc.getName());
        
        Map<String, Object> properties = new HashMap<String, Object>();
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Properties saved without entering Mandatory Not Enforced"); 
        
        // Edit Properties: Edit All
        properties = new HashMap<String, Object>();
        properties.put(propertyOptT, "AaBcCc");
        properties.put(propertyNotEnforcedT, "New Value");
        properties.put(propertyOptA, "123456");
        properties.put(propertyNotEnforcedA, "****");
        siteActions.editNodeProperties(driver, true, properties);
        
        expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(modelName+":"+propertyOptT, "AaBcCc");
        expectedProps.put(modelName+":"+propertyNotEnforcedT, "New Value");
        expectedProps.put(modelName+":"+propertyOptA, "123456");
        expectedProps.put(modelName+":"+propertyNotEnforcedA, "****");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected"); 
    }
    
    /**
     * Create Int Property and check the usage on Share
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testIntProperty() throws Exception
    {
            String testName = getUniqueTestName();
            
            String modelName = "model" + testName;
            String typeName = "type" + testName;
            String aspectName = "aspect" + testName;
            
            String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";
            String aspectNameOnShare = getShareAspectName(modelName, aspectName);
            
            String propertyOptT = "propT";
            String propertyNotEnforcedT = propertyOptT + "NE";
            
            String propertyOptA = "propA";
            String propertyNotEnforcedA = propertyOptA + "NE";

            String compositeTypeName = modelName + ":" + typeName;
            String compositeAspectName = modelName + ":" + aspectName;
            
            String compositePropOptT = modelName + ":" + propertyOptT;
            String compositePropNotEnforcedT = modelName + ":" + propertyNotEnforcedT;
            
            String compositePropOptA = modelName + ":" + propertyOptA;
            String compositePropNotEnforcedA = modelName + ":" + propertyNotEnforcedA;
            
            String contentName = "content" + testName;
            File doc = siteUtil.prepareFile(contentName);
            
            String maxInteger = "2147483647";

            loginAs(driver, new String[] {testUser});
            
            cmmActions.navigateToModelManagerPage(driver);

            // Create New Model
            cmmActions.createNewModel(driver, modelName).render();
                    
            // Activate Model
            cmmActions.setModelActive(driver, modelName, true);

            // View Types and Aspects: Model
            cmmActions.viewTypesAspectsForModel(driver, modelName);

            // Add Types:
            cmmActions.createType(driver, typeName).render();
                    
            cmmActions.viewProperties(driver, compositeTypeName);
            
            // Create String Property1 for Type: Optional
            cmmActions.createProperty(driver, propertyOptT, "", "", DataType.Int, MandatoryClassifier.Optional, false, "0").render();                
      
            // Create String Property2 for Type: Mandatory Not Enforced
            ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNotEnforcedT, "", "", DataType.Int, MandatoryClassifier.Mandatory, false, "").render();                
            
            // Check the properties are created
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptT), "Unable to view Property: Optional");                
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedT), "Unable to view Property: Mandatory Not enforced");
            
            // Apply Form Editor
            cmmActions.viewTypesAspectsForModel(driver, modelName);
            cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
            
            // Add Aspect: 
            cmmActions.createAspect(driver, aspectName);
            
            cmmActions.viewProperties(driver, compositeAspectName);
            
            // Create String Property1 for Aspect: Optional
            cmmActions.createProperty(driver, propertyOptA, "", "", DataType.Int, MandatoryClassifier.Optional, false, "").render();                
      
            // Create String Property2 for Aspect: Mandatory Not Enforced
            propListPage = cmmActions.createProperty(driver, propertyNotEnforcedA, "", "", DataType.Int, MandatoryClassifier.Mandatory, false, maxInteger).render();                
            
            // Check the properties are created
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptA), "Unable to view Property: Optional");                
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedA), "Unable to view Property: Mandatory Not enforced");
            
            // Apply Form Layout: Aspect
            cmmActions.viewTypesAspectsForModel(driver, modelName);
            cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
            
            // Create Node in Share
            siteActions.openSitesDocumentLibrary(driver, siteName);
            siteActions.uploadFile(driver, doc);
            siteActions.selectContent(driver, doc.getName());
            
            // Change Type in Share
            siteActions.changeType(driver, shareTypeName);
            
            // Add Aspect in Share
            List<String> aspects = new ArrayList<String>();
            aspects.add(aspectNameOnShare);
            siteActions.addAspects(driver, aspects);
            
            // Check the property Values
            Map<String, Object> expectedProps = new HashMap<String, Object>();
            expectedProps.put("Name", doc.getName());
            expectedProps.put(modelName+":"+propertyOptT, "0");
            expectedProps.put(modelName+":"+propertyNotEnforcedT, "");
            expectedProps.put(modelName+":"+propertyOptA, "");
            expectedProps.put(modelName+":"+propertyNotEnforcedA, maxInteger);
            
            // Compare Properties
            Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
            
            // Edit Properties: No Change: Save without specifying Mandatory Enforced Property
            siteActions.getEditPropertiesPage(driver, doc.getName());
            
            Map<String, Object> properties = new HashMap<String, Object>();
            EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
            Assert.assertNotNull(editPropPage, "Error: Properties saved without entering Mandatory Not Enforced"); 
            
            // Edit Properties: Edit All
            properties = new HashMap<String, Object>();
            properties.put(propertyOptT, "100");
            properties.put(propertyNotEnforcedT, "0");
            properties.put(propertyOptA, "");
            properties.put(propertyNotEnforcedA, "50000");
            siteActions.editNodeProperties(driver, true, properties);
            
            expectedProps = new HashMap<String, Object>();
            expectedProps.put("Name", doc.getName());
            expectedProps.put(modelName+":"+propertyOptT, "100");
            expectedProps.put(modelName+":"+propertyNotEnforcedT, "0");
            expectedProps.put(modelName+":"+propertyOptA, "");
            expectedProps.put(modelName+":"+propertyNotEnforcedA, "50000");
            Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected"); 
        }
    
    /**
     * Create Boolean Property and check the usage on Share
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testBooleanProperty() throws Exception
    {
            String testName = getUniqueTestName();
            
            String modelName = "model" + testName;
            String typeName = "type" + testName;
            String aspectName = "aspect" + testName;
            
            String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";
            String aspectNameOnShare = getShareAspectName(modelName, aspectName);
            
            String propertyOptT = "propT";
            String propertyNotEnforcedT = propertyOptT + "NE";
            
            String propertyOptA = "propA";
            String propertyNotEnforcedA = propertyOptA + "NE";

            String compositeTypeName = modelName + ":" + typeName;
            String compositeAspectName = modelName + ":" + aspectName;
            
            String compositePropOptT = modelName + ":" + propertyOptT;
            String compositePropNotEnforcedT = modelName + ":" + propertyNotEnforcedT;
            
            String compositePropOptA = modelName + ":" + propertyOptA;
            String compositePropNotEnforcedA = modelName + ":" + propertyNotEnforcedA;
            
            String contentName = "content" + testName;
            File doc = siteUtil.prepareFile(contentName);
            
            String booleanValue = "true";

            loginAs(driver, new String[] {testUser});
            
            cmmActions.navigateToModelManagerPage(driver);

            // Create New Model
            cmmActions.createNewModel(driver, modelName).render();
                    
            // Activate Model
            cmmActions.setModelActive(driver, modelName, true);

            // View Types and Aspects: Model
            cmmActions.viewTypesAspectsForModel(driver, modelName);

            // Add Types:
            cmmActions.createType(driver, typeName).render();
                    
            cmmActions.viewProperties(driver, compositeTypeName);
            
            // Create String Property1 for Type: Optional
            cmmActions.createProperty(driver, propertyOptT, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, booleanValue).render();                
      
            // Create String Property2 for Type: Mandatory Not Enforced
            ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNotEnforcedT, "", "", DataType.Boolean, MandatoryClassifier.Mandatory, false, "").render();                
            
            // Check the properties are created
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptT), "Unable to view Property: Optional");                
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedT), "Unable to view Property: Mandatory Not enforced");
            
            // Apply Form Editor
            cmmActions.viewTypesAspectsForModel(driver, modelName);
            cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
            
            // Add Aspect: 
            cmmActions.createAspect(driver, aspectName);
            
            cmmActions.viewProperties(driver, compositeAspectName);
            
            // Create String Property1 for Aspect: Optional
            cmmActions.createProperty(driver, propertyOptA, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "").render();                
      
            // Create String Property2 for Aspect: Mandatory Not Enforced
            propListPage = cmmActions.createProperty(driver, propertyNotEnforcedA, "", "", DataType.Boolean, MandatoryClassifier.Mandatory, false, booleanValue).render();                
            
            // Check the properties are created
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptA), "Unable to view Property: Optional");                
            Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedA), "Unable to view Property: Mandatory Not enforced");
            
            // Apply Form Layout: Aspect
            cmmActions.viewTypesAspectsForModel(driver, modelName);
            cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
            
            // Create Node in Share
            siteActions.openSitesDocumentLibrary(driver, siteName);
            siteActions.uploadFile(driver, doc);
            siteActions.selectContent(driver, doc.getName());
            
            // Change Type in Share
            siteActions.changeType(driver, shareTypeName);
            
            // Add Aspect in Share
            List<String> aspects = new ArrayList<String>();
            aspects.add(aspectNameOnShare);
            siteActions.addAspects(driver, aspects);
            
            // Check the property Values
            Map<String, Object> expectedProps = new HashMap<String, Object>();
            expectedProps.put("Name", doc.getName());
            expectedProps.put(modelName+":"+propertyOptT, "Yes");
            expectedProps.put(modelName+":"+propertyNotEnforcedT, "No");
            expectedProps.put(modelName+":"+propertyOptA, "No");
            expectedProps.put(modelName+":"+propertyNotEnforcedA, "Yes");
            
            // Compare Properties
            Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
            
            // Edit Properties: No Change: Save without specifying Mandatory Enforced Property
            siteActions.getEditPropertiesPage(driver, doc.getName());
            
            Map<String, Object> properties = new HashMap<String, Object>();
            // Boolean Properties appear as checkbox, which are set / unset based on value. So value is always specified
            DetailsPage detailsPage = siteActions.editNodeProperties(driver, true, properties).render();
            Assert.assertNotNull(detailsPage, "Error: Properties saved without entering Mandatory Not Enforced"); 
            
            siteActions.getEditPropertiesPage(driver, doc.getName());
            
            // Edit Properties: Edit All
            properties = new HashMap<String, Object>();
            properties.put(propertyOptT, "");
            properties.put(propertyNotEnforcedT, "TRUE");
            properties.put(propertyOptA, "false");
            properties.put(propertyNotEnforcedA, booleanValue);
            
            siteActions.editNodeProperties(driver, true, properties);
            
            expectedProps = new HashMap<String, Object>();
            expectedProps.put("Name", doc.getName());
            expectedProps.put(modelName+":"+propertyOptT, "No");
            expectedProps.put(modelName+":"+propertyNotEnforcedT, "Yes");
            expectedProps.put(modelName+":"+propertyOptA, "No");
            expectedProps.put(modelName+":"+propertyNotEnforcedA, "Yes");
            Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected"); 
        }
    
    /**
     * Create Date Property and check the usage on Share
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testDateProperty() throws Exception
    {
        String testName = getUniqueTestName();
        
        String modelName = "model" + testName;
        String typeName = "type" + testName;
        String aspectName = "aspect" + testName;
        
        String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";
        String aspectNameOnShare = getShareAspectName(modelName, aspectName);
        
        String propertyOptT = "propT";
        String propertyNotEnforcedT = propertyOptT + "NE";
        
        String propertyOptA = "propA";
        String propertyNotEnforcedA = propertyOptA + "NE";

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositePropOptT = modelName + ":" + propertyOptT;
        String compositePropNotEnforcedT = modelName + ":" + propertyNotEnforcedT;
        
        String compositePropOptA = modelName + ":" + propertyOptA;
        String compositePropNotEnforcedA = modelName + ":" + propertyNotEnforcedA;
        
        String contentName = "content" + testName;
        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName);

        // Add Types:
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: Optional
        // SHA-1253: Amended the date format in line with the new UI / Aikau changes (Original: "2016")
        cmmActions.createProperty(driver, propertyOptT, "", "", DataType.Date, MandatoryClassifier.Optional, false, dateEntry).render();                
  
        // Create String Property2 for Type: Mandatory Not Enforced
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNotEnforcedT, "", "", DataType.Date, MandatoryClassifier.Mandatory, false, "").render();                
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptT), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedT), "Unable to view Property: Mandatory Not enforced");
        
        // Apply Form Editor
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Add Aspect: 
        cmmActions.createAspect(driver, aspectName);
        
        cmmActions.viewProperties(driver, compositeAspectName);
        
        // Create String Property1 for Aspect: Optional
        cmmActions.createProperty(driver, propertyOptA, "", "", DataType.Date, MandatoryClassifier.Optional, false, "").render();                
  
        // Create String Property2 for Aspect: Mandatory Not Enforced
        // SHA-1253: Amended the date format in line with the new UI / Aikau changes (Original: "2015-09-15")
        propListPage = cmmActions.createProperty(driver, propertyNotEnforcedA, "", "", DataType.Date, MandatoryClassifier.Mandatory, false, dateEntry).render();                
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptA), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedA), "Unable to view Property: Mandatory Not enforced");
        
        // Apply Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // Create Node in Share
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName());
        
        // Change Type in Share
        siteActions.changeType(driver, shareTypeName);
        
        // Add Aspect in Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectNameOnShare);
        siteActions.addAspects(driver, aspects);
        
        // Check the property Values
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        // SHA-1253: Amended the date format in line with the new UI / Aikau changes (Original: "2016", "", "", "2015-09-15")
        expectedProps.put(modelName+":"+propertyOptT, dateEntry);
        expectedProps.put(modelName+":"+propertyNotEnforcedT, "");
        expectedProps.put(modelName+":"+propertyOptA, "");
        expectedProps.put(modelName+":"+propertyNotEnforcedA, dateEntry);
        
        // Compare Properties: TODO: Date Specific Changes? Format displayed: "Wed 5 Aug 2015 13:35:18" or "Wed 5 Aug 2015"
        // Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
        
        // Edit Properties: No Change: Save without specifying Mandatory Enforced Property
        siteActions.getEditPropertiesPage(driver, doc.getName());
        
        Map<String, Object> properties = new HashMap<String, Object>();
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Properties saved without entering Mandatory Not Enforced"); 
        
        // Edit Properties: Edit All
        properties = new HashMap<String, Object>();
        properties.put(propertyOptT, dateEntry);
        properties.put(propertyNotEnforcedT, dateEntry);
        properties.put(propertyOptA, "");
        properties.put(propertyNotEnforcedA, dateEntry);
        siteActions.editNodeProperties(driver, true, properties);
        
        // Check the Properties on DetailsPage: TODO: Date Specific Changes?
        expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(modelName+":"+propertyOptT, dateEntry);
        expectedProps.put(modelName+":"+propertyNotEnforcedT, dateEntry);
        expectedProps.put(modelName+":"+propertyOptA, dateEntry);
        expectedProps.put(modelName+":"+propertyNotEnforcedA, dateEntry);
        
        // Compare Properties: TODO: Date Specific Changes?
        // Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected"); 
    }
    
    /**
     * Create String Property and check the usage on Share
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel5")
    @Test(groups = "EnterpriseOnly", priority=5, enabled=false)
    public void testPropertyAppearsAfterChangeInPrefix() throws Exception
    {
        String testName = getUniqueTestName();
        
        String modelName = "model" + testName;
        String typeName = "type" + testName;
        String aspectName = "aspect" + testName;
        
        String newPrefixName = "pr" + modelName;
        
        String shareTypeName = typeName + " (" + newPrefixName + ":" + typeName + ")";
        String aspectNameOnShare = getShareAspectName(newPrefixName, aspectName);
        
        String propertyOptT = "propT";
        String propertyNotEnforcedT = propertyOptT + "NE";
        
        String propertyOptA = "propA";
        String propertyNotEnforcedA = propertyOptA + "NE";

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String compositePropOptT = modelName + ":" + propertyOptT;
        String compositePropNotEnforcedT = modelName + ":" + propertyNotEnforcedT;
        
        String compositePropOptA = modelName + ":" + propertyOptA;
        String compositePropNotEnforcedA = modelName + ":" + propertyNotEnforcedA;
        
        String contentName = "content" + testName;
        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName);

        // Add Types:
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: Optional
        cmmActions.createProperty(driver, propertyOptT, "", "", DataType.Text, MandatoryClassifier.Optional, false, "N/A").render();                
  
        // Create String Property2 for Type: Mandatory Not Enforced
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNotEnforcedT, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "").render();                
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptT), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedT), "Unable to view Property: Mandatory Not enforced");
        
        // Apply Form Editor
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Add Aspect: 
        cmmActions.createAspect(driver, aspectName);
        
        cmmActions.viewProperties(driver, compositeAspectName);
        
        // Create String Property1 for Aspect: Optional
        cmmActions.createProperty(driver, propertyOptA, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
  
        // Create String Property2 for Aspect: Mandatory Not Enforced
        propListPage = cmmActions.createProperty(driver, propertyNotEnforcedA, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "A-0").render();                
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptA), "Unable to view Property: Optional");                
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNotEnforcedA), "Unable to view Property: Mandatory Not enforced");
        
        // Apply Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        cmmActions.navigateToModelManagerPage(driver);
        
        // Deactivate Model
        cmmActions.setModelActive(driver, modelName, false);
        
        // Amend Model Prefix
        cmmActions.editModel(driver, modelName, modelName, newPrefixName);

        // Activate Model
        cmmActions.setModelActive(driver, modelName, true);
        
        // Create Node in Share
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName());

        // Change Type in Share
        siteActions.changeType(driver, shareTypeName);
        
        // Add Aspect in Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectNameOnShare);
        siteActions.addAspects(driver, aspects).render();
        
        // Check the property Values
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(modelName+":"+propertyOptT, "N/A");
        expectedProps.put(modelName+":"+propertyNotEnforcedT, "");
        expectedProps.put(modelName+":"+propertyOptA, "");
        expectedProps.put(modelName+":"+propertyNotEnforcedA, "A-0");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
        
        // Edit Properties: No Change: Save without specifying Mandatory Enforced Property
        siteActions.getEditPropertiesPage(driver, doc.getName());
        
        Map<String, Object> properties = new HashMap<String, Object>();
        EditDocumentPropertiesPage editPropPage = siteActions.editNodePropertiesExpectError(driver, properties).render();
        Assert.assertNotNull(editPropPage, "Error: Properties saved without entering Mandatory Not Enforced"); 
        
        // Edit Properties: Edit All
        properties = new HashMap<String, Object>();
        properties.put(propertyOptT, "AaBcCc");
        properties.put(propertyNotEnforcedT, "New Value");
        properties.put(propertyOptA, "123456");
        properties.put(propertyNotEnforcedA, "****");
        siteActions.editNodeProperties(driver, true, properties);
        
        expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(modelName+":"+propertyOptT, "AaBcCc");
        expectedProps.put(modelName+":"+propertyNotEnforcedT, "New Value");
        expectedProps.put(modelName+":"+propertyOptA, "123456");
        expectedProps.put(modelName+":"+propertyNotEnforcedA, "****");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected"); 
    }

}
