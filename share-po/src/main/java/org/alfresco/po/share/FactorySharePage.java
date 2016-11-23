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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.Page;
import org.alfresco.po.PageElement;
import org.alfresco.po.WebDriverAwareDecorator;
import org.alfresco.po.alfresco.AlfrescoTransformationServerHistoryPage;
import org.alfresco.po.alfresco.AlfrescoTransformationServerStatusPage;
import org.alfresco.po.alfresco.RepositoryAdminConsolePage;
import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.po.alfresco.WebScriptsPage;
import org.alfresco.po.alfresco.webdav.WebDavPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.adminconsole.ReplicationJobPage;
import org.alfresco.po.share.adminconsole.TagManagerPage;
import org.alfresco.po.share.bulkimport.BulkImportPage;
import org.alfresco.po.share.bulkimport.InPlaceBulkImportPage;
import org.alfresco.po.share.bulkimport.StatusBulkImportPage;
import org.alfresco.po.share.cmm.admin.ApplyDefaultLayoutPopUp;
import org.alfresco.po.share.cmm.admin.ClearFormLayoutPopUp;
import org.alfresco.po.share.cmm.admin.ConfirmDeletePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.EditCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.EditModelPopUp;
import org.alfresco.po.share.cmm.admin.EditPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.FormEditorPage;
import org.alfresco.po.share.cmm.admin.ImportModelPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.dashlet.ConfigureSiteNoticeDialogBoxPage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.repository.ModelsPage;
import org.alfresco.po.share.search.AdvanceSearchCRMPage;
import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.search.AdvanceSearchFolderPage;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.AllSitesResultsPage;
import org.alfresco.po.share.search.CopyAndMoveContentFromSearchPage;
import org.alfresco.po.share.search.CopyOrMoveFailureNotificationPopUp;
import org.alfresco.po.share.search.CreateNewFilterPopUpPage;
import org.alfresco.po.share.search.FacetedSearchConfigPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.RepositoryResultsPage;
import org.alfresco.po.share.search.SearchConfirmDeletePage;
import org.alfresco.po.share.search.SiteResultsPage;
import org.alfresco.po.share.site.AddGroupsPage;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.ConfirmRequestToJoinPopUp;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.EditSitePage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.PendingInvitesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteDashboardErrorPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPreRender;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.ChangeTypePage;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CreateHtmlContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.MyFilesPage;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.po.share.site.document.ViewPropertiesPage;
import org.alfresco.po.share.site.document.ViewPublicLinkPage;
import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.systemsummary.FileServersPage;
import org.alfresco.po.share.systemsummary.ModelAndMessagesConsole;
import org.alfresco.po.share.systemsummary.RepositoryServerClusteringPage;
import org.alfresco.po.share.systemsummary.SystemSummaryPage;
import org.alfresco.po.share.systemsummary.TenantConsole;
import org.alfresco.po.share.systemsummary.TransformationServicesPage;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryManagementPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.task.TaskDetailsPage;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.EditProfilePage;
import org.alfresco.po.share.user.FollowersPage;
import org.alfresco.po.share.user.FollowingPage;
import org.alfresco.po.share.user.LanguageSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.NotificationPage;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.user.UserContentPage;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
@SuppressWarnings("deprecation")
@Component
public class FactorySharePage implements FactoryPage 
{
    private static Log logger = LogFactory.getLog(FactorySharePage.class);
    @Autowired private ApplicationContext ac;
    @Autowired FactoryShareDashlet dashletFactory;
    private long defaultWaitTime;
    private long maxPageLoadingTime;
    private String alfrescoUrl;
    private static final By COPY_MOVE_DIALOGUE_SELECTOR = By.cssSelector(".dijitDialogTitleBar");
    private static final String CREATE_PAGE_ERROR_MSG = "Unable to instantiate the page";
    protected static final String FAILURE_PROMPT = "div[id='prompt']";
    protected static final String NODE_REF_IDENTIFIER = "?nodeRef";
    protected static final String QUICKVIEW_IDENTIFIER = "/share/s/";
    protected static final String SHARE_DIALOGUE = "div.hd, .dijitDialogTitleBar";
    protected static ConcurrentHashMap<String, Class<? extends Page>> pages;
    protected static final By SHARE_DIALOGUE_HEADER = By.cssSelector("div.hd");
    private static Properties poProperties;
    public static final String DOCUMENTLIBRARY = "documentlibrary";
    public static final String NODE_REFRESH_META_DATA_IDENTIFIER = "?refreshMetadata";
    protected static final String SHARE_DIALOGUE_AIKAU = "div.dijitDialogTitleBar";
    private static final String CMM_URL = "custom-model-manager";
    private static final String TPG_HASH = "view=types_property_groups";
    private static final String PROPERTIES_HASH = "view=properties";
    private static final String FORM_EDITOR_HASH = "view=editor";
    
    private static final By NO_DASHBOARD = By.cssSelector(".alf-error-nav");
    static
    {
        pages = new ConcurrentHashMap<String, Class<? extends Page>>();
        pages.put("dashboard", DashBoardPage.class);
        pages.put("site-dashboard", SiteDashboardPage.class);
        pages.put("site-dashboard-error", SiteDashboardErrorPage.class);
        pages.put("document-details", DocumentDetailsPage.class);
        pages.put("documentlibrary", DocumentLibraryPage.class);
        pages.put("folder-details", FolderDetailsPage.class);
        pages.put("my-tasks", MyTasksPage.class);
        pages.put("my-workflows", MyWorkFlowsPage.class);
        pages.put("workflow-details", WorkFlowDetailsPage.class);
        pages.put("people-finder", PeopleFinderPage.class);
        pages.put("profile", MyProfilePage.class);
        pages.put("user-trashcan", TrashCanPage.class);
        pages.put("site-finder", SiteFinderPage.class);
        pages.put("wiki-page", WikiPage.class);
        pages.put("wiki", WikiPageList.class);
        pages.put("change-password", ChangePasswordPage.class);
        pages.put("repository", RepositoryPage.class);
        pages.put("manage-permissions", ManagePermissionsPage.class);
        pages.put("plain", CreatePlainTextContentPage.class);
        pages.put("xml", CreatePlainTextContentPage.class);
        pages.put("html", CreateHtmlContentPage.class);
        pages.put("inline-edit", InlineEditPage.class);
        pages.put("edit-metadata", EditDocumentPropertiesPage.class);
        pages.put("site-members", SiteMembersPage.class);
        pages.put("invite", InviteMembersPage.class);
        pages.put("add-users", AddUsersToSitePage.class);
        pages.put("users-create", NewUserPage.class);
        pages.put("users-view", UserProfilePage.class);
        pages.put("users-update", EditUserPage.class);
        pages.put("users", UserSearchPage.class);
        pages.put("customise-site", CustomizeSitePage.class);
        pages.put("customise-site-dashboard", CustomiseSiteDashboardPage.class);
        pages.put("customise-user-dashboard", CustomiseUserDashboardPage.class);
        pages.put("advsearch", AdvanceSearchPage.class);
        pages.put("advcontent-search", AdvanceSearchContentPage.class);
        pages.put("advfolder-search", AdvanceSearchFolderPage.class);
        pages.put("advCRM-search", AdvanceSearchCRMPage.class);
        pages.put("siteResultsPage", SiteResultsPage.class);
        pages.put("repositoryResultsPage", RepositoryResultsPage.class);
        pages.put("allSitesResultsPage", AllSitesResultsPage.class);
        pages.put("folder-rules", FolderRulesPreRender.class);
        pages.put("rule-edit", CreateRulePage.class);
        pages.put("task-edit", EditTaskPage.class);
        pages.put("task-details", TaskDetailsPage.class);
        pages.put("change-locale", LanguageSettingsPage.class);
        pages.put("groups", GroupsPage.class);
        pages.put("site-groups", SiteGroupsPage.class);
        pages.put("add-groups", AddGroupsPage.class);
        pages.put("discussions-createtopic", CreateNewTopicPage.class);
        pages.put("discussions-topicview", TopicDetailsPage.class);
        pages.put("discussions-topiclist", DiscussionsPage.class);
        pages.put("search", SiteResultsPage.class);
        pages.put("start-workflow", StartWorkFlowPage.class);
        pages.put("node-browser", NodeBrowserPage.class);
        pages.put("category-manager", CategoryManagerPage.class);
        pages.put("admin-console", AdminConsolePage.class);
        pages.put("manage-sites", ManageSitesPage.class);
        pages.put("link.htm", InsertOrEditLinkPage.class);
        pages.put("page", LoginPage.class); //temporary solution
        pages.put("sharedfiles", SharedFilesPage.class);
        pages.put("myfiles", MyFilesPage.class);
        pages.put("admin-systemsummary", SystemSummaryPage.class);
        pages.put("admin-clustering", RepositoryServerClusteringPage.class);
        pages.put("admin-directorymanagement", DirectoryManagementPage.class);
        pages.put("admin-tenantconsole", TenantConsole.class);
        pages.put("admin-repoconsole", ModelAndMessagesConsole.class);
        pages.put("admin-fileservers", FileServersPage.class);
        pages.put("admin-transformations", TransformationServicesPage.class);
        pages.put("calendar", CalendarPage.class);
        pages.put("links-view", LinksDetailsPage.class);
        pages.put("links", LinksPage.class);
        pages.put("pending-invites", PendingInvitesPage.class);
        pages.put("edit-profile", EditProfilePage.class);
        pages.put("user-notifications", NotificationPage.class);
        pages.put("user-sites", UserSitesPage.class);
        pages.put("tag-management", TagManagerPage.class);
        pages.put("faceted-search", FacetedSearchPage.class);
        pages.put("faceted-search-config", FacetedSearchConfigPage.class);
        pages.put("user-sites", UserSitesPage.class);
        pages.put("bulkfsimport", BulkImportPage.class);
        pages.put("status", StatusBulkImportPage.class);
        pages.put("inplace", InPlaceBulkImportPage.class);
        pages.put("index", WebScriptsPage.class);
        pages.put("webdav", WebDavPage.class);
        pages.put("admin-tenantconsole", TenantAdminConsolePage.class);
        pages.put("admin-repoconsole", RepositoryAdminConsolePage.class);
        pages.put("user-content", UserContentPage.class);
        pages.put("following", FollowingPage.class);
        pages.put("followers", FollowersPage.class);
        pages.put("replication-jobs", ReplicationJobPage.class);
        pages.put("manage-users", AccountSettingsPage.class);
        pages.put("transformations", AlfrescoTransformationServerHistoryPage.class);
        pages.put("transformation-server", AlfrescoTransformationServerStatusPage.class);
        pages.put("models", ModelsPage.class);
        pages.put("ModelManager", ModelManagerPage.class);
        pages.put("ManageTypesAndAspects", ManageTypesAndAspectsPage.class);
        pages.put("ManageProperties", ManagePropertiesPage.class);
        pages.put("FormEditor", FormEditorPage.class);
        pages.put("quick-view", ViewPublicLinkPage.class);
    }

    public HtmlPage getPage(WebDriver driver)
    {
        return resolvePage(driver);
    }

    public Dashlet getDashlet(WebDriver driver, String name)
    {
        return dashletFactory.getPage(driver, name);
    }

    /**
     * Creates the appropriate page object based on the current page the {@link WebDriver} is on.
     *
     * @param driver WebDriver Alfresco unmanned web browser client
     * @return SharePage the page object response
     * @throws PageException
     */
    public HtmlPage resolvePage(final WebDriver driver) throws PageException
    {
        // Determine if user is logged in if not return login page
        // if (driver.getTitle().toLowerCase().contains(driver.getLanguageValue("login.title")))
        if (driver.getTitle().toLowerCase().contains("login"))
        {
            return instantiatePage(driver,LoginPage.class);
        }
        else
        {
            // Share Error PopUp
            try
            {
                WebElement errorPrompt = driver.findElement(By.cssSelector(FAILURE_PROMPT));
                if (errorPrompt.isDisplayed())
                {
                    return instantiatePage(driver,SharePopup.class);
                }
            }
            catch (NoSuchElementException nse)
            {
            }

            // Check for Share Dialogue
            try
            {
                WebElement shareDialogue = driver.findElement(By.cssSelector(SHARE_DIALOGUE));
                if (shareDialogue.isDisplayed() || !driver.findElements(COPY_MOVE_DIALOGUE_SELECTOR).isEmpty())
                {
                    HtmlPage response = resolveShareDialoguePage(driver);
                    if(response != null)
                    {
                        return response;
                    }
                }
            }
            catch(NoSuchElementException n){}

            // Determine what page we're on based on url
            return getPage(driver.getCurrentUrl(), driver);
        }
    }

    /**
     * Factory method to produce a page that defers all work until render time.
     *
     * @param driver browser driver
     * @return a page that is meaningless until {@link SharePage#render()} is called
     */
    public HtmlPage getUnknownPage(final WebDriver driver)
    {
        return instantiatePage(driver,UnknownSharePage.class);
    }

    @SuppressWarnings("unchecked")
    /**
     * Instantiates the page object matching the argument.
     * @param <T>
     *
     * @param z            {@link WebDriver}
     * @param pageClassToProxy expected Page object
     * @return {@link SharePage} page response
     * @throws Exception 
     */
    public <T> T instantiatePage(WebDriver driver,Class<T> pageClassToProxy) throws PageException
    {
        if (driver == null)
        {
            throw new IllegalArgumentException("WebDriver is required");
        }
        if (pageClassToProxy == null)
        {
            throw new IllegalArgumentException("Page object is required for url: " + driver.getCurrentUrl());
        }
        try
        {
          //We first create the page object.
            Page page = (Page) pageClassToProxy.newInstance();
            page.setWebDriver(driver);
            //Wrap it with a decorator to provide htmlelements with webdriver power.
            WebDriverAwareDecorator decorator = new WebDriverAwareDecorator(driver);
            //Init HtmlElements with webdriver power.
            initElements(decorator, page);
            //Wire spring into page.
            ac.getAutowireCapableBeanFactory().autowireBean(page);
            return (T)page;
        }
        catch (Exception e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
    }
    /**
     * Creates elements nested in page object.
     * @param decorator 
     * @param page
     */
    private void initElements(FieldDecorator decorator, Object page)
    {
        Class<?> proxyIn = page.getClass();
        while (proxyIn != Object.class) 
        {
          proxyFields(decorator, page, proxyIn);
          proxyIn = proxyIn.getSuperclass();
        }
    }
    /**
     * Applies proxy and injects spring awareness to fields.
     * @param decorator
     * @param page
     * @param proxyIn
     */
    private void proxyFields(FieldDecorator decorator, Object page, Class<?> proxyIn)
    {
        Field[] fields = proxyIn.getDeclaredFields();
        for (Field field : fields)
        {
            Object value = decorator.decorate(page.getClass().getClassLoader(), field);
            if (value != null)
            {
                try 
                {
                    field.setAccessible(true);
                	if (value instanceof PageElement)
                	{
                        //Wire spring 
                        ac.getAutowireCapableBeanFactory().autowireBean(value);
                        ((PageElement) value).setDefaultWaitTime(defaultWaitTime);
                        ((PageElement) value).setMaxPageLoadingTime(maxPageLoadingTime);
                	}
                    field.set(page, value);
                }
                catch (IllegalAccessException e) 
                {
           	      throw new RuntimeException(e);
                }
            }
        }
    }
    
    
    public PageElement instantiatePageElement(WebDriver driver, Class<?> pageClassToProxy)
    {
        if (driver == null)
        {
            throw new IllegalArgumentException("WebDriver is required");
        }
        if (pageClassToProxy == null)
        {
            throw new IllegalArgumentException("Page object is required for url: " + driver.getCurrentUrl());
        }
        try
        {
            WebDriverAwareDecorator decorator = new WebDriverAwareDecorator(driver);
            PageElement pageElement = (PageElement) pageClassToProxy.newInstance();
            pageElement.setWebDriver(driver);
            PageFactory.initElements(decorator, pageElement);
            //Wire spring into page elements.
            ac.getAutowireCapableBeanFactory().autowireBean(pageElement);
            return pageElement;

        }
        catch (InstantiationException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (IllegalAccessException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
    }
    /**
     * Resolves the required page based on the URL containing a keyword
     * that identify's the page the driver is currently on. Once a the name
     * is extracted it is used to get the class from the map which is
     * then instantiated.
     *
     * @param driver WebDriver browser client
     * @return SharePage page object
     */
    public HtmlPage getPage(final String url, WebDriver driver)
    {
        String pageName = resolvePage(url);
        if (logger.isTraceEnabled())
        {
            logger.trace(url + " : page name: " + pageName);
        }
        if (pages.get(pageName) == null)
        {
            return instantiatePage(driver, UnknownSharePage.class);
        }
        else if (pageName == "site-dashboard")
        {
        	if(checkIfError(driver))
        	{
        		pageName = "site-dashboard-error";
        	}
        }
        return instantiatePage(driver, pages.get(pageName));
    }

    /**
     * Extracts the name from any url noise.
     *
     * @param pageName String page name
     * @return the page name
     */
    private static String extractName(String pageName)
    {
        String regex = "([?&#])";
        String vals[] = pageName.split(regex);
        return vals[0];
    }

    /**
     * Extracts the String value from the last occurrence of slash in the url.
     *
     * @param url String url.
     * @return String page title
     */
    protected static String resolvePage(String url)
    {
        if (url == null || url.isEmpty())
        {
            throw new UnsupportedOperationException("Empty url is not allowed");
        }

        if (url.endsWith("dashboard"))
        {
            if (url.endsWith("customise-site-dashboard"))
            {
                return "customise-site-dashboard";
            }
            if (url.endsWith("customise-user-dashboard"))
            {
                return "customise-user-dashboard";
            }
            if (url.contains("/page/site/"))
            {
                return "site-dashboard";
            }
            return "dashboard";
        }

        if (url.endsWith("create"))
        {
            return "users-create";
        }
        
        if (url.contains(QUICKVIEW_IDENTIFIER))
        {
        	return "quick-view";
        }

        if (url.contains(NODE_REF_IDENTIFIER))
        {
            int index = url.indexOf(NODE_REF_IDENTIFIER);
            url = url.subSequence(0, index).toString();
        }

        if (url.contains(NODE_REFRESH_META_DATA_IDENTIFIER))
        {
            int index = url.indexOf(NODE_REFRESH_META_DATA_IDENTIFIER);
            url = url.subSequence(0, index).toString();
        }

        if (url.contains("/repository"))
        {
            if (url.contains("#filter=path%7C%2FData%2520Dictionary%2FModels"))
            {
                return "models";
            }
            return "repository";
        }
        // The admin console has an unusual url which we handle here
        // 'application' by itself would be inappropriate
        if (url.contains("/admin-console/application"))
        {
            return "admin-console";
        }

        // Get the last element of url
        StringTokenizer st = new StringTokenizer(url, "/");
        String val = "";
        while (st.hasMoreTokens())
        {
            if (st.hasMoreTokens())
            {
                val = st.nextToken();
            }
        }

        // Check if its advance search folder or content
        if (val.contains("advsearch"))
        {
            if (val.contains("%3afolder"))
            {
                return "advfolder-search";
            }
            else if (val.contains("prop_crm"))
            {
                return "advCRM-search";
            }
            else if (val.contains("Acontent"))
            {
                return "advcontent-search";
            }
            return "advsearch";
        }
        if (val.startsWith("search?"))
        {
            if (url.contains("/site/"))
            {
                return "siteResultsPage";
            }
            else if (val.endsWith("&a=true&r=true"))
            {
                return "repositoryResultsPage";
            }
            else
            {
                return "allSitesResultsPage";
            }
        }
        
        // Remove any clutter.
        if (val.contains("?") || val.contains("#"))
        {
            val = extractName(val);
        }
        if(val.contains("edit") && url.contains("docs.google.com"))
        {
            val = "googledocsEditor";
        }
        
        if (url.contains(TPG_HASH))
        {
        	return "ManageTypesAndAspects";
        }
        else if (url.contains(PROPERTIES_HASH))
        {
        	return "ManageProperties";
        }
        else if (url.contains(FORM_EDITOR_HASH))
        {
        	return "FormEditor";
        }
        else if (url.contains(CMM_URL))
        {
        	return "ModelManager";
        }
       
        return val;
    }

    /**
     * Helper method to return right Page for Share Dialogue displayed
     *
     * @return HtmlPage
     */
    private HtmlPage resolveShareDialoguePage(WebDriver driver)
    {
        HtmlPage sharePage = null;
        try
        {
            List<WebElement> dialogues = driver.findElements(By.cssSelector(SHARE_DIALOGUE));
            WebElement dialogue = null;
            for(WebElement e : dialogues)
            {
                if(e.isDisplayed())
                {
                    dialogue = e;
                    break;
                }
            }
            WebElement copyMoveDialogue = null;
            try
            {
                copyMoveDialogue = driver.findElements(COPY_MOVE_DIALOGUE_SELECTOR).get(0);
            }
            catch (Exception e)
            {
            }
            if (dialogue != null && dialogue.isDisplayed())
            {
                String dialogueID = dialogue.getAttribute("id");

                if (dialogueID.contains("createSite"))
                {
                    sharePage = instantiatePage(driver, CreateSitePage.class);
                }
                else if (dialogueID.contains("createFolder"))
                {
                    sharePage = instantiatePage(driver, NewFolderPage.class);
                }
                else if (dialogueID.contains("upload"))
                {
                    sharePage = instantiatePage(driver, UploadFilePage.class);
                }
                else if (dialogueID.contains("taggable-cntrl-picker"))
                {
                    sharePage = instantiatePage(driver, TagPage.class);
                }
                else if (dialogueID.contains("editDetails") || dialogueID.contains("edit-metadata"))
                {
                    sharePage = instantiatePage(driver, EditDocumentPropertiesPage.class);
                }

                else if (dialogueID.contains("copyMoveTo"))
                {
                    sharePage = instantiatePage(driver,CopyOrMoveContentPage.class);
                }

                else if (dialogueID.contains("historicPropertiesViewer"))
                {
                    sharePage = instantiatePage(driver, ViewPropertiesPage.class);
                }

                // The below dialogeId will be changed once this ACE-1047 issue is fixed.
                else if (dialogueID.contains("configDialog-configDialog_h"))
                {
                    sharePage = instantiatePage(driver,ConfigureSiteNoticeDialogBoxPage.class);
                }
                else if(copyMoveDialogue != null && (copyMoveDialogue.getText().startsWith("Copy") || copyMoveDialogue.getText().startsWith("Move")))
                {
                    sharePage = instantiatePage(driver,CopyAndMoveContentFromSearchPage.class);
                }
                else if(dialogueID.contains("Create New Filter"))
                {
                        sharePage = instantiatePage(driver,CreateNewFilterPopUpPage.class);
                }
                else if(dialogueID.contains("cloud-folder-title"))
                {
                    sharePage = instantiatePage(driver,DestinationAndAssigneePage.class);
                }
                else if(dialogueID.contains("changeType"))
                {
                    sharePage = instantiatePage(driver,ChangeTypePage.class);
                }
                else if(dialogueID.contains("default-aspects"))
                {
                    sharePage = instantiatePage(driver,SelectAspectsPage.class);
                }
                String dialogueText = dialogue.getText();

                if ("Create Site".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateSitePage.class);
                }
                else if ("Edit Site Details".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditSitePage.class);
                }
                else if ("Create Model".equals(dialogueText))
                {
                	sharePage = instantiatePage(driver, CreateNewModelPopUp.class);
                }
                else if ("Create Custom Type".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateNewCustomTypePopUp.class);
                }
                else if ("Create Aspect".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateNewPropertyGroupPopUp.class);
                }
                else if ("Create Property".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateNewPropertyPopUp.class);
                }
                else if ("Edit Model".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditModelPopUp.class);
                }
                else if ("Edit Custom Type".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditCustomTypePopUp.class);
                }
                else if ("Edit Aspect".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditPropertyGroupPopUp.class);
                }
                else if ("Edit Property".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditPropertyPopUp.class);
                }
                else if ("Apply the default layout".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ApplyDefaultLayoutPopUp.class);
                }
                else if ("Delete Model".equals(dialogueText) || "Delete Custom Type".equals(dialogueText)
                        || "Delete Aspect".equals(dialogueText) || "Delete Property".equals(dialogueText) || "Delete Site".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ConfirmDeletePopUp.class);
                }
                else if ("Clear the Layout Designer".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ClearFormLayoutPopUp.class);
                }
                else if ("Import Model".equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ImportModelPopUp.class);
                }
                else if ("Confirm Deletion".equals(dialogueText))
                {
                	sharePage = instantiatePage(driver, SearchConfirmDeletePage.class);
                }
                else if ("Copy Failed".equals(dialogueText) || "Move Failed".equals(dialogueText))
                {
                	sharePage = instantiatePage(driver, CopyOrMoveFailureNotificationPopUp.class);
                }
                else if ("Request Sent".equals(dialogueText))
                {
                	sharePage = instantiatePage(driver, ConfirmRequestToJoinPopUp.class);
                }
            }
        }
        catch (NoSuchElementException nse){}

        return sharePage;
    }
    
	private boolean checkIfError(WebDriver driver)
    {    	
    	try
    	{
    		WebElement dashError = driver.findElement(NO_DASHBOARD);
    		return (dashError != null);
    	}
    	catch(NoSuchElementException nse)
    	{
    		return false;
    	}
    }
    
    public String getValue(String key)
    {
        if(key == null || key.isEmpty())
        {
            throw new IllegalArgumentException("Key is required to find value");
        }
        
        return poProperties.getProperty(key);
    }
    
    public static void setPoProperties(Properties poProperties)
    {
        FactorySharePage.poProperties = poProperties;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ac = applicationContext;
    }

	public void setDefaultWaitTime(long defaultWaitTime)
	{
		this.defaultWaitTime = defaultWaitTime;
	}

	public void setMaxPageLoadingTime(long maxPageLoadingTime) 
	{
		this.maxPageLoadingTime = maxPageLoadingTime;
	}


	public void setAlfrescoUrl(String alfrescoUrl) 
	{
		this.alfrescoUrl = alfrescoUrl;
	}
    
}
