package org.alfresco.po.share.dashlet;

public enum WebQuickStartOptions
{
    FINANCE("Finance"),
    GOVERNMENT("Government");
    
    private String description;

    WebQuickStartOptions(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
