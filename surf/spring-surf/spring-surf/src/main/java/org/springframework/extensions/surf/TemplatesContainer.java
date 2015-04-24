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

import org.springframework.extensions.webscripts.ScriptProcessorRegistry;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;

/**
 * Simple container to gather together services that support the
 * separation of a template engine within the Web Framework.
 * 
 * Web Scripts have their own script processor and template processor
 * registries.
 * 
 * Templates process outside of this and can have entirely different
 * processors or search paths.
 * 
 * @author muzquiano
 */
public class TemplatesContainer
{
    private TemplateProcessorRegistry templateProcessorRegistry;
    private ScriptProcessorRegistry scriptProcessorRegistry;
    
    /**
     * Sets the template processor registry.
     * 
     * @param templateProcessorRegistry the new template processor registry
     */
    public void setTemplateProcessorRegistry(TemplateProcessorRegistry templateProcessorRegistry)
    {
        this.templateProcessorRegistry = templateProcessorRegistry;
    }
    
    /**
     * Gets the template processor registry.
     * 
     * @return the template processor registry
     */
    public TemplateProcessorRegistry getTemplateProcessorRegistry()
    {
        return this.templateProcessorRegistry;
    }
    
    /**
     * Sets the script processor registry.
     * 
     * @param scriptProcessorRegistry the new script processor registry
     */
    public void setScriptProcessorRegistry(ScriptProcessorRegistry scriptProcessorRegistry)
    {
        this.scriptProcessorRegistry = scriptProcessorRegistry;
    }
    
    /**
     * Gets the script processor registry.
     * 
     * @return the script processor registry
     */
    public ScriptProcessorRegistry getScriptProcessorRegistry()
    {
        return this.scriptProcessorRegistry;
    }
    
    /**
     * Resets the templates container
     */
    public void reset()
    {
        this.scriptProcessorRegistry.reset();
        this.templateProcessorRegistry.reset();
    }
}
