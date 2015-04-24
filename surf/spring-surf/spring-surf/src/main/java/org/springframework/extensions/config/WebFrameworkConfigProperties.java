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

package org.springframework.extensions.config;

import org.springframework.extensions.config.WebFrameworkConfigElement.ErrorHandlerDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.FormatDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.PersisterConfigDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.ResourceLoaderDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.ResourceResolverDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.RuntimeConfigDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.SystemPageDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.TagLibraryDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.TypeDescriptor;

/**
 * Web Framework configuration interface
 * 
 * @author muzquiano
 */
public interface WebFrameworkConfigProperties
{
    // formats
    public String[] getFormatIds();
    public FormatDescriptor getFormatDescriptor(String id);
    
    // error handlers
    public String[] getErrorHandlerIds();
    public ErrorHandlerDescriptor getErrorHandlerDescriptor(String id);
    
    // system pages
    public String[] getSystemPageIds();
    public SystemPageDescriptor getSystemPageDescriptor(String id);
    
    // tag libraries
    public String[] getTagLibraryIds();
    public TagLibraryDescriptor getTagLibraryDescriptor(String id);
    
    // resource loaders
    public String[] getResourceLoaderIds();
    public ResourceLoaderDescriptor getResourceLoaderDescriptor(String id);
    
    // resource resolvers
    public String[] getResourceResolverIds();    
    public ResourceResolverDescriptor getResourceResolverDescriptor(String id);    
    
    // debug
    public boolean isTimerEnabled();

    // default services
    public String getDefaultUserFactoryId();

    // default application settings
    public String getDefaultFormatId();
    public String getDefaultRegionChrome();
    public String getDefaultComponentChrome();
    public String[] getDefaultPageTypeIds();
    public String getDefaultPageTypeInstanceId(String id);
    public String getDefaultThemeId();
    public String getDefaultSiteConfigurationId();
    
    // persister
    public String getDefaultPersisterId();
            
    // object types
    public String[] getTypeIds();
    public TypeDescriptor getTypeDescriptor(String id);
    
    // persister configuration
    public PersisterConfigDescriptor getPersisterConfigDescriptor();
    
    /** AUTOWIRE HELPERS **/
    
    // autowire runtime properties
    public String getAutowireRuntimeId();
    
    // autowire mode properties
    public String getAutowireModeId();    
    public boolean isAutowireModeDevelopment();
    public boolean isAutowireModeProduction();
    public boolean isAutowireModePreview();
            
    // are we in preview mode
    public boolean isPreviewEnabled();
        
    // runtime config
    public RuntimeConfigDescriptor getRuntimeConfigDescriptor(String id);
    
    public static final String MANUAL_MODULE_DEPLOYMENT = "manual";
    public static final String AUTO_MODULE_DEPLOYMENT = "auto";
    
    public String getModuleDeploymentMode();
}
