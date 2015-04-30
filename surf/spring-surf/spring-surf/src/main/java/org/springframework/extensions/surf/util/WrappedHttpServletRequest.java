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

package org.springframework.extensions.surf.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A wrapper class for buffering around HttpServletRequest objects
 * 
 * @author muzquiano
 */
public class WrappedHttpServletRequest extends HttpServletRequestWrapper
{
    /**
     * Instantiates a new wrapped http servlet request.
     * 
     * @param request the request
     */
    public WrappedHttpServletRequest(HttpServletRequest request)
    {
        super(request);
    }
    
    private String requestUri;
    
    /**
     * Allows for the request URI to be manually overridden
     * 
     * @param requestUri the new request uri
     */
    public void setRequestURI(String requestUri)
    {
        this.requestUri = requestUri;
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURI()
     */
    public String getRequestURI()
    {
        String value = null;
        
        if (requestUri != null)
        {
            value = requestUri;
        }
        else
        {
            value = super.getRequestURI();
        }
        
        return value;
    }
    
}
