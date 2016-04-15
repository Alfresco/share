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
