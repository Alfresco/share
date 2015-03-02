package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * When the user Click on Discard button ,
 * they will b shown a message to discard which will contain Ok and Cancel.
 * 
 * @author Subashni Prasanna
 * @since 1.5
 */

public class GoogleDocsDiscardChanges extends EditInGoogleDocsPage
{
    private static final By BUTTON_TAG_NAME = By.tagName("button");
    private boolean isGoogleCreate;

    /**
     * Constructor
     */
    public GoogleDocsDiscardChanges(WebDrone drone, boolean isGoogleCreate)
    {
        super(drone, isGoogleCreate);
    }

    /**
     * Basic Render method
     */
    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsDiscardChanges render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsDiscardChanges render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsDiscardChanges render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Check the confirmation message is displayed.
     * 
     * @return true if the message displayed
     */
    public boolean isDiscardPromptDisplayed()
    {
        boolean displayed = false;
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            displayed = prompt.isDisplayed();
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Click OK Button on the confirmation dialog.
     * 
     * @return - DocumentDetailsPage
     */
    public HtmlPage clickOkButton()
    {
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("OK", elements);
            okButton.click();
            drone.waitUntilElementDeletedFromDom(By.cssSelector("span[class='message']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google discard Page ok button not visible", te);
        }
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Click on Cancel Button on the confirmation dialog.
     * 
     * @return - EditInGoogleDocsPage
     */
    public EditInGoogleDocsPage clickCancelButton()
    {
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement cancelButton = findButton("Cancel", elements);
            cancelButton.click();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google discard Page ok button not visible", te);
        }
        return new EditInGoogleDocsPage(drone, isGoogleCreate);
    }

    /**
     * Click OK Button on the confirmation dialog.
     * 
     * @return - DocumentDetailsPage
     */
    public HtmlPage clickOkConcurrentEditorButton()
    {
        String text = "Discarding Changes";
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("OK", elements);
            okButton.click();
            drone.waitUntilElementDisappears(PROMPT_PANEL_ID, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google discard Page ok button not visible", te);
        }

        waitUntilAlert(10);
        HtmlPage page = drone.getCurrentPage().render();
        if ((page instanceof DocumentDetailsPage) || (page instanceof DocumentLibraryPage))
        {
            drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return FactorySharePage.resolvePage(drone);
        }
        else
        {
            try
            {
                drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
                List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
                WebElement okButton = findButton("OK", elements);
                okButton.click();
                drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                drone.waitUntilElementDeletedFromDom(By.cssSelector("span[class='message']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                waitUntilAlert(5);
            }
            catch (TimeoutException te)
            {
                throw new TimeoutException("Ok button is not visible", te);
            }
        }

        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }

}
