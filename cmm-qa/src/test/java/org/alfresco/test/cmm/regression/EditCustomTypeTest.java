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
 * Test Class to test EditCustomTypeTest
 * 
 * @author Charu 
 */
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
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
public class EditCustomTypeTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(EditCustomTypeTest.class);
       
    private String modelName = "model" + System.currentTimeMillis();
    
    private String typeName = "type"+ System.currentTimeMillis();
    
    String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);     
        
/*        
        testUser = getUserNameFreeDomain(testName+ System.currentTimeMillis());
        
        //Login as Admin to create a new user
        loginAs(driver, new String[] {username});
        
        //Create User and add to modelAdmin group
<<<<<<< .working
        adminActions.createEnterpriseUserWithGroup(driver, userInfo, userInfo, userInfo, userInfo, DEFAULT_PASSWORD, modelAdmin );        
=======
        adminActions.createEnterpriseUserWithGroup(drone, testName, testName, testName, testName, DEFAULT_PASSWORD, modelAdmin );        
>>>>>>> .merge-right.r110971
        
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

    //This test is to edit type which is referenced by any other model type    
    
    /**
     * Test:
     * <ul>
     * <li>Create New Model1</li>
     * <li>Activate model1</li>
     * <li>Activate a Model1</li>
     * <li>Create a type1 for Model1</li>
     * <li>Create New Model2</li>     
     * <li>Add Type2 for Model2 with default parent type </li>
     * <li>Verify able to edit type1 of active model1 without parent type</li>
     * <li>Verify able to edit type2 of draft model2  with parent type as type1</li>    
     * <li>Activate model2</li   
     * <li>Apply default layout For type1</li> 
     * <li>Apply default layout For type2</li>
     * <li>Create site, content and select content</li>
     * <li>Verify type1 is available in change type drop down in details page</li>
     * <li>Verify type2 is available in change type drop down in details page</li>
     * <li>Deactivate Model2</li> 
     * <li>Verify able to edit type2 of deactivated model2</li>
     * <li>Delete type2</li>
     * <li>Deactivate Model1</li>
     * <li>Verify able to edit type1 of deactivated model1 from parent type as "cm:content" to "cm:Folder"</li>      
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tadam1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testEditTypeWithPTInDiffModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();
        String modelName2 = "model2" + getUniqueTestName();
        String name = "name"+System.currentTimeMillis();            
        String siteName = "site"+System.currentTimeMillis();
               
        String typeName1 = "type1";   
        String typeName2 = "type2";
      
        String compositeTypeName1 = modelName1+":"+typeName1;   
        String compositeTypeName2 = modelName2+":"+typeName2;
       
        String parentContent ="cm"+":"+"content";      
        String newParentType1 = compositeTypeName1 + " ("+ "atitle1"+ ")";        
        String contentType = "cm" + ":" + "content" + " ("+ "Content"+ ")";
        String folderParent = "cm" + ":" + "folder" + " ("+ "Folder"+ ")";             
       
        String shareTypeName1 = "atitle1" + " (" + modelName1 + ":" + typeName1 + ")";
        String shareTypeName2 = "atitle2" + " (" + modelName2 + ":" + typeName2 + ")";   
               
        ContentDetails contentDetails =new ContentDetails();
        contentDetails.setName(name);            

        loginAs(driver, new String[] { username });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model1
        cmmActions.createNewModel(driver, modelName1).render();        
        
        //Activate model1
        cmmActions.setModelActive(driver, modelName1, true).render();       
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        // Add Type1 for Model       
        cmmActions.createType(driver, typeName1).render();                 
               
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model2
        cmmActions.createNewModel(driver, modelName2).render();       
                        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        // Add Type2 for Model2 with default parent type  
        cmmActions.createType(driver, typeName2).render();              
                              
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Verify able to edit type1 of active model1 without parent type
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.editTypewithoutParent(driver, compositeTypeName1, "atitle1", "adescription1").render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");        
        
        //View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        //Verify able to edit type2 of draft model2  with parent type as type1      
        ManageTypesAndAspectsPage managetypesandaspectsPage = cmmActions.editType(driver, compositeTypeName2, "atitle2", "adescription2", newParentType1).render();
        Assert.assertTrue(managetypesandaspectsPage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");        
        
        //Navigate to model manager page
        cmmActions.navigateToModelManagerPage(driver);
        
        //Activate model2
        cmmActions.setModelActive(driver, modelName2, true).render();
        
        //Apply default layout For type1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName1); 
        
        //Apply default layout For type2
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName2);        
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();

        //Verify type1 name is available in change type drop down in details page
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName1), "Type is not available on Share: " + shareTypeName1);
        
        //Verify type2 name is available in change type drop down in details page            
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName2), "Type is not available on Share: " + shareTypeName2);
        
        //Deactivate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName2, false).render();
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();                  
             
        //Verify able to edit type2 of deactivated model2
        ManageTypesAndAspectsPage manageTypesandaspectspage = cmmActions.editType(driver, compositeTypeName2, "dtitle3", "ddescription3",contentType).render();
        Assert.assertTrue(manageTypesandaspectspage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");        
        Assert.assertTrue(manageTypesandaspectspage.getCustomModelTypeRowByName(compositeTypeName2).getDisplayLabel().equals("dtitle3"),"display label displayed correctly");
        Assert.assertEquals(manageTypesandaspectspage.getCustomModelTypeRowByName(compositeTypeName2).getParent(),parentContent,"display label displayed correctly");
                     
        //Delete type2
        cmmActions.deleteType(driver , compositeTypeName2, "Delete").render();        
        
        //Deactivate Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //Verify able to edit type1 of deactivated model1 from parent type as "cm:content" to "cm:Folder"
        ManageTypesAndAspectsPage managetypesandaspectspage = cmmActions.editType(driver, compositeTypeName1, "dtitle4", "ddescription4", folderParent).render();
        Assert.assertTrue(managetypesandaspectspage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");
        Assert.assertTrue(managetypesandaspectspage.getCustomModelTypeRowByName(compositeTypeName1).getDisplayLabel().equals("dtitle4"),"display label displayed correctly");  
            
        
    }
    
    
   // verify able to edit type which is referenced as parent type to any other type with in same model
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>     
     * <li>Activate a Model1</li>
     * <li>Add Type1 with default parent type</li>
     * <li>Add Type2 with parent type as type1</li> 
     * <li>Verify able to edit type1 of active model1</li> 
     * <li>Verify able to edit type2 of active model1</li> 
     * <li>Apply default layout For type1</li>
     * <li>Apply default layout For type2 </li>
     * <li>Create site, content and select content</li>
     * <li>Verify type1 is available in change type drop down in details page</li>
     * <li>Verify type2 is available in change type drop down in details page</li>  
     * <li>Deactivate model1</li>  
     * <li>verify edit type1 including parent type which is referenced by type2 within same deactivated model1</li>
     * <li> verify able to edit type2 with new parent type</li>       
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tadam2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testEditypeWithPTInSameModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();
        String name = "name"+System.currentTimeMillis();            
        String siteName = "site"+System.currentTimeMillis();

        String typeName1 = "type1";      
        String typeName2 = "type2";     
              
        String compositeTypeName1 = modelName1+":"+typeName1;
        String compositeTypeName2 = modelName1+":"+typeName2;
        String parentTypeName = compositeTypeName1 + " ("+ typeName1+ ")";
                       
        String parentContent ="cm"+":"+"content";
        String folderContent ="cm"+":"+"folder";
          
        String contentType = "cm" + ":" + "content" + " ("+ "Content"+ ")";
        String folderParent = "cm" + ":" + "folder" + " ("+ "Folder"+ ")";             
       
        String shareTypeName1 = "atitle1" + " (" + modelName1 + ":" + typeName1 + ")";
        String shareTypeName2 = "atitle2" + " (" + modelName1 + ":" + typeName2 + ")";
       
        ContentDetails contentDetails =new ContentDetails();
        contentDetails.setName(name);            
        
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
        
        // Add Type1 with default parent type        
        cmmActions.createType(driver, typeName1).render();   
                   
        // Add Type2 with parent type as type1     
        cmmActions.createType(driver, typeName2, parentTypeName).render();        
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();      
            
        //Verify able to edit type1 of active model1      
        ManageTypesAndAspectsPage managetypesandaspectsPage = cmmActions.editTypewithoutParent(driver, compositeTypeName1, "atitle1", "adescription1").render();
        Assert.assertTrue(managetypesandaspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");
        
        //Verify able to edit type2 of active model1      
        ManageTypesAndAspectsPage managetypesandaspectspage = cmmActions.editTypewithoutParent(driver, compositeTypeName2, "atitle2", "adescription2").render();
        Assert.assertTrue(managetypesandaspectspage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed");
        
        //Apply default layout For type1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName1); 
        
        //Apply default layout For type2
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName2); 
          
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        //Verify type1 is available in change type drop down in details page
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName1), "Type is not available on Share: " + shareTypeName1);
        
        //Verify type2 is available in change type drop down in details page
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName2), "Type is not available on Share: " + shareTypeName2);
        
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //verify edit type1 including parent type which is referenced by type2 within same deactivated model1     
        ManageTypesAndAspectsPage managetypesAndaspectsPage = cmmActions.editType(driver, compositeTypeName1, "edittitle1", "editdescription1", contentType).render();
        Assert.assertTrue(managetypesAndaspectsPage.isCustomTypeRowDisplayed(compositeTypeName1), "Custom Type Row not disaplayed");
        Assert.assertEquals(managetypesandaspectsPage.getCustomModelTypeRowByName(compositeTypeName1).getParent(),parentContent,"display label displayed correctly");       
                
        //verify able to edit type2 with new parent type   
        ManageTypesAndAspectsPage managetypesAndaspectspage = cmmActions.editType(driver, compositeTypeName2, "edittitle2", "editdescription2",folderParent).render();
        Assert.assertTrue(managetypesAndaspectspage.isCustomTypeRowDisplayed(compositeTypeName2), "Custom Type Row not disaplayed"); 
        Assert.assertEquals(managetypesAndaspectspage.getCustomModelTypeRowByName(compositeTypeName2).getParent(),folderContent,"display label displayed correctly");        
    }    
}
    
