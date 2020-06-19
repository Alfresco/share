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
package org.alfresco.wcm.client.util;

public class QuerySanitizer
{
    private static QuerySanitizer instance = new QuerySanitizer();
    
    protected QuerySanitizer()
    {
    }
    
    public static String sanitize(String text)
    {
        return instance.sanitizeImpl(text);
    }
    
    /**
     * Overridable sanitization method
     * @param text String
     * @return String
     */
    protected String sanitizeImpl(String text)
    {
        return text == null ? null : text.replaceAll("[\"'%?*()$^<>/{}\\[\\]#~@.,|\\\\+!:;&`¬=]", " ");
    }
    
    /**
     * Inject a new implementation if desired. Create a subclass of this class, override the sanitizeImpl operation,
     * and inject an instance of it using this operation. QuerySanitizer.sanitize will then be routed to your object.
     * @param sanitizer QuerySanitizer
     */
    public static void setSanitizer(QuerySanitizer sanitizer)
    {
        instance = sanitizer;
    }
}
