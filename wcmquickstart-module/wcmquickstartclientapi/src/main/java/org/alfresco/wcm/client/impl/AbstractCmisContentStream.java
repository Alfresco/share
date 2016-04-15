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
