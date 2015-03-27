package org.alfresco.po.share.site.links;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object to represent Add Link Form
 *
 * @author Marina.Nenadovets
 */
public class AddLinkForm extends AbstractLinkForm
{
    private static final By SAVE_BTN = By.cssSelector("button[id$='default-ok-button']");

    /**
     * Constructor
     *
     * @param drone
     */
    public AddLinkForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    public AddLinkForm render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(SAVE_BTN),
                getVisibleRenderElement(CANCEL_BTN));

        return this;
    }

    @SuppressWarnings("unchecked")
    public AddLinkForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    public AddLinkForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for clicking Save button on Add Link form
     *
     * @return
     */
    public LinksDetailsPage clickSaveBtn()
    {
        drone.findAndWait(SAVE_BTN).click();
        return new LinksDetailsPage(drone).waitUntilAlert().render();
    }
}
