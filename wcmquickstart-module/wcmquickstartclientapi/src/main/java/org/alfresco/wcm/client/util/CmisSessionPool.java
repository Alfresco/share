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

import org.apache.chemistry.opencmis.client.api.Session;

/**
 * CMIS session pool interface
 * @author Chris Lack
 */
public interface CmisSessionPool  
{

	/** 
	 * Get an anonymous connection for guest site visitors. 
	 * @throws Exception 
	 */
	Session getGuestSession() throws Exception;
	
	/** 
	 * Get a session authenticated against the repository.
	 * @param username repository username
	 * @param password repository password 
	 */
	Session getSession(String username, String password);
	
	/** 
	 * Finish using a session.
	 * @param session the CMIS session to close 
	 * @throws Exception 
	 */
	void closeSession(Session session) throws Exception;
}