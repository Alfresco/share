/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.alfresco.po.share.dashlet;

import org.alfresco.webdrone.exception.PageException;

/**
 * Thrown by {@link WebDrone} when Dashlet has not rendered in the set time.
 * 
 * @author Shan Nagarajan
 * @since 1.6
 */
public class NoSuchDashletExpection extends PageException
{

    /**
     * The Serial Version UID.
     */
    private static final long serialVersionUID = -8942012569207507506L;

    private static final String DEFAULT_MESSAGE = "Not able find the given dashlet";

    public NoSuchDashletExpection(String reason)
    {
        super(reason);
    }

    public NoSuchDashletExpection(String reason, Throwable cause)
    {
        super(reason, cause);
    }

    public NoSuchDashletExpection()
    {
        super(DEFAULT_MESSAGE);
    }

}
