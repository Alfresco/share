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
 * The Class CreateNewPropertyGroupPopUp
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class CreateNewPropertyGroupPopUp extends ShareDialogueAikau
{

    /** The logger */
    private static final Log LOGGER = LogFactory.getLog(CreateNewPropertyGroupPopUp.class);

    /** The selectors */
    private static final String UNIQUE_DIALOG_SELECTOR = "div[id='CMM_CREATE_PROPERTYGROUP_DIALOG']";

    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogTitle");

    private static final By SHARE_DIALOGUE_CLOSE_ICON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + ">div>span.dijitDialogCloseIcon");

    private static final By NAME_FIELD = By.cssSelector(".create-propertygroup-name input.dijitInputInner");

    private static final By NAME_VALIDATION_MSG = By.cssSelector(".create-propertygroup-name .validation-message");

    private static final By PARENT_PROPERTY_GROUP_FIELD = By.cssSelector(".create-propertygroup-parent");

    private static final By PARENT_PROPERTY_GROUP_VALIDATION_MSG = By.cssSelector(".create-propertygroup-parent .validation-message");

    private static final By TITLE_FIELD = By.cssSelector(".create-propertygroup-title input.dijitInputInner");

    private static final By TITLE_VALIDATION_MSG = By.cssSelector(".create-propertygroup-title .validation-message");

    private static final By DESCRIPTION_FIELD = By.cssSelector(".create-propertygroup-description textarea");

    private static final By DESCRIPTION_VALIDATION_MSG = By.cssSelector(".create-propertygroup-description .validation-message");

    private static final By NEW_PROPERTY_GROUP_CREATE_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + BUTTON_FIRST);

    private static final By NEW_PROPERTY_GROUP_CANCEL_BUTTON = By.cssSelector(UNIQUE_DIALOG_SELECTOR + BUTTON_LAST);

    private static final By BUTTON_CLICKABLE = By.cssSelector(".dijitButtonText");


    @Override
    public CreateNewPropertyGroupPopUp render(RenderTime timer)
    {
        elementRender(
                timer,
                new RenderElement(SHARE_DIALOGUE_HEADER, ElementState.PRESENT), // Amended from visible to present, as server error hides the Header
                getVisibleRenderElement(NAME_FIELD),
                new RenderElement(ERROR_MSG_DIALOG,ElementState.INVISIBLE),
                getVisibleRenderElement(PARENT_PROPERTY_GROUP_FIELD),
                getVisibleRenderElement(TITLE_FIELD),
                getVisibleRenderElement(DESCRIPTION_FIELD),
                getVisibleRenderElement(SHARE_DIALOGUE_CLOSE_ICON),
                getVisibleRenderElement(NEW_PROPERTY_GROUP_CANCEL_BUTTON));

        return this;
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
     * Sets the name field.
     * 
     * @param name the name
     * @return the CreateNewPropertyGroupPopUp
     */
    public CreateNewPropertyGroupPopUp setNameField(String name)
    {
        PageUtils.checkMandatoryParam("name", name);
        try
        {
            findAndWait(NAME_FIELD).sendKeys(name + "\t");
            
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: NAME_FIELD", toe);
        }
    }

    /**
     * Gets the parent property group field.
     * 
     * @return the parent property group field
     */
    public String getParentPropertyGroupField()
    {
        SelectList list = new SelectList(driver, findAndWait(PARENT_PROPERTY_GROUP_FIELD));
        return list.getValue();
    }

    /**
     * Sets the parent property group field.
     * 
     * @param value the value
     * @return the CreateNewPropertyGroupPopUp
     */
    public CreateNewPropertyGroupPopUp setParentPropertyGroupField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            SelectList list = new SelectList(driver, findAndWait(PARENT_PROPERTY_GROUP_FIELD));
            if (list.selectValue(value, true))
            {
                return this;
            }
            else
            {
                throw new PageOperationException("Could not select Parent Aspect: Value not found");
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: PARENT_PROPERTY_GROUP_FIELD", toe);
        }
    }

    /**
     * Checks if group displayed in parent list
     * 
     * @return true, if group is displayed
     */
    public boolean isGroupDisplayedInParentList(String value)
    {
        try
        {
            SelectList list = new SelectList(driver, findAndWait(PARENT_PROPERTY_GROUP_FIELD));
            return list.selectValue(value, true);
        }
        catch (TimeoutException e)
        {
            LOGGER.info("Aspect not displayed in the Parent Aspects' List: ", e);
        }
        return false;
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
     * @return the CreateNewPropertyGroupPopUp
     */
    public CreateNewPropertyGroupPopUp setTitleField(String title)
    {
        PageUtils.checkMandatoryParam("title", title);
        try
        {
            findAndWait(TITLE_FIELD).sendKeys(title);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: TITLE_FIELD", toe);
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
     * @return the CreateNewPropertyGroupPopUp
     */
    public CreateNewPropertyGroupPopUp setDescriptionField(String description)
    {
        PageUtils.checkMandatoryParam("description", description);
        try
        {
            findAndWait(DESCRIPTION_FIELD).sendKeys(description);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DESCRIPTION_FIELD: ", toe);
        }
    }

    /**
     * Checks if the create button is enabled.
     * 
     * @return true, if the create button is enabled
     */
    public boolean isCreateButtonEnabled()
    {
        return isElementEnabled(NEW_PROPERTY_GROUP_CREATE_BUTTON);
    }

    /**
     * Checks if the cancel button is enabled.
     * 
     * @return true, if the cancel button is enabled
     */
    public boolean isCancelButtonEnabled()
    {
        return isElementEnabled(NEW_PROPERTY_GROUP_CANCEL_BUTTON);
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
        	WebElement outerButton = findAndWait(NEW_PROPERTY_GROUP_CREATE_BUTTON);
            outerButton.findElement(BUTTON_CLICKABLE).click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.trace("Unable to select the create button ", e);
        }
        return getCurrentPage();
    }

    /**
     * Select cancel button.
     * 
     * @return the model manager page
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            WebElement outerButton = findAndWait(NEW_PROPERTY_GROUP_CANCEL_BUTTON);
            outerButton.findElement(BUTTON_CLICKABLE).click();
            return factoryPage.instantiatePage(driver, ManageTypesAndAspectsPage.class);
        }
        catch (TimeoutException e)
        {
            LOGGER.trace("Unable to select the cancel button ", e);
        }

        throw new PageOperationException("Unable to select the cancel button");
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
            LOGGER.trace("Unable to find the SHARE_DIALOGUE_HEADER ", nse);
        }
        return null;
    }

    /**
     * Select close button.
     * 
     * @return the model manager page
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
            LOGGER.trace("Unable to select the close button ", e);
        }

        throw new PageOperationException("Unable to select the closebutton");
    }

    /**
     * Checks if name validation message is displayed.
     * 
     * @return true, if name validation message is displayed
     */
    public boolean isNameValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(NAME_VALIDATION_MSG);
    }

    /**
     * Checks if parent property group validation message is displayed.
     * 
     * @return true, if parent property group validation message is displayed
     */
    public boolean isParentAspectValidationMessageDisplayed()
    {
        return isFieldBeingDisplayed(PARENT_PROPERTY_GROUP_VALIDATION_MSG);
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
     * Gets the value of the input field
     * 
     * @param by input field descriptor
     * @return String input value
     */
    private String getValue(By by)
    {
        return driver.findElement(by).getAttribute("value");
    }
}
