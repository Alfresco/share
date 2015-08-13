/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.dashlet;

import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.openqa.selenium.WebDriver;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class holds the elements of Insert/Edit Anchor page and which is invoked from Site Notice Tiny MCE editor dialog.
 * 
 * @author cbairaajoni
 * 
 */
public class InsertOrEditAnchorPage extends BaseAdvancedTinyMceOptionsPage
{
    private static Log logger = LogFactory.getLog(InsertOrEditAnchorPage.class);

    @RenderWebElement
    private static By NAME_CSS = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'Name')]/following-sibling::input[starts-with(@class, 'mce-textbox')]");

    /**
     * Constructor.
     * 
     * @param drone WebDrone
     * @param element WebElement
     */
    public InsertOrEditAnchorPage(WebDriver driver, WebElement element)
    {
        super(driver, element);
    }

    @SuppressWarnings("unchecked")
    public InsertOrEditAnchorPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public InsertOrEditAnchorPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method sets the given text into name.
     * 
     * @param text String
     */
    public void setName(String text)
    {
        if (text == null)
        {
            throw new IllegalArgumentException("Name is required");
        }

        try
        {
            findAndWait(NAME_CSS).sendKeys(text);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the NAME field.", te);
            throw new PageOperationException("Unable to find NAME field.", te);
        }
    }
}
