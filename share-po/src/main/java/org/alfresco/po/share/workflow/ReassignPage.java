package org.alfresco.po.share.workflow;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Sergey Kardash
 */
public class ReassignPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ReassignPage.class);

    @SuppressWarnings("unused")
    private static final int LOADING_WAIT_TIME = 2000;
    private static final By SEARCH_PEOPLE = By.cssSelector("input[id$='peopleFinder-search-text']");
    private static final By SEARCH_BUTTON = By.cssSelector("button[id$='peopleFinder-search-button-button']");
    private final By closeButton = By.cssSelector("div[id$='_default-reassignPanel']>a.container-close");
    private static final By LIST_REASSIGN = (By.cssSelector("table>tbody.yui-dt-data>tr"));
    private static final By SELECT_REASSIGN = By.cssSelector("td[class$='actions yui-dt-last']>div>span>span>span>button");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public ReassignPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReassignPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(SEARCH_PEOPLE), getVisibleRenderElement(SEARCH_BUTTON), getVisibleRenderElement(closeButton));
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReassignPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReassignPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to select a single user for reassign
     * test is EditTaskPageTest.selectReassign
     * 
     * @param userName String
     * @return HtmlPage
     */
    public HtmlPage selectUser(String userName)
    {

        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name can't empty or null.");
        }
        List<WebElement> elements = retrieveUsers(userName);

        userName = userName.toLowerCase();
        for (WebElement webElement : elements)
        {

            if (webElement.findElement(By.cssSelector(".itemname>a")).getText().toLowerCase().contains(userName))
            {
                drone.mouseOver(webElement.findElement(SELECT_REASSIGN));
                webElement.findElement(SELECT_REASSIGN).click();
                break;
            }
        }
        waitUntilAlert();
        return drone.getCurrentPage().render();
    }

    /**
     * Method to get the list of assignees
     * 
     * @return List of users
     */
    public List<WebElement> retrieveUsers(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("User Name can't empty or null.");
        }
        try
        {
            searchForUser(userName);
            //drone.waitForElement(LIST_REASSIGN, SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
//            drone.waitForElement(LIST_REASSIGN, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return drone.findDisplayedElements(LIST_REASSIGN);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding element", toe);
        }
        throw new PageException();
    }

    /**
     * Method to search for given user
     * 
     * @param userName String
     */
    public void searchForUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("UserName cannot be null");
        }
        try
        {
            clearSearchField();
            getVisibleElement(SEARCH_PEOPLE).sendKeys(userName);
            selectSearchButton();
            try
            {
                drone.waitForElement(LIST_REASSIGN, SECONDS.convert(WAIT_TIME_3000, MILLISECONDS));
                waitUntilAlert(5);
            }
            catch (TimeoutException toe)
            {
            }
            // drone.waitFor(LOADING_WAIT_TIME);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Element Not found", nse);
        }
        catch (TimeoutException toe)
        {
            logger.error("Timed out: ", toe);
        }
    }

    /**
     * Method to clear Search Field
     */
    public void clearSearchField()
    {
        try
        {
            drone.findFirstDisplayedElement(SEARCH_PEOPLE).clear();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Unable to find Search Field", nse);
            }
        }
    }

    /**
     *
     */
    public void selectSearchButton()
    {
        try
        {
            getVisibleElement(SEARCH_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find Search button", nse);
        }
    }
}