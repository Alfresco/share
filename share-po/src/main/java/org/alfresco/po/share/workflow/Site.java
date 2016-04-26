package org.alfresco.po.share.workflow;

import java.util.Set;

/**
 * @author Shan Nagarajan
 * @since 1.7.1
 */
public class Site
{

    private String name;

    private Set<Content> contents;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
