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
 * Test Class to test AspectLifeCycle
 * 
 * @author Charu 
 */
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
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
public class AspectLifeCycleTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(AspectLifeCycleTest.class);
    
    private String testName;
    private String parentModelName = "parent" + System.currentTimeMillis();
    private String aspectName = "Aspect" + System.currentTimeMillis();
    private String parentPropertyGroup = getParentTypeAspectName(parentModelName, aspectName);
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        
        setupParentModel();
    }
    
    private void setupParentModel() throws Exception
    {
        loginAs(driver, new String[] { username });
        
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        // SHA-1103: Create another Model, Aspect to be used as Parent Type Group later
        cmmActions.createNewModel(driver, parentModelName);

        cmmPage = cmmActions.setModelActive(driver, parentModelName, true).render();

        cmmPage.selectCustomModelRowByName(parentModelName).render();

        cmmActions.createAspect(driver, aspectName).render();
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

    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Create Model2</li>
     * <li>Activate a Model1</li>
     * <li>Create a aspect1 for Model1</li>     
     * <li>Activate Model2</li>
     * <li>Create aspect2 for active Model2 with parent aspect as aspect1</li>
     * <li>Verify not able to delete aspect2 of active model2</li>    
     * <li>Deactivate Model2</li   
     * <li>Verify able to delete aspect2 of deactivated model2</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tasdam1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testCaspectWithPAInDiffModelADel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();
        String modelName2 = "model2" + getUniqueTestName();         
               
        String aspectName1 = "aspect";   
        String aspectName2 = "aspect";            
               
        String compositeaspectName1 = modelName1+":"+aspectName1;   
        String compositeaspectName2 = modelName2+":"+aspectName2;        
        String parentaspectName = compositeaspectName1 + " ("+ aspectName1 + ")";       

        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        cmmActions.createNewModel(driver, modelName1).render();        
        
        //Activate model1
        cmmActions.setModelActive(driver, modelName1, true).render();       
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add aspect1        
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createAspect(driver, aspectName1).render(); 
        
        //verify aspect displayed        
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group Row disaplayed"); 
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Verify not able to delete aspect1 of active model1 (which is not used)
        manageTypesAndAspectsPage = cmmActions.deleteAspect(driver , compositeaspectName1, "Delete").render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group Row disaplayed");   
        
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model2
        cmmActions.createNewModel(driver, modelName2).render();
        
        //Activate Model2       
        cmmActions.setModelActive(driver, modelName2, true).render();    
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        // Add aspect2 for active Model2 with parent aspect as aspect1 from model1  
        ManageTypesAndAspectsPage manageTypesandAspectsPage = cmmActions.createAspect(driver, aspectName2, parentaspectName).render();
        
        //verify aspect displayed
        Assert.assertTrue(manageTypesandAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2), "Property Group Row disaplayed");
                              
        // View Types and Aspects
        manageTypesandAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        //Verify not able to delete aspect2 of active model2
        manageTypesandAspectsPage = cmmActions.deleteAspect(driver , compositeaspectName2, "Delete").render();
        Assert.assertTrue(manageTypesandAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2), "Property Group Row disaplayed");        
              
        
        //Deactivate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName2, false).render();
        
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
                     
        //Verify able to delete aspect2 of deactivated model2
        manageTypesandAspectsPage = cmmActions.deleteAspect(driver , compositeaspectName2, "Delete").render();
        Assert.assertFalse(manageTypesandAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2), "Property Group Row not disaplayed");        
             
        //Deactivate Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //Verify able to delete aspect of deactivated model1 when not referenced to other model
        ManageTypesAndAspectsPage managetypesandaspectspage = cmmActions.deleteAspect(driver , compositeaspectName1, "Delete").render();
        Assert.assertFalse(managetypesandaspectspage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group Row not disaplayed");           
            
        
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>     
     * <li>Activate a Model1</li>
     * <li>Create a aspect for Model1</li>
     * <li>Create aspect two for Model1 with parent aspect as aspect1</li> 
     * <li>Verify not able to delete aspect1 which is referenced by aspect2 in active model1</li> 
     * <li>Deactivate model1</li> 
     * <li>verify not able to delete aspect1 which is referenced by aspect2 with in same deactivated model1</li>
     * <li>verify able to delete aspect2 </li>
     * <li>verify able to delete aspect1 which is not referenced by aspect2 with in same deactivated model1</li>
     *     
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tasdam2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testCAWPTInSameModel() throws Exception
    {
        String modelName1 = "m1" + getUniqueTestName();       

        String aspectName1 = "a1";      
        String aspectName2 = "a2";     
              
        String compositeaspectName1 = modelName1+":"+aspectName1;
        String compositeaspectName2 = modelName1+":"+aspectName2;
        String parentaspectName = compositeaspectName1 + " ("+ aspectName1 + ")";     
        
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
        
        // Add aspect1        
        cmmActions.createAspect(driver, aspectName1).render();   
                   
        // Add aspect2        
        cmmActions.createAspect(driver, aspectName2, parentaspectName).render(); 
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();                        
        
        //Verify not able to delete aspect1 which is referenced to aspect2 with in active model1    
        ManageTypesAndAspectsPage managetypesandaspectsPage = cmmActions.deleteAspect(driver , compositeaspectName1, "Delete").render();
        Assert.assertTrue(managetypesandaspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group Row disaplayed");   
                
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //verify can't delete aspect1 which is referenced by aspect2 within same deactivated model1     
        managetypesandaspectsPage = cmmActions.deleteAspect(driver , compositeaspectName1, "Delete").render();
        Assert.assertTrue(managetypesandaspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group Row disaplayed");                
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //verify able to delete aspect2      
        ManageTypesAndAspectsPage manageTypesandaspectsPage = cmmActions.deleteAspect(driver , compositeaspectName2, "Delete").render();        
        Assert.assertFalse(manageTypesandaspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2), "Property Group Row not displayed");
             
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // verify aspect1 can be deleted   
        managetypesandaspectsPage = cmmActions.deleteAspect(driver , compositeaspectName1, "Delete").render();
        Assert.assertFalse(managetypesandaspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group Row not displayed");       
        
    }        
        
    //Verify not able to Create duplicate aspect for active model
    
    @AlfrescoTest(testlink="tasdam3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testCreateDuplicateAspForActiveModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();                
               
        String aspectName1 = "aspect";           
                     
        String compositeaspectName1 = modelName1+":"+aspectName1;
       
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));
        
        //Activate model1         
        cmmActions.setModelActive(driver, modelName1, true).render();       
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add aspect1        
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createAspect(driver, aspectName1).render();   
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();       
        
        //Verify size is incremented by 1 after creating a aspect
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelPropertyGroupRows().size());
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Create duplicate aspect2 with same aspect1 name and choosing different parent aspect
        cmmActions.createAspect(driver, aspectName1,parentPropertyGroup);
        
        // Expect Error: CreateNewPropertyGroupPopUp displayed        
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.closeShareDialogue(driver, CreateNewPropertyGroupPopUp.class).render();
        Assert.assertTrue(managetypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");
        
        //Verify parent aspect is not changed for existing aspect1
        managetypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        Assert.assertFalse(managetypesAndAspectsPage.getCustomModelPropertyGroupRowByName(compositeaspectName1).getParent().equalsIgnoreCase(parentPropertyGroup),"parenttype not modified" );
        
        //Verify size is not incremented by 1 after creating a duplicate aspect
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelPropertyGroupRows().size()); 
          
    
    }
    
    //Not able to Create duplicate aspect for draft model 
    @AlfrescoTest(testlink="tasdam4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testCreateDuplicateAForDraftModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();            
               
        String aspectName1 = "aspect";   
        
        String compositeaspectName1 = modelName1+":"+aspectName1;      

        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));   
                       
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();     
               
        // Add aspect1 for draft Model1  
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createAspect(driver, aspectName1).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");
        
        //Verify size is incremented by 1 after creating a aspect
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelPropertyGroupRows().size());
        
        //Create duplicate aspect2
        cmmActions.createAspect(driver, aspectName1,parentPropertyGroup);
        
        // Expect Error: CreateNewPropertyGroupPopUp displayed        
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.closeShareDialogue(driver, CreateNewPropertyGroupPopUp.class).render();
        Assert.assertTrue(managetypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");
        
        //Verify parent aspect is not changed for existing aspect1
        managetypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        Assert.assertFalse(managetypesAndAspectsPage.getCustomModelPropertyGroupRowByName(compositeaspectName1).getParent().equalsIgnoreCase(parentPropertyGroup),"parent Group not modified" );
        
        //Verify size is not incremented by 1 after creating a duplicate aspect
        Assert.assertTrue(1 == manageTypesAndAspectsPage.getCustomModelPropertyGroupRows().size());          
    
    }
    
    /**Create aspect1 with aspect name same as model name for draft model
    *delete aspect1
    *Create new aspect with deleted aspect1 name 
    *create aspect2 with aspect name same as property name for draft model
    *delete aspect2
    *create aspect3 with deleted aspect name
    */
    
    @AlfrescoTest(testlink="tasdam5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testCTWMNPNFDM() throws Exception
    {
        String modelName1 = "m1" + getUniqueTestName();         
               
        String aspectName1 = modelName1; 
        String property1 ="p1" + getUniqueTestName();       
        
        String compositeaspectName1 = modelName1+":"+aspectName1;
        String compositeaspectName2 = modelName1+":"+property1;
        //String compositePropName1 = modelName1+":"+property1;     
               
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));     
               
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add aspect1 with aspect name same as model name       
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createAspect(driver, aspectName1).render();   
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //verify able to Delete aspect1         
        ManageTypesAndAspectsPage managetypesAndaspectsPage = cmmActions.deleteAspect(driver , compositeaspectName1, "Delete").render();
        Assert.assertFalse(managetypesAndaspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1), "Property Group not disaplayed");
             
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Verify able to create aspect with deleted aspect1 name
        ManageTypesAndAspectsPage manageTypesandAspectsPage = cmmActions.createAspect(driver, aspectName1).render();   
        Assert.assertTrue(manageTypesandAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");
               
        //Create property 
        cmmActions.viewProperties(driver, compositeaspectName1).render();
        cmmActions.createProperty(driver, property1).render();    
                
        //view types and aspects page
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add aspect2 with same name as property name       
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.createAspect(driver, property1).render();   
        Assert.assertTrue(managetypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2),"Property Group displayed successfully"); 
              
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Delete aspect2      
        managetypesAndAspectsPage = cmmActions.deleteAspect(driver , compositeaspectName2, "Delete").render();
        Assert.assertFalse(managetypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2), "Property Group Row not disaplayed");
        
        //create aspect with deleted aspect name    
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        ManageTypesAndAspectsPage managetypesandAspectsPage = cmmActions.createAspect(driver, property1).render();   
        Assert.assertTrue(managetypesandAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2),"Property Group displayed successfully"); 
    }
    
    
    /*Create aspect1 with aspect name same as model name for Active model  
    create aspect2 with aspect name same as property name for active model*/
       
    @AlfrescoTest(testlink="tasdam6")
    @Test(groups = "EnterpriseOnly", priority=6)
    public void testCTWMNPNFAM() throws Exception
    {
        String modelName1 = "m1" + getUniqueTestName();         
               
        String aspectName1 = modelName1;
        String property1 ="p1" + getUniqueTestName();       
        
        String compositeaspectName1 = modelName1+":"+aspectName1;
        String compositeaspectName2 = modelName1+":"+property1;       
                
        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1));        
       
        //Activate model1
        cmmActions.setModelActive(driver, modelName1, true).render(); 
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add aspect1 with aspect name same as model1 name       
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.createAspect(driver, aspectName1).render();   
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName1),"Property Group displayed successfully");     
                 
        //Create property for aspect1
        cmmActions.viewProperties(driver, compositeaspectName1).render();
        cmmActions.createProperty(driver, property1).render();    
                
        //view types and aspects page
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add aspect2 with same name as property name       
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.createAspect(driver, property1).render();   
        Assert.assertTrue(managetypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2),"Property Group displayed successfully");          
      
        
    } 
    
    
    
}
    
