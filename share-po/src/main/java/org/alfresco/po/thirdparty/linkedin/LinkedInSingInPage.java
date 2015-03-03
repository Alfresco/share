package org.alfresco.po.thirdparty.linkedin;

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
public class LinkedInSingInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='session_key-oauthAuthorizeForm']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='session_password-oauthAuthorizeForm']");
    private final static By ALLOW_ACCESS_BUTTON = By.xpath("//input[@value='Allow access']");

    public LinkedInSingInPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedInSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(USERNAME_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(ALLOW_ACCESS_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedInSingInPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedInSingInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void login(String userName, String password)
    {

        fillField(USERNAME_INPUT, userName);
        fillField(PASSWORD_INPUT, password);
        click(ALLOW_ACCESS_BUTTON);
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
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
