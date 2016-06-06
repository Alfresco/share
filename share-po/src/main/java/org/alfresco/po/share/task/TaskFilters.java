package org.alfresco.po.share.task;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.workflow.StartedFilter;
import org.alfresco.po.share.workflow.WorkFlowFilters;
import org.alfresco.po.share.workflow.WorkFlowType;

/**
 * @author Aliaksei Boole
 */
public class TaskFilters extends WorkFlowFilters
{

    public HtmlPage select(WorkFlowType workFlowType)
    {
        throw new PageOperationException("Not allowed here");
    }

    public HtmlPage select(StartedFilter startedFilter)
    {
        throw new PageOperationException("Not allowed here");
    }

    public HtmlPage select(AssignFilter assignFilter)
    {
        checkNotNull(assignFilter);
        findAndWait(assignFilter.by).click();
        waitUntilAlert();
        return getCurrentPage();
    }
}
