/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.surf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Helper functions for extracting servlet container specific data from the
 * generalized web request and request attribute objects.
 * 
 * @author muzquiano
 * @author David Draper
 */
public class ServletUtil 
{
    public final static String VIEW_REQUEST_ATTRIBUTE_NAME = "surfViewHttpServletRequest";
    
    private static final Log logger = LogFactory.getLog(ServletUtil.class);
    
    public static final String IE_USER_AGENT_HEADER_LOWER_CASE = "msie";
    public static final String USER_AGENT = "User-Agent";
    public static final String SEMI_COLON = ";";
    
    /**
     * <p>Attempts to determine the Internet Explorer version specified in the User-Agent header in the
     * supplied {@link HttpServletRequest}. If the browser is not a version of Internet Explorer (or there
     * were problems parsing the header) then <code>null</code> null is returned.</p>
     * @param request The current {@link HttpServletRequest} to retrieve the User-Agent string from.
     * @return A {@link Float} containing the Internet Explorer version or <code>null</code> if the browser
     * was not a version of Internet Explorer or there were problems parsing the header.
     */
    public static final Float getInternetExplorerVersion(HttpServletRequest request)
    {
        Float version = null;
        String userAgentHeader = request.getHeader(USER_AGENT);
        if (userAgentHeader != null)
        {
            int ieIndex = userAgentHeader.toLowerCase().indexOf(IE_USER_AGENT_HEADER_LOWER_CASE);
            if (ieIndex != -1)
            {
                // This is Internet Explorer, need to check the version...
                int closingSemiColonIndex = userAgentHeader.indexOf(SEMI_COLON, ieIndex + IE_USER_AGENT_HEADER_LOWER_CASE.length());
                if (closingSemiColonIndex == -1)
                {
                    // Invalid user-agent header.
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Could not find closing \";\" for Internet Explorer data in User-Agent header: " + userAgentHeader);
                    }
                }
                else
                {
                    String ieString = userAgentHeader.substring(ieIndex + IE_USER_AGENT_HEADER_LOWER_CASE.length(), closingSemiColonIndex).trim();
                    try
                    {
                        version = Float.parseFloat(ieString);
                    }
                    catch (NumberFormatException e)
                    {
                        // Could not parse IE version.
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Could not parse Internet Explorer version from User-Agent header: " + userAgentHeader);
                        }
                    }
                }
            }
        }
        return version;
    }
    
    /**
     * Sets the http servlet request onto the spring request attributes
     * 
     * @param request
     */
    public static void setRequest(HttpServletRequest request)
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null)
        {
            attributes.setAttribute(ServletUtil.VIEW_REQUEST_ATTRIBUTE_NAME, request, RequestAttributes.SCOPE_REQUEST);
        }
    }
    
    /**
     * Retrieves the http servlet object heard by the view implementation.
     * This is stored on a thread local by the Spring view handler.
     * 
     * @param request
     * @return
     */
    public static HttpServletRequest getRequest()
    {
        HttpServletRequest request = null;
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null)
        {
            Object tmp = attributes.getAttribute(VIEW_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
            if (tmp instanceof HttpServletRequest)
            {
                request = (HttpServletRequest) tmp;
            }
        }
        return request;
    }
    
    /**
     * Returns the session for the current http session.  If a session doesn't exist, it is created.
     * 
     * @return http session
     */
    public static HttpSession getSession()
    {
        return getSession(true);
    }
    
    /**
     * Returns the session for the current http session.
     * Optionally creates the session.
     * 
     * @param create whether to create
     * 
     * @return http session
     */
    public static HttpSession getSession(boolean create)
    {
        HttpSession session = null;
        
        HttpServletRequest request = getRequest();
        if (request != null)
        {
            session = request.getSession(create);            
        }
        
        return session;
    }
}
