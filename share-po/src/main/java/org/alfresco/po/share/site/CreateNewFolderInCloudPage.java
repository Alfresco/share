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
package org.alfresco.po.share.site;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Create folder page object, holds all element of the HTML page relating to
 * share's create new folder in cloud page.
 * 
 * @author Ranjith Manyam
 * @since 1.0
 */
public class CreateNewFolderInCloudPage extends SharePage
{
    private static final Logger logger = Logger.getLogger(CreateNewFolderInCloudPage.class);

    private static final By TITLE = By.cssSelector("input[id$='createFolderInTheCloud_prop_cm_title']");
    private static final By NAME = By.cssSelector("input[id$='_default-cloud-folder-createFolderInTheCloud_prop_cm_name']");
    private final By NAME_LABEL = By.cssSelector("div[class='form-field']>label[for$='folder-createFolderInTheCloud_prop_cm_name']");
    private static final By DESCRIPTION = By.cssSelector("textarea[id$='_default-cloud-folder-createFolderInTheCloud_prop_cm_description']");
    private final By DESCRIPTION_LABEL = By.cssSelector("div>label[for$='folder-createFolderInTheCloud_prop_cm_description']");
    private static final By SAVE_BUTTON = By.cssSelector("button[id$='_default-cloud-folder-createFolderInTheCloud-form-submit-button']");

    /**
     * Constructor.
     */
    public CreateNewFolderInCloudPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderInCloudPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderInCloudPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewFolderInCloudPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * @see #createNewFolder(String, String)
     */
    public DestinationAndAssigneePage createNewFolder(final String folderName)
    {
        return createNewFolder(folderName, null);
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @return {@link DestinationAndAssigneePage} page response
     */
    public DestinationAndAssigneePage createNewFolder(final String folderName, final String description)
    {
        if (folderName == null || folderName.isEmpty())
        {
            throw new UnsupportedOperationException("Folder Name input required.");
        }
        try
        {
            WebElement inputFolderName = drone.findAndWait(NAME);
            inputFolderName.sendKeys(folderName);
            if (description != null)
            {
                WebElement inputDescription = drone.find(DESCRIPTION);
                inputDescription.sendKeys(description);
            }
            submit(SAVE_BUTTON, ElementState.INVISIBLE);
            // Wait till the pop up disappears
            // canResume();
            try
            {
                drone.waitForElement(By.id("AlfrescoWebdronez1"), SECONDS.convert(WAIT_TIME_3000, MILLISECONDS));
            }
            catch (TimeoutException e)
            {
            }
            // drone.waitFor(WAIT_TIME_3000);
            return new DestinationAndAssigneePage(drone);
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"Name\" element", te);
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"Description/Save button\" elements", nse);
            }
        }
        throw new PageException("Unable to find element");
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @param folderTitle options folder Title
     * @return {@link DestinationAndAssigneePage} page response
     */
    public DestinationAndAssigneePage createNewFolder(final String folderName, final String folderTitle, final String description)
    {
        if (folderName == null || folderName.isEmpty())
        {
            throw new UnsupportedOperationException("Folder Name input required.");
        }

        if (folderTitle != null && !folderTitle.isEmpty())
        {
            WebElement inputFolderName = drone.findAndWait(TITLE);
            inputFolderName.sendKeys(folderTitle);
        }

        return createNewFolder(folderName, description);
    }

    public boolean isNameLabelDisplayed()
    {
        try
        {
            drone.findAndWait(NAME_LABEL);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }

    public boolean isDescriptionLabelDisplayed()
    {
        try
        {
            drone.findAndWait(DESCRIPTION_LABEL);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }
}
