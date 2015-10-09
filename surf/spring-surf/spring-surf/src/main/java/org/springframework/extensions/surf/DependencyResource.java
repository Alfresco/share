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

import java.io.UnsupportedEncodingException;

/**
 * <p>Defines key information required for handling dependency resources.</p> 
 * @author David Draper
 * @author Kevin Roast
 */
public class DependencyResource
{
    private final String mimetype;
    private final byte[] content;
    private final String charset;
    
    public DependencyResource(String mimetype, String content, String charset)
    {
        this.mimetype = mimetype;
        this.charset = charset;
        try
        {
            this.content = content.getBytes(charset);
        }
        catch (UnsupportedEncodingException e)
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
        return content;
    }
    
    @Override
    public String toString()
    {
        try
        {
            return new String(this.content, this.charset);
        }
        catch (UnsupportedEncodingException e)
        {
            return e.getMessage();
        }
    }
}
