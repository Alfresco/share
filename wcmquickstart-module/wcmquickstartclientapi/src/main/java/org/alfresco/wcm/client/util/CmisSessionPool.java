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