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
package org.alfresco.po.share;

import org.alfresco.po.alfresco.AlfrescoTransformationServerHistoryPage;
import org.alfresco.po.alfresco.AlfrescoTransformationServerStatusPage;
import org.alfresco.po.alfresco.RepositoryAdminConsolePage;
import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.po.alfresco.WebScriptsPage;
import org.alfresco.po.alfresco.webdav.WebDavPage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.adminconsole.CategoryManagerPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.adminconsole.TagManagerPage;
import org.alfresco.po.share.adminconsole.replicationjobs.NewReplicationJobPage;
import org.alfresco.po.share.adminconsole.replicationjobs.ReplicationJobsPage;
import org.alfresco.po.share.bulkimport.BulkImportPage;
import org.alfresco.po.share.bulkimport.InPlaceBulkImportPage;
import org.alfresco.po.share.bulkimport.StatusBulkImportPage;
import org.alfresco.po.share.dashlet.ConfigureSiteNoticeDialogBoxPage;
import org.alfresco.po.share.dashlet.Dashlet;
import org.alfresco.po.share.dashlet.InsertOrEditLinkPage;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.search.AdvanceSearchCRMPage;
import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.search.AdvanceSearchFolderPage;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.AllSitesResultsPage;
import org.alfresco.po.share.search.CopyAndMoveContentFromSearchPage;
import org.alfresco.po.share.search.CreateNewFilterPopUpPage;
import org.alfresco.po.share.search.FacetedSearchConfigPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.RepositoryResultsPage;
import org.alfresco.po.share.search.SiteResultsPage;
import org.alfresco.po.share.site.AddGroupsPage;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.PendingInvitesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.contentrule.FolderRulesPreRender;
import org.alfresco.po.share.site.contentrule.createrules.CreateRulePage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CreateHtmlContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.MyFilesPage;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.po.share.site.document.TagPage;
import org.alfresco.po.share.site.document.ViewPropertiesPage;
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
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.EditProfilePage;
import org.alfresco.po.share.user.LanguageSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.NotificationPage;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.user.UserContentPage;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.po.share.user.FollowingPage;
import org.alfresco.po.share.user.FollowersPage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.PageFactory;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class FactorySharePage implements PageFactory
{
    private static final By COPY_MOVE_DIALOGUE_SELECTOR = By.cssSelector(".dijitDialogTitleBar");
	private static Log logger = LogFactory.getLog(FactorySharePage.class);
    private static final String CREATE_PAGE_ERROR_MSG = "Unabel to instantiate the page";
    protected static final String NODE_REF_IDENTIFIER = "?nodeRef";
    public static final String DOCUMENTLIBRARY = "documentlibrary";
    public static final String NODE_REFRESH_META_DATA_IDENTIFIER = "?refreshMetadata";
    protected static final String FAILURE_PROMPT = "div[id='prompt']";
    protected static final String SHARE_DIALOGUE = "div.hd, .dijitDialogTitleBar";
    protected static ConcurrentHashMap<String, Class<? extends SharePage>> pages;
    protected static ConcurrentHashMap<String, Class<? extends Dashlet>> dashletPages;
    protected static final By SHARE_DIALOGUE_HEADER = By.cssSelector("div.hd");
    private static final String cloudSignInDialogueHeader = "Sign in to Alfresco in the cloud";

    static
    {
        dashletPages = new ConcurrentHashMap<String, Class<? extends Dashlet>>();

        pages = new ConcurrentHashMap<String, Class<? extends SharePage>>();
        pages.put("dashboard", DashBoardPage.class);
        pages.put("site-dashboard", SiteDashboardPage.class);
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
        pages.put("googledocsEditor", EditInGoogleDocsPage.class);
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
        pages.put("user-cloud-auth", CloudSyncPage.class);
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
        pages.put("blog-postlist", BlogPage.class);
        pages.put("blog-postview", PostViewPage.class);
        pages.put("data-lists", DataListPage.class);
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
        pages.put("replication-jobs", ReplicationJobsPage.class);
        pages.put("replication-job", NewReplicationJobPage.class);
        pages.put("manage-users", AccountSettingsPage.class);
        pages.put("transformations", AlfrescoTransformationServerHistoryPage.class);
        pages.put("transformation-server", AlfrescoTransformationServerStatusPage.class);
 
    }

    public HtmlPage getPage(WebDrone drone)
    {
        return resolvePage(drone);
    }
 
    public Dashlet getDashletPage(WebDrone drone, String name)
    {
        return resolveDashletPage(drone, name);
    }
    
    public static Dashlet resolveDashletPage(final WebDrone drone, final String name)
    {
         
        return instantiateDashletPage(drone, dashletPages.get(name));
   
    }
    
    
    

    /**
     * Creates the appropriate page object based on the current page the {@link WebDrone} is on.
     *
     * @param drone WebDrone Alfresco unmanned web browser client
     * @return SharePage the page object response
     * @throws PageException
     */
    public static HtmlPage resolvePage(final WebDrone drone) throws PageException
    {
        // Determine if user is logged in if not return login page
        // if (drone.getTitle().toLowerCase().contains(drone.getLanguageValue("login.title")))
        if (drone.getTitle().toLowerCase().contains("login"))
        {
            return new LoginPage(drone);
        }
        else
        {
            // Share Error PopUp
            try
            {
                WebElement errorPrompt = drone.find(By.cssSelector(FAILURE_PROMPT));
                if (errorPrompt.isDisplayed())
                {
                    return new SharePopup(drone);
                }
            }
            catch (NoSuchElementException nse)
            {
            }

            // Check for Share Dialogue
            try
            {
                WebElement shareDialogue = drone.findFirstDisplayedElement(By.cssSelector(SHARE_DIALOGUE));
                if (shareDialogue.isDisplayed() || drone.findFirstDisplayedElement(COPY_MOVE_DIALOGUE_SELECTOR).isDisplayed())
                {
                    return resolveShareDialoguePage(drone);
                }
            }
            catch (NoSuchElementException nse)
            {

            }
            catch (StaleElementReferenceException ste)
            {

            }
            // Determine what page we're on based on url
            return getPage(drone.getCurrentUrl(), drone);
        }
    }

    /**
     * Factory method to produce a page that defers all work until render time.
     *
     * @param drone browser driver
     * @return a page that is meaningless until {@link SharePage#render()} is called
     */
    public static SharePage getUnknownPage(final WebDrone drone)
    {
        return new UnknownSharePage(drone);
    }

    /**
     * Instantiates the page object matching the argument.
     *
     * @param drone            {@link WebDrone}
     * @param pageClassToProxy expected Page object
     * @return {@link SharePage} page response
     */
    
    protected static <T extends HtmlPage> T instantiatePage(WebDrone drone, Class<T> pageClassToProxy)
    {
        if (drone == null)
        {
            throw new IllegalArgumentException("WebDrone is required");
        }
        if (pageClassToProxy == null)
        {
            throw new IllegalArgumentException("Page object is required for url: " + drone.getCurrentUrl());
        }
        try
        {
            try
            {
                Constructor<T> constructor = pageClassToProxy.getConstructor(WebDrone.class);
                return constructor.newInstance(drone);
            }
            catch (NoSuchMethodException e)
            {
                return pageClassToProxy.newInstance();
            }
        }
        catch (InstantiationException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (IllegalAccessException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (InvocationTargetException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
    }

    
    
    protected static <T extends Dashlet> T instantiateDashletPage(WebDrone drone, Class<T> pageClassToProxy)
    {
        if (drone == null)
        {
            throw new IllegalArgumentException("WebDrone is required");
        }
        if (pageClassToProxy == null)
        {
            throw new IllegalArgumentException("Page object is required for url: " + drone.getCurrentUrl());
        }
        try
        {
            try
            {
                Constructor<T> constructor = pageClassToProxy.getConstructor(WebDrone.class);
                return constructor.newInstance(drone);
            }
            catch (NoSuchMethodException e)
            {
                return pageClassToProxy.newInstance();
            }
        }
        catch (InstantiationException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (IllegalAccessException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (InvocationTargetException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
    }
    
    
    /**
     * Resolves the required page based on the URL containing a keyword
     * that identify's the page the drone is currently on. Once a the name
     * is extracted it is used to get the class from the map which is
     * then instantiated.
     *
     * @param drone WebDriver browser client
     * @return SharePage page object
     */
    public static SharePage getPage(final String url, WebDrone drone)
    {
        String pageName = resolvePage(url);
        if (logger.isTraceEnabled())
        {
            logger.trace(url + " : page name: " + pageName);
        }
        if (pages.get(pageName) == null)
        {
            return instantiatePage(drone, UnknownSharePage.class);
        }            
        return instantiatePage(drone, pages.get(pageName));
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
        return val;
    }

    /**
     * Helper method to return right Page for Share Dialogue displayed
     *
     * @return HtmlPage
     */
    private static HtmlPage resolveShareDialoguePage(WebDrone drone)
    {
        SharePage sharePage = null;
        try
        {
            WebElement dialogue = drone.findFirstDisplayedElement(By.cssSelector(SHARE_DIALOGUE));
            WebElement copyMoveDialogue = null;
            try
            {
                copyMoveDialogue = drone.findFirstDisplayedElement(COPY_MOVE_DIALOGUE_SELECTOR);
            }
            catch (NoSuchElementException e)
            {
            }
            if (dialogue != null && dialogue.isDisplayed())
            {
                String dialogueID = dialogue.getAttribute("id");
                if (dialogueID.contains("createSite"))
                {
                    sharePage = new CreateSitePage(drone);

                }
                else if (dialogueID.contains("createFolder"))
                {
                    sharePage = new NewFolderPage(drone);
                }
                else if (dialogueID.contains("upload"))
                {
                    sharePage = new UploadFilePage(drone);
                }
                else if (dialogueID.contains("taggable-cntrl-picker"))
                {
                    sharePage = new TagPage(drone);
                }
                else if (dialogueID.contains("editDetails"))
                {
                    sharePage = new EditDocumentPropertiesPage(drone);
                }

                else if (dialogueID.contains("copyMoveTo"))
                {
                    sharePage = new CopyOrMoveContentPage(drone);
                }

                else if (dialogueID.contains("historicPropertiesViewer"))
                {
                    sharePage = new ViewPropertiesPage(drone);
                }

                // The below dialogeId will be changed once this ACE-1047 issue is fixed.
                else if (dialogueID.contains("configDialog-configDialog_h"))
                {
                    sharePage = new ConfigureSiteNoticeDialogBoxPage(drone);
                }
                //                else if(dialogueID.contains("simple-dialog"))
                //                {
                //                    sharePage = new ConfirmDeletePage(drone);
                //                }

                else if (cloudSignInDialogueHeader.equals(dialogue.getText()))
                {
                    sharePage = new CloudSignInPage(drone);
                }
                else if (dialogueID.contains("newList"))
                {
                    sharePage = new NewListForm(drone);
                }
                else if(copyMoveDialogue != null && (copyMoveDialogue.getText().startsWith("Copy") || copyMoveDialogue.getText().startsWith("Move")))
                {
                	sharePage = new CopyAndMoveContentFromSearchPage(drone);
                }
                else if(dialogueID.contains("Create New Filter"))
                {
                        sharePage = new CreateNewFilterPopUpPage(drone);
                }
                else if(dialogueID.contains("cloud-folder-title"))
                {
                    sharePage = new DestinationAndAssigneePage(drone);
                }
            }
        }
        catch (NoSuchElementException nse)
        {
        }

        return sharePage;
    }

}
