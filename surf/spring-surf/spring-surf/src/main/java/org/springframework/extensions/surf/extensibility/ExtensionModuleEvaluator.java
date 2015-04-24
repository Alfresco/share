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
package org.springframework.extensions.surf.extensibility;

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;

/**
 * <p>Implementations of this interface can be used to determine whether or not an extension module should be applied
 * to an existing extensibility model.
 * 
 * @author David Draper
 *
 */
public interface ExtensionModuleEvaluator
{
    /**
     * <p>Determines whether or not to apply a module. The module being processed will already have been matched to the
     * path being processed but an evaluator can still be used to only apply the module in certain circumstances. These
     * will typically be dictated by whether or not some data in the supplied model matches the criteria defined in the
     * supplied <code>evaluationProperties</code>.</p>  
     * @param context The current {@link RequestContext}
     * @param evaluationProperties The evaluation properties defined in the module.
     * @return <code>true</code> if the module should be applied and <code>false</code> otherwise.
     */
    public boolean applyModule(RequestContext context,
                               Map<String, String> evaluationProperties);
    
    /**
     * <p>Returns the names of the required evaluation properties that are needed to successfully perform an evaluation.
     * This information is used when provided a user-interface that allows a {@link ExtensionModuleEvaluator} to be 
     * dynamically configured for a module.</p>
     * 
     * @return A String array containing the names of the properties that are required by the evaluator.
     */
    public String[] getRequiredProperties();
}
