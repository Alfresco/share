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