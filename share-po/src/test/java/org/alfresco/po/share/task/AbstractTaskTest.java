package org.alfresco.po.share.task;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.workflow.NewWorkflowPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowFormDetails;
import org.alfresco.po.share.workflow.WorkFlowType;

/**
 * Purpose of this class is to abstract functions of Task related test.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public abstract class AbstractTaskTest extends AbstractTest
{

    protected String taskName;
    protected MyTasksPage myTasksPage;
    protected String siteName;
    protected DocumentLibraryPage documentLibraryPage;
    protected long maxPageLoadingTime = 20000;


    /**
     * 
     */
    public AbstractTaskTest()
    {
        super();
    }

    /**
     * @throws Exception
     * @throws InterruptedException
     */
    protected void createTask(String username, String password) throws Exception, InterruptedException
    {
        loginAs(username, password);
        myTasksPage = ((DashBoardPage) resolvePage(driver)).getNav().selectMyTasks().render();
        StartWorkFlowPage startWorkFlowPage = myTasksPage.selectStartWorkflowButton().render();
        NewWorkflowPage newWorkflowPage = ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();

        List<String> reviewers = new ArrayList<String>();
        reviewers.add(username);
        WorkFlowFormDetails formDetails = new WorkFlowFormDetails(siteName, taskName, reviewers);
        newWorkflowPage.startWorkflow(formDetails);
    }

}
