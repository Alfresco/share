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