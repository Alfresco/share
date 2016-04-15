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
package org.alfresco.po.alfresco;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Class containing page objects of Alfresco Transformation Server History page
 *
 * @author Marina.Nenadovets
 */
public class AlfrescoTransformationServerHistoryPage extends AlfrescoTransformationServerStatusPage
{
    private static final By TRANSFORMATION_TABLE = By.cssSelector("#transformation-table>table");
    private static final String FILENAME_CELL = "//div[@id='transformation-table']//td[contains(@class,'filename')]/div[text()='%s']";
    private static final String STATUS_OUTCOME = "//../../td[contains(@class, 'outcome')]//img";
    private static final String STATUS_OK_IMG = "/transformation-server/res/images/icons/yes_approve_tick_ok_confirm_accept.png";
    private static final String STATUS_FAIL_IMG = "/transformation-server/res/images/icons/error_delete.png";
    private int retryCount = 0;

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerHistoryPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(SERVER_STATUS),
            getVisibleRenderElement(SERVER_HISTORY),
            getVisibleRenderElement(SERVER_STATS),
            getVisibleRenderElement(SERVER_SETTINGS),
            getVisibleRenderElement(TRANSFORMATION_TABLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerHistoryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private WebElement getFileInTheTable(String fileName)
    {
        try
        {
            selectRowsPerPage(50);
            return driver.findElement(By.xpath(String.format(FILENAME_CELL, fileName)));
        }
        catch (NoSuchElementException nse)
        {
            retryCount++;
            openServerHistoryPage(driver).render();
            if (retryCount == 3)
            {
                throw new PageOperationException("The " + fileName + " doesn't exist");
            }
            return getFileInTheTable(fileName);
        }
    }

    /**
     * Method to verify whether file is transformed
     *
     * @param fileName String name of the file
     * @return true if transformed
     */
    public boolean isFileTransformed(String fileName)
    {
        WebElement status;
        try
        {
            status = getFileInTheTable(fileName).findElement(By.xpath(STATUS_OUTCOME));
        }
        catch (PageOperationException e)
        {
            return false;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find status outcome cell", nse);
        }
        return status.getAttribute("src").contains(STATUS_OK_IMG);
    }

    /**
     * Method to find a file whose transformation has failed
     *
     * @return String fileName
     */
    public String fileTransformFailed()
    {
        try
        {
            return findAndWait(By.xpath("//img[@src='" + STATUS_FAIL_IMG + "']")).findElement(By.xpath("./../../../td[4]/div")).getText();
        }
        catch (TimeoutException e)
        {
            logger.info("None transformation has failed");
            return "";
        }
        catch(StaleElementReferenceException ser)
        {
            retryCount++;
            if (retryCount == 3)
            {
                throw new PageOperationException("Unable to find the file whose transformation failed", ser);
            }
            return fileTransformFailed();
        }
    }

    public AlfrescoTransformationServerHistoryPage selectRowsPerPage(int value)
    {
        WebElement selectMenu = driver.findElement(By.id("rowsPerPage-button"));
        selectMenu.click();
        selectMenu.findElement(By.xpath(String.format("//a[text()='%s']", Integer.toString(value)))).click();
        waitUntilAlert();
        return this;
    }
}
