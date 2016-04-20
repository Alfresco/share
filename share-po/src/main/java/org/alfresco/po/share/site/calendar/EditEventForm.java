package org.alfresco.po.share.site.calendar;

import org.alfresco.po.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Marina.Nenadovets on 12.04.14.
 */
public class EditEventForm extends AbstractEventForm
{
    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(this.getClass());

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
}
