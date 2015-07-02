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
package org.alfresco.po.share.task;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.MyTasksPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Task Details Page.
 * 
 * @author Shan Nagarajan
 * @since 1.7.1
 */
public class TaskDetailsPage extends SharePage
{
    private static final By TASK_DETAILS_HEADER = By.cssSelector("div.task-details-header>h1");
    private static final By MENU_TITLE = By.cssSelector(".alfresco-header-Title");
    private static final By WORKFLOW_DETAILS = By.cssSelector(".links>a");
    private static final By MY_TASK_DETAILS = By.cssSelector(".backLink>a");
    private RenderElement menuTitle = getVisibleRenderElement(MENU_TITLE);
    private RenderElement workflowDetails = getVisibleRenderElement(WORKFLOW_DETAILS);
    private static final By ALL_FIELD_LABELS = By.cssSelector("span[class$='viewmode-label']");

    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By ITEM_ROW = By.cssSelector("div[id$='assoc_packageItems-cntrl'] table>tbody.yui-dt-data>tr");

    private static final By EDIT_BUTTON = By.cssSelector("button[id$='_default-edit-button']");

    private static final boolean isViewMoreActionDisplayed = false;

    public TaskDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskDetailsPage render(RenderTime timer)
    {
        elementRender(timer, menuTitle, workflowDetails);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskDetailsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TaskDetailsPage render()
    {
        return render(maxPageLoadingTime);
    }

    /**
     * Method to get Task Details Header (Eg: Details: Task name (Task))
     * 
     * @return Task Details Header
     */
    public String getTaskDetailsHeader()
    {
        return getElementText(TASK_DETAILS_HEADER);
    }

    /**
     * @return String
     */
    public String getComment()
    {
        return getElementText(By.xpath("//span[@class='viewmode-label' and contains(text(), 'Comment')]/../span[@class='viewmode-value']"));
    }

    /**
     * Method to get Required Approval percentage value
     * 
     * @return int
     */
    public int getRequiredApprovalPercentage()
    {
        return Integer.parseInt(getElementText(By
                .xpath("//span[@class='viewmode-label' and contains(text(), 'Required approval percentage:')]/../span[@class='viewmode-value']")));
    }

    /**
     * Method to get Actual Approval percentage value
     * 
     * @return int
     */
    public int getActualApprovalPercentage()
    {
        return Integer.parseInt(getElementText(By
                .xpath("//span[@class='viewmode-label' and contains(text(), 'Actual approval percentage:')]/../span[@class='viewmode-value']")));
    }

    /**
     * Mimics the action clicking My Tasks List hyper link.
     * 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage clickMyTasksList()
    {
        try
        {
            drone.find(MY_TASK_DETAILS).click();
            return new MyTasksPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"My Tasks List\" link" + nse);
        }
    }

    /**
     * Method to get Info section of Task Details page
     * 
     * @return TaskInfo
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

    /**
     * Method to get Task Status
     * 
     * @return TaskStatus
     */
    public TaskStatus getTaskStatus()
    {
        return TaskStatus.getTaskFromString(getElementText(By
                .xpath("//span[@class='viewmode-label' and contains(text(), 'Status:')]/../span[@class='viewmode-value']")));
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
     * @return List
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
     * @param fileName String
     * @return List
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
            throw new PageOperationException("Unable to find Task Item element" + nse);
        }
    }

    /**
     * Method to select Edit button
     * 
     * @return {@link EditTaskPage}
     */
    public EditTaskPage selectEditButton()
    {
        try
        {
            drone.find(EDIT_BUTTON).click();
            return new EditTaskPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to locate Edit button" + nse);
        }
    }

    /**
     * Method to verify Edit Button is present or not
     * 
     * @return True if present
     */
    public boolean isEditButtonPresent()
    {
        try
        {
            return drone.find(EDIT_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to select Task History link
     * 
     * @return {@link TaskHistoryPage}
     */
    public TaskHistoryPage selectTaskHistoryLink()
    {
        try
        {
            drone.find(By.linkText(drone.getValue("task.history.link.text"))).click();
            return new TaskHistoryPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Task History\" link", nse);
        }
    }

    /**
     * Method to get All labels from Workflow Form
     * 
     * @return List<String>
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