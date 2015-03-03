package org.alfresco.po.share.site.discussions;

import static org.alfresco.po.share.site.discussions.TopicsListFilter.FilterOption.ALL;
import static org.alfresco.po.share.site.discussions.TopicsListFilter.FilterOption.MOST_ACTIVE;
import static org.alfresco.po.share.site.discussions.TopicsListFilter.FilterOption.MY_TOPICS;
import static org.alfresco.po.share.site.discussions.TopicsListFilter.FilterOption.NEW;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests for Discussion page web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class DiscussionsPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    DiscussionsPage discussionsPage = null;
    TopicViewPage topicViewPage = null;
    String text = getClass().getSimpleName();
    String editedText = text + "edited";
    String textLines = "This is a topic";
    String tag = "gognotag";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "discussions" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addDiscussionsPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<>();
        addPageTypes.add(SitePageType.DISCUSSIONS);
        customizeSitePage.addPages(addPageTypes);
        discussionsPage = siteDashBoard.getSiteNav().selectDiscussionsPage().render();
        assertNotNull(discussionsPage);
    }

    @Test(dependsOnMethods = "addDiscussionsPage")
    public void isNoTopicsDisplayed()
    {
        assertTrue(discussionsPage.isNoTopicsDisplayed());
    }

    @Test(dependsOnMethods = "isNoTopicsDisplayed")
    public void createTopic()
    {
        assertTrue(discussionsPage.isNewTopicEnabled());
        topicViewPage = discussionsPage.createTopic(text).render();
        assertNotNull(topicViewPage.render());
        assertEquals(verifyCreatedTopic(), text);
    }

    @Test(dependsOnMethods = "createTopic")
    public void viewTopic()
    {
        topicViewPage.clickBack();
        assertNotNull(discussionsPage);
        topicViewPage = discussionsPage.viewTopic(text).render();
        assertNotNull(topicViewPage);
    }

    @Test(dependsOnMethods = "viewTopic")
    public void createReply()
    {
        assertTrue(topicViewPage.isReplyLinkDisplayed());
        topicViewPage.createReply(text).render();
        assertEquals(verifyCreatedReply(), text);
    }

    @Test(dependsOnMethods = "viewTopic")
    public void editTopic()
    {
        discussionsPage = topicViewPage.clickBack().render();
        topicViewPage = discussionsPage.editTopic(text, editedText, textLines).render();
        assertEquals(editedText, verifyCreatedTopic());
    }

    @Test(dependsOnMethods = "createReply")
    public void editReply()
    {
        topicViewPage.editReply(text, editedText).render();
        assertEquals(verifyCreatedReply(), editedText);
    }

    @Test(dependsOnMethods = "editReply")
    public void verifyFilters()
    {
        discussionsPage = topicViewPage.clickBack().render();
        TopicsListFilter topicsListFilter = discussionsPage.getTopicsListFilter();
        topicsListFilter.select(NEW);
        assertEquals(discussionsPage.getTopicsCount(), 1);
        topicsListFilter.select(MOST_ACTIVE);
        assertEquals(discussionsPage.getTopicsCount(), 1);
        topicsListFilter.select(ALL);
        assertEquals(discussionsPage.getTopicsCount(), 1);
        topicsListFilter.select(MY_TOPICS);
        assertEquals(discussionsPage.getTopicsCount(), 1);
    }

    @Test(dependsOnMethods = "verifyFilters")
    public void deleteTopic()
    {
        int expNum = discussionsPage.getTopicsCount() - 1;
        discussionsPage.deleteTopicWithConfirm(editedText).render();
        assertEquals(discussionsPage.getTopicsCount(), expNum);
    }

    @Test(dependsOnMethods = "deleteTopic")
    public void verifyClickOnTag()
    {
        topicViewPage = discussionsPage.createTopic(text);
        topicViewPage.editTopic(text, text, text, tag);
        assertEquals(topicViewPage.getTagName(), tag);
        discussionsPage = topicViewPage.clickOnTag(tag);
        for (int i = 0; i < 1000; i++)
        {
            drone.refresh();
            if (discussionsPage.getTopicsCount() != 0)
            {
                break;
            }
        }
        assertTrue(discussionsPage.isDeleteTopicDisplayed(text));
        discussionsPage.getTopicsListFilter().clickOnTag(tag);
        assertTrue(discussionsPage.isDeleteTopicDisplayed(text));
    }

    @Test(dependsOnMethods = "verifyClickOnTag", groups = "ChromeIssue", priority = 1)
    public void verifyRss()
    {
        RssFeedPage rssFeedPage = discussionsPage.selectRssFeed(username, password);
        assertTrue(rssFeedPage.isSubscribePanelDisplay());
        TopicDetailsPage topicDetailsPage = rssFeedPage.clickOnFeedContent(text).render();
        assertEquals(topicDetailsPage.getTopicTitle(), text);
        assertEquals(topicDetailsPage.getTopicText(), text);
        discussionsPage = topicDetailsPage.getSiteNav().selectDiscussionsPage();
    }

    @Test(dependsOnMethods = "verifyClickOnTag", priority = 2)
    public void clickView()
    {
        TopicDirectoryInfo topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(text);
        topicViewPage = topicDirectoryInfo.viewTopic();
    }

    @Test(dependsOnMethods = "clickView")
    public void verifySubReplies()
    {
        topicViewPage.createReply(text);
        ReplyDirectoryInfo replyDirectoryInfo = topicViewPage.getReplyDirectoryInfo(text);
        replyDirectoryInfo.createSubReply(text);
        assertTrue(replyDirectoryInfo.isSubReply(text));
        assertEquals(replyDirectoryInfo.getSubRepliesCount(), 1);
    }

    @Test(dependsOnMethods = "verifySubReplies")
    public void clickRead()
    {
        discussionsPage = topicViewPage.clickBack().render();
        TopicDirectoryInfo topicDirectoryInfo = discussionsPage.getTopicDirectoryInfo(text);
        TopicDetailsPage topicDetailsPage = topicDirectoryInfo.clickRead().render();
        assertEquals(topicDetailsPage.getTopicTitle(), text);
        topicViewPage.clickBack().render();
    }

    @Test(dependsOnMethods = "clickRead")
    public void isTopicPresented()
    {
        assertTrue(discussionsPage.isTopicPresented(text));
    }

    @Test(dependsOnMethods = "isTopicPresented")
    public void checkTags()
    {
        assertTrue(discussionsPage.checkTags(text, tag));
    }

    @Test(dependsOnMethods = "checkTags")
    public void removeTag()
    {
        discussionsPage.editTopic(text, text, text, tag, true);
        topicViewPage.clickBack().render();
        assertTrue(discussionsPage.checkTags(text, null));
    }

    private String verifyCreatedTopic()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(".nodeTitle>a")).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the topic");
        }
    }

    private String verifyCreatedReply()
    {
        try
        {
            return drone.findAndWait(By.cssSelector("div[class='reply']>.nodeContent>div[class*='content']>p")).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the reply");
        }
    }
}
