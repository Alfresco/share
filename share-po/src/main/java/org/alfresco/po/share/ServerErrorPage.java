package org.alfresco.po.share;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public class ServerErrorPage extends SharePage
{
    private static final By RETURN_LINK = By.xpath("//a[@href='/share']");
    private static final By ERROR_MESSAGE = By.xpath("//p[contains (text(),'A server error has occurred.')]");


    @SuppressWarnings("unchecked")
    @Override
    public ServerErrorPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
                getVisibleRenderElement(RETURN_LINK),
                getVisibleRenderElement(ERROR_MESSAGE)
        );
        return this;
    }


    @SuppressWarnings("unchecked")
    @Override
    public ServerErrorPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


}
