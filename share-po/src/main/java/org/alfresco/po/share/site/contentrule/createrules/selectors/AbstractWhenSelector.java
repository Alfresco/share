package org.alfresco.po.share.site.contentrule.createrules.selectors;

import org.alfresco.po.PageElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * User: aliaksei.bul Date: 08.07.13 Time: 12:12
 */
public abstract class AbstractWhenSelector extends PageElement
{
    @FindBy(css="ul[id$=ruleConfigType-configs] select[class$='config-name']") WebElement whenDropdown;

    protected void selectWhenOption(int whenOptionNumber)
    {
        Select selector = new Select(whenDropdown); 
        selector.selectByIndex(whenOptionNumber);
    }
}
