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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.webscripts.AbstractStore;
import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.web.context.ServletContextAware;

import freemarker.cache.TemplateLoader;

/**
 * Simple implementation of a local store file system.
 * 
 * This is extremely light weight and is used as a base case for comparing other
 * store performance vs. the local file system.
 * 
 * @author muzquiano
 */
public class LocalFileSystemStore extends AbstractStore implements ServletContextAware
{
    private static Log logger = LogFactory.getLog(LocalFileSystemStore.class);

    private String root;
    private String path;
    private File rootDir;
    private ServletContext servletContext;

    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;        
    }
    
    public ServletContext getServletContext()
    {
        return this.servletContext;
    }

    protected File getRootDir()
    {
        if (this.rootDir == null)
        {
            this.rootDir = new File(getBasePath());
        }

        return this.rootDir;
    }

    /**
     * @param root the root path
     */
    public void setRoot(String root)
    {
        this.root = root;
    }
    
    public String getRoot()
    {
        return this.root;
    }

    /**
     * @param path the relative path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public String getPath()
    {
        return this.path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#init()
     */
    public void init()
    {
        if (this.path == null)
        {
            this.path = "";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#isSecure()
     */
    public boolean isSecure()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#exists()
     */
    public boolean exists()
    {
        if (getRootDir() == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Root directory for Store does not exist");

            return false;
        }
        return getRootDir().exists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#hasDocument(java.lang.String)
     */
    public boolean hasDocument(String documentPath)
    {
        File file = new File(toAbsolutePath(documentPath));
        return (file != null && file.exists() && file.isFile());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#lastModified(java.lang.String)
     */
    public long lastModified(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Unable to locate file to check modification time: " + documentPath);
        }

        return file.lastModified();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#updateDocument(java.lang.String,
     *      java.lang.String)
     */
    public void updateDocument(String documentPath, String content)
            throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Unable to locate file for update: " + documentPath);
        }

        FileWriter fw = new FileWriter(file);
        try
        {
            fw.write(content);
        }
        finally
        {
            fw.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#removeDocument(java.lang.String)
     */
    public boolean removeDocument(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Update to remove document failed, file not found: " + documentPath);
        }

        return file.delete();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#createDocument(java.lang.String,
     *      java.lang.String)
     */
    public void createDocument(String documentPath, String content)
            throws IOException
    {
        // check whether a file already exists
        if (hasDocument(documentPath))
        {
//            throw new IOException(
//                    "Unable to create document, already exists: " + documentPath);
            removeDocument(documentPath);
        }

        File file = new File(toAbsolutePath(documentPath));
        if (file.getParentFile().exists())
        {
            // The file already exists, no action required.            
        }
        else
        {
            file.getParentFile().mkdirs();
        }

        FileWriter fw = new FileWriter(file);
        try
        {
            fw.write(content);
        }
        finally
        {
            fw.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getDocument(java.lang.String)
     */
    public InputStream getDocument(String documentPath) throws IOException
    {
        File file = new File(toAbsolutePath(documentPath));
        if (file == null)
        {
            throw new IOException(
                    "Unable to get input stream from document: " + documentPath);
        }

        return new FileInputStream(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getAllDocumentPaths()
     */
    public String[] getAllDocumentPaths()
    {
        List<String> list = new ArrayList<String>(256);

        // exhaustive traverse of absolute paths
        gatherAbsolutePaths(getRootDir().getAbsolutePath(), list);

        // convert to array
        String[] array = list.toArray(new String[list.size()]);

        // down shift to relative paths
        String absRootPath = getRootDir().getAbsolutePath() + File.separatorChar;
        int absRootPathLen = absRootPath.length();
        for (int i = 0; i < array.length; i++)
        {
            array[i] = array[i].substring(absRootPathLen);
            
            // so as to be consistent with expected store syntax
            array[i] = array[i].replace("\\", "/");            
        }

        return array;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.Store#getDocumentPaths(java.lang.String, boolean, java.lang.String)
     */
    public String[] getDocumentPaths(String path, boolean includedSubPaths, String documentPattern)
    {
        String regexPattern = documentPattern.replaceAll("\\*", ".*");
        
        if (!regexPattern.startsWith(".*"))
        {
            regexPattern = ".*" + regexPattern;
        }
        
        return getDocumentPathsByRegEx(path, regexPattern, includedSubPaths);
    }
    
    /**
     * Performs a pattern filter look up using a regex and starting
     * from a given path.
     * 
     * Returns an array of valid document paths
     * 
     * @param path
     * @param regexPattern
     * @param traverseChildren
     * 
     * @return document paths
     */
    protected String[] getDocumentPathsByRegEx(String path, String regexPattern, boolean traverseChildren)
    {
        PatternFileFilter filter = new PatternFileFilter(regexPattern);

        String absParentPath = toAbsolutePath(path);
        int absParentPathLen = absParentPath.length() - 1;
        File f = new File(absParentPath);

        List<File> fileList = listPath(f, filter, traverseChildren);

        String[] paths = new String[fileList.size()];
        for (int i = 0; i < fileList.size(); i++)
        {
            String thePath = ((File) fileList.get(i)).getPath();
            paths[i] = thePath.substring(absParentPathLen);
            
            // so as to be consistent with expected store syntax
            paths[i] = paths[i].replace("\\", "/");
        }

        return paths;
    }
        
    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getDescriptionDocumentPaths()
     */
    public String[] getDescriptionDocumentPaths()
    {
        return getDocumentPathsByRegEx("/", ".*\\.desc\\.xml", true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getScriptDocumentPaths(org.alfresco.web.scripts.WebScript)
     */
    public String[] getScriptDocumentPaths(WebScript script)
    {
        String scriptPaths = script.getDescription().getId() + ".*";
        return getDocumentPathsByRegEx("/", scriptPaths, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getScriptLoader()
     */
    public ScriptLoader getScriptLoader()
    {
        return new LocalFileSystemStoreScriptLoader();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getTemplateLoader()
     */
    public TemplateLoader getTemplateLoader()
    {
        return new LocalFileSystemStoreTemplateLoader();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.scripts.Store#getBasePath()
     */
    public String getBasePath()
    {
        String fullPath = this.path;

        if (this.root != null)
        {
            if (!root.endsWith("/"))
            {
                root += "/";
            }

            if (root.startsWith("."))
            {
                // make relative to the web app real path
                fullPath = getRealPath(this.root.substring(1)) + this.path;
            }
            else
            {
                fullPath = this.root + this.path;
            }
        }

        return fullPath;
    }
    
    /**
     * Helper function for converting a web application path
     * to a real system path.
     * 
     * The input path might be /products/product.xml and the
     * generated real path would be the full system path to the
     * file in the servlet container's web application.
     * 
     * For example, d:/tomcat/webapps/surf/products/product.xml
     * 
     * @param path the relative path
     * 
     * @return the system path
     */
    public String getRealPath(String path)
    {
        String realPath = servletContext.getRealPath(path);
        if (realPath != null && realPath.endsWith(java.io.File.separator))
        {
            realPath = realPath.substring(0, realPath.length() - 1);
        }
        
        // clean up the path
        realPath = realPath.replace("/", java.io.File.separator);
        realPath = realPath.replace("\\", java.io.File.separator);
        
        return realPath;
    }

    /**
     * Returns the absolute path relative to the root of the store
     * given a particular document path.
     * 
     * @param documentPath
     * @return
     */
    protected String toAbsolutePath(String documentPath)
    {
        return getRootDir().getAbsolutePath() + File.separatorChar + documentPath;
    }

    protected void gatherAbsolutePaths(String absPath, List<String> list)
    {
        File file = new File(absPath);
        if (file.exists())
        {
            if (file.isFile())
            {
                list.add(absPath);
            }
            else if (file.isDirectory())
            {
                // get all of the children
                String[] childDocumentPaths = file.list();
                for (int i = 0; i < childDocumentPaths.length; i++)
                {
                    String childAbsPath = absPath + File.separatorChar + childDocumentPaths[i];
                    gatherAbsolutePaths(childAbsPath, list);
                }
            }
        }
    }

    /**
     * Local File System Store implementation of a Script Loader
     * 
     * @author muzquiano
     */
    protected class LocalFileSystemStoreScriptLoader implements ScriptLoader
    {
        /**
         * @see org.springframework.extensions.webscripts.ScriptLoader#getScript(java.lang.String)
         */
        public ScriptContent getScript(String path)
        {
            ScriptContent sc = null;
            if (hasDocument(path))
            {
                sc = new LocalFileSystemStoreScriptContent(path);
            }
            return sc;
        }
    }

    /**
     * Local File System Store implementation of a Template Loader
     * 
     * @author muzquiano
     */
    private class LocalFileSystemStoreTemplateLoader implements TemplateLoader
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
            LocalFileSystemStoreTemplateSource source = null;
            if (hasDocument(name))
            {
                source = new LocalFileSystemStoreTemplateSource(name);
            }
            return source;
        }

        /**
         * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
         */
        public long getLastModified(Object templateSource)
        {
            return ((LocalFileSystemStoreTemplateSource) templateSource).lastModified();
        }

        /**
         * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object,
         *      java.lang.String)
         */
        public Reader getReader(Object templateSource, String encoding)
                throws IOException
        {
            return ((LocalFileSystemStoreTemplateSource) templateSource).getReader(encoding);
        }
    }

    /**
     * Template Source - loads from a Local File System Store.
     * 
     * @author muzquiano
     */
    private class LocalFileSystemStoreTemplateSource
    {
        private String templatePath;

        private LocalFileSystemStoreTemplateSource(String path)
        {
            this.templatePath = path;
        }

        private long lastModified()
        {
            try
            {
                return LocalFileSystemStore.this.lastModified(templatePath);
            }
            catch (IOException e)
            {
                return -1;
            }
        }

        private Reader getReader(String encoding) throws IOException
        {
            Reader reader = null;

            File f = new File(toAbsolutePath(templatePath));
            if (f.exists())
            {
                reader = new FileReader(f);
            }

            return reader;
        }
    }

    /**
     * Script Content - loads from a Local File System Store.
     * 
     * @author muzquiano
     */
    private class LocalFileSystemStoreScriptContent implements ScriptContent
    {
        private String scriptPath;

        /**
         * Constructor
         * 
         * @param path Path to remote script content
         */
        private LocalFileSystemStoreScriptContent(String path)
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
                File f = new File(toAbsolutePath(scriptPath));
                if (f.exists())
                {
                    is = new FileInputStream(f);
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
                File f = new File(toAbsolutePath(scriptPath));
                if (f.exists())
                {
                    reader = new FileReader(f);
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

    private class PatternFileFilter implements FileFilter
    {
        Pattern pattern;

        public PatternFileFilter(String pat)
        {
            this.pattern = Pattern.compile(pat);
        }

        public boolean accept(File pathname)
        {
            boolean accept = false;
            
            if (pathname.isDirectory())
            {
                accept = false;
            }
            else
            {
                String path = null;
                try
                {
                    path = pathname.toURL().toExternalForm();
                    if (path.endsWith("\\") || path.endsWith("/"))
                    {
                        path = path.substring(0, path.length() - 1);
                    }
                    
                    accept = pattern.matcher(path).matches();
                }
                catch(MalformedURLException mue) { }
            }
            
            return accept;
        }
    }

    private List<File> listPath(File path, FileFilter filter,
            boolean listChildren)
    {
        List<File> results = new ArrayList<File>();

        listPath(path, filter, results, listChildren);

        return results;
    }

    private void listPath(File path, FileFilter filter, List<File> results,
            boolean listChildren)
    {
        // list of files in this dir
        File files[] = path.listFiles(filter);
        if (files.length > 0)
        {
            // Sort with help of Collections API
            Arrays.sort(files);

            // add into the results
            for (int i = 0; i < files.length; i++)
            {
                results.add(files[i]);
            }
        }

        // dive down into the subdirectories?
        if (listChildren)
        {
            // list of all files
            files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                // walk through children if deemed to be thus
                if (files[i].isDirectory())
                {
                    // recursively descend dir tree
                    listPath(files[i], filter, results, listChildren);
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "localfilesystem:" + getBasePath();
    }
}
