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
import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.workflow.SelectContentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Abstract of edit properties
 *
 * @author Michael Suzuki
 * @since 1.4
 */
@SuppressWarnings("unchecked")
public abstract class AbstractEditProperties extends ShareDialogue
{

    protected static final By INPUT_NAME_SELECTOR = By.cssSelector("input[id$='prop_cm_name']");
    protected static final By INPUT_TITLE_SELECTOR = By.cssSelector("input[id$='prop_cm_title']");
    protected static final By INPUT_DESCRIPTION_SELECTOR = By.cssSelector("textarea[id$='prop_cm_description']");
    protected static final By INPUT_AUTHOR_SELECTOR = By.cssSelector("input[id$='prop_cm_author']");
    protected static final By INPUT_RESOLUTION_UNIT_SELECTOR = By.cssSelector("input[id$='prop_exif_resolutionUnit']");
    protected static final By INPUT_VERTICAL_RESOLUTION_SELECTOR = By.cssSelector("input[id$='_prop_exif_yResolution']");
    protected static final By INPUT_ORIENTATION_SELECTOR = By.cssSelector("input[id$='prop_exif_orientation']");
    protected static final By BUTTON_SELECT_TAG = By.cssSelector("div[id$='cntrl-itemGroupActions']");
    protected static final By CATEGORY_BUTTON_SELECT_TAG = By.cssSelector("div[id$='categories-cntrl-itemGroupActions']");
    protected static final By BUTTON_ALL_PROPERTIES = By.cssSelector("a[id$='editMetadata-button']");
    protected static final By CHECK_BOX_MODEL_ACTIVE = By.cssSelector("input[id$='_modelActive-entry']");
    protected static final By CHECK_BOX_WORKFLOW_DEPLOYED = By.cssSelector("input[id$='_definitionDeployed-entry']");
    protected static final By INPUT_PUBLISHER_SELECTOR = By.cssSelector("input[id$='prop_cm_publisher']");
    protected static final By INPUT_CONTRIBUTOR_SELECTOR = By.cssSelector("input[id$='prop_cm_contributor']");
    protected static final By INPUT_TYPE_SELECTOR = By.cssSelector("input[id$='prop_cm_type']");
    protected static final By INPUT_IDENTIFIER_SELECTOR = By.cssSelector("input[id$='prop_cm_identifier']");
    protected static final By INPUT_SOURCE_SELECTOR = By.cssSelector("input[id$='prop_cm_dcsource']");
    protected static final By INPUT_COVERAGE_SELECTOR = By.cssSelector("input[id$='prop_cm_coverage']");
    protected static final By INPUT_RIGHTS_SELECTOR = By.cssSelector("input[id$='prop_cm_rights']");
    protected static final By INPUT_SUBJECT_SELECTOR = By.cssSelector("input[id$='prop_cm_subject']");
    protected static final By INPUT_ENDPOINT_HOST_SELECTOR = By.cssSelector("input[id$='endpointhost']");
    protected static final By INPUT_ENDPOINT_PORT_SELECTOR = By.cssSelector("input[id$='endpointport']");
    protected static final By SELECT_BTN = By.cssSelector(".show-picker button");
    protected static final By TRANSFER_ENABLED = By.cssSelector("input[id$='enabled-entry']");
    protected static final By INPUT_SITE_CONFIGURATION_SELECTOR = By.cssSelector("textarea[id$='siteConfig']");
    protected static final By INPUT_SITE_HOSTNAME_SELECTOR = By.cssSelector("input[id$='hostName']");
    protected static final By WEB_ASSETS_LIST = By.cssSelector("div[id$='webassets-cntrl-currentValueDisplay']");
    protected static final By REDITION_CONFIG = By.cssSelector("textarea[id$='prop_ws_renditionConfig']");
    protected static final By INPUT_RECORD_LOCATION = By.cssSelector("input[id$='prop_rma_location']");
    protected static final By INPUT_EMAIL_ALIAS = By.cssSelector("input[id$='prop_emailserver_alias']");
    protected static final By BUTTON_SAVE = By.cssSelector("button[id$='form-submit-button']");
    /**
     * Clear the input field and inserts the new value.
     *
     * @param input {@link WebElement} represents the form input
     * @param value String input value to enter
     */
    public void setInput(final WebElement input, final String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Gets the value of the input field
     *
     * @param by input field descriptor
     * @return String input value
     */
    protected String getValue(By by)
    {
        return findAndWait(by).getAttribute("value");
    }

    /**
     * Get the String value of name input value.
     */
    public String getName()
    {
        return getValue(INPUT_NAME_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param name String name input
     */
    public void setName(final String name)
    {
        setInput(findAndWait(INPUT_NAME_SELECTOR), name);
    }

    /**
     * Get value seen on the title input value.
     */
    public String getDocumentTitle()
    {
        return getValue(INPUT_TITLE_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param title String name input
     */
    public void setDocumentTitle(final String title)
    {
        setInput(findAndWait(INPUT_TITLE_SELECTOR), title);
    }

    /**
     * Get value seen on the description input value.
     */
    public String getDescription()
    {
        return getValue(INPUT_DESCRIPTION_SELECTOR);
    }

    /**
     * Enters a value in to the properties form.
     *
     * @param description String name input
     */
    public void setDescription(final String description)
    {
        setInput(findAndWait(INPUT_DESCRIPTION_SELECTOR), description);
    }

    /**
     * Click on Select button to go to Tag page
     *
     * @return TagPage
     */
    public TagPage getTag()
    {
        WebElement tagElement = findAndWait(BUTTON_SELECT_TAG);
        tagElement.findElement(By.tagName("button")).click();
        return factoryPage.instantiatePage(driver, TagPage.class);
    }

    /**
     * Click on Select button to go to Category page
     *
     * @return CategoryPage
     */
    public CategoryPage getCategory()
    {
        WebElement tagElement = findAndWait(CATEGORY_BUTTON_SELECT_TAG);
        tagElement.findElement(By.tagName("button")).click();
        return factoryPage.instantiatePage(driver, CategoryPage.class);
    }

    /**
     * Get the {@link List} of added {@link Categories}.
     *
     * @return {@link List} of {@link Categories}
     * @deprecated Use {@link #getCategoryList()} instead.
     */
    @Deprecated
    public List<Categories> getCategories()
    {
        List<Categories> categories = new ArrayList<Categories>();
        try
        {
            List<WebElement> categoryElements = findAndWaitForElements(By.cssSelector("div[class='itemtype-cm:category']"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(Categories.getCategory(webElement.getText()));
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /**
     * Get the {@link List} of added categories.
     *
     * @return {@link List} of categories
     */
    public List<String> getCategoryList()
    {
        List<String> categories = new ArrayList<>();
        try
        {
            List<WebElement> categoryElements = findAndWaitForElements(By.cssSelector("div[class='itemtype-cm:category']"));
            for (WebElement webElement : categoryElements)
            {
                categories.add(webElement.getText());
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find categories", e);
        }
        return categories;
    }

    /**
     * Select cancel button.
     */
    public void clickOnCancel()
    {
        findAndWait(By.cssSelector("button[id$='form-cancel-button']")).click();
    }
    
    /**
     * Select cancel button.
     */
    public void clickOnSave()
    {
        findAndWait(BUTTON_SAVE).click();
        waitUntilAlert();
    }

    /**
     * Selects the save button that posts the form.
     */
    public void clickSave()
    {
        WebElement saveButton = findAndWait(By.cssSelector("button[id$='form-submit-button']"));
        if (saveButton.isDisplayed())
        {
        	submit(By.cssSelector("button[id$='form-submit-button']"), ElementState.DELETE_FROM_DOM);
        	//saveButton.click();
            waitUntilAlert();
        }
    }

    /**
     * Select all properties button.
     */
    public void clickAllProperties()
    {
        findAndWait(BUTTON_ALL_PROPERTIES).click();
    }

    /**
     * Method to set Model Active check box
     */

    public void setModelActive()
    {
        findAndWait(CHECK_BOX_MODEL_ACTIVE).click();

    }
    
    /**
     * Method to check if Model is Active
     */

    public boolean isModelActive()
    {
        try
        {
            return driver.findElement(CHECK_BOX_MODEL_ACTIVE).isSelected();
        }
        catch (TimeoutException te)
        {

        }
        return false;
    }

    /**
     * Method to set Workflow Deployed check box
     */

    public void setWorkflowDeployed()
    {
        findAndWait(CHECK_BOX_WORKFLOW_DEPLOYED).click();

    }

    public void setEndpointHost(String endpointHost)
    {
        setInput(findAndWait(INPUT_ENDPOINT_HOST_SELECTOR), endpointHost);
    }

    public void setEndpointPort(String endpointPort)
    {
        setInput(findAndWait(INPUT_ENDPOINT_PORT_SELECTOR), endpointPort);
    }

    @FindBy(css="input[id$='username']") TextInput usernameInput;
    public void setUserName(String username)
    {
        usernameInput.sendKeys(username);
    }

    @FindBy(css="input[id$='password']") TextInput passwordInput;
    public void setPassword(String password)
    {
        passwordInput.sendKeys(password);
    }

    public SelectContentPage clickSelect()
    {
        WebElement selectBtn = findAndWait(SELECT_BTN);
        selectBtn.click();
        return factoryPage.instantiatePage(driver, SelectContentPage.class);
    }

    public void selectTransferEnabled()
    {
        findAndWait(TRANSFER_ENABLED).click();
    }
    
    /**
     * Enters a value in to the record properties.
     *
     * @param location String location
     */
    public void setLocation(final String location)
    {
        setInput(findAndWait(INPUT_RECORD_LOCATION), location);
    }

    /**
     * Enters a value in to the record properties.
     *
     * @param alias
     */
    public void setEmailAlias(String alias)
    {
        setInput(findAndWait(INPUT_EMAIL_ALIAS), alias);
    }

}
