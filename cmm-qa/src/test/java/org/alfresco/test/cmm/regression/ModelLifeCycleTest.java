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
 * Test Class to test Model Life Cycle
 * 
 * @author Meenal Bhave
 */

import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
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
public class ModelLifeCycleTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(ModelLifeCycleTest.class);

    private String testName;

    public DashBoardPage dashBoardpage;

    private String modelAdmin = "ALFRESCO_MODEL_ADMINISTRATORS";

    private String testUser;

    private String modelStatusDraft = "Inactive";
    
    private String modelStatusActive = "Active";
    
    private String okAction = "Ok";    

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        
         //Login as Admin to create a new user 
         loginAs(driver, new String[] {username}); 
         testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());
           
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

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Delete a Model</li>
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeaddeddupl1")
    @Test(groups = "EnterpriseOnly", priority = 1)
    public void testDeleteDraftModel() throws Exception
    {
        String modelName = "model" + getUniqueTestName();

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Error: Model not deleted: " + modelName);

        // Delete the model before adding types or aspects
        cmmActions.deleteModel(driver, modelName);
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName));

        // Tests after Model is deleted: Same model can be created
        cmmPage = cmmActions.createNewModel(driver, modelName).render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName));
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>View Types and Aspects</li>
     * <li>Add Type</li>
     * <li>Add Aspect</li>
     * <li>Delete a Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl2")
    @Test(groups = "EnterpriseOnly", priority = 2)
    public void testDeleteDraftModelWithTypesAspects() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        String typeName = "type";
        String compositeTypeName = modelName + ":" + typeName;
        String aspectName = "aspect";
        String compositeAspectName = modelName + ":" + aspectName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects
        ManageTypesAndAspectsPage typesAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        Assert.assertTrue(0 == typesAspectsPage.getCustomModelTypeRows().size());
        Assert.assertTrue(0 == typesAspectsPage.getCustomModelPropertyGroupRows().size());

        // Add Types
        typesAspectsPage = cmmActions.createType(driver, typeName).render();
        Assert.assertTrue(1 == typesAspectsPage.getCustomModelTypeRows().size());

        // Add Aspects
        typesAspectsPage = cmmActions.createAspect(driver, aspectName).render();
        Assert.assertTrue(1 == typesAspectsPage.getCustomModelPropertyGroupRows().size());

        // View Properties for Type
        ManagePropertiesPage propertiesPage = cmmActions.viewProperties(driver, compositeTypeName).render();

        // No Properties for Type
        Assert.assertTrue(0 == propertiesPage.getPropertyRows().size());
        Assert.assertTrue(0 == propertiesPage.getPropertyRows().size());

        // No Properties for Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);

        propertiesPage = cmmActions.viewProperties(driver, compositeAspectName).render();
        Assert.assertTrue(0 == propertiesPage.getPropertyRows().size());
        Assert.assertTrue(0 == propertiesPage.getPropertyRows().size());

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
        ModelManagerPage cmmPage = propertiesPage.getCurrentPage().render();
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Error: Model not deleted: " + modelName);
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Activate the model before adding types or aspects</li>
     * <li>Deactivate Model</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl3")
    @Test(groups = "EnterpriseOnly", priority = 3)
    public void testDeleteActivatedModelNoTypesAspects() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        cmmActions.createNewModel(driver, modelName).render();

        // Activate the model before adding types or aspects
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // View Types and Aspects
        ManageTypesAndAspectsPage typesAspectsPage = cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        Assert.assertTrue(0 == typesAspectsPage.getCustomModelTypeRows().size());
        Assert.assertTrue(0 == typesAspectsPage.getCustomModelPropertyGroupRows().size());

        // Check that model can't be deleted
        cmmActions.navigateToModelManagerPage(driver);
        
        try
        {
            cmmActions.deleteModel(driver, modelName);
        }
        catch(PageOperationException e)
        {
            //ignore as it should not be able to delete.
        }
        
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Model should not have been deleted: " + modelName);

        // Deactivate Model
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Error: Model not deleted: " + modelName);
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Activate the model before adding types or aspects</li>
     * <li>Add Type</li>
     * <li>Add Aspect</li>
     * <li>Deactivate Model</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl4")
    @Test(groups = "EnterpriseOnly", priority = 4)
    public void testDeleteActivatedModelWithTypesAspects() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        String typeName = "type";
        String aspectName = "aspect";

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        cmmActions.createNewModel(driver, modelName).render();

        // Activate the model before adding types or aspects
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types
        cmmActions.createType(driver, typeName).render();

        // Add Aspects
        cmmActions.createAspect(driver, aspectName).render();

        // Check that model can't be deleted
        cmmActions.navigateToModelManagerPage(driver);
        try{cmmActions.deleteModel(driver, modelName);} catch(PageOperationException e) {}
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Model should not have been deleted: " + modelName);

        // Deactivate Model
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Error: Model not deleted: " + modelName);
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Activate the model</li>
     * <li>Use Type and Aspect within the same model</li>
     * <li>Deactivate Model</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl5")
    @Test(groups = "EnterpriseOnly", priority = 5)
    public void testDeleteActivatedModelRefInSameModel() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;

        String typeName = "type";
        String aspectName = "aspect";
        String depTypeName = "childT";
        String depAspectName = "childA";

        String parentTypeName = getParentTypeAspectName(modelName, typeName);

        String parentAspectName = getParentTypeAspectName(modelName, aspectName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types
        cmmActions.createType(driver, typeName).render();

        // Add Aspects
        cmmActions.createAspect(driver, aspectName).render();

        // Activate the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();

        // Check that model can't be deleted
        try{cmmActions.deleteModel(driver, modelName);}catch(PageOperationException e){};
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelName), "Model should not have been deleted: " + modelName);

        // Check that Type created above can be used as parent types
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        cmmActions.createType(driver, depTypeName, parentTypeName).render();

        // Check that Aspect created above can be used as parent Aspect
        cmmActions.createAspect(driver, depAspectName, parentAspectName).render();

        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft);

        // Can't use the type / aspect
        // Existing functionality: not to be automated if tests already exist

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Activate the model</li>
     * <li>Use Type and Aspect within another model</li>
     * <li>Deactivate Model</li>
     * <li>Delete Model</li>
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeaddeddupl6")
    @Test(groups = "EnterpriseOnly", priority = 6)
    public void testDeleteActivatedModelRefInDiffModel() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;
        String depModelName = "childModel" + testName;

        String typeName = "type";
        String aspectName = "aspect";
        String depTypeName = "childType";
        String depAspectName = "childAspect";

        String parentTypeName = getParentTypeAspectName(modelName, typeName);

        String parentAspectName = getParentTypeAspectName(modelName, aspectName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Parent: Model1
        cmmActions.createNewModel(driver, modelName).render();

        // Create New Draft Model: Dependent
        cmmActions.createNewModel(driver, depModelName).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        cmmActions.createType(driver, typeName).render();

        // Add Aspects: Model1
        cmmActions.createAspect(driver, aspectName).render();

        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();

        // Check that Type created for Model1 can be used as parent types
        cmmActions.viewTypesAspectsForModel(driver, depModelName).render();

        cmmActions.createType(driver, depTypeName, parentTypeName).render();

        // Check that Aspect created above can be used as parent Aspect
        cmmActions.createAspect(driver, depAspectName, parentAspectName).render();

        // Activate Model: Dependent
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, depModelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(depModelName).getCmStatus(), modelStatusActive);

        // Deactivate Model: Parent: Does not Succeed: When dependent model is active
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // Deactivate Model: Dependent
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, depModelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(depModelName).getCmStatus(), modelStatusDraft);

        // Deactivate Model: Parent: Does not Succeed: When dependent model is Draft
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // Delete Model: Dependent
        cmmActions.deleteModel(driver, depModelName);
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(depModelName), "Error: Model not deleted: " + depModelName);

        // Deactivate Model: Parent: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Add Properties</li>
     * <li>Can Delete Properties</li>
     * <li>Activate the model</li>
     * <li>Can Delete Properties: As not in use</li>
     * <li>Deactivate Model</li>
     * <li>Add Properties</li>
     * <li>Can Delete Properties: As not in use</li>
     * <li>Delete Model</li>
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeaddeddupl7")
    @Test(groups = "EnterpriseOnly", priority = 7)
    public void testModelDraftToActivateAddDeleteProperties() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;

        String typeName = "type" + testName;
        String aspectName = "aspect" + testName;
        String typePropertyName = "propType";
        String aspectPropertyName = "propAspect";

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        String compositeTypePropertyName = modelName + ":" + typePropertyName;
        String compositeAspectPropertyName = modelName + ":" + aspectPropertyName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types
        cmmActions.createType(driver, typeName).render();

        // Add Aspects
        cmmActions.createAspect(driver, aspectName).render();

        // Add Property1: For Type
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName+"1");
        // SHA-1253: Amended the date format in line with the new UI / Aikau changes (Original: "2015-04-11")
        cmmActions.createProperty(driver, typePropertyName+"2", "", "", DataType.Date, MandatoryClassifier.Mandatory, false, dateEntry).render();

        // Delete Property: for Type
        ManagePropertiesPage propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Property is not deleted for Type: " + compositeTypeName);

        // Add Property1: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName+"1");
        // SHA-1253: Amended the date format in line with the new UI / Aikau changes (Original: "2015-04-11T10:26:36.00+01:00")
        cmmActions.createProperty(driver, aspectPropertyName+"2", "", "", DataType.DateTime, MandatoryClassifier.Mandatory, false, dateEntry).render();
                
        // Delete Property: for Aspect
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"1"), "Property is not deleted for Aspect: " + compositeAspectName);
        
        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);
        
        // Check that Property can be deleted: When unused & Model is Active: For Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Error deleting Property for Active Model. Ref Type: " + compositeTypeName);

        // Check that Property can be deleted: When unused & Model is Active: For Aspect
        cmmActions.viewProperties(driver, compositeAspectName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"2", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"2"), "Error deleting Property for Active Model. Ref. Aspect: " + compositeAspectName);
        
        // Deactivate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft);
        
        // Add Property3, 4: For Type
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName+"3", "", "", DataType.Boolean, MandatoryClassifier.Mandatory, false, "true").render();
        cmmActions.createProperty(driver, typePropertyName+"4", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "false").render();

        // Delete Properties for: Type
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"4"), "Error Deleting Property when not in use. Ref Type: " + compositeTypeName);

        // Add Property3, 4: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName+"3", "", "", DataType.Int, MandatoryClassifier.Optional, false, "0").render();
        cmmActions.createProperty(driver, aspectPropertyName+"4", "", "", DataType.Float, MandatoryClassifier.Optional, false, "0").render();
        
        // Delete Properties: For Aspect
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Deleting Property when not in use. Ref Type: " + compositeAspectName);
        
        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(modelName), "Error: Model not deleted: " + modelName);
    }
}
