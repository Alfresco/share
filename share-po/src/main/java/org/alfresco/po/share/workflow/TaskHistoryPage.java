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
package org.alfresco.po.share.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * This page represents the Task History details page.
 * 
 * @author Chiran
 * @since 1.7.1
 */
public class TaskHistoryPage extends AbstractWorkFlowTaskDetailsPage
{
    //private static final By MY_TASKS_LIST_LINK = By.cssSelector("span>a[href*='workflows|active']");

    private static final By ALL_FIELD_LABELS = By.cssSelector("span[class$='viewmode-label']");

    private final Log logger = LogFactory.getLog(this.getClass());

    //private RenderElement myTasksListLink = getVisibleRenderElement(MY_TASKS_LIST_LINK);

    @Override
    public TaskHistoryPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getMenuTitle(), getWorkflowDetailsHeader(), getFormFieldsElements());
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @Override
    public TaskHistoryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    /**
     * Method to get All labels from Workflow Form
     * 
     * @return List<String>
     */
    public List<String> getAllLabels()
    {
        List<String> labels = new ArrayList<String>();
        try
        {
            List<WebElement> webElements = driver.findElements(ALL_FIELD_LABELS);
            for (WebElement label : webElements)
            {
                labels.add(label.getText());
            }
            return labels;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No labels found", nse);
            }
        }
        return labels;
    }
}
