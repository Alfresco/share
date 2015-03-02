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
package org.alfresco.po.share.task;

import org.alfresco.po.share.workflow.StartedFilter;
import org.alfresco.po.share.workflow.WorkFlowFilters;
import org.alfresco.po.share.workflow.WorkFlowType;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Aliaksei Boole
 */
public class TaskFilters extends WorkFlowFilters
{
    public TaskFilters(WebDrone drone)
    {
        super(drone);
    }

    public HtmlPage select(WorkFlowType workFlowType)
    {
        throw new PageOperationException("Not allowed here");
    }

    public HtmlPage select(StartedFilter startedFilter)
    {
        throw new PageOperationException("Not allowed here");
    }

    public HtmlPage select(AssignFilter assignFilter)
    {
        checkNotNull(assignFilter);
        drone.findAndWait(assignFilter.by).click();
        waitUntilAlert();
        return drone.getCurrentPage();
    }
}
