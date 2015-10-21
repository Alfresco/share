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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>Defines key information required for handling dependency resources.</p> 
 * 
 * @author David Draper
 * @author Kevin Roast
 */
public final class DependencyResource
{
    private final String mimetype;
    private final byte[] content;
    private final String charset;
    private int length;
    
    /**
     * Create a Dependency Resource - compressing the given content before storing it.
     */
    public DependencyResource(String mimetype, String content, String charset)
    {
        this.mimetype = mimetype;
        this.charset = charset;
        this.length = content.length();
        try
        {
            // GZIP the content string into an array of compressed bytes
            ByteArrayOutputStream bao = new ByteArrayOutputStream(this.length >> 2 > 32 ? this.length >> 2 : 32);
            GZIPOutputStream gzos = new GZIPOutputStream(bao);
            gzos.write(content.getBytes(charset));
            gzos.close();
            this.content = bao.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getMimetype()
    {
        return mimetype;
    }

    public byte[] getContent()
    {
        try
        {
            // unzip our byte array and return the raw byte data - this is generally streamed back to a client
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(this.content));
            byte[] buf = new byte[4096*2];
            ByteArrayOutputStream bos = new ByteArrayOutputStream(this.length);
            int len;
            while ((len=gzip.read(buf, 0, buf.length)) != -1)
            {
               bos.write(buf, 0, len);
            }
            bos.close();
            gzip.close();
            return bos.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /*package*/ int getStoredSize()
    {
        return this.content.length;
    }
    
    @Override
    public String toString()
    {
        try
        {
            return new String(getContent(), this.charset);
        }
        catch (IOException e)
        {
            return e.getMessage();
        }
    }
}
