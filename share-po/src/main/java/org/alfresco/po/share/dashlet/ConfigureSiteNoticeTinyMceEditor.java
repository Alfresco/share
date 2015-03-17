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
package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Site Notice Configure TinyMce Editor page object, it is used to apply the styles to text using fore&back color,font and formatting.
 * 
 * @author Chiran
 */
public class ConfigureSiteNoticeTinyMceEditor extends AdvancedTinyMceEditor
{
    public ConfigureSiteNoticeTinyMceEditor(WebDrone drone)
    {
        super(drone);
        setTinyMce(drone.findAndWait(By.cssSelector("iframe[id$='configDialog-text_ifr']")).getAttribute("id"));
        setForeColorLinkCss("div[aria-label='Text color']>button.mce-open");
        setBGColorLinkCss("div[aria-label='Background color']>button.mce-open");
    }
}