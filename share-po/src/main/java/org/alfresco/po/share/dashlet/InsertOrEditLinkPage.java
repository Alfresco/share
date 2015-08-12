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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This class holds the elements of Insert/Edit Link page and which is invoked from Site Notice Tiny MCE editor dialog.
 * @author cbairaajoni
 *
 */
public class InsertOrEditLinkPage extends BaseAdvancedTinyMceOptionsPage
{
    private static Log logger = LogFactory.getLog(InsertOrEditLinkPage.class);
    
    @RenderWebElement
    private static By LINK_URL = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'Url')]/following-sibling::div/input[starts-with(@class, 'mce-textbox')]");
    @RenderWebElement
    private static By TEXT_TO_DISPLAY = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'Text to display')]/following-sibling::input[starts-with(@class, 'mce-textbox')]");
    @RenderWebElement
    private static By TITLE = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'Title')]/following-sibling::input[starts-with(@class, 'mce-textbox')]");
    @RenderWebElement
    private static By TARGET_LIST = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'Target')]/following-sibling::div/button");

    /**
     * Constructor.
     * @param drone WebDrone
     * @param element WebElement
     */
    public InsertOrEditLinkPage(WebDriver driver, WebElement element)
    {
        super(driver, element);
    }

    /**
     * This enum is used to describe the target items present on Target dropdown.
     */
    public enum InsertLinkPageTargetItems
    {
        NONE ("None"),
        NEW_WINDOW ("New window");
        
        private String itemName;
        
        private InsertLinkPageTargetItems(String name)
        {
            itemName = name;
        }
        
        public String getItemName() {
            return itemName;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditLinkPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditLinkPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
    /**
     * This method sets the given text into Link Url.
     * 
     * @param text String
     */
    public void setLinkUrl(String text)
    {
        if(text == null)
        {
            throw new IllegalArgumentException("Link url value is required");
        }
        
        try
        {
            findAndWait(LINK_URL).sendKeys(text);
        }
        catch(TimeoutException te)
        {
            logger.info("Unable to find the Link Url field.", te);
            throw new PageOperationException("Unable to find Link Url field.", te);
        }
    }
    
    /**
     * This method sets the given text into title.
     * 
     * @param text String
     */
    public void setTitle(String text)
    {
        if(text == null)
        {
            throw new IllegalArgumentException("Title is required");
        }
        
        try
        {
            WebElement title = findAndWait(TITLE);
            title.clear();
            title.sendKeys(text);
        }
        catch(TimeoutException te)
        {
            logger.info("Unable to find the Title field.", te);
            throw new PageOperationException("Unable to find Title field.", te);
        }
    }
    
    /**
     * This method sets the given Target item from the Target dropdown values.
     * 
     * @param target InsertLinkPageTargetItems
     */
    public void setTarget(InsertLinkPageTargetItems target)
    {
        if(target == null)
        {
            throw new IllegalArgumentException("Target value is required");
        }
        
        try
        {
            selectTarget(TARGET_LIST, target.getItemName());
        }
        catch(TimeoutException te)
        {
            logger.info("Unable to find the Target Item field.", te);
            throw new PageOperationException("Unable to find Target Item field.", te);
        }
    }

    /**
     * Util method to select given Target.
     * @param by By
     * @param text String
     */
    private void selectTarget(By by, String text)
    {
        try
        {
            driver.findElement(by).click();
            findAndWait(By.xpath("//div[contains(@class, 'mce-stack-layout')]/div/span[contains(text(), '" + text + "')]")).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("Unable to find Target dropdown box");
            throw new PageOperationException("Unable to find Target dropdown box", nse);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find Target");
            throw new PageOperationException("Unable to find Target", te);
        }
    }
}
