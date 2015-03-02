package org.alfresco.po.share.site.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * Message error when uploading a file > 50 MB on cloud
 * 
 * @author Bogdan.Bocancea
 */

public class UploadLimitCloudMessage extends SharePage
{
    private static final String OK_BUTTON = "//button[text()='OK']";
    private static final String MESSAGE_LABEL = "//div[@id='prompt']/div[@class='bd']";

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public UploadLimitCloudMessage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadLimitCloudMessage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadLimitCloudMessage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadLimitCloudMessage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Helper method to click on the OK button
     */
    public HtmlPage clickOk()
    {
        try
        {
            drone.findAndWait(By.xpath(OK_BUTTON)).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the OK button", e);
        }
        return new UploadFilePage(drone);
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
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded the time to find the Message label", e);
        }
        return message;
    }
}
