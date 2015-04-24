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

import java.io.IOException;
import java.io.Serializable;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.resource.Resource;

/*
 * @author muzquiano
 */
public final class ScriptResource extends ScriptBase
{
    private ScriptResourceContent payloadContent = null;
    private ScriptResourceContent payloadMetadata = null;
    
    final private Resource resource;

    public ScriptResource(RequestContext context, Resource resource)
    {
        super(context);
        
        this.resource = resource;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        return null;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties

    public String getId()
    {
        return this.resource.getResourceId();
    }
    
    public String getProtocolId()
    {
        return this.resource.getProtocolId();
    }
    
    public String getEndpointId()
    {
        return this.resource.getEndpointId();
    }
    
    public String getObjectId()
    {
        return this.resource.getObjectId();
    }
    
    public String getObjectTypeId()
    {
        return this.resource.getObjectTypeId();
    }
    
    public String getName()
    {
        return this.resource.getName();
    }
    
    public synchronized ScriptResourceContent getContent()
    {
        if (payloadContent == null)
        {
            try
            {
                payloadContent = new ScriptResourceContent(context, this, resource.getContent());
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        return payloadContent;
    }
    
    public String getContentUrl()
    {
        return this.resource.getContentURL();
    }
    
    public synchronized ScriptResourceContent getMetadata()
    {
        if (payloadMetadata == null)
        {
            try
            {
                payloadMetadata = new ScriptResourceContent(context, this, resource.getMetadata());
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        return payloadMetadata;
    }
    
    public String getMetadataUrl()
    {
        return this.resource.getMetadataURL();
    }
    
    public boolean getIsContainer()
    {
        return this.resource.isContainer();
    }
}