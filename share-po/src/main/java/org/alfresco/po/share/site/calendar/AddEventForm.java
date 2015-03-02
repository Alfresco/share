package org.alfresco.po.share.site.calendar;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * Add Event form page object
 * relating to Share site Calendar page
 * 
 * @author Marina Nenadovets
 */

public class AddEventForm extends AbstractEventForm
{
    public AddEventForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddEventForm render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public AddEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AddEventForm render(long time)
    {
        return render(new RenderTime(time));
    }
}
