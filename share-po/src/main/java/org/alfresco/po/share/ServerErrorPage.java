/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Aliaksei Boole
 */
public class ServerErrorPage extends SharePage
{
    private static final By RETURN_LINK = By.xpath("//a[@href='/share']");
    private static final By ERROR_MESSAGE = By.xpath("//p[text()='A server error has occured.']");

    public ServerErrorPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerErrorPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
                getVisibleRenderElement(RETURN_LINK),
                getVisibleRenderElement(ERROR_MESSAGE)
        );
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerErrorPage render(long l)
    {
        checkArgument(l > 0);
        return render(new RenderTime(l));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServerErrorPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


}
