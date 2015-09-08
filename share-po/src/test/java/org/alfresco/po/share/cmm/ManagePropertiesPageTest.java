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

/**
 * Test Class to test ManageProperties Page
 * 
 * @author Richard Smith
 * @author mbhave
 */
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ManagePropertiesPageTest extends AbstractTestCMM
{
    @Value("${cmm.model.action.ok}") String okAction;
    @Value("${cmm.model.action.cancel}") String cancelAction;

    ModelManagerPage mmp;
    CreateNewModelPopUp cnmp;
    ManageTypesAndAspectsPage mtaap;
    CreateNewCustomTypePopUp cnctp;
    CreateNewPropertyGroupPopUp cnpgp;
    ManagePropertiesPage mpp;

    private String name = "model1" + System.currentTimeMillis();
    private String typename = "modeltype1" + System.currentTimeMillis();
    private String propGroupName = "modelpropgroup1" + System.currentTimeMillis();
    private String compoundTypeName = name + ":" + typename;
    private String compoundPGName = name + ":" + propGroupName;
    private String propertyName = "property1" + System.currentTimeMillis();
    private String compoundPropertyName = name + ":" + propertyName;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {
        loginAs(username, password);
        // Navigate to the CMM page
        mmp = cmmActions.navigateToModelManagerPage(driver).render();

        // Create a model
        CreateNewModelPopUp cnmp = mmp.clickCreateNewModelButton().render();
        cnmp.setName(name);
        cnmp.setNameSpace(name);
        cnmp.setPrefix(name);
        cnmp.setDescription(name);
        mmp = cnmp.selectCreateModelButton("Create").render();

        // Navigate to the tpg page
        mtaap = mmp.selectCustomModelRowByName(name).render();

        // Create a custom type
        cnctp = mtaap.clickCreateNewCustomTypeButton().render();
        cnctp.setNameField(typename);
        cnctp.setDescriptionField(typename);
        cnctp.setTitleField(typename);
        mtaap = cnctp.selectCreateButton().render();

        // Create a property group
        cnpgp = mtaap.clickCreateNewPropertyGroupButton().render();
        cnpgp.setNameField(propGroupName);
        cnpgp.setDescriptionField(propGroupName);
        cnpgp.setTitleField(propGroupName);
        mtaap = cnpgp.selectCreateButton().render();

    }

    @AfterClass
    public void cleanupSession()
    {
        cmmActions.navigateToModelManagerPage(driver).render();
        cmmActions.deleteModel(driver, name);

        cleanSession(driver);
    }

    /**
     * Verify properties screen loaded
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void propertiesScreenLoadedTest() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        mpp = mtaap.selectCustomTypeRowByName(compoundTypeName).render();

        // Empty list For Types
        Assert.assertTrue(0 == mpp.getPropertyRows().size());
        Assert.assertTrue(mpp.getTitle().startsWith(name));
        Assert.assertTrue(mpp.getTitle().endsWith(typename));

        // Visit properties via property group
        mtaap = mpp.selectBackToTypesPropertyGroupsButton().render();
        mpp = mtaap.selectPropertyGroupRowByName(compoundPGName).render();

        // Empty list For PropGroup
        Assert.assertTrue(0 == mpp.getPropertyRows().size());

        Assert.assertTrue(mpp.getTitle().startsWith(name));
        Assert.assertTrue(mpp.getTitle().endsWith(propGroupName));

    }

    /**
     * Delete a Property
     * 
     * @throws Exception the exception
     */

    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void deletePropertyTest() throws Exception
    {

        cmmActions.navigateToModelManagerPage(driver);

        cmmActions.viewTypesAspectsForModel(driver, name);

        cmmActions.viewProperties(driver, compoundTypeName);

        mpp = cmmActions.createProperty(driver, propertyName).render();

        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property Row not displayed");
        Assert.assertTrue(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property Row not displayed");

        mpp = cmmActions.deleteProperty(driver, compoundPropertyName, cancelAction).render();

        Assert.assertFalse(mpp.isPropertyRowDisplayed(compoundPropertyName), "Property Row should not be displayed");
    }
}
