package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import static com.google.common.base.Preconditions.checkNotNull;

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

    /**
     * Constructor.
     */
    protected WebViewDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebViewDashlet render(RenderTime timer)
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
                    this.dashlet = drone.find(DASHLET_CONTAINER_PLACEHOLDER);
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

    @SuppressWarnings("unchecked")
    @Override
    public WebViewDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebViewDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Web View Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
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
            return new ConfigureWebViewDashletBoxPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * return default text from dashlet. or throw Exception.
     * 
     * @return
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
     * @param url
     * @return
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
        drone.waitUntilElementPresent(DASHLET_TITLE_WEB, 6);
        return dashlet.findElement(DASHLET_TITLE_WEB).getText();
    }

}
