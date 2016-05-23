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
 * Test Class to test Applying Dynamic Model types and aspects using Cmis
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.alfresco.test.enums.CMISBinding;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class DynamicModelCmisTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(DynamicModelCmisTest.class);

    private String testName;

    private String testUser;

    private String[] authDetails;
    @Value("${default.site.name}") private String siteName;


    private CMISBinding bindingType = CMISBinding.ATOMPUB11;

    @BeforeClass(alwaysRun = true)
    public void setupTest() throws Exception
    {
        super.setupCmis();
        
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);

        testUser = username;

        authDetails = new String[] { testUser, DEFAULT_PASSWORD };
        
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
    public void testDeactivateActiveModelTypeRefByANode() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String typeName = "type";

        String cmisTypeName = "D:" + modelName + ":" + typeName;

        String docName = "testaddCMMType" + testName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        cmmActions.createType(driver, typeName).render();

        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();

        // Check that nodes can be created with Type and Aspects: Using Cmis
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName);

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docName, properties);

        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docName);
        Assert.assertNotNull(docNodeRef);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Should Not Succeed
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Remove Referenced / delete nodes
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver, docName);

        // Wait for Solr to index the deleted node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Fails as the document is still in the TrashCan
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Delete Node Permanently
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, docName, "documentLibrary");

        // Wait for Solr to index after the node has been deleted from trashcan
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft, "Issue with Model: " + modelName);

        // Can't use the type / aspect
        try
        {
        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docName, properties);
        Assert.fail("Expected Error creating document as Type not found");
        }
        catch(CmisObjectNotFoundException e)
        {
            Assert.assertTrue(e.getMessage().contains(String.format("Type '%s' is unknown", cmisTypeName)));
        }

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
     * <li>Add Aspect to a node</li>
     * <li>Try to Deactivate Model - should fail</li>
     * <li>Remove the Aspect from the node</li>
     * <li>Try to Deactivate Model - should succeed</li>
     * <li>Try to add the aspect to a node - should fail</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl2")
    @Test(groups = "EnterpriseOnly", priority = 2)
    public void testDeactivateActiveModelAspectRefByANode() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String aspectName = "aspect";

        String cmisAspectName = "P:" + modelName + ":" + aspectName;

        String docName = "testaddCMMAspect" + testName;

        File doc = siteUtil.prepareFile(docName);

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Aspects: Model1
        cmmActions.createAspect(driver, aspectName).render();

        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);

        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", doc.getName());
        Assert.assertNotNull(docNodeRef);
        
        // Add Aspect
        Map<String, Object> aspectProps = new HashMap<>();
        cmisApiClient.addAspect(bindingType, authDetails, "", docNodeRef, cmisAspectName, aspectProps);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Does Not Succeed
        cmmActions.navigateToModelManagerPage(driver);
        ModelManagerPage cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Remove Reference
        cmisApiClient.removeAspect(bindingType, authDetails, "", docNodeRef, cmisAspectName);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft, "Issue with Model: " + modelName);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }

    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add Type, aspect</li>
     * <li>Add one or more Properties for Types, Aspects</li>
     * <li>Activate the model</li>
     * <li>Create a node of type and add the Aspect with Properties</li>
     * <li>Deactivate Model - should fail</li>
     * <li>Remove the Aspect from the node</li>
     * <li>Try to Deactivate Model - should fail</li>
     * <li></li>
     * <li>Deactivate Model - should succeed</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl3")
    @Test(groups = "EnterpriseOnly", priority = 3)
    public void testDeactivateActiveModelPropsRefByNode() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        String aspectName = "aspect";

        String typePropertyName = "TypeName";
        String aspectPropertyName = "AspectName";
        String aspectPropertyName2 = "theme";
        
        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;

        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        
        String cmisTypePropName = modelName + ":" + typePropertyName;
        String cmisAspectPropName = modelName + ":" + aspectPropertyName;
        String cmisAspectPropName2 = modelName + ":" + aspectPropertyName2;
        
        String docName = "doc" + testName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type
        cmmActions.createType(driver, typeName).render();
        
        // Add Aspects
        cmmActions.createAspect(driver, aspectName).render();

        // Add Property: For Aspect
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName);
        cmmActions.createProperty(driver, aspectPropertyName2);

        // Add Property: For Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName);

        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);
        
        // Create Node of new Type: Using Cmis
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName);
        properties.put(cmisTypePropName, typePropertyName);

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docName, properties);

        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docName);
        Assert.assertNotNull(docNodeRef);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Add Aspect with Properties
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(cmisAspectPropName, testName);
        aspectProps.put(cmisAspectPropName2, "alfresco");
        
        cmisApiClient.addAspect(bindingType, authDetails, "", docNodeRef, cmisAspectName, aspectProps);

        // Deactivate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Remove Reference
        cmisApiClient.removeAspect(bindingType, authDetails, "", docNodeRef, cmisAspectName);

        // Wait for Solr to index
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Fails
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);
        
        // Remove Referenced / delete nodes Permanently
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver, docName);

        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, docName, "documentLibrary");
        
        // Wait for Solr to index the deleted node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft, "Issue with Model: " + modelName);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }

    /**
     * Test to test Properties as appear on share, when default values are set and not set
     * <li>1.  Optional properties are not specified</li>
     * <li>2.  Optional properties Empty String</li>
     * <li>3.  Optional properties with specific values</li>
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testStringPropertyDefaultVariants() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        
        String propertyName = "property";
        String propertyNameWithDefault = "propertyDefault";
        String defaultValue = "alfresco";

        String compositeTypeName = modelName + ":" + typeName;
        
        String compositePropName = modelName + ":" + propertyName;
        String compositePropNameWithDefault = modelName + ":" + propertyNameWithDefault;
        
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        
        String cmisTypePropName = modelName + ":" + propertyName;
        String cmisTypePropNameWithDefault = modelName + ":" + propertyNameWithDefault;
        
        String docPropNotSpecified = "doc" + testName;
        String docPropEmptyString = "doc2" + testName;
        String docPropSpecified = "doc3" + testName;

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: No Default Set
        cmmActions.createProperty(driver, propertyName, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
        
        // Create String Property2 for Type: Default Set
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNameWithDefault, "", "", DataType.Text, MandatoryClassifier.Optional, false, defaultValue).render();
        
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropName), "Unable to create String Property without default value");
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNameWithDefault), "Unable to create String Property with default value");
        
        // Create Doc1: Optional properties not specified
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docPropNotSpecified);

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docPropNotSpecified, properties);

        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docPropNotSpecified);
        Assert.assertNotNull(docNodeRef);
        
        // Create Doc2: Optional properties Empty String
        properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docPropEmptyString);
        properties.put(cmisTypePropName, "");
        properties.put(cmisTypePropNameWithDefault, "");

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docPropEmptyString, properties);

        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docPropEmptyString);
        Assert.assertNotNull(docNodeRef);
        
        // Create Doc3: Optional properties with specific values
        properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docPropSpecified);
        properties.put(cmisTypePropName, docPropSpecified);
        properties.put(cmisTypePropNameWithDefault, docPropSpecified);

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docPropSpecified, properties);

        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docPropSpecified);
        Assert.assertNotNull(docNodeRef);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Check properties on DocumentDetailsPage
        siteActions.openSiteDashboard(driver, siteName);       
        
        // Doc1: Optional properties not specified
        siteActions.openDocumentLibrary(driver);
        DocumentDetailsPage docDetailsPage = siteActions.selectContent(driver, docPropNotSpecified).render();
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropName)),propertyEmpty, "Property Values incorrect when optional properties not specified: " + cmisTypePropName);
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropNameWithDefault)), defaultValue, "Property Values incorrect when optional properties not specified: " + cmisTypePropNameWithDefault);
        
        // Doc2: Optional properties specified as Empty String
        siteActions.openDocumentLibrary(driver);
        docDetailsPage = siteActions.selectContent(driver, docPropEmptyString).render();
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropName)),propertyEmpty, "Property Values incorrect when optional properties not specified: " + cmisTypePropName);
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropNameWithDefault)),propertyEmpty, "Property Values incorrect when optional properties not specified: " + cmisTypePropNameWithDefault);
        
        // Doc3: Optional properties specified
        siteActions.openDocumentLibrary(driver);
        docDetailsPage = siteActions.selectContent(driver, docPropSpecified).render();
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropName)),docPropSpecified, "Property Values incorrect when optional properties not specified: " + cmisTypePropName);
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropNameWithDefault)),docPropSpecified, "Property Values incorrect when optional properties not specified: " + cmisTypePropNameWithDefault);       
    }
    
    /**
     * Test to test Properties on share, with 3 variants: Mandatory (En), Mandatory (not enforced), optional
     * <li> Create Model with a Type and 3 types of properties defined. </li>
     * <li> Test that Node of this type can be created with cmis and check the properties on share.</li>
     * <li>1.  Optional properties with no default set</li>
     * <li>2.  Optional properties with default set</li>
     * <li>3.  Mandatory (Not En) properties with no default set</li>
     * <li>4.  Mandatory (Not En) properties with default set</li>
     * <li>5.  Mandatory (Not En) properties no default set</li>
     * <li>6.  Mandatory (Not En) properties with default set</li>
     * @throws Exception
     */
    @AlfrescoTest(testlink="tobeaddeddel5")
    @Test(groups = "EnterpriseOnly", priority=5)
    public void testStringPropertyMandatoryVariants() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        
        String propertyOptional = "propertyOptional";
        String propertyNEMandatory = "propertyNotEnforced";
        
        String propertyOptionalWithDefault = "propertyOptionalDefault";
        String propertyNEMandatoryWithDefault = "propertyNEMandatoryDefault";
        
        String defaultValue = "alfresco";

        String compositeTypeName = modelName + ":" + typeName;
        
        String compositePropOptional = modelName + ":" + propertyOptional;
        String compositePropNEMandatory = modelName + ":" + propertyNEMandatory;
        
        String compositePropOptionalWithDefault = modelName + ":" + propertyOptionalWithDefault;
        String compositePropNEMandatoryWithDefault = modelName + ":" + propertyNEMandatoryWithDefault;
        
        String cmisTypeName = "D:" + modelName + ":" + typeName;
        
        String cmisTypePropOptional = modelName + ":" + propertyOptional;

        String cmisTypePropNEMandatory = modelName + ":" + propertyNEMandatory;
        
        String cmisTypePropOptionalWithDefault = modelName + ":" + propertyOptionalWithDefault;

        String cmisTypePropNEMandatoryWithDefault = modelName + ":" + propertyNEMandatoryWithDefault;
        
        // SHA-1172: Removed References to Property Type: Mandatory Enforced
        // String propertyMandatory = "propertyMandatory";
        // String propertyMandatoryWithDefault = "propertyMandatoryDefault";
        // String compositePropMandatory = modelName + ":" + propertyMandatory;
        // String compositePropMandatoryWithDefault = modelName + ":" + propertyMandatoryWithDefault;
        // String cmisTypePropMandatory = modelName + ":" + propertyMandatory;
        // String cmisTypePropMandatoryWithDefault = modelName + ":" + propertyMandatoryWithDefault;

        String docPropNotSpecified = "doc" + testName;
        String docPropMandatorySpecified = "doc2" + testName;
        String docPropSpecified = "doc3" + testName;

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);
        
        // Create String Property1 for Type: Optional
        cmmActions.createProperty(driver, propertyOptional, "", "", DataType.Text, MandatoryClassifier.Optional, false, "").render();                
        
        // Create String Property2 for Type: Optional With Default Value
        cmmActions.createProperty(driver, propertyOptionalWithDefault, "", "", DataType.Text, MandatoryClassifier.Optional, false, defaultValue + "OP").render();                
        
        // Create String Property3 for Type: Mandatory Not Enforced
        cmmActions.createProperty(driver, propertyNEMandatory, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "").render();
        
        // Create String Property4 for Type: Mandatory Not Enforced With Default
        ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyNEMandatoryWithDefault, "", "", DataType.Text, MandatoryClassifier.Mandatory, false, defaultValue + "NE").render();

        // SHA-1172: Removed references for Mandatory Enforced Properties
        /*
         *         
            // Create String Property5 for Type: Mandatory
            cmmActions.createProperty(driver, propertyMandatory, "", "", DataType.Text, MandatoryClassifier.MANDATORYENF, false, "").render();
                
            // Create String Property6 for Type: Mandatory With Default
            ManagePropertiesPage propListPage = cmmActions.createProperty(driver, propertyMandatoryWithDefault, "", "", DataType.Text, MandatoryClassifier.MANDATORYENF, false, defaultValue).render();
         *   
         */     
        // Check the properties are created
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptional), "Unable to create String Property: Optional");
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNEMandatory), "Unable to create String Property: Mandatory Not enforced");

        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropOptionalWithDefault), "Unable to create String Property: Optional with default value");
        Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropNEMandatoryWithDefault), "Unable to create String Property: Mandatory NE with default value");

        // SHA-1172: Removed references for Mandatory Enforced Properties
        // Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropMandatory), "Unable to create String Property: Mandatory");
        // Assert.assertTrue(propListPage.isPropertyRowDisplayed(compositePropMandatoryWithDefault), "Unable to create String Property: Mandatory with default value");
        
        // SHA-1172: Amended the expected results since Mandatory = Mandatory Not enforced now on. Amend test to expect a Pass
        // Create Node of new Type: Using Cmis: Do not specify Mandatory properties:         
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docPropNotSpecified);

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docPropNotSpecified, properties);
        
        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docPropNotSpecified);
        Assert.assertNotNull(docNodeRef, "Expected Document creation to Pass without Mandatory Not enforced Properties");
        
        // Create Node of new Type: Using Cmis: Specify Mandatory Property Without Default
        properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docPropMandatorySpecified);
        properties.put(cmisTypePropNEMandatory, "MandatoryNENoDefaultOnly");

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docPropMandatorySpecified, properties);

        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docPropMandatorySpecified);
        Assert.assertNotNull(docNodeRef);
        
        // Create Node of new Type: Using Cmis: Specify All Properties
        properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docPropSpecified);
        
        properties.put(cmisTypePropOptional, "AllProps");
        properties.put(cmisTypePropNEMandatory, "AllProps");
        
        properties.put(cmisTypePropOptionalWithDefault, "AllProps");
        properties.put(cmisTypePropNEMandatoryWithDefault, "AllProps");
        
        // SHA-1172: Removed references for Mandatory Enforced Properties
        // properties.put(cmisTypePropMandatory, "AllProps");
        // properties.put(cmisTypePropMandatoryWithDefault, "AllProps");

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docPropSpecified, properties);

        docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docPropSpecified);
        Assert.assertNotNull(docNodeRef);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Check properties on DocumentDetailsPage
        siteActions.openSiteDashboard(driver, siteName);       
        
        // Doc1
        siteActions.openDocumentLibrary(driver);
        Assert.assertTrue(siteActions.isFileVisible(driver, docPropNotSpecified), "Document should be present");

        // Doc2
        siteActions.openDocumentLibrary(driver);
        DocumentDetailsPage docDetailsPage = siteActions.selectContent(driver, docPropMandatorySpecified).render();        
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropOptionalWithDefault)), defaultValue + "OP", "Property Values incorrect when optional properties not specified: " + cmisTypePropOptionalWithDefault);
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropNEMandatoryWithDefault)), defaultValue + "NE", "Property Values incorrect when optional properties not specified: " + cmisTypePropNEMandatoryWithDefault);
        
        // SHA-1172: Removed references for Mandatory Enforced Properties
        // Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropMandatoryWithDefault)), defaultValue, "Property Values incorrect when optional properties not specified: " + cmisTypePropMandatoryWithDefault);
        // Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropMandatory)),"MandatoryNoDefaultOnly", "Property Values incorrect when optional properties not specified: " + cmisTypePropMandatory);
        
        // Doc3
        siteActions.openDocumentLibrary(driver);
        docDetailsPage = siteActions.selectContent(driver, docPropSpecified).render();
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropOptional)),"AllProps", "Property Values incorrect for Optional Property");
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropNEMandatory)),"AllProps", "Property Values incorrect for NE Mandatory Property");
        
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropOptionalWithDefault)),"AllProps", "Property Values incorrect for Optional Property");
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropNEMandatoryWithDefault)),"AllProps", "Property Values incorrect for NE Mandatory Property");
        
        // SHA-1172: Removed references for Mandatory Enforced Properties
        // Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropMandatory)),"AllProps", "Property Values incorrect for Mandatory Property");
        // Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropMandatoryWithDefault)),"AllProps", "Property Values incorrect for Mandatory Property");
       
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add Type, aspect</li>
     * <li>Add one or more Properties for Types, Aspects</li>
     * <li>Activate the model</li>
     * <li>Create a document node of type and add the Aspect with Properties</li>
     * <li>Check Properties</li>
     * <li>Update Properties and check Property values on Share</li>
     * <li>Deactivate Model - should fail</li>
     * <li>Remove the Aspect from the node</li>
     * <li>Try to Deactivate Model - should fail</li>
     * <li>Remove Node from Doclib and TrashCan</li>
     * <li>Deactivate Model - should succeed</li>
     * <li>Delete Model</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl6")
    @Test(groups = "EnterpriseOnly", priority = 6)
    public void testStringPropertyValueUpdate() throws Exception
    {
        String testName = getUniqueTestName();
        String modelName = "model" + testName;
        
        String typeName = "type";
        String aspectName = "aspect";

        String typePropertyName = "TypeName";
        String aspectPropertyName = "AspectName";
        String aspectPropertyName2 = "theme";
        
        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;

        String cmisTypeName = "D:" + modelName + ":" + typeName;
        String cmisAspectName = "P:" + modelName + ":" + aspectName;
        
        String cmisTypePropName = modelName + ":" + typePropertyName;
        String cmisAspectPropName = modelName + ":" + aspectPropertyName;
        String cmisAspectPropName2 = modelName + ":" + aspectPropertyName2;
        
        String docName = "doc" + testName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model
        ModelManagerPage cmmPage = cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type
        cmmActions.createType(driver, typeName).render();
        
        // Add Aspects
        cmmActions.createAspect(driver, aspectName).render();

        // Add Property: For Aspect
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName);
        cmmActions.createProperty(driver, aspectPropertyName2);

        // Add Property: For Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName);

        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);
        
        // Create Node of new Type: Using Cmis
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, docName);
        properties.put(cmisTypePropName, typePropertyName);

        cmisApiClient.createDocument(bindingType, authDetails, testUser, "", siteName, docName, properties);

        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", docName);
        Assert.assertNotNull(docNodeRef);

        // Wait for Solr to index the newly created node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);

        // Check Properties for Type
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        DocumentDetailsPage docDetailsPage = siteActions.selectContent(driver, docName).render();

        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropName)), typePropertyName, "Property Values incorrect: " + cmisTypePropName);

        // Add Aspect with Properties
        Map<String, Object> aspectProps = new HashMap<>();
        aspectProps.put(cmisAspectPropName, testName);
        aspectProps.put(cmisAspectPropName2, "alfresco");
        
        cmisApiClient.addAspect(bindingType, authDetails, "", docNodeRef, cmisAspectName, aspectProps);
        
        // Wait for Solr
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Check Properties for Aspect
        siteActions.openDocumentLibrary(driver);
        docDetailsPage = siteActions.selectContent(driver, docName).render();
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisAspectPropName2)), "alfresco", "Property Values incorrect when optional properties not specified: " + cmisAspectPropName2);

        // Check Type Properties are still visible and correct
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropName)), typePropertyName, "Property Values incorrect: " + cmisTypePropName);

        // Update Property Value
        aspectProps = new HashMap<>();
        aspectProps.put(cmisAspectPropName2, "alfresco-cmm");
        aspectProps.put(cmisTypePropName, "alfresco-cmm");
        cmisApiClient.updateProperties(bindingType, authDetails, "", docNodeRef, aspectProps);
        
        // Refresh DetailsPage
        driver.navigate().refresh();
        docDetailsPage = factoryPage.getPage(driver).render();
        
        // Check updated Properties
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisTypePropName)), "alfresco-cmm", "Property Values incorrect when optional properties not specified: " + cmisTypePropName);
        Assert.assertEquals(docDetailsPage.getProperties().get(getDocDetailsPropName(cmisAspectPropName2)), "alfresco-cmm", "Property Values incorrect when optional properties not specified: " + cmisAspectPropName2);
        
        // Deactivate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);

        // Remove Reference
        cmisApiClient.removeAspect(bindingType, authDetails, "", docNodeRef, cmisAspectName);

        // Wait for Solr to index the changes
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Fails
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive, "Issue with Model: " + modelName);
        
        // Remove Referenced / delete nodes Permanently
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver, docName);

        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, docName, "documentLibrary");
        
        // Wait for Solr to index the deleted node
        cmmActions.webDriverWait(driver, SOLR_WAIT_TIME);
        
        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusDraft, "Issue with Model: " + modelName);

        // Delete the model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.deleteModel(driver, modelName);
    }
    
    /**
     * Test: cm:folder: Create using cmis, Check via Share
     * <ul>
     * <li>Create a Model</li>
     * <li>Add type T1 with Parent = cm:folder</li>
     * <li>Add Property to the Type</li>
     * <li>Activate the model</li>
     * <li>Create node of Type T1 using CMIS</li>
     * <li>Check the Properties are correctly applied using Share</li>
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl7")
    @Test(groups = "EnterpriseOnly", priority = 7)
    public void testNodeParentTypeCmFolder() throws Exception
    {
        String testName = getUniqueTestName();

        String modelName = "model" + testName;

        String typeName = "type";

        String compositeTypeName = modelName + ":" + typeName;
        
        String cmisTypeName = "F:" + compositeTypeName;   
        
        String propertyName = typeName;

        String contentName = "testaddCMMType" + testName;

        loginAs(driver, new String[] { testUser });

        cmmActions.navigateToModelManagerPage(driver);

        // Create New Draft Model: Model1
        cmmActions.createNewModel(driver, modelName).render();

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Types: Model1
        cmmActions.createType(driver, typeName, "cm:folder (Folder)").render();      
        
        // Create Property for Type
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, propertyName);

        // Activate the model: Model1
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true).render();

        // Check that nodes can be created with Type: cm:folder: Using Cmis
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, cmisTypeName);
        properties.put(PropertyIds.NAME, contentName);
        properties.put(compositeTypeName, "alfresco");

        cmisApiClient.createFolder(bindingType, authDetails, testUser, "", siteName, properties);

        String docNodeRef = cmisApiClient.getContentNodeRef(bindingType, authDetails, "", siteName, "", contentName);
        Assert.assertNotNull(docNodeRef);
        
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        DetailsPage detailsPage = siteActions.viewDetails(driver, contentName).render();

        // Check the Properties using Share
        Assert.assertEquals(detailsPage.getProperties().get(getDocDetailsPropName(compositeTypeName)), "alfresco", "Incorrect Property values for : " + contentName);   
        
    }
}
