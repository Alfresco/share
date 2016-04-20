package org.alfresco.wcm.client.util;

import org.apache.chemistry.opencmis.client.api.Session;

/** 
 * Wrapper for ThreadLocal variable to hold the current CMIS session
 * used to service a HTTP request.
 * @author Chris Lack
 */
public abstract class CmisSessionHelper
{	 
     private static ThreadLocal<Session> sessionPerThread = new ThreadLocal<Session>() {};  	 
	 
     public static Session getSession() 
     {
         return sessionPerThread.get();
     }

     public static void setSession(Session session) 
     {
         sessionPerThread.set(session);
     }
}
