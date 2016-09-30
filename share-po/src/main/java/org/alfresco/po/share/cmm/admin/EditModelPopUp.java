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

import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogueAikau;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

@SuppressWarnings("unchecked")
public class EditModelPopUp extends ShareDialogueAikau
{

    private static final String UNIQUE_DIALOG_SELECTOR = "#CMM_EDIT_MODEL_DIALOG ";

    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogTitle");

    private static final By NAME_SPACE_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputField input[name='namespace']");

    private static final By PREFIX_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputField input[name='prefix']");

    private static final By NAME_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputInner[name='name']");

    private static final By DESCRIPTION_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " textarea[name='description']");

    private static final By AUTHOR_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputField input[name='author']");

    private static final By BUTTON_EDIT_MODEL = By.cssSelector(UNIQUE_DIALOG_SELECTOR + BUTTON_OK);

    private static final By BUTTON_CANCEL_EDIT_MODEL = By.cssSelector(UNIQUE_DIALOG_SELECTOR + BUTTON_CANCEL);

    private static final By NAMESPACE_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-namespace .validation-message.display");

    private static final By NAME_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-name .validation-message.display");

    private static final By PREFIX_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-prefix .validation-message.display");

    private static final By AUTHOR_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-author .validation-message.display");

    private static final By DESC_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-description .validation-message.display");

    private static final By SELECT_CLOSE_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogCloseIcon");

    private static final Log LOGGER = LogFactory.getLog(CreateNewModelPopUp.class);

    @Override
    public EditModelPopUp render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(SHARE_DIALOGUE_HEADER));

        elementRender(
                timer,
                getVisibleRenderElement(NAME_SPACE_TEXT),
                getVisibleRenderElement(NAME_TEXT),
                getVisibleRenderElement(DESCRIPTION_TEXT),
                getVisibleRenderElement(DESCRIPTION_TEXT),
                getVisibleRenderElement(SELECT_CLOSE_BUTTON),
                getVisibleRenderElement(BUTTON_CANCEL_EDIT_MODEL),
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
     * Verify name disabled
     * 
     * @return boolean
     */
    public boolean isNameDisabled()
    {
        try
        {
            // Get the name
            WebElement name = driver.findElement(NAME_TEXT);

            if (name.isDisplayed() && !name.isEnabled())
            {
                return true;
            }

        }
        catch (Exception e)
        {
            LOGGER.debug("Timed out while getting the status of Name field: ", e);
        }
        return false;

    }

    /**
     * Get the String value of name input value.
     */

    public String getNameSpace()
    {
        return getValue(NAME_SPACE_TEXT);
    }

    /**
     * Send namespace in EditModelPopUp
     * 
     * @param namespace
     * @return EditModelPopUp
     */

    public EditModelPopUp setNameSpace(String namespace)
    {
        PageUtils.checkMandatoryParam("namespace", namespace);
        try
        {
            findAndWait(NAME_SPACE_TEXT).clear();
            findAndWait(NAME_SPACE_TEXT).sendKeys(namespace);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: NAME_SPACE", toe);
        }

    }

    /**
     * Get the String value of description input value.
     */

    public String getDescription()
    {
        return getValue(DESCRIPTION_TEXT);

    }

    /**
     * Send description in EditModelPopUp
     * 
     * @param description
     * @return EditModelPopUp
     */

    public EditModelPopUp setDescription(String description)
    {
        PageUtils.checkMandatoryParam("description", description);
        try
        {
            findAndWait(DESCRIPTION_TEXT).clear();
            findAndWait(DESCRIPTION_TEXT).sendKeys(description);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DESCRIPTION ", toe);
        }

    }

    /**
     * Get the String value of prefix input value.
     */

    public String getPrefix()
    {
        return getValue(PREFIX_TEXT);
    }

    /**
     * Send prefix in EditModelPopUp
     * 
     * @param description
     * @return EditModelPopUp
     */

    public EditModelPopUp setPrefix(String prefix)
    {
        PageUtils.checkMandatoryParam("prefix", prefix);
        try
        {
            findAndWait(PREFIX_TEXT).clear();
            findAndWait(PREFIX_TEXT).sendKeys(prefix);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: PREFIX_TEXT ", toe);
        }

    }

    /**
     * Get the String value of Author input value.
     */

    public String getAuthor()
    {
        return getValue(AUTHOR_TEXT);

    }

    /**
     * Send author in EditModelPopUp
     * 
     * @param author
     * @return EditModelPopUp
     */

    public EditModelPopUp setAuthor(String author)
    {
        try
        {
            findAndWait(AUTHOR_TEXT).clear();
            findAndWait(AUTHOR_TEXT).sendKeys(author);

            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: AUTHOR ", toe);
        }

    }

    /**
     * Verify Cancel button enabled in EditModelPopUp
     * 
     * @return boolean
     */

    public boolean isCancelButtonEnabled(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            List<WebElement> buttonNames = findAndWaitForElements(BUTTON_CANCEL_EDIT_MODEL);
            // Iterate list of buttons
            for (WebElement button : buttonNames)
            {
                if (button.isDisplayed() && button.getText().equalsIgnoreCase(buttonName))
                {
                    return true;
                }
            }
        }
        catch (TimeoutException e)
        {
            LOGGER.debug("Timed out while getting the status of Cancel Button: ", e);
        }
        return false;
    }

    /**
     * Verify Edit button enabled in EditModelPopUp
     * 
     * @return boolean
     */
    public boolean isEditButtonEnabled(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the button
            WebElement buttonname = findAndWait(BUTTON_EDIT_MODEL);
            if (buttonname.isDisplayed() && buttonname.getText().equalsIgnoreCase(buttonName))
            {
                return true;
            }

        }
        catch (TimeoutException e)
        {
            LOGGER.debug("Timed out while getting the status of Edit Button: ", e);
        }
        return false;
    }

    /**
     * Select Save button in EditModelPopUp
     * 
     * @param buttonName
     * @return {@link ModelManagerPage Page} page response
     */
    public HtmlPage selectEditModelButton(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            WebElement buttonname = findAndWait(BUTTON_EDIT_MODEL);
            if (buttonname.getText().equalsIgnoreCase(buttonName) && (buttonname.isDisplayed()))
            {
                buttonname.click();
                return factoryPage.getPage(driver);
            }

        }
        catch (TimeoutException e)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Unable to select the" + buttonName + "button: ", e);
            }
        }

        throw new PageOperationException("Unable to select the" + buttonName + "button");
    }

    /**
     * Select cancel button in EditModelPopUp
     * 
     * @param buttonName
     * @return {@link ModelManagerPage Page} page response
     */
    public HtmlPage selectCancelModelButton(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            List<WebElement> buttonNames = findAndWaitForElements(BUTTON_CANCEL_EDIT_MODEL);
            // Iterate list of buttons
            for (WebElement button : buttonNames)
            {
                if (button.getText().equalsIgnoreCase(buttonName) && (button.isDisplayed()))
                {
                    button.click();
                    return factoryPage.getPage(driver);
                }
            }

        }
        catch (TimeoutException e)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Unable to select the" + buttonName + "button: ", e);
            }
        }

        throw new PageOperationException("Unable to select the" + buttonName + "button");
    }

    @Override
    public WebElement getDialogueHeader()
    {
        try
        {
            WebElement shareDialogueHeader = findFirstDisplayedElement(SHARE_DIALOGUE_HEADER);
            return shareDialogueHeader;
        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("Unable to find the css ", nse);
        }
    }

    /**
     * Helper method to get the Dialogue title
     * 
     * @return String
     */
    @Override
    public String getDialogueTitle()
    {
        String title = "";
        try
        {
            WebElement dialogue = getDialogueHeader();
            title = dialogue.getText();
            return title;
        }
        catch (NoSuchElementException nse)
        {
            LOGGER.debug("Timed out while getting the Dialogue Title: ", nse);
        }
        return title;
    }

    /**
     * Select close button in EditModelPopUp
     * 
     * @return {@link ModelManagerPage Page} page response
     */
    public HtmlPage selectCloseButton()
    {
        try
        {
            // Get the Close button web element
            WebElement closebutton = driver.findElement(SELECT_CLOSE_BUTTON);

            if (closebutton.isEnabled() && (closebutton.isDisplayed()))
            {
                closebutton.click();
                // return FactoryShareCMMPage.resolveCMMPage(driver).render();
                return factoryPage.getPage(driver);
            }

        }
        catch (TimeoutException e)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Unable to select the close button: ", e);
            }
        }

        throw new PageOperationException("Unable to select the closebutton");
    }

    /**
     * Verify namespaceValidation message displayed correctly
     * 
     * @return boolean
     */
    public boolean isNamespaceValationMessageDisplayed()
    {
        return isFieldBeingDisplayed(NAMESPACE_VALIDATION_MESSAGE);
    }

    /**
     * Verify nameValidation message displayed correctly
     * 
     * @return boolean
     */
    public boolean isNameValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(NAME_VALIDATION_MESSAGE);
    }

    /**
     * Verify prefix Validation message displayed correctly
     * 
     * @return boolean
     */
    public boolean isPrefixValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(PREFIX_VALIDATION_MESSAGE);
    }

    /**
     * Verify Author Validation message displayed correctly
     * 
     * @return boolean
     */
    public boolean isAuthorValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(AUTHOR_VALIDATION_MESSAGE);
    }

    /**
     * Verify Description Validation message displayed correctly
     * 
     * @return boolean
     */
    public boolean isDescValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(DESC_VALIDATION_MESSAGE);
    }

}
