/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.dashlet;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
@FindBy(css="div[id*='DASHLET']")
/**
 * SiteContentBreakdownDashlet page object for site content breakdown report dashlet
 * 
 * @author jcule
 */
public class SiteContentBreakdownDashlet extends AbstractDashlet implements Dashlet
{

    private static Log logger = LogFactory.getLog(SiteContentBreakdownDashlet.class);

    private static final String PIE_CHART_SLICES = "path[transform]";
    private static final String TOOLTIP_DATA = "div[id^='tipsyPvBehavior']";
    private static final String ORIGINAL_TITLE_ATTRIBUTE = "original-title";

    /**
     * Gets the list of files data appearing in tooltips (file type-count) 
     * @return List<String>
     */
    public List<String> getTooltipFileData()throws Exception
    {
        List<WebElement> pieChartSlices = getPieChartSlices();
        List<String> toolTipData = new ArrayList<String>();
        for (WebElement pieChartSlice : pieChartSlices)
        {
            mouseOver(pieChartSlice);
            WebElement tooltipElement = findAndWait(By.cssSelector(TOOLTIP_DATA));
            String fileType = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/div/strong");
            String items = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/div/text()[preceding-sibling::br]");
            String [] counts = items.split(" ");
            String fileCount = counts[0];
            StringBuilder builder = new StringBuilder();
            builder.append(fileType).append("-").append(fileCount);
            toolTipData.add(builder.toString());
        }   
        return toolTipData;
    }
    
    
    /**
     * Gets the list of file types data appearing in tooltips  
     * @return List<String>
     */
    public List<String> getTooltipFileTypes() throws Exception
    {
        List<WebElement> pieChartSlices = getPieChartSlices();
        List<String> toolTipFileTypes = new ArrayList<String>();
        for (WebElement pieChartSlice : pieChartSlices)
        {
            mouseOver(pieChartSlice);
            WebElement tooltipElement = findAndWait(By.cssSelector(TOOLTIP_DATA));
            String fileType = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/div/strong");
            toolTipFileTypes.add(fileType);
        }   
        return toolTipFileTypes;
    }
    
    /**
     * Gets the list of pie chart slices elements
     * 
     * @return List<WebElement>
     */
    private List<WebElement> getPieChartSlices()
    {
        List<WebElement> pieChartSlices = new ArrayList<WebElement>();
        try
        {
            pieChartSlices = driver.findElements(By.cssSelector(PIE_CHART_SLICES));

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Site Content Report pie chart slices " + nse);
        }
        return pieChartSlices;
    }


    @Override
    @SuppressWarnings("unchecked")
    public SiteContentBreakdownDashlet render(RenderTime timer)
    {
        return this;
    }
    @SuppressWarnings("unchecked")
    @Override
    public SiteContentBreakdownDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
