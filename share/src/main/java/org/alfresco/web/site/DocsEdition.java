/*
 * #%L
 * Alfresco Share WAR
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
package org.alfresco.web.site;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates the documentation editions and their relationships to
 * the Alfresco editions and versions.
 * 
 * @author Ahmed Owian
 */
public class DocsEdition implements Serializable
{
    private static final long serialVersionUID = -6180536415511757664L;
    
    public static final String COMMUNITY = "community";
    public static final String CLOUD = "cloud";
    
    private final String value;
    
    /**
     * Value will be "community"
     */
    public DocsEdition()
    {
        this(null, null, false);
    }

    /**
     * If isInCloud is true, then value will be "cloud"
     */
    public DocsEdition(boolean isInCloud)
    {
        this(null, null, isInCloud);
    }

    /**
     * If not in cloud and the edition is "ENTERPRISE" and the specificationVersion is of the form major.minor,
     * then the value will be the enterprise version (major.minor)
     */
    public DocsEdition(String edition, String specificationVersion, boolean isInCloud)
    {
        String value = COMMUNITY;
        if (isInCloud)
        {
            value = CLOUD;
        }
        else if (EditionInfo.ENTERPRISE_EDITION.equals(edition) && specificationVersion != null)
        {
            Matcher matcher = Pattern.compile("^(\\d+\\.\\d+)").matcher(specificationVersion);
            if (matcher.find())
            {
                value = matcher.group();
            }
        }
        this.value = value;
    }
    
    /**
     * Returns the appropriate edition used for documentation URLs. 
     * @return "community", the enterprise version (major.minor), or "cloud"
     */
    public String getValue()
    {
        return this.value;
    }
}
