package org.alfresco.po.share.site.contentrule.createrules;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPageWithRules;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractActionSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.po.share.site.contentrule.createrules.selectors.impl.*;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

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
    private static final By CHECK_BOX_DISABLE = By.cssSelector("div[class='form-field disabled'] input[title='Disable rule']");
    private static final By CHECK_BOX_APPLY_TO_SUBFOLDER = By.cssSelector("div[class='form-field applyToChildren'] input[title='Rule applies to subfolders']");
    private static final By CHECK_BOX_RULE_IN_BACKGROUND = By
            .cssSelector("div[class='form-field executeAsynchronously'] input[title='Run rule in background']");

    // Messages
    private static final By BALLOON_TEXT_MESSAGE = By.cssSelector("div[class='balloon'] div[class='text'] div");
    public static final String BALLOON_TEXT_VALUE_NOT_EMPTY = "The value cannot be empty.";

    // Buttons
    private static final By CANCEL_BUTTON = By.cssSelector("span[id*='cancel-button'] button[id*='default-cancel-button']");
    private static final By CREATE_BUTTON = By.cssSelector("span[id*='create-button'] button[id*='default-create-button']");
    private static final By SAVE_BUTTON = By.cssSelector("span[id*='save-button'] button[id*='default-save-button']");
    private static final By CREATE_AND_CREATE_ANOTHER_BUTTON = By.cssSelector("span[id*='createAnother-button'] button[id*='default-createAnother-button']");

    private static final By CREATED_ALERT = By.xpath(".//*[@id='message']/div/span");

    public CreateRulePage(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public CreateRulePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public void addOrRemoveOptionsFieldsToBlock(final Block block, final AddRemoveAction operation)
    {
        WebElement blockElement = drone.findAndWait(block.selector);
        List<WebElement> operationButton = blockElement.findElements(operation.actionSelector);
        operationButton.get(operationButton.size() - 1).click();
    }

    public WhenSelectorImpl getWhenOptionObj()
    {
        return new WhenSelectorImpl(drone);
    }

    public IfErrorEnterpImpl getIfErrorObj()
    {
        return new IfErrorEnterpImpl(drone);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractIfSelector> T getIfOptionObj()
    {
        if (alfrescoVersion.isCloud())
        {
            return (T) new IfSelectorCloudImpl(drone);
        }
        else
        {
            return (T) new IfSelectorEnterpImpl(drone);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractActionSelector> T getActionOptionsObj()
    {
        if (alfrescoVersion.isCloud())
        {
            return (T) new ActionSelectorCloudImpl(drone);
        }
        else
        {
            return (T) new ActionSelectorEnterpImpl(drone);
        }
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
        WebElement inputField = drone.findAndWait(selector);
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
        WebElement inputField = drone.findAndWait(SET_VALUE_CHECKBOX);
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
        WebElement applyToSubfolderCheckBox = drone.findAndWait(CHECK_BOX_APPLY_TO_SUBFOLDER);
        applyToSubfolderCheckBox.click();
    }

    public void selectRunRuleInBackgroundCheckbox()
    {
        WebElement runRuleInBackgroundCheckbox = drone.findAndWait(CHECK_BOX_RULE_IN_BACKGROUND);
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
        return drone.findAndWait(selector).getAttribute("value");
    }

    public <T extends FolderRulesPage> T clickCancelButton()
    {
        click(CANCEL_BUTTON);
        return drone.getCurrentPage().render();
    }

    public CreateRulePage clickAnotherCreate()
    {
        click(CREATE_AND_CREATE_ANOTHER_BUTTON);
        waitUntilCreatedAlert();
        return this.render();
    }

    public FolderRulesPageWithRules clickCreate()
    {
        click(CREATE_BUTTON);
        waitUntilCreatedAlert();
        return drone.getCurrentPage().render();
    }

    public FolderRulesPageWithRules clickSave()
    {
        click(SAVE_BUTTON);
        waitUntilCreatedAlert();
        return drone.getCurrentPage().render();
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    public boolean isNameFieldAndDescriptionEmpty()
    {
        WebElement nameFieldText = drone.findAndWait(NAME_FIELD);
        WebElement descriptionFieldText = drone.findAndWait(DESCRIPTION_FIELD);
        return (isFieldEmpty(nameFieldText) && isFieldEmpty(descriptionFieldText));
    }

    public boolean isDefaultSelectsChoiceCorrect()
    {
        Select whenSelect = new Select(drone.findAndWait(WHEN_OPTIONS_SELECT));
        boolean isWhenDefaultTextCorrect = "Items are created or enter this folder".equals(whenSelect.getFirstSelectedOption().getText());
        Select ifSelect = new Select(drone.findAndWait(IF_OPTIONS_SELECT));
        boolean isIfDefaultTextCorrect = "All Items".equals(ifSelect.getFirstSelectedOption().getText());
        Select actionSelect = new Select(drone.findAndWait(ACTION_OPTIONS_SELECT));
        boolean isActionDefaultTextCorrect;
        if (alfrescoVersion.isCloud())
        {
            isActionDefaultTextCorrect = "Copy".equals(actionSelect.getFirstSelectedOption().getText());
        }
        else
        {
            isActionDefaultTextCorrect = "Select...".equals(actionSelect.getFirstSelectedOption().getText());
        }
        return (isWhenDefaultTextCorrect && isIfDefaultTextCorrect && isActionDefaultTextCorrect);
    }

    public boolean isCheckBoxesCorrectByDefault()
    {
        WebElement disableRuleCheckBox = drone.findAndWait(CHECK_BOX_DISABLE);
        WebElement applyToSubfolderCheckBox = drone.findAndWait(CHECK_BOX_APPLY_TO_SUBFOLDER);
        return (!disableRuleCheckBox.isSelected() && !applyToSubfolderCheckBox.isSelected());
    }

    public boolean isButtonsCorrectByDefault()
    {
        boolean isCreateButtonCorrect = false;
        boolean isCancelButtonCorrect = false;
        boolean isAnotherCreateButtonCorrect = false;

        WebElement createButton = drone.findAndWait(CREATE_BUTTON);
        if (alfrescoVersion.isDojoSupported())
        {
            if (isElementEnableAndDisplay(createButton))
            {
                createButton.submit();
                isCreateButtonCorrect = isBalloonMessageDisplayed(BALLOON_TEXT_VALUE_NOT_EMPTY);
            }
        }
        else
        {
            isCreateButtonCorrect = (createButton.isDisplayed() && !createButton.isEnabled());
        }
        WebElement anotherCreateButton = drone.findAndWait(CREATE_AND_CREATE_ANOTHER_BUTTON);
        if (alfrescoVersion.isDojoSupported())
        {
            if (isElementEnableAndDisplay(anotherCreateButton))
            {
                anotherCreateButton.submit();
                isAnotherCreateButtonCorrect = isBalloonMessageDisplayed(BALLOON_TEXT_VALUE_NOT_EMPTY);
            }
        }
        else
        {
            isAnotherCreateButtonCorrect = (createButton.isDisplayed() && !createButton.isEnabled());
        }
        WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
        isCancelButtonCorrect = isElementEnableAndDisplay(cancelButton);

        return (isAnotherCreateButtonCorrect && isCancelButtonCorrect && isCreateButtonCorrect);
    }

    public boolean isAllButtonEnableAndDisplay()
    {
        WebElement createButton = drone.findAndWait(CREATE_BUTTON);
        WebElement anotherCreateButton = drone.findAndWait(CREATE_AND_CREATE_ANOTHER_BUTTON);
        WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
        return (isElementEnableAndDisplay(createButton) && isElementEnableAndDisplay(anotherCreateButton) && isElementEnableAndDisplay(cancelButton));
    }

    private boolean isElementEnableAndDisplay(WebElement element)
    {
        return (element.isDisplayed() && element.isEnabled());
    }

    private boolean isFieldEmpty(WebElement element)
    {
        return "".equals(element.getText());
    }

    private void waitUntilCreatedAlert()
    {
        try
        {
            drone.waitUntilElementPresent(CREATED_ALERT, 5);
            drone.waitUntilElementDeletedFromDom(CREATED_ALERT, 5);
        }
        catch (TimeoutException ex)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Throw exception", ex);
            }
        }
    }

    public boolean isPageCorrect()
    {
        return (isButtonsCorrectByDefault() && isCheckBoxesCorrectByDefault() && isNameFieldAndDescriptionEmpty());
    }

    public boolean isBalloonMessageDisplayed(String text)
    {
        WebElement balloonAlert = drone.findAndWait(BALLOON_TEXT_MESSAGE);
        if (balloonAlert.isDisplayed() && text.equals(balloonAlert.getText().trim()))
        {
            return true;
        }
        return false;
    }

}
