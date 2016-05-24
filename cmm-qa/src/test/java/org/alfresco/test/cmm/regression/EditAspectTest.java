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
 * Test Class to test EditAspectTest
 * 
 * @author Charu 
 */
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
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
public class EditAspectTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(EditCustomTypeTest.class);
       
    private String modelName = "model" + System.currentTimeMillis();
    
    private String typeName = "type"+ System.currentTimeMillis();
    
    String shareTypeName = getShareTypeName(modelName, typeName);
   
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
        adminActions.createEnterpriseUserWithGroup(drone, testUser, testUser, testUser, testUser, DEFAULT_PASSWORD, modelAdmin );        
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

    //This test is to verify able to edit type of active model with parent type
    //as any other model type.    
    /**
     * Test:
     * <ul>
     * <li> Create New Model1</li>
     * <li>Create Model2</li>
     * <li>Activate a Model1</li>
     * <li>Create a Aspect1 for Model1</li>     
     * <li>Activate Model2</li>
     * <li>Create Aspect2 for active Model2 with parent Aspect as Aspect1</li>
     * <li>Verify not able to edit Aspect2 of active model2</li>    
     * <li>Deactivate Model2</li   
     * <li>Verify able to edit Aspect2 of deactivated model2</li>    
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tadam1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testEditAspectWithPTInDiffModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();
        String modelName2 = "model2" + getUniqueTestName();
        String name = "name"+System.currentTimeMillis();            
        String siteName = "site"+System.currentTimeMillis();
               
        String aspectName1 = "Aspect1";   
        String aspectName2 = "Aspect2"; 
        String title1 = "atitle1";
        String title2 = "atitle2";
      
        String compositeAspectName1 = modelName1+":"+aspectName1;   
        String compositeAspectName2 = modelName2+":"+aspectName2;
           
        String newParentAspect1 = compositeAspectName1 + " ("+ "atitle1"+ ")";        
        String noneAspect = "None";            
       
        String shareAspectName1 = getShareAspectName(modelName1, title1);
               
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
        
        // Add Aspect1        
        cmmActions.createAspect(driver, aspectName1).render();                 
               
        cmmActions.navigateToModelManagerPage(driver);
        
        // Create New Model2
        cmmActions.createNewModel(driver, modelName2).render();       
                        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        // Add Aspect2 for Model2 with default Aspect  
        cmmActions.createAspect(driver, aspectName2).render();              
                              
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
        
        //Verify able to edit Aspect1 of active model1 
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmActions.editAspectWithoutParent(driver, compositeAspectName1, title1, "adescription1").render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compositeAspectName1), "Custom Type Row not displayed");        
        
        //View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        
        //Verify able to edit Aspect2 of model2  with parent Aspect as Aspect1      
        ManageTypesAndAspectsPage managetypesandaspectsPage = cmmActions.editAspect(driver, compositeAspectName2, title2, "adescription2", newParentAspect1).render();
        Assert.assertTrue(managetypesandaspectsPage.isPropertyGroupRowDisplayed(compositeAspectName2), "Custom Type Row not disaplayed");        
        
        //Navigate to model manager page
        cmmActions.navigateToModelManagerPage(driver);
        
        //Activate model2
        cmmActions.setModelActive(driver, modelName2, true).render();
        
        //Apply default layout For Aspect1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName1); 
        
        //Apply default layout For Aspect2
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName2);        
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();
        
        // Add Aspect 1
        List<String> aspects1 = new ArrayList<String>();
        aspects1.add(shareAspectName1);
        siteActions.addAspects(driver, aspects1);
        
        // Add Aspect 2
        List<String> aspects2 = new ArrayList<String>();
        aspects2.add(title2);
        siteActions.addAspects(driver, aspects2);

        //Verify Aspect1 is available in aspects page to add
        Assert.assertTrue(siteActions.isAspectAdded(driver, title1),"Aspect1 displayed in aspects page");
        
        //Verify Aspect2 is available in aspects page to add
        Assert.assertTrue(siteActions.isAspectAdded(driver, title2),"Aspect2 displayed in aspects page"); 
        
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.selectContent(driver, name).render();
        
        // Remove Aspect 1
        List<String> aspects3 = new ArrayList<String>();
        aspects3.add(title1);
        siteActions.removeAspects(driver, aspects3);
        
        // Remove Aspect 2
        List<String> aspects4 = new ArrayList<String>();
        aspects4.add(title2);
        siteActions.removeAspects(driver, aspects4);
        
        //Deactivate Model2
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName2, false).render();
                
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();                  
             
        //Verify able to edit aspect2 of deactivated model2
        ManageTypesAndAspectsPage manageTypesandaspectspage = cmmActions.editAspect(driver, compositeAspectName2, "dtitle3", "ddescription3", noneAspect).render();
        Assert.assertTrue(manageTypesandaspectspage.isPropertyGroupRowDisplayed(compositeAspectName2), "Aspect not disaplayed");        
        Assert.assertTrue(manageTypesandaspectspage.getCustomModelPropertyGroupRowByName(compositeAspectName2).getDisplayLabel().equals("dtitle3"),"display label displayed correctly");
        Assert.assertEquals(manageTypesandaspectspage.getCustomModelPropertyGroupRowByName(compositeAspectName2).getParent(),"","display label displayed correctly");
                     
        //Delete aspect2
        cmmActions.deleteAspect(driver , compositeAspectName2, "Delete").render();        
        
        //Deactivate Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //Verify able to edit Aspect1 of deactivated model1 from parent Aspect to none
        ManageTypesAndAspectsPage managetypesandaspectspage = cmmActions.editAspect(driver, compositeAspectName1, "dtitle4", "ddescription4", noneAspect).render();
        Assert.assertTrue(managetypesandaspectspage.isPropertyGroupRowDisplayed(compositeAspectName1), "Aspect not disaplayed");
        Assert.assertTrue(managetypesandaspectspage.getCustomModelPropertyGroupRowByName(compositeAspectName1).getDisplayLabel().equals("dtitle4"),"display label displayed correctly");  
            
        
    }
    
    //SHA-679
   // verify not able to edit aspect which is referenced as parent aspect to any other aspect with in same deactivated model
    /**
     * Test:
     * <ul>
     * <li>Create Model1</li>     
     * <li>Activate a Model1</li>
     * <li>Create a Aspect for Model1</li>
     * <li>Create Aspect two for Model1 with parent Aspect as Aspect1</li> 
     * <li>Verify not able to delete Aspect1 which is referenced by Aspect2 in active model1</li> 
     * <li>Deactivate model1</li> 
     * <li>verify not able to delete Aspect1 which is referenced by Aspect2 with in same deactivated model1</li>
     * <li>verify able to delete Aspect2 </li>
     * <li>verify able to delete Aspect1 which is not referenced by Aspect2 with in same deactivated model1</li>
     *     
     * @throws Exception 
     */
    @AlfrescoTest(testlink="tadam2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testCAspectWithPTInSameModel() throws Exception
    {
        String modelName1 = "model1" + getUniqueTestName();
        String name = "name"+System.currentTimeMillis();            
        String siteName = "site"+System.currentTimeMillis();

        String AspectName1 = "Aspect1";      
        String AspectName2 = "Aspect2";
        
        String title1 = "atitle1";
        String title2 = "atitle2";
              
        String compositeAspectName1 = modelName1+":"+AspectName1;
        String compositeAspectName2 = modelName1+":"+AspectName2;
        String parentAspectName = compositeAspectName1 + " ("+ AspectName1+ ")";
          
        String noneAspect = "None";
       
       
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
        
        // Add Aspect1        
        cmmActions.createAspect(driver, AspectName1).render();   
                   
        // Add Aspect2        
        cmmActions.createAspect(driver, AspectName2, parentAspectName).render();        
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();      
            
        //Verify able to edit Aspect1 of active model1      
        ManageTypesAndAspectsPage managetypesandaspectsPage = cmmActions.editAspectWithoutParent(driver, compositeAspectName1, title1, "adescription1").render();
        Assert.assertTrue(managetypesandaspectsPage.isPropertyGroupRowDisplayed(compositeAspectName1), "Property Group Row not disaplayed");
        
        //Verify able to edit Aspect2 of active model1      
        ManageTypesAndAspectsPage managetypesandaspectspage = cmmActions.editAspectWithoutParent(driver, compositeAspectName2, title2, "adescription2").render();
        Assert.assertTrue(managetypesandaspectspage.isPropertyGroupRowDisplayed(compositeAspectName2), "Property Group Row not disaplayed");
        
        //Apply default layout For Aspect1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName1); 
        
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render(); 
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName2); 
          
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        siteActions.selectContent(driver, name).render();      
               
        // Add Aspect 1
        List<String> aspects1 = new ArrayList<String>();
        aspects1.add(title1);
        siteActions.addAspects(driver, aspects1);
        
        // Add Aspect 2
        List<String> aspects2 = new ArrayList<String>();
        aspects2.add(title2);
        siteActions.addAspects(driver, aspects2);

        //Verify Aspect1 is available in aspects page to add
        Assert.assertTrue(siteActions.isAspectAdded(driver, title1),"Aspect1 displayed in aspects page");
        
        //Verify Aspect2 is available in aspects page to add
        Assert.assertTrue(siteActions.isAspectAdded(driver, title2),"Aspect2 displayed in aspects page"); 
        
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.selectContent(driver, name).render();
        
        // Remove Aspect 1
        List<String> aspects3 = new ArrayList<String>();
        aspects3.add(title1);
        siteActions.removeAspects(driver, aspects3);
        
        // Remove Aspect 2
        List<String> aspects4 = new ArrayList<String>();
        aspects4.add(title2);
        siteActions.removeAspects(driver, aspects4);
               
        //Deactivate model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName1, false).render();
        
        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName1).render();
                        
        //verify edit Aspect1 including parent Aspect which is referenced by Aspect2 within same deactivated model1     
        ManageTypesAndAspectsPage managetypesAndaspectsPage = cmmActions.editAspect(driver, compositeAspectName1, "edittitle1", "editdescription1", noneAspect).render();
        Assert.assertTrue(managetypesAndaspectsPage.isPropertyGroupRowDisplayed(compositeAspectName1), "Property Group Row not disaplayed");
        Assert.assertEquals(managetypesandaspectsPage.getCustomModelPropertyGroupRowByName(compositeAspectName1).getParent(),"","display label displayed correctly");       
                
        //verify able to edit Aspect2     
        ManageTypesAndAspectsPage managetypesAndaspectspage = cmmActions.editAspect(driver, compositeAspectName2, "edittitle2", "editdescription2",noneAspect).render();
        Assert.assertTrue(managetypesAndaspectspage.isPropertyGroupRowDisplayed(compositeAspectName2), "Property Group Row not disaplayed"); 
        Assert.assertEquals(managetypesAndaspectspage.getCustomModelPropertyGroupRowByName(compositeAspectName2).getParent(),"","display label displayed correctly");        
          
    }    
         
    
    
    
    
    
}
