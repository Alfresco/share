/*
 * #%L
 * share-po
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
package org.alfresco.po.share.cmm;

import org.alfresco.po.share.cmm.admin.ImportModelPopUp;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Test Class holds all tests to test ImportModelPage methods
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class ImportModelPopUpTest extends AbstractTestCMM
{
    @Value("${cmm.dialogue.label.import.model}") String importCMDialogueHeader;
    private String filePath = "";
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
    public void testImportModelButton() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        Assert.assertNotNull(importModelPage);
        Assert.assertNotNull(importModelPage.getDialogueTitle());
        Assert.assertTrue(importCMDialogueHeader.equals(importModelPage.getDialogueTitle()));
    }

    /**
     * Click Cancel
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testImportModelCancel() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        cmmPage = importModelPage.clickCancel().render();
        Assert.assertNotNull(cmmPage);
    }

    /**
     * Click Import when file is not selected
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testImportModelImportNoFileSelected() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        cmmPage = importModelPage.importModel("").render();
        Assert.assertNotNull(cmmPage);
    }

    /**
     * Click Browse
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testImportModelBrowse() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        cmmPage = importModelPage.clickCancel().render();
        Assert.assertNotNull(cmmPage);
    }

    /**
     * Click Import when file is not selected
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testImportModelSelectClose() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        cmmPage = importModelPage.clickClose().render();
        Assert.assertNotNull(cmmPage);
    }

    /**
     * Click Import when file is not selected
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testImportModelImportFileSelected() throws Exception
    {
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();

        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        cmmPage = importModelPage.importModel(filePath).render();
        Assert.assertNotNull(cmmPage);
    }
}
