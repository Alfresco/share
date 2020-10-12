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
package org.alfresco.wcm.client;

import java.io.Serializable;
import java.util.Map;

/**
 * An item of content such as a page, blog article, image etc
 * @author Roy Wetherall
 */
public interface Resource extends Serializable 
{	
	public final static String PROPERTY_TITLE = "cm:title";
    public final static String PROPERTY_DESCRIPTION = "cm:description";
    public final static String PROPERTY_AUTHOR = "cm:author";
	public static final String PROPERTY_MODIFIED_TIME = "cmis:lastModificationDate";
	
	/**
	 * The id of the item
	 * @return String item id
	 */
	String getId();
	
	/**
	 * The name of the item
	 * @return String item name
	 */
	String getName();
	
	/**
	 * The title of the item
	 * @return String item title
	 */
	String getTitle();	
	
	/**
	 * The description
	 * @return String description
	 */
	String getDescription();
	
	/**
	 * Get the type of the resource
	 * @return String	type
	 */
	String getType();
	
	/**
	 * Get any property by name
	 * String name property name
	 * @return Object the property
	 */
	Serializable getProperty(String name);
	
	/**
	 * Get properties mape
	 * @return Map of properties
	 */
	Map<String, Serializable> getProperties();
	
	/**
	 * Gets the section the resource is contained within.
	 * 
	 * @return	Section		the section
	 */
	Section getContainingSection();
	
}
