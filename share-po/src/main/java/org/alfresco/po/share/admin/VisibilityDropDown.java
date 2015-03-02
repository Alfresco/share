package org.alfresco.po.share.admin;

import java.util.List;

import org.alfresco.po.share.enums.SiteVisibility;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Representation of a Visibility Menu
 * 
 * @author Richard Smith
 */
public class VisibilityDropDown
{

    /** Constants */
    private static final By CONTROL_ELEMENT = By.cssSelector("table.dijitSelect");
    private static final String MENU_ELEMENT_SUFFIX = "_dropdown";
    private static final String MENU_ELEMENT_SELECTOR_TEMPLATE = "div#?";
    private static final By MENU_ROWS = By.cssSelector("tr.dijitMenuItem");
    private static final By MENU_LABEL = By.cssSelector("td.dijitMenuItemLabel");

    private WebDrone drone;
    private WebElement control;
    private String menuId;
    private SiteVisibility value;

    /**
     * Instantiates a new visibility drop down.
     * 
     * @param webDrone the web drone
     * @param element the element
     */
    public VisibilityDropDown(WebDrone drone, WebElement element)
    {
        this.drone = drone;
        this.value = SiteVisibility.getEnum(StringUtils.trim(element.getText()));
        this.control = element.findElement(CONTROL_ELEMENT);
        // The dropdown menu has the same id as the control element with '_dropdown' appended
        this.menuId = this.control.getAttribute("id") + MENU_ELEMENT_SUFFIX;
    }

    /**
     * Get the current value of the visibility drop down.
     * 
     * @return the value
     */
    public SiteVisibility getValue()
    {
        return value;
    }

    /**
     * Select a new visibility drop down value.
     * 
     * @param visibility the visibility
     */
    public void selectValue(SiteVisibility visibility)
    {
        // Click the control to open the menu
        control.click();

        // Compose the selector for the drop down menu
        String menuSelector = StringUtils.replace(MENU_ELEMENT_SELECTOR_TEMPLATE, "?", this.menuId);

        // Find the menu
        WebElement menu = this.drone.find(By.cssSelector(menuSelector));

        // If the menu is not null and is displayed and is enabled
        if (PageUtils.usableElement(menu))
        {

            // Within the menu element find the MENU_ROWS
            List<WebElement> menuRows = menu.findElements(MENU_ROWS);

            // Iterate over the menuRows and click the item that matches the required visibility
            for (WebElement menuRow : menuRows)
            {
                if (visibility.getDisplayValue().equals(StringUtils.trim(menuRow.findElement(MENU_LABEL).getText())))
                {
                    menuRow.click();
                    this.value = visibility;
                    break;
                }
            }
        }
    }

    /**
     * Is the provided value the current value of the drop down?
     * 
     * @return true, if the value is selected
     */
    public boolean isValueSelected(SiteVisibility visibility)
    {
        return this.value == visibility;
    }
}