package org.alfresco.po.wqs;


/**
 * Representation of Article Details that can be used while create/edit the content of an article.
 *
 * @author Cristina Axinte
 */
public class WcmqsArticleDetails
{
    private String name;
    private String title;
    private String description;
    private String templateName;
    private String content;

    public WcmqsArticleDetails(String name, String title, String description, String content, String templateName)
    {
        this.setName(name);
        this.setTitle(title);
        this.setDescription(description);
        this.setTemplateName(templateName);
    }

    public WcmqsArticleDetails(String name)
    {
        this.setName(name);
    }

    public WcmqsArticleDetails()
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

    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
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
