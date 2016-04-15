package org.alfresco.po.share;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Change password page object, holds all element of the html page relating to
 * share's my profile change password page.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class ChangePasswordPage extends SharePage
{
    private static final By CHANGE_PASSWORD = By.cssSelector("div[id$=change-password]");
    private final static By OLD_PASSWORD = By.cssSelector ("input[id$='-oldpassword']");
    private final static By NEW_PASSWORD = By.cssSelector ("input[id$='-newpassword1']");
    private final static By CONFIRM_NEW_PASSWORD = By.cssSelector ("input[id$='-newpassword2']");
    private final static By OK_BUTTON = By.cssSelector ("button[id$='-button-ok-button']");

    @SuppressWarnings("unchecked")
    @Override
    public ChangePasswordPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChangePasswordPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public boolean formPresent()
    {
        return isDisplayed(CHANGE_PASSWORD);
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }
    public HtmlPage changePassword (String oldPassword, String newPassword)
    {
        fillField(OLD_PASSWORD, oldPassword);
        fillField(NEW_PASSWORD, newPassword);
        fillField(CONFIRM_NEW_PASSWORD, newPassword);
        click(OK_BUTTON);
        return getCurrentPage();
    }
}
