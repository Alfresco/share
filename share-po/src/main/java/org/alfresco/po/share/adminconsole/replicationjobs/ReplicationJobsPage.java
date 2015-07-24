package org.alfresco.po.share.adminconsole.replicationjobs;

import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Class holds elements related to Replication Jobs share page
 *
 * @author Marina.Nenadovets
 */
public class ReplicationJobsPage extends AdminConsolePage
{
    private static final Logger logger = Logger.getLogger(ReplicationJobsPage.class);
    private final static By SUMMARY_PANEL = By.cssSelector(".summary-panel");
    private final static By JOBS_LIST_CONTAINER = By.cssSelector(".jobs-list-container");
    private final static By JOB_DETAIL_CONTAINER = By.cssSelector(".job-detail-container");
    private final static By CREATE_BUTTON = By.cssSelector("a[id$='default-create-button']");
    private final static String JOBS_LIST = ".jobs-list li";
    private final static String JOB_XPATH = ".//div[@class='jobs-list']//span[text()='%s']/..";


    /**
     * Instantiates a new admin console page(Replication Jobs page).
     *
     * @param drone WebDriver browser client
     */
    public ReplicationJobsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReplicationJobsPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(SUMMARY_PANEL),
            getVisibleRenderElement(JOBS_LIST_CONTAINER),
            getVisibleRenderElement(JOB_DETAIL_CONTAINER));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReplicationJobsPage render(long l)
    {
        checkArgument(l > 0);
        return render(new RenderTime(l));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReplicationJobsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to click new job button
     *
     * @return NewReplicationJobPage
     */
    public NewReplicationJobPage clickNewJob()
    {
        drone.findAndWait(CREATE_BUTTON).click();
        logger.info("New Job page is open");
        return new NewReplicationJobPage(drone).render();
    }

    private List<WebElement> getJobsWebElements()
    {
        return drone.findAndWaitForElements(By.cssSelector(JOBS_LIST + " a span"));
    }

    /**
     * Method to return the list of jobs' names
     *
     * @return List<String>
     */
    public List<String> getTheListOfJobs()
    {
        logger.info("Getting the list of jobs");
        List<String> jobsNames = new ArrayList<>();
        List<WebElement> allJobsWebElements = getJobsWebElements();
        for (WebElement allTheJobs : allJobsWebElements)
        {
            jobsNames.add(allTheJobs.getText());
        }
        return jobsNames;
    }

    /**
     * Method to check whether job exists in the list
     *
     * @param jobTitle String
     * @return true if present
     */
    public boolean isJobExists(String jobTitle)
    {
        List<String> allJobs = getTheListOfJobs();
        return allJobs.contains(jobTitle);
    }

    /**
     * Method to get replication job details
     *
     * @param jobTitle String
     * @return ReplicationJob
     */
    public ReplicationJob getJobDetails(String jobTitle)
    {
        logger.info("Getting job's details");
        WebElement job = null;
        List<WebElement> allJobs = getJobsWebElements();
        for (WebElement theJob : allJobs)
        {
            if (theJob.getText().equals(jobTitle))
            {
                theJob.click();
                waitUntilAlert();
                drone.waitUntilElementPresent(By.cssSelector("div[id$='jobDetailContainer']"), 3000);
                job = drone.findAndWait(By.cssSelector("div[id$='jobDetailContainer']"));
            }
        }
        return new ReplicationJob(drone, job);
    }

    /**
     * Method to get job status from jobs' list
     *
     * @param jobTitle String
     * @return ReplicationJobStatus
     */
    public ReplicationJobStatus getJobStatus(String jobTitle)
    {
        logger.info("Getting jobs status");
        WebElement theJob = drone.findAndWait(By.xpath(String.format(JOB_XPATH, jobTitle)));
        String statusInString = theJob.getAttribute("class");
        for (ReplicationJobStatus theStatus : ReplicationJobStatus.values())
        {
            if (theStatus.toString().equalsIgnoreCase(statusInString))
            {
                logger.info("Jobs status is " + theStatus);
                return theStatus;
            }
        }
        throw new ShareException("No status available");
    }
}
