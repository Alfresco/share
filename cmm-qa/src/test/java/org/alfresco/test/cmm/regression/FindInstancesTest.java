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
 * Test Class to test Find Instances
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
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
public class FindInstancesTest extends AbstractCMMQATest
{
    private static final Log logger = LogFactory.getLog(FindInstancesTest.class);

    private String testName;

    public DashBoardPage dashBoardpage;
    @Value("${cmm.model.admin.group}")
    private String modelAdmin = "ALFRESCO_MODEL_ADMINISTRATORS";
    
    private String siteName;

    private String testUser;

    private String modelName = "model" + System.currentTimeMillis();

    private String typeName = "type" + System.currentTimeMillis();

    private String propGroupName = "propgroup" + System.currentTimeMillis();

    private String compoundTypeName = modelName + ":" + typeName;

    private String compoundPGName = modelName + ":" + propGroupName;
    
    private String shareTypeName = getShareTypeName(modelName, typeName);
    
    private String shareAspectName = propGroupName;

    String propertyName = "Prop" + System.currentTimeMillis();

    @BeforeClass(alwaysRun = true)
    public void setupTest() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);
        
        siteName = testName + System.currentTimeMillis();
        
        shareAspectName = getShareAspectName(modelName, propGroupName);

        // Login as Admin to create a new user
        loginAs(driver, new String[] { username });
        testUser = getUserNameFreeDomain(testName + System.currentTimeMillis());

        // Create User and add to modelAdmin group
        adminActions.createEnterpriseUserWithGroup(driver, testUser, testUser, testUser, testUser, DEFAULT_PASSWORD, modelAdmin);

        // Logout as admin
        logout(driver);

        
        loginAs(testUser, password);
        
        siteActions.createSite(driver, siteName, siteName, "");

        // Navigate to the CMM page
        cmmActions.navigateToModelManagerPage(driver).render();

        // Create a model
        cmmActions.createNewModel(driver, modelName);
        cmmActions.setModelActive(driver, modelName, true);

        // Navigate to the tpg page
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Create a custom type
        cmmActions.createType(driver, typeName);

        // Create a property group
        cmmActions.createAspect(driver, propGroupName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compoundTypeName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compoundPGName); 
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
     * <li>Create and Activate Model</li>
     * <li>Add Type, Aspect, Properties for Types and Aspects</li>
     * <li>Find Instances: Find Instances: None Found</li>
     * <li>Apply Type, Aspect to a node: Find Instances: should find correct Nodes</li> 
     * <li>Remove Aspect from a node: Find Instances: should not find the Node now</li> 
     * <li>Delete the node: Find Instances: should not find the Node now</li> 
     * </ul>
     * 
     * @throws Exception
     */
    @AlfrescoTest(testlink = "tobeaddeddupl1")
    // Disabled test as CMM team discussed that auto test isn't necessary
    @Test(groups = "EnterpriseOnly", priority = 1, enabled = false)
    public void testFindInstances() throws Exception
    {
        String testName = getUniqueTestName();

        String docName = "testaddCMMType" + testName;
        String docName2 = "testaddCMMAspect" + testName;
        
        File doc = siteUtil.prepareFile(docName);
        File doc2 = siteUtil.prepareFile(docName2);

        loginAs(driver, new String[] { testUser, password });
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model1
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        
        // Find Instances: Type
        cmmActions.findInstances(driver, compoundTypeName);
        
        // TODO: None found
        String searchTerm = cmmActions.getSearchTerm(driver, true, modelName, typeName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());
        
        // Find Instances: Aspect
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundPGName);
        
        // TODO: None found
        searchTerm = cmmActions.getSearchTerm(driver, false, modelName, propGroupName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());
        
        siteActions.openSiteDashboard(driver, siteName);        
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, doc);
        siteActions.uploadFile(driver, doc2);
        
        // Change Type
        siteActions.selectContent(driver, doc.getName());        
        siteActions.changeType(driver, shareTypeName);

        // Add Aspect
        siteActions.openDocumentLibrary(driver);
        siteActions.selectContent(driver, doc2.getName());
        
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);

        // Find Instances: Type
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundTypeName);
        
        // TODO: 1 found
        searchTerm = cmmActions.getSearchTerm(driver, true, modelName, typeName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());
        
        // Find Instances: Aspect
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundPGName);
        
        // TODO: 1 found
        searchTerm = cmmActions.getSearchTerm(driver, false, modelName, propGroupName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());        
        
        // Remove Aspect
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.selectContent(driver, doc2.getName());
        siteActions.removeAspects(driver, aspects);
        
        // Find Instances: Aspect
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundPGName);
        
        // TODO: 1 found
        searchTerm = cmmActions.getSearchTerm(driver, false, modelName, propGroupName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());        
        
        // Find Instances: Aspect
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundPGName);
        
        // TODO: None found
        searchTerm = cmmActions.getSearchTerm(driver, false, modelName, propGroupName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());
        
        // Delete Document with Type
        siteActions.openSitesDocumentLibrary(driver, siteName);
        siteActions.deleteContentInDocLib(driver, doc.getName());
        
        // Find Instances: Type
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundTypeName);
        
        // TODO: None found
        searchTerm = cmmActions.getSearchTerm(driver, true, modelName, typeName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());
        
        // Find Instances: Aspect
        navigateToTypesAndAspects(modelName);
        cmmActions.findInstances(driver, compoundPGName);
        
        // TODO: None found
        searchTerm = cmmActions.getSearchTerm(driver, false, modelName, propGroupName);
        Assert.assertTrue(driver.getCurrentUrl().endsWith(searchTerm), "SearchTerm not correct: expected: " + searchTerm + " Found: " + driver.getCurrentUrl());        
    }
    
    private void navigateToTypesAndAspects(String modelName)
    {
        // cmmActions.navigateToModelManagerPage(driver);
        // cmmActions.viewTypesAspectsForModel(driver, modelName);
        
        driver.navigate().to(shareUrl + "/page/console/custom-model-management-console/custom-model-manager#view=types_property_groups&model=" + modelName);
        ManageTypesAndAspectsPage typesAspectsListPage = resolvePage(driver).render();
        typesAspectsListPage.render();
    }

}