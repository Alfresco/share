package org.alfresco.wcm.client.impl;

import java.io.Serializable;

public class WebscriptParam implements Serializable
{
    private static final long serialVersionUID = -6122583337277445701L;

    private String name;
    private String value;

    public WebscriptParam(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
