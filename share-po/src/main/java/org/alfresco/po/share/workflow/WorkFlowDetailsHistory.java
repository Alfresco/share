package org.alfresco.po.share.workflow;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Representation of History rows on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class WorkFlowDetailsHistory
{
    private WorkFlowHistoryType type;
    private String completedBy;
    private DateTime completedDate;
    private String completedDateString;
    private WorkFlowHistoryOutCome outcome;
    private String comment;

    private static final By TYPE = By.cssSelector("td.yui-dt-col-name.yui-dt-first");
    private static final By COMPLETED_BY = By.cssSelector("td.yui-dt-col-owner");
    private static final By DATE_COMPLETED = By.cssSelector("td.yui-dt-col-id");
    private static final By OUTCOME = By.cssSelector("td.yui-dt-col-state");
    private static final By COMMENT = By.cssSelector("td.yui-dt-col-properties");

    public WorkFlowDetailsHistory(WebElement element)
    {
        try
        {
            type = WorkFlowHistoryType.getWorkFlowHistoryType(element.findElement(TYPE).getText());
            completedBy = element.findElement(COMPLETED_BY).getText();
            try
            {
                completedDate = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(element.findElement(DATE_COMPLETED).getText());
            }
            catch (IllegalArgumentException ie)
            {
                completedDate = null;
            }
            completedDateString = element.findElement(DATE_COMPLETED).getText();
            outcome = WorkFlowHistoryOutCome.getWorkFlowHistoryOutCome(element.findElement(OUTCOME).getText());
            comment = element.findElement(COMMENT).getText();
        }
        catch (NoSuchElementException nse)
        {
            type = null;
            completedBy = null;
            completedDate = null;
            completedDateString = null;
            outcome = null;
            comment = null;
        }
    }

    public WorkFlowHistoryType getType()
    {
        return type;
    }

    public String getCompletedBy()
    {
        return completedBy;
    }

    public DateTime getCompletedDate()
    {
        return completedDate;
    }

    public String getDueCompletedString()
    {
        return completedDateString;
    }

    public WorkFlowHistoryOutCome getOutcome()
    {
        return outcome;
    }

    public String getComment()
    {
        return comment;
    }
}
