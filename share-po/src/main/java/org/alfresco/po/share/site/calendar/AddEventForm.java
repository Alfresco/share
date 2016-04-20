package org.alfresco.po.share.site.calendar;

import org.alfresco.po.RenderTime;

/**
 * Add Event form page object
 * relating to Share site Calendar page
 * 
 * @author Marina Nenadovets
 */

public class AddEventForm extends AbstractEventForm
{
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
}
