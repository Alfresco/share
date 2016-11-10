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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Abstract of Workflowpage.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public abstract class WorkFlowPage extends SharePage implements WorkFlow
{
    private static Log logger = LogFactory.getLog(WorkFlowPage.class);

    private static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    private static final By REMOVE_ALL_BUTTON = By.xpath("//div[contains(@id, '_packageItems-cntrl-itemGroupActions')]/span[2]/span/button");
    private static final By NO_ITEM_SELECTED_MESSAGE = By
            .cssSelector("div[id$='_packageItems-cntrl-currentValueDisplay']>table>tbody.yui-dt-message>tr>td.yui-dt-empty>div");
    private static final By ITEM_ROW = By.cssSelector("div[id$='_packageItems-cntrl-currentValueDisplay']>table>tbody.yui-dt-data>tr");
    private static final By ITEM_NAME = By.cssSelector("h3.name");
    private static final By ERROR_MESSAGE = By.cssSelector("div.balloon>div.text>div");
    private static final By PRIORITY_DROPDOWN = By.cssSelector("select[id$='_bpm_workflowPriority']");

    @RenderWebElement
    protected static final By DUE_DATED_PICKER = By.cssSelector("img.datepicker-icon");

    private static final By WORKFLOW_COULD_NOT_BE_STARTED_PROMPT_HEADER = By.cssSelector("#prompt_h");
    private static final By WORKFLOW_COULD_NOT_BE_STARTED_MESSAGE = By.cssSelector("#prompt>div.bd");

    protected static final By WORKFLOW_DESCRIPTION_HELP_ICON = By.cssSelector("img[id$='_prop_bpm_workflowDescription-help-icon']");
    protected static final By WORKFLOW_DESCRIPTION_HELP_MESSAGE = By.cssSelector("div[id$='_prop_bpm_workflowDescription-help']");


    /**
     * @param messageString
     *            - The message that should be entered in message box
     */
    @Override
    public void enterMessageText(String messageString)
    {
        if (StringUtils.isEmpty(messageString))
        {
            //throw new IllegalArgumentException("Message cannot be Empty or null");
        }
        WebElement workFlowDescription = getMessageTextareaElement();// findAndWait(MESSAGE_TEXT);
        workFlowDescription.sendKeys(messageString);
    }

    /**
     * @param date
     *            - The message that should be entered in message box
     */
    @Override
    public void enterDueDateText(String date)
    {
        if (StringUtils.isEmpty(date))
        {
            throw new IllegalArgumentException("Date cannot be Empty or null");
        }
        WebElement workFlowDescription = getDueDateElement();
        workFlowDescription.clear();
        workFlowDescription.sendKeys(date);
    }

    /**
     * Clicks on Select button for selecting reviewers
     */
    @Override
    public AssignmentPage selectReviewer()
    {
        getSelectReviewButton().click();
        return factoryPage.instantiatePage(driver, AssignmentPage.class);
    }

    /**
     * @return WebElement
     */
    protected abstract WebElement getSelectReviewButton();

    /**
     * Clicks on Select button for selecting reviewers
     */
    public HtmlPage selectStartWorkflow()
    {
        driver.findElement(SUBMIT_BUTTON).click();
        waitUntilElementDeletedFromDom(SUBMIT_BUTTON, TimeUnit.SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * @return WebElement
     */
    protected abstract WebElement getStartWorkflowButton();

    /**
     * @param cloudUsers List<String>
     * @return boolean suggesting any user is blank.
     */
    protected boolean isReviewersBlank(List<String> cloudUsers)
    {
        for (String cloudUser : cloudUsers)
        {
            if (StringUtils.isEmpty(cloudUser))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the WebElement for message textarea.
     * 
     * @return WebElement
     */
    abstract WebElement getMessageTextareaElement();

    /**
     * Mimics the click Add Items button.
     * 
     * @return {@link SelectContentPage}
     */
    public SelectContentPage clickAddItems()
    {
        clickUnamedButton("Add");
        return factoryPage.instantiatePage(driver, SelectContentPage.class);
    }

    private void clickUnamedButton(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name cannot be Empty or null");
        }
        List<WebElement> elements = driver.findElements(By.cssSelector("button[type='button']"));
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
     * Returns the WebElement for message textarea.
     * 
     * @return WebElement
     */
    abstract WebElement getDueDateElement();

    /**
     * Method to select given file from the given site.
     * 
     * @param fileName String
     * @param siteName String
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
     * Method to verify "No items selected" message is displayed
     * 
     * @return True if "No items selected" message is displayed
     */
    public boolean isNoItemsSelectedMessagePresent()
    {
        try
        {
            return driver.findElement(NO_ITEM_SELECTED_MESSAGE).isDisplayed() && findAndWait(NO_ITEM_SELECTED_MESSAGE).getText().equals("No items selected");
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"No Items Selected\" Message", nse);
        }
    }

    /**
     * Method to verify Remove All button is Enabled
     * 
     * @return True if Remove All button
     */
    public boolean isRemoveAllButtonEnabled()
    {
        try
        {
            return driver.findElement(REMOVE_ALL_BUTTON).isEnabled();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Remove\" All button", nse);
        }
    }

    private List<WebElement> getSelectedItemElements()
    {
        try
        {
            return driver.findElements(ITEM_ROW);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Item Rows", nse);
        }
    }

    /**
     * Method to get List of all selected workflow items
     * 
     * @return List
     */
    public List<SelectedWorkFlowItem> getSelectedItems()
    {
        List<SelectedWorkFlowItem> selectedWorkFlowItems = new ArrayList<SelectedWorkFlowItem>();
        try
        {
            List<WebElement> itemsRows = getSelectedItemElements();

            for (WebElement item : itemsRows)
            {
                selectedWorkFlowItems.add(new SelectedWorkFlowItem(item, driver, factoryPage));
            }
            return selectedWorkFlowItems;
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
     * Method to get the Selected Items List for a given file name
     * 
     * @param fileName String
     * @return List
     */
    public List<SelectedWorkFlowItem> getSelectedItem(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be empty");
        }
        List<WebElement> selectedItemElements = getSelectedItemElements();
        List<SelectedWorkFlowItem> selectedWorkFlowItems = new ArrayList<SelectedWorkFlowItem>();
        for (WebElement item : selectedItemElements)
        {
            if (fileName.equals(item.findElement(ITEM_NAME).getText()))
            {
                selectedWorkFlowItems.add(new SelectedWorkFlowItem(item, driver, factoryPage));
            }
        }
        return selectedWorkFlowItems;
    }

    /**
     * Method to verify if the item is added or not
     * 
     * @param fileName String
     * @return True if the item is added
     */
    public boolean isItemAdded(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name can't null or empty");
        }
        List<SelectedWorkFlowItem> selectedWorkFlowItems = getSelectedItem(fileName);

        if (selectedWorkFlowItems.size() == 0)
        {
            return false;
        }
        for (SelectedWorkFlowItem item : selectedWorkFlowItems)
        {
            if (fileName.equals(item.getItemName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to verify Remove All button is Enabled
     */
    public void selectRemoveAllButton()
    {
        try
        {
            driver.findElement(REMOVE_ALL_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Remove\" All button", nse);
        }
    }

    public boolean isErrorBalloonPresent()
    {
        try
        {

            return driver.findElement(ERROR_MESSAGE).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Error PopUp", nse);
            }
        }
        return false;
    }

    public String getErrorBalloonMessage()
    {
        try
        {
            return driver.findElement(ERROR_MESSAGE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Error Message", nse);
        }
    }

    public String getWorkFlowCouldNotBeStartedPromptHeader()
    {
        try
        {
            return findAndWait(WORKFLOW_COULD_NOT_BE_STARTED_PROMPT_HEADER).getText();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find WorkFlow Could NOT be started prompt", te);
        }
    }

    public String getWorkFlowCouldNotBeStartedPromptMessage()
    {
        try
        {
            return findAndWait(WORKFLOW_COULD_NOT_BE_STARTED_MESSAGE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find WorkFlow Could NOT be started message", nse);
        }
    }

    /**
     * Mimics the action of clicking the help icon.
     */
    public void clickHelpIcon()
    {
        try
        {
            driver.findElement(WORKFLOW_DESCRIPTION_HELP_ICON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable find help icon", nse);
        }
    }

    /**
     * Find the help text and return, else throw {@link PageOperationException}.
     * 
     * @return Help text
     */
    public String getHelpText()
    {
        try
        {
            return driver.findElement(WORKFLOW_DESCRIPTION_HELP_MESSAGE).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable find help text, Please click the help icon first.", nse);
        }
    }

    /**
     * Method to select a date from Calender date picker. The date should be in
     * "dd/MM/yyyy" format
     * 
     * @param date String
     */
    public void selectDateFromCalendar(String date) throws InterruptedException
    {
        if (StringUtils.isEmpty(date))
        {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        DateTime dueDate;
        int due;
        try
        {
            dueDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(date);
            due = dueDate.getDayOfMonth();
        }
        catch (IllegalArgumentException iae)
        {
            throw new IllegalArgumentException("Due date should be in \"dd/MM/yyyy\" format");
        }

        try
        {
            DateTime today = new DateTime();

            waitForElement(DUE_DATED_PICKER, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            driver.findElement(DUE_DATED_PICKER).click();

            if (dueDate.isBeforeNow()
                    && !dueDate.toLocalDate().toString(DateTimeFormat.forPattern("dd-MM-yyyy"))
                            .equals(today.toLocalDate().toString(DateTimeFormat.forPattern("dd-MM-yyyy"))))
            {
                throw new UnsupportedOperationException("Due date cannot be in past");
            }
            else
            {

                WebElement calenderElement = findAndWait(By.cssSelector("table[id$='_workflowDueDate-cntrl']"));
                waitForElement(By.cssSelector("a.calnav"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                calenderElement.findElement(By.cssSelector("a.calnav")).click();

                WebElement monthAndYearSelector = findAndWait(By.cssSelector("div.yui-cal-nav"));

                Select monthSelector = new Select(driver.findElement(By.cssSelector("select[id$='_workflowDueDate-cntrl_nav_month']")));
                monthSelector.selectByValue(String.valueOf(dueDate.getMonthOfYear() - 1));

                monthAndYearSelector.findElement(By.cssSelector("input[id$='_workflowDueDate-cntrl_nav_year']")).clear();
                monthAndYearSelector.findElement(By.cssSelector("input[id$='_workflowDueDate-cntrl_nav_year']")).sendKeys(String.valueOf(dueDate.getYear()));

                monthAndYearSelector.findElement(By.cssSelector("button[id$='_workflowDueDate-cntrl_nav_submit']")).click();

                // Wait for the title to show the month
                waitUntilVisible(By.cssSelector("a.calnav"), dueDate.toString(DateTimeFormat.forPattern("MMMM yyyy")),
                        SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                calenderElement = findAndWait(By.cssSelector("table[id$='_workflowDueDate-cntrl']>tbody"));


                List<WebElement> allDays = calenderElement.findElements(By.cssSelector("a.selector"));

                for (WebElement day : allDays)
                {
                    if (Integer.parseInt(day.getText()) == (due))
                    {
                        day.click();
                        break;
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find element: ", nse);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Timed out on waiting for: ", te);
        }

    }

    /**
     * Method to close Calender Date Picker
     */
    public void closeCalendarDatePicker()
    {
        try
        {
            driver.findElement(By.cssSelector("div[id$='_workflowDueDate-cntrl'] span.close-icon.calclose")).click();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Close button on Calendar date picker", nse);
            }
            throw new PageOperationException("Unable to find Close button on Calendar date picker");
        }
    }

    /**
     * Method to get the Due date entered in Due field
     * 
     * @return String
     */
    public String getDueDate()
    {
        try
        {
            String due = driver.findElement(By.cssSelector("input[id$='_workflowDueDate']")).getAttribute("value").substring(0, 10);

            return (DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(due)).toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find DueDate field", nse);
            }
        }
        catch (IllegalArgumentException ie)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unalbe to parse Due Date", ie);
            }
        }
        return "";
    }

    /**
     * Method to get Priority Dropdown options
     * 
     * @return List<String>
     */
    public List<String> getPriorityOptions()
    {
        List<String> options = new ArrayList<String>();
        try
        {
            Select priorityOptions = new Select(driver.findElement(PRIORITY_DROPDOWN));
            List<WebElement> optionElements = priorityOptions.getOptions();

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
     * Method to get Selected Priority Option
     * 
     * @return Priority
     */
    public Priority getSelectedPriorityOption()
    {
        try
        {
            Select priorityOptions = new Select(driver.findElement(PRIORITY_DROPDOWN));
            return Priority.getPriority(priorityOptions.getFirstSelectedOption().getText());
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Priority Dropdown", nse);
        }
    }

    /**
     * Selects the priority down list.
     */
    public void selectPriorityDropDown(Priority priority)
    {
        if (priority == null)
        {
            throw new IllegalArgumentException("Priority can't be empty.");
        }
        Select priorityDropDown = new Select(findAndWait(PRIORITY_DROPDOWN));
        priorityDropDown.selectByValue(priority.getValue());
    }

    /**
     * Method to check if REMOVE_ALL_BUTTON is present
     * 
     * @return boolean
     */
    public boolean isRemoveAllButtonPresent()
    {
        try
        {
            return driver.findElement(REMOVE_ALL_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
    
    /**
     * Click Start workflow button
     */
    public void clickStartWorkflow()
    {
        try
        {
            findAndWait(SUBMIT_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find button Start Workflow", nse);
        }
    }

}
