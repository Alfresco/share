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
package org.alfresco.po.share.site.calendar;

import org.alfresco.po.RenderTime;

/**
 * Calendar Container page object
 * relating to Share site Calendar page
 * 
 * @author Sergey Kardash
 */
public class CalendarContainer extends AbstractCalendarContainer
{

    @SuppressWarnings("unchecked")
    @Override
    public CalendarContainer render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CalendarContainer render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
