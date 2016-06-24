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
package org.alfresco.po.share.site.contentrule.createrules;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Maryia Zaichanka
 */
public class SetPropertyValuePage extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(SetPropertyValuePage.class);

    private final RenderElement headerElement = getVisibleRenderElement(By
            .cssSelector("div[id*='selectSetPropertyDialog-dialog_h']"));

    private final By propertyFoldersListCss = By.cssSelector("span[class$='ygtvlabel']");
    private final By setValueOkButtonCss = By
            .cssSelector("span[id$='selectSetPropertyDialog-ok-button']>span>button");
    private final By valuesListCss = By.cssSelector("tbody[class='yui-dt-data'] div[class*='yui-dt-liner']");

    private final String DATE_BUTTON = "//table[contains(@class,'calendar')]//a[text()='%s']";
    private final By CALENDAR_BUTTON = By.cssSelector(".datepicker-icon");
    private final By SET_PROPERTY_VALUE_SELECT = By.cssSelector("span[class*='set-property-value'] button");


    private final By getValueXpath (String valueName)
    {
        return By.xpath("//tr/td[2]//div[contains(@class,'yui-dt-liner') and contains(text(),'" + valueName + "')]");
    }



    @SuppressWarnings("unchecked")
    @Override
    public SetPropertyValuePage render(RenderTime timer)
    {
        elementRender(timer, headerElement);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SetPropertyValuePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method finds the clicks on ok button
     *
     * @return HtmlPage Create Rule Page
     */
    public HtmlPage selectOkButton()
    {
        try
        {
            findAndWait(setValueOkButtonCss).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find an ok button : ", e);
            throw new PageException("Unable to find the ok button.");
        }
    }

    /**
     * This method finds and selects the type folder from the
     * displayed list
     *
     * @return SetPropertyValuePage
     */
    public HtmlPage selectPropertyTypeFolder(String folderName)
    {
        if (StringUtils.isEmpty(folderName))
        {
            throw new IllegalArgumentException("Folder name is required");
        }
        try
        {
            for (WebElement folder : findAndWaitForElements(propertyFoldersListCss))
            {
                if (folder.getText() != null)
                {
                    if (folder.getText().equalsIgnoreCase(folderName))
                    {
                        folder.click();
                        waitForElement(propertyFoldersListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        return factoryPage.instantiatePage(driver, SetPropertyValuePage.class);
                    }
                }
            }
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find values", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of values", e);
        }

        throw new PageOperationException("Unable to select " + folderName);
    }

    /**
     * This method finds and selects the value for Set Value Property from the
     * displayed list
     *
     * @return SetPropertyValuePage
     */
    public HtmlPage selectValueFromList(String valueName)
    {

        try
        {
            WebElement value = findAndWait(getValueXpath(valueName));
            value.click();
            return factoryPage.instantiatePage(driver, SetPropertyValuePage.class);

        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find values", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of values", e);
        }

        throw new PageOperationException("Unable to select value");
    }

    /**
     * Method to set date
     *
     * @param date
     */
    public void setDate(String date)
    {
        if (date == null)
        {
            throw new IllegalArgumentException("Date is required");
        }

        try
        {
            String dateXpath = String.format(DATE_BUTTON, date);
            WebElement element = findAndWait(By.xpath(dateXpath));
            element.click();
        }
        catch (TimeoutException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find the date button ", te);
            }
        }
    }

    /**
     * This method finds and clicks on calendar icon
     *
     */
    public void clickCalendarButton()
    {
        try
        {
            findAndWait(CALENDAR_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find calendar icon : ", e);
            throw new PageException("Unable to find the calendar icon.");
        }
    }

    /**
     * This method finds the list of values and return those as list of
     * string values.
     *
     * @return List<String>
     */
    public List<String> getValues()
    {
        List<String> values = new LinkedList<String>();
        try
        {
            for (WebElement value : findAndWaitForElements(valuesListCss))
            {
                values.add(value.getText());
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of values : ", e);
            }
        }
        return values;
    }

    /**
     * This method finds and clicks on select button
     * @return SetPropertyValuePage
     */
    public HtmlPage clickSelectButton()
    {

        try
        {
            findAndWait(SET_PROPERTY_VALUE_SELECT).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find a select button : ", e);
            throw new PageException("Unable to find the select button.");
        }
        return factoryPage.instantiatePage(driver, SetPropertyValuePage.class);
    }

}
