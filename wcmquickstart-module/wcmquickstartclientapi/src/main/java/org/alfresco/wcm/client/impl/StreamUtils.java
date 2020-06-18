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
