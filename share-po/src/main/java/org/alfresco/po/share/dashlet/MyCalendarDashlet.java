package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Page object to hold My Calendar dashlet
 * 
 * @author Bogdan.Bocancea
 */

public class MyCalendarDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.user-calendar");
    private static final By EVENTS_LINKS = By.cssSelector(".detail>h4>a");
    private static final By EVENTS_DETAILS = By.cssSelector("div.detail");
    private static final By SITE_DETAILS = By.cssSelector(".detail>div>a");
    // empty dashlet message
    private static final String EMPTY_DASHLET_MESSAGE = "div[id$='_default-events']>table>tbody>tr.yui-dt-first.yui-dt-last>td>div";
    private final static String EVENT_ON_DASHLET = "//a[contains(text(),'%s')]/parent::h4/following-sibling::div[contains(text(),'%s')]/following-sibling::div/a[text()='%s']";

    private static Log logger = LogFactory.getLog(MyCalendarDashlet.class);

    /**
     * Constructor.
     */
    protected MyCalendarDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.user-calendar .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized MyCalendarDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    scrollDownToDashlet();
                    getFocus();
                    this.dashlet = drone.findAndWait((DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change
                    // is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site notice dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyCalendarDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyCalendarDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on My Calendar Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    private List<WebElement> getEventLinksElem()
    {
        try
        {
            return dashlet.findElements(EVENTS_LINKS);
        }
        catch (StaleElementReferenceException e)
        {
            return getEventLinksElem();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    private List<WebElement> getEventDetailsElem()
    {
        try
        {
            return dashlet.findElements(EVENTS_DETAILS);
        }
        catch (StaleElementReferenceException e)
        {
            return getEventDetailsElem();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Gets count of Events displayed in Dashlet
     * 
     * @return
     */
    public int getEventsCount()
    {
        return getEventLinksElem().size();
    }

    /**
     * Return true if link with eventName Displayed.
     * 
     * @param eventName
     * @return
     */
    public boolean isEventsDisplayed(String eventName)
    {
        checkNotNull(eventName);
        List<WebElement> eventLinks = getEventLinksElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (eventName.equals(linkText))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }

    /**
     * Return true if event with the details is displayed
     * 
     * @param eventDetail
     * @return
     */
    public boolean isEventDetailsDisplayed(String eventDetail)
    {
        checkNotNull(eventDetail);
        List<WebElement> eventLinks = getEventDetailsElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (eventDetail.equals(linkText))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }

    /**
     * Click on event
     * 
     * @return CalendarPage
     */
    public CalendarPage clickEvent(String eventName)
    {
        checkNotNull(eventName);
        List<WebElement> eventLinks = getEventLinksElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.equalsIgnoreCase(eventName))
            {
                eventLink.click();
                return new CalendarPage(drone);
            }
        }

        throw new PageOperationException("Event '" + eventName + "' was not found!");
        
        
        
    }

    private List<WebElement> getSitesDetailsElem()
    {
        try
        {
            return dashlet.findElements(SITE_DETAILS);
        }
        catch (StaleElementReferenceException e)
        {
            return getSitesDetailsElem();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    /**
     * Click on site
     * 
     * @return SitePage
     */
    public SiteDashboardPage clickSite(String siteName)
    {
        checkNotNull(siteName);
        List<WebElement> eventLinks = getSitesDetailsElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.equalsIgnoreCase(siteName))
            {
                eventLink.click();
                return new SiteDashboardPage(drone);
            }
        }

        throw new PageOperationException("Site '" + siteName + "' was not found!");
    }

    /**
     * Return the name of the event.
     * 
     * @param eventName
     * @return
     */
    public boolean isRepeating(String event)
    {
        checkNotNull(event);
        List<WebElement> eventLinks = getEventLinksElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(event))
            {
                Boolean repeating = eventLink.getText().contains("Repeating");
                return repeating;
            }
        }

        return false;
    }

    /**
     * Return true if event with the details is displayed
     * 
     * @param eventDetail
     * @return
     */
    public String getEventDetails(String eventDetail)
    {
        checkNotNull(eventDetail);
        List<WebElement> eventLinks = getEventDetailsElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(eventDetail))
            {
                return linkText;
            }
        }
        return "";
    }

    /**
     * Gets empty dashlet message
     */
    public String getEmptyDashletMessage()
    {
        try
        {
            return drone.find(By.cssSelector(EMPTY_DASHLET_MESSAGE)).getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find empty dashlet message.", nse);
        }
        throw new PageOperationException("Error in finding the css for empty dashlet message.");
    }

    /**
     * true if event displayed in dashlet
     * 
     * @param eventName
     * @param date
     * @param siteName
     * @return
     */
    public boolean isEventDisplayed(String eventName, String date, String siteName)
    {
        checkNotNull(eventName);
        checkNotNull(date);
        checkNotNull(siteName);
        WebElement webElement = drone.findAndWait(By.xpath(String.format(EVENT_ON_DASHLET, eventName, date, siteName)));
        return webElement.isDisplayed();
    }

    /**
     * Click on event's date
     * 
     * @return CalendarPage
     */
    public SiteDashboardPage clickEventSiteName(String eventName, String siteName)
    {
        WebElement webElement = drone.findAndWait(By.xpath(String.format(EVENT_ON_DASHLET, eventName, "", siteName)));
        webElement.click();

        return drone.getCurrentPage().render();
    }
}
