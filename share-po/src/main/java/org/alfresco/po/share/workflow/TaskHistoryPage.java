package org.alfresco.po.share.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * This page represents the Task History details page.
 * 
 * @author Chiran
 * @since 1.7.1
 */
public class TaskHistoryPage extends AbstractWorkFlowTaskDetailsPage
{
    //private static final By MY_TASKS_LIST_LINK = By.cssSelector("span>a[href*='workflows|active']");

    private static final By ALL_FIELD_LABELS = By.cssSelector("span[class$='viewmode-label']");

    private final Log logger = LogFactory.getLog(this.getClass());

    //private RenderElement myTasksListLink = getVisibleRenderElement(MY_TASKS_LIST_LINK);

    @Override
    public TaskHistoryPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getMenuTitle(), getWorkflowDetailsHeader(), getFormFieldsElements());
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @Override
    public TaskHistoryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    /**
     * Method to get All labels from Workflow Form
     * 
     * @return List<String>
     */
    public List<String> getAllLabels()
    {
        List<String> labels = new ArrayList<String>();
        try
        {
            List<WebElement> webElements = driver.findElements(ALL_FIELD_LABELS);
            for (WebElement label : webElements)
            {
                labels.add(label.getText());
            }
            return labels;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No labels found", nse);
            }
        }
        return labels;
    }
}
