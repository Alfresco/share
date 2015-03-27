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
package org.alfresco.po.share.user;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.NoSuchElementException;

/**
 * My profile page object, holds all element of the html page relating to
 * share's my profile page.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyProfilePage extends SharePage
{

    private final By editProfileButton = By.cssSelector("button[id$='-button-edit-button'], button[id$='-button-following-button']");
    private final By userName = By.cssSelector(".namelabel");
    private final By emailName = By.cssSelector(".fieldvalue");
    private static final By TRASHCAN_LINK = By.cssSelector("div>a[href='user-trashcan']");
    private static final By FOLLOWING_LINK = By.cssSelector("div>a[href='following']");

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public MyProfilePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(editProfileButton));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render(final long time)
    {
        return render(new RenderTime(time));
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
        isPresent = title.contains("Profile");
        return isPresent;
    }

    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(drone);
    }

    /**
     * Method to get element text for Username
     *
     * @return
     */
    public String getUserName()
    {
        return getElementText(userName);
    }

    /**
     * Method to get element text for email.
     *
     * @return
     */
    public String getEmailName()
    {
        return getElementText(emailName);
    }

    /**
     * Check that Trashcan link is displayed on page.
     *
     * @return true - if link displayed.
     */

    public boolean isTrashcanLinkDisplayed()
    {
        try
        {
            WebElement trashcanLink = drone.findAndWait(TRASHCAN_LINK);
            return trashcanLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Check that Following link is displayed on page.
     *
     * @return true - if link displayed.
     */

    public boolean isFollowingLinkDisplayed()
    {
        try
        {
            WebElement followingLink = drone.findAndWait(FOLLOWING_LINK);
            return followingLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    public EditProfilePage openEditProfilePage()
    {
        drone.findAndWait(editProfileButton).click();
        return new EditProfilePage(drone);
    }
}