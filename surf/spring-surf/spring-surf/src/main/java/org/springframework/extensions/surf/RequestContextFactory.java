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

import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.web.context.request.WebRequest;

/**
 * Interface for a RequestContext factory.
 * <p>
 * A request context factory is invoked by the framework at the start of the
 * request chain.  It is responsible for producing a RequestContext object
 * which is bound to the request.  The RequestContext object is a single
 * object instance with which all downstream framework elements can consult.
 * <p>
 * The RequestContext object is scoped to the request.
 * 
 * @author muzquiano
 */
public interface RequestContextFactory
{
    /**
     * Indicates whether the request context factory can produce a request context
     * for the given request object.
     * 
     * @param webRequest web request
     * 
     * @return
     */
    public boolean canHandle(WebRequest webRequest);
    
    /**
     * Produces a new RequestContext instance for a given request. Always returns
     * a new RequestContext instance - or an exception is thrown.
     * 
     * @param webRequest the web request object
     * 
     * @return The RequestContext instance
     * @throws RequestContextException
     */
    public RequestContext newInstance(WebRequest webRequest) throws RequestContextException;
}