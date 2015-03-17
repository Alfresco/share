package org.alfresco.po.share.site.document;

import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Class holds elements related to Google Docs Checkin window
 *
 * @author Marina.Nenadovets
 */
public class GoogleDocCheckInPage extends UpdateFilePage
{
    /**
     * Constructor.
     */
    public GoogleDocCheckInPage(WebDrone drone, String documentVersion, boolean editOffline)
    {
        super(drone, documentVersion, editOffline);
        setMinorVersionRadioButton("input[id$='default-minorVersion-radioButton']");
        setMajorVersionRadioButton("input[id$='default-majorVersion-radioButton']");
        setSubmitButton("div[id$='default-new-version-dialog'] span.default button");
        setCancelButton("div[id$='default-new-version-dialog'] span:nth-child(2)");
        setTextAreaCssLocation("textarea[id$='default-description-textarea']");
    }

    /**
     * Render method Overridden as we have a different set of parameters to
     * check.
     */
    @Override
    public synchronized GoogleDocCheckInPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            // Look for comment box
            try
            {
                if (!drone.find(By.cssSelector(getMinorVersionRadioButton())).isDisplayed())
                {
                    continue;
                }
                if (!drone.find(By.cssSelector(getMajorVersionRadioButton())).isDisplayed())
                {
                    continue;
                }
            }
            catch (NoSuchElementException e)
            {
                // It's not there
                continue;
            }
            // Everything was found and is visible
            break;
        }
        return this;
    }

    @Override
    public GoogleDocCheckInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public GoogleDocCheckInPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Clicks on the submit upload button.
     */
    @Override
    public HtmlPage submit()
    {
        WebElement submitButtonElement = drone.findAndWait(By.cssSelector(getSubmitButton()));
        submitButtonElement.click();
        waitUntilAlert(10);

        String text = "Checkin Google Doc";
        RenderTime time = new RenderTime(maxPageLoadingTime + maxPageLoadingTime);
        time.start();
        while (true)
        {
            try
            {
                drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            }
            catch (TimeoutException e)
            {
                SharePopup errorPopup = new SharePopup(drone);
                try
                {
                    errorPopup.render(new RenderTime(popupRendertime));
                    return errorPopup;
                }
                catch (PageRenderTimeException exception)
                {
                    continue;
                }
            }
            finally
            {
                time.end();
            }
            break;
        }

        HtmlPage page = drone.getCurrentPage().render();

        if (page instanceof DocumentLibraryPage)
        {
            return new DocumentLibraryPage(drone);
        }

        return new DocumentDetailsPage(drone, getDocumentVersion());
    }
}
