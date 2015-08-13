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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * @author Sergey Kardash
 */
public class InformationEventForm extends AbstractEventForm
{
    private Log logger = LogFactory.getLog(this.getClass());

    private final static By EDIT_BUTTON = By.cssSelector("button[id$='edit-button-button']");
    private final static By DELETE_BUTTON = By.cssSelector("button[id$='delete-button-button']");
    private final static By TAG = By.xpath("//div[text()='Tags:']/following-sibling::div");
    private final static By WHAT_DETAIL = By.xpath("//div[contains(text(),'What:')]/following-sibling::div");
    private final static By WHERE_DETAIL = By.xpath("//div[contains(text(),'Where:')]/following-sibling::div");
    private final static By DESCRIPTION_DETAIL = By.xpath("//div[contains(text(),'Description:')]/following-sibling::div");
    @RenderWebElement
    private final static By OK_BUTTON = By.cssSelector("button[id$='_defaultContainer-cancel-button-button']");
    @RenderWebElement
    private final static By START_DATE_TIME = By.cssSelector("div[id$='_defaultContainer-startdate']");
    @RenderWebElement
    private final static By END_DATE_TIME = By.cssSelector("div[id$='_defaultContainer-enddate']");
    private final static By RECURRENCE_DETAIL = By.xpath("//div[contains(text(),'Recurrence:')]/following-sibling::div");

    @SuppressWarnings("unchecked")
    @Override
    public InformationEventForm render(RenderTime timer)
    {

        basicRender(timer);

        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public InformationEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    /**
     * Method for click on 'Edit' on information event form
     *
     * @return EditEventForm
     */
    public EditEventForm clickOnEditEvent()
    {

        findAndWait(EDIT_BUTTON).click();
        if (logger.isDebugEnabled())
        {
            logger.info("Click edit event button");
        }
        return factoryPage.instantiatePage(driver, EditEventForm.class);
    }

    /**
     * Method for click on 'Delete' on information event form
     *
     * @return DeleteEventForm
     */
    public DeleteEventForm clickOnDeleteEvent()
    {
        findAndWait(DELETE_BUTTON).click();
        if (logger.isDebugEnabled())
        {
            logger.info("Click delete event button");
        }
        return factoryPage.instantiatePage(driver, DeleteEventForm.class);

    }

    /**
     * Method to verify whether Edit button is present
     *
     * @return boolean
     */
    public boolean isEditButtonPresent()
    {
        return isElementDisplayed(EDIT_BUTTON);

    }

    /**
     * Method to verify whether Delete button is present
     *
     * @return boolean
     */
    public boolean isDeleteButtonPresent()
    {
        boolean isPresent;
        isPresent = isElementDisplayed(DELETE_BUTTON);
        return isPresent;
    }

    /**
     * Method to retrieve tag added to Calendar Event
     *
     * @return String
     */
    public String getTagName()
    {
        try
        {
            String tagName = findAndWait(TAG).getText();
            if (!tagName.isEmpty())
                return tagName;
            else
                throw new IllegalArgumentException("Cannot find tag");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
    }

    /**
     * Method to retrieve what Detail added to Calendar Event
     *
     * @return String
     */
    public String getWhatDetail()
    {
        try
        {
            String whatDetail = findAndWait(WHAT_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find what Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the what Detail");
        }
    }

    /**
     * Method to retrieve where Detail added to Calendar Event
     *
     * @return String
     */
    public String getWhereDetail()
    {
        try
        {
            String whatDetail = findAndWait(WHERE_DETAIL).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find where Detail");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the where Detail");
        }
    }

    /**
     * Method to retrieve description Detail added to Calendar Event
     *
     * @return String
     */
    public String getDescriptionDetail()
    {
        try
        {
            String whatDetail = findAndWait(DESCRIPTION_DETAIL).getText();
            return whatDetail;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the description Detail");
        }
    }

    /**
     * Method for close information event form
     *
     * @return CalendarPage
     */
    public HtmlPage closeInformationForm()
    {
        findAndWait(OK_BUTTON).click();
        if (logger.isDebugEnabled())
        {
            logger.info("Click ok event button");
        }

        return getCurrentPage();
    }

    /**
     * Method to retrieve Start Date Time of event
     *
     * @return String
     * <br/><br/>author Bogdan.Bocancea
     */
    public String getStartDateTime()
    {
        try
        {
            String whatDetail = findAndWait(START_DATE_TIME).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find Start Date");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the Start Date");
        }
    }

    /**
     * Method to retrieve End Date Time of event
     *
     * @return String
     * <br/><br/>author Bogdan.Bocancea
     */
    public String getEndDateTime()
    {
        try
        {
            String whatDetail = findAndWait(END_DATE_TIME).getText();
            if (!whatDetail.isEmpty())
                return whatDetail;
            else
                throw new IllegalArgumentException("Cannot find End Date");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the End Date");
        }
    }

    /**
     * Method to verify whether Delete button is enabled
     *
     * @return boolean
     */
    public boolean isDeleteButtonEnabled()
    {
        try
        {
            return driver.findElement(DELETE_BUTTON).isEnabled();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Method to verify whether Delete button is enabled
     *
     * @return boolean
     */
    public boolean isOkButtonEnabled()
    {
        try
        {
            return driver.findElement(OK_BUTTON).isEnabled();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Method to verify whether Recurrence is present
     *
     * @return boolean
     */
    public boolean isRecurrencePresent()
    {
        try
        {
            return driver.findElement(RECURRENCE_DETAIL).isEnabled();
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Method to retrieve description Detail added to Calendar Event
     *
     * @return String
     */
    public String getRecurrenceDetail()
    {
        if (isRecurrencePresent())
        {
            try
            {
                String recurrenceDetail = findAndWait(RECURRENCE_DETAIL).getText();
                return recurrenceDetail;
            }
            catch (TimeoutException te)
            {
                throw new ShareException("Unable to retrieve the description Detail");
            }
        }
        else
        {
            return "";
        }
    }

}
