package org.alfresco.po.thirdparty.wordpress;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.exception.ShareException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */

public class WordPressMainPage extends Page
{
    private static final By LOG_IN_BUTTON = By.cssSelector(".login>a");

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LOG_IN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public WordPressSignInPage clickLogIn()
    {
        try
        {
            WebElement loginBtn = findAndWait(LOG_IN_BUTTON);
            loginBtn.click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the " + LOG_IN_BUTTON);
        }
        return factoryPage.instantiatePage(driver, WordPressSignInPage.class).render();
    }
}
