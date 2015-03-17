package org.alfresco.po.share.site.blog;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Add Blog comment page object relating to Share Blog page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class BlogCommentForm extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private final TinyMceEditor tinyMceEditor;
    private static final By CANCEL_BTN = By.cssSelector(".buttons>span[class*=yui-reset-button]");

    private static final By ADD_COMMENT_BUTTON = By.cssSelector("button[id$='submit-button']");

    public BlogCommentForm(WebDrone drone)
    {
        super(drone);
        tinyMceEditor = new TinyMceEditor(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogCommentForm render(RenderTime timer)
    {
        return this;
    }

    @SuppressWarnings("unchecked")
    public BlogCommentForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogCommentForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click Add Comment button
     *
     * @return Post View page object
     */
    public PostViewPage clickAddComment()
    {
        try
        {
            WebElement saveButton = getVisibleElement(By.cssSelector("button[id$='submit-button']"));
            saveButton.click();
            waitUntilAlert();
            return new PostViewPage(drone).render();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + ADD_COMMENT_BUTTON, nse);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding " + ADD_COMMENT_BUTTON, te);
        }
    }

    /**
     * Method to insert text into Comment field
     *
     * @param comment
     */
    public void insertText(String comment)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", comment);
            drone.executeJavaScript(setCommentJs);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding #tinymce", toe);
        }
    }
}
