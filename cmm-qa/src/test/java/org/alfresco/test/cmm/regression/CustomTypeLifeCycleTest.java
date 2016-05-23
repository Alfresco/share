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
 * Test Class to test CustomTypeLifeCycle
 * 
 * @author Charu 
 */
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class CustomTypeLifeCycleTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(CustomTypeLifeCycleTest.class);
    
    private String testName;
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        
        String testUser = getUserNameFreeDomain(testName+ System.currentTimeMillis());
        
        //Login as Admin to create a new user
        loginAs(driver, new String[] {username});
        
        //Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, testUser, testUser, testUser, testUser+"@test.com", DEFAULT_PASSWORD, modelAdmin );
        
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

    //This test is to verify not able to delete type of active model with parent type
    //as any other model type. 
    //Able to Delete type only after deactivating the model
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Create Model2</li>
     * <li>Activate a Model1</li>
     * <li>Create a type1 for Model1</li>     
     * <li>Activate Model2</li>
     * <li>Create type2 for active Model2 with parent type as type1</li>
     * <li>Verify not able to delete type2 of active model2</li>    
     * <li>Deactivate Model2</li   
     * <li>Verify able to delete type2 of deactivated model2</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tadam1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testCTypeWithPTInDiffModelADel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();
        String modelName2 = "model2" + getUniqueTestName();         
               
        String typeName1 = "type";   
        String typeName2 = "type";            
               
        String compositeTypeName1 = modelName1+":"+typeName1;   
        String compositeTypeName2 = modelName2+":"+typeName2;        
        String parentTypeName = compositeTypeName1 + " ("+ typeName1 + ")";       

        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        cmmActions.createNewModel(driver, modelName1).render();        
        
        //Activate model1
        cmmActions.setModelActive(driver, modelName1, true).render();       
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type1        
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createType(driver, typeName1).render(); 
        
        //verify type displayed        
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row disaplayed");  
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Verify not able to delete type1 of active model1 (which is not used)
        manageTypesAndAspectsPage = cmmActions.deleteType(driver , compositeTypeName1, "Delete").render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom type Row disaplayed");   
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model2
        cmmActions.createNewModel(driver, modelName2).render();
        
        //Activate Model2       
        cmmActions.setModelActive(driver, modelName2, true).render();    
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        // Add Type2 for active Model2 with parent type as type1 from model1  
        ManageTypesAndAspectsPage manageTypesandAspectsPage = cmmActions.createType(driver, typeName2, parentTypeName).render();
        
        //verify type displayed
        Assert.assertTrue(manageTypesandAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row is disaplayed");
                              
        // View Types and Aspects
        manageTypesandAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        //Verify not able to delete type2 of active model2
        manageTypesandAspectsPage = cmmActions.deleteType(driver , compositeTypeName2, "Delete").render();
        Assert.assertTrue(manageTypesandAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");
        
        //Deactivate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName2, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
                     
        //Verify able to delete type2 of deactivated model2
        manageTypesandAspectsPage = cmmActions.deleteType(driver , compositeTypeName2, "Delete").render();
        Assert.assertFalse(manageTypesandAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");        
             
        //Deactivate Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //Verify able to delete type1 of deactivated model1 when not referenced to other model
        ManageTypesAndAspectsPage managetypesandaspectspage = cmmActions.deleteType(driver , compositeTypeName1, "Delete").render();
        Assert.assertFalse(managetypesandaspectspage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");           
            
        
    }
    
    //SHA-679
   // verify not able to delete a type which is referenced as parent type to any other type with in same deactivated model
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>     
     * <li>Activate a Model1</li>
     * <li>Create a type for Model1</li>
     * <li>Create type two for Model1 with parent type as type1</li> 
     * <li>Verify not able to delete type1 which is referenced by type2 in active model1</li> 
     * <li>Deactivate model1</li> 
     * <li>verify not able to delete type1 which is referenced by type2 with in same deactivated model1</li>
     * <li>verify able to delete type2 </li>
     * <li>verify able to delete type1 which is not referenced by type2 with in same deactivated model1</li>
     *     
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tadam2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testCTypeWithPTInSameModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();       

        String typeName1 = "type1";      
        String typeName2 = "type2";     
              
        String compositeTypeName1 = modelName1+":"+typeName1;
        String compositeTypeName2 = modelName1+":"+typeName2;
        String parentTypeName = compositeTypeName1 + " ("+ typeName1 + ")";     
        
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));
        
        //Activate model1
        //cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, true).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type1        
        cmmActions.createType(driver, typeName1).render();   
                   
        // Add Type2        
        cmmActions.createType(driver, typeName2, parentTypeName).render(); 
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();                        
        
        //Verify not able to delete type1 which is referenced to type2 with in active model1     
        ManageTypesAndAspectsPage managetypesandaspectsPage = cmmActions.deleteType(driver , compositeTypeName1, "Delete").render();
        Assert.assertTrue(managetypesandaspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");   
                
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //verify cant delete type1 which is referenced by type2 within same deactivated model1     
        managetypesandaspectsPage = cmmActions.deleteType(driver , compositeTypeName1, "Delete").render();
        Assert.assertTrue(managetypesandaspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");                
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //verify able to delete type2      
        managetypesandaspectsPage = cmmActions.deleteType(driver , compositeTypeName2, "Delete").render();
        Assert.assertFalse(managetypesandaspectsPage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");
             
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // verify type1 can be deleted after deleting type2   
        managetypesandaspectsPage = cmmActions.deleteType(driver , compositeTypeName1, "Delete").render();
        Assert.assertFalse(managetypesandaspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");       
        
    }        
        
    //Verify not able to Create duplicate type for active model
    
    @AlfrescoTest(testlink="tadam3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testCreateDuplicateTypeForActiveModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();                
               
        String typeName1 = "type";           
                     
        String compositeTypeName1 = modelName1+":"+typeName1;
        
        String parentTypeName = "cm:thumbnail (Thumbnail)";       
       
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));
        
        //Activate model1         
        cmmActions.setModelActive(driver, modelName1, true).render();       
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type1        
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createType(driver, typeName1).render();   
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();       
        
        //Verify size is incremented by 1 after creating a type
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelTypeRows().size());
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Create duplicate type2 with same type1 name and choosing different parent type
        cmmActions.createType(driver, typeName1,parentTypeName);
        
        // Expected Error: CreateNewCustomTypePopUp Displayed
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.closeShareDialogue(driver, CreateNewCustomTypePopUp.class).render();
        Assert.assertTrue(managetypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");
        
        //Verify parent type is not changed for existing type1
        managetypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        Assert.assertFalse(managetypesAndAspectsPage.getCustomModelTypeRowByName(compositeTypeName1).getParent().equalsIgnoreCase(parentTypeName),"parenttype not modified" );
        
        //Verify size is not incremented by 1 after creating a duplicate type
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelTypeRows().size()); 
          
    
    }
    
    //Not able to Create duplicate type for draft model 
    @AlfrescoTest(testlink="tadam4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testCreateDuplicateTypeForDraftModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();            
               
        String typeName1 = "type";   
        
        String compositeTypeName1 = modelName1+":"+typeName1;  
       
        String parentTypeName = "cm:thumbnail (Thumbnail)";       

        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));   
                       
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();     
               
        // Add Type1 for draft Model1  
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createType(driver, typeName1).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");
        
        //Verify size is incremented by 1 after creating a type
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelTypeRows().size());
        
        //Create duplicate type2
        cmmActions.createType(driver, typeName1,parentTypeName);
        
        // Expected Error: CreateNewCustomTypePopUp Displayed
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.closeShareDialogue(driver, CreateNewCustomTypePopUp.class).render();
        Assert.assertTrue(managetypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");
        
        //Verify parent type is not changed for existing type1
        managetypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        Assert.assertFalse(managetypesAndAspectsPage.getCustomModelTypeRowByName(compositeTypeName1).getParent().equalsIgnoreCase(parentTypeName),"parenttype not modified" );
        
        //Verify size is not incremented by 1 after creating a duplicate type
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelTypeRows().size());          
    
    }
    
    /**Create type1 with type name same as model name for draft model
    *delete type1
    *Create new type with deleted type1 name 
    *create type2 with type name same as property name for draft model
    *delete type2
    *create new type with deleted type2 name
    */
    
    @AlfrescoTest(testlink="tadam5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testCTWMNPNFDM() throws Exception
    {
        String modelName1 = "m1" + getUniqueTestName();         
               
        String typeName1 = modelName1; 
        String property1 ="p1" + getUniqueTestName();       
        
        String compositeTypeName1 = modelName1+":"+typeName1;
        String compositeTypeName2 = modelName1+":"+property1;             
               
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));     
               
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type1 with type name same as model name       
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createType(driver, typeName1).render();   
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //verify able to Delete type1         
        ManageTypesAndAspectsPage managetypesAndaspectsPage = cmmActions.deleteType(driver , compositeTypeName1, "Delete").render();
        Assert.assertFalse(managetypesAndaspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");
             
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Verify able to create type with deleted type1 name
        ManageTypesAndAspectsPage manageTypesandAspectsPage = cmmActions.createType(driver, typeName1).render();   
        Assert.assertTrue(manageTypesandAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");
               
        //Create property 
        cmmActions.viewProperties(driver, compositeTypeName1).render();
        cmmActions.createProperty(driver, property1).render();    
                
        //view types and aspects page
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type2 with same name as property name       
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.createType(driver, property1).render();   
        Assert.assertTrue(managetypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2),"Type displayed successfully"); 
              
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Delete type2      
        managetypesAndAspectsPage = cmmActions.deleteType(driver , compositeTypeName2, "Delete").render();
        Assert.assertFalse(managetypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");
        
        //create type with deleted type name    
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        ManageTypesAndAspectsPage managetypesandAspectsPage = cmmActions.createType(driver, property1).render();   
        Assert.assertTrue(managetypesandAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2),"Type displayed successfully"); 
    }
    
    
    /**
     * Create type1 with type name same as model name for Active model  
     * create type2 with type name same as property name for active model
     * @throws Exception
     */    
       
    @AlfrescoTest(testlink="tadam6")
    @Test(groups = "EnterpriseOnly", priority=6)
    public void testCTWMNPNFAM() throws Exception
    {
        String modelName1 = "m1" + getUniqueTestName();         
               
        String typeName1 = modelName1;
        String property1 ="p1" + getUniqueTestName();       
        
        String compositeTypeName1 = modelName1+":"+typeName1;
        String compositeTypeName2 = modelName1+":"+property1;       
                
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));        
       
        //Activate model1
        cmmActions.setModelActive(driver, modelName1, true).render(); 
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type1 with type name same as model1 name       
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createType(driver, typeName1).render();   
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1),"Type displayed successfully");     
                 
        //Create property for type1
        cmmActions.viewProperties(driver, compositeTypeName1).render();
        cmmActions.createProperty(driver, property1).render();    
                
        //view types and aspects page
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type2 with same name as property name       
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.createType(driver, property1).render();   
        Assert.assertTrue(managetypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2),"Type displayed successfully");          
      
        
    } 
    
    
    
}
    




    
    
    

