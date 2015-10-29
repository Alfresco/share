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

import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.exception.ResourceLoaderException;
import org.springframework.extensions.surf.resource.AbstractCachingResourceLoader;
import org.springframework.extensions.surf.resource.Resource;

/**
 * Resource loader implementation for the Alfresco repository
 * 
 * @author muzquiano
 */
public class AlfrescoResourceLoader extends AbstractCachingResourceLoader
{
    public AlfrescoResourceLoader(String protocolId, String endpointId, FrameworkBean frameworkUtil)
    {
        super(protocolId, endpointId, frameworkUtil);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractCachingResourceLoader#buildResource(java.lang.String)
     */
    public Resource buildResource(String objectId)
        throws ResourceLoaderException
    {
        return new AlfrescoResource(getProtocolId(), getEndpointId(), objectId, frameworkUtil);
    }
}
