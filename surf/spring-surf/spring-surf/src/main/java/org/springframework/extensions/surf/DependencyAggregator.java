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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.GenericConfigElement;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.web.context.WebApplicationContext;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.yahoo.platform.yui.javascript.ErrorReporter;
import com.yahoo.platform.yui.javascript.EvaluatorException;

public class DependencyAggregator implements ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(DependencyAggregator.class);
    
    public String charset = "UTF-8";
    public int linebreak = -1;
    public boolean munge = true;
    public boolean verbose = false;
    public boolean preserveAllSemiColons = false;
    public boolean disableOptimizations = false;
    
    private Boolean isDebugMode = null;
    private Boolean isCollationDebugMode = null;
    
    public static final String FLAGS = "flags";
    public static final String CLIENT_DEBUG = "client-debug";
    public static final String CLIENT_COLLATION_DEBUG = "client-collation-debug";
    
    // This marker would be illegal in a path. When detected in a path it indicates that the "path"
    // is in fact inline JavaScript or CSS to insert into the aggregated results..
    public static final String INLINE_AGGREGATION_MARKER = ">>>";
    
    /** Set the size of the file cache for MD5 checksums */
    public int cacheSize = 10240;
    public void setCacheSize(int cacheSize)
    {
        this.cacheSize = cacheSize;
    }
    
    private CssImageDataHandler cssImageDataHandler;
    public void setCssImageDataHandler(CssImageDataHandler cssImageDataHandler)
    {
        this.cssImageDataHandler = cssImageDataHandler;
    }

    /**
     * The {@link CssThemeHandler} is used to perform token substitution on supplied CSS source
     * files to allow a single source file to be customized by themes.
     */
    private CssThemeHandler cssThemeHandler;
    public void setCssThemeHandler(CssThemeHandler cssThemeHandler)
    {
        this.cssThemeHandler = cssThemeHandler;
    }
    
    private DependencyHandler dependencyHandler;
    
    public DependencyHandler getDependencyHandler()
    {
        return dependencyHandler;
    }

    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }

    private List<String> compressionExclusions;
    
    private List<Pattern> compressionExclusionPatterns = new ArrayList<Pattern>();
    
    public void setCompressionExclusions(List<String> compressionExclusions)
    {
        this.compressionExclusions = compressionExclusions;
        
        // Compile the exclusion patterns when provided to save repeating effort later on...
        for (String exlusion: this.compressionExclusions)
        {
            // Convert ? and * wildcards to regex style...
            String regex = exlusion.replace("?", "(.?)").replace("*", "(.*)");
            compressionExclusionPatterns.add(Pattern.compile(regex));
        }
    }

    /**
     * <p>Indicates whether the client should operate in debug mode. This means that all dependency resources
     * should not be compressed or collated.</p>
     * 
     * @return
     */
    public boolean isDebugMode()
    {
        if (this.isDebugMode == null)
        {
            this.isDebugMode = getDebugFlag(CLIENT_DEBUG, Boolean.FALSE);
        }
        return this.isDebugMode;
    }
    
    /**
     * <p>Indicates whether the client should operate in collation debug mode. This means that collated dependency
     * resources should include the names of the files that have been collated.</p>  
     * 
     * @return
     */
    public boolean isCollationDebugMode()
    {
        if (this.isCollationDebugMode == null)
        {
            this.isCollationDebugMode = getDebugFlag(CLIENT_COLLATION_DEBUG, Boolean.FALSE);
        }
        return this.isCollationDebugMode;
    }
    
    private Boolean getDebugFlag(String element, Boolean defaultValue)
    {
        Boolean debugValue = defaultValue;
        Map<String, ConfigElement> global = scriptConfigModel.getGlobal();
        if (global != null)
        {
            Object flags = global.get(FLAGS);
            if (flags instanceof GenericConfigElement)
            {
                ConfigElement clientDebugElement = ((GenericConfigElement) flags).getChild(element);
                if (clientDebugElement != null)
                {
                    debugValue = Boolean.valueOf(clientDebugElement.getValue());
                }
            }
        }
        
        return debugValue;
    }
    
    private ServletContext servletContext = null;
    
    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if (applicationContext instanceof WebApplicationContext) 
        {
            this.servletContext =  ((WebApplicationContext) applicationContext).getServletContext();
        }
    }
    
    private ScriptConfigModel scriptConfigModel;
    
    public void setScriptConfigModel(ScriptConfigModel scriptConfigModel)
    {
        this.scriptConfigModel = scriptConfigModel;
    } 
    
    /**
     * This should be set to a list of paths that the {@link DependencyAggregator} should not output error
     * messages about not being able to find. The paths can include RegularExpressionsto support wildcard 
     * matching.
     */
    private List<String> missingFileWarningSuppressionList;
    
    /**
     * Gets the paths to suppress missing file warnings for.
     * 
     * @return A list of paths to suppress warnings for.
     */
    public List<String> getMissingFileWarningSuppressionList()
    {
        return missingFileWarningSuppressionList;
    }

    /**
     * Setter provided for Spring to set the missing file warning suppression list from the configuration.
     * 
     * @param missingFileWarningSuppressionList The list of paths to suppress warnings for.
     */
    public void setMissingFileWarningSuppressionList(List<String> missingFileWarningSuppressionList)
    {
        this.missingFileWarningSuppressionList = missingFileWarningSuppressionList;
    }

    public DependencyAggregator()
    {
    }
    
    public enum CompressionType
    {
        JAVASCRIPT("text/javascript", ".js"), CSS("text/css", ".css");
        private String mimetype = null;
        private String fileExtension = null;
        private CompressionType(String mimetype, String fileExtension)
        {
            this.mimetype = mimetype;
            this.fileExtension = fileExtension;
        }
    }
    
    /**
     * <p>Generates a single compressed JavaScript resource from the supplied list of paths and
     * returns an MD5 checksum value that should be passed to the browser to when requesting
     * the dependencies from the server. The combined compressed source is cached using the MD5
     * checksum as a key.</p>
     *  
     * @param paths A list of paths to compress and combine into a single resource.
     * @return An MD5 checksum that can be used as a key to retrieve the resource from the cache.
     */
    public String generateJavaScriptDependencies(LinkedHashSet<String> paths)
    {
        return generateDependencies(paths, CompressionType.JAVASCRIPT);
    }
    
    /**
     * <p>Generates a single compressed CSS resource from the supplied list of paths and
     * returns an MD5 checksum value that should be passed to the browser to when requesting
     * the dependencies from the server. The combined compressed source is cached using the MD5
     * checksum as a key.</p>
     *  
     * @param paths A list of paths to compress and combine into a single resource.
     * @return An MD5 checksum that can be used as a key to retrieve the resource from the cache.
     */
    public String generateCSSDependencies(LinkedHashSet<String> paths)
    {
        return generateDependencies(paths, CompressionType.CSS);
    }
    
    /**
     * <p>Retrieves, compresses and combines the requested dependencies into a single
     * resource using the supplied compression type and returns an MD5 checksum that can
     * be used to retrieve the resource from the cache.</p>
     * 
     * @param paths A list of the paths to retrieve, compress and combine.
     * @param compressionType
     * @return
     */
    private String generateDependencies(LinkedHashSet<String> paths, CompressionType compressionType)
    {
        String checksum = getCachedChecksumForFileSet(paths);
        if (checksum != null)
        {
            // Re-use the checksum previously generated for this file set...
        }
        else
        {
            boolean cacheByFileSet = true;

            // Iterate over the requested paths and aggregate all the content into a single resource (this
            // will be compressed or uncompressed depending upon the debug mode of the application)...
            StringBuilder aggregatedFileContents = new StringBuilder(10240);
            for (String path: paths)
            {
                try
                {
                    String fileContents = null;
                    if (path.startsWith(INLINE_AGGREGATION_MARKER))
                    {
                        aggregatedFileContents.append("\n/*Path=Inline insert...*/\n\n");
                        fileContents = path.substring(INLINE_AGGREGATION_MARKER.length());
                        aggregatedFileContents.append(fileContents);
                        aggregatedFileContents.append("\n\n");
                        
                        cacheByFileSet = false; // If there is any inline dynamic JavaScipt then we shouldn't use the file set as a cache key
                    }
                    else if (this.dependencyHandler.isDebugMode() && compressionType != CompressionType.CSS) // Temporary check on CSS compression - see commment below
                    {
                        // If we're running in debug mode then we still want to aggregate the requested files but that we
                        // want to aggregate them in their uncompressed format...
                        InputStream in = this.dependencyHandler.getResourceInputStream(path);
                        if (in != null)
                        {
                            fileContents = this.dependencyHandler.convertResourceToString(in);
                            aggregatedFileContents.append("\n/*Path=");
                            aggregatedFileContents.append(path);
                            aggregatedFileContents.append("*/\n\n");
                            
                            // As a temporary workaround (with the potential to become a permanent workaround) the
                            // CSS resources will always be compressed even in debug mode to ensure that the LESS compiler
                            // can run without error. It's less important (no pun intended) to have the CSS uncompressed because
                            // the browser dev tools will display it in a sensible fashion anyway.
//                            if (compressionType == CompressionType.CSS)
//                            {
//                                fileContents = processCssImports(path, fileContents, new HashSet<String>()).toString();
//                                StringBuilder sb = new StringBuilder(fileContents);
//                                adjustImageURLs(path, sb);
//                                fileContents = this.cssThemeHandler.processCssThemes(path, sb);
//                            }
                            
                            aggregatedFileContents.append(fileContents);
                            aggregatedFileContents.append("\n");
                        }
                    }
                    else
                    {
                        // Retrieve and compress the requested file...
                        fileContents = getCompressedFile(path, compressionType);
                        fileContents = processCssImports(path, fileContents, new HashSet<String>()).toString();
                        
                        // For CSS files it's important to adjust URLs to ensure that relative paths are processed
                        // for un-imported CSS file URLs.
                        if (compressionType == CompressionType.CSS)
                        {
                            StringBuilder sb = new StringBuilder(fileContents);
                            adjustImageURLs(path, sb);
                            fileContents = this.cssThemeHandler.processCssThemes(path, sb);
                        }
                        if (fileContents == null)
                        {
                            // The file could not be found, generate an error but don't fail the process.
                            // If a file is requested by a browser that does not exist we would not necessarily
                            // expect the page to fail if it does not truly depend upon that file.
                            if (logger.isErrorEnabled())
                            {
                                logger.error("Could not retrieve path:" + path);
                            }
                        }
                        else
                        {
                            // Append the compressed file to the current combined resource...
                            aggregatedFileContents.append(fileContents);
                        }
                    }
                    
                }
                catch (IOException e)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("An exception occurred compressing: " + path);
                    }
                }
            }
            
            // Generate a checksum from the combined dependencies and add it to the cache...
            String combinedDependencies = aggregatedFileContents.toString();
            checksum = this.dependencyHandler.generateCheckSum(combinedDependencies) + compressionType.fileExtension;
            DependencyResource resource = new DependencyResource(compressionType.mimetype, combinedDependencies);
            cacheDependencyResource(checksum, resource);
            
            if (cacheByFileSet == true)
            {
                cacheChecksumForFileSet(paths, checksum);
            }
        }
        return checksum;
    }
    
    /**
     * <p>This is a map of String Sets to MD5 checksums. It is maintained in memory for the life cycle of the server.
     * It is used to cache requests for paths against MD5 checksums to prevent those checksums being repeatedly 
     * generated. This works on the assumption that file contents cannot be changed while the server is running. 
     * During development and/or migration the server should be shutdown before updating files.</p> 
     */
    private Map<Set<String>, String> fileSetToMD5Map = null;
    private Map<String, String> compressedJSResources = new HashMap<String, String>();
    private Map<String, String> compressedCSSResources = new HashMap<String, String>();
    private Map<String, DependencyResource> combinedDependencyMap = null;
    
    // Locks for accessing caches...
    private ReentrantReadWriteLock fileSetToMD5MapLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock compressedJSResourcesLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock compressedCSSResourcesLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock combinedDependencyMapLock = new ReentrantReadWriteLock();
    
    /**
     * This checks the cache to see if the requested set of files has previously been used to generate
     * an aggregated resource. The supplied fileset is updated to include the current theme id to ensure
     * that theme specific resources are returned (the theme id is removed after the cached is checked).
     * 
     * @param fileSet
     * @return
     */
    public String getCachedChecksumForFileSet(Set<String> fileSet)
    {
        String themeId = ThreadLocalRequestContext.getRequestContext().getThemeId();
        fileSet.add(themeId);
        String checksum = this.getFileSetChecksumCache().get(fileSet);
        fileSet.remove(themeId);
        return checksum;
    }
    
    /**
     * Construct the File Set MD5 Checksum Cache
     * Currently this cache is based on a ConcurrentLinkedHashMap impl - to maintain a maximum size and insert order
     * allowing old items to be ejected in a deterministic pattern.
     * 
     * @return cache Map
     */
    protected Map<Set<String>, String> getFileSetChecksumCache()
    {
        this.fileSetToMD5MapLock.readLock().lock();
        try
        {
            if (this.fileSetToMD5Map == null)
            {
                // upgrade to write lock
                this.fileSetToMD5MapLock.readLock().unlock();
                this.fileSetToMD5MapLock.writeLock().lock();
                try
                {
                    // check again as multiple threads could have been waiting on the write lock
                    if (this.fileSetToMD5Map == null)
                    {
                        this.fileSetToMD5Map = new ConcurrentLinkedHashMap.Builder<Set<String>, String>()
                                 .maximumWeightedCapacity(this.cacheSize)
                                 .concurrencyLevel(32)
                                 .weigher(Weighers.singleton())
                                 .build();
                    }
                }
                finally
                {
                    this.fileSetToMD5MapLock.readLock().lock();
                    this.fileSetToMD5MapLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.fileSetToMD5MapLock.readLock().unlock();
        }
        return this.fileSetToMD5Map;
    }
    
    /**
     * Caches a generated aggregated resource checksum against the fileset that it was
     * generated against. The current theme id is added to the fileset to ensure that
     * resources are cached per theme.
     * 
     * @param fileSet
     * @param checksum
     */
    protected void cacheChecksumForFileSet(Set<String> fileSet, String checksum)
    {
        String themeId = ThreadLocalRequestContext.getRequestContext().getThemeId();
        fileSet.add(themeId);
        this.getFileSetChecksumCache().put(fileSet, checksum);
    }
    
    public String getCachedCompressedJSResource(String path)
    {
        String checksum = null;
        this.compressedJSResourcesLock.readLock().lock();
        try
        {
            checksum = this.compressedJSResources.get(path);
        }
        finally
        {
            this.compressedJSResourcesLock.readLock().unlock();
        }
        return checksum;
    }
    
    protected void cacheCompressedJSResource(String path, String content)
    {
        this.compressedJSResourcesLock.writeLock().lock();
        try
        {
            this.compressedJSResources.put(path, content);
        }
        finally
        {
            this.compressedJSResourcesLock.writeLock().unlock();
        }
    }
    
    /**
     * Attempts to retrieve a previously cached CSS resource. Each CSS resource is cached using the
     * current theme ID as a prefix. This is done so that the same CSS resource is not used when
     * switching themes.
     * 
     * @param path
     * @return
     */
    public String getCachedCompressedCssResource(String path)
    {
        String content = null;
        String prefix = ThreadLocalRequestContext.getRequestContext().getThemeId();
        this.compressedCSSResourcesLock.readLock().lock();
        try
        {
            content = this.compressedCSSResources.get(prefix + path);
        }
        finally
        {
            this.compressedCSSResourcesLock.readLock().unlock();
        }
        return content;
    }
    
    /**
     * Caches the supplied CSS resource using a combination of the current Theme ID with
     * the CSS source path. 
     * @param path
     * @param content
     */
    protected void cacheCompressedCssResource(String path, String content)
    {
        String prefix = ThreadLocalRequestContext.getRequestContext().getThemeId();
        this.compressedCSSResourcesLock.writeLock().lock();
        try
        {
            this.compressedCSSResources.put(prefix + path, content);
        }
        finally
        {
            this.compressedCSSResourcesLock.writeLock().unlock();
        }
    }
    
    /**
     * Attempts to retrieve a previously stored aggregated resource that has been 
     * mapped to a specific checksum. 
     * 
     * @param checksum The checksum to look in the cache for
     * @return The previously cached {@link DependencyResource} or null if not cached. 
     */
    public DependencyResource getCachedDependencyResource(String checksum)
    {
        DependencyResource dependencyResource = this.getCombinedDependencyCache().get(checksum);
        return dependencyResource;
    }
    
    /**
     * Retrieves the the cached map of checksums to aggregated resources (and creates 
     * a new map if one doesn't already exist)
     * 
     * @return
     */
    protected Map<String, DependencyResource> getCombinedDependencyCache()
    {
        this.combinedDependencyMapLock.readLock().lock();
        try
        {
            if (this.combinedDependencyMap == null)
            {
                this.combinedDependencyMapLock.readLock().unlock();
                this.combinedDependencyMapLock.writeLock().lock();
                try
                {
                    if (this.combinedDependencyMap == null)
                    {
                        this.combinedDependencyMap = new ConcurrentLinkedHashMap.Builder<String, DependencyResource>()
                                .maximumWeightedCapacity(this.cacheSize)
                                .concurrencyLevel(32)
                                .weigher(Weighers.singleton())
                                .build();
                    }
                }
                finally
                {
                    this.combinedDependencyMapLock.readLock().lock();
                    this.combinedDependencyMapLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.combinedDependencyMapLock.readLock().unlock();
        }
        return this.combinedDependencyMap;
    }
    
    protected void cacheDependencyResource(String checksum, DependencyResource content)
    {
        this.getCombinedDependencyCache().put(checksum, content);
    }
    
    public void clearCaches()
    {
        this.fileSetToMD5MapLock.writeLock().lock();
        try
        {
            // clear the reference for this cache will force a rebuild
            this.fileSetToMD5Map = null;
        }
        finally
        {
            this.fileSetToMD5MapLock.writeLock().unlock();
        }
        this.compressedJSResourcesLock.writeLock().lock();
        try
        {
            this.compressedJSResources.clear();
        }
        finally
        {
            this.compressedJSResourcesLock.writeLock().unlock();
        }
        this.compressedCSSResourcesLock.writeLock().lock();
        try
        {
            this.compressedCSSResources.clear();
        }
        finally
        {
            this.compressedCSSResourcesLock.writeLock().unlock();
        }
        this.combinedDependencyMapLock.writeLock().lock();
        try
        {
            this.combinedDependencyMap = null;
        }
        finally
        {
            this.combinedDependencyMapLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>This method is used to ensure all image URL are correct when CSS files are aggregated together. It does this by 
     * converting relative paths to absolute paths to avoid issues where a relative path becomes invalid following aggregation
     * of the CSS file.</p>
     * @param cssPath The path of the CSS file being aggregated.
     * @param cssContents The contents of the CSS file being aggregated.
     * @throws IOException
     */
    public void adjustImageURLs(String cssPath, StringBuilder cssContents) throws IOException
    {
        String pathPrefix = "";
        int lastForwardSlash = cssPath.lastIndexOf(CssImageDataHandler.FORWARD_SLASH);
        if (lastForwardSlash != -1)
        {
            pathPrefix = cssPath.substring(0, lastForwardSlash);
        }
        else
        {
            // No action required.
        }
        int index = cssContents.indexOf(CssImageDataHandler.URL_OPEN_TARGET_PATTERN);
        while (index != -1)
        {
            int matchingClose = cssContents.indexOf(CssImageDataHandler.URL_CLOSE_TARGET_PATTERN, index + CssImageDataHandler.URL_OPEN_TARGET_PATTERN.length());
            if (matchingClose == -1)
            {
                // This would be a CSS error!
                return;
            }
            else
            {
                // Get the image source and trim any white space...
                String imageSrc = cssContents.substring(index + CssImageDataHandler.URL_OPEN_TARGET_PATTERN.length(), matchingClose).trim();
                
                // Remove opening and closing quotes...
                if (imageSrc.startsWith(CssImageDataHandler.DOUBLE_QUOTES) || imageSrc.startsWith(CssImageDataHandler.SINGLE_QUOTE))
                {
                    imageSrc = imageSrc.substring(1);
                }
                if (imageSrc.endsWith(CssImageDataHandler.DOUBLE_QUOTES) || imageSrc.endsWith(CssImageDataHandler.SINGLE_QUOTE))
                {
                    imageSrc = imageSrc.substring(0, imageSrc.length() -1);
                }
                
                if (imageSrc.startsWith(CssImageDataHandler.DATA_PREFIX_PART1) ||
                    imageSrc.toLowerCase().startsWith("http://") || 
                    imageSrc.startsWith(CssImageDataHandler.FORWARD_SLASH))
                {
                    // If the image is data encoded then we just need to move to the index along to the end of the pattern...
                    index = cssContents.indexOf(CssImageDataHandler.URL_OPEN_TARGET_PATTERN, matchingClose);
                }
                else
                {
                    if (imageSrc.startsWith(CssImageDataHandler.FULL_STOP) && !imageSrc.startsWith(CssImageDataHandler.DOUBLE_FULL_STOP))
                    {
                        // The image  source starts with either a single full stop (to indicate relativity to the CSS file) and NOT a double
                        // full stop (to indicate the parent folder of the CSS file) so we need to append this value to the CSS path...
                        imageSrc = imageSrc.substring(1);
                        imageSrc = pathPrefix + imageSrc;
                    }
                    else if (!imageSrc.startsWith(CssImageDataHandler.DOUBLE_FULL_STOP))
                    {
                        // The image source doesn't start with a single full stop, a forward slash or a double full stop so it is assumed
                        // relative to the CSS file location...
                        imageSrc = pathPrefix + CssImageDataHandler.FORWARD_SLASH + imageSrc;
                    }
                    else
                    {
                        String tmp = pathPrefix;
                        while (imageSrc.startsWith(CssImageDataHandler.DOUBLE_FULL_STOP))
                        {
                            imageSrc = imageSrc.substring(3);
                            int lastSlashIndex = tmp.lastIndexOf(CssImageDataHandler.FORWARD_SLASH);
                            if (lastSlashIndex != -1)
                            {
                                tmp = tmp.substring(0, lastSlashIndex);
                            }
                            else
                            {
                                tmp = "";
                            }
                        }
                        
                        if (!tmp.endsWith(CssImageDataHandler.FORWARD_SLASH) && !imageSrc.startsWith(CssImageDataHandler.FORWARD_SLASH))
                        {
                            imageSrc = tmp + CssImageDataHandler.FORWARD_SLASH + imageSrc;
                        }
                        else
                        {
                            imageSrc = tmp + imageSrc;
                        }
                    }
                    
                    
                    if (imageSrc.startsWith(CssImageDataHandler.FORWARD_SLASH))
                    {
                        imageSrc = imageSrc.substring(1);
                    }
                    // Make the path absolute...
                    String prefix = getServletContext().getContextPath() + this.dependencyHandler.getResourceControllerMapping() + CssImageDataHandler.FORWARD_SLASH;
                    imageSrc = prefix + imageSrc;
                    
                    int offset = index + CssImageDataHandler.URL_OPEN_TARGET_PATTERN.length();
                    cssContents.delete(offset, matchingClose);               // Delete the original URL
                    offset = cssImageDataHandler.insert(cssContents, offset, imageSrc); // Add new URL...
                    index = cssContents.indexOf(CssImageDataHandler.URL_OPEN_TARGET_PATTERN, offset);
                }
            }
        }
    }
    
    public static char SINGLE_QUOTE = new Character('\'');
    public static char DOUBLE_QUOTE = new Character('\"');
    
//    public static final Pattern p = Pattern.compile("(@import[\\s\\t]*url[\\s\\t]*\\((.*?)\\)[\\s\\t]*;)");
    public static final Pattern p = Pattern.compile("(@import[\\s\\t]*url[\\s\\t]*\\((.*?)\\))");
    
    /**
     * When aggregating CSS files together its important to process any import statements that are included. The rules of CSS imports
     * are that they must occur before anything else in the CSS file (even comments!) so we need to make sure that all imports are
     * expanded to become the imported file contents
     * 
     * TODO: It would be worth looking into caching processed CSS files.
     * 
     * @param fileContents
     * @return
     */
    public StringBuffer processCssImports(String cssPath, String fileContents, Set<String> processedPaths)
    {
        // This will hold the updated contents...
        StringBuffer s = new StringBuffer(1024);
        
        // This pattern matches all the CSS imports...
        if (fileContents != null)
        {
            Matcher m = p.matcher(fileContents);
            while (m.find())
            {
                if (m.group(2) != null)
                {
                    StringBuilder path = new StringBuilder(m.group(2).trim());
                    if (path.charAt(0) == SINGLE_QUOTE || path.charAt(0) == DOUBLE_QUOTE)
                    {
                        path.deleteCharAt(0);
                    }
                    char lastChar = path.charAt(path.length()-1);
                    if (lastChar == SINGLE_QUOTE || lastChar == DOUBLE_QUOTE)
                    {
                        path.deleteCharAt(path.length()-1);
                    }
                    
                    String importContents = null;
                    try
                    {
                        if (this.isDebugMode())
                        {
                            // Process the CSS import using its uncompressed contents (DEBUG MODE)
                            String importPath = this.dependencyHandler.getRelativePath(cssPath, path.toString());
                            InputStream in = this.dependencyHandler.getResourceInputStream(importPath);
                            if (in != null)
                            {
                                importContents = this.dependencyHandler.convertResourceToString(in);
                                importContents = processCssImport(importContents, cssPath, importPath, processedPaths);
                            }
                        }
                        else
                        {
                            // Process the CSS import using its compressed contents (PRODUCTION MODE)
                            String importPath = this.dependencyHandler.getRelativePath(cssPath, path.toString());
                            importContents = this.getCompressedFile(importPath, CompressionType.CSS);
                            importContents = processCssImport(importContents, cssPath, importPath, processedPaths);
                        }
                    }
                    catch (IOException e)
                    {
                        // If there's an exception then don't worry - we just won't replace the contents...
                    }
                    
                    if (importContents != null)
                    {
                        m.appendReplacement(s, importContents);
                    }
                }
                
            }
            
            // Append the remainder of the file.
            m.appendTail(s);
        }
        
        return s;
    }
    
    /**
     * <p>Processes a single CSS import request. This method ensures that infinite loops do not occur. The functionality
     * has been abstracted to a separate method so that it can be called when either in debug or production modes.</p>
     * 
     * @param importContents The current contents of the CSS file being processed.
     * @param cssPath The current path of the CSS file being processed.
     * @param importPath The path requested to be imported within the current CSS file being processed.
     * @param processedPaths A {@link Set} of the paths already processed.
     * @return The processed CSS contents
     * @throws IOException
     */
    protected String processCssImport(String importContents, String cssPath, String importPath, Set<String> processedPaths) throws IOException
    {
        StringBuilder s1 = new StringBuilder();
        
        // Every CSS file could import others - it is therefore important that we recursively process them all...
        if (importPath.equals(cssPath) || processedPaths.contains(importPath))
        {
            // The path to be processed is either the current path or has already been processed - either way this would be
            // the start of an infinite loop and needs to be avoided so don't recurse!!
            s1.append(importContents);
        }
        else
        {
            // Recursively process the requested CSS import...
            processedPaths.add(importPath);
            s1.append(processCssImports(importPath, importContents, processedPaths));
        }
        adjustImageURLs(importPath, s1);
        importContents = s1.toString();
        return importContents;
    }
    
    
    /**
     * Returns the compressed version of the 
     * @param path
     * @return
     * @throws IOException
     */
    String getCompressedFile(String path, CompressionType type) throws IOException
    {
        // Check the compression exclusions to ensure that we really want to compress the file...
        
        String compressedFile = null;
        if (type == CompressionType.JAVASCRIPT)
        {
            compressedFile = getCachedCompressedJSResource(path);
        }
        else if (type == CompressionType.CSS)
        {
            compressedFile = getCachedCompressedCssResource(path);
        }
        if (compressedFile == null)
        {
            if (excludeFileFromCompression(path))
            {
                InputStream in = this.dependencyHandler.getResourceInputStream(path);
                if (in != null)
                {
                    compressedFile = this.dependencyHandler.convertResourceToString(in);
                    if (type == CompressionType.JAVASCRIPT)
                    {
                        cacheCompressedJSResource(path, compressedFile);
                    }
                    else if (type == CompressionType.CSS)
                    {
                        StringBuilder source = new StringBuilder(compressedFile);
                        this.cssImageDataHandler.processCssImages(path, source);
                        compressedFile = this.cssThemeHandler.processCssThemes(path, source);
                        cacheCompressedCssResource(path, compressedFile);
                    }
                }
            }
            else
            {
                // The file hasn't previously been compressed and isn't excluded from compression, let's compress it now...
                InputStream in = this.dependencyHandler.getResourceInputStream(path);
                if (in == null)
                {
                    boolean outputError = true;
                    for (String pathToSuppress: this.missingFileWarningSuppressionList)
                    {
                        if (path.matches(pathToSuppress))
                        {
                            outputError = false;
                            break;
                        }
                    }
                    
                    // We couldn't find the resource - generate an error...
                    if (outputError && logger.isErrorEnabled())
                    {
                        logger.error("Could not find compressed file: " + path);
                    }
                }
                else 
                {
                    try
                    {
                        // Compress the file based on the requested compression type...
                        if (type == CompressionType.JAVASCRIPT)
                        {
                            Reader reader = new InputStreamReader(in, "UTF-8");
                            compressedFile = compressJavaScript(reader);
                            cacheCompressedJSResource(path, compressedFile);
                        }
                        else if (type == CompressionType.CSS)
                        {
                            compressedFile = compressCSSFile(in);
                            
                            StringBuilder source = new StringBuilder(compressedFile);
                            this.cssImageDataHandler.processCssImages(path, source);
                            compressedFile = source.toString();
                            cacheCompressedCssResource(path, compressedFile);
                        }
                    }
                    catch (EvaluatorException e)
                    {
                        // An exception occurred compressing the file. 
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("The file: \"" + path + "\" could not be compressed due to the following error: ", e);
                        }
                        
                        // Generate a String of the uncompressed file...
                        compressedFile = IOUtils.toString(in, this.charset);
                        if (type == CompressionType.JAVASCRIPT)
                        {
                            cacheCompressedJSResource(path, compressedFile);
                        }
                        else if (type == CompressionType.CSS)
                        {
                            cacheCompressedCssResource(path, compressedFile);
                        }
                    }
                }
            }
        }
        
        return compressedFile;
    }

    /**
     * <p>Compresses the JavaScript file provided by the supplied {@link InputStream} using the YUI {@link JavaScriptCompressor}.</p>
     * 
     * @param in
     * @return A String representation of the compressed JavaScript file
     * @throws IOException
     */
    public String compressJavaScript(Reader reader) throws IOException
    {
        String compressedFile = null;
        StringWriter out = new StringWriter();

        // This form of debug mode is debugging how JS/CSS files are concatenated together. 
        if (isCollationDebugMode())
        {
           try
           {
               char[] buffer = new char[1024];
               try 
               {
                  int n;
                  while ((n = reader.read(buffer)) != -1) 
                  {
                      out.write(buffer, 0, n);
                  }
               }
               finally 
               {
                  reader.close();
               }
           }
           catch (Exception e)
           {
              logger.error("Compression error: ", e);
           }
        } 
        else
        {
            JavaScriptCompressor jsc = new JavaScriptCompressor(reader, new YuiCompressorErrorReporter());
            reader.close();
            jsc.compress(out, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
        }
        compressedFile = out.toString();
        return compressedFile;
    }
    
    /**
     * <p>Compresses the CSS file provided by the supplied {@link InputStream} using the YUI {@link CssCompressor}.</p>
     * 
     * @param in
     * @return A String representation of the compressed CSS file
     * @throws IOException
     */
    public String compressCSSFile(InputStream in) throws IOException
    {
        String compressedFile = null;
        Reader reader = new InputStreamReader(in, "UTF-8");
        CssCompressor cssc = new CssCompressor(reader);
        reader.close();
        in.close();
        StringWriter out = new StringWriter();
        cssc.compress(out, linebreak);
        compressedFile = out.toString();
        return compressedFile;
    }
    
    /**
     * Checks to see whether or not the path meets any of the compression exclusion criteria. This
     * can be used to prevent attempts to re-compress already compressed files or prevent attempts
     * to compress files that are known to fail under compression.  
     * 
     * @param path The path to check against the filters
     * @return <code>true</code> if the path should be excluded and <code>false</code> otherwise.
     */
    public boolean excludeFileFromCompression(String path)
    {
        boolean exclude = false;
        
        for (Pattern p: this.compressionExclusionPatterns)
        {
            Matcher m = p.matcher(path);
            if (m.matches())
            {
                exclude = true;
                break;
            }
        }
        
        return exclude;
    }
    
    
    /* ****************************************************
     *                                                    *
     * SPRING BEAN SETTERS FOR CONFIGURING YUI COMPRESSOR *
     *                                                    *
     ******************************************************/
    
    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    public void setLinebreak(int linebreak)
    {
        this.linebreak = linebreak;
    }

    public void setMunge(boolean munge)
    {
        this.munge = munge;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public void setPreserveAllSemiColons(boolean preserveAllSemiColons)
    {
        this.preserveAllSemiColons = preserveAllSemiColons;
    }

    public void setDisableOptimizations(boolean disableOptimizations)
    {
        this.disableOptimizations = disableOptimizations;
    }
    
    /* *********************************************************
     *                                                         *
     * PRIVATE INNER CLASS FOR HANDLING YUI COMPRESSION ERRORS *
     *                                                         *
     ***********************************************************/
    
    private static class YuiCompressorErrorReporter implements ErrorReporter
    {
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset)
        {
            if (line < 0)
            {
                logger.warn(message);
            }
            else
            {
                logger.warn(line + ':' + lineOffset + ':' + message);
            }
        }

        public void error(String message, String sourceName, int line, String lineSource, int lineOffset)
        {
            if (line < 0)
            {
                logger.error(message);
            }
            else
            {
                logger.error(line + ':' + lineOffset + ':' + message);
            }
        }

        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
                int lineOffset)
        {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
}
