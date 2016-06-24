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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareLink;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.xml.sax.InputSource;

/**
 * Abstract of an Alfresco Share dashlet web element.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public abstract class AbstractDashlet extends PageElement
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
     * Gets the title on the dashlet panel.
     *
     * @return String dashlet title
     */
    public String getDashletTitle()
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
    protected boolean isEmpty(final String css)
    {
        if (dashlet == null)
        {
            return true;
        }
        try
        {
            String selector = css + " div.empty";
            boolean empty = driver.findElement(By.cssSelector(selector)).isDisplayed();
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
    protected boolean isVisibleResults()
    {
        if (dashlet == null)
        {
            return true;
        }
        try
        {
            return driver.findElement(By.cssSelector("tbody.yui-dt-data tr")).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Populates the data seen in dashlet.
     */
    protected List<ShareLink> getList(final String csslocator)
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
                shareLinks.add(new ShareLink(site, driver, factoryPage));
            }
            return shareLinks;
        }
        catch (StaleElementReferenceException e)
        {
            return getList(csslocator);
        }
    }

    /**
     * Retries the {@link ShareLink} object that matches the title.
     *
     * @param cssLocation String css selector description
     * @param title       String identifier to match
     * @return {@link ShareLink} link that matches the title
     */
    protected ShareLink getLink(final String cssLocation, final String title)
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
        ;
        Actions a = new Actions(driver);
        a.moveToElement(driver.findElement(resizeHandle));
        a.perform();
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
            return findAndWait(DASHLET_HELP_BALLOON).isDisplayed();
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
            return findAndWait(DASHLET_HELP_BALLOON_TEXT).getText();
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
            findAndWait(DASHLET_HELP_BALLOON_CLOSE_BUTTON).click();
            waitUntilElementDisappears(DASHLET_HELP_BALLOON, TimeUnit.SECONDS.convert(getDefaultWaitTime(), TimeUnit.MILLISECONDS));
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
     * @param xml String
     * @param element String
     * @return String
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
            findAndWait(By.cssSelector(CHART_NODE)).click();
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
     * @return boolean
     */
    public boolean isNoDataFoundDisplayed()
    {
        try
        {
            WebElement noDataFound = driver.findElement(By.cssSelector(NO_DATA_FOUND));
            return noDataFound.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No No data found message " + nse);
            throw new PageException("Unable to find No data found message.", nse);
        }
    }

    /**
     * This method gets the focus by placing mouse over on Dashlet.
     */
    protected void getFocus(By dashletPlaceholder)
    {
        mouseOver(driver.findElement(dashletPlaceholder));
    }
}
