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
package org.springframework.extensions.surf.types;

import java.util.List;
import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.extensibility.Customization;

public interface ModuleDeployment extends ModelObject, Comparable<ModuleDeployment>
{

    public static String TYPE_ID = "module-deployment";

    // properties
    public static String PROP_MODULE_DEPLOYMENT_TYPE = "module-deployment-type";

    public static String PROP_INDEX = "index";
    public static String PROP_EXTENSION_MODULE = "extension-module";
    public static String PROP_EVALUATOR_OVERRIDE = "evaluator-override";
    public static String PROP_EVALUATOR_PROPERTY_OVERRIDES = "evaluator-property-overrides";
    
    public String getModuleDeploymentType();
    
    public void setModuleDeploymentType(String moduleDeploymentType);
    

    public String getExtensionModuleId();
   
    public void setExtensionModuleId(String extensionModule);
    
    public ExtensionModule getExtensionModule();
    
    public void setExtensionModule(ExtensionModule extensionModule);
    
    public int getIndex();
    
    public void setIndex(int index);

    public String getVersion();
    
    public String getAutoDeployIndex();
    
    public String getEvaluator();

    public Map<String, String> getEvaluatorProperties();
    
    public void setEvaluatorOverride(String evaluatorOverride);
    
    public void setEvaluatorPropertyOverrides(Map<String, String> evaluatorProperties);
    
    public String getEvaluatorOverride();
    
    public Map<String, String> getEvaluatorPropertyOverrides();
    
    /**
     * Checks to see if the module declares an extension to the {@link AdvancedComponent} defined by the 
     * supplied id. If the an extension has been declared then it is returned.</p>
     * 
     * @param id The identifier of the {@link AdvancedComponent} extension to retrieve.
     * @return A {@link AdvancedComponent} extension matching the supplied id.
     */
    public AdvancedComponent getAdvancedComponent(String id);
    
    public List<Customization> getCustomizations();
}
