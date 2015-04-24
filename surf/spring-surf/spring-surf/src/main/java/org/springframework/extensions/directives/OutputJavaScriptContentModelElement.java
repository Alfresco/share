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
package org.springframework.extensions.directives;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentSourceModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

/**
 * <p>The {@link ContentModelElement} associated with the {@link OutputJavaScriptDirective} that is
 * used to flush out all <{@code}script> elements.</p>
 * 
 * @author David Draper
 */
public class OutputJavaScriptContentModelElement extends DependencyDeferredContentTargetModelElement
{
    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(OutputJavaScriptContentModelElement.class);

    private String resourceControllerMapping = null;
    private DependencyAggregator dependencyAggregator;
    private WebFrameworkConfigElement webFrameworkConfig;
    private static final String TYPE = "DependencyContent";
    
    public OutputJavaScriptContentModelElement(String id, 
                                               String directiveName, 
                                               DependencyAggregator dependencyAggregator,
                                               WebFrameworkConfigElement webFrameworkConfig)
    {
        super(id, directiveName);
        this.dependencyAggregator = dependencyAggregator;
        this.webFrameworkConfig = webFrameworkConfig;
        resourceControllerMapping = this.dependencyAggregator.getServletContext().getContextPath() + this.dependencyAggregator.getDependencyHandler().getResourceControllerMapping() + "/";
    }

    protected String getResourceControllerMapping()
    {
        return this.resourceControllerMapping;
    }
    
    @Override
    public String getType()
    {
        return TYPE;
    }
    
    /**
     * <p>A map of groups of JavaScript dependencies. A set is used so that
     * dependencies are only included once. This list will be used to generate
     * the dependencies.</p>
     */
    private LinkedHashMap<String, LinkedHashSet<String>> javaScriptFiles = new LinkedHashMap<String, LinkedHashSet<String>>();
    
    /**
     * <p>A map of groups of JavaScript dependencies to be aggregated. A set is used so that
     * dependencies are only included once. This list will be used to generate
     * the dependencies.</p>
     */
    private LinkedHashMap<String, LinkedHashSet<String>> aggJavaScriptFiles = new LinkedHashMap<String, LinkedHashSet<String>>();

    /**
     * <p>A map of non-AMD JavaScript dependencies identified through module analysis FOR AGGREGATION.</p>
     */
    private LinkedHashMap<String, LinkedHashSet<String>> dojoNonAmdFiles = new LinkedHashMap<String, LinkedHashSet<String>>();
    
    public LinkedHashMap<String, LinkedHashSet<String>> getDojoNonAmdFiles()
    {
        return this.dojoNonAmdFiles;
    }
    
    /**
     * Handles adding JavaScript dependencies and inline script content. 
     * @param fileName
     * @param groupName
     * @param forAggregation
     */
    public void addJavaScriptFile(String fileName, String groupName, boolean forAggregation)
    {
        if (fileName.startsWith(DependencyAggregator.INLINE_AGGREGATION_MARKER))
        {
            // Don't do any special processing with inline JavaScript requests...
            if (forAggregation)
            {
                this.addJavaScriptFile(fileName, groupName, this.aggJavaScriptFiles);
            }
            else
            {
                this.addJavaScriptFile(fileName, groupName, this.javaScriptFiles);
            }
        }
        else
        {
            fileName = this.normaliseDependency(fileName);
            if (dependencyAlreadyRequested(fileName))
            {
                // No action required. Don't add a duplicate dependency.
            }
            else
            {
                markDependencyAsRequested(fileName);
                if (forAggregation)
                {
                    this.addJavaScriptFile(fileName, groupName, this.aggJavaScriptFiles);
                }
                else
                {
                    String checksumPath = this.dependencyAggregator.getDependencyHandler().getChecksumPath(fileName);
                    if (checksumPath.toLowerCase().startsWith(DirectiveConstants.HTTP_PREFIX) || checksumPath.toLowerCase().startsWith(DirectiveConstants.HTTPS_PREFIX))
                    {
                        // Don't prefix explicitly requested resources...
                        this.addJavaScriptFile(checksumPath, groupName, this.javaScriptFiles);
                    }
                    else
                    {
                        this.addJavaScriptFile(this.resourceControllerMapping + checksumPath, groupName, this.javaScriptFiles);
                    }
                }
            }
        }
    }
    
    /**
     * <p>Use this method to add a new JavaScript file to the list of dependencies
     * to be imported.</p>
     * 
     * @param fileName The filename of the JavaScript dependency.
     * @param groupName The group to add the JavaScript dependency to (passing a groupName of <code>null</code>
     * effectively assigns the dependency to the default group).
     */
    protected void addJavaScriptFile(String fileName, String groupName, LinkedHashMap<String, LinkedHashSet<String>> files)
    {
        LinkedHashSet<String> group = files.get(groupName);
        if (group == null)
        {
            group = new LinkedHashSet<String>();
            files.put(groupName, group);
        }
        group.add(fileName);
    }
    
    /**
     * The Dojo generated non AMD JavaScript dependencies need to be handled separately from the those provided by the <{@code}@script> directive.
     * The reason for this is that Dojo dependencies get added as soon as they are encountered but the <{@code}@link> directive
     * dependencies only get added after all extensions have finished processing (this is so that the {@link DeferredContentSourceModelElement}
     * instances can be manipulated to add/remove/change dependency requests). However, we want to only output Dojo dependencies
     * if they have <b>not</b> requested via a <{@code}@script> directive.
     * 
     * @param fileName
     * @param group
     */
    public void addNonAmdJavaScriptFile(String fileName, 
                                        String group)
    {
        fileName = this.normaliseDependency(fileName);
        this.addJavaScriptFile(fileName, group, this.dojoNonAmdFiles);
    }
    
    /**
     * <p>Adds raw JavaScript into the dependency list (so that order is preserved) by prefixing the code
     * with illegal path characters. These characters can be detected by the {@link DependencyHandler} and
     * used to create a new script block with the contents.</p>
     * 
     * @param script The raw JavaScript to insert.
     * @param groupName The name of the group. This is important when dependency aggregation is enabled as
     * it ensures that the code is inserted into the correct resource.
     */
    protected void addInlineJavaScriptForAggregation(String script, String groupName)
    {
        // Call with an illegal path prefix... we could be adding large string, but hopefully not...
        addJavaScriptFile(DependencyAggregator.INLINE_AGGREGATION_MARKER + script, groupName, this.aggJavaScriptFiles);
    }
    
    /**
     * Processes the supplied {@link LinkedHashMap} to ensure that any dependencies that have been previously 
     * requested are removed.
     * 
     * @param files The {@link LinkedHashMap} of files to filter
     * @return The filtered {@link LinkedHashMap}
     */
    protected LinkedHashMap<String, LinkedHashSet<String>> filterJsDependencies(LinkedHashMap<String, LinkedHashSet<String>> files) 
    {
        // Iterate over the supplied map...
        LinkedHashMap<String, LinkedHashSet<String>> filteredMap = new LinkedHashMap<String, LinkedHashSet<String>>();
        for (Entry<String, LinkedHashSet<String>> group: files.entrySet())
        {
            LinkedHashSet<String> filteredGroup = new LinkedHashSet<String>();
            filteredMap.put(group.getKey(), filteredGroup);
            for (String currFile: group.getValue())
            {
                if (dependencyAlreadyRequested(currFile))
                {
                    // If the JavaScript resource has already been requested then it needs to be removed
                }
                else
                {
                    // If the JavaScript resource hasn't been requested yet then add it to the filtered list and
                    // mark it as having been requested...
                    filteredGroup.add(currFile);
                    this.markDependencyAsRequested(currFile);
                }
            }
        }
        return filteredMap;
    }

    /**
     * <p>
     * This method will be called when the {@link ExtensibilityModel} containing
     * this {@link ContentModelElement} is closed. At this point any nested
     * models will have already added their required JavaScript dependencies and
     * <{@code}script> elements added. 
     */
    @Override
    public String flushContent()
    {
        // Build the dependencies in the order requested following all extension processing...
        for (DeferredContentSourceModelElement sourceElement: this.getSourceElements())
        {
            if (sourceElement instanceof DependencyDeferredContentSourceModelElement)
            {
                // Sort the elements into their respective groups...
                DependencyDeferredContentSourceModelElement dependency = (DependencyDeferredContentSourceModelElement) sourceElement;
                addJavaScriptFile(dependency.getDependency(), dependency.getGroup(), dependency.isAggregate());
            }
        }
        
        // Process the dependencies into JavaScript output...
        StringBuilder content = new StringBuilder();
        // Temporailty commented out for ACE-1354
//        if (javaScriptFiles.size() != 0 || aggJavaScriptFiles.size() != 0)
//        {
//            // we either output a lot here or nothing at all
//            content.setLength(20480);
//        }
        content.append(generateJavaScriptDependencies(javaScriptFiles, false));
        content.append(generateJavaScriptDependencies(aggJavaScriptFiles, true));
        
        // Filter out any already requested dependencies from those requested by Dojo widgets...
        LinkedHashMap<String, LinkedHashSet<String>> filteredNonAmdFiles = this.filterJsDependencies(this.dojoNonAmdFiles);
        content.append(generateJavaScriptDependencies(filteredNonAmdFiles, true));
        return content.toString();
    }
    
    /**
     * <p>Generates the complete list of JavaScript dependencies (including raw script inserts)
     * into a {@link StringBuilder} which can then be output to the output stream. If Surf is
     * running in dependency aggregation mode then this work is deleted to the {@link DependencyAggregator}
     * otherwise it is handled within the method.</p>
     * 
     * @param dependencies The dependencies to create the correct output for.
     * @return
     */
    protected StringBuilder generateJavaScriptDependencies(HashMap<String, LinkedHashSet<String>> dependencies, boolean aggregate)
    {
        StringBuilder jsDeps = new StringBuilder(128);
        for (Entry<String, LinkedHashSet<String>> entry: dependencies.entrySet())
        {
            if (aggregate)
            {
                // If Surf is in dependency aggregation mode then delegate to the DependencyAggregator
                // service as this effort will involve compressing, aggregating and caching the dependency contents.
                String checksum = dependencyAggregator.generateJavaScriptDependencies(entry.getValue());
                appendJavaScriptDependency(jsDeps, resourceControllerMapping + checksum, entry.getKey());
            }
            else
            {
                // When running in "normal" mode we can just process the dependencies as normal - the key
                // thing is to check for the inline JavaScript marker to ensure that it is NOT treated as
                // a resource file...
                for (String javaScript: entry.getValue())
                {
                    if (javaScript != null)
                    {
                        if (javaScript.startsWith(DependencyAggregator.INLINE_AGGREGATION_MARKER))
                        {
                            jsDeps.append(DirectiveConstants.OPEN_SCRIPT);
                            jsDeps.append(javaScript.substring(DependencyAggregator.INLINE_AGGREGATION_MARKER.length()));
                            jsDeps.append(DirectiveConstants.CLOSE_SCRIPT);
                        }
                        else
                        {
                            jsDeps.append(DirectiveConstants.OPEN_DEP_SCRIPT_TAG);
                            jsDeps.append(javaScript);
                            jsDeps.append(DirectiveConstants.CLOSE_DEP_SCRIPT_TAG);
                            jsDeps.append(DirectiveConstants.NEW_LINE);
                        }
                    }
                }
            }
        }
        return jsDeps;
    }
    
    /**
     * <p>Method for adding a single JavaScript dependency to the supplied String builder, e.g.
     * <{@code}script type="text/javascript" src="src"><{@code}/script>
     * 
     * @param jsDeps
     * @param src
     */
    protected void appendJavaScriptDependency(StringBuilder jsDeps, String src, String group)
    {
        jsDeps.append(DirectiveConstants.OPEN_DEP_SCRIPT_TAG);
        jsDeps.append(src);
        jsDeps.append(DirectiveConstants.CLOSE_DEP_SCRIPT_TAG);
        if (group != null)
        {
            jsDeps.append(DirectiveConstants.OPEN_GROUP_COMMENT);
            jsDeps.append(group);
            jsDeps.append(DirectiveConstants.CLOSE_GROUP_COMMENT);
        }
        jsDeps.append(DirectiveConstants.NEW_LINE);
    }

    public WebFrameworkConfigElement getWebFrameworkConfig()
    {
        return webFrameworkConfig;
    }

    public String getRequestPrefix()
    {
        return resourceControllerMapping;
    }

    public DependencyAggregator getDependencyAggregator()
    {
        return dependencyAggregator;
    }
}
