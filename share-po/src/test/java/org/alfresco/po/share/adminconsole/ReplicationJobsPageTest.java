package org.alfresco.po.share.adminconsole;

import static org.alfresco.po.share.adminconsole.replicationjobs.RepeatEveryValue.MINUTE;
import static org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobStatus.FAILED;
import static org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobStatus.NEW;
import static org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobStatus.RUNNING;
import static org.alfresco.po.share.site.document.ConfirmDeletePage.Action.Delete;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.adminconsole.replicationjobs.DeleteJobPage;
import org.alfresco.po.share.adminconsole.replicationjobs.NewReplicationJobPage;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJob;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobStatus;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobsPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.CompanyHome;
import org.alfresco.po.share.workflow.Content;
import org.alfresco.po.share.workflow.SelectContentPage;
import org.alfresco.po.share.workflow.Site;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class ReplicationJobsPageTest extends AbstractTest
{
    ReplicationJobsPage replicationJobsPage;
    ReplicationJob replicationJob;
    NewReplicationJobPage newReplicationJobPage;
    ReplicationJobStatus status;
    String simpleReplicationName = "Job_simple" + System.currentTimeMillis();
    String scheduledReplicationName = "Job_scheduled" + System.currentTimeMillis();
    String replicationDesc = "Desc_" + System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    boolean isJobExists;
    Date now;
    Calendar cal;

    String repeatEvery = "2";
    DocumentLibraryPage documentLibPage;
    List<String> payLoadNames;
    String jobName = "job" + System.currentTimeMillis();
    String jobName1 = jobName + "1";
    String folder1 = "folder1" + System.currentTimeMillis();
    String folder1_1 = folder1 + "2";
    String folder2 = "folder2" + System.currentTimeMillis();
    String transferName = "transfer_" + System.currentTimeMillis();
    String transferName1 = transferName + "1";

    @Test
    public void checkThatFactoryReturnsReplicationJobsPage() throws Exception
    {
        SharePage page = loginAs(username, password);
        page.getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        replicationJobsPage = drone.getCurrentPage().render();
    }

    @Test(dependsOnMethods = "checkThatFactoryReturnsReplicationJobsPage")
    public void testClickNewJobButton() throws Exception
    {
        newReplicationJobPage = replicationJobsPage.clickNewJob().render();
        assertTrue(newReplicationJobPage != null);
    }

    @Test(dependsOnMethods = "testClickNewJobButton")
    public void createSimpleReplicationJob() throws Exception
    {
        newReplicationJobPage.setName(simpleReplicationName);
        newReplicationJobPage.setDescription(replicationDesc);
        newReplicationJobPage.clickSave();
        replicationJobsPage = drone.getCurrentPage().render();
        assertTrue(replicationJobsPage != null);
    }

    @Test(dependsOnMethods = "createSimpleReplicationJob")
    public void testCheckSimpleJobFromJobsPage() throws Exception
    {
        status = replicationJobsPage.getJobStatus(simpleReplicationName);
        isJobExists = replicationJobsPage.isJobExists(simpleReplicationName);
        assertTrue(isJobExists && status.equals(NEW), "Job isn't displayed or status isn't correct");
    }

    @Test(dependsOnMethods = "testCheckSimpleJobFromJobsPage")
    public void testGetSimpleJobDetails() throws Exception
    {
        replicationJob = replicationJobsPage.getJobDetails(simpleReplicationName);
        status = replicationJob.getStatus();
        Date noDate = replicationJob.getScheduleStartDate();
        assertTrue(status.equals(NEW) && noDate == null);
    }

    @Test(dependsOnMethods = "testGetSimpleJobDetails")
    public void createReplicationJobWithSchedule() throws Exception
    {
        now = new Date();
        String dueDateAndTime = sdf.format(now);
        String[] date = dueDateAndTime.split(" ");
        String dueDate = date[0];
        String dueTime;

        //Adding two minutes to current time value and changing time format
        cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 2);
        sdf = new SimpleDateFormat("HH:mm");
        dueTime = sdf.format(cal.getTime());

        newReplicationJobPage = replicationJobsPage.clickNewJob();
        newReplicationJobPage.setName(scheduledReplicationName);
        newReplicationJobPage.setScheduling();
        newReplicationJobPage.setDueDate(dueDate);
        newReplicationJobPage.setTime(dueTime);
        newReplicationJobPage.setRepeatEveryField(repeatEvery);
        newReplicationJobPage.selectIntervalPeriod(MINUTE);
        newReplicationJobPage.setEnabled();
        newReplicationJobPage.clickSave();
        replicationJobsPage = drone.getCurrentPage().render();
        assertTrue(replicationJobsPage != null);
        replicationJob = replicationJobsPage.getJobDetails(scheduledReplicationName);
        assertTrue(replicationJob.getStatus().equals(NEW) || replicationJob.getStatus().equals(FAILED));
    }

    @Test(dependsOnMethods = "createReplicationJobWithSchedule")
    public void testCheckScheduledJobFromJobsPage() throws Exception
    {
        isJobExists = replicationJobsPage.isJobExists(scheduledReplicationName);
        status = replicationJobsPage.getJobStatus(scheduledReplicationName);
        assertTrue(isJobExists && (status.equals(NEW) || status.equals(FAILED)), "Job isn't displayed or status isn't correct");
    }

    @Test(dependsOnMethods = "testCheckScheduledJobFromJobsPage")
    public void testWaitForJobToStart() throws Exception
    {
        replicationJob = replicationJobsPage.getJobDetails(scheduledReplicationName);
        replicationJob.waitUntilJobStarts(drone).render();
        assertTrue(replicationJobsPage != null);
    }

    @Test(dependsOnMethods = "testWaitForJobToStart")
    public void testGetScheduledJobDetails() throws Exception
    {
        Date afterAddingTwoMins = new Date(now.getTime() + 120000);
        cal.setTime(afterAddingTwoMins);

        //setting seconds and millis to zeros as they are not displayed
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        afterAddingTwoMins = cal.getTime();
        replicationJob = replicationJobsPage.getJobDetails(scheduledReplicationName);
        status = replicationJob.getStatus();
        Date startDate = replicationJob.getScheduleStartDate();
        String repeatInt = replicationJob.getRepeatInterval();

        //No transfer target was configured - so status should be either failed or running
        assertTrue((status.equals(FAILED) || status.equals(RUNNING)) && startDate.equals(afterAddingTwoMins) && repeatInt.equals(repeatEvery + MINUTE.name) );
    }

    @Test(dependsOnMethods = "testGetScheduledJobDetails")
    public void testSelectFileAsPayload() throws Exception
    {
        String siteName = "site_" + System.currentTimeMillis();
        String jobName = "job" + System.currentTimeMillis();
        String fileName = "Doc" + System.currentTimeMillis();

        SharePage page = drone.getCurrentPage().render();
        SiteUtil.createSite(drone, siteName, "public");
        SitePage site = drone.getCurrentPage().render();
        documentLibPage = site.getSiteNav().selectSiteDocumentLibrary().render();

        // uploading new file.
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        contentPage.render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentPage.create(contentDetails).render();
        replicationJobsPage = page.getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        newReplicationJobPage = replicationJobsPage.clickNewJob().render();
        newReplicationJobPage.setName(jobName);
        newReplicationJobPage.setDescription(replicationDesc);
        SelectContentPage selectContentPage = newReplicationJobPage.clickSelect();
        Content content1 = new Content();
        content1.setName(fileName);
        content1.setFolder(false);
        Set<Content> site2Contents = new HashSet<>();
        site2Contents.add(content1);
        Site site2 = new Site();
        site2.setName(siteName);
        site2.setContents(site2Contents);
        Set<Site> sites = new HashSet<>();
        sites.add(site2);
        CompanyHome companyHome = new CompanyHome();
        companyHome.setSites(sites);
        selectContentPage.addItems(companyHome);
        selectContentPage.selectOKButton().render();
        newReplicationJobPage.clickSave();
        payLoadNames = replicationJobsPage.getJobDetails(jobName).getPayloadNames();
        assertTrue(payLoadNames.contains(fileName));
    }

    @Test(dependsOnMethods = "testSelectFileAsPayload")
    public void testSelectFoldersAsPayload() throws Exception
    {
        String jobName = "job_" + System.currentTimeMillis();
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repo = page.getNav().selectRepository().render();
        repo.getNavigation().selectCreateNewFolder().createNewFolder(folder1);
        repo.getNavigation().selectCreateNewFolder().createNewFolder(folder2);
        repo.getFileDirectoryInfo(folder1).clickOnTitle();
        repo.getNavigation().selectCreateNewFolder().createNewFolder(folder1_1);

        replicationJobsPage = page.getNav().getAdminConsolePage().navigateToReplicationJobs();
        newReplicationJobPage = replicationJobsPage.clickNewJob();
        newReplicationJobPage.setName(jobName);
        newReplicationJobPage.setDescription(replicationDesc);
        SelectContentPage selectContentPage = newReplicationJobPage.clickSelect();

        Content content1 = new Content();
        content1.setName(folder1);
        content1.setFolder(true);

        Content content2 = new Content();
        content2.setName(folder2);
        content2.setFolder(false);

        Content content3 = new Content();
        content3.setName(folder1_1);
        content3.setFolder(false);

        Set<Content> ff = new HashSet<>();
        ff.add(content3);
        content1.setContents(ff);

        Set<Content> contents = new HashSet<>();
        contents.add(content1);
        contents.add(content2);

        CompanyHome companyHome = new CompanyHome();
        companyHome.setContents(contents);
        selectContentPage.addItems(companyHome);
        selectContentPage.selectOKButton();

        newReplicationJobPage.clickSave();
        payLoadNames = replicationJobsPage.getJobDetails(jobName).getPayloadNames();
        assertTrue(payLoadNames.contains(folder2) && payLoadNames.contains(folder1_1));
    }

    @Test(dependsOnMethods = "testSelectFoldersAsPayload")
    public void testSelectTransferTarget() throws Exception
    {
        createTransferTarget(transferName1);
        SharePage page = drone.getCurrentPage().render();
        replicationJobsPage = page.getNav().getAdminConsolePage().navigateToReplicationJobs().render();
        newReplicationJobPage = replicationJobsPage.clickNewJob();
        newReplicationJobPage.setName(jobName);
        newReplicationJobPage.selectTransferTarget(drone,transferName1 );
        newReplicationJobPage.clickSave();
        replicationJobsPage.render();
        assertTrue(replicationJobsPage.getJobDetails(jobName).getTransferTargetName().equals(transferName1));
    }

    @Test(dependsOnMethods = "testSelectTransferTarget")
    public void testEditReplicationJob() throws Exception
    {
        String[] sourceToDelete = { folder1_1, folder2 };

        Content content1 = new Content();
        content1.setName(folder1);
        content1.setFolder(false);
        Set<Content> contents = new HashSet<>();
        contents.add(content1);
        CompanyHome companyHome = new CompanyHome();
        companyHome.setContents(contents);

        newReplicationJobPage = replicationJobsPage.getJobDetails(jobName).clickEditButton().render();
        newReplicationJobPage.setName(jobName1);
        newReplicationJobPage.deleteSourceItems(drone, sourceToDelete);
        newReplicationJobPage.selectPayLoad(drone, companyHome);
        newReplicationJobPage.deleteTransferTarget(drone);
        newReplicationJobPage.selectTransferTarget(drone, transferName1);
        newReplicationJobPage.clickSave();

        ReplicationJob theJob = replicationJobsPage.getJobDetails(jobName1);
        String actualName = theJob.getName();
        List<String> payLoadNames = theJob.getPayloadNames();
        String targetName = theJob.getTransferTargetName();
        assertTrue(actualName.equals(jobName1) && payLoadNames.contains(folder1) && !payLoadNames.contains(folder1_1) && !payLoadNames.contains(folder2)
            && targetName.equals(transferName1) && !targetName.equals(transferName));
    }

    @Test(dependsOnMethods = "testEditReplicationJob")
    public void testDeleteReplicationJob() throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        replicationJobsPage = page.getNav().getAdminConsolePage().navigateToReplicationJobs();
        DeleteJobPage deleteJobPage = replicationJobsPage.getJobDetails(jobName1).clickDeleteButton();
        deleteJobPage.selectAction(Delete);
        assertFalse(replicationJobsPage.isJobExists(jobName1));
    }

    private void createTransferTarget(String transferName) throws Exception
    {
        SharePage page = drone.getCurrentPage().render();
        RepositoryPage repositoryPage = page.getNav().selectRepository().render();
        repositoryPage = repositoryPage.getNavigation().selectDetailedView().render();
        repositoryPage.getFileDirectoryInfo(drone.getValue("system.folder.data.dictionary")).clickOnTitle().render();
        repositoryPage.getFileDirectoryInfo(drone.getValue("system.folder.transfers")).clickOnTitle().render();
        repositoryPage.getFileDirectoryInfo(drone.getValue("system.folder.transfer.target.groups")).clickOnTitle().render();
        repositoryPage.getFileDirectoryInfo(drone.getValue("system.folder.transfer.default.group")).clickOnTitle().render();
        NewFolderPage newFolderPage = repositoryPage.getNavigation().selectCreateNewFolder();
        newFolderPage.createNewFolderWithValidation(transferName);
        repositoryPage.getFileDirectoryInfo(transferName).selectViewFolderDetails().changeType(drone.getValue("file.transfer.target"));
    }
}
