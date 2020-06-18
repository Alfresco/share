/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.alfresco.wcm.client.ContentStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachingContentStreamImpl implements ContentStream
{
    private static final Log log = LogFactory.getLog(CachingContentStreamImpl.class);
    private static final String SYSTEM_PROPERTY_TEMP_DIR = "java.io.tmpdir";
    private static String WQS_TEMP_FOLDER_NAME = "alfresco-wqs";
        
    private final String fileName;
    private final String mimeType;
    private final long length;
    private transient final File cacheFile;

    public CachingContentStreamImpl(ContentStream contentStream) throws IOException
    {
        this.fileName = contentStream.getFileName();
        this.length = contentStream.getLength();
        this.mimeType = contentStream.getMimeType();
        File tempFile = File.createTempFile("wqscontent-", null, getTempDir());
        tempFile.deleteOnExit();
        log.debug("Created temp cache file: " + tempFile.getPath());
        OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
        contentStream.output(os);
        os.flush();
        os.close();
        this.cacheFile = tempFile;
    }

    public static void setTempFolderName(String folderName)
    {
        WQS_TEMP_FOLDER_NAME = folderName;
    }
    
    private File getSystemTempDir() throws IOException
    {
        String systemTempDirPath = System.getProperty(SYSTEM_PROPERTY_TEMP_DIR);
        if (systemTempDirPath == null)
        {
            throw new IOException("System property not available: " + SYSTEM_PROPERTY_TEMP_DIR);
        }
        return new File(systemTempDirPath);
    }
    
    private File getTempDir() throws IOException
    {
        File systemTempDir = getSystemTempDir();
        // append the Alfresco directory
        File tempDir = new File(systemTempDir, WQS_TEMP_FOLDER_NAME);
        // ensure that the temp directory exists
        if (tempDir.exists())
        {
            // nothing to do
        }
        else
        {
            // not there yet
            if (!tempDir.mkdirs())
            {
                throw new IOException("Failed to create temp directory: " + tempDir);
            }
            if (log.isDebugEnabled())
            {
                log.debug("Created temp directory: " + tempDir);
            }
        }
        // done
        return tempDir;
    }
    
    @Override
    public String getFileName()
    {
        return fileName;
    }

    @Override
    public long getLength()
    {
        return length;
    }

    @Override
    public String getMimeType()
    {
        return mimeType;
    }

    @Override
    public InputStream getStream()
    {
        try
        {
            return new FileInputStream(cacheFile);
        }
        catch (FileNotFoundException e)
        {
            log.warn("Failed to open input stream for cached content file", e);
            return null;
        }
    }

    @Override
    public void output(OutputStream output) throws IOException
    {
        InputStream is = new FileInputStream(cacheFile);
        try
        {
            StreamUtils.output(is, output);
        }
        finally
        {
            is.close();
        }
    }

    @Override
    public void write(Writer writer) throws IOException
    {
        InputStream is = new FileInputStream(cacheFile);
        try
        {
            StreamUtils.write(is, writer, "UTF-8");
        }
        finally
        {
            is.close();
        }
    }

    @Override
    public void write(Writer writer, String encoding) throws IOException
    {
        InputStream is = new FileInputStream(cacheFile);
        try
        {
            StreamUtils.write(is, writer, encoding);
        }
        finally
        {
            is.close();
        }
    }

    public void finalize()
    {
        if (cacheFile != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Attempting to delete temp cache file " + cacheFile.getPath());
            }
            cacheFile.delete();
        }
    }
}
