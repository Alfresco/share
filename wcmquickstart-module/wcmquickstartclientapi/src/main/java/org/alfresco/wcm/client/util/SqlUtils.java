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

import java.util.Calendar;
import java.util.Date;

import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

/**
 * Collection of CMIS SQL utils
 * @author Chris Lack
 */
public class SqlUtils
{
	/**
	 * Enclose a SQL string value in single quotes.
	 * @param value		SQL string value
	 * @return String	enclosed value
	 */
	public static String encloseSQLString(String value)
	{
		StringBuffer buffer = new StringBuffer(value.length()+2);
		buffer.append("'").append(value).append("'");
		return buffer.toString();
	}
	
	/**
	 * Open CMIS returns dates as a Calendar but freemarker cannot
	 * cope with these so convert them to a Date.
	 * @param result Queryresult from CMIS
	 * @param propertyId property name
	 * @return Date 
	 */
	public static Date getDateProperty(QueryResult result, String propertyId)
	{
        PropertyData<Object> property = result.getPropertyById(propertyId);
        if (property == null) return null;
        Calendar calendar = (Calendar)property.getFirstValue();
        return calendar != null ? calendar.getTime() : null;
	}
}
