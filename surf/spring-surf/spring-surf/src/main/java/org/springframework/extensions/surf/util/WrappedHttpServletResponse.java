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

package org.springframework.extensions.surf.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A wrapper class for buffering around HttpServletResponse objects.
 * The output is trapped and retrievable from this object.
 * 
 * @author muzquiano
 */
public class WrappedHttpServletResponse extends HttpServletResponseWrapper
{
    /**
     * Instantiates a new wrapped http servlet response.
     * 
     * @param response the response
     */
    public WrappedHttpServletResponse(HttpServletResponse response)
    {
        super(response);
        this.outputStream = new ByteArrayOutputStream();
        this.printWriter = new PrintWriter(outputStream);
    }

    /** The print writer. */
    private PrintWriter printWriter = null;
    
    /** The output stream. */
    private ByteArrayOutputStream outputStream = null;

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getWriter()
     */
    public PrintWriter getWriter() throws IOException
    {
        // return this instead of the output stream
        return printWriter;
    }

    /**
     * Gets the output.
     * 
     * @return the output
     */
    public String getOutput()
    {
        printWriter.flush();
        printWriter.close();
        return this.outputStream.toString();
    }
}
