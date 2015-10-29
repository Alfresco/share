/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.contentrule.createrules;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.ActionSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.IfErrorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.IfSelectorEnterpImpl;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.WhenSelectorImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;

/**
 * CreateRulePage page object, holds all element of the HTML page relating to Create Rule Page
 * 
 * @author Aliaksei Boole
 * @since 1.0
 */
public class CreateRulePage extends SitePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    @SuppressWarnings("unused")
    private static boolean isHaveCreatedRules = false;

    // Drop down selects.
    private static final By WHEN_OPTIONS_SELECT = By.cssSelector("ul[id$=ruleConfigType-configs] select[class$='config-name']");
    private static final By IF_OPTIONS_SELECT = By.cssSelector("ul[id$=ruleConfigIfCondition-configs] select[class$='config-name']");
    private static final By ACTION_OPTIONS_SELECT = By.cssSelector("ul[id$=ruleConfigAction-configs]>li select[class$='config-name']");
    @SuppressWarnings("unused")
    private static final By IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT = By.xpath("//div[@class='form-field scriptRef']/select[contains(@id,'default-scriptRef')]");

    // textField
    private static final By NAME_FIELD = By.cssSelector("input[name='title']");
    private static final By DESCRIPTION_FIELD = By.cssSelector("textarea[name='description']");
    private static final By SET_VALUE_FIELD = By.cssSelector("span[class*='paramtype_arca_set-property-value'] input");
    private static final By SET_VALUE_CHECKBOX = By.cssSelector("span[class*='paramtype_arca_set-property-value'] input[type='checkbox']");
    private static final By SET_VALUE_FIELD_DATE = By.cssSelector("span[class*='paramtype_arca_set-property-value'] input[type='text']");

    // CheckBoxes
    private static final By CHECK_BOX_APPLY_TO_SUBFOLDER = By.cssSelector("div[class='form-field applyToChildren'] input[title='Rule applies to subfolders']");
    private static final By CHECK_BOX_RULE_IN_BACKGROUND = By
            .cssSelector("div[class='form-field executeAsynchronously'] input[title='Run rule in background']");

    // Messages
    private static final By BALLOON_TEXT_MESSAGE = By.cssSelector("div[class='balloon'] div[class='text'] div");
    public static final String BALLOON_TEXT_VALUE_NOT_EMPTY = "The value cannot be empty.";

    // Buttons
    private static final By CANCEL_BUTTON = By.cssSelector("span[id*='cancel-button'] button[id*='default-cancel-button']");
    private static final By SAVE_BUTTON = By.cssSelector("span[id*='save-button'] button[id*='default-save-button']");
    private static final By CREATE_AND_CREATE_ANOTHER_BUTTON = By.cssSelector("span[id*='createAnother-button'] button[id*='default-createAnother-button']");

    private static final By CREATED_ALERT = By.xpath(".//*[@id='message']/div/span");

    public enum Block
    {
        IF_BLOCK("ul[id$=ruleConfigIfCondition-configs]"), WHEN_BLOCK("ul[id$=ruleConfigType-configs]"), ACTION_BLOCK("ul[id$=ruleConfigAction-configs]");

        private final By selector;

        Block(String selector)
        {
            this.selector = By.cssSelector(selector);
        }
    }

    public enum AddRemoveAction
    {
        ADD("span[class*='add-config'] button"), REMOVE("span[class*='remove-config'] button");

        private final By actionSelector;

        AddRemoveAction(String actionSelector)
        {
            this.actionSelector = By.cssSelector(actionSelector);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateRulePage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(WHEN_OPTIONS_SELECT), getVisibleRenderElement(IF_OPTIONS_SELECT),
                getVisibleRenderElement(ACTION_OPTIONS_SELECT));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateRulePage render()
    {
        // this page has big render time
        return render(new RenderTime(maxPageLoadingTime + 20000));
    }

    public void addOrRemoveOptionsFieldsToBlock(final Block block, final AddRemoveAction operation)
    {
        WebElement blockElement = findAndWait(block.selector);
        List<WebElement> operationButton = blockElement.findElements(operation.actionSelector);
        operationButton.get(operationButton.size() - 1).click();
    }
    WhenSelectorImpl whenSelectorImpl;
    public WhenSelectorImpl getWhenOptionObj()
    {
        return whenSelectorImpl;
    }

    public IfErrorEnterpImpl getIfErrorObj()
    {
        return new IfErrorEnterpImpl();
    }

    IfSelectorEnterpImpl ifSelectorEnterpImpl;
    public IfSelectorEnterpImpl getIfOptionObj()
    {
        return ifSelectorEnterpImpl;
    }
    ActionSelectorEnterpImpl actionSelectorEnterpImpl;
    public ActionSelectorEnterpImpl getActionOptionsObj()
    {
        return actionSelectorEnterpImpl;
    }

    public void fillNameField(final String text)
    {
        fillField(NAME_FIELD, text);
    }

    public void fillDescriptionField(final String text)
    {
        fillField(DESCRIPTION_FIELD, text);
    }

    private void fillField(By selector, String text)
    {
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    public void fillSetValueField(final String text)
    {
        fillField(SET_VALUE_FIELD, text);
    }

    /**
     * Clicks on the checkbox to set value for boolean 'Set Property Value'
     */
    public void fillSetValueFieldCheckbox()
    {
        WebElement inputField = findAndWait(SET_VALUE_CHECKBOX);
        inputField.click();
        waitUntilAlert(5);
    }

    /**
     * Set date to set value field 'Set Property Value'
     */
    public void fillSetValueFieldDate(final String text)
    {
        fillField(SET_VALUE_FIELD_DATE, text);
        waitUntilAlert(5);
    }

    /**
     * Clicks on the checkbox to apply rule for subfolders
     */
    public void selectApplyToSubfolderCheckbox()
    {
        WebElement applyToSubfolderCheckBox = findAndWait(CHECK_BOX_APPLY_TO_SUBFOLDER);
        applyToSubfolderCheckBox.click();
    }

    public void selectRunRuleInBackgroundCheckbox()
    {
        WebElement runRuleInBackgroundCheckbox = findAndWait(CHECK_BOX_RULE_IN_BACKGROUND);
        runRuleInBackgroundCheckbox.click();
    }

    public String getNameFieldText()
    {
        return getTextFromInput(NAME_FIELD);
    }

    public String getDescriptionFieldText()
    {
        return getTextFromInput(DESCRIPTION_FIELD);
    }

    private String getTextFromInput(By selector)
    {
        return findAndWait(selector).getAttribute("value");
    }

    public HtmlPage clickCancelButton()
    {
        click(CANCEL_BUTTON);
        return getCurrentPage();
    }

    public CreateRulePage clickAnotherCreate()
    {
        click(CREATE_AND_CREATE_ANOTHER_BUTTON);
        waitUntilCreatedAlert();
        return this.render();
    }

    @FindBy(css="button[id$='create-button-button']") Button create;
    public HtmlPage clickCreate()
    {
        create.click();
        waitUntilCreatedAlert();
        return getCurrentPage();
    }

    public HtmlPage clickSave()
    {
        click(SAVE_BUTTON);
        waitUntilCreatedAlert();
        return getCurrentPage();
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void waitUntilCreatedAlert()
    {
        try
        {
            waitUntilElementPresent(CREATED_ALERT, 5);
            waitUntilElementDeletedFromDom(CREATED_ALERT, 5);
        }
        catch (TimeoutException ex)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Throw exception", ex);
            }
        }
    }

    public boolean isBalloonMessageDisplayed(String text)
    {
        WebElement balloonAlert = findAndWait(BALLOON_TEXT_MESSAGE);
        if (balloonAlert.isDisplayed() && text.equals(balloonAlert.getText().trim()))
        {
            return true;
        }
        return false;
    }

}
