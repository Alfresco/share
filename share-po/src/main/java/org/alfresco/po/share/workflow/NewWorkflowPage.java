/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.workflow;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

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

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public NewWorkflowPage(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public NewWorkflowPage render(final long time)
    {
        return render(new RenderTime(time));
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
        WebElement saveButton = drone.findAndWait(SUBMIT_BUTTON);
        String saveButtonId = saveButton.getAttribute("id");
        saveButton.click();
        drone.waitUntilElementDisappears(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
       // drone.waitUntilElementDeletedFromDom(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    public HtmlPage doubleClickStartWorkflow()
    {
        try
        {
            WebElement saveButton = drone.findAndWait(SUBMIT_BUTTON);
            String id = saveButton.getAttribute("id");
            drone.mouseOver(saveButton);
            drone.doubleClickOnElement(saveButton);
            drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException e)
        {
        }
        return FactorySharePage.resolvePage(drone);
    }

    @Override
    protected WebElement getMessageTextareaElement()
    {
        return drone.findAndWait(MESSAGE_TEXT);
    }

    /**
     * Returns the WebElement for Select reviewer button.
     *
     * @return WebElement
     */
    @Override
    protected WebElement getSelectReviewButton()
    {
        return drone.findAndWait(By.xpath("//button[text()='Select']"));
    }

    /**
     * Returns the WebElement for Start workflow button.
     *
     * @return WebElement
     */
    @Override
    protected WebElement getStartWorkflowButton()
    {
        return drone.find(SUBMIT_BUTTON);
    }

    @Override
    WebElement getDueDateElement()
    {
        return drone.find(DUE_DATE);
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
            WebElement approvalElem = drone.findAndWait(REQUIRED_APPROVAL);
            approvalElem.clear();
            approvalElem.sendKeys(String.valueOf(formDetails.getApprovalPercentage()));
        }
    }

    /**
     * Method clicks on the Cancel WorkFlow button
     */
    public HtmlPage cancelWorkflow()
    {
        WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
        String cancelButtonId = cancelButton.getAttribute("id");
        cancelButton.click();
        drone.waitUntilElementDeletedFromDom(By.id(cancelButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

}
