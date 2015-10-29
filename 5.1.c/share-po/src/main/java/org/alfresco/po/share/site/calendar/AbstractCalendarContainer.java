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

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of Calendar Container for Calendar Event form
 * 
 * @author Sergey Kardash
 */
public class AbstractCalendarContainer extends AbstractEventForm
{
    private Log logger = LogFactory.getLog(this.getClass());

    private final static String DATE_BUTTON = "//table[@id='buttoncalendar']//a[text()='%s']";
    private final static String NEXT_MONTH__BUTTON = "//table[@id='buttoncalendar']//a[@class='calnavright']";
    private final static String PREVIOUS_MONTH_BUTTON = "//table[@id='buttoncalendar']//a[@class='calnavleft']";
    private final static String CURRENT_MONTH = "//table[@id='buttoncalendar']//div[@class='calheader']";


    @SuppressWarnings("unchecked")
    @Override
    public AbstractCalendarContainer render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public AbstractCalendarContainer render()
    {
        return render(new RenderTime(maxPageLoadingTime));
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
     * Method to set date
     * 
     * @param date
     */
    public void setDate(String date)
    {
        if (date == null)
        {
            throw new IllegalArgumentException("Date is required");
        }

        try
        {
            String dateXpath = String.format(DATE_BUTTON, date);
            WebElement element = findAndWait(By.xpath(dateXpath));
            element.click();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the date button ", te);
            }
        }
    }

    /**
     * @param month
     */
    public void setMonth(String month)
    {
        if (month == null)
        {
            throw new IllegalArgumentException("Month is required");
        }

        try
        {
            ArrayList<String> monthValues = new ArrayList<String>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August",
                    "September", "October", "November", "December"));

            String currentMonth = getMonthHeader();
            if (!currentMonth.equals(month))
            {

                String buttonToClickXpath = "";
                int count = monthValues.indexOf(month) - monthValues.indexOf(currentMonth);
                if (count > 0)
                    buttonToClickXpath = NEXT_MONTH__BUTTON;
                else
                    buttonToClickXpath = PREVIOUS_MONTH_BUTTON;

                for (int i = 0; i < Math.abs(count); i++)
                {
                    WebElement buttonToClick = findAndWait(By.xpath(buttonToClickXpath));
                    buttonToClick.click();

                }

            }

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the date button ", te);
            }
        }
    }

    /**
     * @param year
     */
    public void setYear(String year)
    {
        if (year == null)
        {
            throw new IllegalArgumentException("Month is required");
        }

        try
        {

            String currentYear = getYearHeader();
            if (!currentYear.equals(year))
            {

                String buttonToClickXpath = "";
                int count = Integer.parseInt(year) - Integer.parseInt(currentYear);
                if (count > 0)
                    buttonToClickXpath = NEXT_MONTH__BUTTON;
                else
                    buttonToClickXpath = PREVIOUS_MONTH_BUTTON;

                for (int i = 0; i < Math.abs(count) * 12; i++)
                {
                    WebElement buttonToClick = findAndWait(By.xpath(buttonToClickXpath));
                    buttonToClick.click();

                }

            }

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the date button ", te);
            }
        }
    }

    /**
     * @return month value from header
     */
    public String getMonthHeader()
    {

        try
        {
            WebElement monthElem = findAndWait(By.xpath(CURRENT_MONTH));
            String value = monthElem.getText();

            return value.split("\n")[1].split(" ")[0];
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to retrieve the month header control");
        }
    }

    /**
     * @return month value from header
     */
    public String getYearHeader()
    {

        try
        {
            WebElement monthElem = findAndWait(By.xpath(CURRENT_MONTH));
            String value = monthElem.getText();

            return value.split("\n")[1].split(" ")[1];
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to retrieve the year header control");
        }
    }

}
