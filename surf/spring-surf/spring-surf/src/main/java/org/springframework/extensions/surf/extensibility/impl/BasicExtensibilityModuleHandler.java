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
package org.springframework.extensions.surf.extensibility.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.ModuleDeploymentService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.Customization;
import org.springframework.extensions.surf.extensibility.ExtensibilityModuleHandler;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;
import org.springframework.extensions.surf.extensibility.WebScriptExtensibilityModuleHandler;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.ModuleDeployment;

/**
 * <p>This is the default {@link ExtensibilityModuleHandler} provided for Spring Surf and is configured as a Spring
 * bean.</p>
 * 
 * @author David Draper
 */
public class BasicExtensibilityModuleHandler extends WebScriptExtensibilityModuleHandler implements ExtensibilityModuleHandler, ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(BasicExtensibilityModuleHandler.class);
    
    private ModuleDeploymentService moduleDeploymentService;
    
    public void setModuleDeploymentService(ModuleDeploymentService moduleDeploymentService)
    {
        this.moduleDeploymentService = moduleDeploymentService;
    }

    /**
     * <p>The <code>ModelObjectService</code> should be set through Spring configuration. It is used to retrieve the Extension
     * objects that have been defined in Spring Surf configuration files.</p>
     */
    private ModelObjectService modelObjectService;
    
    public ModelObjectService getModelObjectService()
    {
        return modelObjectService;
    }

    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }

    private ExtensionModuleEvaluator defaultModuleEvaluator = null;
    
    public void setDefaultModuleEvaluator(ExtensionModuleEvaluator defaultModuleEvaluator)
    {
        this.defaultModuleEvaluator = defaultModuleEvaluator;
    }

    private ApplicationContext applicationContext = null;
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * <p>Returns a list of template paths that <b>could</b> provide valid extensions to the supplied
     * template. These paths are derived from the module names defined in all the extension configurations
     * for the application. There is no guarantee that these template paths will map to a file so there
     * existence should be validated by the template processor before attempted to process them.</p>
     * @param path The path of the template being processed to look for extensions for.
     */
    public List<String> getExtendingModuleFiles(ExtensionModule module, String path)
    {
        List<String> customizationPaths = super.getExtendingModuleFiles(module, path);
        return customizationPaths;
    }
    
    /**
     * 
     * @param module
     * @param path
     * @return
     */
    public LinkedHashSet<String> getModuleJsDeps(ExtensionModule module, String path)
    {
        LinkedHashSet<String> jsDeps = new LinkedHashSet<String>();
        for (Customization customization: module.getCustomizations())
        {
            // Check that the path falls within the target package (this is so that we only
            // apply dependencies if the path matches the target defined in the customization)...
            if (customization.getTargetPackageName() != null)
            {
                String targetPackage = customization.getTargetPackageName().replace(".", "/");
                if (path.startsWith(targetPackage))
                {
                    jsDeps.addAll(customization.getJsDependencies());
                }
            }
        }
        return jsDeps;
    }
    
    /**
     * 
     * @param module
     * @param path
     * @return
     */
    public Map<String, LinkedHashSet<String>> getModuleCssDeps(ExtensionModule module, String path)
    {
        Map<String, LinkedHashSet<String>> cssDeps = new HashMap<String, LinkedHashSet<String>>();
        for (Customization customization: module.getCustomizations())
        {
            // Check that the path falls within the target package (this is so that we only
            // apply dependencies if the path matches the target defined in the customization)...
            if (customization.getTargetPackageName() != null)
            {
                String targetPackage = customization.getTargetPackageName().replace(".", "/");
                if (path.startsWith(targetPackage))
                {
                    for (Entry<String, List<String>> entry: customization.getCssDependencies().entrySet())
                    {
                        // Get the list of dependencies specific to the requested media type...
                        LinkedHashSet<String> mediaSpecificDependencies = cssDeps.get(entry.getKey());
                        if (mediaSpecificDependencies == null)
                        {
                            // If no other dependencies for the requested media have not been added yet
                            // then create a new list to hold them and add it to the map...
                            mediaSpecificDependencies = new LinkedHashSet<String>();
                            cssDeps.put(entry.getKey(), mediaSpecificDependencies);
                        }
                        
                        // Add the dependency to the list...
                        mediaSpecificDependencies.addAll(entry.getValue());
                    }
                }
            }
        }
        return cssDeps;
    }
    
    /**
     * <p>Retrieves a list of modules that can be applied to the file defined by the supplied path. The path
     * supplied could be to a FreeMarker template, JavaScript controller or NLS properties file. Modules will
     * only be added to the list returned if they meet target and evaluation criteria.</p>
     * @param context The current {@link RequestContext}
     * @param basePath The path of the file being processed.
     * 
     * @return A list of {@link ExtensionModule} instances that are applicable to the file being processed.
     */
    public List<ExtensionModule> evaluateModules(RequestContext context)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Evaluating modules for request: " + context.getUri());
        }
        
        List<ExtensionModule> modules = new ArrayList<ExtensionModule>();

        // Get all the Extensions that have been configured for the application and construct module template
        // based on the information configured for each one. It doesn't matter if the templatePath does not
        // actually exist because the template processor should validate each path - this only provides a 
        // "suggestion" for which paths to try...
        if (this.moduleDeploymentService != null)
        {
            for (ModuleDeployment module: this.moduleDeploymentService.getDeployedModules())
            {
                if (logger.isDebugEnabled())
                {
                   logger.debug("Evaluating module: " + module.getId());
                }
                if (applyModule(module, context))
                {
                    modules.add(module.getExtensionModule());
                }
            }
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("No module deployment service has been configured in the application context, applicable modules cannot be discovered");
            }
        }
        return modules;
    }
    
    /**
     * <p>Determines whether or not to apply the supplied {@link ModuleDeployment} based on the supplied {@link RequestContext}.</p>
     * @param module The {@link ModuleDeployment} to test
     * @param context The current {@link RequestContext}
     * @return <code>true</code> if the module should be applied and <code>false</code> otherwise.
     */
    protected boolean applyModule(ModuleDeployment module, 
                                  RequestContext context)
    {
        boolean apply = false;
        
        // Get the evaluator for the the module and check that it applies to the current request...
        ExtensionModuleEvaluator moduleEvaluator = null;
        if (module.getEvaluator() != null)
        {
            try
            {
                moduleEvaluator = this.applicationContext.getBean(module.getEvaluator(), ExtensionModuleEvaluator.class);
            }
            catch (NoSuchBeanDefinitionException e)
            {
                // No action required.
                if (logger.isErrorEnabled())
                {
                    logger.error("The following exception occurred retrieving evaluator: " + module.getEvaluator(), e);
                }
            }
        }
        else if (this.defaultModuleEvaluator != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Using default module evaluator on module '" + module.getId() + "'");
            }
            moduleEvaluator = defaultModuleEvaluator;
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("No evaluator defined for module: '" + (module != null ? module.getId() : null) + "' and no default configured - module will not be applied");
            }
        }
            
        if (moduleEvaluator != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Evaluating module : '" + (module != null ? module.getId() : null) + "' using evaluator '" + moduleEvaluator.getClass() + "'");
            }
            apply = moduleEvaluator.applyModule(context, module.getEvaluatorProperties());
        }
        else
        {
            // No evaluator provided.
        }
        return apply;
    }
}
