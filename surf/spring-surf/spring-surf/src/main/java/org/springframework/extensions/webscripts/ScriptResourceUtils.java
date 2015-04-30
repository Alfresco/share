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
package org.springframework.extensions.webscripts;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.springframework.extensions.surf.DependencyAggregator;

public class ScriptResourceUtils extends ScriptBase
{
    private static final long serialVersionUID = 1L;

    private DependencyAggregator dependencyAggregator = null;
    
    public ScriptResourceUtils(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }
    
    @Override
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        return null;
    }
    
    /**
     * Builds a new {@link LinkedHashSet} from the supplied {@link String} array.
     * @param input The {@link String} array to convert to a {@link LinkedHashSet}
     * @return The populated {@link LinkedHashSet}
     */
    protected LinkedHashSet<String> buildHashSet(String[] input)
    {
        LinkedHashSet<String> output = new LinkedHashSet<String>();
        if (input != null)
        {
            for (String value: input)
            {
                // Trim off any initial forward slash otherwise the resource won't be found
                if (value.startsWith("/"))
                {
                    value = value.substring(1);
                }
                output.add(value);
            }
        }
        return output;
    }
    
    /**
     * 
     * @param jsResources
     * @return
     */
    public String getAggregratedJsResources(String[] jsResources)
    {
        String aggregatedResource = null;
        if (jsResources != null)
        {
            aggregatedResource = this.dependencyAggregator.generateJavaScriptDependencies(buildHashSet(jsResources));
        }
        return aggregatedResource;
    }
    
    /**
     * 
     * @param cssResources
     * @return
     */
    public String getAggregratedCssResources(String[] cssResources)
    {
        String aggregatedResource = null;
        if (cssResources != null)
        {
            aggregatedResource = this.dependencyAggregator.generateJavaScriptDependencies(buildHashSet(cssResources));
        }
        return aggregatedResource;
    }
    
}
