package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Marina.Nenadovets
 */
public class TopicDirectoryInfoImpl extends HtmlElement implements TopicDirectoryInfo
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By VIEW_LINK = By.cssSelector(".onViewTopic>a");
    private static final By EDIT_TOPIC = By.cssSelector(".onEditTopic>a");
    private static final By DELETE_TOPIC = By.cssSelector(".onDeleteTopic>a");
    private static final By REPLIES_COUNT = By.xpath(".//div[@class='nodeFooter']//span[2]");
    private static final By READ_LINK = By.xpath(".//div[@class='nodeFooter']/span[4]/a");
    private static final By TAG_LINKS = By.xpath(".//div[@class='nodeFooter']//span[@class='tag']/a");

    /**
     * Constructor
     */
    protected TopicDirectoryInfoImpl(WebDrone drone, WebElement webElement)
    {
        super(webElement, drone);
    }

    /**
     * Method to view the topic from Discussion list page
     *
     * @return TopicViewPage
     */
    public TopicViewPage viewTopic()
    {
        try
        {

            findAndWait(VIEW_LINK).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new TopicViewPage(drone);
    }

    /**
     * Method to view the topic from Discussion list page
     *
     * @return TopicViewPage
     */
    public NewTopicForm editTopic()
    {
        try
        {
            findAndWait(EDIT_TOPIC).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new NewTopicForm(drone);
    }

    /**
     * Method to delete a topic
     *
     * @return Discussions Page
     */
    public DiscussionsPage deleteTopic()
    {
        try
        {
            findAndWait(DELETE_TOPIC).click();
            drone.findAndWait(By.xpath("//span[@class='button-group']/span[1]/span/button")).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find View button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new DiscussionsPage(drone).waitUntilAlert().render();
    }

    /**
     * Method to verify whether edit topic is displayed
     *
     * @return boolean
     */
    public boolean isEditTopicDisplayed()
    {
        try
        {
            WebElement editLink = findElement(EDIT_TOPIC);
            return editLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to verify whether edit topic is displayed
     *
     * @return boolean
     */
    public boolean isDeleteTopicDisplayed()
    {
        try
        {
            WebElement deleteLink = findElement(DELETE_TOPIC);
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * @see org.alfresco.po.share.site.discussions.TopicDirectoryInfo
     */
    @Override
    public int getRepliesCount()
    {
        String countAsString = findAndWait(REPLIES_COUNT).getText();
        int count = Integer.parseInt(countAsString.replace("(", "").replace(")", ""));
        return count;
    }

    /**
     * @see org.alfresco.po.share.site.discussions.TopicDirectoryInfo
     */
    @Override
    public TopicDetailsPage clickRead()
    {
        findAndWait(READ_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * @see org.alfresco.po.share.site.discussions.TopicDirectoryInfo
     */
    @Override
    public DiscussionsPage clickOnTag(String tagName)
    {
        List<WebElement> elements = findAllWithWait(TAG_LINKS);
        for (WebElement element : elements)
        {
            if (tagName.equals(element.getText()))
            {
                element.click();
                return new DiscussionsPage(drone).waitUntilAlert().render();
            }
        }
        throw new PageException(String.format("Tag[%s] don't found.", tagName));
    }

}
