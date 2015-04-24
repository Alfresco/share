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

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;

/**
 * <p>This <code>ExtensionModuleEvaluator</code> will always approve each module request. It is designed to be the default
 * Spring Surf evaluator and applications should override the Spring bean configuration to provide a new default evaluator
 * and/or additional evaluators that modules can directly specify</p>
 * 
 * @author David Draper
 */
public class ApproveAllModulesEvaluator implements ExtensionModuleEvaluator
{
    public boolean applyModule(RequestContext context, 
                               Map<String, String> evaluationProperties)
    {
        return true;
    }

    public String[] getRequiredProperties()
    {
        return new String[] {};
    }
    
}
