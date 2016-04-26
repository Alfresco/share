package org.alfresco.po.share.site.contentrule.createrules.selectors;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Created by sergey.kardash on 2/26/14.
 */
public abstract class AbstractIfErrorSelector extends PageElement
{
    private static final By IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT = By.xpath("//div[@class='form-field scriptRef']/select[contains(@id,'default-scriptRef')]");

    
    protected void selectScript(String visibleName)
    {
        List<WebElement> scriptOptions = findAndWaitForElements(IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT);
        List<Select> scriptSelects = new ArrayList<Select>();
        for (WebElement scriptOption : scriptOptions)
        {
            scriptSelects.add(new Select(scriptOption));
        }
        scriptSelects.get(scriptSelects.size() - 1).selectByVisibleText(visibleName);
    }
}
