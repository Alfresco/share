/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.dashlet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.xml.sax.InputSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract of an Alfresco Share dashlet web element.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public abstract class AbstractDashlet extends SharePage
{
    private static Log logger = LogFactory.getLog(AbstractDashlet.class);
    protected WebElement dashlet;
    protected static final By HELP_ICON = By.cssSelector("div[class$='titleBarActionIcon help']");
    protected static final By DASHLET_HELP_BALLOON = By.cssSelector("div[style*='visible']>div>div.balloon");
    protected static final By DASHLET_HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div>div.balloon>div.text");
    protected static final By DASHLET_HELP_BALLOON_CLOSE_BUTTON = By.cssSelector("div[style*='visible']>div>div>.closeButton");
    protected static final By CONFIGURE_DASHLET_ICON = By.cssSelector("div.titleBarActionIcon.edit");
    protected static final By DASHLET_TITLE = By.cssSelector(".title");
    private static final By titleBarActions = By.cssSelector("div.titleBarActions");
    private static final String CHART_NODE = "div[data-dojo-attach-point='chartNode']";
    private static final String NO_DATA_FOUND = "text[pointer-events='none']";


    private By resizeHandle;
    

    /**
     * Constructor.
     */
    protected AbstractDashlet(WebDrone drone, By by)
    {
        super(drone);
        try
        {
            this.dashlet = drone.findAndWait(by, 100L);
        }
        catch (Exception e)
        {
            // We treat this as an empty dashlet (it might not be present)
        }
    }

    /**
     * Gets the title on the dashlet panel.
     *
     * @return String dashlet title
     */
    public synchronized String getDashletTitle()
    {
        checkNotNull(dashlet, "dashlet doesn't exist");
        return dashlet.findElement(By.cssSelector("div.title")).getText();
    }

    /**
     * Checks if dashlet is empty by verifying that dashlet div class empty
     * is not displayed.
     *
     * @param css locator
     * @return true if empty
     */
    protected synchronized boolean isEmpty(final String css)
    {
        if (dashlet == null)
        {
            return true;
        }
        try
        {
            String selector = css + " div.empty";
            boolean empty = drone.find(By.cssSelector(selector)).isDisplayed();
            return empty;
        }
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

    /**
     * Check if results table is populated.
     *
     * @return true when results are displayed
     */
    protected synchronized boolean isVisibleResults()
    {
        if (dashlet == null)
        {
            return true;
        }
        try
        {
            return drone.find(By.cssSelector("tbody.yui-dt-data tr")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Populates the data seen in dashlet.
     */
    protected synchronized List<ShareLink> getList(final String csslocator)
    {
        try
        {
            if (csslocator == null || csslocator.isEmpty())
            {
                throw new UnsupportedOperationException("Selector By value is required");
            }
            // Populate ShareLinks with content in dashlet
            List<WebElement> links = dashlet.findElements(By.cssSelector(csslocator));
            if (links == null)
            {
                return Collections.emptyList();
            }

            List<ShareLink> shareLinks = new ArrayList<ShareLink>();
            for (WebElement site : links)
            {
                shareLinks.add(new ShareLink(site, drone));
            }
            return shareLinks;
        }
        catch (StaleElementReferenceException e)
        {
            return getList(csslocator);
        }
    }

    protected synchronized boolean renderBasic(RenderTime timer, final String css)
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
                    this.dashlet = drone.find(By.cssSelector(css));
                    break;
                }
                catch (Exception e)
                {
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return true;
    }

    /**
     * Retries the {@link ShareLink} object that matches the title.
     *
     * @param cssLocation String css selector description
     * @param title       String identifier to match
     * @return {@link ShareLink} link that matches the title
     */
    protected synchronized ShareLink getLink(final String cssLocation, final String title)
    {
        if (null == cssLocation || cssLocation.isEmpty())
        {
            throw new UnsupportedOperationException("css location value is required");
        }
        if (null == title || title.isEmpty())
        {
            throw new UnsupportedOperationException("title value is required");
        }
        List<ShareLink> shareLinks = getList(cssLocation);
        for (ShareLink link : shareLinks)
        {
            if (title.equalsIgnoreCase(link.getDescription()))
            {
                return link;
            }
        }
        throw new PageException("no documents found matching the given title: " + title);
    }

    /**
     * This method is used to scroll down the current window.
     */
    protected void scrollDownToDashlet()
    {
        dashlet.findElement(resizeHandle).click();

    }

    protected void setResizeHandle(By resizeHandle)
    {
        this.resizeHandle = resizeHandle;
    }

    public By getResizeHandle()
    {
        return resizeHandle;
    }

    /**
     * This method is used to Finds Help icon and clicks on it.
     */
    public void clickOnHelpIcon()
    {
        try
        {
            checkNotNull(dashlet, "dashlet doesn't exist");
            dashlet.findElement(titleBarActions);
            dashlet.findElement(HELP_ICON).click();
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find the help icon.", te);
            throw new PageOperationException("Unable to click the Help icon");
        }
    }

    /**
     * Finds whether help icon is displayed or not.
     *
     * @return True if the help icon displayed else false.
     */
    public boolean isHelpIconDisplayed()
    {
        try
        {
            scrollDownToDashlet();
            dashlet.findElements(titleBarActions);
            return dashlet.findElement(HELP_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the help icon.", te);
            }
        }
        return false;
    }

    /**
     * Finds whether help balloon is displayed on this page.
     *
     * @return True if the balloon displayed else false.
     */
    public boolean isBalloonDisplayed()
    {
        try
        {
            return drone.findAndWait(DASHLET_HELP_BALLOON).isDisplayed();
        }
        catch (TimeoutException elementException)
        {
        }
        return false;
    }

    /**
     * This method gets the Help balloon messages and merge the message into
     * string.
     *
     * @return String
     */
    public String getHelpBalloonMessage()
    {
        try
        {
            return drone.findAndWait(DASHLET_HELP_BALLOON_TEXT).getText();
        }
        catch (TimeoutException elementException)
        {
            logger.error("Exceeded time to find the help ballon text");
        }
        throw new UnsupportedOperationException("Not able to find the help text");
    }

    /**
     * This method gets the Dashlet title.
     *
     * @return String
     */
    public String getTitle()
    {
        try
        {
            return dashlet.findElement(DASHLET_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * This method closes the Help balloon message.
     */
    public AbstractDashlet closeHelpBallon()
    {
        try
        {
            drone.findAndWait(DASHLET_HELP_BALLOON_CLOSE_BUTTON).click();
            drone.waitUntilElementDisappears(DASHLET_HELP_BALLOON, TimeUnit.SECONDS.convert(WAIT_TIME_3000, TimeUnit.MILLISECONDS));
            return this;
        }
        catch (TimeoutException elementException)
        {
            throw new UnsupportedOperationException("Exceeded time to find the help ballon close button.", elementException);
        }
    }

    /**
     * Finds whether help icon is displayed or not.
     *
     * @return True if the help icon displayed else false.
     */
    public boolean isConfigureIconDisplayed()
    {
        try
        {
            scrollDownToDashlet();
            dashlet.findElement(titleBarActions);
            return dashlet.findElement(CONFIGURE_DASHLET_ICON).isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the help icon.", te);
            }
        }
        return false;
    }

    
    /**
     * Parses the xml string of the original-title attribute element to get tooltip 
     * data for report dashlets
     * 
     * @param xml
     * @param element
     * @return
     */
    protected String getElement(String xml, String element) throws Exception
    {
        String tooltipElement = " ";
        xml = xml.replaceAll("alt=\"avatar\">", "alt=\"avatar\"/>");
        xml = xml.replaceAll("<br>", "");
        
        
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        InputSource source = new InputSource(new StringReader(xml));
     
        try
        {
            tooltipElement = (String) xpath.evaluate(element, source, XPathConstants.STRING);
            
        } catch (XPathExpressionException ee)
        {
            logger.error("Cannot parse xml string " + ee);
        }
        return tooltipElement;
    }
    
    /**
     * Clicks on chart
     * 
     */
    public void clickOnChart()
    {
        try
        {
            drone.findAndWait(By.cssSelector(CHART_NODE)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click on chart.", e);
            }
        }
    }   
    
    /**
     * Checks if No data found is displayed instead of chart
     * 
     * @return
     */
    public boolean isNoDataFoundDisplayed()
    {
        try
        {
            WebElement noDataFound = drone.find(By.cssSelector(NO_DATA_FOUND));
            return noDataFound.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No No data found message " + nse);
            throw new PageException("Unable to find No data found message.", nse);
        }
    }
}
