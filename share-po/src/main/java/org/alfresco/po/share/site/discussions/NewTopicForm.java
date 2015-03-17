package org.alfresco.po.share.site.discussions;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Add Event form page object
 * relating to Share site Calendar page
 *
 * @author Marina Nenadovets
 */

public class NewTopicForm extends AbstractTopicForm
{

    public NewTopicForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewTopicForm render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(TITLE_FIELD),
            getVisibleRenderElement(SAVE_BUTTON),
            getVisibleRenderElement(CANCEL_BUTTON));

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewTopicForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewTopicForm render(long time)
    {
        return render(new RenderTime(time));
    }
}
