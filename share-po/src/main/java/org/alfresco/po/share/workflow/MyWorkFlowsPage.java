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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * My WorkFlows page object, holds all element of the html page relating to share's
 * my workflows page.
 *
 * @author Ranjith Manyam
 * @since 1.7
 */
public class MyWorkFlowsPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By START_WORKFLOW_BUTTON = By.cssSelector("button[id$='-startWorkflow-button-button']");
    private static final By WORKFLOW_ROWS = By.cssSelector("tr.yui-dt-rec");
    private static final By ACTIVE_LINK = By.cssSelector("a[rel='active']");
    private static final By COMPLETED_LINK = By.cssSelector("a[rel='completed']");
    private static final By TASK_CONTAINER = By.cssSelector("tr[class^='yui-dt-rec']");

    private static final By SUB_TITLE = By.cssSelector("h2[id$='_default-filterTitle']");

    private RenderElement loadingElement = new RenderElement(By.cssSelector(".yui-dt-loading"), ElementState.INVISIBLE);
    private RenderElement startWorkFlowButtonRender = RenderElement.getVisibleRenderElement(START_WORKFLOW_BUTTON);
    private RenderElement content = new RenderElement(By.cssSelector("div.yui-dt-liner"), ElementState.PRESENT);

    @SuppressWarnings("unchecked")
    @Override
    public MyWorkFlowsPage render(RenderTime timer)
    {
        elementRender(timer, startWorkFlowButtonRender, loadingElement, content);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyWorkFlowsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if people finder title is present on the page
     *
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("Workflows I've Started");
    }

    /**
     * Clicks on Start workflow button.
     *
     * @return {@link HtmlPage}
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
     * @param workFlowName String
     * @return List<WebElement>
     */
    private List<WebElement> findWorkFlowRow(String workFlowName)
    {
        if (workFlowName == null)
        {
            throw new IllegalArgumentException("Workflow Name can't be null");
        }
        List<WebElement> workflowRowsElements = new ArrayList<WebElement>();

        try
        {
            List<WebElement> workFlowRows = findAndWaitForElements(WORKFLOW_ROWS);
            if (null != workFlowRows && workFlowRows.size() > 0)
            {
                for (WebElement workFlowRow : workFlowRows)
                {

                    if (StringUtils.deleteWhitespace(workFlowName).equals(
                            StringUtils.deleteWhitespace(workFlowRow.findElement(By.cssSelector("h3 a")).getText())))
                    {
                        workflowRowsElements.add(workFlowRow);
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No workflow found", nse);
            }
        }
        catch (StaleElementReferenceException se)
        {
        }
        return workflowRowsElements;
    }

    /**
     * Method to get workflow details for a given workflow
     *
     * @param workFlowName String
     * @return {@link WorkFlowDetails}
     */
    public List<WorkFlowDetails> getWorkFlowDetails(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }
        try
        {
            List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);
            if (workFlowRow.size() > 0)
            {
                List<WorkFlowDetails> workFlowDetailsList = new ArrayList<WorkFlowDetails>();
                for (WebElement workflow : workFlowRow)
                {
                    mouseOver(workflow);
                    waitUntilAlert();
                    WorkFlowDetails workFlowDetails = new WorkFlowDetails();
                    workFlowDetails.setWorkFlowName(workflow.findElement(By.cssSelector("div.yui-dt-liner>h3>a")).getText());
                    workFlowDetails.setDue(workflow.findElement(By.cssSelector("div.due>span")).getText());
                    workFlowDetails.setStartDate(workflow.findElement(By.cssSelector("div[class^='started']>span")).getText());
                    workFlowDetails.setType(workflow.findElement(By.cssSelector("div.type>span")).getText());
                    workFlowDetails.setDescription(workflow.findElement(By.cssSelector("div.description>span")).getText());
                    workFlowDetails.setPriority(workflow.findElement(By.cssSelector("img")).getAttribute("title"));
                    workFlowDetails.setViewHistoryDisplayed(workflow.findElement(By.cssSelector("div[class$='view-link'] a")).isDisplayed());
                    try
                    {
                        workFlowDetails.setCancelWorkFlowDisplayed(workflow.findElement(By.cssSelector("div[class$='cancel-link'] a")).isDisplayed());
                    }
                    catch (NoSuchElementException e)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Cancel workflow button don't displayed.", e);
                        }
                    }
                    try
                    {
                        workFlowDetails.setDeleteWorkFlowDisplayed(workflow.findElement(By.cssSelector("div[class$='delete-link'] a")).isDisplayed());
                    }
                    catch (NoSuchElementException e)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Delete workflow button don't displayed.", e);
                        }
                    }
                    try
                    {
                        if (workflow.findElement(By.cssSelector("div[class^='started']>span")).isDisplayed())
                        {
                            workFlowDetails.setEndDate(workflow.findElement(By.cssSelector("div[class^='ended']>span")).getText());
                        }
                    }
                    catch (NoSuchElementException nse)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("End date don't displayed.", nse);
                        }
                    }

                    workFlowDetailsList.add(workFlowDetails);
                }
                return workFlowDetailsList;
            }
        }
        catch (StaleElementReferenceException e)
        {
            return getWorkFlowDetails(workFlowName);
        }
        return Collections.emptyList();
    }

    /**
     * Method to select a given WorkFlow
     *
     * @param workFlowName String
     * @return HtmlPage
     */
    public HtmlPage selectWorkFlow(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);

        if (workFlowRow.size() == 0)
        {
            throw new PageException("No WorkFlows exists with name: " + workFlowName);
        }
        else if (workFlowRow.size() == 1)
        {
            workFlowRow.get(0).findElement(By.cssSelector("td.yui-dt-col-title>div.yui-dt-liner>h3>a[title='View History']")).click();
            return getCurrentPage();
        }
        else if (workFlowRow.size() > 1)
        {
            throw new PageException("More than 1 WorkFlows exists with name: " + workFlowName);
        }

        throw new PageException("Select workflow failed");
    }

    /**
     * Method to check if a given workflow is displayed in MyWorkFlows page
     *
     * @param workFlowName String
     * @return True if workflow exists
     */
    public boolean isWorkFlowPresent(String workFlowName)
    {
        if (workFlowName == null)
        {
            throw new IllegalArgumentException("Work flow name cannot be null");
        }
        String xpathExpression = String.format("//h3/a[contains(.,'%s')]", workFlowName);
        try
        {

            WebElement workFlowRow = findAndWait(By.xpath(xpathExpression), 3000);
            return workFlowRow.isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.error("Element not found :" + xpathExpression);
        }
        return false;
    }

    /**
     * Method to get the page subtitle (Active workflows, Completed workflows etc)
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
            throw new PageException("Page Subtitle is not displayed", te);
        }
    }

    /**
     * Clicks on Active WorkFlows link.
     *
     * @return {@link MyWorkFlowsPage}
     */
    public HtmlPage selectActiveWorkFlows()
    {
        findAndWait(ACTIVE_LINK).click();
        waitUntilVisible(SUB_TITLE, "Active Workflows", TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        waitUntilAlert();
        return getCurrentPage();
    }

    /**
     * Clicks on Active WorkFlows link.
     *
     * @return {@link MyWorkFlowsPage}
     */
    public HtmlPage selectCompletedWorkFlows()
    {
        findAndWait(COMPLETED_LINK).click();
        waitUntilVisible(SUB_TITLE, "Completed Workflows", TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Method to cancel given workflow. If more than one workflow found, cancel all workflows.
     *
     * @param workFlowName String
     */
    public HtmlPage cancelWorkFlow(String workFlowName)
    {
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);
        if (workFlowRow.size() < 1)
        {
            throw new PageException("No workflows found with name: " + workFlowName);
        }
        try
        {
            for (int i = (workFlowRow.size() - 1); i >= 0; i--)
            {
                workFlowRow = findWorkFlowRow(workFlowName);
                mouseOver(workFlowRow.get(i));
                workFlowRow.get(i).findElement(By.cssSelector("td.yui-dt-last>div.yui-dt-liner>div.workflow-cancel-link>a>span")).click();
                waitForElement(By.cssSelector("div#prompt"), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
                List<WebElement> buttons = driver.findElements(By.cssSelector("div#prompt>div.ft>span.button-group>span.yui-button>span.first-child>button"));
                for (WebElement button : buttons)
                {
                    if (button.getText().equals("Yes"))
                    {
                        button.click();
                        waitUntilElementPresent(By.cssSelector("div#message>div.bd>span"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        waitUntilElementDeletedFromDom(By.cssSelector("div#message>div.bd>span"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        waitUntilElementDisappears(By.xpath("//div[contains(text(), 'Workflow was successfully cancelled')]"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        break;
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Cancel workflow link doesn't exists for workflow: " + workFlowName, nse);
        }
        return this;
    }

    /**
     * Method to delete given workflow. If more than one workflow found, delete all workflows.
     *
     * @param workFlowName String
     */
    public void deleteWorkFlow(String workFlowName)
    {
        if (StringUtils.isEmpty(workFlowName))
        {
            throw new IllegalArgumentException("Workflow Name cannot be null");
        }
        List<WebElement> workFlowRow = findWorkFlowRow(workFlowName);
        if (workFlowRow.size() < 1)
        {
            throw new PageException("No workflows found with name: " + workFlowName);
        }
        try
        {
            for (WebElement workFlow : workFlowRow)
            {
                mouseOver(workFlow);
                workFlow.findElement(By.cssSelector("td.yui-dt-last>div.yui-dt-liner>div.workflow-delete-link>a")).click();
                waitForElement(By.cssSelector("div#prompt"), TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
                List<WebElement> buttons = driver.findElements(By.cssSelector("div#prompt>div.ft>span.button-group>span.yui-button>span.first-child>button"));
                for (WebElement button : buttons)
                {
                    if (button.getText().equals("Yes"))
                    {
                        button.click();
                        break;
                    }
                }
            }
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Cancel workflow link doesn't exists for workflow: " + workFlowName, nse);
        }
        catch (NoSuchElementException nse)
        {
        }
    }

    /**
     * Return count displayed workflow.
     */
    public int getDisplayedWorkFlowCount()
    {
        try
        {
            return findAndWaitForElements(TASK_CONTAINER, 3).size();
        }
        catch (TimeoutException e)
        {
            return 0;
        }
    }
    WorkFlowFilters workFlowFilters;
    /**
     * Return Object for interacting with left filter panel.
     *
     * @return WorkFlowFilters
     */
    public WorkFlowFilters getWorkFlowsFilter()
    {
        return workFlowFilters;
    }

}
