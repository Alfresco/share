package org.alfresco.po.share.site.document;

/**
 * Representation of Content Details that can be used while create/edit the content.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class ContentDetails
{

    private String name;
    private String title;
    private String description;
    private String content;

    public ContentDetails(String name, String title, String description, String content)
    {
        this.name = name;
        this.title = title;
        this.description = description;
        this.content = content;
    }

    public ContentDetails(String name)
    {
        this.name = name;
    }

    public ContentDetails()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

}
