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
