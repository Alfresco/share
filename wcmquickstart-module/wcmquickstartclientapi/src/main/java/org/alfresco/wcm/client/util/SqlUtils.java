/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
