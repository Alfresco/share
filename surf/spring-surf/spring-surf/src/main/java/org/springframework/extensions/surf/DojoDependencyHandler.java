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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.directives.OutputCSSContentModelElement;
import org.springframework.extensions.directives.OutputJavaScriptContentModelElement;
import org.springframework.extensions.directives.ProcessJsonModelDirective;
import org.springframework.extensions.surf.DependencyAggregator.CompressionType;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.mvc.ResourceController;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.ScriptConfigModel;

/**
 * <p>This bean provides a way of ensuring that Dojo requested dependency file contents are modified to reference MD5 checksum codes</p>
 * 
 * @author David Draper
 */
public class DojoDependencyHandler
{
    private static final Log logger = LogFactory.getLog(DojoDependencyHandler.class);
    
    public static final String WIDGET_NAME = "name";
    public static final String WIDGET_CONFIG = "config";
    public static final String WIDGETS_LIST = "widgets";
    public static final String REQUEST_PACKAGES = "__dojoRequestPackages";
    
    /**
     * A {@link DependencyHandler} is required for general dependency resource handling requests such as retrieving the 
     * {@link InputStream} for resources, etc.
     */
    private DependencyHandler dependencyHandler = null;
    
    /**
     * Setter provided to allow the Spring application context to set a {@link DependencyHandler}.
     * @param dependencyHandler DependencyHandler
     */
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    
    /**
     * A {@link DependencyAggregator} is required for accessing compressed resources. When calculating dependencies a compressed
     * resource is always used to ensure that all unnecessary comments and whitespace have been removed. This makes it easier to 
     * write dependency detecting regular expressions for {@link DojoDependencyRule} configuration.
     */
    private DependencyAggregator dependencyAggregator = null;
    
    /**
     * Setter provided to allow the Spring application context to set a {@link DependencyAggregator}.
     * @param dependencyAggregator DependencyAggregator
     */
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }

    /**
     * The {@link WebFrameworkConfigElement} is required for accessing the Dojo configuration.
     */
    private WebFrameworkConfigElement webFrameworkConfigElement = null;
    
    /**
     * Setter provided to allow the Spring application context to set a {@link WebFrameworkConfigElement}.
     * @param webFrameworkConfigElement WebFrameworkConfigElement
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    /**
     * A {@link List} of the {@link DojoDependencyRule} instances that have been configured in the Spring application
     * context for processing Dojo dependencies.
     */
    private List<DojoDependencyRule> dependencyRules;
    
    /**
     * Setter provided to allow the Spring application context to set the {@link DojoDependencyRule} list.
     * @param dependencyRules List<DojoDependencyRule>
     */
    public void setDependencyRules(List<DojoDependencyRule> dependencyRules)
    {
        this.dependencyRules = dependencyRules;
    }

    /**
     * <p>A {@link ReadWriteLock} to prevent multiple threads accessing the cache at the same time. Multiple
     * threads can read but only one thread can write.</p>
     */
    private final ReadWriteLock depsLock = new ReentrantReadWriteLock();
    
    /**
     * <p>A map of paths to the dependencies required for the file on that path.</p>
     */
    private final Map<String, DojoDependencies> cachedDeps = new HashMap<String, DojoDependencies>();
    
    /**
     * <p>Checks for previously cached {@link DojoDependencies} for the supplied path.</p>
     * @param path The path to check the cache for.
     * 
     * @return The {@link DojoDependencies} for the supplied path if previously cached or <code>null</code> otherwise.
     */
    private DojoDependencies getCachedDeps(String path)
    {
        DojoDependencies deps = null;
        this.depsLock.readLock().lock();
        try
        {
            deps = this.cachedDeps.get(path);
        }
        finally
        {
            this.depsLock.readLock().unlock();
        }
        return deps;
    }
    
    /**
     * <p>Caches the supplied {@link DojoDependencies} against the supplied path for future reference. This is 
     * done to prevent JavaScript files being repeatedly processed unnecessarily.</p>
     * @param path The path to cache the {@link DojoDependencies} against.
     * @param deps The {@link DojoDependencies} to cache.
     */
    private void cacheDeps(String path, DojoDependencies deps)
    {
        this.depsLock.writeLock().lock();
        try
        {
            this.cachedDeps.put(path, deps);
        }
        finally
        {
            this.depsLock.writeLock().unlock();
        }
    }
    
    /**
     * Clears the caches stored in the <code>cachedDeps</code> and <code>generatedResourceCache</code> maps. 
     * This method has been provided to allow a WebScript to clear the caches of running systems.
     */
    public void clearCaches()
    {
        this.dependenciesChecksumLock.writeLock().lock();
        try
        {
           this.dependenciesChecksumCache.clear();
        }
        finally
        {
            this.dependenciesChecksumLock.writeLock().unlock();
        }
        this.depsLock.writeLock().lock();
        try
        {
            this.cachedDeps.clear();
        }
        finally
        {
            this.depsLock.writeLock().unlock();
        }
        this.cachedResourceLock.writeLock().lock();
        try
        {
            this.generatedResourceCache.clear();
        }
        finally
        {
            this.cachedResourceLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>Performs analysis on the supplied String and updates the supplied {@link DojoDependencies} object.</p>
     * 
     * @param contents String
     * @param dependencies DojoDependencies
     * @param dependenciesForCurrentRequest Map<String, DojoDependencies>
     */
    public void processString(String contents,
                              DojoDependencies dependencies,
                              Map<String, DojoDependencies> dependenciesForCurrentRequest)
    {
        if (contents != null && dependencies != null)
        {
            for (DojoDependencyRule rule: this.dependencyRules)
            {
               rule.processRegexRules(null, contents, dependencies);
            }
            // It's essential that we recursively process any dependencies that are found and add them
            // to dependencies that have already been generated. It's the recursive process that ensures
            // the analysed dependencies are output, not the initial analysis of the String...
            this.recursivelyProcessDependencies(dependencies, dependenciesForCurrentRequest);
        }
    }
    
    /**
     * <p>Gets the {@link DojoDependencies} for the supplied path. The cache is checked first to make sure that the
     * processing hasn't already been done before but if it hasn't been processed the path will be processed. All the
     * JavaScript dependencies found for the supplied path should be processed by the calling code.</p>
     * 
     * @return The {@link DojoDependencies} for the supplied path
     */
    public DojoDependencies getDependencies(String path)
    {
        DojoDependencies deps = getCachedDeps(path);
        if (deps == null)
        {
            try
            {
                String compressedFile = this.dependencyAggregator.getCompressedFile(path, CompressionType.JAVASCRIPT);
                if (compressedFile != null)
                {
                    deps = new DojoDependencies();
                    for (DojoDependencyRule rule: this.dependencyRules)
                    {
                        rule.processRegexRules(path, compressedFile, deps);
                    }
                    cacheDeps(path, deps);
                }
            }
            catch (IOException e)
            {
                logger.error(e);
            }
        }
        return deps;
    }
    
    /**
     * <p>Recursively processes all the JavaScript dependencies in the supplied {@link DojoDependencies} object. All dependency
     * paths found are added to the supplied {@link Map} so that all the dependencies for the current request can be tracked and
     * used to prevent infinite loops.</p>
     * @param deps The {@link DojoDependencies} to process.
     * @param allDeps A {@link Map} of all the dependencies processed so far.
     */
    public void recursivelyProcessDependencies(DojoDependencies deps, Map<String, DojoDependencies> allDeps)
    {
        if (deps != null)
        {
            for (String dep: deps.getJavaScriptDeps())
            {
                // Need to check whether or not the current dependency has been processed on the current thread...
                if (allDeps.containsKey(dep))
                {
                    // The current dependency has already been processed for the current request, no action required.
                }
                else
                {
                    DojoDependencies requestedDeps = getCachedDeps(dep);
                    if (requestedDeps != null)
                    {
                        // We've already processed the current dependency file. Add the cached data for the current request
                    }
                    else
                    {
                        // We haven't previously processed the current file - do so now... 
                        requestedDeps = this.getDependencies(dep);
                    }
                    allDeps.put(dep, requestedDeps); // These are the dependencies for the current request
                    recursivelyProcessDependencies(requestedDeps, allDeps);
                }
            }
        }
    }
    
    /**
     * This lock controls access to the <code>generatedResourceCache</code> {@link Map}. This allows multiple threads
     * to concurrently read but only allows one thread to write (whilst blocking all threads attempting to read).
     */
    private final ReadWriteLock cachedResourceLock = new ReentrantReadWriteLock();
    
    /**
     * This {@link Map} is used to cache all of the resources that have been generated.
     */
    private final Map<String, String> generatedResourceCache = new HashMap<String, String>();
    
    /**
     * Attempts to retrieve a generated JavaScript resource that has been previously cached. The resource will be present
     * if it has been cached via a call from a {@link ProcessJsonModelDirective} instance to the <code>getChecksumPathForDependencies</code>
     * method. 
     * @param path The path to look up in the cache.
     * @return A String containing the generated resource content or <code>null</code> if it couldn't be found.
     */
    public String getCachedResource(String path)
    {
        String resource = null;
        this.cachedResourceLock.readLock().lock();
        try
        {
            resource = this.generatedResourceCache.get(path);
        }
        finally
        {
            this.cachedResourceLock.readLock().unlock();
        }
        return resource;
    }
    
    /**
     * Caches a generated JavaScript resource (the resource should be a generated Dojo layer) against the supplied
     * checksum path. This path will be requested by the {@link ResourceController} from the output generated by the
     * {@link ProcessJsonModelDirective}.
     * 
     * @param checksum The checksum to cache against.
     * @param resource The generated resource to cache.
     */
    private void cacheResource(String checksum, String resource)
    {
        this.cachedResourceLock.writeLock().lock();
        try
        {
            this.generatedResourceCache.put(checksum, resource);
        }
        finally
        {
            this.cachedResourceLock.writeLock().unlock();
        }
    }
    
    /**
     * This lock controls access to the <code>dependenciesChecksumCache</code> {@link Map}. This allows multiple threads
     * to concurrently read but only allows one thread to write (whilst blocking all threads attempting to read).
     */
    private final ReadWriteLock dependenciesChecksumLock = new ReentrantReadWriteLock();
    
    /**
     * This {@link Map} is used to cache checksums of for a set of dependencies.
     */
    private final Map<Object, String> dependenciesChecksumCache = new HashMap<Object, String>();
    
    /**
     * This method is used to both generate the checksum for the supplied JavaScript source code and also to cache that generated
     * source code against the checksum generated. This is done so that when the {@link ResourceController} looks for the resource
     * it can be retrieved. The path returned is mapped to the "surf" Dojo package. This package should always exist as it is defined
     * in the core Surf configuration.
     * 
     * @param source The JavaScript source to generate the checksum for and to be cached.
     * @return The path including the checksum.
     */
    public String getChecksumPathForDependencies(final String source)
    {
        // When returning the aggregated Dojo resource we're going to assign it to the "surf" package - this should always be present... 
        final String surfPackage = this.getRequestDojoPackages().get("surf");
        final String checksum = surfPackage + CssImageDataHandler.FORWARD_SLASH + this.dependencyHandler.generateCheckSum(source) + ".js";
        cacheResource(checksum, source);
        return checksum;
    }
    
    /**
     * Return the checksum for a set of dependencies. The checksum for a set is cached. The checksum will be
     * resolved based on the aggregated resource output for the dependencies if it has not already been generated.
     * 
     * @param dependencies  Map of dependencies for a page - the keys are used as the overall hashkey
     * @return checksum
     */
    public String getChecksumForDependencies(
            final Map<String, DojoDependencies> dependencies, final String pagePath, final DojoDependencies pageDeps)
    {
        final String key;
        String checksum;
        this.dependenciesChecksumLock.readLock().lock();
        try
        {
            checksum = this.dependenciesChecksumCache.get(key = getBuildKeyForDependencies(dependencies));
        }
        finally
        {
            this.dependenciesChecksumLock.readLock().unlock();
        }
        
        if (checksum == null)
        {
            // Construct the aggregated output - this is where the bulk of the processing is done.
            // We don't take out the write lock yet - as this is a singleton bean and doing so could block
            // multiple read threads that are generating already cached resources - better to have multiple
            // threads potentially generate identical output and throw it away - same checksum will result.
            StringBuilder aggregatedOutput = this.outputAggregateResource(dependencies, null);
            try
            {
                // wrap with code that hitches the our Dojo "Page" object to the generated Dojo layer resource
                aggregatedOutput.append("\n\n")
                                .append(this.outputDependency("", pagePath, pageDeps).toString());
            }
            catch (IOException e)
            {
                throw new PlatformRuntimeException("IO error during dependency aggregation: " + e.getMessage(), e);
            }
            checksum = this.getChecksumPathForDependencies(aggregatedOutput.toString());
            this.dependenciesChecksumLock.writeLock().lock();
            try
            {
                this.dependenciesChecksumCache.put(key, checksum);
            }
            finally
            {
                this.dependenciesChecksumLock.writeLock().unlock();
            }
        }
        
        return checksum;
    }
    
    /**
     * Construct the hash lookup key for a Map of Dojo dependencies. The large maps of dependencies
     * will be indentical generally - there will be one per major construct per Dojo page e.g. a header
     * or an entire page. We ensure the same cached JS set is cached and looked up per set of dependencies.
     * <p>
     * The key must be based on the keys for each of the dependencies in the list - but not the values e.g.
     * not based on the common HashMap Entry algorithm getKey.hashCode() ^ getValue().hashCode() as we
     * cannot guarantee that the value objects will return the same hash each time.
     * 
     * @param dependencies  Map of DojoDependencies
     * 
     * @return hash key
     */
    protected static String getBuildKeyForDependencies(final Map<String, DojoDependencies> dependencies)
    {
        int h = 0;
        for (final Entry<String, DojoDependencies> entry: dependencies.entrySet())
        {
            h ^= entry.getKey().hashCode();
            
            // This function ensures that hashCodes that differ only by
            // constant multiples at each bit position have a bounded
            // number of collisions (approximately 8 at default load factor).
            h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
        }
        return Integer.toString(h);
    }
    
    /**
     * Calculates the configured Dojo package from the supplied path. The Dojo packages are defined in the
     * Surf configuration which is also used to bootstrap Dojo (assuming that the correct Dojo bootstrap
     * WebScript has been used on the current page).
     *  
     * @param path The path to find the Dojo package for.
     * @return The package for the supplied path (or the unchanged path if it is not part of package).
     */
    protected String reverseAlias(String path)
    {
        for (Entry<String, String> alias: this.getRequestDojoPackages().entrySet())
        {
            if (path.startsWith(alias.getValue()))
            {
                path = alias.getKey() + path.substring(alias.getValue().length());
                break;
            }
        }
        return path;
    }
    
    /**
     * <p>This method builds the equivalent of a Dojo build layer from the {@link Map} of supplied {@link DojoDependencies}.</p>
     * 
     * @return A String representing a simulated Dojo built layer.
     */
    public StringBuilder outputAggregateResource(Map<String, DojoDependencies> depsToOutput, String baseUrl)
    {
        StringBuilder sb = new StringBuilder(256000);
        sb.append("//>>built\n");
        sb.append("require({cache:{\n");
        
        // We're using an Iterator here rather than the Java5 looping options because we need to use the 
        // .hasNext() method for deciding whether or not to add commas to the StringBuilder...
        Iterator<Entry<String, DojoDependencies>> i = depsToOutput.entrySet().iterator();
        while(i.hasNext())
        {
            try
            {
                Entry<String, DojoDependencies> currentDependency = (Entry<String, DojoDependencies>) i.next();

                String depName = currentDependency.getKey();
                String depPath = getPath(baseUrl, depName);
                
                // Remove the alias part of the path...
                depName = reverseAlias(depName);
                
                if (depName.endsWith(".js"))
                {
                    depName = depName.substring(0, depName.length() -3);
                }
                
                // If an error occurs during generation it will not be included in the aggregated resource
                // and will need to be requested separately by the client. Even if that request will ultimately
                // fail it will at least be easier to identify the problematic file request...
                String dep = outputDependency(depName, depPath, currentDependency.getValue()).toString();
                
                sb.append("'");
                sb.append(depName);
                sb.append("':function(){\n");
                sb.append(dep);
                sb.append("}");
                if (i.hasNext())
                {
                    sb.append(",");
                }
                sb.append("\n");
            }
            catch (Exception e)
            {
                // This is a broad catch but allows for extension handling. We only want to output the dependency if 
                // the call to outputDependency is successful.
                if (logger.isDebugEnabled())
                    logger.debug("Developer Warning: " + e.getMessage(), e);
            }
        }
        
        sb.append("}});");
        return sb;
    }
    
    /**
     * <p>This method is used to generate the output for a single dependency in a way that replicates what would be generated
     * from building a Dojo layer. Effectively the supplied file is added to a cache object that prevents Dojo from needing
     * to explicitly load the file when it is required by another file.</p>
     * 
     * @param name The (short) name of the dependency (relative to the Dojo AMD package configuration).
     * @param path The full path of the file
     * @param deps A list of the dependencies known to the current dependency. This is primarily used for processing
     * text dependencies.
     * @return StringBuilder
     * @throws IOException
     */
    public StringBuilder outputDependency(final String name, final String path, final DojoDependencies deps) throws IOException
    {
        final StringBuilder currentDep = new StringBuilder(4096);
        if (deps != null && !deps.getTextDeps().isEmpty())
        {
            currentDep.append("require({cache:{");
            final Iterator<String> j = deps.getTextDeps().iterator();
            while(j.hasNext())
            {
               String textDependency = (String)j.next();
               currentDep.append("'url:");
               String textPath = getPath(path, textDependency);
               if (textPath.startsWith(CssImageDataHandler.FORWARD_SLASH))
               {
                   textPath = textPath.substring(1);
               }
               String shortTextPath = reverseAlias(textPath);
               currentDep.append(shortTextPath);
               currentDep.append("':");
               
               // Get text file and escape all the quotes...
               currentDep.append("'");
               InputStream in = this.dependencyHandler.getResourceInputStream(textPath);
               if (in != null)
               {
                   String textContents = this.dependencyHandler.convertResourceToString(in);
                   textContents = textContents.replace('\'', '"').replace("\"", "\\\"").replace("\n", "").replace("\r", "");
                   currentDep.append(textContents);
               }
               else
               {
                   // No action required.
               }
               currentDep.append("'");
               if (j.hasNext())
               {
                   currentDep.append(",");
               }
               currentDep.append("\n");
               // There could be a minor edge case here were the last resource can't be found. This shouldn't occur because
               // all dependencies should have been found before being processed.
            }
            currentDep.append("}});");
        }
        // Output the file contents...
        if (this.dependencyHandler.isDebugMode())
        {
            InputStream in = this.dependencyHandler.getResourceInputStream(path);
            currentDep.append(this.dependencyHandler.convertResourceToString(in));
        }
        else
        {
            currentDep.append(this.dependencyAggregator.getCompressedFile(path, DependencyAggregator.CompressionType.JAVASCRIPT));
        }
        return currentDep;
    }
    
    /**
     * This returns the Dojo packages for the current request (that will include all dynamic configuration extensions)
     * if available, otherwise returns the spring injected value (the static configuration).
     * 
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getRequestDojoPackages()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        Map<String, String> dojoPackages = (Map<String, String>)rc.getValue(REQUEST_PACKAGES);
        if (dojoPackages == null)
        {
            ScriptConfigModel config = rc.getExtendedScriptConfigModel(null);
            Map<String, ConfigElement> configs = (Map<String, ConfigElement>)config.getScoped().get("WebFramework");
            if (configs != null)
            {
                WebFrameworkConfigElement wfce = (WebFrameworkConfigElement) configs.get("web-framework");
                dojoPackages = wfce.getDojoPackages();
            }
            else
            {
                dojoPackages = this.webFrameworkConfigElement.getDojoPackages();
            }
            rc.setValue(REQUEST_PACKAGES, (Serializable)dojoPackages);
        }
        return dojoPackages;
    }
    
    /**
     * <p>Calculates the path of the dependency relative to the path of the supplied source path. If dependency path is mapped
     * to a Dojo package alias then that package is used to calculate the path.</p>
     * 
     * @param sourcePath The path of the file from which the dependency is referenced.
     * @param dependencyPath The path of the dependency as referenced in the source file. 
     * @return String
     */
    public String getPath(final String sourcePath, String dependencyPath)
    {
        final StringBuilder sb = new StringBuilder(64);
        
        // Remove opening and closing quotes...
        if (dependencyPath.startsWith(CssImageDataHandler.DOUBLE_QUOTES) || dependencyPath.startsWith(CssImageDataHandler.SINGLE_QUOTE))
        {
            dependencyPath = dependencyPath.substring(1);
        }
        if (dependencyPath.endsWith(CssImageDataHandler.DOUBLE_QUOTES) || dependencyPath.endsWith(CssImageDataHandler.SINGLE_QUOTE))
        {
            dependencyPath = dependencyPath.substring(0, dependencyPath.length() -1);
        }
        
        // We need to check whether or not the dependency path begins with a module alias (these can be configured within
        // the Dojo configuration to map modules to directories). If they do start with an alias then we can effectively leave
        // them as they are (and not append them to the source path because Dojo will take care of that for us)
        boolean usesModuleAlias = false;
        if (!dependencyPath.startsWith(CssImageDataHandler.FULL_STOP) && 
            !dependencyPath.startsWith(CssImageDataHandler.DOUBLE_FULL_STOP) &&
            !dependencyPath.startsWith(CssImageDataHandler.FORWARD_SLASH))
        {
           int idx = dependencyPath.indexOf(CssImageDataHandler.FORWARD_SLASH);
           if (idx != -1)
           {
               String firstFolder = dependencyPath.substring(0, idx);
               String alias = this.getRequestDojoPackages().get(firstFolder);
               if (alias != null)
               {
                  // The first folder in the path is a module alias, we don't need to make any changes...
                  usesModuleAlias = true;
                  sb.append(alias).append(dependencyPath.substring(idx));
               }
           }
        }
        
        if (!usesModuleAlias)
        {
            sb.append(this.dependencyHandler.getRelativePath(sourcePath, dependencyPath));
        }
        return sb.toString();
    }
    
    /**
     * <p>Recursively processes lists of widgets that are defined in the WebScript model. This works on the assumption that nested 
     * widgets will be configured as follows:
     * 
     * <pre>
     *    name: <{@code}widget-name>
     *    config: {
     *       widgets: [
     *          {
     *             name: <{@code}widget-name>
     *             <{@code}etc...>
     *          }
     *       ]
     *    }
     * </pre>
     * </p>
     * @param widgets The {@link List} of widgets currently being processed.
     * @param dependenciesForCurrentRequest The dependencies processed on the current request.
     */
    @SuppressWarnings("rawtypes")
    public void processControllerWidgets(List widgets, Map<String, DojoDependencies> dependenciesForCurrentRequest)
    {
        // Widgets processed in the JavaScript controller must all be relative to the Dojo package configuration. 
        if (widgets != null)
        {
            for (Object w: widgets)
            {
                if (w instanceof Map)
                {
                    Map map = (Map) w;
                    Object s = map.get(WIDGET_NAME);
                    if (s instanceof String)
                    {
                        final String widgetPath = getPath(null, ((String)s)) + ".js";
                        DojoDependencies widgetDeps = getDependencies(widgetPath);
                        dependenciesForCurrentRequest.put(widgetPath, widgetDeps);
                        recursivelyProcessDependencies(widgetDeps, dependenciesForCurrentRequest);
                    }
                    Object m = map.get(WIDGET_CONFIG);
                    if (m instanceof Map)
                    {
                        Object l = ((Map) m).get(WIDGETS_LIST);
                        if (l instanceof List)
                        {
                            processControllerWidgets((List) l, dependenciesForCurrentRequest);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * This method iterates through the dependencies provided <b>in reverse</b>. This is so that the dependency CSS resources are 
     * output <b>before</b> the widget that places the dependency. This is done so that overrides will occur as expected. This
     * is only relevant for CSS files. The remaining dependencies are all handled in their own ways. 
     * 
     * @param dependenciesForCurrentRequest Map<String, DojoDependencies>
     * @param outputCss OutputCSSContentModelElement
     * @param prefix String
     * @param group String
     */
    public void processCssDependencies(Map<String, DojoDependencies> dependenciesForCurrentRequest, 
                                       OutputCSSContentModelElement outputCss,
                                       String prefix,
                                       String group) 
    {
        Set<String> depKeys = dependenciesForCurrentRequest.keySet();
        
        Object[] a = depKeys.toArray();
        for (int i=a.length-1; i>=0; i--)
        {
            DojoDependencies currDep = dependenciesForCurrentRequest.get(a[i]);
            if (currDep != null)
            {
                if (outputCss != null)
                {
                    for (DojoDependencies.CssDependency cssDep: currDep.getCssDeps())
                    {
                        outputCss.addDojoCssDependency(cssDep.getPath(), cssDep.getMediaType(), group);
                    }
                }
            }
        }
    }
    
    /**
     * This method iterates through the dependencies provided <b>in reverse</b>. This is so that the non AMD resources are 
     * output <b>before</b> the widget that places the dependency.
     * 
     * @param dependenciesForCurrentRequest Map<String, DojoDependencies>
     * @param outputJs OutputJavaScriptContentModelElement
     * @param prefix String
     * @param group String
     */
    public void processNonAmdDependencies(Map<String, DojoDependencies> dependenciesForCurrentRequest, 
                                          OutputJavaScriptContentModelElement outputJs,
                                          String prefix,
                                          String group) 
    {
        Set<String> depKeys = dependenciesForCurrentRequest.keySet();
        
        Object[] a = depKeys.toArray();
        for (int i=0; i < a.length; i++)
        {
            DojoDependencies currDep = dependenciesForCurrentRequest.get(a[i]);
            if (currDep != null)
            {
                if (outputJs != null)
                {
                    for (String dep: currDep.getNonAmdDependencies())
                    {
                        outputJs.addNonAmdJavaScriptFile(dep, group);
                    }
                }
            }
        }
    }
}
