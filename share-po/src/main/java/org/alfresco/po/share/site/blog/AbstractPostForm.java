package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.SharePage;
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

/**
 * Abstract of Blog post Form
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractPostForm extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final String POST_FORMAT_IFRAME = ("template_x002e_postedit_x002e_blog-postedit_x0023_default-content_ifr");
    protected static final By TITLE_FIELD = By.cssSelector("#template_x002e_postedit_x002e_blog-postedit_x0023_default-title");
    protected static final By CANCEL_BTN = By.cssSelector("#template_x002e_postedit_x002e_blog-postedit_x0023_default-cancel-button-button");
    protected static final By DEFAULT_SAVE = By.cssSelector("button[id$='default-save-button-button']");
    protected static final By DEFAULT_PUBLISH = By.cssSelector("button[id$='default-publish-button-button']");
    protected static final By PUBLISH_INTERNALLY_EXTERNALLY = By.cssSelector("button[id$='default-publishexternal-button-button']");
    private static final By POST_TAG_INPUT = By.cssSelector("#template_x002e_postedit_x002e_blog-postedit_x0023_default-tag-input-field");
    private static final By ADD_TAG_BUTTON = By.cssSelector("#template_x002e_postedit_x002e_blog-postedit_x0023_default-add-tag-button-button");
    protected static final String POST_TAG = "//a[@class='taglibrary-action']/span[text()='%s']";

    protected AbstractPostForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public AbstractPostForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    public AbstractPostForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to set String input in the field
     *
     * @param input
     * @param value
     */

    private void setInput(final WebElement input, final String value)
    {
        input.clear();
        input.sendKeys(value);
    }

    /**
     * Method for inserting text into the title field
     *
     * @param title
     */
    protected void setTitleField(final String title)
    {
        try
        {
            setInput(drone.findAndWait(TITLE_FIELD), title);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + TITLE_FIELD, te);
        }
    }

    /**
     * Method for inserting text into the text field
     *
     * @param txtLines
     */
    public void insertText(String txtLines)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txtLines);
            drone.executeJavaScript(setCommentJs);
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method for clicking Save as Draft button
     */
    protected PostViewPage clickSaveAsDraft()
    {
        try
        {
            WebElement saveButton = drone.findAndWait(DEFAULT_SAVE);
            saveButton.click();
            return new PostViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find Save button", te);
        }
    }

    /**
     * Method for clicking Save as Draft button
     */
    protected PostViewPage clickPublishInternally()
    {
        WebElement saveButton = drone.findAndWait(DEFAULT_PUBLISH);
        try
        {
            saveButton.click();
            return new PostViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find Save button", te);
        }
    }

    protected PostViewPage clickUpdateInternallyPublishExternally()
    {
        WebElement saveButton = drone.findAndWait(PUBLISH_INTERNALLY_EXTERNALLY);
        try
        {
            saveButton.click();
            return new PostViewPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find Save button", te);
        }
    }

    /**
     * Method to add tag to the new blog post page
     *
     * @param tags
     * @return NewPostForm object
     */
    public NewPostForm addTag(List<String> tags)
    {
        String tagsToAdd = "";
        checkNotNull(tags);
        try
        {
            WebElement inputTag = drone.findAndWait(POST_TAG_INPUT);
            for (String tagToAdd : tags)
            {
                tagsToAdd += tagToAdd + " ";
            }
            inputTag.sendKeys(tagsToAdd);
            WebElement addButton = drone.find(ADD_TAG_BUTTON);
            addButton.click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Unable to find " + POST_TAG_INPUT, te);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find " + ADD_TAG_BUTTON, nse);
        }
        return new NewPostForm(drone);
    }

    /**
     * Method to add tag to the new blog post page
     *
     * @param tag
     * @return NewPostForm object
     */
    public NewPostForm addTag(String tag)
    {
        checkNotNull(tag);
        WebElement inputTag = drone.findAndWait(POST_TAG_INPUT);
        inputTag.sendKeys(tag);
        WebElement addButton = drone.find(ADD_TAG_BUTTON);
        addButton.click();
        return new NewPostForm(drone);
    }

    /**
     * Method for removing tag
     * method validate by BlogPageTest.removeTag
     *
     * @param tag
     */
    protected void removeTag(String tag)
    {
        String tagXpath = String.format(POST_TAG, tag);
        WebElement element;
        try
        {
            element = drone.findAndWait(By.xpath(tagXpath));
            element.click();
            drone.waitUntilElementDisappears(By.xpath(tagXpath), 3000);
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find tag");
            throw new PageOperationException("Unable to find tag " + tag + "", nse);
        }
    }
}