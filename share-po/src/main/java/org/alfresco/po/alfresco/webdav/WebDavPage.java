package org.alfresco.po.alfresco.webdav;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * @author Sergey Kardash
 */
public class WebDavPage extends AdvancedWebDavPage
{

    public WebDavPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebDavPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebDavPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebDavPage render(final long time)
    {
        return render(new RenderTime(time));
    }
}
