package org.alfresco.po.thirdparty.facebook;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class FacebookSingInPage extends Page
{
    private static final By EMAIL_INPUT = By.xpath("//input[@id='email']");
    private static final By PASSWORD_INPUT = By.xpath("//input[@type='password']");
    private static final By LOGIN_BUTTON = By.xpath("//input[@type='submit']");


    @SuppressWarnings("unchecked")
    @Override
    public FacebookSingInPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(EMAIL_INPUT), getVisibleRenderElement(PASSWORD_INPUT), getVisibleRenderElement(LOGIN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FacebookSingInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void login(String username, String password)
    {
        fillField(EMAIL_INPUT, username);
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
