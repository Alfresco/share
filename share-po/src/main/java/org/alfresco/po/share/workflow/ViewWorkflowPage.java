/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.workflow;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * This page represents the view workflow page/workflow details page.
 * 
 * @author Abhijeet Bharade
 * @since 1.7.1
 */
public class ViewWorkflowPage extends SharePage
{

    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-cancel-button']");
    private static final By COMMENT_TEXT = By.cssSelector("div[id$='default-recentTaskOwnersComment']");
    private static final By ALL_FIELD_LABELS = By.cssSelector("span[class$='viewmode-label']");

    private final Log logger = LogFactory.getLog(this.getClass());


    @SuppressWarnings("unchecked")
    @Override
    public ViewWorkflowPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, 
                        RenderElement.getVisibleRenderElement(COMMENT_TEXT),
                        RenderElement.getVisibleRenderElement(CANCEL_BUTTON));
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ViewWorkflowPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    /**
     * Selects the Cancel workflow button.
     * 
     * @return {@link HtmlPage} - instance of my task page.
     */
    public HtmlPage selectCancelWorkflowButton()
    {
        findAndWait(CANCEL_BUTTON).click();
        findAndWait(By.cssSelector("#prompt span.button-group>span:first-of-type button")).click();
        waitUntilElementDisappears(CANCEL_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
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

    /**
     * Get comments for the workflow
     * Not required for now.
     * 
     * @return - Comment string.
     *         public String getComments()
     *         {
     *         return driver.findElement(COMMENT_TEXT).getText();
     *         }
     */

}
