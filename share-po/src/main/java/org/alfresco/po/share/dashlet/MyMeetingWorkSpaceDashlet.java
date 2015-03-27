package org.alfresco.po.share.dashlet;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * My meeting workspaces dashlet object.
 * 
 * @author Bogdan.Bocancea
 */

public class MyMeetingWorkSpaceDashlet extends AbstractDashlet implements Dashlet
{
    private final Log logger = LogFactory.getLog(this.getClass());
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.my-meeting-workspaces");
    private static final String DATA_LIST_CSS_LOCATION = "div.site-title > a";
    private static final String DASHLET_CONTENT_DIV_ID_PLACEHOLDER = "div[id$='default-meeting-workspaces']";
    private static final String DASHLET_EMPTY_PLACEHOLDER = "table>tbody>tr>td.yui-dt-empty>div";
    private static final String ROW_OF_SITE = "div[id$='default-meeting-workspaces'] tr[class*='yui-dt-rec']";
    private static final String SITE_NAME_IN_ROW = "div div[class$='site-title']";
    private static final String DELETE_SYMB_IN_ROW = "a[class*='delete-site']";
    private static final String DELETE_CONFIRM = "div#prompt div.ft span span button";
    private static final String DELETE_RE_CONFIRM = "div#prompt div.ft span span button";
    private static final String NO_MEETINGS_DISPLAYED = "div.dashlet-padding>h3";
    private static final String FAVORITE_SITE = "a[class$='favourite-site fav-site6']";
    private static final String FAVORITE_SITE_ENABLED = "a[class$='favourite-site fav-site6 enabled']";

    /**
     * Constructor.
     */
    public MyMeetingWorkSpaceDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.my-meeting-workspaces .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    public MyMeetingWorkSpaceDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyMeetingWorkSpaceDashlet render(final long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    public MyMeetingWorkSpaceDashlet render(RenderTime timer)
    {
        return render(timer, true);
    }

    /**
     * The active sites displayed on my meeting workspaces.
     * 
     * @return List<ShareLink> site links
     */
    public synchronized List<ShareLink> getSites()
    {
        return getList(DATA_LIST_CSS_LOCATION);
    }

    /**
     * Retrieves the link that match the site name.
     * 
     * @param name identifier
     * @return {@link ShareLink} that matches siteName
     */
    public synchronized ShareLink selectSite(final String name)
    {
        if (name == null)
        {
            throw new UnsupportedOperationException("Name value of link is required");
        }
        List<ShareLink> shareLinks = getList(DATA_LIST_CSS_LOCATION);
        for (ShareLink link : shareLinks)
        {
            if (name.equalsIgnoreCase(link.getDescription()))
            {
                return link;
            }
        }
        throw new PageException(String.format("Link %s can not be found on the page, dashlet exists: %s link size: %d", name, dashlet, shareLinks.size()));
    }

    /**
     * Render logic to determine if loaded and ready for use.
     * 
     * @param timer - {@link RenderTime}
     * @param waitForLoading boolean to whether check for waiting for Loading text to disappear.
     * @return {@link }
     */
    public synchronized MyMeetingWorkSpaceDashlet render(RenderTime timer, boolean waitForLoading)
    {
        try
        {
            while (true)
            {
                try
                {
                    timer.start();
                    WebElement dashlet = drone.find(By.cssSelector(DASHLET_CONTENT_DIV_ID_PLACEHOLDER));
                    if (dashlet.isDisplayed())
                    {
                        this.dashlet = drone.find(DASHLET_CONTAINER_PLACEHOLDER);
                        if (waitForLoading)
                        {
                            if (!isLoading(dashlet))
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }

                }
                catch (NoSuchElementException e)
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
        return this;
    }

    private boolean isLoading(WebElement dashletPlaceholder)
    {
        try
        {
            WebElement sitesDash = dashletPlaceholder.findElement(By.cssSelector(DASHLET_EMPTY_PLACEHOLDER));
            if (sitesDash.isDisplayed() && sitesDash.getText().startsWith("Loading..."))
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Delete site from the delete symbol of My Meeting Workspaces Dashlets.
     * 
     * @param siteName
     * @return
     */
    public HtmlPage deleteSite(String siteName)
    {
        if (siteName == null)
        {
            throw new UnsupportedOperationException("Name of the site is required");
        }
        try
        {
            List<WebElement> elements = drone.findAll(By.cssSelector(ROW_OF_SITE));
            for (WebElement webElement : elements)
            {
                if (webElement.findElement(By.cssSelector(SITE_NAME_IN_ROW)).getText().equals(siteName))
                {
                    drone.mouseOver(webElement);
                    webElement.findElement(By.cssSelector(DELETE_SYMB_IN_ROW)).click();
                    confirmDelete();
                    drone.refresh();
                    return FactorySharePage.resolvePage(drone);
                }
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("My Site  Dashlet is not present", nse);
        }
        throw new PageOperationException("My Site  Dashlet is not present in the page.");
    }

    /**
     * Action of selecting ok on confirm delete pop up dialog.
     */
    private void confirmDelete()
    {
        try
        {
            WebElement confirmDelete = drone.find(By.cssSelector(DELETE_CONFIRM));
            confirmDelete.click();
            confirmDelete = drone.find(By.cssSelector(DELETE_RE_CONFIRM));
            confirmDelete.click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Delete dialouge not present");
        }

    }

    /**
     * Return <code>true</code> if the No meeting workspaces to displayed on screen.
     * 
     * @return boolean present
     */
    public boolean isNoMeetingWorkspaceDisplayed()
    {
        boolean present = false;
        try
        {
            present = drone.findAndWait(By.cssSelector(NO_MEETINGS_DISPLAYED)).getText().equals(drone.getValue("dashlet.meeting.workspaces.nomeetings"));
            return present;
        }
        catch (NoSuchElementException e)
        {
        }

        return present;
    }

    /**
     * Delete site from the delete symbol of My Meeting Workspaces Dashlets.
     * 
     * @param siteName
     * @return
     */
    public void selectFavoriteSite(String siteName)
    {
        if (siteName == null)
        {
            throw new UnsupportedOperationException("Name of the site is required");
        }
        try
        {
            List<WebElement> elements = drone.findAll(By.cssSelector(ROW_OF_SITE));
            for (WebElement webElement : elements)
            {
                if (webElement.findElement(By.cssSelector(SITE_NAME_IN_ROW)).getText().equals(siteName))
                {
                    webElement.findElement(By.cssSelector(FAVORITE_SITE)).click();
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element not found", nse);
        }
    }

    /**
     * Checks the site is favourite.
     * 
     * @param siteName Site Name checked for is in Favourite.
     * @return
     */
    public boolean isSiteFavourite(String siteName)
    {
        if (siteName == null)
        {
            throw new UnsupportedOperationException("Name of the site is required");
        }
        try
        {
            List<WebElement> elements = drone.findAll(By.cssSelector(ROW_OF_SITE));
            for (WebElement webElement : elements)
            {
                if (webElement.findElement(By.cssSelector(SITE_NAME_IN_ROW)).getText().equals(siteName))
                {
                    return webElement.findElement(By.cssSelector(FAVORITE_SITE_ENABLED)).isDisplayed();
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element not found", nse);
        }
        return false;
    }

}
