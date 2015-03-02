package org.alfresco.po.share.site.blog;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * New Post Form page object
 *
 * @author Marina.Nenadovets
 */
public class NewPostForm extends AbstractPostForm
{
    protected static final By PUBLISH_INTERNALLY = By.cssSelector("button[id$='default-publish-button-button']");

    public NewPostForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewPostForm render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(TITLE_FIELD),
            getVisibleRenderElement(DEFAULT_SAVE),
            getVisibleRenderElement(PUBLISH_INTERNALLY),
            getVisibleRenderElement(CANCEL_BTN));

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewPostForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewPostForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for clicking Publish Internally button
     */
    protected PostViewPage clickPublishInternally()
    {
        WebElement saveButton = drone.findAndWait(PUBLISH_INTERNALLY);
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

}
