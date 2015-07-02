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

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * The class represents the Site Members page and handles the site members page
 * functionality.
 *
 * @author cbairaajoni
 * @version 1.6.2
 */
public class SiteMembersPage extends SharePage
{

    private static Log logger = LogFactory.getLog(SiteMembersPage.class);
    private static final By SEARCH_USER_ROLE_TEXT = By.cssSelector("input.search-term");
    private static final By SEARCH_USER_ROLE_BUTTON = By.cssSelector("button[id$='default-button-button']");
    private static final By LIST_OF_USERS = By.cssSelector("tbody.yui-dt-data>tr");
    private static final By USER_NAME_FROM_LIST = By.cssSelector("td+td>div.yui-dt-liner>h3>a");
    private static final By USER_SEARCH_PART = By.cssSelector("div.sticky-wrapper");
    private static final String ROLES_DROP_DOWN_VALUES_CSS_PART_1 = "span[id$='";
    private static final String ROLES_DROP_DOWN_VALUES_CSS_PART_2 = "']>div[class*='yui-menu-button-menu']>div>ul>li";
    private static final String ROLES_DROP_DOWN_BUTTON_CSS_PART_1 = "span[id$='";
    private static final String ROLES_DROP_DOWN_BUTTON_CSS_PART_2 = "']>span[class$='menu-button']>span>button";
    private static final By PENDING_INVITES = By.cssSelector("a[id$='pending-invites-link']");
    private static final By PEOPLE_LINK = By.cssSelector(".members-bar-links>a[href='site-members']");
    private static final By GROUPS_LINK = By.cssSelector(".members-bar-links>a[href='site-groups']");
    private static final By INVITE_BTN = By.cssSelector("a[href='invite']");
    @SuppressWarnings("unused")
    private static final By REMOVE_BTN = By.cssSelector("td[class*='uninvite']>div>span>span>span>button");

    private static final String USER_ROLE_XPATH = "//a[contains(text(),'%s')]/../../../..//td[contains(@class,'col-role')]/div/*";


    /**
     * Constructor.
     */
    public SiteMembersPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteMembersPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(USER_SEARCH_PART), getVisibleRenderElement(SEARCH_USER_ROLE_TEXT),
                getVisibleRenderElement(SEARCH_USER_ROLE_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteMembersPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteMembersPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public SiteMembersPage renderWithUserSearchResults(final long time)
    {
        return renderWithUserSearchResults(new RenderTime(time));
    }

    public SiteMembersPage renderWithUserSearchResults()
    {
        return renderWithUserSearchResults(new RenderTime(maxPageLoadingTime));
    }

    public SiteMembersPage renderWithUserSearchResults(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LIST_OF_USERS));
        return this;
    }

    /**
     * This method search for the given userName and returns the list of users.
     *
     * @param userName String
     * @return List<String>
     */
    public List<String> searchUser(String userName)
    {
        if (userName == null)
        {
            throw new UnsupportedOperationException("UserName value is required");
        }
        if (logger.isTraceEnabled())
        {
            logger.trace("Members page: searchUser :" + userName);
        }
        try
        {
            WebElement userRoleSearchTextBox = drone.find(SEARCH_USER_ROLE_TEXT);
            userRoleSearchTextBox.clear();
            userRoleSearchTextBox.sendKeys(userName);

            WebElement searchButton = drone.find(SEARCH_USER_ROLE_BUTTON);
            searchButton.click();
            List<WebElement> list = drone.findAndWaitForElements(LIST_OF_USERS, WAIT_TIME_3000);
            List<String> userNamesList = new ArrayList<String>();
            for (WebElement user : list)
            {
                WebElement userNameElement = user.findElement(USER_NAME_FROM_LIST);
                if (userNameElement != null && userNameElement.getText() != null)
                {
                    userNamesList.add(userNameElement.getText());
                }
            }
            return userNamesList;
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the users list css.", e);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time exceeded to find the users list css.", e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * The filters of the Site content those are diplayed in filters dropdown.
     *
     * @param userName String
     * @return <List<WebElement>>
     */
    private List<WebElement> getRoles(String userName)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Members Page: Returning the roles list");
        }

        List<WebElement> listOfRoles = new ArrayList<WebElement>();
        String name = userName.trim();

        // The below lowercase conversion will be resolved once Cloud-1847 task is finished.
        if (alfrescoVersion.isCloud())
        {
            name = name.toLowerCase();
        }

        try
        {
            drone.findAndWait(By.cssSelector(ROLES_DROP_DOWN_BUTTON_CSS_PART_1 + name + ROLES_DROP_DOWN_BUTTON_CSS_PART_2)).click();
            listOfRoles = drone.findAndWaitForElements(By.cssSelector(ROLES_DROP_DOWN_VALUES_CSS_PART_1 + name + ROLES_DROP_DOWN_VALUES_CSS_PART_2));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the list of roles.", e);
        }

        return listOfRoles;
    }

    /**
     * This method assigns role from drop down values.
     *
     * @param userName String
     * @param userRole UserRole
     * @return {@link SiteMembersPage}
     */
    public SiteMembersPage assignRole(String userName, UserRole userRole)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Members page: Assign role");
        }
        if (userName == null || userName.isEmpty())
        {
            throw new UnsupportedOperationException("usreName  is required.");
        }
        if (userRole == null)
        {
            throw new UnsupportedOperationException("userRole is required.");
        }

        if (!isUserPresent(userName))
        {
            throw new UnsupportedOperationException("Unable to find the user");
        }
        for (WebElement role : getRoles(userName))
        {
            String roleText = role.getText().trim();
            if (roleText != null && userRole.getRoleName().equalsIgnoreCase(roleText))
            {
                role.click();
                return new SiteMembersPage(drone);
            }
        }
        throw new PageException("Unable to find the rolename.");
    }

    /**
     * Method to remove given user from Site.
     *
     * @param userName String
     */
    public SiteMembersPage removeUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name is required.");
        }

        if (alfrescoVersion.isCloud())
        {
            userName = userName.toLowerCase();
        }

        try
        {
            WebElement element = drone.findAndWait(By.cssSelector("span[id$='_default-button-" + userName + "']>span>span>button"));
            String id = element.getAttribute("id");
            element.click();
            drone.waitUntilElementDeletedFromDom(By.id(id), maxPageLoadingTime);
            return new SiteMembersPage(getDrone());
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("User: \"" + userName + "\" can not be found in members list.", e);
            }
        }
        throw new PageException("User: \"" + userName + "\" can not be found in members list.");
    }

    /**
     * Method to verify whether site user can be removed from site
     *
     * @param userName String
     * @param siteName String
     * @return boolean
     */
    public boolean isUserCanBeRemoved (String userName, String siteName)
    {
        boolean isMessageOk = true;
        try
        {
            drone.findAndWait(By.cssSelector("span[id$='_default-button-" + userName + "']>span>span>button")).click();
            if (isDisplayed(PROMPT_PANEL_ID))
            {
                try
                {
                    isMessageOk = drone.findAndWait(By.cssSelector("#prompt>.bd")).getText().equals
                        (String.format("Failed to remove user %s from site %s. A site must have at least one Manager.", userName, siteName.toLowerCase()));
                }
                catch (NoSuchElementException nse)
                {
                    throw new ShareException ("The prompt is incorrect");
                }
                return !isMessageOk;
            }
            else return isMessageOk;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to remove the user");
        }
    }

    /**
     * Action of selecting invite people button.
     *
     * @return {@link InviteMembersPage} page response.
     */
    public InviteMembersPage selectInvitePeople()
    {
        try
        {
            if (AlfrescoVersion.Enterprise41 == alfrescoVersion)
            {
                drone.find(By.cssSelector("a[href$='invite']")).click();
            }
            else
            {
                drone.find(By.cssSelector("span.alf-user-icon")).click();
            }
        }
        catch (TimeoutException e)
        {
            throw new PageException("Unable to find the InviteMembersPage.", e);
        }
        return new InviteMembersPage(getDrone());

    }

    private boolean isUserPresent(String userName)
    {
        try
        {
            List<WebElement> usersList = drone.findAll(USER_NAME_FROM_LIST);

            for (WebElement user : usersList)
            {
                if (user.getText().contains(userName))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find users list", nse);
            }
        }
        return false;
    }

    /**
     * Method to navigate to Pending invites page
     *
     * @return Html page
     */

    public PendingInvitesPage navigateToPendingInvites()
    {
        try
        {
            drone.findAndWait(PENDING_INVITES).click();
            return new PendingInvitesPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not found Element:" + PENDING_INVITES, nse);
        }
    }

    private boolean isDisplayed(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException exc)
        {
            return false;
        }
    }

    /**
     * Method to verify Invite Link
     *
     * @return true if displayed
     */
    public boolean isInviteLinkPresent()
    {
        return isDisplayed(INVITE_BTN);
    }

    /**
     * Method to verify People link is displayed
     *
     * @return true if displayed
     */
    public boolean isPeopleLinkPresent()
    {
        return isDisplayed(PEOPLE_LINK);
    }

    /**
     * Method to verify Groups link is displayed
     *
     * @return true if displayed
     */
    public boolean isGroupLinkPresent()
    {
        return isDisplayed(GROUPS_LINK);
    }

    /**
     * Method to verify remove link is displayed
     *
     * @param userName String
     * @return true if displayed
     */
    public boolean isRemoveButtonPresent(String userName)
    {
        String name = userName.trim();
        if (alfrescoVersion.isCloud())
        {
            name = name.toLowerCase();
        }
        try
        {
            return drone.isElementDisplayed(By.cssSelector(String.format("span[id$='button-%s']>span>span>button", name)));
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out waiting for elements");
        }
    }

    /**
     * Method to open Groups tab from Site Members page
     *
     * @return SiteGroupsPage
     */
    public SiteGroupsPage navigateToSiteGroups()
    {
        try
        {
            drone.findAndWait(GROUPS_LINK).click();
            return new SiteGroupsPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + GROUPS_LINK);
        }
    }

    /**
     * Method to verify whether assign role is displayeed
     *
     * @param userName String
     * @return true if displayed
     */
    public boolean isAssignRolePresent(String userName)
    {
        String name = userName.trim();

        if (alfrescoVersion.isCloud())
        {
            name = name.toLowerCase();
        }
        try
        {
            return drone.isElementDisplayed(By.cssSelector(ROLES_DROP_DOWN_BUTTON_CSS_PART_1 + name + ROLES_DROP_DOWN_BUTTON_CSS_PART_2));
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out waiting for elements");
        }
    }

    /**
     * True if found user has role userRole.
     *
     * @param userName String
     * @param userRole UserRole
     * @return boolean
     */
    public boolean isUserHasRole(String userName, UserRole userRole)
    {
        By smthElement = By.xpath(String.format(USER_ROLE_XPATH, userName));
        try
        {
            WebElement element = drone.findAndWait(smthElement, 1000);
            return element.getText().contains(userRole.getRoleName());
        }
        catch (TimeoutException e)
        {
            throw new PageException("Not found Element:" + smthElement, e);
        }
    }
}