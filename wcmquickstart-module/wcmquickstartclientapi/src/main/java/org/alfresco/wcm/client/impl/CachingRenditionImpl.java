package org.alfresco.wcm.client.impl;

import java.io.IOException;

import org.alfresco.wcm.client.Rendition;

public class CachingRenditionImpl extends CachingContentStreamImpl implements Rendition
{
    private final long height;
    private final long width;
    
    public CachingRenditionImpl(Rendition rendition) throws IOException
    {
        super(rendition);
        this.height = rendition.getHeight();
        this.width = rendition.getWidth();
    }

    @Override
    public long getHeight()
    {
        return height;
    }

    @Override
    public long getWidth()
    {
        return width;
    }

}
