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
package org.alfresco.po.share.site;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The class represents the Members page and handles the members page
 * funcationality. Replaced by AddUsersToSitePage.
 * 
 * 
 * TODO Jelena to update why this is not in use.
 * @author cbairaajoni
 * @version 1.6.1
 */
@Deprecated
public class InviteMembersPage extends SharePage
{

    private static Log logger = LogFactory.getLog(InviteMembersPage.class);

    // 1 - Search for People input field
    private static final String SEARCH_USER_ROLE_TEXT = "div.search-text>input";

    // Search for People Search button
    private static final String SEARCH_USER_ROLE_BUTTON = "button[id*='default-search-button-button']";
    private static final String SEARCH_IS_IN_PROGRESS_BUTTON = "//button[contains(@id,'people-finder') and contains(@disabled,'disabled')]";

    // List of users in search results (in 1 -Search for People Panel)
    private static final String LIST_OF_USERS = "div.results.yui-dt>table>tbody.yui-dt-data>tr";

    // List of invitees in 2 - Invite Users panel
    private static final String LIST_OF_INVITEES = "div.body.inviteelist.yui-dt>table>tbody.yui-dt-data>tr";

    // 2 -Invite Users panel + including ->
    private static final String INVITATION_LIST_PART = "div.invitationlistwrapper";

    // 1 - Search for People panel + Add External Users
    private static final String USER_SEARCH_PART = ".yui-u.first";

    // 2 -Invite Users panel - Select Role drop down values
    private static final String ROLES_DROP_DOWN_VALUES = "div[style*='visible']>div>div[style*='visible']>div.bd>ul>li";

    // Invite button under 2 - Invite Users panel
    private static final String INVITE_BUTTON = "div.invitationlist+div.sinvite>span>span>button";

    // Add user - 1 Search for People button -list of all buttons on the page ???
    private static final String BUTTON = "button[type=button]";

    // 1 - Search for People panel - list of (user name) in search results - 1st
    private static final String INVITEE = "td>div.yui-dt-liner>h3>span";

    // 2 -Invite Users panel - Select Role drop down button
    private static final String ROLES_DROP_DOWN_BUTTON = "div[style*='visible']>div>span>span>button";

    // 1 - Search for People panel - list of (user name) in search results - 2nd
    private static final By SEARCH_USER_FROM_LIST = By.cssSelector("td+td>div.yui-dt-liner>h3>span.lighter");

    // 1 - Search for People panel - list of (user name) in search results - 3rd (duplicates)
    private static final By SEACH_INVITEE_FROM_LIST = By.cssSelector("td+td>div.yui-dt-liner>h3>span.lighter");

    // 1 - Search for People panel - list of (first last) in search results
    private static final By SEARCH_USER_RESULTS = By.cssSelector(".itemname>a");

    // 2 -Invite Users panel - Set All Roles to button
    private static final By SELECT_ROLE_FOR_ALL = By.cssSelector("button[id$='selectallroles-button-button']");

    // ...Add External Users: First Name input field
    private static final By EXTERNAL_FIRST_NAME_INPUT = By.xpath("//input[contains(@id,'default-firstname')]");

    // ...Add External Users: Last Name input field
    private static final By EXTERNAL_LAST_NAME_INPUT = By.xpath("//input[contains(@id,'default-lastname')]");

    // ...Add External Users: Email input field
    private static final By EXTERNAL_EMAIL_INPUT = By.xpath("//input[contains(@id,'default-email')]");

    // ...Add External Users: Add button
    private static final By EXTERNAL_ADD_BUTTON = By.xpath("//button[contains(@id,'email-button-button')]");

    private static final String ADD_BUTTON_FOR_USER_XPATH = "//a[contains(text(),'%s')]/../../../..//button";
    private static final String SELECT_ROLE_BUTTON_FOR_USER_XPATH = "//div[contains(text(),'%s')]/../../..//button";
    private static final String REMOVE_USER_ICON_FOR_USER_XPATH = "//div[contains(text(),'%s')]/../../..//span[@class='removeIcon']/..";

    private final By linkGroup = By.cssSelector("a[id$='-site-groups-link']");
    private final By linkPeople = By.cssSelector("a[id$='-site-members-link']");
    private final By linkPendingInvites = By.cssSelector("a[id$='-pending-invites-link']");


    @SuppressWarnings("unchecked")
    @Override
    public InviteMembersPage render(RenderTime timer)
    {
        while (true)
        {
            try
            {
                timer.start();
                try
                {
                    driver.findElement(By.cssSelector(USER_SEARCH_PART));
                    driver.findElement(By.cssSelector(INVITATION_LIST_PART));
                    driver.findElement(By.cssSelector(SEARCH_USER_ROLE_TEXT));
                    driver.findElement(By.cssSelector(SEARCH_USER_ROLE_BUTTON));
                    driver.findElement(EXTERNAL_ADD_BUTTON);
                    driver.findElement(EXTERNAL_EMAIL_INPUT);
                    driver.findElement(EXTERNAL_FIRST_NAME_INPUT);
                    driver.findElement(EXTERNAL_LAST_NAME_INPUT);
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

    @SuppressWarnings("unchecked")
    @Override
    public InviteMembersPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public InviteMembersPage renderWithUserSearchResults(final long time)
    {
        return renderWithUserSearchResults(new RenderTime(time));
    }

    public InviteMembersPage renderWithUserSearchResults()
    {
        return renderWithUserSearchResults(new RenderTime(maxPageLoadingTime));
    }

    public InviteMembersPage renderWithUserSearchResults(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(SEARCH_USER_RESULTS));
        return this;
    }

    /**
     * Verify if home page banner web element is present
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        boolean isPresent = false;
        String title = getPageTitle();
        if (title != null)
        {
            isPresent = title.contains("Members");
        }
        return isPresent;
    }

    /**
     * This method search for the given userName and returns the list of
     * results.
     * 
     * @param userName String identifier
     * @return List<String> list of users
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
            WebElement searchTextBox = findAndWait(By.cssSelector(SEARCH_USER_ROLE_TEXT));
            searchTextBox.clear();
            searchTextBox.sendKeys(userName);

            findAndWait(By.cssSelector(SEARCH_USER_ROLE_BUTTON)).click();
            waitUntilElementDisappears(By.xpath(SEARCH_IS_IN_PROGRESS_BUTTON), 25);

            List<WebElement> users = getListOfInvitees();
            if (users != null && !users.isEmpty())
            {
                List<String> userNames = new ArrayList<String>();
                for (WebElement element : users)
                {
                    userNames.add(element.findElement(SEARCH_USER_FROM_LIST).getText());
                }
                return userNames;
            }
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the username for the userElement.", e);
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
    public HtmlPage clickAddUser(String user)
    {
        if (user == null || user.isEmpty())
        {
            throw new UnsupportedOperationException("user input required");
        }

        List<WebElement> invitees = getListOfInvitees();
        if (invitees != null)
        {
            for (WebElement element : getListOfInvitees())
            {
                WebElement ele = null;
                try
                {
                    ele = element.findElement(SEACH_INVITEE_FROM_LIST);
                    String value = ele.getText();

                    if (value != null && !value.isEmpty())
                    {
                        if (value.contains(user))
                        {
                            element.findElement(By.cssSelector(BUTTON)).click();
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

        return getCurrentPage();
    }

    /**
     * This method gets the list of invitees present.
     * 
     * @return List<WebElement>
     */

    private List<WebElement> getInvitees()
    {
        try
        {
            return findAndWaitForElements(By.cssSelector(LIST_OF_INVITEES));
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time exceeded to find the invitees list." + e);
            }
        }

        return Collections.emptyList();
    }

    /**
     * This method returns the selected invitee based on the given user element.
     * 
     * @param user String identifier
     * @param role UserRole
     * @return InviteMembersPage page response
     */
    public HtmlPage selectInviteeAndAssignRole(String user, UserRole role)
    {
        List<WebElement> inviteesList = getInvitees();
        if (user == null || inviteesList == null || inviteesList.isEmpty() || role == null)
        {
            throw new UnsupportedOperationException("user input required or inviteesList should not be blank.");
        }

        for (WebElement link : inviteesList)
        {
            try
            {
                WebElement invitee = link.findElement(By.cssSelector(INVITEE));
                String text = invitee.getText();

                if (text != null && user.equalsIgnoreCase(text))
                {
                    selectRolesDropdown();
                    getRoles();
                    assignRole(role);
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Unable to find the invitee css.", e);
                }
            }
        }
        return getCurrentPage();
    }

    /**
     * This method gets list of drop down values as roles.
     */
    private void selectRolesDropdown()
    {
        findAndWait(By.cssSelector(ROLES_DROP_DOWN_BUTTON)).click();
    }

    /**
     * The filters of the Site content those are diplayed in filters dropdown.
     * 
     * @return <List<WebElement>>
     */
    private List<WebElement> getRoles()
    {
        return findAndWaitForElements(By.cssSelector(ROLES_DROP_DOWN_VALUES), maxPageLoadingTime);
    }

    /**
     * This method assigns role from dropdown values.
     * 
     * @param roleName {@link UserRole}
     * @return {@link InviteMembersPage}
     */
    private HtmlPage assignRole(UserRole roleName)
    {
        if (roleName == null)
        {
            throw new UnsupportedOperationException("Role Name input required.");
        }
        for (WebElement role : getRoles())
        {
            if (role.getText().equalsIgnoreCase(roleName.getRoleName()))
            {
                role.click();
            }
        }
        return getCurrentPage();
    }

    /**
     * This method clicks on Invite button on Invite user page.
     * 
     * @return {@link InviteMembersPage} page response
     */
    public HtmlPage clickInviteButton()
    {
        findAndWait(By.cssSelector(INVITE_BUTTON), maxPageLoadingTime).click();
        waitUntilAlert();
        return getCurrentPage();
    }

    /**
     * This method does the inviting user and assigning the new role as given
     * role value.
     * 
     * @param user String identifier
     * @param role {@link UserRole}
     * @return {@link InviteMembersPage}
     */
    public HtmlPage selectRole(String user, UserRole role)
    {
        clickAddUser(user);
        selectInviteeAndAssignRole(user, role);
        return getCurrentPage();
    }

    /**
     * Get List of users who are invited.
     * 
     * @return List<WebElement> Collection of invited users
     */
    private List<WebElement> getListOfInvitees()
    {
        try
        {
            return findAndWaitForElements(By.cssSelector(LIST_OF_USERS));
        }
        catch (TimeoutException e)
        {
            logger.info("Users don't found. Returned empty list.");
        }
        return Collections.emptyList();
    }

    /**
     * Navigate to Site Groups.
     * 
     * @return SiteGroupsPage
     */
    public HtmlPage navigateToSiteGroupsPage()
    {
        try
        {
            click(linkGroup);
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not found Element:" + linkGroup, nse);
        }
    }

    public void removeUserFromInvite(String userName)
    {
        By removeUsers = By.xpath(String.format(REMOVE_USER_ICON_FOR_USER_XPATH, userName));
        findAndWait(removeUsers).click();
    }

    /**
     * @param role UserRole
     */
    public void selectRoleForAll(UserRole role)
    {
        findAndWait(SELECT_ROLE_FOR_ALL).click();
        findAndWait(By.xpath(String.format("//a[contains(text(),'%s')]", role.getRoleName()))).click();
    }

    /**
     * true if 'Add' button displayed and enabled for user.
     * 
     * @param userName String
     * @return boolean
     */
    public boolean isAddButtonEnabledFor(String userName)
    {
        return isSmThEnabledFor(userName, ADD_BUTTON_FOR_USER_XPATH);
    }

    /**
     * @return boolean
     */
    public boolean isInviteButtonEnabled()
    {
        try
        {
            WebElement inviteButton = driver.findElement(By.cssSelector(INVITE_BUTTON));
            return inviteButton.isDisplayed() && inviteButton.isEnabled();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not found Element:" + INVITE_BUTTON, e);
        }
    }

    /**
     * true if 'SelectRole' button displayed and enabled for user.
     * 
     * @param userName String
     * @return boolean
     */
    public boolean isSelectRoleEnabledFor(String userName)
    {
        return isSmThEnabledFor(userName, SELECT_ROLE_BUTTON_FOR_USER_XPATH);
    }

    /**
     * true if 'Remove Icon' image displayed and enabled for user.
     * 
     * @param userName String
     * @return boolean
     */
    public boolean isRemoveIconEnabledFor(String userName)
    {
        return isSmThEnabledFor(userName, REMOVE_USER_ICON_FOR_USER_XPATH);
    }

    /**
     * Mimic click on People link
     * 
     * @return SiteMembersPage
     */
    public HtmlPage navigateToMembersSitePage()
    {
        try
        {
            click(linkPeople);
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not found Element:" + linkPeople, nse);
        }
    }

    /**
     * Mimic click on Pending Invites link
     * 
     * @return PendingInvitesPage
     */
    public HtmlPage navigateToPendingInvitesPage()
    {
        try
        {
            click(linkPendingInvites);
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not found Element:" + linkPendingInvites, nse);
        }
    }

    /**
     * Send external invitation to email for some user
     * 
     * @param firstName String
     * @param lastName String
     * @param email String
     * @param userRole UserRole
     */

    public void invExternalUser(String firstName, String lastName, String email, UserRole userRole)
    {
        fillField(EXTERNAL_FIRST_NAME_INPUT, firstName);
        fillField(EXTERNAL_LAST_NAME_INPUT, lastName);
        fillField(EXTERNAL_EMAIL_INPUT, email);
        click(EXTERNAL_ADD_BUTTON);
        selectRoleForAll(userRole);
        clickInviteButton();
    }

    private boolean isSmThEnabledFor(String userName, String smthXpath)
    {
        By smthElement = By.xpath(String.format(smthXpath, userName));
        try
        {
            WebElement addButton = driver.findElement(smthElement);
            return addButton.isDisplayed() && addButton.isEnabled();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not found Element:" + smthElement, e);
        }
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }

}
