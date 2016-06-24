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
package org.alfresco.po.share.site.calendar;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * @author Sergey Kardash
 */
public class DeleteEventForm extends AbstractEventForm
{

    private Log logger = LogFactory.getLog(this.getClass());

    private static final By DELETE_CONFIRM = By.cssSelector("span[class=button-group] span span button[id$='-button']");

    @SuppressWarnings("unchecked")
    @Override
    public DeleteEventForm render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public DeleteEventForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public HtmlPage confirmDeleteEvent()
    {
        try
        {
            findAndWait(DELETE_CONFIRM).click();
            logger.info("Click delete event confirmation button");
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to locate delete Event button");
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        return getCurrentPage();
    }
}
