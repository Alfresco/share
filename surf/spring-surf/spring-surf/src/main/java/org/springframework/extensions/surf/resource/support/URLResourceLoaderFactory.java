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

package org.springframework.extensions.surf.resource.support;

import org.springframework.extensions.surf.resource.AbstractCachingResourceLoaderFactory;
import org.springframework.extensions.surf.resource.ResourceLoader;

/**
 * Resource loader factory for general http urls.
 * 
 * @author muzquiano
 */
public class URLResourceLoaderFactory extends AbstractCachingResourceLoaderFactory
{
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractCachingResourceLoaderFactory#buildResourceLoader(java.lang.String, java.lang.String)
     */
    public ResourceLoader buildResourceLoader(String protocolId, String endpointId)
    {
        return new URLResourceLoader(protocolId, endpointId, frameworkUtils);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractResourceLoaderFactory#getSupportedProtocols()
     */
    public String[] getSupportedProtocols()
    {
        return new String[] { "http", "https" };
    }
}
