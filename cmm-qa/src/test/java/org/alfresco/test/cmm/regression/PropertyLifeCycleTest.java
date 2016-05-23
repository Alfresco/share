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
 * Test Class to test Property Usage
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.user.TrashCanValues;
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
public class PropertyLifeCycleTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(PropertyLifeCycleTest.class);
    
    private String testName;
    
    private String testUser;
    private String siteName;
    
    private String modelStatusActive = "Active";
    private String modelStatusInactive = "Inactive";
    
    private String okAction = "Ok";
    
    protected String modelName = "model";
   
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);  
        
        modelName = "model" + testName + System.currentTimeMillis();
        siteName = "cmm" + System.currentTimeMillis();
        testUser = username;
        loginAs(driver, new String[] { testUser });
        siteActions.createSite(driver, siteName, null, null);
        logout(driver);
/*        
        testUser = getUserNameFreeDomain(testName+ System.currentTimeMillis());
        
        //Login as Admin to create a new user
        loginAs(driver, new String[] {username});
        
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
    
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Add Properties</li>
     * <li>Try to delete Properties: Can Delete Properties for a Draft Model: Properties not in use</li>
     * <li>Activate the model</li>
     * <li>Apply Default Layout to Forms for Type and Aspect</li>
     * <li>Apply Type / Aspect</li> 
     * <li>Try to delete Properties: Default value NOT specified: Can Delete Properties: As Not explicitly applied by the user</li>
     * <li>Try to delete Properties: Default value specified: Can Not Delete Properties: As default value is applied</li>
     * <li>Add Properties to Type / Aspect, with and without defaults</li>
     * <li>Check that added Properties are displayed correctly on Share</li>
     * <li>Try to delete Properties: Default value NOT specified: Can Delete Properties: As not explicitly applied by the user</li>
     * <li>Try to delete Properties: Default value specified: Can Delete Properties: As not explicitly applied by the user</li>
     * <li>Apply Properties: Edit Properties + explicitly Save the properties for the Node </li>
     * <li>Try to delete Properties: Default value NOT specified: Can Not Delete Properties: As explicitly applied by the user</li>
     * <li>Try to delete Properties: : Default value specified: Can Not Delete Properties: As explicitly applied by the user</li>
     * <li>Deactivate Model: Fails as Model is referenced by the node</li>
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeaddeddupl1")
    @Test(groups = "EnterpriseOnly", priority = 1)
    public void testDeletePropertiesNoConstraints() throws Exception
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
        
        String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";;
        String shareAspectName = getShareAspectName(modelName, aspectName);

        String docName = "doc" + testName;
        
        File doc = siteUtil.prepareFile(docName, docName);
        File doc2 = siteUtil.prepareFile(docName+"2", docName+"2");

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

        // Add Properties 1-3: For Type
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName+"1");
        cmmActions.createProperty(driver, typePropertyName+"2", "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "2015-04-11").render();
        cmmActions.createProperty(driver, typePropertyName+"3");
        
        // Add Properties 1-3: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName+"1");
        cmmActions.createProperty(driver, aspectPropertyName+"2", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "2015-04-11T10:26:36.00+01:00").render();
        cmmActions.createProperty(driver, aspectPropertyName+"3");
        
        // Check that Property is created
        ManagePropertiesPage propertyListPage = cmmActions.createProperty(driver, aspectPropertyName+"4").render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Creating Property. Ref Type: " + compositeTypeName);
        
        // Check that Property can be deleted when not used (Draft Model)
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Deleting Property when in use. Ref Type: " + compositeTypeName);
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Apply Default Form Layout: Aspect    
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
       
        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // Create a Document Node in the Site: Apply Type to a Node
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.uploadFile(driver, doc2);
        DetailsPage detailsPage = siteActions.selectContent(driver, doc.getName()).render();
        
        // Check that nodes can be given the new Type and Aspects: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        detailsPage = siteActions.addAspects(driver, aspects);

        // Try to Delete Properties: Property not yet applied / saved for the node
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Error Deleting Property when in use. Ref Type: " + compositeTypeName);

        // Used Property: with default value: can not be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Property deleted when in use. Ref Type: " + compositeTypeName);

        // Check that Property can be deleted: When unused & Model is Active: For Aspect
        cmmActions.viewProperties(driver, compositeAspectName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"1"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
         
        // Used Property: with default value: can not be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"2", okAction).render();
        
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"2"), "Property deleted when in use. Ref Type: " + compositeAspectName);
        
        // Add Properties 4-6: For Type:
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName+"4");
        cmmActions.createProperty(driver, typePropertyName+"5", "", "", DataType.Boolean, MandatoryClassifier.Mandatory, false, "true").render();
        cmmActions.createProperty(driver, typePropertyName+"6", "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "10-10-2010").render();

        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Add Properties 4-6: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName+"4");
        cmmActions.createProperty(driver, aspectPropertyName+"5", "", "", DataType.Int, MandatoryClassifier.Optional, false, "0").render();
        cmmActions.createProperty(driver, aspectPropertyName+"6", "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "anytext").render();

        // Apply Default Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // TODO: Check that new properties are displayed on Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc.getName());
        
        // Check New Property 6: appears on Share
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(compositeTypePropertyName + "2", "2015-04-11");
        expectedProps.put(compositeTypePropertyName + "3", "");
        expectedProps.put(compositeTypePropertyName + "4", "");
        expectedProps.put(compositeTypePropertyName + "5", "Yes");  // true changes to Yes
        expectedProps.put(compositeTypePropertyName + "6", "10-10-2010");
        expectedProps.put(compositeAspectPropertyName + "2", "2015-04-11T10:26:36.00+01:00");
        expectedProps.put(compositeAspectPropertyName + "3", "");
        expectedProps.put(compositeAspectPropertyName + "4", "");
        expectedProps.put(compositeAspectPropertyName + "5", "0");
        expectedProps.put(compositeAspectPropertyName + "6", "anytext");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
        
        // Check that Property can be deleted: When not saved explicitly: For Type
        // Property without Default Value: Can be deleted
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"4"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
        
        // Property with Default Value: Can be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"5", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"5"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
        
        // Check that Property can be deleted: When not saved explicitly: For Aspect
        // Property without Default Value: Can be deleted
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
     
        // Property with Default Value: Can be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"5", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"5"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);

        // Edit - Save : Apply Properties on Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc.getName());
        
        // Check New Property 6: appears on Share
        expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(compositeTypePropertyName + "2", "2015-04-11");
        expectedProps.put(compositeTypePropertyName + "6", "10-10-2010");
        expectedProps.put(compositeAspectPropertyName + "2", "2015-04-11T10:26:36.00+01:00");
        expectedProps.put(compositeAspectPropertyName + "6", "anytext");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
                
        // Apply - Save Properties
        siteActions.getEditPropertiesPage(driver, doc.getName()).render();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        siteActions.editNodeProperties(driver, true, properties);             
        
        // Try Deleting properties: When Applied / Saved: For Type
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName).render();
        
        // Property without Default Value        
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"3", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"3"), "Error Deleting Property when in use. Ref. Aspect: " + compositeTypeName);
     
        // Property with Default Value
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"6", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"6"), "Error Deleting Property when in use. Ref. Aspect: " + compositeTypeName);
     
        Assert.assertEquals(3, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Type");
        
        // Try Deleting properties: When Applied / Saved: For Aspect
        propertyListPage = cmmActions.viewProperties(driver, compositeAspectName).render();
        
        // Property without Default Value: But explicitly saved: Can not be deleted        
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"3", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"3"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
     
        // Property with Default Value: But explicitly saved: Can not be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"6", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"6"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
     
        Assert.assertEquals(3, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Aspect");
        
        // Deactivate Model: Fails
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);
    }
    
    /**
     * Test:
     * <ul>
     * <li>Create a Draft Model</li>
     * <li>Add types and aspects</li>
     * <li>Add Properties</li>
     * <li>Activate the model</li>
     * <li>Apply Default Layout to Forms for Type and Aspect</li>
     * <li>Apply Type / Aspect to the Node</li> 
     * <li>Apply Properties: Edit Properties + explicitly Save the properties for the Node </li>
     * <li>Remove Aspect, Delete the Node: Node is in the Trash can</li>
     * <li>Try to delete Properties: Default value NOT specified: Can Not Delete Properties: As explicitly applied by the user</li>
     * <li>Try to delete Properties: : Default value specified: Can Not Delete Properties: As explicitly applied by the user</li>
     * <li>Delete the Node: From Trash can</li>
     * <li>Try to delete Properties: Default value NOT specified: Can Delete Properties: As reference removed</li>
     * <li>Try to delete Properties: : Default value specified: Can Delete Properties: As reference removed</li>
     * <li>Deactivate Model: Succeeds as Model is not referenced by the node</li>
     * <li>Delete Model: Succeeds</li>
     * </ul>
     * @throws Exception 
     */
    @AlfrescoTest(testlink = "tobeaddeddupl2")
    @Test(groups = "EnterpriseOnly", priority = 2)
    public void testPropertiesTrashCanRelated() throws Exception
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
        
        String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";;
        String shareAspectName = getShareAspectName(modelName, aspectName);

        String docName = "doc" + testName;
        
        File doc = siteUtil.prepareFile(docName, docName);
        File doc2 = siteUtil.prepareFile(docName+"2", docName+"2");

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

        // Add Properties 1-3: For Type
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, typePropertyName+"1");
        cmmActions.createProperty(driver, typePropertyName+"2", "", "", DataType.Text, MandatoryClassifier.Mandatory, false, "2015-04-11").render();
        cmmActions.createProperty(driver, typePropertyName+"3");
        
        // Add Properties 1-3: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createProperty(driver, aspectPropertyName+"1");
        cmmActions.createProperty(driver, aspectPropertyName+"2", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "2015-04-11T10:26:36.00+01:00").render();
        cmmActions.createProperty(driver, aspectPropertyName+"3");
          
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Apply Default Form Layout: Aspect    
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
       
        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // Create a Document Node in the Site: Apply Type to a Node
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.uploadFile(driver, doc2);
        DetailsPage detailsPage = siteActions.selectContent(driver, doc.getName()).render();
        
        // Check that nodes can be given the new Type and Aspects: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        detailsPage = siteActions.addAspects(driver, aspects);
 
        // Edit - Save : Apply Properties on Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc.getName());
        
        // Check New Property 6: appears on Share
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(compositeTypePropertyName + "1", "");
        expectedProps.put(compositeTypePropertyName + "2", "2015-04-11");
        expectedProps.put(compositeTypePropertyName + "3", "");
        expectedProps.put(compositeAspectPropertyName + "1", "");
        expectedProps.put(compositeAspectPropertyName + "2", "2015-04-11T10:26:36.00+01:00");
        expectedProps.put(compositeAspectPropertyName + "3", "");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
                
        // Apply - Save Properties
        siteActions.getEditPropertiesPage(driver, doc.getName()).render();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        siteActions.editNodeProperties(driver, true, properties);  
        
        // Remove Aspect:
        siteActions.removeAspects(driver, aspects);
        
        // Try to delete Properties for Aspect: Can be deleted: As Aspect removed
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName).render();
        
        // Property without Default Value     
        ManagePropertiesPage propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"1"), "Error Deleting Property when not in use. Ref. Aspect: " + compositeAspectName);
     
        // Property with Default Value
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"2", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"2"), "Error Deleting Property when not in use. Ref. Aspect: " + compositeAspectName);
     
        Assert.assertEquals(1, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Aspect");
        
        // Delete the Node: Node is in the Trash can
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver, doc.getName());

        // Try Deleting properties: When Node is in the Trash can
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName).render();
        
        // Property without Default Value: Can not delete as prop is still being referenced        
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
     
        // Property with Default Value: Can not delete as prop is still being referenced    
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
     
        Assert.assertEquals(3, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Type");
        
        // Delete the Node: From Trash can
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, doc.getName(), "documentLibrary");
        
        // Try Deleting properties: When Node is removed from the Trash can
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName).render();
        
        // Property without Default Value: Can delete: As Referenced removed        
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Error Deleting Property when Not in use. Ref. Type: " + compositeTypeName);
     
        // Property with Default Value: Can delete: As Referenced removed   
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Error Deleting Property when Not in use. Ref. Type: " + compositeTypeName);
     
        Assert.assertEquals(1, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Type");
               
        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusInactive);
        
        // Delete Model: Succeeds
        cmmActions.deleteModel(driver, modelName);
    }

    /**
     * Delete Properties With Constraint
     */
    @AlfrescoTest(testlink = "tobeaddeddupl3")
    @Test(groups = "EnterpriseOnly", priority = 3)
    public void testDeletePropertiesWithConstraints() throws Exception
    {
        //String testName = getUniqueTestName();
        String modelName = "model" + System.currentTimeMillis();
        String name = "name"+System.currentTimeMillis();            
        String siteName = "site"+System.currentTimeMillis();
        
        ContentDetails contentDetails =new ContentDetails();
        contentDetails.setName(name);      

        String typeName = "type" + testName;
        String aspectName = "aspect" + System.currentTimeMillis();
        String typePropertyName = "propType";
        String aspectPropertyName = "propAspect";

        String compositeTypeName = modelName + ":" + typeName;
        String compositeAspectName = modelName + ":" + aspectName;
        String compositeTypePropertyName = modelName + ":" + typePropertyName;
        String compositeAspectPropertyName = modelName + ":" + aspectPropertyName;
        
        String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";;
        String shareAspectName = getShareAspectName(modelName, aspectName);
        
        ConstraintDetails constraintLength = new ConstraintDetails();
        constraintLength.setType(ConstraintTypes.MINMAXLENGTH);        
        constraintLength.setMinValue(1);
        constraintLength.setMaxValue(5);       
                
        ConstraintDetails constraintRegexEmail = new ConstraintDetails();
        constraintRegexEmail.setType(ConstraintTypes.REGEX);
        constraintRegexEmail.setValue(".*@alfresco.com");
        constraintRegexEmail.setMatchRequired(true);       
                
        // Add Property With Constraint: List
        ConstraintDetails constraintList = new ConstraintDetails();
        constraintList.setType(ConstraintTypes.LIST);
        constraintList.setValue("food\ndrink\nshelter\nwork");
        constraintList.setSorted(true);       
        
        //Min max value constraint 
        ConstraintDetails constraintMinMaxValue = new ConstraintDetails();
        constraintMinMaxValue.setType(ConstraintTypes.MINMAXVALUE);        
        constraintMinMaxValue.setMinValue(-1);
        constraintMinMaxValue.setMaxValue(5);
        
        // Add Property With Constraint: List
        ConstraintDetails constraintSortedList = new ConstraintDetails();
        constraintSortedList.setType(ConstraintTypes.LIST);
        constraintSortedList.setValue("-1\n11\n-5\n0");
        constraintSortedList.setSorted(true);
                       

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

        // Add Properties 1-3: For Type
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"1", "", "",DataType.Text, MandatoryClassifier.Mandatory, false,"", constraintList).render();
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"2", "", "",DataType.Text, MandatoryClassifier.Mandatory, true, "fi,ve", constraintLength).render();
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"3", "", "",DataType.Text, MandatoryClassifier.Mandatory, true, "", constraintRegexEmail).render();
        
        // Add Properties 1-3: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"1", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "",constraintLength).render();
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"2", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "fives",constraintLength).render();
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"3", "", "", DataType.MlText, MandatoryClassifier.Optional, true, "",constraintLength).render();
        
        // Check that Property is created
        ManagePropertiesPage propertyListPage = cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"4", "", "",DataType.Text, MandatoryClassifier.Mandatory, true, "", constraintRegexEmail).render();       
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Creating Property. Ref Type: " + compositeTypeName);
        
        // Check that Property can be deleted when not used (Draft Model)
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Deleting Property when in use. Ref Type: " + compositeTypeName);
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Apply Default Form Layout: Aspect    
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
       
        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // Create a Document Node in the Site: Apply Type to a Node
        /*siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.uploadFile(driver, doc2);
        DetailsPage detailsPage = siteActions.selectContent(driver, doc.getName()).render();*/
        
        //Create site, content and select content
        siteActions.createSite(driver,siteName, "", "");
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.createContent(driver,contentDetails, ContentType.PLAINTEXT);
        DetailsPage detailsPage = siteActions.selectContent(driver, name).render();
        
        // Check that nodes can be given the new Type and Aspects: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        detailsPage = siteActions.addAspects(driver, aspects);

        // Try to Delete Properties: Property not yet applied / saved for the node
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Error Deleting Property when in use. Ref Type: " + compositeTypeName);

        // Used Property: with default value: can not be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Property deleted when in use. Ref Type: " + compositeTypeName);

        // Check that Property can be deleted: When unused & Model is Active: For Aspect
        cmmActions.viewProperties(driver, compositeAspectName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"1"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
         
        // Used Property: with default value: can not be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"2", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"2"), "Property deleted when in use. Ref Type: " + compositeAspectName);
        
        // Add Properties 4-6: For Type:
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"4", "", "",DataType.Int, MandatoryClassifier.Mandatory, true,"-1", constraintMinMaxValue).render();
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"5", "", "", DataType.Long, MandatoryClassifier.Optional, false, "",constraintMinMaxValue).render();
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"6", "", "", DataType.Float, MandatoryClassifier.Mandatory, false, "5",constraintMinMaxValue).render();

        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Add Properties 4-6: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"4", "", "",DataType.Double, MandatoryClassifier.Mandatory, false, "5",constraintMinMaxValue).render();
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"5", "", "", DataType.Int, MandatoryClassifier.Optional, false, "0",constraintMinMaxValue).render();
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"6", "", "", DataType.Int, MandatoryClassifier.Mandatory, false, "11",constraintSortedList).render();

        // Apply Default Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
        
        // TODO: Check that new properties are displayed on Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, name);
        
        // Check New Property 6: appears on Share
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", name);
        expectedProps.put(compositeTypePropertyName + "2", "fi,ve");
        expectedProps.put(compositeTypePropertyName + "3", "");
        expectedProps.put(compositeTypePropertyName + "4", "-1");
        expectedProps.put(compositeTypePropertyName + "5", ""); 
        expectedProps.put(compositeTypePropertyName + "6", "5");
        expectedProps.put(compositeAspectPropertyName + "2", "fives");
        expectedProps.put(compositeAspectPropertyName + "3", "");
        expectedProps.put(compositeAspectPropertyName + "4", "5");
        expectedProps.put(compositeAspectPropertyName + "5", "0");
        expectedProps.put(compositeAspectPropertyName + "6", "11");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");
        
        // Check that Property can be deleted: When not saved explicitly: For Type
        // Property without Default Value: Can be deleted
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"4"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
        
        // Property with Default Value: Can be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"5", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"5"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
        
        // Check that Property can be deleted: When not saved explicitly: For Aspect
        // Property without Default Value: Can be deleted
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"4", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"4"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
     
        // Property with Default Value: Can be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"5", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"5"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);

        // Edit - Save : Apply Properties on Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, name);
        
        // Check New Property 6: appears on Share
        expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", name);
        expectedProps.put(compositeTypePropertyName + "2", "fi,ve");
        expectedProps.put(compositeTypePropertyName + "6", "5");
        expectedProps.put(compositeAspectPropertyName + "2", "fives");
        expectedProps.put(compositeAspectPropertyName + "6", "11");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
                
        // Apply - Save Properties
        siteActions.getEditPropertiesPage(driver, name).render();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(typePropertyName+"3", "a@alfresco.com");
        properties.put(aspectPropertyName+"3", "a");
        siteActions.editNodeProperties(driver, true, properties);             
        
        // Try Deleting properties: When Applied / Saved: For Type
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName).render();
        
        // Property without Default Value        
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"3", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"3"), "Error Deleting Property when in use. Ref. Aspect: " + compositeTypeName);
     
        // Property with Default Value
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"6", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"6"), "Error Deleting Property when in use. Ref. Aspect: " + compositeTypeName);
     
        Assert.assertEquals(3, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Type");
        
        // Try Deleting properties: When Applied / Saved: For Aspect
        propertyListPage = cmmActions.viewProperties(driver, compositeAspectName).render();
        
        // Property without Default Value: But explicitly saved: Can not be deleted        
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"3", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"3"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
     
        // Property with Default Value: But explicitly saved: Can not be deleted
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"6", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"6"), "Error Deleting Property when in use. Ref. Aspect: " + compositeAspectName);
     
        Assert.assertEquals(3, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Aspect");
        
        // Deactivate Model: Fails
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);
    }
    
    @AlfrescoTest(testlink = "tobeaddeddupl2")
    @Test(groups = "EnterpriseOnly", priority = 4)
    public void testPropwithconsTrashCanRelated() throws Exception
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
        
        String shareTypeName = typeName + " (" + modelName + ":" + typeName + ")";;
        String shareAspectName = getShareAspectName(modelName, aspectName);

        String docName = "doc" + testName;
        
        File doc = siteUtil.prepareFile(docName, docName);
        File doc2 = siteUtil.prepareFile(docName+"2", docName+"2");        
       
        ConstraintDetails constraintLength = new ConstraintDetails();
        constraintLength.setType(ConstraintTypes.MINMAXLENGTH);        
        constraintLength.setMinValue(1);
        constraintLength.setMaxValue(5);       
                
        ConstraintDetails constraintRegexEmail = new ConstraintDetails();
        constraintRegexEmail.setType(ConstraintTypes.REGEX);
        constraintRegexEmail.setValue(".*@alfresco.com");
        constraintRegexEmail.setMatchRequired(true);       
                
        // Add Property With Constraint: List
        ConstraintDetails constraintListSorted = new ConstraintDetails();
        constraintListSorted.setType(ConstraintTypes.LIST);
        constraintListSorted.setValue("food\ndrink\nshelter\nwork");
        constraintListSorted.setSorted(true);            

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

        // Add Properties 1-3: For Type
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"1", "", "",DataType.Text, MandatoryClassifier.Mandatory, false,"", constraintListSorted).render();
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"2", "", "",DataType.Text, MandatoryClassifier.Mandatory, true, "fi,ve", constraintLength).render();
        cmmActions.createPropertyWithConstraint(driver, typePropertyName+"3", "", "",DataType.Text, MandatoryClassifier.Mandatory, true, "", constraintRegexEmail).render();
        
        // Add Properties 1-3: For Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"1", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "",constraintLength).render();
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"2", "", "", DataType.MlText, MandatoryClassifier.Mandatory, false, "fives",constraintLength).render();
        cmmActions.createPropertyWithConstraint(driver, aspectPropertyName+"3", "", "", DataType.MlText, MandatoryClassifier.Optional, true, "",constraintLength).render();
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);
        
        // Apply Default Form Layout: Aspect    
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);
       
        // Activate the Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, true).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusActive);

        // Create a Document Node in the Site: Apply Type to a Node
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.uploadFile(driver, doc2);
        DetailsPage detailsPage = siteActions.selectContent(driver, doc.getName()).render();
        
        // Check that nodes can be given the new Type and Aspects: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        detailsPage = siteActions.addAspects(driver, aspects);
 
        // Edit - Save : Apply Properties on Share
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc.getName());
        
        // Check New Property 6: appears on Share
        Map<String, Object> expectedProps = new HashMap<String, Object>();
        expectedProps.put("Name", doc.getName());
        expectedProps.put(compositeTypePropertyName + "1", "");
        expectedProps.put(compositeTypePropertyName + "2", "fi,ve");
        expectedProps.put(compositeTypePropertyName + "3", "");
        expectedProps.put(compositeAspectPropertyName + "1", "");
        expectedProps.put(compositeAspectPropertyName + "2", "fives");
        expectedProps.put(compositeAspectPropertyName + "3", "");
        
        // Compare Properties
        Assert.assertTrue(cmmActions.compareCMProperties(driver, expectedProps), "Property values not as expected");   
                
        // Apply - Save Properties
        siteActions.getEditPropertiesPage(driver, doc.getName()).render();
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(typePropertyName + "3", "someone@alfresco.com");
        properties.put(aspectPropertyName + "1", "1");
        properties.put(aspectPropertyName + "3", "enter");
        siteActions.editNodeProperties(driver, true, properties);  
        
        // Remove Aspect:
        siteActions.removeAspects(driver, aspects);
        
        // Try to delete Properties for Aspect: Can be deleted: As Aspect removed
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeAspectName).render();
        
        // Property without Default Value     
        ManagePropertiesPage propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"1"), "Error Deleting Property when not in use. Ref. Aspect: " + compositeAspectName);
     
        // Property with Default Value
        propertyListPage = cmmActions.deleteProperty(driver, compositeAspectPropertyName+"2", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeAspectPropertyName+"2"), "Error Deleting Property when not in use. Ref. Aspect: " + compositeAspectName);
     
        Assert.assertEquals(1, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Aspect");
        
        // Delete the Node: Node is in the Trash can
        siteActions.openSiteDashboard(driver, siteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.deleteContentInDocLib(driver, doc.getName());

        // Try Deleting properties: When Node is in the Trash can
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName).render();
        
        // Property without Default Value: Can not delete as prop is still being referenced        
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
     
        // Property with Default Value: Can not delete as prop is still being referenced    
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertTrue(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Error Deleting Property when in use. Ref. Type: " + compositeTypeName);
     
        Assert.assertEquals(3, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Type");
        
        // Delete the Node: From Trash can
        userActions.navigateToTrashCan(driver);
        userActions.deleteFromTrashCan(driver, TrashCanValues.FILE, doc.getName(), "documentLibrary");
        
        // Try Deleting properties: When Node is removed from the Trash can
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.viewProperties(driver, compositeTypeName).render();
        
        // Property without Default Value: Can delete: As Referenced removed        
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"1", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"1"), "Error Deleting Property when Not in use. Ref. Type: " + compositeTypeName);
     
        // Property with Default Value: Can delete: As Referenced removed   
        propertyListPage = cmmActions.deleteProperty(driver, compositeTypePropertyName+"2", okAction).render();
        Assert.assertFalse(propertyListPage.isPropertyRowDisplayed(compositeTypePropertyName+"2"), "Error Deleting Property when Not in use. Ref. Type: " + compositeTypeName);
     
        Assert.assertEquals(1, propertyListPage.getPropertyRows().size(), "Incorrect Property count for Type");
               
        // Deactivate Model: Succeeds
        cmmActions.navigateToModelManagerPage(driver);
        cmmPage = cmmActions.setModelActive(driver, modelName, false).render();
        Assert.assertEquals(cmmPage.getCustomModelRowByName(modelName).getCmStatus(), modelStatusInactive);
        
        // Delete Model: Succeeds
        cmmActions.deleteModel(driver, modelName);
    }
}
