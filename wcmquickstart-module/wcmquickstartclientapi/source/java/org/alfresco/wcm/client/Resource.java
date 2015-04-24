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
