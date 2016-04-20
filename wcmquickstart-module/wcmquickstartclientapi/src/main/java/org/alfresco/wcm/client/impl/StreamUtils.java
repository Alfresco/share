package org.alfresco.wcm.client.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class StreamUtils
{
    private static ThreadLocal<byte[]> byteBuffer = new ThreadLocal<byte[]>()
    {

        @Override
        protected byte[] initialValue()
        {
            return new byte[10240];
        }
    };

    private static ThreadLocal<char[]> charBuffer = new ThreadLocal<char[]>()
    {
        @Override
        protected char[] initialValue()
        {
            return new char[10240];
        }
    };

    public static void output(InputStream input, OutputStream output) throws IOException
    {
        byte[] buf = byteBuffer.get();
        int count;
        while ((count = input.read(buf)) != -1)
        {
            output.write(buf, 0, count);
        }
        output.flush();
    }

    public static void write(InputStream input, Writer writer, String encoding) throws IOException
    {
        Reader reader = new InputStreamReader(input, encoding);
        BufferedWriter bufWrite = new BufferedWriter(writer);
        char[] buf = charBuffer.get();
        int count;
        while ((count = reader.read(buf)) != -1)
        {
            bufWrite.write(buf, 0, count);
        }
        bufWrite.flush();
    }

}
