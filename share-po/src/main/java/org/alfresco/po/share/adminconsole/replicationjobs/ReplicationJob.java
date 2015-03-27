package org.alfresco.po.share.adminconsole.replicationjobs;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds elements related to replication job details page
 *
 * @author Marina.Nenadovets
 */
public class ReplicationJob
{
    WebDrone drone;
    String name;
    String description;
    ReplicationJobStatus status;
    String schedule;
    String transferTargetName;
    List<String> payloadNames = new ArrayList<>();
    WebElement jobContainer;
    int timeToWait = 60;

    private static Log logger = LogFactory.getLog(ReplicationJob.class);
    private static final By NAME = By.cssSelector(".job-detail h2");
    private static final By DESCRIPTION = By.cssSelector(".job-detail h2 + div");
    private static final By JOB_STATUS = By.cssSelector(".job-status :nth-of-type(1)");
    private static final By SCHEDULE = By.cssSelector(".schedule");
    private static final By EDIT_BUTTON = By.cssSelector(".job-buttons button[id$='default-edit-button']");
    private static final By RUN_BUTTON = By.cssSelector(".job-buttons button[id$='default-run-button']");
    private static final By DELETE_BUTTON = By.cssSelector(".job-buttons button[id$='default-delete-button']");
    private static final By CANCEL_BUTTON = By.cssSelector(".job-buttons button[id$='default-cancel-button']");
    private static final By REFRESH_BUTTON = By.cssSelector("button[id$='default-refresh-button']");
    private static final By TRANSFER_TARGET_NAME = By.cssSelector(".transfer-target");
    private static final By PAYLOAD_NAME = By.cssSelector(".payload a");
    private static final String dateTimeRegex = "(0?[1-9]|[1-2][0-9]|3[01])\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+" +
        "(19[0-9]{2}|[2-9][0-9]{3}|[0-9]{2})\\s+(2[0-3]|[0-1][0-9]):([0-5][0-9])(?::(60|[0-5][0-9]))";

    /**
     * Constructor
     *
     * @param drone
     * @param jobContainer
     */
    protected ReplicationJob(WebDrone drone, WebElement jobContainer)
    {
        this.drone = drone;
        this.jobContainer = jobContainer;
        try
        {
            if (jobContainer.findElement(NAME).isDisplayed())
            {
                name = jobContainer.findElement(NAME).getText();
            }
            if (jobContainer.findElement(DESCRIPTION).isDisplayed())
            {
                description = jobContainer.findElement(DESCRIPTION).getText();
            }
            if (jobContainer.findElement(JOB_STATUS).isDisplayed())
            {
                String statusString = jobContainer.findElement(JOB_STATUS).getAttribute("class");
                for (ReplicationJobStatus jobStatus : ReplicationJobStatus.values())
                {
                    if (jobStatus.getValue().equalsIgnoreCase(statusString))
                        status = jobStatus;
                }
            }
            if (jobContainer.findElement(SCHEDULE).isDisplayed())
            {
                schedule = jobContainer.findElement(SCHEDULE).getText();
            }
            if (jobContainer.findElement(TRANSFER_TARGET_NAME).isDisplayed())
            {
                transferTargetName = jobContainer.findElement(TRANSFER_TARGET_NAME).getText();
            }
            if (jobContainer.findElement(PAYLOAD_NAME).isDisplayed())
            {
                List<WebElement> payloadWebElements = jobContainer.findElements(PAYLOAD_NAME);
                for (WebElement thePayloads : payloadWebElements)
                {
                    payloadNames.add(thePayloads.getText());
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.info("Some elements were not set");
        }
    }

    /**
     * Method to get start date of the job in format of "d MMM yyyy HH:mm:ss"
     * (from schedule section)
     *
     * @return Date
     * @throws ParseException
     */
    public Date getScheduleStartDate() throws ParseException
    {
        String dateInString;
        String schedule = getSchedule();
        Pattern p1 = Pattern.compile(dateTimeRegex);
        Matcher m1 = p1.matcher(schedule);
        if (m1.find())
            dateInString = m1.group();
        else
            return null;

        Date date = new SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.ENGLISH).parse(dateInString);
        return date;
    }

    /**
     * Method to get the date when job has started in 'd MMM yyyy HH:mm:ss' format
     *
     * @return Date
     * @throws Exception
     */
    public Date getDateStarted() throws Exception
    {
        String dateInString;
        String dateInStatus = jobContainer.findElement(JOB_STATUS).getText();
        Pattern p1 = Pattern.compile(dateTimeRegex);
        Matcher m1 = p1.matcher(dateInStatus);
        if (m1.find())
            dateInString = m1.group();
        else
            return null;

        Date date = new SimpleDateFormat("d MMM yyyy HH:mm:ss", Locale.ENGLISH).parse(dateInString);
        return date;
    }

    /**
     * Method to return repeat interval (i.e '2minutes')
     *
     * @return String
     */
    public String getRepeatInterval()
    {
        NewReplicationJobPage newReplicationJobPage = clickEditButton().render();
        int number = newReplicationJobPage.getRepeatEveryPeriod();
        String value = newReplicationJobPage.getIntervalPeriod();
        return Integer.toString(number) + value;
    }

    /**
     * Method to wait until job starts
     *
     * @param driver
     * @return ReplicationJobsPage
     * @throws ParseException
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    public ReplicationJobsPage waitUntilJobStarts(WebDrone driver) throws ParseException, InterruptedException
    {
        Date startDate = getScheduleStartDate();
        Date currTime = new Date(System.currentTimeMillis());
        logger.info("Current time is " + currTime);
        long whenStarts = startDate.getTime();
        long now = currTime.getTime();
        long timeToWait = whenStarts - now;
        logger.info("Waiting For: " + (timeToWait) / 1000 + " seconds");

        try
        {
        	synchronized (this)
            {
                try
                {
                    this.wait(1000L);
                }
                catch (InterruptedException e)
                {
                }
            };
            driver.refresh();
            driver.waitForElement(By.cssSelector(".replication"), 3);
            //driver.waitUntilElementPresent(By.cssSelector(ReplicationJobStatus.RUNNING.getCssSelector()), 3);
        }
        catch (IllegalArgumentException ia)
        {
            logger.info("The wait time is over - job has started");
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to click Edit button on replication job details page
     *
     * @return NewReplicationJobPage
     */
    public NewReplicationJobPage clickEditButton()
    {
        jobContainer.findElement(EDIT_BUTTON).click();
        return new NewReplicationJobPage(drone);
    }

    /**
     * Method to click 'Run' on job details
     *
     * @return ReplicationJob
     */
    public ReplicationJob clickRunButton() throws ParseException, InterruptedException
    {
        WebElement runButton = jobContainer.findElement(RUN_BUTTON);
        if (runButton.isEnabled())
        {
            runButton.click();
            drone.waitUntilElementPresent(By.cssSelector(".job-status " + ReplicationJobStatus.RUNNING.getCssSelector() +
                ",.job-status " + ReplicationJobStatus.FAILED.getCssSelector()), 7);
        }
        else
        {
            throw new ShareException("Run button isn't enabled");
        }
        return this;
    }

    /**
     * Method to click 'Cancel' on job details
     *
     * @return ReplicationJob
     */
    public ReplicationJob clickCancelButton()
    {
        WebElement calcelButton = jobContainer.findElement(CANCEL_BUTTON);
        if (calcelButton.isEnabled())
        {
            calcelButton.click();
            drone.waitUntilElementPresent(By.cssSelector(".job-status" + ReplicationJobStatus.CANCEL_REQUESTED.getCssSelector()), timeToWait);

        }
        else
        {
            throw new ShareException("Cancel button isn't enabled");
        }
        return this;
    }

    /**
     * Method to click 'Refresh' on job details
     *
     * @return ReplicationJob
     */
    public ReplicationJob clickRefresh()
    {
        WebElement refreshButton = jobContainer.findElement(REFRESH_BUTTON);
        if (refreshButton.isEnabled())
        {
            refreshButton.click();
            drone.waitUntilElementPresent(By.cssSelector(".job-status"), 3000);
        }
        else
        {
            throw new ShareException("Refresh button isn't enabled");
        }
        return this;
    }

    /**
     * Method to click 'Delete' button
     *
     * @return DeleteJobPage
     */
    public DeleteJobPage clickDeleteButton()
    {
        drone.find(DELETE_BUTTON).click();
        logger.info("The job is being deleted");
        return new DeleteJobPage(drone).render();
    }

    /**
     * Method to return job status
     *
     * @return ReplicationJobStatus
     */
    public ReplicationJobStatus getStatus()
    {
        return status;
    }

    private boolean isEnabled(By button)
    {
        return drone.find(button).isEnabled();
    }

    /**
     * Method to verify whether run button is enabled
     *
     * @return true if enable
     */
    public boolean isRunButtonEnabled()
    {
        return isEnabled(RUN_BUTTON);
    }

    /**
     * Method to verify whether Cancel button is enabled
     *
     * @return true if enabled
     */
    public boolean isCancelButtonEnabled()
    {
        return isEnabled(CANCEL_BUTTON);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTransferTargetName()
    {
        return transferTargetName;
    }

    public List<String> getPayloadNames()
    {
        return payloadNames;
    }

    public String getSchedule()
    {
        return schedule;
    }
}
