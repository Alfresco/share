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

import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.StoreRegistrar;

/**
 * Registers a store that contains Surf web script files.
 * 
 * The store will be registered into the templates search path and the web scripts search path
 * 
 * @author muzquiano
 */
public class WebScriptsStoreRegistrar extends StoreRegistrar
{
    protected static final String WEBSCRIPTS_SEARCHPATH_ID = "webframework.webscripts.searchpath";
    protected static final String TEMPLATES_SEARCHPATH_ID = "webframework.templates.searchpath";
    
    private SearchPath templatesSearchPath;
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.StoreRegistrar#getSearchPathId()
     */
    protected String getSearchPathId()
    {
        return WEBSCRIPTS_SEARCHPATH_ID;
    }
    
    /**
     * Sets the templates search path
     * 
     * @param templatesSearchPath
     */
    public void setTemplatesSearchPath(SearchPath templatesSearchPath)
    {
        this.templatesSearchPath = templatesSearchPath;
    }

    /**
     * Overrides the templates search path id
     * 
     * @return search path id
     */
    protected String getTemplatesSearchPathId()
    {
        return WEBSCRIPTS_SEARCHPATH_ID;
    }
    
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.StoreRegistrar#init()
     */
    public void init()
    {
        initWebScripts();
        initTemplates();
    }
    
    private void initWebScripts()
    {
        if (searchPath == null)
        {
            searchPath = (SearchPath) getApplicationContext().getBean(getSearchPathId());
        }

        plugin(store, searchPath, prepend);                
    }
    
    private void initTemplates()
    {
        if (templatesSearchPath == null)
        {
            templatesSearchPath = (SearchPath) getApplicationContext().getBean(getTemplatesSearchPathId());
        }

        plugin(store, templatesSearchPath, prepend);
    }
}