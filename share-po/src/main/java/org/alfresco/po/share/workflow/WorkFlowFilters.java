package org.alfresco.po.share.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * @author Aliaksei Boole
 */
public class WorkFlowFilters extends PageElement
{
    private static final Logger logger = Logger.getLogger(WorkFlowFilters.class);
    private final static String PRIORITY_XPATH_TEMPLATE = "//a[@rel='%s']";
    private final static String WORKFLOW_TYPE_TEMPLATE = "//a[text()='%s']";


    public HtmlPage select(StartedFilter startedFilter)
    {
        checkNotNull(startedFilter);
        driver.findElement(startedFilter.by).click();
        waitUntilAlert();
        return getCurrentPage();
    }

    public HtmlPage select(DueFilters dueFilters)
    {
        checkNotNull(dueFilters);
        driver.findElement(dueFilters.by).click();
        waitUntilAlert();
        return getCurrentPage();
    }

    public HtmlPage select(Priority priority)
    {
        checkNotNull(priority);
        By xpath = By.xpath(String.format(PRIORITY_XPATH_TEMPLATE, priority.getValue()));
        driver.findElement(xpath).click();
        waitUntilAlert();
        return getCurrentPage();
    }

    public HtmlPage select(WorkFlowType workFlowType)
    {
        checkNotNull(workFlowType);
        By xpath = By.xpath(String.format(WORKFLOW_TYPE_TEMPLATE, workFlowType.getTitle()));
        driver.findElement(xpath).click();
        waitUntilAlert();
        return getCurrentPage();
    }

    protected void waitUntilAlert()
    {
        final long WAIT_ALERT_PRESENT = 1; //hardcoded - possible temporary excess in most cases.
        try
        {
            By AlertMessage = By.xpath(".//*[@id='message']/div/span");
            waitUntilElementPresent(AlertMessage, WAIT_ALERT_PRESENT);
            waitUntilElementDeletedFromDom(AlertMessage, 3);
        }
        catch (TimeoutException ex)
        {
            if (logger.isDebugEnabled())
            {
                logger.error("Alert message hide quickly", ex);
            }
        }
    }
}
