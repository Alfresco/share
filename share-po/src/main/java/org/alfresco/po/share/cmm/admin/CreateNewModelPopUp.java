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

/**
 * Class represents CreateNewModelPopup
 * 
 * @author mbhave
 * @author Richard Smith
 */
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
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.TextInput;

@SuppressWarnings("unchecked")
public class CreateNewModelPopUp extends ShareDialogueAikau
{
    private static final String UNIQUE_DIALOG_SELECTOR = "#CMM_CREATE_MODEL_DIALOG ";
    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogTitle");
    private static final By NAME_SPACE_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputField input[name='namespace']");
    private static final By NAME_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputField input[name='name']");
    private static final By DESCRIPTION_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " textarea[name='description']");
    private static final By AUTHOR_TEXT = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .dijitInputField input[name='author']");
    private static final By BUTTON_CANCEL_CREATE_MODEL = By.cssSelector("div[class='footer']>span>span>span>span.dijitReset.dijitInline.dijitButtonText");
    private static final By BUTTON_CREATE_MODEL = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .footer #CMM_CREATE_MODEL_DIALOG_OK");
    private static final By BUTTON_CANCEL_MODEL = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .footer #CMM_CREATE_MODEL_DIALOG_CANCEL");
    private static final By BUTTON_CREATE_MODEL_CLICKABLE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .footer #CMM_CREATE_MODEL_DIALOG_OK_label");
    
    private static final By NAMESPACE_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-namespace .validation-message.display");
    private static final By NAME_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-name .validation-message.display");
    private static final By PREFIX_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-prefix .validation-message.display");
    private static final By AUTHOR_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-author .validation-message.display");
    private static final By DESC_VALIDATION_MESSAGE = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-form-description .validation-message.display");
    private static final By SELECT_CLOSE_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogCloseIcon");
    private static final Log LOGGER = LogFactory.getLog(CreateNewModelPopUp.class);
    
    @FindBy(css="div#CMM_CREATE_MODEL_DIALOG input[name='name']")
    private TextInput name;
    @FindBy(css="div#CMM_CREATE_MODEL_DIALOG input[name='prefix']")
    private TextInput prefix;

    @Override
    public CreateNewModelPopUp render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        elementRender(timer, new RenderElement(SHARE_DIALOGUE_HEADER, ElementState.PRESENT));
        elementRender(timer, new RenderElement(NAME_SPACE_TEXT, ElementState.PRESENT));
        elementRender(timer, new RenderElement(NAME_TEXT, ElementState.PRESENT));
        elementRender(timer, new RenderElement(DESCRIPTION_TEXT, ElementState.PRESENT));
        elementRender(
                timer,
                new RenderElement(ERROR_MSG_DIALOG,ElementState.INVISIBLE),
                getVisibleRenderElement(SELECT_CLOSE_BUTTON),
                getVisibleRenderElement(BUTTON_CANCEL_MODEL));

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
     * Get the String value of name input value.
     */
    public String getName()
    {
        return name.getText();
    }
    
    public void setName(String name)
    {
        PageUtils.checkMandatoryParam("name", name);
        try
        {
            findAndWait(NAME_TEXT).sendKeys(name);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: NAME_TEXT", toe);
        }
    }

    /**
     * Get the String value of name input value.
     */

    public String getNameSpace()
    {
        return getValue(NAME_SPACE_TEXT);
    }

    /**
     * Send namespace in CreateNewModelPopUpPage
     * 
     * @param namespace
     * @return CreateNewModelPopUpPage
     */

    public CreateNewModelPopUp setNameSpace(String namespace)
    {
        PageUtils.checkMandatoryParam("namespace", namespace);
        try
        {
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
     * Send description in CreateNewModelPopUpPage
     * 
     * @param description
     * @return CreateNewModelPopUpPage
     */

    public CreateNewModelPopUp setDescription(String description)
    {
        PageUtils.checkMandatoryParam("description", description);
        try
        {
            findAndWait(DESCRIPTION_TEXT).sendKeys(description);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DESCRIPTION: ", toe);
        }

    }

    /**
     * Get the String value of prefix input value.
     */

    public String getPrefix()
    {
        return prefix.getText();
    }

    /**
     * Send prefix in CreateNewModelPopUpPage
     * 
     * @param description
     * @return CreateNewModelPopUpPage
     */

    public void setPrefix(String prefix)
    {
        this.prefix.sendKeys(prefix);
    }

    /**
     * Get the String value of Author input value.
     */

    public String getAuthor()
    {
        return getValue(AUTHOR_TEXT);

    }

    /**
     * Send author in CreateNewModelPopUpPage
     * 
     * @param author
     * @return CreateNewModelPopUpPage
     */

    public CreateNewModelPopUp setAuthor(String author)
    {
        PageUtils.checkMandatoryParam("author", author);
        try
        {
            findAndWait(AUTHOR_TEXT).sendKeys(author);

            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DESCRIPTION: ", toe);
        }

    }

    /**
     * Verify Create/Cancel button enabled in CreateNewModelPopUp
     * 
     * @return boolean
     */

    public boolean isCancelButtonEnabled(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            List<WebElement> buttonNames = findAndWaitForElements(BUTTON_CANCEL_CREATE_MODEL);
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
            LOGGER.info("Cancel button is not enabled: ", e);
        }
        return false;
    }

    /**
     * Verify Create button enabled in CreateNewModelPopUp
     * 
     * @return boolean
     */
    public boolean isCreateButtonEnabled()
    {
        try
        {
            // Get the button
            WebElement buttonname = findAndWait(BUTTON_CREATE_MODEL);
            if (buttonname.isDisplayed())
            {
                return "false".equalsIgnoreCase(buttonname.getAttribute("aria-disabled"));
            }

        }
        catch (TimeoutException e)
        {
            LOGGER.info("Create Button not enabled: ", e);
        }
        return false;
    }
    /**
     * Select create button in Create New Model Pop up Page
     * 
     * @param buttonName
     * @return {@link ModelManagerPage Page} page response
     */
    public HtmlPage selectCreateModelButton(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            WebElement button = findAndWait(BUTTON_CREATE_MODEL);
            List<WebElement> clickableButton = button.findElements(BUTTON_CREATE_MODEL_CLICKABLE);

            for (WebElement buttonname : clickableButton)
            {
                if (buttonname.getText().equalsIgnoreCase(buttonName) && (buttonname.isDisplayed()))
                {
                    buttonname.click();
                    return getCurrentPage();
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
    /**
     * Select create button in Create New Model Pop up Page
     * @deprecated
     * @param buttonName
     * @return {@link ModelManagerPage Page} page response
     */
    public HtmlPage selectCreateModelButton()
    {
        // Get the list of buttons
        driver.findElement(BUTTON_CREATE_MODEL).click();
        try
        {
        	waitUntilElementDisappears(BUTTON_CREATE_MODEL, 1);
        } 
        catch(TimeoutException te)
        {
        	/* Ignore timeout exception as the button may still be visible
        	 * due to validation catching issue with the form
        	 */
        	
        }
        return getCurrentPage();
    }


    /**
     * Select cancel button in Create New Model Pop up Page
     * 
     * @param buttonName
     * @return {@link ModelManagerPage Page} page response
     */
    public ModelManagerPage selectCancelModelButton(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            List<WebElement> buttonNames = findAndWaitForElements(BUTTON_CANCEL_CREATE_MODEL);

            // Iterate list of buttons
            for (WebElement button : buttonNames)
            {
                if (button.getText().equalsIgnoreCase(buttonName) && (button.isDisplayed()))
                {
                    button.click();
                    return factoryPage.instantiatePage(driver, ModelManagerPage.class);
                }
            }

        }
        catch (TimeoutException e)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Unable to select the" + buttonName + "button ", e);
            }
        }

        throw new PageOperationException("Unable to select the" + buttonName + "button");
    }

    @Override
    public WebElement getDialogueHeader()
    {
        try
        {
            return findFirstDisplayedElement(SHARE_DIALOGUE_HEADER);
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
            LOGGER.info("Dialogue Header not displayed: ", nse);
        }
        return title;
    }

    /**
     * Select close button in CreateNewModelPopupPage
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
                return factoryPage.instantiatePage(driver, ModelManagerPage.class);
            }

        }
        catch (TimeoutException e)
        {
            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("Unable to select the close button ", e);
            }
        }

        throw new PageOperationException("Unable to select the closebutton");
    }

    /**
     * Verify namespaceValidation message displayed correctly
     * 
     * @return boolean
     */
    public boolean isNamespaceValidationMessageDisplayed()
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
