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

package org.alfresco.po.share.site;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.enums.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for AddUsersToSitePage
 * 
 * @author jcule
 */
public class AddUsersToSitePage extends SitePage
{
    private static Log logger = LogFactory.getLog(AddUsersToSitePage.class);

    /**
     * 1 - Search for Users panel:
     * Search for users input field
     * Search button
     * List of user search results with Select buttons
     */

    // Search for Users input field
    @FindBy(css = "div.search-text>input")
    WebElement searchTextBox;

    // Search for Users button
    private static final String SEARCH_USER_BUTTON = "button[id*='default-search-button-button']";
    private static final String SEARCH_IS_IN_PROGRESS_BUTTON = "//button[contains(@id,'people-finder') and contains(@disabled,'disabled')]";

    // List of users in user search results
    private static final String SEARCH_RESULTS_USERS = "div.results.yui-dt>table>tbody.yui-dt-data>tr";

    // List of (first last) names in user search results
    private static final By SEARCH_RESULTS_USER_FIRST_LAST = By.cssSelector(".itemname>a");

    // List of (user name) in search results
    private static final By SEARCH_RESULTS_USER_NAMES = By.cssSelector("td+td>div.yui-dt-liner>h3>span.lighter");

    // Add user - 1 Search for People button -list of all buttons on the page ???
    //private static final String SELECT_USER_BUTTONS = "//button[contains(text(),'Select')]";
    private static final String SELECT_USER_BUTTONS = "//span[contains(@id, '%s')]//button[contains(text(),'Select')]";

    /**
     * 2 - Set User Roles panel:
     * Info tooltip button
     * Tooltip header
     * Set All Roles to button
     * Set All Roles to drop down values
     * Select Role button
     * Select Role button drop down values
     * List of invitees
     * List of invitees user names
     */

    // Info tooltip
    private static final By INFO_TOOLTIP_BUTTON = By.cssSelector("button[id$='default-role-info-button-button']");

    // Role info tooltip
    private static final By ROLE_INFOTOOLTIP = By.cssSelector(".alf-info-balloon");

    // Set All Roles to button
    private static final By SET_ALL_ROLES_TO_BUTTON = By.cssSelector("button[id$='selectallroles-button-button']");

    // Set All Roles to drop down values
    private static final By SET_ALL_ROLES_TO_DROP_DOWN_VALUES = By.cssSelector("div[style*='visible']>div>ul.first-of-type>li");

    // Select Role button
    // private static final By SELECT_ROLE_BUTTONS = By.cssSelector("div[style*='visible']>div>span>span>button");
    // private static final String ROLES_DROP_DOWN_BUTTON = "div[style*='visible']>div>span>span>button";
    // private static final String SELECT_ROLE_BUTTONS = "div[style*='visible']>div>span>span>button";
    private static final String SELECT_ROLE_BUTTONS = "//button[contains(text(),'Select Role')]";

    // Select Role button drop down values
    // private static final By SELECT_ROLE_DROP_DOWN_VALUES = By.cssSelector("div[style*='visible']>div>span>span>button");
    private static final String SELECT_ROLE_DROP_DOWN_VALUES = "div[style*='visible']>div>div[style*='visible']>div.bd>ul>li";

    // List of invitees
    private static final String LIST_OF_SELECTED_USERS = "div.body.inviteelist.yui-dt>table>tbody.yui-dt-data>tr";

    // List of invitees user names
    private static final String LIST_OF_SELECTED_USERS_USER_NAMES = "td>div.yui-dt-liner>h3>span";

    // Remove selected user icon
    private static final String REMOVE_SELECTED_USER = "//div[contains(text(),'%s')]/../../..//span[@class='removeIcon']/..";

    /**
     * 3 - Add Users panel:
     * Add Users button
     * You haven't added any users yet message
     * Total users added text
     * Total users added number
     * Added users user names
     * Added users roles
     */

    // Add Users button
    private static final String ADD_USERS_BUTTON = "button[id$='_default-invite-button-button']";
    // private static final String ADD_USERS_BUTTON = "div[contains(@id, '_default-added-users-list')]//button[text() = 'Add']";

    // You haven't added any users yet message
    private static final By YOU_HAVE_NOT_ADDED_ANY_USERS_MESSAGE = By.cssSelector("div[class^='added-users-list-message']");

    // Total users added text
    private static final By TOTAL_USERS_ADDED_TEXT = By.cssSelector("");

    // Total users added count
    private static final By TOTAL_USERS_ADDED_COUNT = By.cssSelector("div[class^='added-users-list-tally']");

    // Added users list
    // private static final By ADDED_USERS_LIST = By.cssSelector(".alfresco-lists-views-layouts-Row");

    // Added users names list
    // private static final By ADDED_USERS_NAMES_LIST = By.xpath("//tr[starts-with(@id, 'alfresco_lists_views_layouts_Row')]/td[1]/span[1]/span/span[2]");
    private static final By ADDED_USERS_NAMES_LIST = By.cssSelector("td[class$='yui-dt-first'] div h3.itemname");

    // Added users roles list
    // private static final By ADDED_USERS_ROLES_LIST = By.xpath("//tr[starts-with(@id, 'alfresco_lists_views_layouts_Row')]/td[1]/span[2]/span/span[2]");
    private static final By ADDED_USERS_ROLES_LIST = By.cssSelector("h3.itemname~div.detail");

    /**
     * 4 - External Users panel:
     * First name input field
     * Last name input field
     * Email input field
     * Select external user button
     */

    // ...Add External Users: First Name input field
    private static final By EXTERNAL_FIRST_NAME_INPUT = By.xpath("//input[contains(@id,'default-firstname')]");

    // ...Add External Users: Last Name input field
    private static final By EXTERNAL_LAST_NAME_INPUT = By.xpath("//input[contains(@id,'default-lastname')]");

    // ...Add External Users: Email input field
    private static final By EXTERNAL_EMAIL_INPUT = By.xpath("//input[contains(@id,'default-email')]");

    // ...Add External Users: Add button
    private static final By EXTERNAL_ADD_BUTTON = By.xpath("//button[contains(@id,'email-button-button')]");

    private final By linkGroups = By.linkText("Groups");
    private final By linkUsers = By.linkText("Users");
    private final By linkPendingInvites = By.linkText("Pending");

    @SuppressWarnings("unchecked")
    @Override
    public AddUsersToSitePage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        while (true)
        {
            try
            {
                timer.start();
                try
                {
                    searchTextBox.isDisplayed();
                    driver.findElement(By.cssSelector(SEARCH_USER_BUTTON));
                    driver.findElement(SET_ALL_ROLES_TO_BUTTON);
                    break;
                }
                catch (NoSuchElementException pe)
                {
                }
            }
            finally
            {
                timer.end();
            }
        }

        return this;
    }

    public AddUsersToSitePage renderWithUserSearchResults(final long time)
    {
        return renderWithUserSearchResults(new RenderTime(time));
    }

    public AddUsersToSitePage renderWithUserSearchResults()
    {
        return renderWithUserSearchResults(new RenderTime(maxPageLoadingTime));
    }

    public AddUsersToSitePage renderWithUserSearchResults(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            if (findAndWait(SEARCH_RESULTS_USER_FIRST_LAST).isDisplayed())
            {
                return this;
            }
            timer.end();
        }
    }

    /**
     * Mimic click on Users link
     * 
     * @return
     */
    public SiteMembersPage navigateToMembersSitePage()
    {
        try
        {
            findAndWait(linkUsers).click();
            return getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Users link.", te);
            }
            throw new PageException("Not found Element:" + linkUsers, te);
        }
    }

    /**
     * Navigate to Site Groups.
     * 
     * @return
     */
    public SiteGroupsPage navigateToSiteGroupsPage()
    {
        try
        {
            findAndWait(linkGroups).click();
            return getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            throw new PageException("Not found Element:" + linkGroups, te);
        }
    }

    /**
     * Mimic click on Pending Invites link
     * 
     * @return
     */
    public PendingInvitesPage navigateToPendingInvitesPage()
    {
        try
        {
            findAndWait(linkPendingInvites).click();
            return getCurrentPage().render();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not found Element:" + linkPendingInvites, nse);
        }
    }

    /**
     * 1 - Search for Users panel:
     * getUserSearchResults()
     * List<String> searchUser(String userName)
     * clickSelectUser()
     */

    /**
     * Get List of search results users
     * 
     * @return List<WebElement> Collection of search results users
     */
    private List<WebElement> getUserSearchResults()
    {
        try
        {
            List<WebElement> userSearchResults = driver.findElements(By.cssSelector(SEARCH_RESULTS_USERS));
            return userSearchResults;
        }
        catch (NoSuchElementException e)
        {
            logger.info("Users don't found. Returned empty list.", e);
        }
        return Collections.emptyList();
    }

    /**
     * Enters the userName in the user search input field and returns the list of search results usernames.
     * 
     * @param userName String identifier
     * @return List<String> list of user names
     */

    public List<String> searchUser(String userName)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Members page: searchUser :" + userName);
        }

        if (userName == null || userName.trim().isEmpty())
        {
            throw new UnsupportedOperationException("userName input required");
        }

        try
        {
            searchTextBox.clear();
            searchTextBox.sendKeys(userName);

            findAndWait(By.cssSelector(SEARCH_USER_BUTTON)).click();
            waitUntilElementDisappears(By.xpath(SEARCH_IS_IN_PROGRESS_BUTTON), 25);

            List<WebElement> users = getUserSearchResults();
            if (users != null && !users.isEmpty())
            {
                List<String> userNames = new ArrayList<String>();
                for (WebElement element : users)
                {
                    userNames.add(element.findElement(SEARCH_RESULTS_USER_NAMES).getText());
                }
                return userNames;
            }
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the username for the userElement.", te);
            }
        }
        return Collections.emptyList();
    }

    /**
     * This method clicks on add button.
     * 
     * @param user String user identifier
     * @return {@link InviteMembersPage} page response
     */
    public AddUsersToSitePage clickSelectUser(String user)
    {
        if (user == null || user.isEmpty())
        {
            throw new UnsupportedOperationException("user input required");
        }
        List<WebElement> searchResults = getUserSearchResults();
        if (searchResults != null)
        {
            for (WebElement searchResult : searchResults)
            {
                WebElement element = null;
                try
                {
                    element = searchResult.findElement(SEARCH_RESULTS_USER_NAMES);
                    String value = element.getText();
                    if (value != null && !value.isEmpty())
                    {
                        if (value.indexOf(user) != -1)
                        {
                            searchResult.findElement(By.xpath(String.format(SELECT_USER_BUTTONS, user))).click();
                            break;
                        }
                    }
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Unable to find the username for the userElement.");
                    throw new PageException("Unable to find the username for the userElement.", e);
                }
            }
        }

        return factoryPage.instantiatePage(driver, AddUsersToSitePage.class);
    }

    /**
     * 2 - Set User Roles panel:
     * clickOnInfoTooltip()
     * getSelectedUsers()
     * getSelectedUserNames()
     * selectRole()
     * setUserRoles()
     * addUsers()
     * removeSelectedUser()
     */

    /**
     * Clickcks on info tooltil button in select role panel
     * 
     * @return
     */
    public AddUsersToSitePage clickOnInfoTooltip()
    {
        try
        {
            findAndWait(INFO_TOOLTIP_BUTTON, maxPageLoadingTime).click();

        }
        catch (TimeoutException te)
        {
            logger.info("Cannot find info tooltip button.", te);
        }
        return factoryPage.instantiatePage(driver, AddUsersToSitePage.class);
    }

    /**
     * Returns true if tooltip
     * 
     * @return
     */
    public boolean isRoleInfoTooltipDisplayed()
    {
        try
        {
            return findAndWait(ROLE_INFOTOOLTIP).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            logger.info("Cannot find info tooltip header.", nse);
        }
        return false;
    }

    /**
     * This method gets the list of selected users.
     * 
     * @return List<WebElement>
     */
    private List<WebElement> getSelectedUsers()
    {
        try
        {
            return driver.findElements(By.cssSelector(LIST_OF_SELECTED_USERS));
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time exceeded to find the selected users list." + e);
            }
        }

        return Collections.emptyList();
    }

    /**
     * This method gets the list of selected user names.
     * 
     * @return List<WebElement>
     */

    public List<String> getSelectedUserNames()
    {
        try
        {
            List<String> selectedUserNames = new ArrayList<String>();
            List<WebElement> selectedUsers = driver.findElements(By.cssSelector(LIST_OF_SELECTED_USERS_USER_NAMES));
            for (WebElement selectedUser : selectedUsers)
            {
                selectedUserNames.add(selectedUser.getText());
            }
            return selectedUserNames;
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time exceeded to find the selected users list." + e);
            }
        }

        return Collections.emptyList();
    }

    /**
     * This method selects role from drop down values.
     * 
     * @param roleName {@link UserRole}
     * @return {@link InviteMembersPage}
     */
    private AddUsersToSitePage selectRole(UserRole roleName)
    {
        if (roleName == null)
        {
            throw new UnsupportedOperationException("Role Name input required.");
        }

        try
        {
            List<WebElement> roles = findAndWaitForElements(By.cssSelector(SELECT_ROLE_DROP_DOWN_VALUES), maxPageLoadingTime);
            for (WebElement role : roles)
            {
                if (role.getText().equalsIgnoreCase(roleName.getRoleName()))
                {
                    role.click();
                }
            }

        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find roles css.", te);
            }
        }
        return factoryPage.instantiatePage(driver, AddUsersToSitePage.class);
    }

    /**
     * This method selects user role based on the given user element.
     * 
     * @param user String identifier
     * @param role UserRole
     * @return AddUsersToSitePage page response
     */
    public AddUsersToSitePage setUserRoles(String user, UserRole role)
    {
        List<WebElement> selectedUsersList = getSelectedUsers();
        if (user == null || selectedUsersList == null || selectedUsersList.isEmpty() || role == null)
        {
            throw new UnsupportedOperationException("user input required or selected users list should not be blank.");
        }

        for (WebElement selectedUser : selectedUsersList)
        {
            try
            {
                WebElement userNameElement = selectedUser.findElement(By.cssSelector(LIST_OF_SELECTED_USERS_USER_NAMES));
                String userName = userNameElement.getText();
                if (userName != null && userName.indexOf(user) != -1)
                {
                    findAndWait(By.xpath(SELECT_ROLE_BUTTONS), maxPageLoadingTime).click();
                    selectRole(role);
                    break;
                }
            }
            catch (TimeoutException te)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Unable to find selected user css.", te);
                }
            }
        }
        return factoryPage.instantiatePage(driver, AddUsersToSitePage.class);
    }

    /**
     * Clicks on the Set All Roles to button and selects the role from the drop down
     * 
     * @param role
     */
    public void setAllRolesTo(UserRole role)
    {
        findAndWait(SET_ALL_ROLES_TO_BUTTON).click();
        findAndWait(By.xpath(String.format("//a[.='%s']", role.getRoleName()))).click();
    }

    /**
     * Clicks on remove selected user icon
     * 
     * @param userName
     */
    public void removeSelectedUser(String userName)
    {
        By removeUsers = By.xpath(String.format(REMOVE_SELECTED_USER, userName));
        findAndWait(removeUsers).click();
    }

    /**
     * 3 - Add Users panel:
     * getTotalAddedUsersCount()
     * getAddedUsers()
     * clickAddUsersButton()
     */

    /**
     * Get total added users count
     * 
     * @return String
     */
    public String getTotalAddedUsersCount()
    {
        String totalAddedUsersCount = "";
        try
        {
            return driver.findElement(TOTAL_USERS_ADDED_COUNT).getText();
        }
        catch (NoSuchElementException e)
        {
            logger.info("Added users count don't found. Returned empty list.", e);
        }
        return totalAddedUsersCount;

    }

    /**
     * Get added user user name
     * 
     * @return String user name
     */
    public List<String> getAddedUsersNames()
    {
        try
        {
            List<String> addedUserNames = new ArrayList<String>();
            List<WebElement> addedUsers = driver.findElements(ADDED_USERS_NAMES_LIST);
            for (WebElement addedUser : addedUsers)
            {
                addedUserNames.add(addedUser.getText());
            }
            return addedUserNames;
        }
        catch (NoSuchElementException e)
        {
            logger.info("Added users don't found. Returned empty list.", e);
        }
        return Collections.emptyList();
    }

    /**
     * This method clicks on Add Users button on Invite user page.
     * 
     * @return {@link InviteMembersPage} page response
     */
    public AddUsersToSitePage clickAddUsersButton()
    {
        findAndWait(By.cssSelector(ADD_USERS_BUTTON), maxPageLoadingTime).click();
        waitUntilAlert();
        return factoryPage.instantiatePage(driver, AddUsersToSitePage.class);
    }

    /**
     * This method returns added users roles.
     * 
     * @param user String identifier
     * @return String user name
     */
    public List<String> getAddedUsersRoles()
    {
        try
        {
            List<String> addedUserRoles = new ArrayList<String>();
            List<WebElement> addedRoles = driver.findElements(ADDED_USERS_ROLES_LIST);
            for (WebElement addedRole : addedRoles)
            {
                addedUserRoles.add(addedRole.getText());
            }
            return addedUserRoles;
        }
        catch (NoSuchElementException e)
        {
            logger.info("Added users roles don't found. Returned empty list.", e);
        }
        return Collections.emptyList();
    }

    /**
     * 4 - External Users panel:
     * enterExternalUserFirstName()
     * enterExternalUserLastName()
     * enterExternalUserEmail()
     * Select external user button
     */

    /**
     * Enters external user's first name in the input field
     * 
     * @param firstName
     */
    public void enterExternalUserFirstName(String firstName)
    {
        checkNotNull(firstName);
        WebElement inputField = findAndWait(EXTERNAL_FIRST_NAME_INPUT);
        inputField.clear();
        inputField.sendKeys(firstName);
    }

    /**
     * Enters external user's last name in the input field
     * 
     * @param lastName
     */
    public void enterExternalUserLastName(String lastName)
    {
        checkNotNull(lastName);
        WebElement inputField = findAndWait(EXTERNAL_LAST_NAME_INPUT);
        inputField.clear();
        inputField.sendKeys(lastName);
    }

    /**
     * Enters external user's email in the input field
     * 
     * @param email
     */
    public void enterExternalUserEmail(String email)
    {
        checkNotNull(email);
        WebElement inputField = findAndWait(EXTERNAL_EMAIL_INPUT);
        inputField.clear();
        inputField.sendKeys(email);

    }

    /**
     * Clicks on Select external users button
     * 
     * @return {@link AddUsersToSitePage}
     */
    public AddUsersToSitePage selectExternalUser()
    {
        try
        {
            findAndWait(EXTERNAL_ADD_BUTTON, maxPageLoadingTime).click();
        }
        catch (TimeoutException te)
        {
            logger.info("Cannot find Select external user button.", te);
        }
        return factoryPage.instantiatePage(driver, AddUsersToSitePage.class);
    }
}
