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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Create site page object, holds all element of the HTML page relating to
 * share's create site page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class CreateSitePage extends ShareDialogue
{
    protected static final By MODERATED_CHECKBOX = By.cssSelector("input[id$='-isModerated']");
    protected static final By PRIVATE_CHECKBOX = By.cssSelector("input[id$='-isPrivate']");
    protected static final By PUBLIC_CHECKBOX = By.cssSelector("input[id$='-isPublic']");
    protected static final By INPUT_DESCRIPTION = By.cssSelector("textarea[id$='-description']");
    protected static final By INPUT_TITLE = By.name("title");
    protected static final By SUBMIT_BUTTON = By.cssSelector("button[id$='ok-button-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='cancel-button-button']");
    protected static final By CREATE_SITE_FORM = By.cssSelector("form[id$='createSite-instance-form']");

    /**
     * Constructor.
     */
    public CreateSitePage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    public CreateSitePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(CREATE_SITE_FORM), RenderElement.getVisibleRenderElement(INPUT_DESCRIPTION));
        return this;
    }

    @Override
    public CreateSitePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public CreateSitePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if the create dialog is displayed. A wait is introduce to deal
     * with javascript side effects not rendering the page in the same way as
     * html.
     * 
     * @return true if dialog is displayed.
     */
    public boolean isCreateSiteDialogDisplayed()
    {
        try
        {
            return drone.findAndWait(CREATE_SITE_FORM).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Create a new site based on the site type.
     * 
     * @param siteName String mandatory field
     * @param description String site description
     * @param boolean is a private site
     * @param boolean is a moderated site
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewSite(final String siteName, final String description, final boolean isPriavte, final boolean isModerated)
    {
        return createNewSite(siteName, description, isPriavte, isModerated, SiteType.COLLABORATION);
    }

    /**
     * Create a new site based on the site type.
     * 
     * @param siteName String mandatory field
     * @param description String site description
     * @param boolean is a private site
     * @param boolean is a moderated site
     * @param type Collaboration
     * @return {@link HtmlPage} page response
     */
    protected HtmlPage createNewSite(final String siteName, final String description, final boolean isPriavte, final boolean isModerated, final String siteType)
    {
        if (siteType == null)
        {
            throw new IllegalArgumentException("Site type is required");
        }

        selectSiteVisibility(isPriavte, isModerated);

        return createSite(siteName, description, siteType);
    }

    protected HtmlPage createSite(final String siteName, final String description, final String siteType)
    {
        switch (siteType)
        {
            case SiteType.COLLABORATION:
                WebElement inputSiteName = drone.findAndWait(INPUT_TITLE);
                inputSiteName.sendKeys(siteName);
                if (description != null)
                {
                    WebElement inputDescription = drone.find(INPUT_DESCRIPTION);
                    inputDescription.clear();
                    inputDescription.sendKeys(description);
                }
                selectSiteType(siteType);
                return selectOk();
            default:
                throw new PageOperationException("No site type match found for: " + siteType + " out of the following possible options: Collaboration");
        }

    }

    /**
     * Clicks on OK buttong and checks whether site page has been loaded.
     * 
     * @return
     */
    public HtmlPage selectOk()
    {
        return submit(SUBMIT_BUTTON, ElementState.DELETE_FROM_DOM);
    }

    /**
     * Selects the visibility required for site to be created/edited.
     * 
     * @param isPrivate
     * @param isModerated
     */
    public void selectSiteVisibility(final boolean isPrivate, final boolean isModerated)
    {
        if (isPrivate)
        {
            drone.find(PRIVATE_CHECKBOX).click();
            return;
        }
        else
        {
            drone.findAndWait(PUBLIC_CHECKBOX).click();
            if (isModerated)
            {
                drone.find(MODERATED_CHECKBOX).click();
            }
        }
    }

    /**
     * Create a new public site action.
     * 
     * @param siteName String mandatory field
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewSite(final String siteName)
    {
        return createNewSite(siteName, null, false, false);
    }

    /**
     * Create a new public site action.
     * 
     * @param siteName String mandatory field
     * @param Site Description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewSite(final String siteName, String desc)
    {
        return createNewSite(siteName, desc, false, false);
    }

    /**
     * Create a new private site action.
     * 
     * @param siteName String mandatory field
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createPrivateSite(final String siteName)
    {
        return createNewSite(siteName, null, true, false);
    }

    /**
     * Create a new private site action.
     * 
     * @param siteName String mandatory field
     * @param Site Description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createPrivateSite(final String siteName, String desc)
    {
        return createNewSite(siteName, desc, true, false);
    }

    /**
     * Create a new public moderated site action.
     * 
     * @param siteName String mandatory field
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createModerateSite(final String siteName)
    {
        return createNewSite(siteName, null, false, true);
    }

    /**
     * Create a new public moderated site action.
     * 
     * @param siteName String mandatory field
     * @param Site Description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createModerateSite(final String siteName, String desc)
    {
        return createNewSite(siteName, desc, false, true);
    }

    /**
     * Clicks on the cancel button on the create site form.
     */
    public void cancel()
    {
        drone.findAndWait(CANCEL_BUTTON).click();
    }

    /**
     * Checks if the check box with the label private is ticked.
     * 
     * @return true if selected
     */
    public boolean isPrivate()
    {
        try
        {
            return drone.findAndWait(PRIVATE_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Checks if the check box with the label public is ticked.
     * 
     * @return true if selected
     */
    public boolean isPublic()
    {
        try
        {
            return drone.findAndWait(PUBLIC_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Checks if the check box with the label private is ticked.
     * 
     * @return true if selected
     */
    public boolean isModerate()
    {
        try
        {
            return drone.findAndWait(MODERATED_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Action of selecting site type drop down.
     * 
     * @param type of site
     */
    public void selectSiteType(String siteType)
    {
        WebElement dropdown = drone.find(By.tagName("select"));
        // Check option size if only one in dropdown return.
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        if (options.isEmpty() || options.size() > 1)
        {
            WebElement siteOption;
            switch (siteType)
            {
                case SiteType.COLLABORATION:
                    siteOption = dropdown.findElement(By.cssSelector("option:nth-of-type(1)"));
                    break;
                default:
                    throw new PageOperationException("No suitable site type was found");
            }
            siteOption.click();
        }
    }

    /**
     * Get the values of the site type from site create form.
     * 
     * @return List String site type
     */

    public List<String> getSiteType()
    {

        List<String> options = new ArrayList<String>();
        try
        {
            Select typeOptions = new Select(drone.find(By.tagName("select")));
            List<WebElement> optionElements = typeOptions.getOptions();

            for (WebElement option : optionElements)
            {
                options.add(option.getText());
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find After Completion Dropdown", nse);
        }
        return options;

    }

    /**
     * Set the Name of the site
     * 
     * @param String site name
     */

    public void setSiteName(String siteName)
    {
        WebElement inputSiteName = drone.findAndWait(INPUT_TITLE);
        inputSiteName.sendKeys(siteName);
    }

    /**
     * Set the URL of the site
     * 
     * @param String site name
     */

    public void setSiteURL(String siteURL)
    {
        WebElement inputSiteURL = drone.find(By.name("shortName"));

        inputSiteURL.clear();
        inputSiteURL.sendKeys(siteURL);
    }

    /**
     * Get the value of site name from site create form.
     * 
     * @return String site name
     */
    public String getSiteName()
    {
        return drone.find(By.name("title")).getAttribute("value");
    }

    /**
     * Get the value of site description from site create form.
     * 
     * @return String site description
     */
    public String getDescription()
    {
        return drone.find(By.name("description")).getAttribute("value");
    }

    /**
     * Get the value of site url from site create form.
     * 
     * @return String site short name for url
     */
    public String getSiteUrl()
    {
        return drone.find(By.name("shortName")).getAttribute("value");
    }

    /**
     * Check whether URL Name disabled for editing.
     * 
     * @return True if URL Name Input text box disabled, else false.
     */
    public boolean isUrlNameEditingDisaabled()
    {
        if (drone.find(By.name("shortName")).getAttribute("disabled") != null)
        {
            return true;
        }
        return false;
    }

    /**
     * Check whether URL Name disabled for editing.
     * 
     * @return True if URL Name Input text box disabled, else false.
     */
    public boolean isNameEditingDisaabled()
    {
        if (drone.find(By.name("title")).getAttribute("disabled") != null)
        {
            return true;
        }
        return false;
    }

}
