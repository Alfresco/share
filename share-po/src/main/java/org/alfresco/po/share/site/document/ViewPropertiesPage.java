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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * View Properties page object, holds all element of the HTML page relating to
 * share's View Properties page
 * 
 * @author Maryia Zaichanka
 */
public class ViewPropertiesPage extends ShareDialogue
{

    private Log logger = LogFactory.getLog(this.getClass());

    protected static final String NEXT_BUTTON = ("a[rel~='next']");
    protected static final String PREVIOUS_BUTTON = ("a[rel~='previous']");
    protected static final String VERSION_BUTTON = "button[id*='versionNav-button']";
    protected static final By VIEW_PROPERTIES_FORM = By.cssSelector("div[id*='PropertiesViewer'] div[class='form-container']");



    /**
     * Constructor.
     */
    public ViewPropertiesPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPropertiesPage render(RenderTime timer)
    {
    elementRender(timer, RenderElement.getVisibleRenderElement(VIEW_PROPERTIES_FORM));
    return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPropertiesPage render()
    {
    return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewPropertiesPage render(final long time)
    {
    return render(new RenderTime(time));
    }

    /**
     * Verify if view properties element,
     * that contains the form is visible.
     * 
     * @return true if displayed
     */
    public boolean isViewPropertiesVisible()
    {
        try
        {
            if (!isShareDialogueDisplayed())
            {
                return drone.find(VIEW_PROPERTIES_FORM).isDisplayed();
            }
            else
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method let move to other version of the document in the dialogue page
     * 
     * @param forward true when moving to the next version, false - previous
     */
    public void selectOtherVersion(boolean forward)
    {

        try
        {
            if (forward)
            {
                drone.findAndWait(By.cssSelector(NEXT_BUTTON)).click();
            }
            else
            {
                drone.findAndWait(By.cssSelector(PREVIOUS_BUTTON)).click();
            }
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element", exception);
        }

    }

    /**
     * Method to close the dialogue page
     */
    HtmlPage closeDialogue()
    {
        clickClose();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Checks if button is present
     * 
     * @return
     */
    public boolean isVersionButtonDisplayed()
    {
        try
        {
            return getVersionButton().isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Element is not present ", nse);
            }
        }
        return false;
    }

    public String getVersionButtonTitle()
    {
        String title = "";
        try
        {
            WebElement button = getVersionButton();
            title = button.getText();
            return title;
        }
        catch (NoSuchElementException nse)
        {
        }
        return title;
    }

    private WebElement getVersionButton()
    {
        try
        {
            WebElement versionButton = drone.findAndWait(By.cssSelector(VERSION_BUTTON));
            return versionButton;
        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("Unable to find the css ", nse);
        }
    }
}
