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
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.cmm.enums.IndexingOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The Class EditPropertyPopUp
 * 
 * @author Charu
 */
@SuppressWarnings("unchecked")
public class EditPropertyPopUp extends ShareDialogueAikau
{
    private static final Log LOGGER = LogFactory.getLog(EditPropertyPopUp.class);

    /** The selectors */
    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .dijitDialogTitle");

    private static final By SHARE_DIALOGUE_CLOSE_ICON = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .dijitDialogCloseIcon");

    private static final By NAME_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-name input.dijitInputInner");

    private static final By NAME_VALIDATION_MSG = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-name .validation-message");

    private static final By TITLE_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-title input.dijitInputInner");

    private static final By TITLE_VALIDATION_MSG = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-title .validation-message");

    private static final By DESCRIPTION_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-description textarea");

    private static final By DESCRIPTION_VALIDATION_MSG = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-description .validation-message");

    private static final By DATATYPE_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-datatype");

    private static final By DATATYPE_FIELD_DISABLED = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-datatype div.control table.dijitDisabled");

    private static final By DATATYPE_VALIDATION_MSG = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-datatype .validation-message");

    private static final By MANDATORY_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-mandatory");

    private static final By MANDATORY_FIELD_DISABLED = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-mandatory div.control table.dijitDisabled");

    private static final By MANDATORY_VALIDATION_MSG = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-mandatory .validation-message");

    private static final By MULTIPLE_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-multiple .dijitCheckBox");

    private static final By MULTIPLE_FIELD_DISABLED = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-multiple .dijitCheckBoxDisabled");

    private static final By DEFAULT_VALUE_FIELD_TEXT = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.text input.dijitInputInner");

    private static final By DEFAULT_VALUE_VALIDATION_MSG_TEXT = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.text .validation-message");

    private static final By DEFAULT_VALUE_FIELD_NUMBER = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.number input.dijitInputInner");

    private static final By DEFAULT_VALUE_VALIDATION_MSG_NUMBER = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.number .validation-message");

    private static final By DEFAULT_VALUE_FIELD_DATE = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.date input.dijitInputInner");

    private static final By DEFAULT_VALUE_VALIDATION_MSG_DATE = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.date .validation-message");

    private static final By DEFAULT_VALUE_FIELD_BOOLEAN = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.boolean");

    private static final By DEFAULT_VALUE_VALIDATION_MSG_BOOLEAN = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-default.boolean .validation-message");

    private static final By CONSTRAINT_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint");

    private static final By INDEX_FIELD = By.cssSelector("#CMM_CREATE_PROPERTY_DIALOG .create-property-indexing");

    private static final By CONSTRAINT_EXPRESSION_FIELD = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-expression input.dijitInputInner");

    private static final By CONSTRAINT_EXPRESSION_VALIDATION_MSG = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-expression .validation-message");

    private static final By CONSTRAINT_REQUIRES_MATCH_FIELD = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-requires-match .dijitCheckBox");

    private static final By CONSTRAINT_MIN_LENGTH_FIELD = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-min-length input.dijitInputInner");

    private static final By CONSTRAINT_MIN_LENGTH_VALIDATION_MSG = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-min-length .validation-message");

    private static final By CONSTRAINT_MAX_LENGTH_FIELD = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-max-length input.dijitInputInner");

    private static final By CONSTRAINT_MAX_LENGTH_VALIDATION_MSG = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-max-length .validation-message");

    private static final By CONSTRAINT_MIN_VALUE_FIELD = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-min-value input.dijitInputInner");

    private static final By CONSTRAINT_MIN_VALUE_VALIDATION_MSG = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-min-value .validation-message");

    private static final By CONSTRAINT_MAX_VALUE_FIELD = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-max-value input.dijitInputInner");

    private static final By CONSTRAINT_MAX_VALUE_VALIDATION_MSG = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-max-value .validation-message");

    private static final By CONSTRAINT_ALLOWED_VALUES_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-allowed-values textarea");

    private static final By CONSTRAINT_ALLOWED_VALUES_VALIDATION_MSG = By
            .cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-allowed-values .validation-message");
    private static final By CONSTRAINT_SORTED_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-sorted .dijitCheckBox");
    private static final By CONSTRAINT_CLASS_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-class input.dijitInputInner");
    private static final By CONSTRAINT_CLASS_VALIDATION_MSG = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-constraint-class .validation-message");
    private static final By INDEXING_TEXT_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-indexing.text");
    private static final By INDEXING_BOOLEAN_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-indexing.boolean");

    private static final By INDEXING_NONTEXT_FIELD = By.cssSelector("#CMM_EDIT_PROPERTY_DIALOG .create-property-indexing.nontext");
    private static final String NEW_PROPERTY_SAVE_BUTTON = "#CMM_EDIT_PROPERTY_DIALOG span[widgetid=CMM_EDIT_PROPERTY_DIALOG_SAVE]";
    private static final String NEW_PROPERTY_SAVE_BUTTON_CLICKABLE = "#CMM_EDIT_PROPERTY_DIALOG_SAVE";
    private static final String NEW_PROPERTY_CANCEL_BUTTON = "#CMM_EDIT_PROPERTY_DIALOG span[widgetid=CMM_EDIT_PROPERTY_DIALOG_CANCEL]";
    private static final String NEW_PROPERTY_CANCEL_BUTTON_CLICKABLE = "#CMM_EDIT_PROPERTY_DIALOG_CANCEL";


    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.ShareDialogue#render(org.alfresco.webdriver.RenderTime)
     */
    @Override
    public EditPropertyPopUp render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);

        elementRender(
                timer,
                new RenderElement(ERROR_MSG_DIALOG,ElementState.INVISIBLE),
                getVisibleRenderElement(SHARE_DIALOGUE_HEADER),
                getVisibleRenderElement(NAME_FIELD),
                getVisibleRenderElement(TITLE_FIELD),
                getVisibleRenderElement(DESCRIPTION_FIELD),
                getVisibleRenderElement(DATATYPE_FIELD),
                getVisibleRenderElement(MANDATORY_FIELD),
                getVisibleRenderElement(MULTIPLE_FIELD),
                getVisibleRenderElement(CONSTRAINT_FIELD),
                getVisibleRenderElement(SHARE_DIALOGUE_CLOSE_ICON),
                getVisibleRenderElement(By.cssSelector(NEW_PROPERTY_CANCEL_BUTTON)));

        return this;
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
     * Checks if the Data type field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isDataTypeDisabled()
    {
        return isDisplayed(DATATYPE_FIELD_DISABLED);
    }

    /**
     * Checks if the Mandatory field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isMandatoryDisabled()
    {
        return isDisplayed(MANDATORY_FIELD_DISABLED);

    }

    /**
     * Checks if the Multiple check box field is Enabled
     * 
     * @return boolean true if field is Enabled
     */
    public boolean isMultipleCBDisabled()
    {
        return isDisplayed(MULTIPLE_FIELD_DISABLED);
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
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setTitleField(String value)
    {
        try
        {
            findAndWait(TITLE_FIELD).clear();
            findAndWait(TITLE_FIELD).sendKeys(value);
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
     * @return the CreateNewPropertyPopUp
     */
    public void setDescriptionField(String description)
    {
        try
        {
            findAndWait(DESCRIPTION_FIELD).clear();
            findAndWait(DESCRIPTION_FIELD).sendKeys(description);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DESCRIPTION_FIELD ", toe);
        }
    }

    /**
     * Gets the data type field.
     * 
     * @return the data type field
     */
    public String getDataTypeField()
    {
        SelectList list = new SelectList(driver, findAndWait(DATATYPE_FIELD));
        return list.getValue();
    }

    /**
     * Sets the data type field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setDataTypeField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            SelectList list = new SelectList(driver, findAndWait(DATATYPE_FIELD));
            if (!list.selectValue(value, true))
            {
                throw new PageOperationException("Could not set Mandatory Field: Value not found");
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DATATYPE_FIELD ", toe);
        }
    }

    /**
     * Gets the mandatory field.
     * 
     * @return the mandatory field
     */
    public String getMandatoryField()
    {
        SelectList list = new SelectList(driver, findAndWait(MANDATORY_FIELD));
        return list.getValue();
    }

    /**
     * Sets the mandatory field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setMandatoryField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            SelectList list = new SelectList(driver, findAndWait(MANDATORY_FIELD));
            if (!list.selectValue(value, true))
            {
                throw new PageOperationException("Could not set Mandatory Field: Value not found");
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: MANDATORY_FIELD ", toe);
        }
    }

    /**
     * Checks if the multiple field is selected.
     * 
     * @return true, if the multiple field is selected
     */
    public boolean isMultipleFieldSelected()
    {
        return isCheckBoxSelected(MULTIPLE_FIELD);
    }

    /**
     * Click the multiple field.
     * 
     * @return the void
     */
    public void clickMultipleField()
    {
        selectCheckBox(MULTIPLE_FIELD);
    }

    /**
     * Gets the default text value field.
     * 
     * @return the default text value field
     */
    public String getDefaultTextValueField()
    {
        return getValue(DEFAULT_VALUE_FIELD_TEXT);
    }

    /**
     * Sets the default text value field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setDefaultTextValueField(String value)
    {
        try
        {
            findAndWait(DEFAULT_VALUE_FIELD_TEXT).clear();
            findAndWait(DEFAULT_VALUE_FIELD_TEXT).sendKeys(value);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DEFAULT_VALUE_FIELD ", toe);
        }
    }

    /**
     * Gets the default number value field.
     * 
     * @return the default number value field
     */
    public String getDefaultNumberValueField()
    {
        return getValue(DEFAULT_VALUE_FIELD_NUMBER);
    }

    /**
     * Sets the default number value field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setDefaultNumberValueField(String value)
    {
        try
        {
            findAndWait(DEFAULT_VALUE_FIELD_NUMBER).clear();
            findAndWait(DEFAULT_VALUE_FIELD_NUMBER).sendKeys(value);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DEFAULT_VALUE_FIELD_NUMBER: ", toe);
        }
    }

    /**
     * Gets the default date value field.
     * 
     * @return the default date value field
     */
    public String getDefaultDateValueField()
    {
        return getValue(DEFAULT_VALUE_FIELD_DATE);
    }

    /**
     * Sets the default date value field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setDefaultDateValueField(String value)
    {
        try
        {
            findAndWait(DEFAULT_VALUE_FIELD_DATE).clear();
            findAndWait(DEFAULT_VALUE_FIELD_DATE).sendKeys(value);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: DEFAULT_VALUE_FIELD_DATE: ", toe);
        }
    }

    /**
     * Gets the default boolean value field.
     * 
     * @return the default boolean value field
     */
    public String getDefaultBooleanValueField()
    {
        SelectList list = new SelectList(driver, findAndWait(DEFAULT_VALUE_FIELD_BOOLEAN));
        return list.getValue();
    }

    /**
     * Sets the default boolean value field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setDefaultBooleanValueField(String value)
    {
        if ("true".equalsIgnoreCase(value))
        {
            try
            {
                WebElement booleanDefault = findAndWait(DEFAULT_VALUE_FIELD_BOOLEAN);
                SelectList list = new SelectList(driver, booleanDefault);
                list.selectValue("True", true);
                booleanDefault.sendKeys(Keys.TAB);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: DEFAULT_VALUE_FIELD_BOOLEAN", toe);
            }
        }
    }

    /**
     * Gets the constraint field.
     * 
     * @return the constraint field
     */
    public String getConstraintField()
    {
        SelectList list = new SelectList(driver, findAndWait(CONSTRAINT_FIELD));
        return list.getValue();
    }

    /**
     * Sets the constraint field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            SelectList list = new SelectList(driver, findAndWait(CONSTRAINT_FIELD));
            list.selectValue(value, true);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: CONSTRAINT_FIELD ", toe);
        }
    }

    /**
     * Gets the constraint expression field.
     * 
     * @return the constraint expression field
     */
    public String getConstraintExpressionField()
    {
        if (isElementDisplayed(CONSTRAINT_EXPRESSION_FIELD))
        {
            return getValue(CONSTRAINT_EXPRESSION_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint expression field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintExpressionField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_EXPRESSION_FIELD))
        {
            try
            {
                findAndWait(CONSTRAINT_EXPRESSION_FIELD).clear();
                findAndWait(CONSTRAINT_EXPRESSION_FIELD).sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_EXPRESSION_FIELD ", toe);
            }
        }
    }

    /**
     * Checks if the constraint requires match field is selected.
     * 
     * @return true, if the constraint requires match field is selected
     */
    public boolean isConstraintRequiresMatchFieldSelected()
    {
        return isCheckBoxSelected(CONSTRAINT_REQUIRES_MATCH_FIELD);
    }

    /**
     * Click the constraint requires match field.
     * 
     * @return the CreateNewPropertyPopUp
     */
    public void clickConstraintRequiresMatchField()
    {
        try
        {
            WebElement checkBoxParent = findAndWait(CONSTRAINT_REQUIRES_MATCH_FIELD);
            WebElement checkBox = checkBoxParent.findElement(By.cssSelector("input"));
            checkBox.click();
            if (!checkBox.isSelected())
            {
                throw new PageOperationException("Unable to select CheckBox: " + CONSTRAINT_REQUIRES_MATCH_FIELD);
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: CONSTRAINT_REQUIRES_MATCH_FIELD ", toe);
        }
    }

    /**
     * Gets the constraint min length field.
     * 
     * @return the constraint min length field
     */
    public String getConstraintMinLengthField()
    {
        if (isElementDisplayed(CONSTRAINT_MIN_LENGTH_FIELD))
        {
            return getValue(CONSTRAINT_MIN_LENGTH_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint min length field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintMinLengthField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_MIN_LENGTH_FIELD))
        {
            try
            {
                WebElement field = findAndWait(CONSTRAINT_MIN_LENGTH_FIELD);
                field.clear();
                field.sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_MIN_LENGTH_FIELD ", toe);
            }
        }
    }

    /**
     * Gets the constraint max length field.
     * 
     * @return the constraint max length field
     */
    public String getConstraintMaxLengthField()
    {
        if (isElementDisplayed(CONSTRAINT_MAX_LENGTH_FIELD))
        {
            return getValue(CONSTRAINT_MAX_LENGTH_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint max length field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintMaxLengthField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_MAX_LENGTH_FIELD))
        {
            try
            {
                WebElement field = findAndWait(CONSTRAINT_MAX_LENGTH_FIELD);
                field.clear();
                field.sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_MAX_LENGTH_FIELD ", toe);
            }
        }
    }

    /**
     * Gets the constraint min value field.
     * 
     * @return the constraint min value field
     */
    public String getConstraintMinValueField()
    {
        if (isElementDisplayed(CONSTRAINT_MIN_VALUE_FIELD))
        {
            return getValue(CONSTRAINT_MIN_VALUE_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint min value field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintMinValueField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_MIN_VALUE_FIELD))
        {
            try
            {
                WebElement field = findAndWait(CONSTRAINT_MIN_VALUE_FIELD);
                field.clear();
                field.sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_MIN_VALUE_FIELD ", toe);
            }
        }
    }

    /**
     * Gets the constraint max value field.
     * 
     * @return the constraint max value field
     */
    public String getConstraintMaxValueField()
    {
        if (isElementDisplayed(CONSTRAINT_MAX_VALUE_FIELD))
        {
            return getValue(CONSTRAINT_MAX_VALUE_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint max value field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintMaxValueField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_MAX_VALUE_FIELD))
        {
            try
            {
                WebElement field = findAndWait(CONSTRAINT_MAX_VALUE_FIELD);
                field.clear();
                field.sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_MAX_VALUE_FIELD ", toe);
            }
        }
    }

    /**
     * Gets the constraint allowed values field.
     * 
     * @return the constraint allowed values
     */
    public String getConstraintAllowedValuesField()
    {
        if (isElementDisplayed(CONSTRAINT_ALLOWED_VALUES_FIELD))
        {
            return getValue(CONSTRAINT_ALLOWED_VALUES_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint allowed values field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintAllowedValuesField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_ALLOWED_VALUES_FIELD))
        {
            try
            {
                findAndWait(CONSTRAINT_ALLOWED_VALUES_FIELD).clear();
                findAndWait(CONSTRAINT_ALLOWED_VALUES_FIELD).sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_ALLOWED_VALUES_FIELD ", toe);
            }
        }
    }

    /**
     * Checks if the constraint sorted field is selected.
     * 
     * @return true, if the constraint sorted field is selected
     */
    public boolean isConstraintSortedFieldSelected()
    {
        return isCheckBoxSelected(CONSTRAINT_SORTED_FIELD);
    }

    /**
     * Click the constraint sorted field.
     * 
     * @return the CreateNewPropertyPopUp
     */
    public void clickConstraintSortedField()
    {
        try
        {
            WebElement checkBoxParent = findAndWait(CONSTRAINT_SORTED_FIELD);
            WebElement checkBox = checkBoxParent.findElement(By.cssSelector("input"));
            checkBox.click();
            if (!checkBox.isSelected())
            {
                throw new PageOperationException("Unable to select CheckBox: " + CONSTRAINT_SORTED_FIELD);
            }
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: CONSTRAINT_SORTED_FIELD ", toe);
        }
    }

    /**
     * Gets the constraint class field.
     * 
     * @return the constraint class field
     */
    public String getConstraintClassField()
    {
        if (isElementDisplayed(CONSTRAINT_CLASS_FIELD))
        {
            return getValue(CONSTRAINT_CLASS_FIELD);
        }
        return null;
    }

    /**
     * Sets the constraint class field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setConstraintClassField(String value)
    {
        if (isElementDisplayed(CONSTRAINT_CLASS_FIELD))
        {
            try
            {
                findAndWait(CONSTRAINT_CLASS_FIELD).clear();
                findAndWait(CONSTRAINT_CLASS_FIELD).sendKeys(value);
            }
            catch (TimeoutException toe)
            {
                throw new PageOperationException("Not visible Element: CONSTRAINT_CLASS_FIELD ", toe);
            }
        }
    }

    /**
     * Gets the indexing text field.
     * 
     * @return the indexing text field
     */
    public String getIndexingTextField()
    {
        SelectList list = new SelectList(driver, findAndWait(INDEXING_TEXT_FIELD));
        return list.getValue();
    }

    /**
     * Sets the indexing text field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setIndexingTextField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            WebElement indexingType = findAndWait(INDEXING_TEXT_FIELD);
            SelectList list = new SelectList(driver, indexingType);
            list.selectValue(value, true);
            indexingType.sendKeys(Keys.TAB);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: INDEXING_TEXT_FIELD ", toe);
        }
    }

    /**
     * Gets the indexing boolean field.
     * 
     * @return the indexing boolean field
     */
    public String getIndexingBooleanField()
    {
        SelectList list = new SelectList(driver, findAndWait(INDEXING_BOOLEAN_FIELD));
        return list.getValue();
    }

    /**
     * Sets the indexing boolean field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setIndexingBooleanField(String value)
    {
        PageUtils.checkMandatoryParam("value", value);
        try
        {
            WebElement indexingType = findAndWait(INDEXING_BOOLEAN_FIELD);
            SelectList list = new SelectList(driver, indexingType);
            list.selectValue(value, true);
            indexingType.sendKeys(Keys.TAB);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: INDEXING_BOOLEAN_FIELD ", toe);
        }
    }

    /**
     * Gets the indexing non text field.
     * 
     * @return the indexing non text field
     */
    public String getIndexingNonTextField()
    {
        SelectList list = new SelectList(driver, findAndWait(INDEXING_NONTEXT_FIELD));
        return list.getValue();
    }

    /**
     * Sets the indexing non text field.
     * 
     * @param value the value
     * @return the CreateNewPropertyPopUp
     */
    public void setIndexingNonTextField(String value)
    {
    	PageUtils.checkMandatoryParam("value", value);
        try
        {
            WebElement indexingType = findAndWait(INDEXING_NONTEXT_FIELD);
            SelectList list = new SelectList(driver, indexingType);
            list.selectValue(value, true);
            indexingType.sendKeys(Keys.TAB);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: INDEXING_NONTEXT_FIELD ", toe);
        }
    }

    /**
     * Checks if the save button is enabled.
     * 
     * @return true, if the save button is enabled
     */
    public boolean isSaveButtonEnabled()
    {
        return isElementEnabled(By.cssSelector(NEW_PROPERTY_SAVE_BUTTON));
    }

    /**
     * Checks if the cancel button is enabled.
     * 
     * @return true, if the cancel button is enabled
     */
    public boolean isCancelButtonEnabled()
    {
        return isElementEnabled(By.cssSelector(NEW_PROPERTY_CANCEL_BUTTON));
    }

    /**
     * Select create button.
     * 
     * @return the model manager page
     */
    public HtmlPage selectSaveButton()
    {
        try
        {
            findFirstDisplayedElement(By.cssSelector(NEW_PROPERTY_SAVE_BUTTON_CLICKABLE)).click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException | NoSuchElementException e)
        {
            LOGGER.error("Unable to select the create button: ", e);
            throw new PageOperationException("Unable to select the create button", e);
        }
    }

    /**
     * Select cancel button.
     * 
     * @return ManagePropertiesPage the model manager page
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            findFirstDisplayedElement(By.cssSelector(NEW_PROPERTY_CANCEL_BUTTON_CLICKABLE)).click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.trace("Unable to select the cancel button: ", e);
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
            LOGGER.trace("Unable to find the SHARE_DIALOGUE_HEADER: ", nse);
        }
        return "";
    }

    /**
     * Select close button.
     * 
     * @return ManagePropertiesPage the manage properties page
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
            LOGGER.trace("Unable to select the close button: ", e);
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
        return isDisplayed(NAME_VALIDATION_MSG);
    }

    /**
     * Checks if title validation message is displayed.
     * 
     * @return true, if title validation message is displayed
     */
    public boolean isTitleValidationMessageDisplayed()
    {
        return isDisplayed(TITLE_VALIDATION_MSG);
    }

    /**
     * Checks if description validation message is displayed.
     * 
     * @return true, if description validation message is displayed
     */
    public boolean isDescriptionValidationMessageDisplayed()
    {
        return isDisplayed(DESCRIPTION_VALIDATION_MSG);
    }

    /**
     * Checks if data type validation message is displayed.
     * 
     * @return true, if data type validation message is displayed
     */
    public boolean isDataTypeValidationMessageDisplayed()
    {
        return isDisplayed(DATATYPE_VALIDATION_MSG);
    }

    /**
     * Checks if mandatory validation message is displayed.
     * 
     * @return true, if mandatory validation message is displayed
     */
    public boolean isMandatoryValidationMessageDisplayed()
    {
        return isDisplayed(MANDATORY_VALIDATION_MSG);
    }

    /**
     * Checks if the default text value validation message is displayed.
     * 
     * @return true, if default text value validation message is displayed
     */
    public boolean isDefaultTextValueValidationMessageDisplayed()
    {
        return isDisplayed(DEFAULT_VALUE_VALIDATION_MSG_TEXT);
    }

    /**
     * Checks if the default number value validation message is displayed.
     * 
     * @return true, if default number value validation message is displayed
     */
    public boolean isDefaultNumberValueValidationMessageDisplayed()
    {
        return isDisplayed(DEFAULT_VALUE_VALIDATION_MSG_NUMBER);
    }

    /**
     * Checks if the default date value validation message is displayed.
     * 
     * @return true, if default date value validation message is displayed
     */
    public boolean isDefaultDateValueValidationMessageDisplayed()
    {
        return isDisplayed(DEFAULT_VALUE_VALIDATION_MSG_DATE);
    }

    /**
     * Checks if the default boolean value validation message is displayed.
     * 
     * @return true, if default boolean value validation message is displayed
     */
    public boolean isDefaultBooleanValueValidationMessageDisplayed()
    {
        return isDisplayed(DEFAULT_VALUE_VALIDATION_MSG_BOOLEAN);
    }

    /**
     * Checks if the constraint expression validation message is displayed.
     * 
     * @return true, if constraint expression validation message is displayed
     */
    public boolean isConstraintExpressionValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_EXPRESSION_VALIDATION_MSG);
    }

    /**
     * Checks if the constraint min length validation message is displayed.
     * 
     * @return true, if constraint min length validation message is displayed
     */
    public boolean isConstraintMinLengthValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_MIN_LENGTH_VALIDATION_MSG);
    }

    /**
     * Checks if the constraint max length validation message is displayed.
     * 
     * @return true, if constraint max length validation message is displayed
     */
    public boolean isConstraintMaxLengthValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_MAX_LENGTH_VALIDATION_MSG);
    }

    /**
     * Checks if the constraint min value validation message is displayed.
     * 
     * @return true, if constraint min value validation message is displayed
     */
    public boolean isConstraintMinValueValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_MIN_VALUE_VALIDATION_MSG);
    }

    /**
     * Checks if the constraint max value validation message is displayed.
     * 
     * @return true, if constraint max value validation message is displayed
     */
    public boolean isConstraintMaxValueValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_MAX_VALUE_VALIDATION_MSG);
    }

    /**
     * Checks if the constraint allowed values validation message is displayed.
     * 
     * @return true, if constraint allowed values validation message is displayed
     */
    public boolean isConstraintAllowedValuesValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_ALLOWED_VALUES_VALIDATION_MSG);
    }

    /**
     * Checks if the constraint class validation message is displayed.
     * 
     * @return true, if constraint class validation message is displayed
     */
    public boolean isConstraintClassValidationMessageDisplayed()
    {
        return isDisplayed(CONSTRAINT_CLASS_VALIDATION_MSG);
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

    /**
     * Utility to edit REGEX Constraint Details to the Create Property Page
     * 
     * @param constraintDetails
     */
    public void editRegexConstraint(ConstraintDetails constraintDetails)
    {
        setConstraintField(getValue(ConstraintTypes.REGEX.getListValue()));
        setConstraintExpressionField(constraintDetails.getValue());
        // SHA-961: Removal of Regtex Match Required option
        // if (constraintDetails.isMatchRequired())
        // {
        // clickConstraintRequiresMatchField();
        // }
    }

    /**
     * Utility to edit MinMax Length Constraint Details to the Create Property Page
     * 
     * @param constraintDetails
     */
    public void editLengthConstraint(ConstraintDetails constraintDetails)
    {
        setConstraintField(getValue(ConstraintTypes.MINMAXLENGTH.getListValue()));
        setConstraintMinLengthField(constraintDetails.getMinValue());
        setConstraintMaxLengthField(constraintDetails.getMaxValue());
    }

    /**
     * Utility to add MinMax Value Constraint Details to the Create Property Page
     * 
     * @param constraintDetails
     */
    public void editMinMaxValueConstraint(ConstraintDetails constraintDetails)
    {
        setConstraintField(getValue(ConstraintTypes.MINMAXVALUE.getListValue()));
        setConstraintMinValueField(constraintDetails.getMinValue());
        setConstraintMaxValueField(constraintDetails.getMaxValue());
    }

    /**
     * Utility to edit List Constraint Details to the Create Property Page
     * 
     * @param constraintDetails
     */
    public void editListConstraint(ConstraintDetails constraintDetails)
    {
        setConstraintField(getValue(ConstraintTypes.LIST.getListValue()));
        setConstraintAllowedValuesField(constraintDetails.getValue());
        if (constraintDetails.isSorted())
        {
            clickConstraintSortedField();
        }
    }

    /**
     * Utility to edit Java Class Constraint Details to the Create Property Page
     * 
     * @param constraintDetails
     */
    public void editJavaClassConstraint(ConstraintDetails constraintDetails)
    {
        setConstraintField(getValue(ConstraintTypes.JAVACLASS.getListValue()));
        setConstraintClassField(constraintDetails.getValue());
    }

    /**
     * Utility to edit None Constraint Details to the Edit Property Page
     * 
     * @param constraintDetails
     */
    public void editNoneClassConstraint(ConstraintDetails constraintDetails)
    {
        setConstraintField(getValue(ConstraintTypes.None.getListValue()));
        setConstraintClassField(constraintDetails.getValue());
    }

    /**
     * Sets the default value on a create popup.
     * 
     * @param createPropertyPopup the create property popup
     * @param dataType the data type
     * @param defaultValue the default value
     */
    public void setDefaultValue(DataType dataType, String defaultValue)
    {
        // SHA: 787, 1260: Removal of cm:content from the Property data types
        if (dataType.equals(DataType.Text) || dataType.equals(DataType.MlText) || dataType.equals(DataType.MlTextContent))
        {
            setDefaultTextValueField(defaultValue);
        }
        else if (dataType.equals(DataType.Int) || dataType.equals(DataType.Double) || dataType.equals(DataType.Float) || dataType.equals(DataType.Long))
        {
            setDefaultNumberValueField(defaultValue);
        }
        else if (dataType.equals(DataType.Date) || dataType.equals(DataType.DateTime))
        {
            setDefaultDateValueField(defaultValue);
        }
        else if (dataType.equals(DataType.Boolean))
        {
            setDefaultBooleanValueField(defaultValue);
        }
    }

    /**
     * Util to select the specified indexing option for the selected data type
     * 
     * @param indexingOption
     */
    public void setIndexingOption(IndexingOptions indexingOption)
    {
        PageUtils.checkMandatoryParam("IndexingOption", indexingOption);

        String selectOption = getValue(indexingOption.getListValue());

        if (!getIndexingOptionElement().selectValue(selectOption, true))
        {
            throw new PageOperationException("Unable to select the Indexing option: " + indexingOption);
        }
    }

    /**
     * Util to select the specified indexing option for the selected data type
     * 
     * @param indexingOption
     */
    public String getIndexingOption()
    {
        return getIndexingOptionElement().getValue();
    }

    private SelectList getIndexingOptionElement()
    {
        try
        {
            WebElement indexType = findFirstDisplayedElement(INDEX_FIELD);
            return new SelectList(driver, indexType);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not visible Element: Index Field", nse);
        }
    }

}