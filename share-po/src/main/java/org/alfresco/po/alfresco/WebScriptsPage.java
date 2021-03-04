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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

/**
 * Created by olga.lokhach on 6/16/2014.
 */
@SuppressWarnings("unused")
public class WebScriptsPage extends SharePage

{
    private static Log logger = LogFactory.getLog(WebScriptsPage.class);
    private final By REFRESH_WEB_SCRIPTS_BUTTON = By.cssSelector("div>form>table>tbody>tr>td>input[value*='Refresh']");
    private final By CLEAR_CHACHES_BUTTON = By.cssSelector("div>form>table>tbody>tr>td>input[value*='Clear']");

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(REFRESH_WEB_SCRIPTS_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method for click Refresh Button
     *
     * @return WebScriptsMaintenancePage
     */

    public HtmlPage clickRefresh()
    {
        findAndWait(REFRESH_WEB_SCRIPTS_BUTTON).click();
        return factoryPage.instantiatePage(driver, WebScriptsMaintenancePage.class);

    }

    /**
     * Method for click Clear Button
     */

    public void clickClear()
    {
        findAndWait(CLEAR_CHACHES_BUTTON).click();
    }

    /**
     * Method to verify console is opened
     *
     * @return boolean
     */

    public boolean isOpened()
    {
        return findAndWait(By.xpath("*//span[contains(text(), 'Web Scripts Home')]")).isDisplayed();
    }

}
