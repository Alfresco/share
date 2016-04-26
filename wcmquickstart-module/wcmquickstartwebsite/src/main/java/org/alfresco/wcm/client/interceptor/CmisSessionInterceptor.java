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


