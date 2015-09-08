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
 * Test Class to test FormEditor Page
 * 
 * @author mbhave
 */
import org.alfresco.po.share.cmm.admin.ApplyDefaultLayoutPopUp;
import org.alfresco.po.share.cmm.admin.ClearFormLayoutPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.FormEditorPage;
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
public class FormEditorPageTest extends AbstractTestCMM
{
    ModelManagerPage mmp;
    CreateNewModelPopUp cnmp;
    ManageTypesAndAspectsPage mtaap;
    CreateNewCustomTypePopUp cnctp;
    CreateNewPropertyGroupPopUp cnpgp;
    ManagePropertiesPage mpp;
    @Value("${cmm.model.action.apply}") String applyButton;
    @Value("${cmm.model.action.cancel}") String cancelButton;
    @Value("${cmm.model.action.clear}") String clearButton;
    private String name = "model1" + System.currentTimeMillis();
    private String typename = "modeltype1" + System.currentTimeMillis();
    private String propGroupName = "modelpropgroup1" + System.currentTimeMillis();
    private String compoundTypeName = name + ":" + typename;
    private String compoundPGName = name + ":" + propGroupName;

    private String yesValue;

    private String noValue;

    @BeforeClass(groups = { "Enterprise-only" }, alwaysRun = true)
    public void setup() throws Exception
    {
        noValue = factoryPage.getValue("cmm.value.no");
        yesValue = factoryPage.getValue("cmm.value.yes");

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
     * Verify Form Editor Action loads Form Editor
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 1)
    public void testSelectFormEditorActionForType() throws Exception
    {
        // Visit properties via type
        FormEditorPage formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundTypeName).render();

        Assert.assertNotNull(formEditor);
    }

    /**
     * Verify Form Editor selectDefaultLayoutButton Action
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 2)
    public void selectDefaultLayoutButton() throws Exception
    {
        // Visit properties via type
        FormEditorPage formEditor = cmmActions.getSharePage(driver).render();

        ApplyDefaultLayoutPopUp applyPopup = formEditor.selectDefaultLayoutButton().render();

        formEditor = applyPopup.clickActionByName(applyButton).render();

        Assert.assertNotNull(formEditor);

        applyPopup = formEditor.selectDefaultLayoutButton().render();

        formEditor = applyPopup.clickActionByName(cancelButton).render();

        Assert.assertNotNull(formEditor);

        applyPopup = formEditor.selectDefaultLayoutButton().render();

        formEditor = applyPopup.clickClose().render();

        Assert.assertNotNull(formEditor);
    }

    /**
     * Verify Form Editor Save Action
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 3)
    public void testFormEditorSaveButton() throws Exception
    {
        // Visit properties via type
        FormEditorPage formEditor = cmmActions.getSharePage(driver).render();

        formEditor = formEditor.selectSaveButton().render();

        Assert.assertNotNull(formEditor);

        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertNotNull(mtaap);

        Assert.assertTrue(mtaap.getCustomModelTypeRowByName(compoundTypeName).getLayout().equals(yesValue));
    }

    /**
     * Verify Form Editor Clear Action
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 4)
    public void testFormEditorClearButton() throws Exception
    {
        // Visit properties via type
        FormEditorPage formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundTypeName).render();
        ClearFormLayoutPopUp clearLayout = formEditor.selectClearButton().render();

        formEditor = clearLayout.clickActionByName(cancelButton).render();
        Assert.assertNotNull(formEditor);

        clearLayout = formEditor.selectClearButton().render();

        formEditor = clearLayout.clickActionByName(clearButton).render();
        Assert.assertNotNull(formEditor);
    }

    /**
     * Verify Form Editor selectBackToTypesPropertyGroupsButton Action
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 5)
    public void testFormEditorBackButton() throws Exception
    {
        FormEditorPage formEditor = cmmActions.getSharePage(driver).render();
        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertNotNull(mtaap);

    }

    /**
     * Verify Form Editor Action loads Form Editor: From Aspects
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 6)
    public void testSelectFormEditorActionForAspect() throws Exception
    {
        // Visit properties via type
        mmp = cmmActions.navigateToModelManagerPage(driver).render();
        mtaap = mmp.selectCustomModelRowByName(name).render();
        FormEditorPage formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundPGName).render();

        Assert.assertNotNull(formEditor);

        // Test Save Button
        formEditor = formEditor.selectSaveButton().render();

        Assert.assertNotNull(formEditor);

        // Test Clear Button + Clear Popup

        ClearFormLayoutPopUp clearForm = formEditor.selectClearButton().render();

        formEditor = clearForm.clickClose().render();

        Assert.assertNotNull(formEditor);

        // Test Apply Default Layout Button + ApplyDefaultLayout Popup

        ApplyDefaultLayoutPopUp applyPopup = formEditor.selectDefaultLayoutButton().render();

        formEditor = applyPopup.clickActionByName(applyButton).render();

        Assert.assertNotNull(formEditor);

        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertNotNull(mtaap);

        Assert.assertTrue(mtaap.getCustomModelPropertyGroupRowByName(compoundPGName).getLayout().equals(noValue));
    }

    /**
     * Verify Form Layout Column: For Aspects
     * 
     * @throws Exception the exception
     */
    @Test(groups = { "Enterprise-only" }, priority = 7)
    public void testFormLayoutYesToNo() throws Exception
    {
        FormEditorPage formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundTypeName).render();

        ApplyDefaultLayoutPopUp applyPopup = formEditor.selectDefaultLayoutButton().render();
        formEditor = applyPopup.clickActionByName(applyButton).render();

        formEditor = formEditor.selectSaveButton().render();
        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertTrue(mtaap.getCustomModelTypeRowByName(compoundTypeName).getLayout().equals(yesValue));

        formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundTypeName).render();

        ClearFormLayoutPopUp clearLayout = formEditor.selectClearButton().render();

        formEditor = clearLayout.clickActionByName(clearButton).render();

        formEditor = formEditor.selectSaveButton().render();
        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertTrue(mtaap.getCustomModelPropertyGroupRowByName(compoundPGName).getLayout().equals(noValue));

        formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundPGName).render();

        applyPopup = formEditor.selectDefaultLayoutButton().render();
        formEditor = applyPopup.clickActionByName(applyButton).render();

        formEditor = formEditor.selectSaveButton().render();
        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertTrue(mtaap.getCustomModelPropertyGroupRowByName(compoundPGName).getLayout().equals(yesValue));

        formEditor = cmmActions.getFormEditorForTypeOrAspect(driver, compoundPGName).render();
        clearLayout = formEditor.selectClearButton().render();

        formEditor = clearLayout.clickActionByName(clearButton).render();

        formEditor = formEditor.selectSaveButton().render();
        mtaap = formEditor.selectBackToTypesPropertyGroupsButton().render();

        Assert.assertTrue(mtaap.getCustomModelPropertyGroupRowByName(compoundPGName).getLayout().equals(noValue));
    }
}