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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.Dashboard;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

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
    private static final By MENU_BAR = By.cssSelector("div[class^='navigation-menu'] div.alf-menu-bar");
    private static final By DASHLET_TITLE = By.cssSelector(".title");

    SiteMembersDashlet siteMembersDashlet;
    SiteContentDashlet siteContentDashlet;
    @SuppressWarnings("unchecked")
    @Override
    public SiteDashboardPage render(RenderTime timer)
    {
        if (!this.getPageTitleLabel().equalsIgnoreCase("Moderated"))
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
                            siteMembersDashlet.render(timer);
                            siteContentDashlet.render(timer);
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
                    // Catch stale element exception caused by js message on
                    // page
                }
                finally
                {
                    timer.end();
                }

            }
        }
        else
        {
            basicRender(timer);
            return this;

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteDashboardPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
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
            WebElement siteProfile = findAndWait(By.className("site-profile"));
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
        return factoryPage.getDashlet(driver, name);
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
            clickableElements = getValue("multiple.clickable.object");
            List<WebElement> elements = findAndWaitForElements(By.cssSelector(clickableElements));
            for (WebElement webElement : elements)
            {
                if ("More".equals(webElement.getText()) || name.equals(webElement.getText()))
                {
                    webElement.click();
                    findAndWait(By.cssSelector(PROJECT_WIKI_ID)).click();
                    break;
                }
            }
        }
        catch (TimeoutException toe)
        {
        }
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
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
            WebElement titleSpan = findAndWait(By.cssSelector(".alfresco-header-Title"));
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
            return driver.findElement(By.cssSelector(WELCOME_DASHLET)).isDisplayed();
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
            waitUntilElementDisappears(CONFIGURE_SEARCH_DIALOG_BOX, 2);
        }
        catch (TimeoutException te)
        {
            return true;
        }
        return false;
    }

    SiteNavigation siteNavigation;
    /**
     * Method to find if the Customize Site Dashboard link is displayed
     * 
     * @return True if displayed
     */

    public boolean isCustomizeSiteDashboardLinkPresent()
    {
        try
        {
            siteNavigation.selectConfigure();
            WebElement option = driver.findElement(AbstractSiteNavigation.CUSTOMIZE_SITE_DASHBOARD);
            return option.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        	logger.error("Cannot find CustomizeSiteDashboard link.", nse);
        }
        return false;
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
            siteNavigation.selectConfigure();
            WebElement option = driver.findElement(AbstractSiteNavigation.EDIT_SITE_DETAILS);
            return option.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        	logger.error("Cannot find Edit Site Details Link.", nse);
        }
        return false;
    }


    /**
     * Method to find if the Leave Site link is displayed
     * 
     * @return True if displayed
     */

    public boolean isLeaveSiteLinkPresent()
    {
        try
        {
            siteNavigation.selectConfigure();
            WebElement option = driver.findElement(AbstractSiteNavigation.LEAVE_SITE);
            return option.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Cannot find Leave Site Link.", nse);
        }
        return false;
     }
    
    /**
     * Method to find if the Request to Join/Join Site link is displayed
     * 
     * @return True if displayed
     */

    public boolean isJoinSiteLinkPresent()
    {
        try
        {
            siteNavigation.selectConfigure();
            WebElement option = driver.findElement(AbstractSiteNavigation.JOIN_SITE);
            return option.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        	logger.error("Cannot find Edit Site Details Link.", nse);
        }
        return false;
    }
    
    /**
     * Method to find if the Cancel site request link is displayed
     * 
     * @return True if displayed
     */

    public boolean isCancelSiteRequestLinkPresent()
    {
        try
        {
            siteNavigation.selectConfigure();
            WebElement option = driver.findElement(AbstractSiteNavigation.CANCEL_SITE_REQUEST);
            return option.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        	logger.error("Cannot find Cancel Site Request Link.", nse);
        }
        return false;
    }

    /**
     * Method used to discursively display help balloon on all the dashlets
     * 
     * @return true if help is available for all the dashlets
     */
    public boolean isHelpDisplayedForAllDashlets()
    {
        List<WebElement> allIcons = driver.findElements(HELP_ICON);

        for (int i = 0; i < allIcons.size(); i++)
        {
            allIcons.get(i).click();
            if (isElementDisplayed(DASHLET_HELP_BALLOON))
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
            WebElement option = driver.findElement(MORE_PAGES_BUTTON);
            return option.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        	logger.error("Cannot find Pages More Button.", nse);
        }
        return false;
    }

    /**
     * Method for get counts links for site pages.
     * 
     * @return int
     */
    public int getPagesLinkCount()
    {
        WebElement navigationHeader = driver.findElement(MENU_BAR);
        return navigationHeader.findElements(By.cssSelector("div[id^='HEADER_SITE_'] a")).size();
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
            for (WebElement element : driver.findElements(DASHLET_TITLE))
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
    
    /**
     * Method to request to join the site if option is displayed
     * 
     * @return HtmlPage
     */

    public HtmlPage requestToJoinSite()
    {
        try
        {
            siteNavigation.selectConfigure();
            WebElement requestJoinSite = driver.findElement(AbstractSiteNavigation.JOIN_SITE);
            if (requestJoinSite.isDisplayed())
            {
            	requestJoinSite.click();   
            	waitUntilAlert();
                return factoryPage.getPage(driver);
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Request to Join Site button not found", nse);
        }
        throw new PageOperationException("Unable to Request to Join Site");
    }
    
    /**
     * Method to request to cancel the site joining request
     * 
     * @return HtmlPage
     */

    public HtmlPage cancelRequestToJoinSite()
    {
        try
        {
            siteNavigation.selectConfigure();
            WebElement cancelRequestToJoinSite = driver.findElement(AbstractSiteNavigation.CANCEL_SITE_REQUEST);
            if (cancelRequestToJoinSite.isDisplayed())
            {
            	cancelRequestToJoinSite.click();   
            	waitUntilAlert();
                return factoryPage.getPage(driver);
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Cancel Request to Join Site button not found", nse);
        }
        throw new PageOperationException("Unable to cancel Request to Join Site");
    }
    
    
}
