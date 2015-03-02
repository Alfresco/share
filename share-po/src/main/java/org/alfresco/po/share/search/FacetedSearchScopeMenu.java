package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchScopeMenu.
 */
public class FacetedSearchScopeMenu
{
    /** Constants */
    private static final By FACETED_SEARCH_TOP_MENU_BAR = By.cssSelector("div#FCTSRCH_TOP_MENU_BAR");
    private static final By LABEL_STRING = By.cssSelector("span.alfresco-html-Label");
    private static final By MENU_BUTTON = By.cssSelector("div#FCTSRCH_SCOPE_SELECTION_MENU");
    private static final By MENU_BUTTON_TEXT = By.cssSelector("span#FCTSRCH_SCOPE_SELECTION_MENU_text");
    private static final By MENU_ITEMS = By.cssSelector("div#FCTSRCH_SCOPE_SELECTION_MENU_dropdown tr.dijitMenuItem");

    private WebDrone drone;
    private WebElement labelElement;
    private WebElement menuButton;
    private String currentSelection;
    private List<WebElement> menuElements = new ArrayList<WebElement>();

    /**
     * Instantiates a new faceted search form.
     */
    public FacetedSearchScopeMenu(WebDrone drone)
    {
        this.drone = drone;
        WebElement facetedSearchTopMenuBar = drone.find(FACETED_SEARCH_TOP_MENU_BAR);
        this.labelElement = facetedSearchTopMenuBar.findElement(LABEL_STRING);
        this.menuButton = facetedSearchTopMenuBar.findElement(MENU_BUTTON);
        this.currentSelection = facetedSearchTopMenuBar.findElement(MENU_BUTTON_TEXT).getText();
    }

    /**
     * Gets the menu button.
     *
     * @return the menu button
     */
    public WebElement getMenuButton()
    {
        return menuButton;
    }

    /**
     * Gets the current selection.
     *
     * @return the current selection
     */
    public String getCurrentSelection()
    {
        return currentSelection;
    }

    /**
     * Scope by label.
     *
     * @param label the label to be scoped on
     * @return the html page
     */
    public HtmlPage scopeByLabel(String label)
    {
        openMenu();
        boolean found = false;
        for(WebElement option : this.menuElements)
        {
            if(StringUtils.trim(option.getText()).equalsIgnoreCase(label))
            {
                this.currentSelection = StringUtils.trim(option.getText());
                option.click();
                found = true;
                break;
            }
        }
        if(!found)
        {
            cancelMenu();
        }
        return FactorySharePage.resolvePage(this.drone);
    }
    
    /**
     * Checks for scope label.
     *
     * @param label the label
     * @return true, if successful
     */
    public boolean hasScopeLabel(String label)
    {
        openMenu();
        boolean found = false;
        for(WebElement option : this.menuElements)
        {
            if(StringUtils.trim(option.getText()).equalsIgnoreCase(label))
            {
                found = true;
                break;
            }
        }
        cancelMenu();
        return found;
    }

    /**
     * Open the scope menu.
     */
    private void openMenu()
    {
        this.menuButton.click();
        this.menuElements = this.drone.findAll(MENU_ITEMS);
    }

    /**
     * Cancel an open menu.
     */
    private void cancelMenu()
    {
        this.labelElement.click();
        this.menuElements.clear();
    }

}