package org.alfresco.po.share.bulkimport;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sergey Kardash on 5/19/14.
 */
@SuppressWarnings("unused")
public class StatusBulkImportPage extends AdvancedBulkImportPage
{
    private Log logger = LogFactory.getLog(StatusBulkImportPage.class);

    public StatusBulkImportPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StatusBulkImportPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StatusBulkImportPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public StatusBulkImportPage render(final long time)
    {
        return render(new RenderTime(time));
    }
}
