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

package org.springframework.extensions.webscripts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.RemoteConfigElement.ConnectorDescriptor;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.AlfrescoConnector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Extension of the AlfrescoConnector to provide GET method request caching
 * for the lifetime of a single page request.
 * <p>
 * If a GET request is within the context of an executing page with a valid
 * RequestContext, then the request response is cached keyed on the URI.
 * <p>
 * The cache is examined for further GET requests. The cache is then
 * destroyed when the RequestContext is discarded at the end of the page
 * processing lifecycle. This removes the need for complex cache management
 * and concerns over stale data between page requests - the cache is simply
 * used to provide identical data to discrete components that execute within
 * the context of the same page without causing multiple requests. 
 * 
 * @author kevinr
 */
public class RequestCachingConnector extends AlfrescoConnector
{
    private static Log logger = LogFactory.getLog(RequestCachingConnector.class);
    
    /** RequestContext value for the request cache */
    private static final String REQUEST_CACHE_KEY = "_alf_request_cache";
    
    
    /**
     * Constructor
     * 
     * @param descriptor
     * @param endpoint
     */
    public RequestCachingConnector(ConnectorDescriptor descriptor, String endpoint)
    {
        super(descriptor, endpoint);
    }
    
    /**
     * Override the call method to intercept GET requests - maintain a simple
     * URI keyed cache of the Response in the thread local RequestContext.
     */
    @Override
    public Response call(final String uri, final ConnectorContext context)
    {
        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        if (rc != null && context == null || context.getMethod() == HttpMethod.GET)
        {
            final boolean debug = logger.isDebugEnabled();
            if (debug) logger.debug("Intercepted call to URI: " + uri);
            
            final Map<String, Response> cache = getResponseCache(rc);
            Response response = cache.get(uri);
            if (response == null)
            {
                if (debug) logger.debug(" uncached - calling super...");
                response = super.call(uri, context);
                if (response.getStatus().getCode() == HttpServletResponse.SC_OK)
                {
                    cache.put(uri, response);
                }
            }
            else
            {
                if (debug) logger.debug(" cached - returning cached response...");
            }
            return response;
        }
        else
        {
            return super.call(uri, context);
        }
    }
    
    /**
     * Aquire the Response cache - creating and apply to current RequestContext
     * if it does not already exist. No synchronization is required at this level
     * as the RequestContext is itself a thread locale - nothing is shared.
     * 
     * @param rc    RequestContext
     * 
     * @return Cache Map
     */
    private static Map<String, Response> getResponseCache(final RequestContext rc)
    {
        Map<String, Response> cache = (Map<String, Response>)rc.getValue(REQUEST_CACHE_KEY);
        if (cache == null)
        {
            cache = new HashMap<String, Response>(16);
            rc.setValue(REQUEST_CACHE_KEY, (Serializable)cache);
        }
        return cache;
    }
}
