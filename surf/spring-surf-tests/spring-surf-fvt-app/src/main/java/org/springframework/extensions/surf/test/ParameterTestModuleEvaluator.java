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
package org.springframework.extensions.surf.test;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;

/**
 * Checks that all the evaluation properties exist as request parameters.
 * @author David Draper
 *
 */
public class ParameterTestModuleEvaluator implements ExtensionModuleEvaluator 
{
    @Override
    public boolean applyModule(RequestContext context, Map<String, String> evaluationProperties)
    {
        boolean apply = true;
        for(Entry<String, String> prop: evaluationProperties.entrySet())
        {
            String param = context.getParameters().get(prop.getKey());
            if (param != null && param.equals(prop.getValue()))
            {
                // Found matching parameter, keep going...
            }
            else
            {
                apply = false;
                break;
            }
        }
        return apply;
    }

    @Override
    public String[] getRequiredProperties()
    {
        return new String[] {};
    }
   
}
