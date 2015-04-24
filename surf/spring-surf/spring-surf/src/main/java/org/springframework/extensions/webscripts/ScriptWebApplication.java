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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.resource.ResourceService;

/**
 * <p>Helper object for dealing with the web application's environment.
 * </p><p>
 * This object can be used on both the production and preview tiers to gain access to the correct
 * web application mount points and more.
 * </p>
 * @author muzquiano
 */
public final class ScriptWebApplication extends ScriptBase
{
    private static final long serialVersionUID = -4449467261985787691L;

    private static Log logger = LogFactory.getLog(ScriptWebApplication.class);
    
    /**
     * Constructs a new ScriptWebApplication object.
     * 
     * @param context   The RenderContext instance for the current request
     */
    public ScriptWebApplication(RequestContext context)
    {
        super(context);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        return null;
    }


    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Returns the root web application context
     */
    public String getContext()
    {        
        StringBuilder builder = new StringBuilder(512);

        // path to web application
        builder.append(context.getContextPath());
        
        // path to servlet
        // TODO - needs to be tested to resolve how this works
        //builder.append(context.getServletPath());
        
        // use the resource controller
        builder.append("/res");            
        
        return builder.toString();
    }  

    /**
     * Performs a server-side include of a web asset
     * 
     * The result string is returned.
     * 
     * Value paths are:
     * 
     *    /a/b/c.gif
     *    /images/test.jpg
     * 
     * @param path
     * @param endpointId
     * 
     * @return
     */
    public String include(String relativePath)
    {
        String buffer = null;
        
        try
        {   
            // resource service
            ResourceService resourceService = FrameworkUtil.getServiceRegistry().getResourceService();
            
            // resource
            Resource resource = resourceService.getResource(relativePath);
            if (resource != null)
            {
                buffer = resource.getContent().getStringContent();
            }
            
            // some post treatment of the buffer
            buffer = buffer.replace("${app.context}", this.getContext());
        }
        catch (Exception ex)
        {
            logger.warn("Unable to include: " + relativePath, ex);
        }
        
        return buffer;
    }
}
