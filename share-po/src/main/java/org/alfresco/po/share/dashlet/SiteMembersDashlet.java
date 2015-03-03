/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SiteMember;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Site members dashlet object, holds all element of the HTML relating to share's site members dashlet.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteMembersDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SiteMembersDashlet.class);
    private static final String DATA_LIST_CSS_LOCATION = "div.detail-list-item > div.person > h3 > a";
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.colleagues");
    private static final By INVITE_LINK = By.cssSelector("div.dashlet.colleagues>div.toolbar>div>span>span>a[href='invite']");
    private static final By ALL_MEMBERS_LINK = By.cssSelector("div.dashlet.colleagues>div.toolbar>div>span>span>[href$='site-members']");
    private static final By USER_LINK = By.cssSelector("h3>.theme-color-1");
    private WebElement dashlet;

    /**
     * Constructor.
     */
    protected SiteMembersDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    public SiteMembersDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteMembersDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * The member of the site that is displayed on site members dashlet.
     *
     * @return List<ShareLink> site links
     */
    public synchronized List<ShareLink> getMembers()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    @SuppressWarnings("unchecked")
    public synchronized SiteMembersDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                try
                {
                    timer.start();
                    this.dashlet = drone.findAndWait((DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
                    break;
                }
                catch (Exception e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    /**
     * This method gets the focus by placing mouse over on Site Members Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Retrieves the SiteMember that match the site members name.
     *
     * @param emailId identifier
     * @return {@link SiteMember} that matches members name
     */
    public synchronized SiteMember selectMember(String emailId)
    {
        if (emailId == null)
        {
            throw new IllegalArgumentException("Name value of link is required");
        }
        
        try
        {
            List<WebElement> userRowList = dashlet.findElements(By.cssSelector("div.person"));
            SiteMember siteMember = new SiteMember();
            for (WebElement userRow : userRowList)
            {
                WebElement link = userRow.findElement(By.cssSelector("h3>a"));
                if (link.getText().contains(emailId) || ("admin".equals(emailId) && link.getText().equals("Administrator")))
                {
                    siteMember.setShareLink(new ShareLink(link, drone));
                    siteMember.setRole(UserRole.getUserRoleforName(userRow.findElement(By.cssSelector("div")).getText()));
                    return siteMember;
                }
            }
        }
        catch(NoSuchElementException nse)
        {
            logger.error("Unabled to find the member css.", nse);
        }
        
        throw new PageOperationException("Could not find site member for name - " + emailId);
    }

    /**
     * Method to verify if invite link is available
     *
     * @return boolean
     */
    public boolean isInviteLinkDisplayed()
    {
        try
        {
            getFocus();
            return drone.find(INVITE_LINK).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * Method to verify if all members link is available
     *
     * @return boolean
     */
    public boolean isAllMembersLinkDisplayed()
    {
        try
        {
            getFocus();
            return drone.findAndWait(ALL_MEMBERS_LINK).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Mimic click on 'All members' button.
     *
     * @return
     */
    public SiteMembersPage clickAllMembers()
    {
        drone.findAndWait(ALL_MEMBERS_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimic click on user link.
     *
     * @param userName
     * @return
     */
    public SharePage clickOnUser(String userName)
    {
        List<WebElement> userLinks = dashlet.findElements(USER_LINK);
        for (WebElement userLink : userLinks)
        {
            if (userLink.getText().contains(userName))
            {
                userLink.click();
                return drone.getCurrentPage().render();
            }
        }
        throw new PageOperationException(String.format("User[%s] didn't find in dashlet", userName));
    }
}
