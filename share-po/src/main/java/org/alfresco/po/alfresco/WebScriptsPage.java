/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by olga.lokhach on 6/16/2014.
 */
@SuppressWarnings("unused")
public class WebScriptsPage extends SharePage

{
    private static Log logger = LogFactory.getLog(WebScriptsPage.class);
    private final By REFRESH_WEB_SCRIPTS_BUTTON = By.cssSelector("div>form>table>tbody>tr>td>input[value*='Refresh']");
    private final By CLEAR_CHACHES_BUTTON = By.cssSelector("div>form>table>tbody>tr>td>input[value*='Clear']");

    public WebScriptsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(REFRESH_WEB_SCRIPTS_BUTTON),
            getVisibleRenderElement(CLEAR_CHACHES_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for click Refresh Button
     *
     * @param
     * @return
     */

    public WebScriptsMaintenancePage clickRefresh()
    {
        drone.findAndWait(REFRESH_WEB_SCRIPTS_BUTTON).click();
        return new WebScriptsMaintenancePage(drone);

    }

    /**
     * Method for click Clear Button
     *
     * @param
     * @return
     */

    public void clickClear()
    {
        drone.findAndWait(CLEAR_CHACHES_BUTTON).click();
    }

    /**
     * Method to verify console is opened
     *
     * @return boolean
     */

    public boolean isOpened()
    {
        return drone.findAndWait(By.xpath("*//span[contains(text(), 'Web Scripts Home')]")).isDisplayed();
    }

}
