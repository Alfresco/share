package org.alfresco.wcm.client.impl;

import java.io.Serializable;

public class ContentInfo implements Serializable
{
    private String mimeType;
    private String encoding;
    private long size;

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

}
