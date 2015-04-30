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

import org.springframework.extensions.webscripts.PreviewContext;

/**
 * A thread local sandbox context implementation that is responsible for holding the value
 * of the sandbox context for the current thread.  It supplies a static instance getter
 * that can be used directly to return the current SandboxContext object.
 * 
 * @author muzquiano
 */
public class ThreadLocalPreviewContext extends PreviewContext
{
    /** The SandboxContext holder for the current thread */
    private static ThreadLocal<PreviewContext> instance = new ThreadLocal<PreviewContext>();
    
    
    /**
     * Override the default constructor to set the SandboxContext value for the current thread
     */
    public ThreadLocalPreviewContext(String storeId, String webappId)
    {
        super(storeId, webappId);
        
        ThreadLocalPreviewContext.instance.set(this);
    }
    
    /**
     * Instance getter to return the PreviewContext for the current thread
     * 
     * @return PreviewContext
     */
    public static ThreadLocalPreviewContext getPreviewContext()
    {
        return (ThreadLocalPreviewContext) instance.get();
    }

    /**
     * Release resources
     */
    public void release()
    {
        instance.remove();
    }
}
