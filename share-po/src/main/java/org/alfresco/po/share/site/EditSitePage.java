/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site;

import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Edit site page object, holds all element of the HTML page relating to
 * share's edit site page.
 * 
 * @author Michael Suzuki
 * @since 1.5
 */
public class EditSitePage extends CreateSitePage
{
    private static final By EDIT_SITE_FORM = By.cssSelector("form#alfresco-editSite-instance-form");

    /**
     * Constructor.
     * 
     * @param drone {@link WebDrone}
     */
    public EditSitePage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    public EditSitePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public EditSitePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public EditSitePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(EDIT_SITE_FORM));

        return this;
    }
}
