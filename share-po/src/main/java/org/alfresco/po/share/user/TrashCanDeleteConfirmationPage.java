
package org.alfresco.po.share.user;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * When the users Deletes an item in the trashcan they will be presented with confirmation Dialog.
 * This page is validate the confirmation dialog.
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */

public class TrashCanDeleteConfirmationPage extends TrashCanPage
{
    protected static final By DELETE_CONFIRMATION_PROMPT = By.cssSelector("div[id='prompt']");
    protected static final By CONFIRMATION_BUTTON = By.cssSelector("div.ft>span button");
    private boolean deleteInitiator = true;
    protected static final By DELETE_CONFIRMATION_OK_BUTTON = By.cssSelector("div.ft button");
    protected static final By DELETE_SUCCESSFULLY_COMPLETED_MESSAGE = By.cssSelector("div[class='bd']>span");

    public boolean isDeleteInitiator()
    {
        return deleteInitiator;
    }

    public void setDeleteInitiator(boolean deleteInitiator)
    {
        this.deleteInitiator = deleteInitiator;
    }

    /**
     * Basic Render method
     */
    @SuppressWarnings("unchecked")
    @Override
    public TrashCanDeleteConfirmationPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TrashCanDeleteConfirmationPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Is confirmation Dialog displayed
     * 
     * @return - Boolean
     */
    public boolean isConfirmationDialogDisplayed()
    {
        boolean displayed = false;
        try
        {
            WebElement prompt = findAndWait(DELETE_CONFIRMATION_PROMPT);
            displayed = prompt.isDisplayed();
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Click on Cancel button in the confirmation dialog should take the control back to trashcan Page
     * 
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public TrashCanPage clickCancelButton() throws PageOperationException
    {
        try
        {
            List<WebElement> buttons = findAndWaitForElements(CONFIRMATION_BUTTON);
            for (WebElement buttonElement : buttons)
            {
                if (buttonElement.getText().equalsIgnoreCase("Cancel"))
                {
                    buttonElement.click();
                    return factoryPage.instantiatePage(driver, TrashCanPage.class);
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Cancel button is not visible", te);
        }
        return factoryPage.instantiatePage(driver, TrashCanPage.class);
    }

    /**
     * Click on Cancel button in the confirmation dialog should take the control back to trashcan Page
     * 
     * @return - TrashCanPage, TrashCanDeleteConfirmDialog
     * @throws - PageOperationException
     */
    public HtmlPage clickOkButton() throws PageOperationException
    {
        try
        {
            List<WebElement> buttons = findAndWaitForElements(CONFIRMATION_BUTTON);
            for (WebElement buttonElement : buttons)
            {
                if (buttonElement.getText().equalsIgnoreCase("OK"))
                {
                    buttonElement.click();
                    if (deleteInitiator)
                    {
                        return factoryPage.instantiatePage(driver,TrashCanDeleteConfirmDialog.class);
                    }
                    else
                    {
                        waitUntilElementDisappears(DELETE_SUCCESSFULLY_COMPLETED_MESSAGE, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    }
                    return factoryPage.instantiatePage(driver, TrashCanPage.class);
                }
            }
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Ok button is not visible", te);
        }
        return factoryPage.instantiatePage(driver, TrashCanPage.class);
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
            throw new PageOperationException ("Time out finding notification message", toe);
        }

    }
}
