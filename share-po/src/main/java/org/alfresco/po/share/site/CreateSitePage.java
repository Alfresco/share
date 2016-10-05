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
package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Create site page object, holds all element of the HTML page relating to
 * share's create site page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class CreateSitePage extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(SitePage.class);

    protected static final By MODERATED_CHECKBOX = By.cssSelector("input[id$='-isModerated']");
    protected static final By PRIVATE_CHECKBOX = By.cssSelector("input[id$='-isPrivate']");
    protected static final By PUBLIC_CHECKBOX = By.cssSelector("input[id$='-isPublic']");
    protected static By MODERATED_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='moderated-help-text']");
    protected static By PRIVATE_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='private-help-text']");
    protected static By PUBLIC_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='public-help-text']");
    protected static final By INPUT_DESCRIPTION = By.cssSelector("textarea[id$='-description']");
    protected static final By INPUT_TITLE = By.name("title");
    protected static final By SUBMIT_BUTTON = By.cssSelector("button[id$='ok-button-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='cancel-button-button']");
    protected static final By CREATE_SITE_FORM = By.id("alfresco-createSite-instance-form");
    protected static final By SAVE_BUTTON = By.cssSelector("span.yui-button.yui-submit-button.alf-primary-button");

    @Override
    public CreateSitePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(CREATE_SITE_FORM), RenderElement.getVisibleRenderElement(INPUT_DESCRIPTION), RenderElement.getVisibleRenderElement(SAVE_BUTTON));
        return this;
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
            return findAndWait(CREATE_SITE_FORM).isDisplayed();
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
     * @param isPrivate boolean is a private site
     * @param isModerated boolean is a moderated site
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewSite(final String siteName, final String description, final boolean isPrivate, final boolean isModerated)
    {
        return createNewSite(siteName, description, isPrivate, isModerated, SiteType.COLLABORATION);
    }

    /**
     * Create a new site based on the site type.
     * 
     * @param siteName String mandatory field
     * @param description String site description
     * @param isPrivate boolean is a private site
     * @param isModerated boolean is a moderated site
     * @param siteType Collaboration
     * @return {@link HtmlPage} page response
     */
    protected HtmlPage createNewSite(final String siteName, final String description, final boolean isPrivate, final boolean isModerated, final String siteType)
    {
        if (siteType == null)
        {
            throw new IllegalArgumentException("Site type is required");
        }

        selectSiteVisibility(isPrivate, isModerated);

        return createSite(siteName, description, siteType);
    }

    protected HtmlPage createSite(final String siteName, final String description, final String siteType)
    {
        switch (siteType)
        {
            case SiteType.COLLABORATION:
                WebElement inputSiteName = driver.findElement(INPUT_TITLE);
                inputSiteName.sendKeys(siteName);
                if (description != null)
                {
                    WebElement inputDescription = driver.findElement(INPUT_DESCRIPTION);
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
     * @return HtmlPage
     */
    public HtmlPage selectOk()
    {
        return submit(SUBMIT_BUTTON, ElementState.INVISIBLE);
    }

    /**
     * Selects the visibility required for site to be created/edited.
     * 
     * @param isPrivate boolean
     * @param isModerated boolean
     */
    public void selectSiteVisibility(final boolean isPrivate, final boolean isModerated)
    {
        if (isPrivate)
        {
            findAndWait(PRIVATE_CHECKBOX).click();
            return;
        }
        else
        {
            findAndWait(PUBLIC_CHECKBOX).click();
            if (isModerated)
            {
                findAndWait(MODERATED_CHECKBOX).click();
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
     * @param desc Site Description
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
     * @param desc Site Description
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
     * @param desc Site Description
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
        driver.findElement(CANCEL_BUTTON).click();
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
            return findAndWait(PRIVATE_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Returns help text under private checkbox
     * 
     * @return
     */
    public String getPrivateCheckboxHelpText()
    {
        return driver.findElement(PRIVATE_CHECKBOX_HELP_TEXT).getText();
    }

    /**
     * Returns true if help text under privete checkbox is displayed
     * 
     * @return
     */
    public boolean isPrivateCheckboxHelpTextDisplayed()
    {
        try
        {
            findAndWait(PRIVATE_CHECKBOX_HELP_TEXT);
            return true;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Can't find css for private checkbox help. ", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Timed out finding css for private checkbox help. ", te);
        }
        return false;
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
            return findAndWait(PUBLIC_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Returns help text under public checkbox
     * 
     * @return
     */
    public String getPublicCheckboxHelpText()
    {
        return driver.findElement(PUBLIC_CHECKBOX_HELP_TEXT).getText();
    }

    /**
     * Returns true if help text under public checkbox is displayed
     * 
     * @return
     */
    public boolean isPublicCheckboxHelpTextDisplayed()
    {
        try
        {
            findAndWait(PUBLIC_CHECKBOX_HELP_TEXT);
            return true;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Can't find css for public checkbox help. ", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Timed out finding css for public checkbox help. ", te);
        }
        return false;
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
            return findAndWait(MODERATED_CHECKBOX).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Returns help text under moderated checkbox
     * 
     * @return
     */
    public String getModeratedCheckboxHelpText()
    {
        return driver.findElement(MODERATED_CHECKBOX_HELP_TEXT).getText();
    }

    /**
     * Returns true if help text under moderated checkbox is displayed
     * 
     * @return
     */
    public boolean isModeratedCheckboxHelpTextDisplayed()
    {
        try
        {
            findAndWait(MODERATED_CHECKBOX_HELP_TEXT);
            return true;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Can't find css for moderated checkbox help. ", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Timed out finding css for moderated checkbox help. ", te);
        }
        return false;
    }

    /**
     * Action of selecting site type drop down.
     * 
     * @param siteType type of site
     */
    public void selectSiteType(String siteType)
    {
        WebElement dropdown = driver.findElement(By.tagName("select"));
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
            Select typeOptions = new Select(driver.findElement(By.tagName("select")));
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
     * @param siteName String site name
     */

    public void setSiteName(String siteName)
    {
        WebElement inputSiteName = findAndWait(INPUT_TITLE);
        inputSiteName.sendKeys(siteName);
    }

    /**
     * Set the URL of the site
     * 
     * @param siteURL String site name
     */

    public void setSiteURL(String siteURL)
    {
        WebElement inputSiteURL = driver.findElement(By.name("shortName"));

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
        return driver.findElement(By.name("title")).getAttribute("value");
    }

    /**
     * Get the value of site description from site create form.
     * 
     * @return String site description
     */
    public String getDescription()
    {
        return driver.findElement(By.name("description")).getAttribute("value");
    }

    /**
     * Get the value of site url from site create form.
     * 
     * @return String site short name for url
     */
    public String getSiteUrl()
    {
        return driver.findElement(By.name("shortName")).getAttribute("value");
    }

    /**
     * Check whether URL Name disabled for editing.
     * 
     * @return True if URL Name Input text box disabled, else false.
     */
    public boolean isUrlNameEditingDisaabled()
    {
        if (findAndWait(By.name("shortName")).getAttribute("disabled") != null)
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
        if (findAndWait(By.name("title")).getAttribute("disabled") != null)
        {
            return true;
        }
        return false;
    }

}
