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
package org.alfresco.po.share.search;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object holds all elements of HTML page objects relating to
 * CreateNewFilterPopUpPage
 * 
 * @author Charu
 * @since 5.0
 */
public class CreateNewFilterPopUpPage extends SharePage
{

    private static final int MAXNIUM_FILTER_SIZE = 20;
    protected static final By FILTER_ID = By.cssSelector("div[class$='dijitInputField dijitInputContainer']>input[name='filterID']");
    private static final By FILTER_NAME = By.cssSelector("div[class$='dijitInputField dijitInputContainer']>input[name='displayName']");
    private static final By SORT_BY_DD_CTRL = By.cssSelector("table[id='FORM_SORTBY_CONTROL']>tbody>tr>td[class$='dijitArrowButtonContainer']>input");
    private static final By SORT_POP_UP = By.cssSelector("table[id='FORM_SORTBY_CONTROL_menu']");
    private static final By SORT_BY_DD_POPUPMENU_ITEM = By
            .cssSelector("table[id='FORM_SORTBY_CONTROL_menu']>tbody>tr[class='dijitReset dijitMenuItem']>td[class='dijitReset dijitMenuItemLabel']");
    private static final By SORT_BY_SELECTED = By.cssSelector("table[id='FORM_SORTBY_CONTROL']>tbody>tr>td>div>span");
    private static final By SITE_DD_CTRL = By
            .cssSelector("table[id^='alfresco_forms_controls_DojoSelect']>tbody>tr>td[class$='dijitArrowButtonContainer']>input");
    private static final By FILTER_AVAI_DD_CTRL = By.cssSelector("table[id='FORM_SCOPE_CONTROL']>tbody>tr>td[class$='dijitArrowButtonContainer']>input");
    private static final By FILTER_AVAI_POP_UP = By.cssSelector("table[id='FORM_SCOPE_CONTROL_menu']");
    private static final By FILTER_AVAI_DD_POPUPMENU_ITEM = By
            .cssSelector("table[id='FORM_SCOPE_CONTROL_menu']>tbody>tr[class='dijitReset dijitMenuItem']>td[class='dijitReset dijitMenuItemLabel']");
    private static final By FILTER_AVAI_SELECTED = By.cssSelector("table[id='FORM_SCOPE_CONTROL']>tbody>tr>td>div>span");
    private static final By SELECTED_SITE_DD = By.cssSelector("table[id^='alfresco_forms_controls_DojoSelect']>tbody>tr>td>div>span");
    private static final By SITE_POP_UP = By.cssSelector("div[id$='CONTROL_dropdown']+[id^='alfresco_forms_controls_DojoSelect']");
    private static final By POP_UP_SITES_DD = By
            .cssSelector("div[id$='CONTROL_dropdown']>table[id^='alfresco_forms_controls_DojoSelect']>tbody>tr[class='dijitReset dijitMenuItem']");
    private static final By SAVED_SITE_DISPLAY = By.cssSelector("div[class='read-display']");
    private static final By SAVE_SITE = By.cssSelector("div[class='button doneEditing']>img");
    private static final By FILTER_PROPERTY__DD_CTRL = By.cssSelector("div[id='widget_FORM_FACET_QNAME_CONTROL']>div>input[class$='dijitArrowButtonInner']");
    private static final By SELECTED_FILTER_PROPERTY = By.cssSelector("div[class$='dijitInputField dijitInputContainer']>input[id='FORM_FACET_QNAME_CONTROL']");
    private static final By POP_UP_MENU = By.cssSelector("div[id='widget_FORM_FACET_QNAME_CONTROL_dropdown']");
    private static final By FILTER_PROPERTY_DD_POPUPMENU_ITEM = By
            .cssSelector("div[id='FORM_FACET_QNAME_CONTROL_popup']>div[id^='FORM_FACET_QNAME_CONTROL_popup']");
    private static final By CREATE_FILTER_POPUP_TITLE_BAR = By.cssSelector("div[class='dijitDialogTitleBar']");
    private static final By NEW_FILTER_SAVE_OR_CANCEL_BUTTON = By.cssSelector("div[class='footer']>span[class$='alfresco-buttons-AlfButton dijitButton']>span");
    private static Log logger = LogFactory.getLog(CreateNewFilterPopUpPage.class);
    private static final By MIN_FILTER_LENGTH_UP_ARROW = By
            .cssSelector("div[id$='FORM_MIN_FILTER_VALUE_LENGTH']>div>div>div>div>div[class$='dijitUpArrowButton']>div");
    private static final By MIN_FITER_LENGTH = By
            .cssSelector("div[id$='FORM_MIN_FILTER_VALUE_LENGTH']>div>div>div>div>input[class='dijitReset dijitInputInner']");
    private static final By MAX_NO_OF_FILTERS_UP_ARROW = By.cssSelector("div[id$='FORM_MAX_FILTERS']>div>div>div>div>div[class$='dijitUpArrowButton']>div");
    private static final By MAX_NO_OF_FITERS = By.cssSelector("div[id$='FORM_MAX_FILTERS']>div>div>div>div>input[class='dijitReset dijitInputInner']");
    private static final By MIN_REQ_RESULTS_UP_ARROW = By.cssSelector("div[id$='FORM_HIT_THRESHOLD']>div>div>div>div>div[class$='dijitUpArrowButton']>div");
    private static final By MIN_REQ_RESULT = By.cssSelector("div[id$='FORM_HIT_THRESHOLD']>div>div>div>div>input[class='dijitReset dijitInputInner']");
    private static final By ADD_NEW_ENTRY = By.cssSelector("div[id='FORM_SCOPED_SITES']>div>div>div>div[class='button add']");

    @SuppressWarnings("unchecked")
    public CreateNewFilterPopUpPage render(RenderTime timer)
    {
        RenderElement actionMessage = getActionMessageElement(ElementState.INVISIBLE);
        elementRender(timer, getVisibleRenderElement(CREATE_FILTER_POPUP_TITLE_BAR), actionMessage);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFilterPopUpPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
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
    public String getFilterID()
    {
        return getValue(FILTER_ID);
    }

    /**
     * Send Filter ID in CreateNewFilterPopUpPage
     * 
     * @param filterID String
     * @return CreateNewFilterPopUpPage
     */

    public CreateNewFilterPopUpPage sendFilterID(String filterID)
    {
        PageUtils.checkMandatoryParam("filterID", filterID);
        try
        {
            findAndWait(FILTER_ID).sendKeys(filterID);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: FilterID" + toe.getMessage());
        }

    }

    /**
     * Send displayName in CreateNewFilterPopUpPage
     * 
     * @param filterName String
     * @return CreateNewFilterPopUpPage
     **/

    public CreateNewFilterPopUpPage sendFilterName(String filterName)
    {
        PageUtils.checkMandatoryParam("filterName", filterName);
        try
        {
            findAndWait(FILTER_NAME).sendKeys(filterName);
            return this;
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: filterName" + toe.getMessage());
        }

    }

    /**
     * Get the String value of name input value.
     */
    public String getFilterName()
    {
        return getValue(FILTER_NAME);
    }

    /**
     * Select Property from drop down in CreateNewFilterPopUpPage
     * 
     * @param property the propertyName
     * @return CreateNewFilterPopUpPage
     **/

    public CreateNewFilterPopUpPage selectFilterProperty(String property)
    {
        PageUtils.checkMandatoryParam("property", property);
        try
        {
            // Find the select control
            WebElement selectControl = findAndWait(FILTER_PROPERTY__DD_CTRL);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = findAndWait(POP_UP_MENU);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(FILTER_PROPERTY_DD_POPUPMENU_ITEM);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().equalsIgnoreCase(property))
                {
                    menuItem.click();
                    break;
                }
            }
            return this;

        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the property" + e);
            }
        }
        catch (TimeoutException exception)
        {
        }
        throw new PageOperationException("Unable to select the property : ");

    }

    /**
     * Get the String value of SelectedProperty in CreateNewFilterPopUpPage
     */
    public String getSelectedProperty()
    {
        return getValue(SELECTED_FILTER_PROPERTY);
    }

    /**
     * Select sort by from drop down in CreateNewFilterPopUpPage
     *
     * @param order the sort order
     */
    public CreateNewFilterPopUpPage selectSortBy(String order)
    {
        PageUtils.checkMandatoryParam("order", order);
        try
        {
            // Find the select control
            WebElement selectControl = findAndWait(SORT_BY_DD_CTRL);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = findAndWait(SORT_POP_UP);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(SORT_BY_DD_POPUPMENU_ITEM);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().contains(order))
                {
                    menuItem.click();
                    return this;

                }
            }

        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the sortby order" + ne);
            }
        }
        catch (TimeoutException e)
        {
        }
        throw new PageOperationException("Unable to select the sort order : ");

    }

    /**
     * Helper method to return true if Sort by field is displayed with expected sort
     * 
     * @return boolean <tt>true</tt> is Selected 'SortBy' is displayed
     */
    public boolean isSortByDisplayed(String sortBy)
    {
        PageUtils.checkMandatoryParam("sortBy", sortBy);
        try
        {
            if (findAndWait(SORT_BY_SELECTED).isDisplayed() && findAndWait(SORT_BY_SELECTED).getText().endsWith(sortBy))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Select save button in Create New Filter Page
     * 
     * @return {@link FacetedSearchConfigPage Page} page response
     */
    public HtmlPage selectSaveOrCancel(String buttonName)
    {
        PageUtils.checkMandatoryParam("buttonName", buttonName);
        try
        {
            // Get the list of buttons
            List<WebElement> buttonNames = findAndWaitForElements(NEW_FILTER_SAVE_OR_CANCEL_BUTTON);

            // Iterate list of buttons
            for (WebElement button : buttonNames)
            {
                if (button.getText().equalsIgnoreCase(buttonName) && (button.isDisplayed()))
                {
                    button.click();
                    waitUntilVisible(By.cssSelector("div.bd"), "Operation Completed Successfully", 10);
                    waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd"), "Operation Completed Successfully", 10);
                    return getCurrentPage();
                }
            }

        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the" + buttonName + "button" + e.getMessage());
            }
        }

        throw new PageOperationException("Unable to select the" + buttonName + "button");
    }

    /**
     * IncrimentMinimumFilterLength field in Create New Filter Page
     *
     * @param clickCount int number of clicks/ incremental count/ how many times the up arrow
     *        on min filter length field should be clicked
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage incrementMinimumFilterLength(int clickCount)
    {

        PageUtils.checkMandatoryParam("clickCount", clickCount);
        try
        {

            // Get the initial text in the minFilterLength field
            String initialminFilLength = getValue(MIN_FITER_LENGTH);

            // Convert string to integer value in the minFilterLength field
            int initialMinFilterVal = Integer.valueOf(initialminFilLength).intValue();

            if (!(initialMinFilterVal > MAXNIUM_FILTER_SIZE - 1) && !(clickCount + initialMinFilterVal > MAXNIUM_FILTER_SIZE))
            {
                // Find the select control
                WebElement selectControl = findAndWait(MIN_FILTER_LENGTH_UP_ARROW);

                for (int count = 1; count <= clickCount; count++)
                {
                    selectControl.click();
                    // Get the text in the minFilterLength field
                    String minFilLength = getValue(MIN_FITER_LENGTH);

                    // Convert string to integer value in the minFilterLength
                    // field
                    int MinFilterVal = Integer.valueOf(minFilLength).intValue();

                    if (MinFilterVal > MAXNIUM_FILTER_SIZE)
                    {
                        throw new PageOperationException("Unable to increment by" + clickCount + "since Minimum filter length exceeds maximum value"
                                + MAXNIUM_FILTER_SIZE);

                    }

                    // Get the final text in the minFilterLength field after
                    // incrementing
                    String finalMinFilterLength = getValue(MIN_FITER_LENGTH);

                    // Convert string to integer value in the minFilterLength
                    // field
                    int finalMinFilterVal = Integer.valueOf(finalMinFilterLength).intValue();

                    if (finalMinFilterVal == (clickCount + initialMinFilterVal))
                    {
                        return this;
                    }
                }
            }
            else
            {
                throw new PageOperationException("Unable to increment by" + clickCount + " since Minimum filter length exceeds maximum value"
                        + MAXNIUM_FILTER_SIZE);

            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to increment minimum filter length" + e.getMessage());
            }
        }
        throw new PageOperationException("Unable to increment minimum filter length");
    }

    /**
     * IncrimentMinimumFilterLength field in Create New Filter Page
     *
     * @param clickCount - number of clicks/ incremental count/ how many times the up arrow
     *        on min filter length field should be clicked
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage incrementMaxNumberOfFilters(int clickCount)
    {

        PageUtils.checkMandatoryParam("clickCount", clickCount);
        try
        {

            // Get the initial text in maxNumberOfFilters field
            String initialMaxNumberOfFilters = getValue(MAX_NO_OF_FITERS);

            // Convert string to integer value in themaxNumberOfFilters field
            int initialMaxNumberOfFiltersVal = Integer.valueOf(initialMaxNumberOfFilters).intValue();

            if (!(initialMaxNumberOfFiltersVal > MAXNIUM_FILTER_SIZE - 1) && !(clickCount + initialMaxNumberOfFiltersVal > MAXNIUM_FILTER_SIZE))
            {
                // Find the select control
                WebElement selectControl = findAndWait(MAX_NO_OF_FILTERS_UP_ARROW);

                for (int count = 1; count <= clickCount; count++)
                {
                    selectControl.click();
                    // Get the text in the maxNumberOfFilters field
                    String maxNumberOfFilters = getValue(MAX_NO_OF_FITERS);

                    // Convert string to integer value in the maxNumberOfFilters
                    // field
                    int maxNumberOfFiltersVal = Integer.valueOf(maxNumberOfFilters).intValue();

                    if (maxNumberOfFiltersVal > MAXNIUM_FILTER_SIZE)
                    {
                        throw new PageOperationException("Unable to increment by" + clickCount + " since Max No of filters lengt exceeds maximum value"
                                + MAXNIUM_FILTER_SIZE);

                    }

                    // Get the final text in the maxNumberOfFilters field after
                    // incrementing
                    String finalMaxNumberOfFilters = getValue(MAX_NO_OF_FITERS);

                    // Convert string to integer value in the maxNumberOfFilters
                    // field
                    int finalMaxNumberOfFiltersVal = Integer.valueOf(finalMaxNumberOfFilters).intValue();

                    if (finalMaxNumberOfFiltersVal == (clickCount + initialMaxNumberOfFiltersVal))
                    {
                        return this;
                    }
                }
            }
            else
            {
                throw new PageOperationException("Unable to increment by" + clickCount + " since Max No of filters lengt exceeds maximum value"
                        + MAXNIUM_FILTER_SIZE);

            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to increment MaxNumberOffilter" + e.getMessage());
            }
        }
        throw new PageOperationException("Unable to increment MaxNumberOfFilters");
    }

    /**
     * incrementMinimumRequiredResults field in Create New Filter Page
     *
     * @param clickCount - number of clicks/ incremental count/ how many times the up arrow
     *        on Number of Filters field should be clicked
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage incrementMinimumRequiredResults(int clickCount)
    {

        PageUtils.checkMandatoryParam("clickCount", clickCount);
        try
        {

            // Get the initial text in the MinReqResult field
            String initialMinReqResult = getValue(MIN_REQ_RESULT);

            // Convert string to integer value in the MinReqResult field
            int initialMinReqResultVal = Integer.valueOf(initialMinReqResult).intValue();

            if (!(initialMinReqResultVal > MAXNIUM_FILTER_SIZE - 1) && !(clickCount + initialMinReqResultVal > MAXNIUM_FILTER_SIZE))
            {
                // Find the select control
                WebElement selectControl = findAndWait(MIN_REQ_RESULTS_UP_ARROW);

                for (int count = 1; count <= clickCount; count++)
                {
                    selectControl.click();
                    // Get the text in the MinReqResult field
                    String minReqReult = getValue(MIN_REQ_RESULT);

                    // Convert string to integer value in the MinReqResult
                    // field
                    int minReqResultVal = Integer.valueOf(minReqReult).intValue();

                    if (minReqResultVal > MAXNIUM_FILTER_SIZE)
                    {
                        throw new PageOperationException("Unable to increment by" + clickCount + " since Min Req Result length exceeds maximum value"
                                + MAXNIUM_FILTER_SIZE);

                    }

                    // Get the final text in the MinReqResult field after
                    // incrementing
                    String finalMinReqResultLength = getValue(MIN_REQ_RESULT);

                    // Convert string to integer value in the MinReqResult
                    // field
                    int finalMinReqResultVal = Integer.valueOf(finalMinReqResultLength).intValue();

                    if (finalMinReqResultVal == (clickCount + initialMinReqResultVal))
                    {
                        return this;
                    }
                }
            }
            else
            {
                throw new PageOperationException("Unable to increment by" + clickCount + " since Min Req Result length exceeds maximum value"
                        + MAXNIUM_FILTER_SIZE);

            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to increment Minimum Required Result" + e.getMessage());
            }
        }
        throw new PageOperationException("Unable to increment by" + clickCount + " since Min Req Result length exceeds maximum value" + MAXNIUM_FILTER_SIZE);
    }

    /**
     * Select Filter Availability from drop down.
     *
     * @param availability - the Filter Availability
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage selectFilterAvailability(String availability)
    {
        PageUtils.checkMandatoryParam("availability", availability);
        try
        {
            // Find the select control
            WebElement selectControl = findAndWait(FILTER_AVAI_DD_CTRL);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = findAndWait(FILTER_AVAI_POP_UP);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(FILTER_AVAI_DD_POPUPMENU_ITEM);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().contains(availability))
                {
                    menuItem.click();
                    return this;
                }
            }

        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the filter availability" + ne);
            }
        }
        catch (TimeoutException e)
        {
        }
        throw new PageOperationException("Unable to select the filter availability");

    }

    /**
     * Helper method to return true if selected Filter Availability is displayed
     * 
     * @return boolean <tt>true</tt> if expected Filter Availability is displayed
     */
    public boolean isFilterAvailabiltyDisplayed(String availability)
    {
        PageUtils.checkMandatoryParam("availability", availability);
        try
        {
            if (findAndWait(FILTER_AVAI_SELECTED).isDisplayed() && findAndWait(FILTER_AVAI_SELECTED).getText().equalsIgnoreCase(availability))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Click Add New Entry button.
     * 
     * @return {@link CreateNewFilterPopUpPage Page} page response
     */
    public CreateNewFilterPopUpPage clickAddNewEntry()
    {
        try
        {
            // Get the list of buttons
            WebElement addNewEntryButton = findAndWait(ADD_NEW_ENTRY);

            // Iterate list of buttons
            if (addNewEntryButton.isDisplayed())
            {
                addNewEntryButton.click();
                return this;
            }

        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the new entry button" + e.getMessage());
            }
        }

        throw new PageOperationException("Unable to select the new entry button");
    }

    /**
     * Helper method to get the Minimum Filter Length
     * 
     * @return String
     */
    public int getMinFilterLength()
    {
        // Convert string to integer value in the minFilterLength field
        return Integer.valueOf(getValue(MIN_FITER_LENGTH)).intValue();
    }

    /**
     * Helper method to get the Minimum Filter Length
     * 
     * @return String
     */
    public int getMinReqResults()
    {
        // Convert string to integer value in the MinReqResults field
        return Integer.valueOf(getValue(MIN_REQ_RESULT)).intValue();
    }

    /**
     * Helper method to get the Max Number of Filters
     * 
     * @return String
     */
    public int getMaxNoOfFilters()
    {
        // Convert string to integer value in the MaxNoOfFilters field
        return Integer.valueOf(getValue(MAX_NO_OF_FITERS)).intValue();
    }

    /**
     * Helper method to return true if selected Site is displayed
     * 
     * @return boolean <tt>true</tt> if selected Site is displayed
     */
    public boolean isSelectedSiteDisplayed(String siteName)
    {
        PageUtils.checkMandatoryParam("siteName", siteName);
        try
        {
            if (findAndWait(SELECTED_SITE_DD).isDisplayed() && findAndWait(SELECTED_SITE_DD).getText().contains(siteName))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Select Site name from drop down.
     *
     * @param siteName String
     * @return CreateNewFilterPopUpPage
     */
    public CreateNewFilterPopUpPage selectSiteNameAndSave(String siteName)
    {

        PageUtils.checkMandatoryParam("siteName", siteName);
        try
        {
            // Find the select control
            WebElement selectControl = findAndWait(SITE_DD_CTRL);
            // WebElement selectCancel = findAndWait(CANCEL_SITE);

            selectControl.click();

            // Find the pop up menu
            WebElement popupMenu = findAndWait(SITE_POP_UP);

            // Get the pop up menu items
            List<WebElement> menuItems = popupMenu.findElements(POP_UP_SITES_DD);

            // Iterate pop up menu items
            for (WebElement menuItem : menuItems)
            {
                if (menuItem.getText().equalsIgnoreCase(siteName))
                {
                    menuItem.click();
                    WebElement selectSave = findAndWait(SAVE_SITE);
                    if (selectSave.isDisplayed() && selectSave.isEnabled())
                    {
                        selectSave.click();
                    }
                    return this;

                }
            }

        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the site from drop down" + ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to select the site from drop down" + e.getMessage());
            }
        }

        throw new PageOperationException("Unable to select the sitefrom drop down");
    }

    /**
     * Helper method to return true if Selected Site is displayed
     * 
     * @return boolean <tt>true</tt> is site is displayed
     */
    public boolean isSavedSiteDisplayed(String siteName)
    {
        PageUtils.checkMandatoryParam("siteName", siteName);
        try
        {
            if (findAndWait(SAVED_SITE_DISPLAY).isDisplayed() && findAndWait(SAVED_SITE_DISPLAY).getText().contains(siteName))
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

}
