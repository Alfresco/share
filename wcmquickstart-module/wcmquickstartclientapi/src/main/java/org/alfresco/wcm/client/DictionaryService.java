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

/**
 * Dictionary service interface.
 * 
 * @author Roy Wetherall
 */
public interface DictionaryService
{
	/** CMIS root types */
	public static final String TYPE_CMIS_DOCUMENT = "cmis:document";
	public static final String TYPE_CMIS_FOLDER = "cmis:folder";
	
	/** CMIS type prefixes */
	public static final String TYPE_PREFIX_DOCUMENT = "D";
	public static final String TYPE_PREFIX_FOLDER = "F";
	
	/** Full CMIS root types */
	public static final String FULL_TYPE_CMIS_DOCUMENT = TYPE_PREFIX_DOCUMENT + ":" + TYPE_CMIS_DOCUMENT;
	public static final String FULL_TYPE_CMIS_FOLDER = TYPE_PREFIX_FOLDER + ":" + TYPE_CMIS_FOLDER;
	
	/**
	 * Indicates whether the given type is a root type or not.
	 * 
	 * @param type		type
	 * @return boolean	true if root type, false otherwise
	 */
	boolean isRootType(String type);
	
	/**
	 * Indicates whether the given type is a CMIS document type. 
	 * 
	 * Type can be passed as the full string id (eg D:cmis:document) or as
	 * the query string value (eg cmis:document). 
	 * 
	 * @param type		type
	 * @return boolean	true if document type, false otherwise
	 */
	boolean isDocumentSubType(String type);
	
	/**
	 * Indicates whether the given type is a CMIS document type. 
	 * 
	 * Type can be passed as the full string id (eg F:cmis:folder) or as
	 * the query string value (eg cmis:folder). 
	 * 
	 * @param type		type
	 * @return boolean	true if folder type, false otherwise
	 */
	boolean isFolderSubType(String type);
	
	/**
	 * Gets the parent type of the given type, null if none.  
	 * 
	 * Type can be passed as the full string id (eg D:cmis:document) or as
	 * the query string value (eg cmis:document). 
	 * 
	 * By default the type is returned as the full string id
	 * 
	 * @param type		type
	 * @return String	parent type, null otherwise
	 */
	String getParentType(String type);
	
	/**
	 * @see DictionaryService#getParentType(String type)
	 * 
	 * If queryName is true then the parent type is returned in the format cmis:document, 
	 * otherwise it is returned in the full id form, D:cmis:document.
	 * 
	 * @param type		type
	 * @param queryName returns parent type string as query name if true, otherwise as full id
	 * @return String	parent type, null otherwise
	 */
	String getParentType(String type, boolean queryName);
	
	/**
	 * Removes the type prefix from the type name.  Eg D:cmis:document becomes cmis:document.
	 * 
	 * If an non-prefixed name is passed it is unmodified.
	 * 
	 * @param type		type name with (or without) type prefix
	 * @return String	type name with the prefix striped
	 */
	String removeTypePrefix(String type);	
}
