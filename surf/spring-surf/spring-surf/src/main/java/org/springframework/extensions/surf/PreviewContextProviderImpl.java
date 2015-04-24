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
import org.springframework.extensions.webscripts.PreviewContextProvider;

/**
 * Web Framework sandbox context provider implementation
 *  
 * @author muzquiano
 */
public class PreviewContextProviderImpl implements PreviewContextProvider
{
    private String defaultStoreId = null;
    private String defaultWebappId = null;
    
    public PreviewContext provide()
    {
        String storeId = null;
        String webappId = null;
        
        // retrieve the sandbox context (if we have one)
        PreviewContext sandboxContext = ThreadLocalPreviewContext.getPreviewContext();
        if (sandboxContext != null)
        {
            // store id 
            storeId = (String) sandboxContext.getStoreId();

            // webapp id 
            webappId = (String) sandboxContext.getWebappId();            
        }
        
        // process any defaults
        if (storeId == null)
        {
            storeId = defaultStoreId;
        }
        if (webappId == null)
        {
            webappId = defaultWebappId;
        }
        
        return new PreviewContext(storeId, webappId);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.SandboxContextProvider#setDefaultStoreId(java.lang.String)
     */
    public void setDefaultStoreId(String defaultStoreId)
    {
        this.defaultStoreId = defaultStoreId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.SandboxContextProvider#getDefaultStoreId()
     */
    public String getDefaultStoreId()
    {
        return this.defaultStoreId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.SandboxContextProvider#setDefaultWebappId(java.lang.String)
     */
    public void setDefaultWebappId(String defaultWebappId)
    {
        this.defaultWebappId = defaultWebappId;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.SandboxContextProvider#getDefaultWebappId()
     */
    public String getDefaultWebappId()
    {
        return this.defaultWebappId;
    }
}
