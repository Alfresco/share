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

import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;

/**
 * Servlet implementation of a request context object for Surf.
 * 
 * @see AbstractRequestContext
 * @see RequestContext
 * 
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class ServletRequestContext extends AbstractRequestContext
{    
    private static final long serialVersionUID = 2855264613512964578L;

    private LinkBuilder linkBuilder = null;

    /**
     * <p>Constructor for default servlet container request context</p>
     * 
     * @param webRequest servlet web request object
     * @param frameworkBean
     * @param linkBuilder
     */
    public ServletRequestContext(WebFrameworkServiceRegistry serviceRegistry, 
                                 FrameworkBean frameworkBean, 
                                 LinkBuilder linkBuilder)
    {
        super(serviceRegistry, frameworkBean);
        this.linkBuilder = linkBuilder;
    }
    
    /**
     * <p>Constructor for default servlet container request context.</p>
     * 
     * @param webRequest servlet web request object
     * @param linkBuilder 
     * @deprecated
     */
    public ServletRequestContext(WebFrameworkServiceRegistry serviceRegistry, LinkBuilder linkBuilder)
    {
        super(serviceRegistry);
        this.linkBuilder = linkBuilder;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.support.AbstractRequestContext#getLinkBuilder()
     */
    public LinkBuilder getLinkBuilder()    
    {
        return linkBuilder;
    }

    public boolean isExtensibilitySuppressed()
    {
        return false;
    }
}