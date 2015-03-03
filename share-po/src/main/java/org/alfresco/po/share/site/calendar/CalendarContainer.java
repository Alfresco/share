package org.alfresco.po.share.site.calendar;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Calendar Container page object
 * relating to Share site Calendar page
 * 
 * @author Sergey Kardash
 */
public class CalendarContainer extends AbstractCalendarContainer
{

    protected CalendarContainer(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CalendarContainer render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CalendarContainer render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CalendarContainer render(long time)
    {
        return render(new RenderTime(time));
    }
}
