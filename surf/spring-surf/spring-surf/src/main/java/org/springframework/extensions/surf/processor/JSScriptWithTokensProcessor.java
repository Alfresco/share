/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.springframework.extensions.surf.processor;

import java.util.Map;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.extensions.webscripts.processor.JSScriptProcessor;

/**
 * This extends the the default JSScriptProcessor to provide the capability to substitute variables
 * in the requested resource name. This currently only supports the replacing the "aikauVersion" 
 * token in order that page model library files can be accurately located in the version of Aikau
 * that is in use. In the future this could be further enhanced to support additional tokens but
 * was initially created to address a single use-case. 
 *  
 * @author Dave Draper
 */
public class JSScriptWithTokensProcessor extends JSScriptProcessor
{
    /**
     * Reference to the Web Framework configuration that will enable us to access data such as the 
     * "aikau-version" configuration. This will be populated via Spring dependency injection.
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    /**
     * Returns the configuration for the web framework.
     * 
     * @return WebFrameworkConfigElement
     */
    public WebFrameworkConfigElement getWebFrameworkConfigElement()
    {
        return webFrameworkConfigElement;
    }

    /**
     * Required by Spring to inject the Web Framework configuration.
     * 
     * @param webFrameworkConfigElement WebFrameworkConfigElement
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }
    
    /**
     * Returns the current Aikau version. This will found in the version of Aikau that is in use.
     * It will typically be the latest version of Aikau found unless module deployment order has
     * been manually set to use a different version.
     * 
     * @return A String containing the current Aikau version.
     */
    @SuppressWarnings("unchecked")
    public String getAikauVersion()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        String aikauVersion = null;
        ScriptConfigModel config = rc.getExtendedScriptConfigModel(null);
        Map<String, ConfigElement> configs = (Map<String, ConfigElement>)config.getScoped().get("WebFramework");
        if (configs != null)
        {
            WebFrameworkConfigElement wfce = (WebFrameworkConfigElement) configs.get("web-framework");
            aikauVersion = wfce.getAikauVersion();
        }
        else
        {
            aikauVersion = this.getWebFrameworkConfigElement().getAikauVersion();
        }
        return aikauVersion;
    }
    
    /**
     * Extends the inherited method to replace any "{aikauVersion}" tokens with the current version of
     * Aikau that is in use.
     * 
     * @param resource Script resource to load. Supports either classpath: prefix syntax or a resource path within the WebScript stores. 
     * @return The content from the resource, null if not recognised format
     */
    @Override
    public String loadScriptResource(String resource)
    {
        String aikauVersion = this.getAikauVersion();
        if (aikauVersion != null)
        {
            resource = resource.replaceAll("\\{aikauVersion\\}", aikauVersion);
        }
        return super.loadScriptResource(resource);
    }
}
