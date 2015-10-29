/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.webview")
/**
 * Page object to hold Web View dashlet
 * 
 * @author Marina.Nenadovets
 */
public class WebViewDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(WebViewDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.webview");
    private static final By IF_FRAME_WITH_SITE = By.cssSelector("iframe[class='iframe-body']");
    private static final By DEFAULT_MESSAGE = By.cssSelector("h3[class$='default-body']");
    protected static final By DASHLET_TITLE_WEB = By.cssSelector(".title > a");

//    /**
//     * Constructor.
//     */
//    protected WebViewDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector(".yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
    public WebViewDashlet render(RenderTime timer)
    {
        try
        {
            setResizeHandle(By.cssSelector(".yui-resize-handle"));
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
                    logger.error("The placeholder for WebViewDashlet dashlet was not found ", e);
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
     * This method gets the focus by placing mouse over on Site Web View Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to click Configure icon
     * 
     * @return ConfigureWebViewDashletBox page object
     */
    public ConfigureWebViewDashletBoxPage clickConfigure()
    {
        try
        {
            getFocus();
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return factoryPage.instantiatePage(driver, ConfigureWebViewDashletBoxPage.class).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * return default text from dashlet. or throw Exception.
     * 
     * @return String
     */
    public String getDefaultMessage()
    {
        try
        {
            return dashlet.findElement(DEFAULT_MESSAGE).getText();
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Default message in web view dashlet missing or don't display.");
        }
    }

    /**
     * return true if frame with url displayed.
     * 
     * @param url String
     * @return boolean
     */
    public boolean isFrameShow(String url)
    {
        checkNotNull(url);
        try
        {
            WebElement element = dashlet.findElement(IF_FRAME_WITH_SITE);
            return element.getAttribute("src").equals(url);
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public void clickTitle()
    {
        try
        {
            dashlet.findElement(DASHLET_TITLE_WEB).click();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    public String getWebViewDashletTitle()
    {
        waitUntilElementPresent(DASHLET_TITLE_WEB, 6);
        return dashlet.findElement(DASHLET_TITLE_WEB).getText();
    }
    @SuppressWarnings("unchecked")
    @Override
    public WebViewDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
