package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

/**
 * Page object to represent Site Profile dashlet
 *
 * @author Marina.Nenadovets
 */
public class SiteProfileDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(SiteSearchDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.site-profile");
    private static final By DASHLET_CONTENT = By.cssSelector(".msg");

    /**
     * Constructor.
     */
    protected SiteProfileDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.site-profile .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteProfileDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                synchronized (this)
                {
                    timer.start();

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
                    getFocus(DASHLET_CONTAINER_PLACEHOLDER);
                    this.dashlet = drone.find(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.error("The placeholder for SiteSearchDashlet dashlet was not found ", e);
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
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site links dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteProfileDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteProfileDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Wiki Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to get contents from the dashlet
     *
     * @return String value
     */
    public String getContent()
    {
        getFocus();
        WebElement content = drone.findAndWait(DASHLET_CONTENT);
        return content.getText();
    }
}
