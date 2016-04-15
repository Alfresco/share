/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.workflow;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page relating to the Adhoc Workflow page
 * load.
 *
 * @author Abhijeet Bharade
 * @since 1.6.2
 */
public class NewWorkflowPage extends WorkFlowPage
{
    @RenderWebElement
    private static final By DUE_DATE = By.cssSelector("input[id$='workflowDueDate-cntrl-date']");
    @RenderWebElement
    private static final By MESSAGE_TEXT = By.cssSelector("textarea[id$='prop_bpm_workflowDescription']");
    @RenderWebElement
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='-form-submit-button']");
    @RenderWebElement
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='-form-cancel-button']");
    @RenderWebElement
    private static final By PRIORITY_DROPDOWN = By.cssSelector("select[id$='_bpm_workflowPriority']");
    private static final By REQUIRED_APPROVAL = By.cssSelector("input[id$='requiredApprovePercent']");


    @SuppressWarnings("unchecked")
    @Override
    public NewWorkflowPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewWorkflowPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to fill in the details for form and submit. formDetails to get the
     * form details
     *
     * @return HtmlPage
     */
    @Override
    public HtmlPage startWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException
    {
        fillUpWorkflowForm(formDetails);
        AssignmentPage assignmentPage = selectReviewer().render();
        assignmentPage.selectReviewers(formDetails.getReviewers()).render();
        return submitWorkflow();
    }

    /**
     * Method clicks on the StartWorkFlow button
     */
    public HtmlPage submitWorkflow()
    {
        WebElement saveButton = findAndWait(SUBMIT_BUTTON);
        String saveButtonId = saveButton.getAttribute("id");
        saveButton.click();
        waitUntilElementDisappears(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
       // waitUntilElementDeletedFromDom(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    public HtmlPage doubleClickStartWorkflow()
    {
        try
        {
            WebElement saveButton = findAndWait(SUBMIT_BUTTON);
            doubleClickOnElement(saveButton);
            waitUntilElementDisappears(SUBMIT_BUTTON, 1);
        }
        catch (TimeoutException e)
        {
        }
        return getCurrentPage();
    }

    @Override
    protected WebElement getMessageTextareaElement()
    {
        return findAndWait(MESSAGE_TEXT);
    }

    /**
     * Returns the WebElement for Select reviewer button.
     *
     * @return WebElement
     */
    @Override
    protected WebElement getSelectReviewButton()
    {
        return findAndWait(By.xpath("//button[text()='Select']"));
    }

    /**
     * Returns the WebElement for Start workflow button.
     *
     * @return WebElement
     */
    @Override
    protected WebElement getStartWorkflowButton()
    {
        return driver.findElement(SUBMIT_BUTTON);
    }

    @Override
    WebElement getDueDateElement()
    {
        return driver.findElement(DUE_DATE);
    }

    /**
     * Method to fill in the form details and cancel new workflow.
     *
     * @return HtmlPage
     */
    @Override
    public HtmlPage cancelCreateWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException
    {
        fillUpWorkflowForm(formDetails);
        AssignmentPage assignmentPage = selectReviewer().render();
        assignmentPage.selectReviewers(formDetails.getReviewers()).render();
        return cancelWorkflow();
    }

    /**
     * Method to fill up all static details on the current Workflow form page object
     *
     * @param formDetails WorkFlowFormDetails
     */
    public void fillUpWorkflowForm(WorkFlowFormDetails formDetails)
    {
        if (formDetails == null || StringUtils.isEmpty(formDetails.getMessage()) || formDetails.getReviewers().size() < 1
                || isReviewersBlank(formDetails.getReviewers()))
        {
            throw new UnsupportedOperationException("siteName or message or cloudUsers cannot be blank");
        }
        enterMessageText(formDetails.getMessage());
        if (formDetails.getDueDate() != null)
        {
            enterDueDateText(formDetails.getDueDate());
        }
        if (formDetails.getTaskPriority() != null)
        {
            selectPriorityDropDown(formDetails.getTaskPriority());
        }
        if (formDetails.getApprovalPercentage() != 0)
        {
            WebElement approvalElem = findAndWait(REQUIRED_APPROVAL);
            approvalElem.clear();
            approvalElem.sendKeys(String.valueOf(formDetails.getApprovalPercentage()));
        }
    }

    /**
     * Method clicks on the Cancel WorkFlow button
     */
    public HtmlPage cancelWorkflow()
    {
        WebElement cancelButton = findAndWait(CANCEL_BUTTON);
        String cancelButtonId = cancelButton.getAttribute("id");
        cancelButton.click();
        waitUntilElementDeletedFromDom(By.id(cancelButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

}
