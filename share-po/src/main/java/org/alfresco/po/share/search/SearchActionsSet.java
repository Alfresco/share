package org.alfresco.po.share.search;

import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.admin.ActionsSet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Representation of an Action Set seen in faceted search result page.
 * 
 * @author Michael Suzuki
 */
public class SearchActionsSet extends ActionsSet
{
    /** Constants */
    private static final By CONTROL_ELEMENT = By.cssSelector("td.actionsCell>div");

    /**
     * Instantiates a new actions set.
     * 
     * @param driver the web driver
     * @param element the element
     */
    public SearchActionsSet(WebDriver driver, WebElement element, FactoryPage factoryPage)
    {
        super(driver,factoryPage);
        this.control = element.findElement(CONTROL_ELEMENT);
        // The dropdown menu has the same id as the control element with '_dropdown' appended
    }
}
