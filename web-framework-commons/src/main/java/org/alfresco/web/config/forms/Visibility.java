/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.web.config.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Visibility
{
    SHOW, HIDE;

    private static Log logger = LogFactory.getLog(Visibility.class);
    
    public static Visibility visibilityFromString(String visibilityString)
    {
        if (visibilityString.equalsIgnoreCase("show")) {
            return Visibility.SHOW;
        }
        else if (visibilityString.equalsIgnoreCase("hide"))
        {
            return Visibility.HIDE;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Illegal visibilityString: ")
                    .append(visibilityString);
                logger.debug(msg.toString());
            }
            return null;
        }
    }
}