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
