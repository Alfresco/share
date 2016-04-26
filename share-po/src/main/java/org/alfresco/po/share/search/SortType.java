package org.alfresco.po.share.search;

import org.apache.commons.lang3.StringUtils;

/**
 * This enums used to describe list of Sort item in search results page.
 * 
 * @author Subashni Prasanna
 * @since v1.7.1
 */
public enum SortType
{
    RELEVANCE("Relevance"),
    NAME("Name"), 
    TITLE("Title"),
    DESCRIPTION("Description"), 
    AUTHOR("Author"), 
    MODIFIER("Modifier"), 
    MODIFIED("Modified date"),
    CREATOR("Creator"), 
    CREATED("Created date"),
    SIZE("Size"), 
    MIMETYPE("Mime type"), 
    TYPE("Type");

    private String sortName;

    private SortType(String type)
    {
        sortName = type;
    }

    public String getSortName()
    {
        return sortName;
    }

    /**
     * Find the {@link SortType} based it is name.
     * 
     * @param name - Aspect's Name
     * @return {@link SortType}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static SortType getSortType(String name) throws Exception
    {
        if (StringUtils.isEmpty(name))
        {
            throw new UnsupportedOperationException("Name can't null or empty, It is required.");
        }
        for (SortType sortType : SortType.values())
        {
            if (sortType.sortName != null && sortType.sortName.equalsIgnoreCase(name.trim()))
            {
                return sortType;
            }
        }
        throw new IllegalArgumentException("Not able to find the Sort Type for given name : " + name);
    }
}
