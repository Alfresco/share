package org.alfresco.po.alfresco.webdav;

import org.alfresco.po.RenderTime;

/**
 * @author Sergey Kardash
 */
public class WebDavPage extends AdvancedWebDavPage
{
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

}
