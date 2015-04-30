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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Web Framework configuration implementation
 * 
 * @author muzquiano
 * @author David Draper
 */
public class WebFrameworkConfigElement extends ConfigElementAdapter implements WebFrameworkConfigProperties
{
    private static final long serialVersionUID = 1L;
    public static final String MODE_DEVELOPMENT = "development";
    public static final String MODE_PREVIEW = "preview";
    public static final String MODE_PRODUCTION = "production";
    
    public static final String DEFAULT_WEBFRAMEWORK_USER_FACTORY_ID = "webframework.factory.user.default";
    
    public static final String DEFAULT_WEBFRAMEWORK_MODEL_OBJECT_SEARCH_PATH_ID = "webframework.modelobject.searchpath";
    public static final String DEFAULT_WEBFRAMEWORK_MODEL_OBJECT_STORE_ID = "webframework.model.store.classpath";

    public static final String CONFIG_ELEMENT_ID = "web-framework";

    protected HashMap<String, FormatDescriptor> formats = null;
    protected HashMap<String, ErrorHandlerDescriptor> errorHandlers = null;
    protected HashMap<String, SystemPageDescriptor> systemPages = null;
    protected HashMap<String, TagLibraryDescriptor> tagLibraries = null;
    protected HashMap<String, String> pageTypes = null;
    protected HashMap<String, TypeDescriptor> types = null;
    protected HashMap<String, ResourceLoaderDescriptor> resourceLoaders = null;
    protected HashMap<String, ResourceResolverDescriptor> resourceResolvers = null;
    protected HashMap<String, RuntimeConfigDescriptor> runtimeConfigs = null;
    protected List<Pattern> resourcesDeniedPaths = null;

    protected boolean isTimerEnabled = false;

    // default services
    protected String defaultLinkBuilderFactoryId = null;
    protected String defaultRequestContextFactoryId = null;
    protected String defaultUserFactoryId = null;

    // default application settings
    protected String defaultFormatId = null;
    protected String defaultRegionChrome = null;
    protected String defaultComponentChrome = null;
    protected String defaultSubComponentChrome = null;
    protected String defaultTheme = null;
    protected String defaultSiteConfiguration = null;

    /**
     * <p>Indicates whether or not SurfBug is enabled. This property will be used by the
     * <code>RenderService</code> to determine whether or not to add debug HTML to the output
     * stream.</p>
     */
    private boolean surfBugEnabled = false;
    
    /**
     * <p>Indicates whether or not SurfBug is enabled</p>
     * @return <code>true</code> if SurfBug is enabled and <code>false</code> otherwise.
     */
    public boolean isSurfBugEnabled()
    {
        return surfBugEnabled;
    }

    /**
     * <p>Updates the status of SurfBug to indicate whether it is enabled or disabled</code>. This method
     * has been provided to be invoked by the toggle_surfbug WebScript.</p>
     * @param surfBugEnabled
     */
    public void setSurfBugEnabled(boolean surfBugEnabled)
    {
        this.surfBugEnabled = surfBugEnabled;
    }

    /**
     * <p>The Spring Surf configuration for an application can specify a SurfBug object
     * to use for debugging. All though multiple versions of SurfBug can be defined only
     * one will ever be used for debugging.</p>
     */
    protected String surfBug = null;
    
    /**
     * <p>Retrieves the configured SurfBug type for the application. There should always be and
     * instance of SurfBug as one is defined in the default configuration.</p>
     * 
     * @return The SurfBug object ID configured for the application. 
     */
    public String getSurfBug()
    {
        return surfBug;
    }

    // persister
    protected String defaultPersisterId = null;

    // Persister Configuration
    protected PersisterConfigDescriptor persisterConfigDescriptor = null;    
        
    // AutoWire Settings    
    protected String autowireModeId;
    protected String autowireRuntimeId;
    
    // ModuleDeployment settings...
    protected String moduleDeploymentMode;
    protected Boolean enableAutoDeployModules;
    
    // Checksum dependencies settings...
    protected Boolean useChecksumDependencies;
    
    protected Boolean generateCssDataImages;
    
    protected Boolean aggregateDependencies;
    
    protected Boolean calculateWebScriptDependencies;
    
    protected Boolean enableRemoteResourceHandling;
    
    protected Boolean enableExtensionModulesOnGuestPages;
    
    protected Boolean enableDynamicExtensions;
    
    protected Boolean disableResourceCaching;
    
    /**
     * Default Constructor
     */
    public WebFrameworkConfigElement()
    {
        super(CONFIG_ELEMENT_ID);

        formats = new HashMap<String, FormatDescriptor>();
        errorHandlers = new HashMap<String, ErrorHandlerDescriptor>();
        systemPages = new HashMap<String, SystemPageDescriptor>();
        tagLibraries = new HashMap<String, TagLibraryDescriptor>();
        pageTypes = new HashMap<String, String>();
        types = new HashMap<String, TypeDescriptor>();
        
        resourceLoaders = new HashMap<String, ResourceLoaderDescriptor>();
        resourceResolvers = new HashMap<String, ResourceResolverDescriptor>();
        resourcesDeniedPaths = new ArrayList<Pattern>();

        runtimeConfigs = new HashMap<String, RuntimeConfigDescriptor>();
        
        isTimerEnabled = false;
    }

    /* (non-Javadoc)
     * @see org.alfresco.config.element.GenericConfigElement#combine(org.alfresco.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
        WebFrameworkConfigElement configElement = (WebFrameworkConfigElement) element;

        // new combined element
        WebFrameworkConfigElement combinedElement = new WebFrameworkConfigElement();

        // copy in our things
        combinedElement.formats.putAll(this.formats);
        combinedElement.errorHandlers.putAll(this.errorHandlers);
        combinedElement.systemPages.putAll(this.systemPages);
        combinedElement.tagLibraries.putAll(this.tagLibraries);
        combinedElement.types.putAll(this.types);
        combinedElement.pageTypes.putAll(this.pageTypes);
        combinedElement.resourceLoaders.putAll(this.resourceLoaders);
        combinedElement.resourceResolvers.putAll(this.resourceResolvers);
        combinedElement.resourcesDeniedPaths.addAll(this.resourcesDeniedPaths);
        combinedElement.runtimeConfigs.putAll(this.runtimeConfigs);

        // override with things from the merging object
        combinedElement.formats.putAll(configElement.formats);
        combinedElement.errorHandlers.putAll(configElement.errorHandlers);
        combinedElement.systemPages.putAll(configElement.systemPages);
        combinedElement.tagLibraries.putAll(configElement.tagLibraries);
        combinedElement.types.putAll(configElement.types);
        combinedElement.pageTypes.putAll(configElement.pageTypes);
        combinedElement.resourceLoaders.putAll(configElement.resourceLoaders);
        combinedElement.resourceResolvers.putAll(configElement.resourceResolvers);
        combinedElement.resourcesDeniedPaths.addAll(configElement.resourcesDeniedPaths);
        combinedElement.runtimeConfigs.putAll(configElement.runtimeConfigs);
        
        // other properties
        combinedElement.isTimerEnabled = this.isTimerEnabled;
        if (configElement.isTimerEnabled)
        {
            combinedElement.isTimerEnabled = configElement.isTimerEnabled;
        }
        
        // default services
        combinedElement.defaultLinkBuilderFactoryId = this.defaultLinkBuilderFactoryId;
        if (configElement.defaultLinkBuilderFactoryId != null)
        {
            combinedElement.defaultLinkBuilderFactoryId = configElement.defaultLinkBuilderFactoryId;
        }
        combinedElement.defaultRequestContextFactoryId = this.defaultRequestContextFactoryId;
        if (configElement.defaultRequestContextFactoryId != null)
        {
            combinedElement.defaultRequestContextFactoryId = configElement.defaultRequestContextFactoryId;
        }
        combinedElement.defaultUserFactoryId = this.defaultUserFactoryId;
        if (configElement.defaultUserFactoryId != null)
        {
            combinedElement.defaultUserFactoryId = configElement.defaultUserFactoryId;
        }

        // default application settings
        combinedElement.defaultFormatId = this.defaultFormatId;
        if (configElement.defaultFormatId != null)
        {
            combinedElement.defaultFormatId = configElement.defaultFormatId;
        }        
        combinedElement.defaultRegionChrome = this.defaultRegionChrome;
        if (configElement.defaultRegionChrome != null)
        {
            combinedElement.defaultRegionChrome = configElement.defaultRegionChrome;
        }
        combinedElement.defaultComponentChrome = this.defaultComponentChrome;
        if (configElement.defaultComponentChrome != null)
        {
            combinedElement.defaultComponentChrome = configElement.defaultComponentChrome;
        }
        combinedElement.defaultSubComponentChrome = this.defaultSubComponentChrome;
        if (configElement.defaultSubComponentChrome != null)
        {
            combinedElement.defaultSubComponentChrome = configElement.defaultSubComponentChrome;
        }
        combinedElement.defaultTheme = this.defaultTheme;
        if (configElement.defaultTheme != null)
        {
            combinedElement.defaultTheme = configElement.defaultTheme;
        }

        combinedElement.surfBug = this.surfBug;
        if (configElement.surfBug != null)
        {
            combinedElement.surfBug = configElement.surfBug;
        }
        
        combinedElement.surfBug = this.surfBug;
        if (configElement.surfBug != null)
        {
            combinedElement.surfBug = configElement.surfBug;
        }
        
        combinedElement.defaultSiteConfiguration = this.defaultSiteConfiguration;
        if (configElement.defaultSiteConfiguration != null)
        {
            combinedElement.defaultSiteConfiguration = configElement.defaultSiteConfiguration;
        }
        
        
        // default persister setting
        combinedElement.defaultPersisterId = this.defaultPersisterId;
        if (configElement.defaultPersisterId != null)
        {
            combinedElement.defaultPersisterId = configElement.defaultPersisterId;
        }
        
        // Whenever two config elements are merged it is important to ensure that the
        // currently configured types are updated with any overridden default persisters.
        // A type is marked as using the default persister if a persister id is not 
        // explicitly set. It will be set with the default persister configured in the
        // same file as the type is declared, but the "useDefaultPersister" flag will 
        // be set so that it can be overridden if the default persister is updated.
        for (TypeDescriptor type: combinedElement.getTypes())
        {
            if (type.useDefaultPerister())
            {
                type.setPersisterId(combinedElement.defaultPersisterId);
            }
        }
        
        // AutoWire Settings
        combinedElement.autowireModeId = this.autowireModeId;
        if (configElement.autowireModeId != null)
        {
            combinedElement.autowireModeId = configElement.autowireModeId;
        }
        combinedElement.autowireRuntimeId = this.autowireRuntimeId;
        if (configElement.autowireRuntimeId != null)
        {
            combinedElement.autowireRuntimeId = configElement.autowireRuntimeId;
        }
        
        // persister config
        combinedElement.persisterConfigDescriptor = this.persisterConfigDescriptor;
        if (configElement.persisterConfigDescriptor != null)
        {
            combinedElement.persisterConfigDescriptor = configElement.persisterConfigDescriptor;
        }
        
        // Module deployment settings...
        combinedElement.moduleDeploymentMode = this.moduleDeploymentMode;
        if (configElement.moduleDeploymentMode != null)
        {
            combinedElement.moduleDeploymentMode = configElement.moduleDeploymentMode;
        }
        
        combinedElement.enableAutoDeployModules = this.enableAutoDeployModules;
        if (configElement.enableAutoDeployModules != null)
        {
            combinedElement.enableAutoDeployModules = configElement.enableAutoDeployModules;
        }
        
        combinedElement.useChecksumDependencies = this.useChecksumDependencies;
        if (configElement.useChecksumDependencies != null)
        {
            combinedElement.useChecksumDependencies = configElement.useChecksumDependencies;
        }
        
        combinedElement.generateCssDataImages = this.generateCssDataImages;
        if (configElement.generateCssDataImages != null)
        {
            combinedElement.generateCssDataImages = configElement.generateCssDataImages;
        }
        
        combinedElement.aggregateDependencies = this.aggregateDependencies;
        if (configElement.aggregateDependencies != null)
        {
            combinedElement.aggregateDependencies = configElement.aggregateDependencies;
        }
        
        combinedElement.calculateWebScriptDependencies = this.calculateWebScriptDependencies;
        if (configElement.calculateWebScriptDependencies != null)
        {
            combinedElement.calculateWebScriptDependencies = configElement.calculateWebScriptDependencies;
        }
        
        combinedElement.enableRemoteResourceHandling = this.enableRemoteResourceHandling;
        if (configElement.enableRemoteResourceHandling != null)
        {
            combinedElement.enableRemoteResourceHandling = configElement.enableRemoteResourceHandling;
        }
        
        combinedElement.enableExtensionModulesOnGuestPages = this.enableExtensionModulesOnGuestPages;
        if (configElement.enableExtensionModulesOnGuestPages != null)
        {
            combinedElement.enableExtensionModulesOnGuestPages = configElement.enableExtensionModulesOnGuestPages;
        }
        
        combinedElement.enableDynamicExtensions = this.enableDynamicExtensions;
        if (configElement.enableDynamicExtensions != null)
        {
            combinedElement.enableDynamicExtensions = configElement.enableDynamicExtensions;
        }
        
        combinedElement.disableResourceCaching = this.disableResourceCaching;
        if (configElement.disableResourceCaching != null)
        {
            combinedElement.disableResourceCaching = configElement.disableResourceCaching;
        }
        
        // Combine any Dojo configurations...
        combineDojoConfiguration(configElement, combinedElement);
        
        return combinedElement;
    }

    public String[] getFormatIds()
    {
        return this.formats.keySet().toArray(new String[this.formats.size()]);
    }
    public FormatDescriptor getFormatDescriptor(String id)
    {
        return (FormatDescriptor) this.formats.get(id);
    }

    // error handlers
    public String[] getErrorHandlerIds()
    {
        return this.errorHandlers.keySet().toArray(new String[this.errorHandlers.size()]);
    }
    public ErrorHandlerDescriptor getErrorHandlerDescriptor(String id)
    {
        return (ErrorHandlerDescriptor) this.errorHandlers.get(id);
    }

    // system pages
    public String[] getSystemPageIds()
    {
        return this.systemPages.keySet().toArray(new String[this.systemPages.size()]);
    }
    public SystemPageDescriptor getSystemPageDescriptor(String id)
    {
        return (SystemPageDescriptor) this.systemPages.get(id);
    }

    // tag libraries
    public String[] getTagLibraryIds()
    {
        return this.tagLibraries.keySet().toArray(new String[this.tagLibraries.size()]);
    }
    public TagLibraryDescriptor getTagLibraryDescriptor(String id)
    {
        return (TagLibraryDescriptor) this.tagLibraries.get(id);
    }

    /**
     * <p>Returns all the <code>TypeDescriptor</code> instances configured for this <code>WebFrameworkConfigElement</code>
     * 
     * @return A <code>Collection</code> of <code>TypeDescriptor</code> instances.
     */
    public Collection<TypeDescriptor> getTypes()
    {
        return this.types.values();
    }
    
    // types (model files)
    public String[] getTypeIds()
    {
        return this.types.keySet().toArray(new String[this.types.size()]);
    }

    public TypeDescriptor getTypeDescriptor(String id)
    {
        return (TypeDescriptor) this.types.get(id);
    }
    
    // resource loaders
    public String[] getResourceLoaderIds()
    {
        return this.resourceLoaders.keySet().toArray(new String[this.resourceLoaders.size()]);
    }
    
    public ResourceLoaderDescriptor getResourceLoaderDescriptor(String id)
    {
        return (ResourceLoaderDescriptor) this.resourceLoaders.get(id);        
    }
    
    public List<Pattern> getResourcesDeniedPaths()
    {
        return resourcesDeniedPaths;
    }
    
    // resource resolvers
    public String[] getResourceResolverIds()
    {
        return this.resourceResolvers.keySet().toArray(new String[this.resourceResolvers.size()]);
    }
    
    public ResourceResolverDescriptor getResourceResolverDescriptor(String id)
    {
        return (ResourceResolverDescriptor) this.resourceResolvers.get(id);        
    }    

    // debug
    public boolean isTimerEnabled()
    {
        return this.isTimerEnabled;
    }

    // application defaults
    public String getDefaultRegionChrome()
    {
        return this.defaultRegionChrome;
    }

    public String getDefaultComponentChrome()
    {
        return this.defaultComponentChrome;
    }
    
    public String getDefaultSubComponentChrome()
    {
        return this.defaultSubComponentChrome;
    }

    public String[] getDefaultPageTypeIds()
    {
        return this.pageTypes.keySet().toArray(new String[this.pageTypes.size()]);
    }

    public String getDefaultPageTypeInstanceId(String id)
    {
        return (String) this.pageTypes.get(id);
    }

    public String getDefaultThemeId()
    {
        if (this.defaultTheme == null)
        {
            return "default";
        }
        return this.defaultTheme;
    }

    public String getDefaultSiteConfigurationId()
    {
        if (this.defaultSiteConfiguration == null)
        {
            return "default.site.configuration";
        }
        return this.defaultSiteConfiguration;
    }

    // framework defaults
    public String getDefaultFormatId()
    {
        if (this.defaultFormatId == null)
        {
            return "default";
        }
        return this.defaultFormatId;
    }

    public String getDefaultUserFactoryId()
    {
        if (this.defaultUserFactoryId == null)
        {
            return DEFAULT_WEBFRAMEWORK_USER_FACTORY_ID;
        }
        return this.defaultUserFactoryId;
    }
    
    // default persister setting
    public String getDefaultPersisterId()
    {
        return this.defaultPersisterId;
    }

    public boolean isAutowireModeDevelopment()
    {
        return MODE_DEVELOPMENT.equals(this.autowireModeId);
    }

    public boolean isAutowireModeProduction()
    {
        return MODE_PRODUCTION.equals(this.autowireModeId);
    }

    public boolean isAutowireModePreview()
    {
        return MODE_PREVIEW.equals(this.autowireModeId);
    }
    
    public String getAutowireModeId()
    {
        return this.autowireModeId;
    }

    public String getAutowireRuntimeId()
    {
        return this.autowireRuntimeId;
    }
    
    public boolean isPreviewEnabled()
    {
        return this.isAutowireModePreview();
    }
        
    public RuntimeConfigDescriptor getRuntimeConfigDescriptor(String id)
    {
        return (RuntimeConfigDescriptor) this.runtimeConfigs.get(id);
    }
    
    public PersisterConfigDescriptor getPersisterConfigDescriptor()
    {
        return this.persisterConfigDescriptor;
    }
    

    public String getModuleDeploymentMode()
    {
        return this.moduleDeploymentMode;
    }
    
    public boolean isModuleAutoDeployEnabled()
    {
        return (this.enableAutoDeployModules != null) ? this.enableAutoDeployModules.booleanValue() : Boolean.FALSE;
    }
    
    public boolean useChecksumDependencies()
    {
        return (this.useChecksumDependencies != null) ? this.useChecksumDependencies.booleanValue() : Boolean.FALSE;
    }
    
    public boolean isGenerateCssDataImagesEnabled()
    {
        return (this.generateCssDataImages != null) ? this.generateCssDataImages.booleanValue() : Boolean.FALSE;
    }
    
    public boolean isAggregateDependenciesEnabled()
    {
        return (this.aggregateDependencies != null) ? this.aggregateDependencies.booleanValue() : Boolean.FALSE;
    }
    
    public boolean isCalculateWebScriptDependenciesEnabled()
    {
        return (this.calculateWebScriptDependencies != null) ? this.calculateWebScriptDependencies.booleanValue() : Boolean.TRUE;
    }
    
    public boolean isRemoteResourceResolvingEnabled()
    {
        return (this.enableRemoteResourceHandling != null) ? this.enableRemoteResourceHandling.booleanValue() : Boolean.FALSE; 
    }
    
    public boolean isGuestPageExtensionModulesEnabled()
    {
        return (this.enableExtensionModulesOnGuestPages != null) ? this.enableExtensionModulesOnGuestPages.booleanValue() : Boolean.TRUE; 
    }
    
    public boolean isDynamicExtensionModulesEnabled()
    {
        return (this.enableDynamicExtensions != null) ? this.enableDynamicExtensions.booleanValue() : Boolean.FALSE; 
    }
    
    public boolean isResourceCachingDisabled()
    {
        return (this.disableResourceCaching != null) ? this.disableResourceCaching.booleanValue() : Boolean.FALSE; 
    }
    
    /**
     * Base for all Descriptor classes. Defines a basic get/put property bag
     * of descriptor info. Sub classes should provide typed and named getter/setters.
     */
    public static class Descriptor
    {
        private static final String ID = "id";

        HashMap<String, String> propertiesMap;
        HashMap<String, String> attributesMap;

        @SuppressWarnings("unchecked")
        Descriptor(Element el)
        {
            // copy in sub-properties (child nodes)
            List<Element> elements = el.elements();
            for(Element element: elements)
            {
                put(element);
            }
            
            // copy in attributes
            for (int i = 0; i < el.attributeCount(); i++)
            {
                if (this.attributesMap == null)
                {
                    this.attributesMap = new HashMap<String, String>();
                }
                
                Attribute attribute = (Attribute) el.attribute(i);
                
                String value = attribute.getValue();
                if (value != null)
                {
                    this.attributesMap.put(attribute.getName(), value);
                }
            }
        }

        public void put(Element el)
        {
            if (this.propertiesMap == null)
            {
                this.propertiesMap = new HashMap<String, String>();
            }

            String key = el.getName();
            String value = (String) el.getTextTrim();
            if (value != null)
            {
                this.propertiesMap.put(key, value);
            }
        }

        public Object get(String key)
        {
            if (this.propertiesMap == null)
            {
                this.propertiesMap = new HashMap<String, String>();
            }

            return (Object) this.propertiesMap.get(key);
        }

        public String getId()
        {
            return (String) get(ID);
        }

        public Object getProperty(String key)
        {
            return get(key);
        }

        public String getStringProperty(String key)
        {
            return (String) get(key);
        }

        public Map<String, String> map()
        {
            return this.propertiesMap;
        }

        public Object getAttribute(String key)
        {
            return this.attributesMap.get(key);
        }

        public String getStringAttribute(String key)
        {
            return (String) getAttribute(key);
        }

        public Map<String, String> attributes()
        {
            return this.attributesMap;
        }        
    }

    public static class FormatDescriptor extends Descriptor
    {
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";

        FormatDescriptor(Element el)
        {
            super(el);
        }

        public String getName()
        {
            return getStringProperty(NAME);
        }
        public String getDescription()
        {
            return getStringProperty(DESCRIPTION);
        }
    }

    public static class ErrorHandlerDescriptor extends Descriptor
    {
        private static final String PROCESSOR_ID = "processor-id";
        private static final String JSP_PATH = "jsp-path";

        ErrorHandlerDescriptor(Element el)
        {
            super(el);
        }

        public String getJspPath()
        {
            return getStringProperty(JSP_PATH);
        }
        public String getProcessorId()
        {
            return getStringProperty(PROCESSOR_ID);
        }
    }

    public static class SystemPageDescriptor extends Descriptor
    {
        private static final String PROCESSOR_ID = "processor-id";
        private static final String JSP_PATH = "jsp-path";

        SystemPageDescriptor(Element el)
        {
            super(el);
        }

        public String getJspPath()
        {
            return getStringProperty(JSP_PATH);
        }
        public String getProcessorId()
        {
            return getStringProperty(PROCESSOR_ID);
        }
    }

    public static class TagLibraryDescriptor extends Descriptor
    {
        private static final String NAMESPACE = "namespace";
        private static final String URI = "uri";
        TagLibraryDescriptor(Element el)
        {
            super(el);
        }

        public String getUri()
        {
            return getStringProperty(URI);
        }

        public String getNamespace()
        {
            return getStringProperty(NAMESPACE);
        }
    }

    public static class TypeDescriptor extends Descriptor
    {
        private static final String CLAZZ = "class";
        private static final String NAMESPACE = "namespace";
        private static final String DESCRIPTION = "description";
        private static final String NAME = "name";
        private static final String VERSION = "version";
        private static final String PERSISTER_ID = "persister-id";
        private boolean _useDefaultPersister = true; 

        TypeDescriptor(Element el)
        {
            super(el);
            
            // Indicate the default persister should be used if a persister id
            // has not been explicitly configured. When multiple WebFrameworkConfigElements
            // are combined this value will be used to continuously set any overridden
            // default persister id until all WebFrameworkConfigElements have been combined.
            _useDefaultPersister = (getPersisterId() == null);
        }

        public String getImplementationClass()
        {
            return getStringProperty(CLAZZ);
        }
        public String getDescription()
        {
            return getStringProperty(DESCRIPTION);
        }
        public String getName()
        {
            return getStringProperty(NAME);
        }
        public String getNamespace()
        {
            return getStringProperty(NAMESPACE);
        }
        public String getVersion()
        {
            return getStringProperty(VERSION);
        }
        public String getPersisterId()
        {
            return getStringProperty(PERSISTER_ID);
        }

        // Setters which are included for autowire support
        
        public void setPersisterId(String persisterId)
        {
            this.propertiesMap.put(PERSISTER_ID, persisterId);
        }   
        
        public boolean useDefaultPerister()
        {
            return this._useDefaultPersister;
        }
    }
    
    public static class PersisterConfigDescriptor extends Descriptor
    {
        private static final String CACHE_ENABLED = "cache-enabled";
        private static final String CACHE_CHECK_DELAY = "cache-check-delay";

        PersisterConfigDescriptor(Element el)
        {
            super(el);
        }

        public Boolean getCacheEnabled()
        {
            Boolean enabled = null;
            String value = getStringProperty(CACHE_ENABLED);
            if (value != null && value.length() != 0)
            {
                enabled = Boolean.parseBoolean(value);
            }
            return enabled;
        }
        public Integer getCacheCheckDelay()
        {
            Integer value = null;
            String v =  getStringProperty(CACHE_CHECK_DELAY);
            if (v != null && v.length() != 0)
            {
                value = Integer.valueOf(v);
            }
            return value;
        }

        // Setters which are included for autowire support        
        
        public void setCacheEnabled(boolean cacheEnabled)
        {
            this.propertiesMap.put(CACHE_ENABLED, Boolean.toString(cacheEnabled));
        }
        
        public void setCacheCheckDelay(int cacheCheckDelay)
        {
            this.propertiesMap.put(CACHE_CHECK_DELAY, Integer.toString(cacheCheckDelay));
        }
    }
    
    
    public static class ResourceLoaderDescriptor extends Descriptor
    {
        private static final String ENDPOINT = "endpoint";
        private static final String TYPE = "type";

        ResourceLoaderDescriptor(Element el)
        {
            super(el);
        }

        public String getType() 
        {
            return getStringProperty(TYPE);
        }

        public String getEndpoint() 
        {
            return getStringProperty(ENDPOINT);
        }        
    }    
    
    public static class ResourceResolverDescriptor extends Descriptor
    {
        private static final String TYPE = "type";
        private static final String ENDPOINT = "endpoint";

        ResourceResolverDescriptor(Element el)
        {
            super(el);
        }

        public String getType() 
        {
            return getStringProperty(TYPE);
        }
        
        public String getEndpoint() 
        {
            return getStringProperty(ENDPOINT);
        }        
    }

    public static class RuntimeConfigDescriptor extends Descriptor
    {
        private static final String ENDPOINT_ID = "endpoint";
        private static final String STORE_ID = "store";
        private static final String WEBAPP_ID = "webapp";
        
        private List<String> persisterIds = new ArrayList<String>(4);

        @SuppressWarnings("unchecked")
        RuntimeConfigDescriptor(Element el)
        {
            super(el);
            
            // parse persister information
            Element persistersElement = el.element("persisters");
            if (persistersElement != null)
            {
                List<Element> persisterElements = persistersElement.elements("persister");
                for (Element persisterElement: persisterElements)
                {
                    String value = (String) persisterElement.getTextTrim();
                    persisterIds.add(value);
                }
            }
        }

        public String getEndpointId() 
        {
            return this.getStringProperty(ENDPOINT_ID);
        }
        
        public String getStoreId() 
        {
            return this.getStringProperty(STORE_ID);
        }
        
        public String getWebappId() 
        {
            return this.getStringProperty(WEBAPP_ID);
        }        
        
        public List<String> getPersisterIds()
        {
            return persisterIds;
        }
    }    
    
    @SuppressWarnings("unchecked")
    protected static WebFrameworkConfigElement newInstance(Element elem)
    {
        WebFrameworkConfigElement configElement = new WebFrameworkConfigElement();

        // formats
        List<Element> formats = elem.elements("format");
        for(Element el: formats)
        {
            FormatDescriptor descriptor = new FormatDescriptor(el);
            configElement.formats.put(descriptor.getId(), descriptor);
        }

        // error handlers
        List<Element> errorHandlers = elem.elements("error-handler");
        for(Element el: errorHandlers)
        {
            ErrorHandlerDescriptor descriptor = new ErrorHandlerDescriptor(el);
            configElement.errorHandlers.put(descriptor.getId(), descriptor);
        }

        // system pages
        List<Element> systemPages = elem.elements("system-page");
        for(Element el: systemPages)
        {
            SystemPageDescriptor descriptor = new SystemPageDescriptor(el);
            configElement.systemPages.put(descriptor.getId(), descriptor);
        }

        // tag libraries
        List<Element> tagLibraries = elem.elements("tag-library");
        for(Element el: tagLibraries)
        {
            TagLibraryDescriptor descriptor = new TagLibraryDescriptor(el);
            configElement.tagLibraries.put(descriptor.getId(), descriptor);
        }

        // defaults
        Element defaults = elem.element("defaults");
        if (defaults != null)
        {
            /** SERVICES **/
            String _requestContextFactoryId = defaults.elementTextTrim("request-context-factory");
            if (_requestContextFactoryId != null)
            {
                configElement.defaultRequestContextFactoryId = _requestContextFactoryId;
            }
            String _linkBuilderFactoryId = defaults.elementTextTrim("link-builder-factory");
            if (_linkBuilderFactoryId != null)
            {
                configElement.defaultLinkBuilderFactoryId = _linkBuilderFactoryId;
            }
            String _userFactoryId = defaults.elementTextTrim("user-factory");
            if (_userFactoryId != null)
            {
                configElement.defaultUserFactoryId = _userFactoryId;
            }
            
            
            /** SETTINGS **/
            String _format = defaults.elementTextTrim("format");
            if (_format != null)
            {
                configElement.defaultFormatId = _format;
            }
            String _regionChrome = defaults.elementTextTrim("region-chrome");
            if (_regionChrome != null)
            {
                configElement.defaultRegionChrome = _regionChrome;
            }
            String _componentChrome = defaults.elementTextTrim("component-chrome");
            if (_componentChrome != null)
            {
                configElement.defaultComponentChrome = _componentChrome;
            }
            
            String _subComponentChrome = defaults.elementTextTrim("sub-component-chrome");
            if (_subComponentChrome != null)
            {
                configElement.defaultSubComponentChrome = _subComponentChrome;
            }
            
            String _surfBug = defaults.elementTextTrim("surfbug");
            if (_surfBug != null)
            {
                configElement.surfBug = _surfBug;
            }
            
            String _theme = defaults.elementTextTrim("theme");
            if (_theme != null && _theme.length() != 0)
            {
                configElement.defaultTheme = _theme;
            }
            List<Element> pageTypes = defaults.elements("page-type");
            for(Element pageType: pageTypes)
            {
                String pageTypeId = pageType.elementTextTrim("id");
                String pageTypeInstanceId = pageType.elementTextTrim("page-instance-id");
                configElement.pageTypes.put(pageTypeId, pageTypeInstanceId);
            }
            String _siteConfiguration = defaults.elementTextTrim("site-configuration");
            if (_siteConfiguration != null)
            {
                configElement.defaultSiteConfiguration = _siteConfiguration;
            }
            
            
            /** DEFAULT PERSISTER SETTING **/
            String _defaultPersisterId = defaults.elementText("persister");
            if (_defaultPersisterId != null)
            {
                configElement.defaultPersisterId = _defaultPersisterId;
            }
            
        }

        //////////////////////////////////////////////////////
        // Debug Timer
        //////////////////////////////////////////////////////

        Element debugElement = elem.element("debug");
        if (debugElement != null)
        {
            String _isTimerEnabled = debugElement.elementTextTrim("timer");
            if (_isTimerEnabled != null)
            {
                configElement.isTimerEnabled = Boolean.parseBoolean(_isTimerEnabled);
            }
        }

        //////////////////////////////////////////////////////
        // Type Specific Things
        //////////////////////////////////////////////////////

        List<Element> objectTypes = elem.elements("object-type");
        for(Element el: objectTypes)
        {
            TypeDescriptor descriptor = new TypeDescriptor(el);
            if (descriptor.useDefaultPerister() && configElement.getDefaultPersisterId() != null)
            {
                descriptor.setPersisterId(configElement.getDefaultPersisterId());
            }
            
            configElement.types.put(descriptor.getId(), descriptor);
        }
        
        //////////////////////////////////////////////////////
        // Resource Loaders
        //////////////////////////////////////////////////////

        List<Element> loaders = elem.elements("resource-loader");
        for(Element el: loaders)
        {
            ResourceLoaderDescriptor descriptor = new ResourceLoaderDescriptor(el);
            configElement.resourceLoaders.put(descriptor.getId(), descriptor);
        }
        
        
        //////////////////////////////////////////////////////
        // Resource Resolvers
        //////////////////////////////////////////////////////

        List<Element> resolvers = elem.elements("resource-resolver");
        for(Element el: resolvers)
        {
            ResourceResolverDescriptor descriptor = new ResourceResolverDescriptor(el);
            configElement.resourceResolvers.put(descriptor.getId(), descriptor);
        }        
        


        //////////////////////////////////////////////////////
        // Runtime Configuration
        //////////////////////////////////////////////////////
        List<Element> runtimeConfigElements = elem.elements("runtime-config");
        for(Element el: runtimeConfigElements)
        {
            RuntimeConfigDescriptor descriptor = new RuntimeConfigDescriptor(el);
            configElement.runtimeConfigs.put(descriptor.getId(), descriptor);
        }
        
        //////////////////////////////////////////////////////
        // Autowire Configuration
        //////////////////////////////////////////////////////
        Element autowireConfigElement = elem.element("autowire");
        if (autowireConfigElement != null)
        {
            String _autowireModeId = autowireConfigElement.elementTextTrim("mode");
            if (_autowireModeId != null)
            {
                configElement.autowireModeId = _autowireModeId;
            }
            String _autowireRuntimeId = autowireConfigElement.elementTextTrim("runtime");
            if (_autowireRuntimeId != null)
            {
                configElement.autowireRuntimeId = _autowireRuntimeId;
            }
        }
        
        //////////////////////////////////////////////////////
        // Persister Config Descriptor
        //////////////////////////////////////////////////////
        Element persisterConfigElement = elem.element("persisters");
        if (persisterConfigElement != null)
        {
            configElement.persisterConfigDescriptor = new PersisterConfigDescriptor(persisterConfigElement);
        }        

        // Module Deployment mode...
        Element moduleDeploymentElement = elem.element("module-deployment");
        if (moduleDeploymentElement != null)
        {
            String _moduleDeploymentMode = moduleDeploymentElement.elementTextTrim("mode");
            if (_moduleDeploymentMode != null)
            {
                configElement.moduleDeploymentMode = _moduleDeploymentMode;
            }
            
            String _enableAutoDeployModules = moduleDeploymentElement.elementTextTrim("enable-auto-deploy-modules");
            if (_enableAutoDeployModules != null)
            {
                configElement.enableAutoDeployModules = Boolean.valueOf(_enableAutoDeployModules);
            }
        }
        
        // MNT-12724 case, externally specify paths that should be denied by ResourceController
        Element denyAccessPathsElement = elem.element("deny-access-resource-paths");
        if (denyAccessPathsElement != null)
        {
            List<Element> paths = denyAccessPathsElement.elements("resource-path-pattern");
            
            for (Element path : paths)
            {
                configElement.resourcesDeniedPaths.add(Pattern.compile(path.getTextTrim()));
            }
        }
        
        // When "use-checksum-dependencies" is set to true the JavaScriptDependencyDirective and
        // CssDependencyDirectives will be made available to FreeMarker templates...
        String useChecksumDependencies = elem.elementTextTrim("use-checksum-dependencies");
        if (useChecksumDependencies != null)
        {
            configElement.useChecksumDependencies = Boolean.valueOf(useChecksumDependencies);
        }
        
        String generateCssDataImages = elem.elementTextTrim("generate-css-data-images");
        if (generateCssDataImages != null)
        {
            configElement.generateCssDataImages = Boolean.valueOf(generateCssDataImages);
        }
        
        String aggregateDependencies = elem.elementTextTrim("aggregate-dependencies");
        if (aggregateDependencies != null)
        {
            configElement.aggregateDependencies = Boolean.valueOf(aggregateDependencies);
        }
        
        String calculateWebScriptDependencies = elem.elementTextTrim("calculate-webscript-dependencies");
        if (calculateWebScriptDependencies != null)
        {
            configElement.calculateWebScriptDependencies = Boolean.valueOf(calculateWebScriptDependencies);
        }
        
        String enableRemoteResources = elem.elementTextTrim("enable-remote-resource-resolving");
        if (enableRemoteResources != null)
        {
            configElement.enableRemoteResourceHandling = Boolean.valueOf(enableRemoteResources);
        }
        
        String enableGuestPageExtensionModules = elem.elementTextTrim("enable-guest-page-extension-modules");
        if (enableGuestPageExtensionModules != null)
        {
            configElement.enableExtensionModulesOnGuestPages = Boolean.valueOf(enableGuestPageExtensionModules);
        }
        
        String enableDynamicExtensionModules = elem.elementTextTrim("enable-dynamic-extension-modules");
        if (enableDynamicExtensionModules != null)
        {
            configElement.enableDynamicExtensions = Boolean.valueOf(enableDynamicExtensionModules);
        }
        
        String disableResourceCaching = elem.elementTextTrim("disable-resource-caching");
        if (disableResourceCaching != null)
        {
            configElement.disableResourceCaching = Boolean.valueOf(disableResourceCaching);
        }
        
        // Process any Dojo configuration...
        processDojoConfiguration(configElement, elem);
        
        return configElement;
    }
    
    /*
     * STATICS FOR OBTAINING DOJO CONFIG
     */
    public static final String DOJO_CONFIG = "dojo-pages";
    public static final String DOJO_ENABLED = "enabled";
    public static final String DOJO_LOADER_TRACE_ENABLED = "loader-trace-enabled";
    public static final String DOJO_BOOTSTRAP_FILE = "bootstrap-file";
    public static final String DOJO_PAGE_WIDGETS = "page-widget";
    public static final String DOJO_BASE_URL = "base-url";
    public static final String DOJO_PACKAGES = "packages";
    public static final String DOJO_PACKAGE = "package";
    public static final String DOJO_PACKAGE_NAME = "name";
    public static final String DOJO_PACKAGE_LOCATION = "location";
    public static final String DOJO_PACKAGE_MAIN = "main";
    public static final String DOJO_MESSAGES_OBJECT = "messages-object";
    public static final String DOJO_MESSAGES_DEFAULT_SCOPE = "default-messages-scope";
    public static final String DOJO_DEFAULT_LESS_CONFIG = "default-less-configuration";
    
    /*
     * DOJO CONFIGURATION VALUES
     */
    protected Boolean dojoEnabled = null;
    protected Boolean dojoLoaderTraceEnabled = null;
    protected String  dojoBootstrapFile = null;
    protected String  dojoPageWidget = null;
    protected String  dojoBaseUrl = null;
    protected String  dojoMessagesObject = null;
    protected String  dojoMessagesDefaultScope = null;
    protected String  dojoDefaultLessConfig = null;
    protected Map<String, String> dojoPackages = new HashMap<String, String>();
    protected Map<String, String> dojoPackagesMain = new HashMap<String, String>();
    
    /*
     * ACCESSORS FOR THE DOJO CONFIGURATION
     */
    public boolean isDojoEnabled()
    {
        return this.dojoEnabled != null ? this.dojoEnabled : false;
    }
    
    public boolean isDojoLoaderTraceEnabled()
    {
        return this.dojoLoaderTraceEnabled != null ? this.dojoLoaderTraceEnabled : false;
    }
    
    public String getDojoBootstrapFile()
    {
        return this.dojoBootstrapFile;
    }
    public String getDojoPageWidget()
    {
        return dojoPageWidget;
    }

    public String getDojoBaseUrl()
    {
        return dojoBaseUrl;
    }

    public Map<String, String> getDojoPackages()
    {
        return dojoPackages;
    }
    
    public Map<String, String> getDojoPackagesMain()
    {
        return dojoPackagesMain;
    }

    public String getDojoMessagesObject()
    {
        return dojoMessagesObject;
    }

    public String getDojoMessagesDefaultScope()
    {
        return dojoMessagesDefaultScope;
    }
    
    public String getDojoDefaultLessConfig()
    {
        return dojoDefaultLessConfig;
    }

    /**
     * Processes the Dojo configuration from the supplied {@link Element}
     * @param configElement
     * @param elem
     */
    public static void processDojoConfiguration(WebFrameworkConfigElement configElement, Element elem)
    {
        Element dojoConfig = elem.element(DOJO_CONFIG);
        if (dojoConfig != null)
        {
            String dojoEnabled = dojoConfig.elementTextTrim(DOJO_ENABLED);
            if (dojoEnabled != null)
            {
                configElement.dojoEnabled = Boolean.valueOf(dojoEnabled);
            }
            String loaderTraceEnabled = dojoConfig.elementTextTrim(DOJO_LOADER_TRACE_ENABLED);
            if (loaderTraceEnabled != null)
            {
                configElement.dojoLoaderTraceEnabled = Boolean.valueOf(loaderTraceEnabled);
            }
            String bootstrapFile = dojoConfig.elementTextTrim(DOJO_BOOTSTRAP_FILE);
            if (bootstrapFile != null)
            {
                configElement.dojoBootstrapFile = bootstrapFile;
            }
            String pageWidget = dojoConfig.elementTextTrim(DOJO_PAGE_WIDGETS);
            if (pageWidget != null)
            {
                configElement.dojoPageWidget = pageWidget;
            }
            String baseUrl = dojoConfig.elementTextTrim(DOJO_BASE_URL);
            if (baseUrl != null)
            {
                configElement.dojoBaseUrl = baseUrl;
            }
            String messagesObject = dojoConfig.elementTextTrim(DOJO_MESSAGES_OBJECT);
            if (messagesObject != null)
            {
                configElement.dojoMessagesObject = messagesObject;
            }
            String messagesDefaultScope = dojoConfig.elementTextTrim(DOJO_MESSAGES_DEFAULT_SCOPE);
            if (messagesDefaultScope != null)
            {
                configElement.dojoMessagesDefaultScope = messagesDefaultScope;
            }
            String defaultLessConfig = dojoConfig.elementTextTrim(DOJO_DEFAULT_LESS_CONFIG);
            if (defaultLessConfig != null)
            {
                configElement.dojoDefaultLessConfig = defaultLessConfig;
            }
            Element packages = dojoConfig.element(DOJO_PACKAGES);
            if (packages != null)
            {
                @SuppressWarnings("unchecked")
                List<Element> packageList = packages.elements(DOJO_PACKAGE);
                if (packageList != null)
                {
                    for (Element packageEntry: packageList)
                    {
                        String name = packageEntry.attributeValue(DOJO_PACKAGE_NAME);
                        String location = packageEntry.attributeValue(DOJO_PACKAGE_LOCATION);
                        if (name != null && location != null)
                        {
                            configElement.dojoPackages.put(name, location);
                            String main = packageEntry.attributeValue(DOJO_PACKAGE_MAIN);
                            if (main != null)
                            {
                                configElement.dojoPackagesMain.put(name, main);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Combines Dojo configuration from the configElement into the combinedElement.
     * @param configElement
     * @param combinedElement
     */
    public void combineDojoConfiguration(WebFrameworkConfigElement configElement, 
                                         WebFrameworkConfigElement combinedElement)
    {
        combinedElement.dojoEnabled = this.dojoEnabled;
        if (configElement.dojoEnabled != null)
        {
            combinedElement.dojoEnabled = configElement.dojoEnabled;
        }
        combinedElement.dojoLoaderTraceEnabled = this.dojoLoaderTraceEnabled;
        if (configElement.dojoLoaderTraceEnabled != null)
        {
            combinedElement.dojoLoaderTraceEnabled = configElement.dojoLoaderTraceEnabled;
        }
        combinedElement.dojoBootstrapFile = this.dojoBootstrapFile;
        if (configElement.dojoBootstrapFile != null)
        {
            combinedElement.dojoBootstrapFile = configElement.dojoBootstrapFile;
        }
        combinedElement.dojoPageWidget = this.dojoPageWidget;
        if (configElement.dojoPageWidget != null)
        {
            combinedElement.dojoPageWidget = configElement.dojoPageWidget;
        }
        combinedElement.dojoBaseUrl = this.dojoBaseUrl;
        if (configElement.dojoBaseUrl != null)
        {
            combinedElement.dojoBaseUrl = configElement.dojoBaseUrl;
        }
        combinedElement.dojoMessagesObject = this.dojoMessagesObject;
        if (configElement.dojoMessagesObject != null)
        {
            combinedElement.dojoMessagesObject = configElement.dojoMessagesObject;
        }
        combinedElement.dojoMessagesDefaultScope = this.dojoMessagesDefaultScope;
        if (configElement.dojoMessagesDefaultScope != null)
        {
            combinedElement.dojoMessagesDefaultScope = configElement.dojoMessagesDefaultScope;
        }
        combinedElement.dojoDefaultLessConfig = this.dojoDefaultLessConfig;
        if (configElement.dojoDefaultLessConfig != null)
        {
            combinedElement.dojoDefaultLessConfig = configElement.dojoDefaultLessConfig;
        }
        combinedElement.dojoPackages = this.dojoPackages;
        if (configElement.dojoPackages != null)
        {
            combinedElement.dojoPackages.putAll(configElement.dojoPackages);
        }
        
        combinedElement.dojoPackagesMain = this.dojoPackagesMain;
        if (configElement.dojoPackagesMain != null)
        {
            combinedElement.dojoPackagesMain.putAll(configElement.dojoPackagesMain);
        }
    }
}
