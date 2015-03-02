package org.alfresco.po.share.site.calendar;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * @author Sergey Kardash
 */
public class DeleteEventForm extends AbstractEventForm
{

    private Log logger = LogFactory.getLog(this.getClass());

    private static final By DELETE_CONFIRM = By.cssSelector("span[class=button-group] span span button[id$='-button']");

    public DeleteEventForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteEventForm render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public DeleteEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteEventForm render(long time)
    {
        return render(new RenderTime(time));
    }

    public CalendarPage confirmDeleteEvent()
    {
        try
        {
            drone.findAndWait(DELETE_CONFIRM).click();
            logger.info("Click delete event confirmation button");
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate delete Event button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return new CalendarPage(drone);
    }
}
