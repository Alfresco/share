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
