package org.alfresco.po.share.site.blog;

import static org.alfresco.po.share.enums.BlogPostStatus.UPDATED;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.ALL;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.LATEST;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.MY_DRAFTS;
import static org.alfresco.po.share.site.blog.BlogTreeMenuNavigation.PostsMenu.MY_PUBLISHED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests for Blog page web elements
 * 
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class BlogPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    BlogPage blogPage = null;
    PostViewPage postViewPage = null;
    RssFeedPage rssFeedPage = null;
    String text = getClass().getSimpleName();
    List<String> postStatus;

    String editedText = text + " edited";
    String externalTitle = "Hi, everyone! Today is " + Calendar.getInstance().getTime();
    String externalMessage = "Today is " + Calendar.getInstance().getTime();
    String tagName = "blog-tag";
    String postName = "postName";
    String postText = "postText";
    List<String> tags = new ArrayList<>();
    String newPostName = "editPostName";
    String newPostText = "editPostText";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "blog" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addBlogPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.BLOG);
        customizeSitePage.addPages(addPageTypes).render();
        blogPage = siteDashBoard.getSiteNav().selectBlogPage().render();
        assertNotNull(blogPage);
    }

    @Test(dependsOnMethods = "addBlogPage")
    public void blogTreeMenuNavigation()
    {
        BlogTreeMenuNavigation blogTreeMenuNavigation = blogPage.getLeftMenus().render();
        assertTrue(blogTreeMenuNavigation.isMenuTreeVisible());
        assertTrue(blogTreeMenuNavigation.isMenuExpanded());
        blogPage = blogTreeMenuNavigation.selectListNode(ALL).render();
        assertNotNull(blogPage);
        blogPage = blogTreeMenuNavigation.selectListNode(LATEST).render();
        assertNotNull(blogPage);
        blogPage = blogTreeMenuNavigation.selectListNode(MY_DRAFTS).render();
        assertNotNull(blogPage);
        blogPage = blogTreeMenuNavigation.selectListNode(MY_PUBLISHED).render();
        assertNotNull(blogPage);
        blogTreeMenuNavigation.selectListNode(ALL).render();
    }

    @Test(dependsOnMethods = "saveAsDraft")
    public void createBlogPostInternally()
    {
        blogPage = postViewPage.clickBackLink().render();
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.createPostInternally(text, text, tagName).render();
        assertNotNull(postViewPage);

        assertTrue(postViewPage.verifyPostExists(text));

    }

    @Test(dependsOnMethods = "blogTreeMenuNavigation")
    public void saveAsDraft()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.saveAsDraft(text + "draft", text, null).render();
        assertNotNull(postViewPage);

        assertTrue(postViewPage.verifyPostExists(text + "draft"));
    }

    @Test(dependsOnMethods = "createBlogPostInternally")
    public void openPost()
    {
        blogPage = siteDashBoard.getSiteNav().selectBlogPage().render();
        assertNotNull(blogPage);
        postViewPage = blogPage.openBlogPost(text).render();
        assertNotNull(postViewPage);
        assertTrue(postViewPage.verifyPostExists(text));
    }

    @Test(dependsOnMethods = "openPost", groups = "TestBug")
    public void createPostComment()
    {
        assertTrue(postViewPage.isAddCommentDisplayed());
        postViewPage.createBlogComment(text).render();
        assertTrue(postViewPage.isCommentCorrect(text));
    }

    @Test(dependsOnMethods = "createPostComment", groups = "TestBug")
    public void editPostComment()
    {
        postViewPage.editBlogComment(text, editedText);
        assertNotNull(postViewPage);
        assertTrue(postViewPage.isCommentCorrect(editedText));
    }

    @Test(dependsOnMethods = "editPostComment", groups = "TestBug")
    public void deletePostComment()
    {
        int expCount = postViewPage.getCommentCount();
        postViewPage.deleteCommentWithConfirm(editedText);
        assertEquals(postViewPage.getCommentCount(), expCount - 1);
        assertNotNull(postViewPage);
    }

    @Test(dependsOnMethods = "deletePostComment", groups = "TestBug")
    public void editBlogPostAndSaveAsDraft()
    {
        postViewPage.editBlogPostAndUpdate(editedText, editedText, null);
    }

    @Test(dependsOnMethods = "editBlogPostAndSaveAsDraft", groups = "TestBug")
    public void getPostStatus()
    {
        postStatus = postViewPage.getPostStatus();
        assertTrue(postStatus.contains(UPDATED.getPostStatus()), "The post status is incorrect");
    }

    @Test(dependsOnMethods = "getPostStatus", groups = "TestBug")
    public void deletePostWithConfirm()
    {
        blogPage = postViewPage.clickBackLink().render();
        int expCount = blogPage.getPostsCount();
        blogPage.openBlogPost(editedText);
        blogPage = postViewPage.deleteBlogPostWithConfirm().render();
        assertEquals(blogPage.getPostsCount(), expCount - 1);
    }

    @Test(dependsOnMethods = "deletePostWithConfirm", groups = "TestBug")
    public void isPostPresented()
    {
        assertTrue(blogPage.isNewPostEnabled());
        postViewPage = blogPage.createPostInternally(postName, postText, tagName).render();
        blogPage = postViewPage.clickBackLink().render();
        Assert.assertTrue(blogPage.isPostPresented(postName), "Expected post with title '" + postName + "'");
    }

    @Test(dependsOnMethods = "isPostPresented", groups = "TestBug")
    public void checkTags()
    {
        Assert.assertTrue(blogPage.checkTags(postName, tagName), "Expected tag for post with title '" + postName + "' isn't presented");
    }

    @Test(dependsOnMethods = "checkTags", priority = 1, groups = { "ChromeIssue", "TestBug" })
    public void clickRssFeedBtn()
    {
        rssFeedPage = postViewPage.clickRssFeedBtn(username, password).render();
        assertTrue(rssFeedPage.isDisplayedInFeed(postName), "The post isn't displayed in Feed");
        assertTrue(rssFeedPage.isSubscribePanelDisplay(), "Subscribe panel isn't available");
        postViewPage = (PostViewPage) rssFeedPage.clickOnFeedContent(postName);
        assertNotNull(postViewPage);
    }

    @Test(dependsOnMethods = "checkTags", priority = 2, groups = "TestBug")
    public void editPost()
    {
        if (!(drone.getCurrentPage() instanceof BlogPage))
        {
            blogPage = postViewPage.clickBackLink().render();
        }
        blogPage.editPost(postName, newPostName, newPostText, tagName, false).render();
        assertTrue(postViewPage.verifyPostExists(newPostName));
        assertFalse(postViewPage.verifyPostExists(postName));
        postViewPage.clickBackLink().render();
    }

    @Test(dependsOnMethods = "editPost", groups = "TestBug")
    public void removeTag()
    {
        blogPage.editPost(newPostName, newPostName, newPostText, tagName, true).render();
        assertTrue(postViewPage.verifyPostExists(newPostName));
        postViewPage.clickBackLink().render();
        assertTrue(blogPage.checkTags(newPostName, null));
    }
}
