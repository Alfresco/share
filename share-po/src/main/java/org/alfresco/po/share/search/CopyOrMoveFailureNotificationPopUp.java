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

public class CopyOrMoveFailureNotificationPopUp extends ShareDialogueAikau
{
    
    private final Log logger = LogFactory.getLog(CopyOrMoveFailureNotificationPopUp.class);

    private static final By NOTIFICATION_POPUP = By.cssSelector("#NOTIFICATION_PROMPT");
    
    private static final By CONFIRM_NOTIFICATION_TITLE = By.cssSelector("#NOTIFICATION_PROMPT_title");

	private static final By OK_BUTTON = By.id("NOTIFCATION_PROMPT_ACKNOWLEDGEMENT_label");
	
    @SuppressWarnings("unchecked")
    @Override
    public CopyOrMoveFailureNotificationPopUp render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(NOTIFICATION_POPUP), getVisibleRenderElement(CONFIRM_NOTIFICATION_TITLE));
        }
        catch (NoSuchElementException e)
        {
            logger.error(NOTIFICATION_POPUP + "or" + CONFIRM_NOTIFICATION_TITLE + " not found!", e);

        }
        catch (TimeoutException e)
        {
            logger.error(NOTIFICATION_POPUP + "or" + CONFIRM_NOTIFICATION_TITLE + " not found!", e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CopyOrMoveFailureNotificationPopUp render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Select "ok".
     * 
     * @param action
     * @return - HtmlPage
     */
	public HtmlPage selectOk() 
	{
		try 
		{
			WebElement selectok = driver.findElement(OK_BUTTON);

			if (selectok.isEnabled() && selectok.isDisplayed()) 
			{
                WebElement dialog = findAndWait(By.id("NOTIFICATION_PROMPT"));
                dialog.click();
			    selectok.click();
				return factoryPage.getPage(driver);
			}
			
			throw new PageException("Ok Button found, but is not enabled");
		} 
		catch (TimeoutException e) 
		{
			logger.error(" : " + "Not able to find Ok Button");
			throw new PageException("Not able to find Ok Button", e);
		}
	}
    
}



