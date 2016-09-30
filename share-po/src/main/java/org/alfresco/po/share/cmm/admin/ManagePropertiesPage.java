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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * PageObject Class for ManagePropertiesPage
 * This is the page seen after a particular type or aspect is selected.
 * 
 * @author Richard Smith
 */
public class ManagePropertiesPage extends SharePage
{
    /** The logger */
    private static final Log LOGGER = LogFactory.getLog(ManagePropertiesPage.class);

    /** The selectors */
    private static final By CMM_PROPERTIES_LIST = By.id("PROPERTIES_LIST");

    private static final By CMM_PROP_ROWS = By.cssSelector("tr.alfresco-lists-views-layouts-Row");

    private static final By CMM_PROP_ROW_NAME = By.cssSelector("td.nameColumn");

    private static final By CMM_PROP_ROW_DISPLAYLABEL = By.cssSelector("td.displayLabelColumn");

    private static final By CMM_PROP_ROW_DATATYPE = By.cssSelector("td.datatypeColumn");

    private static final By CMM_PROP_ROW_MANDATORY = By.cssSelector("td.mandatoryColumn");

    private static final By CMM_PROP_ROW_DEFAULT = By.cssSelector("td.defaultvalueColumn");

    private static final By CMM_PROP_ROW_MULTIVALUE = By.cssSelector("td.multivalueColumn");

    private static final By CMM_PROP_ROW_ACTIONS = By.cssSelector("td.actionsColumn");

    private static final By BUTTON_BACK_TO_TYPES_PROPERTYGROUPS = By.cssSelector(".alfresco-buttons-AlfButton.backToTypesPropertyGroups > span");

    private static final By BUTTON_CREATE_PROPERTY = By.cssSelector(".alfresco-buttons-AlfButton.createPropertyButton > span");

    private static final By TITLE_TYPE_ASPECT_NAME = By.cssSelector("div.alfresco-html-Heading>h1");

    private List<PropertyRow> propertyRows;
    
    private static final By LOADING_IN_PROGRESS = By.cssSelector(".data-loading-more");

    @SuppressWarnings("unchecked")
	public ManagePropertiesPage render()
    {
    	RenderTime renderTime = new RenderTime(maxPageLoadingTime);
        elementRender(renderTime, getVisibleRenderElement(BUTTON_BACK_TO_TYPES_PROPERTYGROUPS), getVisibleRenderElement(BUTTON_CREATE_PROPERTY));
        elementRender(renderTime, getVisibleRenderElement(CMM_PROPERTIES_LIST),
        new RenderElement(LOADING_IN_PROGRESS, ElementState.INVISIBLE));
        loadElements();
        return this;
    }

    /**
     * Initialises the elements that make up a ManagePropertiesPage.
     */
    public void loadElements()
    {
        // Initialise the Types List
        WebElement propertiesList = driver.findElement(CMM_PROPERTIES_LIST);
        List<WebElement> propertyRows = propertiesList.findElements(CMM_PROP_ROWS);
        this.propertyRows = new ArrayList<PropertyRow>();
        for (WebElement row : propertyRows)
        {
            PropertyRow propertyRow = new PropertyRow();
            propertyRow.setNameElement(row.findElement(CMM_PROP_ROW_NAME));
            propertyRow.setDisplayLabel(row.findElement(CMM_PROP_ROW_DISPLAYLABEL).getText());
            propertyRow.setDatatype(row.findElement(CMM_PROP_ROW_DATATYPE).getText());
            propertyRow.setMandatory(row.findElement(CMM_PROP_ROW_MANDATORY).getText());
            propertyRow.setDefaultValue(row.findElement(CMM_PROP_ROW_DEFAULT).getText());
            propertyRow.setMultiValue(row.findElement(CMM_PROP_ROW_MULTIVALUE).getText());
            propertyRow.setActions(new ActionsSet(driver, row.findElement(CMM_PROP_ROW_ACTIONS), factoryPage));
            this.propertyRows.add(propertyRow);
        }
    }

    /**
     * Gets the property rows.
     * 
     * @return the property rows
     */
    public List<PropertyRow> getPropertyRows()
    {
        return propertyRows;
    }

    /**
     * Gets the property row by name.
     * 
     * @param name the name
     * @return the property row by name
     */
    public PropertyRow getPropertyRowByName(String name)
    {
        PageUtils.checkMandatoryParam(name, "Name can not be Null or Empty");

        for (PropertyRow row : propertyRows)
        {
            if (name.equals(row.getName().trim()))
            {
                return row;
            }
        }
        throw new PageException(String.format("Name %s was not found", name));
    }

    /**
     * Checks if property row is displayed.
     * 
     * @param name the name
     * @return true, if is property row displayed
     */
    public boolean isPropertyRowDisplayed(String name)
    {
        try
        {
            String query = String.format("//div[@id='PROPERTIES_LIST']//tr//span[text()='%s']",name);
            return driver.findElement(By.xpath(query)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Select back to types property groups button.
     * 
     * @return ManageTypesAndAspectsPage the manage types and aspects page
     */
    public HtmlPage selectBackToTypesPropertyGroupsButton()
    {
        try
        {
            WebElement backToTypesPropertyGroupsButton = findFirstDisplayedElement(BUTTON_BACK_TO_TYPES_PROPERTYGROUPS);
            backToTypesPropertyGroupsButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Button not visible: BUTTON_BACK_TO_TYPES_PROPERTYGROUPS");
    }

    /**
     * Click Create New Property Button
     * 
     * @return CreateNewPropertyPopUp
     */
    public HtmlPage clickCreateNewPropertyButton()
    {
        try
        {
            WebElement createButton = findFirstDisplayedElement(BUTTON_CREATE_PROPERTY);
            createButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Not visible Element: BUTTON_CREATE_PROPERTY");
    }

    /**
     * Get Title
     * 
     * @return String Type or PropertyGroupName for which properties are being managed
     */
    @Override
    public String getTitle()
    {
        try
        {
            WebElement modelTypeAspectTitle = findFirstDisplayedElement(TITLE_TYPE_ASPECT_NAME);
            return modelTypeAspectTitle.getText();
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the Title: ", e);
        }
        throw new PageOperationException("Unable to read the title");
    }
}
