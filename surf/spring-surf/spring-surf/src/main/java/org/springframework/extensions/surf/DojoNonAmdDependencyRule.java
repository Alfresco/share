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

import java.util.regex.Matcher;

/**
 * <p>This {@link DojoDependencyRule} is used to detect Dojo dependencies that are defined within JavaScript source files
 * as JSON arrays of JSON objects. This rule will detect dependencies that are not explicitly declared through a "define"
 * function call but that will be dynamically requested at runtime.</p>
 *  
 * @author David Draper
 */
public class DojoNonAmdDependencyRule  extends DojoDependencyRule
{
    /**
     * <p>Overrides the default implementation to recurse over the group returned by the main declaration regular expression. This
     * is required because each JSON object that defined a widget can itself declare an array of dependencies.</p>
     */
    @Override
    protected void processRegexRules(String filePath, 
                                     String fileContents, 
                                     DojoDependencies dependencies)
    {
        Matcher m1 = getDeclarationRegexPattern().matcher(fileContents);
        while (m1.find())
        {
            if (m1.groupCount() >= getTargetGroup())
            {
                // The second group in a regex match will contain array of dependencies...
                String deps = m1.group(getTargetGroup());
                if (deps != null)
                {
                    // Recursively look for nested widgets...
                    processRegexRules(filePath, deps, dependencies);
                    
                    // Find the dependencies in the widgets list...
                    Matcher m2 = getDependencyRegexPattern().matcher(deps);
                    while (m2.find())
                    {
                        String dep = m2.group(1);
                        if (dep != null)
                        {
                            String depPath = getDojoDependencyHandler().getPath(filePath, dep);
                            addNonAmdJavaScriptDependency(dependencies, depPath);
                        }
                    }
                }
            }
        }
    }
}
