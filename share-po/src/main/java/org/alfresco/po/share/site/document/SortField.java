package org.alfresco.po.share.site.document;

import org.openqa.selenium.By;

/**
 * This enum is used to describe the sort field.
 * 
 * @author Jallison
 * @since 2.1.1
 */
public enum SortField
{
    NAME("Name", By.xpath(".//div[@class='sort-field']//a[text()='Name']")),
    POPULARITY("Popularity", By.xpath(".//div[@class='sort-field']//a[text()='Popularity']")),
    TITLE("Title", By.xpath(".//div[@class='sort-field']//a[text()='Title']")),
    DESCRIPTION("Description", By.xpath(".//div[@class='sort-field']//a[text()='Description']")),
    CREATED("Created", By.xpath(".//div[@class='sort-field']//a[text()='Created']")),
    CREATOR("Creator", By.xpath(".//div[@class='sort-field']//a[text()='Creator']")),
    MODIFIED("Modified", By.xpath(".//div[@class='sort-field']//a[text()='Modified']")),
    MODIFIER("Modifier", By.xpath(".//div[@class='sort-field']//a[text()='Modifier']")),
    SIZE("Size", By.xpath(".//div[@class='sort-field']//a[text()='Size']")),
    MIMETYPE("Mimetype", By.xpath(".//div[@class='sort-field']//a[text()='Mimetype']")),
    TYPE("Type", By.xpath(".//div[@class='sort-field']//a[text()='Type']"));

    private String name;
    private By sortLocator;

    SortField(String name, By sortLocator)
    {
        this.name = name;
        this.sortLocator = sortLocator;
    }

    /**
     * Get the xpath for the SortField.
     * 
     * @return By
     */
    public By getSortLocator()
    {
        return sortLocator;
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Get the SortField enum from the name string
     * 
     * @param name
     * @return SortField
     */
    public static SortField getEnum(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Value 'name' cannot be null");
        }

        for (SortField value : values())
        {
            if (value.name.startsWith(name))
            {
                return value;
            }
        }
        throw new IllegalArgumentException("Cannot find SortField with name " + name);
    }
}
