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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DeleteGroupFromGroupPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 * Represent elements found on the HTML page relating to the CloudTaskOrReviewPage page load.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class CloudTaskOrReviewPage extends WorkFlowPage
{

    private static final By ADDED_FILE_ROW = By.cssSelector("div[id$='_assoc_packageItems-cntrl'] .yui-dt-data>tr");
    private static final By VIEW_MORE_ACTIONS = By.cssSelector("a.view_more_actions");
    private static final By REMOVE_BUTTON = By.cssSelector("a[class^='remove-list-item']");
    private static final By AFTER_COMPLETION_DROPDOWN = By.cssSelector("select[id$='hwf_retainStrategy']");
    private static final By PRIORITY_DROPDOWN = By.cssSelector("select[id$='_bpm_workflowPriority']");
    private static final By DUE_DATE = By.cssSelector("input[id$='workflowDueDate-cntrl-date']");
    private static final By LOCK_ON_PREMISE = By.cssSelector("input[id$='lockOnPremiseCopy-entry']");
    private static final By MESSAGE_TEXT = By.cssSelector("textarea[id$='_workflowDescription']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='form-cancel-button']");
    private static final By TYPE_DROP_DOWN_BUTTON = By.cssSelector("select[id$='hwf_cloudWorkflowType']");
    private static final By DESTINATION_BUTTON = By.cssSelector("button[id$='hwf_cloudDestination-select-button-button']");
    private static final By ASSIGNMENT_BUTTON = By.cssSelector("div[id$=_hwf_assignment-cntrl-itemGroupActions] > span > span > button");
    private static final By REQUIRED_APPROVAL_PERCENTAGE = By.cssSelector("input[id$='_requiredApprovalPercentage']");
    private static final By DESTINATION_NETWORK = By.cssSelector("span[id$='_hwf_cloudDestination-tenant']");

    private static final By DESTINATION_SITE = By.cssSelector("span[id$='_hwf_cloudDestination-site']");
    private static final By DESTINATION_FOLDER = By.cssSelector("span[id$='_hwf_cloudDestination-folder']");
    private static final By APPROVAL_PERCENTAGE_HELP_ICON = By.cssSelector("img[id$='_prop_hwf_requiredApprovalPercentage-help-icon']");

    private static final By APPROVAL_PERCENTAGE_HELP_TEXT = By.cssSelector("div[id$='_prop_hwf_requiredApprovalPercentage-help']");
    private static final By ASSIGNEE_OR_REVIEWERS_LABEL = By.cssSelector("label[for$='prop_hwf_assignment-cntrl']");

    private static final By ASSIGNEE_OR_REVIEWERS = By.cssSelector("div[id$='hwf_assignment-cntrl-currentValueDisplay']>div");

    private static final By SELECTED_ITEMS_SECTION = By.cssSelector("div[id$='_packageItems-cntrl-currentValueDisplay']");
    private static final By ADD_ITEMS_BUTTON = By.xpath("//div[contains(@id, '_packageItems-cntrl-itemGroupActions')]/span[1]/span/button");
    private static final By REMOVE_ALL_BUTTON = By.xpath("//div[contains(@id, '_packageItems-cntrl-itemGroupActions')]/span[2]/span/button");
    @SuppressWarnings("unused")
    private static final By NO_ITEM_SELECTED_MESSAGE = By
            .cssSelector("div[id$='_packageItems-cntrl-currentValueDisplay']>table>tbody.yui-dt-message>tr>td.yui-dt-empty>div");

    private static final By ITEM_DATE = By.cssSelector("div.viewmode-label");

    private static final RenderElement MESSAGE_ELEMENT = getVisibleRenderElement(MESSAGE_TEXT);
    private static final RenderElement DESTINATION_NETWORK_ELEMENT = getVisibleRenderElement(DESTINATION_NETWORK);
    private static final RenderElement DESTINATION_SITE_ELEMENT = getVisibleRenderElement(DESTINATION_SITE);
    private static final RenderElement DESTINATION_FOLDER_ELEMENT = getVisibleRenderElement(DESTINATION_FOLDER);
    private static final RenderElement DESTINATION_BUTTON_ELEMENT = getVisibleRenderElement(DESTINATION_BUTTON);
    private static final RenderElement ASSIGNEE_OR_REVIEWERS_LABEL_ELEMENT = getVisibleRenderElement(ASSIGNEE_OR_REVIEWERS_LABEL);
    private static final RenderElement ASSIGNMENT_BUTTON_ELEMENT = getVisibleRenderElement(ASSIGNMENT_BUTTON);
    private static final RenderElement AFTER_COMPLETION_DROPDOWN_ELEMENT = getVisibleRenderElement(AFTER_COMPLETION_DROPDOWN);
    private static final RenderElement LOCK_ON_PREMISE_ELEMENT = getVisibleRenderElement(LOCK_ON_PREMISE);
    private static final RenderElement ADD_ITEM_BUTTON_ELEMENT = getVisibleRenderElement(ADD_ITEMS_BUTTON);
    private static final RenderElement REMOVE_ALL_BUTTON_ELEMENT = getVisibleRenderElement(REMOVE_ALL_BUTTON);
    private static final RenderElement SELECTED_ITEMS_SECTION_ELEMENT = getVisibleRenderElement(SELECTED_ITEMS_SECTION);
    private static final RenderElement WORKFLOW_DESCRIPTION_HELP_ICON_ELEMENT = getVisibleRenderElement(WORKFLOW_DESCRIPTION_HELP_ICON);
    private static final RenderElement PRIORITY_DROPDOWN_ELEMENT = getVisibleRenderElement(PRIORITY_DROPDOWN);
    private static final RenderElement DUE_DATE_ELEMENT = getVisibleRenderElement(DUE_DATE);
    private static final long TIME_LEFT = 1000;
    
    private static final By ALL_FIELD_LABELS = By.cssSelector(".form-field>label");
    

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public CloudTaskOrReviewPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudTaskOrReviewPage render(RenderTime timer)
    {
        elementRender(timer, MESSAGE_ELEMENT, DESTINATION_NETWORK_ELEMENT, DESTINATION_SITE_ELEMENT, DESTINATION_FOLDER_ELEMENT, DESTINATION_BUTTON_ELEMENT,
                ASSIGNEE_OR_REVIEWERS_LABEL_ELEMENT, ASSIGNMENT_BUTTON_ELEMENT, AFTER_COMPLETION_DROPDOWN_ELEMENT, LOCK_ON_PREMISE_ELEMENT,
                ADD_ITEM_BUTTON_ELEMENT, REMOVE_ALL_BUTTON_ELEMENT, SELECTED_ITEMS_SECTION_ELEMENT, WORKFLOW_DESCRIPTION_HELP_ICON_ELEMENT,
                PRIORITY_DROPDOWN_ELEMENT, DUE_DATE_ELEMENT);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudTaskOrReviewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudTaskOrReviewPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to fill in the details for form and submit.
     * formDetails to get the form details
     * 
     * @return HtmlPage
     */
    @Override
    public HtmlPage startWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException
    {
        if (formDetails == null || StringUtils.isEmpty(formDetails.getSiteName()))
        {
            throw new UnsupportedOperationException("siteName cannot be blank");
        }

        if (formDetails.getMessage() != null)
        {
            enterMessageText(formDetails.getMessage());
        }

        if (formDetails.getDueDate() != null)
        {
            enterDueDateText(formDetails.getDueDate());
        }

        selectTask(formDetails.getTaskType());

        if (isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK))
        {
            enterRequiredApprovalPercentage(formDetails.getApprovalPercentage());
        }

        DestinationAndAssigneePage destinationAndAssigneePage = selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectSite(formDetails.getSiteName());
        destinationAndAssigneePage.selectSubmitButtonToSync();

        AssignmentPage assignmentPage = selectAssignmentPage().render();

        if (isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK))
        {
            if (StringUtils.isEmpty(formDetails.getAssignee()))
            {
                throw new UnsupportedOperationException("Assignee cannot be null");
            }
            assignmentPage.selectAssignee(formDetails.getAssignee());
        }
        else if (isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK))
        {
            if (formDetails.getReviewers().size() < 1 || isReviewersBlank(formDetails.getReviewers()))
            {
                throw new UnsupportedOperationException("At least one reviewer should be present");
            }
            assignmentPage.selectReviewers(formDetails.getReviewers()).render();
        }

        selectLockOnPremiseCheckbox(formDetails.isLockOnPremise());

        selectAfterCompleteDropDown(formDetails.getContentStrategy());
        selectPriorityDropDown(formDetails.getTaskPriority());

        WebElement startWorkFlow = drone.findAndWait(SUBMIT_BUTTON);
        if (!startWorkFlow.isEnabled())
        {
            startWorkFlow.wait(4000);
        }

        return submit(SUBMIT_BUTTON, ElementState.DELETE_FROM_DOM);
    }

    /**
     * Verify if workflow text is present on the page
     * 
     * @return true if exists
     */
    public boolean isTaskTypeSelected(TaskType taskType)
    {
        if (taskType == null)
        {
            throw new IllegalArgumentException("Task Type can't be null.");
        }
        Select taskTypeDropDown = new Select(drone.find(TYPE_DROP_DOWN_BUTTON));
        return taskTypeDropDown.getFirstSelectedOption().getText().equals(taskType.getType());
    }

    /**
     * Selects specific tasks on the pageS
     * 
     * @return true if exists
     */
    public void selectTask(TaskType taskType)
    {
        if (taskType == null)
        {
            throw new IllegalArgumentException("Task Type can't be null.");
        }
        WebElement dropDown = drone.findAndWait(TYPE_DROP_DOWN_BUTTON);
        Select select = new Select(dropDown);
        select.selectByIndex(taskType.ordinal());
    }

    /**
     * Method to check for test in WorkFlow page
     * 
     * @param selector
     * @param text
     * @return
     */
    protected boolean isTextPresent(By selector, String text)
    {
        if (StringUtils.isEmpty(text))
        {
            throw new IllegalArgumentException("text can't empty or null.");
        }
        if (selector == null)
        {
            throw new IllegalArgumentException("selector can't be null");
        }
        boolean display = false;
        String workflowText = null;

        try
        {
            workflowText = drone.findAndWait(selector).getText().trim();
        }
        catch (NoSuchElementException e)
        {
            logger.info("Workflow drop down button not Present", e);
        }

        if (workflowText != null)
        {
            display = workflowText.contains(text);
        }

        return display;
    }

    /**
     * Returns the WebElement for message textarea.
     * 
     * @return
     */
    @Override
    protected WebElement getMessageTextareaElement()
    {
        return drone.findAndWait(MESSAGE_TEXT);
    }

    /**
     * Returns the WebElement for message text area.
     * 
     * @return
     */
    @Override
    protected WebElement getDueDateElement()
    {
        return drone.findAndWait(DUE_DATE);
    }

    /**
     * Returns the WebElement for Select reviewer button.
     * 
     * @return
     */
    @Override
    protected WebElement getSelectReviewButton()
    {
        return drone.findAndWait(By.cssSelector("button[id$='yui-gen24-button']"));
    }

    /**
     * Returns the WebElement for Start workflow button.
     * 
     * @return
     */
    @Override
    protected WebElement getStartWorkflowButton()
    {
        return drone.findAndWait(By.cssSelector("button[id$='-form-submit-button']"));
    }

    /**
     * Mimics the action of selecting the Destination and Assignee button.
     * 
     * @return HtmlPage response page object
     */
    public HtmlPage selectDestinationAndAssigneePage()
    {
        RenderTime time = new RenderTime(maxPageLoadingTime);
        WebElement msg = null;
        try
        {
            drone.findAndWait(DESTINATION_BUTTON).click();

            while (true)
            {
                time.start();

                try
                {
                    msg = drone.findAndWait(By.cssSelector("div.bd>span.message"), TIME_LEFT);

                    if (msg != null && msg.getText().equalsIgnoreCase("No network is enabled for sync"))
                    {
                        drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), "No network is enabled for sync",
                                SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        throw new PageException("No network is enabled for sync");
                    }
                }
                catch (TimeoutException nse)
                {
                }

                try
                {
                    if (drone.findAndWait(By.cssSelector("div[id$='cloudDestination-cloud-folder-title']"), TIME_LEFT).isDisplayed())
                    {
                        return new DestinationAndAssigneePage(drone);
                    }
                }
                catch (TimeoutException nse)
                {
                }
                catch (StaleElementReferenceException se)
                {
                }

                try
                {
                    if (drone.findAndWait(By.cssSelector("form.cloud-auth-form"), TIME_LEFT).isDisplayed())
                    {
                        return new CloudSignInPage(drone);
                    }
                }
                catch (TimeoutException nse)
                {
                }
                catch (StaleElementReferenceException se)
                {
                }

                time.end();
            }
        }
        catch (PageRenderTimeException pt)
        {
            throw new PageException("Exceeded time to find destination page : ", pt);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find destination button : ", te);
        }
    }

    /**
     * Mimics the action of selecting the Lock on premise checkbox.
     * 
     * @param toCheck
     * @return HtmlPage response page object
     */
    public void selectLockOnPremiseCheckbox(boolean toCheck)
    {
        WebElement checkbox = drone.findAndWait(LOCK_ON_PREMISE);
        if (toCheck != checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    /**
     * Mimics the action of selecting the Assignment button.
     * 
     * @return HtmlPage response page object
     */
    public AssignmentPage selectAssignmentPage()
    {
        drone.findAndWait(ASSIGNMENT_BUTTON).click();
        return new AssignmentPage(drone);
    }

    /**
     * @return
     */
    public String getErrorMessage()
    {
        try
        {
            drone.find(DESTINATION_BUTTON).click();
            WebElement ele = drone.findAndWait(By.cssSelector("div>span.message"));
            return ele.getText();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding an element");
        }
        throw new PageException();
    }

    /**
     * Mimics the action of selecting the Remove button on an added document.
     */
    public void selecRemoveBtn(String filename)
    {
        performActionForFile(filename, REMOVE_BUTTON);
    }

    /**
     * Mimics the action of selecting the View more actions button on an added
     * document.
     */
    public DocumentDetailsPage selectViewMoreActionsBtn(String filename)
    {
        performActionForFile(filename, VIEW_MORE_ACTIONS);
        return new DocumentDetailsPage(drone);
    }

    /**
     * @param filename
     */
    private void performActionForFile(String filename, By action)
    {
        if (action == null)
        {
            throw new IllegalArgumentException("action selector should not be null.");
        }
        WebElement filerow = findFileRow(filename);
        if (filerow != null)
        {
            filerow.findElement(action).click();
        }
        else
        {
            logger.error("File not added.");
            throw new PageException("File not found");
        }
    }

    /**
     * @param filename
     */
    private String getDetailsForFile(String filename, By action)
    {

        if (action == null)
        {
            throw new IllegalArgumentException("action selector should not be null.");
        }
        WebElement filerow = findFileRow(filename);
        if (filerow != null)
        {
            return filerow.findElement(action).getText();
        }
        else
        {
            logger.error("File not added.");
            throw new PageException("File not found");

        }
    }

    /**
     * @param filename
     * @return
     */
    private WebElement findFileRow(String filename)
    {
        if (StringUtils.isEmpty(filename))
        {
            throw new IllegalArgumentException("File Name can't null or empty.");
        }
        List<WebElement> fileRows = drone.findAndWaitForElements(ADDED_FILE_ROW, maxPageLoadingTime);
        if (null != fileRows && fileRows.size() > 0)
        {
            for (WebElement fileRow : fileRows)
            {
                if (filename.equals(fileRow.findElement(By.cssSelector("h3 a")).getText()))
                {
                    return fileRow;
                }
            }
        }
        return null;
    }

    /**
     * Selects the Status drop down list.
     * 
     * @param strategy
     */
    public void selectAfterCompleteDropDown(KeepContentStrategy strategy)
    {
        if (strategy == null)
        {
            throw new IllegalArgumentException("Keep Content Strategy can't be empty.");
        }
        Select statusSelectDropDown = new Select(drone.findAndWait(AFTER_COMPLETION_DROPDOWN));
        statusSelectDropDown.selectByValue(strategy.getValue());
    }

    /**
     * Method to enter Required Approval Percentage
     * 
     * @param percentage
     */
    public void enterRequiredApprovalPercentage(int percentage)
    {
        if (percentage < 0)
        {
            throw new IllegalArgumentException("Percentage cannot be -ve value");
        }
        if (percentage > 100)
        {
            throw new IllegalArgumentException("Percentage cannot be greater than 100");
        }

        try
        {
            WebElement requiredApprovalPercentage = drone.find(REQUIRED_APPROVAL_PERCENTAGE);
            requiredApprovalPercentage.clear();
            requiredApprovalPercentage.sendKeys(String.valueOf(percentage));
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Required Approval Percentage", nse);
        }
    }

    /**
     * Method to get Cloud Destination Network
     * 
     * @return
     */
    public String getDestinationNetwork()
    {
        return getElementText(DESTINATION_NETWORK);
    }

    /**
     * Method to get Destination Cloud Site
     * 
     * @return
     */
    public String getDestinationSite()
    {
        return getElementText(DESTINATION_SITE);
    }

    /**
     * Method to get Destination Cloud Folder
     * 
     * @return
     */
    public String getDestinationFolder()
    {
        return getElementText(DESTINATION_FOLDER);
    }

    public String getRequiredApprovalPercentageField()
    {
        return drone.find(REQUIRED_APPROVAL_PERCENTAGE).getAttribute("value");
    }

    public Boolean isRequiredApprovalPercentageFieldPresent()
    {
        return drone.find(REQUIRED_APPROVAL_PERCENTAGE).isEnabled();
    }

    /**
     * Method to check Destination network, site, folder,
     * Destination select button, Assignee label and Assignee select button
     * are present.
     * 
     * @return
     */
    public boolean isSimpleCloudTaskElementsPresent()
    {
        return !isCloudReviewTaskElementsPresent();
    }

    /**
     * Method to check Destination network, site, folder,
     * Destination select button, Reviewer label and Reviewer select button,
     * Required Approval Percentage field and Approval percentage Help Icon are present.
     * 
     * @return
     */
    public boolean isCloudReviewTaskElementsPresent()
    {
        try
        {
            return (drone.find(REQUIRED_APPROVAL_PERCENTAGE).isDisplayed() && drone.find(APPROVAL_PERCENTAGE_HELP_ICON).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check if Select Assignee button is Enabled or Disabled
     * 
     * @return True if Select button is Enabled
     */
    public boolean isSelectAssigneeButtonEnabled()
    {
        if (isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK))
        {
            throw new PageOperationException("Selected Task Type is: \"Cloud Review Task\"");
        }
        return isSelectAssigneeOrReviewerButtonEnabled();
    }

    /**
     * Method to check if Select Reviewers button is Enabled or Disabled
     * 
     * @return True if Select button is Enabled
     */
    public boolean isSelectReviewersButtonEnabled()
    {
        if (isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK))
        {
            throw new PageOperationException("Selected Task Type is: \"Simple Cloud Task\"");
        }
        return isSelectAssigneeOrReviewerButtonEnabled();
    }

    /**
     * Method to check if Select Assignee/Reviewers button is Enabled or Disabled
     * 
     * @return True if Select button is Enabled
     */
    private boolean isSelectAssigneeOrReviewerButtonEnabled()
    {
        try
        {
            return drone.find(ASSIGNMENT_BUTTON).isEnabled();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Unable to find Select button for selecting Assignee/Reviewers");
            }
            throw new PageOperationException("Unable to find Select button for selecting Assignee/Reviewers", nse);
        }
    }

    /**
     * Method to get Reviewers List for Cloud Review Task. If no users found, it returns empty list
     * 
     * @return List of Reviewers
     */
    public List<String> getReviewers()
    {
        if (isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK))
        {
            throw new PageOperationException("Can not get Reviewers for Simple Cloud Task");
        }
        return getAssigneeOrReviewersList();
    }

    /**
     * Method to get Assignee for Simple Cloud Task. If no users found, return empty string.
     * 
     * @return
     */
    public String getAssignee()
    {
        if (isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK))
        {
            throw new PageOperationException("Can not get Assignee for Cloud Review Task");
        }

        List<String> assignee = getAssigneeOrReviewersList();

        if (assignee.size() > 1)
        {
            throw new PageOperationException("Assignee can not be more than one");
        }
        else if (assignee.size() == 0)
        {
            return "";
        }

        return assignee.get(0);
    }

    /**
     * Method to check if an Assignee is present
     * 
     * @return True if an Assignee is present
     */
    public boolean isAssigneePresent()
    {
        return !getAssignee().isEmpty();
    }

    /**
     * Method to check if Reviewers are present
     * 
     * @return True if Reviewer(s) present
     */
    public boolean isReviewersPresent()
    {
        return getReviewers().size() > 0;
    }

    /**
     * Method to get Assignee or Reviewers List. If No users found, returns empty list
     * 
     * @return
     */
    private List<String> getAssigneeOrReviewersList()
    {
        List<String> users = new ArrayList<String>();
        try
        {
            List<WebElement> userElements = drone.findAll(ASSIGNEE_OR_REVIEWERS);
            for (WebElement user : userElements)
            {
                users.add(user.getText());
            }
            return users;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No Assignee/Reviewers found", nse);
            }
        }
        return users;
    }

    /**
     * Method to select Approval percentage help icon and
     * 
     * @return
     */
    public String getRequiredApprovalPercentageHelpText()
    {
        if (isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK))
        {
            throw new PageOperationException("Required Approval Percentage is not available for Simple Cloud Task");
        }

        String message;
        try
        {
            drone.find(APPROVAL_PERCENTAGE_HELP_ICON).click();
            message = drone.find(APPROVAL_PERCENTAGE_HELP_TEXT).getText();
            drone.find(APPROVAL_PERCENTAGE_HELP_ICON).click();
            return message;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable find element", nse);
        }
    }

    /**
     * Method to get After Completion Dropdown options
     * 
     * @return
     */
    public List<String> getAfterCompletionOptions()
    {
        List<String> options = new ArrayList<String>();
        try
        {
            Select afterCompletionOptions = new Select(drone.find(AFTER_COMPLETION_DROPDOWN));
            List<WebElement> optionElements = afterCompletionOptions.getOptions();

            for (WebElement option : optionElements)
            {
                options.add(option.getText());
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find After Completion Dropdown", nse);
        }
        return options;
    }

    /**
     * Method to get Selected After Completion Option
     * 
     * @return
     */
    public KeepContentStrategy getSelectedAfterCompletionOption()
    {
        try
        {
            Select afterCompletionOptions = new Select(drone.find(AFTER_COMPLETION_DROPDOWN));
            return KeepContentStrategy.getKeepContentStrategy(afterCompletionOptions.getFirstSelectedOption().getText());
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find After Completion Dropdown", nse);
        }
    }

    /**
     * Method to verify if Lock On-Premise checkbox is selected
     * 
     * @return True if Lock On-Premise checkbox is checked
     */
    public boolean isLockOnPremiseSelected()
    {
        try
        {
            return drone.find(LOCK_ON_PREMISE).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Lock On-Premise option", nse);
        }
    }

    /**
     * Method to fill in the details for form and cancel.
     * formDetails to get the form details
     * 
     * @return HtmlPage
     */
    @Override
    public HtmlPage cancelCreateWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException
    {
        if (formDetails == null || StringUtils.isEmpty(formDetails.getSiteName()) || StringUtils.isEmpty(formDetails.getMessage()))
        {
            throw new UnsupportedOperationException("siteName or message cannot be blank");
        }

        enterMessageText(formDetails.getMessage());

        if (formDetails.getDueDate() != null)
        {
            enterDueDateText(formDetails.getDueDate());
        }

        selectTask(formDetails.getTaskType());

        if (isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK))
        {
            enterRequiredApprovalPercentage(formDetails.getApprovalPercentage());
        }

        DestinationAndAssigneePage destinationAndAssigneePage = selectDestinationAndAssigneePage().render();
        destinationAndAssigneePage.selectSite(formDetails.getSiteName());
        destinationAndAssigneePage.selectSubmitButtonToSync();

        AssignmentPage assignmentPage = selectAssignmentPage().render();

        if (isTaskTypeSelected(TaskType.SIMPLE_CLOUD_TASK))
        {
            if (StringUtils.isEmpty(formDetails.getAssignee()))
            {
                throw new UnsupportedOperationException("Assignee cannot be null");
            }
            assignmentPage.selectAssignee(formDetails.getAssignee());
        }
        else if (isTaskTypeSelected(TaskType.CLOUD_REVIEW_TASK))
        {
            if (formDetails.getReviewers().size() < 1 || isReviewersBlank(formDetails.getReviewers()))
            {
                throw new UnsupportedOperationException("At least one reviewer should be present");
            }
            assignmentPage.selectReviewers(formDetails.getReviewers()).render();
        }

        selectLockOnPremiseCheckbox(formDetails.isLockOnPremise());

        selectAfterCompleteDropDown(formDetails.getContentStrategy());
        selectPriorityDropDown(formDetails.getTaskPriority());
        WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
        String cancelButtonId = cancelButton.getAttribute("id");
        cancelButton.click();
        drone.waitUntilElementDeletedFromDom(By.id(cancelButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Method to check if DropDown After Completion is present
     * 
     * @return boolean
     */
    public boolean isAfterCompletionDropdownPresent()
    {
        try
        {
            return (drone.findAndWait(AFTER_COMPLETION_DROPDOWN).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check if Add Button is present
     * 
     * @return boolean
     */
    public boolean isAddButtonPresent()
    {
        try
        {
            return (drone.findAndWait(ADD_ITEMS_BUTTON).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public boolean isMessageTextFieldPresent()
    {
        try
        {
            return (drone.findAndWait(MESSAGE_TEXT).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public boolean isTypeDropDownPresent()
    {
        try
        {
            return (drone.findAndWait(TYPE_DROP_DOWN_BUTTON).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public boolean isHelpIconPresent()
    {
        try
        {
            return (drone.findAndWait(WORKFLOW_DESCRIPTION_HELP_ICON).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public boolean isDueDatePresent()
    {
        try
        {
            return (drone.findAndWait(DUE_DATE).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public boolean isPriorityDropDownPresent()
    {
        try
        {
            return (drone.findAndWait(PRIORITY_DROPDOWN).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

     /**
     * Verify if if selected After Completion is correct
     * 
     * @return true if exists
     */
    public boolean isAfterCompletionSelected(KeepContentStrategy strategy)
    {
        if (strategy == null)
        {
            throw new IllegalArgumentException("Task Type can't be null.");
        }
        Select afterCompletionDropDown = new Select(drone.findAndWait(AFTER_COMPLETION_DROPDOWN));
        return afterCompletionDropDown.getFirstSelectedOption().getText().equals(strategy.getStrategy());
    }

    /**
     * Get item Date
     * 
     * @return true if exists
     */
    public String getItemDate(String fileName)
    {
        String description = getDetailsForFile(fileName, ITEM_DATE);
        return description;
    }
    
    /**
     * Method to get All labels from Workflow Form
     * 
     * @return
     */
    public List<String> getAllLabels()
    {
        List<String> labels = new ArrayList<String>();
        try
        {
            List<WebElement> webElements = drone.findAll(ALL_FIELD_LABELS);
            for (WebElement label : webElements)
            {
                labels.add(label.getText());
            }
            return labels;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No labels found", nse);
            }
        }
        return labels;
    }
}
