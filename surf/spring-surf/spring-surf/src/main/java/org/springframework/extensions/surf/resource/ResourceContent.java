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

package org.springframework.extensions.surf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.springframework.extensions.surf.FrameworkBean;

/**
 * Interface to describe content around a Resource.
 * 
 * @author muzquiano
 * @author kevinr
 */
public interface ResourceContent
{
    /**
     * A link back to the resource of which this content is a part.
     * 
     * @return
     */
    public Resource getResource();
    
    /**
     * Gets the bytes. Use with caution only, if you know the content encoding.
     * 
     * @return the bytes
     */
    public byte[] getBytes() throws IOException;
        
    /**
     * Retrieves an input stream to the resource content.
     * Use with caution only, if you know the content encoding.
     * 
     * @return the input stream
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public InputStream getInputStream() throws IOException;
    
    /**
     * Gets the reader for the resource content. Reader will use the
     * appropriate character encoding as specified in the resource response.
     * 
     * @return the reader
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Reader getReader() throws IOException;
    
    /**
     * Returns the String content for the resource. Will use the appropriate
     * character encoding as specified in the resource response.
     * 
     * @return String content for the resource
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String getStringContent() throws IOException;
}
