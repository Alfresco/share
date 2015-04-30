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

package org.springframework.extensions.surf.mvc;

import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Script extension helper with utility methods to manipulate ViewResolver caches.
 * This is to allow views to be removed from the cache at runtime e.g. to mirror
 * changes to Surf ModelObject instances such as removed dashboard views.
 * 
 * @author Kevin Roast
 */
public class ViewResolverScriptUtil extends BaseProcessorExtension
{
    private UrlBasedViewResolver viewResolver;
    
    public void setViewResolver(UrlBasedViewResolver viewResolver)
    {
        this.viewResolver = viewResolver;
    }
    
    /**
     * Remove a view from the View Resolver cache.
     * 
     * @param viewName  View to remove
     */
    public void removeFromCache(String viewName)
    {
        this.viewResolver.removeFromCache(viewName, null);
    }
}
