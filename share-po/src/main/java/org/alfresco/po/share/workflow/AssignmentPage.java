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
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.*;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Assignment page is to select the cloud reviewer in the workflow start form.
 *
 * @author Siva Kaliyappan
 * @author Shan Nagarajan
 * @since 1.6.2
 */
public class AssignmentPage extends SharePage
{
    private static final int LOADING_WAIT_TIME = 2000;
    private static Log logger = LogFactory.getLog(AssignmentPage.class);
    private static final By SEARCH_TEXT = By.cssSelector("input[id$='searchText']");
    private static final By SEARCH_BUTTON = By.xpath("//button[contains(@id, 'searchButton-button')]");
    private static final By OK_BUTTON = By.cssSelector("button[id$='cntrl-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='cntrl-cancel-button']");
    private static final By SELECT_CLOUD_REVIEWER = By.cssSelector("a.add-item");
    private static final By LIST_CLOUD_REVIEWER = (By
            .cssSelector("div[id$='ssignee-cntrl-picker-left']>div[id$='-cntrl-picker-results']>table>tbody.yui-dt-data>tr, " +
                    "div[id$='_assignment-cntrl-picker-left']>div[id$='-cntrl-picker-results']>table>tbody.yui-dt-data>tr, " +
                    "div[id$='ssignees-cntrl-picker-left']>div[id$='-cntrl-picker-results']>table>tbody.yui-dt-data>tr"));
    private static final By SELECTED_USERS = (By
            .cssSelector("div[id$='ssignee-cntrl-picker-right']>div[id$='-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr, " +
                    "div[id$='_assignment-cntrl-picker-right']>div[id$='-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr"));
    private static final By SEARCH_RESULTS_SECTION = (By.cssSelector("div[id$='ssignee-cntrl-picker-results'], " +
            "div[id$='_assignment-cntrl-picker-results'], " +
            "div[id$='ssignees-cntrl-picker-results']"));
    private static final By SELECTED_ITEMS_SECTION = (By
            .cssSelector("div[id$='ssignee-cntrl-picker-selectedItems'], " +
                    "div[id$='_assignment-cntrl-picker-selectedItems'], " +
                    "div[id$='bpm_assignees-cntrl-picker-selectedItems']"));
    private static final By CLOSE_BUTTON = (By
            .cssSelector("div[id$='ssignee-cntrl-picker']>a.container-close, " +
                    "div[id$='_assignment-cntrl-picker']>a.container-close, " +
                    "div[id$='ssignees-cntrl-picker']>a.container-close "));
    private static final By ENTER_A_SEARCH_TERM_MESSAGE = (By
            .cssSelector("div[id$='-cntrl-picker-left']>div[id$='ssignee-cntrl-picker-results']>table>tbody.yui-dt-message>tr>td.yui-dt-empty>div.yui-dt-liner, " +
                    "div[id$='-cntrl-picker-left']>div[id$='_assignment-cntrl-picker-results']>table>tbody.yui-dt-message>tr>td.yui-dt-empty>div.yui-dt-liner"));
    private static final By WARNING_MESSAGE = (By.cssSelector("div#message_c>div#message>div.bd>span.message"));

    private final RenderElement searchFieldElement = new RenderElement(SEARCH_TEXT, ElementState.PRESENT);
    private final RenderElement searchButtonElement = new RenderElement(SEARCH_BUTTON, ElementState.PRESENT);
    private final RenderElement searchResultsSectionElement = getVisibleRenderElement(SEARCH_RESULTS_SECTION);
    private final RenderElement selectedItemsSectionElement = getVisibleRenderElement(SELECTED_ITEMS_SECTION);
    private final RenderElement okButtonElement = new RenderElement(OK_BUTTON, ElementState.PRESENT);
    private final RenderElement cancelButtonElement = new RenderElement(CANCEL_BUTTON, ElementState.PRESENT);
    private final RenderElement closeButtonElement = getVisibleRenderElement(CLOSE_BUTTON);

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public AssignmentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AssignmentPage render(RenderTime timer)
    {
        elementRender(timer, searchFieldElement, searchButtonElement, searchResultsSectionElement, selectedItemsSectionElement, okButtonElement, cancelButtonElement, closeButtonElement);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AssignmentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AssignmentPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to add multiple Users
     *
     * @param userNames List<String>
     */
    public void selectUsers(List<String> userNames)
    {
        for (String userName : userNames)
        {
            selectUserWithRetry(userName);
        }
    }

    /**
     * Method to add a single user
     * Move this as a util in qa-share
     *
     * @param userName String
     */
    @Deprecated
    public void selectUserWithRetry(String userName)
    {
        boolean found = false;
        // TODO: Add this as a test property
        int retryCount = 10;

        while (!found && retryCount > 0)
        {
            try
            {
                found = selectUser(userName);
            }
            catch (PageException pe)
            {
                logger.info("User Not Found: Retrying ...");
            }
            finally
            {
                retryCount--;
            }
        }
    }

    /**
     * Method to add a single user
     *
     * @param userName String
     * @return boolean
     */
    public boolean selectUser(String userName)
    {
        boolean userFound = false;

        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name can't empty or null.");
        }
        List<WebElement> elements = retrieveUsers(userName);

        userName = userName.toLowerCase();
        for (WebElement webElement : elements)
        {

            if (webElement.findElement(By.cssSelector(".item-name")).getText().toLowerCase().contains(userName))
            {
                drone.mouseOver(webElement.findElement(SELECT_CLOUD_REVIEWER));
                webElement.findElement(SELECT_CLOUD_REVIEWER).click();
                if (!isUserSelected(userName))
                {
                    try
                    {
                        webElement.findElement(SELECT_CLOUD_REVIEWER).click();
                    }
                    catch (ElementNotVisibleException enve)
                    {

                    }
                }
                userFound = true;
                break;
            }
        }
        return userFound;
    }

    /**
     * Method to select the cloud reviewer and submit.
     *
     * @return Assignment Page return type clod sync cloud user part will be
     * modified after completing other page objects.
     */
    public HtmlPage selectReviewers(List<String> userNames)
    {
        selectUsers(userNames);
        selectOKButton();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Method to select the cloud assignee and submit.
     */
    public void selectAssignee(String userName)
    {
        selectUser(userName);
        selectOKButton();
    }

    public void selectOKButton()
    {
        try
        {
            getVisibleElement(OK_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find OK button", nse);
        }
    }

    /**
     * Method to get the cloud reviewers or list of assignees
     *
     * @return List of users
     */
    public List<WebElement> retrieveUsers(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name can't empty or null.");
        }
        try
        {
            searchForUser(userName);
            drone.waitForElement(LIST_CLOUD_REVIEWER, SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            return drone.findAndWaitForElements(LIST_CLOUD_REVIEWER);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding element", toe);
        }
        throw new PageException();
    }

    /**
     * Method to search for given user
     *
     * @param userName String
     */
    public void searchForUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        try
        {
            clearSearchField();
            getVisibleElement(SEARCH_TEXT).sendKeys(userName);
            selectSearchButton();
            try
            {
                drone.waitForElement(By.id("AlfrescoWebdronez1"), SECONDS.convert(LOADING_WAIT_TIME, MILLISECONDS));
            }
            catch (TimeoutException e)
            {
            }
            // drone.waitFor(LOADING_WAIT_TIME);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element Not found", nse);
        }
        catch (TimeoutException toe)
        {
            logger.error("Timed out: ", toe);
        }
    }

    /**
     * Method to check if "No items found" message is displayed
     *
     * @param userName String
     * @return True if "No items found" message is displayed
     */
    public boolean isNoItemsFoundMessageDisplayed(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        try
        {
            searchForUser(userName);
            WebElement message = drone.find(By.cssSelector("div[id$='ssignment-cntrl-picker-left']>div[id$='-cntrl-picker-results']>table>tbody.yui-dt-message>tr div, div[id$='_assignee-cntrl-picker-left']>div[id$='-cntrl-picker-results']>table>tbody.yui-dt-message>tr div"));
            return message.isDisplayed() && message.getText().equals("No items found");
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check if a given user found after search.
     *
     * @param userName String
     * @return True if given user found
     */
    public boolean isUserFound(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        try
        {
            List<WebElement> users = retrieveUsers(userName);
            for (WebElement user : users)
            {
                if (user.findElement(By.cssSelector(".item-name")).getText().contains("(" + userName + ")"))
                {
                    return true;
                }
            }
        }
        catch (PageException pe)
        {
        }
        return false;
    }

    /**
     * Method to check "Enter a search term" message is displayed
     *
     * @return boolean
     */
    public boolean isEnterASearchTermMessageDisplayed()
    {
        try
        {
            return (drone.find(ENTER_A_SEARCH_TERM_MESSAGE).isDisplayed() && drone.find(ENTER_A_SEARCH_TERM_MESSAGE).getText().equals("Enter a search term"));
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to clear Search Field
     */
    public void clearSearchField()
    {
        try
        {
            drone.findFirstDisplayedElement(SEARCH_TEXT).clear();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Unable to find Search Field", nse);
            }
        }
    }

    /**
     *
     */
    public void selectSearchButton()
    {
        try
        {
            getVisibleElement(SEARCH_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find Search button", nse);
        }
    }

    /**
     * Method to get the warning message (Eg: "Enter at least 1 character(s) to search")
     * Returns empty string if unable to find the element
     * This method should be called as soon as the warning message disappears quickly.
     *
     * @return Warning Message
     */
    public String getWarningMessage()
    {
        try
        {
            return drone.findAndWait(WARNING_MESSAGE).getText();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Warning Message", te);
            }
            return "";
        }
    }

    /**
     * This method gets the User Search results content.
     *
     * @return String
     */
    public String getContent()
    {
        try
        {
            WebElement el = drone.findAndWait(By.cssSelector("div[id$='ssignee-cntrl-picker-results'] tbody[class$='message'] div"));
            String s = el.getText();
            return s;
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * Method to get User list after search
     *
     * @param userName String
     * @return List of users
     */
    public List<String> getUserList(String userName)
    {
        List<WebElement> userElements = retrieveUsers(userName);
        List<String> userList = new ArrayList<String>(userElements.size());
        for (WebElement user : userElements)
        {
            userList.add(user.getText());
        }
        return userList;
    }

    /**
     * Method to verify Add Icon is present for a given user
     *
     * @param userName String
     * @return True if Add icon is present
     */
    public boolean isAddIconPresent(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name cannot be empty");
        }
        List<WebElement> elements = retrieveUsers(userName);
        boolean isDisplayed = false;

        for (WebElement webElement : elements)
        {
            if (webElement.findElement(By.cssSelector(".item-name")).getText().contains("(" + userName + ")"))
            {
                isDisplayed = drone.find(SELECT_CLOUD_REVIEWER).isDisplayed();
                break;
            }
        }
        return isDisplayed;
    }

    /**
     * Method to get Selected User elements
     *
     * @return List of User Elements
     */
    private List<WebElement> getSelectedUserElements()
    {
        try
        {
            return drone.findAll(SELECTED_USERS);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Users found", nse);
        }
        return Collections.<WebElement>emptyList();
    }

    /**
     * Method to verify if the given user is selected or not
     *
     * @param userName String
     * @return True if selected
     */
    public boolean isUserSelected(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name cannot be empty");
        }
        List<WebElement> selectedUsers = getSelectedUserElements();
        boolean isDisplayed = false;
        for (WebElement user : selectedUsers)
        {
            if (user.findElement(By.cssSelector("h3.name")).getText().contains("(" + userName + ")"))
            {
                isDisplayed = true;
                break;
            }
        }
        return isDisplayed;
    }

    /**
     * Method to remove a user from Selected Users list
     *
     * @param userName String
     */
    public void removeUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name cannot be empty");
        }
        List<WebElement> selectedUsers = getSelectedUserElements();

        if (selectedUsers.size() < 1)
        {
            throw new PageOperationException("User is not selected.");
        }

        for (WebElement user : selectedUsers)
        {
            if (user.findElement(By.cssSelector("h3.name")).getText().contains("(" + userName + ")"))
            {
                drone.mouseOver(user.findElement(By.cssSelector("a.remove-item")));
                user.findElement(By.cssSelector("a.remove-item")).click();

                if (isUserSelected(userName))
                {
                    try
                    {
                        user.findElement(By.cssSelector("a.remove-item")).click();
                    }
                    catch (ElementNotVisibleException enve)
                    {

                    }
                }

                break;
            }
        }
    }

    /**
     * Method to remove given users from Selected Users list
     *
     * @param userNames List
     */
    public void removeUsers(List<String> userNames)
    {
        if (userNames == null || userNames.isEmpty())
        {
            throw new IllegalArgumentException("User Names list cannot be Null");
        }
        for (String userName : userNames)
        {
            removeUser(userName);
        }
    }

    /**
     * Method to select Close button
     */
    public void selectCloseButton()
    {
        try
        {
            drone.find(CLOSE_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Close Button", nse);
        }
    }

    /**
     * Method to select Cancel button
     */
    public void selectCancelButton()
    {
        try
        {
            getVisibleElement(CANCEL_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Close Button", nse);
        }
    }
}
