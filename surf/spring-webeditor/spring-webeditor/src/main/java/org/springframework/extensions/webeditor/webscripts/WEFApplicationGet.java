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

package org.springframework.extensions.webeditor.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.webeditor.WEFApplication;
import org.springframework.extensions.webeditor.WEFPlugin;
import org.springframework.extensions.webeditor.WEFPluginRegistry;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * WebScript implementation for the WEF application script call.
 * <p>
 * Responsible for generating an WEFApplication object that retrieves and 
 * initialises all regsitered plugins.
 *
 * @author Gavin Cornwell
 */
public class WEFApplicationGet extends DeclarativeWebScript
{
    protected WEFPluginRegistry pluginRegistry;
    
    public void setPluginRegistry(WEFPluginRegistry registry)
    {
        this.pluginRegistry = registry;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        
        // ensure an application plugin has been registered
        if (this.pluginRegistry.getApplications().size() == 0)
        {
            throw new IllegalStateException("A WEF application plugin could not be found");
        }

        // add the application name to the model
        model.put("appName", this.pluginRegistry.getApplications().get(0).getName());
        
        // build list of plugins excluding application plugins
        ArrayList<WEFPlugin> plugins = new ArrayList<WEFPlugin>(8);
        for (WEFPlugin plugin : this.pluginRegistry.getPlugins())
        {
            if ((plugin instanceof WEFApplication) == false)
            {
                plugins.add(plugin);
            }
        }

        // add the plugins to the model
        model.put("plugins", plugins);

        return model;
    }
}