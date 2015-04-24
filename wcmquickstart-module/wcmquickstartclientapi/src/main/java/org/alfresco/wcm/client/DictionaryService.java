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
	 * @see DictionaryService.getParentType(String type)
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
