package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Site Blog Page object
 * relating to Share site Blog page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class BlogPage extends SitePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By NEW_POST_BTN = By.cssSelector(".new-blog>span");
    private static final By CONFIGURE_BLOG = By.cssSelector(".configure-blog>span");
    private static final String POSTS_CONTAINER = "td[class*='blogposts']";
    private static final By EMPTY_POST_CONTAINER = By.cssSelector("td[class*='empty']");
    private static final By BACK_LINK = By.cssSelector("span.backLink>a");
    private static final String POST_TITLE = "//span[@class='nodeTitle']/a[text()='%s']";
    private static final By RSS_BUTTON = By.cssSelector(".rss-feed a");
    private static final String TAG_NONE = "//a[contains(text(),'%s')]/ancestor::div[@class='node post']"
        + "/following-sibling::div[@class='nodeFooter']/span[@class='nodeAttrLabel tagLabel']/following-sibling::span";
    private static final String TAG_NAME = "//a[contains(text(),'%s')]/ancestor::div[@class='node post']"
        + "/following-sibling::div[@class='nodeFooter']/span[@class='tag']/a[text()='%s']";

    /**
     * Constructor
     */
    public BlogPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogPage render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(NEW_POST_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    public BlogPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click New Topic button
     *
     * @return NewTopicForm page
     */
    private NewPostForm clickNewPost()
    {
        try
        {
            drone.findAndWait(NEW_POST_BTN).click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + NEW_POST_BTN, te);
        }
        return new NewPostForm(drone);
    }

    /**
     * Method to create new topic without text field
     *
     * @param titleField
     * @return
     */
    public PostViewPage createPostInternally(String titleField)
    {
        BlogPage blogPage = new BlogPage(drone);
        NewPostForm newPostForm = blogPage.clickNewPost();
        newPostForm.setTitleField(titleField);
        newPostForm.clickPublishInternally();
        return new PostViewPage(drone).render();
    }

    /**
     * Method to create new topic with text field
     *
     * @param titleField
     * @return
     */
    public PostViewPage createPostInternally(String titleField, String txtLines)
    {
        BlogPage blogPage = new BlogPage(drone);
        NewPostForm newPostForm = blogPage.clickNewPost();
        waitUntilAlert();
        newPostForm.setTitleField(titleField);
        newPostForm.insertText(txtLines);
        newPostForm.clickPublishInternally();
        waitUntilAlert(7);
        return new PostViewPage(drone).render();
    }

    /**
     * Method to create new topic with text field and tag
     *
     * @param titleField
     * @return PostViewPage
     */
    public PostViewPage createPostInternally(String titleField, String txtLines, String tagName)
    {
        BlogPage blogPage = new BlogPage(drone);
        NewPostForm newPostForm = blogPage.clickNewPost();
        waitUntilAlert();
        newPostForm.setTitleField(titleField);
        newPostForm.insertText(txtLines);
        newPostForm.addTag(tagName);
        newPostForm.clickPublishInternally();
        waitUntilAlert(5);
        return new PostViewPage(drone).render();
    }

    /**
     * Method to create new topic with text field and tag
     *
     * @param titleField
     * @return PostViewPage
     */
    public PostViewPage createPostInternally(String titleField, String txtLines, List<String> tags)
    {
        BlogPage blogPage = new BlogPage(drone);
        NewPostForm newPostForm = blogPage.clickNewPost();
        waitUntilAlert();
        newPostForm.setTitleField(titleField);
        newPostForm.insertText(txtLines);
        newPostForm.addTag(tags);
        newPostForm.clickPublishInternally();
        waitUntilAlert(5);
        return new PostViewPage(drone).render();
    }

    /**
     * Method to create new topic with text field and save as draft
     *
     * @param titleField
     * @return
     */
    public PostViewPage saveAsDraft(String titleField, String txtLines)
    {
        BlogPage blogPage = new BlogPage(drone);
        NewPostForm newPostForm = blogPage.clickNewPost();
        newPostForm.setTitleField(titleField);
        newPostForm.insertText(txtLines);
        return newPostForm.clickSaveAsDraft().render(3000);
    }


    /**
     * Method to Configure External Blog (wordpress, typepad)
     * public void configureExternalBlog(ConfigureBlogPage.TypeOptions option, String name, String desc, String url,
     * String userName, String password)
     * {
     * ConfigureBlogPage configureBlogPage = clickConfigureBlog();
     * configureBlogPage.selectTypeOption(option);
     * configureBlogPage.inputNameField(name);
     * configureBlogPage.inputDescriptionField(desc);
     * configureBlogPage.inputURL(url);
     * configureBlogPage.inputUserName(userName);
     * configureBlogPage.inputPassword(password);
     * configureBlogPage.clickOk();
     * waitUntilAlert(7);
     * }
     * /**
     * Method to verify whether configure External Blog is enabled
     *
     * @return true if enabled
     */
    public boolean isNewPostEnabled()
    {
        String someButton = drone.findAndWait(NEW_POST_BTN).getAttribute("class");
        return !someButton.contains("yui-button-disabled");
    }

    /**
     * Method to open a post
     *
     * @param title
     * @return Post View Page
     */
    public PostViewPage openBlogPost(String title)
    {
        try
        {
            WebElement thePost = drone.findAndWait(By.xpath(String.format("//a[text()='%s']", title)));
            drone.mouseOver(thePost);
            thePost.click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to click the link", te);
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to retrieve the posts count
     *
     * @return number of posts
     */
    public int getPostsCount()
    {
        try
        {
            List<WebElement> numOfPosts = drone.findAndWaitForElements(By.cssSelector(POSTS_CONTAINER));
            return numOfPosts.size();
        }
        catch (TimeoutException te)
        {
            return 0;
        }
        catch (NoSuchElementException nse)
        {
            return 0;
        }
    }

    /**
     * Return true if Post, and return false if Post is absent
     * test BlogPageTest.isPostPresented
     *
     * @param postName
     * @return
     */
    public boolean isPostPresented(String postName)
    {
        boolean isDisplayed;
        checkNotNull(postName);
        try
        {
            WebElement theItem = drone.find(By.xpath(String.format(POST_TITLE, postName)));
            isDisplayed = theItem.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        catch (TimeoutException te)
        {
            throw new PageException(String.format("Blog info with title %s was not found", postName), te);
        }
        return isDisplayed;
    }

    /**
     * Method to create new topic with text field and save as draft
     *
     * @param titleField
     * @return
     */
    public PostViewPage saveAsDraft(String titleField, String txtLines, List<String> tagName)
    {
        logger.info("Creating draft post " + titleField);
        BlogPage blogPage = new BlogPage(drone);
        NewPostForm newPostForm = blogPage.clickNewPost();
        newPostForm.setTitleField(titleField);
        newPostForm.insertText(txtLines);
        if (tagName != null)
        {
            newPostForm.addTag(tagName);
        }
        return newPostForm.clickSaveAsDraft().render(3000);
    }

    /**
     * Method to view Rss Feed for post pages
     *
     * @param username
     * @param password
     * @return RssFeedPage
     */
    public RssFeedPage clickRssFeedBtn(String username, String password)
    {
        logger.info("Viewing RSS Feed for Blog page");
        String currentWikiUrl = drone.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentWikiUrl);
        String shareUrlVar = PageUtils.getShareUrl(currentWikiUrl);
        String siteName = PageUtils.getSiteName(currentWikiUrl);
        String rssUrl = String.format("%s%s:%s@%s/feedservice/components/blog/rss?site=%s", protocolVar, username, password, shareUrlVar, siteName);
        drone.navigateTo(rssUrl);
        return new RssFeedPage(drone).render();
    }

    public boolean checkTags(String title, String tag)
    {
        boolean isDisplayed;
        WebElement element;
        String tagXpath;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        if (tag == null)
        {
            tagXpath = String.format(TAG_NONE, title);
            try
            {
                element = drone.findAndWait(By.xpath(tagXpath));
                isDisplayed = element.getText().contains("None");
            }
            catch (TimeoutException ex)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate post page or 'Tags: (None)'", ex);
                }
                throw new PageOperationException("Unable to locate post page or 'Tags: (None)'");
            }

        }
        else
        {
            tagXpath = String.format(TAG_NAME, title, tag);
            try
            {
                element = drone.findAndWait(By.xpath(tagXpath));
                isDisplayed = element.isDisplayed();
            }
            catch (TimeoutException te)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate expected tag or post page", te);
                }
                throw new PageOperationException("Unable to locate expected tag or post page");
            }
        }
        return isDisplayed;
    }

    /**
     * Method to edit post
     * method validate by BlogPageTest.editPost
     *
     * @param oldTitle
     * @param newTitle
     * @param txtLines
     * @param tagName
     * @return
     */
    public PostViewPage editPost(String oldTitle, String newTitle, String txtLines, String tagName, boolean removeTag)
    {
        EditPostForm editPostForm = getPostDirectoryInfo(oldTitle).editPost();
        editPostForm.setTitleField(newTitle);
        editPostForm.insertText(txtLines);
        if (!removeTag)
        {
            editPostForm.addTag(tagName);
        }
        else
        {
            editPostForm.removeTag(tagName);
        }
        editPostForm.clickSaveAsDraft();
        waitUntilAlert();
        logger.info("Edited post " + oldTitle);
        return new PostViewPage(drone).render();
    }

    public PostDirectoryInfo getPostDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']/../../../..", title)), WAIT_TIME_3000);
            drone.mouseOver(row);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException(String.format("Post directory info with title %s was not found", title), nse);
        }
        catch (TimeoutException te)
        {
            throw new PageException(String.format("Post directory info with title %s was not found", title), te);
        }
        return new PostDirectoryInfoImpl(drone, row);
    }

    public BlogTreeMenuNavigation getLeftMenus()
    {
        return new BlogTreeMenuNavigation(drone);
    }
}