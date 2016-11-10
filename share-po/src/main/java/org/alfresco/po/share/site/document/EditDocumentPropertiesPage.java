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
package org.alfresco.po.share.site.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Edit document properties page object, holds all element of the HTML page
 * relating to share's edit document properties page.
 * 
 * @author Michael Suzuki
 * @author Meenal Bhave
 * @since 1.3.1
 */
@SuppressWarnings("unchecked")
public class EditDocumentPropertiesPage extends AbstractEditProperties
{
    private static Log logger = LogFactory.getLog(EditDocumentPropertiesPage.class);
    
    public enum Fields
    {
        NAME, TITLE, DESCRIPTION, AUTHOR, PUBLISHER, CONTRIBUTOR, TYPE, IDENTIFIER, SOURCE, COVERAGE, RIGHTS, SUBJECT, SITE_CONFIGURATION, HOSTNAME
    }

    private String tagName;

    public EditDocumentPropertiesPage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        while (true)
        {
            timer.start();
            try
            {
                //If pop dialog
                if (isDisplayed(By.cssSelector("div[id$='-dialog']")))
                {
                    if (isEditPropertiesPopupVisible())
                    {
                        break;
                    }
                }
                else
                {
                    if (isEditPropertiesVisible() && isSaveButtonVisible())
                    {
                        if (tagName == null || tagName.isEmpty())
                        {
                            break;
                        }
                        else
                        {
                            if (isTagVisible(tagName))
                            {
                                break;
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;

    }

    /**
     * Check to see if tags are visible on the page
     * and match the given tag.
     * @param name identifier tag name
     * @return true if name matches tag
     */
    public boolean isTagVisible(String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new UnsupportedOperationException("Tag name required");
        }
        try
        {
            List<WebElement> tags = findAndWaitForElements(By.cssSelector("div.itemtype-tag"));
            for (WebElement tag : tags)
            {
                if (name.equalsIgnoreCase(tag.getText()))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Verify if edit properties element,
     * that contains the form is visible.
     * @return true if displayed
     */
    public boolean isEditPropertiesVisible()
    {
        return isDisplayed(By.cssSelector("div[id$='edit-metadata']"));
    }

    /**
     * Get value seen on the author input value.
     */
    public String getAuthor()
    {
        return getValue(INPUT_AUTHOR_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param author String name input
     */
    public void setAuthor(final String author)
    {
        setInput(driver.findElement(INPUT_AUTHOR_SELECTOR), author);
    }

    /**
     * Check if tags are attached to the particular document value.
     * @return true if tag elements are displayed
     */
    public boolean hasTags()
    {
        try
        {
            return driver.findElement(By.cssSelector("div.itemtype-tag")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Check if categories are attached to the particular document.
     * 
     * @return true if category elements are displayed
     */
    // public boolean hasCategories()
    // {
    // try
    // {
    // return driver.findElement(By.cssSelector("div[class*='itemtype-cm:category']")).isDisplayed();
    // }
    // catch (NoSuchElementException nse)
    // {
    // return false;
    // }
    // }

    /**
     * Get value seen on the resolution unit input value.
     */
    public String getResolutionUnit()
    {
        return getValue(INPUT_RESOLUTION_UNIT_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param unit String name input
     */
    public void setResolutionUnit(final String unit)
    {
        setInput(driver.findElement(INPUT_RESOLUTION_UNIT_SELECTOR), unit);
    }

    /**
     * Get value seen on the vertical resolution unit input value.
     */
    public String getVerticalResolution()
    {
        return getValue(INPUT_VERTICAL_RESOLUTION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param verticalResolution String name input
     */
    public void setVerticalResolution(final String verticalResolution)
    {
        setInput(driver.findElement(INPUT_VERTICAL_RESOLUTION_SELECTOR), verticalResolution);
    }

    /**
     * Get value seen on the orientation input value.
     */
    public String getOrientation()
    {
        return getValue(INPUT_ORIENTATION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param orientation String name input
     */
    public void setOrientation(final String orientation)
    {
        setInput(driver.findElement(INPUT_ORIENTATION_SELECTOR), orientation);
    }

    /**
     * Get the value of selected mime type
     * 
     * @return String value of select mime type
     */
    protected String getMimeType()
    {
        if (isShareDialogueDisplayed())
        {
            throw new UnsupportedOperationException("This operation is not supported");
        }
        WebElement selected = driver.findElement(By.cssSelector("select[id$='prop_mimetype'] option[selected='selected']"));
        return selected.getText();
    }

    /**
     * Selects a mime type from the dropdown by matching
     * the option displayed with the mimeType input.
     * 
     * @param mimeType String identifier as seen on the dropdown
     * @deprecated use selectMimeType(enum mimeType)
     */
    public void selectMimeType(final String mimeType)
    {
        WebElement dropDown = driver.findElement(By.cssSelector("select[id$='prop_mimetype']"));
        Select select = new Select(dropDown);
        select.selectByVisibleText(mimeType);
    }

    /**
     * Selects a mime type from the dropdown by matching
     * the option displayed with the mimeType input.
     * 
     * @param mimeType String identifier as seen on the dropdown
     */
    public void selectMimeType(final MimeType mimeType)
    {

        if (isShareDialogueDisplayed())
        {
            throw new UnsupportedOperationException("This operation is not supported");
        }
        WebElement dropDown = driver.findElement(By.cssSelector("select[id$='prop_mimetype']"));
        Select select = new Select(dropDown);
        String value = select.getFirstSelectedOption().getAttribute("value");
        select.selectByValue(mimeType.getMimeCode());
        String selected = select.getFirstSelectedOption().getAttribute("value");
        if (StringUtils.isEmpty(value) || value.equalsIgnoreCase(selected))
        {
            throw new PageOperationException(String.format("Select in dropdown failed, expected %s actual %s", mimeType.getMimeCode(), selected));
        }
    }

    /**
     * Verify the save is visible.
     * 
     * @return true if visible
     */
    public boolean isSaveButtonVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector("span.yui-button.yui-submit-button.alf-primary-button")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Clicks on save button.
     * 
     * @return {@link DocumentDetailsPage} page response
     */
    public HtmlPage selectSave()
    {
        clickSave();
        // WEBDRONE-523: Amended to return HtmlPage rather than DocumentDetailsPage
        return getCurrentPage();
    }

    /**
     * Clicks on save button.
     * 
     * @return {@link DocumentDetailsPage} page response
     */
    public HtmlPage selectSaveExpectError()
    {
    	clickOnSave();
        return getCurrentPage();
    }
    /**
     * Clicks on save button.
     * 
     * @return {@link DocumentDetailsPage} page response or {@link EditDocumentPropertiesPage} if there is a validation message.
     */
    public HtmlPage selectSaveWithValidation()
    {
        boolean validationPresent = false;
        validationPresent = isMessagePresent(INPUT_NAME_SELECTOR);
        validationPresent = validationPresent || isMessagePresent(INPUT_TITLE_SELECTOR);
        validationPresent = validationPresent || isMessagePresent(INPUT_DESCRIPTION_SELECTOR);
        validationPresent = validationPresent || isMessagePresent(INPUT_AUTHOR_SELECTOR);

        if (!validationPresent)
        {
            clickSave();
        }
        // WEBDRONE-523: Amended to return HtmlPage rather than DocumentDetailsPage
        return getCurrentPage();
    }

    /**
     * Select cancel button.
     * 
     * @return {@link DocumentDetailsPage} page response
     */
    public HtmlPage selectCancel()
    {
        clickOnCancel();
        // WEBDRONE-523: Amended to return HtmlPage rather than DocumentDetailsPage
        return getCurrentPage();
    }

    /**
     * Returns a map of validation messages for all the fields in the form.
     * 
     * @return The validation message or an empty string if there is no message.
     */
    public Map<Fields, String> getMessages()
    {
        Map<Fields, String> messages = new HashMap<>();

        String message = getMessage(INPUT_NAME_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.NAME, message);
        }

        message = getMessage(INPUT_TITLE_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.TITLE, message);
        }

        message = getMessage(INPUT_DESCRIPTION_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.DESCRIPTION, message);
        }

        message = getMessage(INPUT_AUTHOR_SELECTOR);
        if (message.length() > 0)
        {
            messages.put(Fields.AUTHOR, message);
        }

        return messages;
    }

    private String getMessage(By locator)
    {
        String message = "";
        try
        {
            message = getValidationMessage(locator);
        }
        catch (NoSuchElementException e)
        {
        }
        return message;
    }

    private boolean isMessagePresent(By locator)
    {
        try
        {
            String message = getValidationMessage(locator);
            if (message.length() > 0)
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Verify if edit properties element,
     * that contains the form is visible.
     * 
     * @return true if displayed
     */
    public boolean isEditPropertiesPopupVisible()
    {
        try
        {
            return !driver.findElements(By.tagName("form")).isEmpty();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
    }

    /**
     * click on All Properties button on Edit Properties pop-up
     * 
     * @return {@link EditDocumentPropertiesPage} page response
     */
    public HtmlPage selectAllProperties()
    {
        clickAllProperties();
        return getCurrentPage();
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param publisher String
     */
    public void setPublisher(String publisher)
    {
        setInput(driver.findElement(INPUT_PUBLISHER_SELECTOR), publisher);
    }

    /**
     * Get value seen on the Publisher input value.
     */
    public String getPublisher()
    {
        return getValue(INPUT_PUBLISHER_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param contributor String
     */
    public void setContributor(String contributor)
    {
        setInput(driver.findElement(INPUT_CONTRIBUTOR_SELECTOR), contributor);
    }

    /**
     * Get value seen on the Contributor input value.
     */
    public String getContributor()
    {
        return getValue(INPUT_CONTRIBUTOR_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param type String
     */
    public void setType(String type)
    {
        setInput(driver.findElement(INPUT_TYPE_SELECTOR), type);
    }

    /**
     * Get value seen on the Type input value.
     */
    public String getType()
    {
        return getValue(INPUT_TYPE_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param identifier String
     */
    public void setIdentifier(String identifier)
    {
        setInput(driver.findElement(INPUT_IDENTIFIER_SELECTOR), identifier);
    }

    /**
     * Get value seen on the Identifier input value.
     */
    public String getIdentifier()
    {
        return getValue(INPUT_IDENTIFIER_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param source String
     */
    public void setSource(String source)
    {
        setInput(driver.findElement(INPUT_SOURCE_SELECTOR), source);
    }

    /**
     * Get value seen on the Source input value.
     */
    public String getSource()
    {
        return getValue(INPUT_SOURCE_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param coverage String
     */
    public void setCoverage(String coverage)
    {
        setInput(driver.findElement(INPUT_COVERAGE_SELECTOR), coverage);
    }

    /**
     * Get value seen on the Coverage input value.
     */
    public String getCoverage()
    {
        return getValue(INPUT_COVERAGE_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param rights String
     */
    public void setRights(String rights)
    {
        setInput(driver.findElement(INPUT_RIGHTS_SELECTOR), rights);
    }

    /**
     * Get value seen on the Rights input value.
     */
    public String getRights()
    {
        return getValue(INPUT_RIGHTS_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     * 
     * @param subject String
     */
    public void setSubject(String subject)
    {
        setInput(driver.findElement(INPUT_SUBJECT_SELECTOR), subject);
    }

    /**
     * Get value seen on the Subject input value.
     */
    public String getSubject()
    {
        return getValue(INPUT_SUBJECT_SELECTOR);
    }

    /**
     * Option for folder.
     * Enters a value in the rendition configuration. Option enabled if WQS is installed.
     *
     * @param rendConfig String
     */
    public void setRenditionConfig(String rendConfig)
    {
        setInput(findAndWait(REDITION_CONFIG), rendConfig);
    }

    /**
     * Option for folder.
     * Get value seen on the rendition configuration. Option enabled if WQS is installed.
     */
    public String getRenditionConfig()
    {
        return getValue(REDITION_CONFIG);
    }
    
    /**
     * Enters a value in to the properties form.
     *
     * @param siteConfiguration String
     */
    public void setSiteConfiguration(String siteConfiguration)
    {
        setInput(driver.findElement(INPUT_SITE_CONFIGURATION_SELECTOR), siteConfiguration);
    }

    /**
     * Get text seen on the Site Configuration textarea.
     */
    public String getSiteConfiguration()
    {
        return driver.findElement(INPUT_SITE_CONFIGURATION_SELECTOR).getText();
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param Hostname String
     */
    public void setSiteHostname(String Hostname)
    {
        setInput(driver.findElement(INPUT_SITE_HOSTNAME_SELECTOR), Hostname);
    }

    /**
     * Get text seen on the Site Configuration textarea.
     */
    public String getSiteHostname()
    {
        return driver.findElement(INPUT_SITE_HOSTNAME_SELECTOR).getText();
    }

    /**
     * Gets the web assets visible on the dialog.
     *
     * @return List<String>
     */
    public List<String> getWebAssets()
    {
        WebElement assetsList = findAndWait(WEB_ASSETS_LIST);
        List<WebElement> assets = assetsList.findElements(By.cssSelector("div"));
        List<String> foundAssets = new ArrayList<>();

        for (WebElement asset : assets)
        {
            foundAssets.add(asset.getText());
        }
        return foundAssets;
    }
    
    /**
     * This method returns the input fields for dynamically added Properties through CMM
     * @param propertyName
     * @return {@link}WebElement
     * @author mbhave
     */
    private Map<String, WebElement> getInputFieldForProperty(String propertyName)
    {
        PageUtils.checkMandatoryParam("Specify appropriate Property Name", propertyName);
        
        String fieldType = "input";
        String fieldSelector = "div.form-field>" + fieldType + "[id$='" + propertyName + "']";
        
        Map<String, WebElement> formFieldInfo = new HashMap<String, WebElement>();

        // Find Field: Try TextInput first (text area)
        try
        {
            WebElement field = findFirstDisplayedElement(By.cssSelector(fieldSelector));
            if ("checkbox".equals(field.getAttribute("type")))
            {
                formFieldInfo.put("checkbox", field);
            }
            else
            {
                formFieldInfo.put("text", field);
            }
            return formFieldInfo;
        }
        catch (NoSuchElementException nse)
        {
            // No Text input field for this property
        }
        
        // Find Field: Try checkbox
        try
        {
            fieldSelector = "div.form-field>" + fieldType + "[id$='" + propertyName + "-entry']";
            
            WebElement field = findFirstDisplayedElement(By.cssSelector(fieldSelector));
            if ("checkbox".equals(field.getAttribute("type")))
            {
                formFieldInfo.put("checkbox", field);
            }
            else
            {
                formFieldInfo.put("text", field);
            }
            return formFieldInfo;
        }
        catch (NoSuchElementException nse)
        {
            // No Text input field for this property
        }
        
        // Find Field: Date Field 
        try
        {
            fieldSelector = "div.form-field>" + fieldType + "[id$='" + propertyName + "-cntrl-date']";
            
            WebElement field = findFirstDisplayedElement(By.cssSelector(fieldSelector));
            formFieldInfo.put("text", field);
            return formFieldInfo;
        }
        catch (NoSuchElementException nse)
        {
            // No Date input field for this property
        }
        
        // Find Field: Then try TextArea: (Description, Content)
        try
        {
            fieldType = "textarea";
            fieldSelector = "div.form-field>" + fieldType + "[id$='" + propertyName + "']";
            WebElement field = findFirstDisplayedElement(By.cssSelector(fieldSelector));
            formFieldInfo.put("textarea", field);
            return formFieldInfo;
        }
        catch (NoSuchElementException nse)
        {         
            // No Select List field for this property
        }

        // Find Field: Then try Select List
        try
        {
            fieldType = "select";
            fieldSelector = "div.form-field>" + fieldType + "[id$='" + propertyName + "']";
            WebElement field = findFirstDisplayedElement(By.cssSelector(fieldSelector));
            formFieldInfo.put("select", field);
            return formFieldInfo;
        }
        catch (NoSuchElementException nse)
        {         
            // No Select List field for this property
        }
        
        // Find Field: Then try Select List Multiple Entry
        try
        {
            fieldType = "select";
            fieldSelector = "div.form-field>" + fieldType + "[id$='" + propertyName + "_multilist-entry']";
            WebElement field = findFirstDisplayedElement(By.cssSelector(fieldSelector));
            formFieldInfo.put("multiselect", field);
            return formFieldInfo;
        }
        catch (NoSuchElementException nse)
        {         
            // No Select List field for this property
        }
        
        throw new PageException("No input field found on EditPropertiesPage for Property: " + propertyName);
    }
    
    /**
     * This method sets the property values for dynamically added Properties through CMM
     * @param Map<String, Object> properties: map of Property Names and corresponding values to be set
     * @return void
     * @author mbhave
     */
    public void setProperties(Map<String, Object> properties)
    {
        PageUtils.checkMandatoryParam("Expected Properties Map", properties);
        
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            String propertyName = entry.getKey();
            
            Map<String, WebElement> inputField = getInputFieldForProperty(propertyName);
            
            for (Map.Entry<String, WebElement> field : inputField.entrySet())
            {

                if ("text".equals(field.getKey()) || "textarea".equals(field.getKey()))
                {
                    setInput(field.getValue(), entry.getValue().toString());
                }
                else if ("checkbox".equals(field.getKey()))
                {
                    boolean currentValue = isCheckBoxSet(field.getValue());
                    boolean valueToBeSet = Boolean.parseBoolean(entry.getValue().toString());

                    if (valueToBeSet != currentValue)
                    {
                        field.getValue().click();
                    }
                }
                else if ("select".equals(field.getKey()))
                {
                    Select listElement = new Select(field.getValue());
                    listElement.selectByVisibleText(entry.getValue().toString());
                }
                else if ("multiselect".equals(field.getKey()))
                {
                    Select listElement = new Select(field.getValue());
                    for (String listValue : entry.getValue().toString().split(","))
                    {
                        listElement.selectByVisibleText(listValue);
                    }
                }
                else
                {
                    logger.error("Input type unknown / Not supported");
                }
            }

        }
    }

    private boolean isCheckBoxSet(WebElement checkBox)
    {
        boolean currentValue = false;
        try
        {
            currentValue = checkBox.getAttribute("value").equals("true");
        }
        catch(Exception e)
        {
            
        }
        return currentValue;
    }
    public boolean isErrorDialogDisplayed()
    {
        return isDisplayed(By.cssSelector("div#prompt"));
    }
    public HtmlPage clickOkError()
    {
        driver.findElement(By.xpath(".//div[@id='prompt']//button[text()='OK']")).click();
        return this;
    }
}
