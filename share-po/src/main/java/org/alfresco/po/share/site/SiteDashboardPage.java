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

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.Dashboard;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Site dashboard page object, holds all element of the HTML page relating to
 * share's site dashboard page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteDashboardPage extends SitePage implements Dashboard
{
    private static Log logger = LogFactory.getLog(SiteDashboardPage.class);
    private static final String PROJECT_WIKI_ID = "#HEADER_SITE_WIKI-PAGE_text";
    private static final String WELCOME_DASHLET = "div.dashlet.dynamic-welcome";
    private String clickableElements;
    private static final By CONFIGURE_SEARCH_DIALOG_BOX = By
            .cssSelector("div[id$='default-configDialog-configDialog_c'][style*='visibility: visible']>div[id$='_default-configDialog-configDialog']");
    private static final By DASHLET_HELP_BALLOON = By.cssSelector("div[style*='visible']>div>div.balloon");
    private static final By HELP_ICON = By.cssSelector("div[class$='titleBarActionIcon help']");
    private static final By MORE_PAGES_BUTTON = By.cssSelector("#HEADER_SITE_MORE_PAGES");
    private static final By PAGES_LINKS = By.xpath("//div[@id='HEADER_NAVIGATION_MENU_BAR']//a");
    private static final By MENU_BAR = By.cssSelector("div[class^='navigation-menu'] div.alf-menu-bar");
    private static final By DASHLET_TITLE = By.cssSelector(".title");

    /**
     * Constructor
     */
    public SiteDashboardPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteDashboardPage render(RenderTime timer)
    {
        while (true)
        {
            try
            {
                timer.start();
                // Check site is being created message has disappeared
                if (!isJSMessageDisplayed())
                {
                    try
                    {
                        getDashlet("site-members").render(timer);
                        getDashlet("site-contents").render(timer);
                    }
                    catch (PageException pe)
                    {
                        throw new PageException(this.getClass().getName() + " failed to render in time", pe);
                    }
                    return this;
                }
            }
            catch (Exception e)
            {
                // Catch stale element exception caused by js message on page
            }
            finally
            {
                timer.end();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteDashboardPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if we are on site dashboard page and the site profile dashlet is
     * displayed.
     * 
     * @return true if site profile is visible
     */
    public boolean siteProfileDisplayed()
    {
        boolean displayed = false;
        try
        {
            WebElement siteProfile = drone.findAndWait(By.className("site-profile"));
            return siteProfile.isDisplayed();
        }
        catch (ElementNotVisibleException e)
        {
            displayed = false;
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Gets dashlets in the dashboard page.
     * 
     * @param name String title of dashlet
     * @return HtmlPage page object
     */
    @Override
    public Dashlet getDashlet(final String name)
    {
        return FactoryShareDashlet.getPage(drone, name);
    }
    

    /**
     * Return page based on name of the Dash clickable element passed.
     * This method is as of now only direct to Wiki page for other pages enums
     * with returning css selector to be added.
     *
     * example: For Sample Site if you want to navigate to wiki page pass @Project
     *           Wiki@
     * @param name String
     * @return HtmlPage
     */
    public HtmlPage getDashBoardElement(final String name)
    {
        try
        {
            clickableElements = drone.getElement("multiple.clickable.object");
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector(clickableElements));
            for (WebElement webElement : elements)
            {
                if ("More".equals(webElement.getText()) || name.equals(webElement.getText()))
                {
                    webElement.click();
                    if (AlfrescoVersion.Enterprise42.equals(alfrescoVersion) || AlfrescoVersion.Enterprise43.equals(alfrescoVersion))
                    {
                        drone.findAndWait(By.cssSelector(PROJECT_WIKI_ID)).click();
                    }
                    break;
                }
            }
        }
        catch (TimeoutException toe)
        {
        }
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Checks the title of the site
     * 
     * @return boolean
     */
    public boolean isSiteTitle(String title)
    {
        try
        {
            WebElement titleSpan = drone.findAndWait(By.cssSelector(".alfresco-header-Title"));
            return titleSpan.getText().toLowerCase().contains(title.toLowerCase());
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Check if welcome message is displayed.
     * 
     * @return true if displayed.
     */
    public boolean isWelcomeMessageDashletDisplayed()
    {
        try
        {
            return drone.find(By.cssSelector(WELCOME_DASHLET)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException ste)
        {
        }
        return false;
    }

    /**
     * Method to find if the Configure Saved Search dialog displayed
     * 
     * @return True if displayed
     */
    public boolean isConfigureSavedSearchDialogDisplayed()
    {
        try
        {
            drone.waitUntilElementDisappears(CONFIGURE_SEARCH_DIALOG_BOX, 2);
        }
        catch (TimeoutException te)
        {
            return true;
        }
        return false;
    }

    /**
     * Method to find if the Customize Site Dashboard link is displayed
     * 
     * @return True if displayed
     */

    public boolean isCustomizeSiteDashboardLinkPresent()
    {
        try
        {
            SiteNavigation siteNavigation = new SiteNavigation(drone);
            siteNavigation.selectConfigure();
            return drone.find(AbstractSiteNavigation.CUSTOMIZE_SITE_DASHBOARD).isDisplayed();
        }
        catch (NoSuchElementException exc)
        {
            return false;
        }
    }

    /**
     * Method to find if the Edit Site Details link is displayed
     * 
     * @return True if displayed
     */

    public boolean isEditSiteDetailsLinkPresent()
    {

        try
        {
            SiteNavigation siteNavigation = new SiteNavigation(drone);
            siteNavigation.selectConfigure();
            return drone.find(AbstractSiteNavigation.EDIT_SITE_DETAILS).isDisplayed();
        }
        catch (NoSuchElementException exc)
        {
            return false;
        }
    }

    /**
     * Method to find if the Customize Site link is displayed
     * 
     * @return True if displayed
     */

    public boolean isCustomizeSiteLinkPresent()
    {

        try
        {
            SiteNavigation siteNavigation = new SiteNavigation(drone);
            siteNavigation.selectConfigure();
            return drone.find(AbstractSiteNavigation.CUSTOMIZE_SITE).isDisplayed();
        }
        catch (NoSuchElementException exc)
        {
            return false;
        }
    }

    /**
     * Method to find if the Customize Site link is displayed
     * 
     * @return True if displayed
     */

    public boolean isLeaveSiteLinkPresent()
    {
        try
        {
            SiteNavigation siteNavigation = new SiteNavigation(drone);
            siteNavigation.selectConfigure();
            return drone.find(AbstractSiteNavigation.LEAVE_SITE).isDisplayed();
        }
        catch (NoSuchElementException exc)
        {
            return false;
        }
    }

    /**
     * Method used to discursively display help balloon on all the dashlets
     * 
     * @return true if help is available for all the dashlets
     */
    public boolean isHelpDisplayedForAllDashlets()
    {
        List<WebElement> allIcons = drone.findAll(HELP_ICON);

        for (int i = 0; i < allIcons.size(); i++)
        {
            allIcons.get(i).click();
            if (drone.isElementDisplayed(DASHLET_HELP_BALLOON))
                continue;
            else
                return false;
        }
        return true;
    }

    /**
     * Check is more button for site pages displayed.
     * 
     * @return boolean
     */
    public boolean isPagesMoreButtonDisplayed()
    {
        try
        {
            return drone.find(MORE_PAGES_BUTTON).isDisplayed();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Method for get counts links for site pages.
     * 
     * @return int
     */
    public int getPagesLinkCount()
    {
        if (alfrescoVersion == AlfrescoVersion.Enterprise42)
        {
            WebElement navigationHeader = drone.find(MENU_BAR);
            return navigationHeader.findElements(By.cssSelector("div[id^='HEADER_SITE_'] a")).size();

        }
        else
        {
            return drone.findAndWaitForElements(PAGES_LINKS).size();
        }
    }

    /**
     * This method gets all the present Dashlets' titles
     * 
     * @return <List<String>> topic filter links
     */
    public List<String> getTitlesList()
    {
        List<String> list = new ArrayList<String>();
        try
        {
            for (WebElement element : drone.findAll(DASHLET_TITLE))
            {
                String text = element.getText();
                if (text != null)
                {
                    list.add(text);
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find any dashlet", nse);
        }

        return list;
    }
}