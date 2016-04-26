package org.alfresco.po.thirdparty.wordpress;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */

public class WordPressSignInPage extends Page
{
    private static final By USER_LOGIN = By.cssSelector("#user_login");
    private static final By USER_PASS = By.cssSelector("#user_pass");
    private static final By SUBMIT_BTN = By.cssSelector("#wp-submit");

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
    public WordPressSignInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public WordPressUserPage login(String userName, String password)
    {

        fillField(USER_LOGIN, userName);
        fillField(USER_PASS, password);
        click(SUBMIT_BTN);
        waitUntilElementPresent(By.cssSelector(".reblog"), 5);
        return  factoryPage.instantiatePage(driver, WordPressUserPage.class).render();
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
