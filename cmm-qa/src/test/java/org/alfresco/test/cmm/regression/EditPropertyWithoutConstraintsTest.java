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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


@Listeners(FailedTestListener.class)
/**
 * Test Class to test Edit Property Without Constraints
 * 
 * @author Charu
 */
public class EditPropertyWithoutConstraintsTest extends AbstractCMMQATest
{
        private static final Log logger = LogFactory.getLog(EditPropertyWithoutConstraintsTest.class);
        
        private String testName;        
              
        private String typeName = "type"+ System.currentTimeMillis();
        private String aspectName = "aspect"+ System.currentTimeMillis();     
               
        private String propertyNameT = "propertyt";
        private String propertyNameA = "propertya";       
       
        public DashBoardPage dashBoardpage;
        
        public ManageTypesAndAspectsPage mtap;
        
        public EditPropertyPopUp epp;
        
        public ManagePropertiesPage mpp;
        
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
        @AfterClass
        public void quit() throws Exception
        {
            logout(driver);
        }   
               
        
        /**<ul>
         * <li>End to end test to edit property for a model type without constraint</li>
         * <li>Create Model1</li>
         * <li>Create type1 for model1</li>
         * <li>Create String Property1  for Type: No Default Set</li>
         * <li>Edit property for draft model with valid default value</li>
         * <li>Activate model1</li>
         * <li>Edit active model property with valid default value</li>
         * <li>Verify property details are modified</li>
         * <li>Apply default layout For type</li>
         * <li>Create site, content and select content</li>
         * <li>Change type to any node in share</li>
         * <li>Verify Properties are updated correctly</li>
         * <li>Delete node in share</li>
         * <li>Delete Node Permanently from Trashcan</li>
         * <li>Deactivate model1</li>
         * <li>Edit property of deactivated model1 with valid default value</li>
         * <li>Verify property details are updated in the property list</li>      
         *</ul>
         * @throws Exception 
         */
        // This test covers the end to end scenario for edit type property without out constraint
        
        @AlfrescoTest(testlink="tobeaddedpwc1")
        @Test(groups = "EnterpriseOnly", priority=1)
        public void testEPropEndTEndT() throws Exception
        {                                 
            String name = "name"+System.currentTimeMillis();            
            String siteName = "site"+System.currentTimeMillis();
            String modelName = "model" + System.currentTimeMillis();
            String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";
            String compositeTypeName = modelName + ":" + typeName;
                
            String compositePropNameT = modelName + ":" + propertyNameT;
            
            
            ContentDetails contentDetails =new ContentDetails();
            contentDetails.setName(name);            
                        
            loginAs(driver, new String[] {testUser});            
                        
            cmmActions.navigateToModelManagerPage(driver);
           
            // Create New Model
            cmmActions.createNewModel(driver, modelName).render();               
              
            // View Types and Aspects: Model
            cmmActions.viewTypesAspectsForModel(driver, modelName).render();

            // Add Types: Model
            cmmActions.createType(driver, typeName).render();              
          
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
            cmmActions.viewProperties(driver, compositeTypeName);
            
            // Create String Property1 for draft model Type: without  Default value
            cmmActions.createProperty(driver, propertyNameT+"1", "", "", DataType.Int, MandatoryClassifier.Optional, false, "").render();            
            
            //Edit property for draft model with valid default value
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            mpp = cmmActions.editProperty(driver, compositeTypeName, compositePropNameT+"1", "txt", "txt", DataType.Text, MandatoryClassifier.Mandatory,true,"text").render();
                       
            PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameT+"1");
            Assert.assertNotNull(propRow1);
            Assert.assertEquals(propRow1.getDisplayLabel(),"txt", "Display Label displayed correctly");
            Assert.assertEquals(propRow1.getDatatype(),datatypet,"Data Type updated correctly");
            Assert.assertEquals(propRow1.getDefaultValue(),"text", "Default value incorrect for property: " + compositePropNameT+"1");
            Assert.assertEquals(propRow1.getMandatory(),mandatory,"Mandatory displayed correctly");
            Assert.assertEquals(propRow1.getMultiValue(),"Yes","Mandatory displayed correctly");
                                                        
            //Activate model1
            //Edit property with valid default value
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,true);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            mpp = cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"1", "Atit12", "Ade12", "rt^$^3467").render();
            
            //Verify property details are modified
            PropertyRow propRow2 = mpp.getPropertyRowByName(compositePropNameT+"1");
            Assert.assertNotNull(propRow2);            
            Assert.assertEquals(propRow2.getDisplayLabel(),"Atit12", "Display Label displayed correctly");
            Assert.assertEquals(propRow2.getDatatype(),datatypet,"Data Type updated correctly");
            Assert.assertEquals(propRow2.getDefaultValue(),"rt^$^3467", "Default value incorrect for property: " + compositePropNameT+"1");
            Assert.assertEquals(propRow2.getMandatory(),mandatory,"Mandatory displayed correctly");
            Assert.assertEquals(propRow2.getMultiValue(),"Yes","Mandatory displayed correctly");
            
            //Apply default layout For type
            cmmActions.navigateToModelManagerPage(driver);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
            cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);           
            
            //Create site, content and select content
            siteActions.createSite(driver,siteName, "", "");
            siteActions.openSitesDocumentLibrary(driver, siteName);
            siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
            DetailsPage detailsPage = siteActions.selectContent(driver, name).render();

            //Apply type to any node in share 
            detailsPage.changeType(shareTypeName).render();
            
            // Check Properties
            Map<String, Object> expectedProps = new HashMap<String, Object>();
            expectedProps.put("Atit12", "rt^$^3467");
            
            //Compare properties in share
            Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");                
                                       
            //Delete node in share
            siteActions.openSiteDashboard(driver, siteName);
            siteActions.openDocumentLibrary(driver);
            siteActions.deleteContentInDocLib(driver,name);           
                     
            // Delete Node Permanently
            userActions.navigateToTrashCan(driver);
            userActions.deleteFromTrashCan(driver, TrashCanValues.FILE,name, "documentLibrary");
                                          
            //Deactivate model1
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,false);
            
            //Edit property of deactivated model1 with valid default value            
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            mpp = cmmActions.editProperty(driver, compositeTypeName, compositePropNameT+"1", "boolean", "boolean", DataType.Boolean, MandatoryClassifier.Mandatory,true,"True").render();
            
            //Verify property details are updated
            PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameT+"1");
            Assert.assertNotNull(propRow6);            
            Assert.assertEquals(propRow6.getDisplayLabel(),"boolean", "Display Label displayed correctly");
            Assert.assertEquals(propRow6.getDatatype(),datatypeb,"Data Type updated correctly");
            Assert.assertEquals(propRow6.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameT+"1");
            Assert.assertEquals(propRow6.getMandatory(),mandatory,"Mandatory displayed correctly");
            Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");    
                        
        }
        
        /**<ul>
         * <li>End to end test to edit property for a model aspect without constraint</li>
         * <li>Create Model1</li>
         * <li>Create aspect1 for model1</li>
         * <li>Create String Property1 for aspect: No Default Set</li>
         * <li>Edit property for draft model with valid default value</li>
         * <li>Activate model1</li>
         * <li>Edit active model property with valid default value</li>
         * <li>Verify property details are modified</li>
         * <li>Apply default layout For aspect</li>
         * <li>Create site, content and select content</li>
         * <li>Change aspect to any node in share</li>
         * <li>Verify Properties are updated correctly</li>
         * <li>Delete node in share</li>
         * <li>Delete Node Permanently from Trashcan</li>
         * <li>Deactivate model1</li>
         * <li>Edit property of deactivated model1 with valid default value</li>
         * <li>Verify property details are updated in the property list</li>      
         *</ul>
         * @throws Exception 
         */
        @AlfrescoTest(testlink="tobeadded")
        @Test(groups = "EnterpriseOnly", priority=2)
        public void testEPropEndTEndAsp() throws Exception
        {
            
            String name = "name"+System.currentTimeMillis();
            String siteName = "site"+System.currentTimeMillis();
            String modelName = "model" + System.currentTimeMillis();
          
            String compositeAspectName = modelName + ":" + aspectName;            
          
            String compositePropNameA = modelName + ":" + propertyNameA;
            
            String shareAspectName = getShareAspectName(modelName, aspectName);
                        
            ContentDetails contentDetails =new ContentDetails();
            contentDetails.setName(name);     
                  
            loginAs(driver, new String[] {testUser});            
          
            cmmActions.navigateToModelManagerPage(driver);

            // Create New Model
            cmmActions.createNewModel(driver, modelName).render();               
              
            // View Types and Aspects: Model
            cmmActions.viewTypesAspectsForModel(driver, modelName).render();
           
            // Add Types: Model
            cmmActions.createAspect(driver, aspectName).render();      
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
            cmmActions.viewProperties(driver, compositeAspectName);
            
            // Create String Property1 for Type: No Default Set
            cmmActions.createProperty(driver, propertyNameA+"2", "", "", DataType.Text, MandatoryClassifier.Optional, false, "create").render();            
            
            //Edit property for draft model with valid default value
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            mpp = cmmActions.editProperty(driver, compositeAspectName, compositePropNameA+"2", "date", "date", DataType.Date, MandatoryClassifier.Mandatory,true,dateEntry).render();
                       
            PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameA+"2");
            Assert.assertNotNull(propRow1);
            Assert.assertEquals(propRow1.getDisplayLabel(),"date", "Display Label displayed correctly");
            Assert.assertEquals(propRow1.getDatatype(),datatypedate,"Data Type updated correctly");
            Assert.assertEquals(propRow1.getDefaultValue(),dateValue, "Default value incorrect for property: " + compositePropNameA+"2");
            Assert.assertEquals(propRow1.getMandatory(),mandatory,"Mandatory displayed correctly");
            Assert.assertEquals(propRow1.getMultiValue(),"Yes","Mandatory displayed correctly");
                                                        
            //Activate model1         
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,true);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
            
            //Edit property of active model with valid default value
            mpp = cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"2", "Atitle", "Adesc", "2/2/2000").render();
            
            //Verify property details are modified
            PropertyRow propRow2 = mpp.getPropertyRowByName(compositePropNameA+"2");
            Assert.assertNotNull(propRow2);            
            Assert.assertEquals(propRow2.getDisplayLabel(),"Atitle", "Display Label displayed correctly");
            Assert.assertEquals(propRow2.getDatatype(),datatypedate,"Data Type updated correctly");
            Assert.assertEquals(propRow2.getDefaultValue(),"2000-02-02", "Default value incorrect for property: " + compositePropNameA+"2");
            Assert.assertEquals(propRow2.getMandatory(),mandatory,"Mandatory displayed correctly");
            Assert.assertEquals(propRow2.getMultiValue(),"Yes","Mandatory displayed correctly");
                       
            //Create site, content and select content
            siteActions.createSite(driver,siteName, "", "");
            siteActions.openSitesDocumentLibrary(driver, siteName);
            siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
            siteActions.selectContent(driver, name).render();   
                       
            List<String> aspects = new ArrayList<String>();
            aspects.add(shareAspectName);
            siteActions.addAspects(driver, aspects);
                       
            //Delete node in share 
            siteActions.openSiteDashboard(driver, siteName);
            siteActions.openDocumentLibrary(driver);
            siteActions.deleteContentInDocLib(driver, name);           
         
            // Delete Node Permanently
            userActions.navigateToTrashCan(driver);
            userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");
                       
            //Deactivate model1
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,false);
            
            //Edit property of deactivated model1 with valid default value            
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            mpp = cmmActions.editProperty(driver, compositeAspectName, compositePropNameA+"2", "int", "int", DataType.Int, MandatoryClassifier.Optional,true,"123").render();
            
            //Verify property details are updated
            PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameA+"2");
            Assert.assertNotNull(propRow6);            
            Assert.assertEquals(propRow6.getDisplayLabel(),"int", "Display Label displayed correctly");
            Assert.assertEquals(propRow6.getDatatype(),datatypei,"Data Type updated correctly");
            Assert.assertEquals(propRow6.getDefaultValue(),"123", "Default value incorrect for property: " + compositePropNameA+"2");
            Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
            Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");    
                        
        }
        
        /**<ul>
         * <li>Test to edit property with invalid test data without constraint</li>
         * <li>Create Model1</li>
         * <li>Create aspect1 for model1</li>
         * <li>Create String Property1 for Type:with Default Set</li>
         * <li>Edit property for draft model with invalid default value</li>
         * <li>Verify the property details are not changed</li>
         * <li>Activate model1</li>
         * <li>Edit property with invalid default value</li>
         * <li>Verify property details are not modified</li>
         * <li>Edit property of deactivated model1 with invalid default value</li>
         * <li>Verify property details are not modified</li>              
         *</ul>
         * @throws Exception 
         */
        @AlfrescoTest(testlink="tobeaddedpwoc1")
        @Test(groups = "EnterpriseOnly", priority=3)
        public void testEditProWinvalidT() throws Exception
        {
            
            String modelName = "model" + System.currentTimeMillis();
           
            String compositeTypeName = modelName + ":" + typeName;
         
            String compositePropNameT = modelName + ":" + propertyNameT;
         
            loginAs(driver, new String[] {testUser});
            
            cmmActions.navigateToModelManagerPage(driver);            

            // Create New Model
            cmmActions.createNewModel(driver, modelName).render();               
              
            // View Types and Aspects: Model
            cmmActions.viewTypesAspectsForModel(driver, modelName).render();

            // Add Types: Model
            cmmActions.createType(driver, typeName).render();                             
                     
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
            cmmActions.viewProperties(driver, compositeTypeName);
            
            // Create String Property1 for draft model Type:with Default Set
            cmmActions.createProperty(driver, propertyNameT+"3", "long", "long", DataType.Long, MandatoryClassifier.Optional, false, "1234555666").render();  
                        
            //Edit property for draft model with invalid default value
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            cmmActions.editProperty(driver, compositeTypeName, compositePropNameT+"3", "double", "double", DataType.Double, MandatoryClassifier.Mandatory,false,"invalid").render();
            mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render(); 
            
            //Verify the property details are not changed
            PropertyRow proRow1 = mpp.getPropertyRowByName(compositePropNameT+"3");
            Assert.assertNotNull(proRow1);
            Assert.assertEquals(proRow1.getDisplayLabel(),"long", "Display Label displayed correctly");
            Assert.assertEquals(proRow1.getDatatype(),datatypel,"Data Type updated correctly");
            Assert.assertEquals(proRow1.getDefaultValue(),"1234555666", "Default value incorrect for property: " + compositePropNameT+"3");
            Assert.assertEquals(proRow1.getMandatory(),optional,"Mandatory displayed correctly");
            Assert.assertEquals(proRow1.getMultiValue(),"No","Mandatory displayed correctly");
                    
            //Activate model1
            //Edit property with invalid default value
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,true);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"3", "Atitle", "Adesc", "invalid").render();
            mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();  
            
            //Verify property details are not modified
            PropertyRow propRow3 = mpp.getPropertyRowByName(compositePropNameT+"3");
            Assert.assertNotNull(propRow3);            
            Assert.assertEquals(propRow3.getDatatype(),datatypel,"Data Type updated correctly");
            Assert.assertEquals(propRow3.getDefaultValue(),"1234555666","Default value incorrect for property: " + compositePropNameT+"3");             
               
            //Edit property of deactivated model1 with invalid default value
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,false);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            cmmActions.editProperty(driver, compositeTypeName, compositePropNameT+"3", "date", "date", DataType.Date, MandatoryClassifier.Mandatory,false,"1234567890").render();
            mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();  
            
            //Verify property details are not modified
            PropertyRow propRow5 = mpp.getPropertyRowByName(compositePropNameT+"3");
            Assert.assertNotNull(propRow5);
            Assert.assertEquals(propRow5.getDisplayLabel(),"long", "Display Label displayed correctly");
            Assert.assertEquals(propRow5.getDatatype(),datatypel,"Data Type updated correctly");
            Assert.assertEquals(propRow5.getDefaultValue(),"1234555666", "Default value incorrect for property: " + compositePropNameT+"3");
            Assert.assertEquals(propRow5.getMandatory(),optional,"Mandatory displayed correctly");
            Assert.assertEquals(propRow5.getMultiValue(),"No","Multiple checked");          
                                   
        }
        
        /**<ul>
         * <li>Test to edit property with invalid test data without constraint</li>
         * <li>Create Model1</li>
         * <li>Create aspect1 for model1</li>
         * <li>Create String Property1 for aspect:with Default Set</li>
         * <li>Edit property for draft model with invalid default value</li>
         * <li>Verify the property details are not changed</li>
         * <li>Activate model1</li>
         * <li>Edit property with invalid default value</li>
         * <li>Verify property details are not modified</li>
         * <li>Edit property of deactivated model1 with invalid default value</li>
         * <li>Verify property details are not modified</li>              
         *</ul>
         * @throws Exception 
         */
        @AlfrescoTest(testlink="tobeaddedpwoc1")
        @Test(groups = "EnterpriseOnly", priority=4)
        public void testEditProWinvalidA() throws Exception
        {
            
            String modelName = "model" + System.currentTimeMillis();
            
            String compositeAspectName = modelName + ":" + aspectName;            
          
            String compositePropNameA = modelName + ":" + propertyNameA;
            
            loginAs(driver, new String[] {testUser});
            
            cmmActions.navigateToModelManagerPage(driver);
           
            // Create New Model
            cmmActions.createNewModel(driver, modelName).render();               
              
            // View Types and Aspects: Model
            cmmActions.viewTypesAspectsForModel(driver, modelName).render();
           
            // Add Types: Model
            cmmActions.createAspect(driver, aspectName).render();      
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
            cmmActions.viewProperties(driver, compositeAspectName);
            
            // Create String Property1 for Aspect: with Default Set
            cmmActions.createProperty(driver, propertyNameA+"4", "boolean", "boolean", DataType.Boolean, MandatoryClassifier.Optional, false, "True").render();  
                                    
            //Edit property for draft model with invalid default value
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            cmmActions.editProperty(driver, compositeAspectName, compositePropNameA+"4", "int", "int", DataType.Int, MandatoryClassifier.Mandatory,true,"invalid").render();
            mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render(); 
            
            //Verify the property details are not changed
            PropertyRow proRow1 = mpp.getPropertyRowByName(compositePropNameA+"4");
            Assert.assertNotNull(proRow1);
            Assert.assertEquals(proRow1.getDisplayLabel(),"boolean", "Display Label displayed correctly");
            Assert.assertEquals(proRow1.getDatatype(),datatypeb,"Data Type updated correctly");
            Assert.assertEquals(proRow1.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameA+"4");
            Assert.assertEquals(proRow1.getMandatory(),optional,"Mandatory displayed correctly");
            Assert.assertEquals(proRow1.getMultiValue(),"No","Mandatory displayed correctly");
                    
            //Activate model1
            //Edit property with invalid default value
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,true);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"4", "Atitle", "Adesc", "invalid").render();
            mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render(); 
            
            //Verify property details are not modified
            PropertyRow propRow2 = mpp.getPropertyRowByName(compositePropNameA+"4");
            Assert.assertNotNull(propRow2);            
            Assert.assertEquals(propRow2.getDatatype(),datatypeb,"Data Type updated correctly");
            Assert.assertEquals(propRow2.getDefaultValue(),"true","Default value incorrect for property: " + compositePropNameA+"4");             
               
            //Edit property of deactivated model1 with invalid default value
            cmmActions.navigateToModelManagerPage(driver);
            cmmActions.setModelActive(driver, modelName,false);
            mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
            cmmActions.editProperty(driver, compositeAspectName, compositePropNameA+"4", "long", "long", DataType.Long, MandatoryClassifier.Mandatory,false,"125.567").render();
            mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render(); 
            
            //Verify property details are not modified
            PropertyRow propRow3 = mpp.getPropertyRowByName(compositePropNameA+"4");
            Assert.assertNotNull(propRow3);
            Assert.assertEquals(propRow3.getDisplayLabel(),"Atitle", "Display Label displayed correctly");
            Assert.assertEquals(propRow3.getDatatype(),datatypeb,"Data Type updated correctly");
            Assert.assertEquals(propRow3.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameA+"4");
            Assert.assertEquals(propRow3.getMandatory(),optional,"Mandatory displayed correctly");
            Assert.assertEquals(propRow3.getMultiValue(),"No","Multiple checked");                                   
        }
    }

