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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * New User page object, holds all element of the html page relating to
 * share's New User page. Enterprise only feature for the time being
 *
 * @author Meenal Bhave
 * @since 1.6.1
 */
public class NewUserPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final String FIRSTNAME = "input[id$='admin-console_x0023_default-create-firstname']";
    private static final String LASTNAME = "input[id$='admin-console_x0023_default-create-lastname']";
    private static final String EMAIL = "input[id$='admin-console_x0023_default-create-email']";
    private static final String USERNAME = "input[id$='admin-console_x0023_default-create-username']";
    private static final String PASSWORD = "input[id$='admin-console_x0023_default-create-password']";
    private static final String VERIFY_PASSWORD = "input[id$='admin-console_x0023_default-create-verifypassword']";
    private static final String GROUP_FINDER_SEARCH_TEXT = "input[id$='admin-console_x0023_default-create-groupfinder-search-text']";

    private static final String GROUP_SEARCH_BUTTON = "button[id$='default-create-groupfinder-group-search-button-button']";
    
    private static final String CREATE_USER = "button[id$='default-createuser-ok-button-button']";
    private static final String CREATE_ANOTHER_USER = "button[id$='default-createuser-another-button-button']";
    private static final String CANCEL_CREATE_USER = "button[id$='default-createuser-cancel-button-button']";

    private static final String USER_QUOTA = "input[id$='admin-console_x0023_default-create-quota']";
    private static final String DISABLE_ACCOUNT = "input[id$='admin-console_x0023_default-create-disableaccount']";
    private static final String TABLE_GROUP_NAMES = "div[id$='create-groupfinder-results'] tbody[class$='yui-dt-data']  tr[class*='yui-dt-rec']";
    private static final String ROW_GROUP_NAME = "td[class*='yui-dt-col-description'] div h3.itemname";
    private static final String GROUP_NAME = "td[class*='yui-dt-col-actions'] div span button";
    
    private static final String ADDED_GROUP_NAME = "div[style='visibility: visible;'] .groupselection-row";


    @SuppressWarnings("unchecked")
    @Override
    public NewUserPage render(RenderTime timer)
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
            if (isPageLoaded())
            {
                break;
            }
            timer.end();
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewUserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if admin Console title is present on the page
     *
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("Admin Console");
    }

    /**
     * Enter FirstName.
     */
    public void inputFirstName(String text)
    {
        WebElement input = findAndWait(By.cssSelector(FIRSTNAME));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter LastName.
     */
    public void inputLastName(String text)
    {
        WebElement input = findAndWait(By.cssSelector(LASTNAME));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter Email.
     */
    public void inputEmail(String text)
    {
        WebElement input = findAndWait(By.cssSelector(EMAIL));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter Username into username input field.
     */
    public void inputUsername(String text)
    {
        WebElement input = findAndWait(By.cssSelector(USERNAME));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter Password.
     */
    public void inputPassword(String text)
    {
        WebElement input = findAndWait(By.cssSelector(PASSWORD));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter VerifyPassword.
     */
    public void inputVerifyPassword(String text)
    {
        WebElement input = findAndWait(By.cssSelector(VERIFY_PASSWORD));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter Quota.
     */
    public void inputQuota(String text)
    {
        WebElement input = findAndWait(By.cssSelector(USER_QUOTA));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter the search text is group finder text box and clicks Search on the new user page.
     *
     * @param user String name
     * @return UserSearchPage page response
     */
    public HtmlPage searchGroup(final String user)
    {
        try
        {
            WebElement input = findAndWait(By.cssSelector(GROUP_FINDER_SEARCH_TEXT));
            input.clear();
            input.sendKeys(user);
            WebElement searchButton = findAndWait(By.cssSelector(GROUP_SEARCH_BUTTON));
            searchButton.click();
            if (searchButton.isEnabled())
            {
                 searchButton.click();
            }
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
        }
        throw new PageException("Not able to perform Group Search.");
    }

    /**
     * Checks if the group search button is displayed.
     *
     * @return true if button is displayed
     */
    protected boolean isPageLoaded()
    {
        boolean groupsFrameLoaded = false;
        boolean pageLoaded = false;
        try
        {
            WebElement element = driver.findElement(By.cssSelector(GROUP_SEARCH_BUTTON));
            groupsFrameLoaded = element.isDisplayed();
            
            WebElement button = driver.findElement(By.cssSelector(CREATE_USER));
            pageLoaded = button.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
        }
        return groupsFrameLoaded && pageLoaded;
    }

    
    /**
     * Clicks on Create User Button.
     * To get the error page there is wait added to delete disappear so it may not find the exact time taken to execute method,
     * there might be delay added during error condition.
     *
     * @return NewUserPage
     */
    public HtmlPage selectCreateUser()
    {
        WebElement createUser = findAndWait(By.cssSelector(CREATE_USER));
    	createUser.click();
//    	submit(By.cssSelector("button[id$='ok-button-button']"), ElementState.INVISIBLE);
        waitUntilAlert();
        return getCurrentPage().render();
    }

    /**
     * Clicks on Create and Create Another User button to invoke New User Page.
     *
     * @return NewUserPage
     */
    public HtmlPage selectCreateAnotherUser()
    {
        try
        {
            WebElement newUserButton = driver.findElement(By.cssSelector(CREATE_ANOTHER_USER));
            newUserButton.click();
            return getCurrentPage();
        }
        catch (NoSuchElementException te)
        {
        }
        throw new PageException("Not able to find Create Another User Button.");
    }

    /**
     * Clicks on Cancel button to cancel Create User.
     *
     * @return UserSearchPage
     */
    public HtmlPage cancelCreateUser()
    {
        try
        {
            WebElement element = findAndWait(By.cssSelector(CANCEL_CREATE_USER));
            element.click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to Click Cancel Create User Button.");
    }

    /**
     * Selects the Disable Account checkbox.
     */
    public void selectDisableAccount()
    {
        try
        {
            WebElement selectDisableAccount = driver.findElement(By.cssSelector(DISABLE_ACCOUNT));
            if (!selectDisableAccount.isSelected())
            {
                selectDisableAccount.click();
            }
        }
        catch (NoSuchElementException te)
        {
        }
    }

    /**
     * Method to create user on Enterprise using UI.
     *
     * @param userName String username
     * @param fname    String firstname
     * @param lname    String lastname
     * @param password String password
     * @return {@link UserSearchPage}
     */
    public HtmlPage createEnterpriseUser(String userName, String fname, String lname, String userEmail, String password)
    {
        entrpriseUserDetails(userName, fname, lname, userEmail, password);
        return selectCreateUser();
    }

    private void entrpriseUserDetails(String userName, String fname, String lname, String userEmail, String password)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new UnsupportedOperationException("User Name can't be empty or null, it is required.");
        }
        if (StringUtils.isEmpty(fname))
        {
            throw new UnsupportedOperationException("First Name can't be empty or null, it is required.");
        }
        if (StringUtils.isEmpty(userEmail))
        {
            throw new UnsupportedOperationException("User Email can't be empty or null, it is required.");
        }
        if (StringUtils.isEmpty(password))
        {
            throw new UnsupportedOperationException("Password can't be empty or null, it is required.");
        }
        if (lname == null)
        {
            throw new UnsupportedOperationException("Last Name can't be null, it is required.");
        }
        inputFirstName(fname);
        inputLastName(lname);
        inputEmail(userEmail);
        inputUsername(userName);
        inputPassword(password);
        inputVerifyPassword(password);
    }

    /**
     * Create user with a group.
     *
     * @param userName String
     * @param fname String
     * @param lname String
     * @param userEmail String
     * @param password String
     * @param groupName String
     * @return HtmlPage
     */
    public HtmlPage createEnterpriseUserWithGroup(String userName, String fname, String lname, String userEmail, String password, String groupName)
    {
        if (groupName == null)
        {
            throw new UnsupportedOperationException("Group Name can't be null, it is required.");
        }
        entrpriseUserDetails(userName, fname, lname, userEmail, password);
        searchGroup(groupName).render();
        boolean isGroupCreated  = addGroup(groupName);
        if(!isGroupCreated){
            logger.error("User["+userName+"] don't added to group["+groupName+"]!");
            throw new PageOperationException();
        }
        return selectCreateUser();
    }

    /**
     * Add group with User Name.
     *
     * @param groupName String
     * @return boolean
     */
    private boolean addGroup(String groupName)
    {
        if (StringUtils.isEmpty(groupName))
        {
            throw new IllegalArgumentException("Group Name can't be empty or null");
        }
        try
        {
            List<WebElement> webElements = findAndWaitForElements(By.cssSelector(TABLE_GROUP_NAMES));
            boolean isAdded = false;

            for (WebElement webElement : webElements)
            {
                if (groupName.equals(webElement.findElement(By.cssSelector(ROW_GROUP_NAME)).getText()))
                {
                    webElement.findElement(By.cssSelector(GROUP_NAME)).click();
                    WebElement groupAdded = findAndWait(By.cssSelector(ADDED_GROUP_NAME));
                    
                    isAdded = (groupName.matches(groupAdded.getText()));
                    break;
                }
            }
            if (!isAdded)
            {
                logger.error("Requested group doesn't exist.");
                throw new NoSuchElementException("Requested group doesn't exist.");
            }
            return isAdded;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Group name element not found!!", nse);
        }
        catch (TimeoutException toe)
        {
            logger.error("Group name element doesn't exist!!", toe);
        }
        throw new PageOperationException("Group doesn't exist!!");
    }
}
