package org.alfresco.po.share.user;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class CloudForgotPasswordPage extends ShareDialogue
{
    @RenderWebElement
    private static final By EMAIL_INPUT = By.cssSelector("input[id$='_default-username']");
    @RenderWebElement
    private static final By SEND_INSTRUCTIONS_BUTTON = By.cssSelector("button[id$='_default-submit-button']");
    @RenderWebElement
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='_default-cancel-button']");

    private static final By CONFIRMATION_RESET_PASSWORD = By.cssSelector("div[id$='default-confirmation-container']");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public CloudForgotPasswordPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudForgotPasswordPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudForgotPasswordPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CloudForgotPasswordPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Click Send Instructions button
     * 
     * @param userEmail String user email value
     */
    public void clickSendInstructions(final String userEmail)
    {
        try
        {
            WebElement usernameInput = drone.findAndWait(EMAIL_INPUT);
            usernameInput.clear();
            usernameInput.sendKeys(userEmail);
            WebElement button = drone.findAndWait(SEND_INSTRUCTIONS_BUTTON);
            button.click();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Element not found", te);
        }

    }

    /**
     * Click Cancel button
     */
    public HtmlPage clickCancel()
    {
        try
        {
            WebElement button = drone.findAndWait(CANCEL_BUTTON);
            button.click();
            return new LoginPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Element not found", te);
        }
    }

    public boolean isConfirmationResetPassword()
    {
        try
        {
            return drone.findAndWait(CONFIRMATION_RESET_PASSWORD).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

    }
}
