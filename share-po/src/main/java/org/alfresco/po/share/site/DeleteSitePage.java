package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Delete site popup comes when you click delete button on {@link SiteFinderPage}.
 * 
 * @author Bogdan Bocancea
 */

public class DeleteSitePage extends SharePage
{

    //private static final String DELETE_BUTTON = "//button[text()='Delete']";
    private static final String DELETE_BUTTON = "div#prompt div.ft span span button";
    private static final String CANCEL_BUTTON = "//button[text()='Cancel']";
    private static final String MESSAGE_LABEL = "//div[@id='prompt']/div[@class='bd']";

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public DeleteSitePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSitePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSitePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteSitePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Helper method to click on the Delete button
     * 
     * @return
     */
    public HtmlPage clickDelete()
    {
        try
        {
            drone.findAndWait(By.cssSelector(DELETE_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Delete button", e);
        }
        return new DeleteSiteConfirmPage(drone);
    }

    /**
     * Helper method to click on the Cancel button
     */
    public HtmlPage clickCancel()
    {
        try
        {
            drone.findAndWait(By.xpath(CANCEL_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Cancel button", e);
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