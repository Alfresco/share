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
import org.alfresco.po.share.ShareDialogueAikau;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Create site page object, holds all element of the HTML page relating to
 * share's create site page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class CreateSitePage extends ShareDialogueAikau
{
    private static Log logger = LogFactory.getLog(SitePage.class);

    protected static String DIALOG_ID = "#CREATE_SITE_DIALOG";
    protected static final By SITE_DIALOG = By.cssSelector("div[id*='_SITE_DIALOG']");  
    
    protected static final By CREATE_SITE_TITLE = By.cssSelector(DIALOG_ID + "_title");
    protected static final String SITE_VISIBILITY = "div[id*='_SITE_FIELD_VISIBILITY_CONTROL_OPTION";
    
    protected static final By MODERATED_CHECKBOX = By.cssSelector(SITE_VISIBILITY + "1']");
    protected static final By PRIVATE_CHECKBOX = By.cssSelector(SITE_VISIBILITY + "2']");
    protected static final By PUBLIC_CHECKBOX = By.cssSelector(SITE_VISIBILITY + "0']");
    
    protected static final String CHECKBOX_HELP_TEXT = " .alfresco-forms-controls-RadioButtons__description";
    protected static final By MODERATED_CHECKBOX_HELP_TEXT = By.cssSelector(SITE_VISIBILITY + "1']" + CHECKBOX_HELP_TEXT);
    protected static final By PRIVATE_CHECKBOX_HELP_TEXT = By.cssSelector(SITE_VISIBILITY + "2']" + CHECKBOX_HELP_TEXT);
    protected static final By PUBLIC_CHECKBOX_HELP_TEXT = By.cssSelector(SITE_VISIBILITY + "0']" + CHECKBOX_HELP_TEXT);
    
    protected static final By SITE_TYPE_DROPDOWN = By.cssSelector("table[id*='_SITE_FIELD_PRESET_CONTROL'] .dijitSelectLabel");
    protected static final By INPUT_DESCRIPTION = By.cssSelector("div[id*='_SITE_FIELD_DESCRIPTION'] textarea[name='description']");
    protected static final By INPUT_TITLE = By.cssSelector("div[id*='_SITE_FIELD_TITLE'] input[name='title']");
    protected static final By INPUT_SITEID = By.cssSelector("#CREATE_SITE_FIELD_SHORTNAME input[name='shortName']");
    
    protected static final By SUBMIT_BUTTON = By.cssSelector("[id$='_SITE_DIALOG_OK_label']");
    protected static final By CANCEL_BUTTON = By.cssSelector("[id$='_SITE_DIALOG_CANCEL_label']");
    
    protected static final By DUPLICATE_SITE_WARNING = By.cssSelector("div[id*='_SITE_FIELD_TITLE'] .alfresco-forms-controls-BaseFormControl__warning-row__warning");
    protected static final By SITE_ID_ERROR = By.cssSelector("div[id*='_SITE_FIELD_SHORTNAME'] .validation-message");

    @Override
    public CreateSitePage render(RenderTime timer)
    {
    	DIALOG_ID = "#CREATE_SITE_DIALOG";
    	
    	elementRender(timer, RenderElement.getVisibleRenderElement(SITE_DIALOG), RenderElement.getVisibleRenderElement(INPUT_DESCRIPTION), RenderElement.getVisibleRenderElement(SUBMIT_BUTTON));
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
            return findAndWait(SITE_DIALOG).isDisplayed();
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
        return createNewSite(siteName, description, isPrivate, isModerated, SiteType.COLLABORATION).render();
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

        return createSite(siteName, description, siteType).render();
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
                selectOk();
                waitUntilAlert();
                
                return factoryPage.getPage(driver);
            default:
                throw new PageOperationException("No site type match found for: " + siteType + " out of the following possible options: Collaboration");
        }

    }

    public HtmlPage createSite(String siteName, String siteID, boolean isPrivate, boolean isModerated)
    {

                setSiteName(siteName);                
                setSiteURL(siteID);
                selectSiteVisibility(isPrivate, isModerated);

                return selectOk().render();
    }

    /**
     * Clicks on OK button and checks whether site page has been loaded.
     * 
     * @return HtmlPage
     */
    public HtmlPage selectOk()
    {
        return submit(SUBMIT_BUTTON, ElementState.DELETE_FROM_DOM).render();
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
            selectVisibility(PRIVATE_CHECKBOX);
        }
        else
        {
        	selectVisibility(PUBLIC_CHECKBOX);
            if (isModerated)
            {
            	selectVisibility(MODERATED_CHECKBOX);
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
        return createNewSite(siteName, null, false, false).render();
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
        return isSelected(PRIVATE_CHECKBOX);
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
    	 return isSelected(PUBLIC_CHECKBOX);
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
        	 return isSelected(MODERATED_CHECKBOX);
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
        List<WebElement> options = driver.findElements(SITE_TYPE_DROPDOWN);
        
    	String siteTypeText = factoryPage.getValue("site.type.collaboration");
    	boolean siteTypeFound = false;
    	
        switch (siteType)
        {
            case SiteType.COLLABORATION:
            	siteTypeText = factoryPage.getValue("site.type.collaboration");
                break;
            default:
            	siteTypeText = factoryPage.getValue("site.type.collaboration");
        }

        for (WebElement option : options)
        {
        	if (siteTypeText.equalsIgnoreCase(option.getText()))
        	{
        		option.click();
        		siteTypeFound = true;
        		break;
        	}
        }
        
        if(!siteTypeFound)
        {
        	throw new PageOperationException("No suitable site type was found");
        }
    }

    /**
     * Get the values of the site type from site create form.
     * 
     * @return List String site type
     */

    public List<String> getSiteTypes()
    {
        List<String> options = new ArrayList<String>();
        
        try
        {
        	List<WebElement> siteTypes = driver.findElements(SITE_TYPE_DROPDOWN);
        	
            for (WebElement siteType : siteTypes)
            {
                options.add(siteType.getText());
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find Site Types", te);
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
        inputSiteName.clear();
        inputSiteName.sendKeys(siteName);
        inputSiteName.sendKeys(Keys.TAB);
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
        inputSiteURL.sendKeys(Keys.TAB);
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
    
    /**
     * Check whether Create Site button is enabled.
     * 
     * @return True if button is enabled, else false.
     */
    public boolean isCreateButtonEnabled()
    {
        String submitButtonCss = "#CREATE_SITE_DIALOG_OK";
    	if (findAndWait(By.cssSelector(submitButtonCss)).getAttribute("disabled") == "true")
        {
            return true;
        }
        return false;
    }
    
    /**
     * Selects the visibility required for site to be created/edited.
     * 
     * @param isPrivate boolean
     * @param isModerated boolean
     */
    public void selectVisibility(By visibilitySelector)
    {
        try
        {
            WebElement siteVisibility = findAndWait(visibilitySelector);
            WebElement selection = siteVisibility.findElement(By.cssSelector(".dijitRadio"));
            selection.click();
            return;
        }
        catch(NoSuchElementException nse)
        {
            throw new PageOperationException("Error selecting site visibility", nse);
        }
    }
    
    

    /**
     * Checks if the specified radio button is selected.
     * 
     * @return true if selected
     */
    public boolean isSelected(By selector)
    {
        try
        {
            WebElement siteVisibility = findAndWait(selector);
        	siteVisibility.findElement(By.cssSelector(".dijitRadioChecked"));
        	return true;
        	
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
    
    /**
     * Returns true if sitename in use message is displayed
     * 
     * @return false if warning is not displayed
     */
    public boolean isSiteUsedMessageDisplayed()
    {
        try
        {
            findAndWait(DUPLICATE_SITE_WARNING);
            return true;
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }
    
    /**
     * Returns true if error is displayed for the site id input
     * 
     * @return false if not
     */
    public boolean isSiteIDErrorDisplayed()
    {
        try
        {
            findAndWait(SITE_ID_ERROR);
            return true;
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }

}