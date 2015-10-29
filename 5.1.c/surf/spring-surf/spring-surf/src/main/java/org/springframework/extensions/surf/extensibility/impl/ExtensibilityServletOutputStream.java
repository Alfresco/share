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
package org.springframework.extensions.surf.extensibility.impl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletOutputStream;

public class ExtensibilityServletOutputStream extends ServletOutputStream
{
    private Writer modelWriter = null;
    
    public ExtensibilityServletOutputStream(Writer modelWriter)
    {
        super();
        this.modelWriter = modelWriter;
    }

    @Override
    public void write(int b) throws IOException
    {
        this.modelWriter.write(b);
    }

    @Override
    public void print(String s) throws IOException
    {
        this.modelWriter.write(s);
    }

    @Override
    public void print(char c) throws IOException
    {
        this.modelWriter.write(c);
    }

    @Override
    public void flush() throws IOException
    {
        this.modelWriter.flush();
    }

    @Override
    public void close() throws IOException
    {
        this.modelWriter.close();
    }

}
