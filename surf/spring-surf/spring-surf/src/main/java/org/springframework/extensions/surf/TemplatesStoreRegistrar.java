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

import org.springframework.extensions.webscripts.StoreRegistrar;

/**
 * Registers a store that contains Surf template files.
 * 
 * The store will be registered into the templates search path.
 * 
 * @author muzquiano
 */
public class TemplatesStoreRegistrar extends StoreRegistrar
{
    protected static final String TEMPLATES_SEARCHPATH_ID = "webframework.templates.searchpath";
    
    protected String getSearchPathId()
    {
        return TEMPLATES_SEARCHPATH_ID;
    }            
}