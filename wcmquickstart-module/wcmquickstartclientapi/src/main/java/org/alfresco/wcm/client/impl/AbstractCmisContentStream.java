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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.chemistry.opencmis.commons.data.ContentStream;


public class AbstractCmisContentStream implements org.alfresco.wcm.client.ContentStream
{
    private ContentStream cmisContentStream;

    
    public AbstractCmisContentStream(ContentStream cmisContentStream)
    {
        super();
        this.cmisContentStream = cmisContentStream;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.wcm.client.impl.ContentStream#getFileName()
     */
    public String getFileName()
    {
        return cmisContentStream.getFileName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.wcm.client.impl.ContentStream#getLength()
     */
    public long getLength()
    {
        return cmisContentStream.getLength();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.wcm.client.impl.ContentStream#getMimeType()
     */
    public String getMimeType()
    {
        return cmisContentStream.getMimeType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.wcm.client.impl.ContentStream#getStream()
     */
    public InputStream getStream()
    {
        return cmisContentStream.getStream();
    }

    @Override
    public void output(OutputStream output) throws IOException
    {
        InputStream stream = cmisContentStream.getStream();
        try
        {
            StreamUtils.output(stream, output);
        }
        finally
        {
//            stream.close();
        }
    }

    @Override
    public void write(Writer writer) throws IOException
    {
        write(writer, "UTF-8");
    }

    @Override
    public void write(Writer writer, String encoding) throws IOException
    {
        InputStream stream = cmisContentStream.getStream();
        try
        {
            StreamUtils.write(stream, writer, encoding);
        }
        finally
        {
//            stream.close();
        }
    }
}
