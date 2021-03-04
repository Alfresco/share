/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.dashlet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.user-calendar")
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
//
//    /**
//     * Constructor.
//     */
//    protected MyCalendarDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector("div.dashlet.user-calendar .yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public MyCalendarDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(DASHLET_CONTAINER_PLACEHOLDER);
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
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for MyCalendarDashlet dashlet was not found ", e);
                }
                catch (StaleElementReferenceException ste)
                {
                    logger.error("DOM has changed therefore page should render once change", ste);
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

    /**
     * This method gets the focus by placing mouse over on My Calendar Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
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
     * @return int
     */
    public int getEventsCount()
    {
        return getEventLinksElem().size();
    }

    /**
     * Return true if link with eventName Displayed.
     *
     * @param eventName String
     * @return boolean
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
     * @param eventDetail String
     * @return boolean
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
    public HtmlPage clickEvent(String eventName)
    {
        checkNotNull(eventName);
        List<WebElement> eventLinks = getEventLinksElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.equalsIgnoreCase(eventName))
            {
                eventLink.click();
                return getCurrentPage();
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
    public HtmlPage clickSite(String siteName)
    {
        checkNotNull(siteName);
        List<WebElement> eventLinks = getSitesDetailsElem();
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.equalsIgnoreCase(siteName))
            {
                eventLink.click();
                return getCurrentPage();
            }
        }

        throw new PageOperationException("Site '" + siteName + "' was not found!");
    }

    /**
     * Return the name of the event.
     *
     * @param event String
     * @return boolean
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
     * @param eventDetail String
     * @return String
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
            return driver.findElement(By.cssSelector(EMPTY_DASHLET_MESSAGE)).getText();
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
     * @param eventName String
     * @param date String
     * @param siteName String
     * @return boolean
     */
    public boolean isEventDisplayed(String eventName, String date, String siteName)
    {
        checkNotNull(eventName);
        checkNotNull(date);
        checkNotNull(siteName);
        WebElement webElement = findAndWait(By.xpath(String.format(EVENT_ON_DASHLET, eventName, date, siteName)));
        return webElement.isDisplayed();
    }

    /**
     * Click on event's date
     *
     * @return CalendarPage
     */
    public HtmlPage clickEventSiteName(String eventName, String siteName)
    {
        WebElement webElement = findAndWait(By.xpath(String.format(EVENT_ON_DASHLET, eventName, "", siteName)));
        webElement.click();

        return getCurrentPage();
    }
    @SuppressWarnings("unchecked")
    @Override
    public MyCalendarDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
