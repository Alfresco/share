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

package org.springframework.extensions.surf.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;

/**
 * An abstract Request Context implementation that is responsible for holding the value
 * of the Request Context for the current thread. It supplies a static instance getter
 * that can be used directly to return the current RequestContext object.
 * 
 * @author Kevin Roast
 */
public abstract class ThreadLocalRequestContext extends BaseFactoryBean implements RequestContext
{
    private static final long serialVersionUID = -5110756572122308893L;

    private static Log logger = LogFactory.getLog(ThreadLocalRequestContext.class);
    
    /** The RequestContext holder for the current thread */
    private static ThreadLocal<RequestContext> instance = new ThreadLocal<RequestContext>();
    
    /**
     * Override the default constructor to set the RequestContext value for the current thread
     */
    protected ThreadLocalRequestContext(WebFrameworkServiceRegistry serviceRegistry)
    {
        super(serviceRegistry);
        
        if (logger.isDebugEnabled())
            logger.debug("Setting RequestContext " + this.getId() + " on thread: " + Thread.currentThread().getName());
        instance.set(this);
    }
    
    /**
     * Instance getter to return the RequestContext for the current thread
     * 
     * @return RequestContext
     */
    public static RequestContext getRequestContext()
    {
        return instance.get();
    }

    /**
     * Release resources
     */
    public void release()
    {
        if (logger.isDebugEnabled())
            logger.debug("Releasing RequestContext " + this.getId() + " from thread: " + Thread.currentThread().getName());
        instance.remove();
    }
}