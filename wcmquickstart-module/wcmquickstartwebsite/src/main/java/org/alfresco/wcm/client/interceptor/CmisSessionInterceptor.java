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
package org.alfresco.wcm.client.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.CmisSessionPool;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Get a CMIS Session for the request.
 * @author Chris Lack
 */
public class CmisSessionInterceptor extends HandlerInterceptorAdapter
{
	private final static Log log = LogFactory.getLog(CmisSessionInterceptor.class);
	
	private CmisSessionPool sessionPool;
	private static ThreadLocal<Long> timings = new ThreadLocal<Long>() {};  	 
	
	/**
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(HttpServletRequest, HttpServletResponse, Object)
	 */	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
		if (log.isDebugEnabled())
		{
			timings.set(System.currentTimeMillis());
		}
		
		// Get an anonymous CMIS session
		Session session = sessionPool.getGuestSession();
		
		// Make the session available as a ThreadLocal variable to all
		// classes processing the request on this thread.
		CmisSessionHelper.setSession(session);
		
		return super.preHandle(request, response, handler);
	}

	/**
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#afterCompletion(HttpServletRequest, HttpServletResponse, Object, Exception)
	 */
	@Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
		super.afterCompletion(request, response, handler, ex);
		
		Session session = CmisSessionHelper.getSession();

		// Return the session to the pool
		sessionPool.closeSession(session);
		
		if (log.isDebugEnabled())
		{
			long start = timings.get();		
			timings.remove();
			long end = System.currentTimeMillis();
		    log.debug("*** "+request.getPathInfo()+" "+(end-start)+"ms");
		}
    }

	public void setSessionPool(CmisSessionPool sessionPool) {
		this.sessionPool = sessionPool;
	}
}


