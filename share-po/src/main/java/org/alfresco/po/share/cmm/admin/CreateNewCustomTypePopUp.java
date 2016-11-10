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
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SelectList;
import org.alfresco.po.share.ShareDialogueAikau;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Class represents CreateNewCustomType PopUp
 * 
 * @author Richard Smith
 * @author mbhave
 */
@SuppressWarnings("unchecked")
public class CreateNewCustomTypePopUp extends ShareDialogueAikau
{
    private static final Log logger = LogFactory.getLog(CreateNewCustomTypePopUp.class);

    private static final String UNIQUE_DIALOG_SELECTOR = "#CMM_CREATE_TYPE_DIALOG"; //"div[id='CMM_CREATE_TYPE_DIALOG']";

    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogTitle");
    private static final By SHARE_DIALOGUE_CLOSE_ICON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogCloseIcon");
    private static final By PARENT_PROPERTY_TYPE_FIELD = By.cssSelector(".create-type-parent");
    private static final By TITLE_VALIDATION_MSG = By.cssSelector(".create-type-title .validation-message.display");
    private static final By DESCRIPTION_VALIDATION_MSG = By.cssSelector(".create-type-description .validation-message.display");
    private static final By NEW_CUSTOM_TYPE_CREATE_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + BUTTON_FIRST);
    private static final By NEW_CUSTOM_TYPE_CANCEL_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + BUTTON_LAST);
    private static final By BUTTON_CLICKABLE = By.cssSelector(".dijitButtonText");
    
    private static final By NAME_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-name input.dijitInputInner");

    private static final By TITLE_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-title input.dijitInputInner");

    private static final By DESCRIPTION_FIELD = By.cssSelector(UNIQUE_DIALOG_SELECTOR + " .create-type-description textarea");

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.ShareDialogue#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreateNewCustomTypePopUp render()
    {
    	RenderTime timer = new RenderTime(maxPageLoadingTime);
    	
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
     @RenderWebElement @FindBy(css = UNIQUE_DIALOG_SELECTOR + " .create-type-name input.dijitInputInner") TextInput name;
    /**
     * Gets the name field.
     * 
     * @return the name field
     */
    public String getNameField()
    {
        return name.getText();
    }

    /**
     * Sets the name field.
     * 
     * @param value the name
     * @return the CreateNewPropertyGroupPopUp
     */
    public void setNameField(String value)
    {
        PageUtils.checkMandatoryParam("name", value);
        name.sendKeys(value);
    }

    @RenderWebElement 
        @FindBy(css = UNIQUE_DIALOG_SELECTOR + " .create-type-title input.dijitInputInner") TextInput title;
    /**
     * Gets the title field.
     * 
     * @return the title field
     */
    public String getTitleField()
    {
        return title.getText();
    }

    /**
     * Sets the title field.
     * 
     * @param title the title
     * @return the CreateNewPropertyGroupPopUp
     */
    public void setTitleField(String title)
    {
        PageUtils.checkMandatoryParam("title", title);
        this.title.sendKeys(title);
    }

    @RenderWebElement 
        @FindBy(css = UNIQUE_DIALOG_SELECTOR + " .create-type-description textarea") WebElement description;
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescriptionField()
    {
        return description.getAttribute("value");
    }

    /**
     * Sets the description field.
     * 
     * @param description the description
     * @return the CreateNewPropertyGroupPopUp
     */
    public void setDescriptionField(String description)
    {
        PageUtils.checkMandatoryParam("description", description);
        this.description.sendKeys(description);
    }

    /**
     * Gets the parent property group field.
     * 
     * @return the parent property group field
     */
    public String getParentPropertyTypeField()
    {
        SelectList list = new SelectList(driver, findAndWait(PARENT_PROPERTY_TYPE_FIELD));
        return list.getValue();
    }

    /**
     * Select the parent type field.
     * 
     * @param value the value
     * @return the CreateNewPropertyTypePopUp
     */
    public CreateNewCustomTypePopUp selectParentTypeField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            SelectList list = new SelectList(driver, findAndWait(PARENT_PROPERTY_TYPE_FIELD));
            if (list.selectValue(value, true))
            {
                return this;
            }
            else
            {
                throw new PageOperationException("Parent Type not found: " + value);
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: PARENT_PROPERTY_TYPE_FIELD", toe);
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
            SelectList list = new SelectList(driver, findAndWait(PARENT_PROPERTY_TYPE_FIELD));
            return list.selectValue(value, true);
        }
        catch (TimeoutException e)
        {
            logger.info("Type isn't displayed in Parenttype List", e);
        }
        return false;
    }


    /**
     * Checks if title validation message is displayed.
     * 
     * @return true, if title validation message is displayed
     */
    public boolean isTitleValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(TITLE_VALIDATION_MSG);
    }

    /**
     * Checks if description validation message is displayed.
     * 
     * @return true, if description validation message is displayed
     */
    public boolean isDescriptionValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(DESCRIPTION_VALIDATION_MSG);
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
            logger.trace("Unable to find the SHARE_DIALOGUE_HEADER", nse);
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
                return factoryPage.getPage(driver);
            }

        }
        catch (TimeoutException e)
        {
            logger.trace("Unable to select the close button", e);
        }

        throw new PageOperationException("Unable to select the closebutton");
    }

    /**
     * Checks if the create button is enabled.
     * 
     * @return true, if the create button is enabled
     */
    public boolean isCreateButtonEnabled()
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
     * @return the model manager page
     */
    public HtmlPage selectCreateButton()
    {
        try
        {
            WebElement outerButton = findAndWait(NEW_CUSTOM_TYPE_CREATE_BUTTON);
            outerButton.findElement(BUTTON_CLICKABLE).click();
            waitUntilAlert();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            logger.trace("Unable to select the create button", e);
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
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            logger.trace("Unable to select the cancel button", e);
        }

        throw new PageOperationException("Unable to select the cancel button");
    }

}
