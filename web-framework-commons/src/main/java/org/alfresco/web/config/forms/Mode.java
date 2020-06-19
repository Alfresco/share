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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum Mode
{
    VIEW, EDIT, CREATE;
    
    private static Log logger = LogFactory.getLog(Mode.class);
    
    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }

    public static Mode modeFromString(String modeString)
    {
        if ("create".equalsIgnoreCase(modeString)) {
            return Mode.CREATE;
        }
        else if ("edit".equalsIgnoreCase(modeString))
        {
            return Mode.EDIT;
        }
        else if ("view".equalsIgnoreCase(modeString))
        {
            return Mode.VIEW;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Illegal modeString: " + modeString);
            }
            return null;
        }
    }
    
    public static List<Mode> modesFromString(String commaSeparatedModesString)
    {
        if (commaSeparatedModesString == null)
        {
            return Collections.emptyList();
        }
        List<Mode> result = new ArrayList<Mode>();
        StringTokenizer st = new StringTokenizer(commaSeparatedModesString, ",");
        while (st.hasMoreTokens())
        {
            String nextToken = st.nextToken().trim();
            Mode nextMode = Mode.modeFromString(nextToken);
            result.add(nextMode);
        }
        return result;
    }
}