package org.alfresco.po.share.dashlet;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div.dashlet.site-profile")
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
//
//    /**
//     * Constructor.
//     */
//    protected SiteProfileDashlet(WebDriver driver)
//    {
//        super(driver, DASHLET_CONTAINER_PLACEHOLDER);
//        setResizeHandle(By.cssSelector("div.dashlet.site-profile .yui-resize-handle"));
//    }
    @SuppressWarnings("unchecked")
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
                    this.dashlet = driver.findElement(DASHLET_CONTAINER_PLACEHOLDER);
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

    /**
     * This method gets the focus by placing mouse over on Site Wiki Dashlet.
     */
    protected void getFocus()
    {
        mouseOver(findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to get contents from the dashlet
     *
     * @return String value
     */
    public String getContent()
    {
        getFocus();
        WebElement content = findAndWait(DASHLET_CONTENT);
        return content.getText();
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteProfileDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
