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

import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
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

    @SuppressWarnings("unchecked")
    public EditSitePage render()
    {
    	RenderTime timer = new RenderTime(maxPageLoadingTime);
    	MODERATED_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='moderated-help-text']");
        PRIVATE_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='private-help-text']");
        PUBLIC_CHECKBOX_HELP_TEXT = By.cssSelector("span[id$='public-help-text']");
        elementRender(timer, RenderElement.getVisibleRenderElement(EDIT_SITE_FORM));

        return this;
    }

}
