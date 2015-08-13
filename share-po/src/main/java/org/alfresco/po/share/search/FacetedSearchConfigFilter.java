/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.search;

import java.util.List;

import org.alfresco.po.PageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchConfigFilter.
 * 
 * @author Richard Smith
 */
public class FacetedSearchConfigFilter extends PageElement
{
    /** Constants. */
    private static final By FILTER_ID = By.cssSelector("td:nth-of-type(2)");
    private static final By FILTER_NAME = By.cssSelector("td:nth-of-type(3)");
    private static final By FILTER_PROPERTY = By.cssSelector("td:nth-of-type(4)");
    private static final By FILTER_TYPE = By.cssSelector("td:nth-of-type(5)");
    private static final By FILTER_SHOW = By.cssSelector("td:nth-of-type(6)");
    private static final By FILTER_DEFAULT = By.cssSelector("td:nth-of-type(7)");
    private static final By FILTER_AVAILABILITY = By.cssSelector("td:nth-of-type(8)");
    private static final By FILTER_DELETE = By.cssSelector("td:nth-of-type(9)");
    private static final By CLICK_DELETE_IMAGE = By.cssSelector("td:nth-of-type(9)>span>img");

    private static final By I_EDIT_CTRL = By.cssSelector("img.editIcon");
    private static final By I_EDIT_INPUT = By.cssSelector("input.dijitInputInner");
    // private static final By I_EDIT_DD_CTRL = By.cssSelector("div.alfresco-forms-controls-DojoSelect table.dijitSelect");
    private static final By I_EDIT_DD_CTRL = By.cssSelector("div.control-row table.dijitSelect");
    private static final String I_EDIT_DD_CTRL_MENU_MOD = "_dropdown";
    private static final By I_EDIT_DD_POPUPMENU_ITEM = By.cssSelector("tr.dijitMenuItem");
    private static final By I_EDIT_SAVE = By.cssSelector("span.action.save");
    private static final By CONFIRM_DELETE = By
            .cssSelector("div[style*='opacity: 1']>div>div>span>span>span[class='dijitReset dijitStretch dijitButtonContents']");
    private static Log logger = LogFactory.getLog(FacetedSearchConfigFilter.class);
    // private static final By I_EDIT_CANCEL = By.cssSelector("span.action.cancel");

    private WebDriver driver;

    private WebElement filterId;
    private String filterId_text;

    private WebElement filterName;
    private String filterName_text;

    private WebElement filterProperty;
    private String filterProperty_text;

    private WebElement filterType;
    private String filterType_text;

    private WebElement filterShow;
    private String filterShow_text;

    private String filterDefault_text;

    private String filterAvailability_text;

    private WebElement filterdelete;
    private String filterdelete_text;

    /**
     * Instantiates a new faceted search result - some items may be null.
     * 
     * @param driver the driver
     * @param filter the filter
     */
    public FacetedSearchConfigFilter(WebDriver driver, WebElement filter)
    {
        if (filter.findElements(FILTER_ID).size() > 0)
        {
            filterId = filter.findElement(FILTER_ID);
            filterId_text = filterId.getText();
        }
        if (filter.findElements(FILTER_NAME).size() > 0)
        {
            filterName = filter.findElement(FILTER_NAME);
            filterName_text = filterName.getText();
        }
        if (filter.findElements(FILTER_PROPERTY).size() > 0)
        {
            filterProperty = filter.findElement(FILTER_PROPERTY);
            filterProperty_text = filterProperty.getText();
        }
        if (filter.findElements(FILTER_TYPE).size() > 0)
        {
            filterType = filter.findElement(FILTER_TYPE);
            filterType_text = filterType.getText();
        }
        if (filter.findElements(FILTER_SHOW).size() > 0)
        {
            filterShow = filter.findElement(FILTER_SHOW);
            filterShow_text = filterShow.getText();
        }
        if (filter.findElements(FILTER_DEFAULT).size() > 0)
        {
            filterDefault_text = filter.findElement(FILTER_DEFAULT).getText();
        }
        if (filter.findElements(FILTER_AVAILABILITY).size() > 0)
        {
            filterAvailability_text = filter.findElement(FILTER_AVAILABILITY).getText();
        }
        if (filter.findElements(FILTER_DELETE).size() > 0)
        {
            filterdelete = filter.findElement(FILTER_DELETE);
            filterdelete_text = filterdelete.getText();
        }
    }

    /**
     * Gets the filter id.
     * 
     * @return the filter id
     */
    public WebElement getFilterId()
    {
        return filterId;
    }

    /**
     * Gets the filter id_text.
     * 
     * @return the filter id_text
     */
    public String getFilterId_text()
    {
        return filterId_text;
    }

    /**
     * Gets the filter name_text.
     * 
     * @return the filter name_text
     */
    public String getFilterName_text()
    {
        return filterName_text;
    }

    /**
     * Edits the filter name.
     * 
     * @param name the name
     */
    public void editFilterName(String name)
    {
        iEdit(this.filterName, name);
    }

    /**
     * Gets the filter property_text.
     * 
     * @return the filter property_text
     */
    public String getFilterProperty_text()
    {
        return filterProperty_text;
    }

    /**
     * Edits the filter property.
     * 
     * @param property the property
     */
    public void editFilterProperty(String property)
    {
        iEditDD(this.filterProperty, property);
    }

    /**
     * Gets the filter type_text.
     * 
     * @return the filter type_text
     */
    public String getFilterType_text()
    {
        return filterType_text;
    }

    /**
     * Edits the filter type.
     * 
     * @param type the type
     */
    public void editFilterType(String type)
    {
        iEditDD(this.filterType, type);
    }

    /**
     * Gets the filter show_text.
     * 
     * @return the filter show_text
     */
    public String getFilterShow_text()
    {
        return filterShow_text;
    }

    /**
     * Gets the filter show_i edit.
     * 
     */
    public void editFilterShow(String show)
    {
        iEditDD(this.filterShow, show);
    }

    public void editFilterNew(String value)
    {

    }

    /**
     * Gets the filter default_text.
     * 
     * @return the filter default_text
     */
    public String getFilterDefault_text()
    {
        return filterDefault_text;
    }

    /**
     * Gets the filter availability_text.
     * 
     * @return the filter availability_text
     */
    public String getFilterAvailability_text()
    {
        return filterAvailability_text;
    }

    /**
     * Gets the filter availability_text.
     * 
     * @return the filter availability_text
     */
    public String getFilterDelete_text()
    {
        return filterdelete_text;
    }

    /**
     * In line edit.
     * 
     * @param control the control
     * @param value the value
     */
    private void iEdit(WebElement control, String value)
    {
        // Click the in line edit control
        control.findElement(I_EDIT_CTRL).click();

        // Type the value in the input field
        control.findElement(I_EDIT_INPUT).sendKeys(value);

        // Click the save button
        control.findElement(I_EDIT_SAVE).click();
    }

    /**
     * In line edit drop down.
     * 
     * @param control the control
     * @param value the value
     */
    private void iEditDD(WebElement control, String value)
    {
        // Click the in line edit control
        control.findElement(I_EDIT_CTRL).click();

        // Find the select control
        WebElement selectControl = control.findElement(I_EDIT_DD_CTRL);

        // Compose the id of the pop up menu
        String popupMenuId = selectControl.getAttribute("id") + I_EDIT_DD_CTRL_MENU_MOD;

        // Click the select control
        selectControl.click();

        // Find the pop up menu
        WebElement popupMenu = driver.findElement(By.id(popupMenuId));

        // Get the pop up menu items
        List<WebElement> menuItems = popupMenu.findElements(I_EDIT_DD_POPUPMENU_ITEM);

        // Iterate pop up menu items
        boolean found = false;
        for (WebElement menuItem : menuItems)
        {
            if (menuItem.getText().equalsIgnoreCase(value))
            {
                menuItem.click();
                found = true;
                break;
            }
        }

        // No item match - re-click the select control
        if (!found)
        {
            selectControl.click();
        }

        // Click the save button
        control.findElement(I_EDIT_SAVE).click();
    }

    /**
     * Gets the clickDelete_Filter.
     */
    public void deleteFilter(boolean selectYes)
    {
        try
        {
            WebElement filterdelete = this.filterdelete.findElement(CLICK_DELETE_IMAGE);
            if (filterdelete.isDisplayed())
            {
                filterdelete.click();
                List<WebElement> buttonNames = driver.findElements(CONFIRM_DELETE);

                String buttonName = "Yes";
                if (!selectYes)
                {
                    buttonName = "No";
                }
                // Iterate list of buttons
                for (WebElement button : buttonNames)
                {
                    if (button.getText().equalsIgnoreCase(buttonName) && (button.isDisplayed()))
                    {
                        button.click();
                        waitUntilVisible(By.cssSelector("div.bd"), "Successfully deleted", 10);
                        waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "Successfully deleted", 10);
                        break;
                    }
                }

            }
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the button");
            }
        }

    }

}
