package org.alfresco.po.thirdparty.flickr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class YahooSignInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='username']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='passwd']");
    private final static By LOGIN_BUTTON = By.xpath("//button[@id='.save']");
    private final static By SIGN_IN_CHECKBOX = By.xpath("//span[@id='pLabelC']");

    @SuppressWarnings("unchecked")
    @Override
    public YahooSignInPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(USERNAME_INPUT),
                getVisibleRenderElement(PASSWORD_INPUT),
                getVisibleRenderElement(LOGIN_BUTTON),
                getVisibleRenderElement(SIGN_IN_CHECKBOX));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public YahooSignInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public FlickrUserPage login(String userName, String password)
    {

        fillField(USERNAME_INPUT, userName);
        fillField(PASSWORD_INPUT, password);
        click(SIGN_IN_CHECKBOX);
        click(LOGIN_BUTTON);
        return  factoryPage.instantiatePage(driver, FlickrUserPage.class).render();
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
