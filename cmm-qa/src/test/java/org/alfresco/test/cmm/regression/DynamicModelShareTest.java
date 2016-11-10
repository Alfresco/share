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
 * Test Class to test Applying Dynamic Model types and aspects using Share
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class DynamicModelShareTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(DynamicModelShareTest.class);

    private String testName;

    public DashBoardPage dashBoardpage;

    private String testUser;

    @Value("${default.site.name}") private String siteName;

    @Value("${cmm.model.status.draft}") private String modelStatusDraft = "DRAFT";

    @Value("${cmm.model.status.active}")private String modelStatusActive = "ACTIVE";
    
    @Value("${property.value.empty}")private String propertyEmpty = "(None)";

    @Value("${cmm.model.action.ok}") private String okAction = "Ok";
    
    private String currentDate = "2015-09-15";
    
    @Value("${property.value.not.displayed}") private String notDisplayed;
    
    @Value("${property.value.empty}") private String valNotSpecified;

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        
        testUser = username;
/*
        // Login as Admin to create a new user
        loginAs(driver, new String[] { username });
        testUser = getUserNameFreeDomain(testName);

        // Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, testUser, testUser, testUser, testUser, DEFAULT_PASSWORD, modelAdmin);

        // Logout as admin
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
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Activate the model</li>
     * <li>Use Type in a node</li>
     * <li>Try to Deactivate Model - should fail</li>
     * <li>Delete the node</li>
     * <li>Try to Deactivate Model - should fail as node's in the TrashCan</li>
     * <li>Delete the node from TrashCan</li>
     * <li>Try to Deactivate Model - should succeed</li>
     * <li>Try to Create a Node using Type from the Model - should fail</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl1")
    @Test(groups = "EnterpriseOnly", priority = 1)
    public void testApplyTypesOnShare() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String typeName = "type" + testName;
        String compositeTypeName = modelName + ":" + typeName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        
        String propertyName = "prop" + testName;

        String contentName = "doc" + testName;
        
        File doc = siteUtil.prepareFile(contentName, contentName);
        File doc2 = siteUtil.prepareFile(contentName+"2", contentName+"2");

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        // cmmActions.createType(driver, typeName, "cm:folder (Folder)").render();
        cmmActions.createType(driver, typeName).render();
        
        // Add Properties
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, propertyName+"OptionalD", propertyName+"OptionalD", "", DataType.MlText, MandatoryClassifier.Optional, false, "zzz").render();       
        cmmActions.createProperty(driver, propertyName+"NotEnfD", propertyName+"NotEnfD", "", DataType.Text, MandatoryClassifier.Mandatory, false, "A1#@").render();
        cmmActions.createProperty(driver, propertyName+"MandatoryD", propertyName+"MandatoryD", "", DataType.Boolean, MandatoryClassifier.MANDATORYENF, false, "true").render();       
        cmmActions.createProperty(driver, propertyName+"Optional", propertyName+"Optional", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();       
        cmmActions.createProperty(driver, propertyName+"NotEnf", propertyName+"NotEnf", "", DataType.Int, MandatoryClassifier.Mandatory, false, "").render();
        
        // Apply Default Layout for Types
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);

        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.uploadFile(driver, doc2);

        siteActions.selectContent(driver, doc.getName()).render();

        // Check that nodes can be given the new Type and Aspects: Using Share
        siteActions.changeType(driver, shareTypeName).render();
        
        // Check Properties
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName+"OptionalD", "zzz");
        expectedProps.put(propertyName+"NotEnfD", "A1#@");
        expectedProps.put(propertyName+"MandatoryD", "Yes"); // true converts to Yes
        expectedProps.put(propertyName+"Optional", "");
        expectedProps.put(propertyName+"NotEnf", "");
        
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
        
        // Deactivate Model: Should Not Succeed
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Remove Referenced / delete nodes
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver, doc.getName());

        // Deactivate Model: Fails as the document is still in the TrashCan
        // TODO: Enable this section of the test when MNT-13820 is resolved. Else This step causes Trashcan related other unwanted failures that impact other tests.

        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, doc.getName(), "documentLibrary");

        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft, "Issue with Model: " + modelName);

        // The type / aspect is not available on Share 
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc2.getName()).render();
        Assert.assertFalse(siteActions.isTypeAvailable(driver, shareTypeName), "Type is still available after Deactivating Model: " + shareTypeName);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Add Properties: Different Data Types / Requirements</li>
     * <li>Activate the model</li>
     * <li>Add Aspect to a node</li>
     * <li>Try to Deactivate Model - should fail</li>
     * <li>Remove the Aspect from the node</li>
     * <li>Try to Deactivate Model - should succeed</li>
     * <li>Check that the aspects are not available / not be added to a node</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl2")
    @Test(groups = "EnterpriseOnly", priority = 2)
    public void testApplyAspectsOnShare() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String aspectName = "aspect" + testName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "prop" + testName;

        String contentName = "doc" + testName;

        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();
        
        // Activate the model: Model1
        cmmActions.setModelActive(driver, modelName, true).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Aspects: Model1
        cmmActions.createAspect(driver, aspectName).render();
        
        // Add Properties
        cmmActions.viewProperties(driver, compositeAspectName);
        
        cmmActions.createProperty(driver, propertyName+"OptionalD", propertyName+"OptionalD", "", DataType.MlText, MandatoryClassifier.Optional, false, "zzz").render();       
        cmmActions.createProperty(driver, propertyName+"NotEnfD", propertyName+"NotEnfD", "", DataType.Text, MandatoryClassifier.Mandatory, false, currentDate).render();
        
        cmmActions.createProperty(driver, propertyName+"Optional", propertyName+"Optional", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();       
        cmmActions.createProperty(driver, propertyName+"NotEnf", propertyName+"NotEnf", "", DataType.Int, MandatoryClassifier.Mandatory, false, "").render();

        // Apply Default Layout for Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName()).render();
        
        // Add Aspect 1
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);
        
        // Check Properties:
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName+"OptionalD", "zzz");
        expectedProps.put(propertyName+"NotEnfD", currentDate);
        expectedProps.put(propertyName+"Optional", "");
        expectedProps.put(propertyName+"NotEnf", "");
        
        // Check Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
                
        // Deactivate Model: Does Not Succeed
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Remove Reference Via Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc.getName()).render();
        siteActions.removeAspects(driver, aspects);

        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft, "Issue with Model: " + modelName);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }
    

    
    /**
     * Test: cm:content: Create using Share, Check and Change via Share
     * <ul>
     * <li>Create a Model</li>
     * <li>Add type T1 with Parent = cm:content</li>
     * <li>Activate the model</li>
     * <li>Add type T2 with Parent = T1</li>
     * <li>Create node on Share</li>
     * <li>Check Type 1 is available on Share</li>
     * <li>Check Type 2 is not available on Share</li>
     * <li>Apply Type 1 on Share</li>
     * <li>Then Apply Type 2 on Share</li>
     * <li>Check the Properties are correctly applied using Share</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl3")
    @Test(groups = "EnterpriseOnly", priority = 3)
    public void testTypeHierarchyNodeTypeCmContent() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;
        
        String modelName2 = "second" + modelName;

        String typeName = "type" + testName;
        String typeName2 = typeName + "child1";
        String typeName3 = typeName + "child2";
        
        String shareTypeName = getShareTypeName(modelName, typeName);
        String shareTypeName2 = getShareTypeName(modelName, typeName2);
        String shareTypeName3 = getShareTypeName(modelName2, typeName3);

        String compositeTypeName = modelName + ":" + typeName;
        String parent1TypeName = compositeTypeName + " (" + typeName + ")";
        
        String compositeTypeName2 = modelName + ":" + typeName2;
        String parent2TypeName = compositeTypeName2 + " (" + typeName2 + ")";
        
        String compositeTypeNameForModel2 = modelName2 + ":" + typeName3;        
        
        String propertyName = typeName;
        
        String contentName = "content" + testName;
        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model: Model1, Model2
        cmmActions.createNewModel(driver, modelName).render();
        cmmActions.createNewModel(driver, modelName2).render();
        
        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();
        cmmActions.setModelActive(driver, modelName2, true).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        cmmActions.createType(driver, typeName, "cm:content (Content)").render();
        
        cmmActions.createType(driver, typeName2, parent1TypeName).render();
        
        // Create Property for Type1
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, propertyName);        
        
        // Create Property for Child Type1
        cmmActions.viewProperties(driver, compositeTypeName2);
        cmmActions.createProperty(driver, propertyName+"child1");
        
        // View Types and Aspects: Model2
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();        
        
        cmmActions.createType(driver, typeName3, parent2TypeName).render();
        
        // Create Property for Child Type2
        cmmActions.viewProperties(driver, compositeTypeNameForModel2);
        cmmActions.createProperty(driver, propertyName+"child2");

        // Apply Default Layout for Types
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName2);
        
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeNameForModel2);

        // Check that nodes can be created with Type and Aspects: Using Share        
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        
        siteActions.viewDetails(driver, doc.getName()).render();
        
        // Check Type 1 is available and Type 2 is not
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName), "Type is not available on Share: " + shareTypeName);
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName2), "Type is not available on Share: " + shareTypeName2);
        
        // Apply type: Using Share
        siteActions.changeType(driver, shareTypeName).render();
        
        // Apply Child type1: Using Share
        siteActions.changeType(driver, shareTypeName2).render();
        
        // Apply Child type2: Using Share
        siteActions.changeType(driver, shareTypeName3);
   
        // Check the property Values
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(compositeTypeName, "");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");         
    }
    
    /**
     * Test: cm:folder: Create using Share, Check and Change via Share
     * <ul>
     * <li>Create a Model</li>
     * <li>Add type T1 with Parent = cm:folder</li>
     * <li>Activate the model</li>
     * <li>Add type T2 with Parent = T1</li>
     * <li>Create node on Share</li>
     * <li>Check Type 1 is available on Share</li>
     * <li>Check Type 2 is not available on Share</li>
     * <li>Apply Type 1 on Share</li>
     * <li>Then Apply Type 2 on Share</li>
     * <li>Check the Properties are correctly applied using Share</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl4")
    @Test(groups = "EnterpriseOnly", priority = 4)
    public void testTypeHierarchyNodeTypeCmFolder() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;
        
        String modelName2 = "second" + modelName;

        String typeName = "type" + testName;
        String typeName2 = typeName + "child1";
        String typeName3 = typeName + "child2";

        String compositeTypeName = modelName + ":" + typeName;
        String parentTypeName = compositeTypeName + " (" + typeName + ")";
        
        String compositeTypeNameForModel2 = modelName2 + ":" + typeName3;
        
        String compositeTypeName2 = modelName + ":" + typeName2;
        String parent2TypeName = compositeTypeName2 + " (" + typeName2 + ")";
        
        String shareTypeName = getShareTypeName(modelName, typeName);
        String shareTypeName2 = getShareTypeName(modelName, typeName2);
        String shareTypeName3 = getShareTypeName(modelName2, typeName3);
        
        String propertyName = typeName;
        
        String folderName = "folder" + testName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model: Model1, Model2
        cmmActions.createNewModel(driver, modelName).render();
        cmmActions.createNewModel(driver, modelName2).render();
        
        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();
        cmmActions.setModelActive(driver, modelName2, true).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        cmmActions.createType(driver, typeName, "cm:folder (Folder)").render();
        
        cmmActions.createType(driver, typeName2, parentTypeName).render();
        
        // Create Property for Type1
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, propertyName);
        
        // Create Property for Child Type1
        cmmActions.viewProperties(driver, compositeTypeName+"child1");
        cmmActions.createProperty(driver, propertyName+"child1");
        
        // View Types and Aspects: Model2
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();        
        
        cmmActions.createType(driver, typeName3, parent2TypeName).render();
        
        // Create Property for Child Type2
        cmmActions.viewProperties(driver, compositeTypeNameForModel2);
        cmmActions.createProperty(driver, propertyName+"child2");
        
        // Apply Default Layout for Types
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName2);
        
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeNameForModel2);

        // Check that nodes can be created with Type and Aspects: Using Share        
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.createFolder(driver, folderName, folderName, folderName);
        
        siteActions.viewDetails(driver, folderName).render();
        
        // Check Type 1 is available and Type 2 is not
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName), "Type is not available on Share: " + shareTypeName);
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName2), "Type is available on Share when it should not (TypeHierarchy): " + shareTypeName2);
        
        // Apply type: Using Share
        siteActions.changeType(driver, shareTypeName).render();
        
        // Apply Child type1: Using Share
        siteActions.changeType(driver, shareTypeName2).render();
        
        // Apply Child type2: Using Share
        siteActions.changeType(driver, shareTypeName3).render();
        
        // Check the property Values
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", folderName);
        expectedProps.put(compositeTypeName, "");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");                 
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create 2 Models</li>
     * <li>Add aspect to model 1</li>
     * <li>Add aspect to model 2 with aspect1 as parent</li>
     * <li>Add aspect to model 2 with aspect2 as parent</li>
     * <li>Add Properties: Different Data Types / Requirements for Aspect 1, Aspect 2</li>
     * <li>Add No Properties: for Aspect 3</li>
     * <li>Activate the models</li>
     * <li>Add Aspect 1, Aspect 2 to a node</li>
     * <li>Check Props correctly applied</li>
     * <li>Remove Aspect 2 from Node</li>
     * <li>Add Aspect3 to the node</li>
     * <li>Check Parent Props from Aspect1, Aspect 2 are correctly applied</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl5")
    @Test(groups = "EnterpriseOnly", priority = 5)
    public void testAspectsFromDiffModels() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;
        String modelName2 = modelName + "2";

        String aspectName = "aspect" + testName;
        String compositeAspectName = modelName + ":" + aspectName;
        
        String aspectName2 = aspectName + "2";
        String compositeAspectName2 = modelName2 + ":" + aspectName2;
        
        String aspectName3 = aspectName + "3";
        String compositeAspectName3 = modelName2 + ":" + aspectName3;
        
        String propertyName = "prop" + testName;
        String propertyName2 = propertyName + "2";

        String contentName = "doc" + testName;

        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Models
        cmmActions.createNewModel(driver, modelName).render();
        cmmActions.createNewModel(driver, modelName2).render();
        
        // Activate the model: Models
        cmmActions.setModelActive(driver, modelName, true).render();
        cmmActions.setModelActive(driver, modelName2, true).render();

        // Add Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.createAspect(driver, aspectName).render();

        // Add Properties: Aspect 1
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, propertyName+"OptionalD", propertyName+"OptionalD", "", DataType.MlText, MandatoryClassifier.Optional, false, "A-Z").render();       
        cmmActions.createProperty(driver, propertyName+"NotEnfD", propertyName+"NotEnfD", "", DataType.Text, MandatoryClassifier.Mandatory, false, currentDate).render();
        
        // Apply Default Layout for Aspect1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // Add Aspects: Model2
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        cmmActions.createAspect(driver, aspectName2, getParentTypeAspectName(modelName, aspectName)).render();  
        cmmActions.createAspect(driver, aspectName3, getParentTypeAspectName(modelName2, aspectName2)).render();
        
        // Add Properties: Aspect 2
        cmmActions.viewProperties(driver, compositeAspectName2);
        cmmActions.createProperty(driver, propertyName2+"Optional", propertyName2+"Optional", "", DataType.Int, MandatoryClassifier.Optional, false, "").render();       
        cmmActions.createProperty(driver, propertyName2+"NotEnf", propertyName2+"NotEnf", "", DataType.Float, MandatoryClassifier.Mandatory, false, "").render();
        
        // Apply Default Layout for Aspect2
        cmmActions.viewTypesAspectsForModel(driver, modelName2).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName2);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName3);

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName()).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName);
        aspects.add(aspectName2);
        siteActions.addAspects(driver, aspects);
        
        // Check Properties
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName+"OptionalD", "A-Z");
        expectedProps.put(propertyName+"NotEnfD", currentDate);
        expectedProps.put(propertyName2+"Optional", "");
        expectedProps.put(propertyName2+"NotEnf", "");
        
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
        
        // Remove Aspect 2
        aspects = new ArrayList<String>();
        aspects.add(aspectName2);
        siteActions.removeAspects(driver, aspects);
        
        // Check Properties of Aspect 2 are removed
        expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName+"OptionalD", "A-Z");
        expectedProps.put(propertyName+"NotEnfD", currentDate);
        expectedProps.put(propertyName2+"Optional", notDisplayed);
        expectedProps.put(propertyName2+"NotEnf", notDisplayed);
        
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");        
        
        // Add Aspect 3: Which is the Child of Aspect 2
        aspects = new ArrayList<String>();
        aspects.add(aspectName3);
        siteActions.addAspects(driver, aspects);
        
        expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName+"OptionalD", "A-Z");
        expectedProps.put(propertyName+"NotEnfD", currentDate);
        expectedProps.put(propertyName2+"Optional", "");
        expectedProps.put(propertyName2+"NotEnf", "");
        
        // Verify properties for Aspect 1 and Aspect 2 are displayed
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add 2 Types</li>
     * <li>Add Properties to Type1: With Requirement: Mandatory Enforced, Default Specified</li>
     * <li>Add Properties to Type2: With Requirement: Mandatory Enforced, No Default Specified</li>
     * <li>Activate the model</li>
     * <li>Add Type 1 to a node - should succeed</li>
     * <li>Add Type 2 to a node - should succeed: SHA-1172</li>
     * <li>Check that the Props can be deleted for Type 1</li>
     * <li>Check that the Props can NOT be deleted for Type 2</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl6")
    @Test(groups = "EnterpriseOnly", priority = 6)
    public void testTypesWithMandatoryProperties() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String typeName = "type" + testName;
        
        String shareTypeName = getShareTypeName(modelName, typeName);
        String shareTypeName2 = getShareTypeName(modelName, typeName+"2");
        
        String compositeTypeName = modelName + ":" + typeName;
        String parentTypeName = getParentTypeAspectName(modelName, typeName);
        
        
        String propertyName = "prop" + testName + "Mandatory";
        String compositeTypePropertyName = modelName + ":" + propertyName;
        
        String contentName = "doc" + testName;

        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();
        
        // Activate the model: Model
        cmmActions.setModelActive(driver, modelName, true).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Aspects: Model
        cmmActions.createType(driver, typeName).render();
        cmmActions.createType(driver, typeName + "2", parentTypeName).render();

        // Add Properties: Type 1: Mandatory with Default Value
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, propertyName + "D", propertyName + "D", "", DataType.Boolean, MandatoryClassifier.MANDATORYENF, false, "true").render();

        // Add Properties: Type 2: Mandatory Property without Default value
        cmmActions.viewProperties(driver, compositeTypeName + "2");
        cmmActions.createProperty(driver, propertyName, propertyName, "", DataType.Double, MandatoryClassifier.MANDATORYENF, false, "").render();

        // Apply Default Layout for Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName + "2");        

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName()).render();

        // Change Type to Type 1:
        siteActions.changeType(driver, shareTypeName);
        Assert.assertFalse(siteActions.isTypeAvailable(driver, shareTypeName), "Expecting Type to be changed without mandatory prop value " + typeName);
        Assert.assertTrue(siteActions.isTypeAvailable(driver, shareTypeName2), "Expecting Type to be changed without mandatory prop value " + typeName+"2");

        // Check Properties: Type 1
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName + "D", "Yes");
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");

        // Change Type to Type 2: Expect Success
        siteActions.changeType(driver, shareTypeName2);

        // Check Properties: Type
        expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName, "");

        // Check Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
        
        // Navigate to Model Manager
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        
        // Try to Delete Properties for Type 1: Can not
        cmmActions.viewProperties(driver, compositeTypeName);
        ManagePropertiesPage propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName + "D", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName + "D"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
        
        // Try to Delete Properties for Type 2: Success
        cmmActions.viewProperties(driver, compositeTypeName+"2");
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName, okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName), "Error Deleting Property when not in use. Ref. Type: " + compositeTypeName+"2");
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add 2 Aspects</li>
     * <li>Add Properties to Aspect1: With Requirement: Mandatory Enforced, Default Specified</li>
     * <li>Add Properties to Aspect2: With Requirement: Mandatory Enforced, No Default Specified</li>
     * <li>Activate the model</li>
     * <li>Add Aspect 1 to a node - should succeed</li>
     * <li>Add Aspect 2 to a node - should fail</li>
     * <li>Check that the Props can not be deleted for Aspect 1</li>
     * <li>Check that the Props can be deleted for Aspect 2</li>
     * <li>Check that Aspect 1 can not be deleted</li>
     * <li>Check that Aspect 2 can now be added</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl7")
    @Test(groups = "EnterpriseOnly", priority = 7)
    public void testAspectsWithMandatoryProperties() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String aspectName = "aspect" + testName;
        String compositeAspectName = modelName + ":" + aspectName;
        String parentAspectName = getParentTypeAspectName(modelName, aspectName);
        
        String propertyName = "prop" + testName + "Mandatory";
        String compositeAspectPropertyName = modelName + ":" + propertyName;
        
        String contentName = "doc" + testName;

        File doc = siteUtil.prepareFile(contentName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();
        
        // Activate the model: Model
        cmmActions.setModelActive(driver, modelName, true).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Aspects: Model
        cmmActions.createAspect(driver, aspectName).render();
        cmmActions.createAspect(driver, aspectName + "2", parentAspectName).render();

        // Add Properties: Aspect 1: Mandatory with Default Value
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, propertyName + "D", propertyName + "D", "", DataType.Boolean, MandatoryClassifier.MANDATORYENF, false, "true").render();

        // Add Properties: Aspect 2: Mandatory Property without Default value
        cmmActions.viewProperties(driver, compositeAspectName + "2");
        cmmActions.createProperty(driver, propertyName, propertyName, "", DataType.Text, MandatoryClassifier.MANDATORYENF, false, "").render();

        // Apply Default Layout for Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName + "2");        

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.selectContent(driver, doc.getName()).render();

        // Add Aspect 2: Expect Success: As a result of SHA-1172
        List<String> aspects = new ArrayList<String>();
        aspects.add(aspectName + "2");
        siteActions.addAspects(driver, aspects);
        Assert.assertTrue(siteActions.isAspectAdded(driver, aspectName + "2"), "Aspect could not be added without specifying mandatory non enforced prop value " + aspectName + "2");

        // Check Properties: Aspect2
        Assert.assertEquals(cmmActions.getPropertyValue(driver, propertyName), valNotSpecified, "Property value not as expected");

        // Add Aspect 1: Expect Success
        aspects = new ArrayList<String>();
        aspects.add(aspectName);
        siteActions.addAspects(driver, aspects);

        // Check Properties: Aspect1
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put(propertyName + "D", "Yes"); // true changes to Yes on Share

        // Check Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
        
        // Navigate to Model Manager
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        
        // Try to Delete Properties for Aspect 1: Can not
        cmmActions.viewProperties(driver, compositeAspectName);
        ManagePropertiesPage propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName + "D", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName + "D"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
        
        // Try to Delete Properties for Aspect 2: Success
        cmmActions.viewProperties(driver, compositeAspectName+"2");
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName, okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName), "Error Deleting Property when not in use. Ref. Aspect: " + compositeAspectName+"2");
                
        // Try to Delete Aspect 1: Can not: Covered elsewhere
        // Try to Delete Aspect 2: Can not: Covered elsewhere
        
        // Try to Add Aspect 2: Success
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc.getName()).render();

        aspects = new ArrayList<String>();
        aspects.add(aspectName + "2");
        siteActions.addAspects(driver, aspects);
        Assert.assertTrue(siteActions.isAspectAdded(driver, aspectName + "2"), "Error Adding Aspect: " + aspectName + "2");
    }
}
