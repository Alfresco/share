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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.enums.Encoder;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Create html content page object, Where user can create content.
 * 
 * @author Jamie Allison
 * @since  4.3.0
 */
public class CreateHtmlContentPage extends CreatePlainTextContentPage
{
    private static final String TINYMCE_CONTENT = "template_x002e_create-content_x002e_create-content_x0023_default_prop_cm_content_ifr";

    public CreateHtmlContentPage(WebDrone drone)
    {
        super(drone);
        CONTENT = By.cssSelector("iframe[id$='default_prop_cm_content_ifr']");
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreateHtmlContentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @Override
    protected void createContentField(ContentDetails details)
    {
        if (details != null && details.getContent() != null)
        {
            TinyMceEditor tinyMCEEditor = new TinyMceEditor(drone);
            tinyMCEEditor.setTinyMce(TINYMCE_CONTENT);

            tinyMCEEditor.setText(details.getContent(), Encoder.ENCODER_HTML);
        }
    }
}
