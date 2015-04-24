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
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

public class ExtensibilityHttpResponse extends HttpServletResponseWrapper
{
    private ExtensibilityModel model = null;
    
    public ExtensibilityHttpResponse(HttpServletResponse response, ExtensibilityModel model)
    {
        super(response);
        this.model = model;
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        return new PrintWriter(model.getWriter());
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        ExtensibilityServletOutputStream os = new ExtensibilityServletOutputStream(model.getWriter());
        return os;
    }

    @Override
    public void flushBuffer() throws IOException
    {
        this.model.getWriter().flush();
    }

    @Override
    public void reset()
    {
        super.reset();
    }

    @Override
    public void resetBuffer()
    {
        super.resetBuffer();
    }
}
