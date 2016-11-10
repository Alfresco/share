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

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.calendar")
/**
 * Page object to hold Site Calendar dashlet
 * 
 * @author Marina.Nenadovets
 */
public class SiteCalendarDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.calendar");
    private static final By EVENTS_LINKS = By.cssSelector(".details2>div>span>a");
    private static final By EVENTS_DETAILS = By.cssSelector(".details2>div>span");
    private static final By EVENTS_HEADER = By.cssSelector("div[class='details2']>h4");
    private Log logger = LogFactory.getLog(this.getClass());

//    /**
//     * Constructor.
//     */
//    protected SiteCalendarDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector("div.dashlet.calendar .yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public SiteCalendarDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(By.cssSelector("div.dashlet.calendar .yui-resize-handle"));
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
                    logger.info("The placeholder for SiteCalendarDashlet dashlet was not found  " + e);
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
     * This method gets the focus by placing mouse over on Site Calendar Dashlet.
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
        	this.dashlet = findAndWait(DASHLET_CONTAINER_PLACEHOLDER);
        	return dashlet.findElements(EVENTS_LINKS);
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
            if (linkText.contains(eventName))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }

    /**
     * @param eventDetail String
     * @return boolean
     */
    public boolean isEventsWithDetailDisplayed(String eventDetail)
    {
        checkNotNull(eventDetail);
        List<WebElement> eventLinks = dashlet.findElements(EVENTS_DETAILS);
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(eventDetail))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }
    
    /**
     * 
     * @param eventHeader String
     * @return boolean
     */
    public boolean isEventsWithHeaderDisplayed(String eventHeader)
    {
        checkNotNull(eventHeader);
        List<WebElement> eventLinks = dashlet.findElements(EVENTS_HEADER);
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(eventHeader))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }

    /**
     * @param eventName String
     * @param startTime String
     * @param endTime String
     * @return boolean
     */
    public boolean isEventsWithDetailDisplayed(String eventName, String startTime, String endTime)
    {
        PageUtils.checkMandatoryParam("Event Name", eventName);
        PageUtils.checkMandatoryParam("Start Time", startTime);;
        PageUtils.checkMandatoryParam("End Time", endTime);;
        List<WebElement> eventLinks = dashlet.findElements(EVENTS_DETAILS);
        for (WebElement eventLink : eventLinks)
        {
            String linkText = eventLink.getText();
            if (linkText.contains(eventName) && linkText.contains(startTime) && linkText.contains(endTime))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
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
            if (linkText.equalsIgnoreCase(event))
                ;
            Boolean repeating = eventLink.getText().contains("Repeating");
            return repeating;
        }

        return false;
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteCalendarDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
