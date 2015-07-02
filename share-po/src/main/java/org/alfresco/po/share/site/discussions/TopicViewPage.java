package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Topic View page object
 * relating to Share topic view page
 *
 * @author Marina Nenadovets
 */
public class TopicViewPage extends DiscussionsPage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By REPLY_LINK = By.cssSelector(".onAddReply>a");
    private static final By BACK_LINK = By.cssSelector(".backLink>a");
    private static final By REPLY_CONTAINER = By.cssSelector(".reply");
    private static final By TAG = By.cssSelector(".tag-link");
    private static final By TAG_NONE = By.xpath("//span[@class='nodeAttrValue' and text()='(None)']");

    private static final By TOPIC_TITLE = By.xpath("//div[@class='nodeTitle']/a");
    private static final By TOPIC_TEXT = By.xpath("//div[contains(@class,'topicview')]//p");
    private static final By TOPIC_TAGS = By.xpath("//span[@class='tag']/a");
    private static final By TOPIC_REPLIES = By.xpath("//div/div[@class='reply']//p");

    /**
     * Constructor
     *
     * @param drone WebDrone
     */
    public TopicViewPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicViewPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(BACK_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    public TopicViewPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicViewPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for clicking Back button from Topic View page
     *
     * @return Discussions Page object
     */
    public DiscussionsPage clickBack()
    {
        try
        {
            drone.findAndWait(BACK_LINK).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find Back Link");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new DiscussionsPage(drone);
    }

    /**
     * Method for clicking Reply button on topic view page
     *
     * @return AddReplyForm page object
     */
    private AddReplyForm clickReply()
    {
        try
        {
            drone.findAndWait(REPLY_LINK).click();
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new AddReplyForm(drone);
    }

    /**
     * Method for creating a reply
     *
     * @param replyText String
     * @return TopicViewPage
     */
    public TopicViewPage createReply(String replyText)
    {
        try
        {
            clickReply();
            waitUntilAlert();
            AddReplyForm addReplyForm = new AddReplyForm(drone);
            addReplyForm.insertText(replyText);
            addReplyForm.clickSubmit().render();
            waitUntilAlert();
            logger.info("Created a reply " + "'" + replyText + "'");
            return new TopicViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * Method to verify whether reply link is available
     *
     * @return true if displayed
     */
    public boolean isReplyLinkDisplayed()
    {
        try
        {
            return drone.findAndWait(REPLY_LINK, 2000).isDisplayed();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    /**
     * Return information about replay with text.
     *
     * @param title String
     * @return ReplyDirectoryInfo
     */
    public ReplyDirectoryInfo getReplyDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//div[@class='nodeContent']/div[2]/p[text()='%s']/../../..", title)), WAIT_TIME_3000);
        }
        catch (TimeoutException te)
        {
            throw new ShareException(String.format("File directory info with title %s was not found", title), te);
        }
        return new ReplyDirectoryInfo(drone, row);
    }

    /**
     * Method to edit a reply
     *
     * @param title String
     * @param replyText String
     * @return Topic View Page
     */
    public TopicViewPage editReply(String title, String replyText)
    {
        getReplyDirectoryInfo(title).clickEdit();
        AddReplyForm addReplyForm = new AddReplyForm(drone);
        addReplyForm.insertText(replyText);
        addReplyForm.clickSubmit().render();
        logger.info("Reply was edited");
        return new TopicViewPage(drone).render();
    }

    public TopicViewPage deleteReply(String title)
    {
        getReplyDirectoryInfo(title).clickDelete();
        logger.info("Reply was deleted");
        return drone.getCurrentPage().render();
    }

    /**
     * Method to get the number of replies
     *
     * @return number of replies
     */
    public int getReplyCount()
    {
        try
        {
            if (!drone.isElementDisplayed(REPLY_CONTAINER))
            {
                return 0;
            }
            return drone.findAll(REPLY_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + REPLY_CONTAINER);
        }
    }

    public boolean isEditReplyDisplayed(String reply)
    {
        boolean isDisplayed = getReplyDirectoryInfo(reply).isEditDisplayed();
        return isDisplayed;
    }

    public boolean isDeleteReplyDisplayed(String reply)
    {
        boolean isDisplayed = getReplyDirectoryInfo(reply).isDeleteDisplayed();
        return isDisplayed;
    }

    /**
     * Method to retrieve tag added to Discussion Topic
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
                    return tagName;
                else
                    throw new IllegalArgumentException("Cannot find tag");

            }
            else
                return drone.find(TAG_NONE).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
    }

    /**
     * Method return topic title text
     *
     * @return String
     */
    public String getTopicTitle()
    {
        return drone.findAndWait(TOPIC_TITLE).getText();
    }

    /**
     * Method return topic body text
     *
     * @return String
     */
    public String getTopicText()
    {
        return drone.findAndWait(TOPIC_TEXT).getText();
    }

    /**
     * click on topic tag
     *
     * @param tagName String
     * @return discussionsPage
     */
    public DiscussionsPage clickOnTag(String tagName)
    {
        getElementWithText(TOPIC_TAGS, tagName).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Return true if this topic has reply with text
     *
     * @param replyText String
     * @return boolean
     */
    public boolean isReplyDisplay(String replyText)
    {
        try
        {
            return getElementWithText(TOPIC_REPLIES, replyText).isDisplayed();
        }
        catch (PageException e)
        {
            return false;
        }
    }

    private WebElement getElementWithText(By selector, String text)
    {
        checkNotNull(text);
        checkNotNull(selector);
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(selector);
            for (WebElement element : elements)
            {
                if (element.getText().contains(text))
                {
                    return element;
                }
            }
        }
        catch (StaleElementReferenceException e)
        {
            getElementWithText(selector, text);
        }
        throw new PageException(String.format("Element with selector[%s] and text[%s] not found on page.", selector, text));
    }

}
