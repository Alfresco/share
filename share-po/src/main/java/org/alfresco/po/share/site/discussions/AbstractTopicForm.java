package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

/**
 * Abstract of Topic form
 *
 * @author Marina Nenadovets
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTopicForm extends SharePage
{

    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By DEFAULT_CONTENT_TOOLBAR = By.cssSelector("div[id$='default-content_toolbargroup']>span");
    protected static final By CANCEL_BUTTON = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-cancel-button");
    protected static final By FORM_TITLE = By.cssSelector(".page-form-header>h1");
    protected static final By TITLE_FIELD = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-title");
    protected static final String TOPIC_FORMAT_IFRAME = ("template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-content_ifr");
    protected static final By SAVE_BUTTON = (By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-submit-button"));
    protected static final By TAG_INPUT = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-tag-input-field");
    protected static final By ADD_TAG_BUTTON = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-add-tag-button");
    protected static final String TOPIC_TAG = "//a[@class='taglibrary-action']/span[text()='%s']";

    protected AbstractTopicForm(WebDrone drone)
    {
        super(drone);
    }

    public AbstractTopicForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public AbstractTopicForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Check if topic form is displayed or not.
     *
     * @return
     */
    protected boolean isTopicFormDisplayed()
    {
        try
        {
            return drone.findAndWait(CANCEL_BUTTON).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out finding " + CANCEL_BUTTON.toString(), toe);
            }
        }
        catch (ElementNotVisibleException visibleException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Element Not Visible: " + CANCEL_BUTTON.toString(), visibleException);
            }
        }
        return false;
    }

    /**
     * Check content tool bar is displayed.
     *
     * @return true if displayed
     */
    protected boolean isTinyMCEDisplayed()
    {
        try
        {
            return drone.findAndWait(DEFAULT_CONTENT_TOOLBAR).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + DEFAULT_CONTENT_TOOLBAR.toString(), toe);
        }
        throw new PageException("Page is not rendered");
    }

    /**
     * Method to retrieve the title of a form
     *
     * @return String
     */

    public String getTitle()
    {
        try
        {
            return drone.findAndWait(FORM_TITLE).getText();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find page title");
        }
        throw new PageException("Page is not rendered");
    }

    /**
     * Method to set String input in the field
     *
     * @param input
     * @param value
     */

    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find " + input);
        }
    }

    /**
     * Method to click on any element by its locator
     *
     * @param locator
     */
    protected void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    /**
     * Method to check if the element is displayed
     *
     * @param locator
     * @return boolean
     */

    protected boolean isDisplayed(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.error("The " + locator + " isn't displayed");
        }
        return false;
    }

    /**
     * Method to set Title field
     *
     * @param title
     */
    public void setTitleField(final String title)
    {
        setInput(drone.findAndWait(TITLE_FIELD), title);
    }

    /**
     * Insert text in topic text area.
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
            logger.error("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method for clicking Save button
     */
    public void clickSave()
    {
        WebElement saveButton = drone.findAndWait(SAVE_BUTTON);
        try
        {
            saveButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find Save button");
        }
    }

    /**
     * Method to add tag
     *
     * @param tag
     */
    protected void addTag(final String tag)
    {
        try
        {
            WebElement tagField = drone.findAndWait(TAG_INPUT);
            tagField.clear();
            tagField.sendKeys(tag);
            drone.find(ADD_TAG_BUTTON).click();
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find tag input");
        }
    }

    /**
     * Method for removing tag
     * method validate by DiscussionsPageTest.removeTags
     *
     * @param tag
     */
    protected void removeTag(String tag)
    {
        String tagXpath = String.format(TOPIC_TAG, tag);
        WebElement element;
        try
        {
            element = drone.findAndWait(By.xpath(tagXpath));
            element.click();
            drone.waitUntilElementDisappears(By.xpath(tagXpath), 3000);
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find tag");
            throw new PageException("Unable to find tag " + tag + "");
        }
    }
}
