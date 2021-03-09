/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
package org.alfresco.po.share.search;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.ShareDialogueAikau;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class SearchConfirmDeletePage extends ShareDialogueAikau
{
    
    private final Log logger = LogFactory.getLog(SearchConfirmDeletePage.class);

    private static final By DELETE_POPUP = By.cssSelector("#ALF_DELETE_CONTENT_DIALOG");
    
    private static final By CONFIRM_DELETE_TITLE = By.cssSelector("span[id$=title]");

	private static final By DELETE_BUTTON = By.cssSelector("[widgetid='ALF_DELETE_CONTENT_DIALOG_CONFIRMATION']");
	private static final By CANCEL_BUTTON = By.cssSelector("[widgetid='ALF_DELETE_CONTENT_DIALOG_CANCELLATION']");
	
    @SuppressWarnings("unchecked")
    @Override
    public SearchConfirmDeletePage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(DELETE_POPUP), getVisibleRenderElement(CONFIRM_DELETE_TITLE));
        }
        catch (NoSuchElementException e)
        {
            logger.error(DELETE_POPUP + "or" + CONFIRM_DELETE_TITLE + " not found!", e);

        }
        catch (TimeoutException e)
        {
            logger.error(DELETE_POPUP + "or" + CONFIRM_DELETE_TITLE + " not found!", e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchConfirmDeletePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    
    public HtmlPage clickDelete()
    {
    	String deleteButton = factoryPage.getValue("delete.button.label");
    	return confirmDeleteOrCancel(deleteButton);
    }

    public HtmlPage clickCancel()
    {
    	String cancelButton = factoryPage.getValue("button.text.cancel");
    	return confirmDeleteOrCancel(cancelButton);
    }
    
	private HtmlPage confirmDeleteOrCancel(String option) 
	{
		String deleteButton = factoryPage.getValue("delete.button.label");
		By button = DELETE_BUTTON;
		if (option != deleteButton)
		{
			button = CANCEL_BUTTON;
		}
		
		try 
		{
			WebElement dialog = findAndWait(DELETE_POPUP);
			dialog.click();
			WebElement selectedButton = findAndWait(button);
			selectedButton.click();
			return factoryPage.getPage(driver);
		}		
		catch (NoSuchElementException | TimeoutException e) 
		{
			logger.error(" : " + "Not able to find Cancel/Delete Button", e);
			throw new PageException("Not able to find Cancel/Delete Button", e);
		}
		
	}
	
	
}
