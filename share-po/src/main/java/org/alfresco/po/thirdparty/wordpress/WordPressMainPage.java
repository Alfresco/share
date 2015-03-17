package org.alfresco.po.thirdparty.wordpress;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.Page;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Marina.Nenadovets
 */

public class WordPressMainPage extends Page
{
    private static final By LOG_IN_BUTTON = By.cssSelector(".login>a");

    public WordPressMainPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LOG_IN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    //TODO Temporary method. Before elementRender from SharePage didn't moved out to Page.
    private void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        for (RenderElement element : elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(drone, waitSeconds);
            }
            catch (TimeoutException e)
            {
                throw new PageRenderTimeException("element not rendered in time.");
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }

    public WordPressSignInPage clickLogIn()
    {
        try
        {
            WebElement loginBtn = drone.findAndWait(LOG_IN_BUTTON);
            loginBtn.click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the " + LOG_IN_BUTTON);
        }
        return new WordPressSignInPage(drone).render();
    }
}
