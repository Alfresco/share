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
package org.alfresco.po;
/**
 * A render interface that determines if elements are rendered.
 * 
 * Every page object renders itself by calling the render
 * method which defines what Page object it is, up to that point
 * it is treated as a generic page. The render signifies a response
 * is required and will force the object to wait until all HTML elements
 * specified in the render method are found and displayed.
 * 
 * @author Michael Suzuki
 * @since 1.6.3
 *
 */
public interface Render
{
    /**
     * Page renderer verifies the page has rendered
     * by checking all elements are visible and loaded.
     * 
     * To verify this assertion every page object that implements
     * the method will contain logic to determine if the page has 
     * loaded completely in the given time set by the {@link RenderTime}. 
     * 
     * @param timer {@link RenderTime} time to wait
     * @param <T> object that extends {@link HtmlPage}
     * @return {@link HtmlPage} page object response
     */
    <T extends HtmlPage> T render(final RenderTime timer);
    /**
     * Page renderer verifies the page has rendered
     * by checking java script page loaded status is complete.
     * @param <T> object that extends {@link HtmlPage}
     * @return {@link HtmlPage} object response
     */
    <T extends HtmlPage> T render();
}
