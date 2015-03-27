/*
 * \ * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.task;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.workflow.ReassignPage;
import org.alfresco.po.share.workflow.SelectContentPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.share.task.EditTaskPage.Button.*;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * This class represents the Edit task page which can be navigated from My Tasks
 * page > Edit Task.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public class EditTaskPage extends SharePage
{
    private static final String TASK_STATUS = "select[id$='default_prop_bpm_status']";
    private static final String COMMENT_TEXTAREA = "textarea[id$='_comment']";

    private static final By ITEM_ROW = By.cssSelector("div[id$='assoc_packageItems-cntrl'] table>tbody.yui-dt-data>tr");
    private static final boolean isViewMoreActionDisplayed = true;

    private static final RenderElement TITLE_ELEMENT = getVisibleRenderElement(By.cssSelector("#HEADER_TITLE_BAR"));
    private static final RenderElement EDIT_TASK_HEADER_ELEMENT = getVisibleRenderElement(By.cssSelector("div.task-edit-header h1"));
    private static final RenderElement SAVE_BUTTON_ELEMENT = getVisibleRenderElement(SAVE_AND_CLOSE.by);
    private static final RenderElement CANCEL_BUTTON_ELEMENT = getVisibleRenderElement(CANCEL.by);

    private static final String ACCEPT_BUTTON = "button[id*='accept-button']";
    private static final By ALL_FIELD_LABELS = By.cssSelector("span[class$='viewmode-label']");

    private final Log logger = LogFactory.getLog(this.getClass());

    public enum Button
    {
        REASSIGN("button[id$='default-reassign-button']"),
        APPROVE("button[id$='reviewOutcome-Approve-button'], button[id$='reviewOutcome-approve-button']"),
        REJECT("button[id$='reviewOutcome-Reject-button'], button[id$='reviewOutcome-reject-button']"),
        SAVE_AND_CLOSE("button[id$='default-form-submit-button']"),
        CANCEL("button[id$='_default-form-cancel-button']"),
        TASK_DONE("button[id$='default_prop_transitions-Next-button']"),
        ADD("div[id$='packageItems-cntrl-itemGroupActions'] > span > span > button"),
        REMOVE_ALL("div[id$='packageItems-cntrl-itemGroupActions'] > span ~ span >span>button"),
        CLAIM("button[id$='default-claim-button']"),
        RELEASE_TO_POOL("button[id$='default-release-button']");
        public final By by;

        Button(String cssSelector)
        {
            this.by = By.cssSelector(cssSelector);
        }
    }

    /**
     * @param drone
     */
    public EditTaskPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.webdrone.HtmlPage#render(org.alfresco.webdrone.RenderTime)
     */
    @SuppressWarnings("unchecked")
    @Override
    public EditTaskPage render(RenderTime timer) throws PageException
    {
        elementRender(timer, TITLE_ELEMENT, EDIT_TASK_HEADER_ELEMENT, SAVE_BUTTON_ELEMENT, CANCEL_BUTTON_ELEMENT);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTaskPage render() throws PageException
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditTaskPage render(final long time) throws PageException
    {
        return render(new RenderTime(time));
    }

    /**
     * Selects the Status drop down list.
     */
    public HtmlPage selectStatusDropDown(TaskStatus status)
    {
        Select statusSelect = new Select(drone.findAndWait(By.cssSelector(TASK_STATUS)));
        statusSelect.selectByValue(status.getTaskName());
        return this;
    }

    /**
     * Selects the Status drop down list.
     * 
     * @return {@link TaskStatus} - status selected from dropdown.
     */
    public TaskStatus getSelectedStatusFromDropDown()
    {
        Select comboBox = new Select(drone.findAndWait(By.cssSelector(TASK_STATUS)));
        String selectedTask = comboBox.getFirstSelectedOption().getText();
        return TaskStatus.getTaskFromString(selectedTask);
    }

    /**
     * Selects the Task done button.
     * 
     * @return {@link org.alfresco.po.share.MyTasksPage} - instance of my task page.
     */
    public HtmlPage selectTaskDoneButton()
    {
        WebElement taskDoneButton = drone.findAndWait(TASK_DONE.by);
        String id = taskDoneButton.getAttribute("id");
        taskDoneButton.click();
        drone.waitUntilElementDeletedFromDom(By.id(id), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Selects the Reject button.
     * 
     * @return {@link org.alfresco.po.share.MyTasksPage} - instance of my task page.
     */
    public HtmlPage selectRejectButton()
    {
        try
        {
            WebElement rejectButton = drone.findAndWait(REJECT.by);
            String id = rejectButton.getAttribute("id");
            drone.mouseOverOnElement(rejectButton);
            rejectButton.click();
            drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Reject button", nse);
        }
    }

    /**
     * Selects the Status drop down list.
     * 
     * @return {@link org.alfresco.po.share.MyTasksPage} - instance of my task page.
     */
    public HtmlPage selectApproveButton()
    {
        try
        {
            WebElement approveButton = drone.findAndWait(APPROVE.by);
            String id = approveButton.getAttribute("id");
            drone.mouseOverOnElement(approveButton);
            approveButton.click();
            drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Approve button", nse);
        }
    }

    /**
     * Selects the Save button
     * 
     * @return {@link org.alfresco.po.share.MyTasksPage} - instance of my task page.
     */
    public HtmlPage selectSaveButton()
    {
        try
        {
            WebElement saveButton = drone.find(SAVE_AND_CLOSE.by);
            String id = saveButton.getAttribute("id");
            drone.mouseOverOnElement(saveButton);
            saveButton.click();
            drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Save and Close button" + nse);
        }
    }

    /**
     * Enter comment
     * 
     * @param comment
     */
    public void enterComment(String comment)
    {
        try
        {
            WebElement commentBox = drone.find(By.cssSelector(COMMENT_TEXTAREA));
            commentBox.clear();
            commentBox.sendKeys(comment);
        }
        catch (NoSuchElementException e)
        {
            throw new UnsupportedOperationException("Comment cannot be added for this task", e);
        }
    }

    /**
     * TODO - Dont know whether its absence from 4.2 is expected behaviour.
     * Selects comment box and enters comment into it.
     * public void enterComment(String comment) { if (dojoSupport) { throw new
     * UnsupportedOperationException
     * ("Operation invalid for enterprise versions 4.2."); } WebElement
     * commentBox = drone.find(By.cssSelector(COMMENT_BOX));
     * commentBox.sendKeys(comment); }
     * public String readCommentFromCommentBox() { if (dojoSupport) { throw new
     * UnsupportedOperationException
     * ("Operation invalid for enterprise versions 4.2."); } WebElement
     * commentBox = drone.find(By.cssSelector(COMMENT_BOX)); return
     * commentBox.getText(); }
     */

    /**
     * Method to get Info section of Edit Task page
     * 
     * @return
     */
    public TaskInfo getTaskDetailsInfo()
    {
        TaskInfo info = new TaskInfo();
        try
        {
            info.setMessage(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Message:')]/../span[@class='viewmode-value']")));
            info.setOwner(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Owner:')]/../span[@class='viewmode-value']")));
            info.setPriority(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Priority:')]/../span[@class='viewmode-value']")));
            info.setDueDate(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Due:')]/../span[@class='viewmode-value']")));
            info.setDueString(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Due:')]/../span[@class='viewmode-value']")));
            info.setIdentifier(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Identifier:')]/../span[@class='viewmode-value']")));
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find More Info element" + nse);
        }
        return info;
    }

    private List<WebElement> getTaskItemElements()
    {
        try
        {
            return drone.findAll(ITEM_ROW);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Item Rows", nse);
        }
    }

    /**
     * Method to get the list of Items in a Task
     * 
     * @return {@link List< TaskItem >}
     */
    public List<TaskItem> getTaskItems()
    {
        List<TaskItem> taskItems = new ArrayList<TaskItem>();
        try
        {
            List<WebElement> itemsRows = getTaskItemElements();

            for (WebElement item : itemsRows)
            {
                taskItems.add(new TaskItem(item, drone, isViewMoreActionDisplayed));
            }
            return taskItems;
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (PageOperationException poe)
        {
        }
        return Collections.emptyList();
    }

    /**
     * Method to get the List of TaskItem object for a given File Name
     * 
     * @param fileName
     * @return {@link List< TaskItem >}
     */
    public List<TaskItem> getTaskItem(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be empty");
        }
        List<WebElement> workFlowDetailsItemElements = getTaskItemElements();
        List<TaskItem> taskItems = new ArrayList<TaskItem>();
        try
        {
            for (WebElement item : workFlowDetailsItemElements)
            {
                if (item.findElement(By.cssSelector("h3.name")).getText().equals(fileName))
                {
                    taskItems.add(new TaskItem(item, drone, isViewMoreActionDisplayed));
                }
            }
            return taskItems;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Task Item element", nse);
        }
    }

    /**
     * Method to get Status Drop down options
     * 
     * @return
     */
    public List<TaskStatus> getStatusOptions()
    {
        List<TaskStatus> taskStatusList = new ArrayList<TaskStatus>();
        try
        {
            Select statusOptions = new Select(drone.find(By.cssSelector(TASK_STATUS)));
            List<WebElement> optionElements = statusOptions.getOptions();
            for (WebElement option : optionElements)
            {
                taskStatusList.add(TaskStatus.getTaskFromString(option.getText()));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Status Dropdown", nse);
        }
        return taskStatusList;
    }

    /**
     * Method to select Cancel button on Edit Task Page
     * 
     * @return {@link org.alfresco.po.share.MyTasksPage} or {@link TaskDetailsPage}
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            WebElement cancelButton = drone.find(CANCEL.by);
            drone.mouseOverOnElement(cancelButton);
            String id = cancelButton.getAttribute("id");
            cancelButton.click();
            drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Cancel Button", nse);
        }
    }

    /**
     * Method to check if Reassign button is displayed or not
     * 
     * @return True if displayed
     */
    public boolean isReAssignButtonDisplayed()
    {
        try
        {
            return drone.find(REASSIGN.by).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Mimics the action of clicking the Accept Button.
     * 
     * @return the Current Share Page.
     */
    public HtmlPage selectAcceptButton()
    {
        try
        {
            WebElement approveButton = drone.find(By.cssSelector(ACCEPT_BUTTON));
            String id = approveButton.getAttribute("id");
            drone.mouseOverOnElement(approveButton);
            approveButton.click();
            drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Approve button", nse);
        }
    }

    /**
     * Return is all buttons displayed on page.
     * 
     * @return
     */
    public boolean isButtonsDisplayed(Button button)
    {
        try
        {
            return drone.findAndWait(button.by, 3000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to select given file from the given site.
     * 
     * @param fileName
     * @param siteName
     */
    public void selectItem(String fileName, String siteName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name cannot be Empty");
        }
        if (StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site Name cannot be Empty");
        }
        SelectContentPage selectContentPage = clickAddItems().render();
        selectContentPage.addItemFromSite(fileName, siteName);
        selectContentPage.selectOKButton().render();
    }

    /**
     * Mimics the click Add Items button.
     * 
     * @return {@link SelectContentPage}
     */
    public SelectContentPage clickAddItems()
    {
        clickUnamedButton("Add");
        return new SelectContentPage(drone);
    }

    private void clickUnamedButton(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name cannot be Empty or null");
        }
        List<WebElement> elements = drone.findAll(By.cssSelector("button[type='button']"));
        for (WebElement webElement : elements)
        {
            if (name.equals(webElement.getText()))
            {
                webElement.click();
                break;
            }
        }
    }

    /**
     * Mimics the click Reassign Button button.
     * 
     * @return {@link ReassignPage}
     */
    public ReassignPage clickReassign()
    {
        drone.find(REASSIGN.by).click();
        return new ReassignPage(drone).render();
    }

    /**
     * Method to reassign task for another user.
     * test is EditTaskPageTest.selectReassign
     * 
     * @param userName
     */
    public MyTasksPage selectReassign(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name cannot be Empty");
        }
        ReassignPage reassignPage = clickReassign();
        reassignPage.selectUser(userName).render();
        return drone.getCurrentPage().render();
    }

    /**
     * Method mimic click interaction with Claim button.
     * 
     * @return
     */
    public EditTaskPage selectClaim()
    {
        drone.findAndWait(CLAIM.by).click();
        waitUntilAlert();
        return this.render();
    }

    /**
     * Method to check if COMMENT_TEXTAREA is present
     * 
     * @return boolean
     */
    public boolean isCommentTextAreaDisplayed()
    {
        return drone.isElementDisplayed(By.cssSelector(COMMENT_TEXTAREA));

    }

    /**
     * Method to read comment from COMMENT_TEXTAREA
     * 
     * @return String
     */
    public String readCommentFromCommentTextArea()
    {

        WebElement commentBox = drone.find(By.cssSelector(COMMENT_TEXTAREA));
        return commentBox.getAttribute("value");

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
