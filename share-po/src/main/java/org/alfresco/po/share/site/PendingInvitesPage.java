/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.site;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.Collections;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
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

    /**
     * This method searches all the users whose invites are pending.
     *
     * @return List<WebElement>
     */
    public List<WebElement> getInvitees()
    {
        try
        {
            findAndWait(SEARCH_BTN).click();
            return findAndWaitForElements(LIST_OF_USERS);
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
                //if (text.equalsIgnoreCase("(" + username + ")"))
                if (text.indexOf(username) != -1)
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
        WebElement inputField = findAndWait(SEARCH_FIELD);
        inputField.clear();
        inputField.sendKeys(searchText);
        WebElement searchButton = findAndWait(SEARCH_BTN);
        searchButton.click();
    }
}
