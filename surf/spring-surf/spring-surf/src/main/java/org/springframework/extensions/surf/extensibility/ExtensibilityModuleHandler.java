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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.types.ExtensionModule;


/**
 * <p>Classes implementing this interface should be able to determine paths to the files provided by
 * a module that extend a {@linl ExtensibilityModel} being processed. They should also be able to 
 * return a String representing the HTML instructions for importing client side JavaScript and CSS
 * resource dependencies required by the extending modules.</p>
 * 
 * @author David Draper
 */
public interface ExtensibilityModuleHandler
{
    /**
     * <p>Evaluates the {@link ExtensionModule} instances that are applicable for the supplied
     * {@link RequestContext}. 
     * @param context The {@link RequestContext} to evaluate modules for.
     * @return A {@link List} of {@link ExtensionModule} instances.
     */
    public List<ExtensionModule> evaluateModules(RequestContext context);
    
    /**
     * <p>Returns the JavaScript dependencies for the current extension module for the supplied path.</p>
     * @param module The module to retrieve JavaScript dependencies from.
     * @param path The current path being processed. This determines which dependencies are mapped.
     * @return A list of JavaScript dependencies
     */
    public LinkedHashSet<String> getModuleJsDeps(ExtensionModule module, String path);

    /**
     * <p>Returns the CSS dependencies for the current extension module for the supplied path.</p>
     * @param module The module to retrieve CSS dependencies from.
     * @param path The current path being processed. This determines which dependencies are mapped.
     * @return A list of CSS dependencies
     */
    public Map<String, LinkedHashSet<String>> getModuleCssDeps(ExtensionModule module, String path);
    
    /**
     * <p>Returns a {@link List} of the files that should be applied to an {@link ExtensibilityModel}
     * being processed.</p>
     * @param pathBeingProcessed The path of the file being processed. This will typically be a FreeMarker
     * template, JavaScript controller or NLS properties file.
     * 
     * @return A {@link List} of the files that extend the current file being processed.
     */
    public List<String> getExtendingModuleFiles(ExtensionModule module, String pathBeingProcessed);
}
