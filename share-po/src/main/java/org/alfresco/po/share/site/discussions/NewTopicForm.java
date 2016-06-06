package org.alfresco.po.share.site.discussions;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;

/**
 * Add Event form page object
 * relating to Share site Calendar page
 *
 * @author Marina Nenadovets
 */

public class NewTopicForm extends AbstractTopicForm
{

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
}
