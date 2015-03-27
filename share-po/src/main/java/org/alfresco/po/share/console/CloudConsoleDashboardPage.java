package org.alfresco.po.share.console;

import org.alfresco.po.share.console.tabs.InviteUsersTab;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
public class CloudConsoleDashboardPage extends CloudConsolePage
{
    @RenderWebElement
    private static final By INVITE_USERS_TAB = By.cssSelector("li>a[href*='support/invite']");
    @RenderWebElement
    private static final By AUDIT_LOG_TAB = By.cssSelector("li>a[href*='audit']");

    protected CloudConsoleDashboardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsoleDashboardPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsoleDashboardPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsoleDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public InviteUsersTab openInviteUsersTab()
    {
        drone.findAndWait(INVITE_USERS_TAB).click();
        return new InviteUsersTab(drone);
    }

    public boolean isDashboardOpened()
    {
        boolean isDashboardOpened;
        try
        {
            // Search for no data message
            WebElement logoutLink = drone.find(INVITE_USERS_TAB);
            isDashboardOpened = logoutLink.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            isDashboardOpened = false;
        }
        return isDashboardOpened;
    }
}
