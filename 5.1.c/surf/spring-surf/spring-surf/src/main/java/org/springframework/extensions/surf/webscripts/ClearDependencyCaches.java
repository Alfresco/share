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
package org.springframework.extensions.surf.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.DojoDependencyHandler;
import org.springframework.extensions.surf.I18nDependencyHandler;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This is the Java controller for the clear dependency caches WebScript which allows admins to clear the caches 
 * of running systems.
 *  
 * @author David Draper
 */
public class ClearDependencyCaches extends DeclarativeWebScript
{
    private DependencyHandler dependencyHandler;
    private DependencyAggregator dependencyAggregator;
    private DojoDependencyHandler dojoDependencyHandler;
    private I18nDependencyHandler i18nDependencyHandler;
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }
    public void setDojoDependencyHandler(DojoDependencyHandler dojoDependencyHandler)
    {
        this.dojoDependencyHandler = dojoDependencyHandler;
    }
    public void setI18nDependencyHandler(I18nDependencyHandler i18nDependencyHandler)
    {
        this.i18nDependencyHandler = i18nDependencyHandler;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        this.dependencyHandler.clearCaches();
        this.dependencyAggregator.clearCaches();
        this.dojoDependencyHandler.clearCaches();
        this.i18nDependencyHandler.clearCaches();
        return model;
    }
}
