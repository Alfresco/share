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
package org.alfresco.po.thirdparty.flickr;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class FlickrUserPage extends Page
{
    private final static By YOU_LINK = By.xpath("//a[@class='gn-link']/span[text()='You']");
    private final static By CONFIRM_AUTHORIZE_BUTTON = By.cssSelector("input[value='OK, I\\'LL AUTHORIZE IT']");
    private final static By PHOTO_STREAM_TITLE = By.xpath("//ul[contains(@class,'nav-links')]/li/a[text()='Photostream']");
    private final static String UPLOADED_FILE_XPATH = "//div[contains(@id,'photo_')]/div/div/span/a/img[contains(@alt, '%s')]";

    @SuppressWarnings("unchecked")
    @Override
    public FlickrUserPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(YOU_LINK));
        return this;
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
        waitForElement(PHOTO_STREAM_TITLE, SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
        return driver.findElements(By.xpath(String.format(UPLOADED_FILE_XPATH, fileName))).size() > 0;
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }
}
