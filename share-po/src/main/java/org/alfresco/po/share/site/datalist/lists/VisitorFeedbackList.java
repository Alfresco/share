package org.alfresco.po.share.site.datalist.lists;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.search.SearchResult;
import org.alfresco.po.share.search.SearchResultItem;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.items.VisitorFeedbackRow;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to hold elements of Visitor Feedback List
 * 
 * @author Cristina.Axinte
 */

public class VisitorFeedbackList extends DataListPage
{
    private static final Log logger = LogFactory.getLog(VisitorFeedbackList.class);

    public VisitorFeedbackList(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public VisitorFeedbackList render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LIST_TABLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public VisitorFeedbackList render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public VisitorFeedbackList render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to verify whether item is available
     * 
     * @param visitorEmail
     * @return boolean
     */
    public boolean isItemWithEmailDisplayed(String visitorEmail)
    {
        boolean isDisplayed;
        try
        {
            WebElement theItem = locateAnItem(visitorEmail);
            isDisplayed = theItem.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (TimeoutException nse)
        {
            return false;
        }
        return isDisplayed;
    }

    /**
     * Method to return the row for a specific visitor email
     * 
     * @param visitorEmail
     * @return VisitorFeedbackRow
     */
    public VisitorFeedbackRow getRowForVisitorEmail(String visitorEmail)
    {
        VisitorFeedbackRow rowSearched = null;
        try
        {
            WebElement element = locateAnItem(visitorEmail);
            rowSearched = new VisitorFeedbackRow(element, drone);
        }
        catch (NoSuchElementException nse)
        {
        }
        return rowSearched;
    }

    /**
     * Method to return the row for a specific visitor values
     * 
     * @param visitorEmail
     * @return VisitorFeedbackRow
     */
    public VisitorFeedbackRow getRowForSpecificValues(String visitorEmail, String visitorComment, String visitorName, String visitorWebSite)
    {
        VisitorFeedbackRow rowSearched = null;

        if (!isNoListFoundDisplayed())
        {
            try
            {
                List<WebElement> elements = drone.findAll(By.cssSelector("table>tbody.yui-dt-data tr"));
                for (WebElement element : elements)
                {
                    rowSearched = new VisitorFeedbackRow(element, drone);
                    if (rowSearched.getVisitorEmail().equals(visitorEmail) && rowSearched.getVisitorComment().equals(visitorComment)
                            && rowSearched.getVisitorName().equals(visitorName) && rowSearched.getVisitorWebsite().equals(visitorWebSite))
                    {
                        return rowSearched;                        
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
            }
        }
        return rowSearched;
    }

    /**
     * Gets all items as a collection of SearResultItems.
     * 
     * @return Collections of search result
     */
    public List<VisitorFeedbackRow> getAllFeedbackRows()
    {
        List<VisitorFeedbackRow> allRows = new ArrayList<VisitorFeedbackRow>();
        if (!isNoListFoundDisplayed())
        {
            try
            {
                List<WebElement> elements = drone.findAll(By.cssSelector("table>tbody.yui-dt-data tr"));
                for (WebElement element : elements)
                {
                    allRows.add(new VisitorFeedbackRow(element, drone));
                }
            }
            catch (NoSuchElementException nse)
            {
            }
        }
        return allRows;
    }
}
