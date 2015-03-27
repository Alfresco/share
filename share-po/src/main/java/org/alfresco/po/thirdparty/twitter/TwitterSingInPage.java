package org.alfresco.po.thirdparty.twitter;

import org.alfresco.webdrone.*;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Olga Antonik
 */
public class TwitterSingInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='username_or_email']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='password']");
    private final static By LOGIN_BUTTON = By.xpath("//input[@value='Authorize app']");

    public TwitterSingInPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TwitterSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LOGIN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TwitterSingInPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TwitterSingInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void login(String username, String password)
    {
        fillField(USERNAME_INPUT, username);
        fillField(PASSWORD_INPUT, password);
        WebElement loginButton = drone.findAndWait(LOGIN_BUTTON);
        loginButton.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }

    // TODO Temporary method. Before elementRender from SharePage didn't moved out to Page.
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
}
