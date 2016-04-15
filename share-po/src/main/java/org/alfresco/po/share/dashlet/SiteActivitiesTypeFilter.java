package org.alfresco.po.share.dashlet;

/**
 * Contains the Type filters on Site Activities Dashlet.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public enum SiteActivitiesTypeFilter
{
    ALL_ITEMS("all items"),
    STATUS_UPDATES("status updates"),
    COMMENTS("comments"),
    CONTENT("content"),
    MEMBERSHIPS("memberships");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private SiteActivitiesTypeFilter(String description)
    {
        this.description = description;
    }

    /**
     * Gets description.
     * 
     * @return String description
     */
    public String getDescription()
    {
        return this.description;
    }

    public static SiteActivitiesTypeFilter getFilter(String description)
    {
        for (SiteActivitiesTypeFilter filter : SiteActivitiesTypeFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

}