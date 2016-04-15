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
package org.alfresco.po.share;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.task.TaskDetails;
import org.alfresco.po.share.task.TaskFilters;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.TaskHistoryPage;
import org.alfresco.po.share.workflow.ViewWorkflowPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * My tasks page object, holds all element of the html page relating to share's
 * my tasks page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyTasksPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By ACTIVE_LINK = By.cssSelector("a[rel='active']");
    private static final By COMPLETED_LINK = By.cssSelector("a[rel='completed']");
    private static final By TASKS_ROWS = By.cssSelector("tbody.yui-dt-data tr");
    private static final By TASK_VIEW_LINK = By.cssSelector("div.task-view-link>a>span");
    private static final By WORKFLOW_VIEW_LINK = By.cssSelector("div.workflow-view-link>a");
    private static final By taskLink = By.cssSelector("a");
    private static final By SUB_TITLE = By.cssSelector("h2[id$='_default-filterTitle']");
    private static final By START_WORKFLOW_BUTTON = By.cssSelector("button[id$='-startWorkflow-button-button']");
    private static final By WORKFLOW_FILTER_TITLE = By.cssSelector("h2[class='thin']");
    private static final By PRIORITY_LINK=By.cssSelector("div.yui-dt-liner img");

    private static final RenderElement LOADING_ELEMENT = new RenderElement(By.cssSelector(".yui-dt-loading"), ElementState.INVISIBLE);
    private static final RenderElement START_WORKFLOW_BUTTON_RENDER = RenderElement.getVisibleRenderElement(START_WORKFLOW_BUTTON);
    private static final RenderElement CONTENT = new RenderElement(By.cssSelector("div[id$='_my-tasks']"), ElementState.PRESENT);


    @SuppressWarnings("unchecked")
    @Override
    public MyTasksPage render(RenderTime timer)
    {
        elementRender(timer, START_WORKFLOW_BUTTON_RENDER, LOADING_ELEMENT, CONTENT);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyTasksPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    public MyTasksPage renderTask(final long time, String taskName)
    {
        elementRender(new RenderTime(time), RenderElement.getVisibleRenderElement(By.xpath(String.format("//h3/a[text()='%s']/../../../..", taskName))));
        return this;
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("My Tasks");
    }

    /**
     * Method to select the task and click edit task button
     * 
     * @param searchParams first item should be taskName, second is optional user first name
     */
    public HtmlPage navigateToEditTaskPage(String... searchParams)
    {
        if (searchParams == null || searchParams.length < 1)
        {
            throw new UnsupportedOperationException("Task name can't be null or empty");
        }
        String taskName = searchParams[0];
        if (taskName == null)
        {
            throw new PageOperationException("Task name is required");
        }
        try
        {
            String xpathExpression = String.format("//h3[.='%s']", taskName);
            WebElement row = findAndWait(By.xpath(xpathExpression));
            clickEdit(row);
            return getCurrentPage();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Not able to find the My Task Table.", e);
        }
        throw new PageException("Not able to find the site link element on this row.");
    }

    public void clickEdit(WebElement element, String... attempt)
    {
        try
        {
            element.findElement(taskLink).click();
        }
        catch (StaleElementReferenceException e)
        {
            if (attempt.length < 1)
            {
                clickEdit(element);
            }
            throw new PageOperationException("Unable to select edit task as its not clickable", e);
        }
    }

    /**
     * Clicks on Start workflow button.
     * 
     * @return {@link StartWorkFlowPage}
     */
    public HtmlPage selectStartWorkflowButton()
    {
        try
        {
            findAndWait(By.cssSelector("button[id$='-startWorkflow-button-button']")).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Not able to find start work flow button", e);
        }
        throw new PageException("Not able to find start work flow button");
    }

    /**
     * Method to get the page subtitle (Active Tasks, Completed Tasks etc)
     * 
     * @return String
     */
    public String getSubTitle()
    {
        try
        {
            return findAndWait(SUB_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new NoSuchElementException("Page Subtitle is not displayed", te);
        }
    }

    /**
     * Clicks on Active tasks link.
     * 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage selectActiveTasks()
    {
        findAndWait(ACTIVE_LINK).click();
        waitUntilVisible(SUB_TITLE, getValue("active.tasks.label"), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return getCurrentPage().render();
    }

    /**
     * Clicks on Completed tasks link.
     * 
     * @return {@link MyTasksPage}
     */
    public MyTasksPage selectCompletedTasks()
    {
        findAndWait(COMPLETED_LINK).click();
        waitUntilVisible(SUB_TITLE, getValue("completed.tasks.label"), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return getCurrentPage().render();
    }

    /**
     * Clicks on view workflow for single task.
     * 
     * @return {@link MyTasksPage}
     */
    public HtmlPage selectViewTasks(String taskName)
    {
        performActionOnTask(taskName, TASK_VIEW_LINK);
        return getCurrentPage();

    }

    /**
     * Clicks on view workflow for single task.
     * 
     * @return {@link MyTasksPage}
     */
    public ViewWorkflowPage selectViewWorkflow(String taskName)
    {
        performActionOnTask(taskName, WORKFLOW_VIEW_LINK);
        return factoryPage.instantiatePage(driver, ViewWorkflowPage.class);
    }

    /**
     * @param taskName String
     * @param action By
     */
    private void performActionOnTask(String taskName, By action)
    {
        if (taskName == null || action == null)
        {
            throw new UnsupportedOperationException("Both taskname and action should not be null");
        }
        WebElement taskRow = findTaskRow(taskName);
        if (taskRow != null)
        {
            findAndWait(By.xpath("//a[text()='"+taskName+"']"));
            try
            {
            taskRow.click();
            WebElement lastTD = taskRow.findElement(By.cssSelector("td:last-of-type"));
            mouseOver(lastTD);
            lastTD.findElement(action).click();
            }
            catch (StaleElementReferenceException ex)
            {
                Actions mouseOver = new Actions(driver);
                mouseOver.moveToElement(driver.findElement(By.xpath("//a[text()='"+taskName+"']")))
                         .moveToElement(taskRow.findElement(By.cssSelector("td:last-of-type")))
                         .moveToElement(driver.findElement(action)).moveToElement(driver.findElement(action)).click().perform();
            }

        }
        else
        {
            throw new PageException("File not found");
        }
    }

    /**
     * Method to find given task row
     * 
     * @param taskName String
     * @return WebElement
     */
    public WebElement findTaskRow(String taskName)
    {
        try
        {
            List<WebElement> taskRows = findAndWaitForElements(TASKS_ROWS);
            if (null != taskRows && taskRows.size() > 0)
            {
                for (WebElement taskRow : taskRows)
                {
                    String tName = StringUtils.deleteWhitespace(taskName);
                    WebElement el = taskRow.findElement(By.cssSelector("h3 a"));
                    String eln = el.getText();
                    String elName = StringUtils.deleteWhitespace(eln);
                    if (tName.equals(elName))
                    {
                        return taskRow;
                    }
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            return findTaskRow(taskName);
        }
        catch (TimeoutException e)
        {
            return null;
        }
        return null;
    }

    /**
     * Method to get the Task Details. If more than one task found, the first task details will be returned.
     * 
     * @param taskName String
     * @return {@link TaskDetails}
     */
    public TaskDetails getTaskDetails(String taskName)
    {
        if (StringUtils.isEmpty(taskName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }

        WebElement taskRow = findTaskRow(taskName);

        TaskDetails taskDetails = new TaskDetails();
        try
        {

            if (taskRow != null)
            {
                taskDetails.setTaskName(taskRow.findElement(By.cssSelector("div.yui-dt-liner>h3>a")).getText());
                taskDetails.setDue(taskRow.findElement(By.cssSelector("div.due>span")).getText());
                taskDetails.setStartDate(taskRow.findElement(By.cssSelector("div[class^='started']>span")).getText());
                taskDetails.setStatus(taskRow.findElement(By.cssSelector("div.status>span")).getText());
                taskDetails.setType(taskRow.findElement(By.cssSelector("div.type>span")).getText());
                taskDetails.setDescription(taskRow.findElement(By.cssSelector("div.description>span")).getText());
                taskDetails.setStartedBy(taskRow.findElement(By.cssSelector("div.initiator>span")).getText());

                if (taskRow.findElements(By.cssSelector("div[class^='ended']>span")).size() != 0)
                {
                    if (taskRow.findElement(By.cssSelector("div[class^='ended']>span")).isDisplayed())
                    {
                        taskDetails.setEndDate(taskRow.findElement(By.cssSelector("div[class^='ended']>span")).getText());
                    }
                }

                List<String> labels = new ArrayList<String>();
                List<WebElement> webElements = driver.findElements(By.cssSelector("div > label"));
                for (WebElement label : webElements)
                {
                    labels.add(label.getText());
                }

                taskDetails.setTaskLabels(labels);

                mouseOver(taskRow);
                if (taskRow.findElements(By.xpath(".//div[contains(@class, 'task-edit')]/a/span")).size() != 0)
                {
                    taskDetails.setEditTaskDisplayed(taskRow.findElement(By.xpath(".//div[contains(@class, 'task-edit')]/a/span")).isDisplayed());
                }
                else
                {
                    taskDetails.setEditTaskDisplayed(false);
                }

                if (taskRow.findElements(By.xpath(".//div[contains(@class, 'task-view')]/a/span")).size() != 0)
                {
                    taskDetails.setViewTaskDisplayed(taskRow.findElement(By.xpath(".//div[contains(@class, 'task-view')]/a/span")).isDisplayed());
                }
                else
                {
                    taskDetails.setViewTaskDisplayed(false);
                }

                if (taskRow.findElements(By.xpath(".//div[contains(@class, 'workflow-view')]/a/span")).size() != 0)
                {
                    taskDetails.setViewWorkFlowDisplayed(taskRow.findElement(By.xpath(".//div[contains(@class, 'workflow-view')]/a/span")).isDisplayed());
                }
                else
                {
                    taskDetails.setViewWorkFlowDisplayed(false);
                }
            }
            else
            {
                throw new PageOperationException("Unable to find task: " + taskName);
            }
        }
        catch (NoSuchElementException nse)
        {

        }
        catch (StaleElementReferenceException e)
        {
            return getTaskDetails(taskName);
        }
        return taskDetails;
    }
    
    /**
     * Method to get the Task Details. If more than one task found, the first task details will be returned.
     * 
     * @param taskName String
     * @return {@link TaskDetails}
     */
    public TaskDetails getTaskLabels(String taskName)
    {
        if (StringUtils.isEmpty(taskName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }

        WebElement taskRow = findTaskRow(taskName);

        TaskDetails taskDetails = new TaskDetails();
        try
        {
            if (taskRow != null)
            {
                List<String> labels = new ArrayList<String>();
                List<WebElement> webElements = driver.findElements(By.cssSelector("div > label"));
                for (WebElement label : webElements)
                {
                    labels.add(label.getText());
                }
                
                taskDetails.setTaskLabels(labels);
            }
            else
            {
                throw new PageOperationException("Unable to find task: " + taskName);
            }
        }
        catch (NoSuchElementException nse)
        {

        }
        catch (StaleElementReferenceException e)
        {
            return getTaskDetails(taskName);
        }
        return taskDetails;
    }

    /**
     * Method to check if a given task is displayed in MyTasksPage page
     * 
     * @param taskName String
     * @return True if Task exists
     */
    public boolean isTaskPresent(String taskName)
    {
        return findTaskRow(taskName) != null;
    }

    /**
     * Clicks on TaskHistory link on mytasks page.
     * 
     * @return {@link TaskHistoryPage}
     */
    public TaskHistoryPage selectTaskHistory(String taskName)
    {
        performActionOnTask(taskName, WORKFLOW_VIEW_LINK);
        return factoryPage.instantiatePage(driver, TaskHistoryPage.class);
    }

    /**
     * Returns <code>true</code> if the Task edit button is present and enabled,
     * otherwise returns <code>false</code>.
     * 
     * @param taskName String
     * @return boolean
     */
    public boolean isTaskViewButtonEnabled(String taskName)
    {
        WebElement task = findTaskRow(taskName);
        if (task != null)
        {
            try
            {
                return task.findElement(By.cssSelector(".task-view-link")).isEnabled();
            }
            catch (NoSuchElementException e)
            {
            }
        }

        return false;
    }

    /**
     * Returns <code>true</code> if the Task view button is present and enabled,
     * otherwise returns <code>false</code>.
     * 
     * @param taskName String
     * @return boolean
     */
    public boolean isTaskWorkflowButtonEnabled(String taskName)
    {
        WebElement task = findTaskRow(taskName);
        if (task != null)
        {
            try
            {
                return task.findElement(By.cssSelector(".workflow-view-link")).isEnabled();
            }
            catch (NoSuchElementException e)
            {
            }
        }

        return false;
    }

    /**
     * Returns <code>true</code> if the Task workflow view button is present and
     * enabled, otherwise returns <code>false</code>.
     * 
     * @param taskName String
     * @return boolean
     */
    public boolean isTaskEditButtonEnabled(String taskName)
    {
        WebElement task = findTaskRow(taskName);
        if (task != null)
        {
            try
            {
                return task.findElement(By.cssSelector(".task-edit-link")).isEnabled();
            }
            catch (NoSuchElementException e)
            {
            }
        }

        return false;
    }
    TaskFilters taskFilters;
    /**
     * Return Object for interacting with left filter panel.
     * 
     * @return TaskFilters
     */
    public TaskFilters getTaskFilters()
    {
        return taskFilters;
    }

    /**
     * Return count of task on page.
     * 
     * @return int
     */
    public int getTasksCount()
    {
        try
        {
            return findAndWaitForElements(TASKS_ROWS, 1000).size();
        }
        catch (TimeoutException e)
        {
            return 0;
        }
    }

    /**
     * Return true if filter title text displayed.
     * 
     * @param titleText String
     * @return boolean
     */
    public boolean isFilterTitle(String titleText)
    {
        checkNotNull(titleText);
        String actualTitle = findAndWait(WORKFLOW_FILTER_TITLE).getText();
        return titleText.equals(actualTitle);
    }

    /**
     * Return count workFlows with same taskName.
     * 
     * @param taskName String
     * @return int
     */
    public int getTaskCount(String taskName)
    {
        int count = 0;
        try
        {
            List<WebElement> taskRows = findAndWaitForElements(TASKS_ROWS);
            if (null != taskRows && taskRows.size() > 0)
            {
                for (WebElement taskRow : taskRows)
                {
                    String tName = StringUtils.deleteWhitespace(taskName);
                    WebElement el = taskRow.findElement(By.cssSelector("h3 a"));
                    String eln = el.getText();
                    String elName = StringUtils.deleteWhitespace(eln);
                    if (tName.equals(elName))
                    {
                        count++;
                    }
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            return getTaskCount(taskName);
        }
        catch (TimeoutException e)
        {
            return count;
        }
        return count;
    }
       
    /**
     * Returns <code>true</code> if the Priority icon is present and
     * enabled, otherwise returns <code>false</code>.
     * 
     * @param taskName String
     * @return boolean
     */
    public boolean isTaskPriorityIconEnabled(String taskName)
    {
        WebElement task = findTaskRow(taskName);
        if (task != null)
        {
            try
            {
                return task.findElement(PRIORITY_LINK).isEnabled();
            }
            catch (NoSuchElementException e)
            {
            }
        }

        return false;
    }

    public boolean isTaskNameUnique(String taskName)
    {
        List<WebElement> taskRows = driver.findElements(TASKS_ROWS);
        int count=0;
        if (null != taskRows && taskRows.size() > 0)
        {
            for (WebElement taskRow : taskRows)
            {
                if (StringUtils.deleteWhitespace(taskName).equals(StringUtils.deleteWhitespace(taskRow.findElement(By.cssSelector("h3 a")).getText())))
                {
                    count++;
                }
            }
        }
        if(count == 1)
        {
            return true;
        }
        return false;
    }
}
