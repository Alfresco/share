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
package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SiteMember;
import org.alfresco.po.share.enums.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.colleagues")
/**
 * Site members dashlet object, holds all element of the HTML relating to share's site members dashlet.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteMembersDashlet extends AbstractDashlet implements Dashlet
{
    private static final String DATA_LIST_CSS_LOCATION = "div.detail-list-item > div.person > h3 > a";
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.colleagues");
    private static final By INVITE_LINK = By.cssSelector("div.dashlet.colleagues>div.toolbar>div>span>span>a[href='invite']");
    private static final By ALL_MEMBERS_LINK = By.cssSelector("div.dashlet.colleagues>div.toolbar>div>span>span>[href$='site-members']");
    private static final By USER_LINK = By.cssSelector("h3>.theme-color-1");
    private static Log logger = LogFactory.getLog(SiteMembersDashlet.class);

//    /**
//     * Constructor.
//     */
//    protected SiteMembersDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector(".yui-resize-handle"));
//    }


    /**
     * The member of the site that is displayed on site members dashlet.
     *
     * @return List<ShareLink> site links
     */
    public  List<ShareLink> getMembers()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    @SuppressWarnings("unchecked")
    public SiteMembersDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(By.cssSelector(".yui-resize-handle"));
            while (true)
            {
                timer.start();
                synchronized(this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    getFocus(DASHLET_CONTAINER_PLACEHOLDER);
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }

                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for SiteMembersDashlet dashlet was not found ", e);
                }
                catch (StaleElementReferenceException ste)
                {
                    logger.error("DOM has changed therefore page should render once change", ste);
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
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Retrieves the SiteMember that match the site members name.
     *
     * @param emailId identifier
     * @return {@link SiteMember} that matches members name
     */
    public  SiteMember selectMember(String emailId)
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
                    siteMember.setShareLink(new ShareLink(link, driver, factoryPage));
                    siteMember.setRole(UserRole.getUserRoleforName(userRow.findElement(By.cssSelector("div")).getText()));
                    return siteMember;
                }
            }
        }
        catch (NoSuchElementException nse)
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
            return driver.findElement(INVITE_LINK).isDisplayed();
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
            return findAndWait(ALL_MEMBERS_LINK).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Mimic click on 'All members' button.
     *
     * @return SiteMembersPage
     */
    public HtmlPage clickAllMembers()
    {
        findAndWait(ALL_MEMBERS_LINK).click();
        return getCurrentPage();
    }

    /**
     * Mimic click on user link.
     *
     * @param userName String
     * @return SharePage
     */
    public HtmlPage clickOnUser(String userName)
    {
        List<WebElement> userLinks = dashlet.findElements(USER_LINK);
        for (WebElement userLink : userLinks)
        {
            if (userLink.getText().contains(userName))
            {
                userLink.click();
                return getCurrentPage();
            }
        }
        throw new PageOperationException(String.format("User[%s] didn't find in dashlet", userName));
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteMembersDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
