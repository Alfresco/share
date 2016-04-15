package org.alfresco.po.thirdparty.linkedin;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class LinkedInSingInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='session_key-oauthAuthorizeForm']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='session_password-oauthAuthorizeForm']");
    private final static By ALLOW_ACCESS_BUTTON = By.xpath("//input[@value='Allow access']");

    @SuppressWarnings("unchecked")
    @Override
    public LinkedInSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(USERNAME_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(ALLOW_ACCESS_BUTTON));
        return this;
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
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }
}
