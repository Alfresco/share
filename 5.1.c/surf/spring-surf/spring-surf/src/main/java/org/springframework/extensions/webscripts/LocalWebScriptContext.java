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

import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;

/**
 * The Context for a Local WebScript invocation.
 * 
 * This class holds references to the objects required to invoke a webscript locally.
 * Many local webscripts can be executed and merged into a single response later.
 * 
 * The most common use of this is to represent components within a template on a page.
 * 
 * @author kevinr
 * @author muzquiano
 */
public final class LocalWebScriptContext
{
    private RuntimeContainer runtimeContainer;
    private Map<String, String> tokens;
    private String executeUrl;
    private String scriptUrl;
    
    // Web Framework Elements
    private RequestContext requestContext;
    private ModelObject object;
    
    
    /**
     * @return the runtimeContainer
     */
    public RuntimeContainer getRuntimeContainer()
    {
        return this.runtimeContainer;
    }
    
    /**
     * @param runtimeContainer the runtimeContainer to set
     */
    public void setRuntimeContainer(RuntimeContainer runtimeContainer)
    {
        this.runtimeContainer = runtimeContainer;
    }
    
    /**
     * @return the tokens
     */
    public Map<String, String> getTokens()
    {
        return this.tokens;
    }
    
    /**
     * @param tokens the tokens to set
     */
    public void setTokens(Map<String, String> tokens)
    {
        this.tokens = tokens;
    }
    
    /**
     * @return the executeUrl
     */
    public String getExecuteUrl()
    {
        return this.executeUrl;
    }
    
    /**
     * @param executeUrl the executeUrl to set
     */
    public void setExecuteUrl(String executeUrl)
    {
        this.executeUrl = executeUrl;
    }
    
    /**
     * @return the scriptUrl
     */
    public String getScriptUrl()
    {
        return this.scriptUrl;
    }
    
    /**
     * @param scriptUrl the scriptUrl to set
     */
    public void setScriptUrl(String scriptUrl)
    {
        this.scriptUrl = scriptUrl;
    }
    
    /**
     * @return the renderContext
     */
    public RequestContext getRequestContext()
    {
        return this.requestContext;
    }
    
    /**
     * @param requestContext the renderContext to set
     */
    public void setRequestContext(RequestContext requestContext)
    {
        this.requestContext = requestContext;
    }
    
    /**
     * @return the ModelObject
     */
    public ModelObject getModelObject()
    {
        return this.object;
    }
    
    /**
     * @param object the ModelObject to set
     */
    public void setModelObject(ModelObject object)
    {
        this.object = object;
    }
}
