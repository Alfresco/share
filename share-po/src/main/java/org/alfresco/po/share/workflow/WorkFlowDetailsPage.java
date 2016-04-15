package org.alfresco.po.share.workflow;

import org.alfresco.po.RenderTime;

/**
 * WorkFlow Details Page.
 * 
 * @author Ranjith Manyam
 * @since 1.7.1
 */
public class WorkFlowDetailsPage extends AbstractWorkFlowTaskDetailsPage
{

    @Override
    public WorkFlowDetailsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @Override
    public WorkFlowDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

}
