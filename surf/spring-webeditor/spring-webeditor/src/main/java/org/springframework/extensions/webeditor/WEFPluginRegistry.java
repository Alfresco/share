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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Registry of Web Editor Framework plugins.
 *
 * @author Gavin Cornwell
 */
public class WEFPluginRegistry
{
    public static final String BEAN_NAME = "wefPluginRegistry";
    
    private static final Log logger = LogFactory.getLog(WEFPluginRegistry.class);
    
    private Map<String, WEFPlugin> plugins;
    private Map<String, WEFApplication> applications;
    
    /**
     * Default constructor.
     */
    public WEFPluginRegistry()
    {
        this.plugins = new LinkedHashMap<String, WEFPlugin>(8);
        this.applications = new LinkedHashMap<String, WEFApplication>(1);
    }
    
    /**
     * Adds a plugin.
     * 
     * @param plugin The plugin to add
     */
    public void addPlugin(WEFPlugin plugin)
    {
        // add the plugin
        this.plugins.put(plugin.getName(), plugin);
        
        if (logger.isDebugEnabled())
            logger.debug("Added plugin to plugin registry: " + plugin);
        
        if (plugin instanceof WEFApplication)
        {
            // check there isn't already an application registered
            if (this.applications.size() > 0)
            {
                throw new IllegalStateException("Only one WEF application plugin is currently supported");
            }
            
            this.applications.put(plugin.getName(), (WEFApplication)plugin);
            
            if (logger.isDebugEnabled())
                logger.debug("Added application plugin to application registry: " + plugin);
        }
    }
    
    /**
     * Returns all the regsistered plugins.
     * 
     * @return List of all registered plugins
     */
    public List<WEFPlugin> getPlugins()
    {
        return new ArrayList<WEFPlugin>(this.plugins.values());
    }
    
    /**
     * Returns the plugin with the given name.
     * 
     * @param name Name of a plugin to retrieve
     * @return A WEFPlugin object or null if a plugin with the given name does not exist
     */
    public WEFPlugin getPlugin(String name)
    {
        return this.plugins.get(name);
    }
    
    /**
     * Returns all the registered application plugins.
     * 
     * @return List of all registered application plugins
     */
    public List<WEFApplication> getApplications()
    {
        return new ArrayList<WEFApplication>(this.applications.values());
    }
    
    /**
     * Returns the application plugin with the given name.
     * 
     * @param name Name of an application plugin to retrieve
     * @return A WEFApplication object or null if an application plugin with the given name does not exist
     */
    public WEFApplication getApplication(String name)
    {
        return this.applications.get(name);
    }
    
    /**
     * Returns a list of unique resources for all the registered plugins
     * and their dependencies.
     * 
     * @return List of WEFResource objects representing all resources
     */
    public List<WEFResource> getPluginResources()
    {
        // create map to hold unique list of resources
        LinkedHashMap<String, WEFResource> resourcesMap = new LinkedHashMap<String, WEFResource>(32);
        
        if (this.applications.size() > 0)
        {
            for (WEFApplication app : this.applications.values())
            {
                // recursively build a map of all the application's dependencies
                buildResourceMap(app, resourcesMap);
            }
        }
        else
        {
            for (WEFPlugin plugin : this.plugins.values())
            {
                // recursively build a map of all the plugin's dependencies
                buildResourceMap(plugin, resourcesMap);
            }
        }
        
        return new ArrayList<WEFResource>(resourcesMap.values());
    }
 
    /**
     * Returns a list of unique resources for the given resource
     * and all of it's dependencies.
     * 
     * @param resource A WEFResource object 
     * @return List of WEFResource objects representing all resources
     */
    public List<WEFResource> getAllResources(WEFResource resource)
    {
        // recursively build a map of all the resource's dependencies 
        LinkedHashMap<String, WEFResource> resourcesMap = new LinkedHashMap<String, WEFResource>(32);
        buildResourceMap(resource, resourcesMap);
        
        // return the list of unique resources
        return new ArrayList<WEFResource>(resourcesMap.values());
    }
    
    /**
     * Recursively builds a Map of unique resources for the given resource object.
     * 
     * @param resource The WEFResource to get all resources for
     * @param resources Map of unique resources found
     */
    protected void buildResourceMap(WEFResource resource, Map<String, WEFResource> resources)
    {
        // recurse through the resource's dependencies
        for (WEFResource dependency : resource.getDependencies())
        {
            buildResourceMap(dependency, resources);
        }
        
        // add the given resource to the map, keyed by the name
        if (resources.containsKey(resource.getName()) == false)
        {
            resources.put(resource.getName(), resource);
        }
    }
}
