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

import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class ContentStreamCmisRenditionImpl implements org.alfresco.wcm.client.Rendition
{
    private final Rendition cmisRendition;
    private ContentStream cmisContentStream;

    public ContentStreamCmisRenditionImpl(Rendition cmisRendition)
    {
        super();
        this.cmisRendition = cmisRendition;
    }

    public String getFileName()
    {
        return getContentStream().getFileName();
    }

    public long getLength()
    {
        return cmisRendition.getLength();
    }

    public String getMimeType()
    {
        return cmisRendition.getMimeType();
    }

    public InputStream getStream()
    {
        return getContentStream().getStream();
    }
    
    private ContentStream getContentStream()
    {
        if (cmisContentStream == null)
        {
            cmisContentStream = cmisRendition.getContentStream();
        }
        return cmisContentStream;
    }

    @Override
    public long getHeight()
    {
        return cmisRendition.getHeight();
    }

    @Override
    public long getWidth()
    {
        return cmisRendition.getWidth();
    }

    @Override
    public void output(OutputStream output) throws IOException
    {
        StreamUtils.output(getContentStream().getStream(), output);
    }

    @Override
    public void write(Writer writer) throws IOException
    {
        write(writer, "UTF-8");
    }

    @Override
    public void write(Writer writer, String encoding) throws IOException
    {
        StreamUtils.write(getContentStream().getStream(), writer, encoding);
    }
}
