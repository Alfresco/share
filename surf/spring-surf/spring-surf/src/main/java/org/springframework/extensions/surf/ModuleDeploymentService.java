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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.dom4j.DocumentException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigProperties;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Extension;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.ModuleDeployment;
import org.springframework.extensions.webscripts.Registry;

public class ModuleDeploymentService
{
    private static final Log logger = LogFactory.getLog(ModuleDeploymentService.class);
    
    /**
     * <p>A {@link WebFrameworkConfigElement} is required to determine module deployment mode. If the application is configured
     * to auto deploy modules then this service will automatically deploy all modules that it finds. The downside of this approach
     * is that it does not allow for manual overrides or specifying a module processing order.</p>
     */
    private WebFrameworkConfigElement webFrameworkConfiguration;
    
    /**
     * <p>This method is required in order for the Spring application context to set the {@link WebFrameworkConfigElement} required to 
     * detect the module deployment mode.</p>
     * 
     * @param webFrameworkConfiguration WebFrameworkConfigElement
     */
    public void setWebFrameworkConfiguration(WebFrameworkConfigElement webFrameworkConfiguration)
    {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
    }
    
    private ModelObjectService modelObjectService;
    
    public ModelObjectService getModelObjectService()
    {
        return modelObjectService;
    }

    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }
    
    private Registry webScriptRegistry = null;
    
    public void setWebScriptRegistry(Registry webScriptRegistry)
    {
        this.webScriptRegistry = webScriptRegistry;
    }

    /**
     * <p>Keeps track of the last time the module deployments were updated. This is kept so that the
     * module handlers can check that cached module deployment data is not stale.</p>
     */
    private Date lastConfigurationUpdate = new Date();
    
    public synchronized Date getLastConfigurationUpdate()
    {
        return lastConfigurationUpdate;
    }

    /**
     * <p>Keeps track of errors on the current thread. The use of {@link ThreadLocal} ensures that
     * the correct messages are shown for the user.</p>
     */
    private ThreadLocal<List<String>> currThreadErrors = new ThreadLocal<List<String>>();
    
    /**
     * <p>Returns the current errors for the current thread. This typically is used to report any
     * save errors.</p>
     * @return List<String>
     */
    public List<String> getCurrentThreadErrors()
    {
        List<String> currErrors = currThreadErrors.get();
        if (currErrors == null)
        {
            currErrors = new ArrayList<String>();
        }
        return currErrors;
    }
    
    public void clearCurrentThreadErrors()
    {
        currThreadErrors.get().clear();
    }
    
    private Map<String, ExtensionModule> configuredModules = null;
    private List<ExtensionModule> undeployedModules = null;
    private List<ModuleDeployment> deployedModules = null;
    
    // An ID to use for the default the persisted extension when no specific id is provided.
    public final static String DEFAULT_PERSISTED_EXTENSION = "default-persisted-extension";
    
    /**
     * <p>Retrieves the remotely persisted {@link Extension} instance with the id specified by the <code>DEFAULT_PERSISTED_EXTENSION</code>
     * constant. If the {@link Extension} cannot be found then it is created and saved. </p>
     * @return Extension
     * @throws ModelObjectPersisterException
     */
    public synchronized Extension getPersistedExtension() throws ModelObjectPersisterException
    {
        Extension extension = (Extension) this.modelObjectService.getObject(Extension.TYPE_ID, DEFAULT_PERSISTED_EXTENSION);
        if (extension == null)
        {
            extension = (Extension) this.modelObjectService.newObject(Extension.TYPE_ID, DEFAULT_PERSISTED_EXTENSION);
            this.modelObjectService.saveObject(extension);
        }
        return extension;
    }
    
    /**
     * <p>Adds a module to the requested extension. This extension is remotely persisted and can have modules added to and
     * removed from it without restarting the server.</p>
     * @param xmlFragment String
     * 
     * @throws ModelObjectPersisterException 
     * @throws DocumentException 
     */
    public synchronized boolean addModuleToExtension(String xmlFragment) throws DocumentException, ModelObjectPersisterException
    {
        boolean result = false;
        if (this.webFrameworkConfiguration.isDynamicExtensionModulesEnabled())
        {
            Extension persistedExtension = getPersistedExtension();
            ExtensionModule module = persistedExtension.addExtensionModule(xmlFragment);
            if (module != null)
            {
                this.modelObjectService.saveObject(persistedExtension);
                this.configuredModules.put(module.getId(), module);
                String moduleDeploymentMode = this.webFrameworkConfiguration.getModuleDeploymentMode();
                if (moduleDeploymentMode == null || moduleDeploymentMode.equals(WebFrameworkConfigProperties.AUTO_MODULE_DEPLOYMENT))
                {
                    // If we're in auto-deploy mode then we should deploy any previously unknown modules...
                    this.deployedModules.add(deployModule(module, this.deployedModules.size() + 1, null, null));
                }
                else if (this.webFrameworkConfiguration.isModuleAutoDeployEnabled() && module.isAutoDeploy())
                {
                    // If we're in enable module auto-deploy then we should deploy any modules that ask to be deployed...
                    this.deployedModules.add(deployModule(module, this.deployedModules.size() + 1, null, null));
                }
                else
                {
                    // If we're in manual deployment mode then just add to the undeployed list...
                    this.undeployedModules.add(module);
                }
            }
            this.saveDeployedModuleConfigurations();
            result = (module != null);
        }
        else
        {
            if (logger.isErrorEnabled())
            {
                logger.error("A request was made to add an Extension Module but dynamic modules are disabled");
            }
        }
        return result;
    }
    
    /**
     * <p>Updates a module to the requested extension. This extension is remotely persisted and can have modules added to and
     * removed from it without restarting the server.</p>
     * @param xmlFragment String
     * 
     * @throws ModelObjectPersisterException 
     * @throws DocumentException 
     */
    public synchronized boolean updateModuleToExtension(String xmlFragment) throws DocumentException, ModelObjectPersisterException
    {
        boolean result = false;
        if (this.webFrameworkConfiguration.isDynamicExtensionModulesEnabled())
        {
            Extension persistedExtension = getPersistedExtension();
            ExtensionModule module = persistedExtension.updateExtensionModule(xmlFragment);
            if (module != null)
            {
                this.modelObjectService.saveObject(persistedExtension);
                
                // update configured modules reference
                this.configuredModules.put(module.getId(), module);
                
                // update deployed modules list
                ModuleDeployment deployedModule = null;
                for (ModuleDeployment depMod: this.deployedModules)
                {
                    if (depMod.getExtensionModuleId().equals(module.getId()))
                    {
                        deployedModule = depMod;
                        break;
                    }
                }
                if (deployedModule != null)
                {
                    this.deployedModules.remove(deployedModule);
                }
                String moduleDeploymentMode = this.webFrameworkConfiguration.getModuleDeploymentMode();
                if (moduleDeploymentMode == null || moduleDeploymentMode.equals(WebFrameworkConfigProperties.AUTO_MODULE_DEPLOYMENT))
                {
                    // If we're in auto-deploy mode then we should deploy any previously unknown modules...
                    this.deployedModules.add(deployModule(module, this.deployedModules.size() + 1, null, null));
                }
                else if (this.webFrameworkConfiguration.isModuleAutoDeployEnabled() && module.isAutoDeploy())
                {
                    // If we're in enable module auto-deploy then we should deploy any modules that ask to be deployed...
                    this.deployedModules.add(deployModule(module, this.deployedModules.size() + 1, null, null));
                }
                else
                {
                    // If we're in manual deployment mode then just add to the undeployed list...
                    this.undeployedModules.add(module);
                }
            }
            this.saveDeployedModuleConfigurations();
            result = (module != null);
        }
        else
        {
            if (logger.isErrorEnabled())
            {
                logger.error("A request was made to update an Extension Module but dynamic modules are disabled");
            }
        }
        return result;
    }
    
    /**
     * <p>Deletes the requested module from the extension with the supplied id.</p>
     * @param moduleId String
     * @throws ModelObjectPersisterException
     * @throws DocumentException
     */
    public synchronized boolean deleteModuleFromExtension(String moduleId) throws ModelObjectPersisterException, DocumentException
    {
        boolean result = false;
        if (this.webFrameworkConfiguration.isDynamicExtensionModulesEnabled())
        {
            Extension persistedExtension = getPersistedExtension();
            ExtensionModule deletedModule = persistedExtension.deleteExtensionModule(moduleId);
            if (deletedModule != null)
            {
                this.modelObjectService.saveObject(persistedExtension);
                this.configuredModules.remove(deletedModule.getId());
                
                // Check to see if the deleted module had been deployed...
                ModuleDeployment deployedModule = null;
                for (ModuleDeployment depMod: this.deployedModules)
                {
                    if (depMod.getExtensionModuleId().equals(deletedModule.getId()))
                    {
                        deployedModule = depMod;
                        break;
                    }
                }
                
                // ...and if it had then undeploy it...
                if (deployedModule != null)
                {
                    this.deployedModules.remove(deployedModule);
                    this.modelObjectService.removeObject(deployedModule);
                    this.saveDeployedModuleConfigurations();
                }
                else
                {
                    this.undeployedModules.remove(deletedModule);
                }
                result = (deletedModule != null);
            }
       }
       else
       {
           if (logger.isErrorEnabled())
           {
               logger.error("A request was made to delete an Extension Module but dynamic modules are disabled");
           }
       }
       return result;
    }
    
    /**
     * <p>Retrieves the list of configured by undeployed extension modules.</p>
     * 
     * @return List<ExtensionModule>
     */
    public synchronized List<ExtensionModule> getUndeployedModules()
    {
        if (undeployedModules == null)
        {
            getDeployedModules(); // Calling this method ensures that the undeployed list is populated.
        }
        return undeployedModules;
    }
    
    /**
     * <p>If the <code>configuredModules</code> instance variable has not yet been set then this will assign
     * it by calling the <code>getExtensionModules</code> method. This method is synchronized so that the
     * instance variable should only get set once the first time it is requested. The only way in which it
     * can be updated is through subsequent calls to the <code>resetConfiguredModuleList</code> method.</p>
     * @return Map
     */
    private synchronized Map<String, ExtensionModule> getAllConfiguredExtensionModules()
    {
        if (this.configuredModules == null)
        {
            this.configuredModules = getExtensionModules();
        }
        return this.configuredModules;
    }
    
    /**
     * <p>Finds all the {@link ExtensionModule} configurations and returns them as a list.</p> 
     * @return A {@link List} of all the configured {@link ExtensionModule} instances.
     */
    private Map<String, ExtensionModule> getExtensionModules()
    {
        Map<String, ExtensionModule> allModules = new HashMap<String, ExtensionModule>();
        
        // If we haven't already retrieved all the modules then get them now. Modules are stored inside extensions
        // so we need to retrieve all the extensions and obtain the modules configured within them...
        Map<String, ModelObject> allExtensions = this.modelObjectService.getAllObjects(Extension.TYPE_ID);
        for (ModelObject extension: allExtensions.values())
        {
            if (extension instanceof Extension)
            {
                for (ExtensionModule module: ((Extension) extension).getExtensionModules())
                {
                    // The module is not deployed so can be added to the list.
                    allModules.put(module.getId(), module);
                }
            }
        }
        
        try
        {
            if (this.webFrameworkConfiguration.isDynamicExtensionModulesEnabled())
            {
                // Get all the modules from the persisted extension...
                for (ExtensionModule module: getPersistedExtension().getExtensionModules())
                {
                    allModules.put(module.getId(), module);
                }
            }
        }
        catch (ModelObjectPersisterException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("The following error occurred attempting to retrieve persisted extension modules", e);
            }
        }
        return allModules;
    }
    
    /**
     * Inner {@link Comparator} class for sorting {@link ExtensionModule} instances based on their auto-deploy-index. This index
     * can be a Maven version and will be ordered appropriately.
     */
    private class AutoDeployIndexComparator implements Comparator<ExtensionModule>
    {
        /**
         * Parses the auto-deploy index of the supplied extension.
         * @param extMod ExtensionModule
         * @return DefaultArtifactVersion
         */
        private DefaultArtifactVersion parseAutoDeployIndex(ExtensionModule extMod)
        {
            DefaultArtifactVersion version = null;
            try
            {
               String versionString = extMod.getAutoDeployIndex();
               if (versionString != null)
               {
                   version = new DefaultArtifactVersion(versionString);
               }
            }
            catch(NumberFormatException e)
            {
                // No action required - leave as null
            }
            return version;
        }
        
        public int compare(ExtensionModule o1, ExtensionModule o2)
        {
            int r = 0;
            DefaultArtifactVersion v1 = this.parseAutoDeployIndex(o1);
            DefaultArtifactVersion v2 = this.parseAutoDeployIndex(o2);
            
            if (v1 == null && v2 == null)
            {
                // Neither module has an auto-deploy index so they can stay as they are...
                r = 0;
            }
            else if (v1 != null && v2 == null)
            {
                // If o1 has an auto-deploy index but o2 doesn't then it automatically goes in front
                r = -1;
            }
            else if (v1 == null && v2 != null)
            {
                // If o2 does NOT have an auto-deploy index but o2 DOES then it automatically goes after
                r = 1;
            }
            else
            {
                // If they both have indices then compare them
                r = v1.compareTo(v2);
            }
            return r;
        }
    }
    
    /**
     * <p>Returns the list of configured ModuleDeployments. This is the configured ordered list referencing
     * modules that contain configured overrides (rather than their defaults).</p>
     * 
     * @return List<ModuleDeployment>
     */
    public synchronized List<ModuleDeployment> getDeployedModules()
    {
        if (deployedModules == null)
        {
            getAllConfiguredExtensionModules(); // Make sure we've got all the configured extension modules...
            
            // If we haven't already retrieved the list of deployed modules from the model object service then do it now,
            // this list will then be maintained locally within the service.
            String moduleDeploymentMode = this.webFrameworkConfiguration.getModuleDeploymentMode();
            if (moduleDeploymentMode == null || moduleDeploymentMode.equals(WebFrameworkConfigProperties.AUTO_MODULE_DEPLOYMENT))
            {
                this.deployedModules = new ArrayList<ModuleDeployment>();
                
                // Sort the modules based on the auto-deploy-index (if the modules have them)...
                ArrayList<ExtensionModule> modsForSorting = new ArrayList<ExtensionModule>(this.configuredModules.values());
                Collections.sort(modsForSorting, new ModuleDeploymentService.AutoDeployIndexComparator());
                
                // If the application is configured to auto-deploy modules then we will get all the undeployed modules
                // and deploy them in whatever order they've been returned...
                int i = 0;
                for (ExtensionModule module: modsForSorting)
                {
                    this.deployedModules.add(deployModule(module, i++, null, null));
                }
                
                // Set an empty list of undeployed modules because we've just deployed them all...
                this.undeployedModules = new ArrayList<ExtensionModule>();
            }
            else
            {
                this.undeployedModules = new ArrayList<ExtensionModule>();
                this.undeployedModules.addAll(this.configuredModules.values());

                // The application is configured for manual module deployment so we will be build the list from what has been
                // previously configured at the time that the application was last stopped. If this is the first time that the
                // application has been started then most likely there will be no modules deployed.
                // NOTE: Only attempt to find deployed versions of the configured modules. This prevents invalid
                //       deployed module config from corrupting extension processing.
                deployedModules = new ArrayList<ModuleDeployment>();
                for (ExtensionModule configuredModule: this.configuredModules.values())
                {
                    ModelObject o = this.modelObjectService.getObject(ModuleDeployment.TYPE_ID, configuredModule.getId());
                    if (o instanceof ModuleDeployment)
                    {
                        ModuleDeployment modDep = (ModuleDeployment) o;
                        modDep.setExtensionModule(configuredModule);
                        deployedModules.add(modDep);
                        this.undeployedModules.remove(configuredModule);
                    }
                }
                
                Collections.sort(deployedModules, new ModuleDeploymentService.DeployedModuleComparator());
                
                // Set the index for adding auto-deployment modules...
                int i = 0;
                if (deployedModules.isEmpty())
                {
                    // No action required... leave as 0
                }
                else
                {
                    ModuleDeployment lastDeployedModule = deployedModules.get(deployedModules.size()-1);
                    i = lastDeployedModule.getIndex();
                }
                
                // If modules can automatically deploy themselves in manual mode then deploy any that have registered...
                // this is achieved through the use of the <auto-deploy> being set to "true" in the module definition....
                if (this.webFrameworkConfiguration.isModuleAutoDeployEnabled())
                {
                    // Keep track of any auto-deployed modules so that they can be removed from the undeployed list...
                    List<ExtensionModule> autoDeployed = new ArrayList<ExtensionModule>();
                    for (ExtensionModule module: this.undeployedModules)
                    {
                        if (module.isAutoDeploy())
                        {
                            autoDeployed.add(module);
                        }
                    }

                    // Sort the modules based on the auto-deploy-index (if the modules have them)...
                    Collections.sort(autoDeployed, new ModuleDeploymentService.AutoDeployIndexComparator());
                    for (ExtensionModule module: autoDeployed)
                    {
                        this.deployedModules.add(deployModule(module, ++i, null, null));
                    }
                    
                    // Remove any auto-deployed modules...
                    this.undeployedModules.removeAll(autoDeployed);
                }
            }
            
            // Save (this also sets the cached timestamp)...
            this.saveDeployedModuleConfigurations();
        }
        else
        {
            // Deployed module list has already been constructed no action required.
        }
        
        return this.deployedModules;
    }

    /**
     * Private {@link Comparator} for sorting deployed modules. 
     */
    private class DeployedModuleComparator implements Comparator<ModuleDeployment>
    {
        public int compare(ModuleDeployment o1, ModuleDeployment o2)
        {
            return (o1.getIndex() > o2.getIndex() ? 1 : (o1.getIndex() == o2.getIndex() ? 0 : -1));
        }
    }
    
    private synchronized void saveDeployedModuleConfigurations()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        if (rc != null && rc.getUser() != null && rc.getUser().isAdmin())
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Saving module deployment configuration...");
            }
            
            // Order the list of deployed objects based on their index...
            Collections.sort(deployedModules, new ModuleDeploymentService.DeployedModuleComparator());
            
            // Keep track of any errors...
            List<String> errors = new ArrayList<String>();
            
            boolean saveFail = false;
            
            // Now explicitly set the index based on the sorted order saving the object...
            for (int i = 0; i < deployedModules.size(); i++)
            {
                ModuleDeployment currentDeployedModule = deployedModules.get(i);
                currentDeployedModule.setIndex(i);
                
                try
                {
                    if (!this.modelObjectService.saveObject(currentDeployedModule))
                    {
                        saveFail = true;
                        errors.add("Could not save module: \"" +  currentDeployedModule.getId() +"\"");
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Could not save module deployment for: \"" + currentDeployedModule.getId() + "\"");
                        }
                    }
                }
                catch (ModelObjectPersisterException e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Could not save module deployment configuration", e);
                    }
                    errors.add("The following exception was thrown: " + e.getLocalizedMessage());
                }
            }
            
            if (saveFail)
            {
                errors.add("Could not save deployment configuration, please ensure you are authenticated. Changes will not survive server restart.");
            }
            
            this.lastConfigurationUpdate = new Date();
            this.currThreadErrors.set(errors);
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to saving module deployment configuration as user is not Admin.");
            }
        }
    }
    
    /**
     * <p>This method is provided with the purpose of servicing the WebScript API that controls module deployment.
     * It takes as an argument a String array of the modules that the admin has configured to deploy in the order
     * that they should be processed.</p>
     * 
     * @param modulesToDeploy List<JSONObject>
     * @throws JSONException 
     */
    @SuppressWarnings("rawtypes")
    public synchronized void setDeployedModules(List<JSONObject> modulesToDeploy) throws JSONException
    {
        getAllConfiguredExtensionModules(); // Make sure we've got the list of configured modules...
        getDeployedModules();
        getUndeployedModules();
        
        // Clear the current list of deployed modules...
        for (ModuleDeployment depMod: this.deployedModules)
        {
            String modIdToRemove = depMod.getExtensionModuleId();
            ExtensionModule modToRemove = this.configuredModules.get(modIdToRemove);
            if (!this.undeployedModules.contains(modToRemove))
            {
                this.undeployedModules.add(modToRemove);
            }
            this.modelObjectService.removeObject(depMod);
        }
        
        this.deployedModules.clear();
        
        // Process the new list...
        if (modulesToDeploy != null)
        {
            for (int i=0; i<modulesToDeploy.size(); i++)
            {
                // Convert the JSON representation of the module into data that we can use...
                JSONObject moduleToDeploy = modulesToDeploy.get(i);
                String moduleId = moduleToDeploy.getString("id");
                
                String evaluatorOverride = null;
                if (moduleToDeploy.has("evaluatorOverrideId"))
                {
                    evaluatorOverride = moduleToDeploy.getString("evaluatorOverrideId");
                }
                
                // Create a map of properties from the JSON object...
                HashMap<String, String> evalPropOverrides = null;
                if (moduleToDeploy.has("evaluatorPropertyOverrides"))
                {
                    JSONObject evalPropOverridesJSON = moduleToDeploy.getJSONObject("evaluatorPropertyOverrides");
                    evalPropOverrides = new HashMap<String, String>();
                    Iterator propKeys = evalPropOverridesJSON.keys();
                    while (propKeys.hasNext())
                    {
                        String propKey = (String) propKeys.next();
                        evalPropOverrides.put(propKey, (String) evalPropOverridesJSON.get(propKey));
                    }
                }
                
                ExtensionModule targetModule = null;
                Iterator<ExtensionModule> extModIter = this.configuredModules.values().iterator();
                while(targetModule == null && extModIter.hasNext())
                {
                    ExtensionModule currExtMod = extModIter.next();
                    if (currExtMod != null && currExtMod.getId() != null && currExtMod.getId().equals(moduleId))
                    {
                        targetModule = currExtMod;
                    }
                }
                
                if (targetModule != null) // We might not have found it
                {
                    
                    ModuleDeployment modDep = deployModule(targetModule, i, evaluatorOverride, evalPropOverrides);
                    if (modDep != null)
                    {
                        this.deployedModules.add(modDep);
                        this.undeployedModules.remove(targetModule);
                    }
                }
            }
        }
        
        // Reset the WebScript registry when module deployment is changed. This is necessary because WebScript messages
        // are cached and we need to ensure that when module deployment changes that cached WebScript resource bundles
        // do not contain stale data...
        if (this.webScriptRegistry != null)
        {
            this.webScriptRegistry.reset();
        }
        this.saveDeployedModuleConfigurations();
    }
    
    
    private synchronized ModuleDeployment deployModule(ExtensionModule moduleToDeploy,
                                                       int index,
                                                       String evaluatorOverride,
                                                       Map<String, String> evaluatorPropertyOverrides)
    {
        // 
        ModuleDeployment modDep = (ModuleDeployment) this.modelObjectService.newObject(ModuleDeployment.TYPE_ID, moduleToDeploy.getId());
        modDep.setIndex(index);
        modDep.setExtensionModuleId(moduleToDeploy.getId());
        modDep.setExtensionModule(moduleToDeploy);
        if (evaluatorOverride != null)
        {
            modDep.setEvaluatorOverride(evaluatorOverride);
        }
        if (evaluatorPropertyOverrides != null)
        {
            modDep.setEvaluatorPropertyOverrides(evaluatorPropertyOverrides);
        }
        return modDep;
    }
    
    /**
     * <p>Deletes the supplied {@link ModuleDeployment} and returns the associated {@link ExtensionModule} to the list of 
     * undeployed modules.</p>
     * 
     * @param moduleDeploymentToDelete ModuleDeployment
     * @return <code>true</code> if the {@link ModuleDeployment} was successfully deleted and <code>false</code> otherwise.
     */
    public synchronized boolean deleteModuleDeployment(ModuleDeployment moduleDeploymentToDelete)
    {
        boolean deleted = false;
        List<ExtensionModule> undeployedModules = getUndeployedModules();
        List<ModuleDeployment> deployedModules = getDeployedModules();
        if (deployedModules.contains(moduleDeploymentToDelete))
        {
            undeployedModules.add(this.configuredModules.get(moduleDeploymentToDelete.getExtensionModuleId()));
            this.modelObjectService.removeObject(moduleDeploymentToDelete);
            this.lastConfigurationUpdate = new Date();
            deleted = true;
        }
        return deleted;
    }
}
