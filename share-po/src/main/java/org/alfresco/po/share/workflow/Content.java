package org.alfresco.po.share.workflow;

import java.util.Set;

/**
 * @author Shan Nagarajan
 * @since 1.7.1
 */
public class Content
{

    private String name;

    private boolean folder;

    private Set<Content> contents;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isFolder()
    {
        return folder;
    }

    public void setFolder(boolean folder)
    {
        this.folder = folder;
    }

    public Set<Content> getContents()
    {
        return contents;
    }

    public void setContents(Set<Content> contents)
    {
        this.contents = contents;
    }

}
