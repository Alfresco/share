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
package org.alfresco.po.share;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * @author Aliaksei Boole
 */
public class ServerErrorPage extends SharePage
{
    private static final By RETURN_LINK = By.xpath("//a[@href='/share']");
    private static final By ERROR_MESSAGE = By.xpath("//p[contains (text(),'A server error has occurred.')]");


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
    public ServerErrorPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


}
