package org.alfresco.po.share.adminconsole.replicationjobs;

import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * The class holds elements related to Confirm Delete Replication Job page
 *
 * @author Marina.Nenadovets
 */
public class DeleteJobPage extends ConfirmDeletePage
{
    private final Log logger = LogFactory.getLog(ConfirmDeletePage.class);

    public DeleteJobPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteJobPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteJobPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DeleteJobPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to select the action (Delete or Cancel)
     *
     * @param action Action
     * @return HtmlPage
     */
    public HtmlPage selectAction(Action action)
    {

        try
        {
            By buttonSelector = By.cssSelector(".button-group span span button");
            List<WebElement> buttons = drone.findAll(buttonSelector);
            long elementWaitTime = SECONDS.convert(maxPageLoadingTime, MILLISECONDS);
            for (WebElement button : buttons)
            {
                if (action.name().equals(button.getText()))
                {
                    button.click();
                    waitUntilAlert(elementWaitTime);
                    return drone.getCurrentPage().render();
                }

            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("Buttons not present in this page", nse);

        }
        throw new PageOperationException("Buttons not present in this page");
    }
}
