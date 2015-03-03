package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import org.alfresco.po.share.site.contentrule.createrules.SetPropertyValuePage;
import org.alfresco.po.share.site.contentrule.createrules.EmailMessageForm;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractActionSelector;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:07
 */
public class ActionSelectorEnterpImpl extends AbstractActionSelector
{
    private static final By CHECK_IN_OPTIONS_BUTTON = By.cssSelector("span[class*='check-in'] button");
    private static final By LINK_TO_CATEGORY_SELECT_BUTTON = By.cssSelector("span[class*='category'] button");
    private static final By ASPECT_SELECT = By.cssSelector("select[title='aspect-name']");
    private static final By APPROVE_BUTTON = By.cssSelector("span[class*='simple-workflow'] button:not([disabled])");
    private static final By MESSAGE_BUTTON = By.cssSelector("span[class*='email-dialog'] button");
    private static final By TYPE_SELECTOR = By.cssSelector("span[class*='specialise'] select[param='type-name']");

    private enum PerformActions
    {
        EXECUTE_SCRIPT(1),
        COPY(2),
        MOVE(3),
        CHECK_IN(4),
        CHECK_OUT(5),
        LINK_TO_CATEGORY(6),
        ADD_ASPECT(7),
        REMOVE_ASPECT(8),
        ADD_SIMPLE_WORKFLOW(9),
        SEND_MAIL(10),
        TRANSFORM_AND_COPY_CONTENT(11),
        TRANSFORM_AND_COPY_IMAGE(12),
        EXTRACT_COMMON_METADATA_FIELDS(13),
        IMPORT(14),
        SPECIALISE_TYPE(15),
        INCREMENT_COUNTER(16),
        SET_PROPERTY_VALUE(17);

        private final int numberPosition;

        PerformActions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public ActionSelectorEnterpImpl(WebDrone drone)
    {
        super(drone);
    }

    public void selectExecuteScript(String visibleName)
    {
        super.selectAction(PerformActions.EXECUTE_SCRIPT.numberPosition);
        super.selectScript(visibleName);

    }

    public void selectCopy(String siteName, String... folders)
    {
        super.selectAction(PerformActions.COPY.numberPosition);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectMove(String siteName, String... folders)
    {
        super.selectAction(PerformActions.MOVE.numberPosition);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    @Deprecated
    public void selectCheckIn()
    {
        super.selectAction(PerformActions.CHECK_IN.numberPosition);
        List<WebElement> checkInButtons = getDrone().findAndWaitForElements(CHECK_IN_OPTIONS_BUTTON);
        checkInButtons.get(checkInButtons.size() - 1).click();
        // todo added logic for work with popUp menu.
    }

    public void selectCheckOut(String siteName, String... folders)
    {
        super.selectAction(PerformActions.CHECK_OUT.numberPosition);
        super.selectDestination(siteName, folders);
    }

    @Deprecated
    public void selectLinkToCategory()
    {
        super.selectAction(PerformActions.LINK_TO_CATEGORY.numberPosition);
        List<WebElement> selectButtons = getDrone().findAndWaitForElements(LINK_TO_CATEGORY_SELECT_BUTTON);
        selectButtons.get(selectButtons.size() - 1).click();
        // todo added logic for work with popUp menu.
    }

    public void selectAddAspect(String visibleAspectName)
    {
        super.selectAction(PerformActions.ADD_ASPECT.numberPosition);
        selectAspectType(visibleAspectName);
    }

    public void selectRemoveAspect(String visibleAspectName)
    {
        super.selectAction(PerformActions.REMOVE_ASPECT.numberPosition);
        selectAspectType(visibleAspectName);
    }

    private void selectAspectType(String visibleAspectName)
    {
        List<WebElement> aspectElements = getDrone().findAndWaitForElements(ASPECT_SELECT);
        List<Select> aspectSelects = new ArrayList<Select>();
        for (WebElement aspectElement : aspectElements)
        {
            aspectSelects.add(new Select(aspectElement));
        }
        aspectSelects.get(aspectSelects.size() - 1).selectByVisibleText(visibleAspectName);
    }

    public void selectTransformAndCopy(String visibleTypeText, String siteName, String... folders)
    {
        super.selectAction(PerformActions.TRANSFORM_AND_COPY_CONTENT.numberPosition);
        super.selectTransformContent(visibleTypeText);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectTransformAndCopyImg(String visibleTypeText, String siteName, String... folders)
    {
        super.selectAction(PerformActions.TRANSFORM_AND_COPY_IMAGE.numberPosition);
        super.selectTransformContent(visibleTypeText);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    @Deprecated
    public void selectSimpleWorkFlow()
    {
        super.selectAction(PerformActions.ADD_SIMPLE_WORKFLOW.numberPosition);
        List<WebElement> approveButtons = getDrone().findAndWaitForElements(APPROVE_BUTTON);
        approveButtons.get(approveButtons.size() - 1).click();
        // todo add logic for work with PopUp menu.
    }

    public EmailMessageForm selectSendEmail()
    {
        super.selectAction(PerformActions.SEND_MAIL.numberPosition);
        List<WebElement> messageButtons = getDrone().findAndWaitForElements(MESSAGE_BUTTON);
        messageButtons.get(messageButtons.size() - 1).click();
        EmailMessageForm emailMessageForm = new EmailMessageForm(getDrone());
        if (emailMessageForm.isDisplay())
        {
            return emailMessageForm;
        }
        else
        {
            throw new PageOperationException("Email Form didn't open.");
        }

    }

    public void selectExtractMetadata()
    {
        super.selectAction(PerformActions.EXTRACT_COMMON_METADATA_FIELDS.numberPosition);
    }

    public void selectImport(String siteName, String... folders)
    {
        super.selectAction(PerformActions.IMPORT.numberPosition);
        super.selectDestination(siteName, folders);
    }

    public void selectSpecialiseType(String visibleTypeText)
    {
        super.selectAction(PerformActions.SPECIALISE_TYPE.numberPosition);
        List<WebElement> typeElements = getDrone().findAndWaitForElements(TYPE_SELECTOR);
        List<Select> typeSelects = new ArrayList<Select>();
        for (WebElement typeElement : typeElements)
        {
            typeSelects.add(new Select(typeElement));
        }
        typeSelects.get(typeSelects.size() - 1).selectByVisibleText(visibleTypeText);
    }

    public void selectIncrementCounter()
    {
        super.selectAction(PerformActions.INCREMENT_COUNTER.numberPosition);
    }

    public void selectMoveToDestination(String destinationName, String... folders)
    {
        super.selectAction(PerformActions.MOVE.numberPosition);
        super.selectDestinationName(destinationName, folders).selectOkButton();

    }

    public void selectSetPropertyValue(String folderName,String value)
    {
//        super.selectAction(PerformActions.SET_PROPERTY_VALUE.numberPosition);
        selectSetPropertyValue();
        super.selectPropertyValue(folderName, value);
        SetPropertyValuePage selectValuePage = new SetPropertyValuePage(getDrone());
        selectValuePage.selectOkButton();

    }

    public void selectSetPropertyValue()
    {
        super.selectAction(PerformActions.SET_PROPERTY_VALUE.numberPosition);
    }

}
