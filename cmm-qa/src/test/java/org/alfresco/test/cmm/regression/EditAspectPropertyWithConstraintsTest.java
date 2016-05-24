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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class EditAspectPropertyWithConstraintsTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(EditAspectPropertyWithConstraintsTest.class);
    
    private String testUser;
    
    private String testName; 
        
    public EditPropertyPopUp epp;
    
    //public ManagePropertiesPage mpp;
    
    protected String testSiteName = "swsdp";

    @Value("${cmm.property.optional}") String optional;
    
    
    //private String modelName = "model" + System.currentTimeMillis();   
    private String aspectName = "aspect"+ System.currentTimeMillis();
       
    private String propertyNameA = "propertya";
    
    public ManagePropertiesPage mpp;
    
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);   
        
//        dateValue = datePropList; 
        
//        dateValue = getValidDateEntry(datePropList);
        
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
    
    
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testEndTEndWithConsForAspectMatchSet() throws Exception
    {              
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;

        
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
        
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        
        cmmActions.viewProperties(driver, compositeAspectName);
                
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("2345.6543\n-4567.7654\n23.6765\n-3\n6.0\n5");
        constraintDetails1.setSorted(false);
        
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.MINMAXLENGTH);        
        constraintDetails2.setMinValue(0);
        constraintDetails2.setMaxValue(3);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.REGEX);
        constraintDetails3.setValue(".*@alfresco.com");
        constraintDetails3.setMatchRequired(true);
        
        ConstraintDetails constraintDetails4 = new ConstraintDetails();
        constraintDetails4.setType(ConstraintTypes.None);
        
        // Create String Property1 for draft model with constraint
        mpp = cmmActions.createPropertyWithConstraint(driver, propertyNameA+"1", "Double", "Double", DataType.Double,MandatoryClassifier.Optional, false, "5", constraintDetails1).render();
        
        //Edit property of draft model property with default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"1", "text", "text", DataType.Text, MandatoryClassifier.Mandatory, true, "t$1", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);      
                
        //Edit active model with constraint none and default value
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compositeAspectName, compositePropNameA+"1","Noconstraint", "Noconstraint","test@alfresco.com",constraintDetails4).render();
        //Edit active model with Regex constraint math set true
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compositeAspectName, compositePropNameA+"1","RegexMath","RegexMath","test@alfresco.com", constraintDetails3).render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        //Create site and node in share       
        siteActions.createSite(driver, siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Apply Aspect to node in share        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);  
              
        //Apply type/aspect to node in share
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("RegexMath","test@alfresco.com");      
            
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
        // SHA:1253: Fixes
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"1", "datetime", "datetime", DataType.DateTime, MandatoryClassifier.Optional,true,dateEntry, constraintDetails4).render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameA+"1");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"datetime", "Display Label displayed correctly");        
        Assert.assertEquals(propRow6.getDatatype(),datatypedt,"Data Type updated correctly");
        String actual = propRow6.getDefaultValue();
        Assert.assertEquals(actual,dateValue, 
                "Default value incorrect for property: " + compositePropNameA+"1" +
                " dataValue = " + dateValue + 
                " actual = " + actual );

        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");                                
           
    }     
        
    @AlfrescoTest(testlink="tobeaddeddel2")
    // SHA-961: Removal of Regex Match Required option    
    @Test(groups = "EnterpriseOnly", priority=2, enabled=false)
    public void testRegexForAspectMatchRequiredNotSet() throws Exception
    {
       
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
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
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: Length  an default value
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails1.setMinValue(1);;
        constraintDetails1.setMaxValue(2000000000);
        
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.REGEX);
        constraintDetails2.setValue("[a-z]*");        
        constraintDetails2.setMatchRequired(false);
        
        ConstraintDetails constraintDetails4 = new ConstraintDetails();
        constraintDetails4.setType(ConstraintTypes.None);
        
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"2", "", "", DataType.Long, MandatoryClassifier.Optional, false, "1234567890",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint none and default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"2", "text", "text", DataType.Text, MandatoryClassifier.Mandatory, true, "A%B", constraintDetails4).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);         
        
        //Edit active model with Regex constraint math not set true
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        mpp = cmmActions.editPropertyWithConstraintForAM(driver, compositeAspectName, compositePropNameA+"2","RegexMathNotset","RegexMathNotSet","A%BC", constraintDetails2).render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        //Create site and node in share       
        siteActions.createSite(driver, siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Apply Aspect to node in share        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);  
              
        //Apply type/aspect to node in share
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("RegexMatchNotset","A%BC");      
            
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
        // SHA: 787, 1260: Removal of cm:content from the Property data types        
        mpp = cmmActions.editProperty(driver, compositeAspectName, compositePropNameA+"2", "content", "content", DataType.MlText, MandatoryClassifier.Optional,true,"con456").render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameA+"2");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"content", "Display Label displayed correctly");        
        Assert.assertEquals(propRow6.getDatatype(),datatypec,"Data Type updated correctly");
        Assert.assertEquals(propRow6.getDefaultValue(),"con456", "Default value incorrect for property: " + compositePropNameA+"2");
        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");                       
    }   
    
    
    @AlfrescoTest(testlink="tobeaddeddel3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testMinMaxValueForAspect() throws Exception
    {
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
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
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: List
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("small\nbig\nlarge\nhalf\nfull\nquarter");
        constraintDetails1.setSorted(true);
        
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails2.setMinValue(-4);;
        constraintDetails2.setMaxValue(1);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);
                
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"3", "", "", DataType.Text, MandatoryClassifier.Optional, false, "big",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"3", "negint", "negint", DataType.Int, MandatoryClassifier.Mandatory, true, "-1", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true); 
               
        //Edit active model with Minmaxvalue
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.editPropertyWithConstraintForAM(driver, compositeAspectName, compositePropNameA+"3","negInt","negint","-4", constraintDetails2).render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        //Create site and node in share       
        siteActions.createSite(driver, siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Apply Aspect to node in share        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);  
              
        //Apply type/aspect to node in share
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("negInt","-4");      
            
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
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"3", "float", "float", DataType.Float, MandatoryClassifier.Optional,true,"0",constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow6 = mpp.getPropertyRowByName(compositePropNameA+"3");
        Assert.assertNotNull(propRow6);            
        Assert.assertEquals(propRow6.getDisplayLabel(),"float", "Display Label displayed correctly");          
        Assert.assertEquals(propRow6.getDatatype(),datatypef,"Data Type updated correctly");
        Assert.assertEquals(propRow6.getDefaultValue(),"0", "Default value incorrect for property: " + compositePropNameA+"3");
        Assert.assertEquals(propRow6.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow6.getMultiValue(),"No","Multiple checked");                   
    }  
           
        
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testEditMinMaxLengthForAspect() throws Exception
    {
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
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
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeAspectName);

        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("12\n5097655\n-5667777888\n567\n1");
        constraintDetails1.setSorted(false);
        
        // Add Property With Constraint: MINMAXLENGTH
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.MINMAXLENGTH);
        constraintDetails2.setMinValue(1);
        constraintDetails2.setMaxValue(8);
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);

        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"4", "", "", DataType.Long, MandatoryClassifier.Optional, false, "-5667777888",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"4", "minmaxd", "minmaxd", DataType.Text, MandatoryClassifier.Mandatory, true, "12erty", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true); 
               
        //Edit active model with Minmaxlength
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"4","minmaxa","minmaxa","n1").render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        //Create site and node in share       
        siteActions.createSite(driver, siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Apply Aspect to node in share        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);  
              
        //Apply type/aspect to node in share
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("minmaxa","n1");      
            
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
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"4", "boolean", "boolean", DataType.Boolean, MandatoryClassifier.Optional,true,"True",constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameA+"4");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"boolean", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatypeb,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameA+"4");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");                   
       
    }    
   
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testListForAspect() throws Exception
    {
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
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

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeAspectName);

        // Add Property With Constraint: List
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.LIST);
        constraintDetails1.setValue("food123\ndrink123\nshelter123\nwork123");
        constraintDetails1.setSorted(false);
        
        //Add property with constraint: List with int
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.LIST);
        constraintDetails2.setValue("-11\n-10\n11\n12");
        constraintDetails2.setSorted(true);    
       
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"5", "", "", DataType.Text, MandatoryClassifier.Optional, false, "food123",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"5", "Listd", "Listd", DataType.Int, MandatoryClassifier.Mandatory, true, "-11", constraintDetails2).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true); 
        
        constraintDetails2.setValue("0\n1\n12345\n-12345");
        constraintDetails2.setSorted(false);   
        
        //Edit active model with Minmaxlength
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.editPropertyWithConstraintForAM(driver, compositeAspectName, compositePropNameA+"5","Lista","Lista","0", constraintDetails2).render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        //Create site and node in share       
        siteActions.createSite(driver, siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Apply Aspect to node in share        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);  
              
        //Apply type/aspect to node in share
        HashMap<String, Object> expectedProps = new HashMap<String, Object>();            
        expectedProps.put("Lista","0");      
            
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
        
        constraintDetails1.setValue("food\ndrink\nshelter\nwork");
        
        //Edit property of deactivated model1 with default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"5", "text", "text", DataType.Text, MandatoryClassifier.Optional,true,"food",constraintDetails1).render();
        
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameA+"5");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"text", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatypet,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"food", "Default value incorrect for property: " + compositePropNameA+"5");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");       
    }
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=6)
    public void invalidTestEditPropForActiveModelAspect() throws Exception
    {       
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
        
        cmmActions.setModelActive(driver, modelName, true);       
                 
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createAspect(driver, aspectName).render();          
              
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeAspectName);

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
        constraintDetails3.setMaxValue(8);
        
        //Constraint: None
        ConstraintDetails constraintDetails4 = new ConstraintDetails();
        constraintDetails4.setType(ConstraintTypes.None);
        
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"f", "float", "float", DataType.Float, MandatoryClassifier.Optional, false, "-0.55666",
                constraintDetails1).render();
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"d", "double", "double", DataType.Double, MandatoryClassifier.Optional, false, "0.55666",
                constraintDetails1).render();
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"i", "int", "int", DataType.Int, MandatoryClassifier.Optional, false, "-5",
                constraintDetails1).render();
        
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"t", "Text", "Text", DataType.Text, MandatoryClassifier.Optional, false, "old",
                constraintDetails2).render();
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"date", "Date", "Date", DataType.Date, MandatoryClassifier.Optional, false, dateEntry,
                constraintDetails4).render();    
           
        
        //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"f", "invalidfloatdata","invalidfloatdata" , "-6").render();   
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"d", "invaliddoubledata","invalidoubledata" , "good").render();   
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
       //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"i", "invalidintdata","invalidintdata" , "5.10").render();   
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();                
        
        //Edit property of active model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"date", "Invaliddate", "Invaliddate", "12345346789").render();        
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();           
               
        //Edit property of active model property with constraint and default value not in list  
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyForAM(driver, compositeAspectName, compositePropNameA+"t", "Invalidtext", "Invalidtext", "true").render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();            
             
        //Verify property details are not modified
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameA+"f");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"float", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatypef,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"-0.55666", "Default value incorrect for property: " + compositePropNameA+"f");       
        
         //Verify property details are not modified
        PropertyRow propRow2 = mpp.getPropertyRowByName(compositePropNameA+"d");
        Assert.assertNotNull(propRow2);            
        Assert.assertEquals(propRow2.getDisplayLabel(),"double", "Display Label displayed correctly");       
        Assert.assertEquals(propRow2.getDatatype(),datatyped,"Data Type updated correctly");
        Assert.assertEquals(propRow2.getDefaultValue(),"0.55666", "Default value incorrect for property: " + compositePropNameA+"d");
        
         //Verify property details are not modified
        PropertyRow propRow3 = mpp.getPropertyRowByName(compositePropNameA+"i");
        Assert.assertNotNull(propRow3);            
        Assert.assertEquals(propRow3.getDisplayLabel(),"int", "Display Label displayed correctly");       
        Assert.assertEquals(propRow3.getDatatype(),datatypei,"Data Type updated correctly");
        Assert.assertEquals(propRow3.getDefaultValue(),"-5", "Default value incorrect for property: " + compositePropNameA+"i");
        
        //Verify property details are not modified
        PropertyRow propRow4 = mpp.getPropertyRowByName(compositePropNameA+"t");
        Assert.assertNotNull(propRow4);            
        Assert.assertEquals(propRow4.getDisplayLabel(),"Text", "Display Label displayed correctly");    
        Assert.assertEquals(propRow4.getDatatype(),datatypet,"Data Type updated correctly");
        Assert.assertEquals(propRow4.getDefaultValue(),"old", "Default value incorrect for property: " + compositePropNameA+"t");
        
        //Verify property details are not modified
        PropertyRow propRow5 = mpp.getPropertyRowByName(compositePropNameA+"date");
        Assert.assertNotNull(propRow5);            
        Assert.assertEquals(propRow5.getDisplayLabel(),"Date", "Display Label displayed correctly");    
        Assert.assertEquals(propRow5.getDatatype(),datatypedate,"Data Type updated correctly");
        Assert.assertEquals(propRow5.getDefaultValue(),dateValue, "Default value incorrect for property: " + compositePropNameA+"date");
    }
        
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=7)
    public void invalidTestEditPropForDeactivatedModelAspect() throws Exception
    {
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
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
        cmmActions.createAspect(driver, aspectName).render();                 
               
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
       
        cmmActions.viewProperties(driver, compositeAspectName);

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
                        
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+"7", "double", "double", DataType.Double, MandatoryClassifier.Optional, false, "-4.87689",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"7", "InvalidInt", "InvalidInt", DataType.Int, MandatoryClassifier.Mandatory, true, "-6", constraintDetails2).render();        
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render(); 
        
        //Edit property of draft model property with constraint and invalid  default value           
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"7", "Invalidfloat", "Invalidfloat", DataType.Float, MandatoryClassifier.Optional, true, "4567.8765", constraintDetails2).render();        
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();        
        
        //Edit property of draft model property with constraint and default value exceeding the limit         
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"7", "Invalidlong","Invalidlong", DataType.Long, MandatoryClassifier.Mandatory, true, "6", constraintDetails2).render();        
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //yet to implement
        //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"7", "Invalidtxt", "Invalidtxt", DataType.Text, MandatoryClassifier.Optional, true, "maxsix", constraintDetails3).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"7", "InvalidBoolean", "InvalidBoolean", DataType.Boolean, MandatoryClassifier.Mandatory, true, "inval", constraintDetails3).render();        
        cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();
        
        //Edit property of draft model property with constraint and default value exceeding the limit    
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"7", "Invaliddatetime", "Invaliddatetime", DataType.DateTime, MandatoryClassifier.Mandatory, true, "123453467", constraintDetails1).render();        
        mpp = cmmActions.closeShareDialogue(driver, EditPropertyPopUp.class).render();           
              
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameA+"7");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"double", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatyped,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"-4.87689", "Default value incorrect for property: " + compositePropNameA+"7");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");       
    }
          
    
    @AlfrescoTest
    @Test(groups = "EnterpriseOnly", priority=8, enabled=false)
    public void testJavaClassForAspect() throws Exception
    {
        String name = "name"+System.currentTimeMillis();
        String siteName = "site"+System.currentTimeMillis();
        String modelName = "model" + System.currentTimeMillis();
        String compositeAspectName = modelName + ":" + aspectName;        
        String compositePropNameA = modelName + ":" + propertyNameA;
        
        ContentDetails contentDetails =new ContentDetails();
        contentDetails.setName(name);
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
                
        cmmActions.viewProperties(driver, compositeAspectName);

        //Constraint: MinMaxValue
        ConstraintDetails constraintDetails1 = new ConstraintDetails();
        constraintDetails1.setType(ConstraintTypes.MINMAXVALUE);
        constraintDetails1.setMinValue(-5);
        constraintDetails1.setMaxValue(10);  
        
        // Add Property With Constraint: JavaClass
        ConstraintDetails constraintDetails2 = new ConstraintDetails();
        constraintDetails2.setType(ConstraintTypes.JAVACLASS);
        constraintDetails2.setValue("org.alfresco.extension.classconstraint.example.InvoiceConstraint");
        
        ConstraintDetails constraintDetails3 = new ConstraintDetails();
        constraintDetails3.setType(ConstraintTypes.None);

        //Create property for draft model with constraint
        cmmActions.createPropertyWithConstraint(driver, propertyNameA+8, "Int", "Int", DataType.Int, MandatoryClassifier.Optional, false, "-1",
                constraintDetails1).render();
        
        //Edit property of draft model property with constraint and default value       
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();        
        cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"8", "Long", "Long", DataType.Long, MandatoryClassifier.Mandatory, true, "-5", constraintDetails1).render();
        
        //set model active
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);        
               
        //Edit active model property with Java class constraint
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.editPropertyWithConstraintForAM(driver, compositeAspectName, compositePropNameA+"8","Javaclass","Javaclass","", constraintDetails2).render();
                       
        //Apply default form and save
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        //Create site and node in share       
        siteActions.createSite(driver, siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Apply Aspect to node in share        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);  
              
        //Apply type/aspect to node in share
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
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();   
        mpp = cmmActions.editPropertyWithConstraint(driver, compositeAspectName, compositePropNameA+"8", "boolean", "boolean", DataType.Boolean, MandatoryClassifier.Optional,true,"true",constraintDetails3).render();
        
        //Verify property details are updated
        PropertyRow propRow1 = mpp.getPropertyRowByName(compositePropNameA+"8");
        Assert.assertNotNull(propRow1);            
        Assert.assertEquals(propRow1.getDisplayLabel(),"boolean", "Display Label displayed correctly");          
        Assert.assertEquals(propRow1.getDatatype(),datatypeb,"Data Type updated correctly");
        Assert.assertEquals(propRow1.getDefaultValue(),"true", "Default value incorrect for property: " + compositePropNameA+"8");
        Assert.assertEquals(propRow1.getMandatory(),optional,"Mandatory displayed correctly");
        Assert.assertEquals(propRow1.getMultiValue(),"No","Multiple checked");                   
        
      }
    
    
}
