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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.element.GenericConfigElement;
import org.springframework.extensions.directives.DirectiveConstants;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.extensions.webscripts.servlet.mvc.ResourceController;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ServletContextResource;

/**
 * <p>This class should be instantiated as a Spring bean that provides a service for locating dependency resources and
 * generating checksums against their contents. It caches the location of the resources (which can be loaded from 
 * multiple different sources) to speed up their retrieval. Its primary purpose is to ensure that when resource 
 * requests are made that the URL contains a content based checksum to ensure that browsers do not use stale cached
 * versions. This problem is solved because as the content of the file changes the checksum will change and as
 * such the browser will not determine that it already has a cached copy of the resource. Because checksums are cached
 * against resources this class does not support hot-deploy of new resources.</p> 
 * 
 * @author David Draper
 */
public class DependencyHandler implements ApplicationContextAware
{
    public static final String CSS = ".css";

    private static final Log logger = LogFactory.getLog(DependencyHandler.class);
        
    /**
     * <p>The character set used to read in dependency resource files (e.g "UTF-8"). This variable should be set through
     * the Spring application context.</p>
     */
    private String charset;
    
    /**
     * <p>Sets the character set to use when reading dependency resource files into memory.</p>
     * @param charset The character set to use.
     */
    public void setCharset(String charset)
    {
        this.charset = charset;
    }
    
    /**
     * @return the character set to use when processing dependency resource files in memory.
     */
    public String getCharset()
    {
        return this.charset;
    }

    /**
     * <p>The digest to use when generating a checksum from dependency resource file contents (e.g. "MD5").
     * This variable should be set through the Spring application context.</p>
     */
    private String digest;
    
    /**
     * <p>Sets the digest to use when generating checksums for dependency resource files</p>
     * @param digest The digest to use.
     */
    public void setDigest(String digest)
    {
        this.digest = digest;
    }

    /**
     * <p>The {@link ApplicationContext} is required when retrieving dependency resource files from the 
     * Classpath. This variable is automatically set by the Spring framework through the implementation of the
     * {@link ApplicationContextAware} interface.<p>
     */
    private ApplicationContext applicationContext = null;
    
    /**
     * <p>This method will be called by the Spring framework when the class is first instantiated. It both
     * sets the {@link ApplicationContext} and also sets a {@link ServletContext} providing that the 
     * {@link ApplicationContext} is an instance of a {@link WebApplicationContext} (which it should be).</p>
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
        if (applicationContext instanceof WebApplicationContext) 
        {
            this.servletContext =  ((WebApplicationContext) applicationContext).getServletContext();
        }
    }

    /**
     * <p>The {@link ServletContext} is required to retrieve dependency resource files from a path within
     * the web application. This variable will be set at class instantiation providing that the 
     * {@link ApplicationContext} is an instance of a {@link WebApplicationContext} (which it always
     * should be for a Surf application).
     */
    private ServletContext servletContext = null;
    
    /**
     * <p>Returns the current {@link ServletContext} for the Spring application.</p>
     * 
     * @param scriptConfigModel
     */
    public ServletContext getServletContext()
    {
        return servletContext;
    }

    /**
     * <p>A {@link ScriptConfigModel} is required to determine whether Surf is running in production or debug
     * mode. This information is required to determine whether or not to retrieve the minified version of the
     * requested resource or not. This variable should be set through the Spring application context.</p>
     */
    private ScriptConfigModel scriptConfigModel;
    
    /**
     * <p>Returns the current {@link ScriptConfigModel}.
     * 
     * @param scriptConfigModel
     */
    public void setScriptConfigModel(ScriptConfigModel scriptConfigModel)
    {
        this.scriptConfigModel = scriptConfigModel;
    }
    
    private CssImageDataHandler cssDataImageHandler;
    
    public void setCssDataImageHandler(CssImageDataHandler cssDataImageHandler)
    {
        this.cssDataImageHandler = cssDataImageHandler;
    }

    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    /**
     * <p>This should get set by the Spring application context configuration to the mapping of the 
     * resource controller.</p>
     */
    private String resourceControllerMapping = "";
    
    /**
     * @return The mapping to the resource controller.
     */
    public String getResourceControllerMapping()
    {
        return resourceControllerMapping;
    }

    /**
     * <p>Sets the resource controller mapping.
     * @param resourceControllerMapping
     */
    public void setResourceControllerMapping(String resourceControllerMapping)
    {
        this.resourceControllerMapping = resourceControllerMapping;
    }

    /**
     * The {@link RemoteResourcesHandler} is used to retrieve resources from a remote location. This will
     * be the first location checked for a resource.
     */
    private RemoteResourcesHandler remoteResourcesHandler;
    
    public RemoteResourcesHandler getRemoteResourcesHandler()
    {
        return remoteResourcesHandler;
    }

    public void setRemoteResourcesHandler(RemoteResourcesHandler remoteResourcesHandler)
    {
        this.remoteResourcesHandler = remoteResourcesHandler;
    }

    /**
     * <p>A map of the requested resources to the checksum generated from their contents. This cache works on
     * the assumption that dynamic updating of resources (whilst the server is running) is not supported, i.e.
     * the cache will only get refreshed on server restart.</p>
     */
    private final Map<String, String> cachedChecksumPaths = new HashMap<String, String>(32);
    private final Map<String, String> cachedChecksums = new HashMap<String, String>(32);
    
    /**
     * <p>This lock allows optimum performance when reading from and writing to the cache.</p>
     */
    private final ReadWriteLock checksumPathsLock = new ReentrantReadWriteLock();
    private final ReadWriteLock checksumsLock = new ReentrantReadWriteLock();
    
    private Boolean isDebugMode = null;
    
    public static final String FLAGS = "flags";
    public static final String CLIENT_DEBUG = "client-debug";
    
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
    
    /**
     * <p>A list of suffices for debug mode to apply to requested paths when looking for resources. This list allows
     * an application to compensate for the variations in file name for debug and production modes. This variable
     * should be set through the Spring application context.</p>
     */
    private List<String> debugSuffices;
    
    public void setDebugSuffices(List<String> debugSuffices)
    {
        this.debugSuffices = debugSuffices;
    }

    /**
     * <p>A list of suffices for production mode to apply to requested paths when looking for resources. This list allows
     * an application to compensate for the variations in file name for debug and production modes. This variable
     * should be set through the Spring application context.</p>
     */
    private List<String> productionSuffices;
    
    public void setProductionSuffices(List<String> productionSuffices)
    {
        this.productionSuffices = productionSuffices;
    }

    /**
     * <p>Generates a list of paths to attempt to retrieve for the supplied path. The paths generated are
     * dependent upon the current mode that Surf is running in (i.e. "debug" or "production") and the 
     * list of suffices defined for that mode. The suffices are defined in the Spring application context</p>
     *  
     * @param path The path to generate new paths from.
     * @return A {@link List} of paths generated from the supplied path and the list of suffices defined
     * for the current Surf mode.
     */
    public List<String> generatePathsForClientMode(String path)
    {
        List<String> paths = null;
        if (this.isDebugMode() && this.debugSuffices != null)
        {
            paths = generatePathForSuffices(path, this.debugSuffices);
        }
        else if (!this.isDebugMode() && this.productionSuffices != null)
        {
            paths = generatePathForSuffices(path, this.productionSuffices);
        }
        
        // If paths is still null then no suffices were defined for the current mode
        // (or the path supplied does not contain a fullstop making it impossible to add
        // a suffix) so we should just return a List containing the supplied path...
        if (paths == null)
        {
            paths = new ArrayList<String>(1);
            paths.add(path);
        }
        
        return paths;
    }
    
    /**
     * <p>Generates a {@link List} of paths that are generated from the supplied path and a list
     * of suffices to apply to that supplied path. For example, given the list of suffices 
     * <code>["-min", "-minified", ""]</code> and the path "test.js" the list of paths returned will be 
     * <code>["test-min.js", "test-minified.js", "test.js"]</code></p>
     * @param path The path to generate new paths from
     * @param suffices The suffices to apply to the supplied path
     * @return A {@link List} of paths generated from the supplied path and list of suffices
     */
    private List<String> generatePathForSuffices(String path, List<String> suffices)
    {
        List<String> paths = null;
        
        // Check that the path contains a fullstop as this is the location at which we
        // need to insert each suffix...
        int lastDotIndex = path.lastIndexOf(".");
        if (lastDotIndex != -1)
        {
            paths = new ArrayList<String>(suffices.size());
            for (String suffix: suffices)
            {
                String generatedPath = path.substring(0, lastDotIndex) + suffix + path.substring(lastDotIndex);
                paths.add(generatedPath);
            }
        }
        return paths;
    }
    
    /**
     * <p>A map of the requested resources to the checksum generated from their contents. This cache works on
     * the assumption that dynamic updating of resources (whilst the server is running) is not supported, i.e.
     * the cache will only get refreshed on server restart.</p>
     */
    private final Map<String, ResourceInfo> cachedResourceInfoMap = new HashMap<String, ResourceInfo>();
    
    /**
     * <p>This lock allows optimum performance when reading from and writing to the cache.</p>
     */
    private final ReadWriteLock resourceInfoLock = new ReentrantReadWriteLock();
    
    /**
     * <p>Checks the cache to see if a {@link ResourceInfo} object has been previously generated for the 
     * supplied path. It will <b>not</b> generate the {@link ResourceInfo} object if it does not exist.</p>
     * 
     * @param path The path to check the cache for.
     * @return A previously generated {@link ResourceInfo} object or <code>null</code> if one could not be found.
     */
    protected ResourceInfo getCachedResourceInfo(String path)
    {
        ResourceInfo resource = null;
        this.resourceInfoLock.readLock().lock();
        try
        {
            resource = this.cachedResourceInfoMap.get(path);
        }
        finally
        {
            this.resourceInfoLock.readLock().unlock();
        }
        return resource;
    }
    
    /**
     * Return the state of a given path in the resource cache.
     * 
     * @param path  to test for existence in the cache
     * @return true if a resource exists in the cache for the given path.
     */
    public boolean resourceInCache(final String path)
    {
        final ResourceInfo resource = getCachedResourceInfo(path);
        return !(resource == null || resource == getResourceInfoSentinel());
    }
    
    /**
     * <p>Returns the {@link InputStream} for the supplied path but will load the {@link InputStream} directly
     * rather than using the value stored in the {@link ResourceInfo} as this may potentially have been modified.</p>
     * @param path The path to load the resource from.
     * @return An {@link InputStream} for the requested path and <code>null</code> if one cannot be found.
     * @throws IOException
     */
    public InputStream getUnmodifiedResourceInputStream(String path) throws IOException
    {
        InputStream in = getResourceInputStream(path); // Calling this ensures that the ResourceInfo is cached...
        if (in != null)
        {
            // Get the cached ResourceInfo and load the InputStream - this ensures that
            // we get the unmodified InputStream and not the version stored as part of the
            // ResourceInfo that might potentially have been modified.
            ResourceInfo resourceInfo = getCachedResourceInfo(path);
            in = resourceInfo.loadInputStream();
        }
        return in;
    }
    
    /**
     * <p>Creates an {@link InputStream} to the requested path. This will look in both the
     * defined classpath and within the META-INF of the application.</p>
     * 
     * @param path The path to generate an {@link InputStream} to.
     * @return The {@link InputStream} for the requested resource and <code>null</code> if the
     * resource could not be found.
     * @throws IOException Thrown when an error occurs attempting to generate an {@link InputStream}
     */
    public InputStream getResourceInputStream(String path) throws IOException
    {
        InputStream in = null;
        
        // TODO: Check the sentinel isn't returned...
        ResourceInfo resourceInfo = getCachedResourceInfo(path);
        if (resourceInfo == getResourceInfoSentinel())
        {
            // No action required - the ResourceInfoSentinel indicates that we've previously searched
            // for this path and the resource couldn't be found.
        }
        else if (resourceInfo != null)
        {
            in = resourceInfo.getInputStream();
        }
        else
        {
            // Generate a list of paths that should be checked for the current mode...
            List<String> pathsToCheck = generatePathsForClientMode(path);
            Iterator<String> paths;
            if (this.webFrameworkConfigElement.isRemoteResourceResolvingEnabled())
            {
                paths = pathsToCheck.iterator();
                while (in == null && paths.hasNext())
                {
                    String currPath = paths.next();
                    in = this.remoteResourcesHandler.getRemoteResource(currPath);
                    if (in != null)
                    {
                        addResourceInfoToCache(path, new RemoteResource(currPath));
                        in = this.getCachedResourceInfo(path).getInputStream();
                    }
                }
            }
            
            paths = pathsToCheck.iterator();
            while (in == null && paths.hasNext())
            {
                String currPath = paths.next();
                Resource r = applicationContext.getResource("classpath*:" + currPath);
                if (r != null && r.exists())
                {
                    addResourceInfoToCache(path, new ApplicationContextResource(currPath));
                    in = this.getCachedResourceInfo(path).getInputStream();
                }
            }
            
            paths = pathsToCheck.iterator();
            while (in == null && paths.hasNext())
            {
                String currPath = paths.next();
                if (currPath.startsWith("/"))
                {
                    currPath = currPath.substring(1);
                }
                URL resourceUrl = ClassUtils.getDefaultClassLoader().getResource("META-INF/" + currPath);
                if (resourceUrl != null)
                {
                    addResourceInfoToCache(path, new ClassLoaderResource(currPath));
                    in = this.getCachedResourceInfo(path).getInputStream();
                }
            }
            
            paths = pathsToCheck.iterator();
            while (this.servletContext != null && in == null && paths.hasNext())
            {
                String p = paths.next();
                String tmp = p;
                if (!tmp.startsWith("/"))
                {
                    tmp = "/" + tmp;
                }
                ServletContextResource resource = new ServletContextResource(this.servletContext, tmp);
                {
                    if (resource.exists())
                    {
                        addResourceInfoToCache(path, new ServletContextRes(p));
                        in = this.getCachedResourceInfo(path).getInputStream();
                    }
                }
            }
        }
        
        // If the input stream is still null, add a sentinel...
        if (in == null)
        {
            addResourceInfoToCache(path, getResourceInfoSentinel());
        }
        return in;
    }
    
    /**
     * <p>A very basic method of generating a path including the checksum. This basically concatenates the both
     * String arguments together (separated by an underscore). Although this method is incredibly simple the function
     * has been abstracted into a separate method to support overwriting.</p>
     *  
     * @param path The path of the file
     * @param checksum The checksum generated from the file contents.
     * @return A new path consisting of the combination of the path and checksum.
     */
    public static String generateCheckSumPath(final String path, final String checksum)
    {
        String checksumPath;
        if (checksum != null && checksum.length() == 0)
        {
            checksumPath = path;
        }
        else
        {
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex != -1)
            {
                checksumPath = path.substring(0, lastDotIndex) + "_" + checksum + path.substring(lastDotIndex);
            }
            else
            {
                checksumPath = path + "_" + checksum;
            }
        }
        return checksumPath;
    }
    
    /**
     * <p>Adds a new {@link ResourceInfo} object into the local cache. The {@link ResourceInfo} is added against two 
     * different keys - the basic path AND the checksum path. This is so that it can be retrieved by both directives
     * generating new requests to output to HTML and from the {@link ResourceController} attempting to retrieve 
     * the resources specified from the generated requests.</p>
     * 
     * @param path The path of the resource.
     * @param resourceInfo A {@link ResourceInfo} object specifying where to obtain the resource.
     * @throws IOException 
     */
    private void addResourceInfoToCache(String path, ResourceInfo resourceInfo) throws IOException
    {
        String checksum = "";
        if (resourceInfo == getResourceInfoSentinel())
        {
            // No action required
        }
        else
        {
            // Get the InputStream from the ResourceInfo, use it to get the contents of the file from
            // which the checksum can be generated. Generate a new path for resource using the checksum
            // and then add the ResourceInfo object to the cache under both the basic path and the
            // checksum based path...
            checksum = this.lookupChecksumInCache(path);
            if (checksum == null)
            {
                // The checksum hasn't previously been cached, get it now...
                InputStream in = resourceInfo.getInputStream();
                if (in != null)
                {
                    String resourceContents = convertResourceToString(in);
                    if (this.webFrameworkConfigElement.isGenerateCssDataImagesEnabled() && path.toLowerCase().endsWith(CSS))
                    {
                        // If we're generating CSS data images for CSS files then do it now...
                        StringBuilder processedContents = new StringBuilder(resourceContents);
                        this.cssDataImageHandler.processCssImages(path, processedContents);
                        resourceInfo.setContents(processedContents.toString());
                        resourceContents = processedContents.toString(); // Make sure to update the contents so that the checksum is generated appropriately!
                    }
                    checksum = generateCheckSum(resourceContents);
                    this.addChecksumToCache(path, checksum);
                }
            }
        }
        
        // Generate the path and cache the result...
        String checksumPath = generateCheckSumPath(path, checksum);
        addChecksumPathToCache(path, checksumPath);
        this.resourceInfoLock.writeLock().lock();
        try
        {
            this.cachedResourceInfoMap.put(path, resourceInfo);
            this.cachedResourceInfoMap.put(checksumPath, resourceInfo);
        }
        finally
        {
            this.resourceInfoLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>Converts the supplied {@link InputStream} into a String. This has been provided with the intention
     * of facilitating the generation of checksum values from dependency resource file contents.</p>
     * @param in The {@link InputStream} to read into a String.
     * @return A String containing the contents of the supplied {@link InputStream}.
     * @throws IOException Thrown when an error occurs reading from the {@link InputStream}
     */
    public String convertResourceToString(final InputStream in) throws IOException
    {
        String s = null;
        if (in != null) 
        {
            final Writer writer = new StringBuilderWriter();
            final char[] buffer = new char[1024];
            try
            {
                final Reader reader = new BufferedReader(new InputStreamReader(in, this.charset));
                int n;
                while ((n = reader.read(buffer)) != -1) 
                {
                    writer.write(buffer, 0, n);
                }
                s = writer.toString();
            } 
            finally 
            {
                in.close();
            }
        }
        
        return s;
    }
        
    /**
     * <p>Obtains a checksum path for the supplied path by first checking a cache for previously generated 
     * values and failing that will attempt to actually retrieve the resource which will in turn actually
     * generate a checksum path and place it in the cache which is then rechecked.</p>
     * 
     * @param path The path of the resource to attempt to generate a checksum path for
     * @return If the requested resource could be found then this will return a checksum generated against
     * the contents of the file. If the resource could not be found (or an error occurred attempted to read 
     * the resource contents into memory) then this will return <code>null</code>
     */
    public String getChecksumPath(String path)
    {
        String checksumPath = lookupChecksumPathInCache(path);
        
        // If we've not previously generated the checksum then generate one now...
        if (checksumPath == null)
        {
            try
            {
                this.getResourceInputStream(path);
                checksumPath = lookupChecksumPathInCache(path); 
            }
            catch (IOException e)
            {
                logger.error("The following error occurred attempting to obtain a checksum path", e);
            }
        }
        return checksumPath;
    }
    
    /**
     * <p>Obtains a checksum path for the supplied path by first checking a cache for previously generated 
     * values and failing that will attempt to actually retrieve the resource which will in turn actually
     * generate a checksum path and place it in the cache which is then rechecked.</p>
     * 
     * @param path The path of the resource to attempt to generate a checksum path for
     * @return If the requested resource could be found then this will return a checksum generated against
     * the contents of the file. If the resource could not be found (or an error occurred attempted to read 
     * the resource contents into memory) then this will return <code>null</code>
     */
    public String getChecksum(String path)
    {
        String checksum = lookupChecksumInCache(path);
        
        // If we've not previously generated the checksum then generate one now...
        if (checksum == null)
        {
            try
            {
                this.getResourceInputStream(path);
                checksum = lookupChecksumInCache(path); 
            }
            catch (IOException e)
            {
                logger.error("The following error occurred attempting to obtain a checksum path", e);
            }
        }
        return checksum;
    }
    
    /**
     * <p>Attempts to retrieve a previously generated checksum path from the cache.</p>
     * 
     * @param path The path to look up in the cache.
     * @return A previously generated checksum path or <code>null</code> if one has not previously
     * been created.
     */
    protected String lookupChecksumPathInCache(String path)
    {
        // Get a previously generated checksum path...
        String checksumPath = null;
        this.checksumPathsLock.readLock().lock();
        try
        {
            checksumPath = this.cachedChecksumPaths.get(path);
        }
        finally
        {
            this.checksumPathsLock.readLock().unlock();
        }
        return checksumPath;
    }
    
    /**
     * <p>Attempts to retrieve a previously generated checksum from the cache.</p>
     * 
     * @param path The path to look up in the cache.
     * @return A previously generated checksum or <code>null</code> if one has not previously
     * been created.
     */
    protected String lookupChecksumInCache(String path)
    {
        // Get a previously generated checksum path...
        String checksumPath = null;
        this.checksumsLock.readLock().lock();
        try
        {
            checksumPath = this.cachedChecksums.get(path);
        }
        finally
        {
            this.checksumsLock.readLock().unlock();
        }
        return checksumPath;
    }
    
    /**
     * <p>Adds a new generated checksum path to the cache. This method is called when new
     * {@link ResourceInfo} objects are added to their respective cache because in order to
     * do that the checksum path needs to be generated.</p>
     * 
     * @param path The original path requested.
     * @param checksumPath The generated checksum path.
     */
    protected void addChecksumPathToCache(String path, String checksumPath)
    {
        this.checksumPathsLock.writeLock().lock();
        try
        {
            this.cachedChecksumPaths.put(path, checksumPath);
        }
        finally
        {
            this.checksumPathsLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>Adds a new generated checksum to the cache.</p>
     * 
     * @param path The original path requested.
     * @param checksumPath The generated checksum.
     */
    protected void addChecksumToCache(String path, String checksum)
    {
        this.checksumsLock.writeLock().lock();
        try
        {
            this.cachedChecksums.put(path, checksum);
        }
        finally
        {
            this.checksumsLock.writeLock().unlock();
        }
    }
    
    /**
     * Clears the caches stored in the <code>cachedChecksums</code>, <code>cachedResourceInfoMap</code>
     * and <code>cachedChecksumPaths</code> maps. This method has been provided to allow a WebScript
     * to clear the caches of running systems.
     */
    public void clearCaches()
    {
        this.checksumsLock.writeLock().lock();
        try
        {
            this.cachedChecksums.clear();
        }
        finally
        {
            this.checksumsLock.writeLock().unlock();
        }
        this.resourceInfoLock.writeLock().lock();
        try
        {
            this.cachedResourceInfoMap.clear();
        }
        finally
        {
            this.resourceInfoLock.writeLock().unlock();
        }
        this.checksumPathsLock.writeLock().lock();
        try
        {
            this.cachedChecksumPaths.clear();
        }
        finally
        {
            this.checksumPathsLock.writeLock().unlock();
        }
    }
    
    private final static String[] hex = {
        "00", "01", "02", "03", "04", "05", "06", "07",
        "08", "09", "0a", "0b", "0c", "0d", "0e", "0f",
        "10", "11", "12", "13", "14", "15", "16", "17",
        "18", "19", "1a", "1b", "1c", "1d", "1e", "1f",
        "20", "21", "22", "23", "24", "25", "26", "27",
        "28", "29", "2a", "2b", "2c", "2d", "2e", "2f",
        "30", "31", "32", "33", "34", "35", "36", "37",
        "38", "39", "3a", "3b", "3c", "3d", "3e", "3f",
        "40", "41", "42", "43", "44", "45", "46", "47",
        "48", "49", "4a", "4b", "4c", "4d", "4e", "4f",
        "50", "51", "52", "53", "54", "55", "56", "57",
        "58", "59", "5a", "5b", "5c", "5d", "5e", "5f",
        "60", "61", "62", "63", "64", "65", "66", "67",
        "68", "69", "6a", "6b", "6c", "6d", "6e", "6f",
        "70", "71", "72", "73", "74", "75", "76", "77",
        "78", "79", "7a", "7b", "7c", "7d", "7e", "7f",
        "80", "81", "82", "83", "84", "85", "86", "87",
        "88", "89", "8a", "8b", "8c", "8d", "8e", "8f",
        "90", "91", "92", "93", "94", "95", "96", "97",
        "98", "99", "9a", "9b", "9c", "9d", "9e", "9f",
        "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
        "a8", "a9", "aa", "ab", "ac", "ad", "ae", "af",
        "b0", "b1", "b2", "b3", "b4", "b5", "b6", "b7",
        "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf",
        "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7",
        "c8", "c9", "ca", "cb", "cc", "cd", "ce", "cf",
        "d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7",
        "d8", "d9", "da", "db", "dc", "dd", "de", "df",
        "e0", "e1", "e2", "e3", "e4", "e5", "e6", "e7",
        "e8", "e9", "ea", "eb", "ec", "ed", "ee", "ef",
        "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7",
        "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff"
    };
    
    /**
     * <p>Generates an checksum from the supplied source String using a {@link MessageDigest} with
     * the requested algorithm.</p>
     * 
     * @param source A source String to generate the MD4 checksum from.
     * @return An MD5 checksum.
     */
    public String generateCheckSum(final String source)
    {
        final StringBuilder checksum = new StringBuilder(32);
        try
        {
            final byte[] compressedFileInBytes = source.getBytes(this.charset);
            final MessageDigest md = MessageDigest.getInstance(this.digest);
            md.reset();
            final byte[] checksumInBytes = md.digest(compressedFileInBytes);
            
            for (final byte b: checksumInBytes)
            {
                checksum.append(hex[0xFF & b]);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Could not find MD5 algorithm for generating checksum", e);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Unsupported encoding", e);
        }
        return checksum.toString();
    }
    
    /**
     * <p>Gets the path of the supplied path assuming it is relative to the supplied sourcePath.</p>
     * 
     * @param sourcePath
     * @param targetPath
     * @return
     */
    public String getRelativePath(final String sourcePath, String targetPath)
    {
        final StringBuilder sb = new StringBuilder(64);
        String pathPrefix = "";
        if (sourcePath != null)
        {
            int lastForwardSlashIndex = sourcePath.lastIndexOf(CssImageDataHandler.FORWARD_SLASH);
            if (lastForwardSlashIndex != -1)
            {
                pathPrefix = sourcePath.substring(0, lastForwardSlashIndex);
            }
        }
        
        // Remove opening and closing quotes...
        if (targetPath.startsWith(CssImageDataHandler.DOUBLE_QUOTES) || targetPath.startsWith(CssImageDataHandler.SINGLE_QUOTE))
        {
            targetPath = targetPath.substring(1);
        }
        if (targetPath.endsWith(CssImageDataHandler.DOUBLE_QUOTES) || targetPath.endsWith(CssImageDataHandler.SINGLE_QUOTE))
        {
            targetPath = targetPath.substring(0, targetPath.length() -1);
        }
        
        // Clear any pointless current location markers...
        if (targetPath.startsWith(CssImageDataHandler.FULL_STOP) && !targetPath.startsWith(CssImageDataHandler.DOUBLE_FULL_STOP))
        {
            targetPath = targetPath.substring(2);
        }
        
        while (targetPath.startsWith(CssImageDataHandler.DOUBLE_FULL_STOP_SLASH))
        {
            int idx = pathPrefix.lastIndexOf(CssImageDataHandler.FORWARD_SLASH);
            if (idx != -1)
            {
                pathPrefix = pathPrefix.substring(0,idx);
                targetPath = targetPath.substring(3);
            }
            else if (pathPrefix.length() > 0)
            {
                pathPrefix = "";
                targetPath = targetPath.substring(3);
            }
            else
            {
                // We can't do any more - but need to ensure that we don't continue going around the loop infinitely
                break;
            }
        }
        
        if (targetPath.startsWith(CssImageDataHandler.FORWARD_SLASH))
        {
            // Don't apply the prefix to resources that start with a slash
        }
        else if (targetPath.toLowerCase().startsWith(DirectiveConstants.HTTP_PREFIX) || targetPath.toLowerCase().startsWith(DirectiveConstants.HTTPS_PREFIX))
        {
            // Don't apply the prefix for explicitly requested resources
        }
        else
        {
            // Apply the prefix...
            sb.append(pathPrefix);
            sb.append(CssImageDataHandler.FORWARD_SLASH);
        }
        sb.append(targetPath);
        return sb.toString();
    }
    
    /**
     * <p>A protected abstract inner class that is used to allow its parent {@link DependencyHandler} to easily
     * retrieve the {@link InputStream} for their associated resource. This is an inner class so that it has 
     * access to all the instance variables of the {@link DependencyHandler} such as the {@link ApplicationContext}
     * but is protected so that custom {@link DependencyHandler} classes can define new extensions to it.</p>
     *  
     * @author David Draper
     */
    protected abstract class ResourceInfo
    {
        /**
         * <p>The path that identifies the resource.</p>
         */
        protected String path;
        protected ResourceInfo(String path)
        {
            this.path = path;
        }
        /**
         * <p>The contents of the resource. This should be set if the resource should be
         * cached or if the contents are modified after initial load (e.g. converting
         * CSS URLs to data)</p>
         */
        private String contents;
        /**
         * <p>Returns the contents of the resource.</p>
         * @return
         */
        public String getContents()
        {
            return this.contents;
        }
        /**
         * <p>Sets the contents of the resource.</p>
         * @param contents
         */
        private void setContents(String contents)
        {
            this.contents = contents;
        }
        /**
         * <p>Returns the cached contents of the resource if they have been set otherwise
         * defers to the <code>loadInputStream</code> method.
         * @return
         * @throws IOException
         */
        public InputStream getInputStream() throws IOException
        {
            InputStream in = null;
            if (this.contents == null)
            {
                in = loadInputStream();
            }
            else
            {
                in = new ByteArrayInputStream(this.contents.getBytes(charset));
            }
            return in;
        }
        /**
         * <p>Should be implemented to load the {@link InputStream}</p>
         * @return
         * @throws IOException
         */
        public abstract InputStream loadInputStream() throws IOException;
    }

    /**
     * <p>Used to load resources in from the {@link RemoteResourcesHandler}.</p>
     * 
     * @author David Draper
     */
    private class RemoteResource extends ResourceInfo
    {
        private RemoteResource(String path)
        {
            super(path);
        }
        
        public InputStream loadInputStream() throws IOException
        {
            InputStream in = remoteResourcesHandler.getRemoteResource(path);
            return in;
        }
    }
    
    /**
     * <p>Used to load resources in from the {@link ApplicationContext}.</p>
     * 
     * @author David Draper
     */
    private class ApplicationContextResource extends ResourceInfo
    {
        private ApplicationContextResource(String path)
        {
            super(path);
        }
        
        public InputStream loadInputStream() throws IOException
        {
            InputStream in = null;
            Resource r = applicationContext.getResource("classpath*:" + path);
            if (r != null && r.exists())
            {
                in = r.getInputStream();
            }
            return in;
        }
    }
    
    /**
     * <p>Used to load resources from the default {@link ClassLoader}.</p>
     * @author David Draper
     *
     */
    private class ClassLoaderResource extends ResourceInfo
    {
        private ClassLoaderResource(String path)
        {
            super(path);
        }
        
        public InputStream loadInputStream() throws IOException
        {
            InputStream in = null;
            URL resourceUrl = ClassUtils.getDefaultClassLoader().getResource("META-INF/" + path);
            if (resourceUrl != null)
            {
                in = resourceUrl.openStream();
            }
            return in;
        }
    }
    
    /**
     * <p>Used to load resources from the {@link ServletContext}.</p>
     * @author David Draper
     *
     */
    private class ServletContextRes extends ResourceInfo
    {
        private ServletContextRes(String path)
        {
            super(path);
        }
        
        public InputStream loadInputStream() throws IOException
        {
            InputStream in = null;
            ServletContextResource resource = new ServletContextResource(servletContext, path);
            {
                if (resource.exists())
                {
                    in = resource.getInputStream();
                }
            }
            return in;
        }
    }
    
    // THIS SECTION DEFINES A SENTINEL...
    
    private static ResourceInfoSentinel RESOURCE_INFO_SENTINEL = null;
    
    private ResourceInfoSentinel getResourceInfoSentinel()
    {
        if (RESOURCE_INFO_SENTINEL == null)
        {
            RESOURCE_INFO_SENTINEL = new ResourceInfoSentinel();
        }
        return RESOURCE_INFO_SENTINEL;
    }
    
    private class ResourceInfoSentinel extends ResourceInfo
    {
        private ResourceInfoSentinel()
        {            
            super(null);
        }
                
        @Override
        public InputStream loadInputStream() throws IOException
        {
            return null;
        }
    }
}
