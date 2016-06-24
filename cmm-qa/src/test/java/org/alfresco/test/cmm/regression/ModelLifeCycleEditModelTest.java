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
 * Test Class to test Model Life Cycle Edit Model Test
 * 
 * @author Charu
 */

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.EditModelPopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
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
public class ModelLifeCycleEditModelTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(ModelLifeCycleTest.class);

    private String testName;

    public DashBoardPage dashBoardpage;

    private String testUser;
    private String modelActionEdit = "EDIT";


    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);

        testUser = username;
        
/*        
         //Login as Admin to create a new user 
         loginAs(driver, new String[] {username}); 
         testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
           
         //Create User and add to modelAdmin group 
         adminActions.createEnterpriseUserWithGroup(driver, testUser, testUser, testUser, testUser, DEFAULT_PASSWORD, modelAdmin );
          
         //Logout as admin 
         logout(driver);
*/
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
    
    //Verify able Edit draft model 
    //Verify not able to edit active model
    //Verify able to edit Deactivated model
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>     
     * <li>Edit all the fields of draft model</li>
     * <li>Verify model is edited</li>
     * <li>Activate model</li>
     * <li>Verify not able to edit activated model</li>
     * <li>Deactivate model</li>
     * <li>Edit deactivated model fields</li>
     * <li>Verify model is edited</li>
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "editdraftmodel")
    @Test(groups = "EnterpriseOnly", priority = 1)
    public void testEditDraftActiveModel() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName1 = "model" + testName;        
        String nameSpace = "nameSpace" + testName;
        String prefix = "prefix" + testName;       
        String nameSpace1 = "nameSpace1" + testName;
        String prefix1 = "prefix1" + testName;
        
       
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();

        //Edit draft model1 
        ModelManagerPage cmmpage = cmmActions.editModel(driver, modelName1, nameSpace, prefix).render();               
        Assert.assertEquals(cmmpage.getCustomModelRowByName(modelName1).getCmNamespace(), nameSpace, "Namespace edited");

        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, true).render();        
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
                
        // Deactivate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, false).render();        
                       
        // Edit Deactivated model
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName1, nameSpace1, prefix1).render();        
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName1).getCmNamespace(), nameSpace1, "Namespace edited");
        
       
    }
    
    //Verify able to edit model with with type holding parent type as any other model type
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Activate Model1</li>
     * <li>Create Model2</li>
     * <li>Create new type1 for model1</li>
     * <li>Verify not able to edit active model1 with default type</li>    
     * <li>Create new type2 for model2 with parent type as type1</li>
     * <li>Verify able to edit draft model2 with parent type as any other model type</li>      
     * <li>Activate Mode2</li>     
     * <li>Verify not able to edit activated model2</li>           
     * <li>Deactivate Model2</li>
     * <li>Verify able to edit deactivated model2 with type2</li>  
     * <li>Delete type2 in Model2</li>
     * <li>Verify able to edit Model2 after deleting type2</li>     
     * <li>Deactivate Mode11</li>
     * <li>Verify able to edit deactivated model1 with default type</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeedit")
    @Test(groups = "EnterpriseOnly", priority = 2)
    public void testEDAMWTInDiffM() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName1 = "model1" + testName;
        String modelName2 = "model2" + testName;
        
        String nameSpace1 = "nameSpace1" + testName;       
        String prefix1 = "prefix1" + testName;
        
        String nameSpace2 = "nameSpace2" + testName;   
        String prefix2 = "prefix2" + testName;    
               
        String typeName1 = "type1";
        String typeName2 = "type2";        
        
        String compositeTypeName1 = prefix1 + ":" + typeName1;
        String compositeTypeName2 = prefix2 + ":" + typeName2;
        
        String parentTypeName = compositeTypeName1 + " ("+ typeName1 + ")";
       
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();

        // Create Type1
        cmmActions.createType(driver, typeName1).render();
        
        //Verify able to edit draft model1 
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName1, nameSpace1, prefix1).render();               
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName1).getCmNamespace(), nameSpace1, "Namespace edited");
                
        // Activate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, true).render();
        
        //Verify not able to edit active model1 with default type
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
               
        //Create New Draft Model2
        cmmActions.createNewModel(driver, modelName2).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();

        //Create type2 with parent type as Type2
        cmmActions.createType(driver, typeName2, parentTypeName).render();       
        
        //Verify able to edit draft model2 with type parent type as model1:type1
        cmmActions.navigateToModelManagerPage(driver);               
        ModelManagerPage cmMpage = cmmActions.editModel(driver, modelName2, nameSpace2, prefix2).render(); 
        
        //Verify Name space modified in Model manager page
        Assert.assertEquals(cmMpage.getCustomModelRowByName(modelName2).getCmNamespace(), nameSpace2, "Namespace edited");
        
        // View Types and Aspects
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        //Verify type name prefix modified in Custom types and aspects page
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2));
        
        //Activate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName2, true).render();
        
        //Verify not able to edit activated model2
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
        
        //Deactivate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName2, false).render();
        
        //Delete type2
        cmmActions.navigateToModelManagerPage(driver); 
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        cmmActions.deleteType(driver, compositeTypeName2, "Delete").render();
        
        //verify able to Edit deactivated Model2 after deleting type2
        ModelManagerPage cmmpage = cmmActions.navigateToModelManagerPage(driver);        
        Assert.assertTrue(cmmpage.getCustomModelRowByName(modelName2).getCmActions().hasActionByName(modelActionEdit),"Edit action displayed");          
        
       
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Activate Model1</li>     
     * <li>Create new type1 for model1</li>       
     * <li>Create new type2 for model2 with parent type as type1</li>
     * <li>Verify not able to edit active model2 with parent type as any other type in same model </li>      
     * <li>Deactivate Model1     
     * <li>Verify able to edit deactivated model2</li>
     * <li>Verify edited name space is updated in model manager page</li> 
     * <li>Verify edited prefix is updated in model aspects and types page</li>    
     * <li>Delete type2</li>
     * <li>Delete type1</li>
     * <li>Verify able to edit Model2 after deleting types</li>     
     * <li>Deactivate Mode11</li>
     * <li>Verify able to edit deactivated model1 with default type</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeedit1")
    @Test(groups = "EnterpriseOnly", priority = 3)
    public void testEditDAMWTInSameModel() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        String nameSpace1 = "nameSpace1" + testName;       
        
        String prefix1 = "prefix1" + testName;
       //String prefix2 = "prefix2" + testName;
        
        String typeName1 = "type1";
        String typeName2 = "type2";        
        
        String compositeTypeName1 = modelName + ":" + typeName1;
        String compositeTypeName2 = prefix1 + ":" + typeName1;
        String compositeTypeName3 = prefix1 + ":" + typeName2;
        
        String parentTypeName = compositeTypeName1 + " ("+ typeName1 + ")";
       
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Create type1
        cmmActions.createType(driver, typeName1).render();
        
        // Activate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        
        //Verify not able to edit active model1
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName).getCmActions().hasActionByName(modelActionEdit));            
               
        //Create type2 with parent type as type1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.createType(driver, typeName2, parentTypeName).render();                
        
        //Deactivate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        
        //Verify able to edit deactivated model1     
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName, nameSpace1, prefix1).render();
        
        //Verify name space is modified in model manager page
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName).getCmNamespace(), nameSpace1, "Namespace edited");
       
        //Verify model type1 prefix updated in ManageTypesAndAspectsPage
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2));
        
        //Verify model type2 prefix updated in ManageTypesAndAspectsPage
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        Assert.assertTrue(managetypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName3));              
               
        //Delete type2        
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.deleteType(driver , compositeTypeName3, "Delete").render();
        
        //Delete type1        
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.deleteType(driver, compositeTypeName2, "Delete").render();
        
        //Verify able to Edit deactivated Model2 after deleting type2
        ModelManagerPage cmmpage = cmmActions.navigateToModelManagerPage(driver);        
        Assert.assertTrue(cmmpage.getCustomModelRowByName(modelName).getCmActions().hasActionByName(modelActionEdit),"Edit action displayed");          
               
    }
    
    //Verify not able to Edit a model with type using duplicate name space and prefix
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Create type1 for model1</li>
     * <li>create property1 for type1 </li>     
     * <li>Verify able to edit draft model1 </li>       
     * <li>Activate Model1 </li>
     * <li>Verify not able to edit active model1</li>      
     * <li>Delete property</li>      
     * <li>Deactivate Model1</li>
     * <li>Verify able to edit active model1</li>      
     * <li>Create New Draft Model2</li>
     * <li>Create Type2</li>  
     * <li>Verify not able to edit draft model2 with duplicate name space and prefix</li>  
     * <li>Verify name space not modified in model manager page</li>
     * <li>Verify prefix not modified in Manage types and aspects page</li>
     **/
    @AlfrescoTest(testlink = "tobeedit2")
    @Test(groups = "EnterpriseOnly", priority = 4)
    public void testEDAMWPT() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName1 = "model1" + testName;
        String modelName2 = "model2" + testName;
        
        String nameSpace1 = "nameSpace1" + testName;       
        String prefix1 = "prefix1" + testName;      
                
        String property1 ="p1" + getUniqueTestName();  
               
        String typeName1 = "type1";
        String typeName2 = "type2";        
        
        String compositeTypeName1 = modelName1 + ":" + typeName1;
        String compositeTypeName2 = modelName2 + ":" + typeName2;        
              
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();

        // Create Type1
        cmmActions.createType(driver, typeName1).render();
        
        //Create property1 for type1 
        cmmActions.viewProperties(driver, compositeTypeName1).render();
        cmmActions.createProperty(driver, property1).render();    
        
        //Verify able to edit draft model1 
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName1, nameSpace1, prefix1).render();
        
        //Verify name space updated in model manager page
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName1).getCmNamespace(), nameSpace1, "Namespace edited");
                
        // Activate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, true).render();
        
        //Verify not able to edit active moel1
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
               
        //Delete property
        
        //Deactivate Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, false).render();
        
        //Verify able to edit Deactivated Model1 after deleting property
        Assert.assertTrue(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
               
        //Create New Draft Model2
        cmmActions.createNewModel(driver, modelName2).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();

        //Create Type2
        cmmActions.createType(driver, typeName2).render();
        
        //Verify not able to edit draft model2 with duplicate name space and prefix
        cmmActions.navigateToModelManagerPage(driver);
        
        //Verify name space not modified in model manager page
        cmmActions.editModel(driver, modelName2, nameSpace1, prefix1);
        
        // Expect Error: EditModelPopUp displayed        
        cmmPage = cmmActions.closeShareDialogue(driver, EditModelPopUp.class).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName2).getCmNamespace(),modelName2 , "Namespace edited");
        
        // Verify prefix not modified in Manage types and aspects page
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName2));       
               
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Activate Model1</li>
     * <li>Create Model2</li>
     * <li>Create new aspect1 for model1</li>
     * <li>Verify not able to edit active model1 with default aspect</li>    
     * <li>Create new aspect2 for model2 with parent aspect as aspect1</li>
     * <li>Verify able to edit draft model2 with parent aspect as any other model aspect</li>      
     * <li>Activate Mode2</li>     
     * <li>Verify not able to edit activated model2</li>           
     * <li>Deactivate Model2</li>
     * <li>Verify able to edit deactivated model2 with aspect2</li>  
     * <li>Delete aspect2 in Model2</li>
     * <li>Verify able to edit Model2 after deleting aspect2</li>     
     * <li>Deactivate Mode11</li>
     * <li>Verify able to edit deactivated model1 with default aspect</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeedit3")
    @Test(groups = "EnterpriseOnly", priority = 5)
    public void testEDAMWAInDiffM() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName1 = "model1" + testName;
        String modelName2 = "model2" + testName;
        
        String nameSpace1 = "nameSpace1" + testName;       
        String prefix1 = "prefix1" + testName;
        
        String nameSpace2 = "nameSpace2" + testName;   
        String prefix2 = "prefix2" + testName;    
               
        String aspectName1 = "aspect1";
        String aspectName2 = "aspect2";        
        
        String compositeaspectName1 = prefix1 + ":" + aspectName1;
        String compositeaspectName2 = prefix2 + ":" + aspectName2;
        
        String parentaspectName = compositeaspectName1 + " ("+ aspectName1 + ")";
       
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();

        // Add aspect
        cmmActions.createAspect(driver, aspectName1).render();
        
        //Verify able to edit draft model1 
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName1, nameSpace1, prefix1).render();               
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName1).getCmNamespace(), nameSpace1, "Namespace edited");
                
        // Activate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, true).render();
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
               
        //Create New Draft Model2
        cmmActions.createNewModel(driver, modelName2).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();

        // Add aspects
        cmmActions.createAspect(driver, aspectName2, parentaspectName).render();       
        
        //Verify able to edit draft model2 with aspect using parent aspect as model1:aspect1
        cmmActions.navigateToModelManagerPage(driver);               
        ModelManagerPage cmMpage = cmmActions.editModel(driver, modelName2, nameSpace2, prefix2).render();               
        Assert.assertEquals(cmMpage.getCustomModelRowByName(modelName2).getCmNamespace(), nameSpace2, "Namespace edited");
        
        // View Types and Aspects
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2));
        
        //Activate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName2, true).render();
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
        
        //Deactivate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName2, false).render();
        
        //Delete aspect2
        cmmActions.navigateToModelManagerPage(driver); 
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        cmmActions.deleteAspect(driver, compositeaspectName2, "Delete").render();
        
        //verify able to Edit deactivated Model2 after deleting aspect2
        ModelManagerPage cmmpage = cmmActions.navigateToModelManagerPage(driver);        
        Assert.assertTrue(cmmpage.getCustomModelRowByName(modelName2).getCmActions().hasActionByName("Edit"),"Edit action displayed");          
        
       
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Activate Model1</li>     
     * <li>Create new aspect1 for model1</li>       
     * <li>Create new aspect2 for model2 with parent aspect as aspect1</li>
     * <li>Verify not able to edit active model2 with parent aspect as any other aspect in same model </li>      
     * <li>Deactivate Model1     
     * <li>Verify able to edit deactivated model2</li>
     * <li>Verify edited name space is updated in model manager page</li> 
     * <li>Verify edited prefix is updated in model aspects and types page</li>      
     * <li>Delete aspect2</li>
     * <li>Delete aspect1</li>
     * <li>Verify able to edit Model2 after deleting aspects</li>     
     * <li>Deactivate Mode11</li>
     * <li>Verify able to edit deactivated model1 with default aspect</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeedit4")
    @Test(groups = "EnterpriseOnly", priority = 6)
    public void testEditDAMWAInSameModel() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        String nameSpace1 = "nameSpace1" + testName;       
        
        String prefix1 = "prefix1" + testName;       
        
        String aspectName1 = "aspect1";
        String aspectName2 = "aspect2";        
        
        String compositeaspectName1 = modelName + ":" + aspectName1;
        String compositeaspectName2 = prefix1 + ":" + aspectName1;
        String compositeaspectName3 = prefix1 + ":" + aspectName2;
        
        String parentaspectName = compositeaspectName1 + " ("+ aspectName1 + ")";
       
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Create aspect1
        cmmActions.createAspect(driver, aspectName1).render();
        
        // Activate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        
        //Verify not able to edit active model1
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName).getCmActions().hasActionByName(modelActionEdit));            
               
        //Create aspect2 with parent aspect as aspect1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.createAspect(driver, aspectName2, parentaspectName).render();                
        
        //Deactivate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        
        //Verify able to edit deactivated model1     
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName, nameSpace1, prefix1).render();               
        
        //Verify name space updated in Model manager page
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName).getCmNamespace(), nameSpace1, "Namespace edited");
       
        //Verify model aspect1 prefix updated in ManageTypesAndAspectsPage
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2));
        
        //Verify model aspect1 prefix updated in ManageTypesAndAspectsPage
        ManageTypesAndAspectsPage managetypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        Assert.assertTrue(managetypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName3));             
        
        //Delete aspect2        
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.deleteAspect(driver , compositeaspectName3, "Delete").render();
        
        //Delete aspect1        
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.deleteAspect(driver, compositeaspectName2, "Delete").render();        
        
        //verify able to Edit deactivated Model2 after deleting aspect2
        ModelManagerPage cmmpage = cmmActions.navigateToModelManagerPage(driver);        
        Assert.assertTrue(cmmpage.getCustomModelRowByName(modelName).getCmActions().hasActionByName(modelActionEdit),"Edit action displayed");          
               
    }    
   
    //Verify not able to Edit a model with aspect using duplicate name space and prefix
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>
     * <li>Create aspect1 for model1</li>
     * <li>create property1 for aspect1 </li>     
     * <li>Verify able to edit draft model1 </li>       
     * <li>Activate Model1 </li>
     * <li>Verify not able to edit active model1</li>
     * <li>Delete property</li>      
     * <li>Deactivate Model1</li>
     * <li>Verify able to edit deactivated model1</li>     
     * <li>Create New Draft Model2</li>
     * <li>Create aspect2</li>  
     * <li>Verify not able to edit draft model2 with duplicate name space and prefix</li>  
     * <li>Verify name space not modified in model manager page</li>
     * <li>Verify prefix not modified in Manage types and aspects page</li>
     **/
    @AlfrescoTest(testlink = "tobeedit5")
    @Test(groups = "EnterpriseOnly", priority = 7)
    public void testEDAMWPA() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName1 = "model1" + testName;
        String modelName2 = "model2" + testName;
        
        String nameSpace1 = "nameSpace1" + testName;       
        String prefix1 = "prefix1" + testName;      
                
        String property1 ="p1" + getUniqueTestName();  
               
        String aspectName1 = "aspect1";
        String aspectName2 = "aspect2";        
        
        String compositeaspectName1 = modelName1 + ":" + aspectName1;
        String compositeaspectName2 = modelName2 + ":" + aspectName2;        
              
        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();

        // Add aspects
        cmmActions.createAspect(driver, aspectName1).render();
        
        //Create property 
        cmmActions.viewProperties(driver, compositeaspectName1).render();
        cmmActions.createProperty(driver, property1).render();    
        
        //Verify able to edit draft model1 
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmMPage = cmmActions.editModel(driver, modelName1, nameSpace1, prefix1).render();               
        Assert.assertEquals(cmMPage.getCustomModelRowByName(modelName1).getCmNamespace(), nameSpace1, "Namespace edited");
                
        // Activate Model1 
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, true).render();
        Assert.assertFalse(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
        
        //Deactivate Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName1, false).render();
        
        //Verify able to edit deactivated model1 after deleting property1
        Assert.assertTrue(cmmPage.getCustomModelRowByName(modelName1).getCmActions().hasActionByName(modelActionEdit));
               
        //Create New Draft Model2
        cmmActions.createNewModel(driver, modelName2).render();      
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();

        // Create aspect2
        cmmActions.createAspect(driver, aspectName2).render();
        
        //Verify not able to edit draft model2 with duplicate name space and prefix
        cmmActions.navigateToModelManagerPage(driver);
        
        //Verify name space not modified in model manage page
        cmmActions.editModel(driver, modelName2, nameSpace1, prefix1);
        
        // Expect Error: EditModelPopUp displayed        
        cmmPage = cmmActions.closeShareDialogue(driver, EditModelPopUp.class).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName2).getCmNamespace(),modelName2 , "Namespace edited");
        
        // Verify prefix not modified in Manage types and aspects page
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeaspectName2));          
               
    }

    

}
