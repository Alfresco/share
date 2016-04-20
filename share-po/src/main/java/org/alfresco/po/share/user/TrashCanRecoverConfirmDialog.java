
package org.alfresco.po.share.user;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * When the users selects Recover of many items in the trashcan they will be
 * presented with confirmation about the list of items recoverd. This page is
 * validate the confirmation dialog.
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */

public class TrashCanRecoverConfirmDialog extends TrashCanPage
{
    protected static final By RECOVER_OK_BUTTON = By.cssSelector("div.ft button");

    @SuppressWarnings("unchecked")
    public TrashCanRecoverConfirmDialog render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(RECOVER_OK_BUTTON));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanRecoverConfirmDialog render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method helps to click on OK button
     */
    public TrashCanPage clickRecoverOK()
    {
        findAndWait(RECOVER_OK_BUTTON).click();
        return getCurrentPage().render();
    }

    /**
     * This method helps to get notification message
     * @return - String
     * @throws - PageOperationException
     */

    public String getNotificationMessage()
    {
        try
        {
            WebElement messageText = findAndWait(By.cssSelector("div.bd"));
            return messageText.getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Time out finding notification message", toe);
        }

    }
}
