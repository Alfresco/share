package org.alfresco.po.share;

import java.util.List;

import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.WebDriver;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Charu -To confirm remove user/ not to remove user from any group this page is used
 */

@SuppressWarnings("unused")
public class RemoveUserFromGroupPage extends SharePage
{
    private static final String CONFIRM_MESSAGE = "div[class='yui-module yui-overlay yui-panel' ]>div[class='bd']";
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    public enum Action
    {
        Yes, No
    }


    @SuppressWarnings("unchecked")
    @Override
    public RemoveUserFromGroupPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RemoveUserFromGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Action of selecting "Yes" or "No" on Remove User from group page.
     * 
     * @param action Action
     * @return - HtmlPage
     */
    public HtmlPage selectAction(Action action)
    {
        try
        {
            List<WebElement> buttons = driver.findElements(By.cssSelector(".button-group span span button"));
            for (WebElement button : buttons)
            {
                if (action.name().equals(button.getText()))
                {
                    button.click();
                    canResume();
                    return getCurrentPage();
                }

            }

        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("not present in this page", nse);
        }
        throw new PageOperationException("not present in this page");
    }

    /**
     * Get the Title in Remove user pop up window
     * 
     * @return - String
     */

    public String getTitle()
    {
        try
        {
            return findAndWait(By.cssSelector("div[class='yui-module yui-overlay yui-panel']>div[id='prompt_h']")).getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: Remove user from Goup", toe);
        }

    }

}
