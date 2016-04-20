package org.alfresco.po.share.site.calendar;

import org.alfresco.po.RenderTime;

/**
 * Calendar Container page object
 * relating to Share site Calendar page
 * 
 * @author Sergey Kardash
 */
public class CalendarContainer extends AbstractCalendarContainer
{

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
}
