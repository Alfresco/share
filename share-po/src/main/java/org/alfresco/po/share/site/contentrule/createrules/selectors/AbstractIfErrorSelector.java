package org.alfresco.po.share.site.contentrule.createrules.selectors;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey.kardash on 2/26/14.
 */
public abstract class AbstractIfErrorSelector
{
    private WebDrone drone;

    private static final By IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT = By.xpath("//div[@class='form-field scriptRef']/select[contains(@id,'default-scriptRef')]");

    protected AbstractIfErrorSelector(WebDrone drone)
    {
        this.drone = drone;
    }

    protected void selectScript(String visibleName)
    {
        List<WebElement> scriptOptions = drone.findAndWaitForElements(IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT);
        List<Select> scriptSelects = new ArrayList<Select>();
        for (WebElement scriptOption : scriptOptions)
        {
            scriptSelects.add(new Select(scriptOption));
        }
        scriptSelects.get(scriptSelects.size() - 1).selectByVisibleText(visibleName);
    }

    protected WebDrone getDrone()
    {
        return drone;
    }

}