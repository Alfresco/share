package org.alfresco.po.share.site;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to represent Pending Invites page
 *
 * @author Marina.Nenadovets
 */
public class PendingInvitesPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final By SEARCH_FIELD = By.cssSelector("input[id$='default-search-text']");
    private static final By SEARCH_BTN = By.cssSelector("button[id$='search-button-button']");
    private static final By CANCEL_BTN = By.cssSelector(".yui-button-button");
    private static final By LIST_OF_USERS = By.cssSelector("tbody.yui-dt-data>tr");
    private static final By USER_NAME_FROM_LIST = By.cssSelector(".attr-value>span");

    public PendingInvitesPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PendingInvitesPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(SEARCH_FIELD),
                getVisibleRenderElement(SEARCH_BTN));

        return this;
    }

    @SuppressWarnings("unchecked")
    public PendingInvitesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public PendingInvitesPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * This method searches all the users whose invites are pending.
     *
     * @return List<WebElement>
     */
    public List<WebElement> getInvitees()
    {
        try
        {
            drone.findAndWait(SEARCH_BTN).click();
            return drone.findAndWaitForElements(LIST_OF_USERS);
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time exceeded to find the invitees list." + e);
            }
        }

        return Collections.emptyList();
    }

    /**
     * Methods used to cancel the invitation
     *
     * @param username
     */

    public void cancelInvitation(String username)
    {
        List<WebElement> searchResults = getInvitees();
        if (username == null || searchResults == null || searchResults.isEmpty())
        {
            throw new UnsupportedOperationException("user input required or no invites are retrieved");
        }
        for (WebElement inviteeList : searchResults)
        {
            WebElement invitee = inviteeList.findElement(USER_NAME_FROM_LIST);
            String text = invitee.getText();
            if (text != null && !text.isEmpty())
            {
                if (text.equalsIgnoreCase("(" + username + ")"))
                {
                    inviteeList.findElement(CANCEL_BTN).click();
                    break;
                }
            }
        }
    }

    /**
     * Mimic serach invitation on page.
     *
     * @param searchText
     */
    public void search(String searchText)
    {
        checkNotNull(searchText);
        WebElement inputField = drone.findAndWait(SEARCH_FIELD);
        inputField.clear();
        inputField.sendKeys(searchText);
        WebElement searchButton = drone.findAndWait(SEARCH_BTN);
        searchButton.click();
    }
}
