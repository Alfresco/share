package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Page object to hold Image Preview Dashlet
 *
 * @author Marina.Nenadovets
 */
public class ImagePreviewDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(ImagePreviewDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.xpath("//div[starts-with(@class,'dashlet resizable')]");
    private static final By HELP_ICON = By.xpath("//div[starts-with(@class,'dashlet resizable')] //div[@class='titleBarActionIcon help']");
    private static final By CONFIGURE_DASHLET_ICON = By.xpath("//div[starts-with(@class,'dashlet resizable')] //div[@class='titleBarActionIcon edit']");
    private static final By DASHLET_TITLE = By.xpath("//div[starts-with(@class,'dashlet resizable')] // div[@class='title']");
    private static final By DASHLET_HELP_BALLOON = By.cssSelector("div[style*='visible']>div.bd>div.balloon");
    private static final By DASHLET_HELP_BALLOON_TEXT = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.text");
    private static final By DASHLET_HELP_BALLOON_CLOSE_BUTTON = By.cssSelector("div[style*='visible']>div.bd>div.balloon>div.closeButton");
    private static final By titleBarActions = By.xpath("//div[starts-with(@class,'dashlet resizable')] //div[@class='titleBarActions']");
    private static final By IMAGE_LINK = By.xpath(".//div[@class='thumbnail']/a");

    /**
     * Constructor.
     */
    protected ImagePreviewDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.xpath("//div[starts-with(@class,'dashlet resizable')] //div[starts-with(@class, 'yui-resize-handle')]"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized ImagePreviewDashlet render(RenderTime timer)
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
                    drone.find(DASHLET_CONTAINER_PLACEHOLDER);
                    drone.find(CONFIGURE_DASHLET_ICON);
                    drone.find(HELP_ICON);
                    drone.find(DASHLET_TITLE);
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
    public ImagePreviewDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ImagePreviewDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Content Dashlet.
     */
    protected void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
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
            drone.mouseOverOnElement(drone.find(titleBarActions));
            return drone.findAndWait(HELP_ICON).isDisplayed();
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
     * Finds whether help icon is displayed or not.
     *
     * @return True if the help icon displayed else false.
     */
    public boolean isConfigureIconDisplayed()
    {
        try
        {
            scrollDownToDashlet();
            drone.mouseOverOnElement(drone.find(titleBarActions));
            return drone.findAndWait(CONFIGURE_DASHLET_ICON).isDisplayed();
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
     * This method is used to Finds Help icon and clicks on it.
     */
    public void clickOnHelpIcon()
    {
        try
        {
            drone.mouseOverOnElement(drone.find(titleBarActions));
            drone.findAndWait(HELP_ICON).click();
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find the help icon.", te);
            throw new PageOperationException("Unable to click the Help icon");
        }
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
     * This method closes the Help balloon message.
     */
    public ImagePreviewDashlet closeHelpBallon()
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
     * This method gets the Image Preview Dashlet title.
     *
     * @return String
     */
    public String getTitle()
    {
        try
        {
            return drone.findAndWait(DASHLET_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new UnsupportedOperationException("Exceeded time to find the title.", te);
        }
    }

    /**
     * This method is used to Finds Config icon and clicks on it.
     */
    public SelectImageFolderBoxPage clickOnConfigure()
    {
        try
        {
            drone.mouseOverOnElement(drone.find(titleBarActions));
            drone.findAndWait(CONFIGURE_DASHLET_ICON).click();
            return new SelectImageFolderBoxPage(drone).render();
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find the config icon.", te);
            throw new PageOperationException("Unable to click the Config icon");
        }
    }

    private List<WebElement> getImagePreviewLinks()
    {
        try
        {
            return dashlet.findElements(IMAGE_LINK);
        }
        catch (StaleElementReferenceException e)
        {
            return getImagePreviewLinks();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }

    }

    /**
     * Get Count displayed in dashlet images.
     *
     * @return
     */
    public int getImagesCount()
    {
        return getImagePreviewLinks().size();
    }

    /**
     * true if images with name 'imageName' displayed in dashlet
     *
     * @param imageName
     * @return
     */
    public boolean isImageDisplayed(String imageName)
    {
        checkNotNull(imageName);
        try
        {
            URLEncoder.encode(imageName, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new PageOperationException(String.format("Bad imageName[%s]", imageName), e);
        }
        List<WebElement> eventLinks = getImagePreviewLinks();
        for (WebElement eventLink : eventLinks)
        {
            String linkHref = eventLink.getAttribute("href");
            if (linkHref.contains(""))
            {
                return eventLink.findElement(By.xpath("./img")).isDisplayed();
            }
        }
        return false;
    }

}
