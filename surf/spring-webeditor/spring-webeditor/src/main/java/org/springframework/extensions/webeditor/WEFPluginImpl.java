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

package org.springframework.extensions.webeditor;

/**
 * Implementation of a WEFPlugin that is capable of registering itself
 * with a plugin registry.
 *
 * @author Gavin Cornwell
 */
public class WEFPluginImpl extends WEFResourceImpl implements WEFPlugin
{
    public static final String TYPE_PLUGIN = "plugin"; 
    
    protected WEFPluginRegistry registry;
    
    /**
     * Default constructor
     */
    public WEFPluginImpl()
    {
        this.type = TYPE_PLUGIN;
    }
    
    /**
     * Sets the WEFPluginRegistry instance to register all plugins with.
     * 
     * @param registry The WEFPluginRegistry instance
     */
    public void setPluginRegistry(WEFPluginRegistry registry)
    {
        this.registry = registry;
    }
    
    /**
     * Registers this plugin with the plugin registry.
     */
    public void register()
    {
        if (this.registry != null)
        {
            this.registry.addPlugin(this);
        }
    }
}
