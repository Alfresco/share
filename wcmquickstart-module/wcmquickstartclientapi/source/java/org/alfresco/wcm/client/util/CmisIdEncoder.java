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

/**
 * CMIS Url encoder. This interface is implemented by classes 
 * which translate between a CMIS object id and a valid url string
 * @author Chris Lack
 */
public interface CmisIdEncoder  
{
	/**
	 * Convert a CMIS object Id into a string which will be valid within a url.
	 * @param objectId CMIS object id
	 * @return String url portion
	 */
	String getUrlSafeString(String objectId);	
	
	/**
	 * Convert url valid encoding of a CMIS object Id back into the id.
	 * @param String url portion
	 * @return objectId CMIS object id
	 */
	String getObjectId(String urlPortion);	
	
}
