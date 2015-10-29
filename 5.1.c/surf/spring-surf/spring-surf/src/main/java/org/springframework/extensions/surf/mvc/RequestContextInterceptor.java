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

package org.springframework.extensions.surf.mvc;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextFactory;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

/**
 * Interceptor responsible for creating the request context. The default request context
 * implementation is automatically bound as a threadlocal.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class RequestContextInterceptor extends AbstractWebFrameworkInterceptor 
{
    /** Cached list of RequestContextFactory implementing beans */
    private Map<String, RequestContextFactory> factories = null;
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#preHandle(org.springframework.web.context.request.WebRequest)
     */
    public void preHandle(WebRequest request) throws Exception
    {
        if (factories == null)
        {
            factories = this.getApplicationContext().getBeansOfType(RequestContextFactory.class);
        }
        
        // find a request context factory that can produce for this web request
        RequestContextFactory factory = null;
        for (RequestContextFactory f : factories.values())
        {
            if (f.canHandle(request))
            {
                factory = f;
                break;
            }
        }
        
        // ask factory to produce the request context - binds to the current thread
        if (factory != null)
        {
            try
            {
                factory.newInstance(request);
            }
            catch (RequestContextException rce)
            {
                throw new RequestDispatchException("Error while building request context", rce);
            }
        }
        else
        {
            throw new Exception("Unable to find a RequestContextFactory to handle request: " + request);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#postHandle(org.springframework.web.context.request.WebRequest, org.springframework.ui.ModelMap)
     */
    public void postHandle(WebRequest request, ModelMap model) throws Exception
    {
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#afterCompletion(org.springframework.web.context.request.WebRequest, java.lang.Exception)
     */
    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
        // unbind the RequestContext from the current thread
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        if (context != null)
        {
            context.release();
        }
        
        // cleanup threadlocal values
        I18NUtil.setLocale(null);
    }
}