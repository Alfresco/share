package org.alfresco.po.share.site.blog;

import com.google.common.base.CharMatcher;
import org.alfresco.po.share.exception.ShareException;
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

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to represent Post View page
 *
 * @author Marina.Nenadovets
 */

public class PostViewPage extends BlogPage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By ADD_COMMENT_BTN = By.cssSelector(".onAddCommentClick>span>button");
    private static final By BACK_LINK = By.cssSelector("span.backLink>a, span.forwardLink>a");
    private static final By NEW_POST_BTN = By.cssSelector(".new-blog>span");
    private static final By POST_TITLE = By.cssSelector(".nodeTitle>a");
    private static final By COMMENT_CONTENT = By.cssSelector(".comment-content>p");
    private static final By EDIT_LINK = By.cssSelector(".onEditBlogPost>a");
    private static final By DELETE_LINK = By.cssSelector(".onDeleteBlogPost>a");
    private static final By TAG = By.cssSelector("span.tag > a");
    private static final By TAG_NONE = By.xpath("//span[@class='nodeAttrValue' and text()='(None)']");
    private static final By POST_TEXT = By.cssSelector(".nodeContent .content p");
    private static final By POST_STATUS = By.cssSelector(".nodeStatus");
    private static final By UPDATE_EXTERNALLY = By.cssSelector(".onUpdateExternal a");
    private static final By REMOVE_EXTERNALLY = By.cssSelector(".onUnpublishExternal a");


    public PostViewPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PostViewPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NEW_POST_BTN),
            getVisibleRenderElement(BACK_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    public PostViewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PostViewPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for clicking Add comment button
     *
     * @param comment
     * @return
     */
    private BlogCommentForm clickAddCommentBtn(String comment)
    {
        try
        {
            drone.findAndWait(ADD_COMMENT_BTN).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find comment button", nse);
        }
        return new BlogCommentForm(drone);
    }

    /**
     * Method for creating blog comment
     *
     * @param comment
     * @return Post View page object
     */
    public PostViewPage createBlogComment(String comment)
    {
        PostViewPage postViewPage = new PostViewPage(drone);
        BlogCommentForm blogCommentForm = postViewPage.clickAddCommentBtn(comment);
        blogCommentForm.insertText(comment);
        blogCommentForm.clickAddComment().render();
        waitUntilAlert();
        if (isCommentPresent(comment))
        {
            return new PostViewPage(drone).render();
        }
        throw new PageOperationException("Comment wasn't added");
    }

    private boolean isCommentPresent(String comment)
    {
        boolean isPresent = false;
        List<WebElement> allComments = drone.findAll(COMMENT_CONTENT);
        for (WebElement allTheComments : allComments)
        {
            isPresent = allTheComments.getText().equalsIgnoreCase(comment);
        }
        return isPresent;
    }

    /**
     * Method to retrieve the count of comments
     *
     * @return int
     */
    public int getCommentCount()
    {
        try
        {
            List<WebElement> span = drone.findAll(COMMENT_CONTENT);
            return span.size();
        }
        catch (NoSuchElementException nse)
        {
            return 0;
        }
    }

    private boolean isEnabled(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isEnabled();
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * Method to verify whether add comment button is displayed
     *
     * @return true if visible
     */
    public boolean isAddCommentDisplayed()
    {
        return isEnabled(ADD_COMMENT_BTN);
    }

    /**
     * Method to go back to Blog page list
     *
     * @return BlogPage
     */
    public BlogPage clickBackLink()
    {
        try
        {
            WebElement backLink = drone.findAndWait(BACK_LINK);
            backLink.click();
            waitUntilAlert(7);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + BACK_LINK, te);
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to verify whether post was created
     *
     * @return boolean
     */
    public boolean verifyPostExists(String title)
    {
        try
        {
            String actualTitle = drone.find(POST_TITLE).getText();
            return title.equals(actualTitle);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Timed out finding the post", te);
        }
    }

    private EditPostForm clickEdit()
    {
        try
        {
            drone.findAndWait(EDIT_LINK).click();
            return new EditPostForm(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + EDIT_LINK, te);
        }
    }

    private CommentDirectoryInfo getCommentDirectoryInfo(String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//div[@class='comment-content']/p[text()='%s']/../..", title)), WAIT_TIME_3000);
            drone.mouseOverOnElement(row);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), nse);
        }
        catch (TimeoutException te)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), te);
        }
        return new CommentDirectoryInfo(drone, row);
    }

    /**
     * Method to edit post comment
     *
     * @param oldComment
     * @param newComment
     * @return PostViewPage
     */
    public PostViewPage editBlogComment(String oldComment, String newComment)
    {
        getCommentDirectoryInfo(oldComment).clickEdit();
        BlogCommentForm blogCommentForm = new BlogCommentForm(drone);
        blogCommentForm.insertText(newComment);
        blogCommentForm.clickAddComment();
        waitUntilAlert();
        if (isCommentPresent(newComment))
        {
            return new PostViewPage(drone).render();
        }
        throw new PageOperationException("Comment can't be edited");
    }

    private void clickDelete()
    {
        try
        {
            drone.findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + DELETE_LINK, te);
        }
    }

    /**
     * Method to edit a blog post and save it as draft
     *
     * @param newTitle
     * @param newLines
     * @return PostViewPage
     */
    public PostViewPage editBlogPostAndUpdate(String newTitle, String newLines, List<String> tags)
    {
        EditPostForm editPostForm = clickEdit();
        editPostForm.setTitleField(newTitle);
        editPostForm.insertText(newLines);
        if (!(tags == null))
        {
            editPostForm.addTag(tags);
        }
        editPostForm.clickSaveAsDraft();
        logger.info("Edited blog post" + newTitle + "and saved it as draft");
        return new PostViewPage(drone).render();
    }

    /**
     * Method to edit a blog post and save it as draft
     *
     * @param newTitle
     * @param newLines
     * @return PostViewPage
     */
    public PostViewPage editBlogPostAndPublishInternally(String newTitle, String newLines, List<String> tags)
    {
        EditPostForm editPostForm = clickEdit();
        editPostForm.setTitleField(newTitle);
        editPostForm.insertText(newLines);
        if (!(tags == null))
        {
            editPostForm.addTag(tags);
        }
        editPostForm.clickPublishInternally();
        logger.info("Edited blog post" + newTitle + "and published it internally");
        return new PostViewPage(drone).render();
    }

    /**
     * Method to edit blog post and publish it externally
     *
     * @param newTitle
     * @param newLines
     * @param tags
     * @return PostViewPage
     */
    public PostViewPage editBlogPostAndPublishExternally(String newTitle, String newLines, List<String> tags)
    {
        EditPostForm editPostForm = clickEdit();
        editPostForm.setTitleField(newTitle);
        editPostForm.insertText(newLines);
        if (!(tags == null))
        {
            editPostForm.addTag(tags);
        }
        editPostForm.clickUpdateInternallyPublishExternally();
        if (hasErrorMessage())
        {
            waitUntilAlert();
        }
        logger.info("Edited blog post" + newTitle + "and published it externally");
        return new PostViewPage(drone).render();
    }

    /**
     * Method to retrieve prompt text
     *
     * @return String value
     */
    public String getPromptText()
    {
        String promptText;
        try
        {
            promptText = drone.find(POST_TEXT).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to retrieve the prompt message", nse);
        }
        return promptText;
    }

    /**
     * Method to delete a post and confirm
     *
     * @return BlogPage
     */
    public BlogPage deleteBlogPostWithConfirm()
    {
        try
        {
            clickDelete();
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            logger.info("Deleted blog post");
            return new BlogPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to delete a post", te);
        }
    }

    /**
     * Method to delete a comment and confirm
     *
     * @param commentTitle
     * @return PostViewPage
     */
    public PostViewPage deleteCommentWithConfirm(String commentTitle)
    {
        try
        {
            getCommentDirectoryInfo(commentTitle).clickDelete();
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert(3);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to delete the comment", te);
        }
        logger.info("Deleted blog comment");
        return drone.getCurrentPage().render();
    }

    /**
     * Method to verify whether edit post is displayed
     *
     * @return true if displayed
     */
    public boolean isEditPostDisplayed()
    {
        return drone.isElementDisplayed(EDIT_LINK);
    }

    /**
     * Method to verify whether delete post is displayed
     *
     * @return true if displayed
     */
    public boolean isDeletePostDisplayed()
    {
        return drone.isElementDisplayed(DELETE_LINK);
    }

    /**
     * Method to verify whether edit comment is displayed
     *
     * @param comment
     * @return true if displayed
     */
    public boolean isEditCommentDisplayed(String comment)
    {
        return getCommentDirectoryInfo(comment).isEditDisplayed();
    }

    /**
     * Method to verify whether delete comment is displayed
     *
     * @param comment
     * @return true if displayed
     */
    public boolean isDeleteCommentDisplayed(String comment)
    {
        return getCommentDirectoryInfo(comment).isDeleteDisplayed();
    }

    /**
     * Method to retrieve tag added to Blog
     *
     * @return String
     */
    public String getTagName()
    {
        try
        {
            if (!drone.isElementDisplayed(TAG_NONE))
            {
                String tagName = drone.findAndWait(TAG).getText();
                if (!tagName.isEmpty())
                {
                    return tagName;
                }
                else
                {
                    throw new IllegalArgumentException("Cannot find tag");
                }
            }
            else
            {
                return drone.find(TAG_NONE).getText();
            }

        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to retrieve the tag", te);
        }
    }

    private List<String> getTagsList()
    {
        List<String> tagList = new ArrayList<>();
        try
        {
            List<WebElement> listOfTags = drone.findAndWaitForElements(TAG);
            for (WebElement listOfTheTags : listOfTags)
            {
                tagList.add(listOfTheTags.getText());
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find the list of tags", te);
        }
        return tagList;
    }

    /**
     * Method to verify whether the post is correct
     *
     * @param postTitle
     * @param postText
     * @param tags
     * @return
     */
    public boolean isPostCorrect(String postTitle, String postText, List<String> tags)
    {
        boolean isCorrect;
        if (tags == null)
        {
            boolean isTagNull = drone.isElementDisplayed(TAG_NONE);
            isCorrect = verifyPostExists(postTitle) && getPostText().contentEquals(postText) && isTagNull;
        }
        else
        {
            isCorrect = verifyPostExists(postTitle) && getPostText().contentEquals(postText) && getTagsList().containsAll(tags);
        }
        return isCorrect;
    }

    private String getPostText()
    {
        try
        {
            String postText;
            postText = drone.findAndWait(POST_TEXT).getText();
            return postText;
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + POST_TEXT, te);
        }
    }

    /**
     * Method to click on the tag link
     *
     * @param tagName
     * @return
     */
    public BlogPage clickOnTheTag(String tagName)
    {
        try
        {
            boolean isTagNone = drone.isElementDisplayed(TAG_NONE);
            if (isTagNone)
            {
                throw new PageOperationException("There is no tags!");
            }
            else
            {
                List<WebElement> availableTags = drone.findAll(TAG);
                for (WebElement eachTag : availableTags)
                {
                    if (eachTag.getText().contentEquals(tagName))
                    {
                        eachTag.click();
                        drone.waitForPageLoad(5);
                        break;
                    }
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Not able to find the tags", nse);
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Check is comment has Avatar, Edit and Remove buttons, Commentator name.
     *
     * @param comment
     * @return
     */
    public boolean isCommentCorrect(String comment)
    {
        try
        {
            checkNotNull(comment);
            return isCommentButtonsEnableAndDisplay(comment) && isCommentAvatarDisplay(comment) && isCommentatorNameDisplayAndEnable(comment);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (PageException pe)
        {
            return false;
        }
    }

    private boolean isCommentButtonsEnableAndDisplay(String comment)
    {
        CommentDirectoryInfo commentElement = getCommentDirectoryInfo(comment);
        WebElement edit = commentElement.findElement(By.name(".onEditCommentClick"));
        WebElement delete = commentElement.findElement(By.name(".onConfirmDeleteCommentClick"));
        return edit.isEnabled() && delete.isEnabled() && edit.isDisplayed() && delete.isDisplayed();
    }

    private boolean isCommentAvatarDisplay(String comment)
    {
        CommentDirectoryInfo commentElement = getCommentDirectoryInfo(comment);
        return commentElement.isAvatarDisplayed();
    }

    private boolean isCommentatorNameDisplayAndEnable(String comment)
    {
        CommentDirectoryInfo commentElement = getCommentDirectoryInfo(comment);
        WebElement commentatorName = commentElement.findElement(By.xpath("//a[contains(@href,'profile')]"));
        return commentatorName.isDisplayed() && commentatorName.isEnabled();
    }

    /**
     * Method to retrieve Post Status (Draft, Updated, Out of sync)
     *
     * @return List<String>
     */
    public List<String> getPostStatus()
    {
        String status;
        String charsToRemove = ")(";
        List<String> postStatus = new ArrayList<>();
        try
        {
            status = drone.find(POST_STATUS).getText();
            String[] parts = status.split("\\) ");

            for (String thePart : parts)
            {
                thePart = CharMatcher.anyOf(charsToRemove).removeFrom(thePart);
                postStatus.add(thePart);
            }
            if (postStatus.isEmpty())
            {
                throw new ShareException("The post has not status");
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find " + POST_STATUS, nse);
        }
        return postStatus;
    }

    /**
     * Method to click Update Externally button
     *
     * @return PostViewPage
     */
    public PostViewPage clickUpdateExternally()
    {
        try
        {
            WebElement updateExternallyButton = drone.findAndWait(UPDATE_EXTERNALLY);
            updateExternallyButton.click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + UPDATE_EXTERNALLY, te);
        }
        logger.info("Updated post externally");
        return drone.getCurrentPage().render();
    }

    /**
     * Method to click Remove Externally button
     *
     * @return PostViewPage
     */
    public PostViewPage clickRemoveExternally()
    {
        try
        {
            WebElement removeExternallyButton = drone.findAndWait(REMOVE_EXTERNALLY);
            removeExternallyButton.click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + REMOVE_EXTERNALLY, te);
        }
        logger.info("Removed post from external blog");
        return drone.getCurrentPage().render();
    }

    /**
     * Verify if error message is displayed.
     *
     * @return true if div.bd is displayed
     */
    public boolean hasErrorMessage()
    {
        try
        {
            return drone.find(By.cssSelector("div.bd")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    public String getErrorMessage()
    {
        return drone.find(By.xpath(".//*[@id='message']/div/span")).getText();
    }
}
