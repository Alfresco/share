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
package org.alfresco.po.share.cmm.admin;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * PageObject Class for ManageTypesAndAspectsPage This is the page seen after a particular model is selected.
 * 
 * @author Richard Smith
 * @author mbhave
 */
public class ManageTypesAndAspectsPage extends SharePage
{
    /** The logger */
    private static final Log LOGGER = LogFactory.getLog(ManageTypesAndAspectsPage.class);

    /** The selectors */
    private static final By CMM_MODEL_NAME = By.cssSelector("div.alfresco-html-Heading>h1");
    private static final By CMM_TYPES_LIST = By.id("TYPES_LIST");
    private static final By CMM_PROPERTY_GROUPS_LIST = By.id("PROPERTY_GROUPS_LIST");
    private static final By CMM_ROWS = By.cssSelector("tr.alfresco-lists-views-layouts-Row");
    private static final By CMM_ROW_NAME_ELEMENT = By.cssSelector("td.nameColumn .inner");
    private static final By CMM_ROW_DISPLAY_LABEL = By.cssSelector("td.displayLabelColumn");
    private static final By CMM_ROW_PARENT = By.cssSelector("td.parentColumn");
    private static final By CMM_FORM_LAYOUT = By.cssSelector("td.layoutColumn .inner");

    private static final By CMM_ROW_ACTIONS = By.cssSelector("td.actionsColumn");
    private static final By BUTTON_BACK_TO_MODELS = By.cssSelector(".alfresco-buttons-AlfButton.backToModels > span");
    private static final By BUTTON_CREATE_TYPE = By.cssSelector(".alfresco-buttons-AlfButton.createTypeButton > span");
    private static final By BUTTON_CREATE_PROPERTY_GROUP = By.cssSelector(".alfresco-buttons-AlfButton.createPropertyGroupButton > span");
    private static final By CMM_ERROR_DIALOG = By.cssSelector("span.alfresco-notifications-AlfNotification__message");
    private static final By LOADING_IN_PROGRESS = By.cssSelector(".data-loading-more");


    @SuppressWarnings("unchecked")
    public ManageTypesAndAspectsPage render()
    {
        RenderTime renderTime = new RenderTime(maxPageLoadingTime);
        elementRender(
                renderTime,
                new RenderElement(CMM_ERROR_DIALOG,ElementState.INVISIBLE),
                new RenderElement(LOADING_IN_PROGRESS, ElementState.INVISIBLE),
                getVisibleRenderElement(BUTTON_BACK_TO_MODELS),
                getVisibleRenderElement(BUTTON_CREATE_TYPE),
                getVisibleRenderElement(BUTTON_CREATE_PROPERTY_GROUP),
                getVisibleRenderElement(CMM_PROPERTY_GROUPS_LIST), 
                getVisibleRenderElement(CMM_TYPES_LIST));
        return this;
    }

    /**
     * Gets the custom model type rows.
     * 
     * @return the custom model type rows
     */
    public List<ModelTypeRow> getCustomModelTypeRows()
    {
        WebElement typesList = driver.findElement(CMM_TYPES_LIST);
        List<WebElement> typeRows = typesList.findElements(CMM_ROWS);
        List<ModelTypeRow> customModelTypeRows = new ArrayList<ModelTypeRow>();
        for (WebElement typeRow : typeRows)
        {
            ModelTypeRow customModelTypeRow = new ModelTypeRow();
            customModelTypeRow.setNameElement(typeRow.findElement(CMM_ROW_NAME_ELEMENT));
            customModelTypeRow.setDisplayLabel(typeRow.findElement(CMM_ROW_DISPLAY_LABEL).getText());
            customModelTypeRow.setParent(typeRow.findElement(CMM_ROW_PARENT).getText());
            customModelTypeRow.setLayout(typeRow.findElement(CMM_FORM_LAYOUT).getText());
            customModelTypeRow.setActions(new ActionsSet(driver, typeRow.findElement(CMM_ROW_ACTIONS),factoryPage));
            customModelTypeRows.add(customModelTypeRow);
        }
        return customModelTypeRows;
    }

    /**
     * Gets the custom model type row by name.
     * 
     * @param typeName the type name
     * @return the custom model type row by name
     */
    public ModelTypeRow getCustomModelTypeRowByName(String typeName)
    {
        PageUtils.checkMandatoryParam(typeName, "Type Name can not be Null or Empty");

        for (ModelTypeRow row : getCustomModelTypeRows())
        {
            if (typeName.equals(row.getName().trim()))
            {
                return row;
            }
        }
        throw new PageException(String.format("Type name %s was not found", typeName));
    }

    /**
     * Gets the custom model property group rows.
     * 
     * @return the custom model property group rows
     */
    public List<ModelPropertyGroupRow> getCustomModelPropertyGroupRows()
    {
        // Initialise the Property Groups List
        WebElement propertyGroupsList = driver.findElement(CMM_PROPERTY_GROUPS_LIST);
        List<WebElement> propertyGroupRows = propertyGroupsList.findElements(CMM_ROWS);
        List<ModelPropertyGroupRow> customModelPropertyGroupRows = new ArrayList<ModelPropertyGroupRow>();
        for (WebElement propertyGroupRow : propertyGroupRows)
        {
            ModelPropertyGroupRow customModelPropertyGroupRow = new ModelPropertyGroupRow();
            customModelPropertyGroupRow.setNameElement(propertyGroupRow.findElement(CMM_ROW_NAME_ELEMENT));
            customModelPropertyGroupRow.setDisplayLabel(propertyGroupRow.findElement(CMM_ROW_DISPLAY_LABEL).getText());
            customModelPropertyGroupRow.setParent(propertyGroupRow.findElement(CMM_ROW_PARENT).getText());
            customModelPropertyGroupRow.setLayout(propertyGroupRow.findElement(CMM_FORM_LAYOUT).getText());
            customModelPropertyGroupRow.setActions(new ActionsSet(driver, propertyGroupRow.findElement(CMM_ROW_ACTIONS), factoryPage));
            customModelPropertyGroupRows.add(customModelPropertyGroupRow);
        }
        return customModelPropertyGroupRows;
    }

    /**
     * Gets the custom model property group row by name.
     * 
     * @param aspectName the aspect name
     * @return the custom model property group row by name
     */
    public ModelPropertyGroupRow getCustomModelPropertyGroupRowByName(String aspectName)
    {
        PageUtils.checkMandatoryParam(aspectName, "Aspect Name can not be Null or Empty");

        for (ModelPropertyGroupRow row : getCustomModelPropertyGroupRows())
        {
            if (aspectName.equals(row.getName().trim()))
            {
                return row;
            }
        }
        throw new PageOperationException(String.format("Aspect name %s was not found", aspectName));
    }

    /**
     * Verify custom type row by name is displayed
     * 
     * @param name the name
     * @return boolean
     */
    public boolean isCustomTypeRowDisplayed(String name)
    {
        try
        {
            if ((getCustomModelTypeRowByName(name) != null))
            {
                return true;
            }
        }
        catch (PageException pe)
        {
            LOGGER.info("Type Row not displayed: ", pe);
            return false;
        }
        return false;
    }

    /**
     * Verify property Group row by name is displayed
     * 
     * @param name the name
     * @return boolean
     */
    public boolean isPropertyGroupRowDisplayed(String name)
    {
        try
        {
            if ((getCustomModelPropertyGroupRowByName(name) != null))
            {
                return true;
            }
        }
        catch (PageOperationException pe)
        {
            LOGGER.info("Aspect Row not displayed: ", pe);
            return false;
        }
        return false;
    }

    /**
     * Click type row by name.
     * 
     * @param name the name
     * @return ManagePropertiesPage the custom model row
     */
    public HtmlPage selectCustomTypeRowByName(String name)
    {
        try
        {
            ModelTypeRow thisRow = getCustomModelTypeRowByName(name);
            thisRow.getNameElement().click();
            return factoryPage.getPage(driver);
        }
        catch (PageException pe)
        {
            LOGGER.info("Type Row not displayed: ", pe);
            throw new PageOperationException("Type not found", pe);
        }
    }

    /**
     * Click type row by name.
     * 
     * @param name the name
     * @return ManagePropertiesPage the custom model row
     */
    public HtmlPage selectPropertyGroupRowByName(String name)
    {
        try
        {
            ModelPropertyGroupRow thisRow = getCustomModelPropertyGroupRowByName(name);
            thisRow.getNameElement().click();
            return factoryPage.getPage(driver);
        }
        catch (PageOperationException pe)
        {
            throw new PageOperationException("Property group not found", pe);
        }
    }

    public HtmlPage selectBackToModelsButton()
    {
        WebElement backToModelsButton = findFirstDisplayedElement(BUTTON_BACK_TO_MODELS);
        backToModelsButton.click();
        waitUntilElementDisappears(BUTTON_BACK_TO_MODELS, 1);
            // TODO: Change it later to: FactoryShareCMMPage.resolveCMMPage(driver).render();
            // For now: use this as resolve is based on dom not url and it causes issues
        return factoryPage.instantiatePage(driver, ModelManagerPage.class);
    }

    /**
     * Click Create New Type Button
     * 
     * @return CreateNewCustomTypePopUp
     */
    public HtmlPage clickCreateNewCustomTypeButton()
    {
        try
        {
            WebElement createButton = findFirstDisplayedElement(BUTTON_CREATE_TYPE);
            createButton.click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Not visible Element: Create a new type");
    }

    /**
     * Click Create New Property Group Button
     * 
     * @return CreateNewPropertyGroupPopUp
     */
    public HtmlPage clickCreateNewPropertyGroupButton()
    {
        try
        {
            WebElement createButton = findFirstDisplayedElement(BUTTON_CREATE_PROPERTY_GROUP);
            createButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Not visible Element: Create a new property group");
    }

    /**
     * Get Model Name for which Types and Aspects are being displayed
     * 
     * @return String
     */
    public String getModelName()
    {
        try
        {
            WebElement modelTypeAspectTitle = findFirstDisplayedElement(CMM_MODEL_NAME);
            return modelTypeAspectTitle.getText().trim();
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the Title: ", e);
        }
        throw new PageOperationException("Unable to read the title");
    }

    /**
     * Returns true if the ManageTypesAspectsPage is for specified model
     * 
     * @return boolean
     */
    public boolean isForModel(String modelName)
    {
        PageUtils.checkMandatoryParam("Model Name can not be Null or Empty", modelName);
        return modelName.equals(getModelName());
    }
}
