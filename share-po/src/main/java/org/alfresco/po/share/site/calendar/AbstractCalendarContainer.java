package org.alfresco.po.share.site.calendar;

import java.util.ArrayList;
import java.util.Arrays;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
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

    protected AbstractCalendarContainer(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public AbstractCalendarContainer render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click on any element by its locator
     * 
     * @param locator
     */
    public void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
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
            return drone.findAndWait(locator, 2000).isDisplayed();
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
            WebElement element = drone.findAndWait(By.xpath(dateXpath));
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
     * @param day
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
                    WebElement buttonToClick = drone.findAndWait(By.xpath(buttonToClickXpath));
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
     * @param month
     * @param day
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
                    WebElement buttonToClick = drone.findAndWait(By.xpath(buttonToClickXpath));
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
            WebElement monthElem = drone.findAndWait(By.xpath(CURRENT_MONTH));
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
            WebElement monthElem = drone.findAndWait(By.xpath(CURRENT_MONTH));
            String value = monthElem.getText();

            return value.split("\n")[1].split(" ")[1];
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to retrieve the year header control");
        }
    }

}
