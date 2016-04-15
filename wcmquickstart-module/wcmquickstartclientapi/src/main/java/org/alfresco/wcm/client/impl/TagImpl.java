package org.alfresco.wcm.client.impl;

import org.alfresco.wcm.client.Tag;

/**
 * Base Tag implementation
 */
public class TagImpl implements Tag
{
    private String tagName;
    private Integer tagCount;

    TagImpl(String tagName, Integer tagCount)
    {
        this.tagName = tagName;
        this.tagCount = tagCount;
    }
    
    @Override
    public String getName()
    {
        return tagName;
    }

    @Override
    public int getCount()
    {
        return tagCount;
    }
}
