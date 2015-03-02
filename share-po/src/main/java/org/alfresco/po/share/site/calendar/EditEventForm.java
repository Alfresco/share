package org.alfresco.po.share.site.calendar;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Marina.Nenadovets on 12.04.14.
 */
public class EditEventForm extends AbstractEventForm
{
    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(this.getClass());

    public EditEventForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditEventForm render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public EditEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditEventForm render(long time)
    {
        return render(new RenderTime(time));
    }
}
