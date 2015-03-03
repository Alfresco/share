/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

/**
 * An unknown page that will, at time of {@link #render()} produce a strongly-typed page.
 * <p/>
 * By using this page, source pages do not need to be responsible for determining the target page in functional methods.
 * 
 * <pre>
 *      public HtmlPage selectItem(Integer number)
 *     {
 *             ...
 *             item.click();
 *             ...
 *         return FactorySharePage.getUnknownPage(drone);
 *     }
 * </pre>
 * 
 * @author Derek Hulley
 * @since 1.8.0
 * @see FactorySharePage#getUnknownPage(WebDrone)
 */
public class UnknownSharePage extends SharePage
{
    /**
     * Constructor.
     */
    public UnknownSharePage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * @see FactorySharePage#resolvePage(WebDrone)
     * @return the real page based on what is on the browser
     */
    private HtmlPage getActualPage()
    {
        return FactorySharePage.resolvePage(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render()
    {
        HtmlPage actualPage = getActualPage();
        return (T) actualPage.render();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render(final long time)
    {
        HtmlPage actualPage = getActualPage();
        return (T) actualPage.render(time);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render(RenderTime timer)
    {
        HtmlPage actualPage = getActualPage();
        return (T) actualPage.render(timer);
    }
}
