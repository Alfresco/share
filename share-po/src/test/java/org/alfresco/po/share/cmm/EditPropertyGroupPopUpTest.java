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

import org.alfresco.po.share.cmm.admin.EditPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.test.FailedTestListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The Class EditPropertyGroupPopUpTest
 * 
 * @author Meenal Bhave
 */
@Listeners(FailedTestListener.class)
public class EditPropertyGroupPopUpTest extends AbstractTestCMM
{
    private String name = "model1" + System.currentTimeMillis();
    @Autowired CmmActions cmmActions;
    private String aspectName = "Aspect" + System.currentTimeMillis();
    private String compoundAspectName = name + ":" + aspectName;

    // SHA-1103: Amended Parent Aspect Name to
    private String parentModelName = "parent" + name;

    // private String parentPropertyGroup = "cm:mlDocument (Multilingual Document)";
    private String parentPropertyGroup = getParentTypeAspectName(parentModelName, aspectName);

    private String compoundParentAspectName = parentModelName + ":" + aspectName;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {

        loginAs(username, password);


        cmmActions.navigateToModelManagerPage(driver).render();

        // SHA-1103: Create another Model, Aspect to be used as Parent Type Group later
        cmmActions.createNewModel(driver, parentModelName);
        // Create another model
        cmmActions.createNewModel(driver, name);
        cmmActions.setModelActive(driver, parentModelName, true);

        cmmActions.viewTypesAspectsForModel(driver, parentModelName);

        ManageTypesAndAspectsPage aspectsListPage = cmmActions.createAspect(driver, aspectName).render();
        Assert.assertTrue(aspectsListPage.isPropertyGroupRowDisplayed(compoundParentAspectName));

        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.viewTypesAspectsForModel(driver, name);

        aspectsListPage = cmmActions.createAspect(driver, aspectName).render();
        Assert.assertTrue(aspectsListPage.isPropertyGroupRowDisplayed(compoundAspectName));

        // Model is not Active yet
    }

    /**
     * Logout between tests.
     */
    @AfterClass
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    /**
     * Test the Edit Action works
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testClickEditAspectAction() throws Exception
    {
        EditPropertyGroupPopUp editAspectPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);
        Assert.assertNotNull(editAspectPopUp);
        Assert.assertNotNull(editAspectPopUp.getDialogueTitle());
        ManageTypesAndAspectsPage manageAspectsPage = editAspectPopUp.selectCloseButton().render();
        Assert.assertNotNull(manageAspectsPage);
    }

    /**
     * Test to select cancel button
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void testCancelButtonTest() throws Exception
    {
        EditPropertyGroupPopUp editAspectPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);
        Assert.assertTrue(editAspectPopUp.isCancelButtonEnabled(), "Create button disabled successfully");
        ManageTypesAndAspectsPage manageTypesAndAspectsPage = editAspectPopUp.selectCancelButton().render();
        Assert.assertTrue(manageTypesAndAspectsPage.isPropertyGroupRowDisplayed(compoundAspectName), "Custom Type Row disaplayed");
    }

    /**
     * Test setting the name on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testName() throws Exception
    {
        EditPropertyGroupPopUp editAspectPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);

        Assert.assertFalse(editAspectPopUp.isNameEnabled(), "Name should not be enabled");

        Assert.assertEquals(editAspectPopUp.getNameField(), aspectName, "Name field text displayed correctly");

        editAspectPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the title on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testSetTitle() throws Exception
    {
        EditPropertyGroupPopUp editAspectPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);

        editAspectPopUp.setTitleField("Title").render();

        Assert.assertEquals(editAspectPopUp.getTitleField(), "Title", "Title field text displayed correctly");

        editAspectPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the description on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testSetDescription() throws Exception
    {
        EditPropertyGroupPopUp editAspectPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);

        editAspectPopUp.setDescriptionField("Desc").render();

        Assert.assertEquals(editAspectPopUp.getDescriptionField(), "Desc", "Description field text displayed correctly");

        editAspectPopUp.selectCloseButton().render();
    }

    /**
     * Test setting the parent property group on the form works
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testSetParentAspect() throws Exception
    {
        EditPropertyGroupPopUp editAspectPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);

        editAspectPopUp.selectParentPropertyGroupField(parentPropertyGroup).render();

        Assert.assertEquals(editAspectPopUp.getParentPropertyGroupField(), parentPropertyGroup, "Parent Aspect field text Not displayed correctly");

        Assert.assertTrue(editAspectPopUp.isPropertyGroupDisplayedInParentList(parentPropertyGroup), "Property Group Not displayed in the list");

        editAspectPopUp.selectCloseButton().render();
    }

    /**
     * Edit Aspect Fields checks on Activating Model
     * 
     * @throws Exception if error
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testEditAspectForActiveModel() throws Exception
    {
        cmmActions.navigateToModelManagerPage(driver);
        cmmActions.setModelActive(driver, name, true).render();

        cmmActions.viewTypesAspectsForModel(driver, name);

        EditPropertyGroupPopUp editPropertyGroupPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);

        Assert.assertTrue(editPropertyGroupPopUp.isTitleEnabled(), "Title Field should be enabled");
        Assert.assertTrue(editPropertyGroupPopUp.isDescriptionEnabled(), "Description Field should be enabled");

        Assert.assertFalse(editPropertyGroupPopUp.isNameEnabled(), "Name Field should be disabled");
        Assert.assertFalse(editPropertyGroupPopUp.isParentpropertyGroupEnabled(), "Parent Property Group Field should be disabled");

        editPropertyGroupPopUp.setTitleField("New");
        editPropertyGroupPopUp.setDescriptionField("New");
        editPropertyGroupPopUp.selectSaveButton().render();

        editPropertyGroupPopUp = cmmActions.getEditAspectPopUp(driver, compoundAspectName);
        Assert.assertEquals(editPropertyGroupPopUp.getTitleField(), "New", "Incorrect Title Set");
        Assert.assertEquals(editPropertyGroupPopUp.getDescriptionField(), "New", "Incorrect Description Set");
    }
}
