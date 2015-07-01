package org.alfresco.po.share.site.links;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to reflect Links Details page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class LinksDetailsPage extends SharePage
{
    private static final By TITLE = By.cssSelector(".nodeTitle>a");
    private static final By LINKS_LIST_LINK = By.cssSelector("span[class*='link']>a");
    private static final By COMMENT_LINK = By.cssSelector(".onAddCommentClick");
    private static final By EDIT_LINK = By.cssSelector(".onEditLink>a");
    private static final By DELETE_LINK = By.cssSelector(".onDeleteLink>a");
    private static final By TAG = By.cssSelector("a[class='tag-link']");
    private static final By TAG_NONE = By.xpath("//span[@class='nodeAttrValue' and text()='(None)']");
    private static final By CONFIRM_DELETE_BUTTON = By.xpath("//span[@class='button-group']/span[1]//button");

    private static final By LINK_COMMENTS = By.cssSelector("div[id$='-comment-container']");

    private static final By LINK_TITLE = By.cssSelector(".nodeTitle>a");
    private static final By LINK_DESCRIPTION = By.xpath("//div[@class='detail'][2]/span[2]");
    private static final By LINK_TAGS = By.xpath("//a[@class='tag-link']");
    private static final By LINK_ITSELF = By.xpath("//div[@class='nodeURL']/a");

    /**
     * Constructor
     *
     * @param drone WebDrone
     */
    public LinksDetailsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public LinksDetailsPage render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(LINKS_LIST_LINK),
            getVisibleRenderElement(COMMENT_LINK));

        return this;
    }

    @SuppressWarnings("unchecked")
    public LinksDetailsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    public LinksDetailsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to browse to links list
     *
     * @return Links Page
     */
    public LinksPage browseToLinksList()
    {
        try
        {
            drone.findAndWait(LINKS_LIST_LINK).click();
            return new LinksPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + LINKS_LIST_LINK);
        }
    }

    /**
     * Method to retrieve tag added to Link
     *
     * @return String
     */
    public String getTagName()
    {
        try
        {
            if (!drone.isElementDisplayed(TAG_NONE))
            {
                String tagName = drone.findAndWait(TAG).getAttribute("title");
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
     * Return link url
     *
     * @return String
     */
    public String getUrl()
    {
        return drone.findAndWait(LINK_ITSELF).getText();
    }

    /**
     * Return link title
     *
     * @return String
     */
    public String getTitle()
    {
        return drone.findAndWait(LINK_TITLE).getText();
    }

    /**
     * Return link description
     *
     * @return String
     */
    public String getDescription()
    {
        return drone.findAndWait(LINK_DESCRIPTION).getText();
    }

    /**
     * Mimic edit link.
     *
     * @param url String
     * @param title String
     * @param description String
     * @param tag String
     */
    public void editLink(String url, String title, String description, String tag)
    {
        editLink(url, title, description, tag, false);
    }

    /**
     * Mimic edit link (remove tag or add).
     * set true if you want remove tag
     * set false if you want add tag
     * method validate by LinksPageTest.removeTags
     *
     * @param url String
     * @param title String
     * @param description String
     * @param tag String
     * @param removeTag boolean
     */
    public void editLink(String url, String title, String description, String tag, boolean removeTag)
    {
        checkNotNull(url);
        checkNotNull(title);
        checkNotNull(description);
        checkNotNull(tag);
        drone.findAndWait(EDIT_LINK).click();
        AddLinkForm addLinkForm = new AddLinkForm(drone);
        addLinkForm.setUrlField(url);
        addLinkForm.setTitleField(title);
        addLinkForm.setDescriptionField(description);
        if (!removeTag) {
            addLinkForm.addTag(tag);
        } else {
            addLinkForm.removeTag(tag);
        }
        addLinkForm.clickSaveBtn();
    }

    /**
     * Click on link reference.
     */
    public void clickOnLinkUrl()
    {
        drone.findAndWait(LINK_ITSELF).click();
    }

    /**
     * Mimic click on tag
     */
    public LinksPage clickOnTag()
    {
        try
        {
            if (!drone.isElementDisplayed(TAG_NONE))
            {
                WebElement tagElement = drone.findAndWait(TAG);
                String tagName = tagElement.getAttribute("title");
                if (!tagName.isEmpty())
                {
                    tagElement.click();
                    return new LinksPage(drone).waitUntilAlert().render();
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the tag");
        }
        throw new IllegalArgumentException("Cannot find tag");
    }

    /**
     * Mimic add comment to link.
     *
     * @param comment String
     */
    public void addComment(String comment)
    {
        checkNotNull(comment);
        drone.findAndWait(COMMENT_LINK).click();
        AddCommentLinkForm addCommentLinkForm = new AddCommentLinkForm(drone);
        addCommentLinkForm.insertText(comment);
        addCommentLinkForm.clickSubmit();
    }

    /**
     * Return object related to comment block.
     *
     * @param text String
     * @return LinkComment
     */
    public LinkComment getLinkComment(String text)
    {
        checkNotNull(text);
        try
        {
            List<WebElement> commentBaseElements = drone.findAndWaitForElements(LINK_COMMENTS);
            for (WebElement baseElement : commentBaseElements)
            {
                LinkComment linkComment = new LinkComment(baseElement, drone);
                String commentText = linkComment.getText();
                if (commentText.equals(text))
                {
                    return linkComment;
                }
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException(String.format("Can't find any comment on page.", text));
        }
        throw new PageOperationException(String.format("Can't find comment with text[%s].", text));
    }

    public LinksPage deleteLink()
    {
        drone.findAndWait(DELETE_LINK).click();
        drone.findAndWait(CONFIRM_DELETE_BUTTON).click();
        return new LinksPage(drone).waitUntilAlert().render();
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
    
    public String getLinkTitle()
    {
        try
        {
            return drone.findAndWait(By.cssSelector(".nodeTitle>a")).getText();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find the links title", te);
        }
    }

}
