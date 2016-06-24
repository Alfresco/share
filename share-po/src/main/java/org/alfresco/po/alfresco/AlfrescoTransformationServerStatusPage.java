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
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Class containing page objects for Alfresco Transformation Server page
 *
 * @author Marina.Nenadovets
 */
public class AlfrescoTransformationServerStatusPage extends SharePage
{
    protected static Log logger = LogFactory.getLog(AlfrescoTransformationServerStatusPage.class);
    protected final By SERVER_STATUS = By.cssSelector("a[href='/transformation-server/home']");
    protected final By SERVER_HISTORY = By.cssSelector("a[href='/transformation-server/transformations']");
    protected final By SERVER_STATS = By.cssSelector("a[href='/transformation-server/stats']");
    protected final By SERVER_SETTINGS = By.cssSelector("a[href='/transformation-server/settings']");

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerStatusPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(SERVER_STATUS),
            getVisibleRenderElement(SERVER_HISTORY),
            getVisibleRenderElement(SERVER_STATS),
            getVisibleRenderElement(SERVER_SETTINGS));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerStatusPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to navigate to server history page
     *
     * @param driver WebDriver instance
     * @return AlfrescoTransformationServerHistoryPage
     */
    public AlfrescoTransformationServerHistoryPage openServerHistoryPage(WebDriver driver)
    {
        findAndWait(SERVER_HISTORY).click();
        return factoryPage.instantiatePage(driver, AlfrescoTransformationServerHistoryPage.class).render();
    }
}
