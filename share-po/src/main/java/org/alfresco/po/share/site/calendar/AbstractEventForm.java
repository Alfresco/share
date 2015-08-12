/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.calendar;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.ShareDialogue;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of Calendar Event form
 * 
 * @author Marina.Nenadovets
 */
public abstract class AbstractEventForm extends ShareDialogue
{
    private final static By FORM_TITLE = By.cssSelector("#eventEditPanel-dialog_h");
    protected static final By WHAT_FIELD = By.cssSelector("input[id$='eventEditPanel-title']");
    protected static final By WHERE_FIELD = By.cssSelector("input[id$='eventEditPanel-location']");
    protected static final By DESCRIPTION_FIELD = By.cssSelector("textarea[id$='eventEditPanel-description']");
    protected static final By ALL_DAY_CHECKBOX = By.cssSelector("input[id$='eventEditPanel-allday']");
    protected static final By START_DAY_FIELD = By.cssSelector("span[id$='eventEditPanel-startdate'] input");
    protected static final By START_TIME_FIELD = By.cssSelector("span[id$='eventEditPanel-starttime'] input");
    protected static final By END_DAY_FIELD = By.cssSelector("span[id$='eventEditPanel-enddate'] input");
    protected static final By END_TIME_FIELD = By.cssSelector("span[id$='eventEditPanel-endtime'] input");
    protected static final By OK_BUTTON = By.cssSelector("#eventEditPanel-ok-button");
    protected static final By START_DATE_PICKER = By.cssSelector("#calendarpicker-button");
    protected static final By END_DATE_PICKER = By.cssSelector("#calendarendpicker-button");
    protected static final By TAGS_FIELD = By.cssSelector("input[id$='eventEditPanel-tag-input-field']");
    protected static final By ADD_TAGS_BUTTON = By.cssSelector("button[id$='eventEditPanel-add-tag-button-button']");
    private final static String eventTag = "//span[text()='%s']/parent::a";

    @SuppressWarnings("unchecked")
    @Override
    public AbstractEventForm render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public AbstractEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    /**
     * Method to retrieve the title of a form
     * 
     * @return String
     */
    public String getTitle()
    {
        return findAndWait(FORM_TITLE).getText();
    }

    /**
     * Method to click on any element by its locator
     * 
     * @param locator
     */
    public void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    /**
     * Method to check if the element is displayed
     * 
     * @param locator
     * @return boolean
     */

    public boolean isDisplayed(By locator)
    {
        try
        {
            return findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to set String input in the field
     * 
     * @param input
     * @param value
     */

    public void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find the element");
        }
    }

    /**
     * Method to set What field
     * 
     * @param title
     */

    public void setWhatField(final String title)
    {
        setInput(findAndWait(WHAT_FIELD), title);
    }

    /**
     * Method to set Where field
     * 
     * @param title
     */

    public void setWhereField(final String title)
    {
        setInput(findAndWait(WHERE_FIELD), title);
    }

    /**
     * Method to set Description field
     * 
     * @param title
     */

    public void setDescriptionField(final String title)
    {
        setInput(findAndWait(DESCRIPTION_FIELD), title);
    }

    /**
     * Method to set All Day check box
     */
    public void setAllDayCheckbox()
    {
        try
        {
            driver.findElement(ALL_DAY_CHECKBOX).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find all-day checkbox button");
        }
    }

    /**
     * Method for clicking OK button
     */
    public void clickSave()
    {
        WebElement saveButton = findAndWait(OK_BUTTON);
        try
        {
            saveButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find Save button");
        }
    }

    /**
     * Method for clicking Start Date Picker button
     */
    public void clickStartDatePicker()
    {
        WebElement startDateButton = findAndWait(START_DATE_PICKER);
        try
        {
            startDateButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find start date button");
        }
    }

    /**
     * Method for clicking End Date Picker button
     */
    public void clickEndDatePicker()
    {
        WebElement endDateButton = findAndWait(END_DATE_PICKER);
        try
        {
            endDateButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find end date button");
        }
    }

    /**
     * Method to set start time
     * 
     * @param startTime
     */
    public void setStartTimeField(final String startTime)
    {
        setInput(findAndWait(START_TIME_FIELD), startTime);
    }

    /**
     * Method to set end time
     * 
     * @param endTime
     */
    public void setEndTimeField(final String endTime)
    {
        setInput(findAndWait(END_TIME_FIELD), endTime);
    }

    /**
     * Method to set tags
     * 
     * @param tags
     */
    public void setTagsField(final String tags)
    {
        setInput(findAndWait(TAGS_FIELD), tags);
    }

    /**
     * Method to remove tags
     * 
     * @param tags
     */
    public void removeTag(final String[] tags)
    {
        WebElement element;
        for (String tag : tags)
        {
            String tagXpath = String.format(eventTag, tag);
            try
            {
                element = findAndWait(By.xpath(tagXpath));
                element.click();
                waitUntilElementDisappears(By.xpath(tagXpath), 3000);
            }
            catch (NoSuchElementException e)
            {
                throw new PageException("Unable to find tag " + tag + "");
            }
        }
    }

    /**
     * Method for clicking OK button
     */
    public void clickAddTag()
    {
        WebElement addButton = findAndWait(ADD_TAGS_BUTTON);
        try
        {
            addButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find add (tags) button");
        }
    }

}
