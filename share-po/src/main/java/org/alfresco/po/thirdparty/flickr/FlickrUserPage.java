/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.thirdparty.flickr;

import org.alfresco.webdrone.Page;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Aliaksei Boole
 */
public class FlickrUserPage extends Page
{
    private final static By YOU_LINK = By.xpath("//a[@class='gn-link']/span[text()='You']");
    private final static By CONFIRM_AUTHORIZE_BUTTON = By.cssSelector("input[value='OK, I\\'LL AUTHORIZE IT']");
    private final static By PHOTO_STREAM_TITLE = By.xpath("//ul[contains(@class,'nav-links')]/li/a[text()='Photostream']");
    private final static String UPLOADED_FILE_XPATH = "//div[contains(@id,'photo_')]/div/div/span/a/img[contains(@alt, '%s')]";

    public FlickrUserPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public FlickrUserPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(YOU_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FlickrUserPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FlickrUserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void confirmAlfrescoAuthorize()
    {
        click(CONFIRM_AUTHORIZE_BUTTON);
    }

    public boolean isFileUpload(String fileName)
    {
        click(YOU_LINK);
        drone.waitForElement(PHOTO_STREAM_TITLE, drone.getDefaultWaitTime());
        return drone.findAll(By.xpath(String.format(UPLOADED_FILE_XPATH, fileName))).size() > 0;
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    //TODO Temporary method. Before elementRender from SharePage didn't moved out to Page.
    private void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        for (RenderElement element : elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(drone, waitSeconds);
            }
            catch (TimeoutException e)
            {
                throw new PageRenderTimeException("element not rendered in time.");
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }
}
