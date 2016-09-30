/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.cmm.admin;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SelectList;
import org.alfresco.po.share.ShareDialogueAikau;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Class represents EditCustomType PopUp
 * 
 * @author mbhave
 */
public class EditCustomTypePopUp extends ShareDialogueAikau
{
    private static final Log LOGGER = LogFactory.getLog(EditCustomTypePopUp.class);

    private static final String UNIQUE_DIALOG_SELECTOR = ".edit-form-dialog"; // "div[id='CMM_EDIT_TYPE_DIALOG']";

    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogTitle");

    private static final By SHARE_DIALOGUE_CLOSE_ICON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogCloseIcon");

    private static final By PARENT_TYPE_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-parent");

    private static final By NAME_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-name input.dijitInputInner");

    private static final By NAME_VALIDATION_MSG = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-name .validation-message.display");

    private static final By TITLE_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-title input.dijitInputInner");

    private static final By TITLE_VALIDATION_MSG = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-title .validation-message.display");

    private static final By DESCRIPTION_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-description textarea");

    private static final By DESCRIPTION_VALIDATION_MSG = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-description .validation-message.display");

    private static final By NEW_CUSTOM_TYPE_CREATE_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .footer .alfresco-buttons-AlfButton:first-of-type");

    private static final By NEW_CUSTOM_TYPE_CANCEL_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .footer .alfresco-buttons-AlfButton:last-of-type");

    private static final By BUTTON_CLICKABLE = By.cssSelector(".dijitButtonText");

    @SuppressWarnings("unchecked")
    @Override
    public EditCustomTypePopUp render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(SHARE_DIALOGUE_HEADER));

        elementRender(
                timer,
                getVisibleRenderElement(NAME_FIELD),
                getVisibleRenderElement(TITLE_FIELD),
                getVisibleRenderElement(DESCRIPTION_FIELD),
                getVisibleRenderElement(SHARE_DIALOGUE_CLOSE_ICON),
                getVisibleRenderElement(NEW_CUSTOM_TYPE_CANCEL_BUTTON),
                new RenderElement(ERROR_MSG_DIALOG,ElementState.INVISIBLE));

        return this;
    }

    /**
     * Gets the value of the input field
     * 
     * @param by input field descriptor
     * @return String input value
     */
    protected String getValue(By by)
    {
        return driver.findElement(by).getAttribute("value");
    }

    /**
     * Gets the name field.
     * 
     * @return the name field
     */
    public String getNameField()
    {
        return getValue(NAME_FIELD);
    }

    /**
     * Checks if the specified field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    private boolean isFieldEnabled(By by)
    {
        try
        {
            return driver.findElement(by).isEnabled();
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            LOGGER.info("Exception while checking if field is enabled", e);
        }

        return false;
    }

    /**
     * Checks if the Name field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isNameEnabled()
    {
        return isFieldEnabled(NAME_FIELD);
    }

    /**
     * Checks if the Title field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isTitleEnabled()
    {
        return isFieldEnabled(TITLE_FIELD);
    }

    /**
     * Checks if the DESCRIPTION field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isDescriptionEnabled()
    {
        return isFieldEnabled(DESCRIPTION_FIELD);
    }

    /**
     * Checks if the ParentType field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isParentTypeEnabled()
    {
        SelectList list = new SelectList(driver, findAndWait(PARENT_TYPE_FIELD));

        return list.isEnabled();
    }

    /**
     * Gets the title field.
     * 
     * @return the title field
     */
    public String getTitleField()
    {
        return getValue(TITLE_FIELD);
    }

    /**
     * Sets the title field.
     * 
     * @param title the title
     * @return the EditCustomTypePopUp
     */
    public EditCustomTypePopUp setTitleField(String title)
    {
        PageUtils.checkMandatoryParam("title", title);
        try
        {
            WebElement titleField = findAndWait(TITLE_FIELD);
            titleField.clear();
            titleField.sendKeys(title);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: TITLE_FIELD ", toe);
        }
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescriptionField()
    {
        return getValue(DESCRIPTION_FIELD);
    }

    /**
     * Sets the description field.
     * 
     * @param description the description
     * @return the EditCustomTypePopUp
     */
    public EditCustomTypePopUp setDescriptionField(String description)
    {
        PageUtils.checkMandatoryParam("description", description);
        try
        {
            WebElement descField = findAndWait(DESCRIPTION_FIELD);
            descField.clear();
            descField.sendKeys(description);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DESCRIPTION_FIELD ", toe);
        }
    }

    /**
     * Gets the parent type field.
     * 
     * @return the parent type field
     */
    public String getParentTypeField()
    {
        SelectList list = new SelectList(driver, findAndWait(PARENT_TYPE_FIELD));
        return list.getValue();
    }

    /**
     * Select the parent type field.
     * 
     * @param value the value
     * @return the EditCustomTypePopUp
     */
    public EditCustomTypePopUp selectParentTypeField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            SelectList list = new SelectList(driver, findAndWait(PARENT_TYPE_FIELD));
            if (list.selectValue(value, true))
            {
                return this;
            }
            else
            {
                LOGGER.debug("Could not select Parent Type: Value not found / List not enabled");
                return this;
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: PARENT_PROPERTY_GROUP_FIELD ", toe);
        }
    }

    /**
     * Checks if type displayed in parent list
     * 
     * @return true, if type is displayed
     */
    public boolean isTypeDisplayedInParentList(String value)
    {
        try
        {
            SelectList list = new SelectList(driver, findAndWait(PARENT_TYPE_FIELD));
            return list.selectValue(value, true);
        }
        catch (TimeoutException e)
        {
            LOGGER.info("Exception while checking if Type is displayed in Parent Type list", e);
        }
        return false;
    }

    /**
     * Checks if name validation message is displayed.
     * 
     * @return true, if name validation message is displayed
     */
    public boolean isNameValidationMessageDisplayed()
    {
        return isFieldEnabled(NAME_VALIDATION_MSG);
    }

    /**
     * Checks if title validation message is displayed.
     * 
     * @return true, if title validation message is displayed
     */
    public boolean isTitleValidationMessageDisplayed()
    {
        return isFieldEnabled(TITLE_VALIDATION_MSG);
    }

    /**
     * Checks if description validation message is displayed.
     * 
     * @return true, if description validation message is displayed
     */
    public boolean isDescriptionValidationMessageDisplayed()
    {
        return isFieldEnabled(DESCRIPTION_VALIDATION_MSG);
    }

    /**
     * Helper method to get the Dialogue title
     * 
     * @return String
     */
    @Override
    public String getDialogueTitle()
    {
        try
        {
            return findFirstDisplayedElement(SHARE_DIALOGUE_HEADER).getText();
        }
        catch (NoSuchElementException nse)
        {
            LOGGER.trace("Unable to find the SHARE_DIALOGUE_HEADER", nse);
        }
        return null;
    }

    /**
     * Select close button.
     * 
     * @return the ManageTypesAndAspectsPage
     */
    public HtmlPage selectCloseButton()
    {
        try
        {
            WebElement closebutton = driver.findElement(SHARE_DIALOGUE_CLOSE_ICON);

            if (closebutton.isEnabled() && (closebutton.isDisplayed()))
            {
                closebutton.click();
                return factoryPage.instantiatePage(driver, ManageTypesAndAspectsPage.class);
            }

        }
        catch (TimeoutException e)
        {
            LOGGER.trace("Unable to select the close button", e);
        }

        throw new PageOperationException("Unable to select the closebutton");
    }

    /**
     * Checks if the Save button is enabled.
     * 
     * @return true, if the Save button is enabled
     */
    public boolean isSaveButtonEnabled()
    {
        return isElementEnabled(NEW_CUSTOM_TYPE_CREATE_BUTTON);
    }

    /**
     * Checks if the cancel button is enabled.
     * 
     * @return true, if the cancel button is enabled
     */
    public boolean isCancelButtonEnabled()
    {
        return isElementEnabled(NEW_CUSTOM_TYPE_CANCEL_BUTTON);
    }

    /**
     * Select create button.
     * 
     * @return ManageTypesAndAspectsPage
     */
    public HtmlPage selectSaveButton()
    {
        try
        {
            WebElement outerButton = findAndWait(NEW_CUSTOM_TYPE_CREATE_BUTTON);
            outerButton.findElement(BUTTON_CLICKABLE).click();
            return factoryPage.instantiatePage(driver, ManageTypesAndAspectsPage.class);
        }
        catch (TimeoutException e)
        {
            LOGGER.trace("Unable to select the create button", e);
        }

        throw new PageOperationException("Unable to select the create button");
    }

    /**
     * Select cancel button.
     * 
     * @return the ManageTypesAndAspectsPage
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            WebElement outerButton = findAndWait(NEW_CUSTOM_TYPE_CANCEL_BUTTON);
            outerButton.findElement(BUTTON_CLICKABLE).click();
            return factoryPage.instantiatePage(driver, ManageTypesAndAspectsPage.class);
        }
        catch (TimeoutException e)
        {
            LOGGER.trace("Unable to select the cancel button", e);
        }

        throw new PageOperationException("Unable to select the cancel button");
    }

}
