/*
 * Copyright (C) 2005-2013 Alfresco Software Limited. This file is part of Alfresco Alfresco is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Alfresco is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import java.util.List;

import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
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

/**
 * New User page object, holds all element of the html page relating to share's Edit User page. Enterprise only feature for the time being
 * 
 * @author Meenal Bhave
 * @since 1.7
 */
public class EditUserPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final String FIRSTNAME = "input[id$='default-update-firstname']";
    private static final String LASTNAME = "input[id$='default-update-lastname']";
    private static final String EMAIL = "input[id$='default-update-email']";

    private static final String PASSWORD = "input[id$='default-update-password']";
    private static final String VERIFY_PASSWORD = "input[id$='default-update-verifypassword']";
    private static final String GROUP_FINDER_SEARCH_TEXT = "input[id$='default-update-groupfinder-search-text']";

    private static final String GROUP_SEARCH_BUTTON = "button[id$='default-update-groupfinder-group-search-button-button']";
    private static final String SAVE_CHANGES = "button[id$='default-updateuser-save-button-button']";
    private static final String USE_DEFAULT_PHOTO = "button[id$='default-updateuser-clearphoto-button-button']";
    private static final String CANCEL_EDIT_USER = "button[id$='default-updateuser-cancel-button-button']";

    private static final String USER_QUOTA = "input[id$='default-update-quota']";
    private static final String DISABLE_ACCOUNT = "input[id$='default-update-disableaccount']";
    private static final String TABLE_GROUP_NAMES = "div[id$='update-groupfinder-results'] tbody[class$='yui-dt-data']  tr[class*='yui-dt-rec']";
    private static final String ROW_GROUP_NAME = "td[class*='yui-dt-col-description'] div h3.itemname";
    private static final String GROUP_NAME = "td[class*='yui-dt-col-actions'] div span button";

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public EditUserPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditUserPage render(RenderTime timer)
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
    public EditUserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditUserPage render(final long time)
    {
        return render(new RenderTime(time));
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
    public void editFirstName(String text)
    {
        WebElement input = drone.findAndWait(By.cssSelector(FIRSTNAME));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter LastName.
     */
    public void editLastName(String text)
    {
        WebElement input = drone.findAndWait(By.cssSelector(LASTNAME));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter Email.
     */
    public void editEmail(String text)
    {
        WebElement input = drone.findAndWait(By.cssSelector(EMAIL));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Clicks on the Use Default Button for te User Profile Photo.
     */
    public EditUserPage selectUseDefault()
    {
        WebElement button = drone.findAndWait(By.cssSelector(USE_DEFAULT_PHOTO));
        button.click();
        return new EditUserPage(drone);

    }

    /**
     * Enter Password.
     */
    public void editPassword(String text)
    {
        WebElement input = drone.findAndWait(By.cssSelector(PASSWORD));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter VerifyPassword.
     */
    public void editVerifyPassword(String text)
    {
        WebElement input = drone.findAndWait(By.cssSelector(VERIFY_PASSWORD));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter Quota.
     */
    public void editQuota(String text)
    {
        WebElement input = drone.findAndWait(By.cssSelector(USER_QUOTA));
        input.clear();
        input.sendKeys(text);
    }

    /**
     * Enter the search text is group finder text box and clicks Search on the new user page.
     * 
     * @param user String name
     * @return UserSearchPage page response
     */
    public EditUserPage searchGroup(final String user)
    {
        try
        {
            WebElement input = drone.findAndWait(By.cssSelector(GROUP_FINDER_SEARCH_TEXT));
            input.clear();
            input.sendKeys(user);
            drone.findAndWait(By.cssSelector(GROUP_SEARCH_BUTTON)).click();
            return new EditUserPage(drone);
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
        try
        {
            WebElement element = drone.find(By.cssSelector(GROUP_SEARCH_BUTTON));
            groupsFrameLoaded = element.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
        }
        return groupsFrameLoaded;
    }

    /**
     * Clicks on Save Changes Button. To get the error page there is wait added to delete disappear so it may not find the exact time taken to execute method,
     * there might be delay added during error condition.
     * 
     * @return NewUserPage
     */
    public HtmlPage saveChanges()
    {
        return submit(By.cssSelector(SAVE_CHANGES), ElementState.INVISIBLE);
    }

    /**
     * Clicks on Cancel button to cancel Create User.
     * 
     * @return UserSearchPage
     */
    public UserSearchPage cancelEditUser()
    {
        try
        {
            WebElement element = drone.findAndWait(By.cssSelector(CANCEL_EDIT_USER));
            element.click();
            return new UserSearchPage(drone);
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to Click Cancel Edit User Button.");
    }

    /**
     * Selects the Disable Account checkbox.
     */
    public void selectDisableAccount()
    {
        try
        {
            WebElement selectDisableAccount = drone.find(By.cssSelector(DISABLE_ACCOUNT));
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
     * @param fName String firstname
     * @param lName String lastname
     * @param password String password
     * @param addToGroup String name of the group to be added
     * @return {@link UserSearchPage}
     */
    public HtmlPage editEnterpriseUser(String fname, String lname, String userEmail, String password, String addToGroup)
    {
        if (!StringUtils.isEmpty(fname))
        {
            editFirstName(fname);
        }
        if (!StringUtils.isEmpty(lname))
        {
            editLastName(lname);
        }
        if (!StringUtils.isEmpty(userEmail))
        {
            editEmail(userEmail);
        }
        if (!StringUtils.isEmpty(password))
        {
            editPassword(password);
            editVerifyPassword(VERIFY_PASSWORD);
        }
        if (!StringUtils.isEmpty(addToGroup))
        {
            addGroup(addToGroup);
        }
        return saveChanges();
    }

    /**
     * Add group with User Name.
     * 
     * @param groupName
     * @return
     */
    public EditUserPage addGroup(String groupName)
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This operation is not supported for CLOUD");
        }
        if (StringUtils.isEmpty(groupName))
        {
            throw new IllegalArgumentException("Group Name can't be empty or null");
        }
        try
        {
            if (hasGroups())
            {
                List<WebElement> webElements = drone.findAndWaitForElements(By.cssSelector(TABLE_GROUP_NAMES));
                boolean isAdded = false;
                for (WebElement webElement : webElements)
                {
                    if (groupName.equals(webElement.findElement(By.cssSelector(ROW_GROUP_NAME)).getText()))
                    {
                        webElement.findElement(By.cssSelector(GROUP_NAME)).click();
                        isAdded = true;
                        break;
                    }
                }
                if (!isAdded)
                {
                    logger.error("Requested group could not be added");
                    throw new NoSuchElementException("Requested group could not be added");
                }
                return new EditUserPage(drone);
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Group could not be located");
        }
        catch (TimeoutException toe)
        {
            logger.error("Timed out: Group could not be located");
        }
        throw new PageOperationException("Group could not be located");
    }

    /**
     * Verify if groups table contains user groups
     * 
     * @return true if groups is displayed.
     */
    public boolean hasGroups()
    {
        try
        {
            WebElement element = drone.find(By.cssSelector(TABLE_GROUP_NAMES));
            String text = element.getText();
            if (text != null)
            {
                if (!text.equalsIgnoreCase("No groups found") || !text.equalsIgnoreCase("Enter a search term to find groups"))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Deselects the Disable Account checkbox.
     */
    public void deSelectDisableAccount()
    {
        try
        {
            WebElement selectDisableAccount = drone.find(By.cssSelector(DISABLE_ACCOUNT));
            if (selectDisableAccount.isSelected())
            {
                selectDisableAccount.click();
            }
        }
        catch (NoSuchElementException te)
        {
        }
    }
}
