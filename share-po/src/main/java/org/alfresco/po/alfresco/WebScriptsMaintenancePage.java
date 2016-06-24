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

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * Created by olga.lokhach on 6/16/2014.
 */
public class WebScriptsMaintenancePage extends WebScriptsPage
{

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsMaintenancePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to verify console is opened
     *
     * @return boolean
     */

    public boolean isOpened()
    {
        return findAndWait(By.xpath("*//b[contains(text(), 'Maintenance Completed')]")).isDisplayed();
    }

}
