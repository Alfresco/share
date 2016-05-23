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
 * Test Class to test properties with various Indexing options
 * 
 * @author mbhave
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.IndexingOptions;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.test.AlfrescoTest;
import org.alfresco.test.FailedTestListener;
import org.alfresco.test.cmm.AbstractCMMQATest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class PropertyWithIndexingOptionsTest extends AbstractCMMQATest
{
    private static final Logger logger = Logger.getLogger(PropertyWithIndexingOptionsTest.class);
    
    private String testUser;

    protected String testSiteName = "swsdp";
    
    protected String testName = getTestName();    

    protected String modelName;
    
    @BeforeClass(alwaysRun = true)
    public void setupTest() throws Exception
    {
        super.setupCmis();
        testName = this.getClass().getSimpleName();
        logger.info("Starting Tests: " + testName);   
       
        setupData();
    }

    private void setupData() throws Exception
    {
        String testName = getUniqueTestName(); 
        
        modelName = "modelIndexTest" + testName;

        testUser = username;  
        
        //Login as RepoAdmin
        loginAs(driver, new String[] {username});
        
        // testSiteName = getSiteName(testName);        
        //siteActions.createSite(driver, testSiteName, testSiteName, "public");
       
        cmmActions.navigateToModelManagerPage(driver);

        // Create New Model
        cmmActions.createNewModel(driver, modelName).render();
                
        // Activate Model
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, modelName, true);

    }
    
    /**
     * User logs out after test is executed
     * 
     * @throws Exception
     */
    @AfterClass
    public void quit() throws Exception
    {
        logout(driver);
    }
    
    @AlfrescoTest(testlink="tobeaddeddel1")
    @Test(groups = "EnterpriseOnly", priority=1)
    public void testBooleanPropIndexingOptions() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeIndexTestBool" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "bool" + testName;
        
        String compositePropName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Create Properties: 
        cmmActions.createPropertyWithIndexingOption(driver, propertyName, "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "",
                IndexingOptions.None).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "Basic", "", "", DataType.Boolean, MandatoryClassifier.Optional, false, "",
                IndexingOptions.Basic).render();
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);        
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Add Properties
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName, "True");
        properties.put(propertyName + "Basic", "False");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Search: Indexing Option: None
        Assert.assertTrue(cmmActions.checkSearchResultsWithRetry(driver, compositePropName, "*", docFile.getName(), true, SOLR_RETRY_COUNT), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName, "True", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName, "False", docFile.getName(), false), "Search Results not as expected");

        // Search: Indexing Option: Basic
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "Basic", "*", docFile.getName(), true), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "Basic", "True", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "Basic", "False", docFile.getName(), true), "Search Results not as expected");
    }
    
    @AlfrescoTest(testlink="tobeaddeddel2")
    @Test(groups = "EnterpriseOnly", priority=2)
    public void testNumericPropIndexingOptions() throws Exception
    {
        String testName = getUniqueTestName();
        
        String aspectName = "aspectIndexTestNum" + System.currentTimeMillis();
        String shareAspectName = getShareAspectName(modelName, aspectName);
        String compositeAspectName = modelName + ":" + aspectName;
        
        String propertyName = "num" + testName;
        
        String compositePropName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);
        
        loginAs(driver, new String[] {testUser});        

        cmmActions.navigateToModelManagerPage(driver);
        
        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model        
        cmmActions.createAspect(driver, aspectName).render();
                
        cmmActions.viewProperties(driver, compositeAspectName);

        // Create Properties: 
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "IntNone", "", "", DataType.Int, MandatoryClassifier.Optional, false, "",
                IndexingOptions.None).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "LongBasic", "", "", DataType.Long, MandatoryClassifier.Optional, false, "",
                IndexingOptions.Basic).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "FloatEnh", "", "", DataType.Float, MandatoryClassifier.Optional, false, "",
                IndexingOptions.Enhanced).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "DblEnh", "", "", DataType.Double, MandatoryClassifier.Optional, false, "",
                IndexingOptions.Enhanced).render();

        // Apply Default Form Layout: Aspect
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeAspectName);

        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share
        List<String> aspects = new ArrayList<String>();
        aspects.add(shareAspectName);
        siteActions.addAspects(driver, aspects);
        
        // Edit Props: Aspect
        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName + "IntNone", "456789");
        properties.put(propertyName + "LongBasic", "7");
        properties.put(propertyName + "FloatEnh", "27.5");
        properties.put(propertyName + "DblEnh", "100");

        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);
        
        // Search: Indexing Option: *
        Assert.assertTrue(cmmActions.checkSearchResultsWithRetry(driver, compositePropName + "IntNone", "*", docFile.getName(), true, SOLR_RETRY_COUNT), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "LongBasic", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "FloatEnh", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DblEnh", "*", docFile.getName(), true), "Search Results not as expected");
        
        // Search: Indexing Option: Basic and Enhanced
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "IntNone", "456789", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "IntNone", "45678910", docFile.getName(), false), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "LongBasic", "7", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "LongBasic", "77", docFile.getName(), false), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "FloatEnh", "27.5", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "FloatEnh", "27", docFile.getName(), false), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DblEnh", "100", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DblEnh", "110", docFile.getName(), false), "Search Results not as expected");
    }
    
    @AlfrescoTest(testlink="tobeaddeddel3")
    @Test(groups = "EnterpriseOnly", priority=3)
    public void testDatePropIndexingOptions() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeIndexTestDate" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "date" + testName;
        
        String compositePropName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Create Properties: 
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "DateNone", "", "", DataType.Date, MandatoryClassifier.Optional, false, "",
                IndexingOptions.None).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "DttmNone", "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "",
                IndexingOptions.None).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "DateBasic", "", "", DataType.Date, MandatoryClassifier.Optional, false, "",
                IndexingOptions.Basic).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "DttmEnh", "", "", DataType.DateTime, MandatoryClassifier.Optional, false, "",
                IndexingOptions.Enhanced).render();
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);        
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Add Properties
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName + "DateNone", dateEntry);
        properties.put(propertyName + "DttmNone", dateEntry);
        properties.put(propertyName + "DateBasic", dateEntry);
        properties.put(propertyName + "DttmEnh", dateEntry);
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Search: Indexing Option: None
        Assert.assertTrue(cmmActions.checkSearchResultsWithRetry(driver, compositePropName + "DateNone", "*", docFile.getName(), true, SOLR_RETRY_COUNT), "Search Results not as expected");        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DttmNone", "*", docFile.getName(), true), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DateNone", "today", docFile.getName(), false), "Search Results not as expected");        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DttmNone", "today", docFile.getName(), false), "Search Results not as expected");
        
        // Search: Indexing Option: Basic, Enhanced
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DateBasic", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DttmEnh", "*", docFile.getName(), true), "Search Results not as expected");

        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DateBasic", "today", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DttmEnh", "today", docFile.getName(), true), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DateBasic", "yesterday", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "DttmEnh", "yesterday", docFile.getName(), false), "Search Results not as expected");
    }
    
    @AlfrescoTest(testlink="tobeaddeddel4")
    @Test(groups = "EnterpriseOnly", priority=4)
    public void testTextPropIndexingOptions() throws Exception
    {
        String testName = getUniqueTestName();

        String typeName = "typeIndexTestText" + testName;
        String shareTypeName = getShareTypeName(modelName, typeName);
        String compositeTypeName = modelName + ":" + typeName;
        
        String propertyName = "text" + testName;
        
        String compositePropName = modelName + ":" + propertyName;
        
        String docName = testName;
        
        File docFile = siteUtil.prepareFile(docName, docName);

        loginAs(driver, new String[] {testUser});
        
        cmmActions.navigateToModelManagerPage(driver);

        // View Types and Aspects: Model
        cmmActions.viewTypesAspectsForModel(driver, modelName).render();

        // Add Type, Aspect: Model
        cmmActions.createType(driver, typeName).render();
                
        cmmActions.viewProperties(driver, compositeTypeName);

        // Create Properties: 
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "TextNone", "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                IndexingOptions.None).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "MlTextNone", "", "", DataType.MlText, MandatoryClassifier.Optional, false, "",
                IndexingOptions.None).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "TextFree", "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                IndexingOptions.FreeText).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "MlTextWhole", "", "", DataType.MlText, MandatoryClassifier.Optional, false, "",
                IndexingOptions.LOVWhole).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "TextPartial", "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                IndexingOptions.LOVPartial).render();
                
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "MlTextUniqueM", "", "", DataType.MlText, MandatoryClassifier.Optional, false, "",
                IndexingOptions.PatternUnique).render();
        
        cmmActions.createPropertyWithIndexingOption(driver, propertyName + "TextManyM", "", "", DataType.Text, MandatoryClassifier.Optional, false, "",
                IndexingOptions.PatternMany).render();
        
        
        // Apply Default Form Layout: Type
        cmmActions.viewTypesAspectsForModel(driver, modelName);
        cmmActions.applyDefaultLayoutForTypeOrAspect(driver, compositeTypeName);        
        
        // Create a Document Node in the Site
        siteActions.openSiteDashboard(driver, testSiteName);
        siteActions.openDocumentLibrary(driver);
        siteActions.uploadFile(driver, docFile);

        DetailsPage detailsPage = siteActions.selectContent(driver, docFile.getName()).render();
        
        // Apply Type / Aspects to a Node: Using Share     
        detailsPage = detailsPage.changeType(shareTypeName).render();
        
        // Add Properties
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(propertyName + "TextNone", "NoValue");
        properties.put(propertyName + "MlTextNone", "NoSuchValue");
        properties.put(propertyName + "TextFree", "Big Cat");
        properties.put(propertyName + "MlTextWhole", "Northen Ireland");
        properties.put(propertyName + "TextPartial", "SL6 1AF");
        properties.put(propertyName + "MlTextUniqueM", "DS 5106");
        properties.put(propertyName + "TextManyM", "Gordon Smith");
        
        siteActions.getEditPropertiesPage(driver, docFile.getName());
        siteActions.editNodeProperties(driver, true, properties);

        // Search: Indexing Option: None
        Assert.assertTrue(cmmActions.checkSearchResultsWithRetry(driver, compositePropName + "TextNone", "*", docFile.getName(), true, SOLR_RETRY_COUNT), "Search Results not as expected");        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextNone", "*", docFile.getName(), true), "Search Results not as expected");
        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextNone", "NoValue", docFile.getName(), false), "Search Results not as expected");        
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextNone", "NoSuchValue", docFile.getName(), false), "Search Results not as expected");
        
        // Search: Indexing Option: FreeText
        Assert.assertTrue(cmmActions.checkSearchResultsWithRetry(driver, compositePropName + "TextFree", "*", docFile.getName(), true, SOLR_RETRY_COUNT), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextFree", "'Black Cat'", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextFree", "'Big Cat'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextFree", "Big", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextFree", "Cat", docFile.getName(), true), "Search Results not as expected");

        // Search: Indexing Option: Whole Match
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextWhole", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextWhole", "'Northen Ireland'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextWhole", "'Northen Rock'", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextWhole", "Northen", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextWhole", "' Ireland'", docFile.getName(), false), "Search Results not as expected");        
        
        // Search: Indexing Option: Partial Match
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextPartial", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextPartial", "'SL6 1AF'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextPartial", "'SL6 1AFG'", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextPartial", "'SL6 1'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextPartial", "1AF", docFile.getName(), true), "Search Results not as expected");        

        // Search: Indexing Option: Unique Match
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextUniqueM", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextUniqueM", "'DS 5106'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextUniqueM", "'DS 5107'", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextUniqueM", "'DS 51*'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "MlTextUniqueM", "'DS 51'", docFile.getName(), false), "Search Results not as expected");        

        // Search: Indexing Option: Many Matches
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextManyM", "*", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextManyM", "'Gordon Smith'", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextManyM", "'Gordon Brown'", docFile.getName(), false), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextManyM", "Gordon", docFile.getName(), true), "Search Results not as expected");
        Assert.assertTrue(cmmActions.checkSearchResults(driver, compositePropName + "TextManyM", "Smith", docFile.getName(), true), "Search Results not as expected");
    }    
}