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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;

import freemarker.cache.TemplateLoader;

/**
 * Store implementation which rides on top of the web application
 * servlet context
 * 
 * @author muzquiano
 */
public abstract class ResourceStore extends AbstractStore
{
    private static Log logger = LogFactory.getLog(ResourceStore.class);

    protected PathMatchingResourcePatternResolver resolver = null;
    private Resource rootResource = null;
    private String root;
    
    /**
     * Returns the resource resolver to be used in resolving resource references by this
     * resources tore.
     * 
     * @return resource resolver
     */
    protected abstract PathMatchingResourcePatternResolver getResourceResolver();

    /**
     * Returns a list of document paths for a given document pattern
     *  
     * @param documentPathPattern
     * @return
     * @throws IOException
     */
    protected abstract List<String> matchDocumentPaths(String documentPathPattern)
        throws IOException;    
    
    /**
     * Determines whether the given document is a forbidden path (i.e. a path that has
     * been explicitly filtered or restricted).
     * 
     * By default, this doesn't do any filtering but it can be overridden by implementation
     * classes that may wish to do so.
     * 
     * @param documentPath
     * @return
     */
    protected boolean isForbidden(String documentPath)
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#init()
     */
    public void init()
    {
        this.resolver = getResourceResolver();
        
        if (this.root == null)
        {
            this.root = "/";
        }
    }
    
    /**
     * Sets the path to the root resource on top of which this store will mount.
     * 
     * If you wanted to mount the store against the WEB-INF directory, you would set
     * root to "/WEB-INF".
     * 
     * @param root
     */
    public void setPath(String root)
    {
        this.root = root;
    }
    
    /**
     * Returns the resource path where the store begins.
     * 
     * @return
     */
    public String getPath()
    {
        return this.root;
    }
    
    protected String getRoot()
    {
        return getPath();
    }
    
    /**
     * Returns the servlet context resource that is the root of the store
     * 
     * @return
     */
    public Resource getRootResource()
    {
        if (rootResource == null)
        {
            rootResource = this.resolver.getResource(root);
        }
        
        return rootResource;
    }
    
    /**
     * Converts from a document path to a full resource path that incorporates
     * the root of the store.
     * 
     * @param documentPath
     * @return
     */
    protected String toResourcePath(String documentPath)
    {
        if (documentPath.startsWith("/"))
        {
            documentPath = documentPath.substring(1);
        }
        
        String resourcePath = null;
        
        if ("/".equals(getRoot()))
        {
            resourcePath = "/" + documentPath;
        }
        else
        {
            resourcePath = getRoot() + "/" + documentPath;
        }
        
        return resourcePath;
    }
    
    /**
     * Converts from a resource path back to a document path.
     * 
     * @param resourcePath
     * @return
     */
    protected String toDocumentPath(String resourcePath)
    {
        String documentPath = null;
        
        if (resourcePath.startsWith(getRoot()))
        {
            documentPath = resourcePath.substring(getRoot().length());
            if (documentPath.startsWith("/"))
            {
                documentPath = documentPath.substring(1);
            }    
        }
        
        return documentPath;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#isSecure()
     */
    public boolean isSecure()
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#exists()
     */
    public boolean exists()
    {
        return getRootResource().exists();
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        boolean has = false;
        
        if (!isForbidden(documentPath))
        {
            String resourcePath = toResourcePath(documentPath);
            
            Resource resource = this.resolver.getResource(resourcePath);
            has = resource.exists();
        }
        
        return has;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        long lastModified = -1;
        
        if (!isForbidden(documentPath))
        {
            String resourcePath = toResourcePath(documentPath);
            
            Resource resource = this.resolver.getResource(resourcePath);
            lastModified = resource.lastModified();
        }
        
        return lastModified; 
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#updateDocument(java.lang.String, java.lang.String)
     */
    public void updateDocument(String documentPath, String content)
            throws IOException
    {
        throw new IOException("Cannot update a document in a web application store - the store is read-only");
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath) throws IOException
    {
        throw new IOException("Cannot update a document in a web application store - the store is read-only");
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#createDocument(java.lang.String, java.lang.String)
     */
    public void createDocument(String documentPath, String content)
            throws IOException
    {
        throw new IOException("Cannot update a document in a web application store - the store is read-only");
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
        if (isForbidden(documentPath))
        {
            throw new IOException("Document path: " + documentPath + " is within a protected directory, cannot retrieve input stream");
        }
        
        String resourcePath = toResourcePath(documentPath);
        
        Resource resource = this.resolver.getResource(resourcePath);
        return resource.getInputStream();
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        String[] paths;

        try
        {
            List<String> documentPaths = matchDocumentPaths("/**/*");
            paths = documentPaths.toArray(new String[documentPaths.size()]);
        }
        catch (IOException e)
        {
            // Note: Ignore: no documents found
            paths = new String[0];
        }

        return paths;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includeSubPaths, String documentPattern)
        throws IOException
    {
        if ((path == null) || (path.length() == 0))
        {
            path = "/";
        }

        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }

        if (!path.endsWith("/"))
        {
            path = path + "/";
        }

        if ((documentPattern == null) || (documentPattern.length() == 0))
        {
            documentPattern = "*";
        }

        // classpath*:
        final StringBuilder pattern = new StringBuilder(128);
        pattern.append(path)
               .append((includeSubPaths ? "**/" : ""))
               .append(documentPattern);

        List<String> documentPaths = matchDocumentPaths(pattern.toString());
        return documentPaths.toArray(new String[documentPaths.size()]);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths() throws IOException
    {
        return getDocumentPaths("/", true, DESC_PATH_PATTERN);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getScriptDocumentPaths(org.springframework.extensions.webscripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script) throws IOException
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPaths("/", false, scriptPaths);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new WebApplicationStoreScriptLoader();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        return new WebApplicationStoreTemplateLoader();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.webscripts.Store#getBasePath()
     */
    public String getBasePath()
    {
        return getRoot();
    }    
        
    /**
     * Web Application Store implementation of a Script Loader
     * 
     * @author muzquiano
     */
    protected class WebApplicationStoreScriptLoader implements ScriptLoader
    {
        /**
         * @see org.springframework.extensions.webscripts.ScriptLoader#getScript(java.lang.String)
         */
        public ScriptContent getScript(String path)
        {
            ScriptContent sc = null;
            if (hasDocument(path))
            {
                sc = new WebApplicationStoreScriptContent(path);
            }
            return sc;
        }
    }

    /**
     * Web Application Store implementation of a Template Loader
     * 
     * @author muzquiano
     */
    private class WebApplicationStoreTemplateLoader implements TemplateLoader
    {
        /**
         * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
         */
        public void closeTemplateSource(Object templateSource)
                throws IOException
        {
            // nothing to do - we return a reader to fully retrieved in-memory
            // data
        }

        /**
         * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
         */
        public Object findTemplateSource(String name) throws IOException
        {
            WebApplicationStoreTemplateSource source = null;
            if (hasDocument(name))
            {
                source = new WebApplicationStoreTemplateSource(name);
            }
            return source;
        }

        /**
         * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
         */
        public long getLastModified(Object templateSource)
        {
            return ((WebApplicationStoreTemplateSource) templateSource).lastModified();
        }

        /**
         * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object,
         *      java.lang.String)
         */
        public Reader getReader(Object templateSource, String encoding)
                throws IOException
        {
            return ((WebApplicationStoreTemplateSource) templateSource).getReader(encoding);
        }
    }

    /**
     * Template Source - loads from a Web Application Store
     * 
     * @author muzquiano
     */
    private class WebApplicationStoreTemplateSource
    {
        private String templatePath;

        private WebApplicationStoreTemplateSource(String path)
        {
            this.templatePath = path;
        }

        private long lastModified()
        {
            try
            {
                return ResourceStore.this.lastModified(templatePath);
            }
            catch (IOException e)
            {
                return -1;
            }
        }

        private Reader getReader(String encoding) throws IOException
        {
            Reader reader = null;
            
            String resourcePath = toResourcePath(templatePath);
            Resource resource = getResourceResolver().getResource(resourcePath);
            if (resource.exists())
            {
                reader = encoding != null ? new java.io.InputStreamReader(resource.getInputStream(), encoding) :
                    new java.io.InputStreamReader(resource.getInputStream());
            }

            return reader;
        }
    }

    /**
     * Script Content - loads from a Web Application Store.
     * 
     * @author muzquiano
     */
    private class WebApplicationStoreScriptContent implements ScriptContent
    {
        private String scriptPath;

        /**
         * Constructor
         * 
         * @param path Path to remote script content
         */
        private WebApplicationStoreScriptContent(String path)
        {
            this.scriptPath = path;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getPath()
         */
        public String getPath()
        {
            return getBasePath() + '/' + this.scriptPath;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getPathDescription()
         */
        public String getPathDescription()
        {
            return getBasePath() + '/' + this.scriptPath;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getInputStream()
         */
        public InputStream getInputStream()
        {
            InputStream is = null;

            try
            {
                String resourcePath = toResourcePath(scriptPath);
                
                Resource resource = getResourceResolver().getResource(resourcePath);
                if (resource.exists())
                {
                    is = resource.getInputStream();
                }                
            }
            catch (IOException e)
            {
                throw new PlatformRuntimeException(
                        "Unable to load script: " + scriptPath, e);
            }

            return is;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#getReader()
         */
        public Reader getReader()
        {
            Reader reader = null;

            try
            {
                String resourcePath = toResourcePath(scriptPath);
                
                Resource resource = getResourceResolver().getResource(resourcePath);
                if (resource.exists())
                {
                    reader = new java.io.InputStreamReader(resource.getInputStream(), "UTF-8");
                }                
            }
            catch (IOException e)
            {
                throw new PlatformRuntimeException(
                        "Unable to load script: " + scriptPath, e);
            }

            return reader;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#isSecure()
         */
        public boolean isSecure()
        {
            return false;
        }

        /**
         * @see org.springframework.extensions.webscripts.ScriptContent#isCachable()
         */
        public boolean isCachable()
        {
            return false;
        }
    }
    
    /**
     * Gets resources that match a given location pattern.  A resource in the returned array can live
     * in the class path as either a class file or as an entry within one of the JAR files in the
     * class path.
     * 
     * This function is provided here as it is generally useful for implementation classes.
     * 
     * @param documentPathPattern
     * @return
     * @throws IOException
     */
    protected Resource[] getDocumentResources(String documentPathPattern)
        throws IOException
    {
        String resourcePath = toResourcePath(documentPathPattern);

        Resource[] resources = resolver.getResources(resourcePath);
        ArrayList<Resource> list = new ArrayList<Resource>(resources.length);
        for (Resource resource : resources)
        {
            // only keep documents, not directories
            if (!resource.getURL().toExternalForm().endsWith("/"))
            {
                list.add(resource);
            }
        }
        
        return list.toArray(new Resource[list.size()]);
    }
}
