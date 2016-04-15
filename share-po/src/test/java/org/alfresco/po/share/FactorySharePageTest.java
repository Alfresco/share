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

import org.alfresco.po.AbstractTest;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.alfresco.RepositoryAdminConsolePage;
import org.alfresco.po.alfresco.TenantAdminConsolePage;
import org.alfresco.po.alfresco.WebScriptsPage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.admin.ManageSitesPage;
import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.repository.ModelsPage;
import org.alfresco.po.share.search.AdvanceSearchContentPage;
import org.alfresco.po.share.search.AdvanceSearchFolderPage;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.AllSitesResultsPage;
import org.alfresco.po.share.search.FacetedSearchPage;
import org.alfresco.po.share.search.RepositoryResultsPage;
import org.alfresco.po.share.search.SiteResultsPage;
import org.alfresco.po.share.site.AddGroupsPage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.PendingInvitesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.CreateHtmlContentPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.MyFilesPage;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.user.FollowersPage;
import org.alfresco.po.share.user.FollowingPage;
import org.alfresco.po.share.user.LanguageSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.NotificationPage;
import org.alfresco.po.share.user.UserContentPage;
import org.alfresco.po.share.user.UserSitesPage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Test Share page factory url parser.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class FactorySharePageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(FactorySharePageTest.class);
    
    private final String baseUrl = "http://localhost:8081/share";
    private final String dashboard = baseUrl + "%s/-system-/page/user/admin/dashboard";
    private final String documentLibrary = baseUrl + "/page/site/test/documentlibrary#filter=path%7C%2Ftest%7C&page=1";
    private final String documentLibrary2 = baseUrl + "%s/page/site/test/documentlibrary";
    private final String siteDashboard = baseUrl + "%/page/site/createcontentpagetest1376912493094/dashboard";
    private final String myTasks = baseUrl + "%s/-system-/page/my-tasks";
    private final String peopleFinder = baseUrl + "%s/page/people-finder";
    private final String userSearchPage = baseUrl + "%s/page/console/admin-console/users";
    private final String siteSarchResult = baseUrl + "%s/page/site/site1376665231775/search?t=ipsum";
    private final String advanceSearch = baseUrl + "%s/page/advsearch";
    private final String advanceContentSearch = baseUrl + "%s/page/advsearch?st=&stag=&ss=&sa=&sr=true&sq=%7b%22prop_cm_name%22%3a%22%22%2c%22prop_cm_title%22%3a%22%22%2c%22prop_cm_description%22%3a%22%22%2c%22prop_mimetype%22%3A%22%22%2C%22prop_cm_modified-date-range%22%3A%22%22%2C%22prop_cm_modifier%22%3A%22%22%2C%22datatype%22%3a%22cm%3Acontent%22%7d";
    private final String advanceFolderSearch = baseUrl + "%s/page/advsearch?st=&stag=&ss=&sa=&sr=true&sq=%7b%22prop_cm_name%22%3a%22%22%2c%22prop_cm_title%22%3a%22%22%2c%22prop_cm_description%22%3a%22%22%2c%22datatype%22%3a%22cm%3afolder%22%7d";   
    private final String searchResult = baseUrl + "%s/page/search?t=ipsum";
    private final String searchResultAllSites = baseUrl + "%s/page/search?t=ipsum&s=&a=true&r=false";
    private final String searchResultRepository = baseUrl + "%s/page/search?t=ipsum&s=&a=true&r=true";
    private final String myProfile = baseUrl + "%s/page/user/admin/profile";
    private final String siteFinder = baseUrl + "%s/page/site-finder";
    private final String wiki = baseUrl + "%s/page/site/swsdp/wiki-page?title=Main_Page";
    private final String changePaswword = baseUrl + "%s/page/user/admin/change-password";
    private final String repository = baseUrl + "%s/page/repository";
    private final String editTasks = baseUrl + "%s/page/task-edit?taskId=activiti$1187&referrer=tasks&myTasksLinkBack=true";
    private final String inlineEdit = baseUrl + "%s/page/inline-edit?nodeRef=workspace://SpacesStore/18f0656e-c2b2-4f24-b197-3123f3e8f53e";
    private final String documentDetailsPage = baseUrl + 
            "%s/page/site/swsdp/document-details?nodeRef=workspace://SpacesStore/99cb2789-f67e-41ff-bea9-505c138a6b23";
    private final String managePermission = baseUrl +
            "%s/page/site/site1376665231775/manage-permissions?nodeRef=workspace://SpacesStore/94d767d5-175a-4a5a-8f59-db855f6159f2";
    private final String createPlainText = baseUrl +
            "%s/page/create-content?destination=workspace://SpacesStore/e35f7167-d5ba-4730-a74c-b3f438bb2ab8&itemId=cm:content&mimeType=text/plain";
    private final String createHtmlText = baseUrl +
                "%s/page/site/editdocumentsitetest1393512596448/create-content?destination=workspace://SpacesStore/9c125f44-e298-403d-b16d-ed34ade47d42&itemId=cm:content&mimeType=text/html";
    private final String createXmlText = baseUrl +
                "%s/page/site/editdocumentsitetest1393512596448/create-content?destination=workspace://SpacesStore/9c125f44-e298-403d-b16d-ed34ade47d42&itemId=cm:content&mimeType=text/xml";
    private final String newuser = baseUrl + "%s/page/console/admin-console/users#state=panel%3Dcreate";
    private final String customizeSite = baseUrl + "%s/page/site/site1376665231775/customise-site";
    private final String customizeSiteDashboard = baseUrl + "%s/page/site/site1376665231775/customise-site-dashboard";
    private final String editDocumentProperties = baseUrl + "%s/page/edit-metadata?nodeRef=workspace://SpacesStore/18f0656e-c2b2-4f24-b197-3123f3e8f53e";
    private final String siteMembers = baseUrl + "%s/page/site/test/site-members";
    private final String invite = baseUrl + "%s/page/site/test/invite";
    private final String foldersDetailsPage = baseUrl + 
            "%s/page/site/site1376665231775/folder-details?nodeRef=workspace://SpacesStore/94d767d5-175a-4a5a-8f59-db855f6159f2";
    private final String startWorkFlowPage = baseUrl + "%s/page/start-workflow?referrer=tasks&myTasksLinkBack=true";
    private final String workFlowDetailsPage = baseUrl + "%s/share/page/workflow-details?workflowId=activiti$17657&referrer=workflows&myWorkflowsLinkBack=true";
    private final String myWorkFlowsPage = baseUrl + "%s/share/page/my-workflows#filter=workflows|active";
    private final String taskDetailsPage = baseUrl + "%s/share/page/my-workflows#filter=workflows|active";
    private final String editTaskPage = baseUrl + "%s/share/page/my-workflows#filter=workflows|active";
    private final String languageSettingsPage = baseUrl + "%s/page/user/userenterprise42-151739%40hybrid.test/change-locale";
    private final String groupsPage = baseUrl+"%s/page/console/admin-console/groups";
    private final String siteGroupsPage = baseUrl+"%s/page/site/sitemsitesapitests1383578859371/site-groups";
    private final String addGroupsPage = baseUrl+"%s/page/site/sitemsitesapitests1383578859371/add-groups";
    private final String repositoryWithFolder = baseUrl+"%s/page/repository#filter=path|/Folderhtc-RepositoryFolderTests3|&page=1";
    private final String createNewTopicPage = baseUrl+"%s/page/site/new-site/discussions-createtopic";
    private final String topicDetailsPage = baseUrl+"%s/page/site/new-site/discussions-topicview?topicId=post-1394637958079_1640&listViewLinkBack=true";
    private final String discussionsPage = baseUrl+"%s/page/site/new-site/discussions-topiclist";
    private final String customiseUserDashboardPage = baseUrl+"%s/page/customise-user-dashboard";
    private final String nodeBrowserPage  = baseUrl+"%s/page/console/admin-console/node-browser";
    private final String adminConsolePage= baseUrl+"%s/page/console/admin-console/application";
    private final String manageSitesPage= baseUrl+"%s/page/console/admin-console/manage-sites";
    private final String myFilesPage= baseUrl+"%s/page/context/mine/myfiles";
    private final String sharedFilesPage= baseUrl+"%s/page/context/shared/sharedfiles";
    private final String notificationPage = baseUrl + "%s/share/page/user/admin/user-notifications";
    private final String userSitesPage = baseUrl + "%s/share/page/user/admin/user-sites";
    private final String links = baseUrl + "%s/page/site/swsdp/links";
    private final String wikiList = baseUrl + "%s/page/site/swsdp/wiki";
    private final String pendingInvites = baseUrl + "%s/page/site/swsdp/pending-invites";
    private final String facetedSearchPage = baseUrl+"%s/page/dp/ws/faceted-search";
    private final String userContentPage =  baseUrl+"/page/user/admin/user-content";
    private final String followingPage = baseUrl+"/page/user/admin/following";
    private final String followersPage = baseUrl+"/page/user/admin/followers";
    private final String modelsPage = baseUrl+"/repository#filter=path%7C%2FData%2520Dictionary%2FModels";

    @Test(groups={"unit"})
    public void resolveUrls()
    {
        WebDriver driver = new HtmlUnitDriver();
        try
        {
            long start = System.currentTimeMillis();
            HtmlPage page = resolvePage(dashboard, "dashboard", driver);
            Assert.assertTrue(page instanceof DashBoardPage);

            page = resolvePage(documentDetailsPage, "documentDetailsPage", driver);
            Assert.assertTrue(page instanceof DocumentDetailsPage);

            page = resolvePage(documentLibrary, "documentLibrary", driver);
            Assert.assertTrue(page instanceof DocumentLibraryPage);
            
            page = resolvePage(documentLibrary2, "documentLibrary2", driver);
            Assert.assertTrue(page instanceof DocumentLibraryPage);

            page = resolvePage(siteDashboard, "siteDashboard", driver);
            Assert.assertTrue(page instanceof SiteDashboardPage);
            
            page = resolvePage(myTasks, "myTasks", driver);
            Assert.assertTrue(page instanceof MyTasksPage);
            
            page = resolvePage(peopleFinder, "peopleFinder", driver);
            Assert.assertTrue(page instanceof PeopleFinderPage);

            page = resolvePage(myProfile, "myProfile", driver);
            Assert.assertTrue(page instanceof MyProfilePage);
            
            page = resolvePage(siteFinder, "siteFinder",  driver);
            Assert.assertTrue(page instanceof SiteFinderPage);

            page = resolvePage(wiki, "wiki", driver);
            Assert.assertTrue(page instanceof WikiPage);

            page = resolvePage(wikiList, "wikiList", driver);
            Assert.assertTrue(page instanceof WikiPageList);

            page = resolvePage(changePaswword, "changePaswword", driver);
            Assert.assertTrue(page instanceof ChangePasswordPage);

            page = resolvePage(repository, "repository", driver);
            Assert.assertTrue(page instanceof RepositoryPage);
            
            page = resolvePage(repositoryWithFolder, "repository", driver);
            Assert.assertTrue(page instanceof RepositoryPage);

            page = resolvePage(managePermission, "managePermission", driver);
            Assert.assertTrue(page instanceof ManagePermissionsPage);

            page = resolvePage(createPlainText, "createPlainText", driver);
            Assert.assertTrue(page instanceof CreatePlainTextContentPage);

            page = resolvePage(createHtmlText, "createHtmlText", driver);
            Assert.assertTrue(page instanceof CreateHtmlContentPage);

            page = resolvePage(createXmlText, "createXmlText", driver);
            Assert.assertTrue(page instanceof CreatePlainTextContentPage);

            page = resolvePage(inlineEdit, "inlineEdit", driver);
            Assert.assertTrue(page instanceof InlineEditPage);
            
            page = resolvePage(editDocumentProperties, "editDocumentProperties", driver);
            Assert.assertTrue(page instanceof EditDocumentPropertiesPage);
            
            page = resolvePage(siteMembers, "siteMembers", driver);
            Assert.assertTrue(page instanceof org.alfresco.po.share.site.SiteMembersPage);

            page = resolvePage(invite, "invite", driver);
            Assert.assertTrue(page instanceof InviteMembersPage);
            
            page = resolvePage(newuser, "newuser", driver);
            Assert.assertTrue(page instanceof NewUserPage);
            
            page = resolvePage(customizeSite, "customizeSite", driver);
            Assert.assertTrue(page instanceof CustomizeSitePage);
            
            page = resolvePage(customizeSiteDashboard, "customizeSiteDashboard", driver);
            Assert.assertTrue(page instanceof CustomiseSiteDashboardPage);

            page = resolvePage(foldersDetailsPage, "foldersDetailsPage", driver);
            Assert.assertTrue(page instanceof FolderDetailsPage);
            
            page = resolvePage(myFilesPage, "myFilesPage", driver);
            Assert.assertTrue(page instanceof MyFilesPage);
            
            page = resolvePage(sharedFilesPage, "sharedFilesPage", driver);
            Assert.assertTrue(page instanceof SharedFilesPage);

            page = resolvePage(links, "links", driver);
            Assert.assertTrue(page instanceof LinksPage);

            page = resolvePage(pendingInvites, "pending-invites", driver);
            Assert.assertTrue(page instanceof PendingInvitesPage);

            page = resolvePage(followingPage, "following", driver);
            Assert.assertTrue(page instanceof FollowingPage);

            page = resolvePage(followersPage, "followers", driver);
            Assert.assertTrue(page instanceof FollowersPage);

            //---------------search ----------------
            page = resolvePage(advanceSearch, "advanceSearch", driver);
            Assert.assertTrue(page instanceof AdvanceSearchPage);            
            
            page = resolvePage(advanceContentSearch, "advContent-search", driver);
            Assert.assertTrue(page instanceof AdvanceSearchContentPage);
            
            page = resolvePage(advanceFolderSearch, "advanceFolderSearch", driver);
            Assert.assertTrue(page instanceof AdvanceSearchFolderPage);
            
            page = resolvePage(searchResult, "searchResult", driver);
            Assert.assertTrue(page instanceof AllSitesResultsPage);
            
            page = resolvePage(searchResultAllSites, "searchResultAll", driver);
            Assert.assertTrue(page instanceof AllSitesResultsPage);

            page = resolvePage(searchResultRepository, "searchResultRepo", driver);
            Assert.assertTrue(page instanceof RepositoryResultsPage);
            
            page = resolvePage(siteSarchResult, "siteSearchResult", driver);
            Assert.assertTrue(page instanceof SiteResultsPage);

            page = resolvePage(userSearchPage, "userSearchPage", driver);
            Assert.assertTrue(page instanceof UserSearchPage);
            
            page = resolvePage(startWorkFlowPage, "start-workflow", driver);
            Assert.assertTrue(page instanceof StartWorkFlowPage);

            page = resolvePage(workFlowDetailsPage, "workflow-details", driver);
            Assert.assertTrue(page instanceof WorkFlowDetailsPage);

            page = resolvePage(myWorkFlowsPage, "my-workflows", driver);
            Assert.assertTrue(page instanceof MyWorkFlowsPage);
            
            page = resolvePage(editTasks, "edit-task", driver);
            Assert.assertTrue(page instanceof EditTaskPage);

            page = resolvePage(taskDetailsPage, "task-edit", driver);
            Assert.assertTrue(page instanceof MyWorkFlowsPage);

            page = resolvePage(editTaskPage, "task-details", driver);
            Assert.assertTrue(page instanceof MyWorkFlowsPage);

            page = resolvePage(languageSettingsPage, "change-locale", driver);
            Assert.assertTrue(page instanceof LanguageSettingsPage);
            
            page = resolvePage(groupsPage, "groups", driver);
            Assert.assertTrue(page instanceof GroupsPage);
            
            page = resolvePage(siteGroupsPage, "site-groups", driver);
            Assert.assertTrue(page instanceof SiteGroupsPage);
            
            page = resolvePage(addGroupsPage, "add-groups", driver);
            Assert.assertTrue(page instanceof AddGroupsPage);

            page = resolvePage(createNewTopicPage, "discussions-createtopic", driver);
            Assert.assertTrue(page instanceof CreateNewTopicPage);

            page = resolvePage(topicDetailsPage, "discussions-topicview", driver);
            Assert.assertTrue(page instanceof TopicDetailsPage);

            page = resolvePage(discussionsPage, "discussions-topiclist", driver);
            Assert.assertTrue(page instanceof DiscussionsPage);

            page = resolvePage(customiseUserDashboardPage, "customise-user-dashboard", driver);
            Assert.assertTrue(page instanceof CustomiseUserDashboardPage);

            page = resolvePage(nodeBrowserPage, "node-browser", driver);
            Assert.assertTrue(page instanceof NodeBrowserPage);

            //---------------admin console ----------------
            page = resolvePage(adminConsolePage, "admin-console", driver);
            Assert.assertTrue(page instanceof AdminConsolePage);
            
            page = resolvePage(manageSitesPage, "manage-sites", driver);
            Assert.assertTrue(page instanceof ManageSitesPage);
            
            page = resolvePage(notificationPage, "user-notifications", driver);
            Assert.assertTrue(page instanceof NotificationPage);
            
            page = resolvePage(userSitesPage, "user-sites", driver);
            Assert.assertTrue(page instanceof UserSitesPage);

            //---------------faceted search ----------------
            page = resolvePage(facetedSearchPage, "faceted-search", driver);
            Assert.assertTrue(page instanceof FacetedSearchPage);

            //---------------User Content Page ----------------
            page = resolvePage(userContentPage, "user-content", driver);
            Assert.assertTrue(page instanceof UserContentPage);
            //---------------Alfresco Admin Pages -------------
            page = resolvePage("http://localhost:8080/alfresco/service/index","index", driver);
            Assert.assertTrue(page instanceof WebScriptsPage);
            
            page = resolvePage("http://localhost:8080/alfresco/s/enterprise/admin/admin-tenantconsole", "admin-tenantconsole", driver);
            Assert.assertTrue(page instanceof TenantAdminConsolePage);
            
            page = resolvePage("http://localhost:8080/alfresco/s/enterprise/admin/admin-repoconsole", "admin-repoconsole", driver);
            Assert.assertTrue(page instanceof RepositoryAdminConsolePage);
            
            page = resolvePage("http://localhost:8080/share/xxyyzz", "UnknownSharePage", driver).render();
            Assert.assertTrue(page instanceof SharePage);
            Assert.assertTrue(page instanceof UnknownSharePage);
            
            page = resolvePage(modelsPage, "ModelsPage", driver);
            Assert.assertTrue(page instanceof ModelsPage);
            
;
            
            long duration = System.currentTimeMillis() - start;
            logger.info("Total duration of test in milliseconds: " + duration);
        }
        finally
        {
            driver.quit();
        }
    }
    
    /**
     * Wrapper to measure time of operation.
     */
    private HtmlPage resolvePage(final String url, final String name, WebDriver driver)
    {
        long startProcess = System.currentTimeMillis();
        FactorySharePage fs = (FactorySharePage) factoryPage;
        HtmlPage page = fs.getPage(url, driver);
        long endProcess = System.currentTimeMillis() - startProcess;
        logger.info(String.format("The page %s returned in %d", name, endProcess));
        return page;
    }
    
}
