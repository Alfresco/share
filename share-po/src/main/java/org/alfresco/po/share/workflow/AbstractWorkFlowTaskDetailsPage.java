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
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract WorkFlow Details Page.
 * 
 * @author Ranjith Manyam
 * @since 1.7.1
 */
public abstract class AbstractWorkFlowTaskDetailsPage extends SharePage
{
    private final Logger logger = Logger.getLogger(AbstractWorkFlowTaskDetailsPage.class);

    private static final By MENU_TITLE = By.cssSelector(".alfresco-header-Title");
    private static final By WORKFLOW_DETAILS_HEADER = By.cssSelector("div.workflow-details-header>h1");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='_default-cancel-button']");
    private static final By DELETE_BUTTON = By.cssSelector("button[id$='_default-delete-button']");
    private static final By ASSIGNEE = By.cssSelector("div[id$='_hwf_assignment-cntrl']>span[id$='_assignment-cntrl-currentValueDisplay']>div");

    private static final By GENERAL_INFO = By.cssSelector("div[id$='_default-general-form-section']");

    private static final By ITEM_ROW = By.cssSelector("div[id$='assoc_packageItems-cntrl'] table>tbody.yui-dt-data>tr");

    private static final By NO_TASKS_MESSAGE = By.cssSelector("div[id$='_default-currentTasks-form-section'] table>tbody.yui-dt-message>tr");
    private static final By CURRENT_TASKS_LIST = By.cssSelector("div[id$='_default-currentTasks-form-section'] table>tbody.yui-dt-data>tr");
    private static final By HISTORY_LIST = By.cssSelector("div[id$='_default-workflowHistory-form-section'] table>tbody.yui-dt-data>tr");

    private final RenderElement menuTitle = getVisibleRenderElement(MENU_TITLE);
    private final RenderElement workflowDetailsHeader = getVisibleRenderElement(WORKFLOW_DETAILS_HEADER);
    private final RenderElement formFieldsElements = getVisibleRenderElement(By.cssSelector("div[id$='-form-fields']"));

    public AbstractWorkFlowTaskDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HtmlPage render(RenderTime timer)
    {
        elementRender(timer, menuTitle, workflowDetailsHeader, formFieldsElements);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HtmlPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public HtmlPage render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Verify if WorkFlow Details title is present on the page
     * 
     * @return true if exists
     */
    public boolean isTitlePresent()
    {
        return isBrowserTitle("Workflow Details");
    }

    /**
     * Method to get WorkFlow Details header
     * 
     * @return
     */
    public String getPageHeader()
    {
        try
        {
            return drone.find(WORKFLOW_DETAILS_HEADER).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find WorkFlow Details Header Element", nse);
        }
    }

    /**
     * Method to get workflow status
     * 
     * @return
     */
    public String getWorkFlowStatus()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("span[id$='_default-status']")).getText();
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Unable to find WorkFlow Status Element", nse);
        }
    }

    /**
     * Method to click Cancel button from Workflow Details page
     * // TODO - Should return to MyWorkFlowsPage rather than MyTasksPage
     * 
     * @return
     */
    public MyTasksPage selectCancelWorkFlow()
    {
        try
        {
            WebElement cancelButton = drone.find(CANCEL_BUTTON);
            drone.mouseOverOnElement(cancelButton);
            cancelButton.click();
            drone.waitForElement(By.cssSelector("#prompt"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            List<WebElement> buttons = drone.findAll(By.cssSelector("div#prompt>div.ft>span.button-group>span.yui-button>span.first-child>button"));
            for (WebElement button : buttons)
            {
                if (button.getText().equals("Yes"))
                {
                    button.click();
                    break;
                }
            }
            return new MyTasksPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Cancel button ");
            }
            throw new PageException("Unable to find Cancel button", nse);
        }
    }

    /**
     * Method to get Assignee
     * 
     * @return Assignee Full Name and e-mail
     */
    public String getAssignee()
    {
        try
        {
            return drone.findAndWait(ASSIGNEE).getText();
        }
        catch (TimeoutException toe)
        {
            logger.error("Unable to find Assignee with exception", toe);
        }
        return "";
    }

    /**
     * Method to get General Info section on WorkFlow Details Page
     * 
     * @return {@link WorkFlowDetailsGeneralInfo}
     */
    public WorkFlowDetailsGeneralInfo getWorkFlowDetailsGeneralInfo()
    {
        WorkFlowDetailsGeneralInfo generalInfo = new WorkFlowDetailsGeneralInfo();
        try
        {
            WebElement infoElement = drone.find(GENERAL_INFO);
            generalInfo.setTitle(infoElement.findElement(By.cssSelector("span[id$='_default-title']")).getText());
            generalInfo.setDescription(infoElement.findElement(By.cssSelector("span[id$='_default-description']")).getText());
            generalInfo.setStartedBy(infoElement.findElement(By.cssSelector("span[id$='_default-startedBy']")).getText());
            generalInfo.setDueDate(infoElement.findElement(By.cssSelector("span[id$='_default-due']")).getText());
            generalInfo.setDueString(infoElement.findElement(By.cssSelector("span[id$='_default-due']")).getText());
            generalInfo.setCompleted(infoElement.findElement(By.cssSelector("span[id$='_default-completed']")).getText());
            generalInfo.setCompletedDate(infoElement.findElement(By.cssSelector("span[id$='_default-completed']")).getText());
            generalInfo.setStartDate(infoElement.findElement(By.cssSelector("span[id$='_default-started']")).getText());
            generalInfo.setPriority(infoElement.findElement(By.cssSelector("span[id$='_default-priority']")).getText());
            generalInfo.setStatus(infoElement.findElement(By.cssSelector("span[id$='_default-status']")).getText());
            generalInfo.setMessage(infoElement.findElement(By.cssSelector("span[id$='_default-message']")).getText());
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find General Info element", nse);
        }
        return generalInfo;
    }

    /**
     * Method to get More Info section on WorkFlow Details Page
     * 
     * @return {@link WorkFlowDetailsMoreInfo}
     */
    public WorkFlowDetailsMoreInfo getWorkFlowDetailsMoreInfo()
    {
        WorkFlowDetailsMoreInfo moreInfo = new WorkFlowDetailsMoreInfo();
        try
        {
            if (isBrowserTitle(drone.getValue("workflow.details.page.title")))
            {
                moreInfo.setType(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Type:')]/../span[@class='viewmode-value']")));
                moreInfo.setDestination(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Destination:')]/../span[@class='viewmode-value']")));
                moreInfo.setAfterCompletion(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'After completion:')]/../span[@class='viewmode-value']")));
                moreInfo.setLockOnPremise(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Lock on-premise content:')]/../span[@class='viewmode-value']")));

                List<WebElement> assignmentElementList = drone.findAndWaitForElements(By.cssSelector("span[id$='hwf_assignment-cntrl-currentValueDisplay']>div"), WAIT_TIME_3000);
                List<String> assignmentList = new ArrayList<String>();
                for (WebElement assignment : assignmentElementList)
                {
                    assignmentList.add(assignment.getText());
                }

                moreInfo.setAssignmentList(assignmentList);
            }
            else if (isBrowserTitle(drone.getValue("task.history.page.title")))
            {
                moreInfo.setNotification(getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Send Email Notifications:')]/../span[@class='viewmode-value']")));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find More Info element", nse);
        }
        return moreInfo;
    }

    private List<WebElement> getWorkFlowItemElements()
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
     * Method to get the list of Items in a workflow
     * 
     * @return {@link List<WorkFlowDetailsItem>}
     */
    public List<WorkFlowDetailsItem> getWorkFlowItems()
    {
        List<WorkFlowDetailsItem> workFlowDetailsItems = new ArrayList<WorkFlowDetailsItem>();
        try
        {
            List<WebElement> itemsRows = getWorkFlowItemElements();
            for (WebElement item : itemsRows)
            {
                workFlowDetailsItems.add(new WorkFlowDetailsItem(item, drone));
            }
            return workFlowDetailsItems;
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
     * Method to get the List of WorkFlowDetailsItem object for a given File Name
     * 
     * @param fileName
     * @return {@link List<WorkFlowDetailsItem>}
     */
    public List<WorkFlowDetailsItem> getWorkFlowItem(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be empty");
        }
        List<WebElement> workFlowDetailsItemElements = getWorkFlowItemElements();
        List<WorkFlowDetailsItem> workFlowItems = new ArrayList<WorkFlowDetailsItem>();
        for (WebElement item : workFlowDetailsItemElements)
        {
            if (item.findElement(By.cssSelector("h3.name")).getText().equals(fileName))
            {
                workFlowItems.add(new WorkFlowDetailsItem(item, drone));
            }
        }
        return workFlowItems;
    }

    /**
     * Method to verify "No Tasks" message is displayed under "Current Tasks"
     * 
     * @return True if "No Tasks" Message is displayed
     */
    public boolean isNoTasksMessageDisplayed()
    {
        try
        {
            return drone.find(NO_TASKS_MESSAGE).isDisplayed() && getElementText(NO_TASKS_MESSAGE).equals("No tasks");
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    private List<WebElement> getCurrentTaskElements()
    {
        try
        {
            return drone.findAll(CURRENT_TASKS_LIST);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Current Task Rows", nse);
        }
    }

    /**
     * Method to get List of Current Tasks that are in the workflow details page
     * 
     * @return {@link List<WorkFlowDetailsCurrentTask>}
     */
    public List<WorkFlowDetailsCurrentTask> getCurrentTasksList()
    {
        try
        {
            List<WebElement> currentTaskElements = getCurrentTaskElements();
            List<WorkFlowDetailsCurrentTask> tasks = new ArrayList<WorkFlowDetailsCurrentTask>();
            for (WebElement item : currentTaskElements)
            {
                tasks.add(new WorkFlowDetailsCurrentTask(item, drone));
            }
            return tasks;
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (PageOperationException poe)
        {
        }
        return Collections.emptyList();
    }

    private List<WebElement> getHistoryElements()
    {
        try
        {
            return drone.findAll(HISTORY_LIST);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find History Rows", nse);
        }
    }

    /**
     * Method to get List of History rows that are in the workflow details page
     * 
     * @return {@link List<WorkFlowDetailsHistory>}
     */
    public List<WorkFlowDetailsHistory> getWorkFlowHistoryList()
    {
        try
        {
            List<WebElement> historyElements = getHistoryElements();
            List<WorkFlowDetailsHistory> historyList = new ArrayList<WorkFlowDetailsHistory>();
            for (WebElement item : historyElements)
            {
                historyList.add(new WorkFlowDetailsHistory(item));
            }
            return historyList;
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
     * This is method is used to find out whether the cancel task or workflow button is displayed or not.
     * 
     * @return boolean
     */
    public boolean isCancelTaskOrWorkFlowButtonDisplayed()
    {
        try
        {
            return drone.find(CANCEL_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }


    /**
     * This is method is used to find out the delete workflow button is displayed or not.
     * 
     * @return boolean
     */
    public boolean isDeleteWorkFlowButtonDisplayed()
    {
        try
        {
            return drone.find(DELETE_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    
    protected RenderElement getMenuTitle()
    {
        return menuTitle;
    }

    protected RenderElement getWorkflowDetailsHeader()
    {
        return workflowDetailsHeader;
    }

    protected RenderElement getFormFieldsElements()
    {
        return formFieldsElements;
    }

}