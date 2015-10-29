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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorFactory;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.processor.AbstractScriptProcessor;

/**
 * Registers a template processor with the web script framework
 * 
 * @author muzquiano
 */
public class TemplateProcessorRegistrar implements ApplicationContextAware
{
    private static final String WEBSCRIPTS_TEMPLATE_REGISTRY_ID = "webframework.webscripts.registry.templateprocessor";
    private static final String TEMPLATES_TEMPLATE_REGISTRY_ID = "webframework.templates.registry.templateprocessor";

    private static final String WEBSCRIPTS_SEARCHPATH_ID = "webframework.webscripts.searchpath";
    private static final String TEMPLATES_SEARCHPATH_ID = "webframework.templates.searchpath";
    
    private ApplicationContext applicationContext;
    private TemplateProcessorRegistry webscriptsRegistry;
    private TemplateProcessorRegistry templatesRegistry;
    private TemplateProcessorFactory factory;
    private SearchPath webscriptsSearchPath;
    private SearchPath templatesSearchPath;    
    private String name;
    private String extension;
    
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public void setWebScriptsRegistry(TemplateProcessorRegistry webscriptsRegistry)
    {
        this.webscriptsRegistry = webscriptsRegistry;
    }
    
    public void setTemplatesRegistry(TemplateProcessorRegistry templatesRegistry)
    {
        this.templatesRegistry = templatesRegistry;
    }
    
    public void setFactory(TemplateProcessorFactory factory)
    {
        this.factory = factory;
    }
    
    public void setWebscriptsSearchPath(SearchPath webscriptsSearchPath)
    {
        this.webscriptsSearchPath = webscriptsSearchPath;
    }
    
    public void setTemplatesSearchPath(SearchPath templatesSearchPath)
    {
        this.templatesSearchPath = templatesSearchPath;
    }    
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setExtension(String extension)
    {
        this.extension = extension;
    }
    
    public void init()
    {
        if (webscriptsRegistry == null)
        {
            webscriptsRegistry = (TemplateProcessorRegistry) applicationContext.getBean(WEBSCRIPTS_TEMPLATE_REGISTRY_ID);
        }
        
        if (templatesRegistry == null)
        {
            templatesRegistry = (TemplateProcessorRegistry) applicationContext.getBean(TEMPLATES_TEMPLATE_REGISTRY_ID);
        }
        
        if (this.factory != null)
        {
            TemplateProcessor templateProcessor1 = factory.newInstance();           
            if (webscriptsSearchPath == null)
            {
                webscriptsSearchPath = (SearchPath) applicationContext.getBean(WEBSCRIPTS_SEARCHPATH_ID);
            }
            if (templateProcessor1 instanceof AbstractScriptProcessor)
            {
                ((AbstractScriptProcessor)templateProcessor1).setSearchPath(webscriptsSearchPath);
            }
            webscriptsRegistry.registerTemplateProcessor(templateProcessor1, extension, name);
            
            TemplateProcessor templateProcessor2 = factory.newInstance();
            if (templatesSearchPath == null)
            {
                templatesSearchPath = (SearchPath) applicationContext.getBean(TEMPLATES_SEARCHPATH_ID);
            }
            if (templateProcessor2 instanceof AbstractScriptProcessor)
            {
                ((AbstractScriptProcessor)templateProcessor2).setSearchPath(templatesSearchPath);
            }
            templatesRegistry.registerTemplateProcessor(templateProcessor2, extension, name);
        }
    }
}