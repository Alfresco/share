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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.ModuleDeploymentService;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.ModuleDeployment;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * <p>This is the Java controller for the Module Deployment WebScript (/modules/deploy) that returns the status of the module
 * deployment configuration. The corrent configuration is retrieved from the configured {@iink ModuleDeploymentService} and
 * converted into "stringified" JSON and added to the model for rendering by the template.</p>
 * 
 * @author David Draper
 */
public class GetDeployedModules extends DeclarativeWebScript implements ApplicationContextAware
{
    /**
     * <p>The Spring {@link ApplicationContext} is required in order to retrieve the list of configured {@link ExtensionModuleEvaluator}
     * instances. These are added to the model to allow the user to specify an evaluator override</p>
     */
    private ApplicationContext applicationContext;
    
    /**
     * <p>Provided to satisfy the {@link ApplicationContextAware} interface and allows Spring to provide the {@link ApplicationContext}
     * to this bean.</p>
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * <p> A @link ModuleDeploymentService} is required for retrieving module deployment configuration. This instance
     * variable should be set via the Spring application context.</p> 
     */
    private ModuleDeploymentService moduleDeploymentService;
    
    /**
     * <p>Provided so that the Spring application context can set the {@link ModuleDeploymentService} to use. It is essential that
     * the Spring bean configuration has been set correctly otherwise this controller will not achieve anything.</p>
     * 
     * @param moduleDeploymentService
     */
    public void setModuleDeploymentService(ModuleDeploymentService moduleDeploymentService)
    {
        this.moduleDeploymentService = moduleDeploymentService;
    }

    /**
     * <p>Builds a model containing lists of undeployed and deployed modules as {@link JSONObject} instances along with
     * a list of the configured {@link ExtensionModuleEvaluator} instances and a timestamp of the last server update.</p>
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        if (this.moduleDeploymentService != null)
        {
            List<String> deployedModules = new ArrayList<String>();
            List<String> undeployedModules = new ArrayList<String>();
            for (ModuleDeployment deployedModule: this.moduleDeploymentService.getDeployedModules())
            {
                try
                {
                    deployedModules.add(createModuleDefinition(deployedModule).toString());
                }
                catch (JSONException e)
                {
                    // TODO: *** EXT *** Handle JSON error creating module definition
                    e.printStackTrace();
                }
            }
            for (ExtensionModule undeployedModule: this.moduleDeploymentService.getUndeployedModules())
            {
                try
                {
                    undeployedModules.add(createModuleDefinition(undeployedModule).toString());
                }
                catch (JSONException e)
                {
                    // TODO: *** EXT *** Handle JSON error creating module definition
                    e.printStackTrace();
                }
            }
            
            Date lastConfigUpdate = this.moduleDeploymentService.getLastConfigurationUpdate();
            model.put("lastCacheUpdate", ((lastConfigUpdate != null) ? lastConfigUpdate.toString() : ""));
            List<String> errors = this.moduleDeploymentService.getCurrentThreadErrors();
            if (!errors.isEmpty())
            {
                List<String> copyOfErrors = new ArrayList<String>();
                copyOfErrors.addAll(errors);
                model.put("errors", copyOfErrors);
                moduleDeploymentService.clearCurrentThreadErrors();
            }
            model.put("deployedModules", deployedModules);
            model.put("undeployedModules", undeployedModules);
            
            if (this.applicationContext != null)
            {
                List<String> evaluators = new ArrayList<String>();
                
                try
                {
                    // Add some empty data to represent the default evaluator...
                    JSONObject defaultEvaluator = new JSONObject();
                    defaultEvaluator.put("id", "");
                    JSONArray defaultEvaluatorReqProps = new JSONArray();
                    defaultEvaluator.put("requiredProps", defaultEvaluatorReqProps);
                    evaluators.add(defaultEvaluator.toString());
                    
                    // Add all the evaluators configured...
                    Map<String, ExtensionModuleEvaluator> evaluatorMap = this.applicationContext.getBeansOfType(ExtensionModuleEvaluator.class);
                    for (Entry<String, ExtensionModuleEvaluator> evaluatorEntry: evaluatorMap.entrySet())
                    {
                            JSONObject evaluator = new JSONObject();
                            evaluator.put("id", evaluatorEntry.getKey());
                            JSONArray requiredProps = new JSONArray();
                            if (evaluatorEntry != null && evaluatorEntry.getValue() != null && evaluatorEntry.getValue().getRequiredProperties() != null)
                            {
                                for (String prop: evaluatorEntry.getValue().getRequiredProperties())
                                {
                                    requiredProps.put(prop);
                                }
                            }
                            evaluator.put("requiredProps", requiredProps);
                            evaluators.add(evaluator.toString());
                    }
                    model.put("evaluators", evaluators);
                }
                catch (JSONException e)
                {
                    
                }
            }
            
        }
        return model;
    }

    /**
     * <p>Constructs a {@link JSONObject} representation of an {@link ExtensionModule}.</p>
     * @param extMod
     * @return
     * @throws JSONException
     */
    private JSONObject createModuleDefinition(ExtensionModule extMod) throws JSONException
    {
        JSONObject module = new JSONObject();
        module.put("id", extMod.getId());
        module.put("version", extMod.getVersion());
        module.put("evaluatorId", extMod.getEvaluator());
        module.put("evaluatorProperties", createEvaluatorProperties(extMod.getEvaluatorProperties()));
        return module;
    }
    
    /**
     * <p>Constructs a {@link JSONObject} representation of a configured {@link ModuleDeployment}. This will only
     * contain <code>evaluatorOverrideId</code> and <code>evaluatorPropertyOverrides</code> attributes if configured.</p> 
     * @param modDep
     * @return
     * @throws JSONException
     */
    private JSONObject createModuleDefinition(ModuleDeployment modDep) throws JSONException
    {
        JSONObject module = new JSONObject();
        module.put("id", modDep.getId());
        ExtensionModule extMod = modDep.getExtensionModule();
        module.put("version", extMod.getVersion());
        module.put("evaluatorId", extMod.getEvaluator());
        module.put("evaluatorProperties", createEvaluatorProperties(extMod.getEvaluatorProperties()));
        if (modDep.getEvaluatorOverride() != null)
        {
            module.put("evaluatorOverrideId", modDep.getEvaluatorOverride());
        }
        if (modDep.getEvaluatorPropertyOverrides() != null && modDep.getEvaluatorPropertyOverrides().size() != 0)
        {
            module.put("evaluatorPropertyOverrides", createEvaluatorProperties(modDep.getEvaluatorPropertyOverrides()));
        }
        
        return module;
    }
    
    /**
     * <p>Constructs a {@link JSONObject} representation of configured evaluator properties. This is used to process
     * both the default and overridden properties.</p>
     * 
     * @param props A map of the configured properties.
     * @return A {@link JSONObject} representation of the supplied map.
     * @throws JSONException
     */
    private JSONObject createEvaluatorProperties(Map<String, String> props) throws JSONException
    {
        JSONObject evaluatorProperties = new JSONObject();
        for (Map.Entry<String, String> evalProp: props.entrySet())
        {
            evaluatorProperties.put(evalProp.getKey(), evalProp.getValue());
        }
        return evaluatorProperties;
    }
}
