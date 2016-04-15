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
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.task.EditTaskPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.my-tasks")
/**
 * My tasks dashlet object, holds all element of the HTML page relating to
 * share's my tasks dashlet on dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyTasksDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(MyTasksDashlet.class);

    private static final String DATA_LIST_CSS_LOCATION = "h3 > a";
    private static final String DIV_DASHLET_CONTENT_PLACEHOLDER = "div.dashlet.my-tasks";
    private static final String LIST_OF_TASKS = "div.dashlet.my-tasks h3>a";
    private static final By COMPLETE_TASK_BUTTON = By.cssSelector("a[href*='completed']");
    private static final By ACTIVE_TASK_BUTTON = By.cssSelector("a[href*='active']");
    private static final String DEFAULT_FILTER_BUTTON = "div.dashlet.my-tasks button[id$='default-filters-button']";
    private static final String DASHLET_LIST_OF_FILTER_BUTTONS = "div[class*='yui-button-menu yui-menu-button-menu visible']>div.bd>ul.first-of-type>li>a";
    private static final String TASK_VIEW_LINK = "a[@class='view-task']";
    private static final String TASK_EDIT_LINK = "a[@class='edit-task']";

//    /**
//     * Constructor.
//     */
//    protected MyTasksDashlet(WebDriver driver)
//    {
//        super(driver, By.cssSelector(DIV_DASHLET_CONTENT_PLACEHOLDER));
//        
//        setResizeHandle(By.xpath(".//div[contains (@class, 'yui-resize-handle')]"));
//    }


    /**
     * The collection of tasks displayed on my tasks dashlet.
     * 
     * @return List<ShareLink> links
     */
    public List<ShareLink> getTasks()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Selects a task that appears on my tasks dashlet by the matching name and
     * clicks on the link.
     */
    public ShareLink selectTask(final String title)
    {
        return getLink(DATA_LIST_CSS_LOCATION, title);
    }

    @SuppressWarnings("unchecked")
    public MyTasksDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(100L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                if (isEmpty(DIV_DASHLET_CONTENT_PLACEHOLDER))
                {
                    // There are no results
                    break;
                }
                else if (isVisibleResults())
                {
                    // populate results
                    break;
                }
                timer.end();
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    /**
     * Renderer to ensure the task is fully loaded.
     * 
     * @param time long
     * @param taskName String
     * @return MyTasksDashlet
     */
    public MyTasksDashlet renderTask(final long time, String taskName)
    {
        elementRender(new RenderTime(time), RenderElement.getVisibleRenderElement(By.xpath(String.format("//h3/a[text()='%s']/../../../..", taskName))));
        return this;
    }

    /**
     * This method clicks on specific task which appears on my-tasks dashlet.
     * 
     * @param task String
     * @return {@link EditTaskPage}
     */
    public HtmlPage clickOnTask(String task)
    {
        if (task == null)
        {
            throw new UnsupportedOperationException("task value of link is required");
        }

        try
        {
            for (WebElement element : findAndWaitForElements(By.cssSelector(LIST_OF_TASKS)))
            {
                String taskName = element.getText().toLowerCase();
                if (taskName != null && taskName.contains(task.toLowerCase()))
                {
                    element.click();
                    return getCurrentPage();
                }
            }

        }
        catch (Exception e)
        {
            logger.error("Unable to find the List of Tasks.", e);
        }
        throw new PageException("Unable to click task: " + task);
    }

    /**
     * select StartWorkFlow... link from myTasks dashlet.
     * 
     * @return StartWorkFlowPage
     */
    public HtmlPage selectStartWorkFlow()
    {
        try
        {
            WebElement startWorkFlow = findAndWait(By.linkText("Start Workflow"));
            startWorkFlow.click();
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Not able to find the web element", nse);
        }
        catch (TimeoutException exception)
        {
            logger.error("Exceeded time to find the web element", exception);
        }

        throw new PageException("Unable to find assign workflow.");
    }

    /**
    * Mimic click on 'Complete task' button
    *
    * @return MyTasksPage
    */
    public HtmlPage selectComplete()
    {
        if (dashlet == null)
        {
            dashlet = findAndWait(By.cssSelector(DIV_DASHLET_CONTENT_PLACEHOLDER), 100L);
        }
        dashlet.findElement(COMPLETE_TASK_BUTTON).click();
        return getCurrentPage();
    }

    /**
     * Retrieves the My Tasks FilterButton based on the given cssSelector
     * and clicks on it.
     */
    private void clickFilterButton()
    {
        try
        {
            findAndWait(By.cssSelector(DEFAULT_FILTER_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the Filter Button.", e);
            }
        }
    }

    /**
     * Select the given {@link MyTasksFilter} on My Tasks Dashlet.
     *
     * @param filter - The {@link MyTasksFilter} to be selected
     * @return {@link org.alfresco.po.HtmlPage}
     */
    public HtmlPage selectTasksFilter(MyTasksFilter filter)
    {
        clickFilterButton();
        List<WebElement> filterElements = findAndWaitForElements(By.cssSelector(DASHLET_LIST_OF_FILTER_BUTTONS));
        if (filterElements != null)
        {
            for (WebElement webElement : filterElements)
            {
                if (webElement.getText().equals(filter.getDescription()))
                {
                    webElement.click();
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * Mimic click on 'Active task' button
     *
     * @return MyTasksPage
     */
    public HtmlPage selectActive()
    {
        if (dashlet == null)
        {
            dashlet = findAndWait(By.cssSelector(DIV_DASHLET_CONTENT_PLACEHOLDER), 100L);
        }
        dashlet.findElement(ACTIVE_TASK_BUTTON).click();
        return getCurrentPage();
    }

    /**
     * Returns the div that hold the task info.
     *
     * @return WebElement
     */
    private WebElement getTaskRow(String taskName)    {

        return findAndWait(By.xpath("//h3/a[text()='" + taskName + "']"));
    }

    /**
     * Method to check if a given task is displayed in My Tasks Dashlet
     *
     * @param taskName String
     * @return True if Task exists
     */
    public boolean isTaskPresent(String taskName)
    {
        List<ShareLink> taskLinks = getTasks();
        try
        {
            for (ShareLink taskLink : taskLinks)
            {
                if (taskLink.getDescription().contains(taskName))
                {
                return true;
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Time out while finding user", e);
            return false;
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
    public boolean isTaskViewButtonEnabled(String taskName)
    {
        WebElement task = getTaskRow(taskName);
        if (task != null)
        {
            try
            {
                return driver.findElement(By.xpath("//h3/a[text()='" + taskName + "']/../../../..//"+TASK_VIEW_LINK)).isEnabled();
            }
            catch (NoSuchElementException e)
            {
                return false;
            }
            catch (TimeoutException e)
            {
                return false;
            }
        }

        return false;
    }

    /**
     * Returns <code>true</code> if the Task edit button is present and
     * enabled, otherwise returns <code>false</code>.
     *
     * @param taskName String
     * @return boolean
     */
    public boolean isTaskEditButtonEnabled(String taskName)
    {
        WebElement task = getTaskRow(taskName);
        if (task != null)
        {
            try
            {
                return driver.findElement(By.xpath("//h3/a[text()='" + taskName + "']/../../../..//"+TASK_EDIT_LINK)).isEnabled();
            }
            catch (NoSuchElementException e)
            {
                return false;
            }
            catch (TimeoutException e)
            {
                return false;
            }
        }

        return false;
    }


    /**
     * Clicks on edit task for single task
     * @param taskName String
     * @return {@link HtmlPage}
     */

    public HtmlPage selectEditTask(String taskName)
    {
        if (taskName == null)
        {
            throw new UnsupportedOperationException("Taskname should not be null");
        }
        try
        {
            List<WebElement> elements = driver.findElements(By.cssSelector(LIST_OF_TASKS));
            for (WebElement webElement : elements)
            {
                if (webElement.getText().equals(taskName))
                {
                    Actions mouseOver = new Actions(driver);
                    mouseOver.moveToElement(driver.findElement(By.xpath("//a[text()='" + taskName + "']"))).
                    moveToElement(driver.findElement(By.xpath("//h3/a[text()='" + taskName + "']/../../../..//"+TASK_EDIT_LINK))).click().perform();
                    return getCurrentPage();
                }
            }
        }
            catch (NoSuchElementException ex)
            {
                logger.error("My Task Dashlet is not present", ex);
            }
            throw new PageException("Task is not found");

    }

    /**
     * Clicks on edit task for single task
     * @param taskName String
     * @return {@link HtmlPage}
     */

    public HtmlPage selectViewTask(String taskName)
    {
        if (taskName == null)
        {
            throw new UnsupportedOperationException("Taskname should not be null");
        }
        try
        {
            List<WebElement> elements = driver.findElements(By.cssSelector(LIST_OF_TASKS));
            for (WebElement webElement : elements)
            {
                if (webElement.getText().equals(taskName))
                {
                    Actions mouseOver = new Actions(driver);
                    mouseOver.moveToElement(driver.findElement(By.xpath("//a[text()='" + taskName + "']"))).
                        moveToElement(driver.findElement(By.xpath("//h3/a[text()='" + taskName + "']/../../../..//"+TASK_VIEW_LINK))).click().perform();
                    return getCurrentPage();
                }
            }
        }
        catch (NoSuchElementException ex)
        {
            logger.error("My Task Dashlet is not present", ex);
        }
        throw new PageException("Task is not found");
    }
    @SuppressWarnings("unchecked")
    @Override
    public MyTasksDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
