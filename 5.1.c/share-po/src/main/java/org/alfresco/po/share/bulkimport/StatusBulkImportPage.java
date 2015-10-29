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
package org.alfresco.po.share.bulkimport;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.WebDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Sergey Kardash on 5/19/14.
 */
@SuppressWarnings("unused")
public class StatusBulkImportPage extends AdvancedBulkImportPage
{
    private Log logger = LogFactory.getLog(StatusBulkImportPage.class);

    @SuppressWarnings("unchecked")
    @Override
    public StatusBulkImportPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StatusBulkImportPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
