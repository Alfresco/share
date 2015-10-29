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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This backs the /surf/xhr/dependencies WebScript and should be used to build resource files containing
 * all the required CSS and JavaScript dependencies for a set of widgets defined in a stringified JSON object
 * passed as a request parameter.
 * 
 * @author David Draper
 *
 */
public class XhrDependencies extends DeclarativeWebScript
{
    private DependencyAggregator dependencyAggregator;

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
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        String jsResource = "";
        String cssResource = "";
        String[] jsDeps = req.getParameterValues("js");
        if (jsDeps != null && jsDeps.length > 0)
        {
            jsResource = this.dependencyAggregator.generateJavaScriptDependencies(buildHashSet(jsDeps));
        }
        String[] cssDeps = req.getParameterValues("css");
        if (cssDeps != null && cssDeps.length > 0)
        {
            cssResource = this.dependencyAggregator.generateCSSDependencies(buildHashSet(cssDeps));
        }
        model.put("jsResource", jsResource);
        model.put("cssResource", cssResource);
        return model;
    }
    
    public DependencyAggregator getDependencyAggregator()
    {
        return dependencyAggregator;
    }

    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }
}
