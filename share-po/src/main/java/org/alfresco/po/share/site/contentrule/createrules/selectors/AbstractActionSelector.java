package org.alfresco.po.share.site.contentrule.createrules.selectors;

import org.alfresco.po.share.site.contentrule.createrules.SetPropertyValuePage;
import org.alfresco.po.share.site.document.CopyOrMoveContentRulesPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * User: aliaksei.bul Date: 08.07.13 Time: 12:12
 */
public abstract class AbstractActionSelector
{
    private WebDrone drone;

    private static final By ACTION_OPTIONS_SELECT = By.cssSelector("ul[id$=ruleConfigAction-configs]>li select[class$='config-name']");
    private static final By SELECT_DESTINATION_BUTTON = By.cssSelector("span[class*='destination-dialog-button'] button");
    private static final By MIMETYPE_SELECT = By.cssSelector("select[title='Mimetype']");
    private static final By RUN_SCRIPTS_SELECT = By.xpath("//select[@class='suppress-validation']");
    private static final By SET_PROPERTY_VALUE_SELECT = By.cssSelector("span[class*='set-property-value'] button");


    protected AbstractActionSelector(WebDrone drone)
    {
        this.drone = drone;
    }

    protected void selectAction(int actionOptionNumber)
    {
        List<WebElement> actionOptions = drone.findAndWaitForElements(ACTION_OPTIONS_SELECT);
        List<Select> actionSelects = new ArrayList<Select>();
        for (WebElement actionOption : actionOptions)
        {
            actionSelects.add(new Select(actionOption));
        }
        actionSelects.get(actionSelects.size() - 1).selectByIndex(actionOptionNumber);
    }

    protected CopyOrMoveContentRulesPage selectDestination(final String site, final String... folders)
    {
        List<WebElement> selectDestButtons = drone.findAndWaitForElements(SELECT_DESTINATION_BUTTON);
        selectDestButtons.get(selectDestButtons.size() - 1).click();
        CopyOrMoveContentRulesPage copyOrMoveContentPage = new CopyOrMoveContentRulesPage(drone);
        copyOrMoveContentPage.selectSite(site).render();
        copyOrMoveContentPage.selectPath(folders).render();
        return copyOrMoveContentPage;
    }

    protected void selectTransformContent(String visibleText)
    {
        List<WebElement> mimeTypes = drone.findAndWaitForElements(MIMETYPE_SELECT);
        List<Select> mimeTypesSelects = new ArrayList<Select>();
        for (WebElement mimeTypeElement : mimeTypes)
        {
            mimeTypesSelects.add(new Select(mimeTypeElement));
        }
        mimeTypesSelects.get(mimeTypesSelects.size() - 1).selectByVisibleText(visibleText);
    }

    protected void selectScript(String visibleName)
    {
        List<WebElement> scriptOptions = drone.findAndWaitForElements(RUN_SCRIPTS_SELECT);
        List<Select> scriptSelects = new ArrayList<Select>();
        for (WebElement scriptOption : scriptOptions)
        {
            scriptSelects.add(new Select(scriptOption));
        }
        scriptSelects.get(scriptSelects.size() - 1).selectByVisibleText(visibleName);
    }

    protected CopyOrMoveContentRulesPage selectDestinationName(final String destinationName, final String... folders)
    {
        List<WebElement> selectDestButtons = drone.findAndWaitForElements(SELECT_DESTINATION_BUTTON);
        selectDestButtons.get(selectDestButtons.size() - 1).click();
        CopyOrMoveContentRulesPage copyOrMoveContentPage = new CopyOrMoveContentRulesPage(drone);
        copyOrMoveContentPage.selectDestination(destinationName).render();
        copyOrMoveContentPage.selectPath(folders).render();
        return copyOrMoveContentPage;
    }

    protected WebDrone getDrone()
    {
        return drone;
    }

    protected SetPropertyValuePage selectPropertyValue(final String folderName, final String value)
    {
        List<WebElement> selectSetPropertyValueButtons = drone.findAndWaitForElements(SET_PROPERTY_VALUE_SELECT);
        selectSetPropertyValueButtons.get(selectSetPropertyValueButtons.size() - 1).click();
        SetPropertyValuePage setValuePage = new SetPropertyValuePage(drone);
        setValuePage.selectPropertyTypeFolder(folderName);
        setValuePage.selectValueFromList(value);
        return new SetPropertyValuePage (drone);
    }

}
