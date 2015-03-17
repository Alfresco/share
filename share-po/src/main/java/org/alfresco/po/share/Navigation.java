/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.share.ShareUtil.RequiredAlfrescoVersion;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.adminconsole.TagManagerPage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.search.FacetedSearchConfigPage;
import org.alfresco.po.share.search.FacetedSearchHeaderSearchForm;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.document.MyFilesPage;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent elements found on the HTML page relating to the main navigation bar
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class Navigation extends SharePage
{
    private static Log logger = LogFactory.getLog(Navigation.class);
    private static final String SITE_FINDER_LINK = "div[id$='app_sites-sites-menu']>div>ul[class^='site-finder-menuitem']>li>a";
    private static final String DEFAULT_NETWORK_MENU_BUTTON = "default.network.dropdown";
    private static final String NETWORK_NAMES = "network.names";
    private static final String MY_SITES = "td[id^='HEADER_SITES_MENU_MY_SITES'] a";
    public static final String REPO_ADMIN_MANAGE_SITE_LINK_SELECTOR = "div#HEADER_ADMIN_CONSOLE";
    public static final String SITE_ADMIN_MANAGE_SITE_LINK_SELECTOR = "span[id='HEADER_SITES_CONSOLE_text']>a";
    private static final String SELECT_SITE_AS_FAVOURITE = "#HEADER_SITES_MENU_ADD_FAVOURITE_text";
    private static final String REMOVE_SITE_AS_FAVOURITE = "#HEADER_SITES_MENU_REMOVE_FAVOURITE_text";
    private static final String RECENT_SITES = "td[id^='HEADER_SITES_MENU_RECENT'] a";
    private static final String FAVOURITE_TEXT = "div[id^='HEADER_SITES_MENU_FAVOURITES'] td[class$='dijitMenuItemLabel']";
    private static final String FAVOURITE_SITES = "div[id^='HEADER_SITES_MENU_FAVOURITES'] td[class$='dijitMenuItemLabel'] a";
    private static final String LINK_FAVOURITES = "#HEADER_SITES_MENU_FAVOURITES_text";
    private static final String SHARED_FILES_LINK = "//span[@id='HEADER_SHARED_FILES_text']/a";
    private static final String MY_FILES_LINK = "div#HEADER_MY_FILES";
    private static final By ADV_SEARCH_DROP_DOWN = By.cssSelector("div#HEADER_SEARCH_BOX_DROPDOWN_MENU");
    private static final By ADV_SEARCH_DROP_DOWN_MENU = By.cssSelector("#HEADER_SEARCH_BOX_DROPDOWN_MENU_dropdown");
    private static final By ADV_SEARCH_BOX_TEXT = By.cssSelector("#HEADER_SEARCH_BOX_ADVANCED_SEARCH_text a");
    private static final String REPORTING = "div#HEADER_PENTAHO";

    /**
     * Constructor
     *
     * @param drone WebDriver browser client
     */
    public Navigation(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Navigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Navigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Navigation render(final long time)
    {
        return render(new RenderTime(time));
    }


    /**
     * Mimics the action of selecting the dashboard link.
     *
     * @return HtmlPage dashboard page object
     */
    public DashBoardPage selectMyDashBoard()
    {
        String selector = isDojoSupport() ? "div#HEADER_HOME" : "a[id$='-dashboard-button']";
        drone.findAndWait(By.cssSelector(selector)).click();
        return new DashBoardPage(drone);
    }

    /**
     * Mimics the action of selecting people finder link.
     *
     * @return HtmlPage people finder page object
     */
    public PeopleFinderPage selectPeople()
    {
        String selector = isDojoSupport() ? "div#HEADER_PEOPLE" : "a[id$='people-button']";
    	drone.findAndWait(By.cssSelector(selector)).click();
        return new PeopleFinderPage(drone);
    }

    /**
     * Mimics the action of selecting site finder link.
     *
     * @return HtmlPage people finder page object
     */
    public SiteFinderPage selectSearchForSites()
    {
        selectSitesDropdown();
        try
        {
            if (isDojoSupport())
            {
                drone.findAndWait(By.cssSelector(drone.getElement("site.finder"))).click();
            }
            else
            {
                drone.findAndWait(By.cssSelector(SITE_FINDER_LINK)).click();
            }
        }
        catch (NoSuchElementException nse)
        {
            //Try again
            selectSearchForSites();
        }
        return new SiteFinderPage(drone);
    }

    /**
     * Mimics the action of selecting create site link.
     *
     * @return HtmlPage people finder page object
     */
    public HtmlPage selectCreateSite()
    {
        String selector = isDojoSupport() ? "td#HEADER_SITES_MENU_CREATE_SITE_text" : "ul.create-site-menuitem>li>a";
        selectSitesDropdown();
        drone.findAndWait(By.cssSelector(selector)).click();
        drone.waitForElement(By.cssSelector("div[id*='createSite-instance-dialog_c']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return drone.getCurrentPage();
    }

    /**
     * Selects the "Sites" icon on main navigation. As this link is created by
     * Java script, a wait is implemented to ensure the link is rendered.
     */
    protected void selectSitesDropdown()
    {
        try
        {
            // Wait is applied as the link is within a java script.
            String selector = isDojoSupport() ? "div#HEADER_SITES_MENU" : "button[id$='app_sites-button']";
            WebElement siteButton = drone.findAndWait(By.cssSelector(selector));
            siteButton.click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the Sites dropdown css.", te);
        }
    }

    /**
     * Selects the "Reporting" icon on main navigation. As this link is created by
     * Java script, a wait is implemented to ensure the link is rendered.
     */
    public void selectReportingDropdown()
    {
        try
        {
            WebElement reportingButton = drone.findAndWait(By.cssSelector(REPORTING));
            reportingButton.click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the Reporting dropdown css.", te);
        }
    }

    /**
     * Checks if Reporting is displayed
     * 
     * @return
     */
    public boolean isReportingVisible()
    {
        boolean isReportingVisible = false;
        try
        {
            WebElement element = drone.find(By.cssSelector(REPORTING));
            isReportingVisible = element.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
        }
        return isReportingVisible;

    }
    
    /**
     * Select Analyze from Reporting dropdown.
     * @return
     */
    public HtmlPage selectAnalyze()
    {
        try
        {
            selectReportingDropdown(); 
            drone.findAndWait(By.cssSelector("td#HEADER_PENTAHO_ANALYZE_text a")).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch(TimeoutException toe)
        {
            logger.error("Analyze option is not found in the Reporting options", toe);
        }
        throw new PageOperationException("Analyze option is not found in the Reporting options");
    }
      
    
    /**
     * Select Analyze Site from Reporting dropdown.
     * @return
     */
    public HtmlPage selectAnalyzeSite()
    {
        try
        {
            selectReportingDropdown(); 
            drone.findAndWait(By.cssSelector("td#HEADER_PENTAHO_ANALYZE_SITE_text a")).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch(TimeoutException toe)
        {
            logger.error("Analyze Site option is not found in the Reporting options", toe);
        }
        throw new PageOperationException("Analyze Site option is not found in the Reporting options");
    }
    
    
    /**
     * Selects the user link on main navigation. As this link is created by Java
     * script, a wait is implemented to ensure the link is rendered.
     * @return {@link UserPage}
     */
    public UserPage selectUserDropdown()
    {
        try
        {
            WebElement userButton = drone.find(By.id("HEADER_USER_MENU_POPUP"));
            userButton.click();
            return new UserPage(drone);
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("User drop down is not present", e);
        }
    }
    
    /**
     * Mimics the action of selecting my profile link.
     *
     * @return {@link MyProfilePage}
     */
    public MyProfilePage selectMyProfile()
    {
        return selectUserDropdown().render().selectMyProfile();
    }

    /**
     * Mimics the action of selecting Account SoSettings link.
     *
     * @return {@link AccountSettingsPage}
     */
    public AccountSettingsPage selectAccountSettingsPage()
    {
        return selectUserDropdown().render().selectAccountSettingsPage();
    }

    /**
     * Mimics the action of selecting my profile link.
     *
     * @return {@link ChangePasswordPage}
     */
    public ChangePasswordPage selectChangePassword()
    {
        return selectUserDropdown().render().selectChangePassword();
    }

    /**
     * Mimics the action of selecting logout link.
     * The page returned from a logout is a LoginPage.
     *
     * @return {@link LoginPage} page response
     */
    public LoginPage logout()
    {
        return selectUserDropdown().render().logout();
    }

    /**
     * Mimics the action of selecting repository link.
     *
     * @return HtmlPage repository page object
     */
    public RepositoryPage selectRepository()
    {
        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.ENTERPRISE_ONLY);
        String selector = isDojoSupport() ? "div#HEADER_REPOSITORY" : "a[id$='app_repository-button']";
        drone.find(By.cssSelector(selector)).click();
        return new RepositoryPage(drone);
    }

    private WebElement clickAdvSearchDropDown()
    {
        try
        {
            WebElement advSearchDropDown = drone.findAndWait(ADV_SEARCH_DROP_DOWN);
            advSearchDropDown.click();
            WebElement advSearchDrpDownMenu = drone.findAndWait(ADV_SEARCH_DROP_DOWN_MENU);
            return advSearchDrpDownMenu;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Cannot find " + ADV_SEARCH_DROP_DOWN + " or " + ADV_SEARCH_DROP_DOWN_MENU);
        }
    }

    public boolean isAdvSearchLinkPresent()
    {
        return clickAdvSearchDropDown().findElement(ADV_SEARCH_BOX_TEXT).isDisplayed();
    }

    /**
     * Select the advance search button from dropdown.
     *
     * @return HtmlPage advance search page.
     */
    public AdvanceSearchContentPage selectAdvanceSearch()
    {
        try
        {
            if (isDojoSupport())
            {
//                // TODO ALF-19185 - Bug Advance Search
//                String usersPageURL = "/page/advsearch";
//                String currentUrl = drone.getCurrentUrl();
//                if (currentUrl != null)
//                {
//                    String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
//                    drone.navigateTo(url);
//                }
                WebElement theMenu = clickAdvSearchDropDown();
                theMenu.findElement(ADV_SEARCH_BOX_TEXT).click();
            }
            else
            {
                drone.findAndWait(By.cssSelector("button[id$='default-search_more-button']")).click();
                drone.findAndWait(By.cssSelector("div[id$='searchmenu_more']>div>ul>li>a")).click();
            }

            return new AdvanceSearchContentPage(drone);
        }
        catch (TimeoutException ne)
        {
            throw new PageException("Advance Search is not visible");
        }
    }

    /**
     * Navigates to the users page on Admin Console - Enterprise Only option.
     *
     * @return {@link UserSearchPage} Instance of UserSearchPage
     */
    public HtmlPage getUsersPage()
    {
        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.ENTERPRISE_ONLY);
        //TODO To be implemented by using UI once JIRA: https://issues.alfresco.com/jira/browse/ALF-18909 is resolved 
        String usersPageURL = "/page/console/admin-console/users";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new UserSearchPage(drone);
    }

    /**
     * Selects the Network dropdown button present on UserDashBoard.
     *
     * @return {@link DashBoardPage}
     */
    public DashBoardPage selectNetworkDropdown()
    {
        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.CLOUD_ONLY);

        try
        {
            String dropDownElementId = drone.getElement(DEFAULT_NETWORK_MENU_BUTTON);
            drone.findAndWait(By.cssSelector(dropDownElementId)).click();
            return new DashBoardPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageException(this.getClass().getName() + " : selectNetworkDropdown() : failed to render in time. ", e);
        }
    }

    /**
     * Selects the given User Network link present in list of networks.
     *
     * @param networkName String name
     * @return {@link DashBoardPage}
     */
    public DashBoardPage selectNetwork(final String networkName)
    {
        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.CLOUD_ONLY);
        if (StringUtils.isEmpty(networkName))
        {
            throw new IllegalArgumentException("Network name is required.");
        }
        try
        {
            String networkNamesid = drone.getElement(NETWORK_NAMES);
            List<WebElement> networks = drone.findAndWaitForElements(By.cssSelector(networkNamesid));
            {
                for (WebElement network : networks)
                {
                    if (network.getText() != null && network.getText().equalsIgnoreCase(networkName.trim()))
                    {
                        network.click();
                        return new DashBoardPage(drone);
                    }
                }
            }
            throw new PageException("Unable to find the User Network : " + networkName);
        }
        catch (TimeoutException e)
        {
            throw new PageException(this.getClass().getName() + " : selectNetwork() : failed to render in time. ", e);
        }
    }

    /**
     * Gets the list of user networks.
     *
     * @return List<String>
     */
    public List<String> getUserNetworks()
    {
        List<WebElement> networks = null;
        try
        {
            String defaultNetworkButton = drone.getElement(DEFAULT_NETWORK_MENU_BUTTON);
            drone.findAndWait(By.cssSelector(defaultNetworkButton)).click();
            String networkNamesid = drone.getElement(NETWORK_NAMES);
            networks = drone.findAndWaitForElements(By.cssSelector(networkNamesid));
            if (networks != null)
            {
                List<String> networkList = new ArrayList<>();
                for (WebElement network : networks)
                {
                    networkList.add(network.getText());
                }
                return networkList;
            }
            throw new PageException("Not able to find the any networks");
        }
        catch (TimeoutException e)
        {
            throw new PageException(this.getClass().getName() + " : selectUserNetwork() : failed to render in time. ", e);
        }
    }

    /**
     * Selects the user link on main navigation. As this link is created by Java
     * script, a wait is implemented to ensure the link is rendered.
     *
     * @return {@link MyTasksPage}
     */
    public MyTasksPage selectMyTasks()
    {
        if (isDojoSupport())
        {
            if (!alfrescoVersion.isCloud())
            {
                drone.find(By.cssSelector("#HEADER_TASKS")).click();
            }
            drone.find(By.cssSelector("#HEADER_MY_TASKS")).click();
        }
        else
        {
            drone.find(By.cssSelector("button[id$='default-app_more-button']")).click();
            drone.find(By.cssSelector("div[id$='-appmenu_more']>div>ul:first-of-type>li:first-of-type>a")).click();
        }
        return new MyTasksPage(drone);
    }

    /**
     * Method to select "Workflows I've Started" under Tasks navigation menu item
     *
     * @return {@link MyWorkFlowsPage}
     */
    public MyWorkFlowsPage selectWorkFlowsIHaveStarted()
    {
        try
        {
            drone.find(By.cssSelector("#HEADER_TASKS")).click();
            drone.find(By.cssSelector("td#HEADER_MY_WORKFLOWS_text")).click();
            return new MyWorkFlowsPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find Workflows I've started link", nse);
        }
    }

    /**
     * Navigates to the groups page on Admin Console - Enterprise Only option.
     *
     * @return {@link GroupsPage} Instance of UserSearchPage
     */
    public GroupsPage getGroupsPage()
    {
        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.ENTERPRISE_ONLY);
        //TODO To be implemented by using UI once JIRA: https://issues.alfresco.com/jira/browse/ALF-18909 is resolved 
        String usersPageURL = "/page/console/admin-console/groups";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
            drone.waitForPageLoad(3000);
        }
        return new GroupsPage(drone);
    }

    /**
     * Select admin tools.
     *
     * @return the html page
     */
    public HtmlPage selectAdminTools()
    {
        drone.find(By.cssSelector(REPO_ADMIN_MANAGE_SITE_LINK_SELECTOR)).click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Select manage sites link from home page by Site Admin.
     *
     * @return the html page
     */

    public HtmlPage selectManageSitesSiteAdmin()
    {
        drone.find(By.cssSelector(SITE_ADMIN_MANAGE_SITE_LINK_SELECTOR)).click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Does the current page have a manage-sites link in the header?
     *
     * @return boolean
     */
    public boolean hasSelectManageSitesSiteAdminLink()
    {
        List<org.openqa.selenium.WebElement> elements = drone.findAll(By.cssSelector(SITE_ADMIN_MANAGE_SITE_LINK_SELECTOR));
        return (elements.size() != 0);
    }
    
    /**
     * Select manage sites link as Admin.
     *
     * @return the html page
     */

    public HtmlPage selectManageSitesRepoAdmin()
    {
        selectAdminTools().render();
        String selector = "ul.toolLink > li > span > a[href=\"manage-sites\"]";
        drone.find(By.cssSelector(selector)).click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Does the current page have an Admin Tools link in the header?
     *
     * @return boolean
     */
    private boolean hasSelectManageSitesRepoAdmin()
    {
        List<org.openqa.selenium.WebElement> elements = drone.findAll(By.cssSelector(REPO_ADMIN_MANAGE_SITE_LINK_SELECTOR));
        return (elements.size() != 0);
    }

    /**
     * Select manage sites link as Network Admin.
     *
     * @return the html page
     */

    public HtmlPage selectManageSitesNetworkAdmin()
    {
        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.CLOUD_ONLY);
        // Navigate direct to URL as link isn't visible on page.
        String manageSitesPageURL = "/page/console/cloud-console/manage-sites";
        String currentUrl = drone.getCurrentUrl();
        String url = currentUrl.replaceFirst("^*/page.*", manageSitesPageURL);
        drone.navigateTo(url);
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Abstract the manage sites page to choose.
     *
     * @return the manage sites page
     */
    public HtmlPage selectManageSitesPage()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Finding the manage sites page.");
        }
        try
        {
            if (alfrescoVersion.isCloud())
            {
                return selectManageSitesNetworkAdmin();
            }
            else if (hasSelectManageSitesRepoAdmin())
            {
                return selectManageSitesRepoAdmin();
            }
            else if (hasSelectManageSitesSiteAdminLink())
            {
                return selectManageSitesSiteAdmin();
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Unable to select manage sites link", e);
        }
        throw new UnsupportedOperationException("The correct method for finding the manage sites page couldn't be determined");
    }

    /**
     * Mimics the action of selecting on the
     * user dashboard configuration icon
     */
    private void selectUserDashboardConfigurationIcon()
    {
        drone.findAndWait(By.id("HEADER_CUSTOMIZE_USER_DASHBOARD")).click();
    }

    /**
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseUserDashboardPage selectCustomizeUserDashboard()
    {
        if (alfrescoVersion.isDojoSupported())
        {
            selectUserDashboardConfigurationIcon();
        }
        return new CustomiseUserDashboardPage(getDrone());
    }

    /**
     * Go to Node-Browser  page use direct URL.
     *
     * @return {@link org.alfresco.po.share.adminconsole.NodeBrowserPage}
     */
    public NodeBrowserPage getNodeBrowserPage()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
        String usersPageURL = "/page/console/admin-console/node-browser";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new NodeBrowserPage(drone).render();
    }

    /**
     * Go to Node-Browser  page use direct URL.
     *
     * @return {@link org.alfresco.po.share.adminconsole.CategoryManagerPage}
     */
    public CategoryManagerPage getCategoryManagerPage()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
        String usersPageURL = "/page/console/admin-console/category-manager";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new CategoryManagerPage(drone).render();
    }

    /**
     * Check if "CreateSite" element is present in the sites menu.
     *
     * @return
     */
    public boolean isCreateSitePresent()
    {
        String selector = isDojoSupport() ? "td#HEADER_SITES_MENU_CREATE_SITE_text" : "ul.create-site-menuitem>li>a";
        selectSitesDropdown();
        if(drone.findAndWait(By.cssSelector(selector)).isDisplayed())
        {
        return true;               
        }
        else
        {
            throw new UnsupportedOperationException("Not able to find create site");
        }                    
    }


    /**
     * Check for details of site is favourite or not.
     *
     * @return
     */
    public boolean isSiteFavourtie()
    {
        try
        {
            if (drone.getCurrentPage() instanceof SiteDashboardPage)
            {
                selectSitesDropdown();
                if (drone.isElementDisplayed(By.cssSelector(SELECT_SITE_AS_FAVOURITE)))
                {
                    return false;
                }
                else if (drone.isElementDisplayed(By.cssSelector(REMOVE_SITE_AS_FAVOURITE)))
                {
                    return true;
                }
                else
                {
                    throw new UnsupportedOperationException("User has to be in Site DashBoard page.");
                }

            }

        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("No option available to check site favourtie.", nse);
        }
        throw new UnsupportedOperationException("User has to be in Site DashBoard page.");
    }

    /**
     * Set Site as favourite.
     *
     * @return
     */
    public HtmlPage setSiteAsFavourite()
    {
        try
        {
            if (drone.getCurrentPage() instanceof SiteDashboardPage)
            {
                selectSitesDropdown();
                drone.find(By.cssSelector(SELECT_SITE_AS_FAVOURITE)).click();
                return FactorySharePage.resolvePage(drone);
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No option available to make site favourtie.", nse);
            throw new PageException("No option available to make site favourtie.", nse);
        }
        throw new UnsupportedOperationException("User has to be in Site DashBoard page.");
    }

    /**
     * Remove site as favourite.
     *
     * @return
     */
    public HtmlPage removeFavourite()
    {

        try
        {
            if (drone.getCurrentPage() instanceof SiteDashboardPage)
            {
                selectSitesDropdown();
                drone.find(By.cssSelector(REMOVE_SITE_AS_FAVOURITE)).click();
                return FactorySharePage.resolvePage(drone);
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No option available to remove site from favourtie.", nse);
            throw new PageException("No option available to remove site from favourtie.", nse);
        }
        throw new UnsupportedOperationException("User has to be in Site DashBoard page.");
    }

    //

    /**
     * Get the names of all recently visited sites.
     *
     * @return
     */
    public List<String> getRecentSitesPresent()
    {
        List<String> siteNames = new ArrayList<String>();
        try
        {

            selectSitesDropdown();
            List<WebElement> sites = drone.findAndWaitForElements(By.cssSelector(RECENT_SITES));
            for (WebElement webElement : sites)
            {
                siteNames.add(webElement.getText());
            }
            return siteNames;

        }
        catch (TimeoutException nse)
        {
            throw new PageOperationException("No Recent Site(s) Available", nse);
        }
    }

    /**
     * Does any sites been selectd as favourite.
     *
     * @return
     */
    public boolean doesAnyFavouriteSiteExist()
    {
        boolean doesAnyFavouriteSiteExist = true;
        try
        {
            selectFavourties();
            WebElement element = drone.findFirstDisplayedElement(By.cssSelector(FAVOURITE_TEXT));
            if (drone.getValue("no.favourites").equals(element.getText()))
            {
                doesAnyFavouriteSiteExist = false;
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Fvourties option is not found in the option", nse);
        }
        return doesAnyFavouriteSiteExist;
    }

    /**
     * Get list of favourite sites available.
     *
     * @return
     */
    public List<String> getFavouriteSites()
    {
        List<String> siteNames = new ArrayList<String>();
        try
        {
            selectFavourties();
            List<WebElement> sites = drone.findAll(By.cssSelector(FAVOURITE_SITES));
            for (WebElement webElement : sites)
            {
                siteNames.add(webElement.getText());
            }
            return siteNames;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Fvourties option is not found in the option", nse);
        }
        throw new PageOperationException("Fvourties option is not found in the option");
    }

    /**
     * Select favourites.
     *
     * @return
     */
    private HtmlPage selectFavourties()
    {
        try
        {
            //Refresh is needed since sites added in favourites dont reflect.
            drone.refresh();

            selectSitesDropdown();
            drone.findAndWait(By.cssSelector(LINK_FAVOURITES)).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Fvourties option is not found in the option", nse);
        }
        throw new PageOperationException("Fvourties option is not found in the option");
    }

    /**
     * Select My Sites from sites dropdown.
     *
     * @return
     */
    public HtmlPage selectMySites()
    {
        try
        {
            //Refresh is needed since sites added in favourites dont reflect.
            selectSitesDropdown();
            drone.findAndWait(By.cssSelector(MY_SITES)).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException nse)
        {
            logger.error("My Sites option is not found in the options", nse);
        }
        throw new PageOperationException("My Sites option is not found in the options");
    }

    /**
     * Mimics the action of selecting shared files link.
     *
     * @return HtmlPage shared files page object
     */
    public SharedFilesPage selectSharedFilesPage()
    {
        try
        {
            drone.find(By.xpath(SHARED_FILES_LINK)).click();
        }
        catch (InvalidElementStateException ise)
        {
        }
        return new SharedFilesPage(drone);
    }

    /**
     * Navigates to the faceted search page.
     *
     * @return {@link FacetedSearchPage} Instance of FacetedSearchPage
     */
    public HtmlPage getFacetedSearchPage()
    {
        String facetedSearchPageURL = "/page/dp/ws/faceted-search";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", facetedSearchPageURL);
            drone.navigateTo(url);
        }
        return new FacetedSearchPage(drone);
    }

    /**
     * Navigates to the faceted search config page.
     * 
     * @return {@link FacetedSearchConfigPage} Instance of FacetedSearchConfigPage
     */
    public HtmlPage getFacetedSearchConfigPage()
    {
        String facetedSearchConfigPageURL = "/page/dp/ws/faceted-search-config";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", facetedSearchConfigPageURL);
            drone.navigateTo(url);
        }
        return new FacetedSearchConfigPage(drone);
    }

    /**
     * Go to Admin Console page (application) use direct URL.
     *
     * @return {@link org.alfresco.po.share.admin.AdminConsolePage}
     */
    public AdminConsolePage getAdminConsolePage()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
        String usersPageURL = "/page/console/admin-console/application";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new AdminConsolePage(drone).render();
    }

    /**
     * Go to Tag Manager page use direct URL.
     *
     * @return {@link org.alfresco.po.share.adminconsole.TagManagerPage}
     */
    public TagManagerPage getTagManagerPage()
    {
        if (alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is Enterprise only, not available for cloud");
        }
        String usersPageURL = "/page/console/admin-console/tag-management";
        String currentUrl = drone.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            drone.navigateTo(url);
        }
        return new TagManagerPage(drone).render();
    }

    /**
     * Mimics the action of selecting My files link.
     *
     * @return HtmlPage shared files page object
     */
    public MyFilesPage selectMyFilesPage()
    {
        drone.find(By.cssSelector(MY_FILES_LINK)).click();
        return new MyFilesPage(drone);
    }

    /**
     * Method to perfrom search using header search bar
     *
     * @param searchString
     * @return
     */
    public FacetedSearchPage performSearch(String searchString)
    {
        FacetedSearchHeaderSearchForm facetedSearchHeaderSearchForm
            = new FacetedSearchHeaderSearchForm(drone);
        facetedSearchHeaderSearchForm.search(searchString);

        return drone.getCurrentPage().render();
    }
}