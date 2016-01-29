/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.cmm;

import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test Class holds all tests to test CreateModelManagerPage methods
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class CreateNewModelPopUpTest extends AbstractTestCMM
{
    private static Log logger = LogFactory.getLog(CreateNewModelPopUpTest.class);
    private String modelStatusInactive = "Inactive";
    private String name = "model1" + System.currentTimeMillis();

    @Autowired CmmActions cmmActions;

    @BeforeClass(groups = { "alfresco-one" }, alwaysRun = true)
    public void setup() throws Exception
    {
        loginAs(username, password);
    }

    @AfterClass
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    /**
     * Navigate to manage custom models from the dashboard page by Repo Admin
     * 
     * @throws Exception if error
     */

    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testCreateModelCloseButton() throws Exception
    {
        String dialogueHeader = "Create Model";
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        CreateNewModelPopUp createModelPage = cmmPage.clickCreateNewModelButton().render();
        Assert.assertNotNull(createModelPage);
        Assert.assertNotNull(createModelPage.getDialogueTitle());
        Assert.assertTrue(dialogueHeader.equals(createModelPage.getDialogueTitle()));
        cmmPage = createModelPage.selectCloseButton().render();
        Assert.assertNotNull(cmmPage);
    }

    /**
     * Send name to name text field Verify name displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testSetName() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
        createNewModelPopUpPage.setName(name);
        Assert.assertEquals(createNewModelPopUpPage.getName(), name, "Name field text dispalyed correctly");
        createNewModelPopUpPage.selectCloseButton().render();
    }

    /**
     * Send name space to name space text field Verify name space displayed correctly
     * 
     * @throws Exception if error
     */

    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testSetNameSpace() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setNameSpace(name).render();
        Assert.assertEquals(createNewModelPopUpPage.getNameSpace(), name, "Namespace field text dispalyed correctly");
        createNewModelPopUpPage.selectCloseButton().render();
    }

    /**
     * Send prefix to prefix text field Verify prefix displayed correctly
     * 
     * @throws Exception if error
     */

    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testSetPrefix() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setPrefix(name);
        Assert.assertEquals(createNewModelPopUpPage.getPrefix(), name, "prefix field text dispalyed correctly");
        createNewModelPopUpPage.selectCloseButton().render();
    }

    /**
     * Send description to description text field Verify description displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testSetDescription() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setDescription(name).render();
        Assert.assertEquals(createNewModelPopUpPage.getDescription(), name, "Namespace field text dispalyed correctly");
        createNewModelPopUpPage.selectCloseButton().render();
    }

    /**
     * Send author to author text field Verify author displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testSetAuthor() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setAuthor(name).render();
        Assert.assertEquals(createNewModelPopUpPage.getAuthor(), name, "Namespace field text dispalyed correctly");
        createNewModelPopUpPage.selectCloseButton().render();
    }

    /**
     * select Cancel button in CreateNewModelPopUpPage Verify description displayed correctly
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testSelectCancelModelButton() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setName(name);
        createNewModelPopUpPage.setNameSpace(name).render();
        createNewModelPopUpPage.setDescription(name).render();
        Assert.assertTrue(createNewModelPopUpPage.isCancelButtonEnabled("Cancel"), "Cancel button enabled");
        ModelManagerPage cmmpage = createNewModelPopUpPage.selectCancelModelButton("Cancel").render();
        Assert.assertFalse(cmmpage.isCustomModelRowDisplayed(name), "Custom Model Row is displayed");

    }

    @Test(groups = { "Enterprise-only" }, priority = 8)
    public void testSelectCreateModelButton() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        int modelCount = cmmPage.getCMCount();
        logger.info("Model Count is:" + modelCount);
        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        Assert.assertFalse(createNewModelPopUpPage.isCreateButtonEnabled(), "Create button enabled");
        createNewModelPopUpPage.setName(name);
        createNewModelPopUpPage.setNameSpace("ns" + name).render();
        createNewModelPopUpPage.setPrefix("pre" + name);
        createNewModelPopUpPage.setDescription(name).render();
        Assert.assertTrue(createNewModelPopUpPage.isCreateButtonEnabled(), "Create button enabled");
        ModelManagerPage cmmpage = createNewModelPopUpPage.selectCreateModelButton("Create").render();

        Assert.assertTrue(cmmpage.isCustomModelRowDisplayed(name), "Custom Model Row is not displayed");

        int modelCountNow = cmmPage.getCMCount();
        logger.info("Model Count is:" + modelCountNow);

        Assert.assertTrue(modelCountNow == modelCount + 1);

        Assert.assertEquals(cmmPage.getCustomModelRowByName(name).getCMName(), name);
        Assert.assertEquals(cmmPage.getCustomModelRowByName(name).getCmNamespace(), "ns" + name);
        Assert.assertEquals(cmmPage.getCustomModelRowByName(name).getCmStatus(), modelStatusInactive);

    }

    @Test(groups = { "Enterprise-only" }, priority = 9)
    public void testDuplicateModelSameName() throws Exception
    {
        String duplicateString = name;

        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        int modelCount = cmmPage.getCMCount();
        logger.info("Model Count is:" + modelCount);
        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setName(duplicateString);
        createNewModelPopUpPage.setNameSpace("ns").render();
        createNewModelPopUpPage.setPrefix("pre");
        createNewModelPopUpPage.setDescription(name).render();

        createNewModelPopUpPage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        cmmPage = createNewModelPopUpPage.selectCloseButton().render();

        // TODO: Test that error is returned

        // Test that model is not added
        Assert.assertEquals(modelCount, cmmPage.getCMCount());        
    }

    @Test(groups = { "Enterprise-only" }, priority = 10)
    public void testDuplicateModelSameNamespace() throws Exception
    {
        String duplicateString = "ns" + name;
        String newModel = "new" + System.currentTimeMillis();

        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        int modelCount = cmmPage.getCMCount();
        logger.info("Model Count is:" + modelCount);
        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setName(newModel);
        createNewModelPopUpPage.setNameSpace(duplicateString).render();
        createNewModelPopUpPage.setPrefix(newModel);
        createNewModelPopUpPage.setDescription(newModel).render();
        createNewModelPopUpPage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        cmmPage = createNewModelPopUpPage.selectCloseButton().render();

        // TODO: Test that error is returned

        // Test that model is not added
        Assert.assertEquals(modelCount, cmmPage.getCMCount());
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(newModel), "Custom Model Row is not displayed");
    }

    @Test(groups = { "Enterprise-only" }, priority = 11)
    public void testDuplicateModelSamePrefix() throws Exception
    {
        String duplicateString = "pre" + name;
        String newModel = "newModel" + System.currentTimeMillis();

        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        int modelCount = cmmPage.getCMCount();
        logger.info("Model Count is:" + modelCount);
        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();

        createNewModelPopUpPage.setName(newModel);
        createNewModelPopUpPage.setNameSpace(newModel).render();
        createNewModelPopUpPage.setPrefix(duplicateString);
        createNewModelPopUpPage.setDescription(newModel).render();
        createNewModelPopUpPage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        cmmPage = createNewModelPopUpPage.selectCloseButton().render();

        // TODO: Test that error is returned

        // Test that model is not added
        Assert.assertEquals(modelCount, cmmPage.getCMCount());
        Assert.assertFalse(cmmPage.isCustomModelRowDisplayed(newModel), "Custom Model Row is not displayed");
    }

}
