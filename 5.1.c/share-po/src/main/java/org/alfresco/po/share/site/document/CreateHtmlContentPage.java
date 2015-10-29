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

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.enums.Encoder;
import org.openqa.selenium.By;

/**
 * Create html content page object, Where user can create content.
 * 
 * @author Jamie Allison
 * @since  4.3.0
 */
public class CreateHtmlContentPage extends CreatePlainTextContentPage
{
    public CreateHtmlContentPage()
    {
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
    TinyMceEditor tinyMCEEditor;
    @Override
    protected void createContentField(ContentDetails details)
    {
        if (details != null && details.getContent() != null)
        {
            tinyMCEEditor.setTinyMce();
            tinyMCEEditor.setText(details.getContent(), Encoder.ENCODER_HTML);
        }
    }
}
