package org.alfresco.po.thirdparty.twitter;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class TwitterSingInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='username_or_email']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='password']");
    private final static By LOGIN_BUTTON = By.xpath("//input[@value='Authorize app']");

    @SuppressWarnings("unchecked")
    @Override
    public TwitterSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LOGIN_BUTTON));
        return this;
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
        WebElement loginButton = findAndWait(LOGIN_BUTTON);
        loginButton.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }
}
