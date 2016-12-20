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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

    private static final By SEARCH_FIELD = By.cssSelector("div[id$='sentinvites'] input[id$='default-search-text']");
    private static final By SEARCH_BTN = By.cssSelector("button[id$='search-button-button']");
    private static final By CANCEL_BTN = By.cssSelector(".yui-button-button");
    private static final By LIST_OF_USERS = By.cssSelector("tbody.yui-dt-data>tr");
    private static final By USER_NAME_FROM_LIST = By.cssSelector(".attr-value>span");
    private static final By REQUEST_SEARCH_FIELD = By.cssSelector("div[id$='pendingrequests'] input[id$='default-search-text']");
    private static final By REQUEST_SEARCH_BTN = By.cssSelector("div[id$='pendingrequests'] button[id$='search-button-button']");
    private static final By REQUEST_LIST_OF_USERS = By.cssSelector("tbody.yui-dt-data>tr");
    private static final By USER_NAME_FROM_REQUEST_LIST = By.cssSelector("tbody.yui-dt-data>tr>td.yui-dt6-col-person span[class='attr-value']>a");
	private static final By VIEW_BUTTON = By.cssSelector(".yui-dt-data .yui-dt-rec .yui-dt-col-actions .yui-button-button>span>button");
	private static final By ACCEPT_BUTTON = By.cssSelector(".yui-dt-data .yui-dt-rec .yui-dt-last .yui-button-button>span>button");
	        

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
    
    /**
     * This method gets the list of all the users whose requests are pending.
     *
     * @return List<WebElement>
     */
    public List<WebElement> getRequests()
    {
        try
        {            
        	WebElement button = findAndWait(REQUEST_SEARCH_BTN);
        	button.click();        	
            return findAndWaitForElements(REQUEST_LIST_OF_USERS);
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
     * Verify user name displayed in the Manage Pending request list
     *
     * @return boolean
     */
    public boolean isUserNameDisplayedInList(String username)
    {
        try
        {	  	
            for (WebElement listOfUsers : findAndWaitForElements(USER_NAME_FROM_REQUEST_LIST))
              {
                  if (listOfUsers.getText().equalsIgnoreCase(username+" "+username))
                  {
                     return true;	                     
                  }
              } 
            
        }
        catch (TimeoutException e )
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No such user name is displayed" + e);               
            }
        }

        return false;
    }
       
    /**
     * This method helps to click on view button on the required user from the list of 
     * list of users
     * @param username
     * @return HtmlPage
     */
	
    public HtmlPage viewRequest(String username) 
	{
		try 
		{
			List<WebElement> searchResults = getRequests();
			if (username == null || searchResults == null || searchResults.isEmpty()) 
			{
				throw new UnsupportedOperationException("user input required or no request users are retrieved");
			}
			for (WebElement requestList : searchResults) 
			{
				WebElement request = requestList.findElement(USER_NAME_FROM_REQUEST_LIST);
				String text = request.getText();
				if (text != null && !text.isEmpty()) 
				{
					if (text.indexOf(username) != -1) 
					{
						requestList.findElement(VIEW_BUTTON).click();
						return getCurrentPage();
					}
				}
			}
			throw new PageException("View Button not found");
		} 
		catch (NoSuchElementException e) 
		{
			logger.error("No request users are retrieved : " + USER_NAME_FROM_REQUEST_LIST.toString());
			throw new PageException("No request users are retrieved:", e);
		}

	}
    
    /**
     * This method helps to click on Approve button on the required user from the list of 
     * list of users
     * @param username
     * @return HtmlPage
     */
    public HtmlPage approveRequest(String username)
    {
        try 
        {
			List<WebElement> searchResults = getRequests();
			if (username == null || searchResults == null || searchResults.isEmpty())
			{
			    throw new UnsupportedOperationException("user input required or no request users are retrieved");
			}
			for (WebElement requestList : searchResults)
			{
			    WebElement request = requestList.findElement(USER_NAME_FROM_REQUEST_LIST);
			    String text = request.getText();
			    if (text != null && !text.isEmpty())
			    {			       
			        if (text.indexOf(username) != -1)
			        {
			        	requestList.findElement(ACCEPT_BUTTON).click();
			            return getCurrentPage();
			        }
			    }
			}
			throw new PageException("Accept Button not found");
		} 
        catch (NoSuchElementException e) 
        {
			logger.error("No request users are retrieved : " + USER_NAME_FROM_REQUEST_LIST.toString());
			throw new PageException("No request users are retrieved", e);
		}
	
    }
    
    /**
     * Mimic to search for specific user Requested on ManagePendingRequestpage.
     *
     * @param searchText
     */
	public PendingInvitesPage searchRequest(String searchText) 
	{
		try 
		{
			checkNotNull(searchText);
			WebElement inputField = findAndWait(REQUEST_SEARCH_FIELD);
			inputField.clear();
			inputField.sendKeys(searchText);
			WebElement searchButton = findAndWait(REQUEST_SEARCH_BTN);
			searchButton.click();
			return this;
		} 
		catch (NoSuchElementException | TimeoutException  ne) 
		{
			logger.error("REQUEST_SEARCH_BTN not available : " + REQUEST_SEARCH_BTN.toString());
			throw new PageException("Not able to find the request search button.", ne);
		}		

	}
    
    /**
     * Methods used to cancel the invitation
     *
     * @param username
     */

    public void Invitation(String username)
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
    
    

}
