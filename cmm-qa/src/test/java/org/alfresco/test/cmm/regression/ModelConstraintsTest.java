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
 * Test Class to test Model Constraints
 * 
 * @author mbhave
 */

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.repository.ModelsPage;
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
public class ModelConstraintsTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(ModelConstraintsTest.class);
    private String testName;
    public DashBoardPage dashBoardpage;
    private String testUser;
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);  
        testUser = username;
/*        
        testUser = getUserNameFreeDomain(testName+ System.currentTimeMillis());
        
        //Login as Admin to create a new user
        loginAs(driver, new String[] {username});
        
        //Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, userInfo, userInfo, userInfo, userInfo, DEFAULT_PASSWORD, modelAdmin );        
        
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

    /**
     * Test:
     * <ul>
     * <li>Model Manager User can create a Draft Model</li>
     * <li>Verify Model xml in Repo > DD > Models</li>
     * </ul>
     * @throws Exception 
     */

    @AlfrescoTest(testlink="tobeaddedview1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testDraft1() throws Exception
    {
        String modelName = getUniqueTestName();

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
         ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");
        
        // Test that No Types and PGs are listed yet
        ManageTypesAndAspectsPage typesAspectsPage = cmmPage.selectCustomModelRowByName(modelName).render();
        
        logger.info(typesAspectsPage.getTitle());
        logger.info(typesAspectsPage.getPageTitle());
        
        Assert.assertTrue(typesAspectsPage.getCustomModelTypeRows().size() == 0);
        Assert.assertTrue(typesAspectsPage.getCustomModelPropertyGroupRows().size() == 0);
        
        // Navigate to the Repo > DD > Models and test that Model info is hidden: SHA-706
        ModelsPage modelsPage = adminActions.openRepositoryModelsPage(driver).render();
        modelsPage = modelsPage.getNavigation().selectDetailedView().render();
        
        Assert.assertFalse(modelsPage.isFileVisible(modelName));
    }
    
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testDraftToDelete2() throws Exception
    {
        String modelName = getUniqueTestName();

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");

        cmmActions.navigateToModelManagerPage(driver);
        
        // Delete Model
        cmmActions.deleteModel(driver, modelName);
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row should not be displayed");

        // Navigate to the Repo > DD > Models and test that Model info is as expected
        ModelsPage modelsPage = adminActions.openRepositoryModelsPage(driver).render();
        modelsPage = modelsPage.getNavigation().selectDetailedView().render();

        Assert.assertFalse(modelsPage.isFileVisible(modelName));
    }
    
    @AlfrescoTest(testlink="tobeaddeddupl3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testDuplicateDraft3() throws Exception
    {
        String modelName = getUniqueTestName();

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");
        
        // Try creating duplicate
        SharePage page = cmmActions.createNewModel(driver, modelName).render();
        if (page instanceof ShareDialogue)
        {
        	cmmPage = cmmActions.closeShareDialogue(driver, CreateNewModelPopUp.class).render();
        }
        
        if (page instanceof ModelManagerPage)
        {
            Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");
        }
        
        // Navigate to the Repo > DD > Models and test that Model info is hidden: SHA-706
        ModelsPage modelsPage = adminActions.openRepositoryModelsPage(driver).render();
        modelsPage = modelsPage.getNavigation().selectDetailedView().render();
        
        Assert.assertFalse(modelsPage.isFileVisible(modelName));        
    }
    
    @AlfrescoTest(testlink="tobeaddeddupl4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testModelWithSameNameAsSysModel4() throws Exception
    {
        String modelName = "contentmodel";

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName);
        
        // Expect Error: CreateNewModelPopUp displayed
        ModelManagerPage cmmPage = cmmActions.closeShareDialogue(driver, CreateNewModelPopUp.class).render();        
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");            
    }
    
    @AlfrescoTest(testlink="tobeaddeddupl5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testModelWithSameNameSpaceAsSysModel5() throws Exception
    {
        String namespace = "http://www.alfresco.org/model/calendar";
        String modelName = "calendar" + System.currentTimeMillis();

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName, namespace, modelName);

        // Expect Error: CreateNewModelPopUp displayed
        ModelManagerPage cmmPage = cmmActions.closeShareDialogue(driver, CreateNewModelPopUp.class).render();
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");            
    }
    
    @AlfrescoTest(testlink="tobeaddeddupl6")
    @Test(groups = "EnterpriseOnly", priority=6)
    public void testModelWithSamePrefixAsSysModel6() throws Exception
    {
        String prefix = "cm";
        String modelName = prefix + System.currentTimeMillis();

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName, modelName, prefix);
        
        // Expect Error: CreateNewModelPopUp displayed
        ModelManagerPage cmmPage = cmmActions.closeShareDialogue(driver, CreateNewModelPopUp.class).render();
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Custom Model Row is not displayed");            
    }
    
    @AlfrescoTest(testlink="tobeaddeddupl7")
    @Test(groups = "EnterpriseOnly", priority=7, enabled = false)
    public void testModelListSortOrder7() throws Exception
    {
        String modelName1 = "I" + System.currentTimeMillis();
        String modelName2 = "A" + System.currentTimeMillis();
        String modelName3 = "E" + System.currentTimeMillis();

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model: Model1
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName1, modelName1, modelName1).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName1), "Custom Model Row is not displayed for Model1");
        
        // Create New Model: Model2
        cmmPage = cmmActions.createNewModel(driver, modelName2, modelName2, modelName2).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName2), "Custom Model Row is not displayed for Model2");
        
        // Create New Model: Model3
        cmmPage = cmmActions.createNewModel(driver, modelName3, modelName3, modelName3).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName3), "Custom Model Row is not displayed for Model3");
        
        // Check Sorting order
        int model1Index = cmmPage.getCMRows().indexOf(cmmPage.getCustomModelRowByName(modelName1));
        int model2Index = cmmPage.getCMRows().indexOf(cmmPage.getCustomModelRowByName(modelName2));
        int model3Index = cmmPage.getCMRows().indexOf(cmmPage.getCustomModelRowByName(modelName3));
        
        Assert.assertTrue(model2Index < model1Index);
        Assert.assertTrue(model1Index < model3Index);
    }

}
