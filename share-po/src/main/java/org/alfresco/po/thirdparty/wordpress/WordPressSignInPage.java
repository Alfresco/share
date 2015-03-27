package org.alfresco.po.thirdparty.wordpress;

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
import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Marina.Nenadovets
 */

public class WordPressSignInPage extends Page
{
    private static final By USER_LOGIN = By.cssSelector("#user_login");
    private static final By USER_PASS = By.cssSelector("#user_pass");
    private static final By SUBMIT_BTN = By.cssSelector("#wp-submit");

    public WordPressSignInPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressSignInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(USER_LOGIN),
            getVisibleRenderElement(USER_PASS),
            getVisibleRenderElement(SUBMIT_BTN));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressSignInPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressSignInPage render()
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

    public WordPressUserPage login(String userName, String password)
    {

        fillField(USER_LOGIN, userName);
        fillField(USER_PASS, password);
        click(SUBMIT_BTN);
        drone.waitUntilElementPresent(By.cssSelector(".reblog"), 5);
        return new WordPressUserPage(drone).render();
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
}
