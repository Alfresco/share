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

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This page object is to set the image into site notice dashlet. This Page holds all the required element finding methods.
 *
 * @author cbairaajoni
 */
public class InsertOrEditImagePage extends BaseAdvancedTinyMceOptionsPage
{
    private static Log logger = LogFactory.getLog(InsertOrEditImagePage.class);

    @RenderWebElement
    private static By IMAGE_URL_CSS = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'Source')]/following-sibling::div/input[starts-with(@class, 'mce-textbox')]");
    @RenderWebElement
    private static By IMAGE_DESC_CSS = By.xpath("//div[starts-with(@class, 'mce-container-body')]/label[contains(text(), 'description')]/following-sibling::input[starts-with(@class, 'mce-textbox')]");
    @RenderWebElement
    private static By DIMENSIONS_HEIGHT_CSS2 = By.xpath("//input[starts-with(@class, 'mce-textbox') and @aria-label='Height']");
    @RenderWebElement
    private static By DIMENSIONS_WIDTH_CSS1 = By.xpath("//input[starts-with(@class, 'mce-textbox') and @aria-label='Width']");


    /**
     * Constructor.
     *
     * @param element
     */
    public InsertOrEditImagePage(WebDrone drone, WebElement element)
    {
        super(drone, element);
    }

    /**
     * This enum is used to describe the target items present on alignment dropdown.
     */
    public enum ImageAlignment
    {
        NOT_SET("-- Not Set --"),
        BASE_LINE("Baseline"),
        TOP("Top"),
        MIDDLE("Middle"),
        BOTTOM("Bottom"),
        TEXT_TOP("Text Top"),
        TEXT_BOTTOM("Text Bottom"),
        LEFT("Left"),
        RIGHT("Right");

        private String itemName;

        private ImageAlignment(String name)
        {
            itemName = name;
        }

        public String getItemName()
        {
            return itemName;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditImagePage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditImagePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InsertOrEditImagePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method sets the given text into Link Url.
     *
     * @param url
     */
    public void setImageUrl(String url)
    {
        if (StringUtils.isEmpty(url))
        {
            throw new IllegalArgumentException("Link url value is required");
        }

        try
        {
            WebElement imageUrlField = drone.findAndWait(IMAGE_URL_CSS);
            imageUrlField.clear();
            imageUrlField.sendKeys(url);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the image Url field.", te);
            throw new PageOperationException("Unable to find image Url field.", te);
        }
    }

    /**
     * This method sets the given description into image description field.
     *
     * @param desc
     */
    public void setDescription(String desc)
    {
        if (desc == null)
        {
            throw new IllegalArgumentException("Description should not be null");
        }

        try
        {
            WebElement descriptionField = drone.findAndWait(IMAGE_DESC_CSS);
            descriptionField.clear();
            descriptionField.sendKeys(desc);
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the image descrption field.", te);
            throw new PageOperationException("Unable to find image descrption field.");
        }
    }

    //    /**
    //     * This method sets the given Target item from the Target dropdown values.
    //     *
    //     * @param target
    //     */
    //    public void setAlignment(ImageAlignment target)
    //    {
    //        if (target == null)
    //        {
    //            throw new IllegalArgumentException("Alignment value is required");
    //        }
    //
    //        try
    //        {
    //            selectOption(ALIGNMENT_CSS, target.getItemName());
    //        }
    //        catch (TimeoutException te)
    //        {
    //            logger.info("Unable to find the Alignment Item field.", te);
    //            throw new PageOperationException("Unable to find Alignment Item field.");
    //        }
    //    }

    /**
     * This method sets the given description into image description field.
     *
     * @param width
     * @param height
     */
    public void setDimensions(long width, long height)
    {
        if (width < 0 || height < 0)
        {
            throw new IllegalArgumentException("Width or Height of Image values should not be less than 0");
        }

        try
        {
            WebElement widthElement = drone.findAndWait(DIMENSIONS_WIDTH_CSS1);
            widthElement.clear();
            widthElement.sendKeys(Long.valueOf(width).toString());
            WebElement heightElement = drone.findAndWait(DIMENSIONS_HEIGHT_CSS2);
            heightElement.clear();
            heightElement.sendKeys(Long.valueOf(height).toString());
        }
        catch (TimeoutException te)
        {
            logger.info("Unable to find the image dimensions field.", te);
            throw new PageOperationException("Unable to find image dimensions field.", te);
        }
    }
}