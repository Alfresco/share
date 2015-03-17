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
package org.alfresco.po.share.console;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

// "extends SharePage" should be changed to "extends Page" we should re-locate method "render" from SharePage to Page
@SuppressWarnings("unused")
public class CloudConsolePage extends SharePage
{
    private static final By USERNAME_INPUT = By.cssSelector("input[id$='edit-name']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[id$='edit-pass']");
    private static final By SUBMIT_BUTTON = By.cssSelector("input[id$='edit-submit--2']");

    private static final RenderElement USERNAME_INPUT_RENDER = RenderElement.getVisibleRenderElement(USERNAME_INPUT);
    private static final RenderElement PASSWORD_INPUT_RENDER = RenderElement.getVisibleRenderElement(PASSWORD_INPUT);
    private static final RenderElement SUBMIT_BUTTON_RENDER = RenderElement.getVisibleRenderElement(SUBMIT_BUTTON);

    private static final By DASHBOARD_LINK = By.cssSelector("li>a[href*='/support']");
    private static final By LOGOUT_LINK = By.cssSelector("li>a[href*='logout']");
    private static final By SEARCH_INPUT = By.cssSelector("input[id$='edit-search-field']");
    @RenderWebElement
    private static final By FIND_BUTTON = By.cssSelector("input[id$='edit-submit']");

    /**
     * Constructor.
     */
    public CloudConsolePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Verify if login html elements inputs are displayed.
     * 
     * @return true if elements are displayed
     */
    @SuppressWarnings("unchecked")
    @Override
    public CloudConsolePage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsolePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if cloud console title is present on the page
     * 
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("Test");
    }

    /**
     * the function is private because we have only one user for Cloud Console
     * 
     * @return CloudConsolePage
     */
    public CloudConsolePage loginAs(final String username, final String password)
    {
        if (username == null || password == null)
        {
            throw new IllegalArgumentException("Input param can not be null");
        }
        WebElement usernameInput = drone.findAndWait(USERNAME_INPUT);
        WebElement passwordInput = drone.findAndWait(PASSWORD_INPUT);

        usernameInput.click();
        usernameInput.clear();
        usernameInput.sendKeys(username);

        passwordInput.click();
        passwordInput.clear();
        passwordInput.sendKeys(password);

        String usernameEntered = usernameInput.getAttribute("value");
        if (!username.equalsIgnoreCase(usernameEntered))
        {
            throw new PageOperationException(String.format("The username %s did not match input %s", username, usernameEntered));
        }
        WebElement button = drone.findAndWait(SUBMIT_BUTTON);
        button.submit();
        drone.findAndWait(LOGOUT_LINK);

        return new CloudConsolePage(drone);
    }

    public CloudConsolePage openCloudConsole(String consoleUrl)
    {
        if (consoleUrl == null)
        {
            throw new IllegalArgumentException("Input 'shareUrl'  param can not be null");
        }
        drone.navigateTo(consoleUrl);
        return new CloudConsolePage(drone).render();
    }

    public boolean isLoggedToCloudConsole()
    {
        boolean isLogged;
        try
        {
            // Search for no data message
            WebElement logoutLink = drone.find(LOGOUT_LINK);
            isLogged = logoutLink.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            isLogged = false;
        }
        return isLogged;
    }

    public CloudConsolePage logOutFromCloudConsole()
    {
        if (isLoggedToCloudConsole())
        {
            drone.find(LOGOUT_LINK).click();
        }
        return new CloudConsolePage(drone).render();
    }

    public CloudConsoleSearchResultPage executeSearch(String query)
    {
        if (query == null || query.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement searchField = drone.findAndWait(SEARCH_INPUT);
        searchField.clear();
        searchField.sendKeys(query);
        drone.findAndWait(FIND_BUTTON).click();
        return new CloudConsoleSearchResultPage(drone).render();
    }

    public CloudConsoleDashboardPage openDashboardPage()
    {
        drone.findAndWait(DASHBOARD_LINK).click();
        return new CloudConsoleDashboardPage(drone);
    }

    public String getCloudConsoleUrl(String shareUrl)
    {
        String consoleUrl;
        if (shareUrl.contains("stagmy.alfresco.com"))
        {
            consoleUrl = "http://cloudconsoletest.alfresco.com/";
        }
        else
        {
            if (shareUrl.contains("alfresco.me"))
            {

                consoleUrl = shareUrl.substring(0, shareUrl.lastIndexOf("alfresco.me"));
                consoleUrl = consoleUrl + "testcloudconsole.alfresco.com/";
            }
            else
            {
                throw new UnsupportedOperationException("cannot get cloud console url, pelase verify shareUrl is correct");
            }
        }
        return consoleUrl;
    }
}
