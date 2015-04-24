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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.resource.AbstractResource;
import org.springframework.extensions.surf.resource.ResourceContent;
import org.springframework.extensions.surf.resource.ResourceContentImpl;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;

/**
 * Virtualized web application resource
 * 
 * Object ids are of the following format:
 * 
 *    images/abc.gif
 *    WEB-INF/config/file.xml
 *    webapp://images/abc.gif
 *    webapp://WEB-INF/config/file.xml 
 * 
 * @author muzquiano
 */
public class VirtualizedWebappResource extends AbstractResource
{
    private static Log logger = LogFactory.getLog(VirtualizedWebappResource.class);
    
    public VirtualizedWebappResource(String protocolId, String endpointId, String objectId, FrameworkBean frameworkUtil)
    {
        super(protocolId, endpointId, objectId, frameworkUtil);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getMetadata()
     */
    public ResourceContent getMetadata() throws IOException
    {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getMetadataURL()
     */
    public String getMetadataURL()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getContent()
     */
    public ResourceContent getContent() throws IOException
    {
        return new ResourceContentImpl(this, getContentURL(), frameworkUtil);        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getContentURL()
     */
    public String getContentURL()
    {
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        
        String contentURL = context.getContextPath() + "/res" + this.getObjectId();
        
        if (logger.isDebugEnabled())
            logger.debug("Formed virtual content url: " + contentURL);
        
        return contentURL;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getObjectTypeId()
     */
    public String getObjectTypeId()
    {
        String extension = null;
        
        // get extension
        int i = getContentURL().lastIndexOf(".");
        if (i > -1)
        {
            extension = getContentURL().substring(i+1);
        }
        
        return extension;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getResourceTypeId()
     */
    public String getResourceTypeId()
    {
        return "webapp";        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.AbstractResource#isContainer()
     */
    public boolean isContainer()
    {
        // TODO: determine whether the resource is a container...
        // assume not
        return false;
    }
    
}
