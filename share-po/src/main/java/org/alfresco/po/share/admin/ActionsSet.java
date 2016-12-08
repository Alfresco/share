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
package org.alfresco.po.share.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Representation of an Action Set
 * 
 * @author Richard Smith
 */
public class ActionsSet extends PageElement
{
    /** Constants */
    private static final By CONTROL_ELEMENT = By.cssSelector("div[aria-label='Actions ']");
    private static final By MENU_ROWS = By.cssSelector("tr.dijitMenuItem");
    private static final By MENU_LABEL = By.cssSelector("td.dijitMenuItemLabel");
    private static final By DIALOG = By.cssSelector("div.alfresco-dialog-AlfDialog");
    private static final By DIALOG_BUTTONS = By.cssSelector("span.dijitButtonNode");
    protected String nodeRef;
    protected String rowElementXPath = null;

    protected WebElement control;
    /**
     * Instantiates a new actions set.
     * 
     * @param driver the web driver
     * @param element the element
     */
    public ActionsSet(WebDriver driver, FactoryPage factoryPage)
    {
        this.driver = driver;
        this.factoryPage = factoryPage;
    }
    /**
     * Instantiates a new actions set.
     * 
     * @param driver the web driver
     * @param element the element
     */
    public ActionsSet(WebDriver driver, WebElement element, FactoryPage factoryPage)
    {
        this.driver = driver;
        this.factoryPage = factoryPage;
        this.control = element.findElement(CONTROL_ELEMENT);
        // The dropdown menu has the same id as the control element with '_dropdown' appended
    }
    /**
     * Checks if the menu contains a named action
     * 
     * @param actionName the action name
     * @return true, if successful
     */
    public boolean hasActionByName(String actionName)
    {
        // Iterate over the menuRows and return true if we find an item that matches the named action
        for (WebElement menuRow : getMenuRows())
        {
            if (actionName.equalsIgnoreCase(StringUtils.trim(menuRow.findElement(MENU_LABEL).getText())))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Click action by name.
     * 
     * @param actionName the action name
     */
    public HtmlPage clickActionByName(String actionName)
    {
        String availableActions = "";
        
        // Iterate over the menuRows and click the control that matches the named action
        for (WebElement menuRow : getMenuRows())
        {
        	WebElement actionMenu = menuRow.findElement(MENU_LABEL);
        	availableActions = availableActions + actionMenu.getText() + " "; 
  
            if (actionName.equalsIgnoreCase(StringUtils.trim(actionMenu.getText())))
            {
            	actionMenu.click();
                return getCurrentPage();
            }
        }
        
        throw new PageException("Action can not be found in the dropdown, " + actionName + " Actions Found: " + availableActions);
        
    }

    /**
     * Click and dialog action by name.
     * 
     * @param actionName the action name
     * @param dialogButtonName the name of the dialog button to click
     */
    public HtmlPage clickActionByNameAndDialogByButtonName(String actionName, String dialogButtonName)
    {
        // Click the action
        clickActionByName(actionName);
        // Find the dialog
        WebElement dialog = this.driver.findElement(DIALOG);
        if (PageUtils.usableElement(dialog))
        {
            // Within the dialog find the buttons
            List<WebElement> dialogButtons = dialog.findElements(DIALOG_BUTTONS);
            // Find the dialog
            // Iterate over the dialogButtons and click the button that matches the named dialog button name
            for (WebElement button : dialogButtons)
            {
                if (dialogButtonName.equalsIgnoreCase(StringUtils.trim(button.getText())))
                {
                    button.click();
                    break;
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Opens and returns the menu row WebElements.
     * 
     * @return the menu rows
     */
    private List<WebElement> getMenuRows()
    {
        // Click the control to open the menu
        //Regain focus
        driver.findElement(By.tagName("body")).click();
        mouseOver(control);
        control.click();
        // Compose the selector for the drop down menu
        String dropdown = String.format("div[aria-labelledby^= '%s']",control.getAttribute("id"));
        WebElement drop = driver.findElement(By.cssSelector(dropdown ));

        // Find the menu
        WebElement menu = drop.findElement(By.cssSelector("div.alf-menu-group-items"));
        // If the menu is not null and is displayed and is enabled
        if (PageUtils.usableElement(menu))
        {
            // Within the menu element find the MENU_ROWS
            List<WebElement> actions = menu.findElements(MENU_ROWS);
            if (actions.size() > 0)
            {
                return actions;
            }
            else
            {
                return getMenuRows();
            }
        }

        return new ArrayList<WebElement>();
    }
}
