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
package org.alfresco.po.share.user;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;


/**
 * User cloud Sync page object holds all elements of HTML page objects relating to Cloud Sync connect page.
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public class CloudSyncPage extends SharePage
{
    private static final By SIGN_IN_BUTTON = By.cssSelector("button[id$='default-button-signIn-button']");
    public static final By DISCONNECT_BUTTON = By.cssSelector("button[id$='default-button-delete-button']");
    private static final By EDIT_BUTTON = By.cssSelector("button[id$='default-button-edit-button']");

    private final Log logger = LogFactory.getLog(CloudSyncPage.class);

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public CloudSyncPage(WebDrone drone)
    {
        super(drone);
    }

    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudSyncPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, RenderElement.getVisibleRenderElement(By.cssSelector("a[id$='default-user-cloud-auth-link']")));
        }
        catch (NoSuchElementException e)
        {
            logger.error(SIGN_IN_BUTTON.toString() + "not found", e);
        }
        catch (TimeoutException e)
        {
            logger.error(SIGN_IN_BUTTON.toString() + " took a lot of time to locate", e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudSyncPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudSyncPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if cloud Auth page web element is present
     * 
     * @return true if exists
     */
    public boolean isTitlePresent()
    {
        return getPageTitle().contains("Cloud Auth");
    }

    /**
     * Retrieves the link based on the given cssSelector.
     * 
     * @return boolean
     */
    public boolean isDisconnectButtonDisplayed()
    {
        try
        {
            return drone.find(DISCONNECT_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Disconnect button not Present", e);
            }
            try
            {
                return !drone.find(SIGN_IN_BUTTON).isDisplayed();
            }
            catch (NoSuchElementException nse)
            {
            }
        }
        return false;
    }

    /**
     * Confirm delete dialog acceptance action.
     */
    public void confirmDelete()
    {
        final String promptPanelId = "prompt.panel.id";
        WebElement prompt = drone.findAndWaitById(promptPanelId);
        List<WebElement> elements = prompt.findElements(By.tagName("button"));
        // Find the delete button in the prompt
        WebElement delete = findButton("Delete", elements);
        delete.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("Confirm delete button has been found and selected");
        }

    }

    /**
     * This method tries to connect to cloud account. Else throws {@link PageException} if already connected.
     * 
     * @return
     */
    public CloudSignInPage selectCloudSign()
    {
        try
        {
            drone.findAndWait(SIGN_IN_BUTTON).click();
        }
        catch (StaleElementReferenceException ser)
        {
            selectCloudSign();
        }
        catch (NoSuchElementException e)
        {
            logger.info("User is already connected to Cloud account");
            throw new PageException("User is already connected to Cloud account", e);
        }
        return new CloudSignInPage(drone);
    }

    /**
     * This method tries to disconnect to cloud account. Else throws {@link PageException} if already disconnected.
     * 
     * @return
     */
    public CloudSyncPage disconnectCloudAccount()
    {
        if (!isDisconnectButtonDisplayed())
        {
            throw new PageException("User is already disconnected");
        }
        try
        {
            drone.find(DISCONNECT_BUTTON).click();
            confirmDelete();
            drone.waitUntilNotVisible(By.cssSelector("span.message"), "Cloud account successfully disconnected", maxPageLoadingTime);
            return new CloudSyncPage(drone);
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }
        throw new PageException("Not able click disconnect button.");
    }

    public CloudSignInPage selectEditButton()
    {
        try
        {
            drone.findAndWait(EDIT_BUTTON).click();
        }
        catch (StaleElementReferenceException ser)
        {
            selectEditButton();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Not able to find Edit button", e);
        }
        return new CloudSignInPage(drone).render();
    }
}