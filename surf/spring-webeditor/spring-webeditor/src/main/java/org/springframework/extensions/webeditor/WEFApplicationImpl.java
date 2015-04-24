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
import java.util.List;

/**
 * Implementation of a WEFApplication that is capable of registering itself
 * with a plugin registry.
 *
 * @author Gavin Cornwell
 */
public class WEFApplicationImpl extends WEFPluginImpl implements WEFApplication
{
    public static final String TYPE_APPLICATION = "application";
    
    /**
     * Default constructor.
     */
    public WEFApplicationImpl()
    {
        this.type = TYPE_APPLICATION;
    }

    /**
     * Returns the list of dependencies for the application, this includes
     * the configured dependencies and automatically includes 
     * all non-application plugins.
     * 
     * @return List of WEFResource objects representing the dependencies
     */
    @Override
    public List<WEFResource> getDependencies()
    {
        // start with direct dependencies 
        List<WEFResource> dependencies = new ArrayList<WEFResource>(super.getDependencies());
        
        // add all non application plugins as dependencies
        List<WEFPlugin> plugins = this.registry.getPlugins();
        for (WEFPlugin plugin : plugins)
        {
            if ((plugin instanceof WEFApplication) == false)
            {
                dependencies.add(plugin);
            }
        }
        
        return dependencies;
    }
}
