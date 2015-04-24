/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public interface ContentStream
{

    String getFileName();

    long getLength();

    String getMimeType();

    /**
     * 
     * @return
     * @deprecated
     */
    InputStream getStream();

    /**
     * Writes the content of this stream into the supplied Writer using UTF-8 encoding.
     * This operation neither flushes nor closes the supplied writer.
     * @param writer
     * @throws IOException
     */
    void write(Writer writer) throws IOException;

    /**
     * Writes the content of this stream into the supplied Writer using the specified character encoding.
     * This operation neither flushes nor closes the supplied writer.
     * @param writer
     * @param encoding
     * @throws IOException
     */
    void write(Writer writer, String encoding) throws IOException;
    
    /**
     * Streams the content from this stream into the supplied output stream.
     * This operation neither flushes nor closes the supplied output stream.
     * @param output
     * @throws IOException
     */
    void output(OutputStream output) throws IOException;
}