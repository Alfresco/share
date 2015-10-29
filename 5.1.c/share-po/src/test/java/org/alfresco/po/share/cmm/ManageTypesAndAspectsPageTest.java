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
 * Test Class to test ManageTypesAndAspects Page
 * 
 * @author Charu
 * @author mbhave
 * @author Richard Smith
 */
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.ModelPropertyGroupRow;
import org.alfresco.po.share.cmm.admin.ModelTypeRow;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ManageTypesAndAspectsPageTest extends AbstractTestCMM
{
    ManageTypesAndAspectsPage manageTypesAndAspectsPage;
    private String name = "model1" + System.currentTimeMillis();
    private String typename = "modeltype1" + System.currentTimeMillis();
    private String propGroupName = "modelpropgroup1" + System.currentTimeMillis();
    private String compoundTypeName = name + ":" + typename;
    private String compoundPGName = name + ":" + propGroupName;

    private String noValue;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {
        noValue = factoryPage.getValue("cmm.value.no");
        loginAs(username, password);
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
        createNewModelPopUpPage.setName(name);
        createNewModelPopUpPage.setNameSpace(name);
        createNewModelPopUpPage.setPrefix(name);
        createNewModelPopUpPage.setDescription(name);
        cmmPage = createNewModelPopUpPage.selectCreateModelButton("Create").render();
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(name), "Custom Model Row is not displayed");

    }

    @AfterClass
    public void cleanupSession()
    {
        cmmActions.navigateToModelManagerPage(driver).render();
        cmmActions.deleteModel(driver, name);
        cleanSession(driver);
    }

    /**
     * Verify list of Custom Type displayed
     * 
     * @throws Exception the exception
     */

    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void createViewTypeTest() throws Exception
    {
        cmmActions.navigateToModelManagerPage(driver);

        cmmActions.viewTypesAspectsForModel(driver, name);

        ManageTypesAndAspectsPage manageTypesAndAspectspage = cmmActions.createType(driver, typename).render();

        Assert.assertTrue(manageTypesAndAspectspage.isCustomTypeRowDisplayed(compoundTypeName), "Custom Type Row not disaplayed");
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelTypeRowByName(compoundTypeName).getDisplayLabel().equals(typename));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelTypeRowByName(compoundTypeName).getParent().equals("cm:content"));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelTypeRowByName(compoundTypeName).getLayout().equals(noValue));

        ModelTypeRow row = manageTypesAndAspectspage.getCustomModelTypeRowByName(compoundTypeName);

        ActionsSet actions = row.getActions();
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));

        // Select Back Button test
        ModelManagerPage cmmpage = manageTypesAndAspectspage.selectBackToModelsButton().render();
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmpage.selectCustomModelRowByName(name).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isCustomTypeRowDisplayed(compoundTypeName), "Custom Type Row not disaplayed");
        Assert.assertTrue(manageTypesAndAspectsPage.getCustomModelTypeRowByName(compoundTypeName).getDisplayLabel().equals(typename));
        Assert.assertTrue(manageTypesAndAspectsPage.getCustomModelTypeRowByName(compoundTypeName).getParent().equals("cm:content"));

        ModelTypeRow typerow = manageTypesAndAspectsPage.getCustomModelTypeRowByName(compoundTypeName);
        actions = typerow.getActions();
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));

    }

    /**
     * Verify list of Property Group displayed
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void createViewPropertyGroupTest() throws Exception
    {
        cmmActions.navigateToModelManagerPage(driver);

        cmmActions.viewTypesAspectsForModel(driver, name);

        ManageTypesAndAspectsPage manageTypesAndAspectspage = cmmActions.createAspect(driver, propGroupName).render();

        Assert.assertTrue(manageTypesAndAspectspage.isPropertyGroupRowDisplayed(compoundPGName), "Custom Prop Group Row not displayed");
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName).getDisplayLabel().equals(propGroupName));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName).getParent().equals(""));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName).getLayout().equals(noValue));

        ModelPropertyGroupRow row = manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName);

        ActionsSet actions = row.getActions();
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));

        // Select Back Button test

        ModelManagerPage cmmpage = manageTypesAndAspectspage.selectBackToModelsButton().render();
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = cmmpage.selectCustomModelRowByName(name).render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compoundPGName), "Custom Property Group Row not displayed");
        Assert.assertTrue(manageTypesAndAspectsPage.getCustomModelPropertyGroupRowByName(compoundPGName).getDisplayLabel().equals(propGroupName));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName).getParent().equals(""));

        ModelPropertyGroupRow aspectrow = manageTypesAndAspectsPage.getCustomModelPropertyGroupRowByName(compoundPGName);
        actions = aspectrow.getActions();
        Assert.assertTrue(actions.hasActionByName(editAction));
        Assert.assertTrue(actions.hasActionByName(deleteAction));

    }

    /**
     * Verify list of Property Group displayed
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void createViewPropertyGroupSameNameAsTypeTest() throws Exception
    {
        cmmActions.navigateToModelManagerPage(driver);

        cmmActions.viewTypesAspectsForModel(driver, name);

        CreateNewPropertyGroupPopUp newAspectPopUp = cmmActions.createAspect(driver, typename).render();

        ManageTypesAndAspectsPage manageTypesAndAspectspage = newAspectPopUp.selectCancelButton().render();

        Assert.assertFalse(manageTypesAndAspectspage.isPropertyGroupRowDisplayed(compoundTypeName), "Custom Type Row disaplayed");
    }

    /**
     * Delete a Custom Type
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void deleteCustomTypeTest() throws Exception
    {
        typename = typename + "1";
        compoundTypeName = name + ":" + typename;

        cmmActions.navigateToModelManagerPage(driver);

        cmmActions.viewTypesAspectsForModel(driver, name);

        ManageTypesAndAspectsPage manageTypesAndAspectspage = cmmActions.createType(driver, typename).render();

        Assert.assertTrue(manageTypesAndAspectspage.isCustomTypeRowDisplayed(compoundTypeName), "Custom type not displayed");
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelTypeRowByName(compoundTypeName).getDisplayLabel().equals(typename));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelTypeRowByName(compoundTypeName).getParent().equals("cm:content"));

        Assert.assertTrue(manageTypesAndAspectspage.isCustomTypeRowDisplayed(compoundTypeName), "Custom type row not displayed");
        manageTypesAndAspectspage = cmmActions.deleteType(driver, compoundTypeName, cancelAction).render();

        Assert.assertFalse(manageTypesAndAspectspage.isCustomTypeRowDisplayed(compoundTypeName), "Custom type Row not displayed");
    }

    /**
     * Delete a Property Group
     * 
     * @throws Exception the exception
     */

    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void deletePropertyGroupTest() throws Exception
    {
        propGroupName = propGroupName + "1";
        compoundPGName = name + ":" + propGroupName;

        cmmActions.navigateToModelManagerPage(driver);

        cmmActions.viewTypesAspectsForModel(driver, name);

        ManageTypesAndAspectsPage manageTypesAndAspectspage = cmmActions.createAspect(driver, propGroupName).render();

        Assert.assertTrue(manageTypesAndAspectspage.isPropertyGroupRowDisplayed(compoundPGName), "Custom Prop Group Row not displayed");
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName).getDisplayLabel().equals(propGroupName));
        Assert.assertTrue(manageTypesAndAspectspage.getCustomModelPropertyGroupRowByName(compoundPGName).getParent().equals(""));

        Assert.assertTrue(manageTypesAndAspectspage.isPropertyGroupRowDisplayed(compoundPGName), "Custom Prop Group Row not displayed");
        
        manageTypesAndAspectspage = cmmActions.deleteAspect(driver, compoundPGName, cancelAction).render();
        Assert.assertFalse(manageTypesAndAspectspage.isPropertyGroupRowDisplayed(compoundPGName), "Custom Prop Group Row not displayed");
    }
}
