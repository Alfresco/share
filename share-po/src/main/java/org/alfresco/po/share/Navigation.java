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
package org.alfresco.po.share;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.search.FacetedSearchConfigPage;
import org.alfresco.po.share.search.FacetedSearchHeaderSearchForm;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.MyFilesPage;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Represent elements found on the HTML page relating to the main navigation bar
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
@FindBy(tagName = "nav")
public class Navigation extends PageElement
{

    private static Log logger = LogFactory.getLog(Navigation.class);
    private static final By FAVOURITE_ID = By.id("HEADER_SITES_MENU_REMOVE_FAVOURITE_text");
    private static final By ADVANCE_SEARCH = By.id("HEADER_SEARCH_BOX_ADVANCED_SEARCH_text");
    private static final By CREATE_SITE_LINK = By.id("HEADER_SITES_MENU_CREATE_SITE_text");
    public static final String REPO_ADMIN_MANAGE_SITE_LINK_SELECTOR = "div#HEADER_ADMIN_CONSOLE";
    public static final String SITE_ADMIN_MANAGE_SITE_LINK_SELECTOR = "span[id='HEADER_SITES_CONSOLE_text']>a";
    private static final String SELECT_SITE_AS_FAVOURITE = "#HEADER_SITES_MENU_ADD_FAVOURITE_text";
    private static final String RECENT_SITES = "td[id^='HEADER_SITES_MENU_RECENT'] a";
    private static final String FAVOURITE_TEXT = "div[id^='HEADER_SITES_MENU_FAVOURITES'] td[class$='dijitMenuItemLabel']";
    private static final String FAVOURITE_SITES = "div[id^='HEADER_SITES_MENU_FAVOURITES'] td[class$='dijitMenuItemLabel'] a";
    private static final String ADMIN_TOOLS_LINK_SELECTOR = "div#HEADER_ADMIN_CONSOLE";
    private static final String NON_ADMIN_TOOLS_LINK_SELECTOR = "div[id='HEADER_NON_ADMIN_ADMIN_CONSOLE']";
    private static final String MANAGE_CUSTOM_MODELS_LINK_SELECTOR = "tr[id='HEADER_CUSTOM_MODEL_MANAGER_CONSOLE']>td>a";
    @FindBy(id="HEADER_HOME_text")Link home;
    /**
     * Mimics the action of selecting the dashboard link.
     * 
     * @return HtmlPage dashboard page object
     */
    public HtmlPage selectMyDashBoard()
    {
        home.click();
        return getCurrentPage().render();
    }

    @FindBy(id = "HEADER_PEOPLE_text")
    Link peopleFinder;

    /**
     * Mimics the action of selecting people finder link.
     * 
     * @return HtmlPage people finder page object
     */
    public PeopleFinderPage selectPeople()
    {
        peopleFinder.click();
        return getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting site finder link.
     * 
     * @return HtmlPage people finder page object
     */
    public HtmlPage selectSearchForSites()
    {
        selectSitesDropdown();
        try
        {
            driver.findElement(By.cssSelector(getValue("site.finder"))).click();
        }
        catch (NoSuchElementException nse)
        {
            // Try again
            selectSearchForSites();
        }
        return getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting create site link.
     * 
     * @return HtmlPage people finder page object
     */
    public HtmlPage selectCreateSite()
    {
        selectSitesDropdown();
        findAndWait(CREATE_SITE_LINK).click();
        return factoryPage.instantiatePage(driver, CreateSitePage.class);
    }

    @FindBy(id = "HEADER_SITES_MENU_text")
    WebElement siteDropdown;

    /**
     * Selects the "Sites" icon on main navigation. As this link is created by
     * Java script, a wait is implemented to ensure the link is rendered.
     */
    protected void selectSitesDropdown()
    {
        try
        {
            // Wait is applied as the link is within a java script.
            siteDropdown.click();
            driver.findElement(By.id("HEADER_SITES_MENU_dropdown")).isDisplayed();
        }
        catch (NoSuchElementException te)
        {
            throw new PageOperationException("Unable to find site dropdown.", te);
        }
    }

    @FindBy(id = "HEADER_USER_MENU_POPUP")
    WebElement userDropdown;

    /**
     * Selects the user link on main navigation. As this link is created by Java
     * script, a wait is implemented to ensure the link is rendered.
     * 
     * @return {@link UserPage}
     */
    public UserPage selectUserDropdown()
    {
        userDropdown.click();
        return factoryPage.instantiatePage(driver, UserPage.class);
    }

    /**
     * Mimics the action of selecting my profile link.
     * 
     * @return {@link MyProfilePage}
     */
    public HtmlPage selectMyProfile()
    {
        selectUserDropdown();
        driver.findElement(By.id("HEADER_USER_MENU_PROFILE_text")).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting Account SoSettings link.
     * 
     * @return {@link AccountSettingsPage}
     */
    public HtmlPage selectAccountSettingsPage()
    {
        selectUserDropdown();
        selectAccountSettingsPage();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting my profile link.
     * 
     * @return {@link ChangePasswordPage}
     */
    public HtmlPage selectChangePassword()
    {
        selectUserDropdown();
        driver.findElement(By.id("HEADER_USER_MENU_CHANGE_PASSWORD_text")).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting logout link.
     * The page returned from a logout is a LoginPage.
     * 
     * @return {@link LoginPage} page response
     */
    public HtmlPage logout()
    {
        selectUserDropdown();
        driver.findElement(By.id("HEADER_USER_MENU_LOGOUT_text")).click();
        return getCurrentPage();
    }

    @FindBy(id = "HEADER_REPOSITORY_text")
    Link repository;

    /**
     * Mimics the action of selecting repository link.
     * 
     * @return HtmlPage repository page object
     */
    public HtmlPage selectRepository()
    {
        repository.click();
        return getCurrentPage();
    }

    @FindBy(id = "HEADER_SEARCH_BOX_DROPDOWN_MENU")
    WebElement advSearchDropDown;

    private void clickAdvSearchDropDown()
    {
        advSearchDropDown.click();
    }

    @FindBy(id = "HEADER_SEARCH_BOX_ADVANCED_SEARCH_text")
    Link advanceSearch;

    public boolean isAdvSearchLinkPresent()
    {
        clickAdvSearchDropDown();
        return isDisplayed(ADVANCE_SEARCH);
    }

    /**
     * Select the advance search button from dropdown.
     * 
     * @return HtmlPage advance search page.
     */
    public HtmlPage selectAdvanceSearch()
    {
        clickAdvSearchDropDown();
        driver.findElement(ADVANCE_SEARCH).click();
        return getCurrentPage();
    }

    /**
     * Navigates to the users page on Admin Console - Enterprise Only option.
     * 
     * @return {@link UserSearchPage} Instance of UserSearchPage
     */
    public HtmlPage getUsersPage()
    {
        // TODO To be implemented by using UI once JIRA: https://issues.alfresco.com/jira/browse/ALF-18909 is resolved
        String usersPageURL = "/page/console/admin-console/users";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            driver.navigate().to(url);
        }
        return getCurrentPage();
    }

    @FindBy(id = "HEADER_TASKS")
    WebElement tasks;

    /**
     * Selects the user link on main navigation. As this link is created by Java
     * script, a wait is implemented to ensure the link is rendered.
     * 
     * @return {@link MyTasksPage}
     */
    public HtmlPage selectMyTasks()
    {
        tasks.click();
        driver.findElement(By.id("HEADER_MY_TASKS_text")).click();
        return getCurrentPage();
    }

    /**
     * Method to select "Workflows I've Started" under Tasks navigation menu item
     * 
     * @return {@link MyWorkFlowsPage}
     */
    public HtmlPage selectWorkFlowsIHaveStarted()
    {
        try
        {
            tasks.click();
            driver.findElement(By.cssSelector("td#HEADER_MY_WORKFLOWS_text")).click();
            return getCurrentPage();
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
    public HtmlPage getGroupsPage()
    {
        // TODO To be implemented by using UI once JIRA: https://issues.alfresco.com/jira/browse/ALF-18909 is resolved
        String usersPageURL = "/page/console/admin-console/groups";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            driver.navigate().to(url);
            waitForPageLoad(3000);
        }
        return getCurrentPage();
    }

    /**
     * Select admin tools.
     * 
     * @return the html page
     */
    public HtmlPage selectAdminTools()
    {
        driver.findElement(By.cssSelector(REPO_ADMIN_MANAGE_SITE_LINK_SELECTOR)).click();
        return getCurrentPage();
    }

    /**
     * Select manage sites link from home page by Site Admin.
     * 
     * @return the html page
     */

    public HtmlPage selectManageSitesSiteAdmin()
    {
        driver.findElement(By.cssSelector(SITE_ADMIN_MANAGE_SITE_LINK_SELECTOR)).click();
        return getCurrentPage();
    }

    /**
     * Does the current page have a manage-sites link in the header?
     * 
     * @return boolean
     */
    public boolean hasSelectManageSitesSiteAdminLink()
    {
        List<org.openqa.selenium.WebElement> elements = driver.findElements(By.cssSelector(SITE_ADMIN_MANAGE_SITE_LINK_SELECTOR));
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
        driver.findElement(By.cssSelector(selector)).click();
        return getCurrentPage();
    }

    /**
     * Does the current page have an Admin Tools link in the header?
     * 
     * @return boolean
     */
    private boolean hasSelectManageSitesRepoAdmin()
    {
        List<org.openqa.selenium.WebElement> elements = driver.findElements(By.cssSelector(REPO_ADMIN_MANAGE_SITE_LINK_SELECTOR));
        return (elements.size() != 0);
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
            if (hasSelectManageSitesRepoAdmin())
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

    @FindBy(id = "HEADER_CUSTOMIZE_USER_DASHBOARD")
    Link config;

    /**
     * Mimics the action of selecting on the
     * user dashboard configuration icon
     */
    private void selectUserDashboardConfigurationIcon()
    {
        driver.findElement(By.id("HEADER_CUSTOMIZE_USER_DASHBOARD")).click();
    }

    /**
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseUserDashboardPage selectCustomizeUserDashboard()
    {
        selectUserDashboardConfigurationIcon();
        return getCurrentPage().render();
    }

    /**
     * Go to Node-Browser page use direct URL.
     * 
     * @return {@link org.alfresco.po.share.adminconsole.NodeBrowserPage}
     */
    public NodeBrowserPage getNodeBrowserPage()
    {
        String usersPageURL = "/page/console/admin-console/node-browser";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            driver.navigate().to(url);
        }
        return getCurrentPage().render();
    }

    /**
     * Go to Node-Browser page use direct URL.
     * 
     * @return {@link org.alfresco.po.share.adminconsole.CategoryManagerPage}
     */
    public CategoryManagerPage getCategoryManagerPage()
    {
        String usersPageURL = "/page/console/admin-console/category-manager";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            driver.navigate().to(url);
        }
        return getCurrentPage().render();
    }

    /**
     * Check if "CreateSite" element is present in the sites menu.
     * 
     * @return boolean
     */
    public boolean isCreateSitePresent()
    {
        selectSitesDropdown();
        return isDisplayed(CREATE_SITE_LINK);
    }

    /**
     * Check for details of site is favorite or not.
     * 
     * @return boolean
     */
    public boolean isSiteFavourtie()
    {
        try
        {
            if (getCurrentPage() instanceof SiteDashboardPage)
            {
                selectSitesDropdown();
                if (isElementDisplayed(By.cssSelector(SELECT_SITE_AS_FAVOURITE)))
                {
                    return false;
                }
                else if (driver.findElement(FAVOURITE_ID).isDisplayed())
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
     * @return HtmlPage
     */
    public HtmlPage setSiteAsFavourite()
    {
        try
        {
            if (getCurrentPage() instanceof SiteDashboardPage)
            {
                selectSitesDropdown();
                driver.findElement(By.cssSelector(SELECT_SITE_AS_FAVOURITE)).click();
                // remove focus point from the drop down.
                driver.findElement(By.tagName("body")).click();
                return getCurrentPage();
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
     * @return HtmlPage
     */
    public HtmlPage removeFavourite()
    {
        try
        {
            if (getCurrentPage() instanceof SiteDashboardPage)
            {
                selectSitesDropdown();
                driver.findElement(FAVOURITE_ID).click();
                return getCurrentPage();
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No option available to remove site from favourtie.", nse);
            throw new PageException("No option available to remove site from favourtie.", nse);
        }
        throw new UnsupportedOperationException("User has to be in Site DashBoard page.");
    }

    /**
     * Get the names of all recently visited sites.
     * 
     * @return List<String>
     */
    public List<String> getRecentSitesPresent()
    {
        List<String> siteNames = new ArrayList<String>();
        selectSitesDropdown();
        List<WebElement> sites = driver.findElements(By.cssSelector(RECENT_SITES));
        for (WebElement webElement : sites)
        {
            siteNames.add(webElement.getText());
        }
        return siteNames;
    }

    /**
     * Clicks on the most recent site from the recent site list in sites dropdown
     * 
     * @return
     */
    public HtmlPage selectMostRecentSite()
    {
        List<String> recentSites = getRecentSitesPresent();
        findAndWait(By.linkText(recentSites.get(0))).click();
        return getCurrentPage();
    }

    /**
     * Does any sites been selectd as favourite.
     * 
     * @return boolean
     */
    public boolean doesAnyFavouriteSiteExist()
    {
        boolean doesAnyFavouriteSiteExist = true;
        try
        {
            selectFavourties();
            WebElement element = findFirstDisplayedElement(By.cssSelector(FAVOURITE_TEXT));
            String noFav = getValue("no.favourites");
            if (noFav.equals(element.getText()))
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
     * @return List<String>
     */
    public List<String> getFavouriteSites()
    {
        List<String> siteNames = new ArrayList<String>();
        try
        {
            selectFavourties();
            List<WebElement> sites = driver.findElements(By.cssSelector(FAVOURITE_SITES));
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
     * @return HtmlPage
     */
    private HtmlPage selectFavourties()
    {
        // Refresh is needed since sites added in favourites dont reflect.
        driver.navigate().refresh();
        selectSitesDropdown();
        driver.findElement(By.id("HEADER_SITES_MENU_FAVOURITES_text")).click();
        return getCurrentPage();
    }
    
    /**
     * Select site from favourites.
     * 
     * @return HtmlPage
     */
    public HtmlPage selectSiteFromFavourties(String siteName)
    {
        // Refresh is needed since sites added in favourites don't reflect.
        driver.navigate().refresh();
        selectSitesDropdown();
        driver.findElement(By.id("HEADER_SITES_MENU_FAVOURITES_text")).click();
        //select site name
        String siteSelector = "#HEADER_SITES_MENU_FAVOURITES_dropdown a[title = '" + siteName + "']";
        findAndWait(By.cssSelector(siteSelector));
        return getCurrentPage();
    }

    /**
     * Select My Sites from sites drop down.
     * 
     * @return HtmlPage
     */
    public HtmlPage selectMySites()
    {
        selectSitesDropdown();
        driver.findElement(By.id("HEADER_SITES_MENU_MY_SITES_text")).click();
        return getCurrentPage();
    }

    @FindBy(id = "HEADER_SHARED_FILES_text")
    Link sharedFiles;

    /**
     * Mimics the action of selecting shared files link.
     * 
     * @return HtmlPage shared files page object
     */
    public SharedFilesPage selectSharedFilesPage()
    {
        sharedFiles.click();
        return getCurrentPage().render();
    }

    /**
     * Navigates to the faceted search page.
     * 
     * @return {@link FacetedSearchPage} Instance of FacetedSearchPage
     * @deprecated Incorrect use, should not use url to reach the page.
     */
    public HtmlPage getFacetedSearchPage()
    {
        // FIXME should not use url
        String facetedSearchPageURL = "/page/dp/ws/faceted-search";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", facetedSearchPageURL);
            driver.navigate().to(url);
        }
        return getCurrentPage().render();
    }

    /**
     * Navigates to the faceted search config page.
     * 
     * @return {@link FacetedSearchConfigPage} Instance of FacetedSearchConfigPage
     */
    public HtmlPage getFacetedSearchConfigPage()
    {
        // FIXME should not use url
        String facetedSearchConfigPageURL = "/page/dp/ws/faceted-search-config";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", facetedSearchConfigPageURL);
            driver.navigate().to(url);
        }
        return getCurrentPage().render();
    }

    /**
     * Go to Admin Console page (application) use direct URL.
     * 
     * @return {@link org.alfresco.po.share.admin.AdminConsolePage}
     */
    public AdminConsolePage getAdminConsolePage()
    {
        // FIXME should not use url
        String usersPageURL = "/page/console/admin-console/application";
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null)
        {
            String url = currentUrl.replaceFirst("^*/page.*", usersPageURL);
            driver.navigate().to(url);
        }
        return getCurrentPage().render();
    }

    @FindBy(id = "HEADER_MY_FILES")
    Link myFiles;

    /**
     * Mimics the action of selecting My files link.
     * 
     * @return HtmlPage shared files page object
     */
    public MyFilesPage selectMyFilesPage()
    {
        myFiles.click();
        return getCurrentPage().render();
    }

    /**
     * Method to perfrom search using header search bar
     * 
     * @param searchString String
     * @return FacetedSearchPage
     */
    public HtmlPage performSearch(String searchString)
    {
        FacetedSearchHeaderSearchForm facetedSearchHeaderSearchForm = (FacetedSearchHeaderSearchForm) factoryPage
                .instantiatePageElement(driver, FacetedSearchHeaderSearchForm.class);
        facetedSearchHeaderSearchForm.search(searchString);
        return getCurrentPage();
    }
    /**
     * Select manage custom models link as Admin.
     * 
     * @return the html page
     */

    private HtmlPage selectManageCustomModelsRepoAdmin()
    {
        selectAdminTools().render();
        String selector = "ul.toolLink > li > span > a[href=\"custom-model-manager\"]";
        driver.findElement(By.cssSelector(selector)).click();
        return getCurrentPage();
    }

    /**
     * Select manage custom models link from Navigation Bar.
     * 
     * @return the html page
     */
    private HtmlPage selectManageCustomModelsNonAdmin()
    {
        try
        {
            // Select Non Admin Drop down
            WebElement monAdminMenuSelector = driver.findElement(By.cssSelector(NON_ADMIN_TOOLS_LINK_SELECTOR));
            monAdminMenuSelector.click();

            // Select Custom Models Menu
            WebElement menuSelector = driver.findElement(By.cssSelector(MANAGE_CUSTOM_MODELS_LINK_SELECTOR));
            menuSelector.click();

            return factoryPage.getPage(driver);
        }
        catch (NoSuchElementException | TimeoutException te)
        {
            throw new PageOperationException("Unable to select Model Manager Option as Non Admin User", te);
        }
    }

//    /**
//     * Select manage Custom Models link as Network Admin for cloud.
//     * 
//     * @return the html page
//     */
//
//    private HtmlPage selectManageCustomModelsNetworkAdmin()
//    {
//        ShareUtil.validateAlfrescoVersion(alfrescoVersion, RequiredAlfrescoVersion.CLOUD_ONLY);
//        throw new UnsupportedOperationException("Operation not supported for MyAlfresco");
//    }

    /**
     * Does the current page have a Admin-tools link in the header?
     * 
     * @return boolean
     */
    private boolean hasAdminToolsLink()
    {
        List<WebElement> elements = driver.findElements(By.cssSelector(ADMIN_TOOLS_LINK_SELECTOR));
        return !elements.isEmpty();
    }

    /**
     * Does the current page have a manage-custom-models link in the header?
     * 
     * @return boolean
     */
    public boolean hasManageModelsLink()
    {
        List<WebElement> elements = driver.findElements(By.cssSelector(MANAGE_CUSTOM_MODELS_LINK_SELECTOR));
        return !elements.isEmpty();
    }

    /**
     * Select Manage Custom Models Page: calls appropriate method for AlfrescoVersion and user type
     * 
     * @return the HtmlPage
     */
    public HtmlPage selectManageCustomModelsPage()
    {
        String msg = "Unable to select appropriate menu option for manage custom models";

        try
        {
        	//Check if modal is already open
        	WebElement cancelBtn = driver.findElement(By.cssSelector(".footer .cancellationButton .dijitButtonNode"));
        	if(cancelBtn.isDisplayed())
        	{
        		cancelBtn.click();
        	}
        }
        catch(Exception e)
        {
        	//ignore as might not meet the condition
        }
        try
        {

        	if (hasAdminToolsLink())
            {
                return selectManageCustomModelsRepoAdmin();
            }
            else if (hasManageModelsLink())
            {
                return selectManageCustomModelsNonAdmin();
            }
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException(msg, e);
        }
        throw new UnsupportedOperationException(msg);
    }

}


