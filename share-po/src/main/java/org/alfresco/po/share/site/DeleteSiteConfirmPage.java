package org.alfresco.po.share.site;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Delete site popup - confirmation page comes when you delete button on {@link DeleteSitePage}
 * 
 * @author Bogdan Bocancea
 */

public class DeleteSiteConfirmPage extends SharePage
{

    //private static final String YES_BUTTON = "//button[text()='Yes']";
    private static final String YES_BUTTON = "div#prompt div.ft span span button";
    private static final String NO_BUTTON = "//button[text()='No']";
    private static final String MESSAGE_LABEL = "//div[@id='prompt']/div[@class='bd']";

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public DeleteSiteConfirmPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSiteConfirmPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSiteConfirmPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSiteConfirmPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Helper method to click on the Delete button
     */
    public HtmlPage clickYes()
    {
        try
        {
            drone.findAndWait(By.cssSelector(YES_BUTTON)).click();
            drone.waitUntilVisible(By.xpath(".//*[@id='message']/div/span"), "Site was deleted", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilNotVisibleWithParitalText(By.xpath(".//*[@id='message']/div/span"), "Site was deleted", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Yes button", e);
        }
        return new SiteFinderPage(drone);
    }

    /**
     * Helper method to click on the Delete button
     */
    public HtmlPage clickNo()
    {
        try
        {
            drone.findAndWait(By.xpath(NO_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the No button", e);
        }
        return new SiteFinderPage(drone);
    }

    /**
     * Get the message from the popup
     * 
     * @return String message
     */
    public String getMessage()
    {
        String message = "";
        try
        {
            message = drone.findAndWait(By.xpath(MESSAGE_LABEL)).getText();
        }
        catch (NoSuchElementException e)
        {
        }
        return message;
    }
}
