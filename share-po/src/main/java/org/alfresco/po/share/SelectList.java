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
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.share.util.PageUtils;
import org.openqa.selenium.WebDriver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Representation of a Select List
 * 
 * @author Richard Smith
 */
public class SelectList
{
    private static final Log LOGGER = LogFactory.getLog(SelectList.class);

    /** Constants */
    private static final By CONTROL_ELEMENT = By.cssSelector("table.dijitSelect");

    private static final String MENU_ELEMENT_SUFFIX = "_dropdown";

    private static final String MENU_ELEMENT_SELECTOR_TEMPLATE = "div#?";

    private static final By MENU_ROWS = By.cssSelector("tr.dijitMenuItem");

    private static final By MENU_LABEL = By.cssSelector("td.dijitMenuItemLabel");

    private static final By SELECT_LABEL = By.cssSelector("span.dijitSelectLabel");

    private WebDriver driver;

    private WebElement element;

    private WebElement control;

    private String menuId;

    private String value;

    /**
     * Instantiates a new select list.
     * 
     * @param webDrone the web driver
     * @param element the element
     */
    public SelectList(WebDriver driver, WebElement element)
    {
        this.driver = driver;
        this.element = element;
        this.value = StringUtils.trim(element.getText());
        this.control = element.findElement(CONTROL_ELEMENT);
        // The dropdown menu has the same id as the control element with '_dropdown' appended
        this.menuId = this.control.getAttribute("id") + MENU_ELEMENT_SUFFIX;
    }

    /**
     * Get the current visible value of the select list.
     * 
     * @return the value
     */
    public String getValue()
    {
        WebElement selectLabel = this.element.findElement(SELECT_LABEL);
        return selectLabel.getText();
    }

    /**
     * Select a new select list down value.
     * 
     * @param value the value
     * @param selectExactMatch true if list item with exact matching text is required
     * @return true if the specified value is selected, else false
     */
    public boolean selectValue(String value, boolean selectExactMatch)
    {
        // Click the control to open the menu
        control.click();

        // Compose the selector for the drop down menu
        String menuSelector = StringUtils.replace(MENU_ELEMENT_SELECTOR_TEMPLATE, "?", this.menuId);

        // Find the menu
        WebElement menu = this.driver.findElement(By.cssSelector(menuSelector));

        // If the menu is not null and is displayed and is enabled
        if (PageUtils.usableElement(menu))
        {

            // Within the menu element find the MENU_ROWS
            List<WebElement> menuRows = menu.findElements(MENU_ROWS);

            // Iterate over the menuRows and click the item that matches the required visibility
            for (WebElement menuRow : menuRows)
            {
                String listItem = StringUtils.trim(menuRow.findElement(MENU_LABEL).getText());
                LOGGER.debug("List Item Value: " + listItem);
                if (value.equals(listItem))
                {
                    menuRow.click();
                    this.value = value;
                    return true;
                }

                if (!selectExactMatch)
                {
                    if (listItem != null)
                    {
                        if (listItem.startsWith(value))
                        {
                            menuRow.click();
                            this.value = value;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Is the provided value the current value of the select list?
     * 
     * @return true, if the value is selected
     */
    public boolean isValueSelected(String value)
    {
        return this.value.equals(value);
    }

    /**
     * Is the select list enabled?
     * 
     * @return true, if the value is selected
     */
    public boolean isEnabled()
    {
        try
        {
            if ("true".equals(this.control.getAttribute("aria-disabled")))
            {
                return false;
            }
        }
        catch (Exception e)
        {
            LOGGER.info("List control is disabled", e);
        }
        return true;
    }
}
