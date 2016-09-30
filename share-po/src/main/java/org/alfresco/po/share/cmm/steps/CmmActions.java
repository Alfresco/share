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

package org.alfresco.po.share.cmm.steps;

/**
 * Class contains Test steps / actions / utils for regression tests
 * 
 *  @author mbhave
 */

import java.util.HashMap;
import java.util.Map;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.ShareDialogueAikau;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.cmm.admin.ApplyDefaultLayoutPopUp;
import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.EditCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.EditModelPopUp;
import org.alfresco.po.share.cmm.admin.EditPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.FormEditorPage;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.ModelPropertyGroupRow;
import org.alfresco.po.share.cmm.admin.ModelRow;
import org.alfresco.po.share.cmm.admin.ModelTypeRow;
import org.alfresco.po.share.cmm.admin.PropertyRow;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.IndexingOptions;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.SearchBox;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.steps.CommonActions;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
@Component
public class CmmActions extends CommonActions
{
    private String createAction = "Create";
    private String editAction = "Edit";
    private String findInstanceAction = "Find Where Used";

    private static final Log logger = LogFactory.getLog(CmmActions.class);

    /**
     * Util to get to the ModelManager Page from any SharePage
     * 
     * @param driver
     * @return HtmlPage
     */
    public ModelManagerPage navigateToModelManagerPage(WebDriver driver)
    {
        try
        {
            ModelManagerPage cmmPage;
            SharePage page = getSharePage(driver);
            if (page instanceof ModelManagerPage)
            {
                cmmPage = page.render();
            }
            else if (page instanceof ManageTypesAndAspectsPage)
            {
                ManageTypesAndAspectsPage typesAspectsPage = page.render();
                cmmPage = typesAspectsPage.selectBackToModelsButton().render();
            }
            else if (page instanceof ManagePropertiesPage)
            {
                ManagePropertiesPage propertiesPage = page.render();
                ManageTypesAndAspectsPage typesAspectsPage = propertiesPage.selectBackToTypesPropertyGroupsButton().render();
                cmmPage = typesAspectsPage.selectBackToModelsButton().render();
            }
            else
            {
                cmmPage = page.getNav().selectManageCustomModelsPage().render();
            }

            return cmmPage;
        }
        catch (PageException c)
        {
            throw new UnexpectedSharePageException(ModelManagerPage.class, c);
        }
    }


    /**
     * Util closes share Popups
     * 
     * @param driver
     * @return HtmlPage
     */
    public HtmlPage closeSharePopUp(WebDriver driver)
    {
        SharePage sharePage = getSharePage(driver);

        if (sharePage instanceof SharePopup)
        {
            logger.debug("Unexpected SharePopup found: " + sharePage.getPageTitle());
            ((SharePopup) sharePage).close();
            return getSharePage(driver);
        }
        else if (sharePage instanceof ShareDialogueAikau)
        {
            logger.debug("Unexpected ShareDialogue found: " + sharePage.getPageTitle());
            ((ShareDialogueAikau) sharePage).clickClose();
            return getSharePage(driver);
        }
        // Added as a workaround for QA-1919
        else if (sharePage instanceof EditDocumentPropertiesPage)
        {
            // No action required but do not check for dialogue
        }
        else if (sharePage instanceof ShareDialogue)
        {
            logger.debug("Unexpected ShareDialogue found: " + sharePage.getPageTitle());
            ((ShareDialogue) sharePage).clickClose();
            return getSharePage(driver);
        }

        return sharePage;
    }

    /**
     * Util closes the specified share PopUp / Dialogue, if found
     * 
     * @param driver
     * @return HtmlPage
     */
    public HtmlPage closeShareDialogue(WebDriver driver, Object pageName)
    {
        SharePage sharePage = getSharePage(driver);

        if (sharePage.getClass().equals(pageName))
        {
            logger.debug("Expected ShareDialogue found: " + sharePage.getPageTitle());
            return closeSharePopUp(driver);
        }
        else
        {
            logger.debug("Unexpected ShareDialogue found: " + sharePage.getPageTitle());
        }

        return sharePage;
    }

    /**
     * Logout of the Alfresco application. This logout differs slightly than in share-po such that it resolves cmm pages that are not available in share-po
     * correctly
     */
    public HtmlPage logout(WebDriver driver)
    {
        SharePage page = getSharePage(driver).render();
        return page.getNav().logout();
    }

    /**
     * Utility to Create New Model: Expects the user to start from ModelManagerPage
     * 
     * @param WebDriver driver
     * @param String modelName
     * @return HtmlPage
     */
    public HtmlPage createNewModel(WebDriver driver, String modelName)
    {
        PageUtils.checkMandatoryParam("modelName", modelName);

        try
        {
            ModelManagerPage cmmPage = getSharePage(driver).render();

            CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
            createNewModelPopUpPage.setName(modelName);
            createNewModelPopUpPage.setNameSpace(modelName).render();
            createNewModelPopUpPage.setPrefix(modelName);

            return createNewModelPopUpPage.selectCreateModelButton(createAction).render();
        }
        catch (PageException pe)
        {
            throw new UnexpectedSharePageException(ModelManagerPage.class, pe);
        }
    }

    /**
     * Utility to Create New Model: Expects the user to be on ModelManagerPage
     * 
     * @param WebDriver driver
     * @param String modelName
     * @return HtmlPage
     */
    public HtmlPage createNewModel(WebDriver driver, String modelName, String nameSpace, String prefix)
    {
        PageUtils.checkMandatoryParam("modelName", modelName);
        PageUtils.checkMandatoryParam("nameSpace", nameSpace);
        PageUtils.checkMandatoryParam("prefix", prefix);

        try
        {
            ModelManagerPage cmmPage = getSharePage(driver).render();

            CreateNewModelPopUp createNewModelPopUpPage = cmmPage.clickCreateNewModelButton().render();
            createNewModelPopUpPage.setName(modelName);
            createNewModelPopUpPage.setNameSpace(nameSpace).render();
            createNewModelPopUpPage.setPrefix(prefix);
            createNewModelPopUpPage.setAuthor(modelName);
            createNewModelPopUpPage.setDescription(modelName).render();

            return createNewModelPopUpPage.selectCreateModelButton(createAction).render();
        }
        catch (PageException pe)
        {
            throw new UnexpectedSharePageException(ModelManagerPage.class, pe);
        }
    }

    /**
     * Util to Delete the specified model
     */
    public void deleteModel(WebDriver driver, String modelName)
    {
        PageUtils.checkMandatoryParam("Model Name must be specified", modelName);

        String deleteAction = factoryPage.getValue("cmm.model.action.delete");

        try
        {
            ModelManagerPage cmmPage = getSharePage(driver).render();

            ActionsSet actions = cmmPage.getCustomModelRowByName(modelName).getCmActions();
            actions.clickActionByName(deleteAction);
            String q = String.format("//div[@id='CMM_DELETE_MODEL_DIALOG']//*[text()='%s']", deleteAction);
            driver.findElement(By.xpath(q)).click();
        }
        catch (PageOperationException e)
        {
            // Do not throw an exception since it's being used by tests to confirm something isn't deleted.
            logger.debug("No Open Dialog found: ", e);
        }
        catch (PageException c)
        {
            throw new PageOperationException("Unable to delete Model: " + modelName, c);
        }
    }

    /**
     * Util to activate / deactivate the specified model from the ModelManagerPage
     */
    public HtmlPage setModelActive(WebDriver driver, String modelName, boolean activateModel)
    {
        PageUtils.checkMandatoryParam("Model Name must be specified", modelName);

        String actionName = factoryPage.getValue("cmm.model.action.activate");

        if (!activateModel)
        {
            actionName = factoryPage.getValue("cmm.model.action.deactivate");
        }

        try
        {
            ModelManagerPage cmmPage = getSharePage(driver).render();

            ActionsSet actions = cmmPage.getCustomModelRowByName(modelName).getCmActions();
            return actions.clickActionByName(actionName).render();
        }
        catch (PageException c)
        {
            throw new PageOperationException(String.format("Unable to perform the action %s on the Model: %s", actionName, modelName), c);
        }
    }

    /**
     * Navigate to ManageTypesAndAspectsPage for the selected model from the ModelManagerPage Or ManagePropertiesPage
     * 
     * @return HtmlPage
     * @throws Exception the exception
     */
    public HtmlPage viewTypesAspectsForModel(WebDriver driver, String modelName) throws Exception
    {
        PageUtils.checkMandatoryParam("Model Name must be specified", modelName);

        try
        {
            ManageTypesAndAspectsPage typesAspectsPage;

            SharePage page = getSharePage(driver);

            if (page instanceof ManagePropertiesPage)
            {
                ManagePropertiesPage propertiesPage = page.render();
                page = propertiesPage.selectBackToTypesPropertyGroupsButton().render();
            }

            if (page instanceof ManageTypesAndAspectsPage)
            {
                typesAspectsPage = page.render();
                if (typesAspectsPage.isForModel(modelName))
                {
                    return typesAspectsPage;
                }
                else
                {
                    page = typesAspectsPage.selectBackToModelsButton().render();
                }
            }

            if (page instanceof ModelManagerPage)
            {
                ModelManagerPage modelManagerPage = page.render();
                typesAspectsPage = modelManagerPage.selectCustomModelRowByName(modelName).render();
                return typesAspectsPage;
            }

        }
        catch (PageException c)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, c);
        }
        throw new UnsupportedOperationException("Unable to navigate to ManageTypesAndAspectsPage");
    }

    /**
     * Util to Add a new Prop Group or Aspect to the selected model from {@link ManageTypesAndAspectsPage}
     */
    public HtmlPage createAspect(WebDriver driver, String aspectName)
    {

        PageUtils.checkMandatoryParam("Aspect Name must be specified", aspectName);

        try
        {
            ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();

            CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = manageTypesAndAspectsPage.clickCreateNewPropertyGroupButton().render();

            createNewPropertyGroupPopUp.setNameField(aspectName).render();
            createNewPropertyGroupPopUp.setDescriptionField(aspectName).render();
            createNewPropertyGroupPopUp.setTitleField(aspectName).render();
            return createNewPropertyGroupPopUp.selectCreateButton().render();
        }
        catch (PageException c)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, c);
        }
    }

    /**
     * Util to Add a new Prop Group or Aspect of the specified parent aspect from {@link ManageTypesAndAspectsPage}
     */
    public HtmlPage createAspect(WebDriver driver, String aspectName, String parentAspect)
    {

        PageUtils.checkMandatoryParam("Aspect Name must be specified", aspectName);
        PageUtils.checkMandatoryParam("Aspect Name must be specified", parentAspect);

        try
        {
            ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();

            CreateNewPropertyGroupPopUp createNewPropertyGroupPopUp = manageTypesAndAspectsPage.clickCreateNewPropertyGroupButton().render();

            createNewPropertyGroupPopUp.setNameField(aspectName).render();
            createNewPropertyGroupPopUp.setParentPropertyGroupField(parentAspect);
            createNewPropertyGroupPopUp.setDescriptionField(aspectName).render();
            createNewPropertyGroupPopUp.setTitleField(aspectName).render();
            return createNewPropertyGroupPopUp.selectCreateButton().render();
        }
        catch (PageException c)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, c);
        }
    }

    /**
     * Util to Add a new Type to the selected model from {@link ManageTypesAndAspectsPage} Expects the ManageTypesAndAspectsPage is available
     */
    public HtmlPage createType(WebDriver driver, String typeName)
    {

        PageUtils.checkMandatoryParam("Type Name must be specified", typeName);

        try
        {
            ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();

            CreateNewCustomTypePopUp newType = manageTypesAndAspectsPage.clickCreateNewCustomTypeButton().render();

            newType.setNameField(typeName);
            newType.setDescriptionField(typeName);

            // Set the title as unique: as Title gets used in the <Change Type> drop down if specified and can not be guaranteed to be unique
            newType.setTitleField(typeName);

            return newType.selectCreateButton().render();
        }
        catch (PageException c)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, c);
        }
    }

    /**
     * Util to Add a new Type based on the specified parent type from {@link ManageTypesAndAspectsPage} Expects the ManageTypesAndAspectsPage is available
     */
    public HtmlPage createType(WebDriver driver, String typeName, String parentType)
    {

        PageUtils.checkMandatoryParam("Type Name must be specified", typeName);
        PageUtils.checkMandatoryParam("Parent Name must be specified", parentType);

        try
        {
            ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();

            CreateNewCustomTypePopUp newType = manageTypesAndAspectsPage.clickCreateNewCustomTypeButton().render();

            newType.setNameField(typeName);
            newType.selectParentTypeField(parentType);
            newType.setDescriptionField(typeName);

            // Set the title as unique: as Title gets used in the <Change Type> drop down if specified and can not be guaranteed to be unique
            newType.setTitleField(typeName);

            return newType.selectCreateButton().render();
        }
        catch (PageException c)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, c);
        }
    }

    /**
     * Delete Action for a Property Group / Aspect
     * 
     * @param driver WebDriver
     * @param aspectName String name of the aspect to be deleted
     * @param confirmDeleteAction String Ok or Cancel action on the confirmation dialogue
     * @return HtmlPage
     */
    public HtmlPage deleteAspect(WebDriver driver, String aspectName, String confirmDeleteAction)
    {
        PageUtils.checkMandatoryParam("Aspect Name", aspectName);
        PageUtils.checkMandatoryParam("confirmDeleteAction", confirmDeleteAction);

        String deleteAction = factoryPage.getValue("cmm.model.action.delete");
        String deleteActionCancel = factoryPage.getValue("cmm.model.action.cancel");

        if (!confirmDeleteAction.equalsIgnoreCase(deleteActionCancel))
        {
            confirmDeleteAction = factoryPage.getValue("cmm.model.action.delete");
        }

        try
        {
            ManageTypesAndAspectsPage typesAspectsPage = getSharePage(driver).render();
            ModelPropertyGroupRow row = typesAspectsPage.getCustomModelPropertyGroupRowByName(aspectName);

            ActionsSet actions = row.getActions();

            if (actions.hasActionByName(deleteAction))
            {
                actions.clickActionByName(deleteAction);
                String q = String.format("//div[@id='CMM_DELETE_PROPERTYGROUP_DIALOG']//*[text()='%s']", deleteAction);
                driver.findElement(By.xpath(q)).click();
                return getSharePage(driver);
//                return actions.clickActionByNameAndDialogByButtonName(deleteAction, confirmDeleteAction).render();
            }
        }
        catch (PageException pe)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, pe);
        }
        throw new PageOperationException(String.format("Unable to Delete Aspect: %s", aspectName));
    }

    /**
     * Delete Action for a Type
     * 
     * @param driver WebDriver
     * @param typeName String name of the type to be deleted
     * @param confirmDeleteAction String Ok or Cancel action on the confirmation dialogue
     * @return HtmlPage
     */
    public HtmlPage deleteType(WebDriver driver, String typeName, String confirmDeleteAction)
    {
        PageUtils.checkMandatoryParam("Type Name", typeName);
        PageUtils.checkMandatoryParam("confirmDeleteAction", confirmDeleteAction);

        String deleteAction = factoryPage.getValue("cmm.model.action.delete");
        String deleteActionCancel = factoryPage.getValue("cmm.model.action.cancel");

        if (!confirmDeleteAction.equalsIgnoreCase(deleteActionCancel))
        {
            confirmDeleteAction = factoryPage.getValue("cmm.model.action.delete");
        }

        try
        {
            ManageTypesAndAspectsPage typesAspectsPage = getSharePage(driver).render();
            ModelTypeRow row = typesAspectsPage.getCustomModelTypeRowByName(typeName);

            ActionsSet actions = row.getActions();

            if (actions.hasActionByName(deleteAction))
            {
                actions.clickActionByName(deleteAction);
                String q = String.format("//div[@id='CMM_DELETE_TYPE_DIALOG']//*[text()='%s']", deleteAction);
                driver.findElement(By.xpath(q)).click();
                return getSharePage(driver);
            }
        }
        catch (PageException pe)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, pe);
        }
        throw new PageOperationException(String.format("Unable to Delete Type: %s", typeName));
    }

    /**
     * Navigate to ManagePropertiesPage for the selected Type or Aspect Expects ManageTypesAndAspectsPage is available Assumes business requirement that Type
     * and Aspect should have unique name within the Model
     * 
     * @return HtmlPage
     * @throws Exception the exception
     */
    public HtmlPage viewProperties(WebDriver driver, String typeAspectName) throws Exception
    {
        PageUtils.checkMandatoryParam("Type or Aspect Name must be specified", typeAspectName);

        SharePage page = getSharePage(driver).render();

        if (page instanceof ManagePropertiesPage)
        {
            ManagePropertiesPage propertyListPage = page.render();
            page = propertyListPage.selectBackToTypesPropertyGroupsButton().render();
        }

        if (page instanceof ManageTypesAndAspectsPage)
        {
            ManageTypesAndAspectsPage typesAspectsPage = page.render();

            if (typesAspectsPage.isCustomTypeRowDisplayed(typeAspectName))
            {
                return typesAspectsPage.selectCustomTypeRowByName(typeAspectName).render();
            }
            else if (typesAspectsPage.isPropertyGroupRowDisplayed(typeAspectName))
            {
                return typesAspectsPage.selectPropertyGroupRowByName(typeAspectName).render();
            }
        }
        throw new PageOperationException("Unable to View Properties for Type / Aspect: " + typeAspectName);

    }

    /**
     * Utility to Create New Property using default settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param propertyName
     * @return
     */
    public HtmlPage createProperty(WebDriver driver, String propertyName)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        ManagePropertiesPage propertyListPage = getSharePage(driver).render();

        CreateNewPropertyPopUp createPropertyPopup = propertyListPage.clickCreateNewPropertyButton().render();

        createPropertyPopup.setNameField(propertyName);

        return createPropertyPopup.selectCreateButton().render();
    }

    /**
     * Utility to Create New Property with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param propertyName
     * @return
     */
    public HtmlPage createProperty(WebDriver driver, String propertyName, String title, String desc, DataType dataType, MandatoryClassifier mandatory, boolean multivalued, String defaultValue)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        String datatype = factoryPage.getValue(dataType.getListValue());
        String mandatoryClassifier = factoryPage.getValue(mandatory.getListValue());

        ManagePropertiesPage propertyListPage = getSharePage(driver).render();

        CreateNewPropertyPopUp createPropertyPopup = propertyListPage.clickCreateNewPropertyButton().render();

        createPropertyPopup.setNameField(propertyName);
        createPropertyPopup.setTitleField(title);
        createPropertyPopup.setDescriptionField(desc);

        createPropertyPopup.setDataTypeField(datatype);
        createPropertyPopup.setMandatoryField(mandatoryClassifier);

        if (multivalued)
        {
            createPropertyPopup.clickMultipleField();
        }

        createPropertyPopup.setDefaultValue(dataType, defaultValue);

        return createPropertyPopup.selectCreateButton().render();
    }

    /**
     * Utility to Create New Property with Constraints, with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param propertyName
     * @return
     */
    public HtmlPage createPropertyWithConstraint(WebDriver driver, String propertyName, String title, String desc, DataType dataType, MandatoryClassifier mandatory, boolean multivalued, String defaultValue, ConstraintDetails constraintDetails)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        String datatype = factoryPage.getValue(dataType.getListValue());
        String mandatoryClassifier = factoryPage.getValue(mandatory.getListValue());

        ManagePropertiesPage propertyListPage = getSharePage(driver).render();

        CreateNewPropertyPopUp createPropertyPopup = propertyListPage.clickCreateNewPropertyButton().render();

        createPropertyPopup.setNameField(propertyName);
        createPropertyPopup.setTitleField(title);
        createPropertyPopup.setDescriptionField(desc);

        createPropertyPopup.setDataTypeField(datatype);
        createPropertyPopup.setMandatoryField(mandatoryClassifier);

        if (multivalued)
        {
            createPropertyPopup.clickMultipleField();
        }

        createPropertyPopup.setDefaultValue(dataType, defaultValue);

        if (ConstraintTypes.REGEX.equals(constraintDetails.getType()))
        {
            createPropertyPopup.addRegexConstraint(constraintDetails);
        }
        else if (ConstraintTypes.MINMAXLENGTH.equals(constraintDetails.getType()))
        {
            createPropertyPopup.addLengthConstraint(constraintDetails);
        }
        else if (ConstraintTypes.MINMAXVALUE.equals(constraintDetails.getType()))
        {
            createPropertyPopup.addMinMaxValueConstraint(constraintDetails);
        }
        else if (ConstraintTypes.LIST.equals(constraintDetails.getType()))
        {
            createPropertyPopup.addListConstraint(constraintDetails);
        }
        else if (ConstraintTypes.JAVACLASS.equals(constraintDetails.getType()))
        {
            createPropertyPopup.addJavaClassConstraint(constraintDetails);
        }
        else
        {
            // Select No Constraint
        }

        return createPropertyPopup.selectCreateButton().render();
    }

    /**
     * Utility to Edit Model: Expects the user to be on ModelManagerPage
     * 
     * @param WebDriver driver
     * @param String modelName
     * @param String nameSpace
     * @param String prefix
     * @return HtmlPage
     */
    public HtmlPage editModel(WebDriver driver, String modelName, String nameSpace, String prefix)
    {
        PageUtils.checkMandatoryParam("modelName", modelName);
        PageUtils.checkMandatoryParam("nameSpace", nameSpace);
        PageUtils.checkMandatoryParam("prefix", prefix);

        try
        {
            ModelManagerPage cmmPage = getSharePage(driver).render();

            ModelRow row = cmmPage.getCustomModelRowByName(modelName);
            ActionsSet actions = row.getCmActions();
            EditModelPopUp editModelPopUp = actions.clickActionByName("Edit").render();
            editModelPopUp.setNameSpace(nameSpace).render();
            editModelPopUp.setPrefix(prefix).render();
            return editModelPopUp.selectEditModelButton("Save").render();
        }
        catch (PageException pe)
        {
            throw new UnexpectedSharePageException(ModelManagerPage.class, pe);
        }
    }

    /**
     * Util to get the Edit Type PopUp for the specified type Name
     * 
     * @param driver
     * @param typeName
     * @return EditCustomTypePopUp
     */
    public EditCustomTypePopUp getEditTypePopUp(WebDriver driver, String typeName)
    {
        PageUtils.checkMandatoryParam("Type Name must be specified", typeName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();
        ModelTypeRow typeRow = manageTypesAndAspectsPage.getCustomModelTypeRowByName(typeName);
        ActionsSet actions = typeRow.getActions();

        if (actions.hasActionByName(editAction))
        {
            return actions.clickActionByName(editAction).render();
        }

        throw new PageOperationException("Error Getting Edit Action for the Type: " + typeName);
    }

    /**
     * Util to get the Edit Property Group / Aspect PopUp for the specified Aspect Name
     * 
     * @param driver
     * @param aspectName
     * @return EditPropertyGroupPopUp
     */
    public EditPropertyGroupPopUp getEditAspectPopUp(WebDriver driver, String aspectName)
    {
        PageUtils.checkMandatoryParam("Aspect Name must be specified", aspectName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();
        ModelPropertyGroupRow aspectRow = manageTypesAndAspectsPage.getCustomModelPropertyGroupRowByName(aspectName);
        ActionsSet actions = aspectRow.getActions();

        if (actions.hasActionByName(editAction))
        {
            return actions.clickActionByName(editAction).render();
        }

        throw new PageOperationException("Error Getting Edit Action for the Aspect: " + aspectName);
    }

    /**
     * Util to Edit Type
     * 
     * @param driver
     * @param typeName
     * @param title
     * @param description
     * @param parentType
     * @return ManageTypesAndAspectsPage
     */
    public HtmlPage editType(WebDriver driver, String typeName, String title, String description, String parentType)
    {
        PageUtils.checkMandatoryParam("Type Name must be specified", typeName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        try
        {
            EditCustomTypePopUp editType = getEditTypePopUp(driver, typeName);
            editType.setTitleField(title);
            editType.setDescriptionField(description);
            editType.selectParentTypeField(parentType);
            return editType.selectSaveButton().render();
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, ce);
        }
        catch (Exception e)
        {
            logger.error("Error Editing Type", e);
        }
        throw new PageOperationException("Error Editing Type");

    }

    /**
     * Util to Edit Aspect
     * 
     * @param driver
     * @param aspectName
     * @param title
     * @param description
     * @param parentType
     * @return ManageTypesAndAspectsPage
     */
    public HtmlPage editAspect(WebDriver driver, String aspectName, String title, String description, String parentType)
    {
        PageUtils.checkMandatoryParam("Aspect Name must be specified", aspectName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        try
        {
            EditPropertyGroupPopUp editAspect = getEditAspectPopUp(driver, aspectName);
            editAspect.setTitleField(title);
            editAspect.setDescriptionField(description);
            editAspect.selectParentPropertyGroupField(parentType);
            return editAspect.selectSaveButton().render();
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, ce);
        }
        catch (Exception e)
        {
            logger.error("Error Editing Aspect", e);
        }
        throw new PageOperationException("Error Editing Aspect");

    }

    /**
     * Util to Edit Type
     * 
     * @param driver
     * @param typeName
     * @param title
     * @param description
     * @param parentType
     * @return ManageTypesAndAspectsPage
     */
    public HtmlPage editTypewithoutParent(WebDriver driver, String typeName, String title, String description)
    {
        PageUtils.checkMandatoryParam("Type Name must be specified", typeName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        try
        {
            EditCustomTypePopUp editType = getEditTypePopUp(driver, typeName);
            editType.setTitleField(title);
            editType.setDescriptionField(description);
            return editType.selectSaveButton().render();
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, ce);
        }
        catch (Exception e)
        {
            logger.error("Error Editing Type", e);
        }
        throw new PageOperationException("Error Editing Type");

    }

    /**
     * Util to Edit Aspect
     * 
     * @param driver
     * @param aspectName
     * @param title
     * @param description
     * @param parentType
     * @return ManageTypesAndAspectsPage
     */
    public HtmlPage editAspectWithoutParent(WebDriver driver, String aspectName, String title, String description)
    {
        PageUtils.checkMandatoryParam("Aspect Name must be specified", aspectName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        try
        {
            EditPropertyGroupPopUp editAspect = getEditAspectPopUp(driver, aspectName);
            editAspect.setTitleField(title);
            editAspect.setDescriptionField(description);
            return editAspect.selectSaveButton().render();
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, ce);
        }
        catch (Exception e)
        {
            logger.error("Error Editing Aspect", e);
        }
        throw new PageOperationException("Error Editing Aspect");

    }

    /**
     * Delete Action for a Property
     * 
     * @param driver WebDriver
     * @param proeprtyName String name of the property to be deleted
     * @param confirmDeleteAction String Ok or Cancel action on the confirmation dialogue
     * @return HtmlPage
     */
    public HtmlPage deleteProperty(WebDriver driver, String propertyName, String confirmDeleteAction)
    {
        PageUtils.checkMandatoryParam("Property Name", propertyName);
        PageUtils.checkMandatoryParam("confirmDeleteAction", confirmDeleteAction);

        String deleteAction = factoryPage.getValue("cmm.model.action.delete");
        String deleteActionCancel = factoryPage.getValue("cmm.model.action.cancel");

        if (!confirmDeleteAction.equalsIgnoreCase(deleteActionCancel))
        {
            confirmDeleteAction = factoryPage.getValue("cmm.model.action.delete");
        }

        try
        {
            ManagePropertiesPage propertiesPage = getSharePage(driver).render();
            PropertyRow row = propertiesPage.getPropertyRowByName(propertyName);

            ActionsSet actions = row.getActions();

            if (actions.hasActionByName(deleteAction))
            {
                actions.clickActionByName(deleteAction);
                String q = String.format("//div[@id='CMM_DELETE_PROPERTY_DIALOG']//*[text()='%s']", deleteAction);
                driver.findElement(By.xpath(q)).click();
                return getSharePage(driver);
            }
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException(ManagePropertiesPage.class, ce);
        }
        throw new PageOperationException(String.format("Unable to Delete Property: %s", propertyName));
    }

    /**
     * Util to get EditPropertyForType
     * 
     * @param driver
     * @param typename
     * @param propertyName
     * @return EditPropertyPopUp
     */
    public EditPropertyPopUp getEditPropertyForType(WebDriver driver, String typeAspectName, String compoundPropertyName)
    {
        PageUtils.checkMandatoryParam("Type Name must be specified", typeAspectName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();

        ManagePropertiesPage managePropertiesPage = manageTypesAndAspectsPage.selectCustomTypeRowByName(typeAspectName).render();

        ActionsSet actions = managePropertiesPage.getPropertyRowByName(compoundPropertyName).getActions();

        if (actions.hasActionByName(editAction))
        {
            return actions.clickActionByName(editAction).render();
        }

        throw new PageOperationException("Error Getting Edit Action for the Aspect: " + compoundPropertyName);
    }

    /**
     * Util to get EditPropertyForAspect
     * 
     * @param driver
     * @param aspectname
     * @param propertyName
     * @return EditPropertyPopUp
     */

    public EditPropertyPopUp getEditPropertyForAspect(WebDriver driver, String typeAspectName, String compoundPropertyName)

    {
        PageUtils.checkMandatoryParam("Aspect Name must be specified", typeAspectName);

        editAction = factoryPage.getValue("cmm.model.action.edit");

        ManageTypesAndAspectsPage manageTypesAndAspectsPage = getSharePage(driver).render();

        ManagePropertiesPage managePropertiesPage = manageTypesAndAspectsPage.selectPropertyGroupRowByName(typeAspectName).render();

        ActionsSet actions = managePropertiesPage.getPropertyRowByName(compoundPropertyName).getActions();

        if (actions.hasActionByName(editAction))
        {
            return actions.clickActionByName(editAction).render();
        }

        throw new PageOperationException("Error Getting Edit Action for the Aspect: " + compoundPropertyName);
    }

    /**
     * Util to get EditPropertyForAspect/Type
     * 
     * @param driver
     * @param aspectname/typename
     * @param propertyName
     * @return EditPropertyPopUp
     */
    public EditPropertyPopUp getEditPropertyPopUp(WebDriver driver, String typeAspectName, String compoundPropertyName)
    {
        PageUtils.checkMandatoryParam("Type or Aspect Name must be specified", typeAspectName);

        SharePage page = getSharePage(driver);

        if (page instanceof ManagePropertiesPage)
        {
            ManagePropertiesPage propList = page.render();

            // Check if Right Property is displayed
            if (!propList.isPropertyRowDisplayed(compoundPropertyName))
            {
                page = propList.selectBackToTypesPropertyGroupsButton().render();
            }
            else
            {
                ActionsSet actions = propList.getPropertyRowByName(compoundPropertyName).getActions();

                if (actions.hasActionByName(editAction))
                {
                    return actions.clickActionByName(editAction).render();
                }
            }
        }

        if (page instanceof ManageTypesAndAspectsPage)
        {
            ManageTypesAndAspectsPage typesAspectsPage = page.render();

            if (typesAspectsPage.isCustomTypeRowDisplayed(typeAspectName))
            {
                return getEditPropertyForType(driver, typeAspectName, compoundPropertyName);
            }
            else if (typesAspectsPage.isPropertyGroupRowDisplayed(typeAspectName))
            {
                return getEditPropertyForAspect(driver, typeAspectName, compoundPropertyName);
            }
        }
        throw new PageOperationException("Unable to View Properties for Type / Aspect: " + typeAspectName);
    }

    /**
     * Utility to Edit Property for draft model with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param type/aspectName
     * @param propertyName
     * @return ManagePropertiesPage
     */
    public HtmlPage editProperty(WebDriver driver, String typeAspectName, String propertyName, String title, String desc, DataType dataType, MandatoryClassifier mandatory, boolean multivalued, String defaultValue)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        String datatype = factoryPage.getValue(dataType.getListValue());
        String mandatoryClassifier = factoryPage.getValue(mandatory.getListValue());

        EditPropertyPopUp editPropertyPopup = getEditPropertyPopUp(driver, typeAspectName, propertyName);
        editPropertyPopup.setTitleField(title);
        editPropertyPopup.setDescriptionField(desc);
        editPropertyPopup.setDataTypeField(datatype);
        editPropertyPopup.setMandatoryField(mandatoryClassifier);

        if (multivalued)
        {
            editPropertyPopup.clickMultipleField();
        }

        editPropertyPopup.setDefaultValue(dataType, defaultValue);

        return editPropertyPopup.selectSaveButton();
    }

    /**
     * Utility to Edit Property for draft model with Constraints, with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param propertyName
     * @return HtmlPage
     */
    public HtmlPage editPropertyWithConstraint(WebDriver driver, String typeAspectName, String propertyName, String title, String desc, DataType dataType, MandatoryClassifier mandatory, boolean multivalued, String defaultValue, ConstraintDetails constraintDetails)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        String datatype = factoryPage.getValue(dataType.getListValue());
        String mandatoryClassifier = factoryPage.getValue(mandatory.getListValue());

        EditPropertyPopUp editPropertyPopup = getEditPropertyPopUp(driver, typeAspectName, propertyName).render();

        editPropertyPopup.setTitleField(title);
        editPropertyPopup.setDescriptionField(desc);
        editPropertyPopup.setDataTypeField(datatype);
        editPropertyPopup.setMandatoryField(mandatoryClassifier);

        if (multivalued)
        {
            editPropertyPopup.clickMultipleField();
        }

        editPropertyPopup.setDefaultValue(dataType, defaultValue);

        if (ConstraintTypes.REGEX.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editRegexConstraint(constraintDetails);
        }
        else if (ConstraintTypes.MINMAXLENGTH.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editLengthConstraint(constraintDetails);
        }
        else if (ConstraintTypes.MINMAXVALUE.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editMinMaxValueConstraint(constraintDetails);
        }
        else if (ConstraintTypes.LIST.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editListConstraint(constraintDetails);
        }
        else if (ConstraintTypes.JAVACLASS.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editJavaClassConstraint(constraintDetails);
        }
        else
        {
            editPropertyPopup.editNoneClassConstraint(constraintDetails);
        }

        return editPropertyPopup.selectSaveButton().render();
    }

    /**
     * Utility to Edit Property for Active model with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param type/aspectName
     * @param propertyName
     * @return ManagePropertiesPage
     */
    public HtmlPage editPropertyForAM(WebDriver driver, String typeAspectName, String propertyName, String title, String desc, String defaultValue)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        EditPropertyPopUp editPropertyPopup = getEditPropertyPopUp(driver, typeAspectName, propertyName).render();
        editPropertyPopup.setTitleField(title);
        editPropertyPopup.setDescriptionField(desc);
        editPropertyPopup.setDefaultValue(DataType.findByListValue(editPropertyPopup.getDataTypeField(), factoryPage), defaultValue);
        return editPropertyPopup.selectSaveButton().render();
    }

    /**
     * Utility to Edit Property for Active model with Constraints, with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param propertyName
     * @return HtmlPage
     */
    public HtmlPage editPropertyWithConstraintForAM(WebDriver driver, String typeAspectName, String propertyName, String title, String desc, String defaultValue, ConstraintDetails constraintDetails)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        EditPropertyPopUp editPropertyPopup = getEditPropertyPopUp(driver, typeAspectName, propertyName).render();

        editPropertyPopup.setTitleField(title);
        editPropertyPopup.setDescriptionField(desc);

        editPropertyPopup.setDefaultValue(DataType.findByListValue(editPropertyPopup.getDataTypeField(), factoryPage), defaultValue);

        if (ConstraintTypes.REGEX.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editRegexConstraint(constraintDetails);
        }
        else if (ConstraintTypes.MINMAXLENGTH.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editLengthConstraint(constraintDetails);
        }
        else if (ConstraintTypes.MINMAXVALUE.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editMinMaxValueConstraint(constraintDetails);
        }
        else if (ConstraintTypes.LIST.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editListConstraint(constraintDetails);
        }
        else if (ConstraintTypes.JAVACLASS.equals(constraintDetails.getType()))
        {
            editPropertyPopup.editJavaClassConstraint(constraintDetails);
        }
        else
        {
            editPropertyPopup.editNoneClassConstraint(constraintDetails);
        }

        return editPropertyPopup.selectSaveButton().render();
    }

    /**
     * Util compares the Expected Props with Actual Prop values displayed on Share
     * Returns true if these match
     * 
     * @param driver
     * @param expectedProps
     * @return
     */

    public boolean compareCMProperties(WebDriver driver, Map<String, Object> expectedProps)
    {
        boolean match = true;

        PageUtils.checkMandatoryParam("Expected Properties Map", expectedProps);

        Map<String, Object> propsToCompare = new HashMap<String, Object>();

        String noValue = factoryPage.getValue("property.value.empty");

        String notDisplayed = factoryPage.getValue("property.value.not.displayed");

        DetailsPage detailsPage = getSharePage(driver).render();

        Map<String, Object> actualProps = detailsPage.getProperties();

        for (Map.Entry<String, Object> entry : expectedProps.entrySet())
        {
            String propertyName = entry.getKey().replace(":", "");

            // Value Empty: Not specified (None)
            if (entry.getValue().equals(""))
            {
                // Share shows value as (None) = noValue, when value is not available
                entry.setValue(noValue);
                logger.debug("Value not Specified or (None) for Property" + propertyName);
            }

            propsToCompare.put(propertyName, actualProps.get(propertyName));

            if (entry.getValue().toString().equals(actualProps.get(propertyName)))
            {
                // Values match
                match = match && true;
            }
            else
            {
                // Value not found or displayed (Null) - Property not expected to be displayed
                if (null == actualProps.get(propertyName) && entry.getValue().equals(notDisplayed))
                {
                    match = match && true;
                    logger.info("Value not displayed or (null) as expected" + propertyName);
                }
                // Value not found or displayed (Null) - Property is expected to be displayed
                else if (null == actualProps.get(propertyName))
                {
                    match = false;
                    logger.error("Value not displayed or (null)" + propertyName);
                }
                // Value not specified (None)
                else if (noValue.equals(actualProps.get(propertyName)))
                {
                    match = match && true;
                    logger.debug("Value not Specified or (None) for Property" + propertyName);
                }
                // Value does not match
                else
                {
                    match = false;
                    logger.error(String.format(
                            "Value not displayed Or Unexpected Property Value for: %s Expected: %s, Actual %s",
                            propertyName,
                            entry.getValue(),
                            actualProps.get(propertyName)));
                }
            }
        }

        // Unable to use propsToCompare.equals(expectedProps), as there may be extra properties displayed
        return match;
    }

    /**
     * Util Returns the Prop value displayed on Share
     * 
     * @param driver
     * @param String propToCheck: NAme of the property to be checked
     * @return Object
     */
    public Object getPropertyValue(WebDriver driver, String propToCheck)
    {
        PageUtils.checkMandatoryParam("Properties to be checked", propToCheck);

        DetailsPage detailsPage = getSharePage(driver).render();

        Map<String, Object> actualProps = detailsPage.getProperties();

        return actualProps.get(propToCheck);
    }

    /**
     * Selects Form Editor Action for a Type / Aspect
     * 
     * @param driver WebDriver
     * @param typeAspectName String name of the aspect for which FormLayout is to be defined
     * @return HtmlPage
     */
    public HtmlPage getFormEditorForTypeOrAspect(WebDriver driver, String typeAspectName)
    {
        PageUtils.checkMandatoryParam("Type or Aspect Name", typeAspectName);

        String editFormsAction = factoryPage.getValue("cmm.model.action.form.editor");

        FormEditorPage formEditor = null;

        try
        {
            ManageTypesAndAspectsPage typesAspectsPage = getSharePage(driver).render();

            if (typesAspectsPage.isCustomTypeRowDisplayed(typeAspectName))
            {
                ModelTypeRow row = typesAspectsPage.getCustomModelTypeRowByName(typeAspectName);

                ActionsSet actions = row.getActions();

                if (actions.hasActionByName(editFormsAction))
                {
                    formEditor = actions.clickActionByName(editFormsAction).render();
                }
            }
            else if (typesAspectsPage.isPropertyGroupRowDisplayed(typeAspectName))
            {

                ModelPropertyGroupRow row = typesAspectsPage.getCustomModelPropertyGroupRowByName(typeAspectName);

                ActionsSet actions = row.getActions();

                if (actions.hasActionByName(editFormsAction))
                {
                    formEditor = actions.clickActionByName(editFormsAction).render();
                }
            }
            return formEditor;
        }
        catch (PageException | PageOperationException pe)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, pe);
        }
    }

    /**
     * Selects and Applies Default Form Layout for Type / Aspect
     * 
     * @param driver WebDriver
     * @param typeAspectName String name of the aspect for which FormLayout is to be defined
     * @return HtmlPage
     */
    public HtmlPage applyDefaultLayoutForTypeOrAspect(WebDriver driver, String typeAspectName)
    {
        try
        {
            String applyAction = factoryPage.getValue("cmm.model.action.apply");

            // Get Form Editor
            FormEditorPage formEditor = getFormEditorForTypeOrAspect(driver, typeAspectName).render();

            // Apply Default Layout
            ApplyDefaultLayoutPopUp applyDefaultPopup = formEditor.selectDefaultLayoutButton().render();
            formEditor = applyDefaultPopup.clickActionByName(applyAction).render();

            // Save
            formEditor = formEditor.selectSaveButton().render();

            // Back to ManagePropertiesPage
            return formEditor.selectBackToTypesPropertyGroupsButton().render();
        }
        catch (PageException | PageOperationException pe)
        {
            throw new UnexpectedSharePageException(ManageTypesAndAspectsPage.class, pe);
        }
    }

    /**
     * Util to FindInstances for Type / Aspect
     * 
     * @param driver
     * @param aspectname / typename
     * @return HtmlPage
     */
    public HtmlPage findInstances(WebDriver driver, String typeAspectName)
    {
        PageUtils.checkMandatoryParam("Type or Aspect Name must be specified", typeAspectName);

        findInstanceAction = factoryPage.getValue("cmm.Find.Instance");

        ActionsSet actions = null;

        SharePage page = getSharePage(driver).render();

        if (page instanceof ManageTypesAndAspectsPage)
        {
            ManageTypesAndAspectsPage typesAspectsPage = page.render();

            if (typesAspectsPage.isCustomTypeRowDisplayed(typeAspectName))
            {
                actions = typesAspectsPage.getCustomModelTypeRowByName(typeAspectName).getActions();
            }
            else if (typesAspectsPage.isPropertyGroupRowDisplayed(typeAspectName))
            {
                actions = typesAspectsPage.getCustomModelPropertyGroupRowByName(typeAspectName).getActions();
            }
        }

        if (actions != null)
        {
            if (actions.hasActionByName(findInstanceAction))
            {
                return actions.clickActionByName(findInstanceAction).render();
            }
        }
        throw new PageOperationException("Find Instances action not found for Type / Aspect: " + typeAspectName);
    }

    public String getSearchTerm(WebDriver driver, boolean isType, String modelName, String typeAspectName)
    {
        String typeOrAspect = "ASPECT";
        if (isType)
        {
            typeOrAspect = "TYPE";
        }
        return String.format("#searchTerm=%s%s%s%s%s%s%s&scope=repo", typeOrAspect, "%3A", "%22", modelName, "%3A", typeAspectName, "%22");
    }

    /**
     * Utility to Create New Property with IndexingOption, with specified settings for Data Type, Mandatory etc
     * 
     * @param driver
     * @param propertyName
     * @param title
     * @param desc
     * @param dataType
     * @param mandatory
     * @param multivalued
     * @param defaultValue
     * @param indexingOption
     * @return HtmlPage
     */
    public HtmlPage createPropertyWithIndexingOption(WebDriver driver, String propertyName, String title, String desc, DataType dataType, MandatoryClassifier mandatory, boolean multivalued, String defaultValue, IndexingOptions indexingOption)
    {
        PageUtils.checkMandatoryParam("Property Name must be specified", propertyName);

        String datatype = factoryPage.getValue(dataType.getListValue());
        String mandatoryClassifier = factoryPage.getValue(mandatory.getListValue());

        ManagePropertiesPage propertyListPage = getSharePage(driver).render();

        CreateNewPropertyPopUp createPropertyPopup = propertyListPage.clickCreateNewPropertyButton().render();

        createPropertyPopup.setNameField(propertyName);
        createPropertyPopup.setTitleField(title);
        createPropertyPopup.setDescriptionField(desc);

        createPropertyPopup.setDataTypeField(datatype);
        createPropertyPopup.setMandatoryField(mandatoryClassifier);

        if (multivalued)
        {
            createPropertyPopup.clickMultipleField();
        }

        createPropertyPopup.setDefaultValue(dataType, defaultValue);

        createPropertyPopup.setIndexingOption(indexingOption);

        return createPropertyPopup.selectCreateButton().render();
    }

    /**
     * Util to perform search using the given search string and value
     * 
     * @param driver
     * @param searchString
     * @return FacetedSearchPage
     */
    public HtmlPage search(WebDriver driver, String searchString)
    {
        SearchBox search = getSharePage(driver).getSearch();
        FacetedSearchPage resultPage = search.search(searchString).render();
        return resultPage;
    }

    /**
     * Util to perform search and check if search results are as expected
     * 
     * @param driver
     * @param typeAspectPropName
     * @param value
     * @param nodeNameToLookFor
     * @param expectedInResults
     * @return true if search results are as expected
     */
    public boolean checkSearchResults(WebDriver driver, String typeAspectPropName, String value, String nodeNameToLookFor, boolean expectedInResults)
    {
        FacetedSearchPage resultPage = search(driver, typeAspectPropName + ":" + value).render();
        if (resultPage.hasResults())
        {
            return expectedInResults == resultPage.isItemPresentInResultsList(SitePageType.DOCUMENT_LIBRARY, nodeNameToLookFor);
        }
        else
        {
            return expectedInResults == false;
        }
    }
    
    /**
     * Util to perform search and retry waiting for solr indexing : check if search results are as expected
     * 
     * @param driver
     * @param typeAspectPropName
     * @param value
     * @param nodeNameToLookFor
     * @param expectedInResults
     * @return true if search results are as expected
     */
    public boolean checkSearchResultsWithRetry(WebDriver driver, String typeAspectPropName, String value, String nodeNameToLookFor, boolean expectedInResults, int retrySearchCount)
    {
        boolean resultOk = false;
        
        for (int searchCount = 1; searchCount < retrySearchCount; searchCount++)
        {
        	resultOk = checkSearchResults(driver, typeAspectPropName, value, nodeNameToLookFor, expectedInResults);
        	
        	// ResultOk?
        	if (resultOk)
        	{
        		return resultOk;
        	}
            else
            {
            	// Retry: Wait for Solr Indexing
            	logger.info("Waiting for the solr indexing to catchup for Node: " + nodeNameToLookFor);
            	webDriverWait(driver, 20000);
            }
         }
        return checkSearchResults(driver, typeAspectPropName, value, nodeNameToLookFor, expectedInResults);
    }
}