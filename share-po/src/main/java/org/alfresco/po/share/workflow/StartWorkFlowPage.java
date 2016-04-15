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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page object holds all elements of HTML page objects relating to Start
 * WorkFlow connect page.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class StartWorkFlowPage extends SharePage
{

    private static final By WORKFLOW_DROP_DOWN_BUTTON = By.cssSelector("button[id$='default-workflow-definition-button-button']");
    private static final String WORKFLOW_TEXT = "Please select a workflow";
    private static final By WORKFLOW_BUTTON = By.cssSelector("button[id$='default-workflow-definition-button-button']");
    private static final By WORKFLOW_TITLE_LIST = By.cssSelector("li.yuimenuitem>span.title");
    private static final By WORKFLOW_DROP_DOWN = By.cssSelector("div[id$='default-workflow-definition-menu'] li span.title");
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By ADD_BUTTON = By.cssSelector("div[id$='packageItems-cntrl-itemGroupActions'] span:nth-child(1) span button");
    private static final By SELECT_BUTTON = By.cssSelector("div[id$='assoc_bpm_assignee-cntrl-itemGroupActions'] button");

    @SuppressWarnings("unchecked")
    @Override
    public StartWorkFlowPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(50L);
                }
                catch (InterruptedException e)
                {
                }
                try
                {
                    driver.findElement(WORKFLOW_DROP_DOWN_BUTTON);
                    break;
                }
                catch (NoSuchElementException e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StartWorkFlowPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if workflow text is present on the page
     * 
     * @return true if exists
     */
    public boolean isWorkFlowTextPresent()
    {
        return (isTextPresent(WORKFLOW_DROP_DOWN_BUTTON, WORKFLOW_TEXT));
    }

    /**
     * Method to check for test in WorkFlow page
     * 
     * @param selector By
     * @param text String
     * @return boolean
     */
    protected boolean isTextPresent(By selector, String text)
    {
        boolean display = false;
        String workflowText = null;

        try
        {
            workflowText = findAndWait(selector).getText().trim();
        }
        catch (TimeoutException e)
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
     * Method to get the workflow sub page for the workflow passed.
     * StartWorkFlow page is returned in common,for any of its subclass.
     * 
     * @param workFlowType WorkFlowType
     * @return WorkFlow
     */
    public WorkFlow getWorkflowPage(WorkFlowType workFlowType)
    {
        if (workFlowType == null)
        {
            throw new IllegalArgumentException("Workflow Type can't be null");
        }
        findAndWait(WORKFLOW_BUTTON).click();
        workFlowType.getTaskTypeElement(driver).click();
        return factoryPage.instantiatePage(driver, NewWorkflowPage.class);
    }


    /**
     * Method to get workflow types exists in select workflow dropdown
     * 
     * @return List of WorkFlowType
     */
    public List<WorkFlowType> getWorkflowTypes()
    {
        List<WorkFlowType> workFlowTypes = Collections.emptyList();
        try
        {
            driver.findElement(WORKFLOW_BUTTON).click();
            if (logger.isInfoEnabled())
            {
                logger.info("Clicked on WORKFLOW_BUTTON");
            }
            List<WebElement> workflowElements = driver.findElements(WORKFLOW_TITLE_LIST);
            workFlowTypes = new ArrayList<WorkFlowType>(workflowElements.size());

            for (WebElement workFlow : workflowElements)
            {
                if (!workFlow.getText().isEmpty())
                {
                    workFlowTypes.add(WorkFlowType.getWorkflowTypeByTitle(workFlow.getText()));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        // Click on WorkFlow button to close the drop down
        if (driver.findElement(WORKFLOW_TITLE_LIST).isDisplayed())
        {
            driver.findElement(WORKFLOW_BUTTON).click();
        }
        return workFlowTypes;
    }

    /**
     * Method to check if a given WorkFlowType is present in the select Workflow drop down
     * 
     * @param workFlowType WorkFlowType
     * @return boolean
     */
    public boolean isWorkflowTypePresent(WorkFlowType workFlowType)
    {
        if (workFlowType == null)
        {
            throw new IllegalArgumentException("Workflow Type can not be null");
        }
        return getWorkflowTypes().contains(workFlowType);
    }

    public HtmlPage startTaskWorkflow(WebDriver driver, String taskName, String assigneeUser, String fileName, String siteName)
    {
        HtmlPage htmlPage = null;
        findAndWait(WORKFLOW_DROP_DOWN_BUTTON).click();
        List<WebElement> liElements = findAndWaitForElements(WORKFLOW_DROP_DOWN);
        liElements.get(0).click();
        NewWorkflowPage newTaskPage = factoryPage.instantiatePage(driver, NewWorkflowPage.class);
        newTaskPage.render();

        newTaskPage.enterMessageText(taskName);

        findAndWait(SELECT_BUTTON).click();
        AssignmentPage assignmentPage = 
                factoryPage.instantiatePage(driver, AssignmentPage.class);
        assignmentPage.render();
        List<String> reviewersList = new ArrayList<String>();
        reviewersList.add(assigneeUser);
        assignmentPage.selectReviewers(reviewersList).render();

        findAndWait(ADD_BUTTON).click();
        SelectContentPage selectContentPage = 
                factoryPage.instantiatePage(driver, SelectContentPage.class);
        selectContentPage.render();
        selectContentPage.addItemFromSite(fileName, siteName);
        selectContentPage.selectOKButton().render();

        htmlPage = newTaskPage.submitWorkflow();

        return htmlPage;
    }
}
