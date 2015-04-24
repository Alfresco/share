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
