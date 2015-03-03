package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

/**
 * Page object to represent site wiki dashlet
 *
 * @author Marina.Nenadovets
 */
public class WikiDashlet extends AbstractDashlet implements Dashlet
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(WikiDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.wiki");
    private static final By TEXT_IN_DASHLET = By.cssSelector("div[class^=body]>div>*");

    /**
     * Constructor.
     */
    protected WikiDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiDashlet render(RenderTime timer)
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
                    this.dashlet = drone.find(DASHLET_CONTAINER_PLACEHOLDER);
                    break;
                }
                catch (NoSuchElementException e)
                {
                    logger.info("The Dashlate was not found " + e);
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
    public WikiDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiDashlet render()
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
     * Method to click Configure icon
     *
     * @return
     */
    public SelectWikiDialogueBoxPage clickConfigure()
    {
        try
        {
            getFocus();
            dashlet.findElement(CONFIGURE_DASHLET_ICON).click();
            return new SelectWikiDialogueBoxPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("timed out finding " + CONFIGURE_DASHLET_ICON);
        }
    }

    /**
     * Return content text from dashlet
     *
     * @return
     */
    public String getContent()
    {
        try
        {
            return dashlet.findElement(TEXT_IN_DASHLET).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getContent();
        }
    }
}
