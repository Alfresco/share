package org.alfresco.po.share.workflow;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * enum class that holds all elements of start workflow drop down contents.
 * 
 * @author Abhijeet Bharade
 * @since 1.6.2
 */
public enum WorkFlowType
{

    CLOUD_TASK_OR_REVIEW("Cloud Task or Review"),
    NEW_WORKFLOW("New Task"),
    GROUP_REVIEW_AND_APPROVE("Review and Approve (group review)"),
    SEND_DOCS_FOR_REVIEW("Review and Approve (one or more reviewers)"),
    POOLED_REVIEW_AND_APPROVE("Review and Approve (pooled review)"),
    REVIEW_AND_APPROVE("Review And Approve (single reviewer)"),
    LIFECYCLE_REVIEW_AND_APPROVE("Lifecycle Review & Approve"),
    ADHOC_WORKFLOW("Adhoc Workflow (JBPM)"),
    GROUP_REVIEW_AND_APPROVE_JBPM("Group Review And Approve (JBPM)"),
    PARALLEL_REVIEW_AND_APPROVE("Parallel Review And Approve (JBPM)"),
    POOLED_REVIEW_AND_APPROVE_JBPM("Pooled Review And Approve (JBPM)"),
    HANDLE_CONTACT_REQUEST("Handle Contact Request"),
    REVIEW_AND_APPROVE_JBPM("Review And Approve (JBPM)");

    private String title;

    WorkFlowType(String desc)
    {
        this.title = desc;
    }

    public String getTitle()
    {
        return title;
    }

    protected WebElement getTaskTypeElement(WebDriver driver)
    {
        By dropDown = By.cssSelector("div[id$='default-workflow-definition-menu'] li span.title");
        List<WebElement> liElements = driver.findElements(dropDown);
        for (WebElement liElement : liElements)
        {
            String elementText = liElement.getText().trim();
            if (elementText.equalsIgnoreCase(this.title.trim()))
            {
                return liElement;
            }
        }
        return null;
    }

    public static WorkFlowType getWorkflowType(String type)
    {
        for (WorkFlowType workflow : WorkFlowType.values())
        {
            if (type.contains(workflow.toString()))
            {
                return workflow;
            }
        }
        throw new IllegalArgumentException("Workflow Type not able find for given name: " + type);
    }

    /**
     * Based title return {@link WorkFlowType}.
     * 
     * @param title String
     * @return {@link WorkFlowType}
     */
    public static WorkFlowType getWorkflowTypeByTitle(String title)
    {
        for (WorkFlowType workflow : WorkFlowType.values())
        {
            if (title.contains(workflow.getTitle()))
            {
                return workflow;
            }
        }
        throw new IllegalArgumentException("Workflow Type not able find for given Title: " + title);
    }

    public void setTitle(String newTitle)
    {
        this.title = newTitle;
    }
}
