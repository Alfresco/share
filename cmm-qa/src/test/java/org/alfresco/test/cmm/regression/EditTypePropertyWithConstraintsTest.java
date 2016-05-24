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
 * Test Class to test edit property with constraints for type and aspect
 * 
 * @author Charu
 */

import java.util.HashMap;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class EditTypePropertyWithConstraintsTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(EditTypePropertyWithConstraintsTest.class);

    private String testUser;
    
    public EditPropertyPopUp epp;

    protected String testSiteName = "swsdp";

    
    private String testName; 
      
    private String typeName = "type"+ System.currentTimeMillis();;
  
    private String propertyNameT = "propertyt";  
   
    public DashBoardPage dashBoardpage;
    
    public ManageTypesAndAspectsPage mtap;
    
    public ManagePropertiesPage mpp;
    
    
    
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
     * <li>End to end test to edit property for a model type with constraint</li>
     * <li>Create Model1</li>
     * <li>Create type1 for model1</li>
     * <li>Create String Property1 with constraint  for Type: No Default Set</li>
     * <li>Edit property for draft model with valid default value</li>
     * <li>Activate model1</li>
     * <li>Edit active model property with Regex constraint math set false and with default value</li>
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
    @AlfrescoTest(testlink="tobeaddeddel1")
    // SHA-961: Removal of Regex Match Required option
    @Test(groups = "EnterpriseOnly", priority=1, enabled = false)
    public void testEndTEndWithConsForTypeMatchNotSet() throws Exception
    {              
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
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
                
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.REGEX);
        constraintDetails1.setValue("[1-10]|11");
        constraintDetails1.setMatchRequired(false);
        
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.MINMAXLENGTH);        
        constraintDetails2.setMinValue(1);
        constraintDetails2.setMaxValue(30);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.REGEX);
        constraintDetails3.setValue(".*@alfresco.com");
        constraintDetails3.setMatchRequired(false); 
        
        ConstraintDetails constraintDetails4 = new ConstraintDetails();
        constraintDetails4.setType(ConstraintTypes.None);
                       
        // Create String Property1 for draft model with Regular Expression constraint
        mpp = cmmActions.createPropertyWithConstraint(driver, propertyNameT+"1", "int", "int", DataType.Int,MandatoryClassifier.Optional, false, "12", constraintDetails1).render();
        
        //Edit property of draft model         
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);          
            
        //Edit active model property with Regex constraint math set false
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"1","RegexNoMath","RegexNoMath","ABC").render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
                
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        //Apply type/aspect to node in share
        detailsPage.changeType(getShareTypeName(modelName, typeName)).render();
        
        //Compare cmm properties matches with Share        
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("RegexNoMath","ABC"); 
               
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                   
        //Delete node in share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver,name);           
            
        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");            
                            
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName,false);
        
        //Edit property of deactivated model1 with default value and with constraint none          
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"1", "boolean", "boolean", DataType.Boolean, MandatoryClassifier.Optional,true,"true", constraintDetails4).render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameT+"1");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"boolean", "Display Label displayed correctly");        
        Assert.assertEquals(propRow6.getDatatype(),datatypeb,"Data Type updated correctly");
        Assert.assertEquals(propRow6.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameT);
        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");                                 
        
    }   
       
    /**<ul>
     * <li>End to end test to edit property for a model type with constraint</li>
     * <li>Create Model1</li>
     * <li>Create type1 for model1</li>
     * <li>Create String Property1 with constraint  for Type: No Default Set</li>
     * <li>Edit property for draft model with valid default value</li>
     * <li>Activate model1</li>
     * <li>Edit active model property with Regex constraint math set true and with default value</li>
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
    @AlfrescoTest(testlink="tobeaddeddel3")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testEditRegexForTypeMatchRequiredSet() throws Exception
    {      
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
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
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: Length  an default value
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails1.setMinValue(4);;
        constraintDetails1.setMaxValue(5);
        
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.REGEX);
        constraintDetails2.setValue("[a-z]*");
        // SHA-961: Removal of Regex Match Required option        
        // constraintDetails2.setMatchRequired(false);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);
        
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"2", "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                constraintDetails1).render();
        
        //Edit property of draft model property        
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"2", "text", "text", DataType.Text, MandatoryClassifier.Mandatory, true, "ab", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);        
                
        constraintDetails2.setMatchRequired(true);
        //Edit active model with Regex constraint math set true
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.editPropertyWithConstraintForAM(driver, compositeTypeName, compositePropNameT+"2","RegexMatch","RegexMatch","abc", constraintDetails2).render();
        
         //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
                
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        //Apply type/aspect to node in share
        detailsPage.changeType(getShareTypeName(modelName, typeName)).render();
        
        //Compare cmm properties matches with Share        
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("RegexMatch","abc"); 
               
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                   
        //Delete node in share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver,name);           
            
        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");                 
                   
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName,false);
        
        //Edit property of deactivated model1 with default value and without constraint           
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"2", "date", "date", DataType.Date, MandatoryClassifier.Optional,true,dateEntry,constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameT+"2");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"date", "Display Label displayed correctly");        
        Assert.assertEquals(propRow6.getDatatype(),datatypedate,"Data Type updated correctly");
        Assert.assertEquals(propRow6.getDefaultValue(),dateValue, "Default value incorrect for property: " + compositePropNameT+"2");
        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");                          
           
       
    }    
       
    @AlfrescoTest(testlink="tobeaddeddel5")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testEditMinMaxValueForType() throws Exception
    {
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
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

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Add Property With Constraint: List
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("12.234\n-0.5\n5\n1234.6789\n12345\n-1234");
        constraintDetails1.setSorted(true);
        
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails2.setMinValue(-4);;
        constraintDetails2.setMaxValue(5);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);
        
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"3", "", "", DataType.Float, MandatoryClassifier.Optional, false, "-1234",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"3", "negint", "negint", DataType.Int, MandatoryClassifier.Mandatory, true, "-3", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);  
          
        //Edit active model property with min max value constraint
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"3","posInt","posint","5").render();
        
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        //Apply type/aspect to node in share
        detailsPage.changeType(getShareTypeName(modelName, typeName)).render();
        
        //Compare cmm properties matches with Share        
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("posInt","5"); 
               
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                   
        //Delete node in share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver,name);           
            
        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");                 
                       
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName,false);
        
        //Edit property of deactivated model1 with default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"3", "long", "long", DataType.Long, MandatoryClassifier.Optional,true,"5", constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameT+"3");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"long", "Display Label displayed correctly");          
        Assert.assertEquals(propRow6.getDatatype(),datatypel,"Data Type updated correctly");
        Assert.assertEquals(propRow6.getDefaultValue(),"5", "Default value incorrect for property: " + compositePropNameT+"3");
        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");            
        
        
    }
    
   
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testEditMinMaxLengthForType() throws Exception
    {
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
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

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeTypeName);

        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        
        // TODO: Amend line below for SHA-1077: constraintDetails1.setValue("12\n5\n-3\n6\n-6");
        constraintDetails1.setValue("12\n5.6\n-3.5\n6.0\n-6");      
        
        // Add Property With Constraint: MINMAXLENGTH
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails2.setMinValue(1);
        constraintDetails2.setMaxValue(3);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);

        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"4", "", "", DataType.Double, MandatoryClassifier.Optional, false, "12",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"4", "minmaxd", "minmaxd", DataType.Text, MandatoryClassifier.Mandatory, true, "pet", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true); 
               
        //Edit active model with Minmaxlength
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compositeTypeName, compositePropNameT+"4","minmaxa","minmaxa","£m5", constraintDetails2).render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        //Apply type/aspect to node in share
        detailsPage.changeType(getShareTypeName(modelName, typeName)).render();
        
        //Compare cmm properties matches with Share        
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("minmaxa","£m5"); 
               
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                   
        //Delete node in share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver,name);           
            
        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");
        
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName,false);
        
        //Edit property of deactivated model1 with default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"4", "boolean", "boolean", DataType.Boolean, MandatoryClassifier.Optional,true,"True", constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameT+"4");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"boolean", "Display Label displayed correctly");          
        Assert.assertEquals(propRow6.getDatatype(),datatypeb,"Data Type updated correctly");
        Assert.assertEquals(propRow6.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameT+"4");
        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");                   
    }  
           
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testEditListForType() throws Exception
    {
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
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

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();       
        
        cmmActions.viewProperties(driver, compositeTypeName);

        //Add property with constraint: List with int
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("-11\n-10\n11\n12");
        constraintDetails1.setSorted(false);
        
        // Add Property With Constraint: List
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.LIST);
        constraintDetails2.setValue("food\ndrink\nshelter\nwork");
        constraintDetails2.setSorted(true);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);

        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"5", "", "", DataType.Int, MandatoryClassifier.Optional, false, "11",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"5", "minmaxd", "minmaxd", DataType.Text, MandatoryClassifier.Mandatory, true, "food", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true); 
        
        constraintDetails2.setValue("food123\ndrink123\nshelter123\nwork123");
        constraintDetails2.setSorted(false);
        
        //Edit active model with Minmaxlength
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compositeTypeName, compositePropNameT+"5","minmaxa","minmaxa","work123", constraintDetails2).render();
        
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        //Apply type/aspect to node in share
        detailsPage.changeType(getShareTypeName(modelName, typeName)).render();
        
        //Compare cmm properties matches with Share        
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("minmaxa","work123"); 
               
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                   
        //Delete node in share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver,name);           
            
        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");        
                       
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName,false);
        
        //Edit property of deactivated model1 with default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"5", "Double", "Double", DataType.Double, MandatoryClassifier.Optional,true,"-11",constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameT+"5");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"Double", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatyped,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"-11", "Default value incorrect for property: " + compositePropNameT+"5");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");       
                     
    }
    
   
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=6)
    public void invalidTestEditPropForActiveModelType() throws Exception
    {        
        String modelName = "model" + System.currentTimeMillis();
        String compositeTypeName = modelName + ":" + typeName;       
        String compositePropNameT = modelName + ":" + propertyNameT;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model
        cmmActions.createNewModel(driver, modelName).render(); 
        cmmActions.setModelActive(driver, modelName, true);       
                  
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();    
                   
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeTypeName);

        //Constraint: MinMaxValue
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails1.setMinValue(-5);
        constraintDetails1.setMaxValue(5);      
              
        //Constraint: List 
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.LIST);
        constraintDetails2.setValue("new\nold\nbrilliant\ngood\nbad\nexcellant");
        constraintDetails2.setSorted(false);
        
        //Constraint: List-MinMaxLength
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails3.setMinValue(1);
        constraintDetails3.setMaxValue(8); 
        
        //Contraint:None
        ConstraintDetails constraintDetails4 = new ConstraintDetails();
        constraintDetails4.setType(ConstraintTypes.None);        
                        
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"f", "float", "float", DataType.Float, MandatoryClassifier.Optional, false, "-0.55666",
                constraintDetails1).render();
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"d", "double", "double", DataType.Double, MandatoryClassifier.Optional, false, "0.55666",
                constraintDetails1).render();
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"i", "int", "int", DataType.Int, MandatoryClassifier.Optional, false, "-5",
                constraintDetails1).render();
        
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"t", "Text", "Text", DataType.Text, MandatoryClassifier.Optional, false, "old",
                constraintDetails2).render();
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"date", "Date", "Date", DataType.Date, MandatoryClassifier.Optional, false, dateEntry,
                constraintDetails4).render();    
           
        
        //Edit property of draft model property with constraint and invalid default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"f", "invalidfloatdata","invalidfloatdata" , "-6").render();   
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();     
                       
        //Edit property of draft model property with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"d", "invaliddoubledata","invalidoubledata" , "one").render();   
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
                
        //Edit property of draft model property with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"i", "invalidintdata","invalidintdata" , "5.10").render();   
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();                
        
        //Edit property of active model property with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"date", "Invaliddate", "Invaliddate", "12345346789").render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
               
        //Edit property of active model property with constraint and default value not in list  
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.editPropertyForAM(driver, compositeTypeName, compositePropNameT+"t", "Invalidtext", "Invalidtext", "true").render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();            
             
        //Verify property details are not modified
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameT+"f");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"float", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatypef,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"-0.55666", "Default value incorrect for property: " + compositePropNameT+"f");       
        
         //Verify property details are not modified
        PropertyRow propRow2 = mpp.getPropertyRowByName(compositePropNameT+"d");
        Assert.assertNotNull(propRow2);            
        Assert.assertEquals(propRow2.getDisplayLabel(),"double", "Display Label displayed correctly");       
        Assert.assertEquals(propRow2.getDatatype(),datatyped,"Data Type updated correctly");
        Assert.assertEquals(propRow2.getDefaultValue(),"0.55666", "Default value incorrect for property: " + compositePropNameT+"d");
        
         //Verify property details are not modified
        PropertyRow propRow3 = mpp.getPropertyRowByName(compositePropNameT+"i");
        Assert.assertNotNull(propRow3);            
        Assert.assertEquals(propRow3.getDisplayLabel(),"int", "Display Label displayed correctly");       
        Assert.assertEquals(propRow3.getDatatype(),datatypei,"Data Type updated correctly");
        Assert.assertEquals(propRow3.getDefaultValue(),"-5", "Default value incorrect for property: " + compositePropNameT+"i");
        
        //Verify property details are not modified
        PropertyRow propRow4 = mpp.getPropertyRowByName(compositePropNameT+"t");
        Assert.assertNotNull(propRow4);            
        Assert.assertEquals(propRow4.getDisplayLabel(),"Text", "Display Label displayed correctly");    
        Assert.assertEquals(propRow4.getDatatype(),datatypet,"Data Type updated correctly");
        Assert.assertEquals(propRow4.getDefaultValue(),"old", "Default value incorrect for property: " + compositePropNameT+"t");
        
        //Verify property details are not modified
        PropertyRow propRow5 = mpp.getPropertyRowByName(compositePropNameT+"date");
        Assert.assertNotNull(propRow5);            
        Assert.assertEquals(propRow5.getDisplayLabel(),"Date", "Display Label displayed correctly");    
        Assert.assertEquals(propRow5.getDatatype(),datatypedate,"Data Type updated correctly");
        Assert.assertEquals(propRow5.getDefaultValue(),dateValue, "Default value incorrect for property: " + compositePropNameT+"date");
        
      
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=7)
    public void invalidTestEditPropForDeactiveModelType() throws Exception
    {
        String modelName = "model" + System.currentTimeMillis();
        String compositeTypeName = modelName + ":" + typeName;       
        String compositePropNameT = modelName + ":" + propertyNameT;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
        
        cmmActions.setModelActive(driver, modelName, true);
        cmmActions.navigateToModelManagerPage(driver);        
        cmmActions.setModelActive(driver, modelName, false);
                  
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();      
               
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeTypeName);

        //Constraint: MinMaxValue
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails1.setMinValue(-5);
        constraintDetails1.setMaxValue(5);      
              
        //Constraint: List 
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.LIST);
        constraintDetails2.setValue("new\nold\nbrilliant\ngood\nbad\nexcellant");
        constraintDetails2.setSorted(true);
        
        //Constraint: List-MinMaxLength
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails3.setMinValue(1);
        constraintDetails3.setMaxValue(5);      
                        
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+"7", "float", "float", DataType.Float, MandatoryClassifier.Optional, false, "-0.55666",
                constraintDetails1).render();
        
        //Edit property of draft model with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"7", "InvalidInt", "InvalidInt", DataType.Int, MandatoryClassifier.Mandatory, true, "-6", constraintDetails2).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render(); 
        
        //Edit property of draft model with constraint and default value exceeding the limit          
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"7", "Invalidfloat", "Invalidfloat", DataType.Float, MandatoryClassifier.Optional, true, "4567.8765", constraintDetails2).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model with constraint and default value exceeding the limit         
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"7", "Invaliddouble","Invaliddouble", DataType.Double, MandatoryClassifier.Mandatory, true, "6", constraintDetails2).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"7", "Invalidtxt", "Invalidtxt", DataType.Text, MandatoryClassifier.Optional, true, "maxsix", constraintDetails3).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"7", "InvalidBoolean", "InvalidBoolean", DataType.Boolean, MandatoryClassifier.Mandatory, true, "inval", constraintDetails3).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model with constraint and default value exceeding the limit    
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"7", "Invaliddate", "Invaliddate", DataType.Date, MandatoryClassifier.Mandatory, true, "123453467", constraintDetails1).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();           
              
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameT+"7");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"float", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatypef,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"-0.55666", "Default value incorrect for property: " + compositePropNameT+"7");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");       
    } 
    
      
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=8, enabled=false)
    public void testEditJavaClassForType() throws Exception
    {
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String compositeTypeName = modelName + ":" + typeName;       
        String compositePropNameT = modelName + ":" + propertyNameT;
        
        ContentDetails contentDetails =new ContentDetails();
        contentDetails.setName(name);         
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
        
        cmmActions.setModelActive(driver, modelName, true);
        cmmActions.navigateToModelManagerPage(driver);        
        cmmActions.setModelActive(driver, modelName, false);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        
        cmmActions.viewProperties(driver, compositeTypeName);

        //Constraint: MinMaxValue
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails1.setMinValue(5);
        constraintDetails1.setMaxValue(10);  
        
        // Add Property With Constraint: JavaClass
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.JAVACLASS);
        constraintDetails2.setValue("org.alfresco.extension.classconstraint.example.InvoiceConstraint");
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);

        //Create property for draft model with constraint
        cmmActions.createPropertyWithConstraint(driver, propertyNameT+8, "Int", "Int", DataType.Int, MandatoryClassifier.Optional, false, "10",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"8", "Long", "Long", DataType.Long, MandatoryClassifier.Mandatory, true, "5", constraintDetails1).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);        
               
        //Edit active model property with Java class constraint
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compositeTypeName, compositePropNameT+"8","Javaclass","Javaclass","", constraintDetails2).render();
        
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        //Apply type/aspect to node in share
        detailsPage.changeType(getShareTypeName(modelName, typeName)).render();
        
        //Compare cmm properties matches with Share        
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("Javaclass",""); 
               
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                   
        //Delete node in share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver,name);           
            
        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, name, "documentLibrary");        
                       
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName,false);
        
        //Edit property of deactivated model1 with default value       
        mtap = cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeTypeName, compositePropNameT+"8", "Double", "Double", DataType.Double, MandatoryClassifier.Optional,true,"15.98665",constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameT+"8");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"Double", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatyped,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"15.98665", "Default value incorrect for property: " + compositePropNameT+"8");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");       
                     
    }
        
        
       
    
    
   
    
}
