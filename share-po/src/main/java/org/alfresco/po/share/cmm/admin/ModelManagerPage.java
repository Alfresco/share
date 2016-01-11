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

package org.alfresco.po.share.cmm.admin;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.ActionsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * PageObject Class for CustomModelManagerPage with CMM option
 * 
 * @author Meenal Bhave
 */

public class ModelManagerPage extends SharePage
{

    private static final Log LOGGER = LogFactory.getLog(ModelManagerPage.class);
    private static final By BUTTON_CREATE_NEW_MODEL = By.cssSelector(".createButton>span");
    private static final By BUTTON_IMPORT_MODEL = By.cssSelector(".alfresco-buttons-AlfButton.importButton>span");
    private static final By CMM_MODEL_LIST = By.id("MODELS_LIST");
    private static final By MODEL_ROWS = By.cssSelector("tr.alfresco-lists-views-layouts-Row");
    private static final By MODEL_ROW_NAME_ELEMENT = By.cssSelector("td.alfresco-lists-views-layouts-Cell.nameColumn .inner");
    private static final By MODEL_ROW_NAMESPACE = By.cssSelector("td.alfresco-lists-views-layouts-Cell.namespaceColumn");
    private static final By MODEL_ROW_STATUS = By.cssSelector("td.alfresco-lists-views-layouts-Cell.statusColumn");
    private static final By MODEL_ROW_ACTIONS = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actionsColumn");
    private static final By CMM_MODEL_NO_LISTING = By.cssSelector("div.alfresco-lists-views-layouts-AlfListView > div");

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdriver.Render#render(org.alfresco.webdriver.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ModelManagerPage render()
    {
        RenderTime renderTime = new RenderTime(maxPageLoadingTime);
        // Button
        elementRender(renderTime,
        			  getVisibleRenderElement(BUTTON_CREATE_NEW_MODEL),
        			  getVisibleRenderElement(BUTTON_IMPORT_MODEL));

        return this;
    }


    /**
     * Gets the custom model row.
     * 
     * @param modelName the name
     * @return the custom model row
     */
    public ModelRow getCustomModelRowByName(String modelName)
    {
        if (modelName == null || modelName.isEmpty())
        {
            throw new IllegalArgumentException("Model Name is required");
        }

        for (ModelRow row : getCMRows())

        {
            if (row.getCMName().equalsIgnoreCase(modelName))
            {
                return row;
            }
        }

        throw new PageException(String.format("Model name %s was not found", modelName));
    }

    /**
     * Gets the custom model row based on the ModelName (Uses xpath).
     * 
     * @param modelName the name
     * @return the custom model row
     */
    public ModelRow getCustomModelRowFor(String modelName)
    {
        if (modelName == null || modelName.isEmpty())
        {
            throw new IllegalArgumentException("Model Name is required");
        }

        String xpathExpression = "//span[@class='value' and text()='" + modelName + "']//..//..//..//..";

        try
        {
            WebElement modelRow = driver.findElement(By.xpath(xpathExpression));
            ModelRow customModelRow = new ModelRow();
            customModelRow.setCmName(modelRow);
            customModelRow.setCmName(modelRow.findElement(MODEL_ROW_NAME_ELEMENT));
            customModelRow.setCMNamespace(modelRow.findElement(MODEL_ROW_NAMESPACE).getText());
            customModelRow.setCmStatus(modelRow.findElement(MODEL_ROW_STATUS).getText());
            customModelRow.setCmActions(new ActionsSet(driver, modelRow.findElement(MODEL_ROW_ACTIONS), factoryPage));

            return customModelRow;
        }
        catch (Exception e)
        {
            throw new PageException(String.format("Model name %s was not found", modelName), e);
        }
    }

    /**
     * Verify custom model row by name is displayed
     * 
     * @param name the name
     * @return boolean
     */
    public boolean isCustomModelRowDisplayed(String name)
    {
        try
        {
            if ((getCustomModelRowByName(name) != null))
            {
                return true;
            }
        }
        catch (PageException pe)
        {
            LOGGER.info("Model Row not displayed: ", pe);
            return false;
        }
        return false;
    }

    /**
     * Click custom model row by name.
     * 
     * @param name the name
     * @return the custom model row
     */

    public HtmlPage selectCustomModelRowByName(String name)
    {
        try
        {
            ModelRow thisRow = getCustomModelRowByName(name);
            thisRow.getCmNameElement().click();
            return factoryPage.getPage(driver);
        }
        catch (PageException pe)
        {
            throw new PageOperationException("Model Not found", pe);
        }
    }

    /**
     * Method to get the List of Custom Models.
     * 
     * @return List<{@link ModelRow}>
     */
    public List<ModelRow> getCMRows()
    {
        // Initialise the Models List
        List<WebElement> modelRows = Collections.emptyList();

        // Find List
        WebElement modelsList = driver.findElement(CMM_MODEL_LIST);

        // if empty: CMM_MODEL_NO_LISTING is found
        if (!modelsList.findElements(CMM_MODEL_NO_LISTING).isEmpty())
        {
            // Models List Empty;
            return Collections.emptyList();
        }
        else
        {
            modelRows = modelsList.findElements(MODEL_ROWS);
            List<ModelRow> customModelRows = new ArrayList<ModelRow>();
            for (WebElement modelRow : modelRows)
            {
                ModelRow customModelRow = new ModelRow();
                customModelRow.setCmName(modelRow.findElement(MODEL_ROW_NAME_ELEMENT));
                customModelRow.setCMNamespace(modelRow.findElement(MODEL_ROW_NAMESPACE).getText());
                customModelRow.setCmStatus(modelRow.findElement(MODEL_ROW_STATUS).getText());
                customModelRow.setCmActions(new ActionsSet(driver, modelRow.findElement(MODEL_ROW_ACTIONS), factoryPage));
                customModelRows.add(customModelRow);
            }
            return customModelRows;
        }
    }

    /**
     * Method to get the Count of Custom Models.
     * 
     * @return int count of Models
     */
    public int getCMCount()
    {
        if (getCMRows().isEmpty())
        {
            return 0;
        }
        return getCMRows().size();
    }

    /**
     * Click Create New Model Button
     * 
     * @return CreateNewModelPopUp
     */
    public HtmlPage clickCreateNewModelButton()
    {
        WebElement createButton = findFirstDisplayedElement(BUTTON_CREATE_NEW_MODEL);
        createButton.click();
        return factoryPage.getPage(driver);
    }

    /**
     * Click Import Model Button
     * 
     * @return ImportModelPopUpPage
     */
    public HtmlPage clickImportModelButton()
    {
        try
        {
            WebElement importButton = findFirstDisplayedElement(BUTTON_IMPORT_MODEL);
            importButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Not visible Element: Import Custom Model");
    }

}
