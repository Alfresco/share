package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.*;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Page object to hold data list dashlet
 *
 * @author Marina.Nenadovets
 */
public class SiteDataListsDashlet extends AbstractDashlet implements Dashlet
{
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.site-data-lists");
    private static final By CREATE_DATA_LIST = By.cssSelector("a[href='data-lists#new']");
    private static final By DATA_LIST_IN_DASHLET = By.cssSelector("div#list>a");

    /**
     * Constructor.
     */
    protected SiteDataListsDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector(".yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteDataListsDashlet render(RenderTime timer)
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
                    this.dashlet = drone.findAndWait((DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
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
    public SiteDataListsDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteDataListsDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Content Dashlet.
     */
    private void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to verify whether create data list link is available
     *
     * @return boolean
     */
    public boolean isCreateDataListDisplayed()
    {
        try
        {
            getFocus();
            return drone.isElementDisplayed(CREATE_DATA_LIST);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to click Create Data List button
     *
     * @return NewListForm page object
     */

    public NewListForm clickCreateDataList()
    {
        try
        {
            getFocus();
            drone.findAndWait(CREATE_DATA_LIST).click();
            return new NewListForm(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + CREATE_DATA_LIST);
        }
    }


    /**
     * Get Count displayed in dashlet data-lists.
     *
     * @return
     */
    public int getListsCount()
    {
        return getListElements().size();
    }

    /**
     * true if data-list with name 'dataListName' displayed in dashlet
     *
     * @param dataListName
     * @return
     */
    public boolean isDataListDisplayed(String dataListName)
    {
        checkNotNull(dataListName);
        List<WebElement> eventLinks = getListElements();
        for (WebElement eventLink : eventLinks)
        {
            if (eventLink.getText().contains(dataListName))
            {
                return eventLink.isDisplayed();
            }
        }
        return false;
    }


    private List<WebElement> getListElements()
    {
        try
        {
            return drone.findAndWaitForElements(DATA_LIST_IN_DASHLET);
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
    }
}
