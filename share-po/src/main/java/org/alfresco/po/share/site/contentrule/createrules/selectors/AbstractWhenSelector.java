package org.alfresco.po.share.site.contentrule.createrules.selectors;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * User: aliaksei.bul Date: 08.07.13 Time: 12:12
 */
public abstract class AbstractWhenSelector
{
    private WebDrone drone;

    protected AbstractWhenSelector(WebDrone drone)
    {
        this.drone = drone;
    }

    private static final By WHEN_OPTIONS_SELECT = By.cssSelector("ul[id$=ruleConfigType-configs] select[class$='config-name']");

    protected void selectWhenOption(int whenOptionNumber)
    {
        List<WebElement> whenOptions = drone.findAndWaitForElements(WHEN_OPTIONS_SELECT);
        List<Select> whenSelects = new ArrayList<Select>();
        for (WebElement whenOption : whenOptions)
        {
            whenSelects.add(new Select(whenOption));
        }
        whenSelects.get(whenSelects.size() - 1).selectByIndex(whenOptionNumber);
    }
}
