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
     * @return InputStream
     * @deprecated
     */
    InputStream getStream();

    /**
     * Writes the content of this stream into the supplied Writer using UTF-8 encoding.
     * This operation neither flushes nor closes the supplied writer.
     * @param writer Writer
     * @throws IOException
     */
    void write(Writer writer) throws IOException;

    /**
     * Writes the content of this stream into the supplied Writer using the specified character encoding.
     * This operation neither flushes nor closes the supplied writer.
     * @param writer Writer
     * @param encoding String
     * @throws IOException
     */
    void write(Writer writer, String encoding) throws IOException;
    
    /**
     * Streams the content from this stream into the supplied output stream.
     * This operation neither flushes nor closes the supplied output stream.
     * @param output OutputStream
     * @throws IOException
     */
    void output(OutputStream output) throws IOException;
}