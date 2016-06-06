package org.alfresco.po.share.workflow;


import org.alfresco.po.HtmlPage;

/**
 * A WorkFlow interface.
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public interface WorkFlow extends HtmlPage
{
    /**
     * Gets workflow subpage in the WorkFlow view.
     *
     * @param formDetails WorkFlowFormDetails
     * @return HtmlPage page object
     * @throws InterruptedException
     */
    HtmlPage startWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException;

    /**
     * Enters message in the message box.
     *
     * @param messageString String
     */
    void enterMessageText(String messageString);

    /**
     * Clicks on Select button for selecting reviewers
     */
    AssignmentPage selectReviewer();

    /**
     * Enters due date in the date box.
     *
     * @param date String
     */
    void enterDueDateText(String date);

    /**
     * Cancels creation of the workflow.
     *
     * @param formDetails WorkFlowFormDetails
     * @return HtmlPage page object
     * @throws InterruptedException
     */
    HtmlPage cancelCreateWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException;
}
